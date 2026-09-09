package fichas;

import classes.ClasseRpg;
import itens.Arma;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;

public class FichaRpg {
    // Identificação
    private String nomePersonagem = "Desconhecido", nomePessoa;
    
    // Status de Sobrevivência
    private int vidaPersonagem, manaPersonagem;
    
    // Atributos Base
    private int constituicaoBase, destrezaBase, forcaBase, sabedoriaBase, intelectoBase, presencaBase;
    
    // Atributos Finais (Base + Modificadores)
    private int constituicao, destreza, forca, sabedoria, intelecto, presenca;
    
    // Classe
    private ClasseRpg classeDoPersonagem = null;
    
    // Defesa
    private int defesa, bonusDeDefesa;

    // Equipamento e Inventário
    private Arma armaEquipada;
    private List<ItemRpg> inventario = new ArrayList<>();
    
    // Habilidades
    private List<habilidades.Habilidade> habilidades = new ArrayList<>();

    // Construtor
    public FichaRpg(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public void setNomePersonagem(String nomePersonagem) {
        this.nomePersonagem = nomePersonagem;
    }

    // Distribuição de Pontos
    public void adicionarAtributo(int opcao, int pontos) {
        switch (opcao) {
            case 1 -> constituicaoBase += pontos;
            case 2 -> destrezaBase += pontos;
            case 3 -> forcaBase += pontos;
            case 4 -> sabedoriaBase += pontos;
            case 5 -> intelectoBase += pontos;
            case 6 -> presencaBase += pontos;
        }
    }

    public void setClasse(ClasseRpg classe) {
        this.classeDoPersonagem = classe;
        aplicarBonus();
    }

    public void resetarPontosBase() {
        this.constituicaoBase = 0;
        this.presencaBase = 0;
        this.destrezaBase = 0;
        this.sabedoriaBase = 0;
        this.intelectoBase = 0;
        this.forcaBase = 0;
        aplicarBonus();
    }

    // Aplicação de Modificadores e Equipamentos Iniciais
    public void aplicarBonus() {
        this.constituicao = constituicaoBase;
        this.destreza = destrezaBase;
        this.forca = forcaBase;
        this.sabedoria = sabedoriaBase;
        this.intelecto = intelectoBase;
        this.presenca = presencaBase;
        
        if (classeDoPersonagem == null) {
            this.vidaPersonagem = 0;
            this.manaPersonagem = 0;
            this.defesa = 10 + this.destreza + bonusDeDefesa;
            return;
        }

        this.vidaPersonagem = classeDoPersonagem.calcularVidaBase(this.constituicaoBase);
        this.manaPersonagem = classeDoPersonagem.calcularManaBase(this.presencaBase);
        
        this.constituicao += classeDoPersonagem.getBonusConstituicao();
        this.forca += classeDoPersonagem.getBonusForca();
        this.destreza += classeDoPersonagem.getBonusDestreza();
        this.sabedoria += classeDoPersonagem.getBonusSabedoria();
        this.intelecto += classeDoPersonagem.getBonusIntelecto();
        this.presenca += classeDoPersonagem.getBonusPresenca();
        
        this.defesa = 10 + this.destreza + bonusDeDefesa;

        // Ficha ganha a arma e os itens da classe
        this.armaEquipada = classeDoPersonagem.getArmaPrincipal();
        this.inventario = new ArrayList<>(classeDoPersonagem.getItensIniciais());
        
        // Ficha ganha as habilidades da classe
        this.habilidades = new ArrayList<>(classeDoPersonagem.getHabilidadesIniciais());

        // Calcula a defesa extra provida pelas Armaduras na mochila
        for (ItemRpg item : this.inventario) {
            if (item instanceof itens.Armadura) {
                this.defesa += ((itens.Armadura) item).getBonusDefesa();
            }
        }
    }

    // Validador de Ficha
    public boolean isFichaCompleta() {
        if (nomePersonagem.equals("Desconhecido") || nomePersonagem.trim().isEmpty()) {
            return false;
        }
        if (classeDoPersonagem == null) {
            return false;
        }
        int totalAtributosBase = constituicaoBase + destrezaBase + forcaBase + sabedoriaBase + intelectoBase + presencaBase;
        if (totalAtributosBase < 6) {
            return false;
        }
        return true;
    }

    // Getters
    public String getNomePersonagem() { return nomePersonagem; }
    public String getNomePessoa() { return nomePessoa; }
    public int getVidaPersonagem() { return vidaPersonagem; }
    public int getManaPersonagem() { return manaPersonagem; }
    public int getConstituicao() { return constituicao; }
    public int getDestreza() { return destreza; }
    public int getForca() { return forca; }
    public int getSabedoria() { return sabedoria; }
    public int getIntelecto() { return intelecto; }
    public int getPresenca() { return presenca; }
    public int getDefesa() { return defesa; }
    public ClasseRpg getClasseDoPersonagem() { return classeDoPersonagem; }
    public Arma getArmaEquipada() { return armaEquipada; }
    public List<ItemRpg> getInventario() { return inventario; }
    public List<habilidades.Habilidade> getHabilidades() { return habilidades; }
}
