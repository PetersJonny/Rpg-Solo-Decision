package classes;

import fichas.FichaRpg;
import itens.Arma;
import itens.Consumivel;

public class Mago extends ClasseRpg {
    
    public Mago(String elemento) {
        this.nome = "Mago";
        this.armaPrincipal = new Arma("Cajado", "Um cajado de madeira simples que causa 1d4 de dano. Pode ser usado para canalizar magia ou para bater.", "CaC/mágico", 4, 1, 1);
        this.itensIniciais.add(this.armaPrincipal);
        this.itensIniciais.add(new Consumivel("Poção de Mana", "Restaura 5 pontos de mana. É consumida após o uso.", 1));
        
        // Magia Base
        this.habilidadesIniciais.add(new habilidades.Magia("Bola Elementar (" + elemento + ")", "Lança uma esfera de " + elemento.toLowerCase() + " que causa 2d8 de dano do elemento escolhido.", 3, 2, 8));

        // Escolhas de habilidade por nível
        this.escolhasNivel.put(5, java.util.List.of(
            new habilidades.Magia("Magia Desperta", "Cria uma grande massa do seu elemento, causando 6d12 de dano massante.", 6, 6, 12),
            new habilidades.Habilidade("Proteção Absoluta", "Envolve-se do seu elemento: +3 de defesa e reflete 2d8 de dano do elemento a quem te acertar. Dura até o fim do combate.", 5)
        ));
    }

    @Override
    public int calcularVidaBase(int constituicaoBase) { return 10 + constituicaoBase; }

    @Override
    public int calcularManaBase(int presencaBase) { return 8 + presencaBase; }

    @Override
    public void aplicarBonusNivel(FichaRpg ficha) {
        int ganhoVida = 2 + ficha.getConstituicao();
        ficha.setVidaMaxima(ficha.getVidaMaxima() + ganhoVida);
        ficha.setVidaPersonagem(ficha.getVidaPersonagem() + ganhoVida);
        int ganhoMana = 3 + ficha.getPresenca();
        ficha.setManaMaxima(ficha.getManaMaxima() + ganhoMana);
        ficha.setManaPersonagem(ficha.getManaPersonagem() + ganhoMana);
    }

    @Override
    public void aplicarHabilidadesNivel(FichaRpg ficha, int novoNivel) {
        super.aplicarHabilidadesNivel(ficha, novoNivel);
        if (novoNivel == 3) {
            for (habilidades.Habilidade hab : ficha.getHabilidades()) {
                if (hab instanceof habilidades.Magia && hab.getNome().startsWith("Bola Elementar")) {
                    ((habilidades.Magia) hab).setAtaqueArea(true);
                }
            }
        }
    }

    @Override
    public int getBonusConstituicao() { return -2; }

    @Override
    public int getBonusPresenca() { return 2; }

    @Override
    public int getBonusIntelecto() { return 1; }
}
