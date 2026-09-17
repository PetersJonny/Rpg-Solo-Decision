package combate;

import classes.Guerreiro;
import classes.Mago;
import classes.Healer;
import companheiros.Companheiro;
import criaturas.Criatura;
import fichas.FichaRpg;
import habilidades.Habilidade;
import habilidades.Magia;
import itens.ItemRpg;
import mecanicas.MecanicasRpg;
import telas.Interface;

import java.util.ArrayList;
import java.util.List;

public class CombatManager {

    private static final String RESET = Interface.RESET;
    private static final String VERMELHO = "\u001B[31m";
    private static final String CIANO = Interface.CIANO;
    private static final String AMARELO = Interface.AMARELO;
    private static final String VERDE = Interface.VERDE;

    public static void IniciarCombate(FichaRpg ficha, Criatura c, Criatura c2) {
        IniciarCombate(ficha, c, c2, false);
    }

    public static void IniciarCombate(FichaRpg ficha, List<Criatura> inimigos, boolean jogadorSurpreendeu) {
        if (inimigos.isEmpty()) return;
        Criatura c = inimigos.get(0);
        Criatura c2 = inimigos.size() > 1 ? inimigos.get(1) : null;
        IniciarCombate(ficha, c, c2, jogadorSurpreendeu);
    }

    public static void IniciarCombate(FichaRpg ficha, Criatura c, Criatura c2, boolean jogadorSurpreendeu) {
        Interface.MostrarMensagem("\n================ COMBATE ================");
        Interface.Pausa(2500);

        List<Criatura> inimigos = new ArrayList<>();
        inimigos.add(c);
        if (c2 != null) {
            inimigos.add(c2);
        }
        List<Criatura> inimigosOriginais = new ArrayList<>(inimigos);

        System.out.println("\n" + AMARELO + "Inimigos: " + RESET);
        for (int i = 0; i < inimigos.size(); i++) {
            Criatura inimigo = inimigos.get(i);
            String nomeExibicao = inimigos.size() > 1 ? inimigo.getNome() + " " + (i + 1) : inimigo.getNome();
            System.out.println("  " + (i + 1) + ". " + VERMELHO + nomeExibicao + " (Vida: " + inimigo.getVida() + ")" + RESET);
        }

        int bonusIniciativaJogador = jogadorSurpreendeu ? 2 : 0;
        Interface.pressionarParaTeste("Destreza");
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int iniciativaJogador = dadoJogador + ficha.getDestrezaTeste() + bonusIniciativaJogador;

        Interface.MostrarMensagem("-> Iniciativa [" + ficha.getNomePersonagem() + "]: " + dadoJogador + " (Dado) + " + ficha.getDestrezaTeste() + " (Destreza) + " + bonusIniciativaJogador + " (Bônus) = " + iniciativaJogador);
        Interface.Pausa(2500);

        List<int[]> ordem = new ArrayList<>();
        ordem.add(new int[]{iniciativaJogador, 0});

        Companheiro comp = ficha.getCompanheiro();
        if (comp != null && comp.getFicha().getVidaPersonagem() > 0) {
            int dadoComp = MecanicasRpg.rolarDado(20);
            int iniciativaComp = dadoComp + comp.getFicha().getDestrezaTeste();
            Interface.MostrarMensagem("-> Iniciativa [" + comp.getNomeCompleto() + "]: " + dadoComp + " (Dado) + " + comp.getFicha().getDestrezaTeste() + " (Destreza) = " + iniciativaComp);
            Interface.Pausa(1500);
            ordem.add(new int[]{iniciativaComp, -1});
        }

        for (int i = 0; i < inimigos.size(); i++) {
            Criatura inimigo = inimigos.get(i);
            int dadoInimigo = MecanicasRpg.rolarDado(20);
            int iniciativaInimigo = dadoInimigo + inimigo.getIniciativa();
            Interface.MostrarMensagem("-> Iniciativa [" + CombatManager.rotuloCriatura(inimigos, inimigo) + "]: " + dadoInimigo + " (Dado) + " + inimigo.getIniciativa() + " (Iniciativa Base) = " + iniciativaInimigo);
            Interface.Pausa(1500);
            ordem.add(new int[]{iniciativaInimigo, i + 1});
        }

        ordem.sort((a, b) -> Integer.compare(b[0], a[0]));

        StringBuilder ordemTexto = new StringBuilder();
        for (int[] token : ordem) {
            String nomeOrdem;
            if (token[1] == 0) {
                nomeOrdem = ficha.getNomePersonagem();
            } else if (token[1] == -1) {
                nomeOrdem = comp != null ? comp.getNomeCompleto() : "Companheiro";
            } else {
                nomeOrdem = CombatManager.rotuloCriatura(inimigos, inimigos.get(token[1] - 1));
            }
            if (ordemTexto.length() > 0) ordemTexto.append(" > ");
            ordemTexto.append(nomeOrdem);
        }
        Interface.MostrarMensagem("\nOrdem de Iniciativa: " + ordemTexto);
        Interface.Pausa(2500);

        RodadasDeCombate(ficha, inimigos, inimigosOriginais, ordem);
    }

    public static void RodadasDeCombate(FichaRpg ficha, List<Criatura> inimigos, List<Criatura> inimigosOriginais, List<int[]> ordem) {
        boolean[] cascaGrossaAtiva = {false};
        int[] tentativasFuga = {0};
        List<Criatura> mortesProcessadas = new ArrayList<>();
        ficha.resetarEfeitosCombate();

        if (PassiveHandler.temHabilidade(ficha, "Defesa Absoluta")) {
            ficha.setDefesaAbsolutaAtiva(true);
            Interface.MostrarMensagem("\n(Defesa Absoluta ativa! +5 de defesa até você atacar.)");
            Interface.Pausa(1500);
        }

        while (ficha.getVidaPersonagem() > 0 && !inimigosVivos(inimigos).isEmpty()) {
            Interface.cabecalhoMenu("COMBATE");
            Interface.MostrarMensagem("  Sua Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + " | Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
            if (ficha.getCuraAbsolutaBonus() > 0) {
                Interface.MostrarMensagem("Proteção da Cura Absoluta: +" + ficha.getCuraAbsolutaBonus());
            }
            Interface.MostrarMensagem("  Inimigos:");
            for (int i = 0; i < inimigos.size(); i++) {
                Criatura c = inimigos.get(i);
                if (c.getVida() > 0) {
                    Interface.MostrarMensagem("  " + (i + 1) + ". " + c.getNome() + " (Vida: " + c.getVida() + ")");
                }
            }
            if (tentativasFuga[0] > 0) {
                Interface.MostrarMensagem("  Tentativas de fuga: " + tentativasFuga[0] + "/3");
            }
            Interface.Pausa(1500);

            cascaGrossaAtiva[0] = false;
            ficha.setMagiaProibidaAtiva(false);

            if (ficha.isCuraParaMortePreparado()) {
                ficha.setCuraParaMortePreparado(false);
                ficha.setCuraParaMorteAtivo(true);
                Interface.MostrarMensagem("\nO líquido mortal injetado começa a agir!");
                Interface.Pausa(1500);
            }

            if (ficha.getVidaPersonagem() <= 0 && !SkillExecutor.tentarReviver(ficha)) break;
            if (inimigosVivos(inimigos).isEmpty()) break;

            boolean semHabilidades = ficha.getRodadasSemHabilidade() > 0;
            int[] acaoDeclarada = declararAcao(ficha, inimigos, cascaGrossaAtiva, semHabilidades);

            boolean acaoResolvida = false;
            if (acaoDeclarada[0] == 4) {
                int resultadoFuga = executarAcaoDeclarada(ficha, inimigos, cascaGrossaAtiva, tentativasFuga, acaoDeclarada);
                if (resultadoFuga == 0) {
                    Interface.MostrarMensagem("\nVocê conseguiu escapar da floresta!");
                    Interface.Pausa(2500);
                    return;
                }
                if (resultadoFuga == 1) {
                    continue;
                }
                acaoResolvida = true;
                LootManager.processarMortes(ficha, inimigos, inimigosOriginais);
            }

            for (int[] token : ordem) {
                if (ficha.getVidaPersonagem() <= 0 && !SkillExecutor.tentarReviver(ficha)) break;
                if (inimigosVivos(inimigos).isEmpty()) break;

                if (token[1] == 0) {
                    if (acaoResolvida) continue;
                    int resultado = executarAcaoDeclarada(ficha, inimigos, cascaGrossaAtiva, tentativasFuga, acaoDeclarada);
                    if (resultado == 0) {
                        Interface.MostrarMensagem("\nVocê conseguiu escapar da floresta!");
                        Interface.Pausa(2500);
                        return;
                    }
                    LootManager.processarMortes(ficha, inimigos, inimigosOriginais);
                } else if (token[1] == -1) {
                    Companheiro comp = ficha.getCompanheiro();
                    if (comp != null && comp.getFicha().getVidaPersonagem() > 0) {
                        companheiros.CompanionManager.acaoDoCompanheiro(ficha, comp, inimigos);
                        LootManager.processarMortes(ficha, inimigos, inimigosOriginais);
                    }
                } else {
                    Criatura c = inimigos.get(token[1] - 1);
                    if (c.getVida() > 0) {
                        if (ficha.getPrisaoAtiva() == c) {
                            int testePrisao = MecanicasRpg.rolarDado(20);
                            Interface.MostrarMensagem("\n" + CombatManager.rotuloCriatura(inimigos, c) + " tenta se libertar da prisão (d20, precisa de 15 ou mais): " + testePrisao);
                            Interface.Pausa(1500);
                            if (testePrisao < 15) {
                                Interface.MostrarMensagem("A prisão o mantém imóvel! " + CombatManager.rotuloCriatura(inimigos, c) + " não consegue agir.");
                                Interface.Pausa(1500);
                                continue;
                            }
                            ficha.setPrisaoAtiva(null);
                            Interface.MostrarMensagem("A prisão se desfaz! " + CombatManager.rotuloCriatura(inimigos, c) + " está livre!");
                            Interface.Pausa(1500);
                        }

                        if (ficha.isMagiaProibidaAtiva()) {
                            Interface.MostrarMensagem("\n" + CombatManager.rotuloCriatura(inimigos, c) + " investe, mas a Magia Proibida corrompe seu golpe e ele erra!");
                            Interface.Pausa(1500);
                            continue;
                        }

                        Interface.MostrarMensagem("\n" + CombatManager.rotuloCriatura(inimigos, c) + " avança para atacar!");
                        Interface.Pausa(1500);

                        Companheiro comp2 = ficha.getCompanheiro();
                        boolean atacarCompanheiro = comp2 != null
                                && comp2.getFicha().getVidaPersonagem() > 0
                                && MecanicasRpg.rolarDado(2) == 1;
                        if (atacarCompanheiro) {
                            Interface.MostrarMensagem(CombatManager.rotuloCriatura(inimigos, c) + " mira em " + comp2.getNomeCompleto() + "!");
                            Interface.Pausa(1500);
                            c.atacarJogador(comp2.getFicha(), false);
                            if (comp2.getFicha().getVidaPersonagem() <= 0) {
                                Interface.MostrarMensagem("\n" + comp2.getNomeCompleto() + " cai em combate!");
                                Interface.Pausa(1500);
                            }
                        } else {
                            c.atacarJogador(ficha, cascaGrossaAtiva[0]);
                        }
                        LootManager.processarMortes(ficha, inimigos, inimigosOriginais);
                    }
                }
            }

            if (ficha.getRodadasSemHabilidade() > 0) {
                ficha.setRodadasSemHabilidade(ficha.getRodadasSemHabilidade() - 1);
            }
        }

        Companheiro comp = ficha.getCompanheiro();

        if (comp != null) {
            FichaRpg cf = comp.getFicha();
            if (cf.getVidaPersonagem() <= 0) {
                Interface.cabecalhoMenu("RESGATE DO COMPANHEIRO");
                Interface.MostrarMensagem("\n  " + comp.getNomeCompleto() + " cai no chão, sem vida...");
                Interface.Pausa(1500);
                Interface.MostrarMensagem("  Você precisa agir rápido! Teste de Intelecto (DT 14) para salvá-lo!\n");
                Interface.Pausa(1000);

                int dado = MecanicasRpg.rolarDado(20);
                int total = dado + ficha.getIntelectoTeste();
                Interface.MostrarMensagem("    Dado: " + dado + "  |  Intelecto: " + ficha.getIntelectoTeste() + "  |  Total: " + total + "  (DT 14)");
                Interface.Pausa(1500);

                if (total >= 14) {
                    cf.setVidaPersonagem(Math.max((int) (cf.getVidaMaxima() * 0.3), 1));
                    Interface.MostrarMensagem("\n  Você apoia " + comp.getNomeCompleto() + " a tempo! Ele/a acorda ferido, mas vivo(a).");
                } else {
                    Interface.MostrarMensagem("\n  Você falha em estabilizar " + comp.getNomeCompleto() + "... Ele(a) parte em silêncio.");
                    Interface.Pausa(1500);

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
                Interface.MostrarMensagem("\n" + comp.getNomeCompleto() + " respira fundo e se recupera um pouco após o combate.");
                cf.setVidaPersonagem(Math.max((int) (cf.getVidaMaxima() * 0.5), 1));
                Interface.Pausa(2000);
            }
        }

        if (ficha.getVidaPersonagem() <= 0 && comp != null) {
            FichaRpg cf = comp.getFicha();
            Interface.cabecalhoMenu("RESGATE DO JOGADOR");
            Interface.MostrarMensagem("\n  Você cai... " + comp.getNomeCompleto() + " se joga ao seu lado!");
            Interface.Pausa(1500);
            Interface.MostrarMensagem("  " + comp.getNomeCompleto() + " precisa pensar rápido! Teste de Intelecto (DT 14) para te salvar!\n");
            Interface.Pausa(1000);

            int dado = MecanicasRpg.rolarDado(20);
            int total = dado + cf.getIntelectoTeste();
            Interface.MostrarMensagem("    Dado: " + dado + "  |  Intelecto: " + cf.getIntelectoTeste() + "  |  Total: " + total + "  (DT 14)");
            Interface.Pausa(1500);

            if (total >= 14) {
                ficha.setVidaPersonagem(Math.max((int) (ficha.getVidaMaxima() * 0.3), 1));
                Interface.MostrarMensagem("\n  " + comp.getNomeCompleto() + " estabiliza você a tempo! Você acorda ferido, mas vivo.");
            } else {
                Interface.MostrarMensagem("\n  " + comp.getNomeCompleto() + " não consegue te estabilizar...");
            }
            Interface.Pausa(2000);
        }

        Interface.cabecalhoMenu("FIM DO COMBATE");
        if (ficha.getVidaPersonagem() <= 0) {
            Interface.MostrarMensagem("\n  Você foi derrotado... A floresta recupera o silêncio.");
            Interface.Pausa(3000);
            System.exit(0);
        } else {
            Interface.MostrarMensagem("\n  Você sobreviveu ao combate!");
            Interface.Pausa(2500);
        }
        Interface.barraDivisoria();
    }

    public static int[] declararAcao(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, boolean semHabilidades) {
        while (true) {
            Interface.barraDivisoria();
            System.out.println("\n  O que deseja fazer?\n");
            System.out.println("  1. Lutar");
            System.out.println("  2. Abrir Mochila");
            System.out.println("  3. Tentar Fugir");
            System.out.println("  4. Ver Ficha\n");
            System.out.println("  Escolha uma opção:");
            int escolha = Interface.lerOpcao(1, 4);

            if (escolha == 4) {
                Interface.MostrarFicha(ficha);
                Interface.Pausa(1500);
                continue;
            }

            if (escolha == 1) {
                int[] resultado = MenuLutarComEscolha(ficha, inimigos, cascaGrossaAtiva, semHabilidades);
                if (resultado == null) continue;

                if (resultado[0] == 5) {
                    resultado = MenuLutarComEscolha(ficha, inimigos, cascaGrossaAtiva, semHabilidades);
                    if (resultado == null || resultado[0] == 5) {
                        return new int[]{6, -1, -1, -1};
                    }
                }
                return resultado;
            } else if (escolha == 2) {
                int item = ItemUser.escolherItemParaUsar(ficha);
                if (item == -1) continue;
                if (item == -2) return new int[]{4, -1, -1, -1};
                return new int[]{3, item, -1, -1};
            } else if (escolha == 3) {
                return new int[]{4, -1, -1, -1};
            } else {
                Interface.ExibirErro("Escolha inválida!");
                Interface.Pausa(1500);
            }
        }
    }

    public static int executarAcaoDeclarada(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, int[] tentativasFuga, int[] acao) {
        int tipo = acao[0];

        if (tipo == 1 || tipo == 2) {
            executarAcaoJogador(ficha, inimigos, tipo, acao[1], acao[2], acao[3]);
            return 1;
        } else if (tipo == 3) {
            ItemUser.usarItemNaVez(ficha, acao[1]);
            return 1;
        } else if (tipo == 6) {
            Interface.MostrarMensagem("\nVocê aguarda, mantendo a guarda.");
            Interface.Pausa(1500);
            return 1;
        } else {
            return TentarFugirNaVez(ficha, inimigos, tentativasFuga);
        }
    }

    public static int TentarFugirNaVez(FichaRpg ficha, List<Criatura> inimigos, int[] tentativasFuga) {
        int resultadoFuga = TentarFugir(ficha, inimigos, tentativasFuga[0]);

        if (resultadoFuga == tentativasFuga[0] && resultadoFuga != -1) {
            Interface.MostrarMensagem("\nVocê hesita e perde a oportunidade!");
            Interface.Pausa(1500);
            return -1;
        }

        if (resultadoFuga == -1) {
            return -1;
        } else if (resultadoFuga == 3) {
            return 0;
        } else {
            tentativasFuga[0] = resultadoFuga;
            return 1;
        }
    }

    public static int TentarFugir(FichaRpg ficha, List<Criatura> inimigos, int tentativasAtuais) {
        Interface.cabecalhoMenu("TENTAR FUGIR");
        System.out.println("\n  Deseja realmente tentar fugir?\n");
        System.out.println("  1. Sim, tentar fugir");
        System.out.println("  2. Não, voltar ao combate");

        int confirmar = Interface.lerInteiro();

        if (confirmar != 1) return tentativasAtuais;

        Interface.MostrarMensagem("\nVocê tenta se esquivar e recuar...");
        Interface.Pausa(2000);

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

    public static void executarAcaoJogador(FichaRpg ficha, List<Criatura> inimigos, int tipoAcao, int alvoIndex, int armaIndex, int habIndex) {
        boolean sucesso;
        if (tipoAcao == 1) {
            sucesso = WeaponSelector.executarAtaqueComArma(ficha, inimigos, alvoIndex, armaIndex);
        } else {
            sucesso = WeaponSelector.executarHabilidadeEscolhida(ficha, inimigos, alvoIndex, habIndex);
        }

        if (!sucesso) {
            tentarConhecimentoAvancado(ficha, inimigos, tipoAcao, alvoIndex, armaIndex, habIndex);
        }
    }

    public static void tentarConhecimentoAvancado(FichaRpg ficha, List<Criatura> inimigos, int tipoAcao, int alvoIndex, int armaIndex, int habIndex) {
        for (Habilidade hab : ficha.getHabilidades()) {
            if (hab.getNome().equals("Conhecimento Avançado") && ficha.getManaPersonagem() >= hab.getCustoMana()) {
                System.out.println("\nDeseja usar Conhecimento Avançado para rerrolar? (Custo: " + hab.getCustoMana() + " Mana)");
                System.out.println("1. Sim");
                System.out.println("2. Não");
                int escolha = Interface.lerOpcao(1, 2);

                if (escolha == 1) {
                    ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
                    Interface.MostrarMensagem("\nVocê foca seus conhecimentos e tenta novamente!");
                    Interface.Pausa(1500);

                    boolean sucessoReroll;
                    if (tipoAcao == 1) {
                        sucessoReroll = WeaponSelector.executarAtaqueComArma(ficha, inimigos, alvoIndex, armaIndex);
                    } else {
                        sucessoReroll = WeaponSelector.executarHabilidadeEscolhida(ficha, inimigos, alvoIndex, habIndex);
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

    public static List<Criatura> inimigosVivos(List<Criatura> inimigos) {
        List<Criatura> vivos = new ArrayList<>();
        for (Criatura c : inimigos) {
            if (c.getVida() > 0) vivos.add(c);
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

    public static int[] MenuLutarComEscolha(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, boolean semHabilidades) {
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
                Habilidade hab = ficha.getHabilidades().get(i);
                if (hab.isPassiva()
                        && !hab.getNome().equals("Defesa Absoluta")
                        && !hab.getNome().equals("Arma Mental")
                        && !hab.getNome().equals("Deus")
                        && !hab.getNome().equals("Conhecimento Absoluto")
                        && PassiveHandler.podeAtivarPassiva(ficha, hab, cascaGrossaAtiva[0])
                        && ficha.getManaPersonagem() >= hab.getCustoMana()) {
                    descricoes.add(hab.getNome() + " (Custo: " + hab.getCustoMana() + " Mana) - ainda pode atacar após usar");
                    acoes.add(new int[]{3, i, -1, -1});
                }
            }

            if (PassiveHandler.temHabilidade(ficha, "Magia Proibida") && !ficha.isMagiaProibidaUsada() && ficha.getManaPersonagem() >= 5) {
                descricoes.add("Magia Proibida (Custo: 5 Mana) - não gasta sua ação");
                acoes.add(new int[]{4, -1, -1, -1});
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
            int armaIdx = WeaponSelector.EscolherArma(ficha);
            if (armaIdx == -1) return null;
            int alvo = escolherAlvo(inimigos);
            if (alvo == -1) return null;
            return new int[]{1, alvo, armaIdx, -1};
        }

        if (acao[0] == 2) {
            int habIdx = WeaponSelector.EscolherHabilidadeAtiva(ficha);
            if (habIdx == -1) return null;
            Habilidade habEscolhida = ficha.getHabilidades().get(habIdx);
            int alvo = -1;
            if (habEscolhida instanceof Magia || habEscolhida.getNome().equals("Prisão")) {
                alvo = escolherAlvo(inimigos);
                if (alvo == -1) return null;
            }
            return new int[]{2, alvo, -1, habIdx};
        }

        if (acao[0] == 3) {
            Habilidade hab = ficha.getHabilidades().get(acao[1]);
            ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
            PassiveHandler.aplicaPassiva(ficha, hab, cascaGrossaAtiva);
            Interface.MostrarMensagem("\nVocê ativa " + hab.getNome() + "!");
            Interface.MostrarMensagem(hab.getDescricao());
            Interface.Pausa(2000);
            return new int[]{5, -1, -1, -1};
        }

        if (acao[0] == 4) {
            ficha.setManaPersonagem(ficha.getManaPersonagem() - 5);
            ficha.setMagiaProibidaUsada(true);
            ficha.setMagiaProibidaAtiva(true);
            Interface.MostrarMensagem("\nVocê invoca a Magia Proibida! Todos os ataques dos inimigos desta rodada falharão.");
            Interface.Pausa(2000);
            return new int[]{5, -1, -1, -1};
        }

        return null;
    }

    public static int escolherAlvo(List<Criatura> inimigos) {
        List<Criatura> vivos = new ArrayList<>();
        for (Criatura c : inimigos) {
            if (c.getVida() > 0) vivos.add(c);
        }

        if (vivos.isEmpty()) return -1;
        if (vivos.size() == 1) return inimigos.indexOf(vivos.get(0));

        System.out.println("\n  " + CIANO + "[ ESCOLHA SEU ALVO ]" + RESET + "\n");
        for (int i = 0; i < vivos.size(); i++) {
            Criatura c = vivos.get(i);
            System.out.println("  " + (i + 1) + ". " + CombatManager.rotuloCriatura(inimigos, c) + " (Vida: " + c.getVida() + ")");
        }
        System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

        int escolha = Interface.lerInteiro();

        if (escolha < 1 || escolha > vivos.size()) return -1;

        return inimigos.indexOf(vivos.get(escolha - 1));
    }
}
