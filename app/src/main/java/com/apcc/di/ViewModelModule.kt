package com.apcc.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apcc.di.factory.EmmaViewModelFactory
import com.apcc.emma.ui.deeplink.DeepLinkViewModel
import com.apcc.emma.ui.dialog.option.OptionViewModel
import com.apcc.emma.ui.dialog.order.OrderViewModel
import com.apcc.emma.ui.dialog.setting.SettingViewModel
import com.apcc.emma.ui.error.ErrorTrackerViewModel
import com.apcc.emma.ui.file.detail.FileDetailViewModel
import com.apcc.emma.ui.file.local.FolderManagerViewModel
import com.apcc.emma.ui.info.app.InfoViewModel
import com.apcc.emma.ui.info.license.LicenseViewModel
import com.apcc.emma.ui.info.term.TermViewModel
import com.apcc.emma.ui.search.SearchViewModel
import com.apcc.service.SyncServiceViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {
    /////////////////
    // factory
    /////////////////
    @Binds
    abstract fun bindViewModelFactory(factory: EmmaViewModelFactory): ViewModelProvider.Factory

    /////////////////
    // system user
    /////////////////


    /////////////////
    // start
    /////////////////

    @Binds
    @IntoMap
    @ViewModelKey(DeepLinkViewModel::class)
    abstract fun bindDeepLinkViewModel(viewModel: DeepLinkViewModel): ViewModel

    /////////////////
    // system menu_done
    /////////////////

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel

    /////////////////
    // system info
    /////////////////
    @Binds
    @IntoMap
    @ViewModelKey(InfoViewModel::class)
    abstract fun bindInfoViewModel(viewModel: InfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TermViewModel::class)
    abstract fun bindTermViewModel(viewModel: TermViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LicenseViewModel::class)
    abstract fun bindLicenseViewModel(viewModel: LicenseViewModel): ViewModel
    /////////////////
    // system error
    /////////////////
    @Binds
    @IntoMap
    @ViewModelKey(ErrorTrackerViewModel::class)
    abstract fun bindErrorTrackerViewModel(viewModel: ErrorTrackerViewModel): ViewModel

    /////////////////
    // system note
    /////////////////


    /////////////////
    // system file
    /////////////////
    @Binds
    @IntoMap
    @ViewModelKey(FileDetailViewModel::class)
    abstract fun bindFileDetailViewModel(viewModel: FileDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FolderManagerViewModel::class)
    abstract fun bindFolderManagerViewModel(viewModel: FolderManagerViewModel): ViewModel

    /////////////////
    // Service
    /////////////////

    @Binds
    @IntoMap
    @ViewModelKey(SyncServiceViewModel::class)
    abstract fun bindSyncServiceViewModel(viewModel: SyncServiceViewModel): ViewModel


    /////////////////
    // dialog
    /////////////////
    @Binds
    @IntoMap
    @ViewModelKey(OptionViewModel::class)
    abstract fun bindOptionViewModel(viewModel: OptionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OrderViewModel::class)
    abstract fun bindOrderViewModel(viewModel: OrderViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingViewModel::class)
    abstract fun bindSettingViewModel(viewModel: SettingViewModel): ViewModel

}