package eventos;

import fichas.FichaRpg;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class TravessiaDaFloresta {

    // Códigos de Cores ANSI (reutilizados da Interface)
    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;

    // Cidades para além da floresta (a chegada sorteia uma delas; por enquanto só existe Scarbor)
    private static final String[] CIDADES = { "Vilarejo de Scarbor" };

    // "Tentar sair da floresta": o jogador caminha 1 a 3 períodos em direção à borda.
    // A profundidade oculta (até sair) fica na ficha e nunca é revelada na tela.
    public static void TentarSairDaFloresta(FichaRpg ficha) {
        Interface.cabecalhoMenu("TENTAR SAIR DA FLORESTA");
        Interface.MostrarMensagem("\nVocê se prepara para caminhar adentrando a mata, em busca de algo além de árvores e mato.");
        Interface.MostrarMensagem("Dizem que quem vagueia por tempo suficiente na direção certa acaba saindo da floresta... mas ninguém sabe dizer quanto.");
        Interface.Pausa(2500);

        System.out.println("\n  Quantos períodos deseja caminhar agora? (cada período = 1/3 do dia)");
        System.out.println("  1. Um período");
        System.out.println("  2. Dois períodos");
        System.out.println("  3. Três períodos");
        System.out.println("  4. Não caminhar agora");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        int plano = Interface.lerOpcao(1, 4);
        if (plano == 4) return;

        // Sair rumo ao desconhecido significa ter deixado a cabana/sala/mesa
        if (ficha.isTemCabana() && ficha.isNaCabana()) {
            ficha.sairDaCabana();
            Interface.MostrarMensagem("\nVocê deixa sua cabana para trás e se embrenha no mato, em busca de algo além de árvores.");
            Interface.Pausa(1500);
        } else if (ficha.isNaSalaTreino()) {
            ficha.sairDaCabana();
            Interface.MostrarMensagem("\nVocê deixa sua sala de treino para trás e se embrenha no mato, em busca de algo além de árvores.");
            Interface.Pausa(1500);
        } else if (ficha.isNaMesaMagias()) {
            ficha.sairDaCabana();
            Interface.MostrarMensagem("\nVocê deixa sua mesa de magias para trás e se embrenha no mato, em busca de algo além de árvores.");
            Interface.Pausa(1500);
        }

        int caminhados = 0;
        while (caminhados < plano) {
            boolean houveAcontecimento = percorrerUmTurno(ficha, true);
            if (ficha.getVidaPersonagem() <= 0) return; // morreu no caminho
            caminhados++;

            if (ficha.isNoVilarejo()) {
                chegarForaDaFloresta(ficha);
                return;
            }

            if (caminhados < plano) {
                if (!houveAcontecimento) {
                    // Nada interrompeu a marcha: segue caminhando sem perguntar.
                    Interface.MostrarMensagem("\nNada interrompeu sua marcha. Você continua caminhando...");
                    Interface.Pausa(1500);
                } else {
                    System.out.println("\n  Você caminhou " + caminhados + " de " + plano + " período(s).");
                    System.out.println("  1. Continuar caminhando");
                    System.out.println("  2. Parar por aqui");
                    System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
                    if (Interface.lerOpcao(2) == 2) {
                        Interface.MostrarMensagem("\nVocê decide parar de caminhar por enquanto e permanece onde parou, rodeado de árvores e mato.");
                        Interface.Pausa(2000);
                        return;
                    }
                }
            } else {
                Interface.MostrarMensagem("\nVocê caminhou os " + plano + " período(s) planejado(s) e resolve descansar por aqui por enquanto.");
                Interface.Pausa(2000);
            }
        }
    }

    // Caminha de onde o jogador está até uma construção em outro ponto da mata:
    // a ida/fim custam a distância entre os pontos (turno a turno, como a travessia).
    public static void CaminharAteConstrucao(FichaRpg ficha, int profundidadeAlvo, String nomeConstrucao) {
        int distancia = ficha.getDistanciaAte(profundidadeAlvo);
        if (distancia == 0) {
            // Já estamos NA profundidade da construção: entra nela em vez de
            // simplesmente retornar. Antes, isto deixava o jogador "fora" do ponto
            // (naCabana/naSalaTreino = false) mesmo estando parado nela, e a opção
            // "Dormir" (e o uso da sala/mesa) nunca ficava disponível.
            ficha.entrarNaConstrucao(profundidadeAlvo);
            Interface.MostrarMensagem("\nVocê já está no mesmo ponto de " + nomeConstrucao + " e se acomoda nela.");
            Interface.Pausa(1500);
            return;
        }

        Interface.MostrarMensagem("\nVocê se prepara para ir até " + nomeConstrucao + ".");
        Interface.MostrarMensagem("Será preciso gastar " + distancia + " período(s) de caminhada para chegar lá.");
        Interface.Pausa(2500);

        while (ficha.getProfundidadeFloresta() != profundidadeAlvo && ficha.getVidaPersonagem() > 0) {
            percorrerUmTurno(ficha, ficha.getProfundidadeFloresta() < profundidadeAlvo);
        }
        if (ficha.getVidaPersonagem() <= 0) return;

        Interface.MostrarMensagem("\nA mata se abre aos poucos e você chega a " + nomeConstrucao + ".");
        Interface.Pausa(2000);
    }

    // "Voltar para a floresta": sair do vilarejo e retornar à floresta de Freijord,
    // na margem dela (o ponto mais afastado da travessia, por onde se saiu). Dali o
    // jogador pode explorar, buscar recursos e caminhar até as construções como sempre
    // fez — e, para voltar ao vilarejo, basta tentar sair da floresta outra vez.
    public static void VoltarParaFloresta(FichaRpg ficha) {
        if (!ficha.isNoVilarejo()) return;

        Interface.MostrarMensagem("\nVocê decide voltar para a floresta.");
        Interface.MostrarMensagem("Você vira as costas para a estrada do vilarejo e entra de volta na mata, parando bem na margem dela — os campos abertos ainda são visíveis por entre os troncos.");
        Interface.Pausa(2500);

        // O retorno deixa o jogador na margem da floresta (um passo antes de sair),
        // conservando o ponto mais distante da travessia em vez de mandá-lo de volta
        // para as construções. Dali ele decide quando e para onde caminhar.
        ficha.reduzirProfundidade(ficha.getProfundidadeFloresta() - (FichaRpg.PROFUNDIDADE_PARA_SAIR - 1));

        if (ficha.podeUsarCabana() || ficha.podeUsarSalaTreino() || ficha.podeUsarMesaMagias()) {
            Interface.MostrarMensagem("\nSuas construções estão exatamente neste ponto da mata, erguidas ali.");
        } else {
            Interface.MostrarMensagem("\nVocê está na margem da floresta, longe das suas construções. Dali pode explorar, buscar recursos e caminhar até elas pelo menu de Construção.");
        }
        Interface.Pausa(2500);
    }

    // Percorre um período da travessia (indoEmbora = afastando-se; false = voltando).
    // Avança o tempo. Nos dois sentidos há a mesma chance de encontro da exploração
    // (30% de dia, 50% à noite), sem coletar recursos. Retorna true se algo aconteceu.
    private static boolean percorrerUmTurno(FichaRpg ficha, boolean indoEmbora) {
        if (indoEmbora) {
            ficha.adicionarProfundidade(1);
        } else {
            ficha.reduzirProfundidade(1);
        }

        Interface.MostrarMensagem(indoEmbora
                ? "\nVocê avança mata adentro, seguindo seu caminho entre árvores e mato..."
                : "\nVocê corta o mato de volta, refazendo o caminho por entre as árvores...");
        Interface.Pausa(2000);

        // Mesma chance de encontro da exploração (30% de dia, 50% à noite), nos dois sentidos
        boolean houveAcontecimento;
        int chanceEncontro = ficha.isEhNoite() ? 50 : 30;
        if (MecanicasRpg.rolarDado(100) <= chanceEncontro) {
            Interface.MostrarMensagem("\nAlgo se agita entre as árvores...");
            Interface.Pausa(1500);
            Floresta.EventoAnimal(ficha);
            houveAcontecimento = true;
        } else {
            Interface.MostrarMensagem("\nNada acontece por aqui. O vento frio sopra entre os galhos e você segue em frente.");
            Interface.Pausa(1500);
            houveAcontecimento = false;
        }

        Floresta.avancarTempoComMensagens(ficha, 1);
        return houveAcontecimento;
    }

    // Chegou na borda da floresta: sorteia a cidade de destino (e a mantém na ficha).
    private static void chegarForaDaFloresta(FichaRpg ficha) {
        Interface.MostrarMensagem("\nDiante de você, as árvores se abrem... A floresta de Freijord fica para trás!");
        Interface.Pausa(2500);
        Interface.MostrarMensagem("Depois de tanto mato, seus olhos avistam campos abertos e, ao longe, um vilarejo. Você finalmente saiu da floresta!");
        Interface.Pausa(2000);

        if (ficha.getCidadeAtual() == null) {
            ficha.setCidadeAtual(sortearDestino());
        }

        // A chegada é anunciada por um grande letreiro na entrada, descrito na
        // narração (sem renderizar um letreiro literal na tela).
        Interface.MostrarMensagem("\nVocê segue pela estrada de terra até a entrada do lugar. Na beira do caminho, um grande letreiro de madeira ergue-se do mato, com letras firmes gravadas no tronco envelhecido.");
        Interface.Pausa(2000);
        Interface.MostrarMensagem("O letreiro anuncia o nome da cidade: " + CIANO + ficha.getCidadeAtual() + RESET + ". Você chegou.");
        Interface.Pausa(2500);
    }

    // Sorteia uma das cidades para além da floresta (por enquanto, só o Vilarejo de Scarbor).
    public static String sortearDestino() {
        return CIDADES[MecanicasRpg.rolarDado(CIDADES.length) - 1];
    }
}