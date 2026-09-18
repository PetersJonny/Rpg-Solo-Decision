package eventos;

import classes.*;
import criaturas.Criatura;
import fichas.FichaRpg;
import itens.Arma;
import itens.Consumivel;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class Floresta {

    // Códigos de Cores ANSI (aliases das usadas na Interface, para um único ponto de origem)
    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;

    // Avança o tempo e mostra o que aconteceu com o período (dia/noite) e o cansaço
    private static void avancarTempoComMensagens(FichaRpg ficha, int unidades) {
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
            verificarCompanheiroPosDormir(ficha);
        }

        // Ao ter uma cabana, pode aparecer alguém perdido (20% por período, no dia ou na noite)
        if (ficha.isTemCabana() && !ficha.temCompanheiro() && MecanicasRpg.rolarDado(100) <= 20) {
            EventoPerdido(ficha);
        }
    }

    // Verifica se o companheiro decidiu partir após dormir
    private static void verificarCompanheiroPosDormir(FichaRpg ficha) {
        if (!ficha.companheiroQuerPartir()) return;
        String nomePartiu = ficha.getCompanheiro().getNomeCompleto();
        ficha.removerCompanheiro();
        Interface.MostrarMensagem("\nApós passar a noite e decidir seu futuro, " + nomePartiu + " percebe que é hora de seguir o próprio caminho.");
        Interface.MostrarMensagem("Vocês se despedem com gratidão e ela/e segue a própria jornada!");
        Interface.Pausa(2500);
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
        EventoAnimal(ficha);

        avancarTempoComMensagens(ficha, 1);
    }

    // ==================== BUSCAR RECURSOS ====================

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
            EventoAnimal(ficha);
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

    // ==================== CONSTRUÇÃO ====================

    public static void MenuConstrucao(FichaRpg ficha) {
        while (true) {
            Interface.cabecalhoMenu("C O N S T R U Ç Ã O");

            // Período e localização atuais
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

            // ===================== CABANA =====================
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

            // ===================== SALA DE TREINO =====================
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

            // Menu dinâmico com numeração sequencial
            int opCabana = 0, opSala = 0, opDormir = 0;
            int num = 1;

            System.out.println("\n  O que deseja fazer?");
            if (!ficha.isTemCabana()) {
                System.out.println("  " + num + ". Montar Cabana  (7x Madeira, 10x Folha, 4x Pedra — 2/3 do período)");
                opCabana = num++;
            } else if (!ficha.isNaCabana()) {
                System.out.println("  " + num + ". Voltar para a Cabana  (1/3 do período)");
                opCabana = num++;
            }

            if (!ficha.isTemSalaTreino()) {
                System.out.println("  " + num + ". Montar Sala de Treino  (10x Madeira, 15x Folha, 5x Pedra, 4x Couro — 2/3 do período)");
                opSala = num++;
            } else if (ficha.getTreinoBonusPeriodosRestantes() <= 0) {
                System.out.println("  " + num + ". Treinar na Sala de Treino  (período inteiro; +2 em Força ou Destreza por 2 períodos)");
                opSala = num++;
            } else {
                System.out.println("  " + AMARELO + "  • Treinando... (faltam " + ficha.getTreinoBonusPeriodosRestantes() + " períodos para treinar novamente)" + RESET);
            }

            if (ficha.isTemCabana()) {
                System.out.println("  " + num + ". Dormir  (só à noite, na cabana; recupera metade da vida e mana)");
                opDormir = num++;
            }

            System.out.println("  " + VERDE + "0. Voltar" + RESET);
            int escolha = Interface.lerOpcao(0, num - 1);

            if (escolha == 0) return;

            // =================== CABANA ===================
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
                    int chanceEncontro = ficha.isEhNoite() ? 50 : 30;
                    if (MecanicasRpg.rolarDado(100) <= chanceEncontro) {
                        Interface.MostrarMensagem("\nDurante o trajeto, algo se agita entre as árvores...");
                        Interface.Pausa(1500);
                        EventoAnimal(ficha);
                        if (ficha.getVidaPersonagem() <= 0) return;
                    }
                    ficha.voltarParaCabana();
                    Interface.MostrarMensagem("\nVocê chega em sua cabana, levando 1/3 do período.");
                    Interface.Pausa(2000);
                    avancarTempoComMensagens(ficha, 1);
                }
                continue;
            }

            // =================== SALA DE TREINO ===================
            if (escolha == opSala) {
                if (!ficha.isTemSalaTreino()) {
                    // Montar Sala de Treino
                    if (ficha.construirSalaTreino()) {
                        Interface.MostrarMensagem("\nVocê constrói sua SALA DE TREINO, gastando 10 madeiras, 15 folhas, 5 pedras e 4 couros!");
                        if (ficha.isSalaJuntoCabana()) {
                            Interface.MostrarMensagem("A sala ficou junto à sua cabana — estar nela não conta como ter saído.");
                        } else {
                            Interface.MostrarMensagem("A sala ficou em um local separado da cabana.");
                        }
                        Interface.Pausa(2500);
                        avancarTempoComMensagens(ficha, 2);
                    } else {
                        Interface.ExibirErro("Faltam materiais! Você precisa de 10 Madeiras, 15 Folhas, 5 Pedras e 4 Couros.");
                        Interface.Pausa(1500);
                    }
                } else {
                    // Treinar na Sala de Treino
                    if (ficha.getTreinoBonusPeriodosRestantes() > 0) {
                        Interface.ExibirErro("Você ainda está com o bônus de treino ativo! Aguarde os " + ficha.getTreinoBonusPeriodosRestantes() + " período(s) terminarem para treinar de novo.");
                        Interface.Pausa(1500);
                        continue;
                    }
                    Interface.MostrarMensagem("\nVocê entra na sua sala de treino e se prepara para treinar durante todo o período...");
                    Interface.Pausa(1500);
                    ficha.entrarSalaTreino();

                    int unidadesFaltando = 3 - ficha.getProgressoPeriodo();
                    avancarTempoComMensagens(ficha, unidadesFaltando);
                    Interface.MostrarMensagem("\nVocê treina intensamente durante o período inteiro...");
                    if (ficha.temCompanheiro()) {
                        Interface.MostrarMensagem("Enquanto isso, " + ficha.getCompanheiro().getNome() + " aproveita para treinar junto com você.");
                    }
                    Interface.Pausa(2000);

                    System.out.println("\nQue atributo você deseja treinar? (+2 em um atributo por 2 períodos)");
                    System.out.println("1. Força");
                    System.out.println("2. Destreza");
                    System.out.println("0. Não treinar");
                    int escolhaAtributo = Interface.lerOpcao(0, 2);
                    if (escolhaAtributo == 1) {
                        ficha.treinarAtributo("Força");
                        Interface.MostrarMensagem("\nVocê treinou sua força! +2 em Força por 2 períodos.");
                        Interface.MostrarMensagem("Bônus aplicado: Força, dano e testes de força contam o extra.");
                    } else if (escolhaAtributo == 2) {
                        ficha.treinarAtributo("Destreza");
                        Interface.MostrarMensagem("\nVocê treinou sua destreza! +2 em Destreza por 2 períodos.");
                        Interface.MostrarMensagem("Bônus aplicado: Destreza e testes de destreza contam o extra.");
                    } else {
                        Interface.MostrarMensagem("\nVocê decide não aplicar nenhum bônus de treino agora.");
                    }
                    Interface.Pausa(2000);
                    ficha.terminarTreino();
                }
                continue;
            }

            // =================== DORMIR ===================
            if (escolha == opDormir) {
                if (!ficha.isEhNoite()) {
                    Interface.ExibirErro("Você só consegue dormir quando está de noite.");
                    Interface.Pausa(1500);
                    continue;
                }
                if (!ficha.isTemCabana()) {
                    Interface.ExibirErro("Você ainda não tem uma cabana para dormir! Monte uma no menu de construção.");
                    Interface.Pausa(1500);
                    continue;
                }
                if (!ficha.isNaCabana()) {
                    Interface.ExibirErro("Você está longe da cabana! Volte para ela primeiro.");
                    Interface.Pausa(1500);
                    continue;
                }
                int vidaAntes = ficha.getVidaPersonagem();
                int manaAntes = ficha.getManaPersonagem();
                ficha.dormir();
                int curaVida = ficha.getVidaPersonagem() - vidaAntes;
                int curaMana = ficha.getManaPersonagem() - manaAntes;
                Interface.MostrarMensagem("\nVocê dorme profundamente em sua cabana...");
                if (ficha.temCompanheiro()) {
                    Interface.MostrarMensagem(ficha.getCompanheiro().getNome() + " também descansa na cabana ao seu lado.");
                }
                Interface.MostrarMensagem("Recuperou " + curaVida + " de vida e " + curaMana + " de mana! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + " | Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
                Interface.MostrarMensagem("O sol nasce! Você acorda descansado e sem cansaço.");
                Interface.Pausa(2500);
                verificarCompanheiroPosDormir(ficha);
                continue;
            }

            Interface.ExibirErro("Opção inválida!");
        }
    }

    // ==================== PESSOA PERDIDA (SISTEMA DE AJUDA) ====================

    // Uma pessoa perdida na floresta pode ser encontrada quando o jogador tem uma cabana
    private static void EventoPerdido(FichaRpg ficha) {
        companheiros.Companheiro perdido = new companheiros.Companheiro();

        Interface.MostrarMensagem("\nUm vulto surge entre as árvores, com olhar cansado e roupas surradas...");
        Interface.Pausa(2000);
        Interface.MostrarMensagem("\n" + perdido.getNomeCompleto() + " se aproxima, aliviado(a) por encontrar alguém.");
        Interface.Pausa(2000);
        Interface.MostrarMensagem("\"Por favor! Estou perdido(a) nesta floresta há dias. Ouvi dizer que você tem uma cabana... posso ficar um tempo?\"");
        Interface.Pausa(2000);

        System.out.println("\n  O que você faz?\n");
        System.out.println("  1. Acolhê-lo(a) por um tempo");
        System.out.println("  2. Recusar e seguir seu caminho");
        int escolha = Interface.lerOpcao(2);

        if (escolha == 1) {
            if (ficha.temCompanheiro()) {
                Interface.MostrarMensagem("\nVocê já tem alguém sob sua proteção. " + perdido.getNome() + " compreende e segue adiante.");
                Interface.Pausa(2000);
                return;
            }
            ficha.setCompanheiro(perdido);
            Interface.MostrarMensagem("\nA partir de agora, " + perdido.getNomeCompleto() + " te acompanha em tudo: lutar, dormir, treinar e explorar!");
            Interface.MostrarMensagem("Fale com " + perdido.getNome() + " pelo menu principal para conhecer melhor essa pessoa.");
            Interface.Pausa(2500);
        } else {
            Interface.MostrarMensagem("\n\"Sinto muito, mas não posso ajudar agora.\" " + perdido.getNome() + ", desapontado(a), se afasta para dentro da floresta.");
            Interface.Pausa(2000);
        }
    }

    // Conversa com a pessoa que acompanha o jogador (sem mostrar a ficha completa)
    public static void ConversarComCompanheiro(FichaRpg ficha) {
        companheiros.Companheiro comp = ficha.getCompanheiro();
        if (comp == null) return;

        while (true) {
            Interface.cabecalhoMenu("CONVERSAR COM " + comp.getNome().toUpperCase());
            comp.mostrarResumo();

            System.out.println("\n  O que deseja fazer?\n");
            System.out.println("  1. Ouvir o que ela(e) tem a dizer");
            System.out.println("  2. Ver os itens que ela(e) carrega");

            boolean podeCurar = ficha.temItem("Kit Médico")
                    && comp.getFicha().getVidaPersonagem() < comp.getFicha().getVidaMaxima();

            int num = 3;
            int opCurar = -1, opDespedir = -1;
            if (podeCurar) {
                opCurar = num++;
                System.out.println("  " + opCurar + ". Curar " + comp.getNome() + " com um Kit Médico");
            }
            opDespedir = num++;
            System.out.println("  " + opDespedir + ". Despedir-se de " + comp.getNome());
            System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

            int escolha = Interface.lerOpcao(0, num - 1);
            if (escolha == 0) return;
            if (escolha == 1) {
                comp.falarSobreClasse();
            } else if (escolha == 2) {
                comp.mostrarItens();
                Interface.Pausa(1500);
            } else if (escolha == opCurar) {
                curarCompanheiroComKit(ficha);
            } else if (escolha == opDespedir) {
                String nomePartiu = comp.getNome();
                if (confirmarDespedida(comp)) {
                    ficha.removerCompanheiro();
                    Interface.MostrarMensagem("\nVocês se despedem com gratidão. " + nomePartiu + " segue agora o próprio caminho.");
                    Interface.Pausa(2000);
                    return;
                }
            }
        }
    }

    // Pedido de confirmação antes de dispensar o companheiro (sai do grupo)
    private static boolean confirmarDespedida(companheiros.Companheiro comp) {
        System.out.println("\n  Deseja mesmo se despedir de " + comp.getNome() + "? Ela(e) deixará de te acompanhar.\n");
        System.out.println("  1. Sim, despedir-me");
        System.out.println("  2. Não, quero que fique");
        return Interface.lerOpcao(2) == 1;
    }

    // Usa um Kit Médico do inventário do jogador para curar o companheiro
    private static void curarCompanheiroComKit(FichaRpg ficha) {
        companheiros.Companheiro comp = ficha.getCompanheiro();
        if (comp == null || !ficha.temItem("Kit Médico")) return;

        FichaRpg cf = comp.getFicha();
        int cura = MecanicasRpg.rolarDado(4);
        int antes = cf.getVidaPersonagem();
        cf.setVidaPersonagem(Math.min(antes + cura, cf.getVidaMaxima()));
        int curaReal = cf.getVidaPersonagem() - antes;
        Interface.MostrarMensagem("\nVocê usa um Kit Médico em " + comp.getNome() + " e ela(e) recupera " + curaReal + " de vida! Vida: " + cf.getVidaPersonagem() + "/" + cf.getVidaMaxima());

        // Consome o kit do inventário do jogador
        for (int i = 0; i < ficha.getInventario().size(); i++) {
            ItemRpg item = ficha.getInventario().get(i);
            if (item.getNome().equals("Kit Médico")) {
                item.setQuantidade(item.getQuantidade() - 1);
                if (item.getQuantidade() <= 0) {
                    ficha.getInventario().remove(i);
                    Interface.MostrarMensagem("Seu Kit Médico acabou.");
                } else {
                    Interface.MostrarMensagem("Restam " + item.getQuantidade() + "x Kit Médico.");
                }
                break;
            }
        }
        Interface.Pausa(2000);
    }

    // ==================== SORTEIO DE ENCONTRO ====================

    private static void EventoAnimal(FichaRpg ficha) {
        Interface.MostrarMensagem("\nAlgo se move por entre as árvores...");
        Interface.Pausa(2500);

        // 10% de chance de encontrar um vendedor ambulante
        if (MecanicasRpg.rolarDado(100) <= 10) {
            loja.Vendedor.EncontrarVendedor(ficha);
            return;
        }

        // 20% de chance de encontrar uma Fada (apenas uma vez por personagem)
        if (!ficha.isFadaEncontrada() && MecanicasRpg.rolarDado(100) <= 20) {
            ficha.setFadaEncontrada(true);
            EncontrarFada(ficha);
            return;
        }

        // Sorteia o tipo de criatura (1 = Lobo, 2 = Urso, 3 = Bandido)
        int tipo = MecanicasRpg.rolarDado(3);
        List<Criatura> inimigos = criarGrupoMonstros(tipo, ficha.isEhNoite());
        Criatura referencia = inimigos.get(0);

        int dadoPresenca = 0;
        int totalPresenca = 0;
        Interface.pressionarParaTeste("Presença");
        dadoPresenca = MecanicasRpg.rolarDado(20);
        totalPresenca = dadoPresenca + ficha.getPresencaTeste();
        Interface.MostrarMensagem("-> Teste de Presença: " + dadoPresenca + " (Dado) + " + ficha.getPresencaTeste() + " (Atributo) = " + totalPresenca + " (Dificuldade: " + referencia.getTestePresenca() + ")");
        Interface.Pausa(2500);

        if (totalPresenca >= referencia.getTestePresenca()) {
            Interface.MostrarMensagem("\n" + mecanicas.MotorDeCombate.nomesDosInimigos(inimigos) + " apareceu entre as sombras das árvores e você o avistou antes!");
            Interface.Pausa(2500);

            System.out.println("  O que deseja fazer?");
            System.out.println("  1. Lutar (Você terá +2 de Iniciativa extra por surpreendê-lo)");
            System.out.println("  2. Tentar Fugir furtivamente");
            int escolha = Interface.lerOpcao(2);

            if (escolha == 1) {
                Interface.MostrarMensagem("\nVocê saca sua arma e parte para cima!");
                Interface.Pausa(2500);
                mecanicas.MotorDeCombate.IniciarCombate(ficha, inimigos, true);
            } else {
                Interface.pressionarParaTeste("Destreza (Fuga)");
                int dadoDestreza = MecanicasRpg.rolarDado(20);
                int totalDestreza = dadoDestreza + ficha.getDestrezaTeste();
                Interface.MostrarMensagem("-> Teste de Destreza (Fuga): " + dadoDestreza + " (Dado) + " + ficha.getDestrezaTeste() + " (Atributo) = " + totalDestreza);
                Interface.Pausa(2500);
                if (totalDestreza >= 12) {
                    Interface.MostrarMensagem("\nVocê recua lentamente pelas sombras e foge com sucesso, sem ser notado.");
                    Interface.Pausa(2500);
                } else {
                    Interface.MostrarMensagem("\nVocê pisa em um galho seco! A ameaça percebe você e avança!");
                    Interface.Pausa(2500);
                    mecanicas.MotorDeCombate.IniciarCombate(ficha, inimigos, false);
                }
            }
        } else {
            Interface.MostrarMensagem("\n" + mecanicas.MotorDeCombate.nomesDosInimigos(inimigos) + " saltou das sombras e te surpreendeu!");
            Interface.Pausa(2500);
            mecanicas.MotorDeCombate.IniciarCombate(ficha, inimigos, false);
        }
    }

    // Cria o grupo de monstros conforme o tipo (dia: grupos menores, noite: grupos maiores)
    private static List<Criatura> criarGrupoMonstros(int tipo, boolean deNoite) {
        return criaturas.CriaturaFactory.criarGrupoMonstros(tipo, deNoite);
    }

    // ==================== ENCONTRO COM A FADA ====================

    private static void EncontrarFada(FichaRpg ficha) {
        Interface.pressionarParaTeste("Presença");
        int dadoPresenca = MecanicasRpg.rolarDado(20);
        int totalPresenca = dadoPresenca + ficha.getPresencaTeste();
        Interface.MostrarMensagem("-> Teste de Presença: " + dadoPresenca + " (Dado) + " + ficha.getPresencaTeste() + " (Atributo) = " + totalPresenca + " (Dificuldade: 18)");
        Interface.Pausa(2500);

        boolean avistou = totalPresenca >= 18;

        if (avistou) {
            Interface.MostrarMensagem("\nUm leve brilho chama sua atenção: uma pequena Fada flutua entre as árvores!");
        } else {
            Interface.MostrarMensagem("\nUma Fada surge diante de você, espalhando um brilho suave!");
        }
        Interface.Pausa(2500);

        System.out.println("\n  O que deseja fazer?");
        System.out.println("  1. Tentar conversar com a Fada");
        System.out.println("  2. Lutar contra a Fada");
        System.out.println("  3. Deixá-la em paz e seguir caminho");
        int escolha = Interface.lerOpcao(3);


        if (escolha == 1) {
            Interface.pressionarParaTeste("Sabedoria");
            int dadoSabedoria = MecanicasRpg.rolarDado(20);
            int totalSabedoria = dadoSabedoria + ficha.getSabedoriaTeste();
            Interface.MostrarMensagem("-> Teste de Sabedoria (Conversa): " + dadoSabedoria + " (Dado) + " + ficha.getSabedoriaTeste() + " (Atributo) = " + totalSabedoria + " (Dificuldade: 14)");
            Interface.Pausa(2500);

            if (totalSabedoria >= 14) {
                String atributoAumentado = ficha.aumentarAtributoAleatorio();
                Interface.MostrarMensagem("\nConvencida pela sua gentileza, a Fada concede a você +1 de " + atributoAumentado + "!");
                Interface.Pausa(2500);
            } else {
                Interface.MostrarMensagem("\nA Fada não confia em você e se afasta, sumindo entre as árvores.");
                Interface.Pausa(2500);
            }
        } else if (escolha == 2) {
            Interface.MostrarMensagem("\nVocê avança contra a Fada, que reage irritada!");
            Interface.Pausa(2500);
            List<Criatura> fadaBatalha = new ArrayList<>();
            fadaBatalha.add(criaturas.CriaturaFactory.criarFada());
            mecanicas.MotorDeCombate.IniciarCombate(ficha, fadaBatalha, avistou);
        } else {
            Interface.MostrarMensagem("\nA Fada é deixada em paz e você segue seu caminho tranquilamente.");
            Interface.Pausa(2500);
        }
    }

    // ==================== INICIAR COMBATE ====================

}
