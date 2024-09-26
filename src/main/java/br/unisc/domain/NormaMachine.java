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
    private String macrosPath = "src/main/resources/macros/";

    public NormaMachine(JTextArea output) {
        registers = new HashMap<>();
        instructionPointer = -1;
        this.output = output;
    }

    public void setMacrosPath(String macrosPath) { this.macrosPath = macrosPath; }
    
    private NormaMachineState saveState() {
        return new NormaMachineState(program, instructionPointer, comput);
    }

    private NormaMachineState restoreState(NormaMachineState state) {
        program = state.program();
        instructionPointer = state.instructionPointer();
        comput = state.comput();
        return state;
    }

    public void initializeRegisters(String register, int value) { registers.put(register, value); }

    public boolean hasRegisters() { return !registers.isEmpty(); }

    public void clearRegisters() {
        registers.clear();
    }

    private boolean isInstructionPointerReset() {
        return instructionPointer < 0;
    }

    private void setInstructionPointer(int instructionPointer) {
        if (isInstructionPointerReset()) {
            return;
        }
        this.instructionPointer = instructionPointer;
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
        if (registers.containsKey(register)) {
            return registers.get(register) == 0;
        }
        registerDoesNotExist(register);
        return false;
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

    private File findMacroFile(String prefix) {
       File dir = new File(macrosPath);

       if (!dir.exists() || !dir.isDirectory()) {
           output.setText("ERR - Diretório de macros não encontrado!");
           setInstructionPointer(-1);
           return null;
       }

       File[] files = dir.listFiles((dir1, name) -> name.toLowerCase().startsWith(prefix.toLowerCase()) && name.endsWith(".norma"));

       if (files != null && files.length > 0) {
           return files[0];
       }

       return null;
    }

    private boolean runMacro(String[] content) {
        if (content == null) {
            return false;
        }

        NormaMachineState state = saveState();

        clearInstructionPointer();
        clearComput();

        setProgram(NormaProgram.createMappedProgram(content));
        runProgram();

        restoreState(state);

        return true;
    }

    private String[] interpretMacro(File macroFile, String op) {
        String macroFileName = macroFile.getName().split("\\.")[0];
        String[] macroRegisters = macroFileName.split("_", 2)[1].toUpperCase().split("_");
        String[] opRegisters = op.split("_");

        String[] macroInstructions = readFile(macroFile);
        if (macroInstructions == null) {
            return null;
        }

        Map<String, String> mappedRegs = NormaProgram.interpretRegisters(opRegisters, macroRegisters);
        return NormaProgram.setInterpretedRegistersToInstruction(macroInstructions, mappedRegs);
    }

    private boolean checkMacros(String operation) {
        String[] opParts = operation.split("_", 2);

        File macroFile = findMacroFile(opParts[0]);
        if (macroFile != null && macroFile.exists()) {
            String[] macroInstructions = interpretMacro(macroFile, opParts[1]);
            return runMacro(macroInstructions);
        }

        return false;
    }

    private String[] readFile(File macro) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(macro));
            String content = "";
            String line;

            while ( (line = reader.readLine()) != null ) {
                content += line + "\n";
            }

            return content.split("\n");

        } catch (Exception e) {
            output.setText("ERR - Erro ao ler arquivo de macro " + macro.getName());
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    output.setText("ERR - Erro ao fechar o arquivo de macro " + macro.getName());
                }
            }
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
                    setInstructionPointer(jumpIfZero);
                } else {
                    setInstructionPointer(jumpIfNotZero);
                }
            }
            case "FAÇA" -> {
                executeOperation(parts[2]);
                if (parts[3].equals("VÁ_PARA")) {
                    setInstructionPointer(Integer.parseInt(parts[4]));
                } else {
                    instructionPointer++;
                }
            }
            case "VÁ_PARA" -> {
                setInstructionPointer(Integer.parseInt(parts[2]));
            }
            default -> {
                output.setText("ERR - Instrução desconhecida: " + instruction);
                clearInstructionPointer();
            }
        }
    }
}
