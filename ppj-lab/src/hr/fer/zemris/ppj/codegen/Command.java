package hr.fer.zemris.ppj.codegen;

public class Command {

    // private static final Pattern REG_PAT = Pattern.compile("R(\\d)");

    private String name;
    private Param[] parameters;
    // private BitSet usedRegisters;

    public Command(String name, Param... parameters) {
        this.name = name;
        this.parameters = parameters;

        //        usedRegisters = new BitSet(CodeGen.NUM_REGS);
        //        System.out.println(name);
        //        for (Param p : parameters) {
        //            System.out.println(p);
        //            Matcher m = REG_PAT.matcher(p.toString());
        //            while (m.find()) {
        //                int r = Integer.parseInt(m.group(1));
        //                usedRegisters.set(r);
        //            }
        //        }
    }

    //    public BitSet getUsedRegisters() {
    //        return usedRegisters;
    //    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        String delim = " ";
        for (Param p : parameters) {
            sb.append(delim);
            sb.append(p);
            delim = ", ";
        }
        return sb.toString();
    }

}
