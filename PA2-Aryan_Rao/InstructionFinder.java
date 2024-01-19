import java.util.*;

public class InstructionFinder {
    private Map<Integer, Instruction> instructions;

    public InstructionFinder() {
        instructions = new HashMap<>();
    }

    public void addInstruction(String name, int start, int end, String type) {
        instructions.put(start, new Instruction(name, start, end, type));
    }

    public Instruction findInstruction(int number) {
        if(number ==1680 || number == 840){
            return new Instruction("EQRI", 840, 840, "I");
        }

        if(number ==1424 || number == 712){
            return new Instruction("ORRI", 712, 713, "I");
        }
        
        for (int start : instructions.keySet()) {
            Instruction instruction = instructions.get(start);
            if (number >= instruction.getStart() && number <= instruction.getEnd()) {
                return instruction;
            }
        }
        return null;
    }
}

class Instruction {
    private String name;
    private int start;
    private int end;
    private String type;

    public Instruction(String name, int start, int end, String type) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public String getName() {
        return name;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "name='" + name + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", type='" + type + '\'' +
                '}';
    }
}
