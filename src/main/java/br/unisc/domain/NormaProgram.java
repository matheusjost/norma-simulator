package br.unisc.domain;

import java.util.HashMap;
import java.util.Map;

public class NormaProgram {

    public static Map<Integer, String> createMappedProgram(String[] program) {
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

    public static Map<String, String> interpretRegisters(String[] actualRegisters, String[] macroRegisters) {
        Map<String, String> registersMap = new HashMap<>();
        for (int i = 0; i < actualRegisters.length; i++) {
            try {
                registersMap.put(actualRegisters[i], macroRegisters[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                registersMap.put(actualRegisters[i], "NO REGISTER");
            }
        }

        return registersMap;
    }

    public static String[] setInterpretedRegistersToInstruction(String[] instructions, Map<String, String> registers) {
        for (int i = 0; i < instructions.length; i++) {
            for (Map.Entry<String, String> entry : registers.entrySet()) {
                instructions[i] = instructions[i].replace("_" + entry.getValue(), "_" + entry.getKey());
            }
        }

        return instructions;
    }

}
