package classes;

import fichas.FichaRpg;
import itens.Arma;
import itens.Consumivel;

public class Healer extends ClasseRpg {
    
    public Healer() {
        this.nome = "Healer";
        this.armaPrincipal = new Arma("Bisturi", "Um bisturi afiado e rápido, perfeito para cortes precisos. Causa 1d4 de dano.", "CaC", 4, 1, 1, "Ágil", true);
        this.itensIniciais.add(this.armaPrincipal);
        this.itensIniciais.add(new Consumivel("Kit Médico", "Pode ser usado para curar 1d4 de vida. Possui 5 usos.", 5));
        
        // Habilidade Base
        this.habilidadesIniciais.add(new habilidades.Habilidade("Conhecimento Avançado", "Permite rerrolar um resultado falho em testes de atributos.", 2));
        
        // Habilidades por nível
        this.habilidadesPorNivel.put(3, java.util.List.of(
            new habilidades.Habilidade("Cura Reforçada", "Ao usar o Kit Médico, você pode gastar 1 de mana para curar 2d4 de vida extra.", 1)
        ));
        this.habilidadesPorNivel.put(5, java.util.List.of(
            new habilidades.Habilidade("Cura para a Morte", "Injeta um líquido mortal que causa 3d8 de dano ao final de cada ataque contra o alvo, a partir do próximo turno. Dura até o fim do combate.", 3),
            new habilidades.Habilidade("Cura Total", "Se o personagem morrer, você pode gastar 10 de mana para revivê-lo com a vida cheia. Pode ser usada apenas uma vez por combate.", 10)
        ));
    }

    @Override
    public int calcularVidaBase(int constituicaoBase) { return 14 + constituicaoBase; }

    @Override
    public int calcularManaBase(int presencaBase) { return 5 + presencaBase; }

    @Override
    public void aplicarBonusNivel(FichaRpg ficha) {
        int ganhoVida = 3 + ficha.getConstituicao();
        ficha.setVidaMaxima(ficha.getVidaMaxima() + ganhoVida);
        ficha.setVidaPersonagem(ficha.getVidaPersonagem() + ganhoVida);
        int ganhoMana = 2 + ficha.getPresenca();
        ficha.setManaMaxima(ficha.getManaMaxima() + ganhoMana);
        ficha.setManaPersonagem(ficha.getManaPersonagem() + ganhoMana);
    }

    @Override
    public int getBonusSabedoria() { return 1; }

    @Override
    public int getBonusIntelecto() { return 2; }

    @Override
    public int getBonusForca() { return -2; }
}
