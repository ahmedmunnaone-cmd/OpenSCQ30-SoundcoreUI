package com.oppzippy.openscq30.ui.devicesettings

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.oppzippy.openscq30.R
import com.oppzippy.openscq30.features.equalizer.storage.LegacyEqualizerProfile
import com.oppzippy.openscq30.features.soundcoredevice.service.ConnectionStatus
import com.oppzippy.openscq30.lib.bindings.translateCategoryId
import com.oppzippy.openscq30.lib.bindings.translateDeviceModel
import com.oppzippy.openscq30.lib.wrapper.QuickPreset
import com.oppzippy.openscq30.lib.wrapper.Setting
import com.oppzippy.openscq30.lib.wrapper.Value
import com.oppzippy.openscq30.ui.devicesettings.screens.CategorySelectionScreen
import com.oppzippy.openscq30.ui.devicesettings.screens.Disconnected
import com.oppzippy.openscq30.ui.devicesettings.screens.EditQuickPresetPage
import com.oppzippy.openscq30.ui.devicesettings.screens.MigrateLegacyEqualizerProfilesScreen
import com.oppzippy.openscq30.ui.devicesettings.screens.QuickPresetsScreen
import com.oppzippy.openscq30.ui.devicesettings.screens.SettingCategoryScreen
import com.oppzippy.openscq30.ui.devicesettings.screens.StatusNotificationScreen
import com.oppzippy.openscq30.ui.devicesettings.soundcore.BottomBarItem
import com.oppzippy.openscq30.ui.devicesettings.soundcore.SoundcoreAllSettingsTab
import com.oppzippy.openscq30.ui.devicesettings.soundcore.SoundcoreBackground
import com.oppzippy.openscq30.ui.devicesettings.soundcore.SoundcoreBottomBar
import com.oppzippy.openscq30.ui.devicesettings.soundcore.SoundcoreHomeTab
import com.oppzippy.openscq30.ui.devicesettings.soundcore.SoundcoreSoundTab
import com.oppzippy.openscq30.ui.devicesettings.soundcore.SoundcoreTopBar
import com.oppzippy.openscq30.ui.devicesettings.soundcore.SoundcoreWordmark
import com.oppzippy.openscq30.ui.utils.Loading
import kotlinx.coroutines.flow.Flow

@Composable
fun DeviceSettingsScreen(
    onBack: () -> Unit = {},
    connectionStatus: ConnectionStatus,
    setSettingValues: (settingValues: List<Pair<String, Value>>) -> Unit,
    allSettingsFlow: Flow<List<Pair<String, Setting>>>,
    categoryIdsFlow: Flow<List<String>>,
    getSettingsInCategoryFlow: (categoryId: String) -> Flow<List<Pair<String, Setting>>>,
    quickPresetSlotsFlow: Flow<List<String?>>,
    onQuickPresetSlotChange: (Int, String?) -> Unit,
    quickPresetsFlow: Flow<List<QuickPreset>>,
    activateQuickPreset: (name: String) -> Unit,
    createQuickPreset: (name: String) -> Unit,
    deleteQuickPreset: (name: String) -> Unit,
    toggleQuickPresetSetting: (name: String, settingId: String, enabled: Boolean) -> Unit,
    featuredSettingSlotsFlow: Flow<List<String?>>,
    onFeaturedSettingSlotChange: (Int, String?) -> Unit,
    onQuickPresetLoadCurrentSettings: (String) -> Unit,
    legacyEqualizerProfilesFlow: Flow<List<LegacyEqualizerProfile>>,
    onMigrateLegacyEqualizerProfile: (LegacyEqualizerProfile) -> Unit,
) {
    when (connectionStatus) {
        is ConnectionStatus.Connected -> {
            DeviceSettingsNavigationScreen(
                connectionStatus = connectionStatus,
                onBack = onBack,
                setSettingValues = setSettingValues,
                allSettingsFlow = allSettingsFlow,
                categoryIdsFlow = categoryIdsFlow,
                getSettingsInCategoryFlow = getSettingsInCategoryFlow,
                quickPresetSlotsFlow = quickPresetSlotsFlow,
                onQuickPresetSlotChange = onQuickPresetSlotChange,
                quickPresetsFlow = quickPresetsFlow,
                activateQuickPreset = activateQuickPreset,
                createQuickPreset = createQuickPreset,
                deleteQuickPreset = deleteQuickPreset,
                toggleQuickPresetSetting = toggleQuickPresetSetting,
                featuredSettingSlotsFlow = featuredSettingSlotsFlow,
                onFeaturedSettingSlotChange = onFeaturedSettingSlotChange,
                onQuickPresetLoadCurrentSettings = onQuickPresetLoadCurrentSettings,
                legacyEqualizerProfilesFlow = legacyEqualizerProfilesFlow,
                onMigrateLegacyEqualizerProfile = onMigrateLegacyEqualizerProfile,
            )
        }

        ConnectionStatus.AwaitingConnection -> Loading()

        is ConnectionStatus.Connecting -> Loading()

        ConnectionStatus.Disconnected -> Disconnected()
    }
}

@Composable
private fun DeviceSettingsNavigationScreen(
    connectionStatus: ConnectionStatus.Connected,
    onBack: () -> Unit = {},
    setSettingValues: (settingValues: List<Pair<String, Value>>) -> Unit,
    categoryIdsFlow: Flow<List<String>>,
    allSettingsFlow: Flow<List<Pair<String, Setting>>>,
    getSettingsInCategoryFlow: (categoryId: String) -> Flow<List<Pair<String, Setting>>>,
    quickPresetSlotsFlow: Flow<List<String?>>,
    onQuickPresetSlotChange: (Int, String?) -> Unit,
    quickPresetsFlow: Flow<List<QuickPreset>>,
    activateQuickPreset: (name: String) -> Unit,
    createQuickPreset: (name: String) -> Unit,
    deleteQuickPreset: (name: String) -> Unit,
    toggleQuickPresetSetting: (name: String, settingId: String, enabled: Boolean) -> Unit,
    featuredSettingSlotsFlow: Flow<List<String?>>,
    onFeaturedSettingSlotChange: (Int, String?) -> Unit,
    onQuickPresetLoadCurrentSettings: (String) -> Unit,
    legacyEqualizerProfilesFlow: Flow<List<LegacyEqualizerProfile>>,
    onMigrateLegacyEqualizerProfile: (LegacyEqualizerProfile) -> Unit,
) {
    val categoryIds by categoryIdsFlow.collectAsState(emptyList())
    val allSettings by allSettingsFlow.collectAsState(emptyList())

    val listedScreens: MutableList<ScreenInfo> =
        categoryIds.map { Screen.SettingsCategory(it).screenInfo() }.toMutableList()
    listedScreens.add(Screen.QuickPresets.screenInfo)
    listedScreens.add(Screen.StatusNotification.screenInfo)
    listedScreens.add(Screen.MigrateLegacyEqualizerProfiles.screenInfo)

    // compose navigation does not allow us to use polymorphism with routes, so instead a mapping of
    // class path to route name is kept
    val routeNames = listedScreens.associate {
        Pair(it.baseRoute::class.qualifiedName!!, it.name.translated())
    }

    val rawDeviceModel = connectionStatus.deviceManager.device.model()
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackEntry?.destination?.route
    val homeRoute = Screen.Home::class.qualifiedName
    val soundRoute = Screen.Sound::class.qualifiedName
    val allSettingsRoute = Screen.AllSettings::class.qualifiedName
    val isTab = currentRoute != null &&
        (currentRoute == homeRoute || currentRoute == soundRoute || currentRoute == allSettingsRoute)

    fun navigateToTab(screen: Screen) {
        navController.navigate(screen) {
            popUpTo(Screen.Home)
            launchSingleTop = true
        }
    }

    SoundcoreBackground(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                SoundcoreTopBar(
                    title = {
                        when {
                            backStackEntry == null || currentRoute == homeRoute -> SoundcoreWordmark()

                            currentRoute == soundRoute -> Text(stringResource(R.string.soundcore_sound))

                            currentRoute == allSettingsRoute -> Text(stringResource(R.string.soundcore_all_settings))

                            else -> {
                                val entry = backStackEntry!!
                                val routeName = currentRoute?.let { routeNames[it] }
                                val settingsCategory = try {
                                    translateCategoryId(entry.toRoute<Screen.SettingsCategory>().categoryId)
                                } catch (_: Exception) {
                                    null
                                }
                                val editQuickPreset = try {
                                    entry.toRoute<Screen.EditQuickPreset>().name
                                } catch (_: Exception) {
                                    null
                                }
                                val modelName = translateDeviceModel(rawDeviceModel)
                                Text(routeName ?: settingsCategory ?: editQuickPreset ?: modelName)
                            }
                        }
                    },
                    onBack = {
                        val isAtTopOfStack = !navController.popBackStack()
                        if (isAtTopOfStack) {
                            onBack()
                        }
                    },
                    actionIcon = if (currentRoute == homeRoute) R.drawable.settings_24px else null,
                    onAction = {
                        navController.navigate(Screen.StatusNotification) {
                            launchSingleTop = true
                        }
                    },
                )
            },
            bottomBar = {
                if (isTab) {
                    SoundcoreBottomBar(
                        items = listOf(
                            BottomBarItem(R.drawable.ic_soundcore_home, R.string.soundcore_home),
                            BottomBarItem(R.drawable.ic_soundcore_music, R.string.soundcore_sound),
                            BottomBarItem(R.drawable.settings_24px, R.string.soundcore_all_settings),
                        ),
                        selectedIndex = when (currentRoute) {
                            soundRoute -> 1
                            allSettingsRoute -> 2
                            else -> 0
                        },
                        onSelect = { index ->
                            when (index) {
                                0 -> navigateToTab(Screen.Home)
                                1 -> navigateToTab(Screen.Sound)
                                else -> navigateToTab(Screen.AllSettings)
                            }
                        },
                    )
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 8.dp),
                enterTransition = {
                    slideInHorizontally { width -> width / 2 } + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally { width -> -width / 2 } + fadeOut()
                },
                popEnterTransition = {
                    slideInHorizontally { width -> -width / 2 } + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally { width -> width / 2 } + fadeOut()
                },
            ) {
                composable<Screen.Home> {
                    val soundModeFlow = remember { getSettingsInCategoryFlow("soundModes") }
                    val soundModeSettings by soundModeFlow.collectAsState(emptyList())
                    SoundcoreHomeTab(
                        rawDeviceModel = rawDeviceModel,
                        displayedModelName = translateDeviceModel(rawDeviceModel),
                        allSettings = allSettings,
                        soundModeSettings = soundModeSettings,
                        setSettings = setSettingValues,
                    )
                }

                composable<Screen.Sound> {
                    val equalizerFlow = remember { getSettingsInCategoryFlow("equalizer") }
                    val equalizerSettings by equalizerFlow.collectAsState(emptyList())
                    SoundcoreSoundTab(
                        equalizerSettings = equalizerSettings,
                        allSettings = allSettings,
                        setSettings = setSettingValues,
                        onOpenCustomEqualizer = {
                            navController.navigate(Screen.SettingsCategory("equalizer")) {
                                launchSingleTop = true
                            }
                        },
                    )
                }

                composable<Screen.AllSettings> {
                    SoundcoreAllSettingsTab(
                        allSettings = allSettings,
                        screens = listedScreens,
                        setSettings = setSettingValues,
                        onNavigate = { screen ->
                            navController.navigate(screen) {
                                launchSingleTop = true
                            }
                        },
                    )
                }

                composable<Screen.ScreenSelection> {
                    CategorySelectionScreen(
                        screens = listedScreens,
                        onNavigation = { screen ->
                            navController.navigate(screen) {
                                popUpTo(Screen.ScreenSelection)
                                launchSingleTop = true
                            }
                        },
                    )
                }
                composable<Screen.SettingsCategory> { entry ->
                    val route = entry.toRoute<Screen.SettingsCategory>()
                    val settings by getSettingsInCategoryFlow(route.categoryId).collectAsState(emptyList())
                    SettingCategoryScreen(
                        deviceModel = rawDeviceModel,
                        categoryId = route.categoryId,
                        settings = settings,
                        setSettings = setSettingValues,
                    )
                }

                composable<Screen.QuickPresets> {
                    val quickPresets by quickPresetsFlow.collectAsState(emptyList())
                    QuickPresetsScreen(
                        quickPresets = quickPresets,
                        onActivate = activateQuickPreset,
                        onCreate = createQuickPreset,
                        onEdit = { navController.navigate(Screen.EditQuickPreset(it)) },
                        onDelete = deleteQuickPreset,
                    )
                }

                composable<Screen.EditQuickPreset> { entry ->
                    val route = entry.toRoute<Screen.EditQuickPreset>()
                    val quickPresets by quickPresetsFlow.collectAsState(emptyList())
                    val quickPreset = quickPresets.find { it.name == route.name }
                    val settings by allSettingsFlow.collectAsState(emptyList())
                    if (quickPreset != null) {
                        EditQuickPresetPage(
                            settings = settings.toMap(),
                            quickPreset = quickPreset,
                            onToggleSetting = toggleQuickPresetSetting,
                            onLoadCurrentSettings = { onQuickPresetLoadCurrentSettings(quickPreset.name) },
                        )
                    }
                }

                composable<Screen.StatusNotification> {
                    val settings by allSettingsFlow.collectAsState(emptyList())
                    val featuredSettingSlots by featuredSettingSlotsFlow.collectAsState(emptyList())
                    val quickPresets by quickPresetsFlow.collectAsState(emptyList())
                    val quickPresetSlots by quickPresetSlotsFlow.collectAsState(emptyList())
                    StatusNotificationScreen(
                        settingIds = settings.map { it.first },
                        featuredSettingSlots = featuredSettingSlots,
                        onFeaturedSettingSlotChange = onFeaturedSettingSlotChange,
                        quickPresets = quickPresets.map { it.name },
                        quickPresetSlots = quickPresetSlots,
                        onQuickPresetSlotChange = onQuickPresetSlotChange,
                    )
                }

                composable<Screen.MigrateLegacyEqualizerProfiles> {
                    val legacyEqualizerProfiles by legacyEqualizerProfilesFlow.collectAsState(emptyList())
                    MigrateLegacyEqualizerProfilesScreen(
                        legacyEqualizerProfiles = legacyEqualizerProfiles,
                        onMigrateLegacyEqualizerProfile = onMigrateLegacyEqualizerProfile,
                    )
                }
            }
        }
    }
}
