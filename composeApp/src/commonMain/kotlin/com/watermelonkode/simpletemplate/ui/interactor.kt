package com.watermelonkode.simpletemplate.ui

import com.outsidesource.oskitkmp.interactor.Interactor

class AppInteractor(
    deepLink: String?,
    private val coordinator: AppCoordinator) : Interactor<AppInteractorState>(
    initialState = AppInteractorState(),
    dependencies = listOf()
) {
    init {
        coordinator.handleDeepLink(deepLink)
    }

}

data class AppInteractorState(
    val sampleProperty: Boolean = false,
)
