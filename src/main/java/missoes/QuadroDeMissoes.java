package missoes;

import java.util.ArrayList;
import java.util.List;

import fichas.FichaRpg;
import telas.Interface;

public class QuadroDeMissoes {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;

    public static class Missao {
        public final String nome;
        public final String onde;
        public final String objetivo;
        public final String recompensa;
                        public final boolean noQuadro;

        public Missao(String nome, String onde, String objetivo, String recompensa) {
            this(nome, onde, objetivo, recompensa, true);
        }

        public Missao(String nome, String onde, String objetivo, String recompensa, boolean noQuadro) {
            this.nome = nome;
            this.onde = onde;
            this.objetivo = objetivo;
            this.recompensa = recompensa;
            this.noQuadro = noQuadro;
        }
    }

        private static final List<Missao> REGISTRO = List.of(
            new Missao(
                    "A Filha Perdida",
                    "Perto das cavernas ao redor da vila",
                    "Uma filha foi vista por último perto das cavernas da vila. É preciso encontrá-la antes que algo pior aconteça.",
                    "100 ouro — falar com a dona da alfaiataria ao encontrá-la"),
            new Missao(
                    "A Neta Perdida",
                    "Entrada da vila, perto da barraca de frutas",
                    "A netinha da velhinha sumiu enquanto colhiam frutas na floresta para fazer uma torta. Encontrá-la antes que algo aconteça.",
                    "A gratidão eterna da velhinha",
                    false)
    );

    private static void exibirAviso(Missao m) {
        Interface.MostrarMensagem("\n" + CIANO + "==============================" + RESET);
        Interface.MostrarMensagem("  " + AMARELO + m.nome.toUpperCase() + RESET);
        Interface.MostrarMensagem(CIANO + "==============================" + RESET);
        System.out.println("  Local: " + m.onde);
        System.out.println("  Objetivo: " + m.objetivo);
        System.out.println("  Recompensa: " + m.recompensa);
        Interface.MostrarMensagem(CIANO + "==============================\n" + RESET);
    }

    private static void exibirNovidades(FichaRpg ficha, Missao m) {
        List<String> novidades = ficha.getNovidades(m.nome);
        if (novidades.isEmpty()) {
            Interface.MostrarMensagem("\n  " + m.nome + ": você ainda não descobriu nada além do aviso.");
            return;
        }
        Interface.MostrarMensagem("\n  " + VERDE + "O que você já sabe sobre " + AMARELO + m.nome + RESET + ":");
        for (String n : novidades) {
            if (n.startsWith("!")) n = n.substring(1);
            Interface.MostrarMensagem("    • " + n);
        }
    }

    public static void QuadroDeMissoes(FichaRpg ficha) {
        List<Missao> disponiveis = new ArrayList<>();
        List<Missao> ativas = new ArrayList<>();
        for (Missao m : REGISTRO) {
            if (ficha.isMissaoAceita(m.nome)) ativas.add(m);
            else if (m.noQuadro) disponiveis.add(m);
        }

        Interface.cabecalhoMenu("QUADRO DE MISSÕES");
        Interface.MostrarMensagem("\nSobre a parede da taverna, um quadro de cortiça guarda avisos e recompensas pregados com alfinetes.");

        if (ativas.isEmpty()) {
            Interface.MostrarMensagem("\n  Você ainda não aceitou nenhuma missão.");
        } else {
            Interface.MostrarMensagem(VERDE + "\n  MISSÕES EM ANDAMENTO (" + ativas.size() + "):" + RESET);
            for (Missao m : ativas) {
                int novas = 0;
                for (String n : ficha.getNovidades(m.nome)) {
                    if (n.startsWith("!")) novas++;
                }
                String marca = novas > 0 ? VERDE + "  [" + novas + " nova" + (novas > 1 ? "s" : "") + "]" + RESET : "";
                Interface.MostrarMensagem("   • " + AMARELO + m.nome + RESET + " — " + m.onde + marca);
            }
        }

        if (disponiveis.isEmpty()) {
            Interface.MostrarMensagem("\nNenhuma missão nova disponível no momento. Volte outro dia.");
            Interface.Pausa(2000);
            return;
        }

        Interface.MostrarMensagem("\n  Avisos disponíveis no quadro:\n");
        for (int i = 0; i < disponiveis.size(); i++) {
            Missao m = disponiveis.get(i);
            System.out.println("  " + (i + 1) + ". " + AMARELO + m.nome + RESET + " (" + m.recompensa + ")");
        }
        System.out.println("  " + VERDE + "0. Voltar" + RESET);

        System.out.println("\n  Escolha um aviso para ler:");
        int escolha = Interface.lerOpcao(0, disponiveis.size());
        if (escolha == 0) return;

        Missao m = disponiveis.get(escolha - 1);
        exibirAviso(m);

        System.out.println("  Deseja aceitar esta missão?\n");
        System.out.println("  1. Aceitar");
        System.out.println("  2. Não aceitar");
        if (Interface.lerOpcao(2) != 1) {
            Interface.MostrarMensagem("\nVocê deixa o aviso de " + m.nome + " no quadro.");
            Interface.Pausa(1200);
            return;
        }

        ficha.aceitarMissao(m.nome);
        Interface.MostrarMensagem("\n" + AMARELO + "Missão aceita: " + m.nome.toUpperCase() + RESET + "!");
        Interface.MostrarMensagem("Lembre-se: a recompensa é entregue por quem postou o aviso ao concluir o objetivo.");
        Interface.Pausa(1800);
    }

        public static void MissoesEmAndamento(FichaRpg ficha) {
        List<String> aceitas = ficha.getMissoesAceitas();
        Interface.cabecalhoMenu("MISSÕES EM ANDAMENTO");
        if (aceitas.isEmpty()) {
            Interface.MostrarMensagem("\nVocê não aceitou nenhuma missão ainda. Vale dar uma passadinha na taverna para ler o quadro de missões.");
            Interface.Pausa(1800);
            return;
        }
        for (String nome : aceitas) {
            Missao m = null;
            for (Missao reg : REGISTRO) {
                if (reg.nome.equals(nome)) {
                    m = reg;
                    break;
                }
            }
            if (m == null) {
                Interface.MostrarMensagem("   • " + AMARELO + nome + RESET + " (missão antiga/desconhecida)");
                continue;
            }
            Interface.MostrarMensagem("\n" + CIANO + "==============================" + RESET);
            Interface.MostrarMensagem("  " + AMARELO + m.nome.toUpperCase() + RESET);
            Interface.MostrarMensagem(CIANO + "==============================" + RESET);
            System.out.println("  Local: " + m.onde);
            System.out.println("  Objetivo: " + m.objetivo);
            System.out.println("  Recompensa: " + m.recompensa);
            Interface.MostrarMensagem(CIANO + "==============================" + RESET);
            exibirNovidades(ficha, m);
            ficha.marcarNovidadesVistas(m.nome);
        }
        Interface.Pausa(2200);
    }
}
