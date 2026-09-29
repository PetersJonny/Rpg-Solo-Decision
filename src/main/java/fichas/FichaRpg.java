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
    String nomePersonagem = "Desconhecido", nomePessoa;
    
    // Status de Sobrevivência
    int nivel = 1;
    int xp = 0;
    int ouro = 0;
    int vidaPersonagem, manaPersonagem;
    int vidaMaxima, manaMaxima;


    // Encontros
    private boolean fadaEncontrada = false;
    
    // Atributos Base
    int constituicaoBase, destrezaBase, forcaBase, sabedoriaBase, intelectoBase, presencaBase;

    // Raça (gera +1 num atributo e uma passiva). Humano escolhe o atributo.
    racas.Raca raca = null;
    private String atributoRacialHumano = null; // "+1" escolhido pelo Humano (ex.: "Força")

    // Passivas diárias (resetadas a cada novo dia)
    boolean sobrevivenciaUsada = false; // Vontade de Viver (Humano): 1x/dia
    boolean menteAfiadaUsada = false;  // Mente Afiada (Gnomo): 1x/dia
    
    // Atributos Finais (Base + Modificadores)
    int constituicao, destreza, forca, sabedoria, intelecto, presenca;
    
    // Classe
    ClasseRpg classeDoPersonagem = null;
    
    // Defesa
    int defesa, bonusDeDefesa;

    // Equipamento e Inventário
    Arma armaEquipada;
    itens.Armadura armaduraEquipada;
    List<ItemRpg> inventario = new ArrayList<>();
    
    // Habilidades
    List<habilidades.Habilidade> habilidades = new ArrayList<>();

    // Efeitos temporários de combate
    boolean espadaAfiadaAtiva;
    boolean protecaoAbsolutaAtiva;
    int bonusDefesaTemporario;
    boolean curaParaMortePreparado;
    boolean curaParaMorteAtivo;
    criaturas.Criatura alvoCuraParaMorte;
    boolean curaTotalUsada;
    boolean infectado; // infecção zumbi: 1d4 de dano por rodada de combate

    // Efeitos lvl 7
    boolean defesaAbsolutaAtiva;
    int rodadasSemHabilidade;
    boolean magiaProibidaUsada;
    boolean magiaProibidaAtiva;
    criaturas.Criatura prisaoAtiva;

    // Efeitos lvl 9
    boolean semiDeusAtivo;
    int semiDeusVidaOriginalMax;
    boolean poderAbsolutoAtivo;
    int curaAbsolutaBonus;
    int curaAbsolutaVidaOriginalMax;

    // Efeitos lvl 10
    boolean deusAtivo;
    boolean curaIncessanteUsada;
    private boolean conhecimentoAbsolutoAplicado;

    // Efeitos de tempo (dia/noite) e abrigo
    boolean ehNoite = false;
    int progressoPeriodo = 0;
    int diaAtual = 1; // Dia 1, Noite 1, Dia 2, Noite 2, ...
    int diasSemDormir = 0;
    boolean cansado = false;
    boolean temCabana = false;
    boolean naCabana = false;

    // Sistema de fome: quantos dias consecutivos sem comer; comeuHoje marca se
    // comeu no dia que passou (usado no virar do dia e no bônus de dormir);
    // enjoado = comeu comida estragada (mantém/ganha -1 em testes de Destreza e Força).
    // Frutas só contam como "comida completa" ao comer 3+ no dia; carne conta 1x.
    int diasSemComer = 0;
    boolean comeuHoje = false;
    boolean enjoado = false;
    int penalidadeEnjoado = 0;
    int frutasComidasHoje = 0;
    static final int FRUTAS_PARA_REFEICAO = 3;
    static final int CHANCE_CARNE_ESTRAGADA = 30;

    // Companheiro (pessoa perdida que o jogador acolheu)
    companheiros.Companheiro companheiro = null;

    // Modo de dificuldade e slot de save vinculado à partida
    private ModoDificuldade modoDificuldade = ModoDificuldade.NORMAL;
    private int slotAtual = 0; // 0 = nenhum save vinculado

    // Sala de Treino
    boolean temSalaTreino = false;
    boolean naSalaTreino = false;
    boolean salaJuntoCabana = false; // se a sala foi construída enquanto se estava na cabana
    String treinoBonusAtributo = null; // "Força" ou "Destreza"
    int treinoBonusPeriodosRestantes = 0;

    // Mesa de Magias
    boolean temMesaMagias = false;
    int magiaBonusPeriodosRestantes = 0;
    boolean naMesaMagias = false; // se o jogador está junto da mesa
    boolean mesaJuntoCabana = false; // se a mesa foi construída estando na cabana
    boolean mesaJuntoSala = false; // se a mesa foi construída estando na sala de treino
    boolean salaJuntoMesa = false; // se a sala foi construída estando na mesa de magias

    // Fogueira (para cozinhar carne crua e deixá-la segura)
    boolean temFogueira = false;
    boolean naFogueira = false; // se o jogador está junto da fogueira
    boolean fogueiraJuntoCabana = false; // se a fogueira foi construída estando na cabana
    boolean fogueiraJuntoSala = false; // se a fogueira foi construída estando na sala de treino
    boolean fogueiraJuntoMesa = false; // se a fogueira foi construída estando na mesa de magias

    // Profundidade da travessia em que cada construção foi montada (o "ponto" dela).
    // Montar de novo em outro lugar move o ponto; a distância entre duas construções
    // é a diferença entre as profundidades onde cada uma está.
    int profundidadeCabana = 0;
    int profundidadeSalaTreino = 0;
    int profundidadeMesaMagias = 0;
    int profundidadeFogueira = 0;

    // Estruturas encontradas na floresta (só podem ser descobertas explorando)
    boolean labirintoEncontrado = false;
    estruturas.Labirinto labirinto = null; // grade salva junto da ficha

    // Travessia para fora da floresta: profundidade oculta ao jogador.
    // 0 = perto das construções; 20 = fora da floresta (em uma cidade).
    int profundidadeFloresta = 0;

    public static final int PROFUNDIDADE_PARA_SAIR = 20;

    // Cidade para além da floresta onde o andarilho parou (sorteada na chegada; null = ainda não saiu)
    private String cidadeAtual = null;

    // Tesouros raros do Labirinto (cada um só pode ser encontrado 1 vez)
    private boolean olhoDemonicoEncontrado = false;
    private boolean espadaMajestralEncontrada = false;
    private boolean coroaReiEncontrada = false;

    // Olho Demoníaco: quem aceita o chamado tem o olho fundido ao próprio corpo,
    // para sempre (não é um item de inventário).
    private boolean olhoDemonicoFundido = false;

    // Coroa do Rei: quem decifra seu segredo (teste de Intelecto 18+) vira o Rei das Criaturas
    private boolean reiDasCriaturas = false;

    // Pacto Mortal (Olho Demoníaco): ativo até o fim do combate — você sofre +3 em todo dano
    boolean pactoMortalAtivo = false;

    // Minotauro: ao ser derrotado no coração do labirinto, ele não aparece de novo
    private boolean minotauroDerrotado = false;

    // ==================== VILAREJO (TAVERNA E FERREIRO) ====================

    // Goblins da taverna: resolvidos ao derrotar/expulsar o bando (libera o serviço da taverna)
    private boolean goblinsResolvido = false;
    // Dono da taverna (o dracônico de pele vermelha) já agradeceu pelo salvamento
    private boolean donoDaTavernaAgradeceu = false;
    // O dono ofereceu "a primeira comida por conta da casa" (quando o jogador estava faminto)
    private boolean comidaPorContaDaCasa = false;
    private boolean comidaDaCasaUsada = false;

    // Ferreiro da vila: encomenda sob medida (+20% do preço), pronta após 1 dia completo
    private String ferreiroOrdemItem = "";
    private int ferreiroOrdemDia = 0;
    // O ferreiro (Gorak Vieira) só se apresenta pelo nome completo na primeira visita
    private boolean ferreiroSeApresentou = false;

    // Missões aceitas no quadro da vila (nomes; podem ser várias ao mesmo tempo)
    List<String> missoesAceitas = new ArrayList<>();

    // Construtor
    public FichaRpg(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public void setNomePersonagem(String nomePersonagem) {
        this.nomePersonagem = nomePersonagem;
    }

    // Distribuição de Pontos
    public void adicionarAtributo(int opcao, int pontos) { GerenciadorDeCrescimento.adicionarAtributo(this, opcao, pontos); }

    public void setClasse(ClasseRpg classe) {
        this.classeDoPersonagem = classe;
        aplicarBonus();
    }
    public void resetarPontosBase() { GerenciadorDeCrescimento.resetarPontosBase(this); }

    // Aplicação de Modificadores e Equipamentos Iniciais
    public void aplicarBonus() { GerenciadorDeCrescimento.aplicarBonus(this); }

    // Validador de Ficha
    public boolean isFichaCompleta() { return GerenciadorDeLocalizacao.isFichaCompleta(this); }

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
    public void marcarSobrevivenciaUsada() { GerenciadorDeVida.marcarSobrevivenciaUsada(this); }
    public boolean isMenteAfiadaUsada() { return menteAfiadaUsada; }
    public void marcarMenteAfiadaUsada() { GerenciadorDeVida.marcarMenteAfiadaUsada(this); }
    public boolean podeUsarMenteAfiada() { return GerenciadorDeVida.podeUsarMenteAfiada(this); }
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
    public void adicionarOuro(int quantidade) { GerenciadorDeOuroEDeslocamento.adicionarOuro(this, quantidade); }

    // Tenta gastar ouro; retorna false se não tiver o suficiente
    public boolean gastarOuro(int quantidade) { return GerenciadorDeOuroEDeslocamento.gastarOuro(this, quantidade); }

    // XP necessária para subir do nível atual para o próximo
    public static int getXpNecessaria(int nivel) { return GerenciadorDeCrescimento.getXpNecessaria(nivel); }

    // Adiciona XP e trata os up's de nível; retorna quantos níveis foram ganhos
    public int adicionarXp(int quantidade) { return GerenciadorDeCrescimento.adicionarXp(this, quantidade); }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    // Adicionar item ao inventário, empilhando se já existir
    public void adicionarItem(ItemRpg novoItem) { GerenciadorDeInventarioFicha.adicionarItem(this, novoItem); }

    // ==================== ESPAÇO DA MOCHILA ====================

    // Capacidade de carga da mochila: 10 + 5 por ponto de Força; com Força
    // negativa, fica fixa em 10 (o mínimo de carregar o básico).
    public double getCapacidadeMochila() { return GerenciadorDeInventarioFicha.getCapacidadeMochila(this); }

    // Peso total carregado (soma do peso de cada unidade do inventário)
    public double getPesoTotalMochila() { return GerenciadorDeInventarioFicha.getPesoTotalMochila(this); }

    // Quanto de espaço ainda resta na mochila
    public double getEspacoLivreMochila() { return GerenciadorDeInventarioFicha.getEspacoLivreMochila(this); }

    // Tenta adicionar o item se houver espaço; retorna false e NÃO adiciona se estourar
    public boolean tentarAdicionarItem(ItemRpg novoItem) { return GerenciadorDeInventarioFicha.tentarAdicionarItem(this, novoItem); }

    // Adiciona apenas a quantidade que couber na mochila (o item tem sua quantidade
    // reduzida ao que foi guardado); retorna quantas unidades foram pegas.
    public int adicionarItemLimitado(ItemRpg novoItem) { return GerenciadorDeInventarioFicha.adicionarItemLimitado(this, novoItem); }

    // Pergunta se o jogador quer pegar o item achado e quantos, respeitando o espaço
    // da mochila. O que não couber ou for recusado fica para trás.
    public void coletarItemEncontrado(ItemRpg item, String origem) { GerenciadorDeInventarioFicha.coletarItemEncontrado(this, item, origem); }

    // Remove itens do inventário; se a arma equipada for vendida, ela é desequipada.
    public boolean removerItem(String nome, int quantidade) { return GerenciadorDeInventarioFicha.removerItem(this, nome, quantidade); }

    // Consome unidades de um item (usar poções, flechas, kit médico...).
    // Conteúdo compartilhado entre o menu de inventário e o combate.
    public void consumirItem(ItemRpg item, int quantidade) { GerenciadorDeInventarioFicha.consumirItem(this, item, quantidade); }

    // Equipa a armadura de maior bônus do inventário; a que estava equipada volta para a mochila
    public void equiparMelhorArmadura() { GerenciadorDeInventarioFicha.equiparMelhorArmadura(this); }

    // Verifica se o jogador possui um item (com quantidade) no inventário
    public boolean temItem(String nome) { return GerenciadorDeInventarioFicha.temItem(this, nome); }

    // Bônus aleatório de atributo (concedido pela Fada)
    public String aumentarAtributoAleatorio() { return GerenciadorDeCrescimento.aumentarAtributoAleatorio(this); }

    // Aumenta o atributo escolhido (ponto de atributo ganho no level up)
    public String aumentarAtributo(int opcao) { return GerenciadorDeCrescimento.aumentarAtributo(this, opcao); }

    // Aumenta a Constituição aplicando retroativo de vida para TODOS os níveis já ganhos:
    // cada ponto extra de Constituição deveria ter dado +1 de vida em cada level up passado.
    public void aumentarConstituicao(int quantidade) { GerenciadorDeCrescimento.aumentarConstituicao(this, quantidade); }

    // Conhecimento Absoluto (Healer lvl 10): +quantidade em TODOS os atributos
    public void aumentarTodosAtributos(int quantidade) { GerenciadorDeCrescimento.aumentarTodosAtributos(this, quantidade); }

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
    public void removerCompanheiro() { GerenciadorDeMissoesECompanheiro.removerCompanheiro(this); }
    public boolean companheiroQuerPartir() { return GerenciadorDeMissoesECompanheiro.companheiroQuerPartir(this); }
    public void registrarDormidaDoCompanheiro() { GerenciadorDeMissoesECompanheiro.registrarDormidaDoCompanheiro(this); }
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
    public boolean isTemFogueira() { return temFogueira; }
    public boolean isNaFogueira() { return naFogueira; }
    public int getProfundidadeFogueira() { return profundidadeFogueira; }

    // Distância (em períodos de caminhada) entre o ponto atual e uma profundidade qualquer.
    public int getDistanciaAte(int profundidadeAlvo) { return GerenciadorDeLocalizacao.getDistanciaAte(this, profundidadeAlvo); }

    // Localização (id) em que o jogador está: 0 = ponto da cabana, 1 = ponto da sala
    // de treino, 2 = ponto da mesa de magias, 3 = meio da mata, 4 = ponto próprio da
    // fogueira. Duas estruturas montadas na MESMA profundidade ficam no mesmo ponto.
    public int getLocalizacaoAtual() { return GerenciadorDeLocalizacao.getLocalizacaoAtual(this); }

    // Ponto onde a sala de treino fica (0 = ponto da cabana; 2 = ponto da mesa; 1 = ponto próprio).
    public int getLocalizacaoSala() { return GerenciadorDeLocalizacao.getLocalizacaoSala(this); }

    // Ponto onde a mesa de magias fica (0 = ponto da cabana; 2 = ponto próprio ou da sala).
    public int getLocalizacaoMesa() { return GerenciadorDeLocalizacao.getLocalizacaoMesa(this); }

    // Ponto onde a fogueira fica (0 = ponto da cabana; 1 = ponto da sala;
    // 2 = ponto da mesa; 4 = ponto próprio).
    public int getLocalizacaoFogueira() { return GerenciadorDeLocalizacao.getLocalizacaoFogueira(this); }

    // Uma construção só pode ser usada quando o jogador está no ponto dela
    // (o ponto em que ela foi montada).
    public boolean podeUsarCabana() { return GerenciadorDeVida.podeUsarCabana(this); }
    public boolean podeUsarSalaTreino() { return GerenciadorDeVida.podeUsarSalaTreino(this); }
    public boolean podeUsarMesaMagias() { return GerenciadorDeVida.podeUsarMesaMagias(this); }
    public boolean podeUsarFogueira() { return GerenciadorDeVida.podeUsarFogueira(this); }

    public boolean isFogueiraJuntoCabana() { return fogueiraJuntoCabana; }
    public boolean isFogueiraJuntoSala() { return fogueiraJuntoSala; }
    public boolean isFogueiraJuntoMesa() { return fogueiraJuntoMesa; }

    public boolean isLabirintoEncontrado() { return labirintoEncontrado; }
    public void setLabirintoEncontrado(boolean labirintoEncontrado) { this.labirintoEncontrado = labirintoEncontrado; }

    public estruturas.Labirinto getLabirinto() { return labirinto; }
    public void setLabirinto(estruturas.Labirinto labirinto) { this.labirinto = labirinto; }

    public boolean isOlhoDemonicoEncontrado() { return olhoDemonicoEncontrado; }
    public void setOlhoDemonicoEncontrado(boolean olhoDemonicoEncontrado) { this.olhoDemonicoEncontrado = olhoDemonicoEncontrado; }

    public boolean isOlhoDemonicoFundido() { return olhoDemonicoFundido; }
    public void setOlhoDemonicoFundido(boolean olhoDemonicoFundido) { this.olhoDemonicoFundido = olhoDemonicoFundido; }

    public boolean isEspadaMajestralEncontrada() { return espadaMajestralEncontrada; }
    public void setEspadaMajestralEncontrada(boolean espadaMajestralEncontrada) { this.espadaMajestralEncontrada = espadaMajestralEncontrada; }

    public boolean isCoroaReiEncontrada() { return coroaReiEncontrada; }
    public void setCoroaReiEncontrada(boolean coroaReiEncontrada) { this.coroaReiEncontrada = coroaReiEncontrada; }

    public boolean isReiDasCriaturas() { return reiDasCriaturas; }
    public void setReiDasCriaturas(boolean reiDasCriaturas) { this.reiDasCriaturas = reiDasCriaturas; }

    public boolean isMinotauroDerrotado() { return minotauroDerrotado; }
    public void setMinotauroDerrotado(boolean minotauroDerrotado) { this.minotauroDerrotado = minotauroDerrotado; }

    // ==================== VILAREJO (TAVERNA E FERREIRO) ====================

    public boolean isGoblinsResolvido() { return goblinsResolvido; }
    public void setGoblinsResolvido(boolean goblinsResolvido) { this.goblinsResolvido = goblinsResolvido; }

    public boolean isDonoDaTavernaAgradeceu() { return donoDaTavernaAgradeceu; }
    public void setDonoDaTavernaAgradeceu(boolean donoDaTavernaAgradeceu) { this.donoDaTavernaAgradeceu = donoDaTavernaAgradeceu; }

    public boolean isComidaPorContaDaCasa() { return comidaPorContaDaCasa; }
    public void setComidaPorContaDaCasa(boolean comidaPorContaDaCasa) { this.comidaPorContaDaCasa = comidaPorContaDaCasa; }

    public boolean isComidaDaCasaUsada() { return comidaDaCasaUsada; }
    public void setComidaDaCasaUsada(boolean comidaDaCasaUsada) { this.comidaDaCasaUsada = comidaDaCasaUsada; }

    public String getFerreiroOrdemItem() { return ferreiroOrdemItem; }
    public void setFerreiroOrdemItem(String ferreiroOrdemItem) { this.ferreiroOrdemItem = ferreiroOrdemItem; }

    public int getFerreiroOrdemDia() { return ferreiroOrdemDia; }
    public void setFerreiroOrdemDia(int ferreiroOrdemDia) { this.ferreiroOrdemDia = ferreiroOrdemDia; }

    public boolean isFerreiroSeApresentou() { return ferreiroSeApresentou; }
    public void setFerreiroSeApresentou(boolean ferreiroSeApresentou) { this.ferreiroSeApresentou = ferreiroSeApresentou; }

    public boolean isOrdemDoFerreiroPendente() { return !ferreiroOrdemItem.isEmpty(); }
    public boolean isOrdemDoFerreiroPronta() { return !ferreiroOrdemItem.isEmpty() && diaAtual > ferreiroOrdemDia; }

    public List<String> getMissoesAceitas() { return missoesAceitas; }
    public boolean isMissaoAceita(String nome) { return missoesAceitas.contains(nome); }
    public void aceitarMissao(String nome) { GerenciadorDeMissoesECompanheiro.aceitarMissao(this, nome); }

    public boolean isPactoMortalAtivo() { return pactoMortalAtivo; }
    public void setPactoMortalAtivo(boolean pactoMortalAtivo) { this.pactoMortalAtivo = pactoMortalAtivo; }

    // O labirinto fica acessível no menu enquanto não tiver sido concluído.
    // Ao alcançar o centro, ele desmorona, o jogador foge para a floresta e a opção some.
    public boolean isLabirintoDisponivel() { return GerenciadorDeVida.isLabirintoDisponivel(this); }

    // Chance de descobrir o Labirinto do Minotauro a cada exploração:
    // começa em 1% e aumenta +1% a cada dia que passa (dia 1 = 1%, dia 2 = 2%...), até no máximo 100%.
    public int getLabirintoChanceDescoberta() { return GerenciadorDeVida.getLabirintoChanceDescoberta(this); }

    // ==================== TRAVESSIA PARA FORA DA FLORESTA ====================

    // Quão longe das construções o jogador está (0 = perto delas; PROFUNDIDADE_PARA_SAIR = fora).
    // O valor é oculto para o jogador.
    public int getProfundidadeFloresta() { return profundidadeFloresta; }

    // Estar "no vilarejo" = ter atravessado a floresta inteira e saído dela.
    public boolean isNoVilarejo() { return profundidadeFloresta >= PROFUNDIDADE_PARA_SAIR; }

    public String getCidadeAtual() { return cidadeAtual; }
    public void setCidadeAtual(String cidadeAtual) { this.cidadeAtual = cidadeAtual; }

    // Aprofunda a travessia (1 unidade = 1/3 do período); no máximo sai da floresta.
    public void adicionarProfundidade(int unidades) { GerenciadorDeOuroEDeslocamento.adicionarProfundidade(this, unidades); }

    // Reduz a profundidade ao voltar para as construções (nunca abaixo de 0).
    public void reduzirProfundidade(int unidades) { GerenciadorDeOuroEDeslocamento.reduzirProfundidade(this, unidades); }

    // Ao mudar de profundidade, o jogador passa a estar "em" qualquer construção
    // que ocupa exatamente aquela profundidade (várias podem ficar juntas no mesmo ponto).
    public void sincronizarLocalizacao() { GerenciadorDeConstrucoes.sincronizarLocalizacao(this); }

    // Entra na construção que fica NO ponto indicado (distância 0): corrige o
    // estado "expulso" que acontecia quando o jogador saía da cabana (sairDaCabana)
    // ou caminhava de volta sem mudar de profundidade — o menu de construção
    // voltava a mostrar "Ir para a Cabana (0 período(s))" para sempre, e a opção
    // "Dormir" nunca mais aparecia. Agora, estando no ponto, ele volta a ficar NELA.
    public void entrarNaConstrucao(int profundidadeAlvo) { GerenciadorDeConstrucoes.entrarNaConstrucao(this, profundidadeAlvo); }

    // Recalcula os flags de "junto": duas estruturas ficam juntas quando foram
    // montadas na MESMA profundidade (mesmo ponto da mata).
    public void recomputarAdjacencias() { GerenciadorDeConstrucoes.recomputarAdjacencias(this); }

    // Sair da cabana para explorar/colher recursos (também sai da sala e da mesa)
    public void sairDaCabana() { GerenciadorDeConstrucoes.sairDaCabana(this); }

    // Voltar para a cabana (custa 1/3 do período)
    public void voltarParaCabana() { GerenciadorDeConstrucoes.voltarParaCabana(this); }

    // Ir até a sala de treino (custa 1/3 do período): quem está nela passa a
    // estar no local da sala (que pode ser junto à cabana, se for o caso).
    public void irParaSalaTreino() { GerenciadorDeConstrucoes.irParaSalaTreino(this); }

    // Ir até a mesa de magias (custa 1/3 do período): quem está nela passa a
    // estar no local da mesa (que pode ser junto à cabana, se for o caso).
    public void irParaMesaMagias() { GerenciadorDeConstrucoes.irParaMesaMagias(this); }

    // Ir até a fogueira (custa 1/3 do período): quem está nela passa a estar
    // no local da fogueira (que pode ser junto à cabana/sala/mesa, se for o caso).
    public void irParaFogueira() { GerenciadorDeConstrucoes.irParaFogueira(this); }

    // Avança o tempo do período (dia ou noite); a cada 3 unidades o período vira.
    // Explorar e buscar recursos consomem 1/3; montar a cabana consome 2/3.
    public boolean avancarTempo(int unidades) { return GerenciadorDeConstrucoes.avancarTempo(this, unidades); }

    // Dormir: só de noite, estando NA cabana (não adianta estando longe na floresta).
    // Se comeu no mesmo dia, recupera 1/2 da vida máxima e 1/2 da mana máxima;
    // caso contrário, recupera 1/3 de cada. Faz amanhecer.
    public boolean dormir() { return GerenciadorDeConstrucoes.dormir(this); }

    // ==================== FOME ====================

    // Número de dias sem comer (a fome é resetada ao comer)
    public int getDiasSemComer() { return diasSemComer; }
    // Se o personagem comeu no dia atual (usado para o bônus de dormir)
    public boolean isComeuHoje() { return comeuHoje; }
    // Se está enjoado (comer carne podre mantém o debuff de status)
    public boolean isEnjoado() { return enjoado; }

    // Descrição do estado de fome para exibir nos menus. Retorna "" se tudo bem.
    public String descreverFome() { return GerenciadorDeConstrucoes.descreverFome(this); }

    // Encerra o dia que passou: se não comeu, soma mais um dia de fome e
    // a perda de vida é aplicada a cada período que se inicia.
    public void registrarNovoDiaFome() { GerenciadorDeConstrucoes.registrarNovoDiaFome(this); }

    // Aplica a perda de vida por fome (a cada período, dia e noite) a partir
    // de 5 dias sem comer: 1, dobrando a cada 5 dias (10→2, 15→4, 20→8...).
    // Retorna o valor de vida perdido (0 se não aplicou).
    public int aplicarPerdaVidaPorFome() { return GerenciadorDeConstrucoes.aplicarPerdaVidaPorFome(this); }

    // Comer comida boa (frutas ou carnes frescas): zera a fome, cura o enjoo e
    // marca que comeu hoje.
    public void comerComidaBoa() { GerenciadorDeConstrucoes.comerComidaBoa(this); }

    // Comer carne podre: zera a contagem de dias sem comer, mas o personagem
    // fica enjoado — continua com o debuff de status que já tinha (se tiver) ou
    // ganha o debuff de -1 nos testes de Destreza e Força (se não tinha).
    // O enjoo passa até comer comida boa.
    public void comerCarnePodre() { GerenciadorDeConstrucoes.comerCarnePodre(this); }

    // Comer carne crua (de Lobo ou de Urso): ela pode estar estragada. Com
    // CHANCE_CARNE_ESTRAGADA de dar o efeito da Carne Podre — zera a contagem de
    // fome mas NÃO recupera vida e deixa enjoado. Retorna true se estragou.
    public boolean comerCarneCrua() { return GerenciadorDeConstrucoes.comerCarneCrua(this); }

    // Frutas: cada fruta é um lanche (cura 1d2, tratado no MotorDeCombate), mas
    // só viram UMA "comida completa" quando somam 3 no dia. O contador reseta
    // a cada novo dia (registrarNovoDiaFome).
    public void comerFrutas(int qtd) { GerenciadorDeConstrucoes.comerFrutas(this, qtd); }

    public int getFrutasComidasHoje() { return frutasComidasHoje; }

    // Montar a cabana: gasta 7 madeiras, 10 folhas e 4 pedras (só a primeira vez).
    // Retorna true se conseguiu construir.
    public boolean montarCabana() { return GerenciadorDeConstrucoes.montarCabana(this); }

    // Montar a sala de treino: gasta 10 madeiras, 15 folhas, 5 pedras e 4 couros.
    // Ela fica ancorada exatamente no ponto da mata onde for construída: se for
    // no mesmo ponto de outra construção, ficam JUNTO (estar em uma permite usar
    // a vizinha sem novo deslocamento); caso contrário, são pontos separados.
    public boolean construirSalaTreino() { return GerenciadorDeConstrucoes.construirSalaTreino(this); }

    // Entrar na sala de treino para treinar. Se a sala for junto da cabana,
    // o jogador continua considerado "na cabana"; caso contrário, ela fica longe.
    public void entrarSalaTreino() { GerenciadorDeConstrucoes.entrarSalaTreino(this); }

    // Aplica o bônus de treino (+2 em Força ou Destreza) que dura os 2 períodos seguintes
    public void treinarAtributo(String atributo) { GerenciadorDeConstrucoes.treinarAtributo(this, atributo); }

    // Depois de treinar o período inteiro: se a sala for junto da cabana,
    // o jogador permanece na cabana; caso contrário, continua na sala.
    public void terminarTreino() { GerenciadorDeConstrucoes.terminarTreino(this); }

    // Montar a mesa de magias: gasta 5 madeiras, 4 folhas, 4 pedras e 1 Pó da Fada.
    // Retorna true se conseguiu construir.
    public boolean construirMesaMagias() { return GerenciadorDeConstrucoes.construirMesaMagias(this); }

    // Montar a fogueira: gasta 4 madeiras e 3 folhas. Ela fica ancorada no ponto
    // da mata onde for construída, exatamente como as outras construções.
    public boolean montarFogueira() { return GerenciadorDeConstrucoes.montarFogueira(this); }

    // Cozinhar TODAS as carnes cruas na fogueira: gasta 2 madeiras (a lenha queima)
    // e transforma TODAS as carnes cruas do inventário (de Lobo e de Urso) nas
    // versões cozidas, que não têm risco de estragar ao serem comidas. Só funciona
    // estando junto da fogueira.
    public boolean cozinharTodasAsCarnes() { return GerenciadorDeConstrucoes.cozinharTodasAsCarnes(this); }

    // Converte `qtd` unidades da carne crua na versão cozida (sem gastar madeira).
    public void cozinharTipoCarne(String crua, String cozida, int qtd) { GerenciadorDeConstrucoes.cozinharTipoCarne(this, crua, cozida, qtd); }

    // Mover (montar uma nova) construção no ponto atual, gastando a mesma matéria-prima.
    // O novo local passa a ser o ponto da construção; o antigo fica para trás.
    public boolean moverCabana() { return GerenciadorDeConstrucoes.moverCabana(this); }
    public boolean moverSalaTreino() { return GerenciadorDeConstrucoes.moverSalaTreino(this); }
    public boolean moverMesaMagias() { return GerenciadorDeConstrucoes.moverMesaMagias(this); }
    public boolean moverFogueira() { return GerenciadorDeConstrucoes.moverFogueira(this); }

    // Profundidade da construção mais adiantada na travessia (a mais próxima da
    // borda da floresta). Usado quando se volta de uma cidade para as construções.
    public int getProfundidadeConstrucaoMaisProxima() { return GerenciadorDeConstrucoes.getProfundidadeConstrucaoMaisProxima(this); }

    // Estudar na mesa de magias (gasta o período inteiro): +1 dado de dano
    // em TODAS as habilidades de dano, valendo os 2 períodos seguintes
    public void estudarMagia() { GerenciadorDeConstrucoes.estudarMagia(this); }

    // Total de um item no inventário (somando as pilhas)
    public int getQuantidadeDe(String nome) { return GerenciadorDeInventarioFicha.getQuantidadeDe(this, nome); }

    // Getters usados em TESTES de atributo: quando cansado, -1 em testes.
    // Não afeta vida, mana, defesa nem dano.
    // Visão na Penumbra (Vigia do Crepúsculo): +2 em testes durante a noite.
    public int bonusTestesNoturnos() { return GerenciadorDeVida.bonusTestesNoturnos(this); }
    // Fome: -1 em testes de Força e Destreza quando sem comer no dia anterior,
    // -2 quando há 3+ dias sem comer. Carne podre (enjoado) mantém o debuff.
    public int getPenalidadeFome() { return GerenciadorDeVida.getPenalidadeFome(this); }
    // Perda de vida por período (dia e noite) por fome: a partir de 5 dias sem
    // comer perde 1, dobra a cada 5 dias (10→2, 15→4, 20→8...).
    public int getPerdaVidaPorFome() { return GerenciadorDeVida.getPerdaVidaPorFome(this); }
    public int getDestrezaTeste() { return getDestreza() - (cansado ? 1 : 0) - getPenalidadeFome() + bonusTestesNoturnos(); }
    public int getPresencaTeste() { return presenca - (cansado ? 1 : 0) + bonusTestesNoturnos(); }
    public int getSabedoriaTeste() { return sabedoria - (cansado ? 1 : 0) + bonusTestesNoturnos(); }
    public int getForcaTeste() { return getForca() - (cansado ? 1 : 0) - getPenalidadeFome() + bonusTestesNoturnos(); }
    public int getIntelectoTeste() { return intelecto - (cansado ? 1 : 0) + bonusTestesNoturnos(); }
    public int getConstituicaoTeste() { return constituicao - (cansado ? 1 : 0) + bonusTestesNoturnos(); }

    // Reseta os efeitos temporários antes de um novo combate
    public void resetarEfeitosCombate() { GerenciadorDeVida.resetarEfeitosCombate(this); }

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
    public void receberDano(int dano) { GerenciadorDeVida.receberDano(this, dano); }
}