package fichas;

import classes.ClasseRpg;
import itens.Arma;
import itens.ItemRpg;
import telas.Interface;
import racas.Raca;
import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;

public class FichaRpg implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    // Identificação
    private String nomePersonagem = "Desconhecido", nomePessoa;
    
    // Status de Sobrevivência
    private int nivel = 1;
    private int xp = 0;
    private int ouro = 0;
    private int vidaPersonagem, manaPersonagem;
    private int vidaMaxima, manaMaxima;


    // Encontros
    private boolean fadaEncontrada = false;
    
    // Atributos Base
    private int constituicaoBase, destrezaBase, forcaBase, sabedoriaBase, intelectoBase, presencaBase;

    // Raça (gera +1 num atributo e uma passiva). Humano escolhe o atributo.
    private racas.Raca raca = null;
    private String atributoRacialHumano = null; // "+1" escolhido pelo Humano (ex.: "Força")

    // Passivas diárias (resetadas a cada novo dia)
    private boolean sobrevivenciaUsada = false; // Vontade de Viver (Humano): 1x/dia
    private boolean menteAfiadaUsada = false;  // Mente Afiada (Gnomo): 1x/dia
    
    // Atributos Finais (Base + Modificadores)
    private int constituicao, destreza, forca, sabedoria, intelecto, presenca;
    
    // Classe
    private ClasseRpg classeDoPersonagem = null;
    
    // Defesa
    private int defesa, bonusDeDefesa;

    // Equipamento e Inventário
    private Arma armaEquipada;
    private itens.Armadura armaduraEquipada;
    private List<ItemRpg> inventario = new ArrayList<>();
    
    // Habilidades
    private List<habilidades.Habilidade> habilidades = new ArrayList<>();

    // Efeitos temporários de combate
    private boolean espadaAfiadaAtiva;
    private boolean protecaoAbsolutaAtiva;
    private int bonusDefesaTemporario;
    private boolean curaParaMortePreparado;
    private boolean curaParaMorteAtivo;
    private criaturas.Criatura alvoCuraParaMorte;
    private boolean curaTotalUsada;
    private boolean infectado; // infecção zumbi: 1d4 de dano por rodada de combate

    // Efeitos lvl 7
    private boolean defesaAbsolutaAtiva;
    private int rodadasSemHabilidade;
    private boolean magiaProibidaUsada;
    private boolean magiaProibidaAtiva;
    private criaturas.Criatura prisaoAtiva;

    // Efeitos lvl 9
    private boolean semiDeusAtivo;
    private int semiDeusVidaOriginalMax;
    private boolean poderAbsolutoAtivo;
    private int curaAbsolutaBonus;
    private int curaAbsolutaVidaOriginalMax;

    // Efeitos lvl 10
    private boolean deusAtivo;
    private boolean curaIncessanteUsada;
    private boolean conhecimentoAbsolutoAplicado;

    // Efeitos de tempo (dia/noite) e abrigo
    private boolean ehNoite = false;
    private int progressoPeriodo = 0;
    private int diaAtual = 1; // Dia 1, Noite 1, Dia 2, Noite 2, ...
    private int diasSemDormir = 0;
    private boolean cansado = false;
    private boolean temCabana = false;
    private boolean naCabana = false;

    // Companheiro (pessoa perdida que o jogador acolheu)
    private companheiros.Companheiro companheiro = null;

    // Modo de dificuldade e slot de save vinculado à partida
    private ModoDificuldade modoDificuldade = ModoDificuldade.NORMAL;
    private int slotAtual = 0; // 0 = nenhum save vinculado

    // Sala de Treino
    private boolean temSalaTreino = false;
    private boolean naSalaTreino = false;
    private boolean salaJuntoCabana = false; // se a sala foi construída enquanto se estava na cabana
    private String treinoBonusAtributo = null; // "Força" ou "Destreza"
    private int treinoBonusPeriodosRestantes = 0;

    // Mesa de Magias
    private boolean temMesaMagias = false;
    private int magiaBonusPeriodosRestantes = 0;
    private boolean naMesaMagias = false; // se o jogador está junto da mesa
    private boolean mesaJuntoCabana = false; // se a mesa foi construída estando na cabana
    private boolean mesaJuntoSala = false; // se a mesa foi construída estando na sala de treino
    private boolean salaJuntoMesa = false; // se a sala foi construída estando na mesa de magias

    // Profundidade da travessia em que cada construção foi montada (o "ponto" dela).
    // Montar de novo em outro lugar move o ponto; a distância entre duas construções
    // é a diferença entre as profundidades onde cada uma está.
    private int profundidadeCabana = 0;
    private int profundidadeSalaTreino = 0;
    private int profundidadeMesaMagias = 0;

    // Estruturas encontradas na floresta (só podem ser descobertas explorando)
    private boolean labirintoEncontrado = false;
    private estruturas.Labirinto labirinto = null; // grade salva junto da ficha

    // Travessia para fora da floresta: profundidade oculta ao jogador.
    // 0 = perto das construções; 20 = fora da floresta (em uma cidade).
    private int profundidadeFloresta = 0;

    public static final int PROFUNDIDADE_PARA_SAIR = 20;

    // Cidade para além da floresta onde o andarilho parou (sorteada na chegada; null = ainda não saiu)
    private String cidadeAtual = null;

    // Tesouros raros do Labirinto (cada um só pode ser encontrado 1 vez)
    private boolean olhoDemonicoEncontrado = false;
    private boolean espadaMajestralEncontrada = false;
    private boolean coroaReiEncontrada = false;

    // Coroa do Rei: quem decifra seu segredo (teste de Intelecto 18+) vira o Rei das Criaturas
    private boolean reiDasCriaturas = false;

    // Pacto Mortal (Olho Demoníaco): ativo até o fim do combate — você sofre +3 em todo dano
    private boolean pactoMortalAtivo = false;

    // Minotauro: ao ser derrotado no coração do labirinto, ele não aparece de novo
    private boolean minotauroDerrotado = false;

    // Construtor
    public FichaRpg(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public void setNomePersonagem(String nomePersonagem) {
        this.nomePersonagem = nomePersonagem;
    }

    // Distribuição de Pontos
    public void adicionarAtributo(int opcao, int pontos) {
        switch (opcao) {
            case 1: constituicaoBase += pontos; break;
            case 2: destrezaBase += pontos; break;
            case 3: forcaBase += pontos; break;
            case 4: sabedoriaBase += pontos; break;
            case 5: intelectoBase += pontos; break;
            case 6: presencaBase += pontos; break;
        }
    }

    public void setClasse(ClasseRpg classe) {
        this.classeDoPersonagem = classe;
        aplicarBonus();
    }

    public void resetarPontosBase() {
        this.constituicaoBase = 0;
        this.presencaBase = 0;
        this.destrezaBase = 0;
        this.sabedoriaBase = 0;
        this.intelectoBase = 0;
        this.forcaBase = 0;
        aplicarBonus();
    }

    // Aplicação de Modificadores e Equipamentos Iniciais
    public void aplicarBonus() {
        this.constituicao = constituicaoBase;
        this.destreza = destrezaBase;
        this.forca = forcaBase;
        this.sabedoria = sabedoriaBase;
        this.intelecto = intelectoBase;
        this.presenca = presencaBase;
        
        if (classeDoPersonagem == null) {
            this.vidaPersonagem = 0;
            this.manaPersonagem = 0;
            // Raça ainda dá atributos/defesa mesmo sem classe escolhida
            if (raca != null) {
                this.constituicao += raca.getBonusConstituicao();
                this.forca += raca.getBonusForca();
                this.destreza += raca.getBonusDestreza();
                this.sabedoria += raca.getBonusSabedoria();
                this.intelecto += raca.getBonusIntelecto();
                this.presenca += raca.getBonusPresenca();
            }
            this.defesa = 10 + this.destreza + bonusDeDefesa + (raca != null ? raca.getBonusDefesa() : 0);
            return;
        }

        this.vidaPersonagem = classeDoPersonagem.calcularVidaBase(this.constituicaoBase);
        this.manaPersonagem = classeDoPersonagem.calcularManaBase(this.presencaBase);
        this.vidaMaxima = this.vidaPersonagem;
        this.manaMaxima = this.manaPersonagem;
        
        this.constituicao += classeDoPersonagem.getBonusConstituicao();
        this.forca += classeDoPersonagem.getBonusForca();
        this.destreza += classeDoPersonagem.getBonusDestreza();
        this.sabedoria += classeDoPersonagem.getBonusSabedoria();
        this.intelecto += classeDoPersonagem.getBonusIntelecto();
        this.presenca += classeDoPersonagem.getBonusPresenca();
        
        // Bônus racial (+1 no atributo da raça; defesa/vida extras p/ Dracônico)
        if (raca != null) {
            this.constituicao += raca.getBonusConstituicao();
            this.forca += raca.getBonusForca();
            this.destreza += raca.getBonusDestreza();
            this.sabedoria += raca.getBonusSabedoria();
            this.intelecto += raca.getBonusIntelecto();
            this.presenca += raca.getBonusPresenca();
        }
        
        // Bônus racial (+1 no atributo da raça + bônus permanentes de defesa/vida)
        this.defesa = 10 + this.destreza + bonusDeDefesa + (raca != null ? raca.getBonusDefesa() : 0);
        this.vidaMaxima += (raca != null ? raca.getBonusVidaMaxima() : 0);

        // Ficha ganha a arma e os itens da classe
        this.armaEquipada = classeDoPersonagem.getArmaPrincipal();
        this.inventario = new ArrayList<>(classeDoPersonagem.getItensIniciais());
        
        // Ficha ganha as habilidades da classe
        this.habilidades = new ArrayList<>(classeDoPersonagem.getHabilidadesIniciais());

        // Equipa a melhor armadura do inventário (as demais ficam na mochila)
        if (armaduraEquipada != null) {
            inventario.add(armaduraEquipada);
            armaduraEquipada = null;
        }
        equiparMelhorArmadura();
    }

    // Validador de Ficha
    public boolean isFichaCompleta() {
        if (nomePersonagem.equals("Desconhecido") || nomePersonagem.trim().isEmpty()) {
            return false;
        }
        if (classeDoPersonagem == null) {
            return false;
        }
        int totalAtributosBase = constituicaoBase + destrezaBase + forcaBase + sabedoriaBase + intelectoBase + presencaBase;
        if (totalAtributosBase < 6) {
            return false;
        }
        return true;
    }

    // Getters
    public String getNomePersonagem() { return nomePersonagem; }
    public String getNomePessoa() { return nomePessoa; }

    // Modo de dificuldade (com fallback para saves antigos que não tinham o campo)
    public ModoDificuldade getModoDificuldade() {
        return modoDificuldade == null ? ModoDificuldade.NORMAL : modoDificuldade;
    }
    public void setModoDificuldade(ModoDificuldade modoDificuldade) { this.modoDificuldade = modoDificuldade; }
    public boolean isModoDificil() { return getModoDificuldade() == ModoDificuldade.DIFICIL; }

    public int getSlotAtual() { return slotAtual; }
    public void setSlotAtual(int slotAtual) { this.slotAtual = slotAtual; }
    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }
    public int getOuro() { return ouro; }
    public int getVidaPersonagem() { return vidaPersonagem; }
    public int getManaPersonagem() { return manaPersonagem; }
    public int getConstituicao() { return constituicao; }
    public int getDestreza() { return destreza + ("Destreza".equals(treinoBonusAtributo) ? 2 : 0); }
    public int getForca() { return forca + ("Força".equals(treinoBonusAtributo) ? 2 : 0); }
    public int getSabedoria() { return sabedoria; }
    public int getIntelecto() { return intelecto; }
    public int getPresenca() { return presenca; }
    public int getDefesa() { return defesa + (armaduraEquipada != null ? armaduraEquipada.getBonusDefesa() : 0) + bonusDefesaTemporario + (defesaAbsolutaAtiva ? 5 : 0); }
    public itens.Armadura getArmaduraEquipada() { return armaduraEquipada; }
    public ClasseRpg getClasseDoPersonagem() { return classeDoPersonagem; }
    public racas.Raca getRaca() { return raca; }
    public void setRaca(racas.Raca raca) { this.raca = raca; }

    // Passivas diárias (1x por dia): Vontade de Viver (Humano) e Mente Afiada (Gnomo)
    public boolean isSobrevivenciaUsada() { return sobrevivenciaUsada; }
    public void marcarSobrevivenciaUsada() { this.sobrevivenciaUsada = true; }
    public boolean isMenteAfiadaUsada() { return menteAfiadaUsada; }
    public void marcarMenteAfiadaUsada() { this.menteAfiadaUsada = true; }
    public boolean podeUsarMenteAfiada() { return raca != null && raca.podeRerrolarTeste() && !menteAfiadaUsada; }
    public Arma getArmaEquipada() { return armaEquipada; }
    public List<ItemRpg> getInventario() { return inventario; }
    public List<habilidades.Habilidade> getHabilidades() { return habilidades; }
    public boolean isFadaEncontrada() { return fadaEncontrada; }
    public void setFadaEncontrada(boolean fadaEncontrada) { this.fadaEncontrada = fadaEncontrada; }

    // Setters de Combate
    public void setVidaPersonagem(int vida) { this.vidaPersonagem = Math.max(0, Math.min(vida, vidaMaxima)); }
    public void setManaPersonagem(int mana) { this.manaPersonagem = Math.max(0, Math.min(mana, manaMaxima)); }

    // Getters de Máximo
    public int getVidaMaxima() { return vidaMaxima; }
    public int getManaMaxima() { return manaMaxima; }

    // Setters de Máximo (usados no level up)
    public void setVidaMaxima(int vidaMaxima) { this.vidaMaxima = vidaMaxima; }
    public void setManaMaxima(int manaMaxima) { this.manaMaxima = manaMaxima; }

    // Dinheiro
    public void adicionarOuro(int quantidade) { 
        if (quantidade <= 0) return;
        if (Integer.MAX_VALUE - this.ouro < quantidade) {
            this.ouro = Integer.MAX_VALUE;
        } else {
            this.ouro += quantidade;
        }
    }

    // Tenta gastar ouro; retorna false se não tiver o suficiente
    public boolean gastarOuro(int quantidade) {
        if (quantidade < 0 || this.ouro < quantidade) {
            return false;
        }
        this.ouro -= quantidade;
        return true;
    }

    // XP necessária para subir do nível atual para o próximo
    public static int getXpNecessaria(int nivel) {
        switch (nivel) {
            case 1: return 100;
            case 2: return 300;
            case 3: return 700;
            case 4: return 1500;
            case 5: return 3500;
            case 6: return 8000;
            case 7: return 15000;
            case 8: return 40000;
            case 9: return 100000;
            default: return -1; // Nível 10 é o máximo
        }
    }

    // Adiciona XP e trata os up's de nível; retorna quantos níveis foram ganhos
    public int adicionarXp(int quantidade) {
        this.xp += Math.max(0, quantidade);
        int niveisGanhos = 0;
        while (nivel < 10) {
            int necessaria = getXpNecessaria(nivel);
            if (xp >= necessaria) {
                xp -= necessaria; // Antes era xp = 0 (bug que sumia com XP excedente)
                nivel++;
                niveisGanhos++;
                // Aplica bônus de vida e mana da classe
                if (classeDoPersonagem != null) {
                    classeDoPersonagem.aplicarBonusNivel(this);
                    // Ganha as habilidades do nível alcançado
                    classeDoPersonagem.aplicarHabilidadesNivel(this, nivel);
                }
            } else {
                break;
            }
        }
        return niveisGanhos;
    }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    // Adicionar item ao inventário, empilhando se já existir
    public void adicionarItem(ItemRpg novoItem) {
        for (ItemRpg existente : inventario) {
            if (existente.getNome().equals(novoItem.getNome())) {
                existente.setQuantidade(existente.getQuantidade() + novoItem.getQuantidade());
                return;
            }
        }
        inventario.add(novoItem);
    }

    // ==================== ESPAÇO DA MOCHILA ====================

    // Capacidade de carga da mochila: 10 + 5 por ponto de Força
    public double getCapacidadeMochila() {
        return 10 + 5.0 * getForca();
    }

    // Peso total carregado (soma do peso de cada unidade do inventário)
    public double getPesoTotalMochila() {
        double total = 0;
        for (ItemRpg item : inventario) {
            total += item.getPeso() * item.getQuantidade();
        }
        return total;
    }

    // Quanto de espaço ainda resta na mochila
    public double getEspacoLivreMochila() {
        return getCapacidadeMochila() - getPesoTotalMochila();
    }

    // Tenta adicionar o item se houver espaço; retorna false e NÃO adiciona se estourar
    public boolean tentarAdicionarItem(ItemRpg novoItem) {
        double espacoNecessario = novoItem.getPeso() * novoItem.getQuantidade();
        double espacoLivre = getEspacoLivreMochila();
        if (espacoNecessario <= espacoLivre + 0.0001) {
            adicionarItem(novoItem);
            return true;
        }
        return false;
    }

    // Adiciona apenas a quantidade que couber na mochila (o item tem sua quantidade
    // reduzida ao que foi guardado); retorna quantas unidades foram pegas.
    public int adicionarItemLimitado(ItemRpg novoItem) {
        double espacoLivre = getEspacoLivreMochila();
        double pesoUnit = novoItem.getPeso();
        int qtd = novoItem.getQuantidade();
        if (pesoUnit <= 0 || espacoLivre <= 0) return 0;
        int qtdCabe = (int) Math.floor(espacoLivre / pesoUnit);
        int qtdPegar = Math.min(qtd, qtdCabe);
        if (qtdPegar <= 0) return 0;
        novoItem.setQuantidade(qtdPegar);
        adicionarItem(novoItem);
        return qtdPegar;
    }

    // Pergunta se o jogador quer pegar o item achado e quantos, respeitando o espaço
    // da mochila. O que não couber ou for recusado fica para trás.
    public void coletarItemEncontrado(ItemRpg item, String origem) {
        double pesoUnit = item.getPeso();
        double espacoLivre = getEspacoLivreMochila();
        int qtd = item.getQuantidade();

        int cabemDeFato = (pesoUnit > 0) ? (int) Math.floor(espacoLivre / pesoUnit) : qtd;
        if (cabemDeFato < 0) cabemDeFato = 0;
        cabemDeFato = Math.min(qtd, cabemDeFato);

        if (cabemDeFato <= 0) {
            Interface.MostrarMensagem(origem + " " + qtd + "x " + item.getNome() + ", mas não há espaço na mochila! (Peso: " + String.format("%.1f", pesoUnit) + " cada, livre: " + String.format("%.1f", espacoLivre) + ")");
            Interface.Pausa(1500);
            return;
        }

        Interface.MostrarMensagem("-> " + origem + " " + qtd + "x " + item.getNome() + " (peso " + String.format("%.1f", pesoUnit) + " cada, espaço livre: " + String.format("%.1f", espacoLivre) + "/" + String.format("%.1f", getCapacidadeMochila()) + ").");
        Interface.Pausa(800);

        System.out.println("  Deseja pegar?");
        System.out.println("  1. Pegar tudo (" + cabemDeFato + "x)");
        System.out.println("  2. Escolher a quantidade");
        System.out.println("  3. Deixar para trás");
        int escolha = Interface.lerOpcao(3);

        int qtdPegar;
        if (escolha == 1) {
            qtdPegar = cabemDeFato;
        } else if (escolha == 2) {
            System.out.println("  Quantidade (1 a " + cabemDeFato + "):");
            int qtdEscolhida = Interface.lerInteiro();
            qtdPegar = Math.min(Math.max(0, qtdEscolhida), cabemDeFato);
            if (qtdPegar <= 0) {
                Interface.MostrarMensagem("-> Você não pegou nada.");
                Interface.Pausa(1000);
                return;
            }
        } else {
            Interface.MostrarMensagem("-> Você deixou " + item.getNome() + " para trás.");
            Interface.Pausa(1000);
            return;
        }

        item.setQuantidade(qtdPegar);
        adicionarItem(item);
        Interface.MostrarMensagem("-> Você coletou " + qtdPegar + "x " + item.getNome() + " (peso: " + String.format("%.1f", pesoUnit * qtdPegar) + "/" + String.format("%.1f", getCapacidadeMochila()) + ").");
        Interface.Pausa(1500);
    }

    // Remove itens do inventário; se a arma equipada for vendida, ela é desequipada.
    public boolean removerItem(String nome, int quantidade) {
        for (ItemRpg item : inventario) {
            if (item.getNome().equals(nome)) {
                int atual = item.getQuantidade();
                int remover = Math.min(atual, quantidade);
                if (atual - remover <= 0) {
                    inventario.remove(item);
                    if (armaEquipada != null && armaEquipada.getNome().equals(nome)) {
                        armaEquipada = null;
                    }
                    if (armaduraEquipada != null && armaduraEquipada.getNome().equals(nome)) {
                        armaduraEquipada = null;
                        equiparMelhorArmadura();
                    }
                } else {
                    item.setQuantidade(atual - remover);
                }
                return true;
            }
        }
        return false;
    }

    // Consome unidades de um item (usar poções, flechas, kit médico...).
    // Conteúdo compartilhado entre o menu de inventário e o combate.
    public void consumirItem(ItemRpg item, int quantidade) {
        if (item != null) {
            item.setQuantidade(item.getQuantidade() - Math.max(0, quantidade));
        }
    }

    // Equipa a armadura de maior bônus do inventário; a que estava equipada volta para a mochila
    public void equiparMelhorArmadura() {
        itens.Armadura melhor = null;
        for (ItemRpg item : new ArrayList<>(inventario)) {
            if (item instanceof itens.Armadura) {
                itens.Armadura arm = (itens.Armadura) item;
                if (melhor == null || arm.getBonusDefesa() > melhor.getBonusDefesa()) {
                    melhor = arm;
                }
            }
        }
        if (melhor != null) {
            if (armaduraEquipada != null) {
                inventario.add(armaduraEquipada);
            }
            inventario.remove(melhor);
            armaduraEquipada = melhor;
        }
    }

    // Verifica se o jogador possui um item (com quantidade) no inventário
    public boolean temItem(String nome) {
        if (inventario == null) return false;
        for (ItemRpg item : inventario) {
            if (item.getNome().equals(nome) && item.getQuantidade() > 0) {
                return true;
            }
        }
        return false;
    }

    // Bônus aleatório de atributo (concedido pela Fada)
    public String aumentarAtributoAleatorio() {
        int sorteado = MecanicasRpg.rolarDado(6);
        switch (sorteado) {
            case 1: aumentarConstituicao(1); return "Constituição";
            case 2: destreza++; return "Destreza";
            case 3: forca++; return "Força";
            case 4: sabedoria++; return "Sabedoria";
            case 5: intelecto++; return "Intelecto";
            default: presenca++; return "Presença";
        }
    }

    // Aumenta o atributo escolhido (ponto de atributo ganho no level up)
    public String aumentarAtributo(int opcao) {
        switch (opcao) {
            case 1: aumentarConstituicao(1); return "Constituição";
            case 2: destreza++; return "Destreza";
            case 3: forca++; return "Força";
            case 4: sabedoria++; return "Sabedoria";
            case 5: intelecto++; return "Intelecto";
            default: presenca++; return "Presença";
        }
    }

    // Aumenta a Constituição aplicando retroativo de vida para TODOS os níveis já ganhos:
    // cada ponto extra de Constituição deveria ter dado +1 de vida em cada level up passado.
    public void aumentarConstituicao(int quantidade) {
        constituicao += quantidade;
        if (quantidade > 0 && nivel > 1) {
            int vidaRetroativa = quantidade * (nivel - 1);
            vidaMaxima += vidaRetroativa;
            vidaPersonagem = Math.min(vidaPersonagem + vidaRetroativa, vidaMaxima);
        }
    }

    // Conhecimento Absoluto (Healer lvl 10): +quantidade em TODOS os atributos
    public void aumentarTodosAtributos(int quantidade) {
        destreza += quantidade;
        forca += quantidade;
        sabedoria += quantidade;
        intelecto += quantidade;
        presenca += quantidade;
        defesa += quantidade;
        aumentarConstituicao(quantidade);
    }

    // ==================== TEMPO (DIA/NOITE) E ABRIGO ====================

    public boolean isEhNoite() { return ehNoite; }
    public int getProgressoPeriodo() { return progressoPeriodo; }
    public int getDiaAtual() { return diaAtual; }
    public String getPeriodoDescritivo() { return (ehNoite ? "Noite " : "Dia ") + diaAtual; }
    public String getPeriodoDescritivoMaiusculo() { return (ehNoite ? "NOITE " : "DIA ") + diaAtual; }
    public int getDiasSemDormir() { return diasSemDormir; }
    public boolean isCansado() { return cansado; }
    public boolean isTemCabana() { return temCabana; }
    public boolean isNaCabana() { return naCabana; }
    public boolean temCompanheiro() { return companheiro != null; }
    public companheiros.Companheiro getCompanheiro() { return companheiro; }
    public void setCompanheiro(companheiros.Companheiro companheiro) { this.companheiro = companheiro; }
    public void removerCompanheiro() { this.companheiro = null; }
    public boolean companheiroQuerPartir() { return companheiro != null && companheiro.isPartindo(); }
    public void registrarDormidaDoCompanheiro() {
        if (companheiro != null) {
            companheiro.aoDormir();
        }
    }
    public boolean isTemSalaTreino() { return temSalaTreino; }
    public boolean isNaSalaTreino() { return naSalaTreino; }
    public boolean isSalaJuntoCabana() { return salaJuntoCabana; }
    public String getTreinoBonusAtributo() { return treinoBonusAtributo; }
    public int getTreinoBonusPeriodosRestantes() { return treinoBonusPeriodosRestantes; }
    public boolean isTemMesaMagias() { return temMesaMagias; }
    public int getMagiaBonusPeriodosRestantes() { return magiaBonusPeriodosRestantes; }
    public boolean isMagiaBonusAtivo() { return magiaBonusPeriodosRestantes > 0; }
    public boolean isNaMesaMagias() { return naMesaMagias; }
    public boolean isMesaJuntoCabana() { return mesaJuntoCabana; }
    public boolean isMesaJuntoSala() { return mesaJuntoSala; }
    public boolean isSalaJuntoMesa() { return salaJuntoMesa; }

    public int getProfundidadeCabana() { return profundidadeCabana; }
    public int getProfundidadeSalaTreino() { return profundidadeSalaTreino; }
    public int getProfundidadeMesaMagias() { return profundidadeMesaMagias; }

    // Distância (em períodos de caminhada) entre o ponto atual e uma profundidade qualquer.
    public int getDistanciaAte(int profundidadeAlvo) {
        return Math.abs(profundidadeFloresta - profundidadeAlvo);
    }

    // Localização (id) em que o jogador está: 0 = ponto da cabana, 1 = ponto da sala
    // de treino, 2 = ponto da mesa de magias, 3 = meio da mata. Duas estruturas
    // montadas na MESMA profundidade ficam no mesmo ponto.
    public int getLocalizacaoAtual() {
        if (naCabana) return 0;
        if (naSalaTreino) return getLocalizacaoSala();
        if (naMesaMagias) return getLocalizacaoMesa();
        return 3;
    }

    // Ponto onde a sala de treino fica (0 = ponto da cabana; 2 = ponto da mesa; 1 = ponto próprio).
    public int getLocalizacaoSala() {
        if (!temSalaTreino) return 1;
        if (profundidadeSalaTreino == profundidadeCabana) return 0;
        if (profundidadeSalaTreino == profundidadeMesaMagias) return 2;
        return 1;
    }

    // Ponto onde a mesa de magias fica (0 = ponto da cabana; 2 = ponto próprio ou da sala).
    public int getLocalizacaoMesa() {
        if (!temMesaMagias) return 2;
        if (profundidadeMesaMagias == profundidadeCabana) return 0;
        return 2;
    }

    // Uma construção só pode ser usada quando o jogador está no ponto dela
    // (o ponto em que ela foi montada).
    public boolean podeUsarCabana() { return temCabana && getLocalizacaoAtual() == 0; }
    public boolean podeUsarSalaTreino() { return temSalaTreino && getLocalizacaoAtual() == getLocalizacaoSala(); }
    public boolean podeUsarMesaMagias() { return temMesaMagias && getLocalizacaoAtual() == getLocalizacaoMesa(); }

    public boolean isLabirintoEncontrado() { return labirintoEncontrado; }
    public void setLabirintoEncontrado(boolean labirintoEncontrado) { this.labirintoEncontrado = labirintoEncontrado; }

    public estruturas.Labirinto getLabirinto() { return labirinto; }
    public void setLabirinto(estruturas.Labirinto labirinto) { this.labirinto = labirinto; }

    public boolean isOlhoDemonicoEncontrado() { return olhoDemonicoEncontrado; }
    public void setOlhoDemonicoEncontrado(boolean olhoDemonicoEncontrado) { this.olhoDemonicoEncontrado = olhoDemonicoEncontrado; }

    public boolean isEspadaMajestralEncontrada() { return espadaMajestralEncontrada; }
    public void setEspadaMajestralEncontrada(boolean espadaMajestralEncontrada) { this.espadaMajestralEncontrada = espadaMajestralEncontrada; }

    public boolean isCoroaReiEncontrada() { return coroaReiEncontrada; }
    public void setCoroaReiEncontrada(boolean coroaReiEncontrada) { this.coroaReiEncontrada = coroaReiEncontrada; }

    public boolean isReiDasCriaturas() { return reiDasCriaturas; }
    public void setReiDasCriaturas(boolean reiDasCriaturas) { this.reiDasCriaturas = reiDasCriaturas; }

    public boolean isMinotauroDerrotado() { return minotauroDerrotado; }
    public void setMinotauroDerrotado(boolean minotauroDerrotado) { this.minotauroDerrotado = minotauroDerrotado; }

    public boolean isPactoMortalAtivo() { return pactoMortalAtivo; }
    public void setPactoMortalAtivo(boolean pactoMortalAtivo) { this.pactoMortalAtivo = pactoMortalAtivo; }

    // O labirinto fica acessível no menu enquanto não tiver sido concluído.
    // Ao alcançar o centro, ele desmorona, o jogador foge para a floresta e a opção some.
    public boolean isLabirintoDisponivel() {
        return labirintoEncontrado && labirinto != null && !labirinto.isCentroAlcancado();
    }

    // Chance de descobrir o Labirinto do Minotauro a cada exploração:
    // começa em 1% e aumenta +1% a cada dia que passa (dia 1 = 1%, dia 2 = 2%...), até no máximo 100%.
    public int getLabirintoChanceDescoberta() {
        return Math.min(diaAtual, 100);
    }

    // ==================== TRAVESSIA PARA FORA DA FLORESTA ====================

    // Quão longe das construções o jogador está (0 = perto delas; PROFUNDIDADE_PARA_SAIR = fora).
    // O valor é oculto para o jogador.
    public int getProfundidadeFloresta() { return profundidadeFloresta; }

    // Estar "no vilarejo" = ter atravessado a floresta inteira e saído dela.
    public boolean isNoVilarejo() { return profundidadeFloresta >= PROFUNDIDADE_PARA_SAIR; }

    public String getCidadeAtual() { return cidadeAtual; }
    public void setCidadeAtual(String cidadeAtual) { this.cidadeAtual = cidadeAtual; }

    // Aprofunda a travessia (1 unidade = 1/3 do período); no máximo sai da floresta.
    public void adicionarProfundidade(int unidades) {
        profundidadeFloresta = Math.min(PROFUNDIDADE_PARA_SAIR, profundidadeFloresta + Math.max(0, unidades));
        sincronizarLocalizacao();
    }

    // Reduz a profundidade ao voltar para as construções (nunca abaixo de 0).
    public void reduzirProfundidade(int unidades) {
        profundidadeFloresta = Math.max(0, profundidadeFloresta - Math.max(0, unidades));
        sincronizarLocalizacao();
    }

    // Ao mudar de profundidade, o jogador passa a estar "em" qualquer construção
    // que ocupa exatamente aquela profundidade (várias podem ficar juntas no mesmo ponto).
    private void sincronizarLocalizacao() {
        naCabana = temCabana && profundidadeFloresta == profundidadeCabana;
        naSalaTreino = temSalaTreino && profundidadeFloresta == profundidadeSalaTreino;
        naMesaMagias = temMesaMagias && profundidadeFloresta == profundidadeMesaMagias;
    }

    // Entra na construção que fica NO ponto indicado (distância 0): corrige o
    // estado "expulso" que acontecia quando o jogador saía da cabana (sairDaCabana)
    // ou caminhava de volta sem mudar de profundidade — o menu de construção
    // voltava a mostrar "Ir para a Cabana (0 período(s))" para sempre, e a opção
    // "Dormir" nunca mais aparecia. Agora, estando no ponto, ele volta a ficar NELA.
    public void entrarNaConstrucao(int profundidadeAlvo) {
        if (temCabana && profundidadeCabana == profundidadeAlvo) {
            naCabana = true;
        }
        if (temSalaTreino && profundidadeSalaTreino == profundidadeAlvo) {
            naSalaTreino = true;
        }
        if (temMesaMagias && profundidadeMesaMagias == profundidadeAlvo) {
            naMesaMagias = true;
        }
    }

    // Recalcula os flags de "junto": duas estruturas ficam juntas quando foram
    // montadas na MESMA profundidade (mesmo ponto da mata).
    private void recomputarAdjacencias() {
        salaJuntoCabana = temSalaTreino && temCabana && profundidadeSalaTreino == profundidadeCabana;
        salaJuntoMesa = temSalaTreino && temMesaMagias && profundidadeSalaTreino == profundidadeMesaMagias;
        mesaJuntoCabana = temMesaMagias && temCabana && profundidadeMesaMagias == profundidadeCabana;
        mesaJuntoSala = temMesaMagias && temSalaTreino && profundidadeMesaMagias == profundidadeSalaTreino;
    }

    // Sair da cabana para explorar/colher recursos (também sai da sala e da mesa)
    public void sairDaCabana() {
        if (temCabana) {
            naCabana = false;
        }
        naSalaTreino = false;
        naMesaMagias = false;
    }

    // Voltar para a cabana (custa 1/3 do período)
    public void voltarParaCabana() {
        if (temCabana) {
            naCabana = true;
        }
        naSalaTreino = false;
        naMesaMagias = false;
    }

    // Ir até a sala de treino (custa 1/3 do período): quem está nela passa a
    // estar no local da sala (que pode ser junto à cabana, se for o caso).
    public void irParaSalaTreino() {
        naSalaTreino = true;
        naMesaMagias = false;
        naCabana = salaJuntoCabana;
    }

    // Ir até a mesa de magias (custa 1/3 do período): quem está nela passa a
    // estar no local da mesa (que pode ser junto à cabana, se for o caso).
    public void irParaMesaMagias() {
        naMesaMagias = true;
        naSalaTreino = false;
        naCabana = mesaJuntoCabana;
    }

    // Avança o tempo do período (dia ou noite); a cada 3 unidades o período vira.
    // Explorar e buscar recursos consomem 1/3; montar a cabana consome 2/3.
    public boolean avancarTempo(int unidades) {
        progressoPeriodo += Math.max(0, unidades);
        boolean virou = false;
        while (progressoPeriodo >= 3) {
            progressoPeriodo -= 3;
            boolean eraNoite = ehNoite;
            ehNoite = !ehNoite;
            if (ehNoite) {
                diasSemDormir++;
            } else {
                diaAtual++;
                if (eraNoite && companheiro != null) {
                    // A noite terminou: o companheiro dormiu na cabana
                    registrarDormidaDoCompanheiro();
                }
            }
            // Passivas diárias (Vontade de Viver e Mente Afiada) renovam a cada novo dia
            sobrevivenciaUsada = false;
            menteAfiadaUsada = false;
            // Decrementa bônus de treino a cada período que se inicia
            if (treinoBonusPeriodosRestantes > 0) {
                treinoBonusPeriodosRestantes--;
                if (treinoBonusPeriodosRestantes <= 0) {
                    treinoBonusAtributo = null;
                }
            }
            // Decrementa o bônus da Mesa de Magias a cada período que se inicia
            if (magiaBonusPeriodosRestantes > 0) {
                magiaBonusPeriodosRestantes--;
            }
            virou = true;
        }
        cansado = diasSemDormir > 2;
        return virou;
    }

    // Dormir: só de noite, estando NA cabana (não adianta estando longe na floresta).
    // Recupera 1/3 da vida máxima e 1/3 da mana máxima e faz amanhecer.
    public boolean dormir() {
        if (!ehNoite) return false;
        if (!temCabana || !naCabana) return false;
        int curaVida = vidaMaxima / 3;
        int curaMana = manaMaxima / 3;
        vidaPersonagem = Math.min(vidaPersonagem + curaVida, vidaMaxima);
        manaPersonagem = Math.min(manaPersonagem + curaMana, manaMaxima);
        ehNoite = false;
        progressoPeriodo = 0;
        diaAtual++;
        diasSemDormir = 0;
        cansado = false;
        naMesaMagias = false;
        sobrevivenciaUsada = false;
        menteAfiadaUsada = false;
        registrarDormidaDoCompanheiro();
        return true;
    }

    // Montar a cabana: gasta 7 madeiras, 10 folhas e 4 pedras (só a primeira vez).
    // Retorna true se conseguiu construir.
    public boolean montarCabana() {
        if (temCabana) return false;
        if (getQuantidadeDe("Madeira") < 7 || getQuantidadeDe("Folha") < 10 || getQuantidadeDe("Pedra") < 4) {
            return false;
        }
        removerItem("Madeira", 7);
        removerItem("Folha", 10);
        removerItem("Pedra", 4);
        temCabana = true;
        profundidadeCabana = profundidadeFloresta;
        naCabana = true;
        naSalaTreino = false;
        naMesaMagias = false;
        recomputarAdjacencias();
        return true;
    }

    // Montar a sala de treino: gasta 10 madeiras, 15 folhas, 5 pedras e 4 couros.
    // Ela fica ancorada exatamente no ponto da mata onde for construída: se for
    // no mesmo ponto de outra construção, ficam JUNTO (estar em uma permite usar
    // a vizinha sem novo deslocamento); caso contrário, são pontos separados.
    public boolean construirSalaTreino() {
        if (temSalaTreino) return false;
        if (getQuantidadeDe("Madeira") < 10 || getQuantidadeDe("Folha") < 15 || getQuantidadeDe("Pedra") < 5 || getQuantidadeDe("Couro") < 4) {
            return false;
        }
        removerItem("Madeira", 10);
        removerItem("Folha", 15);
        removerItem("Pedra", 5);
        removerItem("Couro", 4);
        temSalaTreino = true;
        profundidadeSalaTreino = profundidadeFloresta;
        recomputarAdjacencias();
        naSalaTreino = true;
        naMesaMagias = false;
        return true;
    }

    // Entrar na sala de treino para treinar. Se a sala for junto da cabana,
    // o jogador continua considerado "na cabana"; caso contrário, ela fica longe.
    public void entrarSalaTreino() {
        naSalaTreino = true;
        naMesaMagias = false;
        if (salaJuntoCabana) {
            naCabana = true;
        } else {
            naCabana = false;
        }
    }

    // Aplica o bônus de treino (+2 em Força ou Destreza) que dura os 2 períodos seguintes
    public void treinarAtributo(String atributo) {
        this.treinoBonusAtributo = atributo;
        this.treinoBonusPeriodosRestantes = 2;
    }

    // Depois de treinar o período inteiro: se a sala for junto da cabana,
    // o jogador permanece na cabana; caso contrário, continua na sala.
    public void terminarTreino() {
        naMesaMagias = false;
        if (salaJuntoCabana) {
            naCabana = true;
            naSalaTreino = false;
        } else {
            naCabana = false;
            naSalaTreino = true;
        }
    }

    // Montar a mesa de magias: gasta 5 madeiras, 4 folhas, 4 pedras e 1 Pó da Fada.
    // Retorna true se conseguiu construir.
    public boolean construirMesaMagias() {
        if (temMesaMagias) return false;
        if (getQuantidadeDe("Madeira") < 5 || getQuantidadeDe("Folha") < 4 || getQuantidadeDe("Pedra") < 4 || getQuantidadeDe("Pó da Fada") < 1) {
            return false;
        }
        removerItem("Madeira", 5);
        removerItem("Folha", 4);
        removerItem("Pedra", 4);
        removerItem("Pó da Fada", 1);
        temMesaMagias = true;
        profundidadeMesaMagias = profundidadeFloresta;
        recomputarAdjacencias();
        naMesaMagias = true;
        naSalaTreino = false;
        naCabana = mesaJuntoCabana;
        return true;
    }

    // Mover (montar uma nova) construção no ponto atual, gastando a mesma matéria-prima.
    // O novo local passa a ser o ponto da construção; o antigo fica para trás.
    public boolean moverCabana() {
        if (!temCabana) return false;
        if (getQuantidadeDe("Madeira") < 7 || getQuantidadeDe("Folha") < 10 || getQuantidadeDe("Pedra") < 4) {
            return false;
        }
        removerItem("Madeira", 7);
        removerItem("Folha", 10);
        removerItem("Pedra", 4);
        profundidadeCabana = profundidadeFloresta;
        naCabana = true;
        naSalaTreino = false;
        naMesaMagias = false;
        recomputarAdjacencias();
        return true;
    }

    public boolean moverSalaTreino() {
        if (!temSalaTreino) return false;
        if (getQuantidadeDe("Madeira") < 10 || getQuantidadeDe("Folha") < 15 || getQuantidadeDe("Pedra") < 5 || getQuantidadeDe("Couro") < 4) {
            return false;
        }
        removerItem("Madeira", 10);
        removerItem("Folha", 15);
        removerItem("Pedra", 5);
        removerItem("Couro", 4);
        profundidadeSalaTreino = profundidadeFloresta;
        naSalaTreino = true;
        naMesaMagias = false;
        recomputarAdjacencias();
        naCabana = salaJuntoCabana;
        return true;
    }

    public boolean moverMesaMagias() {
        if (!temMesaMagias) return false;
        if (getQuantidadeDe("Madeira") < 5 || getQuantidadeDe("Folha") < 4 || getQuantidadeDe("Pedra") < 4 || getQuantidadeDe("Pó da Fada") < 1) {
            return false;
        }
        removerItem("Madeira", 5);
        removerItem("Folha", 4);
        removerItem("Pedra", 4);
        removerItem("Pó da Fada", 1);
        profundidadeMesaMagias = profundidadeFloresta;
        naMesaMagias = true;
        naSalaTreino = false;
        recomputarAdjacencias();
        naCabana = mesaJuntoCabana;
        return true;
    }

    // Profundidade da construção mais adiantada na travessia (a mais próxima da
    // borda da floresta). Usado quando se volta de uma cidade para as construções.
    public int getProfundidadeConstrucaoMaisProxima() {
        int p = 0;
        if (temCabana) p = Math.max(p, profundidadeCabana);
        if (temSalaTreino) p = Math.max(p, profundidadeSalaTreino);
        if (temMesaMagias) p = Math.max(p, profundidadeMesaMagias);
        return p;
    }

    // Estudar na mesa de magias (gasta o período inteiro): +1 dado de dano
    // em TODAS as habilidades de dano, valendo os 2 períodos seguintes
    public void estudarMagia() {
        this.magiaBonusPeriodosRestantes = 2;
    }

    // Total de um item no inventário (somando as pilhas)
    public int getQuantidadeDe(String nome) {
        int total = 0;
        if (inventario == null) return 0;
        for (ItemRpg item : inventario) {
            if (item.getNome().equals(nome)) {
                total += item.getQuantidade();
            }
        }
        return total;
    }

    // Getters usados em TESTES de atributo: quando cansado, -1 em testes.
    // Não afeta vida, mana, defesa nem dano.
    // Visão na Penumbra (Vigia do Crepúsculo): +2 em testes durante a noite.
    private int bonusTestesNoturnos() {
        return ehNoite && raca != null && raca.temBonusTestesNoturnos() ? 2 : 0;
    }
    public int getDestrezaTeste() { return getDestreza() - (cansado ? 1 : 0) + bonusTestesNoturnos(); }
    public int getPresencaTeste() { return presenca - (cansado ? 1 : 0) + bonusTestesNoturnos(); }
    public int getSabedoriaTeste() { return sabedoria - (cansado ? 1 : 0) + bonusTestesNoturnos(); }
    public int getForcaTeste() { return getForca() - (cansado ? 1 : 0) + bonusTestesNoturnos(); }
    public int getIntelectoTeste() { return intelecto - (cansado ? 1 : 0) + bonusTestesNoturnos(); }
    public int getConstituicaoTeste() { return constituicao - (cansado ? 1 : 0) + bonusTestesNoturnos(); }

    // Reseta os efeitos temporários antes de um novo combate
    public void resetarEfeitosCombate() {
        this.espadaAfiadaAtiva = false;
        this.protecaoAbsolutaAtiva = false;
        this.bonusDefesaTemporario = 0;
        this.curaParaMortePreparado = false;
        this.curaParaMorteAtivo = false;
        this.alvoCuraParaMorte = null;
        this.curaTotalUsada = false;
        this.infectado = false;
        this.pactoMortalAtivo = false;
        this.defesaAbsolutaAtiva = false;
        this.rodadasSemHabilidade = 0;
        this.magiaProibidaUsada = false;
        this.magiaProibidaAtiva = false;
        this.prisaoAtiva = null;
        if (this.semiDeusAtivo && !this.deusAtivo) {
            this.vidaMaxima = this.semiDeusVidaOriginalMax;
            this.vidaPersonagem = Math.min(this.vidaPersonagem, this.vidaMaxima);
            this.semiDeusAtivo = false;
        }
        this.semiDeusVidaOriginalMax = 0;
        if (this.deusAtivo) {
            this.semiDeusAtivo = true;
        }
        this.poderAbsolutoAtivo = false;
        this.curaAbsolutaBonus = 0;
        this.curaAbsolutaVidaOriginalMax = 0;
        this.curaIncessanteUsada = false;
    }

    public boolean isEspadaAfiadaAtiva() { return espadaAfiadaAtiva; }
    public void setEspadaAfiadaAtiva(boolean espadaAfiadaAtiva) { this.espadaAfiadaAtiva = espadaAfiadaAtiva; }

    public boolean isProtecaoAbsolutaAtiva() { return protecaoAbsolutaAtiva; }
    public void setProtecaoAbsolutaAtiva(boolean protecaoAbsolutaAtiva) { this.protecaoAbsolutaAtiva = protecaoAbsolutaAtiva; }

    public int getBonusDefesaTemporario() { return bonusDefesaTemporario; }
    public void setBonusDefesaTemporario(int bonusDefesaTemporario) { this.bonusDefesaTemporario = bonusDefesaTemporario; }

    public boolean isCuraParaMortePreparado() { return curaParaMortePreparado; }
    public void setCuraParaMortePreparado(boolean curaParaMortePreparado) { this.curaParaMortePreparado = curaParaMortePreparado; }

    public boolean isCuraParaMorteAtivo() { return curaParaMorteAtivo; }
    public void setCuraParaMorteAtivo(boolean curaParaMorteAtivo) { this.curaParaMorteAtivo = curaParaMorteAtivo; }

    public criaturas.Criatura getAlvoCuraParaMorte() { return alvoCuraParaMorte; }
    public void setAlvoCuraParaMorte(criaturas.Criatura alvoCuraParaMorte) { this.alvoCuraParaMorte = alvoCuraParaMorte; }

    public boolean isCuraTotalUsada() { return curaTotalUsada; }
    public void setCuraTotalUsada(boolean curaTotalUsada) { this.curaTotalUsada = curaTotalUsada; }

    public boolean isInfectado() { return infectado; }
    public void setInfectado(boolean infectado) { this.infectado = infectado; }

    public boolean isDefesaAbsolutaAtiva() { return defesaAbsolutaAtiva; }
    public void setDefesaAbsolutaAtiva(boolean defesaAbsolutaAtiva) { this.defesaAbsolutaAtiva = defesaAbsolutaAtiva; }

    public int getRodadasSemHabilidade() { return rodadasSemHabilidade; }
    public void setRodadasSemHabilidade(int rodadasSemHabilidade) { this.rodadasSemHabilidade = rodadasSemHabilidade; }

    public boolean isMagiaProibidaUsada() { return magiaProibidaUsada; }
    public void setMagiaProibidaUsada(boolean magiaProibidaUsada) { this.magiaProibidaUsada = magiaProibidaUsada; }

    public boolean isMagiaProibidaAtiva() { return magiaProibidaAtiva; }
    public void setMagiaProibidaAtiva(boolean magiaProibidaAtiva) { this.magiaProibidaAtiva = magiaProibidaAtiva; }

    public criaturas.Criatura getPrisaoAtiva() { return prisaoAtiva; }
    public void setPrisaoAtiva(criaturas.Criatura prisaoAtiva) { this.prisaoAtiva = prisaoAtiva; }

    public boolean isSemiDeusAtivo() { return semiDeusAtivo; }
    public void setSemiDeusAtivo(boolean semiDeusAtivo) { this.semiDeusAtivo = semiDeusAtivo; }

    public int getSemiDeusVidaOriginalMax() { return semiDeusVidaOriginalMax; }
    public void setSemiDeusVidaOriginalMax(int semiDeusVidaOriginalMax) { this.semiDeusVidaOriginalMax = semiDeusVidaOriginalMax; }

    public boolean isPoderAbsolutoAtivo() { return poderAbsolutoAtivo; }
    public void setPoderAbsolutoAtivo(boolean poderAbsolutoAtivo) { this.poderAbsolutoAtivo = poderAbsolutoAtivo; }

    public int getCuraAbsolutaBonus() { return curaAbsolutaBonus; }
    public void setCuraAbsolutaBonus(int curaAbsolutaBonus) { this.curaAbsolutaBonus = curaAbsolutaBonus; }

    public int getCuraAbsolutaVidaOriginalMax() { return curaAbsolutaVidaOriginalMax; }
    public void setCuraAbsolutaVidaOriginalMax(int curaAbsolutaVidaOriginalMax) { this.curaAbsolutaVidaOriginalMax = curaAbsolutaVidaOriginalMax; }

    public boolean isDeusAtivo() { return deusAtivo; }
    public void setDeusAtivo(boolean deusAtivo) { this.deusAtivo = deusAtivo; }

    public boolean isCuraIncessanteUsada() { return curaIncessanteUsada; }
    public void setCuraIncessanteUsada(boolean curaIncessanteUsada) { this.curaIncessanteUsada = curaIncessanteUsada; }

    public boolean isConhecimentoAbsolutoAplicado() { return conhecimentoAbsolutoAplicado; }
    public void setConhecimentoAbsolutoAplicado(boolean conhecimentoAbsolutoAplicado) { this.conhecimentoAbsolutoAplicado = conhecimentoAbsolutoAplicado; }

    // Recebe dano considerando a proteção da Cura Absoluta (absorve dano primeiro).
    // O Pacto Mortal (Olho Demoníaco) faz você sofrer +3 em todo dano até o fim do combate.
    public void receberDano(int dano) {
        if (dano < 0) return;
        if (pactoMortalAtivo) {
            dano += 3;
            telas.Interface.MostrarMensagem("(Pacto Mortal! Você sofre +3 de dano)");
            telas.Interface.Pausa(1200);
        }
        if (curaAbsolutaBonus > 0) {
            if (dano <= curaAbsolutaBonus) {
                curaAbsolutaBonus -= dano;
                telas.Interface.MostrarMensagem("(Proteção da Cura Absoluta absorve " + dano + " de dano! Restante: " + curaAbsolutaBonus + ")");
                telas.Interface.Pausa(1500);
            } else {
                int restante = dano - curaAbsolutaBonus;
                curaAbsolutaBonus = 0;
                vidaMaxima = curaAbsolutaVidaOriginalMax;
                vidaPersonagem = Math.max(0, vidaPersonagem - restante);
                telas.Interface.MostrarMensagem("(A proteção da Cura Absoluta se esgotou!)");
                telas.Interface.Pausa(1500);
            }
        } else {
            vidaPersonagem = Math.max(0, vidaPersonagem - dano);
        }

        // Vontade de Viver (Humano): ao cair a 0, sobrevive com 1 PV (1x/dia)
        if (vidaPersonagem <= 0 && raca != null && raca.podeSobreviverCom1AoCair0() && !sobrevivenciaUsada) {
            sobrevivenciaUsada = true;
            vidaPersonagem = 1;
            telas.Interface.MostrarMensagem("\n(Vontade de Viver!) Você resiste à morte e permanece de pé com 1 de vida!");
            telas.Interface.Pausa(1500);
        }
    }
}