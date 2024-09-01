document.addEventListener('DOMContentLoaded', () => {
    const themeSelect = document.getElementById('theme');
    const langSelect = document.getElementById('lang');
    const categoryList = document.getElementById('categoryList');
    const categoryForm = document.getElementById('categoryForm');
    const exportButton = document.getElementById('exportButton');
    const deleteAllButton = document.getElementById('deleteAllButton');
    const errorMessage = document.getElementById('errorMessage');
    const editIndexInput = document.getElementById('editIndex');

    let categories = [];
    let lastUsedTheme = '';
    let lastUsedLang = '';
    let lastUsedDifficulty = 1; // Valeur par défaut

    // Fonction pour capitaliser la première lettre d'une chaîne
    function capitalizeFirstLetter(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }

    // Charger les thèmes et les langues
    fetch('https://raw.githubusercontent.com/Romb38/ProjectList/updatedMain/theme.json')
        .then(response => response.json())
        .then(data => {
            data.forEach(theme => {
                const option = document.createElement('option');
                option.value = theme;
                option.textContent = capitalizeFirstLetter(theme);
                themeSelect.appendChild(option);
            });
            // Définir le thème sélectionné précédemment ou la première option
            themeSelect.value = lastUsedTheme || themeSelect.options[0].value;
        });

    fetch('https://raw.githubusercontent.com/Romb38/ProjectList/updatedMain/lang.json')
        .then(response => response.json())
        .then(data => {
            data.forEach(lang => {
                const option = document.createElement('option');
                option.value = lang;
                option.textContent = capitalizeFirstLetter(lang);
                langSelect.appendChild(option);
            });
            // Définir la langue sélectionnée précédemment ou la première option
            langSelect.value = lastUsedLang || langSelect.options[0].value;
        });

    // Ajouter ou modifier une catégorie
    categoryForm.addEventListener('submit', (event) => {
        event.preventDefault();

        const index = parseInt(editIndexInput.value, 10);
        const newCategory = {
            text: document.getElementById('text').value,
            difficulty: parseInt(document.getElementById('difficulty').value, 10) || lastUsedDifficulty,
            theme: document.getElementById('theme').value,
            lang: document.getElementById('lang').value
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
        document.getElementById('text').value = category.text;
        document.getElementById('difficulty').value = category.difficulty || lastUsedDifficulty;
        document.getElementById('theme').value = category.theme || lastUsedTheme;
        document.getElementById('lang').value = category.lang || lastUsedLang;
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
        const json = JSON.stringify(categories, null, 2);
        const blob = new Blob([json], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'categories.json';
        a.click();
        URL.revokeObjectURL(url);
    });

    // Initialiser les valeurs du formulaire avec les valeurs précédemment utilisées
    document.getElementById('difficulty').value = lastUsedDifficulty;
});
