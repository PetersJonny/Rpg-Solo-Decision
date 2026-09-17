package eventos;

import fichas.FichaRpg;
import itens.Consumivel;
import itens.ItemRpg;
import mecanicas.MecanicasRpg;
import telas.Interface;
import companheiros.CompanionManager;

public class ExplorationManager {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;

    public static void avancarTempoComMensagens(FichaRpg ficha, int unidades) {
        boolean virou = ficha.avancarTempo(unidades);
        if (!virou) {
            if (ficha.getProgressoPeriodo() >= 2) {
                String proximo = ficha.isEhNoite() ? "dia" : "noite";
                Interface.MostrarMensagem("\n(Falta pouco para " + proximo + " chegar: " + (3 - ficha.getProgressoPeriodo()) + "/3 restantes.)");
                Interface.Pausa(1000);
            }
            return;
        }
        Interface.Pausa(1000);
        if (ficha.isEhNoite()) {
            Interface.MostrarMensagem("\nO sol se põe no horizonte e a noite cai sobre Freijord... " + AMARELO + "(" + ficha.getPeriodoDescritivo() + ")" + RESET);
            Interface.Pausa(2000);
            if (ficha.temCompanheiro()) {
                companheiros.Companheiro comp = ficha.getCompanheiro();
                if (!comp.isDormiuPrimeiraVez()) {
                    Interface.MostrarMensagem("\nPela primeira vez, " + comp.getNomeCompleto() + " se acomoda na cabana para dormir. De manhã, volta a te seguir.");
                } else {
                    Interface.MostrarMensagem("\n" + comp.getNomeCompleto() + " continua contigo por mais uma noite.");
                }
                Interface.Pausa(2000);
            }
            if (ficha.isCansado()) {
                Interface.MostrarMensagem("\n(Você está há mais de 2 dias sem dormir! Está cansado: -1 em todos os atributos em testes até dormir.)");
                Interface.Pausa(2000);
            }
        } else {
            Interface.MostrarMensagem("\nOs primeiros raios de sol anunciam o amanhecer... é " + AMARELO + ficha.getPeriodoDescritivo() + RESET + " em Freijord.");
            Interface.Pausa(2000);
            CompanionManager.verificarCompanheiroPosDormir(ficha);
        }

        // Ao ter uma cabana, pode aparecer alguém perdido (20% por período, no dia ou na noite)
        if (ficha.isTemCabana() && !ficha.temCompanheiro() && MecanicasRpg.rolarDado(100) <= 20) {
            CompanionManager.EventoPerdido(ficha);
        }
    }

    public static void Explorar(FichaRpg ficha) {
        Interface.cabecalhoMenu("EXPLORAÇÃO");
        Interface.MostrarMensagem("\n  Você adentra as matas geladas da floresta de Freijord...  " + CIANO + "(1/3 de período)" + RESET);
        Interface.Pausa(2000);
        Interface.MostrarMensagem("O vento frio corta entre as árvores e você observa o ambiente ao redor...");
        Interface.Pausa(2000);

        if (ficha.isTemCabana() && ficha.isNaCabana()) {
            Interface.MostrarMensagem("\nVocê deixa sua cabana para trás e se embrenha na floresta.");
            Interface.Pausa(1500);
            ficha.sairDaCabana();
        }

        // Sempre há um encontro ao explorar a floresta (vendedor, fada ou criatura)
        Floresta.EventoAnimal(ficha);

        avancarTempoComMensagens(ficha, 1);
    }

    public static void BuscarRecursos(FichaRpg ficha) {
        Interface.cabecalhoMenu("BUSCAR RECURSOS");
        Interface.MostrarMensagem("\n  Você percorre a floresta em busca de materiais úteis...  " + CIANO + "(1/3 de período)" + RESET);
        Interface.Pausa(2000);

        if (ficha.isTemCabana() && ficha.isNaCabana()) {
            Interface.MostrarMensagem("\nVocê deixa sua cabana para trás e se afasta em direção aos bosques.");
            Interface.Pausa(1500);
            ficha.sairDaCabana();
        }

        boolean achouAlgo = false;
        achouAlgo |= coletarRecurso(ficha, "Madeira", 40, "Troncos e galhos fortes para construção.");
        achouAlgo |= coletarRecurso(ficha, "Folha", 55, "Folhas secas e verdes, úteis como cobertura.");
        achouAlgo |= coletarRecurso(ficha, "Pedra", 35, "Pedras arredondadas de rio, boas para construir.");
        achouAlgo |= coletarRecurso(ficha, "Frutas", 15, "Frutas silvestres comestíveis. Cada uma cura 1d2 de vida.");

        if (!achouAlgo) {
            Interface.MostrarMensagem("\nVocê vasculhou os arredores, mas não encontrou nada aproveitável desta vez.");
            Interface.Pausa(2000);
        }

        // 30% de chance de cruzar com uma criatura (50% durante a noite)
        int chanceEncontro = ficha.isEhNoite() ? 50 : 30;
        if (MecanicasRpg.rolarDado(100) <= chanceEncontro) {
            Interface.MostrarMensagem("\nEnquanto recolhe materiais, você percebe um movimento suspeito nas sombras...");
            Interface.Pausa(1500);
            Floresta.EventoAnimal(ficha);
        }

        avancarTempoComMensagens(ficha, 1);
    }

    private static boolean coletarRecurso(FichaRpg ficha, String nome, int chance, String descricao) {
        if (MecanicasRpg.rolarDado(100) > chance) return false;
        int quantidade = MecanicasRpg.rolarEntre(1, 3);
        ItemRpg item = nome.equals("Frutas")
                ? new Consumivel(nome, descricao, quantidade)
                : new ItemRpg(nome, descricao, quantidade);
        ficha.adicionarItem(item);
        Interface.MostrarMensagem("Você encontrou " + quantidade + "x " + nome + "!");
        Interface.Pausa(1200);
        return true;
    }

    public static void MenuConstrucao(FichaRpg ficha) {
        while (true) {
            Interface.cabecalhoMenu("C O N S T R U Ç Ã O");

            String periodo = ficha.getPeriodoDescritivoMaiusculo();
            System.out.println("\n  Período: " + AMARELO + periodo + RESET + "  (" + (3 - ficha.getProgressoPeriodo()) + "/3 para virar)");

            String local;
            if (ficha.isNaSalaTreino()) {
                String tipoSala = ficha.isSalaJuntoCabana() ? "junto à cabana" : "longe da cabana";
                local = "NA SALA DE TREINO (" + tipoSala + ")";
            } else if (ficha.isNaCabana() && ficha.isTemCabana()) {
                local = "NA CABANA";
            } else if (ficha.isTemCabana()) {
                local = "LONGE da cabana (na floresta)";
            } else {
                local = "na floresta (sem cabana)";
            }
            System.out.println("  Localização: " + CIANO + local + RESET);
            System.out.println("  -----------------------------------------------");

            String statusCabana = ficha.isTemCabana() ? VERDE + "construída" + RESET : AMARELO + "não construída" + RESET;
            System.out.println("\n  " + CIANO + "[ CABANA ]" + RESET + "  Status: " + statusCabana);
            System.out.println("  Custo:    " + ficha.getQuantidadeDe("Madeira") + "/7x Madeira | "
                    + ficha.getQuantidadeDe("Folha") + "/10x Folha | "
                    + ficha.getQuantidadeDe("Pedra") + "/4x Pedra");
            if (ficha.isTemCabana()) {
                System.out.println("  Informação: Pode dormir à noite (estando nela) e serve de abrigo para o companheiro.");
            } else {
                System.out.println("  Informação: Gasta 2/3 do período para montar.");
            }

            String statusSala = ficha.isTemSalaTreino() ? VERDE + "construída" + RESET : AMARELO + "não construída" + RESET;
            System.out.println("\n  " + CIANO + "[ SALA DE TREINO ]" + RESET + "  Status: " + statusSala);
            System.out.println("  Custo:    " + ficha.getQuantidadeDe("Madeira") + "/10x Madeira | "
                    + ficha.getQuantidadeDe("Folha") + "/15x Folha | "
                    + ficha.getQuantidadeDe("Pedra") + "/5x Pedra | "
                    + ficha.getQuantidadeDe("Couro") + "/4x Couro");
            if (!ficha.isTemSalaTreino()) {
                System.out.println("  Informação: Gasta 2/3 do período para montar.");
            }

            if (ficha.getTreinoBonusPeriodosRestantes() > 0) {
                System.out.println("\n  " + VERDE + "+2 em " + ficha.getTreinoBonusAtributo() + " ativo" + RESET + " (restam " + ficha.getTreinoBonusPeriodosRestantes() + " períodos)");
            }

            System.out.println("\n  -----------------------------------------------");

            int opCabana = 0, opSala = 0, opDormir = 0;
            int num = 1;

            System.out.println("\n  O que deseja fazer?");
            if (!ficha.isTemCabana()) {
                System.out.println("  " + num + ". Montar Cabana  (7x Madeira, 10x Folha, 4x Pedra — 2/3 do período)");
                opCabana = num++;
            } else if (!ficha.isNaCabana()) {
                System.out.println("  " + num + ". Ir para a Cabana  (1/3 do período)");
                opCabana = num++;
            }

            if (!ficha.isTemSalaTreino() && ficha.isTemCabana()) {
                System.out.println("  " + num + ". Montar Sala de Treino  (10x Madeira, 15x Folha, 5x Pedra, 4x Couro — 2/3 do período)");
                opSala = num++;
            } else if (ficha.isTemSalaTreino() && !ficha.isNaSalaTreino() && !ficha.isNaCabana()) {
                System.out.println("  " + num + ". Ir para a Sala de Treino  (1/3 do período)");
                opSala = num++;
            }

            if (ficha.isTemSalaTreino() && !ficha.isNaSalaTreino()
                    && (ficha.getTreinoBonusPeriodosRestantes() <= 0)) {
                System.out.println("  " + num + ". Treinar na Sala de Treino  (período inteiro; +2 em Força ou Destreza por 2 períodos)");
                opSala = num++;
            } else if (ficha.isTemSalaTreino() && !ficha.isNaSalaTreino()
                    && ficha.getTreinoBonusPeriodosRestantes() > 0) {
                System.out.println("  " + AMARELO + "  • Treinando... (faltam " + ficha.getTreinoBonusPeriodosRestantes() + " períodos para treinar novamente)" + RESET);
            }

            if (ficha.isTemCabana()) {
                System.out.println("  " + num + ". Dormir  (só à noite, na cabana; recupera metade da vida e mana)");
                opDormir = num++;
            }

            System.out.println("  " + VERDE + "0. Voltar" + RESET);
            int escolha = Interface.lerInteiro();

            if (escolha == 0) return;

            if (escolha == opCabana) {
                if (!ficha.isTemCabana()) {
                    if (ficha.montarCabana()) {
                        Interface.MostrarMensagem("\nVocê constrói sua CABANA, gastando 7 madeiras, 10 folhas e 4 pedras!");
                        Interface.MostrarMensagem("Agora você tem um abrigo seguro e pode dormir à noite (estando nela).");
                        Interface.Pausa(2500);
                        avancarTempoComMensagens(ficha, 2);
                    } else {
                        Interface.ExibirErro("Faltam materiais! Você precisa de 7 Madeiras, 10 Folhas e 4 Pedras.");
                        Interface.Pausa(1500);
                    }
                } else if (!ficha.isNaCabana()) {
                    Interface.MostrarMensagem("\nVocê segue pelo caminho de volta para sua cabana...");
                    Interface.Pausa(1500);
                    ficha.voltarParaCabana();
                    avancarTempoComMensagens(ficha, 1);
                }
            } else if (escolha == opSala && opSala > 0) {
                if (!ficha.isTemSalaTreino() && ficha.isTemCabana()) {
                    if (ficha.construirSalaTreino()) {
                        Interface.MostrarMensagem("\nVocê constrói sua SALA DE TREINO ao lado da cabana!");
                        Interface.MostrarMensagem("Agora pode treinar para ganhar +2 em Força ou Destreza por 2 períodos.");
                        Interface.Pausa(2500);
                        avancarTempoComMensagens(ficha, 2);
                    } else {
                        Interface.ExibirErro("Faltam materiais! Você precisa de 10 Madeiras, 15 Folhas, 5 Pedras e 4 Couros.");
                        Interface.Pausa(1500);
                    }
                } else if (ficha.isTemSalaTreino() && !ficha.isNaSalaTreino()) {
                    if (ficha.getTreinoBonusPeriodosRestantes() <= 0) {
                        Interface.MostrarMensagem("\nVocê entra na Sala de Treino e começa a treinar...");
                        Interface.Pausa(1500);
                        System.out.println("\n  Em qual atributo deseja treinar?\n");
                        System.out.println("  1. Força");
                        System.out.println("  2. Destreza");
                        int atributo = Interface.lerOpcao(1, 2);
                        String nomeAtributo = atributo == 1 ? "Força" : "Destreza";
                        ficha.treinarAtributo(nomeAtributo);
                        Interface.MostrarMensagem("\nTreino iniciado! +2 em " + nomeAtributo + " por 2 períodos!");
                        Interface.Pausa(2000);
                        avancarTempoComMensagens(ficha, 3);
                    } else {
                        Interface.MostrarMensagem("\nVocê segue para a Sala de Treino...");
                        Interface.Pausa(1500);
                        ficha.entrarSalaTreino();
                        avancarTempoComMensagens(ficha, 1);
                    }
                }
            } else if (escolha == opDormir && opDormir > 0) {
                if (!ficha.isEhNoite()) {
                    Interface.ExibirErro("Você só pode dormir à noite!");
                    Interface.Pausa(1500);
                } else if (!ficha.isNaCabana()) {
                    Interface.ExibirErro("Você precisa estar na cabana para dormir!");
                    Interface.Pausa(1500);
                } else {
                    Interface.MostrarMensagem("\nVocê se deita na cabana e adormece profundamente...");
                    Interface.Pausa(2000);
                    if (ficha.dormir()) {
                        Interface.MostrarMensagem("Boa noite! Você recupera metade da vida e mana.");
                        Interface.MostrarMensagem("Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + " | Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
                        Interface.Pausa(2000);
                        avancarTempoComMensagens(ficha, 3);
                    }
                }
            }
        }
    }
}
