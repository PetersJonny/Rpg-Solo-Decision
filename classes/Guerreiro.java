package classes;

import fichas.FichaRpg;
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
        
        // Habilidades por nível
        this.habilidadesPorNivel.put(3, java.util.List.of(
            new habilidades.Magia("Peso da Espada", "Usa o peso de sua própria espada para abater o inimigo, causando 3d8 de dano.", 3, 3, 8)
        ));

        this.escolhasNivel.put(5, java.util.List.of(
            new habilidades.Habilidade("Giro", "Usa sua destreza para girar e atacar em área. Gasta 1 de mana por giro (máximo igual à sua Destreza), causando 1d10 de dano em área por giro.", 1),
            new habilidades.Habilidade("Espada Afiada", "Sua espada ganha +2d8 de dano em todos os ataques durante o combate.", 3, true)
        ));
    }

    @Override
    public int calcularVidaBase(int constituicaoBase) { return 20 + constituicaoBase; }

    @Override
    public int calcularManaBase(int presencaBase) { return 2 + presencaBase; }

    @Override
    public void aplicarBonusNivel(FichaRpg ficha) {
        int ganhoVida = 5 + ficha.getConstituicao();
        ficha.setVidaMaxima(ficha.getVidaMaxima() + ganhoVida);
        ficha.setVidaPersonagem(ficha.getVidaPersonagem() + ganhoVida);
        int ganhoMana = 1 + ficha.getPresenca();
        ficha.setManaMaxima(ficha.getManaMaxima() + ganhoMana);
        ficha.setManaPersonagem(ficha.getManaPersonagem() + ganhoMana);
    }

    @Override
    public int getBonusConstituicao() { return 2; }

    @Override
    public int getBonusForca() { return 1; }

    @Override
    public int getBonusIntelecto() { return -2; }
}
