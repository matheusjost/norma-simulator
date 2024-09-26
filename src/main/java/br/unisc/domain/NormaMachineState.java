package br.unisc.domain;

import java.util.Map;

public record NormaMachineState(Map<Integer, String> program, int instructionPointer, String comput) {
}
