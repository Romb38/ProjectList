package fr.standodua.app.projectlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.standodua.app.projectlist.ui.theme.ProjectListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjectListTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Blue) // Fond bleu pour l'application
                ) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    // Initialisation des couleurs en dehors du remember
    val easyColor = colorResource(R.color.my_cyan)
    val mediumColor = colorResource(R.color.my_yellow)
    val hardColor = colorResource(R.color.my_red)

    // États pour les textes et couleurs des ListItemBox
    val easyText = remember { mutableStateOf("Ca fait penser à Noël") }

    val mediumText = remember { mutableStateOf("Un prénom qui existe (ou pas)") }

    val hardText = remember { mutableStateOf("Le pull préféré de Jean Marie") }

    // Fonction pour mettre à jour un ListItemBox
    fun updateListItemBox(text: String, diff: Int) {
        when (diff) {
            1 -> {
                easyText.value = text
            }
            2 -> {
                mediumText.value = text
            }
            3 -> {
                hardText.value = text
            }
        }
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.my_blue)) /*#352895*/
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Colonne avec les carrés de liste qui occupent presque tout l'espace disponible
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Prend presque tout l'espace vertical disponible
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Facile
                ListItemBox(text = easyText.value, color = easyColor)
                // Normal
                ListItemBox(text = mediumText.value, color = mediumColor)
                // Difficile
                ListItemBox(text = hardText.value, color = hardColor)
            }

            // Bouton carré pour mettre à jour un ListItemBox
            Button(
                onClick = {
                    updateListItemBox("Ceci est un texte", 1) // Mettre à jour l'élément "facile"
                },
                modifier = Modifier
                    .size(100.dp), // Taille du bouton carré
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.my_yellow), // Couleur de fond du bouton (par exemple, violet)
                    contentColor = Color.White // Couleur du texte à l'intérieur du bouton
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh, // Icône de rafraîchissement
                    contentDescription = "Refresh", // Description pour l'accessibilité
                    tint = Color.White, // Couleur de l'icône
                    modifier = Modifier.size(48.dp) // Taille de l'icône
                )
            }

            // Texte sous le bouton, positionné tout en bas de l'écran
            Text(
                text = "Made by Amassif & Romb38",
                color = Color.LightGray,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 30.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ListItemBox(text: String, color : Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(colorResource(R.color.my_red), shape = RoundedCornerShape(40.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(4.dp)
                .background(Color.White,  shape = RoundedCornerShape(30.dp))
        ){

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
                        .background(color, shape = CircleShape) // Point de couleur avec une forme circulaire
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ProjectListTheme {
        MainScreen()
    }
}
