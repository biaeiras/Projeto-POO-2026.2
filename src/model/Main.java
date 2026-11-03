package model;

import java.util.ArrayList;

import controller.ControllerAPI;
import view.ViewAPI;

public class Main {

	 public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ArrayList<Cor> cores = new ArrayList<>();
            cores.add(Cor.AMARELO);
            cores.add(Cor.AZUL);

            FachadaJogo fachada = FachadaJogo.getInstancia();
            fachada.iniciarPartida(cores);

            ViewAPI viewAPI = new ViewAPI(fachada);
            ControllerAPI controllerAPI =
                    new ControllerAPI(viewAPI, fachada);
        });
    }
}
