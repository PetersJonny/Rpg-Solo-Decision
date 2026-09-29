package fichas;

import classes.ClasseRpg;
import itens.Arma;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;
import racas.Raca;
import telas.Interface;

/** Progressão do personagem: XP, nível e atributos (base/final). Opera sobre a ficha via campos package-private. */
public class GerenciadorDeCrescimento {

    public static void aplicarBonus(FichaRpg ficha) {

        ficha.constituicao = ficha.constituicaoBase;
        ficha.destreza = ficha.destrezaBase;
        ficha.forca = ficha.forcaBase;
        ficha.sabedoria = ficha.sabedoriaBase;
        ficha.intelecto = ficha.intelectoBase;
        ficha.presenca = ficha.presencaBase;
        
        if (ficha.classeDoPersonagem == null) {
            ficha.vidaPersonagem = 0;
            ficha.manaPersonagem = 0;
            // Raça ainda dá atributos/defesa mesmo sem classe escolhida
            if (ficha.raca != null) {
                ficha.constituicao += ficha.raca.getBonusConstituicao();
                ficha.forca += ficha.raca.getBonusForca();
                ficha.destreza += ficha.raca.getBonusDestreza();
                ficha.sabedoria += ficha.raca.getBonusSabedoria();
                ficha.intelecto += ficha.raca.getBonusIntelecto();
                ficha.presenca += ficha.raca.getBonusPresenca();
            }
            ficha.defesa = 10 + ficha.destreza + ficha.bonusDeDefesa + (ficha.raca != null ? ficha.raca.getBonusDefesa() : 0);
            return;
        }

        ficha.vidaPersonagem = ficha.classeDoPersonagem.calcularVidaBase(ficha.constituicaoBase);
        ficha.manaPersonagem = ficha.classeDoPersonagem.calcularManaBase(ficha.presencaBase);
        ficha.vidaMaxima = ficha.vidaPersonagem;
        ficha.manaMaxima = ficha.manaPersonagem;
        
        ficha.constituicao += ficha.classeDoPersonagem.getBonusConstituicao();
        ficha.forca += ficha.classeDoPersonagem.getBonusForca();
        ficha.destreza += ficha.classeDoPersonagem.getBonusDestreza();
        ficha.sabedoria += ficha.classeDoPersonagem.getBonusSabedoria();
        ficha.intelecto += ficha.classeDoPersonagem.getBonusIntelecto();
        ficha.presenca += ficha.classeDoPersonagem.getBonusPresenca();
        
        // Bônus racial (+1 no atributo da raça; defesa/vida extras p/ Dracônico)
        if (ficha.raca != null) {
            ficha.constituicao += ficha.raca.getBonusConstituicao();
            ficha.forca += ficha.raca.getBonusForca();
            ficha.destreza += ficha.raca.getBonusDestreza();
            ficha.sabedoria += ficha.raca.getBonusSabedoria();
            ficha.intelecto += ficha.raca.getBonusIntelecto();
            ficha.presenca += ficha.raca.getBonusPresenca();
        }
        
        // Bônus racial (+1 no atributo da raça + bônus permanentes de defesa/vida)
        ficha.defesa = 10 + ficha.destreza + ficha.bonusDeDefesa + (ficha.raca != null ? ficha.raca.getBonusDefesa() : 0);
        ficha.vidaMaxima += (ficha.raca != null ? ficha.raca.getBonusVidaMaxima() : 0);
        // A vida atual acompanha o máximo: começa cheia com o bônus de vida da raça
        ficha.vidaPersonagem += (ficha.raca != null ? ficha.raca.getBonusVidaMaxima() : 0);

        // Ficha ganha a arma e os itens da classe
        ficha.armaEquipada = ficha.classeDoPersonagem.getArmaPrincipal();
        ficha.inventario = new ArrayList<>(ficha.classeDoPersonagem.getItensIniciais());
        
        // Ficha ganha as habilidades da classe
        ficha.habilidades = new ArrayList<>(ficha.classeDoPersonagem.getHabilidadesIniciais());

        // Equipa a melhor armadura do inventário (as demais ficam na mochila)
        if (ficha.armaduraEquipada != null) {
            ficha.inventario.add(ficha.armaduraEquipada);
            ficha.armaduraEquipada = null;
        }
        ficha.equiparMelhorArmadura();
    
    }
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
    public static int adicionarXp(FichaRpg ficha, int quantidade) {

        ficha.xp += Math.max(0, quantidade);
        int niveisGanhos = 0;
        while (ficha.nivel < 10) {
            int necessaria = FichaRpg.getXpNecessaria(ficha.nivel);
            if (ficha.xp >= necessaria) {
                ficha.xp -= necessaria; // Antes era xp = 0 (bug que sumia com XP excedente)
                ficha.nivel++;
                niveisGanhos++;
                // Aplica bônus de vida e mana da classe
                if (ficha.classeDoPersonagem != null) {
                    ficha.classeDoPersonagem.aplicarBonusNivel(ficha);
                    // Ganha as habilidades do nível alcançado
                    ficha.classeDoPersonagem.aplicarHabilidadesNivel(ficha, ficha.nivel);
                }
            } else {
                break;
            }
        }
        return niveisGanhos;
    
    }
    public static void adicionarAtributo(FichaRpg ficha, int opcao, int pontos) {

        switch (opcao) {
            case 1: ficha.constituicaoBase += pontos; break;
            case 2: ficha.destrezaBase += pontos; break;
            case 3: ficha.forcaBase += pontos; break;
            case 4: ficha.sabedoriaBase += pontos; break;
            case 5: ficha.intelectoBase += pontos; break;
            case 6: ficha.presencaBase += pontos; break;
        }
    
    }
    public static void resetarPontosBase(FichaRpg ficha) {

        ficha.constituicaoBase = 0;
        ficha.presencaBase = 0;
        ficha.destrezaBase = 0;
        ficha.sabedoriaBase = 0;
        ficha.intelectoBase = 0;
        ficha.forcaBase = 0;
        ficha.aplicarBonus();
    
    }
    public static String aumentarAtributo(FichaRpg ficha, int opcao) {

        switch (opcao) {
            case 1: ficha.aumentarConstituicao(1); return "Constituição";
            case 2: ficha.destreza++; return "Destreza";
            case 3: ficha.forca++; return "Força";
            case 4: ficha.sabedoria++; return "Sabedoria";
            case 5: ficha.intelecto++; return "Intelecto";
            default: ficha.presenca++; return "Presença";
        }
    
    }
    public static String aumentarAtributoAleatorio(FichaRpg ficha) {

        int sorteado = MecanicasRpg.rolarDado(6);
        switch (sorteado) {
            case 1: ficha.aumentarConstituicao(1); return "Constituição";
            case 2: ficha.destreza++; return "Destreza";
            case 3: ficha.forca++; return "Força";
            case 4: ficha.sabedoria++; return "Sabedoria";
            case 5: ficha.intelecto++; return "Intelecto";
            default: ficha.presenca++; return "Presença";
        }
    
    }
    public static void aumentarConstituicao(FichaRpg ficha, int quantidade) {

        ficha.constituicao += quantidade;
        if (quantidade > 0 && ficha.nivel > 1) {
            int vidaRetroativa = quantidade * (ficha.nivel - 1);
            ficha.vidaMaxima += vidaRetroativa;
            ficha.vidaPersonagem = Math.min(ficha.vidaPersonagem + vidaRetroativa, ficha.vidaMaxima);
        }
    
    }
    public static void aumentarTodosAtributos(FichaRpg ficha, int quantidade) {

        ficha.destreza += quantidade;
        ficha.forca += quantidade;
        ficha.sabedoria += quantidade;
        ficha.intelecto += quantidade;
        ficha.presenca += quantidade;
        ficha.defesa += quantidade;
        ficha.aumentarConstituicao(quantidade);
    
    }
}
