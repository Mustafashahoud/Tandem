package com.mustafa.tandem.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mustafa.tandem.util.AppViewModelFactory
import com.mustafa.tandem.view.CommunityViewModel

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CommunityViewModel::class)
    abstract fun bindCommunityViewModel(communityViewModel: CommunityViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory
}