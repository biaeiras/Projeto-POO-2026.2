package model;

class ConselheiroAberto extends CartaConselheiro {
	private final TipoConselheiroAberto tipo;
	private Jogador cumpridaPor;
	 
	ConselheiroAberto(TipoConselheiroAberto tipo) {
	        this.tipo = tipo;
	 }
	
	 @Override
	String getNome() {
	    return tipo.name();
	 }
	 
	TipoConselheiroAberto getTipo() { 
		 return tipo;
	 }
	 
	boolean isCumprida() {
		return cumpridaPor != null;
	 }
	
	Jogador getCumpridaPor() {
	 	// quando um jogador consegue cumprir a carta ganha 1 voto  
	    return cumpridaPor;
	}
	 
	void marcarCumprida(Jogador jogador) {
		if( jogador != null) {
			throw new IllegalStateException("Jogador não pode ser nulo"); 
	}
	
	if (isCumprida()) {
	    throw new IllegalStateException("Carta já foi cumprida por outro jogador");
		}
		cumpridaPor = jogador;
		//assim que essa carta for cumprida ela sai do jogo 
	 }	
}
