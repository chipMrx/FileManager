package com.apcc.di

import com.apcc.emma.ui.deeplink.DeepLinkBuildersModule
import com.apcc.emma.ui.deeplink.DeepLinkReceiveActivity
import com.apcc.emma.ui.dialog.option.OptionDialog
import com.apcc.emma.ui.dialog.option.OptionDialogBuildersModule
import com.apcc.emma.ui.dialog.order.OrderDialog
import com.apcc.emma.ui.dialog.order.OrderDialogBuildersModule
import com.apcc.emma.ui.dialog.setting.CameraSettingDialog
import com.apcc.emma.ui.dialog.setting.SettingDialogBuildersModule
import com.apcc.emma.ui.error.ErrorTrackerActivity
import com.apcc.emma.ui.error.ErrorTrackerBuildersModule
import com.apcc.emma.ui.file.FileActivity
import com.apcc.emma.ui.file.FileBuildersModule
import com.apcc.emma.ui.info.InfoActivity
import com.apcc.emma.ui.info.InfoBuildersModule
import com.apcc.emma.ui.search.SearchActivity
import com.apcc.emma.ui.search.SearchBuildersModule
import com.apcc.service.SyncBuildersModule
import com.apcc.service.SyncDataService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class AppBuilderModule {
    /////////////////////////////////////////////
    //// System
    ////////////////////////////////////////////////

    @ContributesAndroidInjector(modules = [SearchBuildersModule::class])
    abstract fun contributeSearchActivity(): SearchActivity

    @ContributesAndroidInjector(modules = [InfoBuildersModule::class])
    abstract fun contributeInfoActivity(): InfoActivity

    @ContributesAndroidInjector(modules = [ErrorTrackerBuildersModule::class])
    abstract fun contributeErrorTrackerActivity(): ErrorTrackerActivity

    @ContributesAndroidInjector(modules = [DeepLinkBuildersModule::class])
    abstract fun contributeDeepLinkReceiveActivity(): DeepLinkReceiveActivity

    ///////////////////////////

    @ContributesAndroidInjector(modules = [FileBuildersModule::class])
    abstract fun contributeFileActivity(): FileActivity

    /////////////////////////////////////////////
    //// Service
    ////////////////////////////////////////////////

    @ContributesAndroidInjector(modules = [SyncBuildersModule::class])
    abstract fun contributeSyncDataService(): SyncDataService

    /////////////////////////////////////////////
    //// Dialog
    ////////////////////////////////////////////////

    @ContributesAndroidInjector(modules = [OptionDialogBuildersModule::class])
    abstract fun contributeOptionDialog(): OptionDialog

    @ContributesAndroidInjector(modules = [OrderDialogBuildersModule::class])
    abstract fun contributeOrderDialog(): OrderDialog

    @ContributesAndroidInjector(modules = [SettingDialogBuildersModule::class])
    abstract fun contributeSettingDialog(): CameraSettingDialog

    /////////////////////////////////////////////
    //// Test
    ////////////////////////////////////////////////

}
