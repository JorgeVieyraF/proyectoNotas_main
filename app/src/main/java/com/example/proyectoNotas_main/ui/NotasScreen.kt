package com.example.proyectoNotas_main.ui

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoNotas_main.model.Nota
import androidx.navigation.NavHostController
import com.example.proyectoNotas_main.AppTopBar
import com.example.proyectoNotas_main.AppViewModelProvider
import com.example.proyectoNotas_main.BarraBusqueda
import com.example.proyectoNotas_main.CustomTopAppBar
import com.example.proyectoNotas_main.viewModel.NotasScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasList(
    modifier: Modifier = Modifier,
    configuration: Configuration,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: NotasScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController,
    navigateToItemUpdate: (Int) -> Unit,
){
    val homeUiState by viewModel.homeUiState.collectAsState()

    Scaffold(
         topBar = {
            Column(){
                CustomTopAppBar("Notes")
                if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    // Si estamos en modo landscape, muestra la barra de búsqueda arriba
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        BarraBusqueda(
                            label = "Search note",
                            leadingIcon = Icons.Filled.Search,
                            value = viewModel.busquedaInput.value,
                            onValueChanged = { viewModel.busquedaInput.value = it },
                            modifier = Modifier
                                .fillMaxWidth(.1f)
                                .background(Color.White),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            )
                        )
                        FloatingActionButton(
                            onClick = { navController.navigate(Routes.NotasEditor.route) },
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.Red)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar")
                            Text(text = "Edit")
                        }
                    }
                } else {
                    // Si no estamos en modo landscape, muestra la barra de búsqueda como antes
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(Color.White)) {
                        BarraBusqueda(
                            label = "Search note",
                            leadingIcon = Icons.Default.Search,
                            value = viewModel.busquedaInput.value,
                            onValueChanged = { viewModel.busquedaInput.value = it },
                            modifier = Modifier
                                .padding(bottom = 32.dp)
                                .fillMaxWidth(.925f)
                                .background(Color.White),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {
                // Si no estamos en modo landscape, muestra el FAB como antes
                FloatingActionButton(onClick = { navController.navigate(Routes.NotasEditor.route) }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            AppTopBar(navController = navController)
        }
    ){ innerPadding ->
        HomeBody(
            notaList = homeUiState.itemList,
            onNotaClick = navigateToItemUpdate,
            busquedaInput = viewModel.busquedaInput.value,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        )
    }
}

@Composable
private fun HomeBody(
    notaList: List<Nota>,
    onNotaClick: (Int) -> Unit,
    busquedaInput: String,
    modifier: Modifier = Modifier
) {
    // Filtrar las notas si hay texto en la barra de búsqueda
    val notasFiltradas = notaList.filter { nota ->
        busquedaInput.isEmpty() || nota.name.contains(busquedaInput, ignoreCase = true)
    }
    // Ordenar las notas por fecha de forma descendente
    val notasOrdenadas = notasFiltradas.sortedByDescending { it.fecha }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        NotasList(
            notaList = notasOrdenadas,
            onNotaClick = { onNotaClick(it.id) },
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .background(Color.White)
        )
    }
}

@Composable
private fun NotasList(
    notaList: List<Nota>, onNotaClick: (Nota) -> Unit, modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier
        .background(Color.White)) {
        items(items = notaList, key = { it.id }) { nota ->
            InventoryNota(nota = nota,
                modifier = Modifier
                    .padding(10.dp)
                    .background(Color.White)
                    .clickable { onNotaClick(nota) })
        }
    }
}

@Composable
private fun InventoryNota(
    nota: Nota, modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 72.dp)
                .background(Color.White)
        ) {
            Column(modifier = Modifier
                .weight(1f)
                .background(Color.White)) {
                Text(
                    text = nota.name
                )
                Text(
                    text = nota.contenido
                )
            }
        }
    }
}


private data class NavigationItemContent(
    val title: String,
    val icon: @Composable () -> Unit,
    val route: String
)