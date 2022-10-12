package com.apcc.emma.ui.deeplink


import android.view.View
import android.view.animation.AlphaAnimation
import com.apcc.emma.R
import com.apcc.emma.databinding.DeepLinkFragmentBinding
//import com.apcc.emma.ui.main.MainActivity
//import com.apcc.emma.ui.user.UserActivity
import com.apcc.base.fragment.BaseFragment

class DeepLinkFragment : BaseFragment<DeepLinkFragmentBinding, DeepLinkViewModel>() {
    override val resourceLayoutId: Int
        get() = R.layout.deep_link_fragment

    private var checked = 3

    private var animation:AlphaAnimation? = null

    override fun onInitView(root: View?) {
        binding.viewModel = viewModel
    }

    override fun subscribeUi() {

//        viewModel.getConfigs().observe(viewLifecycleOwner, { response ->
//            if (viewModel.handlerGetConfigs(response)) {
//                //checked--
//                checked = checked and 2.inv()
//                checkAllProcess()
//            }
//        })

    }

    override fun onResume() {
        super.onResume()
        //postDelay()
    }

    override fun onPause() {
        //removeDelay()
        super.onPause()
    }

//    private fun postDelay() {
//        removeDelay()
//        animation = AlphaAnimation(0.0f, 1.0f)
//        animation!!.duration = 500
//        animation!!.setAnimationListener(object :
//            Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {}
//            override fun onAnimationEnd(animation: Animation) {
//                //checked--
//                checked = checked and 1.inv()
//                checkAllProcess()
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {}
//        })
//        binding.imvLogo.startAnimation(animation)
//    }

//    private fun removeDelay(){
//        animation?.cancel()
//    }

//    private fun checkToGo(){
//        if (EmmaApp.isLogged()){
//            goToMain()
//        }else{
//            goToLogin()
//        }
//    }
//
//    private fun goToMain(){
//        val main = Intent(activity, MainActivity::class.java)
//        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        startActivity(main)
//        activity?.finish()
//    }
//
//    private fun goToLogin(){
//        val user = Intent(activity, UserActivity::class.java)
//        user.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        user.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        startActivity(user)
//        activity?.finish()
//    }

//    private fun checkAllProcess(){
//        if (checked <=0 ){
//            checkToGo()
//        }
//    }

}
