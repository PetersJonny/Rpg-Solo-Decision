package eventos;

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

    public static void Explorar(FichaRpg ficha) {
        Interface.barraDivisoria();
        Interface.MostrarMensagem("\nVocê adentra as matas geladas da floresta de Freijord...");
        Interface.Pausa(2500);
        EventoAnimal(ficha);
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
        List<Criatura> inimigos = criarGrupoMonstros(tipo);
        Criatura referencia = inimigos.get(0);

        int dadoPresenca = 0;
        int totalPresenca = 0;
        Interface.pressionarParaTeste("Presença");
        dadoPresenca = MecanicasRpg.rolarDado(20);
        totalPresenca = dadoPresenca + ficha.getPresenca();
        Interface.MostrarMensagem("-> Teste de Presença: " + dadoPresenca + " (Dado) + " + ficha.getPresenca() + " (Atributo) = " + totalPresenca + " (Dificuldade: " + referencia.getTestePresenca() + ")");
        Interface.Pausa(2500);

        if (totalPresenca >= referencia.getTestePresenca()) {
            Interface.MostrarMensagem("\n" + nomesDosInimigos(inimigos) + " apareceu entre as sombras das árvores e você o avistou antes!");
            Interface.Pausa(2500);

            System.out.println("O que deseja fazer?");
            System.out.println("1. Lutar (Você terá +2 de Iniciativa extra por surpreendê-lo)");
            System.out.println("2. Tentar Fugir furtivamente");
            int escolha = Interface.lerInteiro();


            if (escolha == 1) {
                Interface.MostrarMensagem("\nVocê saca sua arma e parte para cima!");
                Interface.Pausa(2500);
                IniciarCombate(ficha, inimigos, true);
            } else if (escolha == 2) {
                Interface.pressionarParaTeste("Destreza (Fuga)");
                int dadoDestreza = MecanicasRpg.rolarDado(20);
                int totalDestreza = dadoDestreza + ficha.getDestreza();
                Interface.MostrarMensagem("-> Teste de Destreza (Fuga): " + dadoDestreza + " (Dado) + " + ficha.getDestreza() + " (Atributo) = " + totalDestreza);
                Interface.Pausa(2500);
                if (totalDestreza >= 12) {
                    Interface.MostrarMensagem("\nVocê recua lentamente pelas sombras e foge com sucesso, sem ser notado.");
                    Interface.Pausa(2500);
                } else {
                    Interface.MostrarMensagem("\nVocê pisa em um galho seco! A ameaça percebe você e avança!");
                    Interface.Pausa(2500);
                    IniciarCombate(ficha, inimigos, false);
                }
            } else {
                Interface.ExibirErro("Escolha inválida! Você hesitou e foi notado!");
                Interface.Pausa(2500);
                IniciarCombate(ficha, inimigos, false);
            }
        } else {
            Interface.MostrarMensagem("\n" + nomesDosInimigos(inimigos) + " saltou das sombras e te surpreendeu!");
            Interface.Pausa(2500);
            IniciarCombate(ficha, inimigos, false);
        }
    }

    // Cria o grupo de monstros conforme o tipo
    private static List<Criatura> criarGrupoMonstros(int tipo) {
        List<Criatura> grupo = new ArrayList<>();
        int quantidade;

        switch (tipo) {
            case 1: { // Lobo Selvagem: 1-3
                quantidade = MecanicasRpg.rolarEntre(1, 3);
                for (int i = 0; i < quantidade; i++) {
                    grupo.add(criarLobo());
                }
                break;
            }
            case 2: { // Urso: 1
                grupo.add(criarUrso());
                break;
            }
            default: { // Bandido: 1-5
                quantidade = MecanicasRpg.rolarEntre(1, 5);
                for (int i = 0; i < quantidade; i++) {
                    grupo.add(criarBandido());
                }
                break;
            }
        }

        return grupo;
    }

    private static Criatura criarLobo() {
        Criatura c = new Criatura("Lobo Selvagem", 1, 14, 10, 3);
        c.setBonusAcerto(3);
        c.setTestePresenca(8);
        c.setXpGanho(25);
        c.adicionarAtaque("Mordida", "", 1, 6);
        c.adicionarAtaque("Aranhão", "", 2, 4);
        c.adicionarDrop("Couro", 1, 2, 40);
        return c;
    }

    private static Criatura criarUrso() {
        Criatura c = new Criatura("Urso", 3, 35, 7, 0);
        c.setBonusAcerto(1);
        c.setTestePresenca(5);
        c.setXpGanho(50);
        c.adicionarAtaque("Mordida", "", 1, 10);
        c.adicionarAtaque("Aranhão", "", 2, 8);
        c.adicionarDrop("Couro", 2, 4, 60);
        c.adicionarDrop("Dente de Urso", 1, 1, 20);
        return c;
    }

    private static Criatura criarBandido() {
        Criatura c = new Criatura("Bandido", 2, 9, 12, 1);
        c.setBonusAcerto(2);
        c.setTestePresenca(15);
        c.setXpGanho(10);
        c.adicionarAtaque("Facada", "", 1, 4);
        c.adicionarAtaque("Soco", "", 1, 3);
        c.setOuroDrop(9, 27, 100);
        c.adicionarDrop("Faca", 1, 1, 35);
        return c;
    }

    private static Criatura criarFada() {
        Criatura c = new Criatura("Fada", 1, 4, 14, 0);
        c.setAcertoAutomatico(true);
        c.setTestePresenca(18);
        c.setChanceAparecer(20);
        c.setXpGanho(30);
        c.adicionarAtaque("Brilho Cintilante", "luz", 1, 6);
        c.adicionarDrop("Brilho Mágico", 1, 1, 100);
        return c;
    }

    // ==================== ENCONTRO COM A FADA ====================

    private static void EncontrarFada(FichaRpg ficha) {
        Interface.pressionarParaTeste("Presença");
        int dadoPresenca = MecanicasRpg.rolarDado(20);
        int totalPresenca = dadoPresenca + ficha.getPresenca();
        Interface.MostrarMensagem("-> Teste de Presença: " + dadoPresenca + " (Dado) + " + ficha.getPresenca() + " (Atributo) = " + totalPresenca + " (Dificuldade: 18)");
        Interface.Pausa(2500);

        boolean avistou = totalPresenca >= 18;

        if (avistou) {
            Interface.MostrarMensagem("\nUm leve brilho chama sua atenção: uma pequena Fada flutua entre as árvores!");
        } else {
            Interface.MostrarMensagem("\nUma Fada surge diante de você, espalhando um brilho suave!");
        }
        Interface.Pausa(2500);

        System.out.println("\nO que deseja fazer?");
        System.out.println("1. Tentar conversar com a Fada");
        System.out.println("2. Lutar contra a Fada");
        System.out.println("3. Deixá-la em paz e seguir caminho");
        int escolha = Interface.lerInteiro();


        if (escolha == 1) {
            Interface.pressionarParaTeste("Sabedoria");
            int dadoSabedoria = MecanicasRpg.rolarDado(20);
            int totalSabedoria = dadoSabedoria + ficha.getSabedoria();
            Interface.MostrarMensagem("-> Teste de Sabedoria (Conversa): " + dadoSabedoria + " (Dado) + " + ficha.getSabedoria() + " (Atributo) = " + totalSabedoria + " (Dificuldade: 14)");
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
            fadaBatalha.add(criarFada());
            IniciarCombate(ficha, fadaBatalha, avistou);
        } else {
            Interface.MostrarMensagem("\nA Fada é deixada em paz e você segue seu caminho tranquilamente.");
            Interface.Pausa(2500);
        }
    }

    // ==================== INICIAR COMBATE ====================

    private static void IniciarCombate(FichaRpg ficha, List<Criatura> inimigos, boolean jogadorSurpreendeu) {
        Interface.MostrarMensagem("\n================ COMBATE ================");
        Interface.Pausa(2500);

        int bonusIniciativaJogador = jogadorSurpreendeu ? 2 : 0;
        Interface.pressionarParaTeste("Destreza");
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int iniciativaJogador = dadoJogador + ficha.getDestreza() + bonusIniciativaJogador;

        Interface.MostrarMensagem("-> Iniciativa [" + ficha.getNomePersonagem() + "]: " + dadoJogador + " (Dado) + " + ficha.getDestreza() + " (Destreza) + " + bonusIniciativaJogador + " (Bônus) = " + iniciativaJogador);
        Interface.Pausa(2500);

        List<int[]> ordem = new ArrayList<>();
        ordem.add(new int[]{iniciativaJogador, 0});

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
            String nomeOrdem = (token[1] == 0) ? ficha.getNomePersonagem() : rotuloCriatura(inimigos, inimigos.get(token[1] - 1));
            if (ordemTexto.length() > 0) ordemTexto.append(" > ");
            ordemTexto.append(nomeOrdem);
        }
        Interface.MostrarMensagem("\nOrdem de Iniciativa: " + ordemTexto);
        Interface.Pausa(2500);

        RodadasDeCombate(ficha, inimigos, ordem);
    }

    // ==================== RODADAS DE COMBATE ====================

    private static void RodadasDeCombate(FichaRpg ficha, List<Criatura> inimigos, List<int[]> ordem) {
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

        while (ficha.getVidaPersonagem() > 0 && !inimigosVivos(inimigos).isEmpty()) {
            Interface.barraDivisoria();
            Interface.MostrarMensagem("Sua Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + " | Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
            if (ficha.getCuraAbsolutaBonus() > 0) {
                Interface.MostrarMensagem("Proteção da Cura Absoluta: +" + ficha.getCuraAbsolutaBonus());
            }
            Interface.MostrarMensagem("Inimigos:");
            for (int i = 0; i < inimigos.size(); i++) {
                Criatura c = inimigos.get(i);
                if (c.getVida() > 0) {
                    Interface.MostrarMensagem("  " + (i + 1) + ". " + c.getNome() + " (Vida: " + c.getVida() + ")");
                }
            }
            if (tentativasFuga[0] > 0) {
                Interface.MostrarMensagem("Tentativas de fuga: " + tentativasFuga[0] + "/3");
            }
            Interface.Pausa(1500);

            cascaGrossaAtiva[0] = false;
            ficha.setMagiaProibidaAtiva(false);

            // O líquido mortal de Cura para a Morte começa a agir a partir do próximo turno
            if (ficha.isCuraParaMortePreparado()) {
                ficha.setCuraParaMortePreparado(false);
                ficha.setCuraParaMorteAtivo(true);
                Interface.MostrarMensagem("\nO líquido mortal injetado começa a agir!");
                Interface.Pausa(1500);
            }

            if (ficha.getVidaPersonagem() <= 0 && !tentarReviver(ficha)) break;
            if (inimigosVivos(inimigos).isEmpty()) break;

            // O jogador escolhe a ação da rodada ANTES de qualquer ataque acontecer.
            // Após o Estrondo, ele não pode usar habilidades no próximo turno.
            boolean semHabilidades = ficha.getRodadasSemHabilidade() > 0;
            int[] acaoDeclarada = declararAcao(ficha, inimigos, cascaGrossaAtiva, semHabilidades);

            // Registra inimigos que já atacaram nesta rodada (ex.: reação à fuga)
            Set<Criatura> jaAtacouNaRodada = new HashSet<>();

            // A FUGA é a única ação fora da ordem de iniciativa:
            // o jogador tenta fugir primeiro; se passar, ninguém ataca; se falhar, os inimigos atacam
            boolean acaoResolvida = false;
            if (acaoDeclarada[0] == 4) {
                int resultadoFuga = executarAcaoDeclarada(ficha, inimigos, cascaGrossaAtiva, tentativasFuga, jaAtacouNaRodada, acaoDeclarada);
                if (resultadoFuga == 0) {
                    Interface.MostrarMensagem("\nVocê conseguiu escapar da floresta!");
                    Interface.Pausa(2500);
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
                if (ficha.getVidaPersonagem() <= 0 && !tentarReviver(ficha)) break;
                if (inimigosVivos(inimigos).isEmpty()) break;

                if (token[1] == 0) {
                    if (acaoResolvida) continue;
                    int resultado = executarAcaoDeclarada(ficha, inimigos, cascaGrossaAtiva, tentativasFuga, jaAtacouNaRodada, acaoDeclarada);
                    if (resultado == 0) {
                        Interface.MostrarMensagem("\nVocê conseguiu escapar da floresta!");
                        Interface.Pausa(2500);
                        return;
                    }
                    // Processa XP/drops dos inimigos que acabaram de morrer
                    processarMortes(inimigos, mortesProcessadas, ficha);
                } else {
                    Criatura c = inimigos.get(token[1] - 1);
                    if (c.getVida() > 0 && !jaAtacouNaRodada.contains(c)) {
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
                        c.atacarJogador(ficha, cascaGrossaAtiva[0]);
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

        Interface.barraDivisoria();
        if (ficha.getVidaPersonagem() <= 0) {
            Interface.MostrarMensagem("\nVocê foi derrotado... A floresta recupera o silêncio.");
            Interface.Pausa(3000);
        } else {
            Interface.MostrarMensagem("\nVocê derrotou todos os inimigos!");
            Interface.Pausa(2500);
        }
        Interface.barraDivisoria();
    }

    // Concede XP e drops dos inimigos mortos ainda não processados, com mensagens de level up
    private static void processarMortes(List<Criatura> inimigos, List<Criatura> mortesProcessadas, FichaRpg ficha) {
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
    private static int[] declararAcao(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, boolean semHabilidades) {
        while (true) {
            System.out.println("\nO que deseja fazer?");
            System.out.println("1. Lutar");
            System.out.println("2. Abrir Mochila");
            System.out.println("3. Tentar Fugir");
            System.out.println("4. Ver Ficha");

            int escolha = Interface.lerInteiro();

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
                int item = escolherItemParaUsar(ficha);
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

    // Fase de resolução: executa a ação declarada quando chega a vez do jogador na iniciativa
    private static int executarAcaoDeclarada(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, int[] tentativasFuga, Set<Criatura> jaAtacouNaRodada, int[] acao) {
        int tipo = acao[0];

        if (tipo == 1 || tipo == 2) {
            executarAcaoJogador(ficha, inimigos, tipo, acao[1], acao[2], acao[3]);
            return 1;
        } else if (tipo == 3) {
            usarItemNaVez(ficha, acao[1]);
            return 1;
        } else if (tipo == 6) {
            Interface.MostrarMensagem("\nVocê aguarda, mantendo a guarda.");
            Interface.Pausa(1500);
            return 1;
        } else {
            return TentarFugirNaVez(ficha, inimigos, cascaGrossaAtiva, tentativasFuga, jaAtacouNaRodada);
        }
    }

    // Tenta fugir gastando o turno; retorna 0 se escapou, 1 se passou (sem chegar nas 3) ou -1 se falhou
    private static int TentarFugirNaVez(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, int[] tentativasFuga, Set<Criatura> jaAtacouNaRodada) {
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
                maisRapido.atacarJogador(ficha, cascaGrossaAtiva[0]);
            }
            return -1;
        } else if (resultadoFuga == 3) {
            return 0;
        } else {
            tentativasFuga[0] = resultadoFuga;
            return 1;
        }
    }

    // Retorna a criatura viva com maior iniciativa base
    private static Criatura inimigoMaisRapidoVivo(List<Criatura> inimigos) {
        Criatura maisRapido = null;
        for (Criatura c : inimigos) {
            if (c.getVida() <= 0) continue;
            if (maisRapido == null || c.getIniciativa() > maisRapido.getIniciativa()) {
                maisRapido = c;
            }
        }
        return maisRapido;
    }

    // Verifica se a habilidade passiva ainda pode ser ativada nesta rodada
    private static boolean podeAtivarPassiva(FichaRpg ficha, habilidades.Habilidade hab, boolean cascaGrossaAtiva) {
        if (!hab.isPassiva()) return false;
        switch (hab.getNome()) {
            case "Casca Grossa" -> { return !cascaGrossaAtiva; }
            case "Espada Afiada" -> { return !ficha.isEspadaAfiadaAtiva(); }
            default -> { return true; }
        }
    }

    // Aplica o efeito da habilidade passiva ativada
    private static void aplicaPassiva(FichaRpg ficha, habilidades.Habilidade hab, boolean[] cascaGrossaAtiva) {
        switch (hab.getNome()) {
            case "Casca Grossa" -> cascaGrossaAtiva[0] = true;
            case "Espada Afiada" -> ficha.setEspadaAfiadaAtiva(true);
        }
    }

    // Apresenta as opções de habilidade ao atingir um novo nível com escolha
    private static void escolherHabilidadeNivel(FichaRpg ficha, int nivel, List<habilidades.Habilidade> opcoes) {
        System.out.println("\n--- ESCOLHA UMA HABILIDADE (NÍVEL " + nivel + ") ---");
        System.out.println("(inclui habilidades de escolha de níveis anteriores ainda não aprendidas)");
        for (int i = 0; i < opcoes.size(); i++) {
            habilidades.Habilidade h = opcoes.get(i);
            System.out.println((i + 1) + ". " + h.getNome() + " (Custo: " + h.getCustoMana() + " Mana)");
            System.out.println("   " + h.getDescricao());
        }

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
    private static void aplicarDeusSeAprendido(FichaRpg ficha, habilidades.Habilidade aprendida) {
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
            ficha.getHabilidades().add(new habilidades.Habilidade("Cura Incessante", "Cura toda a sua vida. Pode ser usada apenas uma vez por combate.", 0));
        }
        Interface.MostrarMensagem("\nVocê se torna um Deus! A forma de Semi Deus fica permanentemente ativa.");
        Interface.MostrarMensagem("Vida máxima aumentada em " + bonusVida + " e você ganhou a habilidade Cura Incessante!");
        Interface.Pausa(2500);
    }

    // Conhecimento Absoluto (Healer lvl 10): +2 em todos os atributos
    private static void aplicarConhecimentoAbsolutoSeAprendido(FichaRpg ficha, habilidades.Habilidade aprendida) {
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
    private static void aplicarArmaMentalSeAprendida(FichaRpg ficha, habilidades.Habilidade aprendida) {
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

    private static void escolherPontoAtributo(FichaRpg ficha) {
        while (true) {
            Interface.barraDivisoria();
            System.out.println("\nVocê ganhou um ponto de atributo! Escolha onde gastar:");
            System.out.println("1. Constituição");
            System.out.println("2. Destreza");
            System.out.println("3. Força");
            System.out.println("4. Sabedoria");
            System.out.println("5. Intelecto");
            System.out.println("6. Presença");

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

    private static void executarAcaoJogador(FichaRpg ficha, List<Criatura> inimigos, int tipoAcao, int alvoIndex, int armaIndex, int habIndex) {
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

    private static void tentarConhecimentoAvancado(FichaRpg ficha, List<Criatura> inimigos, int tipoAcao, int alvoIndex, int armaIndex, int habIndex) {
        for (habilidades.Habilidade hab : ficha.getHabilidades()) {
            if (hab.getNome().equals("Conhecimento Avançado") && ficha.getManaPersonagem() >= hab.getCustoMana()) {
                System.out.println("\nDeseja usar Conhecimento Avançado para rerrolar? (Custo: " + hab.getCustoMana() + " Mana)");
                System.out.println("1. Sim");
                System.out.println("2. Não");
                int escolha = Interface.lerInteiro();


                if (escolha == 1) {
                    ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
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

    private static int[] MenuLutarComEscolha(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, boolean semHabilidades) {
        Interface.barraDivisoria();
        System.out.println("\n--- COMO DESEJA LUTAR? ---");

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
        }

        for (int i = 0; i < descricoes.size(); i++) {
            System.out.println((i + 1) + ". " + descricoes.get(i));
        }
        System.out.println("0. Voltar");

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
            return new int[]{1, alvo, armaIdx, -1};
        }

        if (acao[0] == 2) {
            int habIdx = EscolherHabilidadeAtiva(ficha);
            if (habIdx == -1) return null;
            habilidades.Habilidade habEscolhida = ficha.getHabilidades().get(habIdx);
            int alvo = -1;
            if (habEscolhida instanceof habilidades.Magia || habEscolhida.getNome().equals("Prisão")) {
                alvo = escolherAlvo(inimigos);
                if (alvo == -1) return null;
            }
            return new int[]{2, alvo, -1, habIdx};
        }

        if (acao[0] == 3) {
            habilidades.Habilidade hab = ficha.getHabilidades().get(acao[1]);
            ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
            aplicaPassiva(ficha, hab, cascaGrossaAtiva);
            Interface.MostrarMensagem("\nVocê ativa " + hab.getNome() + "!");
            Interface.MostrarMensagem(hab.getDescricao());
            Interface.Pausa(2000);
            return new int[]{5, -1, -1, -1};
        }

        // Magia Proibida: não gasta a ação (o menu reabre para escolher a ação real)
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

    // Escolhe o alvo entre os inimigos vivos
    private static int escolherAlvo(List<Criatura> inimigos) {
        List<Criatura> vivos = new ArrayList<>();
        for (Criatura c : inimigos) {
            if (c.getVida() > 0) {
                vivos.add(c);
            }
        }

        if (vivos.isEmpty()) return -1;
        if (vivos.size() == 1) return inimigos.indexOf(vivos.get(0));

        System.out.println("\n--- ESCOLHA SEU ALVO ---");
        for (int i = 0; i < vivos.size(); i++) {
            Criatura c = vivos.get(i);
            System.out.println((i + 1) + ". " + rotuloCriatura(inimigos, c) + " (Vida: " + c.getVida() + ")");
        }
        System.out.println("0. Voltar");

        int escolha = Interface.lerInteiro();


        if (escolha < 1 || escolha > vivos.size()) return -1;

        return inimigos.indexOf(vivos.get(escolha - 1));
    }

    private static List<Criatura> inimigosVivos(List<Criatura> inimigos) {
        List<Criatura> vivos = new ArrayList<>();
        for (Criatura c : inimigos) {
            if (c.getVida() > 0) {
                vivos.add(c);
            }
        }
        return vivos;
    }

    private static String rotuloCriatura(List<Criatura> inimigos, Criatura alvo) {
        int contagem = 0;
        for (Criatura c : inimigos) {
            if (c.getNome().equals(alvo.getNome())) {
                contagem++;
            }
        }
        if (contagem <= 1) return alvo.getNome();
        return alvo.getNome() + " " + (inimigos.indexOf(alvo) + 1);
    }

    private static String nomesDosInimigos(List<Criatura> inimigos) {
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

    private static int EscolherArma(FichaRpg ficha) {
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

        Interface.barraDivisoria();
        System.out.println("\n--- ESCOLHA SUA ARMA ---");
        for (int i = 0; i < armas.size(); i++) {
            Arma arma = armas.get(i);
            String extra = ehFlecha.get(i) ? " (Flechas: " + getQtdFlechas(ficha) + ")" : "";
            String atributoMostrado = arma.isAgil() ? "Ágil (Força/Destreza)" : arma.getAtributoAtaque();
            System.out.println((i + 1) + ". " + arma.getNome() + " (" + arma.getQuantidadeDanoArma() + "d" + arma.getDadoDanoArma() + " - " + arma.getTipoArma() + " - " + atributoMostrado + ")" + extra);
        }
        System.out.println((armas.size() + 1) + ". " + socoNome + " (" + socoQtd + "d" + socoDado + " - CaC - Força)");
        System.out.println("0. Voltar");

        int escolha = Interface.lerInteiro();


        if (escolha == 0) return -1;
        if (escolha < 1 || escolha > armas.size() + 1) return -1;

        if (escolha <= armas.size()) {
            return ficha.getInventario().indexOf(armas.get(escolha - 1));
        } else {
            return -2;
        }
    }

    private static boolean temFlechas(FichaRpg ficha) {
        for (ItemRpg item : ficha.getInventario()) {
            if (item.getNome().equals("Flechas") && item.getQuantidade() > 0) {
                return true;
            }
        }
        return false;
    }

    private static int getQtdFlechas(FichaRpg ficha) {
        for (ItemRpg item : ficha.getInventario()) {
            if (item.getNome().equals("Flechas")) {
                return item.getQuantidade();
            }
        }
        return 0;
    }

    private static void consumirFlecha(FichaRpg ficha) {
        for (int i = 0; i < ficha.getInventario().size(); i++) {
            ItemRpg item = ficha.getInventario().get(i);
            if (item.getNome().equals("Flechas")) {
                item.setQuantidade(item.getQuantidade() - 1);
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

    private static boolean executarAtaqueComArma(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, int armaIndex) {
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return false;
        Criatura inimigo = inimigos.get(alvoIndex);

        // Defesa Absoluta do Guerreiro: o bônus some ao realizar um ataque
        if (ficha.isDefesaAbsolutaAtiva()) {
            ficha.setDefesaAbsolutaAtiva(false);
            Interface.MostrarMensagem("(Sua Defesa Absoluta se dissipa ao atacar!)");
            Interface.Pausa(1500);
        }

        Interface.MostrarMensagem("\nVocê prepara seu ataque contra " + rotuloCriatura(inimigos, inimigo) + "...");
        Interface.Pausa(1500);

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

            if (totalAtaque >= inimigo.getDefesa()) {
                Interface.MostrarMensagem("-> Acertou! (defesa do alvo: " + inimigo.getDefesa() + ")");
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

            if (totalAtaque >= inimigo.getDefesa()) {
                Interface.MostrarMensagem("-> Acertou! (defesa do alvo: " + inimigo.getDefesa() + ")");
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
            } else {
                Interface.MostrarMensagem("-> Errou! (defesa do alvo: " + inimigo.getDefesa() + ")");
                Interface.Pausa(1500);
            }

            if (armaEscolhida.getTipoArma().contains("LA")) {
                consumirFlecha(ficha);
            }
        } else {
            return false;
        }

        if (dano > 0) {
            if (ficha.isEspadaAfiadaAtiva() && armaIndex >= 0) {
                int bonusAfiada = MecanicasRpg.rolarDado(8) + MecanicasRpg.rolarDado(8);
                dano += bonusAfiada;
                Interface.MostrarMensagem("(Espada Afiada! +" + bonusAfiada + " de dano)");
                Interface.Pausa(1500);
            }
            inimigo.setVida(inimigo.getVida() - dano);
            Interface.MostrarMensagem(rotuloCriatura(inimigos, inimigo) + " agora tem " + Math.max(0, inimigo.getVida()) + " de vida.");
            Interface.Pausa(2000);
        }

        aplicarVenenoCuraParaMorte(ficha, inimigos, alvoIndex);
        return dano > 0;
    }

    // ==================== HABILIDADES ====================

    private static int EscolherHabilidadeAtiva(FichaRpg ficha) {
        List<habilidades.Habilidade> ativas = new ArrayList<>();
        for (habilidades.Habilidade hab : ficha.getHabilidades()) {
            if (!hab.isPassiva()
                    && !hab.getNome().equals("Cura Reforçada")
                    && !hab.getNome().equals("Magia Proibida")) {
                ativas.add(hab);
            }
        }

        if (ativas.isEmpty()) {
            Interface.MostrarMensagem("\nVocê não possui habilidades ativas.");
            Interface.Pausa(1500);
            return -1;
        }

        Interface.barraDivisoria();
        System.out.println("\n--- SUAS HABILIDADES ---");
        for (int i = 0; i < ativas.size(); i++) {
            habilidades.Habilidade hab = ativas.get(i);
            String extra = "";
            if (hab instanceof habilidades.Magia) {
                habilidades.Magia magia = (habilidades.Magia) hab;
                if (magia.getDadoDano() > 0) {
                    extra = " - Dano: " + magia.getQuantidadeDano() + "d" + magia.getDadoDano();
                }
            }
            System.out.println((i + 1) + ". " + hab.getNome() + " (Custo: " + custoEfetivoMagia(ficha, hab) + " Mana)" + extra);
        }
        System.out.println("0. Voltar");

        int escolha = Interface.lerInteiro();


        if (escolha == 0) return -1;

        if (escolha < 1 || escolha > ativas.size()) {
            Interface.ExibirErro("Escolha inválida!");
            Interface.Pausa(1500);
            return -1;
        }

        habilidades.Habilidade habEscolhida = ativas.get(escolha - 1);

        if (ficha.getManaPersonagem() < habEscolhida.getCustoMana()) {
            Interface.ExibirErro("Mana insuficiente! Precisa de " + habEscolhida.getCustoMana() + " de mana.");
            Interface.Pausa(1500);
            return -1;
        }

        return ficha.getHabilidades().indexOf(habEscolhida);
    }

    // Custo de mana efetivo de uma habilidade (Pequeno Grimório reduz 1 no custo das magias)
    private static int custoEfetivoMagia(FichaRpg ficha, habilidades.Habilidade hab) {
        if (hab instanceof habilidades.Magia && ficha.temItem("Pequeno Grimório")) {
            return Math.max(1, hab.getCustoMana() - 1);
        }
        return hab.getCustoMana();
    }

    private static boolean executarHabilidadeEscolhida(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, int habilidadeIndex) {
        if (habilidadeIndex < 0 || habilidadeIndex >= ficha.getHabilidades().size()) return true;

        habilidades.Habilidade hab = ficha.getHabilidades().get(habilidadeIndex);

        if (ficha.getManaPersonagem() < custoEfetivoMagia(ficha, hab)) {
            Interface.ExibirErro("Mana insuficiente!");
            Interface.Pausa(1500);
            return true;
        }

        String nomeHab = hab.getNome();

        if (nomeHab.equals("Giro")) {
            return executarGiro(ficha, inimigos);
        }
        if (nomeHab.equals("Proteção Absoluta")) {
            return usarProtecaoAbsoluta(ficha, hab);
        }
        if (nomeHab.equals("Cura Total")) {
            Interface.MostrarMensagem("Cura Total só pode ser usada para reviver quem morreu em combate.");
            Interface.Pausa(1500);
            return true;
        }
        if (nomeHab.equals("Cura para a Morte")) {
            return usarCuraParaMorte(ficha, inimigos, hab);
        }
        if (nomeHab.equals("Estrondo")) {
            return executarEstrondo(ficha, inimigos, hab);
        }
        if (nomeHab.equals("Prisão")) {
            return usarPrisao(ficha, inimigos, alvoIndex, hab);
        }
        if (nomeHab.equals("Conhecimento Avassalador")) {
            return tentarConhecimentoAvassalador(ficha, inimigos);
        }
        if (nomeHab.equals("Semi Deus")) {
            return executarSemiDeus(ficha, hab);
        }
        if (nomeHab.equals("Poder Absoluto")) {
            return executarPoderAbsoluto(ficha, hab);
        }
        if (nomeHab.equals("Cura Absoluta")) {
            return executarCuraAbsoluta(ficha, hab);
        }
        if (nomeHab.equals("Cura Incessante")) {
            return executarCuraIncessante(ficha);
        }
        if (nomeHab.equals("Explosão de Poder")) {
            return executarExplosaoDePoder(ficha, inimigos);
        }

        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return true;
        Criatura inimigo = inimigos.get(alvoIndex);

        int custoPago = custoEfetivoMagia(ficha, hab);
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoPago);
        if (custoPago < hab.getCustoMana()) {
            Interface.MostrarMensagem("(Pequeno Grimório reduziu o custo da magia em 1!)");
            Interface.Pausa(1000);
        }
        Interface.MostrarMensagem("\nVocê usa " + hab.getNome() + "!");
        Interface.Pausa(1500);

        if (hab instanceof habilidades.Magia) {
            habilidades.Magia magia = (habilidades.Magia) hab;
            int quantidadeDano = magia.getQuantidadeDano();
            if (ficha.isPoderAbsolutoAtivo()) {
                quantidadeDano *= 2;
                Interface.MostrarMensagem("(Poder Absoluto dobra os dados de dano das suas magias!)");
                Interface.Pausa(1000);
            }
            StringBuilder roladas = new StringBuilder();
            int dano = 0;
            Interface.pressionarParaRolar();
            for (int i = 0; i < quantidadeDano; i++) {
                int dadoRolado = MecanicasRpg.rolarDado(magia.getDadoDano());
                dano += dadoRolado;
                if (roladas.length() > 0) roladas.append(" + ");
                roladas.append(dadoRolado);
            }
            Interface.MostrarMensagem("-> Dados Rolados: " + roladas + " = " + dano + " (Dano Mágico: " + quantidadeDano + "d" + magia.getDadoDano() + ")");
            Interface.Pausa(2000);

            if (ficha.temItem("Chapéu Mágico")) {
                dano += 3;
                Interface.MostrarMensagem("(Chapéu Mágico aumentou o dano em +3!)");
                Interface.Pausa(1000);
            }

            List<Criatura> afetados = new ArrayList<>();
            afetados.add(inimigo);
            if (magia.isAtaqueArea()) {
                if (alvoIndex - 1 >= 0) afetados.add(inimigos.get(alvoIndex - 1));
                if (alvoIndex + 1 < inimigos.size()) afetados.add(inimigos.get(alvoIndex + 1));
                StringBuilder nomes = new StringBuilder();
                for (Criatura afetado : afetados) {
                    if (afetado.getVida() > 0) {
                        if (nomes.length() > 0) nomes.append(", ");
                        nomes.append(rotuloCriatura(inimigos, afetado));
                    }
                }
                Interface.MostrarMensagem("-> Ataque em área! Atinge: " + nomes);
                Interface.Pausa(2000);
            }

            for (Criatura afetado : afetados) {
                if (afetado.getVida() <= 0) continue;
                afetado.setVida(afetado.getVida() - dano);
                Interface.MostrarMensagem(rotuloCriatura(inimigos, afetado) + " agora tem " + Math.max(0, afetado.getVida()) + " de vida.");
                Interface.Pausa(1500);
            }

            aplicarVenenoCuraParaMorte(ficha, inimigos, alvoIndex);
            return true;
        } else {
            Interface.MostrarMensagem(hab.getDescricao());
            Interface.Pausa(2000);
            return false;
        }
    }

    // ==================== NOVAS HABILIDADES ====================

    // Giro do Guerreiro: gasta 1 de mana por giro (máx. = Destreza), 1d10 de dano em área por giro
    private static boolean executarGiro(FichaRpg ficha, List<Criatura> inimigos) {
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

        for (int g = 1; g <= giros; g++) {
            int danoGiro = MecanicasRpg.rolarDado(10);
            Interface.MostrarMensagem("-> Giro " + g + ": " + danoGiro + " (1d10) de dano em área!");
            Interface.Pausa(1500);
            for (Criatura alvo : vivos) {
                if (alvo.getVida() <= 0) continue;
                alvo.setVida(alvo.getVida() - danoGiro);
                Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
            }
            Interface.Pausa(1500);
        }
        return true;
    }

    // Estrondo do Guerreiro: 5 de mana, 7d10 em área e não pode usar habilidades no próximo turno
    private static boolean executarEstrondo(FichaRpg ficha, List<Criatura> inimigos, habilidades.Habilidade hab) {
        ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
        ficha.setRodadasSemHabilidade(2);
        Interface.MostrarMensagem("\nVocê golpeia o chão com toda a sua força! A terra se ergue ao seu redor!");
        Interface.Pausa(1500);

        List<Criatura> vivos = inimigosVivos(inimigos);
        if (vivos.isEmpty()) return true;

        int dano = 0;
        Interface.pressionarParaRolar();
        for (int i = 0; i < 7; i++) {
            dano += MecanicasRpg.rolarDado(10);
        }
        Interface.MostrarMensagem("-> Estrondo: 7d10 = " + dano + " de dano em área!");
        Interface.Pausa(1500);

        for (Criatura alvo : vivos) {
            if (alvo.getVida() <= 0) continue;
            alvo.setVida(alvo.getVida() - dano);
            Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
        }
        Interface.MostrarMensagem("Você não poderá usar habilidades no próximo turno!");
        Interface.Pausa(1500);
        return true;
    }

    // Prisão do Mago: prende um inimigo até ele passar em um teste de d20 (15+) na vez dele
    private static boolean usarPrisao(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, habilidades.Habilidade hab) {
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) {
            Interface.MostrarMensagem("Nenhum alvo escolhido.");
            Interface.Pausa(1500);
            return true;
        }
        Criatura alvo = inimigos.get(alvoIndex);
        ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
        ficha.setPrisaoAtiva(alvo);
        Interface.MostrarMensagem("\nVocê prende " + rotuloCriatura(inimigos, alvo) + " em uma prisão de energia!");
        Interface.MostrarMensagem("Na vez dele, ele precisa tirar 15 ou mais em um d20 para se libertar.");
        Interface.Pausa(2000);
        return true;
    }

    // Semi Deus do Guerreiro (lvl 9): gasta TODA a mana, +50% de vida máxima, cura total e +4 dados CaC
    private static boolean executarSemiDeus(FichaRpg ficha, habilidades.Habilidade hab) {
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
    private static boolean executarPoderAbsoluto(FichaRpg ficha, habilidades.Habilidade hab) {
        if (ficha.isPoderAbsolutoAtivo()) {
            Interface.MostrarMensagem("O Poder Absoluto já está ativo!");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
        ficha.setPoderAbsolutoAtivo(true);
        Interface.MostrarMensagem("\nVocê se envolve na energia do seu elemento! Suas magias dobram de poder!");
        Interface.Pausa(2000);
        return true;
    }

    // Cura Absoluta do Healer (lvl 9): 10 de mana, cura total e vida bônus (dobra a vida, gasta-se primeiro)
    private static boolean executarCuraAbsoluta(FichaRpg ficha, habilidades.Habilidade hab) {
        ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
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
    private static boolean executarCuraIncessante(FichaRpg ficha) {
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
    private static boolean executarExplosaoDePoder(FichaRpg ficha, List<Criatura> inimigos) {
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
            alvo.setVida(alvo.getVida() - dano);
            Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
            Interface.Pausa(1500);
        }

        return true;
    }

    // Conhecimento Avassalador do Healer: teste de Intelecto (DC 15) revela as informações dos monstros
    private static boolean tentarConhecimentoAvassalador(FichaRpg ficha, List<Criatura> inimigos) {
        Interface.MostrarMensagem("\nVocê canaliza todo o seu conhecimento sobre as criaturas...");
        Interface.Pausa(1500);

        Interface.pressionarParaTeste("Intelecto");
        int dado = MecanicasRpg.rolarDado(20);
        int total = dado + ficha.getIntelecto();
        Interface.MostrarMensagem("-> Teste de Intelecto: " + dado + " (Dado) + " + ficha.getIntelecto() + " (Intelecto) = " + total + " (Dificuldade: 15)");
        Interface.Pausa(1500);

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
    private static boolean usarProtecaoAbsoluta(FichaRpg ficha, habilidades.Habilidade hab) {
        if (ficha.isProtecaoAbsolutaAtiva()) {
            Interface.MostrarMensagem("A Proteção Absoluta já está ativa!");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
        ficha.setProtecaoAbsolutaAtiva(true);
        ficha.setBonusDefesaTemporario(ficha.getBonusDefesaTemporario() + 3);
        Interface.MostrarMensagem("\nVocê se envolve no seu elemento! +3 de defesa e reflete 2d8 de dano a quem te acertar.");
        Interface.Pausa(2000);
        return true;
    }

    // Cura para a Morte do Healer: injeta líquido mortal (ativa a partir do próximo turno)
    private static boolean usarCuraParaMorte(FichaRpg ficha, List<Criatura> inimigos, habilidades.Habilidade hab) {
        int alvoVeneno = escolherAlvo(inimigos);
        if (alvoVeneno < 0) {
            Interface.MostrarMensagem("Nenhum alvo escolhido.");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
        Criatura alvo = inimigos.get(alvoVeneno);
        ficha.setAlvoCuraParaMorte(alvo);
        ficha.setCuraParaMortePreparado(true);
        Interface.MostrarMensagem("\nVocê injeta o líquido mortal em " + rotuloCriatura(inimigos, alvo) + "! Ele age a partir do próximo turno.");
        Interface.Pausa(2000);
        return true;
    }

    // Aplica o dano do líquido mortal ao final de cada ataque contra o alvo envenenado
    private static void aplicarVenenoCuraParaMorte(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex) {
        if (!ficha.isCuraParaMorteAtivo() || ficha.getAlvoCuraParaMorte() == null) return;
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return;

        Criatura alvo = inimigos.get(alvoIndex);
        if (alvo.getVida() <= 0 || alvo != ficha.getAlvoCuraParaMorte()) return;

        int veneno = MecanicasRpg.rolarDado(8) + MecanicasRpg.rolarDado(8) + MecanicasRpg.rolarDado(8);
        alvo.setVida(alvo.getVida() - veneno);
        Interface.MostrarMensagem("(Cura para a Morte! O líquido mortal causa " + veneno + " de dano)");
        Interface.Pausa(1500);
    }

    // Tenta reviver o personagem com Cura Total (uma vez por combate)
    private static boolean tentarReviver(FichaRpg ficha) {
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

    private static boolean ehItemConsumivel(ItemRpg item) {
        if (!(item instanceof Consumivel)) return false;
        return !item.getNome().equals("Flechas");
    }

    // Fase de declaração da mochila: escolhe e confirma o item (sem aplicar ainda).
    // Retorna o índice do item, -1 para voltar ao menu principal ou -2 para declarar fuga.
    private static int escolherItemParaUsar(FichaRpg ficha) {
        while (true) {
            Interface.barraDivisoria();
            System.out.println("\n--- SUA MOCHILA ---");

            if (ficha.getInventario().isEmpty()) {
                System.out.println("Sua mochila está vazia.");
            } else {
                for (int i = 0; i < ficha.getInventario().size(); i++) {
                    ItemRpg item = ficha.getInventario().get(i);
                    String tipo = "";
                    if (ehItemConsumivel(item)) tipo = " [Consumível]";
                    else if (item instanceof Arma) tipo = " [Arma]";
                    else if (item.getNome().equals("Flechas")) tipo = " [Munição]";
                    System.out.println((i + 1) + ". " + item.getNome() + " (x" + item.getQuantidade() + ")" + tipo);
                }
            }
            System.out.println("0. Voltar ao combate");
            System.out.println("9. Tentar fugir do combate");

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
                    descExibida = "Pode ser usado para curar 1d4 de vida. Usos restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Poção de Mana")) {
                    descExibida = "Restaura 5 pontos de mana. Usos restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Poção Grande de Mana")) {
                    descExibida = "Restaura 7 pontos de mana. Usos restantes: " + itemEscolhido.getQuantidade();
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
                    if (itemEscolhido.getNome().equals("Kit Médico") && ficha.getVidaPersonagem() >= ficha.getVidaMaxima()) {
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

    private static boolean temHabilidade(FichaRpg ficha, String nome) {
        for (habilidades.Habilidade hab : ficha.getHabilidades()) {
            if (hab.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    // Fase de resolução: aplica o item escolhido quando chega a vez do jogador na iniciativa
    private static void usarItemNaVez(FichaRpg ficha, int itemIndex) {
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
        } else if (itemEscolhido.getNome().equals("Kit Médico")) {
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
        }

        itemEscolhido.setQuantidade(itemEscolhido.getQuantidade() - 1);
        if (itemEscolhido.getQuantidade() <= 0) {
            ficha.getInventario().remove(itemEscolhido);
            Interface.MostrarMensagem("O item foi consumido e removido do inventário.");
        } else {
            Interface.MostrarMensagem("Restam " + itemEscolhido.getQuantidade() + "x " + itemEscolhido.getNome() + ".");
        }

        Interface.Pausa(2000);
    }

    // ==================== FUGA ====================

    private static int TentarFugir(FichaRpg ficha, List<Criatura> inimigos, int tentativasAtuais) {
        Interface.barraDivisoria();
        System.out.println("\nDeseja realmente tentar fugir?");
        System.out.println("1. Sim, tentar fugir");
        System.out.println("2. Não, voltar ao combate");

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
        int totalJogador = dadoJogador + ficha.getDestreza();
        Interface.MostrarMensagem("-> Sua Tentativa de Fuga: " + dadoJogador + " (Dado) + " + ficha.getDestreza() + " (Destreza) = " + totalJogador + " (Dificuldade: " + dificuldadeFuga + ")");
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
}