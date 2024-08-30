# Bibliothèques Discord
import discord
from discord import app_commands
from discord.ext import commands

import requests

import git_manager

bot = commands.Bot(command_prefix="!", intents=discord.Intents.all())

# Lancement du bot
@bot.event
async def on_ready():
    print("🤖 Le bot est lancé, on lui souhaite bonne chance.")
    await bot.user.edit(username="Le bot de crack list")
    try:
        synced = await bot.tree.sync()
        print(f"Synced {len(synced)} command(s)")
    except Exception as e:
        print(e)

# Condition pour pouvoir utiliser les commandes
def command_right(admin=False):
    def predicate(interaction: discord.Interaction):

        whitlist = git_manager.get_whitelist()

        # Filtre pour les personne dite "Admin"
        if interaction.user.name in whitlist:
            return True
        else:
            return False

    return app_commands.check(predicate)

@bot.tree.command(name="ping", description="Renvoie pong")
@command_right()
async def ping(interaction: discord.Interaction):
    await interaction.response.send_message(f"🏓 pong")

@bot.tree.command(name="add", description="Ajoute un élement")
@command_right()
async def add(interaction: discord.Interaction):
    await interaction.response.send_message(f"🏓 pong")


if __name__ == "__main__":
    bot.run("MTI3OTE3NzUzNTU2OTI2NDY5MA.G75bjv.U_HZvtTt-gy8aMOkhiKVI9QUGPi7aNlZwZVweg")