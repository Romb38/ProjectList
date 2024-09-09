package fr.standouda.app.projectlist

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.standouda.app.projectlist.R
import fr.standouda.app.projectlist.Constants.LANG_URL
import fr.standouda.app.projectlist.Constants.THEME_URL
import fr.standouda.app.projectlist.Shared.MAX_DIFFICULTY
import fr.standouda.app.projectlist.Shared.chosen_difficulty


@Composable
fun Separator(){
    // Séparateur : Ligne noire
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.Black)
    )
}

@Composable
fun LanguageSelectionDialog(
    context: Context,
    onDismissRequest: () -> Unit
) {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // États pour les langues et la blacklist
    val languageOptions = remember { mutableStateOf<List<String>>(emptyList()) }
    val languageBlacklist = remember {
        mutableStateOf(
            sharedPreferences.getStringSet("language_blacklist", emptySet()) ?: emptySet()
        )
    }

    // Charger les options de langue en arrière-plan
    LaunchedEffect(Unit) {
        val url = LANG_URL
        val fetchedLanguages = fetchStringFromJson(url, true) ?: emptyList()
        Log.d("LanguageLoad", "Fetched languages: $fetchedLanguages")
        languageOptions.value = fetchedLanguages
    }

    // Initialiser selectedLanguages en excluant les langues dans la blacklist
    val selectedLanguages = remember {
        mutableStateOf(languageOptions.value.toSet() - languageBlacklist.value)
    }

    // Mettre à jour selectedLanguages lorsque languageOptions ou languageBlacklist changent
    LaunchedEffect(languageOptions.value, languageBlacklist.value) {
        selectedLanguages.value = languageOptions.value.toSet() - languageBlacklist.value
    }

    // Vérifier si les options de langue sont chargées
    if (languageOptions.value.isEmpty()) {
        // Afficher un indicateur de chargement pendant le chargement
        CircularProgressIndicator()
    } else {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Sélectionnez des langues") },
            text = {
                LazyColumn {
                    items(languageOptions.value) { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    if (selectedLanguages.value.contains(language)) {
                                        selectedLanguages.value = selectedLanguages.value - language
                                        languageBlacklist.value = languageBlacklist.value + language
                                    } else {
                                        selectedLanguages.value = selectedLanguages.value + language
                                        languageBlacklist.value = languageBlacklist.value - language
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = language,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Checkbox(
                                checked = selectedLanguages.value.contains(language),
                                onCheckedChange = {
                                    if (it) {
                                        selectedLanguages.value = selectedLanguages.value + language
                                        languageBlacklist.value = languageBlacklist.value - language
                                    } else {
                                        selectedLanguages.value = selectedLanguages.value - language
                                        languageBlacklist.value = languageBlacklist.value + language
                                    }
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Sauvegarder les langues sélectionnées et la blacklist
                        with(sharedPreferences.edit()) {
                            putStringSet("selected_languages", selectedLanguages.value)
                            putStringSet("language_blacklist", languageBlacklist.value)
                            Shared.languageBlacklist = languageBlacklist.value
                            apply()
                        }
                        onDismissRequest()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = onDismissRequest) {
                    Text("Annuler")
                }
                Spacer(modifier = Modifier.width(20.dp))
            }
        )
    }
}



@Composable
fun ThemeSelectionDialog(
    context: Context,
    onDismissRequest: () -> Unit
) {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // États pour les thèmes et la blacklist
    val themeOptions = remember { mutableStateOf<List<String>>(emptyList()) }
    val themeBlacklist = remember {
        mutableStateOf(
            sharedPreferences.getStringSet("theme_blacklist", emptySet()) ?: emptySet()
        )
    }

    // Charger les thèmes en arrière-plan
    LaunchedEffect(Unit) {
        val url = THEME_URL
        val fetchedThemes = fetchStringFromJson(url,false) ?: emptyList()
        Log.d("ThemeLoad", "Fetched themes: $fetchedThemes")
        themeOptions.value = fetchedThemes
    }

    // Initialiser selectedTheme en excluant les thèmes dans la blacklist
    val selectedTheme = remember {
        mutableStateOf(themeOptions.value.toSet() - themeBlacklist.value)
    }

    // Mettre à jour selectedTheme lorsque themeOptions ou themeBlacklist changent
    LaunchedEffect(themeOptions.value, themeBlacklist.value) {
        selectedTheme.value = themeOptions.value.toSet() - themeBlacklist.value
    }

    // Vérifier si les thèmes sont chargés
    if (themeOptions.value.isEmpty()) {
        // Afficher un indicateur de chargement pendant le chargement
        CircularProgressIndicator()
    } else {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Sélectionnez des thèmes") },
            text = {
                LazyColumn {
                    items(themeOptions.value) { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    if (selectedTheme.value.contains(theme)) {
                                        selectedTheme.value = selectedTheme.value - theme
                                        themeBlacklist.value = themeBlacklist.value + theme
                                    } else {
                                        selectedTheme.value = selectedTheme.value + theme
                                        themeBlacklist.value = themeBlacklist.value - theme
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = theme,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Checkbox(
                                checked = selectedTheme.value.contains(theme),
                                onCheckedChange = {
                                    if (it) {
                                        selectedTheme.value = selectedTheme.value + theme
                                        themeBlacklist.value = themeBlacklist.value - theme
                                    } else {
                                        selectedTheme.value = selectedTheme.value - theme
                                        themeBlacklist.value = themeBlacklist.value + theme
                                    }
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Sauvegarder les thèmes sélectionnés et la blacklist
                        with(sharedPreferences.edit()) {
                            putStringSet("selected_themes", selectedTheme.value)
                            putStringSet("theme_blacklist", themeBlacklist.value)
                            Shared.themeBlackList = themeBlacklist.value
                            apply()
                        }
                        onDismissRequest()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = onDismissRequest) {
                    Text("Annuler")
                }
            }
        )
    }
}




@Composable
fun ValueSelector(
    context: Context, // Ajouter le contexte comme paramètre
    value: Int,
    onValueChange: (Int) -> Unit,
    maxValue: Int
) {
    // Obtenez SharedPreferences
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Espace entre le texte et les contrôles de valeur
    ) {
        // Texte à gauche
        Text(
            text = "Difficulté maximale",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .weight(1f) // Permet au texte de prendre toute la place disponible avant les contrôles
                .padding(end = 16.dp) // Espace entre le texte et les contrôles
        )

        // Conteneur pour les contrôles de valeur (boutons et cadre de texte)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bouton de réduction
            IconButton(
                onClick = {
                    if (value > 0) {
                        val newValue = (value - 1).coerceAtLeast(0)
                        onValueChange(newValue)

                        // Enregistrer la nouvelle valeur dans SharedPreferences
                        with(sharedPreferences.edit()) {
                            putInt("difficulty_value", newValue)
                            apply()
                        }
                    }
                },
                enabled = value > 0,
                modifier = Modifier
                    .then(
                        if (value <= 0) Modifier.alpha(0.5f) else Modifier
                    ) // Appliquer une transparence si le bouton est désactivé
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Décrémenter",
                    tint = if (value > 0) Color.White else Color.Gray // Couleur grise pour les boutons désactivés
                )
            }

            // Cadre pour afficher la difficulté
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .background(Color.Black, RoundedCornerShape(4.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$value",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }

            // Bouton d'augmentation
            IconButton(
                onClick = {
                    if (value < maxValue) {
                        val newValue = (value + 1).coerceAtMost(maxValue)
                        onValueChange(newValue)

                        // Enregistrer la nouvelle valeur dans SharedPreferences
                        with(sharedPreferences.edit()) {
                            putInt("difficulty_value", newValue)
                            apply()
                        }
                    }
                },
                enabled = value < maxValue,
                modifier = Modifier
                    .then(
                        if (value >= maxValue) Modifier.alpha(0.5f) else Modifier
                    ) // Appliquer une transparence si le bouton est désactivé
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Incrémenter",
                    tint = if (value < maxValue) Color.White else Color.Gray // Couleur grise pour les boutons désactivés
                )
            }
        }
    }
}

@Composable
fun FullWidthClickableText(text : String,onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .heightIn(80.dp,80.dp)
            .clickable(onClick = onClick) // Rendre le texte cliquable
            .background(Color.Transparent), // Fond transparent pour éviter des effets de bord indésirables
        contentAlignment = Alignment.CenterStart // Alignement du texte à gauche
    ) {
        Text(
            text = text,
            color = Color.White, // Couleur du texte
            fontSize = 20.sp, // Taille de la police
            modifier = Modifier
                .padding(start = 16.dp) // Padding pour éloigner le texte du bord gauche
        )
    }
}

@Composable
fun FamilyModeItem(context: Context) {
    // Accéder à SharedPreferences
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // Charger l'état initial du switch à partir de SharedPreferences
    val isFamilyModeEnabled = remember {
        mutableStateOf(sharedPreferences.getBoolean("family_mode", true))
    }

    // Row pour contenir le texte et le switch
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically, // Centre les éléments verticalement
        horizontalArrangement = Arrangement.SpaceBetween // Espace entre le texte et le switch
    ) {
        Text(
            text = "Mode Famille",
            fontSize = 20.sp,
            color = Color.White
        )

        Switch(
            checked = isFamilyModeEnabled.value,
            onCheckedChange = { isChecked ->
                // Mettre à jour l'état du switch
                isFamilyModeEnabled.value = isChecked

                // Enregistrer l'état du switch dans SharedPreferences
                with(sharedPreferences.edit()) {
                    putBoolean("family_mode", isChecked)
                    apply()
                }
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(id = R.color.my_red),
                uncheckedThumbColor = Color.Gray,
                checkedTrackColor = colorResource(id = R.color.light_red),
                uncheckedTrackColor = Color.LightGray // Couleur du fond (track) désactivé

            )
        )
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var isWIP by remember { mutableStateOf(false) }

    val sharedPreferences = LocalContext.current.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var savedDifficulty = sharedPreferences.getInt("difficulty_value", chosen_difficulty) // 3 est la valeur par défaut si aucune valeur n'est trouvée

    // Si la valeur est supérieur a la valeur maximale dès le départ on la redescend
    if (savedDifficulty > MAX_DIFFICULTY){
        savedDifficulty = MAX_DIFFICULTY
        with(sharedPreferences.edit()) {
            putInt("difficulty_value", MAX_DIFFICULTY)
            apply()
        }
    }

    var difficulty by remember { mutableStateOf(savedDifficulty) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.my_blue)) // Fond d'écran my_blue
    ) {
        // Barre supérieure personnalisée avec la couleur "my_cyan"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(R.color.my_cyan)) // Couleur de la barre supérieure
                .padding(16.dp), // Padding autour des éléments de la barre
            verticalAlignment = Alignment.CenterVertically // Centrage vertical du contenu
        ) {
            // Bouton retour
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp)) // Espace entre le bouton et le texte

            // Titre "Paramètres"
            Text(
                text = "Paramètres",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Menu LazyColumn pour le contenu des paramètres
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {

                FamilyModeItem(context = LocalContext.current)
                Separator()
            }

            item {
                FullWidthClickableText(text = "Changer les langues", onClick = { showLanguageDialog = true })
                Separator()
            }

            item {
                // Exemple de champ de valeur avec boutons
                ValueSelector(
                    value = difficulty,
                    onValueChange = { newValue ->
                        difficulty = newValue
                        chosen_difficulty = newValue
                                    },
                    maxValue = MAX_DIFFICULTY,
                    context = LocalContext.current
                )
                Separator()
            }

            item {
                FullWidthClickableText(text = "Changer les thèmes", onClick = { showThemeDialog = true })
                Separator()
            }

            item {
                // [TODO] Faire le fonctionnement de l'import des listes
                FullWidthClickableText(text = "Importer vos listes (WIP)", onClick = {  isWIP = true })
                Separator()
            }

        }
    }

    if (isWIP){
        ShowToast("Work in progress")
        isWIP = false
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            context = LocalContext.current,
            onDismissRequest = { showLanguageDialog = false }
        )
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            context = LocalContext.current,
            onDismissRequest = { showThemeDialog = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettings(){
    SettingsScreen(onBack = { /* Do nothing for preview */ })
}