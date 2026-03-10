package com.watermelonkode.simpletemplate.data.service

import com.watermelonkode.simpletemplate.domain.service.AppInformationService

/**
 * Implementation of the `AppInformationService` for retrieving application-related metadata.
 *
 * This class is responsible for providing information about the application's version
 * and build number. It utilizes platform-specific context to fetch the required data.
 *
 * Usage of this class requires handling platform-specific implementations as defined
 * in the `expect` class declaration.
 *
 * Functions:
 * - `getAppVersion()`: Provides the application's version string.
 * - `getAppBuildNumber()`: Retrieves the application's build number as an integer.
 */
expect class AppInformationServiceImpl: AppInformationService {
    override suspend fun getAppVersion(): String
    override suspend fun getAppBuildNumber(): Int
}