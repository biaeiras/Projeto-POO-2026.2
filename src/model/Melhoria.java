package model;

class Melhoria {
    private final TipoMelhoria tipo;
    private final CorMelhoria cor;
    private final TipoTesouro tesouro;

    private EstadoMelhoria estado;

    Melhoria(TipoMelhoria tipo, CorMelhoria cor) {
        this(tipo, cor, null);
    }

    Melhoria(TipoMelhoria tipo, CorMelhoria cor, TipoTesouro tesouro) {
        this.tipo = tipo;
        this.cor = cor;
        this.tesouro = tesouro;
        this.estado = EstadoMelhoria.RECEM_COMPRADA;
    }

    TipoMelhoria getTipo() {
        return tipo;
    }

    CorMelhoria getCor() {
        return cor;
    }

    TipoTesouro getTesouro() {
        return tesouro;
    }

    EstadoMelhoria getEstado() {
        return estado;
    }

    boolean podeSerUsada() {
        return estado == EstadoMelhoria.PRONTA;
    }

    void liberar() {
        estado = EstadoMelhoria.PRONTA;
    }

    void usar() {
        if (!podeSerUsada()) {
            throw new IllegalStateException(
                "Melhoria não pode ser usada agora"
            );
        }
        estado = EstadoMelhoria.USADA;
    }
}