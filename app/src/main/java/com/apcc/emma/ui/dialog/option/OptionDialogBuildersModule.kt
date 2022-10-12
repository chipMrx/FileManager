package com.apcc.emma.ui.dialog.option

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class OptionDialogBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeOptionDialog(): OptionDialog
}
