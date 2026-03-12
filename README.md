# 🎲 RPG de dados com ficha e ainda mais

Este é um projeto desenvolvido em **Java** para criar um RPG de dado com fichas personalizadas de RPG para diversão própria. O sistema permite que o jogador distribua pontos de atributos e escolha uma classe, calculando automaticamente os status de Vida e Mana baseados nessas escolhas.

## 🚀 Funcionalidades

* **Customização de Nome:** Defina o nome do personagem e do jogador.
* **Distribuição de Atributos:** Sistema de pontos limitados (6 pontos) para distribuir entre:
    * Constituição, Destreza, Força, Sabedoria, Intelecto e Presença.
* **Seleção de Classe:** Escolha entre três classes com modificadores únicos:
    * **Mago:** Foco em Intelecto e Mana, porém com penalidade em Constituição.
    * **Guerreiro:** Alta Vida e Força, mas com Intelecto reduzido.
    * **Healer:** Equilíbrio entre cura e suporte, com bônus em Sabedoria.
* **Cálculo Automático:** O sistema gera os valores finais de Vida e Mana instantaneamente após a escolha da classe.

## 🛠️ Tecnologias e Conceitos Aplicados

* **Linguagem:** Java.
* **Entrada de Dados:** `Scanner` com tratamento de limpeza de buffer.
* **Estruturas de Controle:** `while`, `switch-case` e condicionais `if-else`.
* **Lógica de RPG:** Modificadores de atributos influenciando variáveis de status.

## 🎮 Como Rodar o Projeto

1. Certifique-se de ter o [JDK](https://www.oracle.com/java/technologies/downloads/) instalado.
2. Clone este repositório ou baixe o arquivo `fichaRpg.java`.
3. No terminal, execute o arquivo:
   ```bash
   javac Main.java
4. Execute:
   ```bash
   java Main
