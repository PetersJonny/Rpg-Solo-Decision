import re

with open('src/main/java/fichas/GerenciadorDeConstrucoes.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add companion passarTempo
old_str = """                ficha.diaAtual++;
                ficha.registrarNovoDiaFome();
                if (eraNoite && ficha.companheiro != null) {"""
new_str = """                ficha.diaAtual++;
                ficha.registrarNovoDiaFome();
                if (ficha.companheiro != null) {
                    ficha.companheiro.passarTempo();
                }
                if (eraNoite && ficha.companheiro != null) {"""

content = content.replace(old_str, new_str)

with open('src/main/java/fichas/GerenciadorDeConstrucoes.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("GerenciadorDeConstrucoes.java patched")
