package fichas;

import classes.ClasseRpg;
import itens.Arma;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;

public class FichaRpg {
    // Identificação
    private String nomePersonagem = "Desconhecido", nomePessoa;
    
    // Status de Sobrevivência
    private int nivel = 1;
    private int xp = 0;
    private int ouro = 0;
    private int vidaPersonagem, manaPersonagem;
    private int vidaMaxima, manaMaxima;


    // Encontros
    private boolean fadaEncontrada = false;
    
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
        this.vidaMaxima = this.vidaPersonagem;
        this.manaMaxima = this.manaPersonagem;
        
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
    public int getNivel() { return nivel; }
    public int getOuro() { return ouro; }
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
    public boolean isFadaEncontrada() { return fadaEncontrada; }
    public void setFadaEncontrada(boolean fadaEncontrada) { this.fadaEncontrada = fadaEncontrada; }

    // Setters de Combate
    public void setVidaPersonagem(int vida) { this.vidaPersonagem = Math.min(vida, vidaMaxima); }
    public void setManaPersonagem(int mana) { this.manaPersonagem = Math.min(mana, manaMaxima); }

    // Getters de Máximo
    public int getVidaMaxima() { return vidaMaxima; }
    public int getManaMaxima() { return manaMaxima; }

    // Setters de Máximo (usados no level up)
    public void setVidaMaxima(int vidaMaxima) { this.vidaMaxima = vidaMaxima; }
    public void setManaMaxima(int manaMaxima) { this.manaMaxima = manaMaxima; }

    // Dinheiro
    public void adicionarOuro(int quantidade) { this.ouro += Math.max(0, quantidade); }

    // XP necessária para subir do nível atual para o próximo
    public static int getXpNecessaria(int nivel) {
        switch (nivel) {
            case 1: return 100;
            case 2: return 300;
            case 3: return 700;
            case 4: return 1500;
            case 5: return 3500;
            case 6: return 8000;
            case 7: return 15000;
            case 8: return 40000;
            case 9: return 100000;
            default: return -1; // Nível 10 é o máximo
        }
    }

    // Adiciona XP e trata os up's de nível; retorna quantos níveis foram ganhos
    public int adicionarXp(int quantidade) {
        this.xp += Math.max(0, quantidade);
        int niveisGanhos = 0;
        while (nivel < 10) {
            int necessaria = getXpNecessaria(nivel);
            if (xp >= necessaria) {
                xp = 0; // ao subir de nível, a XP é resetada
                nivel++;
                niveisGanhos++;
                // Aplica bônus de vida e mana da classe
                if (classeDoPersonagem != null) {
                    classeDoPersonagem.aplicarBonusNivel(this);
                }
            } else {
                break;
            }
        }
        return niveisGanhos;
    }

    public int getXp() { return xp; }

    // Adicionar item ao inventário, empilhando se já existir
    public void adicionarItem(ItemRpg novoItem) {
        for (ItemRpg existente : inventario) {
            if (existente.getNome().equals(novoItem.getNome())) {
                existente.setQuantidade(existente.getQuantidade() + novoItem.getQuantidade());
                return;
            }
        }
        inventario.add(novoItem);
    }

    // Bônus aleatório de atributo (concedido pela Fada)
    public String aumentarAtributoAleatorio() {
        int sorteado = MecanicasRpg.rolarDado(6);
        switch (sorteado) {
            case 1 -> { constituicao++; return "Constituição"; }
            case 2 -> { destreza++; return "Destreza"; }
            case 3 -> { forca++; return "Força"; }
            case 4 -> { sabedoria++; return "Sabedoria"; }
            case 5 -> { intelecto++; return "Intelecto"; }
            default -> { presenca++; return "Presença"; }
        }
    }
}