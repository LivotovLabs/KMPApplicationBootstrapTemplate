package com.watermelonkode.simpletemplate

import com.watermelonkode.simpletemplate.data.core.KtorClient
import com.watermelonkode.simpletemplate.data.service.DiamondEdgeLoggingServiceImpl
import com.watermelonkode.simpletemplate.data.service.KVStoreBasedAppSettingsServiceImpl
import com.watermelonkode.simpletemplate.domain.service.AppSettingsService
import com.watermelonkode.simpletemplate.domain.service.LoggingService
import com.watermelonkode.simpletemplate.ui.AppCoordinator
import com.watermelonkode.simpletemplate.ui.AppInteractor
import com.watermelonkode.simpletemplate.ui.screen.details.DetailsScreenViewInteractor
import com.watermelonkode.simpletemplate.ui.screen.home.HomeScreenViewInteractor
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
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
    // Services
    single { DiamondEdgeLoggingServiceImpl() } bind LoggingService::class
    single { KVStoreBasedAppSettingsServiceImpl(get()) } bind AppSettingsService::class
    single { KtorClient(get()) }

    // UI Foundation
    single { AppCoordinator() }
    factory { params -> AppInteractor(params[0], get()) }

    // UI Screens
    factory { HomeScreenViewInteractor(get()) }
    factory { params -> DetailsScreenViewInteractor(params[0], get()) }
}