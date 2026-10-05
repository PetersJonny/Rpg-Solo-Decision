import re

with open('src/main/java/companheiros/Companheiro.java', 'r', encoding='utf-8') as f:
    content = f.read()

# I will just append the methods before the last closing brace
new_methods = """
    public void passarTempo() {
        diasSemComer++;
        diasSemDormir++;
        if (diasSemComer >= 3) alterarAfinidade(-10);
        if (diasSemDormir >= 3) alterarAfinidade(-5);
        if (diasSemComer > 0 && mecanicas.MecanicasRpg.rolarDado(100) <= 20) {
            telas.Interface.MostrarMensagem("\\n(A barriga de " + nome + " ronca alto. \\"Tem algo para comer?\\", ele(a) pergunta.)");
            telas.Interface.Pausa(1500);
        }
        if (diasSemDormir > 0 && mecanicas.MecanicasRpg.rolarDado(100) <= 20) {
            telas.Interface.MostrarMensagem("\\n(" + nome + " boceja pesado. \\"Precisamos descansar na cabana...\\")");
            telas.Interface.Pausa(1500);
        }
        if (doMal && mecanicas.MecanicasRpg.rolarDado(100) <= 15) {
            telas.Interface.MostrarMensagem("\\n(Você percebe " + nome + " olhando fixamente para a sua bolsa de moedas... mas logo disfarça.)");
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
"""

pos = content.rfind('}')
content = content[:pos] + new_methods + content[pos:]

with open('src/main/java/companheiros/Companheiro.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("Companheiro.java patched with methods")
