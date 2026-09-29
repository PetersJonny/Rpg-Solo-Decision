package mecanicas;

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
import telas.MenuVisualizacao;

import static mecanicas.GerenciadorDeAtaque.*;
import static mecanicas.GerenciadorDeEvolucao.*;
import static mecanicas.GerenciadorDeHabilidades.*;
import static mecanicas.GerenciadorDeItens.*;
import static mecanicas.GerenciadorDeTurnos.*;
import static mecanicas.MotorDeCombate.*;

/** Decisões e menus do turno do jogador durante o combate. */
public class GerenciadorDeAcoes {

// ==================== VEZ DO JOGADOR ====================

    // Fase de declaração: o jogador escolhe a ação da rodada SEM executar ainda.
    // A execução acontece quando chega a vez dele na ordem de iniciativa.

    public static ComandoCombate declararAcao(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, boolean semHabilidades) {
        while (true) {
            Interface.barraDivisoria();
            System.out.println("\n  O que deseja fazer?\n");
            System.out.println("  1. Lutar");
            System.out.println("  2. Abrir Mochila");
            if (temBossSemFuga(inimigos)) {
                System.out.println(AMARELO + "  3. Tentar Fugir (INDISPONÍVEL — a porta se fechou!)" + RESET);
            } else {
                System.out.println("  3. Tentar Fugir");
            }
            System.out.println("  4. Ver Ficha\n");
            System.out.println("  Escolha uma opção:");
            int escolha = Interface.lerInteiro();

            if (escolha == 4) {
                MenuVisualizacao.MostrarFicha(ficha);
                Interface.Pausa(1500);
                continue;
            }

            if (escolha == 1) {
                ComandoCombate resultado = MenuLutarComEscolha(ficha, inimigos, cascaGrossaAtiva, semHabilidades);
                if (resultado == null) continue;
                return resultado;
            } else if (escolha == 2) {
                int item = GerenciadorDeItens.escolherItemParaUsar(ficha);
                if (item == -1) continue;
                if (item == -2) return new ComandoAguardar();
                return new ComandoUsarItem(item);
            } else if (escolha == 3) {
                if (temBossSemFuga(inimigos)) {
                    Interface.ExibirErro("A porta se fechou! Não há como fugir deste combate!");
                    Interface.Pausa(1500);
                    continue;
                }
                return new ComandoFuga();
            } else {
                Interface.ExibirErro("Escolha inválida!");
                Interface.Pausa(1500);
            }
        }
    }

// Apresenta as opções de habilidade ao atingir um novo nível com escolha

    // Deus (Guerreiro lvl 10): ativa permanentemente a forma de Semi Deus e concede Cura Incessante

    // Conhecimento Absoluto (Healer lvl 10): +2 em todos os atributos

    // Arma Mental transforma o Bisturi de 1d4 para 3d8 ao ser aprendida


    public static void executarAcaoJogador(FichaRpg ficha, List<Criatura> inimigos, int tipoAcao, int alvoIndex, int armaIndex, int habIndex) {
        boolean sucesso;
        if (tipoAcao == 1) {
            sucesso = executarAtaqueComArma(ficha, inimigos, alvoIndex, armaIndex);
        } else {
            sucesso = GerenciadorDeHabilidades.executarHabilidadeEscolhida(ficha, inimigos, alvoIndex, habIndex);
        }

        if (!sucesso) {
            tentarConhecimentoAvancado(ficha, inimigos, tipoAcao, alvoIndex, armaIndex, habIndex);
        }
    }

// ==================== CONHECIMENTO AVANÇADO ====================


    public static void tentarConhecimentoAvancado(FichaRpg ficha, List<Criatura> inimigos, int tipoAcao, int alvoIndex, int armaIndex, int habIndex) {
        for (habilidades.Habilidade hab : ficha.getHabilidades()) {
            if (hab.getNome().equals("Conhecimento Avançado") && ficha.getManaPersonagem() >= GerenciadorDeHabilidades.custoEfetivoMagia(ficha, hab)) {
                System.out.println("\nDeseja usar Conhecimento Avançado para rerrolar? (Custo: " + GerenciadorDeHabilidades.custoEfetivoMagia(ficha, hab) + " Mana)");
                System.out.println("1. Sim");
                System.out.println("2. Não");
                int escolha = Interface.lerInteiro();


                if (escolha == 1) {
                    ficha.setManaPersonagem(ficha.getManaPersonagem() - GerenciadorDeHabilidades.custoEfetivoMagia(ficha, hab));
                    Interface.MostrarMensagem("\nVocê foca seus conhecimentos e tenta novamente!");
                    Interface.Pausa(1500);

                    boolean sucessoReroll;
                    if (tipoAcao == 1) {
                        sucessoReroll = executarAtaqueComArma(ficha, inimigos, alvoIndex, armaIndex);
                    } else {
                        sucessoReroll = GerenciadorDeHabilidades.executarHabilidadeEscolhida(ficha, inimigos, alvoIndex, habIndex);
                    }

                    if (!sucessoReroll) {
                        Interface.MostrarMensagem("Mesmo com seu conhecimento, a ação falhou.");
                        Interface.Pausa(1500);
                    }
                }
                return;
            }
        }
    }

// ==================== MENU LUTAR ====================


    public static ComandoCombate MenuLutarComEscolha(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, boolean semHabilidades) {
        Interface.cabecalhoMenu("COMO LUTAR?");
        System.out.println("\n");

        if (semHabilidades) {
            System.out.println("Você ainda está se recuperando do Estrondo e NÃO pode usar habilidades nesta rodada!");
            System.out.println("");
        }

        List<String> descricoes = new ArrayList<>();
        List<int[]> acoes = new ArrayList<>();

        descricoes.add("Atacar com Arma");
        acoes.add(new int[]{1, -1, -1, -1});

        if (!semHabilidades) {
            descricoes.add("Usar Habilidade");
            acoes.add(new int[]{2, -1, -1, -1});

            for (int i = 0; i < ficha.getHabilidades().size(); i++) {
                habilidades.Habilidade hab = ficha.getHabilidades().get(i);
                if (hab.isPassiva()
                        && !hab.getNome().equals("Defesa Absoluta")
                        && !hab.getNome().equals("Arma Mental")
                        && !hab.getNome().equals("Deus")
                        && !hab.getNome().equals("Conhecimento Absoluto")
                        && podeAtivarPassiva(ficha, hab, cascaGrossaAtiva[0])
                        && ficha.getManaPersonagem() >= hab.getCustoMana()) {
                    descricoes.add(hab.getNome() + " (Custo: " + hab.getCustoMana() + " Mana) - ainda pode atacar após usar");
                    acoes.add(new int[]{3, i, -1, -1});
                }
            }

            if (temHabilidade(ficha, "Magia Proibida") && !ficha.isMagiaProibidaUsada() && ficha.getManaPersonagem() >= 5) {
                descricoes.add("Magia Proibida (Custo: 5 Mana) - não gasta sua ação");
                acoes.add(new int[]{4, -1, -1, -1});
            }

            if (ficha.temItem("Coroa do Rei") && ficha.isReiDasCriaturas() && ficha.getManaPersonagem() >= 3) {
                descricoes.add("Rei das Criaturas (Custo: 3 Mana) - comande uma criatura sem gastar sua ação");
                acoes.add(new int[]{5, -1, -1, -1});
            }
        }

        for (int i = 0; i < descricoes.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + descricoes.get(i));
        }
        System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

        int escolha = Interface.lerInteiro();

        if (escolha == 0) return null;
        if (escolha < 1 || escolha > acoes.size()) {
            Interface.ExibirErro("Escolha inválida!");
            Interface.Pausa(1500);
            return null;
        }

        int[] acao = acoes.get(escolha - 1);

        if (acao[0] == 1) {
            int armaIdx = EscolherArma(ficha);
            if (armaIdx == -1) return null;
            int alvo = escolherAlvo(inimigos);
            if (alvo == -1) return null;
            return new ComandoAtacarComArma(alvo, armaIdx);
        }

        if (acao[0] == 2) {
            int habIdx = GerenciadorDeHabilidades.EscolherHabilidadeAtiva(ficha);
            if (habIdx == -1) return null;
            habilidades.Habilidade habEscolhida = ficha.getHabilidades().get(habIdx);
            int alvo = -1;
            if (habEscolhida.precisaDeAlvo()) {
                alvo = escolherAlvo(inimigos);
                if (alvo == -1) return null;
            }
            return new ComandoUsarHabilidade(alvo, habIdx);
        }

        if (acao[0] == 3) {
            habilidades.Habilidade hab = ficha.getHabilidades().get(acao[1]);
            ficha.setManaPersonagem(ficha.getManaPersonagem() - GerenciadorDeHabilidades.custoEfetivoMagia(ficha, hab));
            aplicaPassiva(ficha, hab, cascaGrossaAtiva);
            Interface.MostrarMensagem("\nVocê ativa " + hab.getNome() + "!");
            Interface.MostrarMensagem(hab.getDescricao());
            Interface.Pausa(2000);
            return MenuLutarComEscolha(ficha, inimigos, cascaGrossaAtiva, semHabilidades);
        }

        if (acao[0] == 4) {
            ficha.setManaPersonagem(ficha.getManaPersonagem() - 5);
            ficha.setMagiaProibidaUsada(true);
            ficha.setMagiaProibidaAtiva(true);
            Interface.MostrarMensagem("\nVocê invoca a Magia Proibida! Todos os ataques dos inimigos desta rodada falharão.");
            Interface.Pausa(2000);
            return MenuLutarComEscolha(ficha, inimigos, cascaGrossaAtiva, semHabilidades);
        }

        if (acao[0] == 5) {
            GerenciadorDeHabilidades.usarReiDasCriaturas(ficha, inimigos);
            return MenuLutarComEscolha(ficha, inimigos, cascaGrossaAtiva, semHabilidades);
        }

        return null;
    }

// Escolhe o alvo entre os inimigos vivos

    public static int escolherAlvo(List<Criatura> inimigos) {
        List<Criatura> vivos = new ArrayList<>();
        for (Criatura c : inimigos) {
            if (c.getVida() > 0 && !c.isFugiu()) {
                vivos.add(c);
            }
        }

        if (vivos.isEmpty()) return -1;
        if (vivos.size() == 1) return inimigos.indexOf(vivos.get(0));

        System.out.println("\n  " + CIANO + "[ ESCOLHA SEU ALVO ]" + RESET + "\n");
        for (int i = 0; i < vivos.size(); i++) {
            Criatura c = vivos.get(i);
            System.out.println("  " + (i + 1) + ". " + rotuloCriatura(inimigos, c) + " (Vida: " + c.getVida() + ")");
        }
        System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

        int escolha = Interface.lerInteiro();


        if (escolha < 1 || escolha > vivos.size()) return -1;

        return inimigos.indexOf(vivos.get(escolha - 1));
    }
}
