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
        
        int quantidade = getQuantidadeDano();
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
            Interface.MostrarMensagem("A magia atinge: " + nomes.toString());
        }
        
        int bnsMagia = ficha.getIntelectoTeste();
        if (ficha.isSemiDeusAtivo()) bnsMagia += 4;
        int totalMagia = dano + bnsMagia;
        Interface.MostrarMensagem("-> Poder Mágico Total: " + dano + " + " + bnsMagia + " (Intelecto/Bônus) = " + totalMagia);
        Interface.Pausa(1500);
        
        for (Criatura alvo : afetados) {
            if (alvo.getVida() <= 0) continue;
            
            int dadoDefesa = MecanicasRpg.rolarDado(20);
            int totalDefesa = dadoDefesa + alvo.getDefesa();
            Interface.MostrarMensagem("\nDefesa de " + MotorDeCombate.rotuloCriatura(inimigos, alvo) + ": " + dadoDefesa + " (Dado) + " + alvo.getDefesa() + " (Defesa) = " + totalDefesa);
            Interface.Pausa(1500);
            
            if (totalDefesa >= totalMagia) {
                Interface.MostrarMensagem("A criatura resiste e recebe apenas metade do dano!");
                int danoFinal = Math.max(1, totalMagia / 2);
                alvo.setVida(alvo.getVida() - danoFinal);
                Interface.MostrarMensagem("Dano: " + danoFinal + " -> Vida: " + Math.max(0, alvo.getVida()));
            } else {
                Interface.MostrarMensagem("A magia atinge em cheio!");
                alvo.setVida(alvo.getVida() - totalMagia);
                Interface.MostrarMensagem("Dano: " + totalMagia + " -> Vida: " + Math.max(0, alvo.getVida()));
            }
            Interface.Pausa(1500);
        }
        
        return true;
    }
}
