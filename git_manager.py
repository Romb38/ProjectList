import requests
import json  # Importer le module JSON pour le traitement

branche = "bot-test"
token = "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"

headers = {
    'Authorization': f'token {token}',
    'Accept': 'application/vnd.github.v3.raw'
}

# Récupère la liste des item
def get_list():

    url = f'https://raw.githubusercontent.com/Romb38/ProjectList/{branche}/list.json'

    response = requests.get(url, headers=headers)

    # Vérifier si la requête a été réussie
    if response.status_code == 200:
        try:
            # Convertir le texte brut en objet JSON
            json = response.json()
        except json.JSONDecodeError as e:
            print("Erreur lors du décodage JSON:", e)
            print("Contenu brut reçu:", response.text)
    else:
        print(f"Erreur: {response.status_code}")
        print("Détails de l'erreur:", response.text)

    return json

# Récupère la liste des thème
def get_theme():
    
    url = f'https://raw.githubusercontent.com/Romb38/ProjectList/{branche}/theme.json'

    response = requests.get(url, headers=headers)

    # Vérifier si la requête a été réussie
    if response.status_code == 200:
        try:
            # Convertir le texte brut en objet JSON
            json = response.json()
        except json.JSONDecodeError as e:
            print("Erreur lors du décodage JSON:", e)
            print("Contenu brut reçu:", response.text)
    else:
        print(f"Erreur: {response.status_code}")
        print("Détails de l'erreur:", response.text)

    return json

# Récupère les personne whitlise du bot discord
def get_whitelist():
    
    url = f'https://raw.githubusercontent.com/Romb38/ProjectList/{branche}/whitelist.json'

    response = requests.get(url, headers=headers)

    # Vérifier si la requête a été réussie
    if response.status_code == 200:
        try:
            # Convertir le texte brut en objet JSON
            json = response.json()
        except json.JSONDecodeError as e:
            print("Erreur lors du décodage JSON:", e)
            print("Contenu brut reçu:", response.text)
    else:
        print(f"Erreur: {response.status_code}")
        print("Détails de l'erreur:", response.text)

    return json