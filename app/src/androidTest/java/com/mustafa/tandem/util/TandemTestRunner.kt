package com.mustafa.tandem.util

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.mustafa.tandem.TestTandemApp


class TandemTestRunner : AndroidJUnitRunner() {
    @Throws(
        InstantiationException::class,
        IllegalAccessException::class,
        ClassNotFoundException::class
    )
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestTandemApp::class.java.name, context)
    }
}