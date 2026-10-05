import re

with open('README.md', 'r', encoding='utf-8') as f:
    content = f.read()

target = """| **O Labirinto Secreto** | Floresta de Freijord | Balthazar, um ex-aventureiro, está na taverna toda manhã (fale com Draven para encontrá-lo) e quer o chifre do colosso do labirinto como prova de que a fera que matou seus companheiros está morta. | A Espada Jurada (causa 1d12, usando Força) |"""
replacement = """| **O Labirinto Secreto** | Floresta de Freijord | Balthazar, um ex-aventureiro, está na taverna toda manhã (fale com Draven para encontrá-lo) e quer o chifre do colosso do labirinto como prova de que a fera que matou seus companheiros está morta. | A Espada Jurada (causa 1d12 + efeito do vazio) |"""
content = content.replace(target, replacement)

with open('README.md', 'w', encoding='utf-8') as f:
    f.write(content)
