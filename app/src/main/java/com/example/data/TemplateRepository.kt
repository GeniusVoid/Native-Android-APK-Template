package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.ThemeMode
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

private val Context.templateDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "blueprint_template_preferences"
)

/**
 * Snapshot of persisted template settings and demo state.
 */
data class TemplatePreferences(
  val themeMode: ThemeMode = ThemeMode.DARK,
  val dynamicColorEnabled: Boolean = false,
  val counterValue: Int = 0,
  val completedChecklistIds: Set<String> = setOf("theme", "udf"),
)

/**
 * Clean Repository interface for the starter template.
 * Swap or extend this interface to add Room DAOs, Retrofit services, or Firestore collections.
 */
interface TemplateRepository {
  val preferencesFlow: Flow<TemplatePreferences>
  suspend fun setThemeMode(themeMode: ThemeMode)
  suspend fun setDynamicColorEnabled(enabled: Boolean)
  suspend fun updateCounter(newValue: Int)
  suspend fun toggleChecklistItem(itemId: String)
  suspend fun resetAll()
}

/**
 * Production DataStore implementation of [TemplateRepository].
 */
class DataStoreTemplateRepository(
  private val context: Context,
) : TemplateRepository {

  private object Keys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    val COUNTER_VALUE = intPreferencesKey("counter_value")
    val COMPLETED_CHECKLIST = stringSetPreferencesKey("completed_checklist")
  }

  override val preferencesFlow: Flow<TemplatePreferences> =
    context.templateDataStore.data
      .catch { exception ->
        if (exception is IOException) {
          emit(emptyPreferences())
        } else {
          throw exception
        }
      }
      .map { preferences ->
        val rawTheme = preferences[Keys.THEME_MODE]
        val themeMode = ThemeMode.entries.firstOrNull { it.name == rawTheme } ?: ThemeMode.DARK
        TemplatePreferences(
          themeMode = themeMode,
          dynamicColorEnabled = preferences[Keys.DYNAMIC_COLOR] ?: false,
          counterValue = preferences[Keys.COUNTER_VALUE] ?: 0,
          completedChecklistIds = preferences[Keys.COMPLETED_CHECKLIST] ?: setOf("theme", "udf"),
        )
      }

  override suspend fun setThemeMode(themeMode: ThemeMode) {
    context.templateDataStore.edit { preferences ->
      preferences[Keys.THEME_MODE] = themeMode.name
    }
  }

  override suspend fun setDynamicColorEnabled(enabled: Boolean) {
    context.templateDataStore.edit { preferences ->
      preferences[Keys.DYNAMIC_COLOR] = enabled
    }
  }

  override suspend fun updateCounter(newValue: Int) {
    context.templateDataStore.edit { preferences ->
      preferences[Keys.COUNTER_VALUE] = newValue
    }
  }

  override suspend fun toggleChecklistItem(itemId: String) {
    context.templateDataStore.edit { preferences ->
      val current = preferences[Keys.COMPLETED_CHECKLIST] ?: setOf("theme", "udf")
      preferences[Keys.COMPLETED_CHECKLIST] =
        if (current.contains(itemId)) current - itemId else current + itemId
    }
  }

  override suspend fun resetAll() {
    context.templateDataStore.edit { preferences ->
      preferences.clear()
    }
  }
}

/**
 * Fast, zero-dependency in-memory implementation of [TemplateRepository].
 * Ideal for @Preview composables and local unit / Robolectric tests.
 */
class InMemoryTemplateRepository(
  initialPreferences: TemplatePreferences = TemplatePreferences(),
) : TemplateRepository {
  private val state = MutableStateFlow(initialPreferences)
  override val preferencesFlow: Flow<TemplatePreferences> = state.asStateFlow()

  override suspend fun setThemeMode(themeMode: ThemeMode) {
    state.update { it.copy(themeMode = themeMode) }
  }

  override suspend fun setDynamicColorEnabled(enabled: Boolean) {
    state.update { it.copy(dynamicColorEnabled = enabled) }
  }

  override suspend fun updateCounter(newValue: Int) {
    state.update { it.copy(counterValue = newValue) }
  }

  override suspend fun toggleChecklistItem(itemId: String) {
    state.update { current ->
      val updated = if (current.completedChecklistIds.contains(itemId)) {
        current.completedChecklistIds - itemId
      } else {
        current.completedChecklistIds + itemId
      }
      current.copy(completedChecklistIds = updated)
    }
  }

  override suspend fun resetAll() {
    state.value = TemplatePreferences()
  }
}
