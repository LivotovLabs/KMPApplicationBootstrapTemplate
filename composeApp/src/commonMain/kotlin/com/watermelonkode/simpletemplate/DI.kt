package com.watermelonkode.simpletemplate

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect fun platformModule(platformContext: PlatformContext): Module
expect class PlatformContext

fun initKoin(
    appDeclaration: KoinAppDeclaration = {},
    platformContext: PlatformContext,
    extraModules: Array<Module> = emptyArray(),
) = startKoin {
    appDeclaration()
    modules(commonModule(), platformModule(platformContext), *extraModules)
}

fun commonModule() = module {

}