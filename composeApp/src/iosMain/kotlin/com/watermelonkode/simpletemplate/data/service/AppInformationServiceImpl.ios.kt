package com.watermelonkode.simpletemplate.data.service

import com.watermelonkode.simpletemplate.domain.service.AppInformationService
import com.watermelonkode.simpletemplate.domain.service.LoggingService
import platform.Foundation.NSBundle

actual class AppInformationServiceImpl(val logger: LoggingService) : AppInformationService {
    actual override suspend fun getAppVersion(): String {
        return NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String ?: "Unknown"
    }

    actual override suspend fun getAppBuildNumber(): Int {
        val buildString = NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion") as? String
        return buildString?.toIntOrNull() ?: 0
    }
}