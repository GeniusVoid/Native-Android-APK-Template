package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.TemplateRepository
import com.example.model.AppDestination
import com.example.model.ArchitectureLayer
import com.example.model.TemplateUiState
import com.example.model.ThemeMode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Primary state holder for the starter template demonstrating Clean MVVM + Unidirectional Data Flow.
 */
class TemplateViewModel(
  private val repository: TemplateRepository,
) : ViewModel() {

  private data class EphemeralUiState(
    val currentDestination: AppDestination = AppDestination.OVERVIEW,
    val selectedArchitectureLayer: ArchitectureLayer? = null,
    val sandboxTextInput: String = "",
    val sandboxNotificationsEnabled: Boolean = true,
    val sandboxSliderValue: Float = 0.5f,
    val selectedFilterTag: String = "Core",
  )

  private val ephemeralState = MutableStateFlow(EphemeralUiState())

  private val _snackbarMessages = Channel<String>(Channel.BUFFERED)
  val snackbarMessages: Flow<String> = _snackbarMessages.receiveAsFlow()

  val uiState: StateFlow<TemplateUiState> =
    combine(repository.preferencesFlow, ephemeralState) { prefs, local ->
      TemplateUiState(
        currentDestination = local.currentDestination,
        selectedArchitectureLayer = local.selectedArchitectureLayer,
        themeMode = prefs.themeMode,
        dynamicColorEnabled = prefs.dynamicColorEnabled,
        counterValue = prefs.counterValue,
        sandboxTextInput = local.sandboxTextInput,
        sandboxNotificationsEnabled = local.sandboxNotificationsEnabled,
        sandboxSliderValue = local.sandboxSliderValue,
        selectedFilterTag = local.selectedFilterTag,
        completedChecklistIds = prefs.completedChecklistIds,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = TemplateUiState(),
    )

  fun onSelectDestination(destination: AppDestination) {
    ephemeralState.update {
      it.copy(
        currentDestination = destination,
        selectedArchitectureLayer = null,
      )
    }
  }

  fun onSelectArchitectureLayer(layer: ArchitectureLayer?) {
    ephemeralState.update { it.copy(selectedArchitectureLayer = layer) }
  }

  fun onIncrementCounter() {
    viewModelScope.launch {
      val next = uiState.value.counterValue + 1
      repository.updateCounter(next)
    }
  }

  fun onDecrementCounter() {
    viewModelScope.launch {
      val next = uiState.value.counterValue - 1
      repository.updateCounter(next)
    }
  }

  fun onResetCounter() {
    viewModelScope.launch {
      repository.updateCounter(0)
      _snackbarMessages.send("Counter reset to 0")
    }
  }

  fun onToggleChecklistItem(itemId: String) {
    viewModelScope.launch {
      repository.toggleChecklistItem(itemId)
    }
  }

  fun onUpdateSandboxText(text: String) {
    ephemeralState.update { it.copy(sandboxTextInput = text) }
  }

  fun onToggleSandboxNotifications(enabled: Boolean) {
    ephemeralState.update { it.copy(sandboxNotificationsEnabled = enabled) }
  }

  fun onUpdateSandboxSlider(value: Float) {
    ephemeralState.update { it.copy(sandboxSliderValue = value) }
  }

  fun onSelectFilterTag(tag: String) {
    ephemeralState.update { it.copy(selectedFilterTag = tag) }
  }

  fun onSetThemeMode(mode: ThemeMode) {
    viewModelScope.launch {
      repository.setThemeMode(mode)
      _snackbarMessages.send("Theme set to ${mode.label}")
    }
  }

  fun onSetDynamicColor(enabled: Boolean) {
    viewModelScope.launch {
      repository.setDynamicColorEnabled(enabled)
      val status = if (enabled) "enabled" else "disabled"
      _snackbarMessages.send("Dynamic color $status")
    }
  }

  fun onTriggerSampleSnackbar(message: String) {
    viewModelScope.launch {
      _snackbarMessages.send(message)
    }
  }

  fun onResetTemplateState() {
    viewModelScope.launch {
      repository.resetAll()
      ephemeralState.update {
        EphemeralUiState(currentDestination = it.currentDestination)
      }
      _snackbarMessages.send("Template state restored to defaults")
    }
  }

  companion object {
    fun provideFactory(repository: TemplateRepository): ViewModelProvider.Factory =
      object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          return TemplateViewModel(repository) as T
        }
      }
  }
}
