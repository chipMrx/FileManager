package com.apcc.base.adapter

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import java.util.*

abstract class FragmentPagerAdapter(private val mFragmentManager: FragmentManager) : PagerAdapter() {

    private var mCurTransaction: FragmentTransaction? = null
    private val mSavedState = ArrayList<Fragment.SavedState?>()
    private val mFragments = ArrayList<Fragment?>()
    private var mCurrentPrimaryItem: Fragment? = null
    private var mExecutingFinishUpdate = false

    abstract fun getItem(position: Int): Fragment

    override fun startUpdate(container: ViewGroup) {
        check(container.id != View.NO_ID) {
            ("ViewPager with adapter " + this
                    + " requires a view id")
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (mFragments.size > position) {
            mFragments[position]?.let {
                return it
            }
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }

        val fragment = getItem(position)

        if (mSavedState.size > position) {
            mSavedState[position]?.let {
                fragment.setInitialSavedState(it)
            }
        }
        while (mFragments.size <= position) {
            mFragments.add(null)
        }
        fragment.setMenuVisibility(false)

        mFragments[position] = fragment
        mCurTransaction!!.add(container.id, fragment)
        mCurTransaction!!.setMaxLifecycle(fragment, Lifecycle.State.STARTED)

        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj : Any) {
        val fragment = obj as Fragment

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }
        while (mSavedState.size <= position) {
            mSavedState.add(null)
        }
        mSavedState[position] = if (fragment.isAdded) mFragmentManager.saveFragmentInstanceState(fragment) else null
        mFragments[position] = null

        mCurTransaction!!.remove(fragment)
        if (fragment == mCurrentPrimaryItem) {
            mCurrentPrimaryItem = null
        }
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) {
        val fragment = obj as Fragment
        if (fragment !== mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem!!.setMenuVisibility(false)
                if (mCurTransaction == null) {
                    mCurTransaction = mFragmentManager.beginTransaction()
                }
                mCurTransaction!!.setMaxLifecycle(
                    mCurrentPrimaryItem!!,
                    Lifecycle.State.STARTED
                )
            }
            fragment.setMenuVisibility(true)
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction()
            }
            mCurTransaction!!.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
            mCurrentPrimaryItem = fragment
        }
    }

    override fun finishUpdate(container: ViewGroup) {
        if (mCurTransaction != null) {
            if (!mExecutingFinishUpdate) {
                try {
                    mExecutingFinishUpdate = true
                    mCurTransaction!!.commitNowAllowingStateLoss()
                } finally {
                    mExecutingFinishUpdate = false
                }
            }
            mCurTransaction = null
        }
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return (obj as Fragment).view === view
    }

    override fun saveState(): Parcelable? {
        var state: Bundle? = null
        if (mSavedState.size > 0) {
            state = Bundle()
            val fss = arrayOfNulls<Fragment.SavedState>(mSavedState.size)
            mSavedState.toArray(fss)
            state.putParcelableArray("states", fss)
        }
        for (i in mFragments.indices) {
            val f = mFragments[i]
            if (f != null && f.isAdded) {
                if (state == null) {
                    state = Bundle()
                }
                val key = "f$i"
                mFragmentManager.putFragment(state, key, f)
            }
        }
        return state
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        if (state != null) {
            val bundle = state as Bundle
            bundle.classLoader = loader
            val fss = bundle.getParcelableArray("states")
            mSavedState.clear()
            mFragments.clear()
            if (fss != null) {
                for (i in fss.indices) {
                    mSavedState.add(fss[i] as Fragment.SavedState)
                }
            }
            val keys: Iterable<String> = bundle.keySet()
            for (key in keys) {
                if (key.startsWith("f")) {
                    val index = key.substring(1).toInt()
                    mFragmentManager.getFragment(bundle, key)?.let { f->
                        while (mFragments.size <= index) {
                            mFragments.add(null)
                        }
                        f.setMenuVisibility(false)
                        mFragments[index] = f
                    }
                }
            }
        }
    }
}