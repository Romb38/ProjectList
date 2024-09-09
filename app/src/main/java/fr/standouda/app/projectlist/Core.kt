package fr.standouda.app.projectlist

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.standouda.app.projectlist.Constants.LISTS_URL
import fr.standouda.app.projectlist.Shared.MAX_DIFFICULTY
import fr.standouda.app.projectlist.Shared.chosen_difficulty
import fr.standouda.app.projectlist.Shared.familyModeThemes
import fr.standouda.app.projectlist.Shared.isFamilyMode
import fr.standouda.app.projectlist.Shared.languageBlacklist
import fr.standouda.app.projectlist.Shared.themeBlackList
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
    val theme: String,
    val lang : String
)

object Cache {
    @Volatile
    private var cachedCategories: List<Category>? = null

    @Volatile
    private var cachedThemes : List<String>? = null

    @Volatile
    private var cachedLang : List<String>? = null

    @Volatile
    private var cachedFamilyTheme : List<String>? = null

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

    fun getCachedLang(): List<String>? {
        return cachedLang
    }

    fun setCachedLang(lang: List<String>) {
        cachedLang = lang
    }

    fun getCachedFamilyTheme(): List<String>? {
        return cachedFamilyTheme
    }

    fun setCachedFamilyTheme(fm: List<String>) {
        cachedFamilyTheme = fm
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


suspend fun fetchStringFromJson(url: String, isLang : Boolean, isFamily : Boolean = false): List<String>? {
    // Vérifier si les données sont déjà en cache
    if (isLang){
        Cache.getCachedLang()?.let{
            Log.d("Cache", "Returning cached data")
            return it
        }
    } else if (isFamily){
        Cache.getCachedFamilyTheme()?.let{
            Log.d("Cache", "Returning cached data")
            return it
        }
    } else {
        Cache.getCachedThemes()?.let {
            Log.d("Cache", "Returning cached data")
            return it
        }
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
                if (isLang){
                    Cache.setCachedLang(themes)
                } else if (isFamily){
                    Cache.setCachedFamilyTheme(themes)
                } else {
                    Cache.setCachedThemes(themes)
                }


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




// Fonction pour obtenir une catégorie au hasard basée sur la difficulté, les thèmes et les langues exclues
fun getRandomCategory(
    categories: List<Category>,
    difficulty: Int,
    excludedThemes: List<String> = emptyList(),
    excludedLanguages: List<String> = emptyList()
): Category? {
    // Filtrer les catégories selon la difficulté, les thèmes exclus et les langues exclues
    val filteredCategories = categories.filter { category ->
        category.difficulty == difficulty &&
                !excludedThemes.contains(category.theme) &&
                !excludedLanguages.contains(category.lang)
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

    //On update la valeur maximale pour la difficultée
    categories.forEach {categorie ->
        if (categorie.difficulty > MAX_DIFFICULTY) {
            MAX_DIFFICULTY = categorie.difficulty
        }
    }
    if (chosen_difficulty > MAX_DIFFICULTY){
        chosen_difficulty = MAX_DIFFICULTY
    }

    // Liste pour stocker les catégories sélectionnées
    val selectedCategories = mutableListOf<Category>()

    // Liste pour suivre les thèmes déjà utilisés
    val usedThemes: MutableSet<String> = themeBlackList.toMutableSet()

    if (isFamilyMode){
        Log.d("getCategory","Le mode famille est activé !")
        usedThemes.addAll(familyModeThemes)
    }

    val invalidLanguage: MutableSet<String> = languageBlacklist.toMutableSet()

    // Boucle sur les valeurs de difficulté 1, 2, 3
    for (difficulty in 1..chosen_difficulty) {
        // Obtenir une catégorie correspondant à la difficulté et excluant les thèmes déjà utilisés
        val category = getRandomCategory(
            categories = categories,
            difficulty = difficulty,
            excludedThemes = usedThemes.toList(),
            excludedLanguages = invalidLanguage.toList()
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
