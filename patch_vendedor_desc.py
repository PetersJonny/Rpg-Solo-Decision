with open('src/main/java/loja/Vendedor.java', 'r', encoding='utf-8') as f:
    content = f.read()

target = """            case "Espada Jurada":
                return new Arma("Espada Jurada", "A lâmina forjada de Balthazar, entregue como recompensa por vingar seus antigos companheiros. Causa 1d12 de dano, usando Força.", "CaC", 12, 1, 1);"""
replacement = """            case "Espada Jurada":
                return new Arma("Espada Jurada", "A lâmina de Balthazar, imbuída com o poder do Vazio. Causa 1d12 de dano (Força). Ao acertar, você rola d20+Força contra d20+Nível do alvo; vencendo, causa +2d6 de dano do vazio.", "CaC", 12, 1, 1);"""

content = content.replace(target, replacement)

with open('src/main/java/loja/Vendedor.java', 'w', encoding='utf-8') as f:
    f.write(content)
