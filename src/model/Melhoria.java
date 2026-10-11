package model;

class Melhoria {

    private TipoMelhoria tipo;
	private Regiao regiao;

    Melhoria(TipoMelhoria tipo, Regiao regiao) {
        this.tipo = tipo;
        this.regiao = regiao;
    }

    Regiao getRegiao() {
        return regiao;
    }
    
    TipoMelhoria getTipo() {
		return tipo;
	}


    TipoAcao getTipoAcao() {
        switch (tipo) {
            case MOVIMENTO_EXTRA:
                return TipoAcao.MOVIMENTO;

            case PEGAR_TRIBUTO_EXTRA:
                return TipoAcao.PEGAR_TRIBUTO;

            default:
                return null;
        }
    }
}