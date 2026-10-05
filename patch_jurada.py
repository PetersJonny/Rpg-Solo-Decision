with open('src/main/java/mecanicas/GerenciadorDeAtaque.java', 'r', encoding='utf-8') as f:
    content = f.read()

target = """                if (armaEscolhida.getNome().equals("Espada Majestral")) {
                    int dadosLuz = critico ? 2 : 1;
                    int luz = 0;
                    for (int i = 0; i < dadosLuz; i++) {
                        luz += MecanicasRpg.rolarDado(4);
                    }
                    dano += luz;
                    Interface.MostrarMensagem("(Espada Majestral! +" + luz + " de dano de luz" + (dadosLuz > 1 ? " (crítico)" : "") + ")");
                    Interface.Pausa(1500);
                    if (inimigo.isMortoVivo()) {
                        dano *= 2;
                        Interface.MostrarMensagem("(Espada Majestral! DANO DOBRADO contra " + inimigo.getNome() + ", um morto-vivo)");
                        Interface.Pausa(1500);
                    }
                }"""

replacement = """                if (armaEscolhida.getNome().equals("Espada Majestral")) {
                    int dadosLuz = critico ? 2 : 1;
                    int luz = 0;
                    for (int i = 0; i < dadosLuz; i++) {
                        luz += MecanicasRpg.rolarDado(4);
                    }
                    dano += luz;
                    Interface.MostrarMensagem("(Espada Majestral! +" + luz + " de dano de luz" + (dadosLuz > 1 ? " (crítico)" : "") + ")");
                    Interface.Pausa(1500);
                    if (inimigo.isMortoVivo()) {
                        dano *= 2;
                        Interface.MostrarMensagem("(Espada Majestral! DANO DOBRADO contra " + inimigo.getNome() + ", um morto-vivo)");
                        Interface.Pausa(1500);
                    }
                }
                
                if (armaEscolhida.getNome().equals("Espada Jurada")) {
                    Interface.MostrarMensagem("\\n(A Espada Jurada ecoa com o poder do Vazio!)");
                    Interface.Pausa(1500);
                    int testeJogador = MecanicasRpg.rolarDado(20) + ficha.getForca();
                    int testeInimigo = MecanicasRpg.rolarDado(20) + inimigo.getNivel();
                    Interface.MostrarMensagem("-> Teste de Domínio (Você): d20 + " + ficha.getForca() + " (Força) = " + testeJogador);
                    Interface.MostrarMensagem("-> Teste de Resistência (" + inimigo.getNome() + "): d20 + " + inimigo.getNivel() + " (Nível) = " + testeInimigo);
                    Interface.Pausa(2000);
                    
                    if (testeJogador >= testeInimigo) {
                        int vazio1 = MecanicasRpg.rolarDado(6);
                        int vazio2 = MecanicasRpg.rolarDado(6);
                        int vazioTotal = vazio1 + vazio2;
                        dano += vazioTotal;
                        Interface.MostrarMensagem("-> " + inimigo.getNome() + " sucumbe ao vazio! Sofre +" + vazioTotal + " de dano!");
                    } else {
                        Interface.MostrarMensagem("-> " + inimigo.getNome() + " resiste ao efeito do vazio.");
                    }
                    Interface.Pausa(1500);
                }"""

if target in content:
    content = content.replace(target, replacement)
    with open('src/main/java/mecanicas/GerenciadorDeAtaque.java', 'w', encoding='utf-8') as f:
        f.write(content)
    print("GerenciadorDeAtaque patched successfully")
else:
    print("Target not found in GerenciadorDeAtaque.java")
