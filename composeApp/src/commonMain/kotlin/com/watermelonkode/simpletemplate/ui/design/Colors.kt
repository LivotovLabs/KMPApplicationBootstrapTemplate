package com.watermelonkode.simpletemplate.ui.design

import androidx.compose.ui.graphics.Color

/**
 * The application's colour palette.
 *
 * Every property here maps onto a Composables UI colour token, so filling this in is all it takes
 * to re-skin every stock component (`Button`, `TextField`, `AlertDialog`, ...) as well as your own.
 *
 * **This is the file to edit when you brand a new app.** Change the values in [AppLightPalette] and
 * [AppDarkPalette]; do not change the property names, as [AppTheme] binds them to the token names
 * the Composables UI components look up at composition time.
 *
 * The defaults reproduce the stock Composables UI look: a neutral grey surface set with a
 * black-on-white (inverted in dark mode) primary action.
 */
data class AppColorPalette(
    /** Background of a screen. */
    val background: Color,
    /** Content sitting directly on [background]. */
    val onBackground: Color,
    /** Background of raised containers: panels, cards, dialogs, sheets, menus. */
    val panel: Color,
    /** Content sitting directly on [panel]. */
    val onPanel: Color,
    /** Lower-emphasis content, e.g. captions and placeholders. */
    val muted: Color,
    /** Primary action colour. */
    val primary: Color,
    /** Content sitting directly on [primary]. */
    val onPrimary: Color,
    /** Secondary action colour. */
    val secondary: Color,
    /** Content sitting directly on [secondary]. */
    val onSecondary: Color,
    /** Background of controls, e.g. a progress bar track. */
    val control: Color,
    /** Content sitting directly on [control]. */
    val onControl: Color,
    /** Thumb of sliders and toggles. */
    val thumb: Color,
    /** Unselected switch track. */
    val switchTrack: Color,
    /** Selected switch track. */
    val switchSelectedTrack: Color,
    /** Switch thumb. */
    val switchThumb: Color,
    /** Background of a selected control, e.g. the active navigation bar item. */
    val selectedControl: Color,
    /** Content sitting directly on [selectedControl]. */
    val onSelectedControl: Color,
    /** Destructive action colour, e.g. delete. */
    val destructive: Color,
    /** Content sitting directly on [destructive]. */
    val onDestructive: Color,
    /** Borders and separators. */
    val border: Color,
    /** Background of input fields. */
    val field: Color,
    /** Content sitting directly on [field]. */
    val onField: Color,
    /** Scrim painted behind modal content. */
    val scrim: Color,
    /** Focus ring. */
    val ring: Color,
    /** Handle colour for selected text. */
    val textSelectionHandle: Color,
    /**
     * Solid hue for press/hover feedback on normal surfaces. No alpha of its own -- the platform
     * decides how strong the effect is. See [appPressIndication].
     */
    val pressHighlight: Color,
    /** Solid hue for press/hover feedback on primary and destructive surfaces. */
    val pressHighlightInverse: Color,
)

val AppLightPalette = AppColorPalette(
    background = Color(0xFFF4F4F4),
    onBackground = Color.Black,
    panel = Color.White,
    onPanel = Color.Black,
    muted = Color(0xFF777777),
    primary = Color.Black,
    onPrimary = Color.White,
    secondary = Color(0xFFF1F1F1),
    onSecondary = Color.Black,
    control = Color(0xFFE5E5E5),
    onControl = Color(0xFF777777),
    thumb = Color.White,
    switchTrack = Color(0xFFE4E4E7),
    switchSelectedTrack = Color(0xFF18181B),
    switchThumb = Color.White,
    selectedControl = Color(0xFFECECEC),
    onSelectedControl = Color.Black,
    destructive = Color(0xFFDC2626),
    onDestructive = Color.White,
    border = Color(0xFFE0E0E0),
    field = Color(0xFFF1F1F1),
    onField = Color.Black,
    scrim = Color.Black.copy(alpha = 0.38f),
    ring = Color.Black.copy(alpha = 0.24f),
    textSelectionHandle = Color.Black,
    pressHighlight = Color.Black,
    pressHighlightInverse = Color.White,
)

val AppDarkPalette = AppColorPalette(
    background = Color(0xFF0A0A0A),
    onBackground = Color(0xFFE5E5E5),
    panel = Color(0xFF171717),
    onPanel = Color(0xFFE5E5E5),
    muted = Color(0xFFA3A3A3),
    primary = Color(0xFFF5F5F5),
    onPrimary = Color(0xFF171717),
    secondary = Color(0xFF262626),
    onSecondary = Color(0xFFE5E5E5),
    control = Color(0xFF262626),
    onControl = Color(0xFFA3A3A3),
    thumb = Color(0xFFE5E5E5),
    switchTrack = Color.White.copy(alpha = 0.05f),
    switchSelectedTrack = Color.White.copy(alpha = 0.25f),
    switchThumb = Color.White,
    selectedControl = Color(0xFF404040),
    onSelectedControl = Color(0xFFF5F5F5),
    destructive = Color(0xFFF87171),
    onDestructive = Color(0xFF171717),
    border = Color(0xFF404040),
    field = Color(0xFF262626),
    onField = Color(0xFFE5E5E5),
    scrim = Color.Black.copy(alpha = 0.48f),
    ring = Color(0xFFE5E5E5).copy(alpha = 0.28f),
    textSelectionHandle = Color(0xFFE5E5E5),
    pressHighlight = Color(0xFFE5E5E5),
    pressHighlightInverse = Color(0xFF171717),
)
