package com.mustafa.tandem.di

import android.app.Application
import com.mustafa.tandem.TandemApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule ::class,
    MainActivityModule::class,
    AppModule::class])
interface AppComponent {

    fun inject(tandemApp: TandemApp)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }

}
