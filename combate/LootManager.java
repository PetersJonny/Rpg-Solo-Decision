package combate;

import classes.Guerreiro;
import classes.Mago;
import classes.Healer;
import fichas.FichaRpg;
import habilidades.Habilidade;
import itens.ItemRpg;
import mecanicas.MecanicasRpg;
import telas.Interface;

import java.util.ArrayList;
import java.util.List;

public class LootManager {

    private static final String RESET = Interface.RESET;
    private static final String AMARELO = Interface.AMARELO;
    private static final String CIANO = Interface.CIANO;

    public static void processarMortes(FichaRpg ficha, List<criaturas.Criatura> inimigos, List<criaturas.Criatura> inimigosOriginais) {
        List<criaturas.Criatura> mortos = new ArrayList<>();
        for (criaturas.Criatura c : inimigos) {
            if (c.getVida() <= 0) {
                mortos.add(c);
            }
        }

        if (!mortos.isEmpty()) {
            boolean upou = false;

            for (criaturas.Criatura c : mortos) {
                System.out.println("\n" + AMARELO + "A criatura " + c.getNome() + " foi derrotada!" + RESET);
                System.out.println("XP recebido: " + c.getXpGanho() + " XP");
                c.processarDrops(ficha);

                int nivelAntes = ficha.getNivel();
                int niveisGanhos = ficha.adicionarXp(c.getXpGanho());
                System.out.println("XP Total: " + ficha.getXp() + "/" + fichas.FichaRpg.getXpNecessaria(ficha.getNivel()));
                System.out.println("");
                Interface.Pausa(2000);

                if (niveisGanhos > 0) {
                    upou = true;
                    System.out.println("\n*** SUBIU PARA O NÍVEL " + ficha.getNivel() + "! ***");
                    Interface.Pausa(2000);

                    if (ficha.getNivel() < 10) {
                        Interface.MostrarMensagem("XP para o próximo nível: " + fichas.FichaRpg.getXpNecessaria(ficha.getNivel()));
                    } else {
                        Interface.MostrarMensagem("Você atingiu o nível máximo!");
                    }
                    Interface.Pausa(2000);

                    for (habilidades.Habilidade hh : ficha.getHabilidades()) {
                        boolean nova = true;
                        for (habilidades.Habilidade h : ficha.getHabilidades()) {
                            if (h.getNome().equals(hh.getNome())) {
                                nova = false;
                                break;
                            }
                        }
                    }

                    for (int nivelGanho = nivelAntes + 1; nivelGanho <= ficha.getNivel(); nivelGanho++) {
                        if (nivelGanho == 5 || nivelGanho == 7 || nivelGanho == 9 || nivelGanho == 10) {
                            List<habilidades.Habilidade> opcoes = ficha.getClasseDoPersonagem().getEscolhasDisponiveis(ficha, nivelGanho);
                            if (opcoes != null && !opcoes.isEmpty()) {
                                SkillExecutor.escolherHabilidadeNivel(ficha, nivelGanho, opcoes);
                            }
                        }
                        if (nivelGanho == 2 || nivelGanho == 4 || nivelGanho == 6 || nivelGanho == 8) {
                            SkillExecutor.escolherPontoAtributo(ficha);
                        }
                    }
                }
            }

            if (upou) {
                System.out.println("\nApós sua vitória contra as ameaças, você se prepara para o que vem pela frente.");
                System.out.println("Suas habilidades e força cresceram, e agora você está pronto para enfrentar novos desafios.");
                System.out.println("\nEnquanto contempla a paisagem, sua mente se abre e você sente que pode aprender algo novo...");
                Interface.Pausa(2000);
            }
        }

        inimigos.removeAll(mortos);
        inimigosOriginais.removeAll(mortos);
    }
}
