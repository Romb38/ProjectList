package fr.standodua.app.projectlist

object Constants {

    // URL de base pour les listes
    const val LISTS_URL = "https://raw.githubusercontent.com/Romb38/ProjectList/main/list.json"

    // URL de base pour les thèmes
    const val THEME_URL = "https://raw.githubusercontent.com/Romb38/ProjectList/main/theme.json"

    // [TODO] Placer la bonne URL ici (pour l'instant ce n'est pas celle du main)
    // URL de base pour les langues
    const val LANG_URL = "https://raw.githubusercontent.com/Romb38/ProjectList/updatedMain/lang.json"

    // URL de base pour les thématiques non-famille
    // [TODO] Placer ici la bonne URL (pour l'instant ce n'est pas celle du main)
    const val FAM_THME_URL = "https://raw.githubusercontent.com/Romb38/ProjectList/updatedMain/family_theme.json"


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

    // Family Theme blacklist
    var familyModeThemes = emptySet<String>()

    // Family Mode activation state
    var isFamilyMode = true

}
