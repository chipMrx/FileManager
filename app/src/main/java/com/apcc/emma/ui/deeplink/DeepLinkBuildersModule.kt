package com.apcc.emma.ui.deeplink

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class DeepLinkBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeDeepLinkFragment(): DeepLinkFragment


}
