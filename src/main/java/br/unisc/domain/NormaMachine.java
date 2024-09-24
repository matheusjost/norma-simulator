package br.unisc.domain;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class NormaMachine {
    private JTextArea output;
    private Map<String, Integer> registers;
    private Map<Integer, String> program;
    private String comput = "";
    private int instructionPointer;

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

    public void setRegisterValue(String register, int value) {
        if (registers.containsKey(register)) {
            registers.put(register, value);
            return;
        }

        System.out.println("Registrador " + register + " não existe!");
    }

    public int getRegisterValue(String register) {
        return registers.getOrDefault(register, -1);
    }

    public boolean isZero(String register) {
        return registers.get(register) == 0;
    }

    public void add(String register) {
        if (registers.containsKey(register)) {
            registers.put(register, registers.get(register) + 1);
        }
    }

    public void sub(String register) {
        if (registers.containsKey(register)) {
            if (registers.get(register) > 0) {
                registers.put(register, registers.get(register) - 1);
                return;
            }

            System.out.println("Registrador " + register + " já está em 0!");
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
        createComput(); // VALOR FINAL, ROTULO INALCANCAVEL DO PROGRAMA

        output.setText(comput);
        instructionPointer = -1;
        comput = "";
    }

    private void executeOperation(String operation) {
        String[] opParts = operation.split("_");
        String op = opParts[0];
        String reg = opParts[1];

        switch (op) {
            case "ADD":
                add(reg);
                break;

            case "SUB":
                sub(reg);
                break;

            default:
                break;
        }
    }

    private void executeInstruction(String instruction) {
        String[] parts = instruction.split(" ");
        String command = parts[1];

        switch (command) {
            case "SE":
                String registerToTest = parts[2].split("_")[1];
                int jumpIfZero = Integer.parseInt(parts[5]);
                int jumpIfNotZero = Integer.parseInt(parts[8]);

                if (isZero(registerToTest)) {
                    instructionPointer = jumpIfZero;
                } else {
                    instructionPointer = jumpIfNotZero;
                }
                break;

            case "FAÇA":
                executeOperation(parts[2]);
                instructionPointer = parts[3].equals("VÁ_PARA") ? Integer.parseInt(parts[4]) : instructionPointer + 1;
                break;

            case "VÁ_PARA":
                int jumpTo = Integer.parseInt(parts[2]);
                instructionPointer = jumpTo - 1;
                break;

            default:
                System.out.println("Instrução desconhecida: " + instruction);
                break;
        }
    }
}
