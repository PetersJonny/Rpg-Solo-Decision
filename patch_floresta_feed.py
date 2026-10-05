import re

with open('src/main/java/eventos/Floresta.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add option to menu
old_menu = """            if (podeCurar) {
                opCurar = num++;
                System.out.println("  " + opCurar + ". Curar " + comp.getNome() + " com um Kit Médico");
            }
            opDespedir = num++;"""
new_menu = """            if (podeCurar) {
                opCurar = num++;
                System.out.println("  " + opCurar + ". Curar " + comp.getNome() + " com um Kit Médico");
            }
            int opAlimentar = -1;
            boolean temComida = false;
            for (itens.ItemRpg it : ficha.getInventario()) {
                if (it instanceof itens.Consumivel && !it.getNome().equals("Kit Médico") && !it.getNome().contains("Mana") && !it.getNome().contains("Veneno") && !it.getNome().contains("Diabo")) {
                    temComida = true; break;
                }
            }
            if (temComida && comp.getDiasSemComer() > 0) {
                opAlimentar = num++;
                System.out.println("  " + opAlimentar + ". Alimentar " + comp.getNome());
            }
            opDespedir = num++;"""
content = content.replace(old_menu, new_menu)

# Handle option logic
old_logic = """            } else if (escolha == opCurar) {
                curarCompanheiroComKit(ficha);
            } else if (escolha == opDespedir) {"""
new_logic = """            } else if (escolha == opCurar) {
                curarCompanheiroComKit(ficha);
            } else if (escolha == opAlimentar) {
                java.util.List<itens.ItemRpg> comidas = new java.util.ArrayList<>();
                for (itens.ItemRpg it : ficha.getInventario()) {
                    if (it instanceof itens.Consumivel && !it.getNome().equals("Kit Médico") && !it.getNome().contains("Mana") && !it.getNome().contains("Veneno") && !it.getNome().contains("Diabo")) {
                        comidas.add(it);
                    }
                }
                System.out.println("\\n  Qual comida você quer dar para " + comp.getNome() + "?");
                for (int i = 0; i < comidas.size(); i++) {
                    System.out.println("  " + (i + 1) + ". " + comidas.get(i).getNome() + " (x" + comidas.get(i).getQuantidade() + ")");
                }
                System.out.println("  0. Cancelar");
                int escComida = telas.Interface.lerOpcao(0, comidas.size());
                if (escComida > 0) {
                    itens.ItemRpg escolhida = comidas.get(escComida - 1);
                    ficha.consumirItem(escolhida, 1);
                    if (escolhida.getQuantidade() <= 0) ficha.getInventario().remove(escolhida);
                    comp.alimentar();
                    telas.Interface.MostrarMensagem("\\nVocê dá " + escolhida.getNome() + " para " + comp.getNome() + ". A fome passa e a gratidão cresce!");
                    telas.Interface.Pausa(2000);
                }
            } else if (escolha == opDespedir) {"""
content = content.replace(old_logic, new_logic)

with open('src/main/java/eventos/Floresta.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("Floresta.java patched for feeding")
