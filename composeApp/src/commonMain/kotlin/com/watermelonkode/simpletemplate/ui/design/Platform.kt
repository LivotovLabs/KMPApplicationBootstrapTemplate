package com.watermelonkode.simpletemplate.ui.design

import com.outsidesource.oskitkmp.lib.Platform
import com.outsidesource.oskitkmp.lib.current

/**
 * The host platform, collapsed to the four groups that actually want different UI conventions.
 *
 * This is the single switch the whole design package branches on. It is resolved from OSKit's
 * [Platform] rather than `expect`/`actual` on purpose: everything Composables UI needs is available
 * in `commonMain`, so keeping the decisions in one readable file beats spreading near-identical
 * `actual`s across four source sets. `AppCoordinator` already uses `Platform.current` the same way.
 *
 * Note this is deliberately *not* the same thing as `LocalInteractionMode`, which describes the
 * current **input device** (finger vs pointer). An Android tablet driven by a mouse is still
 * `Android` and should still show a Material ripple, while its components may size themselves for
 * a pointer. Use this for "what do users of this OS expect", and interaction mode for "how big
 * should a tap target be right now".
 */
enum class AppPlatform {
    Android,
    IOS,
    Desktop,
    Web,
    ;

    /** Touch-first platforms: system bars and safe areas to avoid, no hover, haptics available. */
    val isMobile get() = this == Android || this == IOS

    /** Mouse/trackpad-first platforms, where hover states are meaningful and density is higher. */
    val isPointerFirst get() = this == Desktop || this == Web
}

val appPlatform: AppPlatform = when (Platform.current) {
    Platform.Android -> AppPlatform.Android
    Platform.IOS -> AppPlatform.IOS
    Platform.MacOS, Platform.Windows, Platform.Linux -> AppPlatform.Desktop
    Platform.WebBrowser -> AppPlatform.Web
    // Unknown only happens on an unrecognised JVM host, which in practice is a desktop.
    Platform.Unknown -> AppPlatform.Desktop
}

/**
 * Switches for platform behaviours that are a matter of taste rather than convention.
 *
 * Kept as plain constants so turning one off is a one-line edit with no call sites to chase.
 */
object AppFeel {

    /**
     * Fire a short haptic tick when a control is pressed.
     *
     * Real on Android and iOS; a no-op on desktop and web, where Compose has no haptics. Set to
     * `false` if you would rather not vibrate on every tap -- some users and reviewers dislike it,
     * and it costs battery.
     */
    const val HAPTIC_FEEDBACK_ON_PRESS = true
}
