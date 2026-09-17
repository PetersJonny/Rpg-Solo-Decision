package classes;

import fichas.FichaRpg;
import itens.Arma;
import itens.Armadura;

public class Guerreiro extends ClasseRpg {
    
    public Guerreiro() {
        this.nome = "Guerreiro";
        this.armaPrincipal = new Arma("Espada", "Uma espada de aço afiada que causa 1d8 de dano.", "CaC", 8, 1, 1);
        this.itensIniciais.add(this.armaPrincipal);
        this.itensIniciais.add(new Armadura("Armadura Leve", "Oferece proteção básica para combate. Concede +3 de Defesa.", 3, 1));
        
        // Habilidade Base
        this.habilidadesIniciais.add(new habilidades.Habilidade("Casca Grossa", "Endurece a pele, diminuindo 5 de dano recebido de um ataque.", 1, true));
        
        // Habilidades por nível
        this.habilidadesPorNivel.put(3, java.util.List.of(
            new habilidades.ativas.HabilidadePesoDaEspada("Peso da Espada", "Usa o peso de sua própria espada para abater o inimigo, causando 3d8 + Força de dano.", 2, 3, 8)
        ));

        this.escolhasNivel.put(5, java.util.List.of(
            new habilidades.ativas.HabilidadeGiro("Giro", "Usa sua destreza para girar e atacar em área. Gasta 1 de mana por giro (máximo igual à sua Destreza), causando 1d10 + Força de dano em área por giro.", 1),
            new habilidades.Habilidade("Espada Afiada", "Sua espada ganha +2d8 de dano em todos os ataques durante o combate.", 3, true)
        ));

        this.escolhasNivel.put(7, java.util.List.of(
            new habilidades.Habilidade("Defesa Absoluta", "Passiva sempre ativa: você ganha +5 de defesa até atacar. Quando atacar, o efeito desaparece.", 0, true),
            new habilidades.ativas.HabilidadeEstrondo("Estrondo", "Gasta 5 de mana para golpear o chão com a arma, levantando terra e acertando todos os inimigos com 7d10 + Força de dano. Você não pode usar nenhuma habilidade no próximo turno.", 5)
        ));

        this.escolhasNivel.put(9, java.util.List.of(
            new habilidades.ativas.HabilidadeSemiDeus("Semi Deus", "Gasta TODA a sua mana para se transformar em um semi-deus: sua vida máxima aumenta em 50%, você recupera toda a vida e seus ataques corpo a corpo ganham +4 dados de dano. Dura até o fim do combate.", 0)
        ));

        this.escolhasNivel.put(10, java.util.List.of(
            new habilidades.Habilidade("Deus", "Você se torna naturalmente poderoso, sem gastar nada: a forma de Semi Deus fica sempre ativa (+50% de vida máxima e +4 dados de dano corpo a corpo) e você ganha a habilidade Cura Incessante, que cura toda a sua vida (uma vez por combate).", 0, true)
        ));
    }

    @Override
    public int calcularVidaBase(int constituicaoBase) { return 20 + constituicaoBase; }

    @Override
    public int calcularManaBase(int presencaBase) { return 2 + presencaBase; }

    @Override
    public void aplicarBonusNivel(FichaRpg ficha) {
        int ganhoVida = 5 + ficha.getConstituicao();
        ficha.setVidaMaxima(ficha.getVidaMaxima() + ganhoVida);
        ficha.setVidaPersonagem(ficha.getVidaPersonagem() + ganhoVida);
        int ganhoMana = 1 + ficha.getPresenca();
        ficha.setManaMaxima(ficha.getManaMaxima() + ganhoMana);
        ficha.setManaPersonagem(ficha.getManaPersonagem() + ganhoMana);
    }

    @Override
    public int getBonusConstituicao() { return 2; }

    @Override
    public int getBonusForca() { return 1; }

    @Override
    public int getBonusIntelecto() { return -2; }
}
