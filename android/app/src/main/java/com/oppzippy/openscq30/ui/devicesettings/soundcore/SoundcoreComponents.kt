package com.oppzippy.openscq30.ui.devicesettings.soundcore

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oppzippy.openscq30.R
import com.oppzippy.openscq30.ui.theme.SoundcoreBlue
import com.oppzippy.openscq30.ui.theme.SoundcoreBlueDark
import com.oppzippy.openscq30.ui.theme.SoundcoreGradientBottom
import com.oppzippy.openscq30.ui.theme.SoundcoreGradientBottomDark
import com.oppzippy.openscq30.ui.theme.SoundcoreGradientMid
import com.oppzippy.openscq30.ui.theme.SoundcoreGradientTop
import com.oppzippy.openscq30.ui.theme.SoundcoreGradientTopDark
import com.oppzippy.openscq30.ui.theme.SoundcoreGreen

@Composable
fun soundcoreIsDark(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

/** Brush used for selected chips / buttons / sliders. */
val SoundcoreBlueBrush: Brush = Brush.verticalGradient(listOf(Color(0xFF3D95FF), SoundcoreBlueDark))

@Composable
fun SoundcoreBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val brush = if (soundcoreIsDark()) {
        Brush.verticalGradient(listOf(SoundcoreGradientTopDark, SoundcoreGradientBottomDark))
    } else {
        Brush.verticalGradient(listOf(SoundcoreGradientTop, SoundcoreGradientMid, SoundcoreGradientBottom))
    }
    Box(modifier = modifier.background(brush), content = content)
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(22.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val dark = soundcoreIsDark()
    val fill = if (dark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.55f)
    val stroke = if (dark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.85f)
    var cardModifier: Modifier = modifier
        .clip(shape)
        .background(fill)
        .border(BorderStroke(1.dp, stroke), shape)
    if (onClick != null) {
        cardModifier = cardModifier.clickable(onClick = onClick)
    }
    Column(modifier = cardModifier, content = content)
}

@Composable
fun GlassSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = SoundcoreBlue,
            checkedBorderColor = Color.Transparent,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color(0xFFB4C0D4),
            uncheckedBorderColor = Color.Transparent,
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundcoreTopBar(
    title: @Composable () -> Unit,
    onBack: () -> Unit,
    @DrawableRes actionIcon: Int? = null,
    onAction: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = title,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back_24px),
                    contentDescription = stringResource(R.string.back),
                )
            }
        },
        actions = {
            if (actionIcon != null) {
                IconButton(onClick = onAction) {
                    Icon(
                        painter = painterResource(actionIcon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
        ),
    )
}

@Composable
fun SoundcoreWordmark() {
    Text(
        text = "soundcore",
        color = SoundcoreBlue,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
    )
}

data class BottomBarItem(@DrawableRes val icon: Int, @StringRes val label: Int)

@Composable
fun SoundcoreBottomBar(items: List<BottomBarItem>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            items.forEachIndexed { index, item ->
                val selected = index == selectedIndex
                val tint = if (selected) SoundcoreBlue else MaterialTheme.colorScheme.onBackground
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelect(index) }
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(28.dp),
                    )
                    Text(
                        text = stringResource(item.label),
                        color = tint,
                        fontSize = 13.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        Modifier
                            .width(56.dp)
                            .height(3.dp)
                            .clip(CircleShape)
                            .background(if (selected) SoundcoreBlue else Color.Transparent),
                    )
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

/** A glass row with an icon, title, optional subtitle and trailing switch. */
@Composable
fun SettingToggleRow(
    @DrawableRes icon: Int,
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    GlassCard(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = SoundcoreBlue,
                modifier = Modifier.size(30.dp),
            )
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                if (subtitle != null) {
                    Text(
                        subtitle,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            GlassSwitch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

/** A glass row with an icon, title, optional subtitle and a trailing chevron. */
@Composable
fun SettingNavRow(@DrawableRes icon: Int, title: String, subtitle: String? = null, onClick: () -> Unit) {
    GlassCard(Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = SoundcoreBlue,
                modifier = Modifier.size(30.dp),
            )
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                if (subtitle != null) {
                    Text(
                        subtitle,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Icon(
                painter = painterResource(R.drawable.ic_soundcore_chevron),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun BatteryTile(
    modifier: Modifier = Modifier,
    letter: String?,
    @DrawableRes icon: Int?,
    valueText: String,
    percent: Int?,
    label: String,
) {
    GlassCard(modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (letter != null) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(SoundcoreBlue),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(letter, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            } else if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = Color(0xFF2B3446),
                    modifier = Modifier.size(26.dp),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                BatteryGlyph(percent)
                Text(valueText, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            }
            Text(
                label,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun BatteryGlyph(percent: Int?) {
    val outline = MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(Modifier.size(width = 22.dp, height = 12.dp)) {
        val bodyW = size.width - 3.dp.toPx()
        val corner = CornerRadius(3.dp.toPx())
        drawRoundRect(
            color = outline.copy(alpha = 0.55f),
            size = Size(bodyW, size.height),
            cornerRadius = corner,
            style = Stroke(1.dp.toPx()),
        )
        val fraction = ((percent ?: 100).coerceIn(0, 100)) / 100f
        val inset = 2.dp.toPx()
        drawRoundRect(
            color = SoundcoreGreen,
            topLeft = Offset(inset, inset),
            size = Size((bodyW - 2 * inset) * fraction, size.height - 2 * inset),
            cornerRadius = CornerRadius(1.5.dp.toPx()),
        )
        drawRoundRect(
            color = outline.copy(alpha = 0.55f),
            topLeft = Offset(bodyW + 0.5.dp.toPx(), size.height * 0.3f),
            size = Size(2.dp.toPx(), size.height * 0.4f),
            cornerRadius = CornerRadius(1.dp.toPx()),
        )
    }
}

/** Stylised vector illustration of a pair of earbuds sitting above their charging case. */
@Composable
fun EarbudsIllustration(modifier: Modifier = Modifier) {
    val dark = soundcoreIsDark()
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f

        // soft glow + ring behind the product
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = if (dark) 0.15f else 0.9f),
                    SoundcoreBlue.copy(alpha = 0.18f),
                    Color.Transparent,
                ),
                center = Offset(cx, h * 0.5f),
                radius = h * 0.62f,
            ),
            radius = h * 0.62f,
            center = Offset(cx, h * 0.5f),
        )
        drawCircle(
            color = Color.White.copy(alpha = if (dark) 0.15f else 0.55f),
            radius = h * 0.46f,
            center = Offset(cx, h * 0.5f),
            style = Stroke(2.dp.toPx()),
        )

        val bodyBrush = Brush.verticalGradient(listOf(Color(0xFF39445A), Color(0xFF111827)))

        // charging case
        val caseW = w * 0.46f
        val caseH = h * 0.36f
        val caseTop = h * 0.60f
        val caseLeft = cx - caseW / 2f
        drawRoundRect(
            brush = bodyBrush,
            topLeft = Offset(caseLeft, caseTop),
            size = Size(caseW, caseH),
            cornerRadius = CornerRadius(caseH * 0.45f),
        )
        // lid seam
        drawRoundRect(
            color = Color.White.copy(alpha = 0.12f),
            topLeft = Offset(caseLeft + caseW * 0.04f, caseTop + caseH * 0.42f),
            size = Size(caseW * 0.92f, 1.5.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx()),
        )
        // logo mark
        drawCircle(
            color = Color.White.copy(alpha = 0.35f),
            radius = caseH * 0.12f,
            center = Offset(cx, caseTop + caseH * 0.24f),
            style = Stroke(2.dp.toPx()),
        )
        // status LED
        drawRoundRect(
            color = SoundcoreBlue,
            topLeft = Offset(cx - w * 0.025f, caseTop + caseH * 0.72f),
            size = Size(w * 0.05f, 3.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx()),
        )

        // earbuds
        fun bud(centerX: Float, tiltDegrees: Float) {
            val headW = w * 0.15f
            val headH = h * 0.26f
            val headTop = h * 0.10f
            rotate(degrees = tiltDegrees, pivot = Offset(centerX, headTop + headH / 2f)) {
                // stem
                drawRoundRect(
                    brush = bodyBrush,
                    topLeft = Offset(centerX - headW * 0.22f, headTop + headH * 0.55f),
                    size = Size(headW * 0.44f, h * 0.30f),
                    cornerRadius = CornerRadius(headW * 0.22f),
                )
                // head
                drawOval(
                    brush = bodyBrush,
                    topLeft = Offset(centerX - headW / 2f, headTop),
                    size = Size(headW, headH),
                )
                // touch-surface highlight
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = headW * 0.16f,
                    center = Offset(centerX, headTop + headH * 0.34f),
                    style = Stroke(1.5.dp.toPx()),
                )
            }
        }
        bud(cx - w * 0.17f, -8f)
        bud(cx + w * 0.17f, 8f)
    }
}
