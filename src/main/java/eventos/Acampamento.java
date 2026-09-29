package eventos;

import itens.Consumivel;
import telas.Interface;
import java.util.ArrayList;
import itens.ItemRpg;
import criaturas.Criatura;
import java.util.List;
import fichas.FichaRpg;
import mecanicas.MecanicasRpg;

public class Acampamento {
    private static final String RESET = telas.Interface.RESET;
    private static final String CIANO = telas.Interface.CIANO;
    private static final String VERDE = telas.Interface.VERDE;
    private static final String AMARELO = telas.Interface.AMARELO;

public static void MenuConstrucao(FichaRpg ficha) {
                                while (true) {
            Interface.cabecalhoMenu("C O N S T R U Ç Ã O");

                        String periodo = ficha.getPeriodoDescritivoMaiusculo();
            System.out.println("\n  Período: " + AMARELO + periodo + RESET + "  (" + (3 - ficha.getProgressoPeriodo()) + "/3 para virar)");

            String local;
            if (ficha.podeUsarCabana()) {
                local = "NA CABANA";
            } else if (ficha.podeUsarSalaTreino()) {
                local = "NA SALA DE TREINO";
            } else if (ficha.podeUsarMesaMagias()) {
                local = "NA MESA DE MAGIAS";
            } else if (ficha.podeUsarFogueira()) {
                local = "NA FOGUEIRA";
            } else {
                local = "NO MEIO DA MATA";
            }
            System.out.println("  Localização: " + CIANO + local + RESET);
            System.out.println("  -----------------------------------------------");

                        String statusCabana = ficha.isTemCabana() ? VERDE + "construída" + RESET : AMARELO + "não construída" + RESET;
            System.out.println("\n  " + CIANO + "[ CABANA ]" + RESET + "  Status: " + statusCabana);
            System.out.println("  Custo:    " + ficha.getQuantidadeDe("Madeira") + "/7x Madeira | "
                    + ficha.getQuantidadeDe("Folha") + "/10x Folha | "
                    + ficha.getQuantidadeDe("Pedra") + "/4x Pedra");
            if (ficha.isTemCabana()) {
                System.out.println("  Localização: " + descreverPonto(ficha, ficha.getProfundidadeCabana()));
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
            } else {
                System.out.println("  Localização: " + descreverPonto(ficha, ficha.getProfundidadeSalaTreino())
                        + " — só usa estando nela (ou vá até o ponto dela)");
            }

                        String statusMesa = ficha.isTemMesaMagias() ? VERDE + "construída" + RESET : AMARELO + "não construída" + RESET;
            System.out.println("\n  " + CIANO + "[ MESA DE MAGIAS ]" + RESET + "  Status: " + statusMesa);
            System.out.println("  Custo:    " + ficha.getQuantidadeDe("Madeira") + "/5x Madeira | "
                    + ficha.getQuantidadeDe("Folha") + "/4x Folha | "
                    + ficha.getQuantidadeDe("Pedra") + "/4x Pedra | "
                    + ficha.getQuantidadeDe("Pó da Fada") + "/1x Pó da Fada");
            if (!ficha.isTemMesaMagias()) {
                System.out.println("  Informação: Gasta 2/3 do período para montar.");
            } else {
                System.out.println("  Localização: " + descreverPonto(ficha, ficha.getProfundidadeMesaMagias())
                        + " — só usa estando nela (ou vá até o ponto dela)");
            }

                        String statusFogueira = ficha.isTemFogueira() ? VERDE + "construída" + RESET : AMARELO + "não construída" + RESET;
            System.out.println("\n  " + CIANO + "[ FOGUEIRA ]" + RESET + "  Status: " + statusFogueira);
            System.out.println("  Custo:    " + ficha.getQuantidadeDe("Madeira") + "/4x Madeira | "
                    + ficha.getQuantidadeDe("Folha") + "/3x Folha");
            if (!ficha.isTemFogueira()) {
                System.out.println("  Informação: Gasta 2/3 do período para montar.");
            } else {
                System.out.println("  Localização: " + descreverPonto(ficha, ficha.getProfundidadeFogueira())
                        + " — cozinha TODAS as carnes cruas de uma vez (2x Madeira), deixando-as seguras");
            }

            if (ficha.getTreinoBonusPeriodosRestantes() > 0) {
                System.out.println("\n  " + VERDE + "+2 em " + ficha.getTreinoBonusAtributo() + " ativo" + RESET + " (restam " + ficha.getTreinoBonusPeriodosRestantes() + " períodos)");
            }

            if (ficha.getMagiaBonusPeriodosRestantes() > 0) {
                System.out.println("\n  " + VERDE + "+1 dado de dano em habilidades ativo" + RESET + " (restam " + ficha.getMagiaBonusPeriodosRestantes() + " períodos)");
            }

            System.out.println("\n  -----------------------------------------------");

                        int opMontarCabana = 0, opIrCabana = 0, opDormir = 0;
            int opMontarSala = 0, opIrSala = 0, opTreinar = 0;
            int opMontarMesa = 0, opIrMesa = 0, opEstudar = 0;
            int opMontarFogueira = 0, opIrFogueira = 0, opCozinhar = 0;
            int num = 1;

            System.out.println("\n  O que deseja fazer?");

                        if (!ficha.isTemCabana()) {
                System.out.println("  " + num + ". Montar Cabana aqui  (7x Madeira, 10x Folha, 4x Pedra — 2/3 do período)");
                opMontarCabana = num++;
            } else if (ficha.podeUsarCabana()) {
                System.out.println("  " + num + ". Dormir  (só à noite; recupera 1/3 da vida e da mana — 1/2 se comeu hoje)");
                opDormir = num++;
            } else {
                System.out.println("  " + num + ". Ir para a Cabana  (" + ficha.getDistanciaAte(ficha.getProfundidadeCabana()) + " período(s) de caminhada)");
                opIrCabana = num++;
                System.out.println("  " + num + ". Montar Cabana aqui  (novo ponto; 7x Madeira, 10x Folha, 4x Pedra — 2/3 do período)");
                opMontarCabana = num++;
            }

                        if (!ficha.isTemSalaTreino()) {
                System.out.println("  " + num + ". Montar Sala de Treino aqui  (10x Madeira, 15x Folha, 5x Pedra, 4x Couro — 2/3 do período)");
                opMontarSala = num++;
            } else if (ficha.getTreinoBonusPeriodosRestantes() > 0) {
                System.out.println("  " + AMARELO + "  • Treinando... (faltam " + ficha.getTreinoBonusPeriodosRestantes() + " períodos para treinar novamente)" + RESET);
            } else if (ficha.podeUsarSalaTreino()) {
                System.out.println("  " + num + ". Treinar na Sala de Treino  (período inteiro; +2 em Força ou Destreza por 2 períodos)");
                opTreinar = num++;
            } else {
                System.out.println("  " + num + ". Ir para a Sala de Treino  (" + ficha.getDistanciaAte(ficha.getProfundidadeSalaTreino()) + " período(s) de caminhada)");
                opIrSala = num++;
                System.out.println("  " + num + ". Montar Sala de Treino aqui  (novo ponto; 10x Madeira, 15x Folha, 5x Pedra, 4x Couro — 2/3 do período)");
                opMontarSala = num++;
            }

                        if (!ficha.isTemMesaMagias()) {
                System.out.println("  " + num + ". Montar Mesa de Magias aqui  (5x Madeira, 4x Folha, 4x Pedra, 1x Pó da Fada — 2/3 do período)");
                opMontarMesa = num++;
            } else if (ficha.getMagiaBonusPeriodosRestantes() > 0) {
                System.out.println("  " + AMARELO + "  • Estudando... (faltam " + ficha.getMagiaBonusPeriodosRestantes() + " períodos para estudar novamente)" + RESET);
            } else if (ficha.podeUsarMesaMagias()) {
                System.out.println("  " + num + ". Estudar na Mesa de Magias  (período inteiro; +1 dado de dano em habilidades por 2 períodos)");
                opEstudar = num++;
            } else {
                System.out.println("  " + num + ". Ir para a Mesa de Magias  (" + ficha.getDistanciaAte(ficha.getProfundidadeMesaMagias()) + " período(s) de caminhada)");
                opIrMesa = num++;
                System.out.println("  " + num + ". Montar Mesa de Magias aqui  (novo ponto; 5x Madeira, 4x Folha, 4x Pedra, 1x Pó da Fada — 2/3 do período)");
                opMontarMesa = num++;
            }

                        if (!ficha.isTemFogueira()) {
                System.out.println("  " + num + ". Montar Fogueira aqui  (4x Madeira, 3x Folha — 2/3 do período)");
                opMontarFogueira = num++;
            } else if (ficha.podeUsarFogueira()) {
                System.out.println("  " + num + ". Cozinhar TODAS as carnes na Fogueira  (2x Madeira; deixa as carnes seguras)");
                opCozinhar = num++;
            } else {
                System.out.println("  " + num + ". Ir para a Fogueira  (" + ficha.getDistanciaAte(ficha.getProfundidadeFogueira()) + " período(s) de caminhada)");
                opIrFogueira = num++;
                System.out.println("  " + num + ". Montar Fogueira aqui  (novo ponto; 4x Madeira, 3x Folha — 2/3 do período)");
                opMontarFogueira = num++;
            }

            System.out.println("  " + VERDE + "0. Voltar para a floresta" + RESET);
            int escolha = Interface.lerOpcao(0, num - 1);

            if (escolha == 0) return;

                        if (escolha == opMontarCabana) {
                if (ficha.isTemCabana()) {
                    if (ficha.moverCabana()) {
                        Interface.MostrarMensagem("\nVocê constrói uma NOVA cabana bem aqui, gastando 7 madeiras, 10 folhas e 4 pedras!");
                        Interface.MostrarMensagem("Ela agora é o ponto da construção — a antiga fica para trás.");
                        Interface.Pausa(2500);
                        Floresta.avancarTempoComMensagens(ficha, 2);
                        continue;
                    }
                    Interface.ExibirErro("Faltam materiais! Você precisa de 7 Madeiras, 10 Folhas e 4 Pedras.");
                    Interface.Pausa(1500);
                    continue;
                }
                if (ficha.montarCabana()) {
                    Interface.MostrarMensagem("\nVocê constrói sua CABANA, gastando 7 madeiras, 10 folhas e 4 pedras!");
                    Interface.MostrarMensagem("Agora você tem um abrigo seguro e pode dormir à noite (estando nela).");
                    Interface.Pausa(2500);
                    Floresta.avancarTempoComMensagens(ficha, 2);
                    continue;
                }
                Interface.ExibirErro("Faltam materiais! Você precisa de 7 Madeiras, 10 Folhas e 4 Pedras.");
                Interface.Pausa(1500);
                continue;
            }

                        if (escolha == opIrCabana) {
                if (!ficha.isTemCabana()) {
                    Interface.ExibirErro("Você ainda não tem uma cabana!");
                    Interface.Pausa(1500);
                    continue;
                }
                TravessiaDaFloresta.CaminharAteConstrucao(ficha, ficha.getProfundidadeCabana(), "sua CABANA");
                if (ficha.getVidaPersonagem() <= 0) return;
                continue;
            }

                        if (escolha == opMontarSala) {
                if (ficha.isTemSalaTreino()) {
                    if (ficha.moverSalaTreino()) {
                        Interface.MostrarMensagem("\nVocê constrói uma NOVA sala de treino bem aqui, gastando 10 madeiras, 15 folhas, 5 pedras e 4 couros!");
                        Interface.MostrarMensagem("Ela agora é o ponto da construção — a antiga fica para trás.");
                        Interface.Pausa(2500);
                        Floresta.avancarTempoComMensagens(ficha, 2);
                        continue;
                    }
                    Interface.ExibirErro("Faltam materiais! Você precisa de 10 Madeiras, 15 Folhas, 5 Pedras e 4 Couros.");
                    Interface.Pausa(1500);
                    continue;
                }
                if (ficha.construirSalaTreino()) {
                    Interface.MostrarMensagem("\nVocê constrói sua SALA DE TREINO, gastando 10 madeiras, 15 folhas, 5 pedras e 4 couros!");
                    Interface.MostrarMensagem(mensagemLocalSala(ficha));
                    Interface.Pausa(2500);
                    Floresta.avancarTempoComMensagens(ficha, 2);
                    continue;
                }
                Interface.ExibirErro("Faltam materiais! Você precisa de 10 Madeiras, 15 Folhas, 5 Pedras e 4 Couros.");
                Interface.Pausa(1500);
                continue;
            }

                        if (escolha == opIrSala) {
                if (!ficha.isTemSalaTreino()) {
                    Interface.ExibirErro("Você ainda não tem uma sala de treino!");
                    Interface.Pausa(1500);
                    continue;
                }
                TravessiaDaFloresta.CaminharAteConstrucao(ficha, ficha.getProfundidadeSalaTreino(), "sua SALA DE TREINO");
                if (ficha.getVidaPersonagem() <= 0) return;
                continue;
            }

                        if (escolha == opTreinar) {
                if (ficha.getTreinoBonusPeriodosRestantes() > 0) {
                    Interface.ExibirErro("Você ainda está com o bônus de treino ativo! Aguarde os " + ficha.getTreinoBonusPeriodosRestantes() + " período(s) terminarem para treinar de novo.");
                    Interface.Pausa(1500);
                    continue;
                }
                Interface.MostrarMensagem("\nVocê entra na sua sala de treino e se prepara para treinar durante todo o período...");
                Interface.Pausa(1500);
                ficha.entrarSalaTreino();

                int unidadesFaltando = 3 - ficha.getProgressoPeriodo();
                Floresta.avancarTempoComMensagens(ficha, unidadesFaltando);
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
                continue;
            }

                        if (escolha == opMontarMesa) {
                if (ficha.isTemMesaMagias()) {
                    if (ficha.moverMesaMagias()) {
                        Interface.MostrarMensagem("\nVocê constrói uma NOVA mesa de magias bem aqui, gastando 5 madeiras, 4 folhas, 4 pedras e 1 Pó da Fada!");
                        Interface.MostrarMensagem("Ela agora é o ponto da construção — a antiga fica para trás.");
                        Interface.Pausa(2500);
                        Floresta.avancarTempoComMensagens(ficha, 2);
                        continue;
                    }
                    Interface.ExibirErro("Faltam materiais! Você precisa de 5 Madeiras, 4 Folhas, 4 Pedras e 1 Pó da Fada.");
                    Interface.Pausa(1500);
                    continue;
                }
                if (ficha.construirMesaMagias()) {
                    Interface.MostrarMensagem("\nVocê constrói sua MESA DE MAGIAS, gastando 5 madeiras, 4 folhas, 4 pedras e 1 Pó da Fada!");
                    Interface.MostrarMensagem(mensagemLocalMesa(ficha));
                    Interface.Pausa(2500);
                    Floresta.avancarTempoComMensagens(ficha, 2);
                    continue;
                }
                Interface.ExibirErro("Faltam materiais! Você precisa de 5 Madeiras, 4 Folhas, 4 Pedras e 1 Pó da Fada.");
                Interface.Pausa(1500);
                continue;
            }

                        if (escolha == opIrMesa) {
                if (!ficha.isTemMesaMagias()) {
                    Interface.ExibirErro("Você ainda não tem uma mesa de magias!");
                    Interface.Pausa(1500);
                    continue;
                }
                TravessiaDaFloresta.CaminharAteConstrucao(ficha, ficha.getProfundidadeMesaMagias(), "sua MESA DE MAGIAS");
                if (ficha.getVidaPersonagem() <= 0) return;
                continue;
            }

                        if (escolha == opMontarFogueira) {
                if (ficha.isTemFogueira()) {
                    if (ficha.moverFogueira()) {
                        Interface.MostrarMensagem("\nVocê constrói uma NOVA fogueira bem aqui, gastando 4 madeiras e 3 folhas!");
                        Interface.MostrarMensagem("Ela agora é o ponto da construção — a antiga fica para trás.");
                        Interface.Pausa(2500);
                        Floresta.avancarTempoComMensagens(ficha, 2);
                        continue;
                    }
                    Interface.ExibirErro("Faltam materiais! Você precisa de 4 Madeiras e 3 Folhas.");
                    Interface.Pausa(1500);
                    continue;
                }
                if (ficha.montarFogueira()) {
                    Interface.MostrarMensagem("\nVocê acende uma FOGUEIRA, gastando 4 madeiras e 3 folhas!");
                    Interface.MostrarMensagem("Agora você pode cozinhar suas carnes cruas aqui (2x Madeira) — as cozidas não estragam.");
                    Interface.Pausa(2500);
                    Floresta.avancarTempoComMensagens(ficha, 2);
                    continue;
                }
                Interface.ExibirErro("Faltam materiais! Você precisa de 4 Madeiras e 3 Folhas.");
                Interface.Pausa(1500);
                continue;
            }

                        if (escolha == opIrFogueira) {
                if (!ficha.isTemFogueira()) {
                    Interface.ExibirErro("Você ainda não tem uma fogueira!");
                    Interface.Pausa(1500);
                    continue;
                }
                TravessiaDaFloresta.CaminharAteConstrucao(ficha, ficha.getProfundidadeFogueira(), "sua FOGUEIRA");
                if (ficha.getVidaPersonagem() <= 0) return;
                continue;
            }

                        if (escolha == opCozinhar) {
                if (!ficha.isTemFogueira()) {
                    Interface.ExibirErro("Você ainda não tem uma fogueira!");
                    Interface.Pausa(1500);
                    continue;
                }
                if (!ficha.podeUsarFogueira()) {
                    Interface.ExibirErro("Você precisa estar junto da fogueira para cozinhar!");
                    Interface.Pausa(1500);
                    continue;
                }
                cozinharNaFogueira(ficha);
                continue;
            }

                        if (escolha == opEstudar) {
                if (ficha.getMagiaBonusPeriodosRestantes() > 0) {
                    Interface.ExibirErro("Você ainda está sob o efeito da Mesa de Magias! Aguarde os " + ficha.getMagiaBonusPeriodosRestantes() + " período(s) terminarem para estudar de novo.");
                    Interface.Pausa(1500);
                    continue;
                }
                Interface.MostrarMensagem("\nVocê se senta na mesa de magias e dedica todo o período ao estudo...");
                Interface.Pausa(1500);
                int unidadesFaltando = 3 - ficha.getProgressoPeriodo();
                Floresta.avancarTempoComMensagens(ficha, unidadesFaltando);
                Interface.MostrarMensagem("\nVocê estuda os princípios de afiar magias durante o período inteiro.");
                Interface.Pausa(2000);
                ficha.estudarMagia();
                Interface.MostrarMensagem("\nVocê sente suas habilidades mais afiadas! +1 dado de dano em TODAS as suas habilidades por 2 períodos.");
                Interface.MostrarMensagem("O efeito vale a partir do próximo período, enquanto durar.");
                Interface.Pausa(2000);
                continue;
            }

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
                int perdaFomeDormir = ficha.getPerdaVidaPorFome();
                int vidaAntes = ficha.getVidaPersonagem();
                int manaAntes = ficha.getManaPersonagem();
                ficha.dormir();
                int curaVida = ficha.getVidaPersonagem() - vidaAntes;
                int curaMana = ficha.getManaPersonagem() - manaAntes;
                Interface.MostrarMensagem("\nVocê dorme profundamente em sua cabana...");
                if (ficha.temItem("Capa do Viajante")) {
                    Interface.MostrarMensagem("(Sua Capa do Viajante te mantém aquecido durante a noite: +4 de vida no descanso.)");
                }
                if (perdaFomeDormir > 0) {
                    Interface.MostrarMensagem("\n(A fome cobra seu preço: você perde " + perdaFomeDormir + " de vida! " + ficha.getDiasSemComer() + " dias sem comer)");
                }
                if (ficha.temCompanheiro()) {
                    Interface.MostrarMensagem(ficha.getCompanheiro().getNome() + " também descansa na cabana ao seu lado.");
                }
                Interface.MostrarMensagem("Recuperou " + curaVida + " de vida e " + curaMana + " de mana! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + " | Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
                Interface.MostrarMensagem("O sol nasce! Você acorda descansado e sem cansaço.");
                Interface.Pausa(2500);
                Floresta.verificarCompanheiroPosDormir(ficha);
                continue;
            }

            Interface.ExibirErro("Opção inválida!");
        }
    }

    public static String descreverPonto(FichaRpg ficha, int profundidadeConstrucao) {
        int distancia = ficha.getDistanciaAte(profundidadeConstrucao);
        if (distancia == 0) {
            return VERDE + "você está aqui" + RESET;
        }
        return "a " + distancia + " período(s) de caminhada daqui";
    }

    public static void cozinharNaFogueira(FichaRpg ficha) {
        int madeiras = ficha.getQuantidadeDe("Madeira");
        int lobos = ficha.getQuantidadeDe("Carne de Lobo");
        int ursos = ficha.getQuantidadeDe("Carne de Urso");
        if (madeiras < 2) {
            Interface.ExibirErro("Faltam 2 Madeiras para esquentar a fogueira!");
            Interface.Pausa(1500);
            return;
        }
        if (lobos == 0 && ursos == 0) {
            Interface.ExibirErro("Você não tem nenhuma carne crua para cozinhar.");
            Interface.Pausa(1500);
            return;
        }
        Interface.cabecalhoMenu("COZINHAR TODAS AS CARNES");
        System.out.println("\n  Madeira: " + AMARELO + madeiras + RESET + " (gasta 2 ao cozinhar)");
        if (lobos > 0) System.out.println("  Carne de Lobo ×" + lobos + " → Carne de Lobo Cozida");
        if (ursos > 0) System.out.println("  Carne de Urso ×" + ursos + " → Carne de Urso Cozida");
        System.out.println("\n  Cozinhar todas agora?  " + VERDE + "1. Sim" + RESET + "  " + AMARELO + "0. Voltar" + RESET);
        if (Interface.lerOpcao(0, 1) != 1) return;

        if (ficha.cozinharTodasAsCarnes()) {
            Interface.MostrarMensagem("\nVocê atiça a fogueira e assa TODAS as carnes: " + (lobos > 0 ? lobos + "x Carne de Lobo Cozida " : "") + (ursos > 0 ? ursos + "x Carne de Urso Cozida" : "") + "!");
            Interface.MostrarMensagem("Agora todas estão seguras para comer (não estragam).");
            Interface.Pausa(2200);
        } else {
            Interface.ExibirErro("Não foi possível cozinhar agora. Verifique se tem carne crua e 2 Madeiras.");
            Interface.Pausa(1500);
        }
    }

    public static String mensagemLocalSala(FichaRpg ficha) {
        if (ficha.isSalaJuntoCabana()) {
            return "A sala ficou no mesmo ponto da sua cabana — você usa as duas sem novo deslocamento.";
        }
        if (ficha.isSalaJuntoMesa()) {
            return "A sala ficou no mesmo ponto da sua mesa de magias — você usa as duas sem novo deslocamento.";
        }
        return "A sala ficou em um ponto separado da mata — para usá-la você precisa caminhar até lá.";
    }

    public static String mensagemLocalMesa(FichaRpg ficha) {
        if (ficha.isMesaJuntoCabana()) {
            return "A mesa ficou no mesmo ponto da sua cabana — você usa as duas sem novo deslocamento.";
        }
        if (ficha.isMesaJuntoSala()) {
            return "A mesa ficou no mesmo ponto da sua sala de treino — você usa as duas sem novo deslocamento.";
        }
        return "A mesa ficou em um ponto separado da mata — para usá-la você precisa caminhar até lá.";
    }
}
