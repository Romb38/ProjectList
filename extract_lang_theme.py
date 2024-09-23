import json
import os
import argparse

def extract_unique_values(data, key):
    return list(set(item[key] for item in data if key in item))

def main(input_file, lang_output='lang.json', themes_output='theme.json'):
    # Vérifier si le fichier d'entrée existe et n'est pas vide
    if not os.path.isfile(input_file):
        print(f"Erreur : Le fichier '{input_file}' n'existe pas.")
        return

    if os.path.getsize(input_file) == 0:
        print(f"Erreur : Le fichier '{input_file}' est vide.")
        return

    # Lire le fichier JSON d'entrée
    try:
        with open(input_file, 'r', encoding='utf-8') as file:
            data = json.load(file)
    except json.JSONDecodeError as e:
        print(f"Erreur de décodage JSON : {e}")
        return
    except Exception as e:
        print(f"Erreur lors de la lecture du fichier : {e}")
        return

    # Vérifier si les données sont sous forme de liste
    if not isinstance(data, list):
        print(f"Erreur : Les données dans le fichier JSON ne sont pas un tableau.")
        return

    # Extraire les langues uniques
    unique_langs = extract_unique_values(data, 'lang')
    # Extraire les thèmes uniques
    unique_themes = extract_unique_values(data, 'theme')

    # Écrire les langues uniques dans lang.json
    try:
        with open(lang_output, 'w', encoding='utf-8') as file:
            json.dump(unique_langs, file, ensure_ascii=False, indent=4)
    except Exception as e:
        print(f"Erreur lors de l'écriture du fichier des langues : {e}")
        return

    # Écrire les thèmes uniques dans themes.json
    try:
        with open(themes_output, 'w', encoding='utf-8') as file:
            json.dump(unique_themes, file, ensure_ascii=False, indent=4)
    except Exception as e:
        print(f"Erreur lors de l'écriture du fichier des thèmes : {e}")
        return

    print(f'Langues uniques écrites dans {lang_output}')
    print(f'Thèmes uniques écrits dans {themes_output}')

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Extract unique languages and themes from a JSON file.')
    parser.add_argument('input_file', type=str, help='Chemin vers le fichier JSON d\'entrée')
    parser.add_argument('--lang_output', type=str, default='lang.json', help='Chemin vers le fichier de sortie pour les langues (par défaut: lang.json)')
    parser.add_argument('--themes_output', type=str, default='themes.json', help='Chemin vers le fichier de sortie pour les thèmes (par défaut: themes.json)')

    args = parser.parse_args()
    
    main(args.input_file, args.lang_output, args.themes_output)
