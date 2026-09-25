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

public class MotorDeCombate {

    // Códigos de Cores ANSI usados nos menus
    public static final String RESET = "\u001B[0m";
    public static final String CIANO = "\u001B[36m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARELO = "\u001B[33m";

    // ==================== INICIAR COMBATE ====================

    public static void IniciarCombate(FichaRpg ficha, List<Criatura> inimigos, boolean jogadorSurpreendeu) {
        Interface.MostrarMensagem("\n================ COMBATE ================");
        Interface.Pausa(2500);

        int bonusIniciativaJogador = jogadorSurpreendeu ? 2 : 0;
        Interface.pressionarParaTeste("Destreza");
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int iniciativaJogador = dadoJogador + ficha.getDestrezaTeste() + bonusIniciativaJogador;

        Interface.MostrarMensagem("-> Iniciativa [" + ficha.getNomePersonagem() + "]: " + dadoJogador + " (Dado) + " + ficha.getDestrezaTeste() + " (Destreza) + " + bonusIniciativaJogador + " (Bônus) = " + iniciativaJogador);
        Interface.Pausa(2500);

        List<int[]> ordem = new ArrayList<>();
        ordem.add(new int[]{iniciativaJogador, 0});

        // Companheiro (se presente e consciente) também entra na iniciativa
        companheiros.Companheiro comp = ficha.getCompanheiro();
        if (comp != null && comp.getFicha().getVidaPersonagem() > 0) {
            int dadoComp = MecanicasRpg.rolarDado(20);
            int iniciativaComp = dadoComp + comp.getFicha().getDestrezaTeste();
            Interface.MostrarMensagem("-> Iniciativa [" + comp.getNomeCompleto() + "]: " + dadoComp + " (Dado) + " + comp.getFicha().getDestrezaTeste() + " (Destreza) = " + iniciativaComp);
            Interface.Pausa(1500);
            ordem.add(new int[]{iniciativaComp, -1});
        }

        for (int i = 0; i < inimigos.size(); i++) {
            Criatura c = inimigos.get(i);
            int dadoInimigo = MecanicasRpg.rolarDado(20);
            int iniciativaInimigo = dadoInimigo + c.getIniciativa();
            Interface.MostrarMensagem("-> Iniciativa [" + rotuloCriatura(inimigos, c) + "]: " + dadoInimigo + " (Dado) + " + c.getIniciativa() + " (Iniciativa Base) = " + iniciativaInimigo);
            Interface.Pausa(1500);
            ordem.add(new int[]{iniciativaInimigo, i + 1});
        }

        // Ordena os combatentes pela iniciativa (do maior para o menor)
        ordem.sort((a, b) -> Integer.compare(b[0], a[0]));

        StringBuilder ordemTexto = new StringBuilder();
        for (int[] token : ordem) {
            String nomeOrdem;
            if (token[1] == 0) {
                nomeOrdem = ficha.getNomePersonagem();
            } else if (token[1] == -1) {
                nomeOrdem = comp != null ? comp.getNomeCompleto() : "Companheiro";
            } else {
                nomeOrdem = rotuloCriatura(inimigos, inimigos.get(token[1] - 1));
            }
            if (ordemTexto.length() > 0) ordemTexto.append(" > ");
            ordemTexto.append(nomeOrdem);
        }
        Interface.MostrarMensagem("\nOrdem de Iniciativa: " + ordemTexto);
        Interface.Pausa(2500);

        RodadasDeCombate(ficha, inimigos, ordem);
    }

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

    public static boolean tentarResgate(FichaRpg salvador, String nomeSalvador, FichaRpg vitima, String nomeVitima) {
        Interface.MostrarMensagem("  " + nomeSalvador + " tenta estabilizar " + nomeVitima + "...\n");
        Interface.Pausa(1000);

        int dado = MecanicasRpg.rolarDado(20);
        int total = dado + salvador.getIntelectoTeste();
        Interface.MostrarMensagem("-> Teste de Intelecto (Resgate): " + dado + " (Dado) + " + salvador.getIntelectoTeste() + " (Atributo) = " + total + " (Dificuldade: 14)");
        Interface.Pausa(1500);

        if (total > 14) {
            vitima.setVidaPersonagem(1);
            Interface.MostrarMensagem("\n  " + nomeSalvador + " consegue estabilizar " + nomeVitima + " a tempo! " + nomeVitima + " acorda com 1 de vida.");
            return true;
        } else {
            Interface.MostrarMensagem("\n  " + nomeSalvador + " falha em estabilizar " + nomeVitima + "...");
            return false;
        }
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
                                    escolherHabilidadeNivel(ficha, nivelGanho, opcoes);
                                }
                            }
                            if (nivelGanho == 2 || nivelGanho == 4 || nivelGanho == 6 || nivelGanho == 8) {
                                escolherPontoAtributo(ficha);
                            }
                        }
                    }
                }
            }
        }
    }

    // ==================== VEZ DO JOGADOR ====================

    // Fase de declaração: o jogador escolhe a ação da rodada SEM executar ainda.
    // A execução acontece quando chega a vez dele na ordem de iniciativa.
    public static ComandoCombate declararAcao(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, boolean semHabilidades) {
        while (true) {
            Interface.barraDivisoria();
            System.out.println("\n  O que deseja fazer?\n");
            System.out.println("  1. Lutar");
            System.out.println("  2. Abrir Mochila");
            if (temBossSemFuga(inimigos)) {
                System.out.println(AMARELO + "  3. Tentar Fugir (INDISPONÍVEL — a porta se fechou!)" + RESET);
            } else {
                System.out.println("  3. Tentar Fugir");
            }
            System.out.println("  4. Ver Ficha\n");
            System.out.println("  Escolha uma opção:");
            int escolha = Interface.lerInteiro();

            if (escolha == 4) {
                Interface.MostrarFicha(ficha);
                Interface.Pausa(1500);
                continue;
            }

            if (escolha == 1) {
                ComandoCombate resultado = MenuLutarComEscolha(ficha, inimigos, cascaGrossaAtiva, semHabilidades);
                if (resultado == null) continue;
                return resultado;
            } else if (escolha == 2) {
                int item = escolherItemParaUsar(ficha);
                if (item == -1) continue;
                if (item == -2) return new ComandoAguardar();
                return new ComandoUsarItem(item);
            } else if (escolha == 3) {
                if (temBossSemFuga(inimigos)) {
                    Interface.ExibirErro("A porta se fechou! Não há como fugir deste combate!");
                    Interface.Pausa(1500);
                    continue;
                }
                return new ComandoFuga();
            } else {
                Interface.ExibirErro("Escolha inválida!");
                Interface.Pausa(1500);
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

    // O companheiro age sozinho na sua vez (o jogador não controla)
    public static void acaoDoCompanheiro(FichaRpg ficha, companheiros.Companheiro comp, List<Criatura> inimigos) {
        FichaRpg cf = comp.getFicha();
        if (cf.getVidaPersonagem() <= 0) return;

        Criatura alvo = escolherAlvoAleatorio(inimigos);
        if (alvo == null) return;

        Interface.MostrarMensagem("\n" + comp.getNomeCompleto() + " age!");
        Interface.Pausa(1200);

        // Healer: prioriza curar o jogador quando ele está ferido; se não, cura a si mesmo
        if (cf.getClasseDoPersonagem() instanceof classes.Healer
                && cf.temItem("Kit Médico")
                && cf.getManaPersonagem() >= 1) {
            boolean jogadorFerido = ficha.getVidaPersonagem() <= (int) (ficha.getVidaMaxima() * 0.6);
            boolean siFerido = cf.getVidaPersonagem() <= (int) (cf.getVidaMaxima() * 0.6);

            if (jogadorFerido) {
                cf.setManaPersonagem(cf.getManaPersonagem() - 1);
                int cura = MecanicasRpg.rolarDado(4) + MecanicasRpg.rolarDado(4);
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                Interface.MostrarMensagem(comp.getNome() + " grita: \"Aguenta! Vou te curar!\" e usa a Medicina Reforçada!");
                Interface.MostrarMensagem("Você recuperou " + cura + " de vida! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                Interface.Pausa(1800);
                return;
            }

            if (siFerido) {
                cf.setManaPersonagem(cf.getManaPersonagem() - 1);
                int cura = MecanicasRpg.rolarDado(4);
                cf.setVidaPersonagem(Math.min(cf.getVidaPersonagem() + cura, cf.getVidaMaxima()));
                Interface.MostrarMensagem(comp.getNome() + " usa o Kit Médico em si mesmo(a) e restaura " + cura + " de vida.");
                Interface.Pausa(1500);
                return;
            }
        }

        // Mago: tenta lançar magia quando tem mana disponível
        if (cf.getClasseDoPersonagem() instanceof classes.Mago) {
            habilidades.Magia bola = null;
            habilidades.Magia pequena = null;
            for (habilidades.Habilidade hab : cf.getHabilidades()) {
                if (hab instanceof habilidades.Magia) {
                    habilidades.Magia mag = (habilidades.Magia) hab;
                    if (mag.getCustoMana() > 0 && bola == null) bola = mag;
                    if (mag.getCustoMana() == 0 && pequena == null) pequena = mag;
                }
            }

            int aleatorio = MecanicasRpg.rolarDado(100);
            habilidades.Magia magiaUsar = null;
            if (bola != null && cf.getManaPersonagem() >= custoEfetivoMagia(cf, bola) && aleatorio <= 60) {
                magiaUsar = bola;
            } else if (pequena != null && aleatorio <= 30) {
                magiaUsar = pequena;
            }

            if (magiaUsar != null) {
                cf.setManaPersonagem(cf.getManaPersonagem() - custoEfetivoMagia(cf, magiaUsar));
                Interface.MostrarMensagem(comp.getNomeCompleto() + " conjura " + magiaUsar.getNome() + "!");
                Interface.Pausa(1500);
                int dano = 0;
                for (int i = 0; i < magiaUsar.getQuantidadeDano(); i++) {
                    dano += MecanicasRpg.rolarDado(magiaUsar.getDadoDano());
                }
                Interface.MostrarMensagem("-> Dano: " + dano + "!");
                Interface.Pausa(1200);
                aplicarDanoCriatura(alvo, dano);
                Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
                Interface.Pausa(1200);
                return;
            }
        }

        // Ataque físico com a arma equipada
        Interface.MostrarMensagem(comp.getNome() + " avança para atacar!");
        Interface.Pausa(1200);
        atacarComArmaDoCompanheiro(cf, alvo, inimigos);
    }

    // Ataque físico do companheiro usando a própria arma (roll dotado de crítico)
    public static void atacarComArmaDoCompanheiro(FichaRpg cf, Criatura alvo, List<Criatura> inimigos) {
        Arma arma = cf.getArmaEquipada();
        if (arma == null && cf.getClasseDoPersonagem() != null) {
            arma = cf.getClasseDoPersonagem().getAtaqueDesarmado();
        }

        int atributoBonus;
        String nomeAtributo;
        if (arma != null && arma.isAgil()) {
            if (cf.getForca() >= cf.getDestreza()) {
                atributoBonus = cf.getForca();
                nomeAtributo = "Força";
            } else {
                atributoBonus = cf.getDestreza();
                nomeAtributo = "Destreza";
            }
        } else if (arma != null && arma.getAtributoAtaque().equals("Destreza")) {
            atributoBonus = cf.getDestreza();
            nomeAtributo = "Destreza";
        } else {
            atributoBonus = cf.getForca();
            nomeAtributo = "Força";
        }

        int dadoAtaque = MecanicasRpg.rolarDado(20);
        int totalAtaque = dadoAtaque + atributoBonus;
        boolean critico = dadoAtaque == 20;
        Interface.MostrarMensagem("-> Ataque [" + (arma != null ? arma.getNome() : "Soco") + "]: " + dadoAtaque + " (Dado) + " + atributoBonus + " (" + nomeAtributo + ") = " + totalAtaque + (critico ? " [CRÍTICO!]" : ""));
        Interface.Pausa(1500);

        if (critico || totalAtaque >= alvo.getDefesa()) {
            Interface.MostrarMensagem("-> Acertou! (defesa do alvo: " + alvo.getDefesa() + ")" + (critico ? " CRÍTICO sempre acerta." : ""));
            Interface.Pausa(1200);
            int dano = 0;
            int dadosTotais = arma != null ? arma.getQuantidadeDanoArma() : 1;
            int dadoDano = arma != null ? arma.getDadoDanoArma() : 4;
            if (critico) dadosTotais *= 2;
            StringBuilder roladas = new StringBuilder();
            for (int i = 0; i < dadosTotais; i++) {
                int d = MecanicasRpg.rolarDado(dadoDano);
                dano += d;
                if (roladas.length() > 0) roladas.append(" + ");
                roladas.append(d);
            }
            dano += atributoBonus;
            Interface.MostrarMensagem("-> Dados Rolados: " + roladas + " = " + dano + " (Dano: " + dadosTotais + "d" + dadoDano + " + " + nomeAtributo + ": " + atributoBonus + ")");
            Interface.Pausa(1500);
            aplicarDanoCriatura(alvo, dano);
            Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
        } else {
            Interface.MostrarMensagem("-> Errou! (defesa do alvo: " + alvo.getDefesa() + ")");
        }
        Interface.Pausa(1200);
    }

    // Verifica se a habilidade passiva ainda pode ser ativada nesta rodada
    public static boolean podeAtivarPassiva(FichaRpg ficha, habilidades.Habilidade habilidade, boolean cascaGrossaAtiva) {
        if (!habilidade.isPassiva()) return false;
        switch (habilidade.getNome()) {
            case "Casca Grossa": return !cascaGrossaAtiva;
            case "Espada Afiada": return !ficha.isEspadaAfiadaAtiva();
            default: return true;
        }
    }

    public static void aplicaPassiva(FichaRpg ficha, habilidades.Habilidade habilidade, boolean[] cascaGrossaAtiva) {
        switch (habilidade.getNome()) {
            case "Casca Grossa": cascaGrossaAtiva[0] = true; break;
            case "Espada Afiada": ficha.setEspadaAfiadaAtiva(true); break;
        }
    }

    // Apresenta as opções de habilidade ao atingir um novo nível com escolha
    public static void escolherHabilidadeNivel(FichaRpg ficha, int nivel, List<habilidades.Habilidade> opcoes) {
        Interface.cabecalhoMenu("NOVA HABILIDADE - NÍVEL " + nivel);
        System.out.println("\n  (inclui habilidades de escolha de níveis anteriores ainda não aprendidas)\n");
        for (int i = 0; i < opcoes.size(); i++) {
            habilidades.Habilidade h = opcoes.get(i);
            System.out.println("  " + (i + 1) + ". " + CIANO + h.getNome() + RESET + " (Custo: " + h.getCustoMana() + " Mana)");
            System.out.println("     " + h.getDescricao());
        }
        System.out.println("\n  Escolha uma habilidade:");

        int escolha = Interface.lerInteiro();

        if (escolha < 1 || escolha > opcoes.size()) {
            Interface.ExibirErro("Escolha inválida!");
            Interface.Pausa(1500);
            escolha = 1;
        }

        habilidades.Habilidade aprendida = opcoes.get(escolha - 1);
        ficha.getHabilidades().add(aprendida);
        Interface.MostrarMensagem("\nVocê aprendeu a habilidade: " + aprendida.getNome() + "!");
        Interface.MostrarMensagem(aprendida.getDescricao());
        Interface.Pausa(2000);
        aplicarArmaMentalSeAprendida(ficha, aprendida);
        aplicarDeusSeAprendido(ficha, aprendida);
        aplicarConhecimentoAbsolutoSeAprendido(ficha, aprendida);
    }

    // Deus (Guerreiro lvl 10): ativa permanentemente a forma de Semi Deus e concede Cura Incessante
    public static void aplicarDeusSeAprendido(FichaRpg ficha, habilidades.Habilidade aprendida) {
        if (!aprendida.getNome().equals("Deus")) return;
        if (ficha.isDeusAtivo()) return;

        // Se a forma temporária de Semi Deus estava ativa, normaliza a vida máxima antes de aplicar
        // o bônus permanente (evita somar duas vezes)
        if (ficha.isSemiDeusAtivo() && ficha.getSemiDeusVidaOriginalMax() > 0) {
            ficha.setVidaMaxima(ficha.getSemiDeusVidaOriginalMax());
            ficha.setSemiDeusVidaOriginalMax(0);
        }

        int bonusVida = ficha.getVidaMaxima() / 2;
        ficha.setVidaMaxima(ficha.getVidaMaxima() + bonusVida);
        ficha.setSemiDeusAtivo(true);
        ficha.setDeusAtivo(true);
        if (!temHabilidade(ficha, "Cura Incessante")) {
            ficha.getHabilidades().add(new habilidades.ativas.HabilidadeCuraIncessante("Cura Incessante", "Cura toda a sua vida. Pode ser usada apenas uma vez por combate.", 0));
        }
        // Deus substitui a Semi Deus: ela some da lista de habilidades
        ficha.getHabilidades().removeIf(h -> h.getNome().equals("Semi Deus"));
        Interface.MostrarMensagem("\nVocê se torna um Deus! A forma de Semi Deus fica permanentemente ativa.");
        Interface.MostrarMensagem("Vida máxima aumentada em " + bonusVida + " e você ganhou a habilidade Cura Incessante!");
        Interface.Pausa(2500);
    }

    // Conhecimento Absoluto (Healer lvl 10): +2 em todos os atributos
    public static void aplicarConhecimentoAbsolutoSeAprendido(FichaRpg ficha, habilidades.Habilidade aprendida) {
        if (!aprendida.getNome().equals("Conhecimento Absoluto")) return;
        if (ficha.isConhecimentoAbsolutoAplicado()) return;

        int vidaAntes = ficha.getVidaMaxima();
        ficha.aumentarTodosAtributos(2);
        ficha.setConhecimentoAbsolutoAplicado(true);
        Interface.MostrarMensagem("\nConhecimento Absoluto! +2 em TODOS os atributos.");
        if (ficha.getVidaMaxima() > vidaAntes) {
            Interface.MostrarMensagem("Vida máxima aumentada em " + (ficha.getVidaMaxima() - vidaAntes) + " pelo retroativo de Constituição!");
        }
        Interface.Pausa(2500);
    }

    // Arma Mental transforma o Bisturi de 1d4 para 3d8 ao ser aprendida
    public static void aplicarArmaMentalSeAprendida(FichaRpg ficha, habilidades.Habilidade aprendida) {
        if (!aprendida.getNome().equals("Arma Mental")) return;

        for (ItemRpg item : ficha.getInventario()) {
            if (item instanceof itens.Arma && item.getNome().equals("Bisturi")) {
                itens.Arma bisturi = (itens.Arma) item;
                bisturi.setDadoDanoArma(8);
                bisturi.setQuantidadeDanoArma(3);
            }
        }
        if (ficha.getArmaEquipada() != null && ficha.getArmaEquipada().getNome().equals("Bisturi")) {
            ficha.getArmaEquipada().setDadoDanoArma(8);
            ficha.getArmaEquipada().setQuantidadeDanoArma(3);
        }
        Interface.MostrarMensagem("\nArma Mental! Seu Bisturi agora causa 3d8 de dano!");
        Interface.Pausa(2000);
    }

    public static void escolherPontoAtributo(FichaRpg ficha) {
        while (true) {
            Interface.cabecalhoMenu("PONTO DE ATRIBUTO");
            System.out.println("\n  Você ganhou um ponto de atributo! Escolha onde gastar:\n");
            System.out.println("  1. Constituição");
            System.out.println("  2. Destreza");
            System.out.println("  3. Força");
            System.out.println("  4. Sabedoria");
            System.out.println("  5. Intelecto");
            System.out.println("  6. Presença");

            int escolha = Interface.lerInteiro();

            if (escolha >= 1 && escolha <= 6) {
                String atributo = ficha.aumentarAtributo(escolha);
                Interface.MostrarMensagem("\n+1 de " + atributo + "!");
                if (atributo.equals("Constituição") && ficha.getNivel() > 1) {
                    Interface.MostrarMensagem("Vida máxima aumentada em " + (ficha.getNivel() - 1) + " pelo retroativo de Constituição dos níveis anteriores!");
                }
                Interface.Pausa(1500);
                return;
            }

            Interface.ExibirErro("Opção inválida!");
        }
    }

    public static void executarAcaoJogador(FichaRpg ficha, List<Criatura> inimigos, int tipoAcao, int alvoIndex, int armaIndex, int habIndex) {
        boolean sucesso;
        if (tipoAcao == 1) {
            sucesso = executarAtaqueComArma(ficha, inimigos, alvoIndex, armaIndex);
        } else {
            sucesso = executarHabilidadeEscolhida(ficha, inimigos, alvoIndex, habIndex);
        }

        if (!sucesso) {
            tentarConhecimentoAvancado(ficha, inimigos, tipoAcao, alvoIndex, armaIndex, habIndex);
        }
    }

    // ==================== CONHECIMENTO AVANÇADO ====================

    public static void tentarConhecimentoAvancado(FichaRpg ficha, List<Criatura> inimigos, int tipoAcao, int alvoIndex, int armaIndex, int habIndex) {
        for (habilidades.Habilidade hab : ficha.getHabilidades()) {
            if (hab.getNome().equals("Conhecimento Avançado") && ficha.getManaPersonagem() >= custoEfetivoMagia(ficha, hab)) {
                System.out.println("\nDeseja usar Conhecimento Avançado para rerrolar? (Custo: " + custoEfetivoMagia(ficha, hab) + " Mana)");
                System.out.println("1. Sim");
                System.out.println("2. Não");
                int escolha = Interface.lerInteiro();


                if (escolha == 1) {
                    ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
                    Interface.MostrarMensagem("\nVocê foca seus conhecimentos e tenta novamente!");
                    Interface.Pausa(1500);

                    boolean sucessoReroll;
                    if (tipoAcao == 1) {
                        sucessoReroll = executarAtaqueComArma(ficha, inimigos, alvoIndex, armaIndex);
                    } else {
                        sucessoReroll = executarHabilidadeEscolhida(ficha, inimigos, alvoIndex, habIndex);
                    }

                    if (!sucessoReroll) {
                        Interface.MostrarMensagem("Mesmo com seu conhecimento, a ação falhou.");
                        Interface.Pausa(1500);
                    }
                }
                return;
            }
        }
    }

    // ==================== MENU LUTAR ====================

    public static ComandoCombate MenuLutarComEscolha(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, boolean semHabilidades) {
        Interface.cabecalhoMenu("COMO LUTAR?");
        System.out.println("\n");

        if (semHabilidades) {
            System.out.println("Você ainda está se recuperando do Estrondo e NÃO pode usar habilidades nesta rodada!");
            System.out.println("");
        }

        List<String> descricoes = new ArrayList<>();
        List<int[]> acoes = new ArrayList<>();

        descricoes.add("Atacar com Arma");
        acoes.add(new int[]{1, -1, -1, -1});

        if (!semHabilidades) {
            descricoes.add("Usar Habilidade");
            acoes.add(new int[]{2, -1, -1, -1});

            for (int i = 0; i < ficha.getHabilidades().size(); i++) {
                habilidades.Habilidade hab = ficha.getHabilidades().get(i);
                if (hab.isPassiva()
                        && !hab.getNome().equals("Defesa Absoluta")
                        && !hab.getNome().equals("Arma Mental")
                        && !hab.getNome().equals("Deus")
                        && !hab.getNome().equals("Conhecimento Absoluto")
                        && podeAtivarPassiva(ficha, hab, cascaGrossaAtiva[0])
                        && ficha.getManaPersonagem() >= hab.getCustoMana()) {
                    descricoes.add(hab.getNome() + " (Custo: " + hab.getCustoMana() + " Mana) - ainda pode atacar após usar");
                    acoes.add(new int[]{3, i, -1, -1});
                }
            }

            if (temHabilidade(ficha, "Magia Proibida") && !ficha.isMagiaProibidaUsada() && ficha.getManaPersonagem() >= 5) {
                descricoes.add("Magia Proibida (Custo: 5 Mana) - não gasta sua ação");
                acoes.add(new int[]{4, -1, -1, -1});
            }

            if (ficha.temItem("Coroa do Rei") && ficha.isReiDasCriaturas() && ficha.getManaPersonagem() >= 3) {
                descricoes.add("Rei das Criaturas (Custo: 3 Mana) - comande uma criatura sem gastar sua ação");
                acoes.add(new int[]{5, -1, -1, -1});
            }
        }

        for (int i = 0; i < descricoes.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + descricoes.get(i));
        }
        System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

        int escolha = Interface.lerInteiro();

        if (escolha == 0) return null;
        if (escolha < 1 || escolha > acoes.size()) {
            Interface.ExibirErro("Escolha inválida!");
            Interface.Pausa(1500);
            return null;
        }

        int[] acao = acoes.get(escolha - 1);

        if (acao[0] == 1) {
            int armaIdx = EscolherArma(ficha);
            if (armaIdx == -1) return null;
            int alvo = escolherAlvo(inimigos);
            if (alvo == -1) return null;
            return new ComandoAtacarComArma(alvo, armaIdx);
        }

        if (acao[0] == 2) {
            int habIdx = EscolherHabilidadeAtiva(ficha);
            if (habIdx == -1) return null;
            habilidades.Habilidade habEscolhida = ficha.getHabilidades().get(habIdx);
            int alvo = -1;
            if (habEscolhida.precisaDeAlvo()) {
                alvo = escolherAlvo(inimigos);
                if (alvo == -1) return null;
            }
            return new ComandoUsarHabilidade(alvo, habIdx);
        }

        if (acao[0] == 3) {
            habilidades.Habilidade hab = ficha.getHabilidades().get(acao[1]);
            ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
            aplicaPassiva(ficha, hab, cascaGrossaAtiva);
            Interface.MostrarMensagem("\nVocê ativa " + hab.getNome() + "!");
            Interface.MostrarMensagem(hab.getDescricao());
            Interface.Pausa(2000);
            return MenuLutarComEscolha(ficha, inimigos, cascaGrossaAtiva, semHabilidades);
        }

        if (acao[0] == 4) {
            ficha.setManaPersonagem(ficha.getManaPersonagem() - 5);
            ficha.setMagiaProibidaUsada(true);
            ficha.setMagiaProibidaAtiva(true);
            Interface.MostrarMensagem("\nVocê invoca a Magia Proibida! Todos os ataques dos inimigos desta rodada falharão.");
            Interface.Pausa(2000);
            return MenuLutarComEscolha(ficha, inimigos, cascaGrossaAtiva, semHabilidades);
        }

        if (acao[0] == 5) {
            usarReiDasCriaturas(ficha, inimigos);
            return MenuLutarComEscolha(ficha, inimigos, cascaGrossaAtiva, semHabilidades);
        }

        return null;
    }

    // Escolhe o alvo entre os inimigos vivos
    public static int escolherAlvo(List<Criatura> inimigos) {
        List<Criatura> vivos = new ArrayList<>();
        for (Criatura c : inimigos) {
            if (c.getVida() > 0 && !c.isFugiu()) {
                vivos.add(c);
            }
        }

        if (vivos.isEmpty()) return -1;
        if (vivos.size() == 1) return inimigos.indexOf(vivos.get(0));

        System.out.println("\n  " + CIANO + "[ ESCOLHA SEU ALVO ]" + RESET + "\n");
        for (int i = 0; i < vivos.size(); i++) {
            Criatura c = vivos.get(i);
            System.out.println("  " + (i + 1) + ". " + rotuloCriatura(inimigos, c) + " (Vida: " + c.getVida() + ")");
        }
        System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

        int escolha = Interface.lerInteiro();


        if (escolha < 1 || escolha > vivos.size()) return -1;

        return inimigos.indexOf(vivos.get(escolha - 1));
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

    // O companheiro está de pé (presente e com vida > 0)?
    public static boolean companheiroEmPe(FichaRpg ficha) {
        companheiros.Companheiro comp = ficha.getCompanheiro();
        return comp != null && comp.getFicha().getVidaPersonagem() > 0;
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

    // ==================== ESCOLHER ARMA ====================

    public static int EscolherArma(FichaRpg ficha) {
        List<Arma> armas = new ArrayList<>();
        List<Boolean> ehFlecha = new ArrayList<>();

        for (ItemRpg item : ficha.getInventario()) {
            if (item instanceof Arma) {
                Arma arma = (Arma) item;
                if (arma.getTipoArma().contains("LA")) {
                    if (temFlechas(ficha)) {
                        armas.add(arma);
                        ehFlecha.add(true);
                    }
                } else {
                    armas.add(arma);
                    ehFlecha.add(false);
                }
            }
        }

        String socoNome = "Soco";
        int socoDado = 4;
        int socoQtd = 1;
        if (ficha.getClasseDoPersonagem() != null && ficha.getClasseDoPersonagem().getAtaqueDesarmado() != null) {
            Arma soco = ficha.getClasseDoPersonagem().getAtaqueDesarmado();
            socoNome = soco.getNome();
            socoDado = soco.getDadoDanoArma();
            socoQtd = soco.getQuantidadeDanoArma();
        }

        Interface.cabecalhoMenu("ESCOLHA SUA ARMA");
        System.out.println("\n");
        for (int i = 0; i < armas.size(); i++) {
            Arma arma = armas.get(i);
            String extra = ehFlecha.get(i) ? " (Flechas: " + getQtdFlechas(ficha) + ")" : "";
            String atributoMostrado = arma.isAgil() ? "Ágil (Força/Destreza)" : arma.getAtributoAtaque();
            System.out.println("  " + (i + 1) + ". " + CIANO + arma.getNome() + RESET + " (" + arma.getQuantidadeDanoArma() + "d" + arma.getDadoDanoArma() + " - " + arma.getTipoArma() + " - " + atributoMostrado + ")" + extra);
        }
        System.out.println("  " + (armas.size() + 1) + ". " + CIANO + socoNome + RESET + " (" + socoQtd + "d" + socoDado + " - CaC - Força)");
        System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

        int escolha = Interface.lerInteiro();


        if (escolha == 0) return -1;
        if (escolha < 1 || escolha > armas.size() + 1) return -1;

        if (escolha <= armas.size()) {
            return ficha.getInventario().indexOf(armas.get(escolha - 1));
        } else {
            return -2;
        }
    }

    public static boolean temFlechas(FichaRpg ficha) {
        for (ItemRpg item : ficha.getInventario()) {
            if (item.getNome().equals("Flechas") && item.getQuantidade() > 0) {
                return true;
            }
        }
        return false;
    }

    public static int getQtdFlechas(FichaRpg ficha) {
        for (ItemRpg item : ficha.getInventario()) {
            if (item.getNome().equals("Flechas")) {
                return item.getQuantidade();
            }
        }
        return 0;
    }

    public static void consumirFlecha(FichaRpg ficha) {
        for (int i = 0; i < ficha.getInventario().size(); i++) {
            ItemRpg item = ficha.getInventario().get(i);
            if (item.getNome().equals("Flechas")) {
                ficha.consumirItem(item, 1);
                Interface.MostrarMensagem("-> Flecha utilizada! Restam " + item.getQuantidade() + " flechas.");
                if (item.getQuantidade() <= 0) {
                    ficha.getInventario().remove(i);
                    Interface.MostrarMensagem("-> Suas flechas acabaram!");
                }
                Interface.Pausa(1000);
                return;
            }
        }
    }

    // ==================== EXECUTAR ATAQUE ====================

    public static boolean executarAtaqueComArma(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, int armaIndex) {
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return false;
        Criatura inimigo = inimigos.get(alvoIndex);

        // Defesa Absoluta do Guerreiro: o bônus some ao realizar um ataque
        if (ficha.isDefesaAbsolutaAtiva()) {
            ficha.setDefesaAbsolutaAtiva(false);
            Interface.MostrarMensagem("(Sua Defesa Absoluta se dissipa ao atacar!)");
            Interface.Pausa(1500);
        }

        // Curandeiro Combatente (Healer): cada 1 de mana compra 1 ataque extra (máx = nível)
        int ataquesExtras = comprarAtaquesExtrasCurandeiro(ficha, inimigo);
        if (ataquesExtras > 0) {
            Interface.MostrarMensagem("\nCurandeiro Combatente! Você gasta " + ataquesExtras + " de mana e executa " + ataquesExtras + " ataque(s) extra(s).");
            Interface.Pausa(1500);
        }

        boolean acertou = false;
        for (int ataque = 0; ataque <= ataquesExtras; ataque++) {
            if (inimigo.getVida() <= 0) break;
            int danoCausado = ataqueComArmaUnico(ficha, inimigos, alvoIndex, armaIndex, ataque > 0);
            if (danoCausado > 0) {
                acertou = true;
                curarCurandeiroNoGolpe(ficha, danoCausado);
            }
        }

        // Espada do Minotauro (Guerreiro): 30% de chance de atacar de novo após o golpe
        if (armaIndex >= 0 && armaIndex < ficha.getInventario().size()
                && ficha.getInventario().get(armaIndex) instanceof Arma
                && ((Arma) ficha.getInventario().get(armaIndex)).getNome().equals("Espada do Minotauro")
                && inimigo.getVida() > 0
                && MecanicasRpg.rolarDado(100) <= 30) {
            Interface.MostrarMensagem("\nA Espada do Minotauro volta com fúria total! Você ataca de novo!");
            Interface.Pausa(1500);
            int danoCausado = ataqueComArmaUnico(ficha, inimigos, alvoIndex, armaIndex, true);
            if (danoCausado > 0) {
                acertou = true;
                curarCurandeiroNoGolpe(ficha, danoCausado);
            }
        }

        return acertou;
    }

    // Curandeiro Combatente (Healer): oferece comprar ataques extras gastando mana
    // (1 de mana por ataque, máximo = nível). O acerto de cada golpe cura metade do dano.
    private static int comprarAtaquesExtrasCurandeiro(FichaRpg ficha, Criatura inimigo) {
        if (!(ficha.getClasseDoPersonagem() instanceof classes.Healer)) return 0;
        if (!temHabilidade(ficha, "Curandeiro Combatente")) return 0;
        if (ficha.getManaPersonagem() < 1 || inimigo.getVida() <= 0) return 0;
        int maxExtra = Math.min(ficha.getNivel(), ficha.getManaPersonagem());
        System.out.println("\n  Curandeiro Combatente: cada 1 de mana compra 1 ataque extra (todo acerto cura metade do dano).");
        System.out.println("  Ataques extras possíveis: 0 a " + maxExtra + ".");
        System.out.println("  Escolha uma opção:");
        int extra = Interface.lerOpcao(0, maxExtra);
        ficha.setManaPersonagem(ficha.getManaPersonagem() - extra);
        return extra;
    }

    // Curandeiro Combatente (Healer): cada ataque que acerta cura metade do dano causado
    private static void curarCurandeiroNoGolpe(FichaRpg ficha, int danoCausado) {
        if (!(ficha.getClasseDoPersonagem() instanceof classes.Healer)) return;
        if (!temHabilidade(ficha, "Curandeiro Combatente")) return;
        int cura = danoCausado / 2;
        if (cura <= 0) return;
        ficha.setVidaPersonagem(Math.min(ficha.getVidaMaxima(), ficha.getVidaPersonagem() + cura));
        Interface.MostrarMensagem("(Curandeiro Combatente! O golpe acerta e você se cura " + cura + " de vida.)");
        Interface.Pausa(1500);
    }

    // Executa UM ataque com arma (ou soco) e devolve o dano causado (0 se errou/inválido)
    private static int ataqueComArmaUnico(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, int armaIndex, boolean golpeExtra) {
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return 0;
        Criatura inimigo = inimigos.get(alvoIndex);

        if (golpeExtra) {
            Interface.MostrarMensagem("\nVocê encadeia um novo golpe contra " + rotuloCriatura(inimigos, inimigo) + "...");
            Interface.Pausa(1200);
        } else {
            Interface.MostrarMensagem("\nVocê prepara seu ataque contra " + rotuloCriatura(inimigos, inimigo) + "...");
            Interface.Pausa(1500);
        }

        int dadoAtaque, totalAtaque, dano = 0;
        int atributoBonus;
        String nomeAtributo;

        if (armaIndex == -2) {
            String socoNome = "Soco";
            int socoDado = 4;
            int socoQtd = 1;
            if (ficha.getClasseDoPersonagem() != null && ficha.getClasseDoPersonagem().getAtaqueDesarmado() != null) {
                Arma soco = ficha.getClasseDoPersonagem().getAtaqueDesarmado();
                socoNome = soco.getNome();
                socoDado = soco.getDadoDanoArma();
                socoQtd = soco.getQuantidadeDanoArma();
            }
            atributoBonus = ficha.getForca();
            nomeAtributo = "Força";

            Interface.pressionarParaRolar();
            dadoAtaque = MecanicasRpg.rolarDado(20);
            totalAtaque = dadoAtaque + atributoBonus;
            boolean critico = dadoAtaque == 20;
            Interface.MostrarMensagem("-> Ataque [" + socoNome + "]: " + dadoAtaque + " (Dado) + " + atributoBonus + " (" + nomeAtributo + ") = " + totalAtaque + (critico ? " [CRÍTICO!]" : ""));
            if (critico) {
                Interface.MostrarMensagem("Golpe crítico! O dano de dados será dobrado!");
            }
            Interface.Pausa(2000);

            if (critico || totalAtaque >= inimigo.getDefesa()) {
                Interface.MostrarMensagem("-> Acertou! (defesa do alvo: " + inimigo.getDefesa() + ")" + (critico ? " CRÍTICO sempre acerta." : ""));
                Interface.Pausa(1500);

                int dadosTotais = socoQtd * (critico ? 2 : 1);
                boolean semiDeusBonus = ficha.isSemiDeusAtivo();
                if (semiDeusBonus) {
                    dadosTotais += 4;
                    Interface.MostrarMensagem("(Semi Deus! +4 dados de dano)");
                    Interface.Pausa(1000);
                }
                StringBuilder roladas = new StringBuilder();
                Interface.pressionarParaRolar();
                for (int i = 0; i < dadosTotais; i++) {
                    int dado = MecanicasRpg.rolarDado(socoDado);
                    dano += dado;
                    if (roladas.length() > 0) roladas.append(" + ");
                    roladas.append(dado);
                }
                dano += atributoBonus;
                Interface.MostrarMensagem("-> Dados Rolados: " + roladas + " = " + dano + " (Dano: " + dadosTotais + "d" + socoDado + " + " + nomeAtributo + ": " + atributoBonus + ")");
                Interface.Pausa(2000);
            } else {
                Interface.MostrarMensagem("-> Errou! (defesa do alvo: " + inimigo.getDefesa() + ")");
                Interface.Pausa(1500);
            }
        } else if (armaIndex >= 0 && armaIndex < ficha.getInventario().size()) {
            Arma armaEscolhida = (Arma) ficha.getInventario().get(armaIndex);
            String atributo = armaEscolhida.getAtributoAtaque();
            if (armaEscolhida.isAgil()) {
                if (ficha.getForca() >= ficha.getDestreza()) {
                    atributoBonus = ficha.getForca();
                    nomeAtributo = "Força";
                } else {
                    atributoBonus = ficha.getDestreza();
                    nomeAtributo = "Destreza";
                }
            } else {
                atributoBonus = atributo.equals("Destreza") ? ficha.getDestreza() : ficha.getForca();
                nomeAtributo = atributo;
            }

            Interface.pressionarParaRolar();
            dadoAtaque = MecanicasRpg.rolarDado(20);
            totalAtaque = dadoAtaque + atributoBonus;
            boolean critico = dadoAtaque == 20;
            Interface.MostrarMensagem("-> Ataque [" + armaEscolhida.getNome() + "]: " + dadoAtaque + " (Dado) + " + atributoBonus + " (" + nomeAtributo + ") = " + totalAtaque + (critico ? " [CRÍTICO!]" : ""));
            if (critico) {
                Interface.MostrarMensagem("Golpe crítico! O dano de dados será dobrado!");
            }
            Interface.Pausa(2000);

            if (critico || totalAtaque >= inimigo.getDefesa()) {
                Interface.MostrarMensagem("-> Acertou! (defesa do alvo: " + inimigo.getDefesa() + ")" + (critico ? " CRÍTICO sempre acerta." : ""));
                Interface.Pausa(1500);

                int dadosTotais = armaEscolhida.getQuantidadeDanoArma() * (critico ? 2 : 1);
                boolean semiDeusBonus = ficha.isSemiDeusAtivo() && armaEscolhida.getTipoArma().contains("CaC");
                if (semiDeusBonus) {
                    dadosTotais += 4;
                    Interface.MostrarMensagem("(Semi Deus! +4 dados de dano)");
                    Interface.Pausa(1000);
                }
                StringBuilder roladas = new StringBuilder();
                Interface.pressionarParaRolar();
                for (int i = 0; i < dadosTotais; i++) {
                    int dado = MecanicasRpg.rolarDado(armaEscolhida.getDadoDanoArma());
                    dano += dado;
                    if (roladas.length() > 0) roladas.append(" + ");
                    roladas.append(dado);
                }
                dano += atributoBonus;
                Interface.MostrarMensagem("-> Dados Rolados: " + roladas + " = " + dano + " (Dano: " + dadosTotais + "d" + armaEscolhida.getDadoDanoArma() + " + " + nomeAtributo + ": " + atributoBonus + ")");
                Interface.Pausa(2000);

                // Espada Majestral: banhada em ouro e magia, causa +1d4 de dano de luz
                // e o dobro do dano total contra mortos-vivos
                if (armaEscolhida.getNome().equals("Espada Majestral")) {
                    int dadosLuz = critico ? 2 : 1;
                    int luz = 0;
                    for (int i = 0; i < dadosLuz; i++) {
                        luz += MecanicasRpg.rolarDado(4);
                    }
                    dano += luz;
                    Interface.MostrarMensagem("(Espada Majestral! +" + luz + " de dano de luz" + (dadosLuz > 1 ? " (crítico)" : "") + ")");
                    Interface.Pausa(1500);
                    if (inimigo.isMortoVivo()) {
                        dano *= 2;
                        Interface.MostrarMensagem("(Espada Majestral! DANO DOBRADO contra " + inimigo.getNome() + ", um morto-vivo)");
                        Interface.Pausa(1500);
                    }
                }
            } else {
                Interface.MostrarMensagem("-> Errou! (defesa do alvo: " + inimigo.getDefesa() + ")");
                Interface.Pausa(1500);
            }

            if (armaEscolhida.getTipoArma().contains("LA")) {
                consumirFlecha(ficha);
            }
        } else {
            return 0;
        }

        if (dano > 0) {
            if (ficha.isEspadaAfiadaAtiva() && armaIndex >= 0) {
                int bonusAfiada = MecanicasRpg.rolarDado(8) + MecanicasRpg.rolarDado(8);
                dano += bonusAfiada;
                Interface.MostrarMensagem("(Espada Afiada! +" + bonusAfiada + " de dano)");
                Interface.Pausa(1500);
            }
            if (ficha.getRaca() != null && ficha.getRaca().temBonusDanoVidaBaixa()
                    && ficha.getVidaPersonagem() <= ficha.getVidaMaxima() * 0.30) {
                dano += 2;
                Interface.MostrarMensagem("(Fúria Sombria! Com a vida baixa, você golpeia com +2 de dano)");
                Interface.Pausa(1500);
            }
            aplicarDanoCriatura(inimigo, dano);
            Interface.MostrarMensagem(rotuloCriatura(inimigos, inimigo) + " agora tem " + Math.max(0, inimigo.getVida()) + " de vida.");
            Interface.Pausa(2000);
        }

        aplicarVenenoCuraParaMorte(ficha, inimigos, alvoIndex);
        return dano;
    }

    // ==================== HABILIDADES ====================

    public static int EscolherHabilidadeAtiva(FichaRpg ficha) {
        List<habilidades.Habilidade> ativas = new ArrayList<>();
        for (habilidades.Habilidade hab : ficha.getHabilidades()) {
            if (!hab.isPassiva()
                    && !hab.getNome().equals("Cura Reforçada")
                    && !hab.getNome().equals("Magia Proibida")
                    && !(hab.getNome().equals("Pacto Mortal") && !ficha.temItem("Olho Demoníaco"))) {
                ativas.add(hab);
            }
        }

        if (ativas.isEmpty()) {
            Interface.MostrarMensagem("\nVocê não possui habilidades ativas.");
            Interface.Pausa(1500);
            return -1;
        }

        Interface.cabecalhoMenu("SUAS HABILIDADES");
        System.out.println("\n");
        for (int i = 0; i < ativas.size(); i++) {
            habilidades.Habilidade hab = ativas.get(i);
            String extra = "";
            if (hab instanceof habilidades.Magia) {
                habilidades.Magia magia = (habilidades.Magia) hab;
                if (magia.getDadoDano() > 0) {
                    extra = " - Dano: " + magia.getQuantidadeDano() + "d" + magia.getDadoDano();
                }
            }
            System.out.println("  " + (i + 1) + ". " + CIANO + hab.getNome() + RESET + " (Custo: " + (custoEfetivoMagia(ficha, hab) == 0 ? "Grátis" : custoEfetivoMagia(ficha, hab) + " Mana") + ")" + extra);
        }
        System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

        int escolha = Interface.lerInteiro();


        if (escolha == 0) return -1;

        if (escolha < 1 || escolha > ativas.size()) {
            Interface.ExibirErro("Escolha inválida!");
            Interface.Pausa(1500);
            return -1;
        }

        habilidades.Habilidade habEscolhida = ativas.get(escolha - 1);

        // Magias do Mago só podem ser usadas se o personagem tiver um Cajado
        if (habEscolhida instanceof habilidades.Magia
                && ficha.getClasseDoPersonagem() instanceof classes.Mago
                && !ficha.temItem("Cajado")
                && !ficha.temItem("Cajado de Sangue")) {
            Interface.ExibirErro("Você precisa de um Cajado para usar suas magias!");
            Interface.Pausa(1500);
            return -1;
        }

        if (ficha.getManaPersonagem() < custoEfetivoMagia(ficha, habEscolhida)) {
            Interface.ExibirErro("Mana insuficiente! Precisa de " + custoEfetivoMagia(ficha, habEscolhida) + " de mana.");
            Interface.Pausa(1500);
            return -1;
        }

        return ficha.getHabilidades().indexOf(habEscolhida);
    }

    // Custo de mana efetivo de uma habilidade/magia.
    // O Pequeno Grimório reduz 1 no custo das magias pagas; a Meio-Fada (Encanto
    // Feérico) reduz 1 no custo de magias e habilidades. O custo nunca pode zerar:
    // o mínimo é 1, a não ser que a própria habilidade tenha custo definido como 0.
    public static int custoEfetivoMagia(FichaRpg ficha, habilidades.Habilidade hab) {
        int custo = hab.getCustoMana();
        if (custo <= 0) return 0;

        boolean meioFada = ficha.getRaca() != null && ficha.getRaca().reduzCustoMana();
        boolean pequenoGrimorio = hab instanceof habilidades.Magia && ficha.temItem("Pequeno Grimório");

        if (meioFada) custo--;
        if (pequenoGrimorio) custo--;
        return Math.max(1, custo);
    }

    public static boolean executarHabilidadeEscolhida(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, int habilidadeIndex) {
        if (habilidadeIndex < 0 || habilidadeIndex >= ficha.getHabilidades().size()) return true;

        habilidades.Habilidade hab = ficha.getHabilidades().get(habilidadeIndex);

        if (ficha.getManaPersonagem() < custoEfetivoMagia(ficha, hab)) {
            telas.Interface.ExibirErro("Mana insuficiente!");
            telas.Interface.Pausa(1500);
            return true;
        }

        return hab.executar(ficha, inimigos, alvoIndex);
    }

    public static boolean executarGiro(FichaRpg ficha, List<Criatura> inimigos) {
        int maxGiros = Math.max(1, ficha.getDestreza());
        System.out.println("\nVocê usa Giro! Quantos giros quer dar? (Custo: 1 de mana por giro)");
        System.out.println("Máximo de giros: " + maxGiros + " (sua Destreza)");

        int giros = Interface.lerInteiro();

        if (giros < 1 || giros > maxGiros) {
            Interface.ExibirErro("Número de giros inválido!");
            Interface.Pausa(1500);
            return true;
        }
        if (ficha.getManaPersonagem() < giros) {
            Interface.ExibirErro("Mana insuficiente para " + giros + " giros!");
            Interface.Pausa(1500);
            return true;
        }

        ficha.setManaPersonagem(ficha.getManaPersonagem() - giros);
        Interface.MostrarMensagem("\nVocê gira " + giros + "x com sua espada!");
        Interface.Pausa(1500);

        List<Criatura> vivos = inimigosVivos(inimigos);
        if (vivos.isEmpty()) return true;

        int dadosPorGiro = 1;
        if (ficha.isMagiaBonusAtivo()) {
            dadosPorGiro++;
            Interface.MostrarMensagem("(Mesa de Magias! +1 dado de dano em cada giro)");
            Interface.Pausa(1000);
        }

        for (int g = 1; g <= giros; g++) {
            int danoGiro = ficha.getForca();
            StringBuilder roladas = new StringBuilder();
            for (int i = 0; i < dadosPorGiro; i++) {
                int dadoGiro = MecanicasRpg.rolarDado(10);
                danoGiro += dadoGiro;
                if (roladas.length() > 0) roladas.append(" + ");
                roladas.append(dadoGiro);
            }
            Interface.MostrarMensagem("-> Giro " + g + ": " + roladas + " (1d10" + (dadosPorGiro > 1 ? " + 1d10 (Mesa de Magias)" : "") + ") + " + ficha.getForca() + " (Força) = " + danoGiro + " de dano em área!");
            Interface.Pausa(1500);
            for (Criatura alvo : vivos) {
                if (alvo.getVida() <= 0) continue;
                aplicarDanoCriatura(alvo, danoGiro);
                Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
            }
            Interface.Pausa(1500);
        }
        return true;
    }

    // Estrondo do Guerreiro: 5 de mana, 7d10 em área e não pode usar habilidades no próximo turno
    public static boolean executarEstrondo(FichaRpg ficha, List<Criatura> inimigos, habilidades.Habilidade hab) {
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        ficha.setRodadasSemHabilidade(2);
        Interface.MostrarMensagem("\nVocê golpeia o chão com toda a sua força! A terra se ergue ao seu redor!");
        Interface.Pausa(1500);

        List<Criatura> vivos = inimigosVivos(inimigos);
        if (vivos.isEmpty()) return true;

        int totalDados = 7;
        if (ficha.isMagiaBonusAtivo()) {
            totalDados++;
            Interface.MostrarMensagem("(Mesa de Magias! +1 dado de dano)");
            Interface.Pausa(1000);
        }

        int dano = 0;
        Interface.pressionarParaRolar();
        for (int i = 0; i < totalDados; i++) {
            dano += MecanicasRpg.rolarDado(10);
        }
        dano += ficha.getForca();
        Interface.MostrarMensagem("-> Estrondo: " + totalDados + "d10 + " + ficha.getForca() + " (Força) = " + dano + " de dano em área!");
        Interface.Pausa(1500);

        for (Criatura alvo : vivos) {
            if (alvo.getVida() <= 0) continue;
            aplicarDanoCriatura(alvo, dano);
            Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
        }
        Interface.MostrarMensagem("Você não poderá usar habilidades no próximo turno!");
        Interface.Pausa(1500);
        return true;
    }

    // Prisão do Mago: prende um inimigo até ele passar em um teste de d20 (15+) na vez dele
    public static boolean usarPrisao(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, habilidades.Habilidade hab) {
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) {
            Interface.MostrarMensagem("Nenhum alvo escolhido.");
            Interface.Pausa(1500);
            return true;
        }
        Criatura alvo = inimigos.get(alvoIndex);
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        ficha.setPrisaoAtiva(alvo);
        Interface.MostrarMensagem("\nVocê prende " + rotuloCriatura(inimigos, alvo) + " em uma prisão de energia!");
        Interface.MostrarMensagem("Na vez dele, ele precisa tirar 15 ou mais em um d20 para se libertar.");
        Interface.Pausa(2000);
        return true;
    }

    // Semi Deus do Guerreiro (lvl 9): gasta TODA a mana, +50% de vida máxima, cura total e +4 dados CaC
    public static boolean executarSemiDeus(FichaRpg ficha, habilidades.Habilidade hab) {
        if (ficha.isSemiDeusAtivo()) {
            Interface.MostrarMensagem("Você já está em forma de semi-deus!");
            Interface.Pausa(1500);
            return true;
        }
        if (ficha.getManaPersonagem() <= 0) {
            Interface.ExibirErro("Sem mana para ativar a forma de semi-deus!");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setSemiDeusVidaOriginalMax(ficha.getVidaMaxima());
        int bonus = ficha.getVidaMaxima() / 2;
        ficha.setVidaMaxima(ficha.getVidaMaxima() + bonus);
        ficha.setVidaPersonagem(ficha.getVidaMaxima());
        ficha.setManaPersonagem(0);
        ficha.setSemiDeusAtivo(true);
        Interface.MostrarMensagem("\nVocê desperta seu poder divino! Toda a sua mana se converte em força vital!");
        Interface.MostrarMensagem("Vida máxima aumentada para " + ficha.getVidaMaxima() + " e vida totalmente recuperada!");
        Interface.MostrarMensagem("Seus ataques corpo a corpo ganham +4 dados de dano.");
        Interface.Pausa(2500);
        return true;
    }

    // Poder Absoluto do Mago (lvl 9): 15 de mana, todas as magias dobram os dados até o fim do combate
    public static boolean executarPoderAbsoluto(FichaRpg ficha, habilidades.Habilidade hab) {
        if (ficha.isPoderAbsolutoAtivo()) {
            Interface.MostrarMensagem("O Poder Absoluto já está ativo!");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        ficha.setPoderAbsolutoAtivo(true);
        Interface.MostrarMensagem("\nVocê se envolve na energia do seu elemento! Suas magias dobram de poder!");
        Interface.Pausa(2000);
        return true;
    }

    // Cura Absoluta do Healer (lvl 9): 10 de mana, cura total e vida bônus (dobra a vida, gasta-se primeiro)
    public static boolean executarCuraAbsoluta(FichaRpg ficha, habilidades.Habilidade hab) {
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        if (ficha.getCuraAbsolutaBonus() == 0) {
            ficha.setCuraAbsolutaVidaOriginalMax(ficha.getVidaMaxima());
        }
        ficha.setVidaPersonagem(ficha.getVidaMaxima());
        ficha.setCuraAbsolutaBonus(ficha.getVidaMaxima());
        Interface.MostrarMensagem("\nVocê injeta o líquido absoluto! Vida totalmente recuperada e uma proteção de +" + ficha.getCuraAbsolutaBonus() + " de vida!");
        Interface.MostrarMensagem("A proteção é gasta primeiro, antes da sua vida real.");
        Interface.Pausa(2500);
        return true;
    }

    // Cura Incessante (Guerreiro lvl 10): cura toda a vida, uma vez por combate
    public static boolean executarCuraIncessante(FichaRpg ficha) {
        if (ficha.isCuraIncessanteUsada()) {
            Interface.MostrarMensagem("A Cura Incessante só pode ser usada uma vez por combate!");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setCuraIncessanteUsada(true);
        ficha.setVidaPersonagem(ficha.getVidaMaxima());
        Interface.MostrarMensagem("\nSua força divina flui por todo o seu corpo! Vida totalmente recuperada!");
        Interface.Pausa(2000);
        return true;
    }

    // Explosão de Poder (Mago lvl 10): gasta mana escolhida, cada 2 de mana causa 2d12 em TODOS os inimigos
    public static boolean executarExplosaoDePoder(FichaRpg ficha, List<Criatura> inimigos) {
        if (ficha.getManaPersonagem() < 2) {
            Interface.MostrarMensagem("Você precisa de pelo menos 2 de mana para a Explosão de Poder.");
            Interface.Pausa(1500);
            return true;
        }

        int maximoGasto = ficha.getManaPersonagem();
        System.out.println("\nVocê canaliza toda a sua energia do elemento!");
        System.out.println("Quanto de mana quer gastar? (cada 2 de mana = 2d12 de dano em todos os inimigos)");
        System.out.println("Mínimo: 2 | Máximo: " + maximoGasto);

        int gasto = Interface.lerInteiro();
        gasto = Math.max(2, Math.min(gasto, maximoGasto));
        gasto -= gasto % 2;

        ficha.setManaPersonagem(ficha.getManaPersonagem() - gasto);
        int pares = gasto / 2;
        int totalDados = pares * 2;
        if (ficha.isMagiaBonusAtivo()) {
            totalDados++;
            Interface.MostrarMensagem("(Mesa de Magias! +1 dado de dano)");
            Interface.Pausa(1000);
        }

        String elemento = "místico";
        for (habilidades.Habilidade h : ficha.getHabilidades()) {
            if (h instanceof habilidades.Magia) {
                String nome = h.getNome();
                int ini = nome.indexOf('(');
                int fim = nome.indexOf(')');
                if (ini >= 0 && fim > ini) {
                    elemento = nome.substring(ini + 1, fim).trim().toLowerCase();
                    break;
                }
            }
        }

        Interface.MostrarMensagem("\nVocê libera a Explosão de Poder! " + gasto + " de mana se convertem em " + totalDados + "d12 de dano de " + elemento + "!");
        Interface.Pausa(2000);

        int dano = 0;
        Interface.pressionarParaRolar();
        for (int i = 0; i < totalDados; i++) {
            dano += MecanicasRpg.rolarDado(12);
        }
        Interface.MostrarMensagem("-> Dados Rolados: " + totalDados + "d12 = " + dano + " de dano em TODOS os inimigos!");
        Interface.Pausa(2000);

        List<Criatura> vivos = inimigosVivos(inimigos);
        for (Criatura alvo : vivos) {
            aplicarDanoCriatura(alvo, dano);
            Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
            Interface.Pausa(1500);
        }

        return true;
    }

    // Conhecimento Avassalador do Healer: teste de Intelecto (DC 15) revela as informações dos monstros
    public static boolean tentarConhecimentoAvassalador(FichaRpg ficha, List<Criatura> inimigos) {
        Interface.MostrarMensagem("\nVocê canaliza todo o seu conhecimento sobre as criaturas...");
        Interface.Pausa(1500);

        Interface.pressionarParaTeste("Intelecto");
        int dado = MecanicasRpg.rolarDado(20);
        int total = dado + ficha.getIntelectoTeste();
        Interface.MostrarMensagem("-> Teste de Intelecto: " + dado + " (Dado) + " + ficha.getIntelectoTeste() + " (Intelecto) = " + total + " (Dificuldade: 15)");
        Interface.Pausa(1500);

        if (total < 15 && ficha.podeUsarMenteAfiada()) {
            Interface.MostrarMensagem("\n(Mente Afiada!) Sua mente aguçada reavalia as criaturas... Deseja rolar novamente?");
            if (Interface.lerOpcao(2) == 1) {
                ficha.marcarMenteAfiadaUsada();
                dado = MecanicasRpg.rolarDado(20);
                total = dado + ficha.getIntelectoTeste();
                Interface.MostrarMensagem("-> Nova tentativa (Intelecto): " + dado + " (Dado) + " + ficha.getIntelectoTeste() + " (Intelecto) = " + total + " (Dificuldade: 15)");
                Interface.Pausa(1500);
            }
        }

        if (total < 15) {
            Interface.MostrarMensagem("As mentes das criaturas são densas demais... Você não encontrou nada útil.");
            Interface.Pausa(1500);
            return true;
        }

        Interface.MostrarMensagem("\nVocê compreende tudo sobre seus inimigos!");
        for (Criatura c : inimigosVivos(inimigos)) {
            StringBuilder ataques = new StringBuilder();
            for (criaturas.Criatura.Ataque a : c.getAtaques()) {
                if (ataques.length() > 0) ataques.append("; ");
                ataques.append(a.nome).append(" (").append(a.qtdDado).append("d").append(a.ladosDado).append(")");
            }
            Interface.MostrarMensagem("-> " + rotuloCriatura(inimigos, c) + ": Vida " + c.getVida() + " | Defesa " + c.getDefesa()
                    + " | Iniciativa " + c.getIniciativa() + " | Ataques: " + ataques + " | XP " + c.getXpGanho());
        }
        Interface.Pausa(2000);
        return true;
    }

    // Proteção Absoluta do Mago: +3 de defesa e reflexo de 2d8 do elemento enquanto acertado
    public static boolean usarProtecaoAbsoluta(FichaRpg ficha, habilidades.Habilidade hab) {
        if (ficha.isProtecaoAbsolutaAtiva()) {
            Interface.MostrarMensagem("A Proteção Absoluta já está ativa!");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        ficha.setProtecaoAbsolutaAtiva(true);
        ficha.setBonusDefesaTemporario(ficha.getBonusDefesaTemporario() + 3);
        Interface.MostrarMensagem("\nVocê se envolve no seu elemento! +3 de defesa e reflete 2d8 de dano a quem te acertar.");
        Interface.Pausa(2000);
        return true;
    }

    // Cura para a Morte do Healer: injeta líquido mortal (ativa a partir do próximo turno)
    public static boolean usarCuraParaMorte(FichaRpg ficha, List<Criatura> inimigos, habilidades.Habilidade hab) {
        int alvoVeneno = escolherAlvo(inimigos);
        if (alvoVeneno < 0) {
            Interface.MostrarMensagem("Nenhum alvo escolhido.");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        Criatura alvo = inimigos.get(alvoVeneno);
        ficha.setAlvoCuraParaMorte(alvo);
        ficha.setCuraParaMortePreparado(true);
        Interface.MostrarMensagem("\nVocê injeta o líquido mortal em " + rotuloCriatura(inimigos, alvo) + "! Ele age a partir do próximo turno.");
        Interface.Pausa(2000);
        return true;
    }

    // Aplica o dano do líquido mortal ao final de cada ataque contra o alvo envenenado
    public static void aplicarVenenoCuraParaMorte(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex) {
        if (!ficha.isCuraParaMorteAtivo() || ficha.getAlvoCuraParaMorte() == null) return;
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return;

        Criatura alvo = inimigos.get(alvoIndex);
        if (alvo.getVida() <= 0 || alvo != ficha.getAlvoCuraParaMorte()) return;

        int veneno = MecanicasRpg.rolarDado(8) + MecanicasRpg.rolarDado(8) + MecanicasRpg.rolarDado(8);
        aplicarDanoCriatura(alvo, veneno);
        Interface.MostrarMensagem("(Cura para a Morte! O líquido mortal causa " + veneno + " de dano)");
        Interface.Pausa(1500);
    }

    // Tenta reviver o personagem com Cura Total (uma vez por combate)
    public static boolean tentarReviver(FichaRpg ficha) {
        if (ficha.getVidaPersonagem() > 0) return true;
        if (ficha.isCuraTotalUsada() || ficha.getManaPersonagem() < 10) return false;
        if (!temHabilidade(ficha, "Cura Total")) return false;

        System.out.println("\nVocê foi derrubado! Deseja usar Cura Total (10 de mana) para reviver com a vida cheia?");
        System.out.println("1. Sim");
        System.out.println("2. Não");
        int escolha = Interface.lerInteiro();

        if (escolha != 1) return false;

        ficha.setManaPersonagem(ficha.getManaPersonagem() - 10);
        ficha.setVidaPersonagem(ficha.getVidaMaxima());
        ficha.setCuraTotalUsada(true);
        Interface.MostrarMensagem("\nCura Total! Você renasce com a vida cheia!");
        Interface.Pausa(2500);
        return true;
    }

    // ==================== MOCHILA ====================

    public static boolean ehItemConsumivel(ItemRpg item) {
        if (!(item instanceof Consumivel)) return false;
        return !item.getNome().equals("Flechas");
    }

    // Uso de consumíveis fora de combate (pela ficha/inventário).
    // Retorna verdadeiro se o item foi usado (consome a quantidade escolhida).
    public static boolean usarItemForaDeCombate(FichaRpg ficha, ItemRpg item, int quantidade) {
        if (item == null || !ehItemConsumivel(item)) return false;
        int qtd = Math.min(Math.max(1, quantidade), item.getQuantidade());
        String nome = item.getNome();

        switch (nome) {
            case "Frutas": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(2);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                int curaReal = ficha.getVidaPersonagem() - antes;
                Interface.MostrarMensagem("Você comeu " + qtd + "x Frutas e recuperou " + curaReal + " de vida! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                break;
            }
            case "Poção de Mana": {
                int antes = ficha.getManaPersonagem();
                ficha.setManaPersonagem(Math.min(ficha.getManaPersonagem() + 5 * qtd, ficha.getManaMaxima()));
                int curaMana = ficha.getManaPersonagem() - antes;
                Interface.MostrarMensagem("Você bebeu " + qtd + "x Poção de Mana e recuperou " + curaMana + " de mana! Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
                break;
            }
            case "Poção Grande de Mana": {
                int antes = ficha.getManaPersonagem();
                ficha.setManaPersonagem(Math.min(ficha.getManaPersonagem() + 7 * qtd, ficha.getManaMaxima()));
                int curaMana = ficha.getManaPersonagem() - antes;
                Interface.MostrarMensagem("Você bebeu " + qtd + "x Poção Grande de Mana e recuperou " + curaMana + " de mana! Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
                break;
            }
            case "Kit Médico": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(4);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                int curaReal = ficha.getVidaPersonagem() - antes;
                Interface.MostrarMensagem("Você usou o Kit Médico e recuperou " + curaReal + " de vida! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                if (ficha.isInfectado()) {
                    ficha.setInfectado(false);
                    Interface.MostrarMensagem("Os curativos do Kit Médico expulsam a infecção! Você está curado.");
                }
                break;
            }
            default:
                return false;
        }

        ficha.consumirItem(item, qtd);
        if (item.getQuantidade() <= 0) {
            ficha.getInventario().remove(item);
            Interface.MostrarMensagem("O item foi consumido e removido do inventário.");
        } else {
            Interface.MostrarMensagem("Restam " + item.getQuantidade() + "x " + item.getNome() + ".");
        }
        Interface.Pausa(1500);
        return true;
    }

    // Fase de declaração da mochila: escolhe e confirma o item (sem aplicar ainda).
    // Retorna o índice do item, -1 para voltar ao menu principal ou -2 para declarar fuga.
    public static int escolherItemParaUsar(FichaRpg ficha) {
        while (true) {
            Interface.cabecalhoMenu("SUA MOCHILA");
            System.out.println("\n");

            if (ficha.getInventario().isEmpty()) {
                System.out.println("  Sua mochila está vazia.");
            } else {
                for (int i = 0; i < ficha.getInventario().size(); i++) {
                    ItemRpg item = ficha.getInventario().get(i);
                    String tipo = "";
                    if (ehItemConsumivel(item)) tipo = " [Consumível]";
                    else if (item instanceof Arma) tipo = " [Arma]";
                    else if (item.getNome().equals("Flechas")) tipo = " [Munição]";
                    System.out.println("  " + (i + 1) + ". " + CIANO + item.getNome() + RESET + " (x" + item.getQuantidade() + ")" + tipo);
                }
            }
            System.out.println("\n  " + VERDE + "0. Voltar ao combate" + RESET);
            System.out.println("  " + AMARELO + "9. Tentar fugir do combate" + RESET);

            int escolha = Interface.lerInteiro();

            if (escolha == 0) return -1;
            if (escolha == 9) return -2;

            if (ficha.getInventario().isEmpty()) {
                Interface.ExibirErro("Escolha inválida!");
                Interface.Pausa(1500);
                continue;
            }

            if (escolha > 0 && escolha <= ficha.getInventario().size()) {
                ItemRpg itemEscolhido = ficha.getInventario().get(escolha - 1);

                String descExibida = itemEscolhido.getDescricao();
                if (itemEscolhido.getNome().equals("Kit Médico")) {
                    descExibida = "Pode ser usado para curar 1d4 de vida e acaba com uma infecção. Usos restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Poção de Mana")) {
                    descExibida = "Restaura 5 pontos de mana. Usos restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Poção Grande de Mana")) {
                    descExibida = "Restaura 7 pontos de mana. Usos restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Frutas")) {
                    descExibida = "Cada fruta cura 1d2 de vida. Frutas restantes: " + itemEscolhido.getQuantidade();
                }
                System.out.println("\n" + itemEscolhido.getNome() + ": " + descExibida);
                Interface.Pausa(1000);

                if (ehItemConsumivel(itemEscolhido)) {
                    if (itemEscolhido.getNome().equals("Poção de Mana") && ficha.getManaPersonagem() >= ficha.getManaMaxima()) {
                        Interface.MostrarMensagem("Sua mana já está no máximo!");
                        Interface.Pausa(1500);
                        continue;
                    }
                    if (itemEscolhido.getNome().equals("Poção Grande de Mana") && ficha.getManaPersonagem() >= ficha.getManaMaxima()) {
                        Interface.MostrarMensagem("Sua mana já está no máximo!");
                        Interface.Pausa(1500);
                        continue;
                    }
                    if ((itemEscolhido.getNome().equals("Kit Médico") || itemEscolhido.getNome().equals("Frutas")) && ficha.getVidaPersonagem() >= ficha.getVidaMaxima()) {
                        Interface.MostrarMensagem("Sua vida já está no máximo!");
                        Interface.Pausa(1500);
                        continue;
                    }

                    System.out.println("\nDeseja usar este item? (Usará sua ação quando chegar sua vez)");
                    System.out.println("1. Sim");
                    System.out.println("2. Não");
                    int confirmar = Interface.lerInteiro();

                    if (confirmar == 1) {
                        return escolha - 1;
                    }
                } else {
                    Interface.MostrarMensagem("Item não é consumível. Apenas visualização.");
                    Interface.Pausa(1500);
                }
            }
        }
    }

    public static boolean temHabilidade(FichaRpg ficha, String nome) {
        for (habilidades.Habilidade hab : ficha.getHabilidades()) {
            if (hab.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    // Fase de resolução: aplica o item escolhido quando chega a vez do jogador na iniciativa
    public static void usarItemNaVez(FichaRpg ficha, int itemIndex) {
        if (itemIndex < 0 || itemIndex >= ficha.getInventario().size()) return;
        ItemRpg itemEscolhido = ficha.getInventario().get(itemIndex);

        if (!ehItemConsumivel(itemEscolhido)) {
            Interface.MostrarMensagem("Item não é consumível.");
            Interface.Pausa(1500);
            return;
        }

        if (itemEscolhido.getNome().equals("Poção de Mana")) {
            ficha.setManaPersonagem(ficha.getManaPersonagem() + 5);
            Interface.MostrarMensagem("Você recuperou 5 de mana! Mana atual: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
        } else if (itemEscolhido.getNome().equals("Poção Grande de Mana")) {
            ficha.setManaPersonagem(ficha.getManaPersonagem() + 7);
            Interface.MostrarMensagem("Você recuperou 7 de mana! Mana atual: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
        } else if (itemEscolhido.getNome().equals("Frutas")) {
            int cura = MecanicasRpg.rolarDado(2);
            ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
            Interface.MostrarMensagem("Você comeu uma fruta e recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
        } else if (itemEscolhido.getNome().equals("Kit Médico")) {
            // Pode usar o Kit em si ou no companheiro (se estiver ferido);
            // com vida cheia, ainda pode usar em você mesmo para curar uma infecção
            companheiros.Companheiro comp = ficha.getCompanheiro();
            boolean podeUsarEmSi = ficha.getVidaPersonagem() < ficha.getVidaMaxima() || ficha.isInfectado();
            boolean podeUsarCompanheiro = comp != null && comp.getFicha().getVidaPersonagem() < comp.getFicha().getVidaMaxima();

            boolean usarNoCompanheiro = false;
            if (podeUsarEmSi && podeUsarCompanheiro) {
                System.out.println("\n  Em quem deseja usar o Kit Médico?\n");
                System.out.println("  1. Em você");
                System.out.println("  2. Em " + comp.getNome());
                int quem = Interface.lerInteiro();
                usarNoCompanheiro = quem == 2;
            } else if (podeUsarCompanheiro) {
                System.out.println("\n  Usar o Kit Médico em " + comp.getNome() + "?\n");
                System.out.println("  1. Sim");
                System.out.println("  2. Não");
                int quem = Interface.lerInteiro();
                usarNoCompanheiro = quem == 1;
            } else if (!podeUsarEmSi) {
                Interface.ExibirErro("Sua vida já está no máximo!");
                Interface.Pausa(1500);
                return;
            }

            if (usarNoCompanheiro) {
                FichaRpg cf = comp.getFicha();
                int cura = MecanicasRpg.rolarDado(4);
                cf.setVidaPersonagem(Math.min(cf.getVidaPersonagem() + cura, cf.getVidaMaxima()));
                Interface.MostrarMensagem("Você usou o Kit Médico em " + comp.getNome() + " e ela(e) recuperou " + cura + " de vida! Vida: " + cf.getVidaPersonagem() + "/" + cf.getVidaMaxima());
            } else {
                int cura = MecanicasRpg.rolarDado(4);
                ficha.setVidaPersonagem(ficha.getVidaPersonagem() + cura);
                Interface.MostrarMensagem("Você recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());

                if (temHabilidade(ficha, "Cura Reforçada") && ficha.getManaPersonagem() >= 1) {
                    System.out.println("\nDeseja gastar 1 de mana para curar 2d4 extras com Cura Reforçada?");
                    System.out.println("1. Sim");
                    System.out.println("2. Não");
                    int usarCura = Interface.lerInteiro();

                    if (usarCura == 1) {
                        ficha.setManaPersonagem(ficha.getManaPersonagem() - 1);
                        int curaExtra = MecanicasRpg.rolarDado(4) + MecanicasRpg.rolarDado(4);
                        ficha.setVidaPersonagem(ficha.getVidaPersonagem() + curaExtra);
                        Interface.MostrarMensagem("Cura Reforçada: você recuperou +" + curaExtra + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                        Interface.Pausa(1500);
                    }
                }

                if (ficha.isInfectado()) {
                    ficha.setInfectado(false);
                    Interface.MostrarMensagem("Os curativos do Kit Médico expulsam a infecção! Você está curado.");
                    Interface.Pausa(2000);
                }
            }
        }

        ficha.consumirItem(itemEscolhido, 1);
        if (itemEscolhido.getQuantidade() <= 0) {
            ficha.getInventario().remove(itemEscolhido);
            Interface.MostrarMensagem("O item foi consumido e removido do inventário.");
        } else {
            Interface.MostrarMensagem("Restam " + itemEscolhido.getQuantidade() + "x " + itemEscolhido.getNome() + ".");
        }

        Interface.Pausa(2000);
    }

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
    public static boolean usarPactoMortal(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, habilidades.Habilidade hab) {
        if (!ficha.temItem("Olho Demoníaco")) {
            Interface.ExibirErro("Você precisa do Olho Demoníaco para usar o Pacto Mortal!");
            Interface.Pausa(1500);
            return true;
        }
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) {
            Interface.MostrarMensagem("Nenhum alvo escolhido.");
            Interface.Pausa(1500);
            return true;
        }
        Criatura alvo = inimigos.get(alvoIndex);
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        ficha.setPactoMortalAtivo(true);
        alvo.setEnfraquecido(true);
        Interface.MostrarMensagem("\nSeu olho demoníaco se volta para " + rotuloCriatura(inimigos, alvo) + " e o Pacto Mortal é selado!");
        Interface.MostrarMensagem("Até o fim do combate: " + alvo.getNome() + " tem -2 nas rolagens e +5 de dano demoníaco, mas VOCÊ sofre +3 em todo dano.");
        Interface.Pausa(2000);
        return true;
    }

    // Rei das Criaturas (Coroa do Rei): 3 de mana, não gasta a ação. O jogador rola um
    // teste de Presença contra a criatura (d20 + nível dela); vencendo, comanda: Fugir,
    // Atacar a si mesma ou Atacar outro monstro.
    public static boolean usarReiDasCriaturas(FichaRpg ficha, List<Criatura> inimigos) {
        if (!ficha.temItem("Coroa do Rei")) {
            Interface.ExibirErro("Você precisa da Coroa do Rei para isso!");
            Interface.Pausa(1500);
            return true;
        }
        if (ficha.getManaPersonagem() < 3) {
            Interface.ExibirErro("Mana insuficiente! O Rei das Criaturas custa 3 de mana.");
            Interface.Pausa(1500);
            return true;
        }
        List<Criatura> vivos = inimigosVivos(inimigos);
        if (vivos.isEmpty()) return true;

        int alvoIndex = escolherAlvo(inimigos);
        if (alvoIndex < 0) return true;
        Criatura alvo = inimigos.get(alvoIndex);

        ficha.setManaPersonagem(ficha.getManaPersonagem() - 3);

        Interface.pressionarParaTeste("Presença (Rei das Criaturas)");
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int totalJogador = dadoJogador + ficha.getPresencaTeste();
        int dadoCriatura = MecanicasRpg.rolarDado(20);
        int totalCriatura = dadoCriatura + alvo.getNivel();
        Interface.MostrarMensagem("-> Você: " + dadoJogador + " + " + ficha.getPresencaTeste() + " (Presença) = " + totalJogador
                + "  |  " + alvo.getNome() + ": " + dadoCriatura + " + " + alvo.getNivel() + " (Nível) = " + totalCriatura);
        Interface.Pausa(3000);

        if (totalJogador <= totalCriatura) {
            Interface.MostrarMensagem("\n" + alvo.getNome() + " resiste à sua vontade e não obedece o comando.");
            Interface.Pausa(2000);
            return true;
        }

        Interface.MostrarMensagem(VERDE + "\n" + alvo.getNome() + " curva-se à sua presença!" + RESET);
        Interface.Pausa(1500);

        List<String> comandos = new ArrayList<>();
        comandos.add("Fugir do combate");
        comandos.add("Atacar a si mesma");
        if (outrosVivos(inimigos, alvo).size() > 0) {
            comandos.add("Atacar outro monstro");
        }

        System.out.println("\n  Qual comando você dá a " + alvo.getNome() + "?\n");
        for (int i = 0; i < comandos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + comandos.get(i));
        }
        int escolha = Interface.lerOpcao(comandos.size());

        if (escolha == 1) {
            alvo.setFugiu(true);
            Interface.MostrarMensagem("\n" + alvo.getNome() + " recua aterrorizado e desaparece nos corredores, fugindo do combate!");
            Interface.Pausa(2000);
        } else if (escolha == 2) {
            Interface.MostrarMensagem("\nAo seu comando, " + alvo.getNome() + " se volta contra si mesmo!");
            Interface.Pausa(1500);
            criaturaAtacaCriatura(alvo, alvo, inimigos);
        } else {
            List<Criatura> outros = outrosVivos(inimigos, alvo);
            Criatura outro = outros.get(MecanicasRpg.rolarDado(outros.size()) - 1);
            Interface.MostrarMensagem("\nAo seu comando, " + alvo.getNome() + " avança sobre " + rotuloCriatura(inimigos, outro) + "!");
            Interface.Pausa(1500);
            criaturaAtacaCriatura(alvo, outro, inimigos);
        }
        return true;
    }

    // Criaturas vivas exceto `excluida` (usado no comando "Atacar outro monstro")
    private static List<Criatura> outrosVivos(List<Criatura> inimigos, Criatura excluida) {
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