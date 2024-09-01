package fr.standodua.app.projectlist

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.standodua.app.projectlist.Constants.LISTS_URL
import fr.standodua.app.projectlist.Constants.MAX_DIFFICULTY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

// Définition des données
data class Category(
    val text: String,
    val difficulty: Int,
    val theme: String
)

object Cache {
    @Volatile
    private var cachedCategories: List<Category>? = null

    @Volatile
    private var cachedThemes : List<String>? = null

    fun getCachedCategories(): List<Category>? {
        return cachedCategories
    }

    fun setCachedCategories(categories: List<Category>) {
        cachedCategories = categories
    }

    fun getCachedThemes(): List<String>? {
        return cachedThemes
    }

    fun setCachedThemes(themes: List<String>) {
        cachedThemes = themes
    }
}

// Fonction suspendue pour récupérer les catégories depuis une URL JSON
suspend fun fetchCategoriesFromJson(): List<Category>? {
    // Vérifier si les données sont déjà en cache
    Cache.getCachedCategories()?.let {
        Log.d("Cache", "Returning cached data")
        return it
    }

    val url = LISTS_URL
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
                val categories = gson.fromJson<List<Category>>(json, categoryType)

                // Mettre les données en cache
                Cache.setCachedCategories(categories)

                categories
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


suspend fun fetchStringFromJson(url: String): List<String>? {
    // Vérifier si les données sont déjà en cache
    Cache.getCachedThemes()?.let {
        Log.d("Cache", "Returning cached data")
        return it
    }

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
                val themeType = object : TypeToken<List<String>>() {}.type
                val themes = gson.fromJson<List<String>>(json, themeType)

                // Mettre les données en cache
                Cache.setCachedThemes(themes)

                themes
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




// Fonction pour obtenir une catégorie au hasard basée sur la difficulté et les thèmes
fun getRandomCategory(
    categories: List<Category>,
    difficulty: Int,
    excludedThemes: List<String> = emptyList()
): Category? {
    // Filtrer les catégories selon la difficulté et exclure celles des thèmes donnés
    val filteredCategories = categories.filter { category ->
        category.difficulty == difficulty && !excludedThemes.contains(category.theme)
    }

    // Sélectionner une catégorie au hasard parmi les catégories filtrées
    return if (filteredCategories.isNotEmpty()) {
        filteredCategories.random() // Choisit une catégorie au hasard
    } else {
        null // Aucun résultat trouvé
    }
}

// Fonction principale pour exécuter le code
fun getData(): List<Category>? = runBlocking {

    val categories = fetchCategoriesFromJson() ?: return@runBlocking null

    // Liste pour stocker les catégories sélectionnées
    val selectedCategories = mutableListOf<Category>()

    // Liste pour suivre les thèmes déjà utilisés
    val usedThemes = mutableSetOf<String>()

    // Boucle sur les valeurs de difficulté 1, 2, 3
    //[TODO] Prendre en compte les paramètres (mode famille, langues ...)
    for (difficulty in 1..MAX_DIFFICULTY) {
        // Obtenir une catégorie correspondant à la difficulté et excluant les thèmes déjà utilisés
        val category = getRandomCategory(
            categories = categories,
            difficulty = difficulty,
            excludedThemes = usedThemes.toList()
        )

        if (category != null) {
            // Ajouter la catégorie sélectionnée à la liste
            selectedCategories.add(category)

            // Ajouter le thème de la catégorie à la liste des thèmes utilisés
            usedThemes.add(category.theme)
        }
    }
    Log.d("getData",selectedCategories.toString())
    return@runBlocking selectedCategories
}
