package com.oppzippy.openscq30.ui.devicesettings.soundcore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oppzippy.openscq30.lib.bindings.translateSettingId
import com.oppzippy.openscq30.lib.wrapper.Setting
import com.oppzippy.openscq30.lib.wrapper.Value
import com.oppzippy.openscq30.lib.wrapper.toValue
import com.oppzippy.openscq30.ui.devicesettings.Screen
import com.oppzippy.openscq30.ui.devicesettings.ScreenInfo

/** Categories that already live on the Home / Sound tabs. */
private val CategoriesOnOtherTabs = setOf("soundModes", "equalizer")

@Composable
fun SoundcoreAllSettingsTab(
    allSettings: List<Pair<String, Setting>>,
    screens: List<ScreenInfo>,
    setSettings: (List<Pair<String, Value>>) -> Unit,
    onNavigate: (Screen) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // every on/off setting gets an inline switch, like the official app
        allSettings
            .filter { (id, setting) -> setting is Setting.ToggleSetting && id !in SoundTabToggleIds }
            .forEach { (id, setting) ->
                SettingToggleRow(
                    icon = toggleIcon(id),
                    title = translateSettingId(id),
                    subtitle = toggleSubtitle(id)?.let { stringResource(it) },
                    checked = (setting as Setting.ToggleSetting).value,
                    onCheckedChange = { setSettings(listOf(id to it.toValue())) },
                )
            }

        // everything else opens its own page
        screens
            .filter { info ->
                val route = info.baseRoute
                !(route is Screen.SettingsCategory && route.categoryId in CategoriesOnOtherTabs)
            }
            .forEach { info ->
                SettingNavRow(
                    icon = info.icon,
                    title = info.name.translated(),
                    onClick = { onNavigate(info.baseRoute) },
                )
            }
        Spacer(Modifier.height(8.dp))
    }
}
