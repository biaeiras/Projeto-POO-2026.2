package model;

class ConselheiroSecreto extends CartaConselheiro {
	private final TipoConselheiroSecreto tipo;
	
	ConselheiroSecreto(TipoConselheiroSecreto tipo) {
        this.tipo = tipo;
	}
	
	@Override
	String getNome() {
	    return tipo.name();
	 }
	
	TipoConselheiroSecreto getTipo() {
	    return tipo;
	}

}
