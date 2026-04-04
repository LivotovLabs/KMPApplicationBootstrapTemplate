package com.watermelonkode.simpletemplate.ui.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.watermelonkode.simpletemplate.ui.design.AppTheme

@Composable
fun AppBaseButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(AppTheme.dimensions.cornerRadius))
            .background(AppTheme.colors.primary)
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.dimensions.elementPadding, vertical = AppTheme.dimensions.smallPadding)
    ) {
        Text(
            text = label,
            color = AppTheme.colors.onPrimary,
            style = AppTheme.typography.labelLarge
        )
    }
}
