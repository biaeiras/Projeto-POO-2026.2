package model;

class Provincia {
	private TipoTributo tributo;
	private int cargas = 0;
	private ProvinciaID id = null;
	public ProvinciaID getId() {
		return id;
	}

	void setId(ProvinciaID id) {
		this.id = id;
	}

	Provincia(TipoTributo tributo) {
		this.tributo = tributo;
	}
	
	TipoTributo getTipoTributo() {
		return this.tributo;
	}
	int carregar() {
		this.cargas++;
		return this.cargas;
	}
	TipoTributo descarregar(int c) {
		this.cargas -= c;
		return this.tributo;
	}
	
	int getCargas() {
		return this.cargas;
	}
	
	void restaurarCargas(int cargas) {
	    if (cargas < 0) {
	        throw new IllegalArgumentException("Quantidade de cargas inválida.");
	    }
	    this.cargas = cargas;
	}

}
