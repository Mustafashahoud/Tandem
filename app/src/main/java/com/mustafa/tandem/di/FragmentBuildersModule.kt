package com.mustafa.tandem.di

import com.mustafa.tandem.view.CommunityFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeMCommunityFragment(): CommunityFragment
}