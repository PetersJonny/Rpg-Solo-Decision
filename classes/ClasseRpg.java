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

    // Getters
    public String getNome() { return nome; }
    public Arma getArmaPrincipal() { return armaPrincipal; }
    public List<ItemRpg> getItensIniciais() { return itensIniciais; }

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
