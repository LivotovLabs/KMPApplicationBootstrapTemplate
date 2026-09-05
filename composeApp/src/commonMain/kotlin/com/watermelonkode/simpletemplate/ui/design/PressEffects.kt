package com.watermelonkode.simpletemplate.ui.design

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalHapticFeedback
import com.composables.compose.ripple.rememberRippleIndication
import kotlinx.coroutines.launch

/**
 * The press feedback for the current platform.
 *
 * This is the highest-leverage piece of platform feel in the app. Composables UI components resolve
 * their press effect from the theme's indication tokens, so returning the right [Indication] here
 * gives every stock `Button`, `IconButton`, `Tabs`, `NavigationBarItem` and menu row native
 * behaviour at once -- no component wrapping required.
 *
 * What each platform gets, and why:
 *
 * | Platform    | Effect                                        | Matches                          |
 * |-------------|-----------------------------------------------|----------------------------------|
 * | Android     | Material ripple expanding from the touch point | `android.R.attr.selectableItemBackground` |
 * | iOS         | Instant tint, slow fade out, no hover          | `UIControl` highlight            |
 * | Desktop/Web | Quick tint on hover, stronger on press         | CSS `:hover` / `:active`         |
 *
 * On Android this really is the Material ripple: `rememberRippleIndication` wraps
 * `androidx.compose.material.ripple`, so ripple radius, bounding and timing are the platform's own.
 *
 * @param color Solid hue for the effect, with no alpha of its own -- the per-platform alphas below
 *   are applied here. Pass [AppColorPalette.pressHighlight] on normal surfaces and
 *   [AppColorPalette.pressHighlightInverse] on primary or destructive ones.
 */
@Composable
internal fun appPressIndication(color: Color): Indication {
    val haptics = AppFeel.HAPTIC_FEEDBACK_ON_PRESS && appPlatform.isMobile

    return when (appPlatform) {
        AppPlatform.Android -> {
            val ripple = rememberRippleIndication(color = color, bounded = true)
            if (haptics) remember(ripple) { HapticIndication(ripple) } else ripple
        }

        // UIKit snaps to the highlighted state and eases out of it, and has no hover state at all.
        AppPlatform.IOS -> remember(color, haptics) {
            HighlightIndication(
                color = color,
                pressedAlpha = IOS_PRESSED_ALPHA,
                hoveredAlpha = 0f,
                fadeInMillis = 0,
                fadeOutMillis = 220,
                haptics = haptics,
            )
        }

        // Pointer platforms lead with hover, and both transitions are short.
        AppPlatform.Desktop, AppPlatform.Web -> remember(color) {
            HighlightIndication(
                color = color,
                pressedAlpha = POINTER_PRESSED_ALPHA,
                hoveredAlpha = POINTER_HOVERED_ALPHA,
                fadeInMillis = 70,
                fadeOutMillis = 120,
                haptics = false,
            )
        }
    }
}

private const val IOS_PRESSED_ALPHA = 0.14f
private const val POINTER_PRESSED_ALPHA = 0.14f
private const val POINTER_HOVERED_ALPHA = 0.07f

/**
 * Web convention: the cursor turns into a hand over anything clickable.
 *
 * Deliberately web-only -- macOS and Windows keep the arrow over buttons, and Composables UI pins
 * `PointerIcon.Default` on its own controls to match. `overrideDescendants` is what lets this win
 * over that inner declaration, so it must be passed as the component's `modifier`.
 *
 * Everywhere except web this returns the receiver untouched, leaving the library's arrow in place.
 */
fun Modifier.appPointerCursor(): Modifier =
    if (appPlatform == AppPlatform.Web) {
        pointerHoverIcon(PointerIcon.Hand, overrideDescendants = true)
    } else {
        this
    }

/**
 * Draws a flat translucent tint over the content while pressed or hovered.
 *
 * A [data class] because [IndicationNodeFactory] implementations are compared by equality to decide
 * whether the indication node can be reused across recompositions.
 */
private data class HighlightIndication(
    private val color: Color,
    private val pressedAlpha: Float,
    private val hoveredAlpha: Float,
    private val fadeInMillis: Int,
    private val fadeOutMillis: Int,
    private val haptics: Boolean,
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode = HighlightNode(
        interactionSource = interactionSource,
        color = color,
        pressedAlpha = pressedAlpha,
        hoveredAlpha = hoveredAlpha,
        fadeInMillis = fadeInMillis,
        fadeOutMillis = fadeOutMillis,
        haptics = haptics,
    )
}

private class HighlightNode(
    private val interactionSource: InteractionSource,
    private val color: Color,
    private val pressedAlpha: Float,
    private val hoveredAlpha: Float,
    private val fadeInMillis: Int,
    private val fadeOutMillis: Int,
    private val haptics: Boolean,
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {

    private val overlayAlpha = Animatable(0f)

    override fun onAttach() {
        coroutineScope.launch {
            // Counted rather than boolean: a control can legitimately see overlapping presses
            // (two fingers) or a hover that outlives a press.
            var presses = 0
            var hovers = 0

            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        presses++
                        if (haptics) {
                            currentValueOf(LocalHapticFeedback)
                                .performHapticFeedback(HapticFeedbackType.VirtualKey)
                        }
                    }
                    is PressInteraction.Release, is PressInteraction.Cancel -> presses--
                    is HoverInteraction.Enter -> hovers++
                    is HoverInteraction.Exit -> hovers--
                }

                val target = when {
                    presses > 0 -> pressedAlpha
                    hovers > 0 -> hoveredAlpha
                    else -> 0f
                }
                val durationMillis = if (target > overlayAlpha.value) fadeInMillis else fadeOutMillis

                // Animatable cancels any animation already in flight, so this cannot pile up.
                launch { overlayAlpha.animateTo(target, tween(durationMillis)) }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        val alpha = overlayAlpha.value
        if (alpha > 0f) {
            // Callers clip to their own shape before the indication runs (Composables UI applies
            // Modifier.clip(shape) ahead of clickable), so a plain rect takes the button's shape.
            drawRect(color = color.copy(alpha = alpha))
        }
    }
}

/**
 * Adds a haptic tick to another indication without changing how it looks.
 *
 * Used to give Android's Material ripple haptics: the ripple node is delegated to untouched, and a
 * second, draw-free node listens for presses alongside it.
 */
private data class HapticIndication(
    private val visual: IndicationNodeFactory,
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        HapticHostNode(interactionSource, visual)
}

private class HapticHostNode(
    private val interactionSource: InteractionSource,
    visual: IndicationNodeFactory,
) : DelegatingNode(), CompositionLocalConsumerModifierNode {

    init {
        delegate(visual.create(interactionSource))
    }

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Press) {
                    currentValueOf(LocalHapticFeedback)
                        .performHapticFeedback(HapticFeedbackType.VirtualKey)
                }
            }
        }
    }
}
