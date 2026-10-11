package model;

class Provincia {
	private TipoTributo tributo;
	private int cargas = 1;
	public Provincia(TipoTributo tributo) {
		this.tributo = tributo;
	}
	public TipoTributo getTipoTributo() {
		return this.tributo;
	}
	public int carregar() {
		this.cargas++;
		return this.cargas;
	}
	public TipoTributo descarregar(int c) {
		this.cargas -= c;
		return this.tributo;
	}
	
	public int getCargas() {
		return this.cargas;
	}

}
