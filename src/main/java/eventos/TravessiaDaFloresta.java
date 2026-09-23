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
        if (distancia == 0) return;

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

    // "Voltar para as construções": sair do vilarejo e retornar direto à floresta,
    // no ponto das construções (o mais adiantado na travessia), num salto narrativo.
    // Dali o jogador decide se quer caminhar até cada construção (menu de Construção).
    public static void VoltarParaConstrucoes(FichaRpg ficha) {
        int alvo = ficha.getProfundidadeConstrucaoMaisProxima();
        int distancia = ficha.getProfundidadeFloresta() - alvo;
        if (distancia <= 0) return;

        Interface.MostrarMensagem("\nVocê decide voltar para as suas construções.");
        Interface.Pausa(2000);
        Interface.MostrarMensagem("Você retoma a trilha marcada na ida e percorre o caminho de volta pela mata sem demora, deixando a estrada do vilarejo para trás.");
        Interface.Pausa(2500);

        // O retorno acontece num salto narrativo: chega-se direto ao ponto das
        // construções, sem períodos extras nem acontecimentos no caminho.
        ficha.reduzirProfundidade(distancia);

        Interface.MostrarMensagem("\nAos poucos a vegetação fica conhecida de novo... suas construções aparecem entre as árvores!");
        if (ficha.podeUsarCabana()) {
            Interface.MostrarMensagem("Você entra em sua cabana, aliviado por estar de volta a um lugar seguro.");
        } else if (alvo == 0) {
            Interface.MostrarMensagem("Você está de volta ao ponto de partida, onde a mata vai se tornando familiar.");
        } else {
            Interface.MostrarMensagem("Você está no ponto mais adiantado das suas construções. Dali, você decide se quer caminhar até cada uma delas pelo menu de Construção.");
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