package com.watermelonkode.simpletemplate.data.service

import com.outsidesource.oskitkmp.outcome.unwrapOrNull
import com.outsidesource.oskitkmp.storage.IKmpKvStore
import com.outsidesource.oskitkmp.storage.IKmpKvStoreNode
import com.watermelonkode.simpletemplate.domain.model.settings.AppSettings
import com.watermelonkode.simpletemplate.domain.model.settings.ThemeMode
import com.watermelonkode.simpletemplate.domain.service.AppSettingsService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class KVStoreBasedAppSettingsServiceImpl(
    private val storage: IKmpKvStore
) : AppSettingsService {

    private val keySettings = "settings"
    private val settingsFileName = "appsettings_v1"
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val node = CompletableDeferred<IKmpKvStoreNode?>()

    init {
        scope.launch {
            node.complete(storage.openNode(settingsFileName).unwrapOrNull())
        }
    }

    override suspend fun observeSettings(): Flow<AppSettings> {
        return (node.await()?.observeSerializable(keySettings, AppSettingsDto.serializer()) ?: emptyFlow()).map { dto ->
            (dto ?: AppSettingsDto()).toModel()
        }
    }

    override suspend fun updateSettings(value: AppSettings) {
        node.await()?.putSerializable(keySettings, AppSettingsDto.fromModel(value), AppSettingsDto.serializer())
    }

    override suspend fun clearSettings() {
        node.await()?.remove(keySettings)
    }
}

/**
 * Persisted shape of [AppSettings].
 *
 * Every field must keep a default: that is what lets settings blobs written by an older build of
 * the app still deserialize after a new field is added here.
 */
@Serializable
data class AppSettingsDto(
    val muted: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.System
) {
    fun toModel() = AppSettings(
        muted = muted,
        themeMode = themeMode
    )

    companion object {
        fun fromModel(value: AppSettings) = AppSettingsDto(
            muted = value.muted,
            themeMode = value.themeMode
        )
    }
}