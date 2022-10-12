package com.apcc.emma.ui.file

import com.apcc.emma.ui.file.detail.FileDetailFragment
import com.apcc.emma.ui.file.local.FolderManagerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FileBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeFileDetailFragment(): FileDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeFolderManagerFragment(): FolderManagerFragment
}
