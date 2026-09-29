package telas;

import itens.ItemRpg;
import salvamento.GerenciadorSaves;
import classes.ClasseRpg;
import fichas.FichaRpg;
import mecanicas.GerenciadorDeItens;
import java.util.Scanner;

import static telas.Interface.*;

public class MenuVisualizacao {

    public static void InspecionarInventario(FichaRpg ficha) {
        while (true) {
            if (ficha.getInventario().isEmpty()) {
                System.out.println(AMARELO + "  Seu inventário está vazio." + RESET);
                return;
            }

            cabecalhoMenu("SEU INVENTÁRIO");
            System.out.println("  Espaço na mochila: " + CIANO + String.format("%.1f", ficha.getPesoTotalMochila()) + RESET + " / " + CIANO + String.format("%.1f", ficha.getCapacidadeMochila()) + RESET + " (Força " + ficha.getForca() + ")");
            System.out.println("\n  Escolha um item para ver a descrição:\n");
            for (int i = 0; i < ficha.getInventario().size(); i++) {
                ItemRpg item = ficha.getInventario().get(i);
                String tipo = mecanicas.GerenciadorDeItens.ehItemConsumivel(item) ? "  [Consumível]" : "";
                System.out.println("  " + (i + 1) + ". " + CIANO + item.getNome() + RESET + " (x" + item.getQuantidade() + ")" + tipo);
            }
            System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

            System.out.println("\n  Digite o número do item que deseja ver a descrição:");
            int escolha = lerOpcao(0, ficha.getInventario().size());
            if (escolha == 0) return;

            ItemRpg itemEscolhido = ficha.getInventario().get(escolha - 1);
            System.out.println("\n" + CIANO + "  -- " + itemEscolhido.getNome().toUpperCase() + " --" + RESET);
            System.out.println("  Descrição: " + itemEscolhido.getDescricao());
            System.out.println(CIANO + "  -----------------------" + RESET);

            boolean consumivel = mecanicas.GerenciadorDeItens.ehItemConsumivel(itemEscolhido);
            int opcaoAcao;
            if (consumivel) {
                System.out.println("\n  O que deseja fazer?");
                System.out.println("  1. Usar este item");
                System.out.println("  2. Dropar este item");
                System.out.println("  3. Voltar");
                opcaoAcao = lerOpcao(3);
            } else {
                System.out.println("\n  O que deseja fazer?");
                System.out.println("  1. Dropar este item");
                System.out.println("  2. Voltar");
                opcaoAcao = lerOpcao(2);
            }

            if (consumivel && opcaoAcao == 1) {
                String nomeItem = itemEscolhido.getNome();
                boolean cheio = false;
                boolean eComida = nomeItem.equals("Frutas") || nomeItem.equals("Carne de Lobo") || nomeItem.equals("Carne de Urso");
                if (nomeItem.equals("Kit Médico") && ficha.getVidaPersonagem() >= ficha.getVidaMaxima() && !ficha.isInfectado()) {
                    ExibirErro("Sua vida já está no máximo!");
                    cheio = true;
                } else if ((nomeItem.equals("Poção de Mana") || nomeItem.equals("Poção Grande de Mana")) && ficha.getManaPersonagem() >= ficha.getManaMaxima()) {
                    ExibirErro("Sua mana já está no máximo!");
                    cheio = true;
                }
                                if (!cheio) {
                    int quantidade = 1;
                    if (itemEscolhido.getQuantidade() > 1) {
                        System.out.println("  Quantidade para usar (1 a " + itemEscolhido.getQuantidade() + "): ");
                        int qtd = lerInteiro();
                        if (qtd > 0 && qtd <= itemEscolhido.getQuantidade()) {
                            quantidade = qtd;
                        }
                    }
                    mecanicas.GerenciadorDeItens.usarItemForaDeCombate(ficha, itemEscolhido, quantidade);
                }
            } else if (consumivel ? opcaoAcao == 2 : opcaoAcao == 1) {
                Interface.droparItemDoInventario(ficha, itemEscolhido);
            }
        }
    }

    public static void InspecionarHabilidades(FichaRpg ficha) {
        if (ficha.getHabilidades().isEmpty()) {
            System.out.println(AMARELO + "  Você não possui nenhuma habilidade." + RESET);
            return;
        }

        cabecalhoMenu("SUAS HABILIDADES");
        System.out.println("\n  Escolha uma habilidade para ler a descrição:\n");
        for (int i = 0; i < ficha.getHabilidades().size(); i++) {
            habilidades.Habilidade hab = ficha.getHabilidades().get(i);
            System.out.println("  " + (i + 1) + ". " + CIANO + hab.getNome() + RESET + " (Custo: " + hab.getCustoMana() + " Mana)");
        }
        System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

        System.out.println("\n  Digite o número da habilidade que deseja ler a descrição:");
        int escolha = lerOpcao(0, ficha.getHabilidades().size());

        if (escolha > 0 && escolha <= ficha.getHabilidades().size()) {
            habilidades.Habilidade habEscolhida = ficha.getHabilidades().get(escolha - 1);
            System.out.println("\n" + CIANO + "  -- " + habEscolhida.getNome().toUpperCase() + " --" + RESET);
            System.out.println("  Custo de Mana: " + habEscolhida.getCustoMana());
            System.out.println("  Descrição: " + habEscolhida.getDescricao());
            System.out.println(CIANO + "  -----------------------" + RESET);
        }
    }

    public static void MostrarFicha(FichaRpg ficha) {
        cabecalhoMenu("FICHA DO PERSONAGEM");
        ClasseRpg classe = ficha.getClasseDoPersonagem();
        String nomeDaClasse = (classe != null) ? classe.getNome() : "Nenhuma";

                StringBuilder combate = new StringBuilder();
        for (ItemRpg item : ficha.getInventario()) {
            if (item instanceof itens.Arma) {
                itens.Arma arma = (itens.Arma) item;
                combate.append("\n    - ").append(arma.getNome())
                    .append("  (Dano: ").append(arma.getQuantidadeDanoArma())
                    .append("d").append(arma.getDadoDanoArma())
                    .append(" | Tipo: ").append(arma.getTipoArma())
                    .append(" | Atributo: ").append(arma.getAtributoAtaque()).append(")");
            }
        }

                String socoNome = "Soco";
        String socoTipo = "-";
        int socoQtdDano = 0;
        int socoDado = 0;
        if (classe != null && classe.getAtaqueDesarmado() != null) {
            socoNome = classe.getAtaqueDesarmado().getNome();
            socoTipo = classe.getAtaqueDesarmado().getTipoArma();
            socoQtdDano = classe.getAtaqueDesarmado().getQuantidadeDanoArma();
            socoDado = classe.getAtaqueDesarmado().getDadoDanoArma();
        }
        combate.append("\n    - ").append(socoNome)
            .append("  (Dano: ").append(socoQtdDano).append("d").append(socoDado)
            .append(" | Tipo: ").append(socoTipo).append(")");

        System.out.println("\n  Nome: " + CIANO + ficha.getNomePersonagem() + RESET + "        Nível: " + ficha.getNivel() + (ficha.getNivel() < 10 ? "  (XP: " + ficha.getXp() + "/" + fichas.FichaRpg.getXpNecessaria(ficha.getNivel()) + ")" : "  (XP: " + ficha.getXp() + " - Nível máximo)"));
        String modoFicha = ficha.isModoDificil() ? AMARELO + "Difícil" : VERDE + "Normal";
        String modoLabel = ficha.isModoDificil() ? " [DIFÍCIL]" : " [Normal]";
        System.out.println("  Dono da ficha: " + ficha.getNomePessoa() + "      Classe: " + nomeDaClasse + "      Raça: " + (ficha.getRaca() != null ? ficha.getRaca().getNome() : "Nenhuma") + "      Modo: " + modoFicha + RESET + modoLabel);
        if (ficha.getRaca() != null) {
            racas.Raca raca = ficha.getRaca();
            System.out.println("  " + CIANO + "[ RAÇA — " + raca.getNome() + " ]" + RESET + "   " + raca.getBonusDescricao());
            System.out.println("    Passiva: " + AMARELO + raca.getPassiva() + RESET + " — " + raca.getPassivaDescricao());
        }
        System.out.println("  Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + "        Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
        System.out.println("  Ouro: " + ficha.getOuro());

        System.out.println("\n  " + CIANO + "[ ATRIBUTOS ]" + RESET);
        System.out.println("    Constituição: " + ficha.getConstituicao() + "    Destreza: " + ficha.getDestreza());
        System.out.println("    Força: " + ficha.getForca() + "    Sabedoria: " + ficha.getSabedoria());
        System.out.println("    Intelecto: " + ficha.getIntelecto() + "    Presença: " + ficha.getPresenca());

        System.out.println("\n  " + CIANO + "[ COMBATE ]" + RESET + combate);
        System.out.println("    Defesa: " + ficha.getDefesa());

        System.out.println("\n  " + CIANO + "[ ABRIGO E TEMPO ]" + RESET);
        System.out.println("    Período: " + ficha.getPeriodoDescritivo() + " (" + (3 - ficha.getProgressoPeriodo()) + "/3 para virar)");
        String cabanaStatus = !ficha.isTemCabana()
                ? "Não construída"
                : (ficha.isNaCabana() ? "Construída (você está nela)" : "Construída (você está longe dela)");
        System.out.println("    Cabana: " + cabanaStatus + " | Dias sem dormir: " + ficha.getDiasSemDormir() + (ficha.isCansado() ? " (CANSADO: -1 em testes)" : ""));
        String salaStatus = !ficha.isTemSalaTreino()
                ? "Não construída"
                : (ficha.isSalaJuntoCabana() ? "Construída (junto à cabana)"
                    : ficha.isSalaJuntoMesa() ? "Construída (junto à mesa)" : "Construída (longe da cabana)");
        System.out.println("    Sala de Treino: " + salaStatus + (ficha.isNaSalaTreino() ? " (você está nela)" : "") + (ficha.getTreinoBonusPeriodosRestantes() > 0 ? " | Bônus de treino: +2 em " + ficha.getTreinoBonusAtributo() + " (restam " + ficha.getTreinoBonusPeriodosRestantes() + " períodos)" : ""));
        String mesaStatus = !ficha.isTemMesaMagias()
                ? "Não construída"
                : (ficha.isMesaJuntoCabana() ? "Construída (junto à cabana)"
                    : ficha.isMesaJuntoSala() ? "Construída (junto à sala)" : "Construída (longe da cabana)");
        System.out.println("    Mesa de Magias: " + mesaStatus + (ficha.isNaMesaMagias() ? " (você está nela)" : "") + (ficha.getMagiaBonusPeriodosRestantes() > 0 ? " | Bônus de estudo: +1 dado de dano (restam " + ficha.getMagiaBonusPeriodosRestantes() + " períodos)" : ""));
        String fogueiraStatus = !ficha.isTemFogueira()
                ? "Não construída"
                : (ficha.isFogueiraJuntoCabana() ? "Construída (junto à cabana)"
                    : ficha.isFogueiraJuntoSala() ? "Construída (junto à sala)"
                    : ficha.isFogueiraJuntoMesa() ? "Construída (junto à mesa)" : "Construída (longe da cabana)");
        System.out.println("    Fogueira: " + fogueiraStatus + (ficha.isNaFogueira() ? " (você está nela)" : "") + " | Cozinha TODAS as carnes cruas de uma vez (2x Madeira)");

        System.out.println("\n  " + CIANO + "[ INVENTÁRIO ]" + RESET);
        if (ficha.getInventario().isEmpty()) {
            System.out.println("    - Vazio");
        } else {
            for (ItemRpg item : ficha.getInventario()) {
                System.out.println("    - " + item.getNome() + " (x" + item.getQuantidade() + ")");
            }
        }

        System.out.println("\n  " + CIANO + "[ HABILIDADES ]" + RESET);
        if (ficha.getHabilidades().isEmpty()) {
            System.out.println("    - Nenhuma");
        } else {
            for (habilidades.Habilidade hab : ficha.getHabilidades()) {
                System.out.println("    - " + hab.getNome() + " (Custo: " + hab.getCustoMana() + " Mana)");
            }
        }

        System.out.println("\n" + CIANO + "==========================================================================================" + RESET);
    }

    public static int MenuFicha() {
        System.out.println("\n");
        cabecalhoMenu("SUA FICHA");
        System.out.println("\n  O que deseja fazer?\n");
        System.out.println("  1. Ver Habilidades");
        System.out.println("  2. Ver Inventário (Ler descrições)");
        System.out.println("  3. Voltar para a Aventura");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        return lerOpcao(3);
    }
}
