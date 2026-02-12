package com.watermelonkode.simpletemplate

import com.outsidesource.oskitkmp.storage.KmpKvStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual class PlatformContext

actual fun platformModule(platformContext: PlatformContext): Module = module {
    single { KmpKvStore() }
}
