package br.unisc.view;

import br.unisc.domain.NormaMachine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NormaSimulatorFrame extends JFrame {

    public NormaSimulatorFrame() {
        super("Norma IDE");
    }

    public void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 300);

        JLabel registersLabel = new JLabel("Registradores:");
        JTextField registersInput = new JTextField(50);
        registersInput.setToolTipText("Ex: A=0,B=1,C=2");
        JButton setRegistersButton = new JButton("Definir");
        setRegistersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String registers = registersInput.getText();
            }
        });

        JPanel registersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        registersPanel.add(registersLabel);
        registersPanel.add(registersInput);
        registersPanel.add(setRegistersButton);
        registersPanel.setBorder(BorderFactory.createTitledBorder("Configuração dos registradores"));


        JPanel contentPanel = new JPanel(new BorderLayout());
        JTextArea codeEditor = new JTextArea(10,100);
        codeEditor.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(codeEditor);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Editor"));

        JButton runButton = new JButton("Executar");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String programCode = codeEditor.getText();
                String[] reg = {"A", "B", "C"};
                NormaMachine nm = new NormaMachine(3,reg);
                nm.setProgram(programCode.split("\n"));
                nm.runProgram();
            }
        });

        JTextArea terminal = new JTextArea(5,100);
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
}
