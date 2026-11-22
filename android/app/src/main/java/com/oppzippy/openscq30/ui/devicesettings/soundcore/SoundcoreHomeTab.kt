package com.oppzippy.openscq30.ui.devicesettings.soundcore

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oppzippy.openscq30.R
import com.oppzippy.openscq30.lib.wrapper.Setting
import com.oppzippy.openscq30.lib.wrapper.Value
import com.oppzippy.openscq30.lib.wrapper.toValue
import com.oppzippy.openscq30.ui.devicesettings.screens.SettingCategoryScreen
import com.oppzippy.openscq30.ui.theme.SoundcoreGreen

private const val AMBIENT_SOUND_MODE_ID = "ambientSoundMode"

private data class BatteryInfo(
    val letter: String?,
    @DrawableRes val icon: Int?,
    @StringRes val label: Int,
    val valueText: String,
    val percent: Int?,
)

private fun informationValue(settings: List<Pair<String, Setting>>, id: String): String? =
    (settings.find { it.first == id }?.second as? Setting.InformationSetting)?.translatedValue

private fun parsePercent(text: String): Int? = Regex("\\d+").find(text)?.value?.toIntOrNull()?.coerceIn(0, 100)

@Composable
fun SoundcoreHomeTab(
    rawDeviceModel: String,
    displayedModelName: String,
    allSettings: List<Pair<String, Setting>>,
    soundModeSettings: List<Pair<String, Setting>>,
    setSettings: (List<Pair<String, Value>>) -> Unit,
) {
    val hasSoundcorePrefix = displayedModelName.startsWith("Soundcore ", ignoreCase = true)
    val brand = if (hasSoundcorePrefix) "Anker Soundcore" else null
    val modelName = if (hasSoundcorePrefix) displayedModelName.substring("Soundcore ".length) else displayedModelName

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (brand != null) {
                Text(brand, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(modelName, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Icon(
                    painter = painterResource(R.drawable.check_circle_24px),
                    contentDescription = null,
                    tint = SoundcoreGreen,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        EarbudsIllustration(
            Modifier
                .fillMaxWidth()
                .height(240.dp),
        )

        BatteryRow(allSettings)

        SoundModeSelector(soundModeSettings, setSettings)

        val otherSoundModeSettings = soundModeSettings.filter { it.first != AMBIENT_SOUND_MODE_ID }
        if (otherSoundModeSettings.isNotEmpty()) {
            GlassCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    SettingCategoryScreen(
                        deviceModel = rawDeviceModel,
                        categoryId = "soundModes",
                        settings = otherSoundModeSettings,
                        setSettings = setSettings,
                        scrollable = false,
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun BatteryRow(allSettings: List<Pair<String, Setting>>) {
    val tiles = buildList {
        informationValue(allSettings, "batteryLevelLeft")?.let {
            add(BatteryInfo("L", null, R.string.soundcore_left_bud, it, parsePercent(it)))
        }
        informationValue(allSettings, "caseBatteryLevel")?.let {
            add(BatteryInfo(null, R.drawable.earbud_case_24px, R.string.soundcore_case, it, parsePercent(it)))
        }
        informationValue(allSettings, "batteryLevelRight")?.let {
            add(BatteryInfo("R", null, R.string.soundcore_right_bud, it, parsePercent(it)))
        }
        if (isEmpty()) {
            informationValue(allSettings, "batteryLevel")?.let {
                add(BatteryInfo(null, R.drawable.earbuds_2_24px, R.string.soundcore_battery, it, parsePercent(it)))
            }
        }
    }
    if (tiles.isEmpty()) return

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        tiles.forEach { tile ->
            BatteryTile(
                modifier = Modifier.weight(1f),
                letter = tile.letter,
                icon = tile.icon,
                valueText = tile.valueText,
                percent = tile.percent,
                label = stringResource(tile.label),
            )
        }
    }
}

@DrawableRes
private fun modeIcon(optionId: String): Int = when {
    optionId.contains("noise", ignoreCase = true) -> R.drawable.ic_soundcore_anc
    optionId.contains("transparen", ignoreCase = true) -> R.drawable.visibility_24px
    optionId.contains("normal", ignoreCase = true) -> R.drawable.light_mode_24px
    else -> R.drawable.settings_24px
}

@Composable
private fun SoundModeSelector(
    soundModeSettings: List<Pair<String, Setting>>,
    setSettings: (List<Pair<String, Value>>) -> Unit,
) {
    val ambient = soundModeSettings.find { it.first == AMBIENT_SOUND_MODE_ID }?.second
    val options: List<String>
    val labels: List<String>
    val current: String?
    when (ambient) {
        is Setting.SelectSetting -> {
            options = ambient.setting.options
            labels = ambient.setting.localizedOptions
            current = ambient.value
        }

        is Setting.OptionalSelectSetting -> {
            options = ambient.setting.options
            labels = ambient.setting.localizedOptions
            current = ambient.value
        }

        else -> return
    }

    GlassCard(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEachIndexed { index, option ->
                val label = if (option.contains("noise", ignoreCase = true)) {
                    stringResource(R.string.soundcore_anc)
                } else {
                    labels.getOrElse(index) { option }
                }
                SoundModeButton(
                    modifier = Modifier.weight(1f),
                    icon = modeIcon(option),
                    label = label,
                    selected = option == current,
                    onClick = { setSettings(listOf(AMBIENT_SOUND_MODE_ID to option.toValue())) },
                )
            }
        }
    }
}

@Composable
private fun SoundModeButton(
    modifier: Modifier,
    @DrawableRes icon: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    val background = if (selected) {
        Modifier.background(SoundcoreBlueBrush, shape)
    } else {
        Modifier.background(Color.Transparent, shape)
    }
    val contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
    Column(
        modifier = modifier
            .height(76.dp)
            .clip(shape)
            .then(background)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}
