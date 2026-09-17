package combate;

import classes.Guerreiro;
import classes.Mago;
import classes.Healer;
import itens.ItemRpg;
import habilidades.Habilidade;
import mecanicas.MecanicasRpg;
import telas.Interface;
import fichas.FichaRpg;

public class PassiveHandler {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String AMARELO = Interface.AMARELO;

    public static boolean podeAtivarPassiva(FichaRpg ficha, Habilidade hab, boolean cascaGrossaAtiva) {
        if (hab.getNome().equals("Magia Proibida") && ficha.isMagiaProibidaUsada()) {
            return false;
        }

        if (hab.getNome().equals("Espada Afiada") && ficha.isEspadaAfiadaAtiva()) {
            return false;
        }

        if (hab.getNome().equals("Defesa Absoluta") && ficha.isDefesaAbsolutaAtiva()) {
            return false;
        }

        if (hab.getNome().equals("Semi Deus") && ficha.isSemiDeusAtivo()) {
            return false;
        }

        if (hab.getNome().equals("Proteção Absoluta") && ficha.isProtecaoAbsolutaAtiva()) {
            return false;
        }

        if (hab.getNome().equals("Casca Grossa") && cascaGrossaAtiva) {
            return false;
        }

        return true;
    }

    public static void aplicaPassiva(FichaRpg ficha, Habilidade hab, boolean[] cascaGrossaAtiva) {
        String nome = hab.getNome();
        if (nome.equals("Espada Afiada")) {
            ficha.setEspadaAfiadaAtiva(true);
        } else if (nome.equals("Defesa Absoluta")) {
            ficha.setDefesaAbsolutaAtiva(true);
        } else if (nome.equals("Semi Deus")) {
            ficha.setSemiDeusAtivo(true);
        } else if (nome.equals("Proteção Absoluta")) {
            ficha.setProtecaoAbsolutaAtiva(true);
        } else if (nome.equals("Casca Grossa")) {
            cascaGrossaAtiva[0] = true;
        } else if (nome.equals("Magia Proibida")) {
            ficha.setMagiaProibidaUsada(true);
            ficha.setMagiaProibidaAtiva(true);
        }
    }

    public static boolean temHabilidade(FichaRpg ficha, String nome) {
        for (Habilidade hab : ficha.getHabilidades()) {
            if (hab.getNome().equals(nome)) return true;
        }
        return false;
    }

    public static boolean ehItemConsumivel(ItemRpg item) {
        return CombatResolver.ehItemConsumivel(item);
    }
}
