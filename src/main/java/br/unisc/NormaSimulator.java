package br.unisc;

import br.unisc.view.NormaSimulatorFrame;

import static javax.swing.SwingUtilities.invokeLater;

public class NormaSimulator {
    public static void main(String[] args) {
        invokeLater(() -> {
            NormaSimulatorFrame frame = new NormaSimulatorFrame();
            frame.init();
        });
    }
}