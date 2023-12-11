package com.lixoten.fido.feature_notes.presentation._components_shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    screenTitle: String,
    canNavigateUp: Boolean,
    navigateUp: () -> Unit = { },
    isGridLayout: Boolean = false,
    canAdd: Boolean = false,
    onAddRecord: () -> Unit = { },
    onToggleLayout: () -> Unit = { },
    onToggleSection: () -> Unit = { },
    onToggleSearch: () -> Unit = { },
    hasMenu: Boolean = false,
    onNavigationIconClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = screenTitle) },
        navigationIcon = {
            if (canNavigateUp) {
                IconButton(
                    onClick = navigateUp
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = ""
                    )
                }
            }
            if (hasMenu) {
                IconButton(onClick = onNavigationIconClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Toggle drawer"
                    )
                }
            }
        },
        actions = {
            if (canAdd) {
                IconButton(
                    onClick = onAddRecord
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "",
                        //tint = Color.Red
                        //modifier = Modifier.size(24.dp),
                    )
                }

                IconButton(
                    onClick = onToggleLayout
                ) {
                    Icon(
                        imageVector = if (isGridLayout) Icons.Default.GridView else Icons.Default.ViewList,
                        // Danger fix me
                        contentDescription = "",
                    )
                }
                IconButton(
                    onClick = onToggleSection
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "",
                    )
                }
                IconButton(
                    onClick = onToggleSearch
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                    )
                }
            }
        },
    )
}