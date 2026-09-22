package habilidades;

import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import java.util.ArrayList;
import mecanicas.MotorDeCombate;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class Magia extends Habilidade {
    private int quantidadeDano;
    private int dadoDano;
    private boolean ataqueArea;

    public Magia(String nome, String descricao, int custoMana) {
        this(nome, descricao, custoMana, 0, 0);
    }

    public Magia(String nome, String descricao, int custoMana, int quantidadeDano, int dadoDano) {
        super(nome, descricao, custoMana);
        this.quantidadeDano = quantidadeDano;
        this.dadoDano = dadoDano;
    }

    public int getQuantidadeDano() { return quantidadeDano; }
    public int getDadoDano() { return dadoDano; }
    public boolean isAtaqueArea() { return ataqueArea; }
    public void setAtaqueArea(boolean ataqueArea) { this.ataqueArea = ataqueArea; }

    @Override
    public boolean executar(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex) {
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return true;
        Criatura inimigo = inimigos.get(alvoIndex);
        
        int custoPago = MotorDeCombate.custoEfetivoMagia(ficha, this);
        if (ficha.getManaPersonagem() < custoPago) {
            Interface.ExibirErro("Mana insuficiente!");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoPago);
        if (custoPago < getCustoMana()) {
            Interface.MostrarMensagem("(Pequeno Grimório reduziu o custo da magia em 1!)");
            Interface.Pausa(1000);
        }
        
        Interface.MostrarMensagem("\nVocê usa " + getNome() + "!");
        Interface.Pausa(1500);
        
        int quantidade = getQuantidadeDano();
        if (ficha.isMagiaBonusAtivo()) {
            quantidade++;
            Interface.MostrarMensagem("(Mesa de Magias! +1 dado de dano na sua habilidade)");
            Interface.Pausa(1000);
        }
        if (ficha.temItem("Cajado de Sangue")) {
            quantidade++;
            Interface.MostrarMensagem("(Cajado de Sangue! +1 dado de dano na sua magia)");
            Interface.Pausa(1000);
        }
        if (ficha.isPoderAbsolutoAtivo()) {
            quantidade *= 2;
            Interface.MostrarMensagem("(Poder Absoluto dobra os dados de dano das suas magias!)");
            Interface.Pausa(1000);
        }
        
        StringBuilder roladas = new StringBuilder();
        int dano = 0;
        Interface.pressionarParaRolar();
        for (int i = 0; i < quantidade; i++) {
            int dadoRolado = MecanicasRpg.rolarDado(getDadoDano());
            dano += dadoRolado;
            if (roladas.length() > 0) roladas.append(" + ");
            roladas.append(dadoRolado);
        }
        
        Interface.MostrarMensagem("-> Dados Rolados: " + roladas + " = " + dano + " (Dano Mágico: " + quantidade + "d" + getDadoDano() + ")");
        Interface.Pausa(2000);
        
        if (ficha.temItem("Chapéu Mágico")) {
            dano += 3;
            Interface.MostrarMensagem("(Chapéu Mágico aumentou o dano em +3!)");
            Interface.Pausa(1000);
        }
        
        if (getNome().equals("Peso da Espada")) {
            dano += ficha.getForca();
            Interface.MostrarMensagem("(Peso da Espada: +" + ficha.getForca() + " de Força no dano!)");
            Interface.Pausa(1000);
        }
        
        List<Criatura> afetados = new ArrayList<>();
        afetados.add(inimigo);
        if (isAtaqueArea()) {
            if (alvoIndex - 1 >= 0) afetados.add(inimigos.get(alvoIndex - 1));
            if (alvoIndex + 1 < inimigos.size()) afetados.add(inimigos.get(alvoIndex + 1));
            StringBuilder nomes = new StringBuilder();
            for (Criatura afetado : afetados) {
                if (afetado.getVida() > 0) {
                    if (nomes.length() > 0) nomes.append(", ");
                    nomes.append(MotorDeCombate.rotuloCriatura(inimigos, afetado));
                }
            }
            Interface.MostrarMensagem("-> Ataque em área! Atinge: " + nomes.toString());
            Interface.Pausa(2000);
        }
        
        for (Criatura alvo : afetados) {
            if (alvo.getVida() <= 0) continue;
            alvo.setVida(alvo.getVida() - dano);
            Interface.MostrarMensagem(MotorDeCombate.rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
            Interface.Pausa(1500);
        }
        
        MotorDeCombate.aplicarVenenoCuraParaMorte(ficha, inimigos, alvoIndex);
        return true;
    }
}
