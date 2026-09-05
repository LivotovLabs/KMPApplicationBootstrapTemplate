package com.watermelonkode.simpletemplate.ui.screen.details

import com.outsidesource.oskitkmp.interactor.Interactor
import com.watermelonkode.simpletemplate.ui.AppCoordinator

class DetailsScreenViewInteractor(
    private val id: String,
    private val coordinator: AppCoordinator
): Interactor<DetailsScreenState>(
    initialState = DetailsScreenState(id),
    dependencies = listOf()
) {

    /** Toolbar back arrow: step back one route. */
    fun onBackClicked() {
        coordinator.pop()
    }

    /** "Close" action: unwind the whole stack back to Home. */
    fun homeClicked() {
        coordinator.returnToHomeClicked()
    }
}

data class DetailsScreenState(
    val id: String
)
