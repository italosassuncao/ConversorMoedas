package com.example.conversormoedas

import android.app.Application
import com.example.conversormoedas.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class QuotationApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@QuotationApplication)
            modules(appModule)
        }
    }

}