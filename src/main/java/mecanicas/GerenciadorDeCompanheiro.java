package mecanicas;

import classes.*;
import criaturas.Criatura;
import fichas.FichaRpg;
import itens.Arma;
import java.util.List;
import mecanicas.MecanicasRpg;
import telas.Interface;

import static mecanicas.GerenciadorDeTurnos.*;

public class GerenciadorDeCompanheiro {

    public static void acaoDoCompanheiro(FichaRpg ficha, companheiros.Companheiro comp, List<Criatura> inimigos) {
        FichaRpg cf = comp.getFicha();
        if (cf.getVidaPersonagem() <= 0) return;

        Criatura alvo = escolherAlvoAleatorio(inimigos);
        if (alvo == null) return;

        Interface.MostrarMensagem("\n" + comp.getNomeCompleto() + " age!");
        Interface.Pausa(1200);

                if (cf.getClasseDoPersonagem() instanceof classes.Healer
                && cf.temItem("Kit Médico")
                && cf.getManaPersonagem() >= 1) {
            boolean jogadorFerido = ficha.getVidaPersonagem() <= (int) (ficha.getVidaMaxima() * 0.6);
            boolean siFerido = cf.getVidaPersonagem() <= (int) (cf.getVidaMaxima() * 0.6);

            if (jogadorFerido) {
                cf.setManaPersonagem(cf.getManaPersonagem() - 1);
                int cura = MecanicasRpg.rolarDado(4) + MecanicasRpg.rolarDado(4);
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                Interface.MostrarMensagem(comp.getNome() + " grita: \"Aguenta! Vou te curar!\" e usa a Medicina Reforçada!");
                Interface.MostrarMensagem("Você recuperou " + cura + " de vida! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                Interface.Pausa(1800);
                return;
            }

            if (siFerido) {
                cf.setManaPersonagem(cf.getManaPersonagem() - 1);
                int cura = MecanicasRpg.rolarDado(4);
                cf.setVidaPersonagem(Math.min(cf.getVidaPersonagem() + cura, cf.getVidaMaxima()));
                Interface.MostrarMensagem(comp.getNome() + " usa o Kit Médico em si mesmo(a) e restaura " + cura + " de vida.");
                Interface.Pausa(1500);
                return;
            }
        }

                if (cf.getClasseDoPersonagem() instanceof classes.Mago) {
            habilidades.Magia bola = null;
            habilidades.Magia pequena = null;
            for (habilidades.Habilidade hab : cf.getHabilidades()) {
                if (hab instanceof habilidades.Magia) {
                    habilidades.Magia mag = (habilidades.Magia) hab;
                    if (mag.getCustoMana() > 0 && bola == null) bola = mag;
                    if (mag.getCustoMana() == 0 && pequena == null) pequena = mag;
                }
            }

            int aleatorio = MecanicasRpg.rolarDado(100);
            habilidades.Magia magiaUsar = null;
            if (bola != null && cf.getManaPersonagem() >= GerenciadorDeHabilidades.custoEfetivoMagia(cf, bola) && aleatorio <= 60) {
                magiaUsar = bola;
            } else if (pequena != null && aleatorio <= 30) {
                magiaUsar = pequena;
            }

            if (magiaUsar != null) {
                cf.setManaPersonagem(cf.getManaPersonagem() - GerenciadorDeHabilidades.custoEfetivoMagia(cf, magiaUsar));
                Interface.MostrarMensagem(comp.getNomeCompleto() + " conjura " + magiaUsar.getNome() + "!");
                Interface.Pausa(1500);
                int dano = 0;
                for (int i = 0; i < magiaUsar.getQuantidadeDano(); i++) {
                    dano += MecanicasRpg.rolarDado(magiaUsar.getDadoDano());
                }
                Interface.MostrarMensagem("-> Dano: " + dano + "!");
                Interface.Pausa(1200);
                aplicarDanoCriatura(alvo, dano);
                Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
                Interface.Pausa(1200);
                return;
            }
        }

                Interface.MostrarMensagem(comp.getNome() + " avança para atacar!");
        Interface.Pausa(1200);
        atacarComArmaDoCompanheiro(cf, alvo, inimigos);
    }

    public static void atacarComArmaDoCompanheiro(FichaRpg cf, Criatura alvo, List<Criatura> inimigos) {
        Arma arma = cf.getArmaEquipada();
        if (arma == null && cf.getClasseDoPersonagem() != null) {
            arma = cf.getClasseDoPersonagem().getAtaqueDesarmado();
        }

        int atributoBonus;
        String nomeAtributo;
        if (arma != null && arma.isAgil()) {
            if (cf.getForca() >= cf.getDestreza()) {
                atributoBonus = cf.getForca();
                nomeAtributo = "Força";
            } else {
                atributoBonus = cf.getDestreza();
                nomeAtributo = "Destreza";
            }
        } else if (arma != null && arma.getAtributoAtaque().equals("Destreza")) {
            atributoBonus = cf.getDestreza();
            nomeAtributo = "Destreza";
        } else {
            atributoBonus = cf.getForca();
            nomeAtributo = "Força";
        }

        int dadoAtaque = MecanicasRpg.rolarDado(20);
        int totalAtaque = dadoAtaque + atributoBonus;
        boolean critico = dadoAtaque == 20;
        Interface.MostrarMensagem("-> Ataque [" + (arma != null ? arma.getNome() : "Soco") + "]: " + dadoAtaque + " (Dado) + " + atributoBonus + " (" + nomeAtributo + ") = " + totalAtaque + (critico ? " [CRÍTICO!]" : ""));
        Interface.Pausa(1500);

        if (critico || totalAtaque >= alvo.getDefesa()) {
            Interface.MostrarMensagem("-> Acertou! (defesa do alvo: " + alvo.getDefesa() + ")" + (critico ? " CRÍTICO sempre acerta." : ""));
            Interface.Pausa(1200);
            int dano = 0;
            int dadosTotais = arma != null ? arma.getQuantidadeDanoArma() : 1;
            int dadoDano = arma != null ? arma.getDadoDanoArma() : 4;
            if (critico) dadosTotais *= 2;
            StringBuilder roladas = new StringBuilder();
            for (int i = 0; i < dadosTotais; i++) {
                int d = MecanicasRpg.rolarDado(dadoDano);
                dano += d;
                if (roladas.length() > 0) roladas.append(" + ");
                roladas.append(d);
            }
            dano += atributoBonus;
            Interface.MostrarMensagem("-> Dados Rolados: " + roladas + " = " + dano + " (Dano: " + dadosTotais + "d" + dadoDano + " + " + nomeAtributo + ": " + atributoBonus + ")");
            Interface.Pausa(1500);
            aplicarDanoCriatura(alvo, dano);
            Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
        } else {
            Interface.MostrarMensagem("-> Errou! (defesa do alvo: " + alvo.getDefesa() + ")");
        }
        Interface.Pausa(1200);
    }

    public static boolean companheiroEmPe(FichaRpg ficha) {
        companheiros.Companheiro comp = ficha.getCompanheiro();
        return comp != null && comp.getFicha().getVidaPersonagem() > 0;
    }

}
