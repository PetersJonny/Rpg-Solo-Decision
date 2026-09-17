package fichas;

// Modo de dificuldade da partida: controla o que acontece com os saves ao morrer.
public enum ModoDificuldade {
    NORMAL("Normal", "Se o personagem morrer, seus saves são mantidos."),
    DIFICIL("Difícil", "Morte permanente: se o personagem morrer, o save dele é apagado.");

    private final String rotulo;
    private final String descricao;

    ModoDificuldade(String rotulo, String descricao) {
        this.rotulo = rotulo;
        this.descricao = descricao;
    }

    public String getRotulo() {
        return rotulo;
    }

    public String getDescricao() {
        return descricao;
    }
}