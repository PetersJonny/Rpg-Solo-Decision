package classes;

import fichas.FichaRpg;
import itens.Arma;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ClasseRpg {
    // Identificação
    protected String nome;
    
    // Equipamentos
    protected Arma armaPrincipal;
    protected List<ItemRpg> itensIniciais = new ArrayList<>();
    
    // Habilidades
    protected List<habilidades.Habilidade> habilidadesIniciais = new ArrayList<>();
    
    // Habilidades ganhas ao subir de nível (nível -> habilidades)
    protected Map<Integer, List<habilidades.Habilidade>> habilidadesPorNivel = new HashMap<>();

    // Opções de habilidade para escolher ao subir de nível (nível -> lista de opções)
    protected Map<Integer, List<habilidades.Habilidade>> escolhasNivel = new HashMap<>();
    
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

    // Recompensa ao subir de nível (aplicada sobre max e atual)
    public abstract void aplicarBonusNivel(FichaRpg ficha);

    // Modificadores de Status
    public int getBonusConstituicao() { return 0; }
    public int getBonusForca() { return 0; }
    public int getBonusDestreza() { return 0; }
    public int getBonusIntelecto() { return 0; }
    public int getBonusPresenca() { return 0; }
    public int getBonusSabedoria() { return 0; }

    // Aplica as habilidades ganhas ao chegar em um novo nível
    public void aplicarHabilidadesNivel(FichaRpg ficha, int novoNivel) {
        List<habilidades.Habilidade> paraNivel = habilidadesPorNivel.get(novoNivel);
        if (paraNivel != null) {
            for (habilidades.Habilidade hab : paraNivel) {
                ficha.getHabilidades().add(hab);
            }
        }
    }

    // Retorna as opções de habilidade para escolher ao chegar em um novo nível
    public List<habilidades.Habilidade> getEscolhasNivel(int nivel) {
        return escolhasNivel.get(nivel);
    }

    // Retorna TODAS as habilidades de escolha ainda não aprendidas, dos níveis de escolha
    // anteriores e do nível atual (a que ficou para trás volta a aparecer)
    public List<habilidades.Habilidade> getEscolhasDisponiveis(FichaRpg ficha, int nivel) {
        List<habilidades.Habilidade> opcoes = new ArrayList<>();
        for (int lvl = 5; lvl <= nivel; lvl++) {
            List<habilidades.Habilidade> dupla = escolhasNivel.get(lvl);
            if (dupla == null) continue;
            for (habilidades.Habilidade hab : dupla) {
                boolean jaTem = false;
                for (habilidades.Habilidade conhecida : ficha.getHabilidades()) {
                    if (conhecida.getNome().equals(hab.getNome())) {
                        jaTem = true;
                        break;
                    }
                }
                if (!jaTem) {
                    opcoes.add(hab);
                }
            }
        }
        return opcoes;
    }
}
