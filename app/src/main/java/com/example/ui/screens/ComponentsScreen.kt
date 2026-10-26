package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.TemplateUiState
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComponentsScreen(
  uiState: TemplateUiState,
  onUpdateText: (String) -> Unit,
  onToggleNotifications: (Boolean) -> Unit,
  onUpdateSlider: (Float) -> Unit,
  onSelectFilterTag: (String) -> Unit,
  onTriggerSnackbar: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var showSampleDialog by rememberSaveable { mutableStateOf(false) }
  val filterOptions = listOf("Core", "Compose", "Material 3", "Coroutines", "DataStore")

  if (showSampleDialog) {
    AlertDialog(
      onDismissRequest = { showSampleDialog = false },
      icon = {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
        )
      },
      title = { Text("Template Dialog Component") },
      text = {
        Text(
          "This standard Material 3 AlertDialog is ready to copy or customize for confirmation flows, alerts, or onboarding prompts."
        )
      },
      confirmButton = {
        TextButton(
          onClick = {
            showSampleDialog = false
            onTriggerSnackbar("Confirmed dialog action")
          },
          modifier = Modifier.testTag("dialog_confirm_button"),
        ) {
          Text("Confirm")
        }
      },
      dismissButton = {
        TextButton(
          onClick = { showSampleDialog = false },
          modifier = Modifier.testTag("dialog_dismiss_button"),
        ) {
          Text("Dismiss")
        }
      },
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("components_screen"),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 720.dp),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
      item {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = stringResource(R.string.components_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Text(
            text = stringResource(R.string.components_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      // 1. Buttons & Feedback Section
      item {
        OutlinedCard(
          shape = MaterialTheme.shapes.large,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            SectionHeader(
              title = "Buttons & Transient Feedback",
              badge = "M3 Actions",
            )
            Text(
              text = "Standard 48.dp touch-target buttons wired to SnackbarHost and modal dialogs.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Button(
                onClick = { onTriggerSnackbar("Primary action triggered from UI Kit") },
                modifier = Modifier.testTag("sandbox_primary_button"),
              ) {
                Icon(
                  imageVector = Icons.Default.Send,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Snackbar")
              }

              FilledTonalButton(
                onClick = { showSampleDialog = true },
                modifier = Modifier.testTag("sandbox_dialog_button"),
              ) {
                Text("Open Dialog")
              }

              OutlinedButton(
                onClick = { onTriggerSnackbar("Secondary outlined action") },
                modifier = Modifier.testTag("sandbox_outlined_button"),
              ) {
                Text("Outlined Action")
              }
            }
          }
        }
      }

      // 2. Form Input & Validation Pattern
      item {
        OutlinedCard(
          shape = MaterialTheme.shapes.large,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            SectionHeader(
              title = "Controlled Text Input",
              badge = "StateFlow Bound",
            )
            OutlinedTextField(
              value = uiState.sandboxTextInput,
              onValueChange = onUpdateText,
              label = { Text("Sample Feature Input") },
              placeholder = { Text("Type to test reactive ViewModel state...") },
              singleLine = true,
              trailingIcon = {
                if (uiState.sandboxTextInput.isNotEmpty()) {
                  IconButton(
                    onClick = { onUpdateText("") },
                    modifier = Modifier.testTag("sandbox_clear_text_button"),
                  ) {
                    Icon(
                      imageVector = Icons.Default.Clear,
                      contentDescription = "Clear input",
                    )
                  }
                }
              },
              supportingText = {
                Text("${uiState.sandboxTextInput.length} characters • Synced with TemplateViewModel")
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("sandbox_text_input"),
            )
          }
        }
      }

      // 3. Filter Chips
      item {
        OutlinedCard(
          shape = MaterialTheme.shapes.large,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            SectionHeader(
              title = "Selection & Filter Chips",
              badge = "Selected: ${uiState.selectedFilterTag}",
            )
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              filterOptions.forEach { tag ->
                val selected = uiState.selectedFilterTag == tag
                FilterChip(
                  selected = selected,
                  onClick = { onSelectFilterTag(tag) },
                  label = { Text(tag) },
                  leadingIcon = if (selected) {
                    {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                      )
                    }
                  } else {
                    null
                  },
                  modifier = Modifier.testTag("filter_chip_$tag"),
                )
              }
            }
          }
        }
      }

      // 4. Toggles & Sliders
      item {
        OutlinedCard(
          shape = MaterialTheme.shapes.large,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
          ) {
            SectionHeader(
              title = "Switches & Continuous Controls",
              badge = "${(uiState.sandboxSliderValue * 100).roundToInt()}%",
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
              ) {
                Icon(
                  imageVector = if (uiState.sandboxNotificationsEnabled) {
                    Icons.Default.NotificationsActive
                  } else {
                    Icons.Default.Notifications
                  },
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                )
                Column {
                  Text(
                    text = "Feature Toggle",
                    style = MaterialTheme.typography.titleMedium,
                  )
                  Text(
                    text = if (uiState.sandboxNotificationsEnabled) "Enabled in state" else "Disabled in state",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }

              Switch(
                checked = uiState.sandboxNotificationsEnabled,
                onCheckedChange = onToggleNotifications,
                modifier = Modifier.testTag("sandbox_feature_switch"),
              )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "Threshold Parameter (${(uiState.sandboxSliderValue * 100).roundToInt()}%)",
                style = MaterialTheme.typography.labelLarge,
              )
              Slider(
                value = uiState.sandboxSliderValue,
                onValueChange = onUpdateSlider,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("sandbox_slider"),
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SectionHeader(
  title: String,
  badge: String,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onSurface,
    )
    Surface(
      color = MaterialTheme.colorScheme.secondaryContainer,
      shape = MaterialTheme.shapes.extraSmall,
    ) {
      Text(
        text = badge,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
      )
    }
  }
}
