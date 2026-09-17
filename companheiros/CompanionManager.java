package companheiros;

import classes.*;
import fichas.FichaRpg;
import itens.Arma;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class CompanionManager {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;

    public static void verificarCompanheiroPosDormir(FichaRpg ficha) {
        if (!ficha.companheiroQuerPartir()) return;
        String nomePartiu = ficha.getCompanheiro().getNomeCompleto();
        ficha.removerCompanheiro();
        Interface.MostrarMensagem("\nApós passar a noite e decidir seu futuro, " + nomePartiu + " percebe que é hora de seguir o próprio caminho.");
        Interface.MostrarMensagem("Vocês se despedem com gratidão e ela/e segue a própria jornada!");
        Interface.Pausa(2500);
    }

    public static void EventoPerdido(FichaRpg ficha) {
        companheiros.Companheiro perdido = new companheiros.Companheiro();

        Interface.MostrarMensagem("\nUm vulto surge entre as árvores, com olhar cansado e roupas surradas...");
        Interface.Pausa(2000);
        Interface.MostrarMensagem("\n" + perdido.getNomeCompleto() + " se aproxima, aliviado(a) por encontrar alguém.");
        Interface.Pausa(2000);
        Interface.MostrarMensagem("\"Por favor! Estou perdido(a) nesta floresta há dias. Ouvi dizer que você tem uma cabana... posso ficar um tempo?\"");
        Interface.Pausa(2000);

        System.out.println("\n  O que você faz?\n");
        System.out.println("  1. Acolhê-lo(a) por um tempo");
        System.out.println("  2. Recusar e seguir seu caminho");
        int escolha = Interface.lerOpcao(1, 2);

        if (escolha == 1) {
            if (ficha.temCompanheiro()) {
                Interface.MostrarMensagem("\nVocê já tem alguém sob sua proteção. " + perdido.getNome() + " compreende e segue adiante.");
                Interface.Pausa(2000);
                return;
            }
            ficha.setCompanheiro(perdido);
            Interface.MostrarMensagem("\nA partir de agora, " + perdido.getNomeCompleto() + " te acompanha em tudo: lutar, dormir, treinar e explorar!");
            Interface.MostrarMensagem("Fale com " + perdido.getNome() + " pelo menu principal para conhecer melhor essa pessoa.");
            Interface.Pausa(2500);
        } else {
            Interface.MostrarMensagem("\n\"Sinto muito, mas não posso ajudar agora.\" " + perdido.getNome() + ", desapontado(a), se afasta para dentro da floresta.");
            Interface.Pausa(2000);
        }
    }

    public static void ConversarComCompanheiro(FichaRpg ficha) {
        companheiros.Companheiro comp = ficha.getCompanheiro();
        if (comp == null) return;

        while (true) {
            Interface.cabecalhoMenu("CONVERSAR COM " + comp.getNome().toUpperCase());
            comp.mostrarResumo();

            System.out.println("\n  O que deseja fazer?\n");
            System.out.println("  1. Ouvir o que ela(e) tem a dizer");
            System.out.println("  2. Ver os itens que ela(e) carrega");

            boolean podeCurar = ficha.temItem("Kit Médico")
                    && comp.getFicha().getVidaPersonagem() < comp.getFicha().getVidaMaxima();
            if (podeCurar) {
                System.out.println("  3. Curar " + comp.getNome() + " com um Kit Médico");
            }
            System.out.println("  4. Expulsar " + comp.getNome());
            System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

            int escolha = Interface.lerOpcao(0, 4);
            if (escolha == 0) return;
            if (escolha == 1) {
                comp.falarSobreClasse();
            } else if (escolha == 2) {
                comp.mostrarItens();
                Interface.Pausa(1500);
            } else if (escolha == 3 && podeCurar) {
                curarCompanheiroComKit(ficha);
            } else if (escolha == 4) {
                expulsarCompanheiro(ficha);
                return;
            }
        }
    }

    public static void expulsarCompanheiro(FichaRpg ficha) {
        companheiros.Companheiro comp = ficha.getCompanheiro();
        if (comp == null) return;

        System.out.println("\n  Tem certeza que deseja expulsar " + comp.getNomeCompleto() + "?");
        System.out.println("  1. Sim, expulsar");
        System.out.println("  2. Não, manter");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        int confirma = Interface.lerOpcao(1, 2);

        if (confirma == 1) {
            String nomePartiu = comp.getNomeCompleto();
            ficha.removerCompanheiro();
            Interface.MostrarMensagem("\nVocê diz para " + nomePartiu + " que é hora de seguir sozinho(a).");
            Interface.Pausa(1500);
            Interface.MostrarMensagem("\"Entendo... Obrigado(a) por tudo.\" " + nomePartiu + " se despede e segue sua jornada.");
            Interface.Pausa(2000);
        }
    }

    public static void curarCompanheiroComKit(FichaRpg ficha) {
        companheiros.Companheiro comp = ficha.getCompanheiro();
        if (comp == null || !ficha.temItem("Kit Médico")) return;

        FichaRpg cf = comp.getFicha();
        int cura = MecanicasRpg.rolarDado(4);
        int antes = cf.getVidaPersonagem();
        cf.setVidaPersonagem(Math.min(antes + cura, cf.getVidaMaxima()));
        int curaReal = cf.getVidaPersonagem() - antes;
        Interface.MostrarMensagem("\nVocê usa um Kit Médico em " + comp.getNome() + " e ela(e) recupera " + curaReal + " de vida! Vida: " + cf.getVidaPersonagem() + "/" + cf.getVidaMaxima());

        ficha.removerItem("Kit Médico", 1);
        if (ficha.temItem("Kit Médico")) {
            int restante = 0;
            for (ItemRpg item : ficha.getInventario()) {
                if (item.getNome().equals("Kit Médico")) {
                    restante = item.getQuantidade();
                    break;
                }
            }
            Interface.MostrarMensagem("Restam " + restante + "x Kit Médico.");
        } else {
            Interface.MostrarMensagem("Seu Kit Médico acabou.");
        }
        Interface.Pausa(2000);
    }

    public static void acaoDoCompanheiro(FichaRpg ficha, companheiros.Companheiro comp, List<criaturas.Criatura> inimigos) {
        FichaRpg cf = comp.getFicha();
        if (cf.getVidaPersonagem() <= 0) return;

        criaturas.Criatura alvo = escolherAlvoAleatorio(inimigos);
        if (alvo == null) return;

        Interface.MostrarMensagem("\n" + comp.getNomeCompleto() + " age!");
        Interface.Pausa(1200);

        // Healer: prioriza curar o jogador quando ele está ferido; se não, cura a si mesmo
        if (cf.getClasseDoPersonagem() instanceof Healer
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

        // Mago: tenta lançar magia quando tem mana disponível
        if (cf.getClasseDoPersonagem() instanceof Mago) {
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
            if (bola != null && cf.getManaPersonagem() >= bola.getCustoMana() && aleatorio <= 60) {
                magiaUsar = bola;
            } else if (pequena != null && aleatorio <= 30) {
                magiaUsar = pequena;
            }

            if (magiaUsar != null) {
                cf.setManaPersonagem(cf.getManaPersonagem() - magiaUsar.getCustoMana());
                Interface.MostrarMensagem(comp.getNomeCompleto() + " conjura " + magiaUsar.getNome() + "!");
                Interface.Pausa(1500);
                int dano = 0;
                for (int i = 0; i < magiaUsar.getQuantidadeDano(); i++) {
                    dano += MecanicasRpg.rolarDado(magiaUsar.getDadoDano());
                }
                Interface.MostrarMensagem("-> Dano: " + dano + "!");
                Interface.Pausa(1200);
                alvo.setVida(alvo.getVida() - dano);
                Interface.MostrarMensagem(eventos.Floresta.rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
                Interface.Pausa(1200);
                return;
            }
        }

        // Ataque físico com a arma equipada
        Interface.MostrarMensagem(comp.getNome() + " avança para atacar!");
        Interface.Pausa(1200);
        atacarComArmaDoCompanheiro(cf, alvo, inimigos);
    }

    public static void atacarComArmaDoCompanheiro(FichaRpg cf, criaturas.Criatura alvo, List<criaturas.Criatura> inimigos) {
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
            alvo.setVida(alvo.getVida() - dano);
            Interface.MostrarMensagem(eventos.Floresta.rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
        } else {
            Interface.MostrarMensagem("-> Errou! (defesa do alvo: " + alvo.getDefesa() + ")");
        }
        Interface.Pausa(1200);
    }

    private static criaturas.Criatura escolherAlvoAleatorio(List<criaturas.Criatura> inimigos) {
        List<criaturas.Criatura> vivos = eventos.Floresta.inimigosVivos(inimigos);
        if (vivos.isEmpty()) return null;
        return vivos.get(MecanicasRpg.rolarDado(vivos.size()) - 1);
    }
}
