package com.watermelonkode.simpletemplate.ui.screen.home

import com.outsidesource.oskitkmp.interactor.Interactor
import com.watermelonkode.simpletemplate.ui.AppCoordinator

class HomeScreenViewInteractor(
    private val coordinator: AppCoordinator
): Interactor<HomeScreenState>(
    initialState = HomeScreenState(),
    dependencies = listOf()
) {

    fun onDetailsClicked(id: String) {
        coordinator.detailsClicked(id)
    }
}

data class HomeScreenState(
    val greeting: String = "Hello KMP!"
)