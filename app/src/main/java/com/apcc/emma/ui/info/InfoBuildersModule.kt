package com.apcc.emma.ui.info

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.apcc.emma.ui.info.app.InfoFragment
import com.apcc.emma.ui.info.license.LicenseFragment
import com.apcc.emma.ui.info.term.TermFragment

@Suppress("unused")
@Module
abstract class InfoBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeInfoFragment(): InfoFragment

    @ContributesAndroidInjector
    abstract fun contributeTermFragment(): TermFragment

    @ContributesAndroidInjector
    abstract fun contributeLicenseFragment(): LicenseFragment

}
