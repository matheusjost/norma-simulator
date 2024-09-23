package br.unisc.domain;

import java.util.HashMap;
import java.util.Map;

public class NormaMachine {
    private Map<String, Integer> registers;
    private String[] program;
    private int instructionPointer;

    public NormaMachine(int numRegisters, String[] registerNames) {
        registers = new HashMap<>();
        for (String name : registerNames) {
            registers.put(name, 0);
        }
        instructionPointer = 0;
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

    public void setProgram(String[] program) {
        this.program = program;
    }

    public void runProgram() {
        while (instructionPointer < program.length) {
            String line = program[instructionPointer];
            executeInstruction(line);
        }
    }

    private void executeInstruction(String instruction) {
        String[] parts = instruction.split(" ");
        String command = parts[1]; // comando

        switch (command) {
            case "se":
                // se zero_a então vá_para 9 senão vá_para 2
                String registerToTest = parts[2].split("_")[1];
                int jumpIfZero = Integer.parseInt(parts[5]);
                int jumpIfNotZero = Integer.parseInt(parts[8]);

                if (isZero(registerToTest)) {
                    instructionPointer = jumpIfZero - 1; // -1 pois index começa em 0
                } else {
                    instructionPointer = jumpIfNotZero - 1;
                }
                break;

            case "add":
                String registerToAdd = parts[1].split("_")[1];
                add(registerToAdd);
                instructionPointer++;
                break;

            case "sub":
                String registerToSub = parts[1].split("_")[1];
                sub(registerToSub);
                instructionPointer++;
                break;

            case "vá_para":
                int jumpTo = Integer.parseInt(parts[1]);
                instructionPointer = jumpTo - 1;
                break;

            default:
                System.out.println("Instrução desconhecida: " + instruction);
                break;
        }
    }

    public void macroMultiplicacao(String regA, String regB, String regResultado) {
        setRegisterValue(regResultado, 0);
        while (!isZero(regB)) {
            add(regResultado);
            sub(regB);
        }
    }

    public void macroDivisao(String regA, String regB, String regResultado) {
        setRegisterValue(regResultado, 0);
        while (!isZero(regA)) {
            sub(regA);
            if (!isZero(regA)) {
                add(regResultado);
                sub(regB);
            }
        }
    }

    public boolean macroPrimo(String regA) {
        if (getRegisterValue(regA) < 2) {
            return false;
        }
        for (int i = 2; i < getRegisterValue(regA); i++) {
            if (getRegisterValue(regA) % i == 0) {
                return false;
            }
        }
        return true;
    }

    public void macroPotencia(String regA, String regB, String regResultado) {
        setRegisterValue(regResultado, 1);
        while (!isZero(regB)) {
            macroMultiplicacao(regResultado, regA, regResultado);
            sub(regB);
        }
    }
}
