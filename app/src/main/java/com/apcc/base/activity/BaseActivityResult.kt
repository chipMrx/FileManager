package com.apcc.base.activity

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class BaseActivityResult private constructor(
    private var caller: ActivityResultCaller,
    private var contract: ActivityResultContract<Intent, ActivityResult>,
    private var onActivityResult: OnActivityResult?
) {

    private var launcher: ActivityResultLauncher<Intent>? = null

    init {
        launcher = caller.registerForActivityResult(contract) { result ->
            callOnActivityResult(result)
        }
    }


    companion object{
        @NonNull
        fun registerForActivityResult(
            @NonNull caller: ActivityResultCaller,
            @NonNull contract: ActivityResultContract<Intent, ActivityResult>,
            @Nullable onActivityResult: OnActivityResult?
        ): BaseActivityResult {
            return BaseActivityResult(caller, contract, onActivityResult)
        }

        @NonNull
        fun registerForActivityResult(
            @NonNull caller: ActivityResultCaller,
            @NonNull contract: ActivityResultContract<Intent, ActivityResult>
        ): BaseActivityResult {
            return registerForActivityResult(caller, contract, null)
        }

        @NonNull
        fun registerActivityForResult(
            @NonNull caller: ActivityResultCaller
        ): BaseActivityResult{
            return registerForActivityResult(caller, StartActivityForResult())
        }
    }

    fun setOnActivityResult(@Nullable onActivityResult: OnActivityResult?) {
        this.onActivityResult = onActivityResult
    }

    fun launch(input: Intent, @Nullable onActivityResult: OnActivityResult?) {
        if (onActivityResult != null) {
            this.onActivityResult = onActivityResult
        }
        launcher?.launch(input)
    }

    fun launch(input: Intent) {
        launch(input, onActivityResult)
    }

    private fun callOnActivityResult(result: ActivityResult) {
        if (onActivityResult != null) onActivityResult?.onActivityResult(result)
    }

    ////////////////////////////////////
    ////// interface
    //////////////////////////////////////
    interface OnActivityResult {
        fun onActivityResult(result: ActivityResult)
    }
}