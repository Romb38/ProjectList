import eld from 'https://cdn.skypack.dev/eld/minified/eld.M60.min.js';

function getLanguageNameInNativeForm(isoCode) {
    console.log(typeof isoCode)
    if (typeof isoCode !== 'string') {
        throw new Error('Invalid ISO language code');
    }

    try {
        const displayNames = new Intl.DisplayNames([navigator.language], { type: 'language' });
        return displayNames.of(isoCode);
    } catch (error) {
        console.error('Error retrieving language name:', error);
        return 'Unknown';
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

    let categories = JSON.parse(localStorage.getItem('categories')) || [];
    let lastUsedTheme = '';
    let lastUsedLang = '';
    let lastUsedDifficulty = 1;
    let themeList = [];

    function saveCategories() {
        localStorage.setItem('categories', JSON.stringify(categories));
    }

    function capitalizeFirstLetter(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }

    fetch('https://raw.githubusercontent.com/Romb38/ProjectList/main/theme.json')
        .then(response => response.json())
        .then(data => {
            themeList = data.map(theme => capitalizeFirstLetter(theme));
            new Awesomplete(themeInput, {
                list: themeList,
                minChars: 1,
                autoFirst: true
            });
            themeInput.value = lastUsedTheme || '';
        })
        .catch(error => {
            console.error('Erreur lors du chargement des thèmes:', error);
            themeList = [];
        });

    detectLangButton.addEventListener('click', () => {
        const text = textInput.value;
        if (text.length > 0) {
            try {
                const result = eld.detect(text);
                const languageName = getLanguageNameInNativeForm(result.language);
                langInput.value = languageName;
            } catch (error) {
                console.error('Erreur lors de la détection de la langue:', error);
            }
        } else {
            alert('Veuillez entrer un texte pour détecter la langue.');
        }
    });

    categoryForm.addEventListener('submit', (event) => {
        event.preventDefault();

        const index = parseInt(editIndexInput.value, 10);
        const newCategory = {
            text: textInput.value,
            difficulty: parseInt(document.getElementById('difficulty').value, 10) || lastUsedDifficulty,
            theme: themeInput.value,
            lang: langInput.value
        };

        const isDuplicate = categories.some(category => category.text === newCategory.text && category !== categories[index]);

        if (isDuplicate) {
            errorMessage.textContent = "Une catégorie avec ce texte existe déjà.";
            return;
        } else {
            errorMessage.textContent = "";
        }

        if (isNaN(index)) {
            categories.push(newCategory);
        } else {
            categories[index] = newCategory;
        }

        const capitalizedTheme = capitalizeFirstLetter(newCategory.theme);
        if (!themeList.includes(capitalizedTheme)) {
            themeList.push(capitalizedTheme);
            new Awesomplete(themeInput, {
                list: themeList,
                minChars: 1,
                autoFirst: true
            });
        }

        lastUsedTheme = newCategory.theme;
        lastUsedLang = newCategory.lang;
        lastUsedDifficulty = newCategory.difficulty;

        saveCategories();
        displayCategories();
        categoryForm.reset();
        editIndexInput.value = '';
    });

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

    window.editCategory = function(index) {
        const category = categories[index];
        textInput.value = category.text;
        document.getElementById('difficulty').value = category.difficulty || lastUsedDifficulty;
        themeInput.value = category.theme || lastUsedTheme;
        langInput.value = category.lang || lastUsedLang;
        editIndexInput.value = index;
    };

    window.deleteCategory = function(index) {
        categories.splice(index, 1);
        saveCategories();
        displayCategories();
    };

    deleteAllButton.addEventListener('click', () => {
        if (confirm('Êtes-vous sûr de vouloir supprimer toutes les catégories ?')) {
            categories = [];
            saveCategories();
            displayCategories();
        }
    });

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

    document.getElementById('difficulty').value = lastUsedDifficulty;

    // Charger les catégories stockées au démarrage
    displayCategories();
});
