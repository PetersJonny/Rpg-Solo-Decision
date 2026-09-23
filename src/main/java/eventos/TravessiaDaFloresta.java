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
            percorrerUmTurno(ficha, true);
            if (ficha.getVidaPersonagem() <= 0) return; // morreu no caminho
            caminhados++;

            if (ficha.isNoVilarejo()) {
                chegarForaDaFloresta(ficha);
                return;
            }

            if (caminhados < plano) {
                System.out.println("\n  Você caminhou " + caminhados + " de " + plano + " período(s).");
                System.out.println("  1. Continuar caminhando");
                System.out.println("  2. Parar por aqui");
                System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
                if (Interface.lerOpcao(2) == 2) {
                    Interface.MostrarMensagem("\nVocê decide parar de caminhar por enquanto e permanece onde parou, rodeado de árvores e mato.");
                    Interface.Pausa(2000);
                    return;
                }
            } else {
                Interface.MostrarMensagem("\nVocê caminhou os " + plano + " período(s) planejado(s) e resolve descansar por aqui por enquanto.");
                Interface.Pausa(2000);
            }
        }
    }

    // "Voltar para as construções": percorre a mesma quantidade de períodos gastos para
    // se afastar (a profundidade oculta volta a zero até chegar perto das construções).
    public static void VoltarParaConstrucoes(FichaRpg ficha) {
        Interface.MostrarMensagem("\nVocê se vira e começa a refazer o caminho, cortando de volta o mato em direção às suas construções...");
        Interface.Pausa(2000);

        while (ficha.getProfundidadeFloresta() > 0 && ficha.getVidaPersonagem() > 0) {
            percorrerUmTurno(ficha, false);
        }
        if (ficha.getVidaPersonagem() <= 0) return;

        Interface.MostrarMensagem("\nAos poucos a vegetação fica conhecida de novo... suas construções aparecem entre as árvores!");
        Interface.MostrarMensagem("Você está de volta perto das suas construções.");
        if (ficha.isTemCabana()) {
            ficha.voltarParaCabana();
            Interface.MostrarMensagem("\nVocê entra em sua cabana, aliviado por estar de volta a um lugar seguro.");
        }
        Interface.Pausa(2500);
    }

    // Percorre um período da travessia (indoEmbora = afastando-se; false = voltando).
    // Avança o tempo e, como ao explorar, há chance de encontro — porém sem achar recursos.
    private static void percorrerUmTurno(FichaRpg ficha, boolean indoEmbora) {
        if (indoEmbora) {
            ficha.adicionarProfundidade(1);
        } else {
            ficha.reduzirProfundidade(1);
        }

        Interface.MostrarMensagem(indoEmbora
                ? "\nVocê adentra o mato, se afastando das construções em busca de algo além de árvores..."
                : "\nVocê corta o mato de volta, na direção das suas construções...");
        Interface.Pausa(2000);

        // Mesma chance de encontro da exploração (30% de dia, 50% à noite), mas não coleta recurso
        int chanceEncontro = ficha.isEhNoite() ? 50 : 30;
        if (MecanicasRpg.rolarDado(100) <= chanceEncontro) {
            Interface.MostrarMensagem("\nAlgo se agita entre as árvores...");
            Interface.Pausa(1500);
            Floresta.EventoAnimal(ficha);
        } else {
            Interface.MostrarMensagem("\nNada acontece por aqui. O vento frio sopra entre os galhos e você segue em frente.");
            Interface.Pausa(1500);
        }

        Floresta.avancarTempoComMensagens(ficha, 1);
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
        Interface.MostrarMensagem("\nVocê chega ao " + CIANO + ficha.getCidadeAtual() + RESET + ".");
        Interface.Pausa(2500);
    }

    // Sorteia uma das cidades para além da floresta (por enquanto, só o Vilarejo de Scarbor).
    public static String sortearDestino() {
        return CIDADES[MecanicasRpg.rolarDado(CIDADES.length) - 1];
    }
}