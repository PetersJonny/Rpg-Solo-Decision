package combate;

import classes.Guerreiro;
import classes.Mago;
import classes.Healer;
import itens.Arma;
import itens.Armadura;
import itens.ItemRpg;
import habilidades.Habilidade;
import habilidades.Magia;
import mecanicas.MecanicasRpg;
import telas.Interface;
import fichas.FichaRpg;

import java.util.ArrayList;
import java.util.List;

public class WeaponSelector {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String AMARELO = Interface.AMARELO;
    private static final String VERDE = Interface.VERDE;

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

    public static boolean executarAtaqueComArma(FichaRpg ficha, List<criaturas.Criatura> inimigos, int alvoIndex, int armaIndex) {
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return false;
        criaturas.Criatura inimigo = inimigos.get(alvoIndex);

        if (ficha.isDefesaAbsolutaAtiva()) {
            ficha.setDefesaAbsolutaAtiva(false);
            Interface.MostrarMensagem("(Sua Defesa Absoluta se dissipa ao atacar!)");
            Interface.Pausa(1500);
        }

        Interface.MostrarMensagem("\nVocê prepara seu ataque contra " + CombatManager.rotuloCriatura(inimigos, inimigo) + "...");
        Interface.Pausa(1500);

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

                int dadosTotais = armaEscolhida.getQuantidadeDanoArma() * (critico ? 2 : 1);
                boolean semiDeusBonus = ficha.isSemiDeusAtivo() && armaEscolhida.getTipoArma().contains("CaC");
                if (semiDeusBonus) {
                    dadosTotais += 4;
                    Interface.MostrarMensagem("(Semi Deus! +4 dados de dano)");
                    Interface.Pausa(1000);
                }
                StringBuilder roladas = new StringBuilder();
                Interface.pressionarParaRolar();
                for (int i = 0; i < dadosTotais; i++) {
                    int dado = MecanicasRpg.rolarDado(armaEscolhida.getDadoDanoArma());
                    dano += dado;
                    if (roladas.length() > 0) roladas.append(" + ");
                    roladas.append(dado);
                }
                dano += atributoBonus;
                Interface.MostrarMensagem("-> Dados Rolados: " + roladas + " = " + dano + " (Dano: " + dadosTotais + "d" + armaEscolhida.getDadoDanoArma() + " + " + nomeAtributo + ": " + atributoBonus + ")");
                Interface.Pausa(2000);
            } else {
                Interface.MostrarMensagem("-> Errou! (defesa do alvo: " + inimigo.getDefesa() + ")");
                Interface.Pausa(1500);
            }

            if (armaEscolhida.getTipoArma().contains("LA")) {
                consumirFlecha(ficha);
            }
        } else {
            return false;
        }

        if (dano > 0) {
            if (ficha.isEspadaAfiadaAtiva() && armaIndex >= 0) {
                int bonusAfiada = MecanicasRpg.rolarDado(8) + MecanicasRpg.rolarDado(8);
                dano += bonusAfiada;
                Interface.MostrarMensagem("(Espada Afiada! +" + bonusAfiada + " de dano)");
                Interface.Pausa(1500);
            }
            inimigo.setVida(inimigo.getVida() - dano);
            Interface.MostrarMensagem(CombatManager.rotuloCriatura(inimigos, inimigo) + " agora tem " + Math.max(0, inimigo.getVida()) + " de vida.");
            Interface.Pausa(2000);
        }

        SkillExecutor.aplicarVenenoCuraParaMorte(ficha, inimigos, alvoIndex);
        return dano > 0;
    }

    public static int EscolherHabilidadeAtiva(FichaRpg ficha) {
        List<Habilidade> ativas = new ArrayList<>();
        for (Habilidade hab : ficha.getHabilidades()) {
            if (!hab.isPassiva()
                    && !hab.getNome().equals("Cura Reforçada")
                    && !hab.getNome().equals("Magia Proibida")) {
                ativas.add(hab);
            }
        }

        if (ativas.isEmpty()) {
            Interface.MostrarMensagem("\nVocê não possui habilidades ativas.");
            Interface.Pausa(1500);
            return -1;
        }

        Interface.cabecalhoMenu("SUAS HABILIDADES");
        System.out.println("\n");
        for (int i = 0; i < ativas.size(); i++) {
            Habilidade hab = ativas.get(i);
            String extra = "";
            if (hab instanceof Magia) {
                Magia magia = (Magia) hab;
                if (magia.getDadoDano() > 0) {
                    extra = " - Dano: " + magia.getQuantidadeDano() + "d" + magia.getDadoDano();
                }
            }
            System.out.println("  " + (i + 1) + ". " + CIANO + hab.getNome() + RESET + " (Custo: " + (custoEfetivoMagia(ficha, hab) == 0 ? "Grátis" : custoEfetivoMagia(ficha, hab) + " Mana") + ")" + extra);
        }
        System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

        int escolha = Interface.lerInteiro();

        if (escolha == 0) return -1;

        if (escolha < 1 || escolha > ativas.size()) {
            Interface.ExibirErro("Escolha inválida!");
            Interface.Pausa(1500);
            return -1;
        }

        Habilidade habEscolhida = ativas.get(escolha - 1);

        if (habEscolhida instanceof Magia
                && ficha.getClasseDoPersonagem() instanceof Mago
                && !ficha.temItem("Cajado")) {
            Interface.ExibirErro("Você precisa de um Cajado para usar suas magias!");
            Interface.Pausa(1500);
            return -1;
        }

        if (ficha.getManaPersonagem() < habEscolhida.getCustoMana()) {
            Interface.ExibirErro("Mana insuficiente! Precisa de " + habEscolhida.getCustoMana() + " de mana.");
            Interface.Pausa(1500);
            return -1;
        }

        return ficha.getHabilidades().indexOf(habEscolhida);
    }

    public static int custoEfetivoMagia(FichaRpg ficha, Habilidade hab) {
        if (hab instanceof Magia && hab.getCustoMana() > 0 && ficha.temItem("Pequeno Grimório")) {
            return Math.max(1, hab.getCustoMana() - 1);
        }
        return hab.getCustoMana();
    }

    public static boolean executarHabilidadeEscolhida(FichaRpg ficha, List<criaturas.Criatura> inimigos, int alvoIndex, int habilidadeIndex) {
        if (habilidadeIndex < 0 || habilidadeIndex >= ficha.getHabilidades().size()) return true;
        Habilidade hab = ficha.getHabilidades().get(habilidadeIndex);
        if (ficha.getManaPersonagem() < custoEfetivoMagia(ficha, hab)) {
            Interface.ExibirErro("Mana insuficiente!");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        String nomeHab = hab.getNome();
        if (nomeHab.equals("Giro")) return SkillExecutor.executarGiro(ficha, inimigos);
        if (nomeHab.equals("Estrondo")) return SkillExecutor.executarEstrondo(ficha, inimigos, hab);
        if (nomeHab.equals("Prisão")) return SkillExecutor.usarPrisao(ficha, inimigos, alvoIndex, hab);
        if (nomeHab.equals("Semi Deus")) return SkillExecutor.executarSemiDeus(ficha, hab);
        if (nomeHab.equals("Poder Absoluto")) return SkillExecutor.executarPoderAbsoluto(ficha, hab);
        if (nomeHab.equals("Cura Absoluta")) return SkillExecutor.executarCuraAbsoluta(ficha, hab);
        if (nomeHab.equals("Cura Incessante")) return SkillExecutor.executarCuraIncessante(ficha);
        if (nomeHab.equals("Explosão de Poder")) return SkillExecutor.executarExplosaoDePoder(ficha, inimigos);
        if (nomeHab.equals("Conhecimento Avassalador")) return SkillExecutor.tentarConhecimentoAvassalador(ficha, inimigos);
        if (nomeHab.equals("Proteção Absoluta")) return SkillExecutor.usarProtecaoAbsoluta(ficha, hab);
        if (nomeHab.equals("Cura para a Morte")) return SkillExecutor.usarCuraParaMorte(ficha, inimigos, hab);
        if (nomeHab.equals("Cura Total")) {
            Interface.MostrarMensagem("Cura Total só pode ser usada para reviver quem morreu em combate.");
            Interface.Pausa(1500);
            return true;
        }
        if (hab instanceof Magia) {
            Magia magia = (Magia) hab;
            int quantidadeDano = magia.getQuantidadeDano();
            StringBuilder roladas = new StringBuilder();
            int dano = 0;
            for (int i = 0; i < quantidadeDano; i++) {
                int dadoRolado = MecanicasRpg.rolarDado(magia.getDadoDano());
                dano += dadoRolado;
                if (roladas.length() > 0) roladas.append(" + ");
                roladas.append(dadoRolado);
            }
            Interface.MostrarMensagem("\nVocê usa " + hab.getNome() + "!");
            Interface.MostrarMensagem("-> Dados Rolados: " + roladas + " = " + dano);
            Interface.Pausa(2000);
            if (!magia.isAtaqueArea() && alvoIndex >= 0 && alvoIndex < inimigos.size()) {
                criaturas.Criatura alvo = inimigos.get(alvoIndex);
                alvo.setVida(alvo.getVida() - dano);
                Interface.MostrarMensagem(CombatManager.rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
            }
            Interface.Pausa(1500);
            return true;
        }
        Interface.MostrarMensagem(hab.getDescricao());
        Interface.Pausa(2000);
        return false;
    }
}
