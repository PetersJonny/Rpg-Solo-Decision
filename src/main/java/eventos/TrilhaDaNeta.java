package eventos;

import java.util.List;

import criaturas.Criatura;
import criaturas.CriaturaFactory;
import fichas.FichaRpg;
import fichas.GerenciadorDeMissoesECompanheiro;
import mecanicas.MecanicasRpg;
import mecanicas.MotorDeCombate;
import telas.Interface;

public class TrilhaDaNeta {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;
    private static final String VERMELHO = Interface.VERMELHO;

    private static final String MISSAO = "A Neta Perdida";
    private static final int TURNOS_TRILHA = 3;
    private static final int TURNOS_CABANA = 2;

    public static void MenuNeta(FichaRpg ficha) {
        while (true) {
            Interface.cabecalhoMenu("A NETA PERDIDA");
            Interface.MostrarMensagem("\nA mata da entrada da vila. Foi aqui que a velhinha perdeu a netinha de vista, e o chão ainda guarda os rastros do sumiço.");
            Interface.Pausa(2000);

            if (ficha.isCabanaAlcancada() && !ficha.isCabanaVisitada()) {
                System.out.println("  1. Seguir até a cabana onde as pegadas terminam");
                System.out.println("  2. Voltar");
                System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
                if (Interface.lerOpcao(2) == 2) return;
                CaminharAteACabana(ficha, Math.max(1, ficha.getTurnosParaVoltar()));
            } else if (ficha.isAcampamentoAlcancado() && !ficha.isBandoVencido()) {
                System.out.println("  1. Atacar o acampamento dos bandidos");
                System.out.println("  2. Voltar");
                System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
                if (Interface.lerOpcao(2) == 2) return;
                AcampamentoDosBandidos(ficha);
            } else if (ficha.isBandoVencido() && !ficha.isGaiolaVasculhada()) {
                System.out.println("  1. Vasculhar o local do acampamento");
                System.out.println("  2. Voltar");
                System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
                if (Interface.lerOpcao(2) == 2) return;
                VasculharAcampamento(ficha);
            } else if (ficha.isPegadasEncontradas() && !ficha.isTrilhaIniciada()) {
                System.out.println("  1. Seguir a trilha das pegadas");
                System.out.println("  2. Tentar achar mais pistas da neta");
                System.out.println("  3. Voltar");
                System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
                int escolha = Interface.lerOpcao(3);
                if (escolha == 3) return;
                if (escolha == 1) SeguirTrilha(ficha);
                else ProcurarPistas(ficha);
            } else if (!ficha.isPegadasEncontradas()) {
                System.out.println("  1. Tentar achar pistas da neta");
                System.out.println("  2. Voltar");
                System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
                int escolha = Interface.lerOpcao(2);
                if (escolha == 2) return;
                ProcurarPistas(ficha);
            } else {
                System.out.println("  1. Voltar");
                System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
                Interface.lerOpcao(1);
                return;
            }

            if (ficha.getVidaPersonagem() <= 0) return;
        }
    }

    public static void ProcurarPistas(FichaRpg ficha) {
        Interface.cabecalhoMenu("PROCURAR PISTAS DA NETA");
        Interface.MostrarMensagem("\nVocê se agacha e examina o chão da mata com atenção, procurando pegadas, fios de roupa, algo que a menina tenha deixado ao correr.");
        Interface.Pausa(2400);

        Interface.pressionarParaTeste("Presença");
        int dado = MecanicasRpg.rolarDado(20);
        int total = dado + ficha.getPresencaTeste();
        Interface.MostrarMensagem("-> Teste de Presença: " + dado + " (Dado) + " + ficha.getPresencaTeste() + " (Atributo) = " + total + " (Dificuldade: 10)");
        Interface.Pausa(2500);

        if (total >= 10) {
            Interface.MostrarMensagem("\nSeus olhos param num detalhe. Entre as folhas há uma dúzia de frutas caídas, ainda frescas. Não foram colhidas: foram " + CIANO + "derrubadas e rolaram" + RESET + " numa direção só, como se tivessem caído de uma bolsa que corria.");
            Interface.Pausa(2600);
            Interface.MostrarMensagem("Você segue a trilha das frutas e encontra " + AMARELO + "pegadas pequenas" + RESET + " indo na mesma direção, marcas de criança arrastando o pé de um lado só.");
            Interface.Pausa(2400);
            GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "Frutas caídas que rolaram numa direção só, marcadas por pegadas de criança arrastando um pé: a netinha corria com a bolsa de frutas.");
        } else {
            Interface.MostrarMensagem("\nVocê vasculha o chão por um bom tempo e encontra pouco: só algumas " + AMARELO + "pegadas" + RESET + ", gastas e já meio apagadas. Elas seguem numa direção só, mas não dizem para onde.");
            Interface.Pausa(2600);
            GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "Pegadas de criança seguindo numa direção única pela mata, mas apagadas demais para dizer mais.");
        }

        ficha.setPresencaNetaPassou(total >= 10);
        ficha.setPegadasEncontradas(true);
        Interface.Pausa(1800);
        System.out.println("\n  As pegadas seguem por entre as árvores, numa direção só.");
        System.out.println("  " + VERDE + "Deseja seguir esse caminho? (1. Sim / 2. Não, por enquanto)" + RESET);
        if (Interface.lerOpcao(2) == 1) {
            SeguirTrilha(ficha);
        }
    }

    public static void SeguirTrilha(FichaRpg ficha) {
        Interface.cabecalhoMenu("TRILHA DAS PEGADAS");
        Interface.MostrarMensagem("\nVocê se abaixa e segue as pegadas, ladeando árvores e passando por baixo dos galhos.");
        Interface.Pausa(2000);
        boolean passou = ficha.isPresencaNetaPassou();
        int turnos = passou ? TURNOS_CABANA : TURNOS_TRILHA;
        if (passou) {
            Interface.MostrarMensagem("\nAs marcas continuam consistentes, e com as frutas_dumpa_da_dica é possível ler o rumo. Dá para contar: " + AMARELO + turnos + RESET + " períodos até onde quer que a trilha acabe.");
        } else {
            Interface.MostrarMensagem("\nAs marcas são fracas, mas não mentem. Dá para contar: " + AMARELO + turnos + RESET + " períodos de caminhada até onde quer que a trilha acabe.");
        }
        Interface.Pausa(2000);
        ficha.setTrilhaIniciada(true);

        String[] passos = {
                "\nVocê avança. As pegadas de criança seguem firmes entre as folhas.",
                "\nVocê avança. O mato engrossa, mas as marcas continuam ali, na mesma direção.",
                "\nVocê avança. As pegadas chegam a uma clareira..."
        };
        for (int i = 0; i < turnos; i++) {
            Interface.MostrarMensagem(passos[i]);
            Interface.Pausa(2200);
            Floresta.avancarTempoComMensagens(ficha, 1);
            if (ficha.getVidaPersonagem() <= 0) return;
        }

        if (passou) {
            CaminharAteACabana(ficha, TURNOS_CABANA);
        } else {
            AcampamentoDosBandidos(ficha);
        }
    }

    private static void AcampamentoDosBandidos(FichaRpg ficha) {
        ficha.setAcampamentoAlcancado(true);
        Interface.cabecalhoMenu("ACAMPAMENTO NA MATA");
        Interface.MostrarMensagem("\nA trilha sai numa clareira. No centro, uma " + AMARELO + "fogueira" + RESET + " ainda fumega. Ao redor dela, gente sentada em troncos de árvore.");
        Interface.Pausa(2400);
        Interface.MostrarMensagem("Bandidos. Pode ser bando, pode ser o pessoal da estrada, mas estão armados e o acampamento é deles.");
        Interface.Pausa(2400);
        GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "A trilha das pegadas termina num acampamento de bandidos numa clareira. Eles podem ser os responsáveis pelo sumiço da netinha.");

        while (true) {
            System.out.println("\n  O que você faz?\n");
            System.out.println("  1. Voltar para a vila");
            System.out.println("  2. Avançar para lutar");
            System.out.println("  3. Tentar ver quantos bandidos são");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);

            int escolha = Interface.lerOpcao(3);
            if (escolha == 1) {
                Interface.MostrarMensagem("\nVocê recua em silêncio, antes de ser visto.");
                Interface.Pausa(2000);
                return;
            }
            if (escolha == 3) {
                Interface.pressionarParaTeste("Presença");
                int dado = MecanicasRpg.rolarDado(20);
                int total = dado + ficha.getPresencaTeste();
                Interface.MostrarMensagem("-> Teste de Presença: " + dado + " (Dado) + " + ficha.getPresencaTeste() + " (Atributo) = " + total + " (Dificuldade: 15)");
                Interface.Pausa(2500);
                if (total >= 15) {
                    Interface.MostrarMensagem("\nVocê se esgueira entre os troncos e conta: são " + AMARELO + "sete bandidos" + RESET + ", todos armados.");
                    Interface.Pausa(2400);
                    GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "O acampamento tem sete bandidos armados.");
                } else {
                    Interface.MostrarMensagem("\nVocê espia por entre os troncos, mas as fogueiras e os troncos atrapalham a visão. Não dá para contar quantos são.");
                    Interface.Pausa(2400);
                }
                continue;
            }

            System.out.println("\n  Como você parte para cima deles?\n");
            System.out.println("  1. Avançar à queima-roupa");
            System.out.println("  2. Tentar pegá-los de surpresa (Teste de Destreza, dificuldade 15)");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
            int ataque = Interface.lerOpcao(2);

            int bonus = 0;
            if (ataque == 2) {
                Interface.pressionarParaTeste("Destreza");
                int dado = MecanicasRpg.rolarDado(20);
                int total = dado + ficha.getDestrezaTeste();
                Interface.MostrarMensagem("-> Teste de Destreza: " + dado + " (Dado) + " + ficha.getDestrezaTeste() + " (Atributo) = " + total + " (Dificuldade: 15)");
                Interface.Pausa(2500);
                if (total >= 15) {
                    bonus = 5;
                    Interface.MostrarMensagem("\nVocê se arrasta pelo chão até a borda da clareira e ataca de surpresa! (+5 de iniciativa)");
                    Interface.Pausa(2500);
                } else {
                    Interface.MostrarMensagem("\nVocê tenta, mas faz barulho demais. Os bandidos te veem chegando. (Sem bônus)");
                    Interface.Pausa(2500);
                }
            } else {
                Interface.MostrarMensagem("\nVocê avança gritando! Os bandidos se levantam e sacam as armas.");
                Interface.Pausa(2500);
            }

            List<Criatura> bandidos = CriaturaFactory.criarBandoDaNeta();
            MotorDeCombate.IniciarCombate(ficha, bandidos, bonus > 0, bonus);
            if (ficha.getVidaPersonagem() <= 0) return;

            if (vivos(bandidos) > 0) {
                Interface.MostrarMensagem("\nOs bandidos que restam fogem pela mata. Você não vai atrás deles agora.");
                Interface.Pausa(2500);
                return;
            }

            Interface.MostrarMensagem("\nO acampamento fica em silêncio. Você vence os sete bandidos.");
            Interface.Pausa(2500);
            GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "Você derrotou os sete bandidos do acampamento.");
            ficha.setBandoVencido(true);

            System.out.println("\n  O que você faz?\n");
            System.out.println("  1. Vasculhar o local");
            System.out.println("  2. Voltar para a vila");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
            if (Interface.lerOpcao(2) == 1) {
                VasculharAcampamento(ficha);
            }
            return;
        }
    }

    private static int vivos(List<Criatura> inimigos) {
        int n = 0;
        for (Criatura c : inimigos) {
            if (c.getVida() > 0 && !c.isFugiu()) n++;
        }
        return n;
    }

    public static void VasculharAcampamento(FichaRpg ficha) {
        Interface.cabecalhoMenu("VASCULHAR O ACAMPAMENTO");
        Interface.MostrarMensagem("\nVocê esvazia os troncos e revira a bagunça do acampamento.");
        Interface.Pausa(2200);
        Interface.MostrarMensagem("\nNuma aba de madeira você encontra uma " + AMARELO + "gaiola" + RESET + ": aberta, arrombada por dentro, a grade reventada para fora.");
        Interface.Pausa(2400);
        Interface.MostrarMensagem("\nUma criança foi mantida aqui. As " + AMARELO + "pegadas" + RESET + " saem da gaiola e continuam por entre as árvores, apertadas, apressadas, correndo na direção de algum lugar.");
        Interface.Pausa(2400);
        ficha.setGaiolaVasculhada(true);
        ficha.setTurnosParaVoltar(1);
        GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "A gaiola do acampamento estava aberta e arrombada por dentro: a netinha foi mantida presa ali e fugiu dali correndo.");

        System.out.println("\n  Dá para seguir essas pegadas até onde elas terminam.");
        System.out.println("  " + VERDE + "Seguir agora? (1. Sim / 2. Não, por enquanto)" + RESET);
        if (Interface.lerOpcao(2) == 1) {
            CaminharAteACabana(ficha, 1);
        }
    }

    public static void CaminharAteACabana(FichaRpg ficha, int turnos) {
        Interface.cabecalhoMenu("SEGUIR AS PEGADAS");
        Interface.MostrarMensagem("\nVocê segue as pegadas que saem da gaiola. " + (turnos <= 1 ? "O caminho é curto." : "O caminho é mais longo, mas as marcas continuam."));
        Interface.Pausa(2200);
        for (int i = 0; i < turnos; i++) {
            Interface.MostrarMensagem("\nVocê avança...");
            Interface.Pausa(1800);
            Floresta.avancarTempoComMensagens(ficha, 1);
            if (ficha.getVidaPersonagem() <= 0) return;
        }
        ficha.setTurnosParaVoltar(turnos);
        ficha.setCabanaAlcancada(true);
        InterfaceNaCabana(ficha);
    }

    private static void InterfaceNaCabana(FichaRpg ficha) {
        Interface.cabecalhoMenu("CABANA NA MATA");
        Interface.MostrarMensagem("\nAs pegadas terminam numa clareira menor. No centro, uma " + AMARELO + "cabana" + RESET + " de madeira, com uma " + AMARELO + "luz acesa" + RESET + " piscando lá dentro.");
        Interface.Pausa(2600);

        while (true) {
            System.out.println("\n  O que você faz?\n");
            System.out.println("  1. Observar a cabana de longe");
            System.out.println("  2. Tentar ver com cuidado o que há lá dentro (Teste de Destreza, dificuldade 7)");
            System.out.println("  3. Voltar para a vila");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);

            int escolha = Interface.lerOpcao(3);
            if (escolha == 3) {
                Interface.MostrarMensagem("\nVocê recua em silêncio, engolindo a curiosidade.");
                Interface.Pausa(2000);
                return;
            }
            if (escolha == 1) {
                Interface.MostrarMensagem("\nVocê se esconde atrás de um tronco. A luz continua piscando. Nada além disso.");
                Interface.Pausa(2200);
                continue;
            }

            Interface.pressionarParaTeste("Destreza");
            int dado = MecanicasRpg.rolarDado(20);
            int total = dado + ficha.getDestrezaTeste();
            Interface.MostrarMensagem("-> Teste de Destreza: " + dado + " (Dado) + " + ficha.getDestrezaTeste() + " (Atributo) = " + total + " (Dificuldade: 7)");
            Interface.Pausa(2500);
            if (total < 7) {
                Interface.MostrarMensagem("\nVocê espia, mas a luz piscante cega seus olhos. Não dá para ver o que há lá dentro.");
                Interface.Pausa(2200);
                continue;
            }

            Interface.MostrarMensagem("\nVocê se esgueira até a janela. Lá dentro há um " + AMARELO + "grande homem" + RESET + ", de roupas de médico, movendo-se de um lado para o outro e trabalhando em algo.");
            Interface.Pausa(2600);

            if (ficha.isPrazoNetaEstourado()) {
                Interface.MostrarMensagem("\nNum canto, no chão, há uma " + VERMELHO + "criança morta" + RESET + ". Ele nem olha para lá, de costas, debruçado sobre a mesa, examinando o que tem em cima dela.");
                Interface.Pausa(2800);
                Interface.MostrarMensagem("\nO prazo de sete dias acabou enquanto você estava atrás disso.");
                Interface.Pausa(2400);
                GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "O prazo de sete dias estourou: quando você chegou à cabana, a netinha já estava morta num canto.");
                ficha.setNetaMorta(true);
            } else {
                Interface.MostrarMensagem("\nAmarrada numa cadeira, há uma " + AMARELO + "criança" + RESET + ". Viva, amordaçada, os olhos arregalados. É ela: a netinha da velhinha.");
                Interface.Pausa(2800);
                GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "A netinha está viva, amarrada numa cadeira na cabana do grande homem de roupas de médico.");
            }
            break;
        }

        while (true) {
            System.out.println("\n  O que você faz?\n");
            System.out.println("  1. Voltar para a vila");
            System.out.println("  2. Entrar com tudo");
            System.out.println("  3. Entrar furtivo (Teste de Destreza, dificuldade 12)");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);

            int escolha = Interface.lerOpcao(3);
            if (escolha == 1) {
                Interface.MostrarMensagem("\nVocê recua, a mente a mil. Precisa de um plano melhor.");
                Interface.Pausa(2200);
                return;
            }

            int bonus = 0;
            if (escolha == 3) {
                Interface.pressionarParaTeste("Destreza");
                int dado = MecanicasRpg.rolarDado(20);
                int total = dado + ficha.getDestrezaTeste();
                Interface.MostrarMensagem("-> Teste de Destreza: " + dado + " (Dado) + " + ficha.getDestrezaTeste() + " (Atributo) = " + total + " (Dificuldade: 12)");
                Interface.Pausa(2500);
                if (total >= 12) {
                    bonus = 3;
                    Interface.MostrarMensagem("\nVocê escorre pela fresta da porta e entra sem ser notado! (+3 de iniciativa)");
                    Interface.Pausa(2500);
                } else {
                    Interface.MostrarMensagem("\nVocê tenta passar pela porta, mas um assoalho range alto. Ele percebeu. (Sem bônus)");
                    Interface.Pausa(2500);
                }
            } else {
                Interface.MostrarMensagem("\nVocê arromba a porta e entra com tudo!");
                Interface.Pausa(2000);
            }

            Interface.MostrarMensagem("\nA luz some no teto. O grande homem se vira, e você vê o que há na mesa: " + VERMELHO + "carne humana" + RESET + ", aberta, em pedaços, cortes e preparos de experiência.");
            Interface.Pausa(2600);
            Interface.MostrarMensagem(VERMELHO + "Não é cozinha. Ele faz magias com carne e plantas, e usa pessoas como matéria-prima." + RESET);
            Interface.Pausa(2800);

            Criatura mago = CriaturaFactory.criarMagoMacabro();
            MotorDeCombate.IniciarCombate(ficha, List.of(mago), bonus > 0, bonus);
            if (ficha.getVidaPersonagem() <= 0) return;

            ficha.setMagicoMacabroDerrotado(true);
            ficha.setCabanaVisitada(true);
            GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "Você derrotou o Mago Macabro que mantinha a netinha presa na cabana.");

            if (!ficha.isNetaMorta()) {
                Interface.MostrarMensagem("\nA menina se solta dos nós, tremendo, e olha para você. Depois respira fundo e diz, com a voz firme de quem não é mais criança:");
                Interface.Pausa(2400);
                Interface.MostrarMensagem("\n\"Minha avó me mandou buscar as frutas da mata\", ela diz, e a voz ainda treme. \"Eu carregava a bolsa quando ele me pegou. Ele me trancou naquela coisa e ficou lá dentro.\"");
                Interface.Pausa(2600);
                Interface.MostrarMensagem("\nEla aperta a saia com as duas mãos e te segue até a entrada da mata.");
                Interface.Pausa(2200);
                VoltarComNeta(ficha, false);
            } else {
                VoltarComNeta(ficha, true);
            }
            return;
        }
    }

    private static void VoltarComNeta(FichaRpg ficha, boolean morta) {
        Interface.cabecalhoMenu("VOLTANDO À VILA");
        if (morta) {
            Interface.MostrarMensagem("\nVocê olha para a criança no canto. Alguém tem de levar ela até a avó, mesmo assim.");
            Interface.Pausa(2200);
            System.out.println("\n  O que você faz com o corpo?\n");
            System.out.println("  1. Levar o corpo da netinha até a vila");
            System.out.println("  2. Deixar o corpo na cabana");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
            if (Interface.lerOpcao(2) == 1) {
                ficha.setNetaCorpoLevado(true);
                Interface.MostrarMensagem("\nVocê embala a menina nos braços e sai da cabana.");
            } else {
                Interface.MostrarMensagem("\nVocê cobre a criança com um pano e deixa a cabana para trás. É o que você consegue.");
            }
            Interface.Pausa(2200);
        }
        int turnos = Math.max(1, ficha.getTurnosParaVoltar());
        Interface.MostrarMensagem("\nVocê pega o caminho de volta, refazendo a trilha na direção da vila.");
        Interface.Pausa(2000);
        for (int i = 0; i < turnos; i++) {
            Interface.MostrarMensagem("\nVocê avança em direção à vila...");
            Interface.Pausa(1800);
            Floresta.avancarTempoComMensagens(ficha, 1);
            if (ficha.getVidaPersonagem() <= 0) return;
        }
        ficha.setNetaSeguindo(false);
        ficha.adicionarProfundidade(FichaRpg.PROFUNDIDADE_PARA_SAIR);
        Interface.MostrarMensagem("\nAs árvores se abrem. Você está de volta à entrada do vilarejo de Scarbor.");
        Interface.Pausa(2400);
        ficha.setNetaEncontrada(true);
    }
}
