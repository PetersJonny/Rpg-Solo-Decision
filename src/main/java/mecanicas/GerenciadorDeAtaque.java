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

import static mecanicas.GerenciadorDeAcoes.*;
import static mecanicas.GerenciadorDeEvolucao.*;
import static mecanicas.GerenciadorDeHabilidades.*;
import static mecanicas.GerenciadorDeItens.*;
import static mecanicas.GerenciadorDeTurnos.*;
import static mecanicas.MotorDeCombate.*;

/** Execução de ataques com arma e cálculo dos dados de dano. */
public class GerenciadorDeAtaque {

// ==================== EXECUTAR ATAQUE ====================


    public static boolean executarAtaqueComArma(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, int armaIndex) {
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return false;
        Criatura inimigo = inimigos.get(alvoIndex);

        // Defesa Absoluta do Guerreiro: o bônus some ao realizar um ataque
        if (ficha.isDefesaAbsolutaAtiva()) {
            ficha.setDefesaAbsolutaAtiva(false);
            Interface.MostrarMensagem("(Sua Defesa Absoluta se dissipa ao atacar!)");
            Interface.Pausa(1500);
        }

        // Curandeiro Combatente (Healer): cada 1 de mana compra 1 ataque extra (máx = nível)
        int ataquesExtras = comprarAtaquesExtrasCurandeiro(ficha, inimigo);
        if (ataquesExtras > 0) {
            Interface.MostrarMensagem("\nCurandeiro Combatente! Você gasta " + ataquesExtras + " de mana e executa " + ataquesExtras + " ataque(s) extra(s).");
            Interface.Pausa(1500);
        }

        boolean acertou = false;
        for (int ataque = 0; ataque <= ataquesExtras; ataque++) {
            if (inimigo.getVida() <= 0) break;
            int danoCausado = ataqueComArmaUnico(ficha, inimigos, alvoIndex, armaIndex, ataque > 0);
            if (danoCausado > 0) {
                acertou = true;
                curarCurandeiroNoGolpe(ficha, danoCausado);
            }
        }

        // Espada do Minotauro (Guerreiro): 30% de chance de atacar de novo após o golpe
        if (armaIndex >= 0 && armaIndex < ficha.getInventario().size()
                && ficha.getInventario().get(armaIndex) instanceof Arma
                && ((Arma) ficha.getInventario().get(armaIndex)).getNome().equals("Espada do Minotauro")
                && inimigo.getVida() > 0
                && MecanicasRpg.rolarDado(100) <= 30) {
            Interface.MostrarMensagem("\nA Espada do Minotauro volta com fúria total! Você ataca de novo!");
            Interface.Pausa(1500);
            int danoCausado = ataqueComArmaUnico(ficha, inimigos, alvoIndex, armaIndex, true);
            if (danoCausado > 0) {
                acertou = true;
                curarCurandeiroNoGolpe(ficha, danoCausado);
            }
        }

        return acertou;
    }

// Curandeiro Combatente (Healer): oferece comprar ataques extras gastando mana
    // (1 de mana por ataque, máximo = nível). O acerto de cada golpe cura metade do dano.

    private static int comprarAtaquesExtrasCurandeiro(FichaRpg ficha, Criatura inimigo) {
        if (!(ficha.getClasseDoPersonagem() instanceof classes.Healer)) return 0;
        if (!temHabilidade(ficha, "Curandeiro Combatente")) return 0;
        if (ficha.getManaPersonagem() < 1 || inimigo.getVida() <= 0) return 0;
        int maxExtra = Math.min(ficha.getNivel(), ficha.getManaPersonagem());
        System.out.println("\n  Curandeiro Combatente: cada 1 de mana compra 1 ataque extra (todo acerto cura metade do dano).");
        System.out.println("  Ataques extras possíveis: 0 a " + maxExtra + ".");
        System.out.println("  Escolha uma opção:");
        int extra = Interface.lerOpcao(0, maxExtra);
        ficha.setManaPersonagem(ficha.getManaPersonagem() - extra);
        return extra;
    }

// Curandeiro Combatente (Healer): cada ataque que acerta cura metade do dano causado

    private static void curarCurandeiroNoGolpe(FichaRpg ficha, int danoCausado) {
        if (!(ficha.getClasseDoPersonagem() instanceof classes.Healer)) return;
        if (!temHabilidade(ficha, "Curandeiro Combatente")) return;
        int cura = danoCausado / 2;
        if (cura <= 0) return;
        ficha.setVidaPersonagem(Math.min(ficha.getVidaMaxima(), ficha.getVidaPersonagem() + cura));
        Interface.MostrarMensagem("(Curandeiro Combatente! O golpe acerta e você se cura " + cura + " de vida.)");
        Interface.Pausa(1500);
    }

// Executa UM ataque com arma (ou soco) e devolve o dano causado (0 se errou/inválido)

    private static int ataqueComArmaUnico(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, int armaIndex, boolean golpeExtra) {
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return 0;
        Criatura inimigo = inimigos.get(alvoIndex);

        if (golpeExtra) {
            Interface.MostrarMensagem("\nVocê encadeia um novo golpe contra " + rotuloCriatura(inimigos, inimigo) + "...");
            Interface.Pausa(1200);
        } else {
            Interface.MostrarMensagem("\nVocê prepara seu ataque contra " + rotuloCriatura(inimigos, inimigo) + "...");
            Interface.Pausa(1500);
        }

        int dadoAtaque, totalAtaque, dano = 0;
        int atributoBonus;
        String nomeAtributo;

        if (armaIndex == -2) {
            String socoNome = "Soco";
            int socoDado = 4;
            int socoQtd = 1;
            if (ficha.getClasseDoPersonagem() != null && ficha.getClasseDoPersonagem().getAtaqueDesarmado() != null) {
                Arma soco = ficha.getClasseDoPersonagem().getAtaqueDesarmado();
                socoNome = soco.getNome();
                socoDado = soco.getDadoDanoArma();
                socoQtd = soco.getQuantidadeDanoArma();
            }
            atributoBonus = ficha.getForca();
            nomeAtributo = "Força";

            Interface.pressionarParaRolar();
            dadoAtaque = MecanicasRpg.rolarDado(20);
            totalAtaque = dadoAtaque + atributoBonus;
            boolean critico = dadoAtaque == 20;
            Interface.MostrarMensagem("-> Ataque [" + socoNome + "]: " + dadoAtaque + " (Dado) + " + atributoBonus + " (" + nomeAtributo + ") = " + totalAtaque + (critico ? " [CRÍTICO!]" : ""));
            if (critico) {
                Interface.MostrarMensagem("Golpe crítico! O dano de dados será dobrado!");
            }
            Interface.Pausa(2000);

            if (critico || totalAtaque >= inimigo.getDefesa()) {
                Interface.MostrarMensagem("-> Acertou! (defesa do alvo: " + inimigo.getDefesa() + ")" + (critico ? " CRÍTICO sempre acerta." : ""));
                Interface.Pausa(1500);

                int dadosTotais = socoQtd * (critico ? 2 : 1);
                boolean semiDeusBonus = ficha.isSemiDeusAtivo();
                if (semiDeusBonus) {
                    dadosTotais += 4;
                    Interface.MostrarMensagem("(Semi Deus! +4 dados de dano)");
                    Interface.Pausa(1000);
                }
                StringBuilder roladas = new StringBuilder();
                Interface.pressionarParaRolar();
                for (int i = 0; i < dadosTotais; i++) {
                    int dado = MecanicasRpg.rolarDado(socoDado);
                    dano += dado;
                    if (roladas.length() > 0) roladas.append(" + ");
                    roladas.append(dado);
                }
                dano += atributoBonus;
                Interface.MostrarMensagem("-> Dados Rolados: " + roladas + " = " + dano + " (Dano: " + dadosTotais + "d" + socoDado + " + " + nomeAtributo + ": " + atributoBonus + ")");
                Interface.Pausa(2000);
            } else {
                Interface.MostrarMensagem("-> Errou! (defesa do alvo: " + inimigo.getDefesa() + ")");
                Interface.Pausa(1500);
            }
        } else if (armaIndex >= 0 && armaIndex < ficha.getInventario().size()) {
            Arma armaEscolhida = (Arma) ficha.getInventario().get(armaIndex);
            String atributo = armaEscolhida.getAtributoAtaque();
            if (armaEscolhida.isAgil()) {
                if (ficha.getForca() >= ficha.getDestreza()) {
                    atributoBonus = ficha.getForca();
                    nomeAtributo = "Força";
                } else {
                    atributoBonus = ficha.getDestreza();
                    nomeAtributo = "Destreza";
                }
            } else {
                atributoBonus = atributo.equals("Destreza") ? ficha.getDestreza() : ficha.getForca();
                nomeAtributo = atributo;
            }

            // Fúria Sombria (Meio-Orque): com 30% ou menos de vida, o dado de dano da arma
            // sobe um degrau (1d2→1d3→1d4→1d6→1d8→1d10→1d12); se a arma já usa 1d12
            // (o maior dado de dano), a passiva acrescenta +1d4 extra por dado rolado.
            int qtyDados = armaEscolhida.getQuantidadeDanoArma();
            int dadoDano = armaEscolhida.getDadoDanoArma();
            boolean furiaSombria = ficha.getRaca() != null && ficha.getRaca().temBonusDanoVidaBaixa()
                    && ficha.getVidaPersonagem() <= ficha.getVidaMaxima() * 0.30;
            int dadosFuria = 0;
            int ladoFuria = 0;
            if (furiaSombria) {
                dadosFuria = dadosExtrasFuria(dadoDano, qtyDados);
                if (dadosFuria > 0) {
                    ladoFuria = 4; // 1d12 + 1d4 por dado
                } else {
                    dadoDano = proximoDadoDeDano(dadoDano);
                }
            }

            Interface.pressionarParaRolar();
            dadoAtaque = MecanicasRpg.rolarDado(20);
            totalAtaque = dadoAtaque + atributoBonus;
            boolean critico = dadoAtaque == 20;
            Interface.MostrarMensagem("-> Ataque [" + armaEscolhida.getNome() + "]: " + dadoAtaque + " (Dado) + " + atributoBonus + " (" + nomeAtributo + ") = " + totalAtaque + (critico ? " [CRÍTICO!]" : ""));
            if (critico) {
                Interface.MostrarMensagem("Golpe crítico! O dano de dados será dobrado!");
            }
            Interface.Pausa(2000);

            if (critico || totalAtaque >= inimigo.getDefesa()) {
                Interface.MostrarMensagem("-> Acertou! (defesa do alvo: " + inimigo.getDefesa() + ")" + (critico ? " CRÍTICO sempre acerta." : ""));
                Interface.Pausa(1500);

                int dadosTotais = qtyDados * (critico ? 2 : 1);
                boolean semiDeusBonus = ficha.isSemiDeusAtivo() && armaEscolhida.getTipoArma().contains("CaC");
                if (semiDeusBonus) {
                    dadosTotais += 4;
                    Interface.MostrarMensagem("(Semi Deus! +4 dados de dano)");
                    Interface.Pausa(1000);
                }
                if (dadosFuria > 0) {
                    dadosFuria *= (critico ? 2 : 1); // +1d4 extra também dobra no crítico
                }
                StringBuilder roladas = new StringBuilder();
                Interface.pressionarParaRolar();
                for (int i = 0; i < dadosTotais; i++) {
                    int dado = MecanicasRpg.rolarDado(dadoDano);
                    dano += dado;
                    if (roladas.length() > 0) roladas.append(" + ");
                    roladas.append(dado);
                }
                for (int i = 0; i < dadosFuria; i++) {
                    int dado = MecanicasRpg.rolarDado(ladoFuria);
                    dano += dado;
                    roladas.append(" + ");
                    roladas.append(dado);
                }
                dano += atributoBonus;
                String resumoDados = dadosTotais + "d" + dadoDano
                        + (dadosFuria > 0 ? " + " + dadosFuria + "d" + ladoFuria : "");
                if (furiaSombria) {
                    Interface.MostrarMensagem("(Fúria Sombria! Com a vida baixa, " + (dadosFuria > 0
                            ? "sua arma 1d" + armaEscolhida.getDadoDanoArma() + " recebe +" + dadosFuria + "d" + ladoFuria + " extras"
                            : "o dado da arma sobe um degrau para " + dadosTotais + "d" + dadoDano) + ")");
                }
                Interface.MostrarMensagem("-> Dados Rolados: " + roladas + " = " + dano + " (Dano: " + resumoDados + " + " + nomeAtributo + ": " + atributoBonus + ")");
                Interface.Pausa(2000);

                // Espada Majestral: banhada em ouro e magia, causa +1d4 de dano de luz
                // e o dobro do dano total contra mortos-vivos
                if (armaEscolhida.getNome().equals("Espada Majestral")) {
                    int dadosLuz = critico ? 2 : 1;
                    int luz = 0;
                    for (int i = 0; i < dadosLuz; i++) {
                        luz += MecanicasRpg.rolarDado(4);
                    }
                    dano += luz;
                    Interface.MostrarMensagem("(Espada Majestral! +" + luz + " de dano de luz" + (dadosLuz > 1 ? " (crítico)" : "") + ")");
                    Interface.Pausa(1500);
                    if (inimigo.isMortoVivo()) {
                        dano *= 2;
                        Interface.MostrarMensagem("(Espada Majestral! DANO DOBRADO contra " + inimigo.getNome() + ", um morto-vivo)");
                        Interface.Pausa(1500);
                    }
                }
            } else {
                Interface.MostrarMensagem("-> Errou! (defesa do alvo: " + inimigo.getDefesa() + ")");
                Interface.Pausa(1500);
            }

            if (armaEscolhida.getTipoArma().contains("LA")) {
                consumirFlecha(ficha);
            }
        } else {
            return 0;
        }

        if (dano > 0) {
            if (ficha.isEspadaAfiadaAtiva() && armaIndex >= 0) {
                int bonusAfiada = MecanicasRpg.rolarDado(8) + MecanicasRpg.rolarDado(8);
                dano += bonusAfiada;
                Interface.MostrarMensagem("(Espada Afiada! +" + bonusAfiada + " de dano)");
                Interface.Pausa(1500);
            }
            aplicarDanoCriatura(inimigo, dano);
            Interface.MostrarMensagem(rotuloCriatura(inimigos, inimigo) + " agora tem " + Math.max(0, inimigo.getVida()) + " de vida.");
            Interface.Pausa(2000);
        }

        GerenciadorDeHabilidades.aplicarVenenoCuraParaMorte(ficha, inimigos, alvoIndex);
        return dano;
    }

// Próximo dado de dano no padrão (1 degrau acima): 1d2→1d3→1d4→1d6→1d8→1d10→
// 1d12 (1d12 é o maior dado de dano; 1d20 é só para testes). Usado pela Fúria
// Sombria (Meio-Orque) com 30% ou menos de vida.

    static int proximoDadoDeDano(int dado) {
        switch (dado) {
            case 2: return 3;
            case 3: return 4;
            case 4: return 6;
            case 6: return 8;
            case 8: return 10;
            case 10: return 12;
            case 12: return 12;
            default: return dado;
        }
    }

// Fúria Sombria com a arma já no dado máximo de dano (1d12): em vez de subir
    // de degrau, a passiva acrescenta +1d4 por cada dado 1d12 da arma (ex.: 1d12
    // vira 1d12 + 1d4). Retorna quantos dados extra essa arma receberia.

    static int dadosExtrasFuria(int dadoDano, int qtyDados) {
        return dadoDano >= 12 ? qtyDados : 0;
    }
}
