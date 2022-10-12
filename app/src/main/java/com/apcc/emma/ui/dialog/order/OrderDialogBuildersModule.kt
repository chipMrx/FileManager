package com.apcc.emma.ui.dialog.order

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class OrderDialogBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeOrderDialog(): OrderDialog
}
