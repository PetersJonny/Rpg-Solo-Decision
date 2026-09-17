package combate;

import criaturas.Criatura;
import fichas.FichaRpg;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class SkillExecutor {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;

    public static boolean executarGiro(FichaRpg ficha, List<Criatura> inimigos) {
        int maxGiros = Math.max(1, ficha.getDestreza());
        System.out.println("\nVocê usa Giro! Quantos giros quer dar? (Custo: 1 de mana por giro)");
        System.out.println("Máximo de giros: " + maxGiros + " (sua Destreza)");

        int giros = Interface.lerInteiro();

        if (giros < 1 || giros > maxGiros) {
            Interface.ExibirErro("Número de giros inválido!");
            Interface.Pausa(1500);
            return true;
        }
        if (ficha.getManaPersonagem() < giros) {
            Interface.ExibirErro("Mana insuficiente para " + giros + " giros!");
            Interface.Pausa(1500);
            return true;
        }

        ficha.setManaPersonagem(ficha.getManaPersonagem() - giros);
        Interface.MostrarMensagem("\nVocê gira " + giros + "x com sua espada!");
        Interface.Pausa(1500);

        List<Criatura> vivos = eventos.Floresta.inimigosVivos(inimigos);
        if (vivos.isEmpty()) return true;

        for (int g = 1; g <= giros; g++) {
            int dadoGiro = MecanicasRpg.rolarDado(10);
            int danoGiro = dadoGiro + ficha.getForca();
            Interface.MostrarMensagem("-> Giro " + g + ": " + dadoGiro + " (1d10) + " + ficha.getForca() + " (Força) = " + danoGiro + " de dano em área!");
            Interface.Pausa(1500);
            for (Criatura alvo : vivos) {
                if (alvo.getVida() <= 0) continue;
                alvo.setVida(alvo.getVida() - danoGiro);
                Interface.MostrarMensagem(eventos.Floresta.rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
            }
            Interface.Pausa(1500);
        }
        return true;
    }

    public static boolean executarEstrondo(FichaRpg ficha, List<Criatura> inimigos, habilidades.Habilidade hab) {
        ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
        ficha.setRodadasSemHabilidade(2);
        Interface.MostrarMensagem("\nVocê golpeia o chão com toda a sua força! A terra se ergue ao seu redor!");
        Interface.Pausa(1500);

        List<Criatura> vivos = eventos.Floresta.inimigosVivos(inimigos);
        if (vivos.isEmpty()) return true;

        int dano = 0;
        Interface.pressionarParaRolar();
        for (int i = 0; i < 7; i++) {
            dano += MecanicasRpg.rolarDado(10);
        }
        dano += ficha.getForca();
        Interface.MostrarMensagem("-> Estrondo: 7d10 + " + ficha.getForca() + " (Força) = " + dano + " de dano em área!");
        Interface.Pausa(1500);

        for (Criatura alvo : vivos) {
            if (alvo.getVida() <= 0) continue;
            alvo.setVida(alvo.getVida() - dano);
            Interface.MostrarMensagem(eventos.Floresta.rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
        }
        Interface.MostrarMensagem("Você não poderá usar habilidades no próximo turno!");
        Interface.Pausa(1500);
        return true;
    }

    public static boolean usarPrisao(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, habilidades.Habilidade hab) {
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) {
            Interface.MostrarMensagem("Nenhum alvo escolhido.");
            Interface.Pausa(1500);
            return true;
        }
        Criatura alvo = inimigos.get(alvoIndex);
        ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
        ficha.setPrisaoAtiva(alvo);
        Interface.MostrarMensagem("\nVocê prende " + eventos.Floresta.rotuloCriatura(inimigos, alvo) + " em uma prisão de energia!");
        Interface.MostrarMensagem("Na vez dele, ele precisa tirar 15 ou mais em um d20 para se libertar.");
        Interface.Pausa(2000);
        return true;
    }

    public static boolean executarSemiDeus(FichaRpg ficha, habilidades.Habilidade hab) {
        if (ficha.isSemiDeusAtivo()) {
            Interface.MostrarMensagem("Você já está em forma de semi-deus!");
            Interface.Pausa(1500);
            return true;
        }
        if (ficha.getManaPersonagem() <= 0) {
            Interface.ExibirErro("Sem mana para ativar a forma de semi-deus!");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setSemiDeusVidaOriginalMax(ficha.getVidaMaxima());
        int bonus = ficha.getVidaMaxima() / 2;
        ficha.setVidaMaxima(ficha.getVidaMaxima() + bonus);
        ficha.setVidaPersonagem(ficha.getVidaMaxima());
        ficha.setManaPersonagem(0);
        ficha.setSemiDeusAtivo(true);
        Interface.MostrarMensagem("\nVocê desperta seu poder divino! Toda a sua mana se converte em força vital!");
        Interface.MostrarMensagem("Vida máxima aumentada para " + ficha.getVidaMaxima() + " e vida totalmente recuperada!");
        Interface.MostrarMensagem("Seus ataques corpo a corpo ganham +4 dados de dano.");
        Interface.Pausa(2500);
        return true;
    }

    public static boolean executarPoderAbsoluto(FichaRpg ficha, habilidades.Habilidade hab) {
        if (ficha.isPoderAbsolutoAtivo()) {
            Interface.MostrarMensagem("O Poder Absoluto já está ativo!");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
        ficha.setPoderAbsolutoAtivo(true);
        Interface.MostrarMensagem("\nVocê se envolve na energia do seu elemento! Suas magias dobram de poder!");
        Interface.Pausa(2000);
        return true;
    }

    public static boolean executarCuraAbsoluta(FichaRpg ficha, habilidades.Habilidade hab) {
        ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
        if (ficha.getCuraAbsolutaBonus() == 0) {
            ficha.setCuraAbsolutaVidaOriginalMax(ficha.getVidaMaxima());
        }
        ficha.setVidaPersonagem(ficha.getVidaMaxima());
        ficha.setCuraAbsolutaBonus(ficha.getVidaMaxima());
        Interface.MostrarMensagem("\nVocê injeta o líquido absoluto! Vida totalmente recuperada e uma proteção de +" + ficha.getCuraAbsolutaBonus() + " de vida!");
        Interface.MostrarMensagem("A proteção é gasta primeiro, antes da sua vida real.");
        Interface.Pausa(2500);
        return true;
    }

    public static boolean executarCuraIncessante(FichaRpg ficha) {
        if (ficha.isCuraIncessanteUsada()) {
            Interface.MostrarMensagem("A Cura Incessante só pode ser usada uma vez por combate!");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setCuraIncessanteUsada(true);
        ficha.setVidaPersonagem(ficha.getVidaMaxima());
        Interface.MostrarMensagem("\nSua força divina flui por todo o seu corpo! Vida totalmente recuperada!");
        Interface.Pausa(2000);
        return true;
    }

    public static boolean executarExplosaoDePoder(FichaRpg ficha, List<Criatura> inimigos) {
        if (ficha.getManaPersonagem() < 2) {
            Interface.MostrarMensagem("Você precisa de pelo menos 2 de mana para a Explosão de Poder.");
            Interface.Pausa(1500);
            return true;
        }

        int maximoGasto = ficha.getManaPersonagem();
        System.out.println("\nVocê canaliza toda a sua energia do elemento!");
        System.out.println("Quanto de mana quer gastar? (cada 2 de mana = 2d12 de dano em todos os inimigos)");
        System.out.println("Mínimo: 2 | Máximo: " + maximoGasto);

        int gasto = Interface.lerInteiro();
        gasto = Math.max(2, Math.min(gasto, maximoGasto));
        gasto -= gasto % 2;

        ficha.setManaPersonagem(ficha.getManaPersonagem() - gasto);
        int pares = gasto / 2;
        int totalDados = pares * 2;

        String elemento = "místico";
        for (habilidades.Habilidade h : ficha.getHabilidades()) {
            if (h instanceof habilidades.Magia) {
                String nome = h.getNome();
                int ini = nome.indexOf('(');
                int fim = nome.indexOf(')');
                if (ini >= 0 && fim > ini) {
                    elemento = nome.substring(ini + 1, fim).trim().toLowerCase();
                    break;
                }
            }
        }

        Interface.MostrarMensagem("\nVocê libera a Explosão de Poder! " + gasto + " de mana se convertem em " + totalDados + "d12 de dano de " + elemento + "!");
        Interface.Pausa(2000);

        int dano = 0;
        Interface.pressionarParaRolar();
        for (int i = 0; i < totalDados; i++) {
            dano += MecanicasRpg.rolarDado(12);
        }
        Interface.MostrarMensagem("-> Dados Rolados: " + totalDados + "d12 = " + dano + " de dano em TODOS os inimigos!");
        Interface.Pausa(2000);

        List<Criatura> vivos = eventos.Floresta.inimigosVivos(inimigos);
        for (Criatura alvo : vivos) {
            alvo.setVida(alvo.getVida() - dano);
            Interface.MostrarMensagem(eventos.Floresta.rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
            Interface.Pausa(1500);
        }

        return true;
    }

    public static boolean tentarConhecimentoAvassalador(FichaRpg ficha, List<Criatura> inimigos) {
        Interface.MostrarMensagem("\nVocê canaliza todo o seu conhecimento sobre as criaturas...");
        Interface.Pausa(1500);

        Interface.pressionarParaTeste("Intelecto");
        int dado = MecanicasRpg.rolarDado(20);
        int total = dado + ficha.getIntelectoTeste();
        Interface.MostrarMensagem("-> Teste de Intelecto: " + dado + " (Dado) + " + ficha.getIntelectoTeste() + " (Intelecto) = " + total + " (Dificuldade: 15)");
        Interface.Pausa(1500);

        if (total < 15) {
            Interface.MostrarMensagem("As mentes das criaturas são densas demais... Você não encontrou nada útil.");
            Interface.Pausa(1500);
            return true;
        }

        Interface.MostrarMensagem("\nVocê compreende tudo sobre seus inimigos!");
        for (Criatura c : eventos.Floresta.inimigosVivos(inimigos)) {
            StringBuilder ataques = new StringBuilder();
            for (criaturas.Criatura.Ataque a : c.getAtaques()) {
                if (ataques.length() > 0) ataques.append("; ");
                ataques.append(a.nome).append(" (").append(a.qtdDado).append("d").append(a.ladosDado).append(")");
            }
            Interface.MostrarMensagem("-> " + eventos.Floresta.rotuloCriatura(inimigos, c) + ": Vida " + c.getVida() + " | Defesa " + c.getDefesa()
                    + " | Iniciativa " + c.getIniciativa() + " | Ataques: " + ataques + " | XP " + c.getXpGanho());
        }
        Interface.Pausa(2000);
        return true;
    }

    public static boolean usarProtecaoAbsoluta(FichaRpg ficha, habilidades.Habilidade hab) {
        if (ficha.isProtecaoAbsolutaAtiva()) {
            Interface.MostrarMensagem("A Proteção Absoluta já está ativa!");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
        ficha.setProtecaoAbsolutaAtiva(true);
        ficha.setBonusDefesaTemporario(ficha.getBonusDefesaTemporario() + 3);
        Interface.MostrarMensagem("\nVocê se envolve no seu elemento! +3 de defesa e reflete 2d8 de dano a quem te acertar.");
        Interface.Pausa(2000);
        return true;
    }

    public static boolean usarCuraParaMorte(FichaRpg ficha, List<Criatura> inimigos, habilidades.Habilidade hab) {
        int alvoVeneno = combate.CombatManager.escolherAlvo(inimigos);
        if (alvoVeneno < 0) {
            Interface.MostrarMensagem("Nenhum alvo escolhido.");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setManaPersonagem(ficha.getManaPersonagem() - hab.getCustoMana());
        Criatura alvo = inimigos.get(alvoVeneno);
        ficha.setAlvoCuraParaMorte(alvo);
        ficha.setCuraParaMortePreparado(true);
        Interface.MostrarMensagem("\nVocê injeta o líquido mortal em " + eventos.Floresta.rotuloCriatura(inimigos, alvo) + "! Ele age a partir do próximo turno.");
        Interface.Pausa(2000);
        return true;
    }

    public static void aplicarVenenoCuraParaMorte(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex) {
        if (!ficha.isCuraParaMorteAtivo() || ficha.getAlvoCuraParaMorte() == null) return;
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return;

        Criatura alvo = inimigos.get(alvoIndex);
        if (alvo.getVida() <= 0 || alvo != ficha.getAlvoCuraParaMorte()) return;

        int veneno = MecanicasRpg.rolarDado(8) + MecanicasRpg.rolarDado(8) + MecanicasRpg.rolarDado(8);
        alvo.setVida(alvo.getVida() - veneno);
        Interface.MostrarMensagem("(Cura para a Morte! O líquido mortal causa " + veneno + " de dano)");
        Interface.Pausa(1500);
    }

    public static boolean tentarReviver(FichaRpg ficha) {
        if (ficha.getVidaPersonagem() > 0) return true;
        if (ficha.isCuraTotalUsada() || ficha.getManaPersonagem() < 10) return false;
        if (!CombatResolver.temHabilidade(ficha, "Cura Total")) return false;

        System.out.println("\nVocê foi derrubado! Deseja usar Cura Total (10 de mana) para reviver com a vida cheia?");
        System.out.println("1. Sim");
        System.out.println("2. Não");
        int escolha = Interface.lerOpcao(1, 2);

        if (escolha != 1) return false;

        ficha.setManaPersonagem(ficha.getManaPersonagem() - 10);
        ficha.setVidaPersonagem(ficha.getVidaMaxima());
        ficha.setCuraTotalUsada(true);
        Interface.MostrarMensagem("\nCura Total! Você renasce com a vida cheia!");
        Interface.Pausa(2500);
        return true;
    }

    public static boolean podeAtivarPassiva(FichaRpg ficha, habilidades.Habilidade hab, boolean cascaGrossaAtiva) {
        if (!hab.isPassiva()) return false;
        switch (hab.getNome()) {
            case "Casca Grossa" -> { return !cascaGrossaAtiva; }
            case "Espada Afiada" -> { return !ficha.isEspadaAfiadaAtiva(); }
            default -> { return true; }
        }
    }

    public static void aplicaPassiva(FichaRpg ficha, habilidades.Habilidade hab, boolean[] cascaGrossaAtiva) {
        switch (hab.getNome()) {
            case "Casca Grossa" -> cascaGrossaAtiva[0] = true;
            case "Espada Afiada" -> ficha.setEspadaAfiadaAtiva(true);
        }
    }

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

    public static void escolherPontoAtributo(FichaRpg ficha) {
        Interface.cabecalhoMenu("PONTO DE ATRIBUTO");
        System.out.println("\n  Você ganhou um ponto de atributo! Escolha onde gastar:\n");
        System.out.println("  1. Constituição");
        System.out.println("  2. Destreza");
        System.out.println("  3. Força");
        System.out.println("  4. Sabedoria");
        System.out.println("  5. Intelecto");
        System.out.println("  6. Presença");

        int escolha = Interface.lerOpcao(1, 6);

        String atributo = ficha.aumentarAtributo(escolha);
        Interface.MostrarMensagem("\n+1 de " + atributo + "!");
        if (atributo.equals("Constituição") && ficha.getNivel() > 1) {
            Interface.MostrarMensagem("Vida máxima aumentada em " + (ficha.getNivel() - 1) + " pelo retroativo de Constituição dos níveis anteriores!");
        }
        Interface.Pausa(1500);
    }

    public static void aplicarDeusSeAprendido(FichaRpg ficha, habilidades.Habilidade aprendida) {
        if (!aprendida.getNome().equals("Deus")) return;
        if (ficha.isDeusAtivo()) return;

        if (ficha.isSemiDeusAtivo() && ficha.getSemiDeusVidaOriginalMax() > 0) {
            ficha.setVidaMaxima(ficha.getSemiDeusVidaOriginalMax());
            ficha.setSemiDeusVidaOriginalMax(0);
        }

        int bonusVida = ficha.getVidaMaxima() / 2;
        ficha.setVidaMaxima(ficha.getVidaMaxima() + bonusVida);
        ficha.setSemiDeusAtivo(true);
        ficha.setDeusAtivo(true);
        if (!CombatResolver.temHabilidade(ficha, "Cura Incessante")) {
            ficha.getHabilidades().add(new habilidades.Habilidade("Cura Incessante", "Cura toda a sua vida. Pode ser usada apenas uma vez por combate.", 0));
        }
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
}
