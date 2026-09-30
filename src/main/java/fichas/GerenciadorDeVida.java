package fichas;

import classes.ClasseRpg;
import criaturas.Criatura;
import itens.Arma;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;
import racas.Raca;
import telas.Interface;

public class GerenciadorDeVida {

    public static void receberDano(FichaRpg ficha, int dano) {

        if (dano < 0) return;
        if (ficha.isFrutaImuneNestaRodada()) {
            telas.Interface.MostrarMensagem("(Fruta do Diabo! Os " + dano + " de dano não chegam a te alcançar nesta rodada)");
            telas.Interface.Pausa(1500);
            return;
        }
        if (ficha.pactoMortalAtivo) {
            dano += 3;
            telas.Interface.MostrarMensagem("(Pacto Mortal! Você sofre +3 de dano)");
            telas.Interface.Pausa(1200);
        }
        if (ficha.curaAbsolutaBonus > 0) {
            if (dano <= ficha.curaAbsolutaBonus) {
                ficha.curaAbsolutaBonus -= dano;
                telas.Interface.MostrarMensagem("(Proteção da Cura Absoluta absorve " + dano + " de dano! Restante: " + ficha.curaAbsolutaBonus + ")");
                telas.Interface.Pausa(1500);
            } else {
                int restante = dano - ficha.curaAbsolutaBonus;
                ficha.curaAbsolutaBonus = 0;
                ficha.vidaMaxima = ficha.curaAbsolutaVidaOriginalMax;
                ficha.vidaPersonagem = Math.max(0, ficha.vidaPersonagem - restante);
                telas.Interface.MostrarMensagem("(A proteção da Cura Absoluta se esgotou!)");
                telas.Interface.Pausa(1500);
            }
        } else {
            ficha.vidaPersonagem = Math.max(0, ficha.vidaPersonagem - dano);
        }

                if (ficha.vidaPersonagem <= 0 && ficha.raca != null && ficha.raca.podeSobreviverCom1AoCair0() && !ficha.sobrevivenciaUsada) {
            ficha.sobrevivenciaUsada = true;
            ficha.vidaPersonagem = 1;
            telas.Interface.MostrarMensagem("\n(Vontade de Viver!) Você resiste à morte e permanece de pé com 1 de vida!");
            telas.Interface.Pausa(1500);
        }

    }
    public static void resetarEfeitosCombate(FichaRpg ficha) {

        ficha.espadaAfiadaAtiva = false;
        ficha.protecaoAbsolutaAtiva = false;
        ficha.bonusDefesaTemporario = 0;
        ficha.curaParaMortePreparado = false;
        ficha.curaParaMorteAtivo = false;
        ficha.alvoCuraParaMorte = null;
        ficha.curaTotalUsada = false;
        ficha.infectado = false;
        ficha.pactoMortalAtivo = false;
        ficha.defesaAbsolutaAtiva = false;
        ficha.rodadasSemHabilidade = 0;
        ficha.magiaProibidaUsada = false;
        ficha.magiaProibidaAtiva = false;
        ficha.prisaoAtiva = null;
        if (ficha.semiDeusAtivo && !ficha.deusAtivo) {
            ficha.vidaMaxima = ficha.semiDeusVidaOriginalMax;
            ficha.vidaPersonagem = Math.min(ficha.vidaPersonagem, ficha.vidaMaxima);
            ficha.semiDeusAtivo = false;
        }
        ficha.semiDeusVidaOriginalMax = 0;
        if (ficha.deusAtivo) {
            ficha.semiDeusAtivo = true;
        }
        ficha.poderAbsolutoAtivo = false;
        ficha.curaAbsolutaBonus = 0;
        ficha.curaAbsolutaVidaOriginalMax = 0;
        ficha.curaIncessanteUsada = false;
        ficha.setFrutaDoDiaboAtiva(false);
        ficha.setFrutaImuneNestaRodada(false);
        ficha.setEmCombate(false);

    }
    public static int bonusTestesNoturnos(FichaRpg ficha) {

        return ficha.ehNoite && ficha.raca != null && ficha.raca.temBonusTestesNoturnos() ? 2 : 0;

    }
    public static int getPenalidadeFome(FichaRpg ficha) {

        int penalidade = ficha.diasSemComer >= 3 ? 2 : ficha.diasSemComer >= 1 ? 1 : 0;
        if (ficha.enjoado) penalidade = Math.max(penalidade, ficha.penalidadeEnjoado);
        return penalidade;

    }
    public static int getPerdaVidaPorFome(FichaRpg ficha) {

        if (ficha.diasSemComer < 5) return 0;
        return 1 << ((ficha.diasSemComer - 5) / 5);

    }
    public static int getLabirintoChanceDescoberta(FichaRpg ficha) {

        return Math.min(ficha.diaAtual, 100);

    }
    public static boolean isLabirintoDisponivel(FichaRpg ficha) {

        return ficha.labirintoEncontrado && ficha.labirinto != null && !ficha.labirinto.isCentroAlcancado();

    }
    public static void marcarSobrevivenciaUsada(FichaRpg ficha) {
 ficha.sobrevivenciaUsada = true;
    }
    public static void marcarMenteAfiadaUsada(FichaRpg ficha) {
 ficha.menteAfiadaUsada = true;
    }
    public static boolean podeUsarMenteAfiada(FichaRpg ficha) {
 return ficha.raca != null && ficha.raca.podeRerrolarTeste() && !ficha.menteAfiadaUsada;
    }
    public static boolean podeUsarCabana(FichaRpg ficha) {
 return ficha.temCabana && ficha.getLocalizacaoAtual() == 0;
    }
    public static boolean podeUsarSalaTreino(FichaRpg ficha) {
 return ficha.temSalaTreino && ficha.getLocalizacaoAtual() == ficha.getLocalizacaoSala();
    }
    public static boolean podeUsarMesaMagias(FichaRpg ficha) {
 return ficha.temMesaMagias && ficha.getLocalizacaoAtual() == ficha.getLocalizacaoMesa();
    }
    public static boolean podeUsarFogueira(FichaRpg ficha) {
 return ficha.temFogueira && ficha.getLocalizacaoAtual() == ficha.getLocalizacaoFogueira();
    }
}
