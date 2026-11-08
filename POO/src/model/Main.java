package model;

import javax.swing.SwingUtilities;

import controller.ControllerAPI;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ControllerAPI controlador =
                    new ControllerAPI();

                controlador.abrirMenu();
            }
        });
    }
}