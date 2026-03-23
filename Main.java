import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // pontos de atributo
        int totalDePontos = 6;
        int escolhaDeGastos;
        int gastoDePontos;
        int escolhaInterface = 0;

        // classe
        int classe = 0;

        // interface carregando barra
        Interface.BarraCarregamento("Carregando jogo...");

        // interface de boas vindas
        Interface.ExibirBoasVindas();

        // escolha do nome do dono da ficha
        System.out.print("Qual seu nome?");
        String nomePessoa = scanner.nextLine();

        // criação do objeto da ficha
        FichaRpg fichaRpg = new FichaRpg(nomePessoa);

        // interface de escolha para criar a ficha
        while(escolhaInterface != 5) {

            System.out.println("\n");
            Interface.barraDivisoria();

            System.out.println("O que deseja fazer entre as seguintes opções?");
            System.out.println("\n1.Escolher nome do personagem/alterar");
            System.out.println("\n2.Distribuir pontos entre atributos/mudar pontos");
            System.out.println("\n3.Escolher classe/mudar classe");
            System.out.println("\n4.Mostrar ficha");
            System.out.println("\n5.Encerrar sistema");
            escolhaInterface = scanner.nextInt();

            fichaRpg.EscolhaInterface(escolhaInterface);
        }

    }
}
