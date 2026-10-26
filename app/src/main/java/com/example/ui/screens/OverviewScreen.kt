package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.BuildConfig
import com.example.R
import com.example.model.AppDestination
import com.example.model.StarterChecklistItem
import com.example.model.TemplateUiState

@Composable
fun OverviewScreen(
  uiState: TemplateUiState,
  onIncrementCounter: () -> Unit,
  onDecrementCounter: () -> Unit,
  onResetCounter: () -> Unit,
  onToggleChecklist: (String) -> Unit,
  onNavigateToTab: (AppDestination) -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("overview_screen"),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 720.dp),
      contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      item {
        BlueprintHeroCard(
          onExploreArchitecture = { onNavigateToTab(AppDestination.ARCHITECTURE) },
          onOpenSandbox = { onNavigateToTab(AppDestination.COMPONENTS) },
        )
      }

      item {
        UdfCounterCard(
          counterValue = uiState.counterValue,
          onIncrement = onIncrementCounter,
          onDecrement = onDecrementCounter,
          onReset = onResetCounter,
        )
      }

      item {
        val completedCount = uiState.completedChecklistIds.size
        val totalCount = uiState.starterChecklist.size
        val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "Starter Integration Checklist",
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onBackground,
            )
            Surface(
              color = MaterialTheme.colorScheme.secondaryContainer,
              shape = MaterialTheme.shapes.small,
            ) {
              Text(
                text = "$completedCount / $totalCount READY",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              )
            }
          }
          Text(
            text = "Tap any step as you customize this project for your target application.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(CircleShape),
          )
        }
      }

      items(uiState.starterChecklist, key = { it.id }) { item ->
        val isCompleted = uiState.completedChecklistIds.contains(item.id)
        StarterChecklistCard(
          item = item,
          isCompleted = isCompleted,
          onToggle = { onToggleChecklist(item.id) },
        )
      }
    }
  }
}

@Composable
private fun BlueprintHeroCard(
  onExploreArchitecture: () -> Unit,
  onOpenSandbox: () -> Unit,
) {
  Card(
    shape = MaterialTheme.shapes.large,
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("hero_card"),
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(148.dp),
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_blueprint_hero),
          contentDescription = stringResource(R.string.cd_hero_illustration),
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize(),
        )
        Surface(
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f),
          shape = MaterialTheme.shapes.extraSmall,
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(16.dp),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary),
            )
            Text(
              text = "${stringResource(R.string.hero_badge)} • v${BuildConfig.VERSION_NAME}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
          }
        }
      }

      Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(
          text = stringResource(R.string.hero_title),
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = stringResource(R.string.hero_subtitle),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Button(
            onClick = onOpenSandbox,
            modifier = Modifier
              .weight(1f)
              .testTag("open_sandbox_button"),
          ) {
            Icon(
              imageVector = Icons.Default.Widgets,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "UI Sandbox")
          }
          FilledTonalButton(
            onClick = onExploreArchitecture,
            modifier = Modifier
              .weight(1f)
              .testTag("explore_architecture_button"),
          ) {
            Icon(
              imageVector = Icons.Default.Layers,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Architecture")
          }
        }
      }
    }
  }
}

@Composable
private fun UdfCounterCard(
  counterValue: Int,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit,
  onReset: () -> Unit,
) {
  OutlinedCard(
    shape = MaterialTheme.shapes.large,
    modifier = Modifier
      .fillMaxWidth()
      .testTag("udf_counter_card"),
  ) {
    Column(
      modifier = Modifier.padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Icon(
              imageVector = Icons.Default.Code,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp),
            )
            Text(
              text = stringResource(R.string.udf_demo_title),
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = stringResource(R.string.udf_demo_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        IconButton(
          onClick = onReset,
          modifier = Modifier.testTag("counter_reset_button"),
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = stringResource(R.string.cd_reset_state),
          )
        }
      }

      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          FilledTonalIconButton(
            onClick = onDecrement,
            modifier = Modifier
              .size(48.dp)
              .testTag("counter_decrement_button"),
          ) {
            Icon(
              imageVector = Icons.Default.Remove,
              contentDescription = stringResource(R.string.action_decrement),
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedContent(
              targetState = counterValue,
              label = "counter_value_animation",
            ) { value ->
              Text(
                text = value.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("counter_value_text"),
              )
            }
            Text(
              text = "STATEFLOW VALUE",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          Button(
            onClick = onIncrement,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            modifier = Modifier
              .height(48.dp)
              .testTag("counter_increment_button"),
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = stringResource(R.string.action_increment))
          }
        }
      }
    }
  }
}

@Composable
private fun StarterChecklistCard(
  item: StarterChecklistItem,
  isCompleted: Boolean,
  onToggle: () -> Unit,
) {
  OutlinedCard(
    shape = MaterialTheme.shapes.medium,
    modifier = Modifier
      .fillMaxWidth()
      .clip(MaterialTheme.shapes.medium)
      .clickable(onClick = onToggle)
      .testTag("checklist_item_${item.id}"),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Icon(
        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
        contentDescription = if (isCompleted) "Completed" else "Incomplete",
        tint = if (isCompleted) {
          MaterialTheme.colorScheme.tertiary
        } else {
          MaterialTheme.colorScheme.outline
        },
        modifier = Modifier.size(24.dp),
      )

      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.extraSmall,
          ) {
            Text(
              text = item.moduleTag,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
          }
        }
        Text(
          text = item.description,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(18.dp),
      )
    }
  }
}
