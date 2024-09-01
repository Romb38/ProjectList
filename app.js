document.addEventListener('DOMContentLoaded', () => {
    const themeSelect = document.getElementById('theme');
    const langSelect = document.getElementById('lang');
    const categoryList = document.getElementById('categoryList');
    const categoryForm = document.getElementById('categoryForm');
    const exportButton = document.getElementById('exportButton');
    const editIndexInput = document.getElementById('editIndex');

    let categories = [];

    // Charger les thèmes et les langues
    fetch('https://raw.githubusercontent.com/Romb38/ProjectList/updatedMain/theme.json')
        .then(response => response.json())
        .then(data => {
            data.forEach(theme => {
                const option = document.createElement('option');
                option.value = theme;
                option.textContent = theme;
                themeSelect.appendChild(option);
            });
        });

    fetch('https://raw.githubusercontent.com/Romb38/ProjectList/updatedMain/lang.json')
        .then(response => response.json())
        .then(data => {
            data.forEach(lang => {
                const option = document.createElement('option');
                option.value = lang;
                option.textContent = lang;
                langSelect.appendChild(option);
            });
        });

    // Ajouter ou modifier une catégorie
    categoryForm.addEventListener('submit', (event) => {
        event.preventDefault();

        const index = parseInt(editIndexInput.value, 10);
        const newCategory = {
            text: document.getElementById('text').value,
            difficulty: parseInt(document.getElementById('difficulty').value, 10),
            theme: document.getElementById('theme').value,
            lang: document.getElementById('lang').value
        };

        if (isNaN(index)) {
            // Ajouter une nouvelle catégorie
            categories.push(newCategory);
        } else {
            // Modifier une catégorie existante
            categories[index] = newCategory;
        }

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
                Thème : ${category.theme}<br>
                Langue : ${category.lang}<br>
                <button onclick="editCategory(${index})">Modifier</button>
            `;
            categoryList.appendChild(div);
        });
    }

    // Modifier une catégorie
    window.editCategory = function(index) {
        const category = categories[index];
        document.getElementById('text').value = category.text;
        document.getElementById('difficulty').value = category.difficulty;
        document.getElementById('theme').value = category.theme;
        document.getElementById('lang').value = category.lang;
        editIndexInput.value = index;
    };

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
});
