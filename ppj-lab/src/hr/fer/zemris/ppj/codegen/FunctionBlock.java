package hr.fer.zemris.ppj.codegen;

import java.util.BitSet;
import java.util.Stack;

public class FunctionBlock extends CodeBlock {

    private String functionName;
    private BitSet usedRegisters;

    public FunctionBlock(String functionName) {
        super();
        this.functionName = functionName.toUpperCase();
        usedRegisters = new BitSet(CodeGen.NUM_REGS);
    }

    @Override
    public void addCode(Code code) {
        super.addCode(code);
        usedRegisters.or(code.getCommand().getUsedRegisters());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        usedRegisters.clear(CodeGen.RET_REG); // don't need to preserve return register
        boolean usedBlockName = false;

        Stack<Integer> regs = new Stack<>();
        for (int r = usedRegisters.nextSetBit(0); r >= 0; r = usedRegisters.nextSetBit(r + 1)) {
            regs.push(r);
            Code push = stackCode("PUSH", r);
            if (!usedBlockName) {
                usedBlockName = true;
                push.setLabel(functionName);
            }
            sb.append(push);
            sb.append('\n');
        }
        if (!usedBlockName) {
            super.labelFirst(functionName);
        }
        sb.append(super.toString());
        while (!regs.empty()) {
            sb.append(stackCode("POP", regs.pop()));
            sb.append('\n');
        }
        sb.append(new Code(new Command("RET")));
        sb.append('\n');

        return sb.toString();
    }

    private static Code stackCode(String command, int r) {
        return new Code(new Command(command, Param.reg(r)));
    }

}
