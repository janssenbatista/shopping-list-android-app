package dev.janssenbatista.shoppinglist.ui.screens.shoppingListForm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.janssenbatista.shoppinglist.R
import dev.janssenbatista.shoppinglist.data.enums.Colors
import dev.janssenbatista.shoppinglist.ui.components.ColorPicker
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

object ShoppingListFormScreen : Screen {
    private fun readResolve(): Any = ShoppingListFormScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val viewModel: ShoppingListFormViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = stringResource(R.string.create_shopping_list)) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_to_shopping_lists_screen)
                            )
                        }
                    })
            }
        ) { paddingValues ->
            Column(
                Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        Modifier.clickable { uiState.setColorPickerVisible(true) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = stringResource(R.string.color))
                        Canvas(
                            modifier = Modifier
                                .size(24.dp)
                        ) {
                            val radius = size.minDimension / 2
                            drawCircle(
                                color = uiState.shoppingListColor,
                                radius = radius,
                            )
                        }
                    }
                    Column(Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = uiState.shoppingListDescription,
                            onValueChange = {
                                uiState.apply {
                                    onShoppingListDescriptionChange(it)
                                    setShoppingListExists(false)
                                }

                            },
                            label = { Text(text = stringResource(R.string.description)) },
                            isError = uiState.shoppingListExists,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                        )
                        AnimatedVisibility(visible = uiState.shoppingListExists) {
                            Text(
                                text = stringResource(R.string.shopping_list_already_exist),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.save(navigator)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.shoppingListDescription.isNotBlank()
                ) {
                    Text(text = stringResource(R.string.save))
                }

                if (uiState.isColorPickerVisible) {
                    ColorPicker(
                        title = stringResource(R.string.select_a_color),
                        colors = Colors.entries,
                        onColorChange = {
                            uiState.apply {
                                onShoppingListColorChange(it.color)
                                onShoppingListColorIdChange(it.id)
                                setColorPickerVisible(false)
                            }
                        },
                        onDismiss = {
                            uiState.setColorPickerVisible(false)
                        })
                }
            }
        }
    }
}