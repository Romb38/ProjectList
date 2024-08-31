package fr.standodua.app.projectlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun separator(){
    // Séparateur : Ligne noire
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.Black)
    )
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
                separator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun previewSettings(){
    SettingsScreen(onBack = { /* Do nothing for preview */ })
}