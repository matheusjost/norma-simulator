package br.unisc.view;

import br.unisc.domain.NormaMachine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class NormaSimulatorFrame extends JFrame {

    private NormaMachine machine;
    private JButton runButton = new JButton("Executar");
    private JButton setRegistersButton = new JButton("Definir");
    private JTextArea terminal = new JTextArea(10,100);

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
        JButton setRegistersButton = new JButton("Definir");
        setRegistersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        JPanel registersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        registersPanel.add(registersLabel);
        registersPanel.add(registersInput);
        registersPanel.add(setRegistersButton);
        registersPanel.setBorder(BorderFactory.createTitledBorder("Configuração dos registradores"));


        JPanel contentPanel = new JPanel(new BorderLayout());
        JTextArea codeEditor = new JTextArea(15,100);
        codeEditor.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(codeEditor);

        runButton.setEnabled(machine.hasRegisters());
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terminal.setText("");

                String programCode = codeEditor.getText();
                if (programCode.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "O código do programa não pode ser vazio!");
                    return;
                }

                machine.setProgram(createMappedProgram(programCode.split("\n")));
                machine.runProgram();

                if (machine.hasRegisters()) {
                    machine.clearRegisters();
                }
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

    private Map<Integer, String> createMappedProgram(String[] program) {
        Map<Integer, String> hash = new HashMap<>();

        for (String line : program) {
            if (line.contains(":")) {
                String[] parts = line.split(":");
                Integer label = Integer.parseInt(parts[0]);
                String instruction = parts[1].toUpperCase();
                hash.put(label, instruction);
            }
        }

        return hash;
    }
}
