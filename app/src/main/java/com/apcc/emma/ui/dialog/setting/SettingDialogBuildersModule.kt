package com.apcc.emma.ui.dialog.setting

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class SettingDialogBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeCameraSettingDialog(): CameraSettingDialog
}
