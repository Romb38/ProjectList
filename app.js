
function getLanguageNameInNativeForm(isoCode) {
    if (typeof isoCode !== 'string' || isoCode.length !== 2) {
        throw new Error('Invalid ISO language code');
    }

    try {
        const displayNames = new Intl.DisplayNames([navigator.language], { type: 'language' });
        return displayNames.of(isoCode);
    } catch (error) {
        console.error('Error retrieving language name:', error);
        return 'Unknown language';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const themeInput = document.getElementById('theme');
    const langInput = document.getElementById('lang');
    const detectLangButton = document.getElementById('detectLangButton');
    const categoryList = document.getElementById('categoryList');
    const categoryForm = document.getElementById('categoryForm');
    const exportButton = document.getElementById('exportButton');
    const deleteAllButton = document.getElementById('deleteAllButton');
    const errorMessage = document.getElementById('errorMessage');
    const editIndexInput = document.getElementById('editIndex');
    const textInput = document.getElementById('text');

    let categories = [];
    let lastUsedTheme = '';
    let lastUsedLang = '';
    let lastUsedDifficulty = 1; // Valeur par défaut
    let themeList = [];

    // Fonction pour capitaliser la première lettre d'une chaîne
    function capitalizeFirstLetter(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }

    // Charger les thèmes depuis une source en ligne
    fetch('https://raw.githubusercontent.com/Romb38/ProjectList/main/theme.json')
        .then(response => response.json())
        .then(data => {
            themeList = data.map(theme => capitalizeFirstLetter(theme));
            new Awesomplete(themeInput, {
                list: themeList,
                minChars: 1,
                autoFirst: true
            });

            // Définir le thème sélectionné précédemment ou laisser vide
            themeInput.value = lastUsedTheme || '';
        })
        .catch(error => {
            console.error('Erreur lors du chargement des thèmes:', error);
            themeList = [];
        });

    // Détecter automatiquement la langue lorsqu'on clique sur le bouton Détecter
    detectLangButton.addEventListener('click', () => {
        console.log("here")
        const text = textInput.value;
        console.log('Text:', text);
        if (text.length > 0) {
            try {
                const result = eld.detect(text);
                const languageName = getLanguageNameInNativeForm(result.language);
                langInput.value = languageName; // Remplir automatiquement la langue détectée
            } catch (error) {
                console.error('Erreur lors de la détection de la langue:', error);
            }
        } else {
            alert('Veuillez entrer un texte pour détecter la langue.');
        }
    });

    // Ajouter ou modifier une catégorie
    categoryForm.addEventListener('submit', (event) => {
        event.preventDefault();

        const index = parseInt(editIndexInput.value, 10);
        const newCategory = {
            text: textInput.value,
            difficulty: parseInt(document.getElementById('difficulty').value, 10) || lastUsedDifficulty,
            theme: themeInput.value,
            lang: langInput.value
        };

        // Vérifier les doublons uniquement par texte
        const isDuplicate = categories.some(category => category.text === newCategory.text && category !== categories[index]);

        if (isDuplicate) {
            errorMessage.textContent = "Une catégorie avec ce texte existe déjà.";
            return;
        } else {
            errorMessage.textContent = ""; // Réinitialiser le message d'erreur
        }

        if (isNaN(index)) {
            // Ajouter une nouvelle catégorie
            categories.push(newCategory);
        } else {
            // Modifier une catégorie existante
            categories[index] = newCategory;
        }

        // Ajouter la nouvelle thématique à la liste d'auto-complétion si elle n'existe pas déjà
        const capitalizedTheme = capitalizeFirstLetter(newCategory.theme);
        if (!themeList.includes(capitalizedTheme)) {
            themeList.push(capitalizedTheme);
            // Mettre à jour Awesomplete avec la nouvelle liste de thèmes
            new Awesomplete(themeInput, {
                list: themeList,
                minChars: 1,
                autoFirst: true
            });
        }

        // Mettre à jour les valeurs précédentes utilisées
        lastUsedTheme = newCategory.theme;
        lastUsedLang = newCategory.lang;
        lastUsedDifficulty = newCategory.difficulty;

        displayCategories();
        categoryForm.reset();
        editIndexInput.value = '';
    });

    // Afficher les catégories
    function displayCategories() {
        categoryList.innerHTML = '';
        categories.forEach((category, index) => {
            const div = document.createElement('div');
            div.classList.add('category-item');
            div.innerHTML = `
                <strong>Catégorie ${index + 1}</strong><br>
                Texte : ${category.text}<br>
                Difficulté : ${category.difficulty}<br>
                Thème : ${capitalizeFirstLetter(category.theme)}<br>
                Langue : ${capitalizeFirstLetter(category.lang)}<br>
                <div class="category-actions">
                    <button onclick="editCategory(${index})">Modifier</button>
                    <button onclick="deleteCategory(${index})">Supprimer</button>
                </div>
            `;
            categoryList.appendChild(div);
        });
    }

    // Modifier une catégorie
    window.editCategory = function(index) {
        const category = categories[index];
        textInput.value = category.text;
        document.getElementById('difficulty').value = category.difficulty || lastUsedDifficulty;
        themeInput.value = category.theme || lastUsedTheme;
        langInput.value = category.lang || lastUsedLang;
        editIndexInput.value = index;
    };

    // Supprimer une catégorie
    window.deleteCategory = function(index) {
        categories.splice(index, 1);
        displayCategories();
    };

    // Supprimer toutes les catégories avec confirmation
    deleteAllButton.addEventListener('click', () => {
        if (confirm('Êtes-vous sûr de vouloir supprimer toutes les catégories ?')) {
            categories = [];
            displayCategories();
        }
    });

    // Exporter les catégories en JSON
    exportButton.addEventListener('click', () => {
        if (categories.length === 0) {
            alert('Aucune catégorie à exporter.');
            return;
        }
        const dataStr = JSON.stringify(categories, null, 2);
        const dataUri = 'data:application/json;charset=utf-8,' + encodeURIComponent(dataStr);

        const exportFileName = 'categories.json';
        const linkElement = document.createElement('a');
        linkElement.setAttribute('href', dataUri);
        linkElement.setAttribute('download', exportFileName);
        linkElement.click();
    });

    // Initialiser les valeurs du formulaire avec les valeurs précédemment utilisées
    document.getElementById('difficulty').value = lastUsedDifficulty;
});
