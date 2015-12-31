package hr.fer.zemris.ppj.codegen;

public class Param {

    private static final String REG = "R";
    private static final char ADR_L = '(';
    private static final char ADR_R = ')';

    private String parameter;

    private Param(String parameter, boolean address) {
        if (address) {
            this.parameter = ADR_L + parameter + ADR_R;
        } else {
            this.parameter = parameter;
        }
    }

    public static Param reg(int r) {
        assert 0 <= r && r < CodeGen.NUM_REGS;
        return new Param(REG + r, false);
    }

    public static Param num(int n) {
        return new Param(dec2Hex(n), false);
    }

    public static Param label(String label){
        return new Param(label, false);
    }
    
    public static Param aLabel(String label) {
        return new Param(label, true);
    }

    public static Param aNum(int n) {
        return new Param(dec2Hex(n), true);
    }

    public static Param aReg(int r) {
        assert 0 <= r && r < CodeGen.NUM_REGS;
        return new Param(REG + r, true);
    }

    public static Param aRegwOff(int r, int offset) {
        assert 0 <= r && r < CodeGen.NUM_REGS;
        String off = dec2Hex(offset);
        return new Param(REG + r + '+' + off, true);
    }

    private static String dec2Hex(int i) {
        String hex = Integer.toHexString(i);
        if (!Character.isDigit(hex.charAt(0))) {
            hex = '0' + hex; // FRISC would think it's a label
        }
        return hex;
    }

    @Override
    public String toString() {
        return parameter;
    }

}
