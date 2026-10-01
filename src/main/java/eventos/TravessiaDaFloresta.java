package eventos;

import fichas.FichaRpg;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class TravessiaDaFloresta {

        private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;

        private static final String[] CIDADES = { "Vilarejo de Scarbor" };

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
            boolean noiteAntes = ficha.isEhNoite();
            boolean houveAcontecimento = percorrerUmTurno(ficha, true);
            if (ficha.getVidaPersonagem() <= 0) return;             caminhados++;

            if (ficha.isNoVilarejo()) {
                chegarForaDaFloresta(ficha, noiteAntes != ficha.isEhNoite());
                return;
            }

            if (caminhados < plano) {
                if (!houveAcontecimento) {
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

            public static void CaminharAteConstrucao(FichaRpg ficha, int profundidadeAlvo, String nomeConstrucao) {
        int distancia = ficha.getDistanciaAte(profundidadeAlvo);
        if (distancia == 0) {
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

                    public static void VoltarParaFloresta(FichaRpg ficha) {
        if (!ficha.isNoVilarejo()) return;

        Interface.MostrarMensagem("\nVocê decide voltar para a floresta.");
        Interface.MostrarMensagem("Você vira as costas para a estrada do vilarejo e entra de volta na mata, parando bem na margem dela — os campos abertos ainda são visíveis por entre os troncos.");
        Interface.Pausa(2500);

                                ficha.reduzirProfundidade(ficha.getProfundidadeFloresta() - (FichaRpg.PROFUNDIDADE_PARA_SAIR - 1));

        if (ficha.podeUsarCabana() || ficha.podeUsarSalaTreino() || ficha.podeUsarMesaMagias()) {
            Interface.MostrarMensagem("\nSuas construções estão exatamente neste ponto da mata, erguidas ali.");
        } else {
            Interface.MostrarMensagem("\nVocê está na margem da floresta, longe das suas construções. Dali pode explorar, buscar recursos e caminhar até elas pelo menu de Construção.");
        }
        Interface.Pausa(2500);
    }

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

        private static void chegarForaDaFloresta(FichaRpg ficha, boolean virouTempo) {
        boolean chegadaSilenciada = VilarejoDeScarbor.chegadaSilenciaTaverna(ficha, virouTempo);

        Interface.MostrarMensagem("\nDiante de você, as árvores se abrem... A floresta de Freijord fica para trás!");
        Interface.Pausa(2500);
        Interface.MostrarMensagem("Depois de tanto mato, seus olhos avistam campos abertos e, ao longe, um vilarejo. Você finalmente saiu da floresta!");
        Interface.Pausa(2000);

        if (ficha.getCidadeAtual() == null) {
            ficha.setCidadeAtual(sortearDestino());
        }

                        Interface.MostrarMensagem("\nVocê segue pela estrada de terra até a entrada do lugar. Na beira do caminho, um grande letreiro de madeira ergue-se do mato, com letras firmes gravadas no tronco envelhecido.");
        Interface.Pausa(2000);
        Interface.MostrarMensagem("O letreiro anuncia o nome da cidade: " + CIANO + ficha.getCidadeAtual() + RESET + ". Você chegou.");
        Interface.Pausa(2500);

        if (chegadaSilenciada) {
            VilarejoDeScarbor.ChegadaSilenciadaPorViradaDeTempo(ficha);
            Interface.MostrarMensagem("\n" + CIANO + "Foi bem na hora que você apareceu: o tempo virou enquanto você vinha pela estrada." + RESET + " A vila já está com a cara do outro horário — " + ficha.getPeriodoDescritivo().toLowerCase() + " em " + ficha.getCidadeAtual() + " — e você não viu nada de mais nas redondezas, só que as ruas já não são as mesmas.");
            Interface.Pausa(2500);
            VilarejoDeScarbor.DescreverChegada(ficha);
        } else {
            VilarejoDeScarbor.ObservarCidade(ficha);
        }
    }

        public static String sortearDestino() {
        return CIDADES[MecanicasRpg.rolarDado(CIDADES.length) - 1];
    }
}
