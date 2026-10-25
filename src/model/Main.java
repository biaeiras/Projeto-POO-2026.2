package model;

import java.util.ArrayList;

import controller.ControllerAPI;
import view.ViewAPI;

public class Main {

	public static void main(String[] args) {
		System.out.println("hello world");
		ArrayList<Cor> cores = new ArrayList<Cor>();
		cores.add(Cor.AMARELO);
		cores.add(Cor.AZUL);
		ModelAPI modelAPI = new ModelAPI(cores);
		ViewAPI viewAPI = new ViewAPI(modelAPI);
		ControllerAPI controllerAPI = new ControllerAPI(viewAPI, modelAPI);

	}

}
