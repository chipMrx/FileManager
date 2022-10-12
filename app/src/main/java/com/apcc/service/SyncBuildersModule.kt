package com.apcc.service

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class SyncBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSyncDataService(): SyncDataService

}
