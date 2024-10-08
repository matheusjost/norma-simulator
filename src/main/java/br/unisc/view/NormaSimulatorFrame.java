package br.unisc.view;

import br.unisc.domain.NormaMachine;
import br.unisc.domain.NormaProgram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class NormaSimulatorFrame extends JFrame {

    private NormaMachine machine;
    private JButton runButton = new JButton("Executar");
    private JButton setRegistersButton = new JButton("Definir");
    private JTextArea terminal = new JTextArea(10,100);
    private JButton setMacrosButton = new JButton("Definir diretório das macros");

    public NormaSimulatorFrame() {
        super("Norma IDE");
        machine = new NormaMachine(terminal);
    }

    public void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);

        JLabel registersLabel = new JLabel("Registradores:");
        JTextField registersInput = new JTextField(50);
        registersInput.setToolTipText("Ex: A=0,B=1,C=2");
        setRegistersButton.addActionListener(e -> {
            String registers = registersInput.getText();

            if (machine.hasRegisters()) {
                machine.clearRegisters();
            }

            if (!registers.matches("([A-Z]=[0-9],?)+")) {
                JOptionPane.showMessageDialog(null, "Entrada inválida! Utilize o formato LETRA=NUMERO,LETRA=NUMERO,LETRA=NUMERO");
                return;
            }

            String[] registersArray = registers.split(",");
            for (String register : registersArray) {
                String[] registerParts = register.split("=");
                if (registerParts.length == 2) {
                    machine.initializeRegisters(registerParts[0].toUpperCase(), Integer.parseInt(registerParts[1]));
                    runButton.setEnabled(true);
                }
            }
        });

        setMacrosButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                machine.setMacrosPath(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        JPanel registersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        registersPanel.add(registersLabel);
        registersPanel.add(registersInput);
        registersPanel.add(setRegistersButton);
        registersPanel.add(setMacrosButton);
        registersPanel.setBorder(BorderFactory.createTitledBorder("Configurações"));


        JPanel contentPanel = new JPanel(new BorderLayout());
        JTextArea codeEditor = new JTextArea(15,100);
        codeEditor.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(codeEditor);

        runButton.setEnabled(machine.hasRegisters());
        runButton.addActionListener(e -> {
            terminal.setText("");

            String programCode = codeEditor.getText();
            if (programCode.isEmpty()) {
                JOptionPane.showMessageDialog(null, "O código do programa não pode ser vazio!");
                return;
            }

            machine.setProgram(NormaProgram.createMappedProgram(programCode.split("\n")));
            terminal.setText("");
            machine.runProgram();

            if (machine.hasRegisters()) {
                machine.clearRegisters();
                disableRunButton();
            }
        });

        terminal.setLineWrap(true);
        terminal.setEnabled(false);
        JScrollPane terminalScrollPane = new JScrollPane(terminal);
        terminalScrollPane.setBorder(BorderFactory.createTitledBorder("Terminal"));

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(terminalScrollPane, BorderLayout.SOUTH);


        JPanel panel = new JPanel(new BorderLayout());
        panel.add(registersPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(runButton, BorderLayout.SOUTH);

        add(panel);
        setResizable(false);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    private void disableRunButton() {
        runButton.setEnabled(false);
    }

}
