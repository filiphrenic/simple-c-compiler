package hr.fer.zemris.ppj.codegen;

import java.util.LinkedList;
import java.util.List;

public class CodeGen {

    public static void main(String[] args) {
        CodeGen cg = new CodeGen();

        FunctionBlock fb = cg.newBlock("main");
        fb.addCode(new Code(new Command("ADD", Param.reg(0), Param.reg(1), Param.reg(6)),
                "zbroji prvi i drugi registar"));

        Code data = new Code(new Command("DW", Param.num(66)), "42 is life");
        data.setLabel(cg.number("x"));
        cg.data.addCode(data);
        System.out.println(cg);
    }

    public static final int NUM_REGS = 8; // number of registers, [0,NUM_REG>
    public static final int RET_REG = 6; // register for return value
    private static final int SP = Integer.parseInt("40000", 16); // default stack size
    private static final int S_REG = 7; // stack pointer register

    private static final String F_PREF = "F_";
    private static final String N_PREF = "G_";

    private CodeBlock startBlock;
    private List<FunctionBlock> blocks;
    private CodeBlock data;
    private int numOfData;

    private FunctionBlock currentBlock;

    public CodeGen() {
        this(SP);
    }

    public CodeGen(int stackSize) {
        startBlock = new CodeBlock();
        blocks = new LinkedList<>();
        data = new CodeBlock();
        numOfData = 0;

        // fill start block
        startBlock.addCode(new Code(new Command("MOVE", Param.num(stackSize), Param.reg(S_REG))));
        startBlock.addCode(new Code(new Command("CALL", Param.label(function("MAIN")))));
        startBlock.addCode(new Code(new Command("HALT")));
    }

    public FunctionBlock newBlock(String functionName) {
        FunctionBlock block = new FunctionBlock(function(functionName));
        blocks.add(block);
        return block;
    }

    public String function(String functionName) {
        return F_PREF + functionName;
    }

    public String number(String variableName) {
        return N_PREF + numOfData++;
    }

    @Override
    public String toString() {
        char delim = '\n';
        StringBuilder sb = new StringBuilder();
        sb.append(startBlock);
        sb.append(delim);
        for (FunctionBlock fb : blocks) {
            sb.append(fb);
            sb.append(delim);
        }
        sb.append(data);
        return sb.toString();
    }

}
