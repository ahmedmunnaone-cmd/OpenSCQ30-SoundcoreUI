package com.oppzippy.openscq30.ui.devicesettings.soundcore

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oppzippy.openscq30.R
import com.oppzippy.openscq30.lib.bindings.translateSettingId
import com.oppzippy.openscq30.lib.wrapper.Setting
import com.oppzippy.openscq30.lib.wrapper.Value
import com.oppzippy.openscq30.lib.wrapper.toValue
import com.oppzippy.openscq30.ui.theme.SoundcoreBlue
import com.oppzippy.openscq30.ui.utils.throttledState
import java.util.Locale
import kotlin.math.roundToInt

private const val PRESET_ID = "presetEqualizerProfile"
private const val VOLUME_ADJUSTMENTS_ID = "volumeAdjustments"

/** Toggles that are shown on the Sound tab (so they are not repeated in All Settings). */
val SoundTabToggleIds: Set<String> = setOf("surroundSound", "immersiveExperience", "soundLeakCompensation")

@StringRes
fun toggleSubtitle(settingId: String): Int? = when (settingId) {
    "dualConnections" -> R.string.soundcore_sub_dual
    "ldac" -> R.string.soundcore_sub_ldac
    "gamingMode" -> R.string.soundcore_sub_gaming
    "windNoiseSuppression" -> R.string.soundcore_sub_wind
    "surroundSound", "immersiveExperience" -> R.string.soundcore_sub_immersive
    else -> null
}

@DrawableRes
fun toggleIcon(settingId: String): Int = when (settingId) {
    "dualConnections" -> R.drawable.devices_other_24px
    "ldac" -> R.drawable.headphones_24px
    "gamingMode" -> R.drawable.bolt_24px
    "windNoiseSuppression" -> R.drawable.speaker_24px
    "surroundSound", "immersiveExperience" -> R.drawable.ic_soundcore_anc
    "touchTone", "lowBatteryPrompt", "voicePrompt", "wearingTone" -> R.drawable.notifications_24px
    else -> R.drawable.settings_24px
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SoundcoreSoundTab(
    equalizerSettings: List<Pair<String, Setting>>,
    allSettings: List<Pair<String, Setting>>,
    setSettings: (List<Pair<String, Value>>) -> Unit,
    onOpenCustomEqualizer: () -> Unit,
) {
    val preset = equalizerSettings.find { it.first == PRESET_ID }?.second as? Setting.PresetEqualizerProfileSelect
    val bands = equalizerSettings.find { it.first == VOLUME_ADJUSTMENTS_ID }?.second as? Setting.EqualizerSetting
    val hasEqualizer = equalizerSettings.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (hasEqualizer) {
            SettingNavRow(
                icon = R.drawable.equalizer_24px,
                title = stringResource(R.string.equalizer),
                subtitle = stringResource(R.string.soundcore_customize_sound),
                onClick = onOpenCustomEqualizer,
            )

            if (preset != null || bands != null) {
                GlassCard(Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (preset != null) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                preset.select.options.forEachIndexed { index, option ->
                                    PresetChip(
                                        label = preset.select.localizedOptions.getOrElse(index) { option },
                                        selected = option == preset.value,
                                        onClick = { setSettings(listOf(PRESET_ID to option.toValue())) },
                                    )
                                }
                            }
                        }
                        if (bands != null) {
                            EqualizerBands(
                                setting = bands,
                                onChange = { setSettings(listOf(VOLUME_ADJUSTMENTS_ID to it.toValue())) },
                            )
                            if (bands.readOnly) {
                                Text(
                                    text = stringResource(R.string.soundcore_eq_locked),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            }
        }

        allSettings
            .filter { (id, setting) -> id in SoundTabToggleIds && setting is Setting.ToggleSetting }
            .forEach { (id, setting) ->
                SettingToggleRow(
                    icon = toggleIcon(id),
                    title = translateSettingId(id),
                    subtitle = toggleSubtitle(id)?.let { stringResource(it) },
                    checked = (setting as Setting.ToggleSetting).value,
                    onCheckedChange = { setSettings(listOf(id to it.toValue())) },
                )
            }

        if (hasEqualizer) {
            SettingNavRow(
                icon = R.drawable.ic_soundcore_tune,
                title = stringResource(R.string.soundcore_custom_eq),
                subtitle = stringResource(R.string.soundcore_advanced_settings),
                onClick = onOpenCustomEqualizer,
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun PresetChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    val background = if (selected) {
        Modifier.background(SoundcoreBlueBrush, shape)
    } else {
        Modifier.background(Color.White.copy(alpha = if (soundcoreIsDark()) 0.10f else 0.6f), shape)
    }
    Text(
        text = label,
        color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground,
        fontSize = 15.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        modifier = Modifier
            .clip(shape)
            .then(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
    )
}

private fun formatHz(hz: Int): String = if (hz < 1000) {
    "${hz}Hz"
} else if (hz % 1000 == 0) {
    "${hz / 1000}kHz"
} else {
    String.format(Locale.ROOT, "%.1f", hz / 1000.0).trimEnd('0').trimEnd('.') + "kHz"
}

private val SliderPadding = 14.dp

@Composable
private fun EqualizerBands(setting: Setting.EqualizerSetting, onChange: (List<Short>) -> Unit) {
    val equalizer = setting.setting
    val count = minOf(equalizer.bandHz.size, setting.value.size)
    if (count == 0) return
    val min = equalizer.min.toFloat()
    val max = equalizer.max.toFloat()
    val enabled = !setting.readOnly
    val curveColor = SoundcoreBlue.copy(alpha = 0.45f)
    val zeroLineColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(180.dp),
        ) {
            // connecting curve through the thumbs, plus a faint 0 dB line
            Canvas(Modifier.fillMaxSize()) {
                val pad = SliderPadding.toPx()
                val usable = size.height - 2 * pad
                val zeroFraction = if (max > min) (0f - min) / (max - min) else 0.5f
                val zeroY = pad + (1f - zeroFraction.coerceIn(0f, 1f)) * usable
                drawLine(
                    color = zeroLineColor,
                    start = Offset(0f, zeroY),
                    end = Offset(size.width, zeroY),
                    strokeWidth = 1.dp.toPx(),
                )
                val points = (0 until count).map { i ->
                    val x = size.width * (i + 0.5f) / count
                    val fraction = if (max > min) (setting.value[i].toFloat() - min) / (max - min) else 0.5f
                    Offset(x, pad + (1f - fraction.coerceIn(0f, 1f)) * usable)
                }
                val path = Path()
                path.moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val a = points[i]
                    val b = points[i + 1]
                    val midX = (a.x + b.x) / 2f
                    path.cubicTo(midX, a.y, midX, b.y, b.x, b.y)
                }
                drawPath(path = path, color = curveColor, style = Stroke(width = 2.dp.toPx()))
            }
            Row(Modifier.fillMaxSize()) {
                for (i in 0 until count) {
                    BandSlider(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        value = setting.value[i],
                        min = equalizer.min,
                        max = equalizer.max,
                        enabled = enabled,
                        onValueChange = { newValue ->
                            onChange(setting.value.mapIndexed { index, v -> if (index == i) newValue else v })
                        },
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            for (i in 0 until count) {
                Text(
                    text = formatHz(equalizer.bandHz[i].toInt()),
                    fontSize = 11.sp,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BandSlider(
    modifier: Modifier,
    value: Short,
    min: Short,
    max: Short,
    enabled: Boolean,
    onValueChange: (Short) -> Unit,
) {
    val (shownValue, setShownValue) = throttledState(value, 150, onValueChange = onValueChange)
    // pointerInput is not restarted on recomposition, so always go through the latest setter
    val latestSetter by rememberUpdatedState(setShownValue)
    val minF = min.toFloat()
    val maxF = max.toFloat()
    val trackColor = Color.White.copy(alpha = 0.7f)
    val dark = soundcoreIsDark()

    Canvas(
        modifier = modifier
            .pointerInput(enabled, min, max) {
                if (!enabled) return@pointerInput
                val pad = SliderPadding.toPx()
                fun valueAt(y: Float): Short {
                    val usable = size.height - 2 * pad
                    val fraction = 1f - ((y - pad) / usable).coerceIn(0f, 1f)
                    return (minF + fraction * (maxF - minF)).roundToInt().toShort()
                }
                detectTapGestures { offset -> latestSetter(valueAt(offset.y)) }
            }
            .pointerInput(enabled, min, max) {
                if (!enabled) return@pointerInput
                val pad = SliderPadding.toPx()
                fun valueAt(y: Float): Short {
                    val usable = size.height - 2 * pad
                    val fraction = 1f - ((y - pad) / usable).coerceIn(0f, 1f)
                    return (minF + fraction * (maxF - minF)).roundToInt().toShort()
                }
                detectVerticalDragGestures(
                    onDragStart = { offset -> latestSetter(valueAt(offset.y)) },
                    onVerticalDrag = { change, _ ->
                        change.consume()
                        latestSetter(valueAt(change.position.y))
                    },
                )
            },
    ) {
        val pad = SliderPadding.toPx()
        val usable = size.height - 2 * pad
        val trackWidth = 6.dp.toPx()
        val cx = size.width / 2f
        val fraction = if (maxF > minF) ((shownValue.toFloat() - minF) / (maxF - minF)).coerceIn(0f, 1f) else 0.5f
        val thumbY = pad + (1f - fraction) * usable

        // background track
        drawRoundRect(
            color = if (dark) Color.White.copy(alpha = 0.18f) else trackColor,
            topLeft = Offset(cx - trackWidth / 2f, pad),
            size = Size(trackWidth, usable),
            cornerRadius = CornerRadius(trackWidth / 2f),
        )
        // filled part
        drawRoundRect(
            brush = SoundcoreBlueBrush,
            topLeft = Offset(cx - trackWidth / 2f, thumbY),
            size = Size(trackWidth, pad + usable - thumbY),
            cornerRadius = CornerRadius(trackWidth / 2f),
        )
        // thumb
        val thumbRadius = 11.dp.toPx()
        drawCircle(
            color = Color.Black.copy(alpha = 0.12f),
            radius = thumbRadius + 1.5.dp.toPx(),
            center = Offset(cx, thumbY + 1.dp.toPx()),
        )
        drawCircle(
            color = if (enabled) Color.White else Color.White.copy(alpha = 0.85f),
            radius = thumbRadius,
            center = Offset(cx, thumbY),
        )
        drawCircle(
            color = SoundcoreBlue,
            radius = thumbRadius - 1.dp.toPx(),
            center = Offset(cx, thumbY),
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}
