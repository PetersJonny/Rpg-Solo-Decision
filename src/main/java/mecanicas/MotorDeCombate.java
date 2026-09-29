package mecanicas;

import telas.MenuVisualizacao;

import classes.*;
import comandos.*;
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

import static mecanicas.GerenciadorDeAcoes.*;
import static mecanicas.GerenciadorDeAtaque.*;
import static mecanicas.GerenciadorDeEvolucao.*;
import static mecanicas.GerenciadorDeHabilidades.*;
import static mecanicas.GerenciadorDeItens.*;
import static mecanicas.GerenciadorDeTurnos.*;

public class MotorDeCombate {

        public static final String RESET = "\u001B[0m";
    public static final String CIANO = "\u001B[36m";
    public static final String VERDE = "\u001B[32m";
    public static final String AMARELO = "\u001B[33m";

    public static void IniciarCombate(FichaRpg ficha, List<Criatura> inimigos, boolean jogadorSurpreendeu) {
        IniciarCombate(ficha, inimigos, jogadorSurpreendeu, jogadorSurpreendeu ? 2 : 0);
    }

    public static void IniciarCombate(FichaRpg ficha, List<Criatura> inimigos, boolean jogadorSurpreendeu, int bonusIniciativaExtra) {
        Interface.MostrarMensagem("\n================ COMBATE ================");
        Interface.Pausa(2500);

        int bonusIniciativaJogador = bonusIniciativaExtra;
        Interface.pressionarParaTeste("Destreza");
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int iniciativaJogador = dadoJogador + ficha.getDestrezaTeste() + bonusIniciativaJogador;

        Interface.MostrarMensagem("-> Iniciativa [" + ficha.getNomePersonagem() + "]: " + dadoJogador + " (Dado) + " + ficha.getDestrezaTeste() + " (Destreza) + " + bonusIniciativaJogador + " (Bônus) = " + iniciativaJogador);
        Interface.Pausa(2500);

        List<int[]> ordem = new ArrayList<>();
        ordem.add(new int[]{iniciativaJogador, 0});

                companheiros.Companheiro comp = ficha.getCompanheiro();
        if (comp != null && comp.getFicha().getVidaPersonagem() > 0) {
            int dadoComp = MecanicasRpg.rolarDado(20);
            int iniciativaComp = dadoComp + comp.getFicha().getDestrezaTeste();
            Interface.MostrarMensagem("-> Iniciativa [" + comp.getNomeCompleto() + "]: " + dadoComp + " (Dado) + " + comp.getFicha().getDestrezaTeste() + " (Destreza) = " + iniciativaComp);
            Interface.Pausa(1500);
            ordem.add(new int[]{iniciativaComp, -1});
        }

        for (int i = 0; i < inimigos.size(); i++) {
            Criatura c = inimigos.get(i);
            int dadoInimigo = MecanicasRpg.rolarDado(20);
            int iniciativaInimigo = dadoInimigo + c.getIniciativa();
            Interface.MostrarMensagem("-> Iniciativa [" + rotuloCriatura(inimigos, c) + "]: " + dadoInimigo + " (Dado) + " + c.getIniciativa() + " (Iniciativa Base) = " + iniciativaInimigo);
            Interface.Pausa(1500);
            ordem.add(new int[]{iniciativaInimigo, i + 1});
        }

                ordem.sort((a, b) -> Integer.compare(b[0], a[0]));

        StringBuilder ordemTexto = new StringBuilder();
        for (int[] token : ordem) {
            String nomeOrdem;
            if (token[1] == 0) {
                nomeOrdem = ficha.getNomePersonagem();
            } else if (token[1] == -1) {
                nomeOrdem = comp != null ? comp.getNomeCompleto() : "Companheiro";
            } else {
                nomeOrdem = rotuloCriatura(inimigos, inimigos.get(token[1] - 1));
            }
            if (ordemTexto.length() > 0) ordemTexto.append(" > ");
            ordemTexto.append(nomeOrdem);
        }
        Interface.MostrarMensagem("\nOrdem de Iniciativa: " + ordemTexto);
        Interface.Pausa(2500);

        RodadasDeCombate(ficha, inimigos, ordem);
    }

    public static boolean tentarResgate(FichaRpg salvador, String nomeSalvador, FichaRpg vitima, String nomeVitima) {
        Interface.MostrarMensagem("  " + nomeSalvador + " tenta estabilizar " + nomeVitima + "...\n");
        Interface.Pausa(1000);

        int dado = MecanicasRpg.rolarDado(20);
        int total = dado + salvador.getIntelectoTeste();
        Interface.MostrarMensagem("-> Teste de Intelecto (Resgate): " + dado + " (Dado) + " + salvador.getIntelectoTeste() + " (Atributo) = " + total + " (Dificuldade: 14)");
        Interface.Pausa(1500);

        if (total > 14) {
            vitima.setVidaPersonagem(1);
            Interface.MostrarMensagem("\n  " + nomeSalvador + " consegue estabilizar " + nomeVitima + " a tempo! " + nomeVitima + " acorda com 1 de vida.");
            return true;
        } else {
            Interface.MostrarMensagem("\n  " + nomeSalvador + " falha em estabilizar " + nomeVitima + "...");
            return false;
        }
    }

    public static boolean podeAtivarPassiva(FichaRpg ficha, habilidades.Habilidade habilidade, boolean cascaGrossaAtiva) {
        if (!habilidade.isPassiva()) return false;
        switch (habilidade.getNome()) {
            case "Casca Grossa": return !cascaGrossaAtiva;
            case "Espada Afiada": return !ficha.isEspadaAfiadaAtiva();
            default: return true;
        }
    }

    public static void aplicaPassiva(FichaRpg ficha, habilidades.Habilidade habilidade, boolean[] cascaGrossaAtiva) {
        switch (habilidade.getNome()) {
            case "Casca Grossa": cascaGrossaAtiva[0] = true; break;
            case "Espada Afiada": ficha.setEspadaAfiadaAtiva(true); break;
        }
    }

    public static int EscolherArma(FichaRpg ficha) {
        List<Arma> armas = new ArrayList<>();
        List<Boolean> ehFlecha = new ArrayList<>();

        for (ItemRpg item : ficha.getInventario()) {
            if (item instanceof Arma) {
                Arma arma = (Arma) item;
                if (arma.getTipoArma().contains("LA")) {
                    if (temFlechas(ficha)) {
                        armas.add(arma);
                        ehFlecha.add(true);
                    }
                } else {
                    armas.add(arma);
                    ehFlecha.add(false);
                }
            }
        }

        String socoNome = "Soco";
        int socoDado = 4;
        int socoQtd = 1;
        if (ficha.getClasseDoPersonagem() != null && ficha.getClasseDoPersonagem().getAtaqueDesarmado() != null) {
            Arma soco = ficha.getClasseDoPersonagem().getAtaqueDesarmado();
            socoNome = soco.getNome();
            socoDado = soco.getDadoDanoArma();
            socoQtd = soco.getQuantidadeDanoArma();
        }

        Interface.cabecalhoMenu("ESCOLHA SUA ARMA");
        System.out.println("\n");
        for (int i = 0; i < armas.size(); i++) {
            Arma arma = armas.get(i);
            String extra = ehFlecha.get(i) ? " (Flechas: " + getQtdFlechas(ficha) + ")" : "";
            String atributoMostrado = arma.isAgil() ? "Ágil (Força/Destreza)" : arma.getAtributoAtaque();
            System.out.println("  " + (i + 1) + ". " + CIANO + arma.getNome() + RESET + " (" + arma.getQuantidadeDanoArma() + "d" + arma.getDadoDanoArma() + " - " + arma.getTipoArma() + " - " + atributoMostrado + ")" + extra);
        }
        System.out.println("  " + (armas.size() + 1) + ". " + CIANO + socoNome + RESET + " (" + socoQtd + "d" + socoDado + " - CaC - Força)");
        System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

        int escolha = Interface.lerInteiro();

        if (escolha == 0) return -1;
        if (escolha < 1 || escolha > armas.size() + 1) return -1;

        if (escolha <= armas.size()) {
            return ficha.getInventario().indexOf(armas.get(escolha - 1));
        } else {
            return -2;
        }
    }

    public static boolean temFlechas(FichaRpg ficha) {
        for (ItemRpg item : ficha.getInventario()) {
            if (item.getNome().equals("Flechas") && item.getQuantidade() > 0) {
                return true;
            }
        }
        return false;
    }

    public static int getQtdFlechas(FichaRpg ficha) {
        for (ItemRpg item : ficha.getInventario()) {
            if (item.getNome().equals("Flechas")) {
                return item.getQuantidade();
            }
        }
        return 0;
    }

    public static void consumirFlecha(FichaRpg ficha) {
        for (int i = 0; i < ficha.getInventario().size(); i++) {
            ItemRpg item = ficha.getInventario().get(i);
            if (item.getNome().equals("Flechas")) {
                ficha.consumirItem(item, 1);
                Interface.MostrarMensagem("-> Flecha utilizada! Restam " + item.getQuantidade() + " flechas.");
                if (item.getQuantidade() <= 0) {
                    ficha.getInventario().remove(i);
                    Interface.MostrarMensagem("-> Suas flechas acabaram!");
                }
                Interface.Pausa(1000);
                return;
            }
        }
    }

    public static boolean tentarReviver(FichaRpg ficha) {
        if (ficha.getVidaPersonagem() > 0) return true;
        if (ficha.isCuraTotalUsada() || ficha.getManaPersonagem() < 10) return false;
        if (!temHabilidade(ficha, "Cura Total")) return false;

        System.out.println("\nVocê foi derrubado! Deseja usar Cura Total (10 de mana) para reviver com a vida cheia?");
        System.out.println("1. Sim");
        System.out.println("2. Não");
        int escolha = Interface.lerInteiro();

        if (escolha != 1) return false;

        ficha.setManaPersonagem(ficha.getManaPersonagem() - 10);
        ficha.setVidaPersonagem(ficha.getVidaMaxima());
        ficha.setCuraTotalUsada(true);
        Interface.MostrarMensagem("\nCura Total! Você renasce com a vida cheia!");
        Interface.Pausa(2500);
        return true;
    }

    public static boolean temHabilidade(FichaRpg ficha, String nome) {
        for (habilidades.Habilidade hab : ficha.getHabilidades()) {
            if (hab.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }
}
