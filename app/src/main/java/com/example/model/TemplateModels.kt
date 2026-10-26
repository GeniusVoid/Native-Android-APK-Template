package com.example.model

/**
 * Reusable generic UI State wrapper for screens or asynchronous operations.
 * Copy or adapt this sealed interface when adding domain-specific screens.
 */
sealed interface UiState<out T> {
  data object Loading : UiState<Nothing>
  data class Success<T>(val data: T) : UiState<T>
  data class Error(val message: String, val cause: Throwable? = null) : UiState<Nothing>
}

/**
 * App-wide theme preference persisted via DataStore.
 */
enum class ThemeMode(val label: String) {
  SYSTEM("System Default"),
  LIGHT("Light"),
  DARK("Dark"),
}

/**
 * Top-level navigation destinations for the starter template.
 * Replace or extend these entries to add new top-level tabs/screens.
 */
enum class AppDestination(
  val route: String,
  val title: String,
) {
  OVERVIEW("overview", "Overview"),
  COMPONENTS("components", "UI Kit"),
  ARCHITECTURE("architecture", "Structure"),
  SETTINGS("settings", "Settings"),
}

/**
 * Describes a layer in the template's Clean Architecture / MVVM package layout.
 */
data class ArchitectureLayer(
  val id: String,
  val title: String,
  val packagePath: String,
  val summary: String,
  val keyFiles: List<String>,
  val extensionGuide: List<String>,
  val sampleSnippet: String,
)

/**
 * Represents a customizable starter checklist item for developers forking the template.
 */
data class StarterChecklistItem(
  val id: String,
  val title: String,
  val moduleTag: String,
  val description: String,
)

/**
 * Immutable UI state for the entire starter template.
 */
data class TemplateUiState(
  val currentDestination: AppDestination = AppDestination.OVERVIEW,
  val selectedArchitectureLayer: ArchitectureLayer? = null,
  val themeMode: ThemeMode = ThemeMode.DARK,
  val dynamicColorEnabled: Boolean = false,
  val counterValue: Int = 0,
  val sandboxTextInput: String = "",
  val sandboxNotificationsEnabled: Boolean = true,
  val sandboxSliderValue: Float = 0.5f,
  val selectedFilterTag: String = "Core",
  val completedChecklistIds: Set<String> = setOf("theme", "udf"),
  val architectureLayers: List<ArchitectureLayer> = defaultArchitectureLayers,
  val starterChecklist: List<StarterChecklistItem> = defaultStarterChecklist,
)

val defaultArchitectureLayers = listOf(
  ArchitectureLayer(
    id = "ui_screens",
    title = "UI & Adaptive Screens",
    packagePath = "com.example.ui.screens",
    summary = "Stateless Jetpack Compose screens receiving immutable state and emitting lambdas.",
    keyFiles = listOf(
      "MainActivity.kt",
      "OverviewScreen.kt",
      "ComponentsScreen.kt",
      "ArchitectureScreen.kt",
      "SettingsScreen.kt",
    ),
    extensionGuide = listOf(
      "Create a new @Composable function accepting state and event callbacks.",
      "Register a new entry in AppDestination to expose it in the NavigationBar / NavigationRail.",
      "Keep screens stateless so they remain trivial to preview with @Preview and test with Robolectric.",
    ),
    sampleSnippet = """
      @Composable
      fun FeatureScreen(
        state: FeatureUiState,
        onAction: (FeatureEvent) -> Unit,
        modifier: Modifier = Modifier
      ) { /* ... */ }
    """.trimIndent(),
  ),
  ArchitectureLayer(
    id = "viewmodel",
    title = "Presentation & StateHolder",
    packagePath = "com.example.viewmodel",
    summary = "Lifecycle-aware ViewModel exposing StateFlow<TemplateUiState> and one-off UI events.",
    keyFiles = listOf(
      "TemplateViewModel.kt",
    ),
    extensionGuide = listOf(
      "Inject repositories via constructor parameters using a ViewModelProvider.Factory.",
      "Collect flows in viewModelScope and transform them into a single StateFlow.",
      "Expose intent methods (e.g., onIncrementCounter, onSelectTab) for Unidirectional Data Flow.",
    ),
    sampleSnippet = """
      val uiState: StateFlow<TemplateUiState> =
        repository.preferencesFlow
          .map { prefs -> /* map to UI state */ }
          .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TemplateUiState())
    """.trimIndent(),
  ),
  ArchitectureLayer(
    id = "data_repo",
    title = "Data & Repository Layer",
    packagePath = "com.example.data",
    summary = "Single source of truth abstracting persistence (DataStore, Room, or Retrofit/Firestore).",
    keyFiles = listOf(
      "TemplateRepository.kt",
    ),
    extensionGuide = listOf(
      "Define an interface first so UI previews and JVM tests can use an in-memory fake.",
      "Uncomment Room, Retrofit, or Firebase Firestore in app/build.gradle.kts when ready.",
      "Perform all I/O operations on Dispatchers.IO or inside DataStore transactions.",
    ),
    sampleSnippet = """
      interface TemplateRepository {
        val preferencesFlow: Flow<TemplatePreferences>
        suspend fun setThemeMode(mode: ThemeMode)
        suspend fun updateCounter(value: Int)
      }
    """.trimIndent(),
  ),
  ArchitectureLayer(
    id = "model_domain",
    title = "Domain & State Models",
    packagePath = "com.example.model",
    summary = "Pure Kotlin data classes, enums, and sealed interfaces independent of Android framework.",
    keyFiles = listOf(
      "TemplateModels.kt",
    ),
    extensionGuide = listOf(
      "Use sealed interfaces like UiState<T> (Loading, Success, Error) to model async screens.",
      "Keep domain models immutable (val properties) for thread safety and Compose smart recomposition.",
    ),
    sampleSnippet = """
      sealed interface UiState<out T> {
        data object Loading : UiState<Nothing>
        data class Success<T>(val data: T) : UiState<T>
        data class Error(val message: String) : UiState<Nothing>
      }
    """.trimIndent(),
  ),
  ArchitectureLayer(
    id = "theme_design",
    title = "Material 3 Design System",
    packagePath = "com.example.ui.theme",
    summary = "Centralized ColorScheme, custom local Google Fonts typography, and rounded geometry.",
    keyFiles = listOf(
      "Theme.kt",
      "Color.kt",
      "Type.kt",
    ),
    extensionGuide = listOf(
      "Adjust brand tokens in Color.kt; never hardcode hex colors inside screen composables.",
      "Install additional local Google Fonts via `font-util install \"<FontName>\" --target app/src/main/res/font`.",
      "Toggle Material You Dynamic Color at runtime from SettingsScreen.",
    ),
    sampleSnippet = """
      BlueprintTheme(
        themeMode = uiState.themeMode,
        dynamicColor = uiState.dynamicColorEnabled
      ) {
         BlueprintAppShell(...)
      }
    """.trimIndent(),
  ),
)

val defaultStarterChecklist = listOf(
  StarterChecklistItem(
    id = "theme",
    title = "Configure Brand Palette & Fonts",
    moduleTag = "ui/theme",
    description = "Customize Color.kt, Type.kt, and Theme.kt for your target brand identity.",
  ),
  StarterChecklistItem(
    id = "udf",
    title = "Verify Unidirectional Data Flow",
    moduleTag = "viewmodel",
    description = "Test reactive StateFlow updates between Compose UI, ViewModel, and DataStore.",
  ),
  StarterChecklistItem(
    id = "domain",
    title = "Define Domain Entities & Routes",
    moduleTag = "model",
    description = "Add your app's data models and screen destinations in TemplateModels.kt.",
  ),
  StarterChecklistItem(
    id = "data",
    title = "Connect Remote or Local Data Source",
    moduleTag = "data",
    description = "Extend TemplateRepository with Room, Retrofit, or Firebase when needed.",
  ),
)
