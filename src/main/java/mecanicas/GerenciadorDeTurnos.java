package mecanicas;

import classes.*;
import comandos.*;
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
import telas.MenuVisualizacao;

import static mecanicas.GerenciadorDeAcoes.*;
import static mecanicas.GerenciadorDeAtaque.*;
import static mecanicas.GerenciadorDeCompanheiro.*;
import static mecanicas.GerenciadorDeEvolucao.*;
import static mecanicas.GerenciadorDeHabilidades.*;
import static mecanicas.GerenciadorDeItens.*;
import static mecanicas.MotorDeCombate.*;

/** Fluxo dos turnos de combate e entidades em campo (companheiro e criaturas). */
public class GerenciadorDeTurnos {

// ==================== RODADAS DE COMBATE ====================


    public static void RodadasDeCombate(FichaRpg ficha, List<Criatura> inimigos, List<int[]> ordem) {
        boolean[] cascaGrossaAtiva = {false};
        int[] tentativasFuga = {0};
        List<Criatura> mortesProcessadas = new ArrayList<>();
        ficha.resetarEfeitosCombate();

        // Defesa Absoluta do Guerreiro: sempre ativa no começo do combate (some ao atacar)
        if (temHabilidade(ficha, "Defesa Absoluta")) {
            ficha.setDefesaAbsolutaAtiva(true);
            Interface.MostrarMensagem("\n(Defesa Absoluta ativa! +5 de defesa até você atacar.)");
            Interface.Pausa(1500);
        }

        while ((ficha.getVidaPersonagem() > 0
                || (ficha.getCompanheiro() != null && ficha.getCompanheiro().getFicha().getVidaPersonagem() > 0))
                && !inimigosVivos(inimigos).isEmpty()) {
            Interface.cabecalhoMenu("COMBATE");
            Interface.MostrarMensagem("  Sua Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + " | Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
            if (ficha.getCuraAbsolutaBonus() > 0) {
                Interface.MostrarMensagem("Proteção da Cura Absoluta: +" + ficha.getCuraAbsolutaBonus());
            }
            Interface.MostrarMensagem("  Inimigos:");
            for (int i = 0; i < inimigos.size(); i++) {
                Criatura c = inimigos.get(i);
                if (c.getVida() > 0 && !c.isFugiu()) {
                    Interface.MostrarMensagem("  " + (i + 1) + ". " + c.getNome() + " (Vida: " + c.getVida() + ")");
                }
            }
            if (tentativasFuga[0] > 0) {
                Interface.MostrarMensagem("  Tentativas de fuga: " + tentativasFuga[0] + "/3");
            }
            Interface.Pausa(1500);

            cascaGrossaAtiva[0] = false;
            ficha.setMagiaProibidaAtiva(false);

            // Infecção zumbi: o ferimento contaminado corrói 1d4 no início de cada rodada
            if (ficha.isInfectado() && ficha.getVidaPersonagem() > 0) {
                int danoInfecao = MecanicasRpg.rolarDado(4);
                ficha.receberDano(danoInfecao);
                Interface.MostrarMensagem("\nSua infecção zumbi corrói as feridas! Dano: " + danoInfecao + " (Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + ")");
                Interface.Pausa(2000);
                if (ficha.getVidaPersonagem() <= 0 && !companheiroEmPe(ficha)) break;
                if (inimigosVivos(inimigos).isEmpty()) break;
            }

            // O líquido mortal de Cura para a Morte começa a agir a partir do próximo turno
            if (ficha.isCuraParaMortePreparado()) {
                ficha.setCuraParaMortePreparado(false);
                ficha.setCuraParaMorteAtivo(true);
                Interface.MostrarMensagem("\nO líquido mortal injetado começa a agir!");
                Interface.Pausa(1500);
            }

            if (ficha.getVidaPersonagem() <= 0 && !companheiroEmPe(ficha)) break;
            if (inimigosVivos(inimigos).isEmpty()) break;

            // O jogador escolhe a ação da rodada ANTES de qualquer ataque acontecer.
            // Após o Estrondo, ele não pode usar habilidades no próximo turno.
            boolean semHabilidades = ficha.getRodadasSemHabilidade() > 0;
            ComandoCombate acaoDeclarada = ficha.getVidaPersonagem() > 0
                    ? declararAcao(ficha, inimigos, cascaGrossaAtiva, semHabilidades)
                    : new ComandoAguardar();

            // Registra inimigos que já atacaram nesta rodada (ex.: reação à fuga)
            Set<Criatura> jaAtacouNaRodada = new HashSet<>();

            // A FUGA é a única ação fora da ordem de iniciativa:
            // o jogador tenta fugir primeiro; se passar, ninguém ataca; se falhar, os inimigos atacam
            boolean acaoResolvida = false;
            if (acaoDeclarada.isPrioritarioFuga()) {
                int resultadoFuga = acaoDeclarada.executar(ficha, inimigos, cascaGrossaAtiva, tentativasFuga, jaAtacouNaRodada);
                if (resultadoFuga == 0) {
                    Interface.MostrarMensagem("\nVocê conseguiu escapar da floresta!");
                    Interface.Pausa(2500);
                    ficha.setInfectado(false); // a infecção some quando o combate acaba
                    ficha.setPactoMortalAtivo(false); // o Pacto Mortal dura só até o fim do combate
                    return;
                }
                if (resultadoFuga == 1) {
                    // Passou na fuga (precisa de 3 para escapar): ninguém ataca nesta rodada
                    continue;
                }
                // Falhou (ou perdeu a oportunidade): sofre os ataques na sequência da iniciativa
                acaoResolvida = true;
                processarMortes(inimigos, mortesProcessadas, ficha);
            }

            // As ações são resolvidas na ORDEM REAL da iniciativa:
            // se um inimigo tem iniciativa maior que a sua, ele age antes de você executar
            for (int[] token : ordem) {
                if (ficha.getVidaPersonagem() <= 0 && !companheiroEmPe(ficha)) break;
                if (inimigosVivos(inimigos).isEmpty()) break;

                if (token[1] == 0) {
                    if (acaoResolvida) continue;
                    if (ficha.getVidaPersonagem() <= 0) continue;
                    int resultado = acaoDeclarada.executar(ficha, inimigos, cascaGrossaAtiva, tentativasFuga, jaAtacouNaRodada);
                    if (resultado == 0) {
                        Interface.MostrarMensagem("\nVocê conseguiu escapar da floresta!");
                        Interface.Pausa(2500);
                        ficha.setInfectado(false); // a infecção some quando o combate acaba
                    ficha.setPactoMortalAtivo(false); // o Pacto Mortal dura só até o fim do combate
                        return;
                    }
                    // Processa XP/drops dos inimigos que acabaram de morrer
                    processarMortes(inimigos, mortesProcessadas, ficha);
                } else if (token[1] == -1) {
                    // Turno do companheiro: ele age sozinho (você não controla)
                    companheiros.Companheiro comp = ficha.getCompanheiro();
                    if (comp != null && comp.getFicha().getVidaPersonagem() > 0) {
                        acaoDoCompanheiro(ficha, comp, inimigos);
                        processarMortes(inimigos, mortesProcessadas, ficha);
                    }
                } else {
                    Criatura c = inimigos.get(token[1] - 1);
                    if (c.getVida() > 0 && !c.isFugiu() && !jaAtacouNaRodada.contains(c)) {
                        // PRISÃO: o inimigo preso tenta se libertar no início da sua vez (d20, precisa de 15+)
                        if (ficha.getPrisaoAtiva() == c) {
                            int testePrisao = MecanicasRpg.rolarDado(20);
                            Interface.MostrarMensagem("\n" + rotuloCriatura(inimigos, c) + " tenta se libertar da prisão (d20, precisa de 15 ou mais): " + testePrisao);
                            Interface.Pausa(1500);
                            if (testePrisao < 15) {
                                Interface.MostrarMensagem("A prisão o mantém imóvel! " + rotuloCriatura(inimigos, c) + " não consegue agir.");
                                Interface.Pausa(1500);
                                continue;
                            }
                            ficha.setPrisaoAtiva(null);
                            Interface.MostrarMensagem("A prisão se desfaz! " + rotuloCriatura(inimigos, c) + " está livre!");
                            Interface.Pausa(1500);
                        }

                        // MAGIA PROIBIDA: todos os testes dos inimigos desta rodada falham
                        if (ficha.isMagiaProibidaAtiva()) {
                            Interface.MostrarMensagem("\n" + rotuloCriatura(inimigos, c) + " investe, mas a Magia Proibida corrompe seu golpe e ele erra!");
                            Interface.Pausa(1500);
                            continue;
                        }

                        Interface.MostrarMensagem("\n" + rotuloCriatura(inimigos, c) + " avança para atacar!");
                        Interface.Pausa(1500);

                        // O inimigo escolhe aleatoriamente entre atacar você ou o companheiro.
                        // Se você caiu, ele só pode mirar no companheiro.
                        companheiros.Companheiro comp2 = ficha.getCompanheiro();
                        boolean atacarCompanheiro = comp2 != null
                                && comp2.getFicha().getVidaPersonagem() > 0
                                && (ficha.getVidaPersonagem() <= 0 || MecanicasRpg.rolarDado(2) == 1);
                        if (atacarCompanheiro) {
                            Interface.MostrarMensagem(rotuloCriatura(inimigos, c) + " mira em " + comp2.getNomeCompleto() + "!");
                            Interface.Pausa(1500);
                            c.atacarJogador(comp2.getFicha(), false, false);
                            if (comp2.getFicha().getVidaPersonagem() <= 0) {
                                Interface.MostrarMensagem("\n" + comp2.getNomeCompleto() + " cai em combate!");
                                Interface.Pausa(1500);
                            }
                        } else {
                            c.atacarJogador(ficha, cascaGrossaAtiva[0], true);
                        }
                        // Inimigos podem morrer pelo reflexo da Proteção Absoluta
                        processarMortes(inimigos, mortesProcessadas, ficha);
                    }
                }
            }

            // Fim da rodada: o Guerreiro se recupera do Estrondo
            if (ficha.getRodadasSemHabilidade() > 0) {
                ficha.setRodadasSemHabilidade(ficha.getRodadasSemHabilidade() - 1);
            }
        }

        // Após o combate, tentativas de resgate (DT 14, teste de Intelecto).
        // Só é possível reviver quem caiu se o grupo venceu (todos os monstros morreram).
        companheiros.Companheiro comp = ficha.getCompanheiro();
        boolean grupoVenceu = inimigosVivos(inimigos).isEmpty();

        if (grupoVenceu) {
            // Caso 1: Companheiro morreu — jogador (se de pé) tenta salvá-lo
            if (comp != null && ficha.getVidaPersonagem() > 0) {
                FichaRpg cf = comp.getFicha();
                if (cf.getVidaPersonagem() <= 0) {
                    Interface.cabecalhoMenu("RESGATE DO COMPANHEIRO");
                    Interface.MostrarMensagem("\n  " + comp.getNomeCompleto() + " cai no chão, sem vida...");
                    Interface.Pausa(1500);

                    boolean salvo = tentarResgate(ficha, "Você", cf, comp.getNomeCompleto());

                    if (!salvo) {
                        Interface.MostrarMensagem("\n  Ele(a) parte em silêncio.");
                        Interface.Pausa(1500);

                        // Transfere os itens do companheiro para o jogador
                        List<ItemRpg> itensComp = new ArrayList<>(cf.getInventario());
                        for (ItemRpg item : itensComp) {
                            ficha.adicionarItem(item);
                        }
                        int ouroComp = cf.getOuro();
                        if (ouroComp > 0) {
                            ficha.adicionarOuro(ouroComp);
                        }

                        if (!itensComp.isEmpty() || ouroComp > 0) {
                            Interface.MostrarMensagem("\n  Você recolhe os pertences de " + comp.getNomeCompleto() + ".");
                            for (ItemRpg item : itensComp) {
                                Interface.MostrarMensagem("    + " + item.getNome() + " (" + item.getQuantidade() + ")");
                            }
                            if (ouroComp > 0) {
                                Interface.MostrarMensagem("    + " + ouroComp + " de ouro");
                            }
                        }

                        ficha.removerCompanheiro();
                        comp = null;
                    }
                    Interface.Pausa(2000);
                } else if (cf.getVidaPersonagem() < cf.getVidaMaxima() * 0.3) {
                    // Companheiro sobreviveu, mas muito ferido: respira fundo e se recupera um pouco
                    Interface.MostrarMensagem("\n" + comp.getNomeCompleto() + " respira fundo e se recupera um pouco após o combate.");
                    cf.setVidaPersonagem(Math.max((int) (cf.getVidaMaxima() * 0.5), 1));
                    Interface.Pausa(2000);
                }
            }

            // Caso 2: Jogador morreu — tenta a Cura Total; se não, companheiro (se de pé) tenta salvá-lo
            if (ficha.getVidaPersonagem() <= 0) {
                if (!tentarReviver(ficha) && companheiroEmPe(ficha)) {
                    FichaRpg cf = comp.getFicha();
                    Interface.cabecalhoMenu("RESGATE DO JOGADOR");
                    Interface.MostrarMensagem("\n  Você cai... " + comp.getNomeCompleto() + " se joga ao seu lado!");
                    Interface.Pausa(1500);

                    tentarResgate(cf, comp.getNomeCompleto(), ficha, "você");

                    Interface.Pausa(2000);
                }
            }
        }

        Interface.cabecalhoMenu("FIM DO COMBATE");
        ficha.setInfectado(false); // a infecção some quando o combate acaba
                    ficha.setPactoMortalAtivo(false); // o Pacto Mortal dura só até o fim do combate
        if (ficha.getVidaPersonagem() <= 0) {
            Interface.MostrarMensagem("\n  Você foi derrotado... A floresta recupera o silêncio.");
            Interface.Pausa(3000);
        } else {
            Interface.MostrarMensagem("\n  Você sobreviveu ao combate!");
            Interface.Pausa(2500);
        }
        Interface.barraDivisoria();
    }

// Concede XP e drops dos inimigos mortos ainda não processados, com mensagens de level up

    public static void processarMortes(List<Criatura> inimigos, List<Criatura> mortesProcessadas, FichaRpg ficha) {
        for (Criatura c : inimigos) {
            if (c.getVida() <= 0 && !mortesProcessadas.contains(c)) {
                mortesProcessadas.add(c);
                if (ficha.getPrisaoAtiva() == c) {
                    ficha.setPrisaoAtiva(null);
                }
                Interface.MostrarMensagem("\nVocê derrotou " + rotuloCriatura(inimigos, c) + "!");
                Interface.Pausa(1500);
                c.processarDrops(ficha);

                if (c.getXpGanho() > 0) {
                    Interface.MostrarMensagem("-> Você ganhou " + c.getXpGanho() + " XP!");
                    Interface.Pausa(1500);
                    int vidasAntes = ficha.getVidaMaxima();
                    int manaAntes = ficha.getManaMaxima();
                    int nivelAntes = ficha.getNivel();
                    List<String> habilidadesAntes = new ArrayList<>();
                    for (habilidades.Habilidade hh : ficha.getHabilidades()) {
                        habilidadesAntes.add(hh.getNome());
                    }
                    int niveisGanhos = ficha.adicionarXp(c.getXpGanho());
                    if (niveisGanhos > 0) {
                        Interface.MostrarMensagem("\n*** SUBIU PARA O NÍVEL " + ficha.getNivel() + "! ***");
                        Interface.Pausa(2000);
                        Interface.MostrarMensagem("+ " + (ficha.getVidaMaxima() - vidasAntes) + " de vida máxima.");
                        Interface.MostrarMensagem("+ " + (ficha.getManaMaxima() - manaAntes) + " de mana máxima.");
                        Interface.Pausa(2000);
                        if (ficha.getNivel() < 10) {
                            Interface.MostrarMensagem("XP para o próximo nível: " + fichas.FichaRpg.getXpNecessaria(ficha.getNivel()));
                        } else {
                            Interface.MostrarMensagem("Você atingiu o nível máximo!");
                        }
                        Interface.Pausa(2000);

                        for (habilidades.Habilidade hh : ficha.getHabilidades()) {
                            if (!habilidadesAntes.contains(hh.getNome())) {
                                Interface.MostrarMensagem("\nVocê aprendeu a habilidade: " + hh.getNome() + "!");
                                Interface.MostrarMensagem(hh.getDescricao());
                                Interface.Pausa(2000);
                            }
                        }

                        if (ficha.getClasseDoPersonagem() instanceof classes.Mago) {
                            for (habilidades.Habilidade hh : ficha.getHabilidades()) {
                                if (hh instanceof habilidades.Magia && ((habilidades.Magia) hh).isAtaqueArea()) {
                                    Interface.MostrarMensagem("\nSua " + hh.getNome() + " agora ataca em área!");
                                    Interface.Pausa(2000);
                                }
                            }
                        }

                        for (int nivelGanho = nivelAntes + 1; nivelGanho <= ficha.getNivel(); nivelGanho++) {
                            if (nivelGanho == 5 || nivelGanho == 7 || nivelGanho == 9 || nivelGanho == 10) {
                                List<habilidades.Habilidade> opcoes = ficha.getClasseDoPersonagem().getEscolhasDisponiveis(ficha, nivelGanho);
                                if (opcoes != null && !opcoes.isEmpty()) {
                                    GerenciadorDeEvolucao.escolherHabilidadeNivel(ficha, nivelGanho, opcoes);
                                }
                            }
                            if (nivelGanho == 2 || nivelGanho == 4 || nivelGanho == 6 || nivelGanho == 8) {
                                GerenciadorDeEvolucao.escolherPontoAtributo(ficha);
                            }
                        }
                    }
                }
            }
        }
    }

// Tenta fugir gastando o turno; retorna 0 se escapou, 1 se passou (sem chegar nas 3) ou -1 se falhou

    public static int TentarFugirNaVez(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, int[] tentativasFuga, Set<Criatura> jaAtacouNaRodada) {
        int resultadoFuga = TentarFugir(ficha, inimigos, tentativasFuga[0]);

        // Desistiu de fugir na confirmação: a ação é perdida e os inimigos atacam
        if (resultadoFuga == tentativasFuga[0] && resultadoFuga != -1) {
            Interface.MostrarMensagem("\nVocê hesita e perde a oportunidade!");
            Interface.Pausa(1500);
            return -1;
        }

        if (resultadoFuga == -1) {
            Interface.MostrarMensagem("\nA ameaça te alcança e aproveita a abertura!");
            Interface.Pausa(1500);
            Criatura maisRapido = inimigoMaisRapidoVivo(inimigos);
            if (maisRapido != null) {
                jaAtacouNaRodada.add(maisRapido);
                maisRapido.atacarJogador(ficha, cascaGrossaAtiva[0], true);
            }
            return -1;
        } else if (resultadoFuga == 3) {
            return 0;
        } else {
            tentativasFuga[0] = resultadoFuga;
            return 1;
        }
    }

// Há uma criatura viva que bloqueia a fuga no combate (ex.: Minotauro)?

    public static boolean temBossSemFuga(List<Criatura> inimigos) {
        for (Criatura c : inimigos) {
            if (c.getVida() > 0 && c.isSemFuga()) {
                return true;
            }
        }
        return false;
    }

// Retorna a criatura viva com maior iniciativa base

    public static Criatura inimigoMaisRapidoVivo(List<Criatura> inimigos) {
        Criatura maisRapido = null;
        for (Criatura c : inimigos) {
            if (c.getVida() <= 0) continue;
            if (maisRapido == null || c.getIniciativa() > maisRapido.getIniciativa()) {
                maisRapido = c;
            }
        }
        return maisRapido;
    }

// ==================== VEZ DO COMPANHEIRO (AUTO) ====================

    // Retorna um inimigo vivo sorteado aleatoriamente

    public static Criatura escolherAlvoAleatorio(List<Criatura> inimigos) {
        List<Criatura> vivos = inimigosVivos(inimigos);
        if (vivos.isEmpty()) return null;
        return vivos.get(MecanicasRpg.rolarDado(vivos.size()) - 1);
    }




    public static List<Criatura> inimigosVivos(List<Criatura> inimigos) {
        List<Criatura> vivos = new ArrayList<>();
        for (Criatura c : inimigos) {
            if (c.getVida() > 0 && !c.isFugiu()) {
                vivos.add(c);
            }
        }
        return vivos;
    }



    public static String rotuloCriatura(List<Criatura> inimigos, Criatura alvo) {
        int contagem = 0;
        for (Criatura c : inimigos) {
            if (c.getNome().equals(alvo.getNome())) {
                contagem++;
            }
        }
        if (contagem <= 1) return alvo.getNome();
        return alvo.getNome() + " " + (inimigos.indexOf(alvo) + 1);
    }


    public static String nomesDosInimigos(List<Criatura> inimigos) {
        Criatura c = inimigos.get(0);
        if (inimigos.size() == 1) return c.getNome();
        switch (c.getNome()) {
            case "Lobo Selvagem": return "Lobos Selvagens";
            case "Urso": return "Ursos";
            case "Bandido": return "Bandidos";
            default: return c.getNome();
        }
    }

// Fase de resolução: aplica o item escolhido quando chega a vez do jogador na iniciativa

    // ==================== FUGA ====================


    public static int TentarFugir(FichaRpg ficha, List<Criatura> inimigos, int tentativasAtuais) {
        Interface.cabecalhoMenu("TENTAR FUGIR");
        System.out.println("\n  Deseja realmente tentar fugir?\n");
        System.out.println("  1. Sim, tentar fugir");
        System.out.println("  2. Não, voltar ao combate");

        int confirmar = Interface.lerInteiro();


        if (confirmar != 1) return tentativasAtuais;

        Interface.MostrarMensagem("\nVocê tenta se esquivar e recuar...");
        Interface.Pausa(2000);

        // Só o jogador rola: a dificuldade é fixa (10 + iniciativa da ameaça mais rápida)
        int melhorIniciativa = -1000;
        for (Criatura c : inimigos) {
            if (c.getVida() <= 0) continue;
            if (c.getIniciativa() > melhorIniciativa) {
                melhorIniciativa = c.getIniciativa();
            }
        }
        int dificuldadeFuga = 10 + melhorIniciativa;

        Interface.pressionarParaTeste("Destreza");
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int totalJogador = dadoJogador + ficha.getDestrezaTeste();
        Interface.MostrarMensagem("-> Sua Tentativa de Fuga: " + dadoJogador + " (Dado) + " + ficha.getDestrezaTeste() + " (Destreza) = " + totalJogador + " (Dificuldade: " + dificuldadeFuga + ")");
        Interface.Pausa(2000);

        if (totalJogador < dificuldadeFuga) {
            Interface.MostrarMensagem("\nA ameaça te alcança! Você perdeu a chance de fugir dessa vez.");
            return -1;
        }

        int novasTentativas = tentativasAtuais + 1;
        Interface.MostrarMensagem("\nVocê se afasta um passo! Tentativa " + novasTentativas + "/3");
        Interface.Pausa(2000);
        return novasTentativas;
    }

// ==================== TESOUROS DO LABIRINTO ====================

    // Aplica dano a uma criatura respeitando o Pacto Mortal: um alvo enfraquecido
    // sofre +5 de dano demoníaco em cada golpe recebido (de qualquer fonte).

    public static void aplicarDanoCriatura(Criatura c, int dano) {
        if (dano <= 0) return;
        if (c.isEnfraquecido()) {
            dano += 5;
            Interface.MostrarMensagem("(Pacto Mortal! " + c.getNome() + " sofre +5 de dano demoníaco)");
            Interface.Pausa(1000);
        }
        c.setVida(Math.max(0, c.getVida() - dano));
    }

// Pacto Mortal (Olho Demoníaco): gasta 4 de mana. Até o fim do combate o alvo tem
    // -2 nas rolagens e +5 de dano demoníaco, mas VOCÊ sofre +3 em todo dano recebido.

    // Rei das Criaturas (Coroa do Rei): 3 de mana, não gasta a ação. O jogador rola um
    // teste de Presença contra a criatura (d20 + nível dela); vencendo, comanda: Fugir,
    // Atacar a si mesma ou Atacar outro monstro.

    // Criaturas vivas exceto `excluida` (usado no comando "Atacar outro monstro")

    public static List<Criatura> outrosVivos(List<Criatura> inimigos, Criatura excluida) {
        List<Criatura> outros = new ArrayList<>();
        for (Criatura c : inimigosVivos(inimigos)) {
            if (c != excluida) outros.add(c);
        }
        return outros;
    }

// Uma criatura ataca outra (ou a si mesma): rola acerto contra a defesa do alvo e,
    // se acertar, causa o dano de um de seus ataques (crítico dobra os dados).

    public static void criaturaAtacaCriatura(Criatura atacante, Criatura alvo, List<Criatura> inimigos) {
        int dado = MecanicasRpg.rolarDado(20);
        int total = dado + atacante.getBonusAcerto();
        boolean critico = dado == 20;
        Interface.MostrarMensagem("-> " + atacante.getNome() + " ataca " + (alvo == atacante ? "a si mesma" : rotuloCriatura(inimigos, alvo)) + ": " + dado + " (Dado) + " + atacante.getBonusAcerto() + " (Bônus) = " + total + " (Defesa: " + alvo.getDefesa() + ")" + (critico ? " [CRÍTICO!]" : ""));
        Interface.Pausa(2000);

        if (!critico && total < alvo.getDefesa()) {
            Interface.MostrarMensagem("-> O golpe erra!");
            Interface.Pausa(1500);
            return;
        }

        Criatura.Ataque ataque = atacante.getAtaques().get(MecanicasRpg.rolarDado(atacante.getAtaques().size()) - 1);
        int dadosTotais = ataque.qtdDado * (critico ? 2 : 1);
        int dano = 0;
        for (int i = 0; i < dadosTotais; i++) {
            dano += MecanicasRpg.rolarDado(ataque.ladosDado);
        }
        Interface.MostrarMensagem("-> " + atacante.getNome() + " acerta com " + ataque.nome + " causando " + dano + " de dano" + (critico ? " (crítico!)" : "") + "!");
        Interface.Pausa(1500);
        aplicarDanoCriatura(alvo, dano);
        Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
        Interface.Pausa(1500);
    }
}
