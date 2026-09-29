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

/** Completude da ficha, distâncias e posição atual do jogador entre as construções. */
public class GerenciadorDeLocalizacao {

    public static boolean isFichaCompleta(FichaRpg ficha) {

        if (ficha.nomePersonagem.equals("Desconhecido") || ficha.nomePersonagem.trim().isEmpty()) {
            return false;
        }
        if (ficha.classeDoPersonagem == null) {
            return false;
        }
        int totalAtributosBase = ficha.constituicaoBase + ficha.destrezaBase + ficha.forcaBase + ficha.sabedoriaBase + ficha.intelectoBase + ficha.presencaBase;
        if (totalAtributosBase < 6) {
            return false;
        }
        return true;
    
    }
    public static int getDistanciaAte(FichaRpg ficha, int profundidadeAlvo) {

        return Math.abs(ficha.profundidadeFloresta - profundidadeAlvo);
    
    }
    public static int getLocalizacaoAtual(FichaRpg ficha) {

        if (ficha.naCabana) return 0;
        if (ficha.naSalaTreino) return ficha.getLocalizacaoSala();
        if (ficha.naMesaMagias) return ficha.getLocalizacaoMesa();
        if (ficha.naFogueira) return ficha.getLocalizacaoFogueira();
        return 3;
    
    }
    public static int getLocalizacaoSala(FichaRpg ficha) {

        if (!ficha.temSalaTreino) return 1;
        if (ficha.profundidadeSalaTreino == ficha.profundidadeCabana) return 0;
        if (ficha.profundidadeSalaTreino == ficha.profundidadeMesaMagias) return 2;
        return 1;
    
    }
    public static int getLocalizacaoMesa(FichaRpg ficha) {

        if (!ficha.temMesaMagias) return 2;
        if (ficha.profundidadeMesaMagias == ficha.profundidadeCabana) return 0;
        return 2;
    
    }
    public static int getLocalizacaoFogueira(FichaRpg ficha) {

        if (!ficha.temFogueira) return 4;
        if (ficha.profundidadeFogueira == ficha.profundidadeCabana) return 0;
        if (ficha.profundidadeFogueira == ficha.profundidadeSalaTreino) return 1;
        if (ficha.profundidadeFogueira == ficha.profundidadeMesaMagias) return 2;
        return 4;
    
    }
}
