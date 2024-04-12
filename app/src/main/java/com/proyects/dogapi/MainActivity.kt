package com.proyects.dogapi

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.proyects.dogapi.ui.theme.DogApiTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private lateinit var retrofit : Retrofit

class DogMetaData{
    private lateinit var message : String
    private lateinit var status: String

    fun getMessage() : String {
        return this.message
    }
    fun setMessage(name : String){
        this.message = name
    }

    fun getStatus() : String {
        return this.status
    }
    fun setStatus(name : String){
        this.status = name
    }
}

@Composable
fun loadImageResource(id: String, context : Context, fade: Boolean = false): AsyncImagePainter {
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()

    return rememberAsyncImagePainter(
        ImageRequest.Builder(context)
        .data(data = id)
        .crossfade(fade)
        .apply(block = {size(Size.ORIGINAL)})
        .build(), imageLoader = imageLoader)
}

private fun fetch(retrofit : Retrofit) : String{
    var text = "";
    CoroutineScope(Dispatchers.IO).launch {
        val call = retrofit.create(DogAPI::class.java).getUrl().execute()
        val dogImg = call.body()
        text =
            if(call.isSuccessful && dogImg != null) dogImg.getMessage()
            else "An error occurred"
    }
    Thread.sleep(1000)
    return text;
}

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DogApiTheme {

                val context = LocalContext.current
                retrofit = retrofit2.Retrofit.Builder()
                    .baseUrl("https://dog.ceo/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background)
                {
                    val imageUrl = remember{ mutableStateOf("") }

                    Scaffold(
                        topBar = { TopAppBar(title = {Text("DogApi")})  },
                        floatingActionButtonPosition = FabPosition.End,
                        content = { Text("Content") },
                        bottomBar = { BottomAppBar{ Text("Bottom App Bar") } }
                    )


                    Column(modifier = Modifier
                        .verticalScroll(rememberScrollState()))
                    {
                        Button(onClick = { imageUrl.value = fetch(retrofit) }){}
                        Image(painter = loadImageResource(id = imageUrl.value,
                            context = context ),
                            contentDescription = "Dog image")
                        Text(text = "Lorem ipsum es el texto que se usa habitualmente en diseño" +
                                " gráfico en demostraciones de tipografías o de borradores de" +
                                " diseño para probar el diseño visual antes de insertar el texto" +
                                " final. Aunque no posee actualmente fuentes para justificar sus" +
                                " hipótesis, el profesor de filología clásica Richard McClintock" +
                                " asegura que su uso se remonta a los impresores de comienzos del" +
                                " siglo XVI. Su uso en algunos editores de texto muy conocidos en" +
                                " la actualidad ha dado al texto lorem ipsum nueva popularidad.")
                    }

                }
            }
        }
    }
}
