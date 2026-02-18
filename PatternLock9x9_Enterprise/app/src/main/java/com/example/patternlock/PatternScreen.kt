
package com.example.patternlock

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PatternScreen(viewModel: PatternViewModel = viewModel()) {

    val color by animateColorAsState(
        if (viewModel.isLocked)
            MaterialTheme.colorScheme.error
        else
            MaterialTheme.colorScheme.primary
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = viewModel.message,
            color = color,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        PatternLockComposable(
            enabled = !viewModel.isLocked,
            onPatternComplete = { viewModel.onPatternEntered(it) }
        )
    }
}
