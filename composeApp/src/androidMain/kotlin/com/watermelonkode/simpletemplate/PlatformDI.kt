package com.watermelonkode.simpletemplate

import android.content.Context
import com.outsidesource.oskitkmp.storage.KmpKvStore
import org.koin.dsl.module

actual data class PlatformContext(val context: Context)

actual fun platformModule(platformContext: PlatformContext) = module {
    single { platformContext.context }
    single { KmpKvStore(appContext = platformContext.context) }
}
