package com.italo.conversormoedas

import android.app.Application
import com.italo.conversormoedas.di.appModule
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