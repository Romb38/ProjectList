package fr.standodua.app.projectlist

object Constants {

    // URL de base pour les listes
    const val LISTS_URL = "https://raw.githubusercontent.com/Romb38/ProjectList/main/list.json"

    // URL de base pour les thèmes
    const val THEME_URL = "https://raw.githubusercontent.com/Romb38/ProjectList/main/theme.json"

    // [TODO] Placer la bonne URL ici !
    // URL de base pour les langues
    const val LANG_URL = ""


}

object Shared{

    //Difficulté maximale, calculée a partir des catégories
    var MAX_DIFFICULTY = 0

    // Difficultée actuelle choisie
    var chosen_difficulty = 3

    // Language blacklist
    var languageBlacklist = emptySet<String>()

    // Theme blacklist
    var themeBlackList = emptySet<String>()

    // Family Mode activation state
    var isFamilyMode = true

}
