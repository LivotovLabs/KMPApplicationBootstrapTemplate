package com.watermelonkode.simpletemplate.ui.screen.home

import com.outsidesource.oskitkmp.interactor.Interactor
import com.watermelonkode.simpletemplate.domain.interactor.AppSettingsInteractor
import com.watermelonkode.simpletemplate.domain.model.settings.ThemeMode
import com.watermelonkode.simpletemplate.ui.AppCoordinator

class HomeScreenViewInteractor(
    private val coordinator: AppCoordinator,
    private val settingsInteractor: AppSettingsInteractor
): Interactor<HomeScreenState>(
    initialState = HomeScreenState(),
    dependencies = listOf(settingsInteractor)
) {

    override fun computed(state: HomeScreenState): HomeScreenState =
        state.copy(themeMode = settingsInteractor.state.settings.themeMode)

    fun onDetailsClicked(id: String) {
        coordinator.detailsClicked(id)
    }

    /** Cycles System -> Light -> Dark -> System. */
    fun onThemeModeClicked() {
        val next = when (state.themeMode) {
            ThemeMode.System -> ThemeMode.Light
            ThemeMode.Light -> ThemeMode.Dark
            ThemeMode.Dark -> ThemeMode.System
        }
        settingsInteractor.setThemeMode(next)
    }
}

data class HomeScreenState(
    val greeting: String = "Hello KMP!",
    val themeMode: ThemeMode = ThemeMode.System
)
