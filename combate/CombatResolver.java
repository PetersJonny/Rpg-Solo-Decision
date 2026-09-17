package combate;

import criaturas.Criatura;
import fichas.FichaRpg;
import itens.Arma;
import itens.ItemRpg;
import java.util.List;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class CombatResolver {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;

    public static int calcularBonusAtributo(FichaRpg ficha, Arma arma) {
        if (arma != null && arma.isAgil()) {
            return Math.max(ficha.getForca(), ficha.getDestreza());
        } else if (arma != null && arma.getAtributoAtaque().equals("Destreza")) {
            return ficha.getDestreza();
        }
        return ficha.getForca();
    }

    public static String nomeAtributoAtaque(FichaRpg ficha, Arma arma) {
        if (arma != null && arma.isAgil()) {
            return ficha.getForca() >= ficha.getDestreza() ? "Força" : "Destreza";
        } else if (arma != null && arma.getAtributoAtaque().equals("Destreza")) {
            return "Destreza";
        }
        return "Força";
    }

    public static ResultadoAtaque resolverAtaqueFisico(FichaRpg ficha, Criatura alvo, Arma arma, String nomeAtaque) {
        int atributoBonus;
        String nomeAtributo;
        if (arma != null && arma.isAgil()) {
            if (ficha.getForca() >= ficha.getDestreza()) {
                atributoBonus = ficha.getForca();
                nomeAtributo = "Força";
            } else {
                atributoBonus = ficha.getDestreza();
                nomeAtributo = "Destreza";
            }
        } else if (arma != null && arma.getAtributoAtaque().equals("Destreza")) {
            atributoBonus = ficha.getDestreza();
            nomeAtributo = "Destreza";
        } else {
            atributoBonus = ficha.getForca();
            nomeAtributo = "Força";
        }

        int qtdDados = arma != null ? arma.getQuantidadeDanoArma() : 1;
        int dadoDano = arma != null ? arma.getDadoDanoArma() : 4;

        boolean ehCaC = arma == null || arma.getTipoArma().contains("CaC");
        return resolverAtaque(ficha, alvo, nomeAtaque, atributoBonus, nomeAtributo, qtdDados, dadoDano, false, ehCaC);
    }

    public static ResultadoAtaque resolverAtaque(FichaRpg ficha, Criatura alvo, String nomeAtaque,
                                                  int atributoBonus, String nomeAtributo,
                                                  int qtdDadosBase, int dadoDano, boolean magico,
                                                  boolean ehCorpoCorpo) {
        Interface.pressionarParaRolar();
        int dadoAtaque = MecanicasRpg.rolarDado(20);
        int totalAtaque = dadoAtaque + atributoBonus;
        boolean critico = dadoAtaque == 20;

        Interface.MostrarMensagem("-> Ataque [" + nomeAtaque + "]: " + dadoAtaque + " (Dado) + " + atributoBonus + " (" + nomeAtributo + ") = " + totalAtaque + (critico ? " [CRÍTICO!]" : ""));
        if (critico) {
            Interface.MostrarMensagem("Golpe crítico! O dano de dados será dobrado!");
        }
        Interface.Pausa(2000);

        int dano = 0;
        boolean acertou = critico || totalAtaque >= alvo.getDefesa();

        if (acertou) {
            Interface.MostrarMensagem("-> Acertou! (defesa do alvo: " + alvo.getDefesa() + ")" + (critico ? " CRÍTICO sempre acerta." : ""));
            Interface.Pausa(1500);

            int dadosTotais = qtdDadosBase * (critico ? 2 : 1);
            boolean semiDeusBonus = !magico && ficha.isSemiDeusAtivo() && ehCorpoCorpo;
            if (semiDeusBonus) {
                dadosTotais += 4;
                Interface.MostrarMensagem("(Semi Deus! +4 dados de dano)");
                Interface.Pausa(1000);
            }

            StringBuilder roladas = new StringBuilder();
            Interface.pressionarParaRolar();
            for (int i = 0; i < dadosTotais; i++) {
                int dado = MecanicasRpg.rolarDado(dadoDano);
                dano += dado;
                if (roladas.length() > 0) roladas.append(" + ");
                roladas.append(dado);
            }
            dano += atributoBonus;
            Interface.MostrarMensagem("-> Dados Rolados: " + roladas + " = " + dano + " (Dano: " + dadosTotais + "d" + dadoDano + " + " + nomeAtributo + ": " + atributoBonus + ")");
            Interface.Pausa(2000);
        } else {
            Interface.MostrarMensagem("-> Errou! (defesa do alvo: " + alvo.getDefesa() + ")");
            Interface.Pausa(1500);
        }

        return new ResultadoAtaque(dano, critico, acertou);
    }

    public static ResultadoAtaque resolverAtaqueCompanheiro(FichaRpg cf, Criatura alvo) {
        Arma arma = cf.getArmaEquipada();
        if (arma == null && cf.getClasseDoPersonagem() != null) {
            arma = cf.getClasseDoPersonagem().getAtaqueDesarmado();
        }
        String nomeAtaque = arma != null ? arma.getNome() : "Soco";
        return resolverAtaqueFisico(cf, alvo, arma, nomeAtaque);
    }

    public static void aplicarDano(Criatura alvo, int dano, List<Criatura> inimigos) {
        if (dano > 0) {
            alvo.setVida(alvo.getVida() - dano);
            Interface.MostrarMensagem(eventos.Floresta.rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
        }
    }

    public static void consumirFlecha(FichaRpg ficha) {
        for (int i = 0; i < ficha.getInventario().size(); i++) {
            ItemRpg item = ficha.getInventario().get(i);
            if (item.getNome().equals("Flechas")) {
                item.setQuantidade(item.getQuantidade() - 1);
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

    public static boolean temFlechas(FichaRpg ficha) {
        return getQtdFlechas(ficha) > 0;
    }

    public static int getQtdFlechas(FichaRpg ficha) {
        for (ItemRpg item : ficha.getInventario()) {
            if (item.getNome().equals("Flechas")) {
                return item.getQuantidade();
            }
        }
        return 0;
    }

    public static boolean temHabilidade(FichaRpg ficha, String nome) {
        for (habilidades.Habilidade hab : ficha.getHabilidades()) {
            if (hab.getNome().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    public static int custoEfetivoMagia(FichaRpg ficha, habilidades.Habilidade hab) {
        if (hab instanceof habilidades.Magia && hab.getCustoMana() > 0 && ficha.temItem("Pequeno Grimório")) {
            return Math.max(1, hab.getCustoMana() - 1);
        }
        return hab.getCustoMana();
    }

    public static boolean ehItemConsumivel(ItemRpg item) {
        if (!(item instanceof itens.Consumivel)) return false;
        return !item.getNome().equals("Flechas");
    }

    public static class ResultadoAtaque {
        public final int dano;
        public final boolean critico;
        public final boolean acertou;

        public ResultadoAtaque(int dano, boolean critico, boolean acertou) {
            this.dano = dano;
            this.critico = critico;
            this.acertou = acertou;
        }
    }
}
