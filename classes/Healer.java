package classes;

import itens.Arma;
import itens.Consumivel;

public class Healer extends ClasseRpg {
    
    public Healer() {
        this.nome = "Healer";
        this.armaPrincipal = new Arma("Arco", "Um arco longo utilizado para realizar ataques à distância, causando 1d6 de dano.", "LA", 6, 1, 1);
        this.itensIniciais.add(this.armaPrincipal);
        this.itensIniciais.add(new Consumivel("Kit Médico", "Pode ser usado para curar 1d4 de vida. Possui 5 usos.", 5));
        this.itensIniciais.add(new Consumivel("Flechas", "Munição necessária para realizar disparos com o arco.", 15));
        
        // Habilidade Base
        this.habilidadesIniciais.add(new habilidades.Habilidade("Conhecimento Avançado", "Permite rerrolar um resultado falho em testes de atributos.", 2));
    }

    @Override
    public int calcularVidaBase(int constituicaoBase) { return 14 + constituicaoBase; }

    @Override
    public int calcularManaBase(int presencaBase) { return 5 + presencaBase; }

    @Override
    public int getBonusSabedoria() { return 1; }

    @Override
    public int getBonusIntelecto() { return 2; }

    @Override
    public int getBonusForca() { return -2; }
}
