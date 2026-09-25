package racas;

import java.io.Serializable;

// Raça do personagem: concede +1 permanente em um atributo (bônus racial) e
// uma passiva única. O padrão espelha classes.ClasseRpg: uma classe abstrata
// com os dados de exibição + uma classe concreta por raça.
public abstract class Raca implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nome;
    protected String bonusAtributo;    // nome do atributo com +1 (null p/ Humano = à escolha)
    protected String bonusDescricao;   // texto amigável do bônus ("+1 em Destreza")
    protected String passiva;          // nome da passiva
    protected String passivaDescricao; // como a passiva funciona

    public Raca(String nome, String bonusAtributo, String bonusDescricao,
                   String passiva, String passivaDescricao) {
        this.nome = nome;
        this.bonusAtributo = bonusAtributo;
        this.bonusDescricao = bonusDescricao;
        this.passiva = passiva;
        this.passivaDescricao = passivaDescricao;
    }

    public String getNome() { return nome; }
    public String getBonusAtributo() { return bonusAtributo; }
    public String getBonusDescricao() { return bonusDescricao; }
    public String getPassiva() { return passiva; }
    public String getPassivaDescricao() { return passivaDescricao; }

    // Bônus racial (+1) — cada raça retorna 1 no seu atributo.
    public int getBonusConstituicao() { return 0; }
    public int getBonusDestreza() { return 0; }
    public int getBonusForca() { return 0; }
    public int getBonusSabedoria() { return 0; }
    public int getBonusIntelecto() { return 0; }
    public int getBonusPresenca() { return 0; }

    // Bônus permanente de defesa (ex.: Dracônico — Escamas de Dragão, +2).
    public int getBonusDefesa() { return 0; }

    // Bônus permanente de vida máxima (ex.: Dracônico).
    public int getBonusVidaMaxima() { return 0; }

    // Passivas que dependem do contexto (sobrescritas nas raças que as têm)
    public boolean podeSobreviverCom1AoCair0() { return false; } // Humano
    public boolean podeRerrolarTeste() { return false; }         // Gnomo
    public boolean dobraChanceEncontrarFada() { return false; }  // Meio-Fada
    public boolean temBonusBuscaRecursos() { return false; }     // Elfo (Toque da Mata)
    public boolean temBonusDanoVidaBaixa() { return false; }     // Meio-Orque (Fúria Sombria)
    public boolean temBonusTestesNoturnos() { return false; }    // Vigia do Crepúsculo
    public boolean reduzCustoMana() { return false; }            // Meio-Fada

    // Texto completo para o menu de escolha (nome + bônus + passiva)
    public String getDescricaoCompleta() {
        return "  " + nome + " — " + bonusDescricao
                + "\n     Passiva: " + passiva + " — " + passivaDescricao;
    }
}
