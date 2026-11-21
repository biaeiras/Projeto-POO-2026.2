package model;

import java.util.Objects;
import java.util.Set;

public class Melhoria {

    private TipoMelhoria tipo;
	private Regiao regiao;
	
    public Melhoria(TipoMelhoria tipo, Regiao regiao) {
        this.tipo = tipo;
        this.regiao = regiao;
    }

    public Regiao getRegiao() {
        return regiao;
    }
    
    public TipoMelhoria getTipo() {
		return tipo;
	}


    public TipoAcao getTipoAcao() {
        switch (tipo) {
            case MOVIMENTO_EXTRA:
                return TipoAcao.MOVIMENTO;

            case PEGAR_TRIBUTO_EXTRA:
                return TipoAcao.PEGAR_TRIBUTO;

            default:
                return null;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Melhoria outra = (Melhoria) o;
        return tipo == outra.tipo
                && regiao == outra.regiao;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, regiao);
    }
    
}