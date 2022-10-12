package com.apcc.emma.ui.error

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ErrorTrackerBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeErrorTrackerFragment(): ErrorTrackerFragment


}
