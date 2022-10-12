package com.apcc.base.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.apcc.emma.R
import com.apcc.utils.ExceptionHandler
import com.apcc.utils.Util

abstract class BaseDialog<BD : ViewDataBinding> : DialogFragment(), AbsDialog.DialogEventListener {
    protected lateinit var binding: BD
    protected var mCancelAble:Boolean = false
    protected var mCancelAbleHighest:Boolean = true // if false: u cannot dismiss by anyway
    protected abstract val resourceLayoutId: Int
    protected abstract val TAG: String
    protected abstract fun extraData()
    protected abstract fun onInitView(root: View?)
    protected abstract fun subscribeUi()

    override fun onTouchOutside() {}


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = AbsDialog(requireActivity(), theme)
        dialog.mDialogEventListener = this // handle action from window
        configDialog(dialog)
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(requireActivity()))

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, resourceLayoutId, container, false, null)
        binding.lifecycleOwner = viewLifecycleOwner
        onInitView(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        extraData()
        subscribeUi()
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            configDialog(it)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun configDialog(dialog:Dialog){
        // Touch outside the dialog
        dialog.setCanceledOnTouchOutside(allowCancel())
        dialog.setCancelable(allowCancel())
//        if(dialog is BaseDialog)
//            dialog.

        val window = dialog.window
        val wlp = window!!.attributes
        configDialogAttributes(wlp)
        window.attributes = wlp
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.setDimAmount(0.8f)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
    open fun configDialogAttributes(attributes:WindowManager.LayoutParams){
        attributes.gravity = Gravity.CENTER
        attributes.width = resources.displayMetrics.widthPixels* Util.getInt(requireContext(), R.integer.dialogWidthPercent)/100 // make dialog width = 65% screen size
        attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    open fun allowCancel():Boolean{
        return true
    }

    open fun show(fragmentManager: FragmentManager):Boolean{
        if (fragmentManager.findFragmentByTag(TAG) == null){
            show(fragmentManager, TAG)
            return true
        }
        return false
    }

    val isShowing: Boolean
        get() = if (dialog != null) dialog!!.isShowing else false

}