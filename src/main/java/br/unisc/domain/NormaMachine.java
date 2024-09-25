package br.unisc.domain;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class NormaMachine {
    private JTextArea output;
    private Map<String, Integer> registers;
    private Map<Integer, String> program;
    private String comput = "";
    private int instructionPointer;
    private String macrosPath = "src/main/resources/macros/"; // TODO: parametrize this

    public NormaMachine(JTextArea output) {
        registers = new HashMap<>();
        instructionPointer = -1;
        this.output = output;
    }

    public void initializeRegisters(String register, int value) {
        registers.put(register, value);
    }

    public boolean hasRegisters() {
        return !registers.isEmpty();
    }

    public void clearRegisters() {
        registers.clear();
    }

    private void clearInstructionPointer() {
        instructionPointer = -1;
    }

    private void clearComput() {
        comput = "";
    }

    private void registerDoesNotExist(String register) {
        output.setText("ERR - Registrador " + register + " não existe!");
        clearInstructionPointer();
    }

    public boolean isZero(String register) {
        return registers.get(register) == 0;
    }

    public void add(String register) {
        if (registers.containsKey(register)) {
            registers.put(register, registers.get(register) + 1);
            return;
        }
        registerDoesNotExist(register);
    }

    public void sub(String register) {
        if (registers.containsKey(register)) {
            if (registers.get(register) > 0) {
                registers.put(register, registers.get(register) - 1);
                return;
            }

            System.out.println("ERR - Registrador " + register + " já está em 0!");
        }
    }

    public void setProgram(Map<Integer, String> labeledProgram) {
        this.program = labeledProgram;
    }

    private void createComput() {
        comput += "(" + instructionPointer + ", (";
        for (Map.Entry<String, Integer> entry : registers.entrySet()) {
            if (entry.equals(registers.entrySet().toArray()[registers.size() - 1])) {
                comput += entry.getValue();
                continue;
            }
            comput += entry.getValue() + ", ";
        }
        comput += "))\n";
    }

    public void runProgram() {
        if (instructionPointer < 0) {
            instructionPointer = program.keySet().iterator().next();
        }

        while (program.containsKey(instructionPointer)) {
            String line = program.get(instructionPointer);
            createComput();
            executeInstruction(line);
        }
        createComput();

        if (!output.getText().startsWith("ERR")) {
            output.setText(comput);
        }
        clearInstructionPointer();
        clearComput();
    }

    private boolean checkMacros(String op) {
        File macroFile = new File(macrosPath + op.toLowerCase() + ".norma");
        if (macroFile.exists()) {
            String[] content = readFile(macroFile);
            if (content != null) {
                int aux = instructionPointer; //armazena instructionPointer do programa principal para iniciar a exec da macro
                String comp = comput;

                clearInstructionPointer();
                clearComput();

                setProgram(NormaProgram.createMappedProgram(content));
                runProgram();

                instructionPointer = aux; // retorna a exec do programa principal
                comput = comp;
                return true;
            }

            return false;
        }

        return false;
    }

    private String[] readFile(File macro) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(macro));
            String content = "";
            String line;

            while ( (line = reader.readLine()) != null ) {
                content += line + "\n";
            }

            return content.split("\n");

        } catch (Exception e) {
            output.setText("ERR - Erro ao ler arquivo de macro " + macro.getName());
            return null;
        }
    }

    private void executeOperation(String operation) {
        String[] opParts = operation.split("_");
        String op = opParts[0];
        String reg = opParts[1];

        switch (op) {
            case "ADD" -> add(reg);
            case "SUB" -> sub(reg);
            default -> {
                if (!checkMacros(operation)) output.setText("ERR - Operação desconhecida: " + operation);
            }
        }
    }

    private void executeInstruction(String instruction) {
        String[] parts = instruction.split(" ");
        String command = parts[1];

        switch (command) {
            case "SE" -> {
                String registerToTest = parts[2].split("_")[1];
                int jumpIfZero = Integer.parseInt(parts[5]);
                int jumpIfNotZero = Integer.parseInt(parts[8]);
                if (isZero(registerToTest)) {
                    instructionPointer = jumpIfZero;
                } else {
                    instructionPointer = jumpIfNotZero;
                }
            }
            case "FAÇA" -> {
                executeOperation(parts[2]);
                instructionPointer = parts[3].equals("VÁ_PARA") ? Integer.parseInt(parts[4]) : instructionPointer + 1;
            }
            case "VÁ_PARA" -> {
                instructionPointer = Integer.parseInt(parts[2]);
            }
            default -> {
                output.setText("ERR - Instrução desconhecida: " + instruction);
                clearInstructionPointer();
            }
        }
    }
}
