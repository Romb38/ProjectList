package fr.standodua.app.projectlist

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.standodua.app.projectlist.Constants.FAM_THME_URL
import fr.standodua.app.projectlist.Constants.LANG_URL
import fr.standodua.app.projectlist.Shared.chosen_difficulty
import fr.standodua.app.projectlist.Shared.familyModeThemes
import fr.standodua.app.projectlist.Shared.isFamilyMode
import fr.standodua.app.projectlist.Shared.languageBlacklist
import fr.standodua.app.projectlist.Shared.themeBlackList
import fr.standodua.app.projectlist.ui.theme.ProjectListTheme


class MainActivity : ComponentActivity() {
    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuration de la vue
        setContent {
            ProjectListTheme {
                // Utilisez NavHost pour gérer les écrans
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(navController = navController)
                    }
                    composable("settings") {
                        SettingsScreen(onBack = {
                            navController.popBackStack() // Retour à l'écran précédent
                        })
                    }
                }
            }
        }
        setFullScreen()
    }
}
@Composable
fun MainScreen(navController: NavHostController, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }

    // On récupère les valeurs dans le cache de l'application
    chosen_difficulty = sharedPreferences.getInt("difficulty_value", chosen_difficulty)
    languageBlacklist = sharedPreferences.getStringSet("language_blacklist", emptySet()) ?: emptySet()
    themeBlackList = sharedPreferences.getStringSet("theme_blacklist", emptySet()) ?: emptySet()
    isFamilyMode = sharedPreferences.getBoolean("family_mode", true)
    familyModeThemes = sharedPreferences.getStringSet("family_theme", emptySet()) ?: emptySet()

    // Toujours remettre isDialogShown à true au démarrage
    val isDialogShown = rememberSaveable {
        mutableStateOf(true)
    }

    // Couleurs définies (à adapter pour les tableaux de couleurs plus tard)
    val colors = arrayOf(
        colorResource(R.color.my_cyan),  // Case 1 : Facile
        colorResource(R.color.my_yellow), // Case 2 : Normal
        colorResource(R.color.my_red)    // Case 3 : Difficile
    )

    // Tableau pour les textes des ListItemBox
    val textArray = remember {
        mutableStateOf(Array(chosen_difficulty) { index ->
            "Catégorie n°${index + 1}" // Génère les chaînes "Catégorie n°1", "Catégorie n°2", "Catégorie n°3"
        })
    }

    val isToast = remember { mutableStateOf("") }

    // Fonction pour mettre à jour un ListItemBox
    fun updateListItemBox(text: String, diff: Int) {
        if (diff in 1..chosen_difficulty) {
            textArray.value[diff - 1] = text
        }
    }

    if (isToast.value.isNotEmpty()) {
        ShowToast(isToast.value)
        isToast.value = ""
    }

    fun updateData() {
        val datas: List<Category>? = getData()
        if (datas != null && datas.size == chosen_difficulty) {
            // On mets à jour les catégories dans le jeu
            datas.forEach { data ->
                updateListItemBox(data.text, data.difficulty)
            }
        } else {
            Log.e("getData", "Error - Fetching data")
            isToast.value = "Error - Check your internet connection"
        }
    }

    // Fonction pour générer une couleur à partir de la difficulté (au-delà de 3)
    fun getColorForDifficulty(difficulty: Int): Color {
        return if (difficulty <= colors.size) {
            colors[difficulty - 1]
        } else {
            // Génère une couleur unique basée sur la difficulté
            val seed = difficulty
            Color(
                red = (seed * 1234567 % 256) / 255f,
                green = (seed * 2345678 % 256) / 255f,
                blue = (seed * 3456789 % 256) / 255f
            )
        }
    }

    // Utiliser LaunchedEffect pour appeler updateData lorsque le composable est initialisé
    LaunchedEffect(Unit) {
        val url = FAM_THME_URL
        val fetchedLanguages = fetchStringFromJson(url, false, true) ?: emptyList()
        Log.d("LanguageLoad", "Fetched languages: $fetchedLanguages")
        familyModeThemes = fetchedLanguages.toSet()

        updateData()
        with(sharedPreferences.edit()) {
            remove("dialog_shown")
            apply()
        }
    }

    // Fonction pour mettre à jour SharedPreferences après avoir affiché le dialogue
    fun markDialogAsShown() {
        with(sharedPreferences.edit()) {
            putBoolean("dialog_shown", true)
            apply()
        }
        isDialogShown.value = false
    }

    // Afficher la boîte de dialogue uniquement si c'est la première fois
    if (isDialogShown.value) {
        AlertDialog(
            onDismissRequest = {
                isDialogShown.value = false
                markDialogAsShown()
            },
            title = { Text("Attention") },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cette application fonctionne avec le jeu de société Crack-List (studio Yaqua) mais nous ne sommes pas affiliés d'une quelconque manière aux créateurs de ce jeu. Il s'agit d'une création purement personnelle à but non-commercial."
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    isDialogShown.value = false
                    markDialogAsShown()
                }) {
                    Text("J'ai compris")
                }
            }
        )
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.my_blue))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Colonne avec les carrés de liste qui occupent presque tout l'espace disponible
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Permet à LazyColumn de prendre tout l'espace disponible
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Espacement entre les éléments
            ) {
                items(chosen_difficulty) { i ->
                    ListItemBox(
                        text = textArray.value[i], color = getColorForDifficulty(i + 1)
                    )
                }
            }

            // Ajouter un Spacer pour créer un espace flexible
            Spacer(modifier = Modifier.weight(0.005f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(100.dp) // Ajustez la hauteur en fonction des besoins
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // Spacer qui occupe la première partie
                    Spacer(
                        modifier = Modifier
                            .weight(1f) // Prend 1/3 de la largeur
                    )

                    // Bouton carré pour mettre à jour un ListItemBox, centré
                    Button(
                        onClick = {
                            updateData()
                        },
                        modifier = Modifier
                            .weight(1f) // Prend 1/3 de la largeur
                            .aspectRatio(1f), // Assure que le bouton reste carré
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.my_yellow),
                            contentColor = Color.White
                        ),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    // Bouton des paramètres, aligné à droite dans la troisième partie
                    IconButton(
                        onClick = {
                            // Logique de navigation
                            navController.navigate("settings")
                        },
                        modifier = Modifier
                            .weight(1f) // Prend 1/3 de la largeur
                            .align(Alignment.CenterVertically) // Assure l'alignement vertical au centre
                            .fillMaxWidth(), // Remplit toute la largeur disponible dans la part assignée
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color.LightGray,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
            }

            // Texte sous le bouton, positionné tout en bas de l'écran
            Text(
                text = "Made by Amassif & Romb38",
                color = Color.LightGray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 30.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Fonction pour afficher un Toast
@Composable
fun ShowToast(message: String) {
    val context = LocalContext.current
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun ListItemBox(text: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(167.dp) // Hauteur ajustée pour afficher 3,5 éléments
            .background(colorResource(R.color.my_red), shape = RoundedCornerShape(40.dp))
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(4.dp)
                .background(Color.White, shape = RoundedCornerShape(30.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp), // Padding horizontal pour la Row
                verticalAlignment = Alignment.CenterVertically, // Centre les éléments verticalement
                horizontalArrangement = Arrangement.SpaceBetween // Espace entre le texte et le point de couleur
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f) // Prend tout l'espace horizontal restant
                        .fillMaxHeight(), // Remplit toute la hauteur de la Row
                    contentAlignment = Alignment.Center // Centre le texte dans le Box
                ) {
                    Text(
                        text = text,
                        style = TextStyle(
                            fontSize = 20.sp, // Taille de la police augmentée
                            fontWeight = FontWeight.Bold, // Rend le texte plus gras
                            textAlign = TextAlign.Center, // Centrer le texte horizontalement
                            color = Color.Black // Définit la couleur du texte en noir
                        ),
                        modifier = Modifier.fillMaxWidth() // S'assure que le Text occupe toute la largeur disponible
                    )
                }

                Spacer(modifier = Modifier.width(15.dp)) // Espacement entre le texte et le point de couleur

                Box(
                    modifier = Modifier
                        .size(16.dp) // Taille du point
                        .background(
                            color, shape = CircleShape
                        ) // Point de couleur avec une forme circulaire
                )
            }
        }
    }
}



@Composable
fun MainScreenWithNav(navController: NavHostController, modifier: Modifier = Modifier) {
    MainScreen(navController = navController, modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ProjectListTheme {
        // Create a NavHostController for the preview
        val navController = rememberNavController()

        // Use a NavHost to simulate navigation
        NavHost(navController = navController, startDestination = "main") {
            composable("main") {
                MainScreenWithNav(navController = navController)
            }
            composable("settings") {
                SettingsScreen(onBack = {
                    navController.popBackStack() // Simulates going back
                })
            }
        }
    }
}

