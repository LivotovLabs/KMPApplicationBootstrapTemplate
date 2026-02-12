package com.watermelonkode.simpletemplate.data.service

import android.content.Context
import android.content.pm.PackageManager
import com.watermelonkode.simpletemplate.domain.service.AppInformationService
import com.watermelonkode.simpletemplate.domain.service.LoggingService

actual class AppInformationServiceImpl(val context: Context, val logger: LoggingService) :
    AppInformationService {

    private val tag = "AppInformationService"

    actual override suspend fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "N/A"
        } catch (err: PackageManager.NameNotFoundException) {
            logger.error(tag, err) { "Failed to get app version: ${err.message}" }
            "N/A"
        }
    }

    actual override suspend fun getAppBuildNumber(): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionCode
        } catch (err: PackageManager.NameNotFoundException) {
            logger.error(tag, err) { "Failed to get app verion code: ${err.message}" }
            0
        }
    }
}