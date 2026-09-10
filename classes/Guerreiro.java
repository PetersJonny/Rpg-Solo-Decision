package classes;

import itens.Arma;
import itens.Armadura;

public class Guerreiro extends ClasseRpg {
    
    public Guerreiro() {
        this.nome = "Guerreiro";
        this.armaPrincipal = new Arma("Espada", "Uma espada de aço afiada que causa 1d8 de dano.", "CaC", 8, 1, 1);
        this.itensIniciais.add(this.armaPrincipal);
        this.itensIniciais.add(new Armadura("Armadura Leve", "Oferece proteção básica para combate. Concede +3 de Defesa.", 3, 1));
        
        // Habilidade Base
        this.habilidadesIniciais.add(new habilidades.Habilidade("Casca Grossa", "Endurece a pele, diminuindo 5 de dano recebido de um ataque.", 1, true));
    }

    @Override
    public int calcularVidaBase(int constituicaoBase) { return 20 + constituicaoBase; }

    @Override
    public int calcularManaBase(int presencaBase) { return 2 + presencaBase; }

    @Override
    public int getBonusConstituicao() { return 2; }

    @Override
    public int getBonusForca() { return 1; }

    @Override
    public int getBonusIntelecto() { return -2; }
}
