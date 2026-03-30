import java.util.Random;

public class MecanicasRpg {
    // criando objeto pseudoaleatório
    Random random = new Random();

    // informações dos monstros
    private int vidaMonstro, manaMonstro, sabedoria, intelecto, forca, presenca, constituicao, destreza, dadoDanoArma, quantidadeDanoArma, totalDanoArma;
    private String arma, tipoArma;

    public void FichaGoblin() {
        this.vidaMonstro = 25;
        this.manaMonstro = 5;
        this.sabedoria = 0;
        this.intelecto = 0;
        this.forca = 1;
        this.presenca = 1;
        this.constituicao = 0; 
        this.destreza = 2;
        this.arma = "faca";
        this.tipoArma = "CaC";
        this.dadoDanoArma = 4;
        this.quantidadeDanoArma = 1;
    }

    public void MecanicaLuta() {
        
    }

}
