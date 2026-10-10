package model;

class PecaBonus {
	private TipoBonus tipo; 
	private boolean usada; 
	
	PecaBonus(TipoBonus tipo){
		this.tipo = tipo; 
		this.usada = false; 
		
	}
	
	TipoBonus getTipo() {
		return tipo ; 
	}
	
	boolean EhUsada() {
		return usada; 
	}
	
	void usar() {
		usada = true; 
	}
}
