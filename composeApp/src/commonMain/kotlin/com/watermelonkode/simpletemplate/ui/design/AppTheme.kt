package com.watermelonkode.simpletemplate.ui.design

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.composables.compose.ripple.rememberRippleIndication
import com.composables.interactioncapabilities.currentInteractionCapabilities
import com.composables.ui.theme.ColorScheme
import com.composables.ui.theme.InteractionMode
import com.composables.ui.theme.LocalColorScheme
import com.composables.ui.theme.LocalInteractionMode
import com.composables.ui.theme.alphas
import com.composables.ui.theme.backgroundColor
import com.composables.ui.theme.borderColor
import com.composables.ui.theme.buttonShape
import com.composables.ui.theme.colors
import com.composables.ui.theme.controlColor
import com.composables.ui.theme.destructiveColor
import com.composables.ui.theme.dialogShape
import com.composables.ui.theme.disabledAlpha
import com.composables.ui.theme.fieldColor
import com.composables.ui.theme.fieldShape
import com.composables.ui.theme.indications
import com.composables.ui.theme.inverseIndication
import com.composables.ui.theme.largeShape
import com.composables.ui.theme.mediumShape
import com.composables.ui.theme.menuShape
import com.composables.ui.theme.mutedColor
import com.composables.ui.theme.onBackgroundColor
import com.composables.ui.theme.onControlColor
import com.composables.ui.theme.onDestructiveColor
import com.composables.ui.theme.onFieldColor
import com.composables.ui.theme.onPanelColor
import com.composables.ui.theme.onPrimaryColor
import com.composables.ui.theme.onSecondaryColor
import com.composables.ui.theme.onSelectedControlColor
import com.composables.ui.theme.overlayShadow
import com.composables.ui.theme.panelColor
import com.composables.ui.theme.primaryColor
import com.composables.ui.theme.raisedShadow
import com.composables.ui.theme.ringColor
import com.composables.ui.theme.scrimColor
import com.composables.ui.theme.secondaryColor
import com.composables.ui.theme.selectedControlColor
import com.composables.ui.theme.shadows
import com.composables.ui.theme.shapes
import com.composables.ui.theme.sheetShape
import com.composables.ui.theme.smallShape
import com.composables.ui.theme.switchSelectedTrackColor
import com.composables.ui.theme.switchThumbColor
import com.composables.ui.theme.switchTrackColor
import com.composables.ui.theme.textSelectionColors
import com.composables.ui.theme.thumbColor
import com.composeunstyled.theme.ThemeComposable
import com.composeunstyled.theme.buildTheme
import com.watermelonkode.simpletemplate.domain.model.settings.ThemeMode

/**
 * The application's theme. Wrap the whole UI tree in it once, in
 * [com.watermelonkode.simpletemplate.ui.App].
 *
 * It is a full replacement for Composables UI's own `ComposablesTheme`, not a wrapper around it:
 * `buildTheme {}` replaces the ambient theme wholesale, so themes cannot be nested to merge
 * tokens, and the library exposes no API for overriding a single token. Defining our own is
 * therefore the supported way to own the palette -- and it is what the Composables UI docs
 * recommend. Because [AppThemeDefinition] populates the library's own token identities, every
 * stock component (`Button`, `TextField`, `AlertDialog`, ...) resolves against our values.
 *
 * To restyle the app, edit [AppColorPalette], [AppShapes], [appTypography] and [appSpacing] --
 * not this file.
 *
 * @param themeMode Which colour scheme to use. Comes from the persisted user setting; see
 *   [com.watermelonkode.simpletemplate.domain.interactor.AppSettingsInteractor].
 */
@Composable
fun AppTheme(
    themeMode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit,
) {
    val useDarkColors = when (themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    // Provided outside the theme definition on purpose: the definition reads LocalColorScheme to
    // pick a palette, and Composables UI components read it too.
    CompositionLocalProvider(
        LocalColorScheme provides if (useDarkColors) ColorScheme.Dark else ColorScheme.Light,
    ) {
        AppThemeDefinition { content() }
    }
}

/**
 * The token definitions behind [AppTheme].
 *
 * If a future Composables UI release adds a token that is missing here, `Theme[property][token]`
 * throws at composition time with a message naming the missing token -- so the gap shows up on the
 * first render rather than silently.
 */
internal val AppThemeDefinition: ThemeComposable = buildTheme {
    name = "AppTheme"

    val useDarkColors = LocalColorScheme.current == ColorScheme.Dark
    val palette = if (useDarkColors) AppDarkPalette else AppLightPalette

    // Components resize for the current input method: rounder and roomier under a finger, tighter
    // under a mouse pointer. An explicit LocalInteractionMode wins, so a screen can force either.
    val interactionMode = LocalInteractionMode.current
        ?: if (currentInteractionCapabilities().hasPointer) InteractionMode.Pointer else InteractionMode.Touch
    val useTouchSizes = interactionMode == InteractionMode.Touch
    val appShapes = if (useTouchSizes) AppTouchShapes else AppPointerShapes

    properties[colors] = mapOf(
        backgroundColor to animatedColor(useDarkColors, palette.background),
        onBackgroundColor to animatedColor(useDarkColors, palette.onBackground),
        panelColor to animatedColor(useDarkColors, palette.panel),
        onPanelColor to animatedColor(useDarkColors, palette.onPanel),
        mutedColor to animatedColor(useDarkColors, palette.muted),
        primaryColor to animatedColor(useDarkColors, palette.primary),
        onPrimaryColor to animatedColor(useDarkColors, palette.onPrimary),
        secondaryColor to animatedColor(useDarkColors, palette.secondary),
        onSecondaryColor to animatedColor(useDarkColors, palette.onSecondary),
        controlColor to animatedColor(useDarkColors, palette.control),
        onControlColor to animatedColor(useDarkColors, palette.onControl),
        thumbColor to animatedColor(useDarkColors, palette.thumb),
        switchTrackColor to animatedColor(useDarkColors, palette.switchTrack),
        switchSelectedTrackColor to animatedColor(useDarkColors, palette.switchSelectedTrack),
        switchThumbColor to palette.switchThumb,
        selectedControlColor to animatedColor(useDarkColors, palette.selectedControl),
        onSelectedControlColor to animatedColor(useDarkColors, palette.onSelectedControl),
        destructiveColor to palette.destructive,
        onDestructiveColor to palette.onDestructive,
        borderColor to animatedColor(useDarkColors, palette.border),
        fieldColor to animatedColor(useDarkColors, palette.field),
        onFieldColor to animatedColor(useDarkColors, palette.onField),
        scrimColor to palette.scrim,
        ringColor to animatedColor(useDarkColors, palette.ring),
    )

    properties[shapes] = mapOf(
        smallShape to appShapes.small,
        mediumShape to appShapes.medium,
        largeShape to appShapes.large,
        buttonShape to appShapes.button,
        dialogShape to appShapes.dialog,
        sheetShape to appShapes.sheet,
        menuShape to appShapes.menu,
        fieldShape to appShapes.field,
    )

    properties[shadows] = mapOf(
        raisedShadow to Shadow(
            radius = 16.dp,
            color = Color.Black,
            offset = DpOffset(x = 0.dp, y = 4.dp),
            alpha = 0.14f,
        ),
        overlayShadow to Shadow(
            radius = 24.dp,
            color = Color.Black,
            offset = DpOffset(x = 0.dp, y = 8.dp),
            alpha = if (useDarkColors) 0.36f else 0.22f,
        ),
    )

    properties[alphas] = mapOf(disabledAlpha to 0.33f)

    val selectionColors = TextSelectionColors(
        handleColor = palette.textSelectionHandle,
        backgroundColor = palette.textSelectionHandle.copy(alpha = 0.24f),
    )
    // Fully qualified: inside buildTheme {} the bare names resolve to the ThemeBuilder properties
    // of the same name, not to these tokens.
    properties[textSelectionColors] =
        mapOf(com.composables.ui.theme.defaultTextSelectionColors to selectionColors)

    val pressIndication = rememberRippleIndication(
        if (useDarkColors) Color(0xFFE5E5E5).copy(alpha = 0.10f) else Color.Black.copy(alpha = 0.08f),
    )
    // For content on inverse or high-contrast surfaces, e.g. inside a primary button.
    val pressIndicationOnInverse = rememberRippleIndication(
        if (useDarkColors) Color(0xFF171717).copy(alpha = 0.10f) else Color.White.copy(alpha = 0.12f),
    )
    properties[indications] = mapOf(
        com.composables.ui.theme.defaultIndication to pressIndication,
        inverseIndication to pressIndicationOnInverse,
    )

    // App-specific properties, on top of the tokens Composables UI defines.
    properties[typography] = appTypography(useTouchSizes)
    properties[spacing] = appSpacing

    defaultTextStyle = appBodyTextStyle(useTouchSizes)
    // Composables UI's own theme leaves this Unspecified, which makes a bare Text render
    // unpredictably. Anchoring it to the background's content colour is a saner default.
    defaultContentColor = palette.onBackground
    defaultIndication = pressIndication
    defaultTextSelectionColors = selectionColors

    extend { themedContent ->
        CompositionLocalProvider(LocalInteractionMode provides interactionMode) {
            themedContent()
        }
    }
}

/** Crossfades a colour when the light/dark scheme flips, so the switch does not snap. */
@Composable
private fun animatedColor(useDarkColors: Boolean, target: Color): Color = animateColorAsState(
    targetValue = target,
    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
    label = if (useDarkColors) "darkColor" else "lightColor",
).value
