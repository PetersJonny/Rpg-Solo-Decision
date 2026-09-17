package fichas;

import classes.ClasseRpg;
import itens.Arma;
import itens.ItemRpg;
import telas.Interface;
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
    private int diasSemDormir = 0;
    private boolean cansado = false;
    private boolean temCabana = false;
    private boolean naCabana = false;

    // Companheiro (pessoa perdida que o jogador acolheu)
    private companheiros.Companheiro companheiro = null;

    // Sala de Treino
    private boolean temSalaTreino = false;
    private boolean naSalaTreino = false;
    private boolean salaJuntoCabana = false; // se a sala foi construída enquanto se estava na cabana
    private String treinoBonusAtributo = null; // "Força" ou "Destreza"
    private int treinoBonusPeriodosRestantes = 0;

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
            case 1 -> constituicaoBase += pontos;
            case 2 -> destrezaBase += pontos;
            case 3 -> forcaBase += pontos;
            case 4 -> sabedoriaBase += pontos;
            case 5 -> intelectoBase += pontos;
            case 6 -> presencaBase += pontos;
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
            this.defesa = 10 + this.destreza + bonusDeDefesa;
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
        
        this.defesa = 10 + this.destreza + bonusDeDefesa;

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
    public int getNivel() { return nivel; }
    public int getOuro() { return ouro; }
    public int getVidaPersonagem() { return vidaPersonagem; }
    public int getManaPersonagem() { return manaPersonagem; }
    public int getConstituicao() { return constituicao; }
    public int getDestreza() { return destreza + ("Destreza".equals(treinoBonusAtributo) ? 3 : 0); }
    public int getForca() { return forca + ("Força".equals(treinoBonusAtributo) ? 3 : 0); }
    public int getSabedoria() { return sabedoria; }
    public int getIntelecto() { return intelecto; }
    public int getPresenca() { return presenca; }
    public int getDefesa() { return defesa + ("Destreza".equals(treinoBonusAtributo) ? 3 : 0) + (armaduraEquipada != null ? armaduraEquipada.getBonusDefesa() : 0) + bonusDefesaTemporario + (defesaAbsolutaAtiva ? 5 : 0); }
    public itens.Armadura getArmaduraEquipada() { return armaduraEquipada; }
    public ClasseRpg getClasseDoPersonagem() { return classeDoPersonagem; }
    public Arma getArmaEquipada() { return armaEquipada; }
    public List<ItemRpg> getInventario() { return inventario; }
    public List<habilidades.Habilidade> getHabilidades() { return habilidades; }
    public boolean isFadaEncontrada() { return fadaEncontrada; }
    public void setFadaEncontrada(boolean fadaEncontrada) { this.fadaEncontrada = fadaEncontrada; }

    // Setters de Combate
    public void setVidaPersonagem(int vida) { this.vidaPersonagem = Math.min(vida, vidaMaxima); }
    public void setManaPersonagem(int mana) { this.manaPersonagem = Math.min(mana, manaMaxima); }

    // Getters de Máximo
    public int getVidaMaxima() { return vidaMaxima; }
    public int getManaMaxima() { return manaMaxima; }

    // Setters de Máximo (usados no level up)
    public void setVidaMaxima(int vidaMaxima) { this.vidaMaxima = vidaMaxima; }
    public void setManaMaxima(int manaMaxima) { this.manaMaxima = manaMaxima; }

    // Dinheiro
    public void adicionarOuro(int quantidade) { this.ouro += Math.max(0, quantidade); }

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
                xp = 0; // ao subir de nível, a XP é resetada
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

    // Remove itens do inventário; se a arma equipada for vendida, ela é desequipada
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
            case 1 -> { aumentarConstituicao(1); return "Constituição"; }
            case 2 -> { destreza++; return "Destreza"; }
            case 3 -> { forca++; return "Força"; }
            case 4 -> { sabedoria++; return "Sabedoria"; }
            case 5 -> { intelecto++; return "Intelecto"; }
            default -> { presenca++; return "Presença"; }
        }
    }

    // Aumenta o atributo escolhido (ponto de atributo ganho no level up)
    public String aumentarAtributo(int opcao) {
        switch (opcao) {
            case 1 -> { aumentarConstituicao(1); return "Constituição"; }
            case 2 -> { destreza++; return "Destreza"; }
            case 3 -> { forca++; return "Força"; }
            case 4 -> { sabedoria++; return "Sabedoria"; }
            case 5 -> { intelecto++; return "Intelecto"; }
            default -> { presenca++; return "Presença"; }
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

    // Sair da cabana para explorar/colher recursos (também sai da sala de treino)
    public void sairDaCabana() {
        if (temCabana) {
            naCabana = false;
        }
        naSalaTreino = false;
    }

    // Voltar para a cabana (custa 1/3 do período)
    public void voltarParaCabana() {
        if (temCabana) {
            naCabana = true;
        }
        naSalaTreino = false;
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
            } else if (eraNoite && companheiro != null) {
                // A noite terminou: o companheiro dormiu na cabana
                registrarDormidaDoCompanheiro();
            }
            // Decrementa bônus de treino a cada período que se inicia
            if (treinoBonusPeriodosRestantes > 0) {
                treinoBonusPeriodosRestantes--;
                if (treinoBonusPeriodosRestantes <= 0) {
                    treinoBonusAtributo = null;
                }
            }
            virou = true;
        }
        cansado = diasSemDormir > 2;
        return virou;
    }

    // Dormir: só de noite, estando NA cabana (não adianta estando longe na floresta).
    // Recupera metade da vida máxima e metade da mana máxima e faz amanhecer.
    public boolean dormir() {
        if (!ehNoite) return false;
        if (!temCabana || !naCabana) return false;
        int curaVida = vidaMaxima / 2;
        int curaMana = manaMaxima / 2;
        vidaPersonagem = Math.min(vidaPersonagem + curaVida, vidaMaxima);
        manaPersonagem = Math.min(manaPersonagem + curaMana, manaMaxima);
        ehNoite = false;
        progressoPeriodo = 0;
        diasSemDormir = 0;
        cansado = false;
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
        naCabana = true;
        naSalaTreino = false;
        return true;
    }

    // Montar a sala de treino: gasta 10 madeiras, 15 folhas, 5 pedras e 4 couros.
    // Se for construída enquanto o jogador estiver na cabana, ela fica JUNTO da cabana
    // (estar nela não conta como ter saído). Se construída fora, são lugares separados.
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
        salaJuntoCabana = naCabana;
        naSalaTreino = true;
        return true;
    }

    // Entrar na sala de treino para treinar. Se a sala for junto da cabana,
    // o jogador continua considerado "na cabana"; caso contrário, ela fica longe.
    public void entrarSalaTreino() {
        naSalaTreino = true;
        if (salaJuntoCabana) {
            naCabana = true;
        } else {
            naCabana = false;
        }
    }

    // Aplica o bônus de treino (+3 em Força ou Destreza) que dura os 2 períodos seguintes
    public void treinarAtributo(String atributo) {
        this.treinoBonusAtributo = atributo;
        this.treinoBonusPeriodosRestantes = 2;
    }

    // Depois de treinar o período inteiro: se a sala for junto da cabana,
    // o jogador permanece na cabana; caso contrário, continua na sala.
    public void terminarTreino() {
        if (salaJuntoCabana) {
            naCabana = true;
            naSalaTreino = false;
        } else {
            naCabana = false;
            naSalaTreino = true;
        }
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
    public int getDestrezaTeste() { return getDestreza() - (cansado ? 1 : 0); }
    public int getPresencaTeste() { return presenca - (cansado ? 1 : 0); }
    public int getSabedoriaTeste() { return sabedoria - (cansado ? 1 : 0); }
    public int getForcaTeste() { return getForca() - (cansado ? 1 : 0); }
    public int getIntelectoTeste() { return intelecto - (cansado ? 1 : 0); }
    public int getConstituicaoTeste() { return constituicao - (cansado ? 1 : 0); }

    // Reseta os efeitos temporários antes de um novo combate
    public void resetarEfeitosCombate() {
        this.espadaAfiadaAtiva = false;
        this.protecaoAbsolutaAtiva = false;
        this.bonusDefesaTemporario = 0;
        this.curaParaMortePreparado = false;
        this.curaParaMorteAtivo = false;
        this.alvoCuraParaMorte = null;
        this.curaTotalUsada = false;
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

    // Recebe dano considerando a proteção da Cura Absoluta (absorve dano primeiro)
    public void receberDano(int dano) {
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
    }
}