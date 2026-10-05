package companheiros;

import classes.Guerreiro;
import classes.Healer;
import classes.Mago;
import fichas.FichaRpg;
import itens.ItemRpg;
import java.util.List;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class Companheiro implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private static final String[] PRIMEIROS_NOMES = {
        "Alaric", "Bianca", "Cedric", "Dara", "Ernesto", "Fiona", "Gael", "Helena",
        "Igor", "Janaína", "Kael", "Lívia", "Márcio", "Nívia", "Orfeu", "Paulo",
        "Rafaela", "Sandro", "Talita", "Ugo", "Vânia", "Waldir", "Yara", "Zenon"
    };

    private static final String[] SOBRENOMES = {
        "Alves", "Barbosa", "Carvalho", "Dias", "Esteves", "Ferreira", "Gomes",
        "Henrique", "Ivelar", "Jardim", "Kraus", "Lopes", "Marques", "Nogueira",
        "Ortega", "Pereira", "Queiroz", "Rocha", "Silva", "Teixeira", "Uchoa",
        "Vieira", "Wiese", "Xavier", "Zanin", "Arantes", "Beltrão"
    };

    private final String nome;
    private final String sobrenome;
    private final int nivel;
    private final FichaRpg ficha;

        private boolean dormiuPrimeiraVez = false;
    private int diasRestantes = 0;
    private boolean partindo = false;
    private final boolean doMal;
    private int ouroRoubado = 0;
    
    private int afinidade = 30; // Starts at 30
    private boolean permanente = false;
    private int diasSemComer = 0;
    private int diasSemDormir = 0;

    public Companheiro() {
        this.nome = sortearNome();
        this.sobrenome = sortearSobrenome();
        this.nivel = MecanicasRpg.rolarEntre(1, 2);
        this.ficha = gerarFicha();
        this.doMal = MecanicasRpg.rolarDado(100) <= 15;
    }

    private static String sortearNome() {
        return PRIMEIROS_NOMES[MecanicasRpg.rolarDado(PRIMEIROS_NOMES.length) - 1];
    }

    private static String sortearSobrenome() {
        return SOBRENOMES[MecanicasRpg.rolarDado(SOBRENOMES.length) - 1];
    }

                private FichaRpg gerarFicha() {
        FichaRpg f = new FichaRpg("");
        for (int i = 0; i < 6; i++) {
            f.adicionarAtributo(MecanicasRpg.rolarDado(6), 1);
        }

        int classeSorteada = MecanicasRpg.rolarDado(3);
        if (classeSorteada == 1) {
            String[] elementos = {"Fogo", "Água", "Gelo", "Elétrico", "Terra", "Ácido"};
            String elemento = elementos[MecanicasRpg.rolarDado(6) - 1];
            f.setClasse(new Mago(elemento));
        } else if (classeSorteada == 2) {
            f.setClasse(new Guerreiro());
        } else {
            f.setClasse(new Healer());
        }

        int racaSorteada = MecanicasRpg.rolarDado(7);
        if (racaSorteada == 1) {
            f.setRaca(new racas.HumanoRaca());
        } else if (racaSorteada == 2) {
            f.setRaca(new racas.ElfoDaFlorestaRaca());
        } else if (racaSorteada == 3) {
            f.setRaca(new racas.VigiaDoCrepusculoRaca());
        } else if (racaSorteada == 4) {
            f.setRaca(new racas.MeioFadaRaca());
        } else if (racaSorteada == 5) {
            f.setRaca(new racas.DraconicoRaca());
        } else if (racaSorteada == 6) {
            f.setRaca(new racas.MeioOrqueRaca());
        } else {
            f.setRaca(new racas.GnomoRaca());
        }

        if (nivel == 2) {
            f.adicionarXp(100);         }
        return f;
    }

            public void aoDormir() {
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + ficha.getVidaMaxima() / 3, ficha.getVidaMaxima()));
        ficha.setManaPersonagem(Math.min(ficha.getManaPersonagem() + ficha.getManaMaxima() / 3, ficha.getManaMaxima()));

        if (!dormiuPrimeiraVez) {
            dormiuPrimeiraVez = true;
            diasRestantes = MecanicasRpg.rolarEntre(1, 3);
            partindo = false;
            return;
        }
        diasRestantes--;
        if (diasRestantes <= 0) {
            partindo = true;
        }
    }

    public boolean isPartindo() { return partindo; }
    public boolean isDoMal() { return doMal; }
    public int getOuroRoubado() { return ouroRoubado; }
    public void adicionarOuroRoubado(int amount) { this.ouroRoubado += amount; }
    public boolean isDormiuPrimeiraVez() { return dormiuPrimeiraVez; }
    public int getDiasRestantes() { return diasRestantes; }

    public String getNome() { return nome; }
    public String getSobrenome() { return sobrenome; }
    public String getNomeCompleto() { return nome + " " + sobrenome; }
    public int getNivel() { return nivel; }
    public FichaRpg getFicha() { return ficha; }

    public String getClasseNome() {
        if (ficha.getClasseDoPersonagem() == null) return "Nenhuma";
        return ficha.getClasseDoPersonagem().getNome();
    }

    public String getRacaNome() {
        if (ficha.getRaca() == null) return "Nenhuma";
        return ficha.getRaca().getNome();
    }

    public void mostrarResumo() {
        Interface.MostrarMensagem("\n  Nome: " + nome + " " + sobrenome);
        Interface.MostrarMensagem("  Classe: " + getClasseNome() + " | Raça: " + getRacaNome() + " | Nível: " + ficha.getNivel());
        Interface.MostrarMensagem("  Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + " | Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
        if (permanente) {
            Interface.MostrarMensagem("  Confia em você com a própria vida. É seu parceiro permanente.");
        } else if (dormiuPrimeiraVez) {
            Interface.MostrarMensagem("  Está te avaliando. Faltam " + diasRestantes + " dia(s) para decidir o futuro.");
            String humor = (afinidade >= 80) ? "Muito amigável" : (afinidade >= 50 ? "Neutro" : "Desconfiado");
            Interface.MostrarMensagem("  Status de Afinidade: " + afinidade + "% (" + humor + ")");
        } else {
            Interface.MostrarMensagem("  Ainda não dormiu na cabana; vai decidir o futuro depois da primeira noite.");
        }
        
        if (diasSemComer > 0) Interface.MostrarMensagem("  Fome: " + diasSemComer + " dias sem comer.");
        if (diasSemDormir > 0) Interface.MostrarMensagem("  Cansaço: " + diasSemDormir + " dias sem dormir na cabana.");
    }

    public void mostrarItens() {
        System.out.println("\n  " + nome + " mostra o que carrega consigo:");
        List<ItemRpg> itens = ficha.getInventario();
        if (itens.isEmpty()) {
            System.out.println("  - Vazio");
            return;
        }
        for (ItemRpg item : itens) {
            System.out.println("  - " + item.getNome() + " (x" + item.getQuantidade() + "): " + item.getDescricao());
        }
    }

        public void falarSobreClasse() {
        Interface.MostrarMensagem("\"" + nome + " conta um pouco sobre como vive na floresta...\"");
        Interface.Pausa(1500);

        if (ficha.getClasseDoPersonagem() instanceof Mago) {
            Interface.MostrarMensagem("\"" + nome + ": Sempre fui fascinado(a) pelas forças da natureza. Meu elemento corre nas minhas veias e arde em meus dedos.\"");
            Interface.Pausa(1800);
            Interface.MostrarMensagem("\"Se precisar, posso te ajudar com as minhas magias... contanto que eu tenha meu cajado por perto.\"");
            Interface.Pausa(1800);
        } else if (ficha.getClasseDoPersonagem() instanceof Guerreiro) {
            Interface.MostrarMensagem("\"" + nome + ": A floresta é dura, mas eu sou mais. Aprendi desde cedo que só o braço forte e a espada garantem o dia de amanhã.\"");
            Interface.Pausa(1800);
            Interface.MostrarMensagem("\"Se um urso cruzar nosso caminho, fica comigo — eu abro o caminho.\"");
            Interface.Pausa(1800);
        } else if (ficha.getClasseDoPersonagem() instanceof Healer) {
            Interface.MostrarMensagem("\"" + nome + ": Meu dom é outro: onde outros veem feridas, eu vejo a chance de curar. Levo meu Kit Médico sempre comigo.\"");
            Interface.Pausa(1800);
            Interface.MostrarMensagem("\"Se você estiver ferido, me chame — enquanto eu tiver fôlego, ninguém sangra sozinho sob minha guarda.\"");
            Interface.Pausa(1800);
        } else {
            Interface.MostrarMensagem("\"Eu só sei sobreviver, e você me deu um teto. Isso já é tudo para mim.\"");
            Interface.Pausa(1800);
        }

        Interface.MostrarMensagem("\n  Ela(e) também conta o que consegue fazer:");
        List<habilidades.Habilidade> habs = ficha.getHabilidades();
        if (habs.isEmpty()) {
            System.out.println("  - Nenhuma");
            return;
        }
        for (habilidades.Habilidade hab : habs) {
            System.out.println("  - " + hab.getNome() + " (Custo: " + hab.getCustoMana() + " Mana): " + hab.getDescricao());
        }
    }

    public void passarTempo() {
        diasSemComer++;
        diasSemDormir++;
        if (diasSemComer >= 3) alterarAfinidade(-10);
        if (diasSemDormir >= 3) alterarAfinidade(-5);
        if (diasSemComer > 0 && mecanicas.MecanicasRpg.rolarDado(100) <= 20) {
            telas.Interface.MostrarMensagem("\n(A barriga de " + nome + " ronca alto. \"Tem algo para comer?\", ele(a) pergunta.)");
            telas.Interface.Pausa(1500);
        }
        if (diasSemDormir > 0 && mecanicas.MecanicasRpg.rolarDado(100) <= 20) {
            telas.Interface.MostrarMensagem("\n(" + nome + " boceja pesado. \"Precisamos descansar na cabana...\")");
            telas.Interface.Pausa(1500);
        }
        if (doMal && mecanicas.MecanicasRpg.rolarDado(100) <= 15) {
            telas.Interface.MostrarMensagem("\n(Você percebe " + nome + " olhando fixamente para a sua bolsa de moedas... mas logo disfarça.)");
            telas.Interface.Pausa(1500);
        }
    }
    
    public void alimentar() {
        diasSemComer = 0;
        alterarAfinidade(15);
    }
    
    public void alterarAfinidade(int valor) {
        if (!doMal && !permanente) {
            this.afinidade += valor;
            if (this.afinidade > 100) this.afinidade = 100;
            if (this.afinidade < 0) this.afinidade = 0;
        } else if (doMal) {
            this.afinidade += valor;
            if (this.afinidade > 99) this.afinidade = 99;
            if (this.afinidade < 0) this.afinidade = 0;
        }
    }
    
    public int getDiasSemComer() { return diasSemComer; }
    public int getDiasSemDormir() { return diasSemDormir; }
    public int getAfinidade() { return afinidade; }
    public boolean isPermanente() { return permanente; }
}
