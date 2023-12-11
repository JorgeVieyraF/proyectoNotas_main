package com.example.proyectoNotas_main.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.proyectoNotas_main.AppViewModelProvider
import com.example.proyectoNotas_main.ComposeFileProvider
import com.example.proyectoNotas_main.InventoryTopAppBar
import com.example.proyectoNotas_main.NavigationDestination
import com.example.proyectoNotas_main.R
import com.example.proyectoNotas_main.VideoPlayer
import com.example.proyectoNotas_main.viewModel.UpdateNotaViewModel
import kotlinx.coroutines.launch

object NotaEditDestination : NavigationDestination {
    override val route = "item_edit"
    override val titleRes = R.string.edit_item_title
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateNotaScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateNotaViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val coroutineScope = rememberCoroutineScope()

    var imageUris by remember { mutableStateOf(listOf<Uri>()) }
    var videoUris by remember { mutableStateOf(listOf<Uri>()) }

    //MULTIMEDIA
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var videoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                imageUris = imageUris.plus(uri!!)
                viewModel.imageUris=viewModel.imageUris.plus(uri!!)
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && imageUri != null) {
                imageUris = imageUris.plus(imageUri!!)
                viewModel.imageUris=viewModel.imageUris.plus(imageUri!!)
            }
        }
    )

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = { success ->
            if (success && videoUri != null) {
                videoUris = videoUris.plus(videoUri!!)
                viewModel.videoUris=viewModel.videoUris.plus(videoUri!!)
            }
        }
    )

    val context = LocalContext.current
    //MULTIMEDIA

    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(NotaEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(55.dp))
            NotaEntryBody(
                notaUiState = viewModel.notaUiState,
                onNotaValueChange = viewModel::updateUiState,
                onSaveClick = {
                    // Note: If the user rotates the screen very fast, the operation may get cancelled
                    // and the item may not be updated in the Database. This is because when config
                    // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                    // be cancelled - since the scope is bound to composition.
                    coroutineScope.launch {
                        viewModel.updateUiState(viewModel.notaUiState.notaDetails)
                        viewModel.updateItem()
                        navigateBack()
                    }
                },
                modifier = Modifier.padding(innerPadding)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.Red),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        val uri = ComposeFileProvider.getImageUri(context)
                        imageUri = uri
                        cameraLauncher.launch(uri)
                    }) {
                    Text(text="Photo")
                }
                Button(
                    onClick = {
                        val uri = ComposeFileProvider.getVideoUri(context)
                        videoUri = uri
                        videoLauncher.launch(uri)
                    } ) {
                    Text(text = "Video")

                }
                Button(onClick = { imagePicker.launch("image/*") }) {
                    Text(text="Gallery")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))        //MOSTRAR MULTIMEDIA

            val imagenesCargadas = viewModel.notaUiState.notaDetails.imageUris.split(",")
            val videosCargados = viewModel.notaUiState.notaDetails.videoUris.split(",")

            val nuevos = imageUris + videoUris

//            val mutableStateFlow: MutableStateFlow<List<NotaMultimedia>> = MutableStateFlow(emptyList())
//            LazyColumn {
//                items(mutableStateFlow) { notaMultimedia ->
//                    Text(text = notaMultimedia.toString())
//                    // Aquí puedes añadir más composables para mostrar la información de NotaMultimedia como prefieras.
//                }
//            }

            val combinedList = listOf(imagenesCargadas, videosCargados, nuevos)
            val idnota = viewModel.notaUiState.notaDetails.id
            LazyColumn {
                itemsIndexed(combinedList) { index, list ->
                    when (index) {
                        0 -> { // Renderiza las imágenes cargadas aquí
                            list.forEach { uri ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        val parsedUri = Uri.parse(uri.toString())
                                        AsyncImage(
                                            model = parsedUri,
                                            modifier = Modifier
                                                .height(400.dp)
                                                .fillMaxWidth()
                                                .align(Alignment.CenterHorizontally),
                                            contentDescription = "Selected image",
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        // Agrega el botón aquí
                                        Button(
                                            onClick = {
                                                // Elimina la tarjeta y quita la imagen del arreglo.
                                                imageUris = imageUris.filter { it != uri }
                                                videoUris = videoUris.filter { it != uri }
                                                val u = Uri.parse(uri.toString())
                                                viewModel.removeUri(u)
                                            },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text("Delete")
                                        }
                                    }
                                }
                            }
                        }
                        1 -> { // Renderiza los videos cargados aquí
                            list.forEach { uri ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        val parsedUri = Uri.parse(uri.toString())
                                        VideoPlayer(
                                            videoUri = parsedUri,
                                            modifier = Modifier
                                                .height(400.dp)
                                                .fillMaxWidth()
                                                .align(Alignment.CenterHorizontally)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        // Agrega el botón aquí
                                        Button(
                                            onClick = {
                                                // Elimina la tarjeta y quita la imagen del arreglo.
                                                imageUris = imageUris.filter { it != uri }
                                                videoUris = videoUris.filter { it != uri }
                                                val u = Uri.parse(uri.toString())
                                                viewModel.removeUri(u)
                                            },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text("Delete")
                                        }
                                    }
                                }
                            }
                        }
                        2 -> { // Renderiza los nuevos elementos aquí
                            Spacer(modifier = Modifier.height(16.dp))
                            list.forEach { uri ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        if (uri in imageUris) {
                                            AsyncImage(
                                                model = uri,
                                                modifier = Modifier
                                                    .height(400.dp)
                                                    .fillMaxWidth()
                                                    .align(Alignment.CenterHorizontally),
                                                contentDescription = "Selected image",
                                            )
                                        } else if (uri in videoUris) {
                                            val parsedUri = Uri.parse(uri.toString())
                                            VideoPlayer(
                                                videoUri = parsedUri,
                                                modifier = Modifier
                                                    .height(400.dp)
                                                    .fillMaxWidth()
                                                    .align(Alignment.CenterHorizontally)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        // Agrega el TextField para la descripción aquí
//                                        TextField(
//                                            value = viewModel.notaMultimediaUiState.notaMultimediaDetails.descripcion,
//                                            onValueChange = { newDescription ->
//                                                viewModel.setNotaMultimediaUiState(
//                                                    viewModel.notaMultimediaUiState.copy(
//                                                        notaMultimediaDetails = viewModel.notaMultimediaUiState.notaMultimediaDetails.copy(descripcion = newDescription)
//                                                    )
//                                                )
//                                            },
//                                            label = { Text("Descripción") },
//                                            modifier = Modifier.fillMaxWidth(),
//                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
//                                        )
//                                        Spacer(modifier = Modifier.height(16.dp))
                                        // Agrega el botón aquí
                                        Button(
                                            onClick = {
                                                // Elimina la tarjeta y quita la imagen del arreglo.
                                                imageUris = imageUris.filter { it != uri }
                                                videoUris = videoUris.filter { it != uri }
                                                val u = Uri.parse(uri.toString())
                                                viewModel.removeUri(u)
                                            },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text(text="Delete")
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }

        }

    }
}

