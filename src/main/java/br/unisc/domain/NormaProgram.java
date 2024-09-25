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
}
