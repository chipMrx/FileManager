package com.apcc.emma.ui.search

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class SearchBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment
}
