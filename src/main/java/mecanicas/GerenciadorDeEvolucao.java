package mecanicas;

import java.util.List;
import classes.*;
import itens.Arma;
import java.util.ArrayList;
import mecanicas.MecanicasRpg;
import criaturas.Criatura;
import itens.ItemRpg;
import java.util.HashSet;
import java.util.Set;
import fichas.FichaRpg;
import comandos.*;
import itens.Consumivel;
import telas.Interface;

import static mecanicas.MotorDeCombate.*;

public class GerenciadorDeEvolucao {

    public static void escolherHabilidadeNivel(FichaRpg ficha, int nivel, List<habilidades.Habilidade> opcoes) {
        Interface.cabecalhoMenu("NOVA HABILIDADE - NÍVEL " + nivel);
        System.out.println("\n  (inclui habilidades de escolha de níveis anteriores ainda não aprendidas)\n");
        for (int i = 0; i < opcoes.size(); i++) {
            habilidades.Habilidade h = opcoes.get(i);
            System.out.println("  " + (i + 1) + ". " + CIANO + h.getNome() + RESET + " (Custo: " + h.getCustoMana() + " Mana)");
            System.out.println("     " + h.getDescricao());
        }
        System.out.println("\n  Escolha uma habilidade:");

        int escolha = Interface.lerInteiro();

        if (escolha < 1 || escolha > opcoes.size()) {
            Interface.ExibirErro("Escolha inválida!");
            Interface.Pausa(1500);
            escolha = 1;
        }

        habilidades.Habilidade aprendida = opcoes.get(escolha - 1);
        ficha.getHabilidades().add(aprendida);
        Interface.MostrarMensagem("\nVocê aprendeu a habilidade: " + aprendida.getNome() + "!");
        Interface.MostrarMensagem(aprendida.getDescricao());
        Interface.Pausa(2000);
        aplicarArmaMentalSeAprendida(ficha, aprendida);
        aplicarDeusSeAprendido(ficha, aprendida);
        aplicarConhecimentoAbsolutoSeAprendido(ficha, aprendida);
    }

    public static void aplicarDeusSeAprendido(FichaRpg ficha, habilidades.Habilidade aprendida) {
        if (!aprendida.getNome().equals("Deus")) return;
        if (ficha.isDeusAtivo()) return;

                        if (ficha.isSemiDeusAtivo() && ficha.getSemiDeusVidaOriginalMax() > 0) {
            ficha.setVidaMaxima(ficha.getSemiDeusVidaOriginalMax());
            ficha.setSemiDeusVidaOriginalMax(0);
        }

        int bonusVida = ficha.getVidaMaximaBase() / 2;
        ficha.setVidaMaxima(ficha.getVidaMaximaBase() + bonusVida);
        ficha.setSemiDeusAtivo(true);
        ficha.setDeusAtivo(true);
        if (!temHabilidade(ficha, "Cura Incessante")) {
            ficha.getHabilidades().add(new habilidades.ativas.HabilidadeCuraIncessante("Cura Incessante", "Cura toda a sua vida. Pode ser usada apenas uma vez por combate.", 0));
        }
                ficha.getHabilidades().removeIf(h -> h.getNome().equals("Semi Deus"));
        Interface.MostrarMensagem("\nVocê se torna um Deus! A forma de Semi Deus fica permanentemente ativa.");
        Interface.MostrarMensagem("Vida máxima aumentada em " + bonusVida + " e você ganhou a habilidade Cura Incessante!");
        Interface.Pausa(2500);
    }

    public static void aplicarConhecimentoAbsolutoSeAprendido(FichaRpg ficha, habilidades.Habilidade aprendida) {
        if (!aprendida.getNome().equals("Conhecimento Absoluto")) return;
        if (ficha.isConhecimentoAbsolutoAplicado()) return;

        int vidaAntes = ficha.getVidaMaxima();
        ficha.aumentarTodosAtributos(2);
        ficha.setConhecimentoAbsolutoAplicado(true);
        Interface.MostrarMensagem("\nConhecimento Absoluto! +2 em TODOS os atributos.");
        if (ficha.getVidaMaxima() > vidaAntes) {
            Interface.MostrarMensagem("Vida máxima aumentada em " + (ficha.getVidaMaxima() - vidaAntes) + " pelo retroativo de Constituição!");
        }
        Interface.Pausa(2500);
    }

    public static void aplicarArmaMentalSeAprendida(FichaRpg ficha, habilidades.Habilidade aprendida) {
        if (!aprendida.getNome().equals("Arma Mental")) return;

        for (ItemRpg item : ficha.getInventario()) {
            if (item instanceof itens.Arma && item.getNome().equals("Bisturi")) {
                itens.Arma bisturi = (itens.Arma) item;
                bisturi.setDadoDanoArma(8);
                bisturi.setQuantidadeDanoArma(3);
            }
        }
        if (ficha.getArmaEquipada() != null && ficha.getArmaEquipada().getNome().equals("Bisturi")) {
            ficha.getArmaEquipada().setDadoDanoArma(8);
            ficha.getArmaEquipada().setQuantidadeDanoArma(3);
        }
        Interface.MostrarMensagem("\nArma Mental! Seu Bisturi agora causa 3d8 de dano!");
        Interface.Pausa(2000);
    }

    public static void escolherPontoAtributo(FichaRpg ficha) {
        while (true) {
            Interface.cabecalhoMenu("PONTO DE ATRIBUTO");
            System.out.println("\n  Você ganhou um ponto de atributo! Escolha onde gastar:\n");
            System.out.println("  1. Constituição");
            System.out.println("  2. Destreza");
            System.out.println("  3. Força");
            System.out.println("  4. Sabedoria");
            System.out.println("  5. Intelecto");
            System.out.println("  6. Presença");

            int escolha = Interface.lerInteiro();

            if (escolha >= 1 && escolha <= 6) {
                String atributo = ficha.aumentarAtributo(escolha);
                Interface.MostrarMensagem("\n+1 de " + atributo + "!");
                if (atributo.equals("Constituição") && ficha.getNivel() > 1) {
                    Interface.MostrarMensagem("Vida máxima aumentada em " + (ficha.getNivel() - 1) + " pelo retroativo de Constituição dos níveis anteriores!");
                }
                Interface.Pausa(1500);
                return;
            }

            Interface.ExibirErro("Opção inválida!");
        }
    }
}
