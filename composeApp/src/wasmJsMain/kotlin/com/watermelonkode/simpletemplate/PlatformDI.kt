package com.watermelonkode.simpletemplate

import com.outsidesource.oskitkmp.storage.KmpKvStore
import com.watermelonkode.simpletemplate.data.service.AppInformationServiceImpl
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual class PlatformContext

actual fun platformModule(platformContext: PlatformContext): Module = module {
    single { KmpKvStore() }
    single { AppInformationServiceImpl(get()) } bind AppInformationServiceImpl::class
}
