package eventos;

import classes.*;
import criaturas.Criatura;
import fichas.FichaRpg;
import itens.Arma;
import itens.Consumivel;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class Floresta {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;

    // ==================== TEMPO ====================

    private static void avancarTempoComMensagens(FichaRpg ficha, int unidades) {
        TimeManager.avancarTempoComMensagens(ficha, unidades);
    }

    // ==================== EXPLORAÇÃO ====================

    public static void Explorar(FichaRpg ficha) {
        TimeManager.Explorar(ficha);
    }

    public static void BuscarRecursos(FichaRpg ficha) {
        TimeManager.BuscarRecursos(ficha);
    }

    // ==================== CONSTRUÇÃO ====================

    public static void MenuConstrucao(FichaRpg ficha) {
        eventos.ExplorationManager.MenuConstrucao(ficha);
    }

    // ==================== COMPANHEIRO ====================

    public static void ConversarComCompanheiro(FichaRpg ficha) {
        companheiros.CompanionManager.ConversarComCompanheiro(ficha);
    }

    // ==================== ENCONTRO COM CRIATURAS ====================

    public static void EventoAnimal(FichaRpg ficha) {
        Interface.MostrarMensagem("\nAlgo se move por entre as árvores...");
        Interface.Pausa(2500);

        if (MecanicasRpg.rolarDado(100) <= 10) {
            loja.Vendedor.EncontrarVendedor(ficha);
            return;
        }

        if (!ficha.isFadaEncontrada() && MecanicasRpg.rolarDado(100) <= 20) {
            ficha.setFadaEncontrada(true);
            EventoFada(ficha);
            return;
        }

        int tipo = MecanicasRpg.rolarDado(3);
        List<Criatura> inimigos = criaturas.CriaturaFactory.criarGrupoMonstros(tipo, ficha.isEhNoite());
        Criatura referencia = inimigos.get(0);

        Interface.pressionarParaTeste("Presença");
        int dadoPresenca = MecanicasRpg.rolarDado(20);
        int totalPresenca = dadoPresenca + ficha.getPresencaTeste();
        Interface.MostrarMensagem("-> Teste de Presença: " + dadoPresenca + " (Dado) + " + ficha.getPresencaTeste() + " (Atributo) = " + totalPresenca + " (Dificuldade: " + referencia.getTestePresenca() + ")");
        Interface.Pausa(2500);

        if (totalPresenca >= referencia.getTestePresenca()) {
            Interface.MostrarMensagem("\n" + nomesDosInimigos(inimigos) + " apareceu entre as sombras das árvores e você o avistou antes!");
            Interface.Pausa(2500);

            System.out.println("  O que deseja fazer?");
            System.out.println("  1. Lutar (Você terá +2 de Iniciativa extra por surpreendê-lo)");
            System.out.println("  2. Tentar Fugir furtivamente");
            int escolha = Interface.lerOpcao(1, 2);

            if (escolha == 1) {
                Interface.MostrarMensagem("\nVocê saca sua arma e parte para cima!");
                Interface.Pausa(2500);
                combate.CombatManager.IniciarCombate(ficha, inimigos, true);
            } else if (escolha == 2) {
                Interface.pressionarParaTeste("Destreza (Fuga)");
                int dadoDestreza = MecanicasRpg.rolarDado(20);
                int totalDestreza = dadoDestreza + ficha.getDestrezaTeste();
                Interface.MostrarMensagem("-> Teste de Destreza (Fuga): " + dadoDestreza + " (Dado) + " + ficha.getDestrezaTeste() + " (Atributo) = " + totalDestreza);
                Interface.Pausa(2500);
                if (totalDestreza >= 12) {
                    Interface.MostrarMensagem("\nVocê recua lentamente pelas sombras e foge com sucesso, sem ser notado.");
                    Interface.Pausa(2500);
                } else {
                    Interface.MostrarMensagem("\nVocê pisa em um galho seco! A ameaça percebe você e avança!");
                    Interface.Pausa(2500);
                    combate.CombatManager.IniciarCombate(ficha, inimigos, false);
                }
            }
        } else {
            Interface.MostrarMensagem("\n" + nomesDosInimigos(inimigos) + " saltou das sombras e te surpreendeu!");
            Interface.Pausa(2500);
            combate.CombatManager.IniciarCombate(ficha, inimigos, false);
        }
    }

    // ==================== ENCONTRO COM A FADA ====================

    private static void EventoFada(FichaRpg ficha) {
        EventManager.EncontrarFada(ficha);
    }

    // ==================== UTILITÁRIOS DE COMBATE (delegados) ====================

    public static boolean ehItemConsumivel(ItemRpg item) {
        return combate.CombatResolver.ehItemConsumivel(item);
    }

    public static boolean usarItemForaDeCombate(FichaRpg ficha, ItemRpg item, int quantidade) {
        return combate.ItemUser.usarItemForaDeCombate(ficha, item, quantidade);
    }

    public static List<Criatura> inimigosVivos(List<Criatura> inimigos) {
        List<Criatura> vivos = new ArrayList<>();
        for (Criatura c : inimigos) {
            if (c.getVida() > 0) vivos.add(c);
        }
        return vivos;
    }

    public static String rotuloCriatura(List<Criatura> inimigos, Criatura alvo) {
        return combate.CombatManager.rotuloCriatura(inimigos, alvo);
    }

    private static String nomesDosInimigos(List<Criatura> inimigos) {
        return combate.CombatManager.nomesDosInimigos(inimigos);
    }
}
