package com.watermelonkode.simpletemplate

import com.outsidesource.oskitkmp.storage.KmpKvStore
import org.koin.core.module.Module
import org.koin.dsl.module

private val koin = initKoin(platformContext = PlatformContext()).koin

actual class PlatformContext

actual fun platformModule(platformContext: PlatformContext): Module = module {
    single { KmpKvStore() }
}

fun loadKoinSwiftModules() {
    koin.loadModules(
        listOf(
            module {
            },
        ),
    )
}
