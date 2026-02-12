package com.watermelonkode.simpletemplate.domain.service

/**
 * Service interface for retrieving application-related metadata.
 *
 * This service provides methods to access information about the application's
 * version and build number. It acts as an abstraction layer to obtain these
 * details, which may vary based on the platform or implementation.
 *
 * Functions:
 * - `getAppVersion()`: Retrieves the application's version as a string.
 * - `getAppBuildNumber()`: Retrieves the application's build number as an integer.
 */
interface AppInformationService {

    /**
     * Retrieves the application's current version as a string.
     *
     * This method is used to obtain the version number of the application. The
     * version information can vary depending on the platform or implementation
     * of the service.
     *
     * @return A string representing the application's version.
     */
    suspend fun getAppVersion(): String

    /**
     * Retrieves the application's build number as an integer.
     *
     * This method is used to obtain the current build number of the application,
     * which is typically a numeric value associated with a specific release or
     * build of the app.
     *
     * @return An integer representing the application's build number.
     */
    suspend fun getAppBuildNumber(): Int

}