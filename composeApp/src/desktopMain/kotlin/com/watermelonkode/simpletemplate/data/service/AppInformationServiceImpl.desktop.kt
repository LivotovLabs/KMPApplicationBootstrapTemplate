package com.watermelonkode.simpletemplate.data.service

import com.watermelonkode.simpletemplate.BuildKonfig
import com.watermelonkode.simpletemplate.domain.service.AppInformationService
import com.watermelonkode.simpletemplate.domain.service.LoggingService

actual class AppInformationServiceImpl(val logger: LoggingService) : AppInformationService {
    actual override suspend fun getAppVersion(): String {
        return BuildKonfig.APP_VERSION
    }

    actual override suspend fun getAppBuildNumber(): Int {
        return BuildKonfig.APP_BUILD_NUMBER
    }
}
