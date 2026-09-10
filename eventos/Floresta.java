package eventos;

import criaturas.Criatura;
import fichas.FichaRpg;
import itens.Arma;
import itens.Consumivel;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;
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

        // 20% de chance de encontrar uma Fada
        if (MecanicasRpg.rolarDado(100) <= 20) {
            EncontrarFada(ficha);
            return;
        }

        // Sorteia o tipo de criatura (1 = Lobo, 2 = Urso, 3 = Bandido)
        int tipo = MecanicasRpg.rolarDado(3);
        List<Criatura> inimigos = criarGrupoMonstros(tipo);
        Criatura referencia = inimigos.get(0);

        int dadoPresenca = MecanicasRpg.rolarDado(20);
        int totalPresenca = dadoPresenca + ficha.getPresenca();
        Interface.MostrarMensagem("-> Teste de Presença: " + dadoPresenca + " (Dado) + " + ficha.getPresenca() + " (Atributo) = " + totalPresenca + " (Dificuldade: " + referencia.getTestePresenca() + ")");
        Interface.Pausa(2500);

        if (totalPresenca >= referencia.getTestePresenca()) {
            Interface.MostrarMensagem("\n" + nomesDosInimigos(inimigos) + " apareceu entre as sombras das árvores e você o avistou antes!");
            Interface.Pausa(2500);

            System.out.println("O que deseja fazer?");
            System.out.println("1. Lutar (Você terá +2 de Iniciativa extra por surpreendê-lo)");
            System.out.println("2. Tentar Fugir furtivamente");
            int escolha = Interface.scanner.nextInt();
            Interface.scanner.nextLine();

            if (escolha == 1) {
                Interface.MostrarMensagem("\nVocê saca sua arma e parte para cima!");
                Interface.Pausa(2500);
                IniciarCombate(ficha, inimigos, true);
            } else if (escolha == 2) {
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
        c.adicionarAtaque("Mordida", "", 1, 6);
        c.adicionarAtaque("Aranhão", "", 2, 4);
        c.adicionarDrop("Couro", 1, 2, 40);
        return c;
    }

    private static Criatura criarUrso() {
        Criatura c = new Criatura("Urso", 3, 35, 7, 0);
        c.setBonusAcerto(1);
        c.setTestePresenca(5);
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
        c.adicionarAtaque("Brilho Cintilante", "luz", 1, 6);
        c.adicionarDrop("Brilho Mágico", 1, 1, 100);
        return c;
    }

    // ==================== ENCONTRO COM A FADA ====================

    private static void EncontrarFada(FichaRpg ficha) {
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
        int escolha = Interface.scanner.nextInt();
        Interface.scanner.nextLine();

        if (escolha == 1) {
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
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int iniciativaJogador = dadoJogador + ficha.getDestreza() + bonusIniciativaJogador;

        Interface.MostrarMensagem("-> Iniciativa [" + ficha.getNomePersonagem() + "]: " + dadoJogador + " (Dado) + " + ficha.getDestreza() + " (Destreza) + " + bonusIniciativaJogador + " (Bônus) = " + iniciativaJogador);
        Interface.Pausa(2500);

        int melhorIniciativaInimiga = -1000;
        Criatura maisRapido = null;
        for (Criatura c : inimigos) {
            int dadoInimigo = MecanicasRpg.rolarDado(20);
            int iniciativaInimigo = dadoInimigo + c.getIniciativa();
            Interface.MostrarMensagem("-> Iniciativa [" + rotuloCriatura(inimigos, c) + "]: " + dadoInimigo + " (Dado) + " + c.getIniciativa() + " (Iniciativa Base) = " + iniciativaInimigo);
            if (iniciativaInimigo > melhorIniciativaInimiga) {
                melhorIniciativaInimiga = iniciativaInimigo;
                maisRapido = c;
            }
            Interface.Pausa(1500);
        }

        if (iniciativaJogador >= melhorIniciativaInimiga) {
            Interface.MostrarMensagem("\n" + ficha.getNomePersonagem() + " é mais ágil e age primeiro!");
        } else {
            Interface.MostrarMensagem("\n" + rotuloCriatura(inimigos, maisRapido) + " é mais rápido e age primeiro!");
        }
        Interface.Pausa(2500);

        RodadasDeCombate(ficha, inimigos, iniciativaJogador >= melhorIniciativaInimiga);
    }

    // ==================== RODADAS DE COMBATE ====================

    private static void RodadasDeCombate(FichaRpg ficha, List<Criatura> inimigos, boolean jogadorMaisRapido) {
        int tentativasFuga = 0;
        boolean cascaGrossaAtiva = false;

        while (ficha.getVidaPersonagem() > 0 && !inimigosVivos(inimigos).isEmpty()) {
            Interface.barraDivisoria();
            Interface.MostrarMensagem("Sua Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + " | Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
            Interface.MostrarMensagem("Inimigos:");
            for (int i = 0; i < inimigos.size(); i++) {
                Criatura c = inimigos.get(i);
                if (c.getVida() > 0) {
                    Interface.MostrarMensagem("  " + (i + 1) + ". " + c.getNome() + " (Vida: " + c.getVida() + ")");
                }
            }
            if (tentativasFuga > 0) {
                Interface.MostrarMensagem("Tentativas de fuga: " + tentativasFuga + "/3");
            }
            Interface.Pausa(1500);

            cascaGrossaAtiva = false;
            int tipoAcao = -1;
            int alvoIndex = -1;
            int armaEscolhidaIndex = -1;
            int habilidadeEscolhidaIndex = -1;

            System.out.println("\nO que deseja fazer?");
            System.out.println("1. Lutar");
            System.out.println("2. Abrir Mochila");
            System.out.println("3. Tentar Fugir");
            System.out.println("4. Ver Ficha");

            int escolha = Interface.scanner.nextInt();
            Interface.scanner.nextLine();

            if (escolha == 4) {
                Interface.MostrarFicha(ficha);
                Interface.Pausa(1500);
                continue;
            }

            if (escolha == 1) {
                int[] resultado = MenuLutarComEscolha(ficha, inimigos, cascaGrossaAtiva);
                if (resultado != null) {
                    tipoAcao = resultado[0];
                    alvoIndex = resultado[1];
                    armaEscolhidaIndex = resultado[2];
                    habilidadeEscolhidaIndex = resultado[3];
                    if (tipoAcao == 5) {
                        cascaGrossaAtiva = true;
                        resultado = MenuLutarComEscolha(ficha, inimigos, true);
                        if (resultado != null) {
                            tipoAcao = resultado[0];
                            alvoIndex = resultado[1];
                            armaEscolhidaIndex = resultado[2];
                            habilidadeEscolhidaIndex = resultado[3];
                        } else {
                            tipoAcao = -1;
                        }
                    }
                }
            } else if (escolha == 2) {
                tipoAcao = AbrirMochilaCombate(ficha);
            } else if (escolha == 3) {
                int resultadoFuga = TentarFugir(ficha, inimigos, tentativasFuga);
                if (resultadoFuga == -1) {
                    tipoAcao = 4;
                } else if (resultadoFuga == 3) {
                    Interface.MostrarMensagem("\nVocê conseguiu escapar da floresta!");
                    Interface.Pausa(2500);
                    return;
                } else {
                    tentativasFuga = resultadoFuga;
                    tipoAcao = 3;
                }
            } else {
                Interface.ExibirErro("Escolha inválida! Você hesitou e perdeu a vez!");
                Interface.Pausa(1500);
            }

            boolean playerAtacou = (tipoAcao == 1 || tipoAcao == 2);

            if (playerAtacou) {
                if (jogadorMaisRapido) {
                    executarAcaoJogador(ficha, inimigos, tipoAcao, alvoIndex, armaEscolhidaIndex, habilidadeEscolhidaIndex);
                    atacarTodosInimigos(inimigos, ficha, cascaGrossaAtiva);
                } else {
                    atacarTodosInimigos(inimigos, ficha, cascaGrossaAtiva);
                    if (ficha.getVidaPersonagem() > 0) {
                        executarAcaoJogador(ficha, inimigos, tipoAcao, alvoIndex, armaEscolhidaIndex, habilidadeEscolhidaIndex);
                    }
                }
            } else if (tipoAcao == 4) {
                if (ficha.getVidaPersonagem() > 0) {
                    atacarTodosInimigos(inimigos, ficha, false);
                }
            }
        }

        Interface.barraDivisoria();
        if (ficha.getVidaPersonagem() <= 0) {
            Interface.MostrarMensagem("\nVocê foi derrotado... A floresta recupera o silêncio.");
            Interface.Pausa(3000);
        } else {
            Interface.MostrarMensagem("\nVocê derrotou todos os inimigos!");
            Interface.Pausa(2500);
            for (Criatura c : inimigos) {
                if (c.getVida() <= 0) {
                    c.processarDrops(ficha);
                }
            }
        }
        Interface.barraDivisoria();
    }

    // Todos os inimigos vivos atacam o jogador
    private static void atacarTodosInimigos(List<Criatura> inimigos, FichaRpg ficha, boolean cascaGrossaAtiva) {
        for (Criatura c : inimigos) {
            if (c.getVida() > 0 && ficha.getVidaPersonagem() > 0) {
                Interface.MostrarMensagem("\n" + rotuloCriatura(inimigos, c) + " avança para atacar!");
                Interface.Pausa(1500);
                c.atacarJogador(ficha, cascaGrossaAtiva);
            }
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
                int escolha = Interface.scanner.nextInt();
                Interface.scanner.nextLine();

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

    private static int[] MenuLutarComEscolha(FichaRpg ficha, List<Criatura> inimigos, boolean cascaGrossaAtiva) {
        Interface.barraDivisoria();
        System.out.println("\n--- COMO DESEJA LUTAR? ---");
        System.out.println("1. Atacar com Arma");
        System.out.println("2. Usar Habilidade");

        for (int i = 0; i < ficha.getHabilidades().size(); i++) {
            habilidades.Habilidade hab = ficha.getHabilidades().get(i);
            if (hab.isPassiva() && !cascaGrossaAtiva && ficha.getManaPersonagem() >= hab.getCustoMana()) {
                System.out.println("3. " + hab.getNome() + " (Custo: " + hab.getCustoMana() + " Mana) - Ainda pode atacar após usar");
            }
        }

        System.out.println("0. Voltar");

        int escolha = Interface.scanner.nextInt();
        Interface.scanner.nextLine();

        if (escolha == 1) {
            int armaIdx = EscolherArma(ficha);
            if (armaIdx == -1) return null;
            int alvo = escolherAlvo(inimigos);
            if (alvo == -1) return null;
            return new int[]{1, alvo, armaIdx, -1};
        } else if (escolha == 2) {
            int habIdx = EscolherHabilidadeAtiva(ficha);
            if (habIdx == -1) return null;
            int alvo = escolherAlvo(inimigos);
            if (alvo == -1) return null;
            return new int[]{2, alvo, -1, habIdx};
        } else if (escolha == 3) {
            for (int i = 0; i < ficha.getHabilidades().size(); i++) {
                habilidades.Habilidade hab = ficha.getHabilidades().get(i);
                if (hab.isPassiva() && !cascaGrossaAtiva && ficha.getManaPersonagem() >= hab.getCustoMana()) {
                    ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
                    Interface.MostrarMensagem("\nVocê ativa " + hab.getNome() + "!");
                    Interface.MostrarMensagem(hab.getDescricao());
                    Interface.Pausa(2000);
                    return new int[]{5, -1, -1, -1};
                }
            }
            return null;
        } else {
            return null;
        }
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

        int escolha = Interface.scanner.nextInt();
        Interface.scanner.nextLine();

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
            System.out.println((i + 1) + ". " + arma.getNome() + " (" + arma.getQuantidadeDanoArma() + "d" + arma.getDadoDanoArma() + " - " + arma.getTipoArma() + " - " + arma.getAtributoAtaque() + ")" + extra);
        }
        System.out.println((armas.size() + 1) + ". " + socoNome + " (" + socoQtd + "d" + socoDado + " - CaC - Força)");
        System.out.println("0. Voltar");

        int escolha = Interface.scanner.nextInt();
        Interface.scanner.nextLine();

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

            dadoAtaque = MecanicasRpg.rolarDado(20);
            totalAtaque = dadoAtaque + atributoBonus;
            boolean critico = dadoAtaque == 20;
            Interface.MostrarMensagem("-> Ataque [" + socoNome + "]: " + dadoAtaque + " (Dado) + " + atributoBonus + " (" + nomeAtributo + ") = " + totalAtaque + (critico ? " [CRÍTICO!]" : ""));
            if (critico) {
                Interface.MostrarMensagem("Golpe crítico! O dano de dados será dobrado!");
            }
            Interface.Pausa(2000);

            if (totalAtaque >= inimigo.getDefesa()) {
                int dadosTotais = socoQtd * (critico ? 2 : 1);
                for (int i = 0; i < dadosTotais; i++) {
                    dano += MecanicasRpg.rolarDado(socoDado);
                }
                dano += atributoBonus;
            }
        } else if (armaIndex >= 0 && armaIndex < ficha.getInventario().size()) {
            Arma armaEscolhida = (Arma) ficha.getInventario().get(armaIndex);
            String atributo = armaEscolhida.getAtributoAtaque();
            atributoBonus = atributo.equals("Destreza") ? ficha.getDestreza() : ficha.getForca();
            nomeAtributo = atributo;

            dadoAtaque = MecanicasRpg.rolarDado(20);
            totalAtaque = dadoAtaque + atributoBonus;
            boolean critico = dadoAtaque == 20;
            Interface.MostrarMensagem("-> Ataque [" + armaEscolhida.getNome() + "]: " + dadoAtaque + " (Dado) + " + atributoBonus + " (" + nomeAtributo + ") = " + totalAtaque + (critico ? " [CRÍTICO!]" : ""));
            if (critico) {
                Interface.MostrarMensagem("Golpe crítico! O dano de dados será dobrado!");
            }
            Interface.Pausa(2000);

            if (totalAtaque >= inimigo.getDefesa()) {
                int dadosTotais = armaEscolhida.getQuantidadeDanoArma() * (critico ? 2 : 1);
                for (int i = 0; i < dadosTotais; i++) {
                    dano += MecanicasRpg.rolarDado(armaEscolhida.getDadoDanoArma());
                }
                dano += atributoBonus;
            }

            if (armaEscolhida.getTipoArma().contains("LA")) {
                consumirFlecha(ficha);
            }
        } else {
            return false;
        }

        if (dano > 0) {
            Interface.MostrarMensagem("-> Acertou! Dano: " + dano + " (defesa do alvo: " + inimigo.getDefesa() + ")");
            Interface.Pausa(2000);
            inimigo.setVida(inimigo.getVida() - dano);
            Interface.MostrarMensagem(rotuloCriatura(inimigos, inimigo) + " agora tem " + Math.max(0, inimigo.getVida()) + " de vida.");
            Interface.Pausa(2000);
            return true;
        } else {
            Interface.MostrarMensagem("-> Errou! (defesa do alvo: " + inimigo.getDefesa() + ")");
            Interface.Pausa(1500);
            return false;
        }
    }

    // ==================== HABILIDADES ====================

    private static int EscolherHabilidadeAtiva(FichaRpg ficha) {
        List<habilidades.Habilidade> ativas = new ArrayList<>();
        for (habilidades.Habilidade hab : ficha.getHabilidades()) {
            if (!hab.isPassiva()) {
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
            System.out.println((i + 1) + ". " + hab.getNome() + " (Custo: " + hab.getCustoMana() + " Mana)");
        }
        System.out.println("0. Voltar");

        int escolha = Interface.scanner.nextInt();
        Interface.scanner.nextLine();

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

    private static boolean executarHabilidadeEscolhida(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, int habilidadeIndex) {
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return true;
        if (habilidadeIndex < 0 || habilidadeIndex >= ficha.getHabilidades().size()) return true;
        Criatura inimigo = inimigos.get(alvoIndex);

        habilidades.Habilidade hab = ficha.getHabilidades().get(habilidadeIndex);

        if (ficha.getManaPersonagem() < hab.getCustoMana()) {
            Interface.ExibirErro("Mana insuficiente!");
            Interface.Pausa(1500);
            return true;
        }

        ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
        Interface.MostrarMensagem("\nVocê conjura " + hab.getNome() + "!");
        Interface.Pausa(1500);

        if (hab instanceof habilidades.Magia) {
            int dano = 0;
            dano += MecanicasRpg.rolarDado(8);
            dano += MecanicasRpg.rolarDado(8);
            Interface.MostrarMensagem("-> Dano Mágico: " + dano + " causado em " + rotuloCriatura(inimigos, inimigo) + "!");
            Interface.Pausa(2000);
            inimigo.setVida(inimigo.getVida() - dano);
            Interface.MostrarMensagem(rotuloCriatura(inimigos, inimigo) + " agora tem " + Math.max(0, inimigo.getVida()) + " de vida.");
            Interface.Pausa(2000);
            return true;
        } else {
            Interface.MostrarMensagem(hab.getDescricao());
            Interface.Pausa(2000);
            return false;
        }
    }

    // ==================== MOCHILA ====================

    private static boolean ehItemConsumivel(ItemRpg item) {
        if (!(item instanceof Consumivel)) return false;
        return !item.getNome().equals("Flechas");
    }

    private static int AbrirMochilaCombate(FichaRpg ficha) {
        while (true) {
            Interface.barraDivisoria();
            System.out.println("\n--- SUA MOCHILA ---");

            if (ficha.getInventario().isEmpty()) {
                System.out.println("Sua mochila está vazia.");
                System.out.println("\n0. Voltar");
                Interface.scanner.nextInt();
                Interface.scanner.nextLine();
                return -1;
            }

            for (int i = 0; i < ficha.getInventario().size(); i++) {
                ItemRpg item = ficha.getInventario().get(i);
                String tipo = "";
                if (ehItemConsumivel(item)) tipo = " [Consumível]";
                else if (item instanceof Arma) tipo = " [Arma]";
                else if (item.getNome().equals("Flechas")) tipo = " [Munição]";
                System.out.println((i + 1) + ". " + item.getNome() + " (x" + item.getQuantidade() + ")" + tipo);
            }
            System.out.println("0. Voltar");

            int escolha = Interface.scanner.nextInt();
            Interface.scanner.nextLine();

            if (escolha == 0) return -1;

            if (escolha > 0 && escolha <= ficha.getInventario().size()) {
                ItemRpg itemEscolhido = ficha.getInventario().get(escolha - 1);

                String descExibida = itemEscolhido.getDescricao();
                if (itemEscolhido.getNome().equals("Kit Médico")) {
                    descExibida = "Pode ser usado para curar 1d4 de vida. Usos restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Poção de Mana")) {
                    descExibida = "Restaura 5 pontos de mana. Usos restantes: " + itemEscolhido.getQuantidade();
                }
                System.out.println("\n" + itemEscolhido.getNome() + ": " + descExibida);
                Interface.Pausa(1000);

                if (ehItemConsumivel(itemEscolhido)) {
                    if (itemEscolhido.getNome().equals("Poção de Mana") && ficha.getManaPersonagem() >= ficha.getManaMaxima()) {
                        Interface.MostrarMensagem("Sua mana já está no máximo!");
                        Interface.Pausa(1500);
                        continue;
                    }
                    if (itemEscolhido.getNome().equals("Kit Médico") && ficha.getVidaPersonagem() >= ficha.getVidaMaxima()) {
                        Interface.MostrarMensagem("Sua vida já está no máximo!");
                        Interface.Pausa(1500);
                        continue;
                    }

                    System.out.println("\nDeseja usar este item? (Usará sua ação)");
                    System.out.println("1. Sim");
                    System.out.println("2. Não");
                    int confirmar = Interface.scanner.nextInt();
                    Interface.scanner.nextLine();

                    if (confirmar == 1) {
                        if (itemEscolhido.getNome().equals("Poção de Mana")) {
                            ficha.setManaPersonagem(ficha.getManaPersonagem() + 5);
                            Interface.MostrarMensagem("Você recuperou 5 de mana! Mana atual: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
                        } else if (itemEscolhido.getNome().equals("Kit Médico")) {
                            int cura = MecanicasRpg.rolarDado(4);
                            ficha.setVidaPersonagem(ficha.getVidaPersonagem() + cura);
                            Interface.MostrarMensagem("Você recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                        }

                        itemEscolhido.setQuantidade(itemEscolhido.getQuantidade() - 1);
                        if (itemEscolhido.getQuantidade() <= 0) {
                            ficha.getInventario().remove(itemEscolhido);
                            Interface.MostrarMensagem("O item foi consumido e removido do inventário.");
                        } else {
                            Interface.MostrarMensagem("Restam " + itemEscolhido.getQuantidade() + "x " + itemEscolhido.getNome() + ".");
                        }

                        Interface.Pausa(2000);
                        return 3;
                    }
                } else {
                    Interface.MostrarMensagem("Item não é consumível. Apenas visualização.");
                    Interface.Pausa(1500);
                }
            }
        }
    }

    // ==================== FUGA ====================

    private static int TentarFugir(FichaRpg ficha, List<Criatura> inimigos, int tentativasAtuais) {
        Interface.barraDivisoria();
        System.out.println("\nDeseja realmente tentar fugir?");
        System.out.println("1. Sim, tentar fugir");
        System.out.println("2. Não, voltar ao combate");

        int confirmar = Interface.scanner.nextInt();
        Interface.scanner.nextLine();

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