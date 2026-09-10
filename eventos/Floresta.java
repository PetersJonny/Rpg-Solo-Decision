package eventos;

import criaturas.Criatura;
import fichas.FichaRpg;
import itens.Arma;
import itens.Consumivel;
import itens.ItemRpg;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class Floresta {

    public static void Explorar(FichaRpg ficha) {
        Interface.barraDivisoria();
        Interface.MostrarMensagem("\nVocê adentra as matas geladas da floresta de Freijord...");
        Interface.Pausa(2500);
        EventoAnimal(ficha);
    }

    private static void EventoAnimal(FichaRpg ficha) {
        Criatura animal = new Criatura("Lobo Selvagem", 1, 14, 10, 3);
        animal.adicionarAtaque("Mordida", 1, 6);
        animal.adicionarAtaque("Aranhão", 2, 4);

        Interface.MostrarMensagem("\nAlgo se move por entre as árvores...");
        Interface.Pausa(2500);

        int dadoPresenca = MecanicasRpg.rolarDado(20);
        int totalPresenca = dadoPresenca + ficha.getPresenca();
        Interface.MostrarMensagem("-> Teste de Presença: " + dadoPresenca + " (Dado) + " + ficha.getPresenca() + " (Atributo) = " + totalPresenca);
        Interface.Pausa(2500);

        if (totalPresenca > 8 && animal.getNivel() <= 2) {
            Interface.MostrarMensagem("\nUm Lobo Selvagem apareceu entre as sombras das árvores!");
            Interface.Pausa(2500);

            System.out.println("O que deseja fazer?");
            System.out.println("1. Lutar (Você terá +2 de Iniciativa extra por surpreendê-lo)");
            System.out.println("2. Tentar Fugir furtivamente");
            int escolha = Interface.scanner.nextInt();
            Interface.scanner.nextLine();

            if (escolha == 1) {
                Interface.MostrarMensagem("\nVocê saca sua arma e parte para cima do Lobo!");
                Interface.Pausa(2500);
                IniciarCombate(ficha, animal, true);
            } else if (escolha == 2) {
                int dadoDestreza = MecanicasRpg.rolarDado(20);
                int totalDestreza = dadoDestreza + ficha.getDestreza();
                Interface.MostrarMensagem("-> Teste de Destreza (Fuga): " + dadoDestreza + " (Dado) + " + ficha.getDestreza() + " (Atributo) = " + totalDestreza);
                Interface.Pausa(2500);
                if (totalDestreza >= 12) {
                    Interface.MostrarMensagem("\nVocê recua lentamente pelas sombras e foge com sucesso, sem ser notado.");
                    Interface.Pausa(2500);
                } else {
                    Interface.MostrarMensagem("\nVocê pisa em um galho seco! O Lobo percebe você e avança!");
                    Interface.Pausa(2500);
                    IniciarCombate(ficha, animal, false);
                }
            } else {
                Interface.ExibirErro("Escolha inválida! Você hesitou e a criatura te notou!");
                Interface.Pausa(2500);
                IniciarCombate(ficha, animal, false);
            }
        } else {
            Interface.MostrarMensagem("\nUm Lobo Selvagem saltou das sombras e te surpreendeu!");
            Interface.Pausa(2500);
            IniciarCombate(ficha, animal, false);
        }
    }

    private static void IniciarCombate(FichaRpg ficha, Criatura inimigo, boolean jogadorSurpreendeu) {
        Interface.MostrarMensagem("\n================ COMBATE ================");
        Interface.Pausa(2500);

        int bonusIniciativaJogador = jogadorSurpreendeu ? 2 : 0;
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int iniciativaJogador = dadoJogador + ficha.getDestreza() + bonusIniciativaJogador;
        int dadoInimigo = MecanicasRpg.rolarDado(20);
        int iniciativaInimigo = dadoInimigo + inimigo.getIniciativa();

        Interface.MostrarMensagem("-> Iniciativa [" + ficha.getNomePersonagem() + "]: " + dadoJogador + " (Dado) + " + ficha.getDestreza() + " (Destreza) + " + bonusIniciativaJogador + " (Bônus) = " + iniciativaJogador);
        Interface.Pausa(2500);
        Interface.MostrarMensagem("-> Iniciativa [" + inimigo.getNome() + "]: " + dadoInimigo + " (Dado) + " + inimigo.getIniciativa() + " (Iniciativa Base) = " + iniciativaInimigo);
        Interface.Pausa(2500);

        if (iniciativaJogador >= iniciativaInimigo) {
            Interface.MostrarMensagem("\n" + ficha.getNomePersonagem() + " é mais ágil e age primeiro!");
        } else {
            Interface.MostrarMensagem("\nO " + inimigo.getNome() + " é mais rápido e age primeiro!");
        }
        Interface.Pausa(2500);

        RodadasDeCombate(ficha, inimigo, iniciativaJogador >= iniciativaInimigo);
    }

    private static void RodadasDeCombate(FichaRpg ficha, Criatura inimigo, boolean jogadorMaisRapido) {
        int tentativasFuga = 0;
        int turno = 1;
        boolean cascaGrossaAtiva = false;

        while (ficha.getVidaPersonagem() > 0 && inimigo.getVida() > 0) {
            Interface.barraDivisoria();
            Interface.MostrarMensagem("Sua Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + " | Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
            Interface.MostrarMensagem("Vida do " + inimigo.getNome() + ": " + inimigo.getVida());
            if (tentativasFuga > 0) {
                Interface.MostrarMensagem("Tentativas de fuga: " + tentativasFuga + "/3");
            }
            Interface.Pausa(1500);

            cascaGrossaAtiva = false;
            int armaEscolhidaIndex = -1;
            int habilidadeEscolhidaIndex = -1;

            System.out.println("\nO que deseja fazer?");
            System.out.println("1. Lutar");
            System.out.println("2. Abrir Mochila");
            System.out.println("3. Tentar Fugir");

            int escolha = Interface.scanner.nextInt();
            Interface.scanner.nextLine();

            int tipoAcao = -1;

            if (escolha == 1) {
                int[] resultado = MenuLutarComEscolha(ficha, inimigo, cascaGrossaAtiva);
                tipoAcao = resultado[0];
                armaEscolhidaIndex = resultado[1];
                habilidadeEscolhidaIndex = resultado[2];
                if (tipoAcao == 5) {
                    cascaGrossaAtiva = true;
                    resultado = MenuLutarComEscolha(ficha, inimigo, cascaGrossaAtiva);
                    tipoAcao = resultado[0];
                    armaEscolhidaIndex = resultado[1];
                    habilidadeEscolhidaIndex = resultado[2];
                }
            } else if (escolha == 2) {
                tipoAcao = AbrirMochilaCombate(ficha);
            } else if (escolha == 3) {
                int resultadoFuga = TentarFugir(ficha, inimigo, tentativasFuga);
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

            // Execução
            boolean playerAtacou = (tipoAcao == 1 || tipoAcao == 2);

            if (playerAtacou) {
                if (jogadorMaisRapido) {
                    executarAcaoJogador(ficha, inimigo, tipoAcao, armaEscolhidaIndex, habilidadeEscolhidaIndex);
                    if (inimigo.getVida() > 0) {
                        Interface.MostrarMensagem("\nO Lobo revida!");
                        Interface.Pausa(1500);
                        inimigo.atacarJogador(ficha, cascaGrossaAtiva);
                    }
                } else {
                    if (inimigo.getVida() > 0) {
                        Interface.MostrarMensagem("\nO Lobo ataca antes de você!");
                        Interface.Pausa(1500);
                        inimigo.atacarJogador(ficha, cascaGrossaAtiva);
                    }
                    if (ficha.getVidaPersonagem() > 0) {
                        executarAcaoJogador(ficha, inimigo, tipoAcao, armaEscolhidaIndex, habilidadeEscolhidaIndex);
                    }
                }
            } else if (tipoAcao == 4) {
                if (ficha.getVidaPersonagem() > 0) {
                    inimigo.atacarJogador(ficha, false);
                }
            }

            turno++;
        }

        Interface.barraDivisoria();
        if (ficha.getVidaPersonagem() <= 0) {
            Interface.MostrarMensagem("\nVocê foi derrotado... O Lobo vagueia triunfante pela floresta.");
            Interface.Pausa(3000);
        } else if (inimigo.getVida() <= 0) {
            Interface.MostrarMensagem("\nVocê derrotou o Lobo Selvagem!");
            Interface.Pausa(3000);
        }
        Interface.barraDivisoria();
    }

    private static void executarAcaoJogador(FichaRpg ficha, Criatura inimigo, int tipoAcao, int armaIndex, int habIndex) {
        boolean sucesso;
        if (tipoAcao == 1) {
            sucesso = executarAtaqueComArma(ficha, inimigo, armaIndex);
        } else {
            sucesso = executarHabilidadeEscolhida(ficha, inimigo, habIndex);
        }

        if (!sucesso) {
            tentarConhecimentoAvancado(ficha, inimigo, tipoAcao, armaIndex, habIndex);
        }
    }

    // ==================== CONHECIMENTO AVANÇADO ====================

    private static void tentarConhecimentoAvancado(FichaRpg ficha, Criatura inimigo, int tipoAcao, int armaIndex, int habIndex) {
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
                        sucessoReroll = executarAtaqueComArma(ficha, inimigo, armaIndex);
                    } else {
                        sucessoReroll = executarHabilidadeEscolhida(ficha, inimigo, habIndex);
                    }

                    if (!sucessoReroll) {
                        Interface.MostrarMensagem("Mesmo com seu conhecimento, o ataque falhou.");
                        Interface.Pausa(1500);
                    }
                }
                return;
            }
        }
    }

    // ==================== MENU LUTAR ====================

    private static int[] MenuLutarComEscolha(FichaRpg ficha, Criatura inimigo, boolean cascaGrossaAtiva) {
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
            if (armaIdx == -1) return new int[]{-1, -1, -1};
            return new int[]{1, armaIdx, -1};
        } else if (escolha == 2) {
            int habIdx = EscolherHabilidadeAtiva(ficha);
            if (habIdx == -1) return new int[]{-1, -1, -1};
            return new int[]{2, -1, habIdx};
        } else if (escolha == 3) {
            for (int i = 0; i < ficha.getHabilidades().size(); i++) {
                habilidades.Habilidade hab = ficha.getHabilidades().get(i);
                if (hab.isPassiva() && !cascaGrossaAtiva && ficha.getManaPersonagem() >= hab.getCustoMana()) {
                    ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
                    Interface.MostrarMensagem("\nVocê ativa " + hab.getNome() + "!");
                    Interface.MostrarMensagem(hab.getDescricao());
                    Interface.Pausa(2000);
                    return new int[]{5, -1, -1};
                }
            }
            return new int[]{-1, -1, -1};
        } else {
            return new int[]{-1, -1, -1};
        }
    }

    // ==================== ESCOLHER ARMA ====================

    private static int EscolherArma(FichaRpg ficha) {
        java.util.List<Arma> armas = new java.util.ArrayList<>();
        java.util.List<Boolean> ehFlecha = new java.util.ArrayList<>();

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
        final String socoNomeF = socoNome;
        final int socoDadoF = socoDado;
        final int socoQtdF = socoQtd;

        Interface.barraDivisoria();
        System.out.println("\n--- ESCOLHA SUA ARMA ---");
        for (int i = 0; i < armas.size(); i++) {
            Arma arma = armas.get(i);
            String extra = ehFlecha.get(i) ? " (Flechas: " + getQtdFlechas(ficha) + ")" : "";
            System.out.println((i + 1) + ". " + arma.getNome() + " (" + arma.getQuantidadeDanoArma() + "d" + arma.getDadoDanoArma() + " - " + arma.getTipoArma() + ")" + extra);
        }
        System.out.println((armas.size() + 1) + ". " + socoNomeF + " (" + socoQtdF + "d" + socoDadoF + " - CaC)");
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

    private static boolean executarAtaqueComArma(FichaRpg ficha, Criatura inimigo, int armaIndex) {
        Interface.MostrarMensagem("\nVocê prepara seu ataque...");
        Interface.Pausa(1500);

        int dadoAtaque, totalAtaque, dano = 0;

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

            dadoAtaque = MecanicasRpg.rolarDado(20);
            totalAtaque = dadoAtaque + ficha.getForca();
            Interface.MostrarMensagem("-> Ataque [" + socoNome + "]: " + dadoAtaque + " (Dado) + " + ficha.getForca() + " (Força) = " + totalAtaque);
            Interface.Pausa(2000);

            if (totalAtaque >= inimigo.getDefesa()) {
                for (int i = 0; i < socoQtd; i++) {
                    dano += MecanicasRpg.rolarDado(socoDado);
                }
                dano += ficha.getForca();
            }
        } else if (armaIndex >= 0 && armaIndex < ficha.getInventario().size()) {
            Arma armaEscolhida = (Arma) ficha.getInventario().get(armaIndex);
            String tipoArma = armaEscolhida.getTipoArma();
            int atributoBonus = tipoArma.contains("CaC") ? ficha.getForca() : ficha.getDestreza();
            String nomeAtributo = tipoArma.contains("CaC") ? "Força" : "Destreza";

            dadoAtaque = MecanicasRpg.rolarDado(20);
            totalAtaque = dadoAtaque + atributoBonus;
            Interface.MostrarMensagem("-> Ataque [" + armaEscolhida.getNome() + "]: " + dadoAtaque + " (Dado) + " + atributoBonus + " (" + nomeAtributo + ") = " + totalAtaque);
            Interface.Pausa(2000);

            if (totalAtaque >= inimigo.getDefesa()) {
                for (int i = 0; i < armaEscolhida.getQuantidadeDanoArma(); i++) {
                    dano += MecanicasRpg.rolarDado(armaEscolhida.getDadoDanoArma());
                }
                dano += tipoArma.contains("CaC") ? ficha.getForca() : ficha.getDestreza();
            }

            if (tipoArma.contains("LA")) {
                consumirFlecha(ficha);
            }
        } else {
            return false;
        }

        if (dano > 0) {
            Interface.MostrarMensagem("-> Acertou! Dano: " + dano + " (defesa do alvo: " + inimigo.getDefesa() + ")");
            Interface.Pausa(2000);
            inimigo.setVida(inimigo.getVida() - dano);
            Interface.MostrarMensagem(inimigo.getNome() + " agora tem " + inimigo.getVida() + " de vida.");
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
        java.util.List<habilidades.Habilidade> ativas = new java.util.ArrayList<>();
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

    private static boolean executarHabilidadeEscolhida(FichaRpg ficha, Criatura inimigo, int habilidadeIndex) {
        if (habilidadeIndex < 0 || habilidadeIndex >= ficha.getHabilidades().size()) {
            return true;
        }

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
            Interface.MostrarMensagem("-> Dano Mágico: " + dano + " (2d8)");
            Interface.Pausa(2000);
            inimigo.setVida(inimigo.getVida() - dano);
            Interface.MostrarMensagem(inimigo.getNome() + " agora tem " + inimigo.getVida() + " de vida.");
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

    private static int TentarFugir(FichaRpg ficha, Criatura inimigo, int tentativasAtuais) {
        Interface.barraDivisoria();
        System.out.println("\nDeseja realmente tentar fugir?");
        System.out.println("1. Sim, tentar fugir");
        System.out.println("2. Não, voltar ao combate");

        int confirmar = Interface.scanner.nextInt();
        Interface.scanner.nextLine();

        if (confirmar != 1) return tentativasAtuais;

        Interface.MostrarMensagem("\nVocê tenta se esquivar e recuar...");
        Interface.Pausa(2000);

        int dadoJogador = MecanicasRpg.rolarDado(20);
        int totalJogador = dadoJogador + ficha.getDestreza();
        Interface.MostrarMensagem("-> Sua Tentativa de Fuga: " + dadoJogador + " (Dado) + " + ficha.getDestreza() + " (Destreza) = " + totalJogador);
        Interface.Pausa(2000);

        int dadoInimigo = MecanicasRpg.rolarDado(20);
        int totalInimigo = dadoInimigo + inimigo.getIniciativa();
        Interface.MostrarMensagem("-> Reação do Lobo: " + dadoInimigo + " (Dado) + " + inimigo.getIniciativa() + " (Iniciativa) = " + totalInimigo);
        Interface.Pausa(2000);

        if (totalInimigo > totalJogador) {
            Interface.MostrarMensagem("\nO Lobo te alcança! Você perdeu a chance de fugir dessa vez.");
            return -1;
        }

        int novasTentativas = tentativasAtuais + 1;
        Interface.MostrarMensagem("\nVocê se afasta um passo! Tentativa " + novasTentativas + "/3");
        Interface.Pausa(2000);
        return novasTentativas;
    }
}
