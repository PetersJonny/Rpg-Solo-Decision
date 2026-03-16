public class FichaRpg {

    // variáveis base do personagem
    public String nomePersonagem, nomePessoa;
    public String classeEscolhida;
    public int vidaPersonagem;
    public int manaPersonagem;

    // atributos base
    public int constituicao, destreza, forca, sabedoria, intelecto, presenca;

    // armas de cada classe
    public String arma; 
    public String danoArma;
    public String tipoArma;

    // definir nome do personagem e dono da ficha
    public FichaRpg(String nomePersonagem, String nomePessoa) {
        this.nomePersonagem = nomePersonagem;
        this.nomePessoa = nomePessoa;
    }

    // distribuição de atributos base
    public void DistribuirAtributos(int opcao, int pontos) {
        switch (opcao) {
            case 1 -> constituicao += pontos;
            case 2 -> destreza += pontos;
            case 3 -> forca += pontos;
            case 4 -> sabedoria += pontos;
            case 5 -> intelecto += pontos;
            case 6 -> presenca += pontos;
        }
    }

    // definir coisas da classe
    public void EscolhaDaClasse(int classe) {
        switch (classe) {
            case 1: // Mago
                this.classeEscolhida = "Mago";
                this.vidaPersonagem = 10 + constituicao;
                this.manaPersonagem = 8 + presenca;
                this.constituicao -= 2;
                this.presenca += 2;
                this.intelecto += 1;
                this.arma = "cajado";
                this.danoArma = "1d4";
                this.tipoArma = "CaC/mágico";
                break;
            case 2: // Guerreiro
                this.classeEscolhida = "Guerreiro";
                this.vidaPersonagem = 20 + constituicao;
                this.manaPersonagem = 2 + presenca;
                this.constituicao += 2;
                this.forca += 1;
                this.intelecto -= 2;
                this.arma = "espada";
                this.danoArma = "1d8";
                this.tipoArma = "CaC";
                break;
            case 3: // Healer
                this.classeEscolhida = "Healer";
                this.vidaPersonagem = 14 + constituicao;
                this.manaPersonagem = 5 + presenca;
                this.sabedoria += 1;
                this.intelecto += 2;
                this.forca -= 2;
                this.arma = "arco";
                this.tipoArma = "LA";
                this.danoArma = "1d6";
                break;
        }
    }

    // mostrar ficha
    public void mostrarFicha() {
        System.out.println("\n --------FICHA-------- \n\nNome: " + nomePersonagem + "\t\tDono da ficha: " + nomePessoa + "\t\tClasse: " + classeEscolhida + "\nVida: " + vidaPersonagem + "\t\tMana: " + manaPersonagem + "\n\nAtributos: \nConstituição: " + constituicao + "\nDestreza: " + destreza + "\nForça: " + forca + "\nSabedoria: " + sabedoria + "\nIntelecto: " + intelecto + "\nPresença: " + presenca + "\n\nCombate: \nArma: " + arma + "\tDano da arma: " + danoArma + "\t tipo da arma: " + tipoArma + "\n----------------------");
    }
}
