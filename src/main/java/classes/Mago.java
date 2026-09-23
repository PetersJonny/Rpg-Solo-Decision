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
        this.habilidadesIniciais.add(new habilidades.Magia("Bola Elementar (" + elemento + ")", "Lança uma esfera de " + elemento.toLowerCase() + " que causa 3d10 de dano do elemento escolhido.", 2, 3, 10));
        this.habilidadesIniciais.add(new habilidades.Magia("Pequena Magia (" + elemento + ")", "Lança uma pequena carga de " + elemento.toLowerCase() + " que causa 2d8 de dano do elemento escolhido. Não gasta mana.", 0, 2, 8));

        // Escolhas de habilidade por nível
        this.escolhasNivel.put(5, java.util.List.of(
            new habilidades.Magia("Magia Desperta", "Cria uma grande massa do seu elemento, causando 6d12 de dano massante.", 6, 6, 12),
            new habilidades.ativas.HabilidadeProtecaoAbsoluta("Proteção Absoluta", "Envolve-se do seu elemento: +3 de defesa e reflete 2d8 de dano do elemento a quem te acertar. Dura até o fim do combate.", 5)
        ));

        this.escolhasNivel.put(7, java.util.List.of(
            new habilidades.ativas.HabilidadePrisao("Prisão", "Prende um inimigo em uma prisão de energia. Na vez dele, ele precisa passar em um teste de d20 (15 ou mais) para se libertar; enquanto preso, ele não consegue agir. Custa 5 de mana.", 5),
            new habilidades.Habilidade("Magia Proibida", "Uma vez por combate, gasta 5 de mana para fazer todos os testes dos inimigos desta rodada falharem. Não gasta sua ação.", 5)
        ));

        this.escolhasNivel.put(9, java.util.List.of(
            new habilidades.ativas.HabilidadePoderAbsoluto("Poder Absoluto", "Se envolve em energia do seu elemento por 15 de mana. Durante todo o combate, todas as suas magias dobram a quantidade de dados de dano.", 15)
        ));

        this.escolhasNivel.put(10, java.util.List.of(
            new habilidades.ativas.HabilidadeExplosaoDePoder("Explosão de Poder", "Você escolhe quanto de mana quer gastar: cada 2 de mana causa 2d12 de dano do seu elemento em TODOS os inimigos.", 0)
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
                if (hab instanceof habilidades.Magia && hab.getNome().startsWith("Pequena Magia")) {
                    ((habilidades.Magia) hab).setAtaqueArea(true);
                }
            }
        }
    }

    @Override
    public int getBonusForca() { return -2; }

    @Override
    public int getBonusPresenca() { return 2; }

    @Override
    public int getBonusIntelecto() { return 1; }
}
