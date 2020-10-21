package com.mustafa.tandem.di

import com.mustafa.tandem.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class MainActivityModule {

  @ContributesAndroidInjector (modules = [FragmentBuildersModule::class])
  abstract fun contributeMainActivity(): MainActivity
}
