package hr.fer.zemris.ppj.codegen;

import hr.fer.zemris.ppj.util.Util;

public class Code {

    private static final int LAB_MAX_LEN = 10;
    private static final int COM_MAX_LEN = 20;

    private String label;
    private Command command;
    private String comment;

    public Code(Command command) {
        this(null, command, null);
    }

    public Code(String label, Command command) {
        this(label, command, null);
    }

    public Code(Command command, String comment) {
        this(null, command, comment);
    }

    public Code(String label, Command command, String comment) {
        this.label = label;
        this.command = command;
        this.comment = comment;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Command getCommand() {
        return command;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // label
        int spacesLeft = LAB_MAX_LEN;
        if (label != null) {
            sb.append(label);
            spacesLeft -= label.length();
        }
        sb.append(Util.spaces(spacesLeft));

        // command
        String scommand = command.toString();
        sb.append(scommand);

        // comment
        if (comment != null) {
            sb.append(Util.spaces(COM_MAX_LEN - scommand.length()));
            sb.append("; ");
            sb.append(comment);
        }

        return sb.toString();
    }

}
