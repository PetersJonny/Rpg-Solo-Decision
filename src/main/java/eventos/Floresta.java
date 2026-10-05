package eventos;

import java.util.ArrayList;
import java.util.List;

import criaturas.Criatura;
import fichas.FichaRpg;
import itens.Consumivel;
import itens.ItemRpg;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class Floresta {

        private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;

        public static void avancarTempoComMensagens(FichaRpg ficha, int unidades) {
        int vidaAntesFome = ficha.getVidaPersonagem();
        boolean virou = ficha.avancarTempo(unidades);
        int perdaFome = vidaAntesFome - ficha.getVidaPersonagem();
        if (!virou) {
            if (ficha.getProgressoPeriodo() >= 2) {
                String proximo = ficha.isEhNoite() ? "dia" : "noite";
                Interface.MostrarMensagem("\n(Falta pouco para " + proximo + " chegar: " + (3 - ficha.getProgressoPeriodo()) + "/3 restantes.)");
                Interface.Pausa(1000);
            }
            return;
        }
        if (perdaFome > 0) {
            Interface.MostrarMensagem("\n(A fome cobra seu preço: você perde " + perdaFome + " de vida! " + ficha.getDiasSemComer() + " dias sem comer)");
            Interface.Pausa(1500);
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

                if (ficha.isTemCabana() && !ficha.temCompanheiro() && MecanicasRpg.rolarDado(100) <= 20) {
            EventoPerdido(ficha);
        }
    }

        public static void verificarCompanheiroPosDormir(FichaRpg ficha) {
        if (!ficha.temCompanheiro()) return;
        companheiros.Companheiro comp = ficha.getCompanheiro();

        // Evil companion steal logic
        if (comp.isDoMal() && mecanicas.MecanicasRpg.rolarDado(100) <= 20) { // 20% chance se for do mal
            int ouroJogador = ficha.getOuro();
            if (ouroJogador > 0) {
                int roubado = Math.max(1, (int)(ouroJogador * 0.5)); // Rouba metade
                ficha.adicionarOuro(-roubado);
                comp.adicionarOuroRoubado(roubado);
                String nomePartiu = comp.getNomeCompleto();
                ficha.removerCompanheiro();
                Interface.MostrarMensagem("\nVocê acorda e percebe que " + nomePartiu + " sumiu na mata durante a noite!");
                Interface.MostrarMensagem("Pior ainda: você foi furtado. " + roubado + " moedas de ouro sumiram da sua mochila.");
                Interface.Pausa(3000);
                return;
            } else {
                String nomePartiu = comp.getNomeCompleto();
                ficha.removerCompanheiro();
                Interface.MostrarMensagem("\nVocê acorda e percebe que " + nomePartiu + " sumiu na mata durante a noite, sem deixar rastros.");
                Interface.Pausa(3000);
                return;
            }
        }

        if (!ficha.companheiroQuerPartir()) return;
        String nomePartiu = comp.getNomeCompleto();
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
        if (ficha.temCompanheiro() && ficha.getCompanheiro().isDoMal() && mecanicas.MecanicasRpg.rolarDado(100) <= 10) {
            Interface.MostrarMensagem("\n" + Interface.VERMELHO + "Traição!" + Interface.RESET + " " + ficha.getCompanheiro().getNomeCompleto() + " te ataca de surpresa!");
            Interface.Pausa(2500);
            java.util.List<Criatura> compList = new java.util.ArrayList<>();
            compList.add(criaturas.CriaturaFactory.criarCompanheiroCriatura(ficha.getCompanheiro()));
            // We temporarily remove the companion so they don't fight alongside the player against themselves
            companheiros.Companheiro compInimigo = ficha.getCompanheiro();
            ficha.removerCompanheiro();
            mecanicas.MotorDeCombate.IniciarCombate(ficha, compList, false);
            // Se sobreviveu e ganhou
            if (ficha.getVidaPersonagem() > 0) {
                Interface.MostrarMensagem("\nVocê derrotou " + compInimigo.getNome() + " e pegou os seus pertences!");
                // Transfer items
                for (itens.ItemRpg item : compInimigo.getFicha().getInventario()) {
                    ficha.adicionarItem(item);
                    Interface.MostrarMensagem("Pegou: " + item.getNome());
                }
                int ouroRoubadoEDele = compInimigo.getFicha().getOuro() + compInimigo.getOuroRoubado();
                if (ouroRoubadoEDele > 0) {
                    ficha.adicionarOuro(ouroRoubadoEDele);
                    Interface.MostrarMensagem("Recuperou/Pegou " + ouroRoubadoEDele + " moedas de ouro!");
                }
            }
        } else {
            

                EventoAnimal(ficha);

        avancarTempoComMensagens(ficha, 1);
        }

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
        if (ficha.getRaca() != null && ficha.getRaca().temBonusBuscaRecursos()) {
            int extra = Math.max(1, (int) Math.round(quantidade * 0.30f));
            quantidade += extra;
            Interface.MostrarMensagem("(Toque da Mata! Você coletou " + extra + "x extra de " + nome + ")");
            Interface.Pausa(800);
        }
        ItemRpg item = nome.equals("Frutas")
                ? new Consumivel(nome, descricao, quantidade)
                : new ItemRpg(nome, descricao, quantidade);
        ficha.coletarItemEncontrado(item, "Você encontrou");
        return true;
    }

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
            int opAlimentar = -1;
            boolean temComida = false;
            for (itens.ItemRpg it : ficha.getInventario()) {
                if (it instanceof itens.Consumivel && !it.getNome().equals("Kit Médico") && !it.getNome().contains("Mana") && !it.getNome().contains("Veneno") && !it.getNome().contains("Diabo")) {
                    temComida = true; break;
                }
            }
            if (temComida && comp.getDiasSemComer() > 0) {
                opAlimentar = num++;
                System.out.println("  " + opAlimentar + ". Alimentar " + comp.getNome());
            }
            opDespedir = num++;
            System.out.println("  " + opDespedir + ". Despedir-se de " + comp.getNome());
            int opLutar = num++;
            System.out.println("  " + opLutar + ". " + Interface.VERMELHO + "Lutar contra " + comp.getNome() + Interface.RESET);
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
            } else if (escolha == opAlimentar) {
                java.util.List<itens.ItemRpg> comidas = new java.util.ArrayList<>();
                for (itens.ItemRpg it : ficha.getInventario()) {
                    if (it instanceof itens.Consumivel && !it.getNome().equals("Kit Médico") && !it.getNome().contains("Mana") && !it.getNome().contains("Veneno") && !it.getNome().contains("Diabo")) {
                        comidas.add(it);
                    }
                }
                System.out.println("\n  Qual comida você quer dar para " + comp.getNome() + "?");
                for (int i = 0; i < comidas.size(); i++) {
                    System.out.println("  " + (i + 1) + ". " + comidas.get(i).getNome() + " (x" + comidas.get(i).getQuantidade() + ")");
                }
                System.out.println("  0. Cancelar");
                int escComida = telas.Interface.lerOpcao(0, comidas.size());
                if (escComida > 0) {
                    itens.ItemRpg escolhida = comidas.get(escComida - 1);
                    ficha.consumirItem(escolhida, 1);
                    if (escolhida.getQuantidade() <= 0) ficha.getInventario().remove(escolhida);
                    comp.alimentar();
                    telas.Interface.MostrarMensagem("\nVocê dá " + escolhida.getNome() + " para " + comp.getNome() + ". A fome passa e a gratidão cresce!");
                    telas.Interface.Pausa(2000);
                }
            } else if (escolha == opDespedir) {
                String nomePartiu = comp.getNome();
                if (confirmarDespedida(comp)) {
                    ficha.removerCompanheiro();
                    Interface.MostrarMensagem("\nVocês se despedem com gratidão. " + nomePartiu + " segue agora o próprio caminho.");
                    Interface.Pausa(2000);
                    return;
                }
            } else if (escolha == opLutar) {
                System.out.println("\n  Tem certeza que deseja ATACAR " + comp.getNome() + "? Essa ação não tem volta.\n");
                System.out.println("  1. Sim, atacar");
                System.out.println("  2. Não, recuar");
                if (Interface.lerOpcao(2) == 1) {
                    Interface.MostrarMensagem("\nVocê saca sua arma! " + comp.getNome() + " recua assustado(a) e se prepara para o combate!");
                    Interface.Pausa(2500);
                    java.util.List<Criatura> compList = new java.util.ArrayList<>();
                    compList.add(criaturas.CriaturaFactory.criarCompanheiroCriatura(comp));
                    companheiros.Companheiro compInimigo = comp;
                    ficha.removerCompanheiro();
                    mecanicas.MotorDeCombate.IniciarCombate(ficha, compList, true);
                    if (ficha.getVidaPersonagem() > 0) {
                        Interface.MostrarMensagem("\nVocê derrotou " + compInimigo.getNome() + ".");
                        for (itens.ItemRpg item : compInimigo.getFicha().getInventario()) {
                            ficha.adicionarItem(item);
                            Interface.MostrarMensagem("Pegou: " + item.getNome());
                        }
                        int ouroRoubadoEDele = compInimigo.getFicha().getOuro() + compInimigo.getOuroRoubado();
                        if (ouroRoubadoEDele > 0) {
                            ficha.adicionarOuro(ouroRoubadoEDele);
                            Interface.MostrarMensagem("Pegou " + ouroRoubadoEDele + " moedas de ouro.");
                        }
                    }
                    return;
                }
            }
        }
    }

        private static boolean confirmarDespedida(companheiros.Companheiro comp) {
        System.out.println("\n  Deseja mesmo se despedir de " + comp.getNome() + "? Ela(e) deixará de te acompanhar.\n");
        System.out.println("  1. Sim, despedir-me");
        System.out.println("  2. Não, quero que fique");
        return Interface.lerOpcao(2) == 1;
    }

        private static void curarCompanheiroComKit(FichaRpg ficha) {
        companheiros.Companheiro comp = ficha.getCompanheiro();
        if (comp == null || !ficha.temItem("Kit Médico")) return;

        FichaRpg cf = comp.getFicha();
        int cura = MecanicasRpg.rolarDado(4);
        int antes = cf.getVidaPersonagem();
        cf.setVidaPersonagem(Math.min(antes + cura, cf.getVidaMaxima()));
        int curaReal = cf.getVidaPersonagem() - antes;
        Interface.MostrarMensagem("\nVocê usa um Kit Médico em " + comp.getNome() + " e ela(e) recupera " + curaReal + " de vida! Vida: " + cf.getVidaPersonagem() + "/" + cf.getVidaMaxima());

                for (int i = 0; i < ficha.getInventario().size(); i++) {
            ItemRpg item = ficha.getInventario().get(i);
            if (item.getNome().equals("Kit Médico")) {
                ficha.consumirItem(item, 1);
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

    static void EventoAnimal(FichaRpg ficha) {
        Interface.MostrarMensagem("\nAlgo se move por entre as árvores...");
        Interface.Pausa(2500);

                if (estruturas.LabirintoDoMinotauro.tentarDescoberta(ficha)) {
            return;
        }

                if (MecanicasRpg.rolarDado(100) <= 10) {
            loja.Vendedor.EncontrarVendedor(ficha);
            return;
        }

                int chanceFada = ficha.getRaca() != null && ficha.getRaca().dobraChanceEncontrarFada() ? 40 : 20;
        if (!ficha.isFadaEncontrada() && MecanicasRpg.rolarDado(100) <= chanceFada) {
            ficha.setFadaEncontrada(true);
            EncontrarFada(ficha);
            return;
        }

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
            Interface.MostrarMensagem("\n" + mecanicas.GerenciadorDeTurnos.nomesDosInimigos(inimigos) + " apareceu entre as sombras das árvores e você o avistou antes!");
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
            Interface.MostrarMensagem("\n" + mecanicas.GerenciadorDeTurnos.nomesDosInimigos(inimigos) + " saltou das sombras e te surpreendeu!");
            Interface.Pausa(2500);
            mecanicas.MotorDeCombate.IniciarCombate(ficha, inimigos, false);
        }
    }

        private static List<Criatura> criarGrupoMonstros(int tipo, boolean deNoite) {
        return criaturas.CriaturaFactory.criarGrupoMonstros(tipo, deNoite);
    }

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

            if (totalSabedoria < 14 && ficha.podeUsarMenteAfiada()) {
                Interface.MostrarMensagem("\n(Mente Afiada!) Sua mente aguçada permite reavaliar a situação... Deseja rolar novamente?");
                if (Interface.lerOpcao(2) == 1) {
                    ficha.marcarMenteAfiadaUsada();
                    dadoSabedoria = MecanicasRpg.rolarDado(20);
                    totalSabedoria = dadoSabedoria + ficha.getSabedoriaTeste();
                    Interface.MostrarMensagem("-> Nova tentativa (Sabedoria): " + dadoSabedoria + " (Dado) + " + ficha.getSabedoriaTeste() + " (Atributo) = " + totalSabedoria + " (Dificuldade: 14)");
                    Interface.Pausa(2500);
                }
            }

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

}
