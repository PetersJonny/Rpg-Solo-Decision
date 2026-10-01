package mecanicas;

import java.util.List;
import classes.*;
import itens.Arma;
import java.util.ArrayList;
import mecanicas.MecanicasRpg;
import criaturas.Criatura;
import itens.ItemRpg;
import java.util.HashSet;
import java.util.Set;
import fichas.FichaRpg;
import comandos.*;
import itens.Consumivel;
import telas.Interface;

import static mecanicas.GerenciadorDeAcoes.*;
import static mecanicas.MotorDeCombate.*;
import static mecanicas.GerenciadorDeTurnos.*;

public class GerenciadorDeHabilidades {

    public static int EscolherHabilidadeAtiva(FichaRpg ficha) {
        List<habilidades.Habilidade> ativas = new ArrayList<>();
        for (habilidades.Habilidade hab : ficha.getHabilidades()) {
            if (!hab.isPassiva()
                    && !hab.getNome().equals("Cura Reforçada")
                    && !hab.getNome().equals("Magia Proibida")
                    && !(hab.getNome().equals("Pacto Mortal") && !ficha.isOlhoDemonicoFundido())) {
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
            habilidades.Habilidade hab = ativas.get(i);
            String extra = "";
            if (hab instanceof habilidades.Magia) {
                habilidades.Magia magia = (habilidades.Magia) hab;
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

        habilidades.Habilidade habEscolhida = ativas.get(escolha - 1);

                if (habEscolhida instanceof habilidades.Magia
                && ficha.getClasseDoPersonagem() instanceof classes.Mago
                && !ficha.temItem("Cajado")
                && !ficha.temItem("Cajado de Sangue")) {
            Interface.ExibirErro("Você precisa de um Cajado para usar suas magias!");
            Interface.Pausa(1500);
            return -1;
        }

        if (ficha.getManaPersonagem() < custoEfetivoMagia(ficha, habEscolhida)) {
            Interface.ExibirErro("Mana insuficiente! Precisa de " + custoEfetivoMagia(ficha, habEscolhida) + " de mana.");
            Interface.Pausa(1500);
            return -1;
        }

        return ficha.getHabilidades().indexOf(habEscolhida);
    }

    public static int custoEfetivoMagia(FichaRpg ficha, habilidades.Habilidade hab) {
        int custo = hab.getCustoMana();
        if (custo <= 0) return 0;

        boolean meioFada = ficha.getRaca() != null && ficha.getRaca().reduzCustoMana();
        boolean pequenoGrimorio = hab instanceof habilidades.Magia && ficha.temItem("Pequeno Grimório");

        if (meioFada) custo--;
        if (pequenoGrimorio) custo--;
        return Math.max(1, custo);
    }

    public static boolean executarHabilidadeEscolhida(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, int habilidadeIndex) {
        if (habilidadeIndex < 0 || habilidadeIndex >= ficha.getHabilidades().size()) return true;

        habilidades.Habilidade hab = ficha.getHabilidades().get(habilidadeIndex);

        if (ficha.getManaPersonagem() < custoEfetivoMagia(ficha, hab)) {
            telas.Interface.ExibirErro("Mana insuficiente!");
            telas.Interface.Pausa(1500);
            return true;
        }

        return hab.executar(ficha, inimigos, alvoIndex);
    }

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

        List<Criatura> vivos = inimigosVivos(inimigos);
        if (vivos.isEmpty()) return true;

        int dadosPorGiro = 1;
        if (ficha.isMagiaBonusAtivo()) {
            dadosPorGiro++;
            Interface.MostrarMensagem("(Mesa de Magias! +1 dado de dano em cada giro)");
            Interface.Pausa(1000);
        }

        for (int g = 1; g <= giros; g++) {
            int dadoGiroTamanho = ficha.upgradeDadoFruta(10);
            int danoGiro = ficha.getForca();
            StringBuilder roladas = new StringBuilder();
            for (int i = 0; i < dadosPorGiro; i++) {
                int dadoGiro = MecanicasRpg.rolarDado(dadoGiroTamanho);
                danoGiro += dadoGiro;
                if (roladas.length() > 0) roladas.append(" + ");
                roladas.append(dadoGiro);
            }
            Interface.MostrarMensagem("-> Giro " + g + ": " + roladas + " (1d10" + (dadosPorGiro > 1 ? " + 1d10 (Mesa de Magias)" : "") + ") + " + ficha.getForca() + " (Força) = " + danoGiro + " de dano em área!");
            Interface.Pausa(1500);
            for (Criatura alvo : vivos) {
                if (alvo.getVida() <= 0) continue;
                aplicarDanoCriatura(alvo, danoGiro);
                Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
            }
            Interface.Pausa(1500);
        }
        return true;
    }

    public static boolean executarEstrondo(FichaRpg ficha, List<Criatura> inimigos, habilidades.Habilidade hab) {
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        ficha.setRodadasSemHabilidade(2);
        Interface.MostrarMensagem("\nVocê golpeia o chão com toda a sua força! A terra se ergue ao seu redor!");
        Interface.Pausa(1500);

        List<Criatura> vivos = inimigosVivos(inimigos);
        if (vivos.isEmpty()) return true;

        int totalDados = 7;
        if (ficha.isMagiaBonusAtivo()) {
            totalDados++;
            Interface.MostrarMensagem("(Mesa de Magias! +1 dado de dano)");
            Interface.Pausa(1000);
        }

        int dadoEstrondo = ficha.upgradeDadoFruta(10);
        int dano = 0;
        Interface.pressionarParaRolar();
        for (int i = 0; i < totalDados; i++) {
            dano += MecanicasRpg.rolarDado(dadoEstrondo);
        }
        dano += ficha.getForca();
        Interface.MostrarMensagem("-> Estrondo: " + totalDados + "d" + dadoEstrondo + " + " + ficha.getForca() + " (Força) = " + dano + " de dano em área!");
        Interface.Pausa(1500);

        for (Criatura alvo : vivos) {
            if (alvo.getVida() <= 0) continue;
            aplicarDanoCriatura(alvo, dano);
            Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
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
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        ficha.setPrisaoAtiva(alvo);
        Interface.MostrarMensagem("\nVocê prende " + rotuloCriatura(inimigos, alvo) + " em uma prisão de energia!");
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
        ficha.setSemiDeusVidaOriginalMax(ficha.getVidaMaximaBase());
        int bonus = ficha.getVidaMaximaBase() / 2;
        ficha.setVidaMaxima(ficha.getVidaMaximaBase() + bonus);
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
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        ficha.setPoderAbsolutoAtivo(true);
        Interface.MostrarMensagem("\nVocê se envolve na energia do seu elemento! Suas magias dobram de poder!");
        Interface.Pausa(2000);
        return true;
    }

    public static boolean executarCuraAbsoluta(FichaRpg ficha, habilidades.Habilidade hab) {
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
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
        if (ficha.isMagiaBonusAtivo()) {
            totalDados++;
            Interface.MostrarMensagem("(Mesa de Magias! +1 dado de dano)");
            Interface.Pausa(1000);
        }

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

        int ladoExtra = ficha.ladoDadoExtraFruta(totalDados);
        String exprBase = totalDados + "d12";
        String exprFruta = ladoExtra > 0 ? exprBase + " + 1d" + ladoExtra + " (Fruta do Diabo)" : exprBase;
        Interface.MostrarMensagem("\nVocê libera a Explosão de Poder! " + gasto + " de mana se convertem em " + exprFruta + " de dano de " + elemento + "!");
        Interface.Pausa(2000);

        int dano = 0;
        Interface.pressionarParaRolar();
        for (int i = 0; i < totalDados; i++) {
            dano += MecanicasRpg.rolarDado(12);
        }
        if (ladoExtra > 0) {
            dano += MecanicasRpg.rolarDado(ladoExtra);
        }
        Interface.MostrarMensagem("-> Dados Rolados: " + exprFruta + " = " + dano + " de dano em TODOS os inimigos!");
        Interface.Pausa(2000);

        List<Criatura> vivos = inimigosVivos(inimigos);
        for (Criatura alvo : vivos) {
            aplicarDanoCriatura(alvo, dano);
            Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
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

        if (total < 15 && ficha.podeUsarMenteAfiada()) {
            Interface.MostrarMensagem("\n(Mente Afiada!) Sua mente aguçada reavalia as criaturas... Deseja rolar novamente?");
            if (Interface.lerOpcao(2) == 1) {
                ficha.marcarMenteAfiadaUsada();
                dado = MecanicasRpg.rolarDado(20);
                total = dado + ficha.getIntelectoTeste();
                Interface.MostrarMensagem("-> Nova tentativa (Intelecto): " + dado + " (Dado) + " + ficha.getIntelectoTeste() + " (Intelecto) = " + total + " (Dificuldade: 15)");
                Interface.Pausa(1500);
            }
        }

        if (total < 15) {
            Interface.MostrarMensagem("As mentes das criaturas são densas demais... Você não encontrou nada útil.");
            Interface.Pausa(1500);
            return true;
        }

        Interface.MostrarMensagem("\nVocê compreende tudo sobre seus inimigos!");
        for (Criatura c : inimigosVivos(inimigos)) {
            StringBuilder ataques = new StringBuilder();
            for (criaturas.Criatura.Ataque a : c.getAtaques()) {
                if (ataques.length() > 0) ataques.append("; ");
                ataques.append(a.nome).append(" (").append(a.qtdDado).append("d").append(a.ladosDado).append(")");
            }
            Interface.MostrarMensagem("-> " + rotuloCriatura(inimigos, c) + ": Vida " + c.getVida() + " | Defesa " + c.getDefesa()
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
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        ficha.setProtecaoAbsolutaAtiva(true);
        ficha.setBonusDefesaTemporario(ficha.getBonusDefesaTemporario() + 3);
        Interface.MostrarMensagem("\nVocê se envolve no seu elemento! +3 de defesa e reflete 2d8 de dano a quem te acertar.");
        Interface.Pausa(2000);
        return true;
    }

    public static boolean usarCuraParaMorte(FichaRpg ficha, List<Criatura> inimigos, habilidades.Habilidade hab) {
        int alvoVeneno = escolherAlvo(inimigos);
        if (alvoVeneno < 0) {
            Interface.MostrarMensagem("Nenhum alvo escolhido.");
            Interface.Pausa(1500);
            return true;
        }
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        Criatura alvo = inimigos.get(alvoVeneno);
        ficha.setAlvoCuraParaMorte(alvo);
        ficha.setCuraParaMortePreparado(true);
        Interface.MostrarMensagem("\nVocê injeta o líquido mortal em " + rotuloCriatura(inimigos, alvo) + "! Ele age a partir do próximo turno.");
        Interface.Pausa(2000);
        return true;
    }

    public static void aplicarVenenoCuraParaMorte(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex) {
        if (!ficha.isCuraParaMorteAtivo() || ficha.getAlvoCuraParaMorte() == null) return;
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) return;

        Criatura alvo = inimigos.get(alvoIndex);
        if (alvo.getVida() <= 0 || alvo != ficha.getAlvoCuraParaMorte()) return;

        int dadoVeneno = ficha.upgradeDadoFruta(8);
        int veneno = MecanicasRpg.rolarDado(dadoVeneno) + MecanicasRpg.rolarDado(dadoVeneno) + MecanicasRpg.rolarDado(dadoVeneno);
        aplicarDanoCriatura(alvo, veneno);
        Interface.MostrarMensagem("(Cura para a Morte! O líquido mortal causa " + veneno + " de dano)");
        Interface.Pausa(1500);
    }

    public static boolean usarPactoMortal(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex, habilidades.Habilidade hab) {
        if (!ficha.isOlhoDemonicoFundido()) {
            Interface.ExibirErro("Você precisa ter aceitado o Olho Demoníaco para usar o Pacto Mortal!");
            Interface.Pausa(1500);
            return true;
        }
        if (alvoIndex < 0 || alvoIndex >= inimigos.size()) {
            Interface.MostrarMensagem("Nenhum alvo escolhido.");
            Interface.Pausa(1500);
            return true;
        }
        Criatura alvo = inimigos.get(alvoIndex);
        ficha.setManaPersonagem(ficha.getManaPersonagem() - custoEfetivoMagia(ficha, hab));
        ficha.setPactoMortalAtivo(true);
        alvo.setEnfraquecido(true);
        Interface.MostrarMensagem("\nSeu olho demoníaco se volta para " + rotuloCriatura(inimigos, alvo) + " e o Pacto Mortal é selado!");
        Interface.MostrarMensagem("Até o fim do combate: " + alvo.getNome() + " tem -2 nas rolagens e +5 de dano demoníaco, mas VOCÊ sofre +3 em todo dano.");
        Interface.Pausa(2000);
        return true;
    }

    public static boolean usarReiDasCriaturas(FichaRpg ficha, List<Criatura> inimigos) {
        if (!ficha.temItem("Coroa do Rei")) {
            Interface.ExibirErro("Você precisa da Coroa do Rei para isso!");
            Interface.Pausa(1500);
            return true;
        }
        if (ficha.getManaPersonagem() < 3) {
            Interface.ExibirErro("Mana insuficiente! O Rei das Criaturas custa 3 de mana.");
            Interface.Pausa(1500);
            return true;
        }
        List<Criatura> vivos = inimigosVivos(inimigos);
        if (vivos.isEmpty()) return true;

        int alvoIndex = escolherAlvo(inimigos);
        if (alvoIndex < 0) return true;
        Criatura alvo = inimigos.get(alvoIndex);

        ficha.setManaPersonagem(ficha.getManaPersonagem() - 3);

        Interface.pressionarParaTeste("Presença (Rei das Criaturas)");
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int totalJogador = dadoJogador + ficha.getPresencaTeste();
        int dadoCriatura = MecanicasRpg.rolarDado(20);
        int totalCriatura = dadoCriatura + alvo.getNivel();
        Interface.MostrarMensagem("-> Você: " + dadoJogador + " + " + ficha.getPresencaTeste() + " (Presença) = " + totalJogador
                + "  |  " + alvo.getNome() + ": " + dadoCriatura + " + " + alvo.getNivel() + " (Nível) = " + totalCriatura);
        Interface.Pausa(3000);

        if (totalJogador <= totalCriatura) {
            Interface.MostrarMensagem("\n" + alvo.getNome() + " resiste à sua vontade e não obedece o comando.");
            Interface.Pausa(2000);
            return true;
        }

        Interface.MostrarMensagem(VERDE + "\n" + alvo.getNome() + " curva-se à sua presença!" + RESET);
        Interface.Pausa(1500);

        List<String> comandos = new ArrayList<>();
        comandos.add("Fugir do combate");
        comandos.add("Atacar a si mesma");
        if (outrosVivos(inimigos, alvo).size() > 0) {
            comandos.add("Atacar outro monstro");
        }

        System.out.println("\n  Qual comando você dá a " + alvo.getNome() + "?\n");
        for (int i = 0; i < comandos.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + comandos.get(i));
        }
        int escolha = Interface.lerOpcao(comandos.size());

        if (escolha == 1) {
            alvo.setFugiu(true);
            Interface.MostrarMensagem("\n" + alvo.getNome() + " recua aterrorizado e desaparece nos corredores, fugindo do combate!");
            Interface.Pausa(2000);
        } else if (escolha == 2) {
            Interface.MostrarMensagem("\nAo seu comando, " + alvo.getNome() + " se volta contra si mesmo!");
            Interface.Pausa(1500);
            criaturaAtacaCriatura(alvo, alvo, inimigos);
        } else {
            List<Criatura> outros = outrosVivos(inimigos, alvo);
            Criatura outro = outros.get(MecanicasRpg.rolarDado(outros.size()) - 1);
            Interface.MostrarMensagem("\nAo seu comando, " + alvo.getNome() + " avança sobre " + rotuloCriatura(inimigos, outro) + "!");
            Interface.Pausa(1500);
            criaturaAtacaCriatura(alvo, outro, inimigos);
        }
        return true;
    }
}
