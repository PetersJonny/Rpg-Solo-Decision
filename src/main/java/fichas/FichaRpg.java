package fichas;

import classes.ClasseRpg;
import itens.Arma;
import itens.ItemRpg;
import telas.Interface;
import racas.Raca;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mecanicas.MecanicasRpg;

public class FichaRpg implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

        String nomePersonagem = "Desconhecido", nomePessoa;

        int nivel = 1;
    int xp = 0;
    int ouro = 0;
    int vidaPersonagem, manaPersonagem;
    int vidaMaxima, manaMaxima;

        private boolean fadaEncontrada = false;

        int constituicaoBase, destrezaBase, forcaBase, sabedoriaBase, intelectoBase, presencaBase;

        racas.Raca raca = null;
    private String atributoRacialHumano = null;
        boolean sobrevivenciaUsada = false;
        boolean menteAfiadaUsada = false;
        int constituicao, destreza, forca, sabedoria, intelecto, presenca;

        ClasseRpg classeDoPersonagem = null;

        int defesa, bonusDeDefesa;

        Arma armaEquipada;
    itens.Armadura armaduraEquipada;
    List<ItemRpg> inventario = new ArrayList<>();

        List<habilidades.Habilidade> habilidades = new ArrayList<>();

        boolean espadaAfiadaAtiva;
    boolean protecaoAbsolutaAtiva;
    int bonusDefesaTemporario;
    boolean curaParaMortePreparado;
    boolean curaParaMorteAtivo;
    criaturas.Criatura alvoCuraParaMorte;
    boolean curaTotalUsada;
    boolean infectado;
    boolean sangrando;
        boolean defesaAbsolutaAtiva;
    int rodadasSemHabilidade;
    boolean magiaProibidaUsada;
    boolean magiaProibidaAtiva;
    criaturas.Criatura prisaoAtiva;

        boolean semiDeusAtivo;
    int semiDeusVidaOriginalMax;
    boolean poderAbsolutoAtivo;
    int curaAbsolutaBonus;
    int curaAbsolutaVidaOriginalMax;

        boolean deusAtivo;
    boolean curaIncessanteUsada;
    private boolean conhecimentoAbsolutoAplicado;

        boolean ehNoite = false;
    int progressoPeriodo = 0;
    int diaAtual = 1;
    int diasSemDormir = 0;
    boolean cansado = false;
    boolean temCabana = false;
    boolean naCabana = false;

                    int diasSemComer = 0;
    boolean comeuHoje = false;
    boolean enjoado = false;
    int penalidadeEnjoado = 0;
    int frutasComidasHoje = 0;
    static final int FRUTAS_PARA_REFEICAO = 3;
    static final int CHANCE_CARNE_ESTRAGADA = 30;

        companheiros.Companheiro companheiro = null;

        private ModoDificuldade modoDificuldade = ModoDificuldade.NORMAL;
    private int slotAtual = 0;
        boolean temSalaTreino = false;
    boolean naSalaTreino = false;
    boolean salaJuntoCabana = false;
    String treinoBonusAtributo = null;
    int treinoBonusPeriodosRestantes = 0;

        boolean temMesaMagias = false;
    int magiaBonusPeriodosRestantes = 0;
    boolean naMesaMagias = false;
    boolean mesaJuntoCabana = false;
    boolean mesaJuntoSala = false;
    boolean salaJuntoMesa = false;
        boolean temFogueira = false;
    boolean naFogueira = false;
    boolean fogueiraJuntoCabana = false;
    boolean fogueiraJuntoSala = false;
    boolean fogueiraJuntoMesa = false;
                int profundidadeCabana = 0;
    int profundidadeSalaTreino = 0;
    int profundidadeMesaMagias = 0;
    int profundidadeFogueira = 0;

        boolean labirintoEncontrado = false;
    estruturas.Labirinto labirinto = null;
            int profundidadeFloresta = 0;

    public static final int PROFUNDIDADE_PARA_SAIR = 20;

        private String cidadeAtual = null;

        private boolean olhoDemonicoEncontrado = false;
    private boolean espadaMajestralEncontrada = false;
    private boolean coroaReiEncontrada = false;

            private boolean olhoDemonicoFundido = false;

        private boolean reiDasCriaturas = false;

        boolean pactoMortalAtivo = false;

        private boolean minotauroDerrotado = false;

        private boolean goblinsResolvido = false;
        private boolean donoDaTavernaAgradeceu = false;
        private boolean comidaPorContaDaCasa = false;
    private boolean comidaDaCasaUsada = false;

        private String ferreiroOrdemItem = "";
    private int ferreiroOrdemDia = 0;
        private boolean ferreiroSeApresentou = false;

        List<String> missoesAceitas = new ArrayList<>();
    Map<String, List<String>> missoesNovidades = new HashMap<>();

        private boolean alfaiatariaConhecida = false;
        private boolean caveConhecida = false;
        private boolean velhinhaEncontrada = false;
    private boolean filhaEncontrada = false;
    private boolean netaEncontrada = false;

    private int diaAceitouNeta = 0;
    private boolean netaMorta = false;
    private boolean trilhaIniciada = false;
    private boolean pegadasEncontradas = false;
    private boolean bandoVencido = false;
    private boolean gaiolaVasculhada = false;
    private boolean magicoMacabroDerrotado = false;
    private boolean cabanaVisitada = false;
    private boolean netaSeguindo = false;
    private boolean missaoNetaEncerrada = false;
    private boolean presencaNetaPassou = false;
    private boolean acampamentoAlcancado = false;
    private boolean cabanaAlcancada = false;
    private boolean netaCorpoLevado = false;
    private int turnosParaVoltar = 3;

        public FichaRpg(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public void setNomePersonagem(String nomePersonagem) {
        this.nomePersonagem = nomePersonagem;
    }

        public void adicionarAtributo(int opcao, int pontos) { GerenciadorDeCrescimento.adicionarAtributo(this, opcao, pontos); }

    public void setClasse(ClasseRpg classe) {
        this.classeDoPersonagem = classe;
        aplicarBonus();
    }
    public void resetarPontosBase() { GerenciadorDeCrescimento.resetarPontosBase(this); }

        public void aplicarBonus() { GerenciadorDeCrescimento.aplicarBonus(this); }

        public boolean isFichaCompleta() { return GerenciadorDeLocalizacao.isFichaCompleta(this); }

        public String getNomePersonagem() { return nomePersonagem; }
    public String getNomePessoa() { return nomePessoa; }

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
    public int getDefesa() { return defesa + (armaduraEquipada != null ? armaduraEquipada.getBonusDefesa() : 0) + bonusDefesaTemporario + (defesaAbsolutaAtiva ? 5 : 0) + (temItem("Lenço de Seda") ? 1 : 0); }
    public itens.Armadura getArmaduraEquipada() { return armaduraEquipada; }
    public ClasseRpg getClasseDoPersonagem() { return classeDoPersonagem; }
    public racas.Raca getRaca() { return raca; }
    public void setRaca(racas.Raca raca) { this.raca = raca; }

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

        public void setVidaPersonagem(int vida) { this.vidaPersonagem = Math.max(0, Math.min(vida, vidaMaxima)); }
    public void setManaPersonagem(int mana) { this.manaPersonagem = Math.max(0, Math.min(mana, manaMaxima)); }

        public int getVidaMaxima() { return vidaMaxima; }
    public int getManaMaxima() { return manaMaxima; }

        public void setVidaMaxima(int vidaMaxima) { this.vidaMaxima = vidaMaxima; }
    public void setManaMaxima(int manaMaxima) { this.manaMaxima = manaMaxima; }

        public void adicionarOuro(int quantidade) { GerenciadorDeOuroEDeslocamento.adicionarOuro(this, quantidade); }

        public boolean gastarOuro(int quantidade) { return GerenciadorDeOuroEDeslocamento.gastarOuro(this, quantidade); }

        public static int getXpNecessaria(int nivel) { return GerenciadorDeCrescimento.getXpNecessaria(nivel); }

        public int adicionarXp(int quantidade) { return GerenciadorDeCrescimento.adicionarXp(this, quantidade); }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

        public void adicionarItem(ItemRpg novoItem) { GerenciadorDeInventarioFicha.adicionarItem(this, novoItem); }

            public double getCapacidadeMochila() { return GerenciadorDeInventarioFicha.getCapacidadeMochila(this); }

        public double getPesoTotalMochila() { return GerenciadorDeInventarioFicha.getPesoTotalMochila(this); }

        public double getEspacoLivreMochila() { return GerenciadorDeInventarioFicha.getEspacoLivreMochila(this); }

        public boolean tentarAdicionarItem(ItemRpg novoItem) { return GerenciadorDeInventarioFicha.tentarAdicionarItem(this, novoItem); }

            public int adicionarItemLimitado(ItemRpg novoItem) { return GerenciadorDeInventarioFicha.adicionarItemLimitado(this, novoItem); }

            public void coletarItemEncontrado(ItemRpg item, String origem) { GerenciadorDeInventarioFicha.coletarItemEncontrado(this, item, origem); }

        public boolean removerItem(String nome, int quantidade) { return GerenciadorDeInventarioFicha.removerItem(this, nome, quantidade); }

            public void consumirItem(ItemRpg item, int quantidade) { GerenciadorDeInventarioFicha.consumirItem(this, item, quantidade); }

        public void equiparMelhorArmadura() { GerenciadorDeInventarioFicha.equiparMelhorArmadura(this); }

        public boolean temItem(String nome) { return GerenciadorDeInventarioFicha.temItem(this, nome); }

        public String aumentarAtributoAleatorio() { return GerenciadorDeCrescimento.aumentarAtributoAleatorio(this); }

        public String aumentarAtributo(int opcao) { return GerenciadorDeCrescimento.aumentarAtributo(this, opcao); }

            public void aumentarConstituicao(int quantidade) { GerenciadorDeCrescimento.aumentarConstituicao(this, quantidade); }

        public void aumentarTodosAtributos(int quantidade) { GerenciadorDeCrescimento.aumentarTodosAtributos(this, quantidade); }

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

        public int getDistanciaAte(int profundidadeAlvo) { return GerenciadorDeLocalizacao.getDistanciaAte(this, profundidadeAlvo); }

                public int getLocalizacaoAtual() { return GerenciadorDeLocalizacao.getLocalizacaoAtual(this); }

        public int getLocalizacaoSala() { return GerenciadorDeLocalizacao.getLocalizacaoSala(this); }

        public int getLocalizacaoMesa() { return GerenciadorDeLocalizacao.getLocalizacaoMesa(this); }

            public int getLocalizacaoFogueira() { return GerenciadorDeLocalizacao.getLocalizacaoFogueira(this); }

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
    public void adicionarNovidade(String missao, String texto) {
        missoesNovidades.computeIfAbsent(missao, k -> new ArrayList<>()).add(texto);
    }
    public List<String> getNovidades(String missao) {
        List<String> n = missoesNovidades.get(missao);
        return n == null ? List.of() : n;
    }
    public boolean temNovidadeNaoVista(String missao) {
        for (String s : getNovidades(missao)) {
            if (s.startsWith("!")) return true;
        }
        return false;
    }
    public void marcarNovidadesVistas(String missao) {
        List<String> n = missoesNovidades.get(missao);
        if (n == null) return;
        List<String> limpas = new ArrayList<>();
        for (String s : n) {
            limpas.add(s.startsWith("!") ? s.substring(1) : s);
        }
        missoesNovidades.put(missao, limpas);
    }
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (missoesAceitas == null) missoesAceitas = new ArrayList<>();
        if (missoesNovidades == null) missoesNovidades = new HashMap<>();
    }
    public boolean isAlfaiatariaConhecida() { return alfaiatariaConhecida; }
    public void setAlfaiatariaConhecida(boolean alfaiatariaConhecida) { this.alfaiatariaConhecida = alfaiatariaConhecida; }
    public boolean isCaveConhecida() { return caveConhecida; }
    public void setCaveConhecida(boolean caveConhecida) { this.caveConhecida = caveConhecida; }
    public boolean isVelhinhaEncontrada() { return velhinhaEncontrada; }
    public void setVelhinhaEncontrada(boolean velhinhaEncontrada) { this.velhinhaEncontrada = velhinhaEncontrada; }
    public boolean isFilhaEncontrada() { return filhaEncontrada; }
    public void setFilhaEncontrada(boolean filhaEncontrada) { this.filhaEncontrada = filhaEncontrada; }
    public boolean isNetaEncontrada() { return netaEncontrada; }
    public void setNetaEncontrada(boolean netaEncontrada) { this.netaEncontrada = netaEncontrada; }
    public int getDiaAceitouNeta() { return diaAceitouNeta; }
    public void setDiaAceitouNeta(int diaAceitouNeta) { this.diaAceitouNeta = diaAceitouNeta; }
    public boolean isNetaMorta() { return netaMorta; }
    public void setNetaMorta(boolean netaMorta) { this.netaMorta = netaMorta; }
    public boolean isTrilhaIniciada() { return trilhaIniciada; }
    public void setTrilhaIniciada(boolean trilhaIniciada) { this.trilhaIniciada = trilhaIniciada; }
    public boolean isPegadasEncontradas() { return pegadasEncontradas; }
    public void setPegadasEncontradas(boolean pegadasEncontradas) { this.pegadasEncontradas = pegadasEncontradas; }
    public boolean isBandoVencido() { return bandoVencido; }
    public void setBandoVencido(boolean bandoVencido) { this.bandoVencido = bandoVencido; }
    public boolean isGaiolaVasculhada() { return gaiolaVasculhada; }
    public void setGaiolaVasculhada(boolean gaiolaVasculhada) { this.gaiolaVasculhada = gaiolaVasculhada; }
    public boolean isMagicoMacabroDerrotado() { return magicoMacabroDerrotado; }
    public void setMagicoMacabroDerrotado(boolean v) { this.magicoMacabroDerrotado = v; }
    public boolean isCabanaVisitada() { return cabanaVisitada; }
    public void setCabanaVisitada(boolean v) { this.cabanaVisitada = v; }
    public boolean isNetaSeguindo() { return netaSeguindo; }
    public void setNetaSeguindo(boolean v) { this.netaSeguindo = v; }
    public boolean isAcampamentoAlcancado() { return acampamentoAlcancado; }
    public void setAcampamentoAlcancado(boolean v) { this.acampamentoAlcancado = v; }
    public boolean isCabanaAlcancada() { return cabanaAlcancada; }
    public void setCabanaAlcancada(boolean v) { this.cabanaAlcancada = v; }
    public boolean isNetaCorpoLevado() { return netaCorpoLevado; }
    public void setNetaCorpoLevado(boolean v) { this.netaCorpoLevado = v; }
    public boolean isPresencaNetaPassou() { return presencaNetaPassou; }
    public void setPresencaNetaPassou(boolean v) { this.presencaNetaPassou = v; }
    public boolean isMissaoNetaEncerrada() { return missaoNetaEncerrada; }
    public void setMissaoNetaEncerrada(boolean v) { this.missaoNetaEncerrada = v; }
    public int getTurnosParaVoltar() { return turnosParaVoltar; }
    public void setTurnosParaVoltar(int v) { this.turnosParaVoltar = v; }
    public static final int PRAZO_MISSAO_NETA = 7;
    public boolean isPrazoNetaEstourado() { return diaAceitouNeta > 0 && (diaAtual - diaAceitouNeta) >= PRAZO_MISSAO_NETA; }

    public boolean isPactoMortalAtivo() { return pactoMortalAtivo; }
    public void setPactoMortalAtivo(boolean pactoMortalAtivo) { this.pactoMortalAtivo = pactoMortalAtivo; }

            public boolean isLabirintoDisponivel() { return GerenciadorDeVida.isLabirintoDisponivel(this); }

            public int getLabirintoChanceDescoberta() { return GerenciadorDeVida.getLabirintoChanceDescoberta(this); }

            public int getProfundidadeFloresta() { return profundidadeFloresta; }

        public boolean isNoVilarejo() { return profundidadeFloresta >= PROFUNDIDADE_PARA_SAIR; }

    public String getCidadeAtual() { return cidadeAtual; }
    public void setCidadeAtual(String cidadeAtual) { this.cidadeAtual = cidadeAtual; }

        public void adicionarProfundidade(int unidades) { GerenciadorDeOuroEDeslocamento.adicionarProfundidade(this, unidades); }

        public void reduzirProfundidade(int unidades) { GerenciadorDeOuroEDeslocamento.reduzirProfundidade(this, unidades); }

            public void sincronizarLocalizacao() { GerenciadorDeConstrucoes.sincronizarLocalizacao(this); }

                        public void entrarNaConstrucao(int profundidadeAlvo) { GerenciadorDeConstrucoes.entrarNaConstrucao(this, profundidadeAlvo); }

            public void recomputarAdjacencias() { GerenciadorDeConstrucoes.recomputarAdjacencias(this); }

        public void sairDaCabana() { GerenciadorDeConstrucoes.sairDaCabana(this); }

        public void voltarParaCabana() { GerenciadorDeConstrucoes.voltarParaCabana(this); }

            public void irParaSalaTreino() { GerenciadorDeConstrucoes.irParaSalaTreino(this); }

            public void irParaMesaMagias() { GerenciadorDeConstrucoes.irParaMesaMagias(this); }

            public void irParaFogueira() { GerenciadorDeConstrucoes.irParaFogueira(this); }

            public boolean avancarTempo(int unidades) { return GerenciadorDeConstrucoes.avancarTempo(this, unidades); }

                public boolean dormir() { return GerenciadorDeConstrucoes.dormir(this); }

        public int getDiasSemComer() { return diasSemComer; }
        public boolean isComeuHoje() { return comeuHoje; }
        public boolean isEnjoado() { return enjoado; }

        public String descreverFome() { return GerenciadorDeConstrucoes.descreverFome(this); }

            public void registrarNovoDiaFome() { GerenciadorDeConstrucoes.registrarNovoDiaFome(this); }

                public int aplicarPerdaVidaPorFome() { return GerenciadorDeConstrucoes.aplicarPerdaVidaPorFome(this); }

            public void comerComidaBoa() { GerenciadorDeConstrucoes.comerComidaBoa(this); }

        public void curarEnjoo() { GerenciadorDeConstrucoes.curarEnjoo(this); }

                    public void comerCarnePodre() { GerenciadorDeConstrucoes.comerCarnePodre(this); }

                public boolean comerCarneCrua() { return GerenciadorDeConstrucoes.comerCarneCrua(this); }

                public void comerFrutas(int qtd) { GerenciadorDeConstrucoes.comerFrutas(this, qtd); }

    public int getFrutasComidasHoje() { return frutasComidasHoje; }

            public boolean montarCabana() { return GerenciadorDeConstrucoes.montarCabana(this); }

                    public boolean construirSalaTreino() { return GerenciadorDeConstrucoes.construirSalaTreino(this); }

            public void entrarSalaTreino() { GerenciadorDeConstrucoes.entrarSalaTreino(this); }

        public void treinarAtributo(String atributo) { GerenciadorDeConstrucoes.treinarAtributo(this, atributo); }

            public void terminarTreino() { GerenciadorDeConstrucoes.terminarTreino(this); }

            public boolean construirMesaMagias() { return GerenciadorDeConstrucoes.construirMesaMagias(this); }

            public boolean montarFogueira() { return GerenciadorDeConstrucoes.montarFogueira(this); }

                    public boolean cozinharTodasAsCarnes() { return GerenciadorDeConstrucoes.cozinharTodasAsCarnes(this); }

        public void cozinharTipoCarne(String crua, String cozida, int qtd) { GerenciadorDeConstrucoes.cozinharTipoCarne(this, crua, cozida, qtd); }

            public boolean moverCabana() { return GerenciadorDeConstrucoes.moverCabana(this); }
    public boolean moverSalaTreino() { return GerenciadorDeConstrucoes.moverSalaTreino(this); }
    public boolean moverMesaMagias() { return GerenciadorDeConstrucoes.moverMesaMagias(this); }
    public boolean moverFogueira() { return GerenciadorDeConstrucoes.moverFogueira(this); }

            public int getProfundidadeConstrucaoMaisProxima() { return GerenciadorDeConstrucoes.getProfundidadeConstrucaoMaisProxima(this); }

            public void estudarMagia() { GerenciadorDeConstrucoes.estudarMagia(this); }

        public int getQuantidadeDe(String nome) { return GerenciadorDeInventarioFicha.getQuantidadeDe(this, nome); }

                public int bonusTestesNoturnos() { return GerenciadorDeVida.bonusTestesNoturnos(this); }
            public int getPenalidadeFome() { return GerenciadorDeVida.getPenalidadeFome(this); }
            public int getPerdaVidaPorFome() { return GerenciadorDeVida.getPerdaVidaPorFome(this); }
    public int getDestrezaTeste() { return getDestreza() - (cansado ? 1 : 0) - getPenalidadeFome() + bonusTestesNoturnos() + (temItem("Botas de Correio") ? 1 : 0); }
    public int getPresencaTeste() { return presenca - (cansado ? 1 : 0) + bonusTestesNoturnos(); }
    public int getSabedoriaTeste() { return sabedoria - (cansado ? 1 : 0) + bonusTestesNoturnos(); }
    public int getForcaTeste() { return getForca() - (cansado ? 1 : 0) - getPenalidadeFome() + bonusTestesNoturnos(); }
    public int getIntelectoTeste() { return intelecto - (cansado ? 1 : 0) + bonusTestesNoturnos(); }
    public int getConstituicaoTeste() { return constituicao - (cansado ? 1 : 0) + bonusTestesNoturnos(); }

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
    public boolean isSangrando() { return sangrando; }
    public void setSangrando(boolean sangrando) { this.sangrando = sangrando; }

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

            public void receberDano(int dano) { GerenciadorDeVida.receberDano(this, dano); }
}
