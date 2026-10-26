package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.DataStoreTemplateRepository
import com.example.data.InMemoryTemplateRepository
import com.example.model.AppDestination
import com.example.ui.screens.ArchitectureScreen
import com.example.ui.screens.ComponentsScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.BlueprintTheme
import com.example.viewmodel.TemplateViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val repository = DataStoreTemplateRepository(applicationContext)

    setContent {
      val viewModel: TemplateViewModel = viewModel(
        factory = TemplateViewModel.provideFactory(repository)
      )
      BlueprintApp(viewModel = viewModel)
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlueprintApp(
  viewModel: TemplateViewModel,
  modifier: Modifier = Modifier,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(viewModel) {
    viewModel.snackbarMessages.collect { message ->
      snackbarHostState.showSnackbar(message)
    }
  }

  // Return to Overview tab when pressing system Back from secondary top-level tabs
  if (uiState.currentDestination != AppDestination.OVERVIEW && uiState.selectedArchitectureLayer == null) {
    BackHandler {
      viewModel.onSelectDestination(AppDestination.OVERVIEW)
    }
  }

  BlueprintTheme(
    themeMode = uiState.themeMode,
    dynamicColor = uiState.dynamicColorEnabled,
  ) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
      val useNavigationRail = maxWidth >= 600.dp

      Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
          CenterAlignedTopAppBar(
            title = {
              Row {
                Text(
                  text = stringResource(R.string.app_name),
                  style = MaterialTheme.typography.titleLarge,
                )
              }
            },
            actions = {
              Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.padding(end = 16.dp),
              ) {
                Text(
                  text = uiState.currentDestination.route.uppercase(),
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
              }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
              containerColor = MaterialTheme.colorScheme.background,
            ),
          )
        },
        snackbarHost = {
          SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.testTag("app_snackbar_host"),
          )
        },
        bottomBar = {
          if (!useNavigationRail) {
            BlueprintBottomBar(
              currentDestination = uiState.currentDestination,
              onSelectDestination = viewModel::onSelectDestination,
            )
          }
        },
      ) { innerPadding ->
        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        ) {
          if (useNavigationRail) {
            BlueprintNavigationRail(
              currentDestination = uiState.currentDestination,
              onSelectDestination = viewModel::onSelectDestination,
            )
          }

          Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(
              targetState = uiState.currentDestination,
              label = "destination_crossfade",
            ) { destination ->
              when (destination) {
                AppDestination.OVERVIEW -> OverviewScreen(
                  uiState = uiState,
                  onIncrementCounter = viewModel::onIncrementCounter,
                  onDecrementCounter = viewModel::onDecrementCounter,
                  onResetCounter = viewModel::onResetCounter,
                  onToggleChecklist = viewModel::onToggleChecklistItem,
                  onNavigateToTab = viewModel::onSelectDestination,
                )
                AppDestination.COMPONENTS -> ComponentsScreen(
                  uiState = uiState,
                  onUpdateText = viewModel::onUpdateSandboxText,
                  onToggleNotifications = viewModel::onToggleSandboxNotifications,
                  onUpdateSlider = viewModel::onUpdateSandboxSlider,
                  onSelectFilterTag = viewModel::onSelectFilterTag,
                  onTriggerSnackbar = viewModel::onTriggerSampleSnackbar,
                )
                AppDestination.ARCHITECTURE -> ArchitectureScreen(
                  uiState = uiState,
                  onSelectLayer = viewModel::onSelectArchitectureLayer,
                )
                AppDestination.SETTINGS -> SettingsScreen(
                  uiState = uiState,
                  onSetThemeMode = viewModel::onSetThemeMode,
                  onSetDynamicColor = viewModel::onSetDynamicColor,
                  onResetAll = viewModel::onResetTemplateState,
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun BlueprintBottomBar(
  currentDestination: AppDestination,
  onSelectDestination: (AppDestination) -> Unit,
) {
  NavigationBar(modifier = Modifier.testTag("bottom_navigation_bar")) {
    AppDestination.entries.forEach { destination ->
      val selected = currentDestination == destination
      NavigationBarItem(
        selected = selected,
        onClick = { onSelectDestination(destination) },
        icon = {
          Icon(
            imageVector = destination.icon(selected = selected),
            contentDescription = destination.title,
          )
        },
        label = { Text(destination.title) },
        modifier = Modifier.testTag("nav_tab_${destination.route}"),
      )
    }
  }
}

@Composable
private fun BlueprintNavigationRail(
  currentDestination: AppDestination,
  onSelectDestination: (AppDestination) -> Unit,
) {
  NavigationRail(
    modifier = Modifier
      .fillMaxHeight()
      .testTag("side_navigation_rail"),
  ) {
    AppDestination.entries.forEach { destination ->
      val selected = currentDestination == destination
      NavigationRailItem(
        selected = selected,
        onClick = { onSelectDestination(destination) },
        icon = {
          Icon(
            imageVector = destination.icon(selected = selected),
            contentDescription = destination.title,
          )
        },
        label = { Text(destination.title) },
        modifier = Modifier.testTag("nav_rail_${destination.route}"),
      )
    }
  }
}

private fun AppDestination.icon(selected: Boolean): ImageVector =
  when (this) {
    AppDestination.OVERVIEW -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
    AppDestination.COMPONENTS -> if (selected) Icons.Filled.Widgets else Icons.Outlined.Widgets
    AppDestination.ARCHITECTURE -> if (selected) Icons.Filled.Layers else Icons.Outlined.Layers
    AppDestination.SETTINGS -> if (selected) Icons.Filled.Settings else Icons.Outlined.Settings
  }

@Preview(showBackground = true)
@Composable
fun BlueprintAppPreview() {
  val previewViewModel = remember {
    TemplateViewModel(InMemoryTemplateRepository())
  }
  BlueprintApp(viewModel = previewViewModel)
}
