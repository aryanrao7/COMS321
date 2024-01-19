import java.io.*;
import java.util.*;

//AUTHOR
//ARYAN RAO - aryanrao@iastate.edu

//------------------------------NOTE---------------------------------
// THE OUTPUT OF THIS PROJECT IS LOCATED IN decodedCode.txt
//-------------------------------------------------------------------

//RUNNING INSTRUCTIONS
//1. COMPILE THE CODE USING build.sh
//2. RUN THE CODE USING run.sh
//3. THE OUTPUT WILL BE IN decodedCode.txt and will be printed out on the console


public class Decoder{
    public static ArrayList<Long> instructions = new ArrayList<>();
    public static InstructionFinder finder = new InstructionFinder();
    public static void main(String[] args) {
        //POPULATING
        finder.addInstruction("ADD", 1112, 1112, "R");
        finder.addInstruction("ADDI", 1160, 1161, "I");
        finder.addInstruction("AND", 1104, 1104, "R");
        finder.addInstruction("ANDI", 1168, 1169, "I");
        finder.addInstruction("B", 160, 191, "B");
        finder.addInstruction("CB", 672, 679, "CB");
        finder.addInstruction("BL", 1184, 1215, "B");
        finder.addInstruction("BR", 1712, 1712, "R");
        finder.addInstruction("CBNZ", 1448, 1455,"CB");
        finder.addInstruction("CBZ", 1440, 1447, "CB");
        finder.addInstruction("EOR", 1616, 1616, "R");
        finder.addInstruction("EORI", 840, 841, "I");
        finder.addInstruction("LDUR", 1986, 1986, "D");
        finder.addInstruction("LSL", 1691, 1691,"R");
        finder.addInstruction("LSR", 1690, 1690,"R");
        finder.addInstruction("ORR", 1360, 1360,"R");
        finder.addInstruction("ORRI", 712, 713,"I");
        finder.addInstruction("STUR", 1984, 1984, "D");
        finder.addInstruction("SUB", 1624, 1624,"R");
        finder.addInstruction("SUBI", 1672, 1673, "I");
        finder.addInstruction("SUBIS", 1928, 1929, "I");
        finder.addInstruction("SUBS", 1880, 1880, "R");
        finder.addInstruction("MUL", 1240, 1240, "R");
        finder.addInstruction("PRNT", 2045, 2045,"R");
        finder.addInstruction("PRNL", 2044, 2044,"R");
        finder.addInstruction("DUMP", 2046, 2046,"R");
        finder.addInstruction("HALT", 2047, 2047,"R");

        //READ THE BITS FROM THE MACHINE CODE FILE OF COMS 321 PROGRAMMING ASSIGNMENT 1
        String filePath = "./assignment1.legv8asm.machine";
        
        readInstructionsFromFile(filePath);

        try {
            File toWrite = new File("decodedCode.txt");
            FileWriter fw = new FileWriter(toWrite);
            int lineNumber = 0;
            for (long instruction : instructions) {

                Instruction data = finder.findInstruction((int)((instruction >> 21) & 0x7FF));

                if (data.getType().equals("R")) {
                    decodeR(data, instruction, fw);
                } else if (data.getType().equals("I")) {
                    decodeI(data, instruction, fw);
                } else if (data.getType().equals("D")) {
                    decodeD(data, instruction, fw);
                } else if (data.getType().equals("CB")) {
                    decodeCB(data, instruction, fw);
                } else {
                    decodeB(data, instruction, fw, lineNumber);
                }
            }
            fw.close();
        }catch (IOException e){
                System.err.println(e.getMessage());
            }
    }
    public static void decodeR(Instruction data, Long ins, FileWriter outputFile) throws IOException {
        String command = data.getName();
        long Rm = (ins >>> 16) & 0b11111; // shift right by 16 bits and mask with 0b11111 to get the next 5 bits
        long shant = (ins >>> 10) & 0b111111; // shift right by 10 bits and mask with 0b111111 to get the next 6 bits
        long Rn = (ins >>> 5) & 0b11111; // shift right by 5 bits and mask with 0b11111 to get the next 5 bits
        long Rd = ins & 0b11111; // mask with 0b11111 to get the last 5 bits

        if (data.getName().equals("BR")) {
            outputFile.write(String.format("%s %d\n", data.getName(), Rn));
            System.out.println(String.format("%s %d\n", data.getName(), Rn));
        } else if (data.getName().equals("PRNT")) {
            outputFile.write(String.format("%s X%d\n", data.getName(), Rd));
            System.out.println(String.format("%s X%d\n", data.getName(), Rd));
        } else if (data.getName().equals("DUMP") || data.getName().equals("HALT") || data.getName().equals("PRNL")) {
            outputFile.write(String.format("%s\n", data.getName()));
            System.out.println(String.format("%s\n", data.getName()));
        }
          else if(data.getName().equals("LSL") || data.getName().equals("LSR")){
            outputFile.write(String.format("%s X%d, X%d, #%d\n", data.getName(), Rd, Rn, shant));
            System.out.println(String.format("%s X%d, X%d, #%d\n", data.getName(), Rd, Rn, shant));
        } else{
            outputFile.write(String.format("%s X%d, X%d, X%d\n", data.getName(), Rd, Rn, Rm));
            System.out.println(String.format("%s X%d, X%d, X%d\n", data.getName(), Rd, Rn, Rm));
        }

    }

    public static void decodeI(Instruction data, Long ins, FileWriter outputFile) throws IOException{
        long opcode = ins >>> 22; // shift right by 22 bits to get the first 10 bits
        long Imm = (ins >>> 10) & 0b111111111111; // shift right by 10 bits and mask with 0b111111111111 to get the next 12 bits
        long Rn = (ins >>> 5) & 0b11111; // shift right by 5 bits and mask with 0b11111 to get the next 5 bits
        long Rd = ins & 0b11111; // mask with 0b11111 to get the last 5 bits

        outputFile.write(String.format("%s X%d, X%d, #%d\n", data.getName(), Rd , Rn, Imm));
        System.out.println(String.format("%s X%d, X%d, #%d\n", data.getName(), Rd , Rn, Imm));
    }

    public static void decodeD(Instruction data, Long ins, FileWriter outputFile) throws IOException {
        long opcode = ins >>> 21; // shift right by 21 bits to get the first 11 bits
        long Address = (ins >>> 12) & 0b111111111; // shift right by 12 bits and mask with 0b111111111 to get the next 9 bits
        long op2 = (ins >>> 10) & 0b11; // shift right by 10 bits and mask with 0b11 to get the next 2 bits
        long Rn = (ins >>> 5) & 0b11111; // shift right by 5 bits and mask with 0b11111 to get the next 5 bits
        long Rt = ins & 0b11111; // mask with 0b11111 to get the last 5 bits

        outputFile.write(String.format("%s X%d, [X%d, #%d]\n", data.getName(), Rt, Rn, Address));
        System.out.println(String.format("%s X%d, [X%d, #%d]\n", data.getName(), Rt, Rn, Address));
    }

    public static void decodeB(Instruction data, Long ins, FileWriter outputFile, int line) throws IOException {
        long opcode = ins >>> 26; // shift right by 26 bits to get the first 6 bits
        long BR_Address = ins & 0x03FFFFFF; // mask with 0x03FFFFFF to get the last 26 bits
        int bLine = line + (int) BR_Address; //where to branch to
        outputFile.write(String.format("%s %d\n", data.getName(), bLine));
        System.out.println(String.format("%s %d\n", data.getName(), bLine));

    }

    public static void decodeCB(Instruction data, long ins, FileWriter outputFile) throws IOException {
        long opcode = ins >>> 24; // shift right by 24 bits to get the first 8 bits
        long COND_BR_Offset = ins >>> 5 & 0xFFFFF; // shift right by 5 bits and mask with 0xFFFFF to get the next 21 bits
    
        // Sign-extend the 21-bit offset to a 64-bit signed value
        long COND_BR_Address = (COND_BR_Offset << 43) >> 43;
    
        int Rt = (int) ((ins >>> 5) & 0b1111); // shift right by 5 bits and mask with 0b1111 to get the next 4 bits
    
        String cond = getCondition(Rt);
    
        outputFile.write(String.format("%s %d\n", "B." + cond, COND_BR_Address));
        System.out.println(String.format("%s %d\n", "B." + cond, COND_BR_Address));
    }
    
    
    private static String getCondition(int Rt) {
        if (Rt >= 0 && Rt <= 15) {
            switch (Rt) {
                case 0:
                    return "EQ";
                case 1:
                    return "NE";
                case 2:
                    return "HS";
                case 3:
                    return "LO";
                case 4:
                    return "MI";
                case 5:
                    return "PL";
                case 6:
                    return "VS";
                case 7:
                    return "VC";
                case 8:
                    return "HI";
                case 9:
                    return "LS";
                case 10:
                    return "GE";
                case 11:
                    return "LT";
                case 12:
                    return "GT";
                case 13:
                    return "LE";
                case 14:
                    return "AL"; 
                case 15:
                    return "NV";
                default:
                    throw new IllegalStateException("Invalid CB Rt value: " + Rt);
            }
        } else {
            System.out.println("Error: Invalid CB Rt value: " + Rt);
            throw new IllegalStateException("CB Rt value should be less than 16");
        }
    }
    
    
    

    public static void readInstructionsFromFile(String filePath)
    {
        File instructionFile = new File(filePath);
        try
        {
            byte[] buffer = new byte[(int)instructionFile.length()];
            FileInputStream inputStream = new FileInputStream(instructionFile);

            int nRead = 0;

            String s = "";
            while((nRead = inputStream.read(buffer)) != -1)
            {
                for (int i = 0; i < nRead; i++)
                {
                    String bin = Integer.toBinaryString(0xFF & buffer[i] | 0x100).substring(1);
                    s += bin;

                    if(s.length() == 32)
                    {
                        instructions.add(Long.parseLong(s,2));
                        s = "";
                    }
                }
            }
            inputStream.close();
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("");
        }

        catch(IOException ex)
        {
            ex.printStackTrace();
        }

    }
}