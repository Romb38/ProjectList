package fr.standodua.app.projectlist

import android.content.Context
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.standodua.app.projectlist.Constants.MAX_DIFFICULTY


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
    val languageOptions = listOf("Français", "English", "Español")
    val selectedLanguages = remember {
        mutableStateOf(languageOptions.toSet())
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Sélectionnez des langues") },
        text = {
            LazyColumn(
            ) {
                items(languageOptions) { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                if (selectedLanguages.value.contains(language)) {
                                    selectedLanguages.value = selectedLanguages.value - language
                                } else {
                                    selectedLanguages.value = selectedLanguages.value + language
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
                                } else {
                                    selectedLanguages.value = selectedLanguages.value - language
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
                    with(sharedPreferences.edit()) {
                        putStringSet("selected_languages", selectedLanguages.value)
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
        },
    )
}


@Composable
fun ThemeSelectionDialog(
    context: Context,
    onDismissRequest: () -> Unit
) {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val themeOptions = listOf("Theme1", "Theme2", "Theme3")
    val selectedTheme = remember {
        mutableStateOf(themeOptions.toSet())
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Sélectionnez des langues") },
        text = {
            LazyColumn(
            ) {
                items(themeOptions) { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                if (selectedTheme.value.contains(theme)) {
                                    selectedTheme.value = selectedTheme.value - theme
                                } else {
                                    selectedTheme.value = selectedTheme.value + theme
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
                                } else {
                                    selectedTheme.value = selectedTheme.value - theme
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
                    with(sharedPreferences.edit()) {
                        putStringSet("selected_languages", selectedTheme.value)
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
        },
    )
}

@Composable
fun ValueSelector(
    value: Int,
    onValueChange: (Int) -> Unit,
    maxValue: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Espace entre le texte et les contrôles de valeur
    ) {
        // Texte à gauche
        Text(
            text = "Diffilcuté maximale",
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
                        onValueChange((value - 1).coerceAtLeast(0))
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
                        onValueChange((value + 1).coerceAtMost(maxValue))
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
fun FamilyModeItem() {
    // Variable pour suivre l'état du switch
    val isFamilyModeEnabled = remember { mutableStateOf(false) }

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
            onCheckedChange = { isFamilyModeEnabled.value = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorResource(id = R.color.my_cyan),
                uncheckedThumbColor = Color.LightGray
            )
        )
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var difficulty by remember { mutableStateOf(3) }

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

                FamilyModeItem()
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
                    onValueChange = { newValue -> difficulty = newValue },
                    maxValue = MAX_DIFFICULTY
                )
                Separator()
            }

            item {
                FullWidthClickableText(text = "Changer les thèmes", onClick = { showThemeDialog = true })
                Separator()
            }

        }
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