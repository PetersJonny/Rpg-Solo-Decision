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

public class GerenciadorDeAtaque {

    public static boolean executarAtaqueComArma(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, int armaIndex) {
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return false;
        Criatura inimigo = inimigos.get(alvoIndex);

                if (ficha.isDefesaAbsolutaAtiva()) {
            ficha.setDefesaAbsolutaAtiva(false);
            Interface.MostrarMensagem("(Sua Defesa Absoluta se dissipa ao atacar!)");
            Interface.Pausa(1500);
        }

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

    private static void curarCurandeiroNoGolpe(FichaRpg ficha, int danoCausado) {
        if (!(ficha.getClasseDoPersonagem() instanceof classes.Healer)) return;
        if (!temHabilidade(ficha, "Curandeiro Combatente")) return;
        int cura = danoCausado / 2;
        if (cura <= 0) return;
        ficha.setVidaPersonagem(Math.min(ficha.getVidaMaxima(), ficha.getVidaPersonagem() + cura));
        Interface.MostrarMensagem("(Curandeiro Combatente! O golpe acerta e você se cura " + cura + " de vida.)");
        Interface.Pausa(1500);
    }

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
        String nomeArmaUsada = null;
        boolean golpeCaC = false, golpeLA = false;
        int atributoBonus;
        String nomeAtributo;

        if (armaIndex == -2) {
            String socoNome = "Soco";
            int socoDado = 4;
            int socoQtd = 1;
            golpeCaC = true;
            if (ficha.getClasseDoPersonagem() != null && ficha.getClasseDoPersonagem().getAtaqueDesarmado() != null) {
                Arma soco = ficha.getClasseDoPersonagem().getAtaqueDesarmado();
                socoNome = soco.getNome();
                socoDado = soco.getDadoDanoArma();
                socoQtd = soco.getQuantidadeDanoArma();
            }
            int socoOriginal = socoDado;
            socoDado = ficha.upgradeDadoFruta(socoDado);
            if (socoDado != socoOriginal) {
                Interface.MostrarMensagem("(Fruta do Diabo! O dado de dano sobe: 1d" + socoOriginal + " -> 1d" + socoDado + ")");
                Interface.Pausa(1500);
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
            nomeArmaUsada = armaEscolhida.getNome();
            golpeCaC = armaEscolhida.getTipoArma().contains("CaC");
            golpeLA = armaEscolhida.getTipoArma().contains("LA");
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

            int qtyDados = armaEscolhida.getQuantidadeDanoArma();
            int dadoOriginal = armaEscolhida.getDadoDanoArma();
            int dadoDano = ficha.upgradeDadoFruta(dadoOriginal);
            if (dadoDano != dadoOriginal) {
                Interface.MostrarMensagem("(Fruta do Diabo! O dado de dano sobe: 1d" + dadoOriginal + " -> 1d" + dadoDano + ")");
                Interface.Pausa(1500);
            }
            boolean furiaSombria = ficha.getRaca() != null && ficha.getRaca().temBonusDanoVidaBaixa()
                    && ficha.getVidaPersonagem() <= ficha.getVidaMaxima() * 0.30;
            int dadosFuria = 0;
            int ladoFuria = 0;
            if (furiaSombria) {
                dadosFuria = dadosExtrasFuria(dadoDano, qtyDados);
                if (dadosFuria > 0) {
                    ladoFuria = 4;                 } else {
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
                    dadosFuria *= (critico ? 2 : 1);                 }
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
            if (ficha.temItem("Túnica de Aventureiro") && golpeCaC) {
                dano += 1;
                Interface.MostrarMensagem("(Túnica de Aventureiro! +1 de dano corpo a corpo)");
                Interface.Pausa(1000);
            }
            if (ficha.temItem("Manto do Atirador") && golpeLA) {
                dano += 1;
                Interface.MostrarMensagem("(Manto do Atirador! +1 de dano à distância)");
                Interface.Pausa(1000);
            }
            if (ficha.isEspadaAfiadaAtiva() && armaIndex >= 0) {
                int bonusAfiada = MecanicasRpg.rolarDado(8) + MecanicasRpg.rolarDado(8);
                dano += bonusAfiada;
                Interface.MostrarMensagem("(Espada Afiada! +" + bonusAfiada + " de dano)");
                Interface.Pausa(1500);
            }
            aplicarDanoCriatura(inimigo, dano);
            if ("Cutelo".equals(nomeArmaUsada) && inimigo.getVida() > 0 && !inimigo.isSangrando()) {
                inimigo.setSangrando(true);
                Interface.MostrarMensagem("(O Cutelo! O corte de " + inimigo.getNome() + " não para de sangrar: 1d6 de dano por rodada)");
                Interface.Pausa(1500);
            }
            Interface.MostrarMensagem(rotuloCriatura(inimigos, inimigo) + " agora tem " + Math.max(0, inimigo.getVida()) + " de vida.");
            Interface.Pausa(2000);
        }

        GerenciadorDeHabilidades.aplicarVenenoCuraParaMorte(ficha, inimigos, alvoIndex);
        return dano;
    }

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

    static int dadosExtrasFuria(int dadoDano, int qtyDados) {
        return dadoDano >= 12 ? qtyDados : 0;
    }
}
