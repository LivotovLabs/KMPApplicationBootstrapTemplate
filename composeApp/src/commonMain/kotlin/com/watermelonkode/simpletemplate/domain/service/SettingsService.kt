package com.watermelonkode.simpletemplate.domain.service

import com.watermelonkode.simpletemplate.domain.model.settings.AppSettings
import kotlinx.coroutines.flow.Flow

/**
 * Service interface for managing the general application settings.
 *
 * This service defines methods for observing and updating general application
 * settings. It provides functionality to track changes to settings over time using.
 *
 * Functions:
 * - `observeSettings()`: Observe changes to the application's general settings.
 * - `updateSettings(value)`: Update the application's general settings.
 */
interface AppSettingsService {

    /**
     * Observes changes to the application's general settings.
     *
     * This method provides a reactive flow of the current application settings, allowing
     * clients to monitor updates and respond to changes in real-time. It emits the latest
     * `AppSettings` whenever there is a modification.
     *
     * @return A flow emitting the current state of `AppSettings` whenever changes occur.
     */
    suspend fun observeSettings(): Flow<AppSettings>

    /**
     * Updates the application's general settings.
     *
     * This method modifies the current application settings by accepting a new
     * `AppSettings` instance. It replaces the previous settings with the provided values.
     *
     * @param value The new application settings to be applied.
     */
    suspend fun updateSettings(value: AppSettings)

    /**
     * Clears app general settings.
     *
     * This method clears the general settings information, resetting it to the
     * default (clean app install) state.
     */
    suspend fun clearSettings()
}