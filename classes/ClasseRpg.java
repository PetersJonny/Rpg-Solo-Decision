package classes;

import itens.Arma;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;

public abstract class ClasseRpg {
    // Identificação
    protected String nome;
    
    // Equipamentos
    protected Arma armaPrincipal;
    protected List<ItemRpg> itensIniciais = new ArrayList<>();
    
    // Habilidades
    protected List<habilidades.Habilidade> habilidadesIniciais = new ArrayList<>();
    
    // Ataque Básico Universal
    protected Arma ataqueDesarmado = new Arma("Soco", "Um ataque corpo a corpo simples e direto que causa 1d3 de dano.", "CaC", 3, 1, 0);

    // Getters
    public String getNome() { return nome; }
    public Arma getArmaPrincipal() { return armaPrincipal; }
    public Arma getAtaqueDesarmado() { return ataqueDesarmado; }
    public List<ItemRpg> getItensIniciais() { return itensIniciais; }
    public List<habilidades.Habilidade> getHabilidadesIniciais() { return habilidadesIniciais; }

    // Status Base
    public abstract int calcularVidaBase(int constituicaoBase);
    public abstract int calcularManaBase(int presencaBase);

    // Modificadores de Status
    public int getBonusConstituicao() { return 0; }
    public int getBonusForca() { return 0; }
    public int getBonusDestreza() { return 0; }
    public int getBonusIntelecto() { return 0; }
    public int getBonusPresenca() { return 0; }
    public int getBonusSabedoria() { return 0; }
}
