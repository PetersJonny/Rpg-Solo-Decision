package classes;

import fichas.FichaRpg;
import itens.Arma;
import itens.Consumivel;

public class Healer extends ClasseRpg {
    
    public Healer() {
        this.nome = "Healer";
        this.armaPrincipal = new Arma("Bisturi", "Um bisturi afiado e rápido, perfeito para cortes precisos. Causa 1d4 de dano.", "CaC", 4, 1, 1, "Ágil", true);
        this.itensIniciais.add(this.armaPrincipal);
        this.itensIniciais.add(new Consumivel("Kit Médico", "Pode ser usado para curar 1d4 de vida e acaba com uma infecção. Possui 5 usos.", 5));
        
        // Habilidade Base
        this.habilidadesIniciais.add(new habilidades.Habilidade("Conhecimento Avançado", "Permite rerrolar um resultado falho em testes de atributos.", 2));
        
        // Habilidades por nível
        this.habilidadesPorNivel.put(3, java.util.List.of(
            new habilidades.Habilidade("Cura Reforçada", "Ao usar o Kit Médico, você pode gastar 1 de mana para curar 2d4 de vida extra.", 1)
        ));
        this.habilidadesPorNivel.put(5, java.util.List.of(
            new habilidades.ativas.HabilidadeCuraParaMorte("Cura para a Morte", "Injeta um líquido mortal que causa 3d8 de dano ao final de cada ataque contra o alvo, a partir do próximo turno. Dura até o fim do combate.", 3),
            new habilidades.ativas.HabilidadeCuraTotal("Cura Total", "Se o personagem morrer, você pode gastar 10 de mana para revivê-lo com a vida cheia. Pode ser usada apenas uma vez por combate.", 10)
        ));

        this.escolhasNivel.put(7, java.util.List.of(
            new habilidades.ativas.HabilidadeConhecimentoAvassalador("Conhecimento Avassalador", "Gasta sua ação para fazer um teste de Intelecto (dificuldade 15). Se passar, você descobre tudo sobre os monstros do combate.", 0),
            new habilidades.Habilidade("Arma Mental", "Você entende sua arma como ninguém: o Bisturi passa a causar 3d8 de dano no lugar de 1d4.", 0, true)
        ));

        this.escolhasNivel.put(9, java.util.List.of(
            new habilidades.ativas.HabilidadeCuraAbsoluta("Cura Absoluta", "Gasta 10 de mana para injetar um líquido que cura toda a sua vida e cria uma proteção extra na pele, dobrando sua vida. A vida extra é gasta primeiro, antes da vida real.", 10)
        ));

        this.escolhasNivel.put(10, java.util.List.of(
            new habilidades.Habilidade("Conhecimento Absoluto", "Seu conhecimento atinge o ápice: você ganha +2 em TODOS os atributos.", 0, true)
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
