package companheiros;

import classes.Guerreiro;
import classes.Healer;
import classes.Mago;
import fichas.FichaRpg;
import itens.ItemRpg;
import java.util.List;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class Companheiro {

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

    // Controle de quanto tempo o companheiro fica com o jogador
    private boolean dormiuPrimeiraVez = false;
    private int diasRestantes = 0;
    private boolean partindo = false;

    public Companheiro() {
        this.nome = sortearNome();
        this.sobrenome = sortearSobrenome();
        this.nivel = MecanicasRpg.rolarEntre(1, 2);
        this.ficha = gerarFicha();
    }

    // ==================== GERAÇÃO ALEATÓRIA ====================

    private static String sortearNome() {
        return PRIMEIROS_NOMES[MecanicasRpg.rolarDado(PRIMEIROS_NOMES.length) - 1];
    }

    private static String sortearSobrenome() {
        return SOBRENOMES[MecanicasRpg.rolarDado(SOBRENOMES.length) - 1];
    }

    // Cria uma ficha completa igual à do jogador: distribui 6 pontos nos atributos,
    // sorteia uma classe (Mago/Guerreiro/Healer), aplica arma, itens e habilidades,
    // e sobe para o nível 2 (com bônus) quando a pessoa nasce no nível 2.
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

        if (nivel == 2) {
            f.adicionarXp(100); // sobe para o nível 2 (aplica bônus de vida/mana/habilidades)
        }
        return f;
    }

    // ==================== CONTROLE DE DIAS ====================

    // Chamado sempre que o companheiro dorme uma noite (na cabana).
    // A contagem de dias começa a partir da PRIMEIRA vez que ele dorme.
    public void aoDormir() {
        // Dormir na cabana recupera metade da vida e da mana (como o jogador)
        ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + ficha.getVidaMaxima() / 2, ficha.getVidaMaxima()));
        ficha.setManaPersonagem(Math.min(ficha.getManaPersonagem() + ficha.getManaMaxima() / 2, ficha.getManaMaxima()));

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
    public boolean isDormiuPrimeiraVez() { return dormiuPrimeiraVez; }
    public int getDiasRestantes() { return diasRestantes; }

    // ==================== GETTERS ====================

    public String getNome() { return nome; }
    public String getSobrenome() { return sobrenome; }
    public String getNomeCompleto() { return nome + " " + sobrenome; }
    public int getNivel() { return nivel; }
    public FichaRpg getFicha() { return ficha; }

    public String getClasseNome() {
        if (ficha.getClasseDoPersonagem() == null) return "Nenhuma";
        return ficha.getClasseDoPersonagem().getNome();
    }

    // ==================== EXIBIÇÃO (CONVERSAR) ====================

    public void mostrarResumo() {
        Interface.barraDivisoria();
        Interface.MostrarMensagem("\nNome: " + nome + " " + sobrenome);
        Interface.MostrarMensagem("Classe: " + getClasseNome() + " | Nível: " + ficha.getNivel());
        Interface.MostrarMensagem("Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + " | Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
        if (dormiuPrimeiraVez) {
            Interface.MostrarMensagem("Pretende ficar mais " + diasRestantes + " dia(s) contigo.");
        } else {
            Interface.MostrarMensagem("Ainda não dormiu na cabana; vai decidir o futuro depois da primeira noite.");
        }
    }

    public void mostrarItens() {
        System.out.println("\n" + nome + " mostra o que carrega consigo:");
        List<ItemRpg> itens = ficha.getInventario();
        if (itens.isEmpty()) {
            System.out.println("- Vazio");
            return;
        }
        for (ItemRpg item : itens) {
            System.out.println("- " + item.getNome() + " (x" + item.getQuantidade() + "): " + item.getDescricao());
        }
    }

    // A pessoa conta sobre si e sobre o que viveu — cada classe tem uma história própria
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

        Interface.MostrarMensagem("\nEla(e) também conta o que consegue fazer:");
        List<habilidades.Habilidade> habs = ficha.getHabilidades();
        if (habs.isEmpty()) {
            System.out.println("- Nenhuma");
            return;
        }
        for (habilidades.Habilidade hab : habs) {
            System.out.println("- " + hab.getNome() + " (Custo: " + hab.getCustoMana() + " Mana): " + hab.getDescricao());
        }
    }
}