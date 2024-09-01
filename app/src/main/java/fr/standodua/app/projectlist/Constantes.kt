package fr.standodua.app.projectlist

object Constants {
    // Constante pour la valeur de difficulté maximale

    //[TODO] Calculer cette valeur automatiquement (avec les catégories)
    const val MAX_DIFFICULTY = 3

    // URL de base pour les listes
    const val LISTS_URL = "https://raw.githubusercontent.com/Romb38/ProjectList/main/list.json"

    const val THEME_URL = "https://raw.githubusercontent.com/Romb38/ProjectList/main/theme.json"

    // [TODO] Placer la bonne URL ici !

    const val LANG_URL = ""


}

object Shared{
    // Difficultée actuelle choisie
    var chosen_difficulty = 3

    // Language blacklist
    var languageBlacklist = emptySet<String>()

    // Theme blacklist
    var themeBlackList = emptySet<String>()

    // Family Mode activation state
    var isFamilyMode = true

}
