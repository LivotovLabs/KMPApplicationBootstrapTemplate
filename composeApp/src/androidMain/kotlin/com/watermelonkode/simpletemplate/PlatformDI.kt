package com.watermelonkode.simpletemplate

import android.content.Context
import com.outsidesource.oskitkmp.storage.KmpKvStore
import com.watermelonkode.simpletemplate.data.service.AppInformationServiceImpl
import org.koin.dsl.bind
import org.koin.dsl.module

actual data class PlatformContext(val context: Context)

actual fun platformModule(platformContext: PlatformContext) = module {
    single { platformContext.context }
    single { KmpKvStore(appContext = platformContext.context) }
    single { AppInformationServiceImpl(platformContext.context, get()) } bind AppInformationServiceImpl::class
}
