import re

with open('README.md', 'r', encoding='utf-8') as f:
    content = f.read()

target = """## Companheiros

Depois que você **constrói a cabana**, a cada troca de período (dia/noite) há **20% de chance** de uma **pessoa perdida** aparecer pedindo abrigo. Você pode acolhê-la ou recusar. Se já tiver um companheiro, o visitante vai embora.

O companheiro é gerado com nome, classe aleatória (Mago/Guerreiro/Healer), nível 1 ou 2 e 6 pontos de atributos sorteados.

**Atenção — ele vai embora:** cada noite que dorme na cabana, o companheiro recupera metade da vida/mana. Na **primeira noite**, um contador de `1–3 dias` é sorteado; quando ele zera, **na manhã seguinte o companheiro agradece e parte para sempre.**

Enquanto estiver com você, o companheiro:

- Aparece no topo do menu (nome, classe e nível).
- Luta ao seu lado (iniciativa própria, agindo sozinho).
- **Conversar** mostra falas da classe e as habilidades dele.
- **Ver itens** mostra o inventário dele.
- **Curar com Kit Médico** cura ele em 1d4 (consome 1 do seu Kit). Ele também dorme na cabana à noite.
- **Despedir-se** (menu de conversa) faz o companheiro se despedir na hora e seguir o próprio caminho — útil se você quiser se livrar dele antes da hora (não há segunda chance).
- **Lutar contra** (menu de conversa): Se a convivência acabar, você pode escolher atacá-mo. É uma luta até a morte: vencendo, você ganha o XP dele e pega todos os itens e o ouro que ele carregava.

<details>
<summary>⚠️ <b>Spoiler: A verdadeira índole dos companheiros</b> — clique para revelar</summary>

Nem todos que pedem abrigo têm boas intenções. O jogo sorteia secretamente se o seu companheiro é do bem ou do mal:

- **Companheiros do bem:** comportam-se normalmente e vão embora pacificamente quando o tempo deles acaba.
- **Companheiros do mal:** em qualquer momento durante a exploração na floresta, eles têm uma chance de te trair, atacando-o de surpresa. Pior ainda: ao dormir, existe uma pequena chance de que o companheiro roube metade do seu ouro e fuja silenciosamente para a mata!

Se você sobreviver à traição e derrotá-lo em combate, você recupera o ouro roubado (e fica com os pertences dele).

</details>"""

replacement = """## Companheiros

Depois que você **constrói a cabana**, a cada troca de período (dia/noite) há **20% de chance** de uma **pessoa perdida** aparecer pedindo abrigo. Você pode acolhê-la ou recusar. Se já tiver um companheiro, o visitante vai embora.

O companheiro é gerado com nome, classe aleatória (Mago/Guerreiro/Healer), nível 1 ou 2 e 6 pontos de atributos sorteados.

Enquanto estiver com você, o companheiro:

- Luta ao seu lado (iniciativa própria, agindo sozinho).
- Aparece no topo do menu da floresta (nome, classe e nível).
- **Conversar** permite saber mais sobre sua classe, habilidades, além de ver seus itens e status de convivência.
- **Curar** (via Kit Médico) restaura a vida dele.
- **Despedir-se** faz com que ele vá embora pacificamente.
- **Lutar contra:** A convivência na floresta não tem leis. Você pode escolher atacá-lo de surpresa; numa luta até a morte, se vencer, você ganha o XP dele e saqueia todos os itens e ouro que carregava.

### Lealdade, Necessidades e Avaliação
Seu companheiro avalia como é viajar com você. A **Afinidade** (lealdade) começa em 30% e dita se ele continuará ao seu lado ou irá embora. 

- **A Cada 2 a 4 dias**, ele avalia a jornada. Se a Afinidade estiver abaixo de 50%, ele vai embora. Se estiver entre 50% e 99%, ele renova sua estadia por mais 2 a 4 dias. Se chegar a 100%, ele se torna um **companheiro permanente** e não te deixa mais!
- **Necessidades:** Companheiros sentem fome e sono. Ao passar do tempo sem dormir ou comer, perdem afinidade. Você deve **alimentá-los** (usando as comidas do seu inventário no menu de conversa) e dormir na cabana (onde também recuperam vida/mana). Se ficarem muito tempo sem comer ou dormir, vão reclamar em voz alta no acampamento.

<details>
<summary>⚠️ <b>Spoiler: A verdadeira índole dos companheiros</b> — clique para revelar</summary>

Nem todos que pedem abrigo têm boas intenções. O jogo sorteia secretamente se o seu companheiro é do bem ou do mal:

- **Companheiros do mal:** fingem ganhar afinidade e se comportam como aliados, mas nunca se tornarão companheiros permanentes. O verdadeiro objetivo deles é ganhar sua confiança para te roubar.
- **Traição e Roubo:** Enquanto exploram, há chance de ele se virar contra você num ataque surpresa. Ao dormir, ele pode roubar furtivamente parte do seu ouro e fugir para a floresta durante a noite.
- **Pistas:** Fique atento. Um companheiro do mal às vezes vai dar dicas de suas intenções, como olhar fixamente para sua bolsa de moedas disfarçadamente durante o dia.

Se você sobreviver à traição e derrotá-lo em combate, ou o caçar após ser roubado, recupera o ouro roubado (e fica com os pertences dele).

</details>"""

if target in content:
    content = content.replace(target, replacement)
    with open('README.md', 'w', encoding='utf-8') as f:
        f.write(content)
    print("README.md patched")
else:
    print("Target text not found in README.md")
