package fr.standodua.app.projectlist

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

// Définition des données
data class Category(
    val text: String,
    val difficulty: Int,
    val theme: String
)

// Fonction suspendue pour récupérer les catégories depuis une URL JSON
suspend fun fetchCategoriesFromJson(): List<Category>? {
    val url = "https://raw.githubusercontent.com/Romb38/ProjectList/main/list.json"
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()

    return withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                Log.d("NetworkResponse", "Response body: $json")

                val gson = Gson()
                val categoryType = object : TypeToken<List<Category>>() {}.type
                gson.fromJson<List<Category>>(json, categoryType)
            } else {
                Log.e("NetworkResponse", "Request failed with status code: ${response.code}")
                null
            }
        } catch (e: IOException) {
            Log.e("NetworkError", "Error fetching data", e)
            null
        }
    }
}

// Fonction principale pour exécuter le code
fun getData(): List<Category>? = runBlocking {
    val categories = fetchCategoriesFromJson()

    //[TODO] Choose wich catégories to use

    val firstThreeCategories = categories?.take(3)

    return@runBlocking firstThreeCategories
}
