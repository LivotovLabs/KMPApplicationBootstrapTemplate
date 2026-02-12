package com.watermelonkode.simpletemplate.domain.interactor

import com.outsidesource.oskitkmp.interactor.Interactor
import com.watermelonkode.simpletemplate.domain.model.settings.AppSettings
import com.watermelonkode.simpletemplate.domain.service.AppInformationService
import com.watermelonkode.simpletemplate.domain.service.AppSettingsService
import kotlinx.coroutines.launch

/**
 * Interactor responsible for managing and updating the application's settings.
 *
 * This class interacts with services to retrieve application settings and information
 * such as app version and build number. It also provides methods for modifying various
 * user-configurable settings.
 *
 * @constructor
 * @param settingsService A service for observing and updating application settings.
 * @param appInfoService A service for retrieving application version and build information.
 */
class AppSettingsInteractor(
    private val settingsService: AppSettingsService,
    private val appInfoService: AppInformationService
) : Interactor<AppSettingsInteractorState>(
    initialState = AppSettingsInteractorState(),
    dependencies = listOf()
) {

    init {
        interactorScope.launch {
            val version = appInfoService.getAppVersion()
            val buildNumber = appInfoService.getAppBuildNumber()
            update { state -> state.copy(appVersion = version, appBuildNumber = buildNumber) }

            settingsService.observeSettings().collect { settings ->
                update { state -> state.copy(settings = settings) }
            }
        }
    }

}

/**
 * Represents the state of the AppSettingsInteractor, encapsulating the configuration
 * settings for the application and metadata such as the application version and build number.
 *
 * This state is used to manage and monitor the application's customizable settings and
 * general application metadata, providing a centralized representation of these properties.
 *
 * @property settings The current application settings, including user preferences and configurations.
 * @property appVersion The version of the application, represented as a string in the format "major.minor.patch".
 * @property appBuildNumber The build number of the application, typically incremented with every build.
 */
data class AppSettingsInteractorState(
    val settings: AppSettings = AppSettings(),
    val appVersion: String = "0.0.0",
    val appBuildNumber: Int = 0
)
