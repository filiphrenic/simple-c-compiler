package hr.fer.zemris.ppj.semantic.analysis;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import hr.fer.zemris.ppj.codegen.CodeGen;
import hr.fer.zemris.ppj.semantic.Attribute;
import hr.fer.zemris.ppj.semantic.ProductionEnum;
import hr.fer.zemris.ppj.semantic.SemNode;
import hr.fer.zemris.ppj.semantic.SemNodeT;
import hr.fer.zemris.ppj.semantic.SemNodeV;
import hr.fer.zemris.ppj.semantic.parse.Trie;
import hr.fer.zemris.ppj.semantic.types.ArrayType;
import hr.fer.zemris.ppj.semantic.types.ConstType;
import hr.fer.zemris.ppj.semantic.types.FunctionType;
import hr.fer.zemris.ppj.semantic.types.ListType;
import hr.fer.zemris.ppj.semantic.types.NumericType;
import hr.fer.zemris.ppj.semantic.types.PrimitiveType;
import hr.fer.zemris.ppj.semantic.types.Type;
import hr.fer.zemris.ppj.util.Util;

/**
 * Version 1.0:
 *      This class performs semantic analysis of the program.
 * Version 2.0:
 *      Added code generation into analysis.
 * 
 * @author fhrenic
 * @version 2.0
 */
public class SemanticAnalyzer {

    // maximum number of elements in an array
    private static final int ARRAY_TOP = 1024;
    private static final String ESCAPED = "tn0'\"\\";

    private Trie productionTrie;
    private SymbolTable global;
    private String error;

    private CodeGen codegen;

    /**
     * Creates a new {@link SemanticAnalyzer} that will do semantic analysis and code generation.
     * 
     * @param trieFilename file path to production definitions
     */
    public SemanticAnalyzer(String trieFilename) {
        productionTrie = new Trie(trieFilename);
        error = null;
        codegen = new CodeGen();
    }

    public CodeGen getCodeGen() {
        return codegen;
    }

    public String getError() {
        return error;
    }

    public void analysis(SemNodeV tree) {
        global = new SymbolTable();
        try {
            check(tree, global);
            checkMain();
            checkFunctions(global);
            System.err.println("OK");
        } catch (SemanticException e) {
            error = e.createError();
            System.err.println(e.getMessage());
        } catch (SemanticFunctionException e) {
            error = e.getFunctionName();
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static String parseString(String s, SemNodeV node) {
        if (s.isEmpty()) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        boolean escaping = false;
        char bound = s.charAt(0);

        for (int idx = 1; idx < s.length() - 1; idx++) {
            char c = s.charAt(idx);
            if (escaping) {
                escaping = false;
                if (!ESCAPED.contains(c + "")) {
                    throw new SemanticException("Unknown escape sequence at " + s, node);
                }
                sb.append(Util.unescape(c));
            } else {
                if (c == '\\') {
                    escaping = true;
                } else {
                    sb.append(c);
                }
                if (c == bound) {
                    throw new SemanticException("Can't have boundings inside", node);
                }
            }
        }
        if (escaping) {
            throw new SemanticException("Invalid string " + s, node);
        }
        return sb.toString();
    }

    private static int parseInt(String value, SemNodeV node) {
        int x = 0;
        try {
            x = Integer.decode(value);
        } catch (NumberFormatException e) {
            throw new SemanticException("Invalid number: " + value, node);
        }
        return x;
    }

    private void binaryOperatorINT(SemNodeV node, SymbolTable table) {
        SemNodeV left = (SemNodeV) node.getChild(0);
        SemNodeV right = (SemNodeV) node.getChild(2);

        // 1. provjeri (<left>)
        check(left, table);
        // 2. <left>.tip ~ int
        checkImplicit2Int(left, node);
        // 3. provjeri (<right>)
        check(right, table);
        // 4. <right>.tip ~ int
        checkImplicit2Int(right, node);

        // tip <- int
        node.setType(Type.INT);
        // l-izraz <- 0
        node.setLExpr(false);

        String operator = ((SemNodeT) node.getChild(1)).getValue();
        codegen.binaryOp(operator);
    }

    private void unaryProduction(SemNodeV node, SymbolTable table) {
        SemNodeV child = (SemNodeV) node.getChild(0);

        // 1. provjeri (<child>)
        check(child, table);

        // tip <- <child>.tip
        node.setType(child.getType());
        // l-izraz <- <child>.l-izraz
        node.setLExpr(child.getLExpr());
    }

    private static void checkImplicit2Int(SemNodeV nodeToCheck, SemNodeV parent) {
        if (!nodeToCheck.getType().implicit(Type.INT)) {
            throw new SemanticException("Can't convert to INT", parent);
        }
    }

    private void checkMain() {
        SymbolTableEntry main = global.getEntry("main");
        if (main == null) {
            throw new SemanticFunctionException("main", "Main not defined");
        }
        FunctionType mainType = new FunctionType(new ListType(Type.VOID), Type.INT);
        if (!mainType.same(main.getType())) {
            throw new SemanticFunctionException("main", "Main has wrong type");
        }
    }

    private void checkFunctions(SymbolTable table) {
        for (Entry<String, SymbolTableEntry> entry : table.getAllEntries()) {
            if (entry.getValue().getType() instanceof FunctionType) {
                String functionName = entry.getKey();
                SymbolTableEntry function = global.getEntry(functionName);

                if (function == null) {
                    // shouldn't happen, will throw exception before this
                    throw new SemanticFunctionException("funkcija",
                            "Function not found in global table");
                }

                if (!function.getDefined()) {
                    throw new SemanticFunctionException("funkcija",
                            "Function " + functionName + " not defined");
                }

                if (!function.getType().same(entry.getValue().getType())) {
                    throw new SemanticFunctionException("funkcija",
                            "Function parameters and/or return type don't match");
                }
            }
        }
        for (SymbolTable child : table.getNestedTables()) {
            checkFunctions(child);
        }
    }

    private void check(SemNodeV node, SymbolTable table) {

        ProductionEnum pe = productionTrie.findProduction(node);
        if (pe == null) {
            throw new SemanticException("Production not found", node);
        }

        if (pe == ProductionEnum.primarni_izraz_1) {
            // <primarni_izraz> ::= IDN
            SemNodeT idn = (SemNodeT) node.getChild(0);
            SymbolTableEntry idnSte = table.getEntry(idn.getValue());

            // 1. IDN.ime je deklarirano
            if (idnSte == null) {
                throw new SemanticException("IDN.ime not declared", node);
            }

            Type idnTip = idnSte.getType();

            // tip <- IDN.tip
            node.setType(idnSte.getType());
            // l-izraz <- IDN.l-izraz
            node.setLExpr(idnSte.isLExpression());

            boolean byValue = idnSte.isLExpression();

            if (idnTip instanceof FunctionType) {
                codegen.prepareForFunctionCall(idn.getValue());
                return;
            }

            boolean oneByte = false;
            if (idnTip instanceof NumericType) oneByte = ((NumericType) idnTip).bytes() == 1;

            if (idnSte.isGlobal()) {
                codegen.globalRef(idn.getValue(), oneByte, byValue);
            } else {
                codegen.localRef(idnSte.getOffset(table), oneByte, byValue);
            }

        } else if (pe == ProductionEnum.primarni_izraz_2) {
            // <primarni_izraz> ::= BROJ
            SemNodeT broj = (SemNodeT) node.getChild(0);

            // 1. vrijednost je u rasponu tipa int
            int intValue = parseInt(broj.getValue(), node);
            if (!Type.INT.isInRange(intValue)) {
                throw new SemanticException("Value not in INT range", node);
            }

            // tip <- int
            node.setType(Type.INT);
            // l-izraz <- 0
            node.setLExpr(false);

            codegen.numberCode(intValue);

        } else if (pe == ProductionEnum.primarni_izraz_3) {
            // <primarni_izraz> ::= ZNAK
            SemNodeT znak = (SemNodeT) node.getChild(0);

            // 1. vrijednost je u redu po 4.3.2
            String znakValue = parseString(znak.getValue(), node);
            if (znakValue.length() != 1) {
                throw new SemanticException("Invalid sign " + znakValue, node);
            }

            // tip <- char
            node.setType(Type.CHAR);
            // l-izraz <- 0
            node.setLExpr(false);

            codegen.characterCode(znakValue.charAt(0));

        } else if (pe == ProductionEnum.primarni_izraz_4) {
            // <primarni_izraz> ::= NIZ_ZNAKOVA
            SemNodeT niz_znakova = (SemNodeT) node.getChild(0);

            // 1. konstantni niz znakova je ispravan po 4.3.2
            String str = parseString(niz_znakova.getValue(), node);

            // tip <- niz (const(char))
            node.setType(Type.A_CONST_CHAR);
            // l-izraz <- 0
            node.setLExpr(false);

            codegen.stringCode(str);

        } else if (pe == ProductionEnum.primarni_izraz_5) {
            // <primarni_izraz> ::= L_ZAGRADA <izraz> D_ZAGRADA
            SemNodeV izraz = (SemNodeV) node.getChild(1);

            // 1. provjeri (<izraz>)
            check(izraz, table);

            // tip <- <izraz>.tip
            node.setType(izraz.getType());
            // l-izraz <- <izraz>.l-izraz
            node.setLExpr(izraz.getLExpr());

        } else if (pe == ProductionEnum.postfiks_izraz_1) {
            // <postfiks_izraz> ::= <primarni_izraz>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.postfiks_izraz_2) {
            // <postfiks_izraz> ::= <postfiks_izraz> L_UGL_ZAGRADA <izraz> D_UGL_ZAGRADA
            SemNodeV postfiks_izraz = (SemNodeV) node.getChild(0);
            SemNodeV izraz = (SemNodeV) node.getChild(2);

            // 1. provjeri (<postfiks_izraz>)
            check(postfiks_izraz, table);
            // 2. <postfiks_izraz>.tip == niz (X)
            Type pi_type = postfiks_izraz.getType();
            if (!(pi_type instanceof ArrayType)) {
                throw new SemanticException("Array should be of array type", node);
            }
            // 3. provjeri (<izraz>)
            check(izraz, table);
            // 4. <izraz>.tip ~ int
            checkImplicit2Int(izraz, node);

            //tip <- X
            NumericType X = ((ArrayType) pi_type).getType();
            node.setType(X);
            //l-izraz <- X != const(T)
            node.setLExpr(!X.isConst());

            codegen.arrayAccess(X.bytes() == 1);

        } else if (pe == ProductionEnum.postfiks_izraz_3) {
            // <postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA D_ZAGRADA
            SemNodeV postfiks_izraz = (SemNodeV) node.getChild(0);

            // 1. provjeri (<postfiks_izraz>)
            check(postfiks_izraz, table);
            // 2. <postfiks_izraz>.tip = funkcija(void -> pov)
            Type t = postfiks_izraz.getType();
            if (!(t instanceof FunctionType)) {
                throw new SemanticException("Not a function type", node);
            }
            FunctionType ft = (FunctionType) t;
            if (!ft.getParameterTypes().isVoid()) {
                throw new SemanticException("Function contains parameters", node);
            }

            // tip <- pov
            node.setType(ft.getReturnType());
            // l-izraz <- 0
            node.setLExpr(false);

            codegen.call(ft.getReturnType() != Type.VOID);

        } else if (pe == ProductionEnum.postfiks_izraz_4) {
            // <postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA <lista_argumenata> D_ZAGRADA
            SemNodeV postfiks_izraz = (SemNodeV) node.getChild(0);
            SemNodeV lista_argumenata = (SemNodeV) node.getChild(2);

            // XXX
            // changed order of first and second check
            // that way I can have paramaters push onto stack and then I get function address
            // It is easier to call the function that way
            // also, these two parts don't relly one on another so it is not wrong to change
            // order

            // this also allows for multiple consecutive calls like:
            // int x = f(f(f(5)));

            // 2. provjeri (<lista_argumenata>)
            check(lista_argumenata, table);
            //1. provjeri (<postfiks_izraz>)
            check(postfiks_izraz, table);
            // 3. <postfiks_izraz>.tip = funkcija(params -> pov ) i redom po elementima
            // arg-tip iz <lista_argumenata>.tipovi i param-tip iz params vrijedi arg-tip
            // ~ param-tip
            Type t = postfiks_izraz.getType();
            if (!(t instanceof FunctionType)) {
                throw new SemanticException("Not a function type", node);
            }
            FunctionType ft = (FunctionType) t;
            ListType ltf = ft.getParameterTypes();
            ListType lta = (ListType) lista_argumenata.getType();
            if (!(lta.implicit(ltf))) {
                throw new SemanticException("Can't convert argument list to function parameters",
                        node);
            }

            // tip <- pov
            node.setType(ft.getReturnType());
            // l-izraz <- 0
            node.setLExpr(false);

            codegen.call(ft.getReturnType() != Type.VOID);

        } else if (pe == ProductionEnum.postfiks_izraz_5 || pe == ProductionEnum.postfiks_izraz_6) {
            // <postfiks_izraz> ::= <postfiks_izraz> (OP_INC | OP_DEC)
            SemNodeV postfiks_izraz = (SemNodeV) node.getChild(0);

            //1. provjeri (<postfiks_izraz>)
            check(postfiks_izraz, table);

            // 2. <postfiks_izraz>.l-izraz = 1 i <postfiks_izraz>.tip ~ int
            if (!postfiks_izraz.getLExpr()) {
                throw new SemanticException("Not an L-expression", node);
            }
            checkImplicit2Int(postfiks_izraz, node);

            // tip <- int
            node.setType(Type.INT);
            // l-izraz <- 0
            node.setLExpr(false);

            boolean oneByte = ((NumericType) postfiks_izraz.getType()).bytes() == 1;
            codegen.byOne(false, pe == ProductionEnum.postfiks_izraz_5, oneByte);

        } else if (pe == ProductionEnum.lista_argumenata_1) {
            // <lista_argumenata> ::= <izraz_pridruzivanja>
            SemNodeV izraz_pridruzivanja = (SemNodeV) node.getChild(0);

            // 1. provjeri (<izraz_pridruzivanja>)
            check(izraz_pridruzivanja, table);

            // tipovi <- [ <izraz_pridruzivanja>.tip ]
            node.setType(new ListType(izraz_pridruzivanja.getType()));

        } else if (pe == ProductionEnum.lista_argumenata_2) {
            // <lista_argumenata> ::= <lista_argumenata> ZAREZ <izraz_pridruzivanja>
            SemNodeV lista_argumenata = (SemNodeV) node.getChild(0);
            SemNodeV izraz_pridruzivanja = (SemNodeV) node.getChild(2);

            // 1. provjeri (<lista_argumenata>)
            check(lista_argumenata, table);
            // 2. provjeri (<izraz_pridruzivanja>)
            check(izraz_pridruzivanja, table);

            // tipovi <- <lista_argumenata>.tipovi + [ <izraz_pridruzivanja>.tip ]
            ListType lt = (ListType) lista_argumenata.getType();
            lt.addType(izraz_pridruzivanja.getType());
            node.setType(lt);

        } else if (pe == ProductionEnum.unarni_izraz_1) {
            // <unarni_izraz> ::= <postfiks_izraz>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.unarni_izraz_2 || pe == ProductionEnum.unarni_izraz_3) {
            // <unarni_izraz> ::= (OP_INC | OP_DEC) <unarni_izraz>
            SemNodeV unarni_izraz = (SemNodeV) node.getChild(1);

            // 1. provjeri (<unarni_izraz>)
            check(unarni_izraz, table);
            // 2. <unarni_izraz>.l-izraz = 1 i <unarni_izraz>.tip ~ int
            if (!unarni_izraz.getLExpr()) {
                throw new SemanticException("Not an L-expression", node);
            }
            checkImplicit2Int(unarni_izraz, node);

            // tip <- int
            node.setType(Type.INT);
            // l-izraz <- 0
            node.setLExpr(false);

            boolean oneByte = ((NumericType) unarni_izraz.getType()).bytes() == 1;
            codegen.byOne(true, pe == ProductionEnum.unarni_izraz_2, oneByte);

        } else if (pe == ProductionEnum.unarni_izraz_4) {
            // <unarni_izraz> ::= <unarni_operator> <cast_izraz>
            // SemNodeV unarni_operator = (SemNodeV) node.getChild(0);
            SemNodeV cast_izraz = (SemNodeV) node.getChild(1);

            // 1. provjeri (<cast_izraz>)
            check(cast_izraz, table);
            // 2. <cast_izraz>.tip ~ int
            checkImplicit2Int(cast_izraz, node);

            // tip <- int
            node.setType(Type.INT);
            // l-izraz <- 0
            node.setLExpr(false);

        } else if (pe == ProductionEnum.unarni_operator_1) {
            // <unarni_operator> ::= PLUS
            // nista ne treba u analizi

        } else if (pe == ProductionEnum.unarni_operator_2) {
            // <unarni_operator> ::= MINUS
            // nista ne treba u analizi

            codegen.negative();

        } else if (pe == ProductionEnum.unarni_operator_3) {
            // <unarni_operator> ::= OP_TILDA
            // nista ne treba u analizi

        } else if (pe == ProductionEnum.unarni_operator_4) {
            // <unarni_operator> ::= OP_NEG
            // nista ne treba u analizi

        } else if (pe == ProductionEnum.cast_izraz_1) {
            // <cast_izraz> ::= <unarni_izraz>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.cast_izraz_2) {
            // <cast_izraz> ::= L_ZAGRADA <ime_tipa> D_ZAGRADA <cast_izraz>
            SemNodeV ime_tipa = (SemNodeV) node.getChild(1);
            SemNodeV cast_izraz = (SemNodeV) node.getChild(3);

            // 1. provjeri (<ime_tipa>)
            check(ime_tipa, table);
            // 2. provjeri (<cast_izraz>)
            check(cast_izraz, table);
            // 3. <cast_izraz>.tip se moze pretvoriti u <ime_tipa>.tip po poglavlju 4.3.1
            if (!cast_izraz.getType().explicit(ime_tipa.getType())) {
                throw new SemanticException("Values aren't numeric", node);
            }

            // tip <- <ime_tipa>.tip
            node.setType(ime_tipa.getType());
            // l-izraz <- 0
            node.setLExpr(false);

        } else if (pe == ProductionEnum.ime_tipa_1) {
            // <ime_tipa> ::= <specifikator_tipa>
            SemNodeV specifikator_tipa = (SemNodeV) node.getChild(0);

            // 1. provjeri (<specifikator_tipa>)
            check(specifikator_tipa, table);

            // tip <- <specifikator_tipa>.tip
            node.setType(specifikator_tipa.getType());

        } else if (pe == ProductionEnum.ime_tipa_2) {
            // <ime_tipa> ::= KR_CONST <specifikator_tipa>
            SemNodeV specifikator_tipa = (SemNodeV) node.getChild(1);

            // 1. provjeri (<specifikator_tipa>)
            check(specifikator_tipa, table);
            // 2. <specifikator_tipa>.tip != void
            if (specifikator_tipa.getType() == Type.VOID) {
                throw new SemanticException("Can't create const void", node);
            }

            // tip <- const(<specifikator_tipa>.tip)
            Type t = Type.getConst(specifikator_tipa.getType());
            if (t == null) {
                throw new SemanticException("Type isn't a numeric type", node);
            }
            node.setType(t);

        } else if (pe == ProductionEnum.specifikator_tipa_1) {
            // <specifikator_tipa> ::= KR_VOID

            // tip <- void
            node.setType(Type.VOID);

        } else if (pe == ProductionEnum.specifikator_tipa_2) {
            // <specifikator_tipa> ::= KR_CHAR

            // tip <- char
            node.setType(Type.CHAR);

        } else if (pe == ProductionEnum.specifikator_tipa_3) {
            // <specifikator_tipa> ::= KR_INT

            // tip <- int
            node.setType(Type.INT);

        } else if (pe == ProductionEnum.multiplikativni_izraz_1) {
            // <multiplikativni_izraz> ::= <cast_izraz>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.multiplikativni_izraz_2
                || pe == ProductionEnum.multiplikativni_izraz_3
                || pe == ProductionEnum.multiplikativni_izraz_4) {
            // <multiplikativni_izraz> ::= <multiplikativni_izraz> 
            // (OP_PUTA | OP_DIJELI | OP_MOD) <cast_izraz>
            binaryOperatorINT(node, table);

        } else if (pe == ProductionEnum.aditivni_izraz_1) {
            // <aditivni_izraz> ::= <multiplikativni_izraz>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.aditivni_izraz_2 || pe == ProductionEnum.aditivni_izraz_3) {
            // <aditivni_izraz> ::= <aditivni_izraz> (PLUS | MINUS) <multiplikativni_izraz>
            binaryOperatorINT(node, table);

        } else if (pe == ProductionEnum.odnosni_izraz_1) {
            // <odnosni_izraz> ::= <aditivni_izraz>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.odnosni_izraz_2 || pe == ProductionEnum.odnosni_izraz_3
                || pe == ProductionEnum.odnosni_izraz_4 || pe == ProductionEnum.odnosni_izraz_5) {
            // <odnosni_izraz> ::= <odnosni_izraz> 
            // (OP_LT | OP_GT | OP_LTE | OP_GTE) <aditivni_izraz>
            binaryOperatorINT(node, table);

        } else if (pe == ProductionEnum.jednakosni_izraz_1) {
            // <jednakosni_izraz> ::= <odnosni_izraz>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.jednakosni_izraz_2
                || pe == ProductionEnum.jednakosni_izraz_3) {
            // <jednakosni_izraz> ::= <jednakosni_izraz> (OP_EQ | OP_NEQ) <odnosni_izraz>
            binaryOperatorINT(node, table);

        } else if (pe == ProductionEnum.bin_i_izraz_1) {
            // <bin_i_izraz> ::= <jednakosni_izraz>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.bin_i_izraz_2) {
            // <bin_i_izraz> ::= <bin_i_izraz> OP_BIN_I <jednakosni_izraz>
            binaryOperatorINT(node, table);

        } else if (pe == ProductionEnum.bin_xili_izraz_1) {
            // <bin_xili_izraz> ::= <bin_i_izraz>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.bin_xili_izraz_2) {
            // <bin_xili_izraz> ::= <bin_xili_izraz> OP_BIN_XILI <bin_i_izraz>
            binaryOperatorINT(node, table);

        } else if (pe == ProductionEnum.bin_ili_izraz_1) {
            // <bin_ili_izraz> ::= <bin_xili_izraz>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.bin_ili_izraz_2) {
            // <bin_ili_izraz> ::= <bin_ili_izraz> OP_BIN_ILI <bin_xili_izraz>
            binaryOperatorINT(node, table);

        } else if (pe == ProductionEnum.log_i_izraz_1) {
            // <log_i_izraz> ::= <bin_ili_izraz>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.log_i_izraz_2) {
            // <log_i_izraz> ::= <log_i_izraz> OP_I <bin_ili_izraz>
            binaryOperatorINT(node, table);

        } else if (pe == ProductionEnum.log_ili_izraz_1) {
            // <log_ili_izraz> ::= <log_i_izraz>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.log_ili_izraz_2) {
            // <log_ili_izraz> ::= <log_ili_izraz> OP_ILI <log_i_izraz>
            binaryOperatorINT(node, table);

        } else if (pe == ProductionEnum.izraz_pridruzivanja_1) {
            // <izraz_pridruzivanja> ::= <log_ili_izraz>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.izraz_pridruzivanja_2) {
            // <izraz_pridruzivanja> ::= <postfiks_izraz> OP_PRIDRUZI <izraz_pridruzivanja>
            SemNodeV postfiks_izraz = (SemNodeV) node.getChild(0);
            SemNodeV izraz_pridruzivanja = (SemNodeV) node.getChild(2);

            // 1. provjeri (<postfiks_izraz>)
            check(postfiks_izraz, table);
            // 2. <postfiks_izraz>.l-izraz = 1
            if (!postfiks_izraz.getLExpr()) {
                throw new SemanticException("Not an L-expression", node);
            }
            // 3. provjeri (<izraz_pridruzivanja>)
            check(izraz_pridruzivanja, table);
            // 4. <izraz_pridruzivanja>.tip ~ <postfiks_izraz>.tip
            if (!izraz_pridruzivanja.getType().implicit(postfiks_izraz.getType())) {
                throw new SemanticException("Can't convert implicitly", node);
            }

            // tip <- <postfiks_izraz>.tip
            node.setType(postfiks_izraz.getType());
            // l-izraz <- 0
            node.setLExpr(false);

            boolean oneByte = false;
            if (postfiks_izraz.getType() instanceof NumericType)
                oneByte = ((NumericType) postfiks_izraz.getType()).bytes() == 1;
            codegen.assign(oneByte);

        } else if (pe == ProductionEnum.izraz_1) {
            // <izraz> ::= <izraz_pridruzivanja>
            unaryProduction(node, table);

        } else if (pe == ProductionEnum.izraz_2) {
            // <izraz> ::= <izraz> ZAREZ <izraz_pridruzivanja>
            SemNodeV izraz = (SemNodeV) node.getChild(0);
            SemNodeV izraz_pridruzivanja = (SemNodeV) node.getChild(2);

            // 1. provjeri (<izraz>)
            check(izraz, table);
            // 2. provjeri (<izraz_pridruzivanja>)
            check(izraz_pridruzivanja, table);

            // tip <- <izraz_pridruzivanja>.tip
            node.setType(izraz_pridruzivanja.getType());
            // l-izraz <- 0
            node.setLExpr(false);

        } else if (pe == ProductionEnum.slozena_naredba_1) {
            // <slozena_naredba> ::= L_VIT_ZAGRADA <lista_naredbi> D_VIT_ZAGRADA
            SemNodeV lista_naredbi = (SemNodeV) node.getChild(1);

            // 1. provjeri (<lista_naredbi>)
            check(lista_naredbi, table);

        } else if (pe == ProductionEnum.slozena_naredba_2) {
            // <slozena_naredba> ::= L_VIT_ZAGRADA <lista_deklaracija> <lista_naredbi> D_VIT_ZAGRADA
            SemNodeV lista_deklaracija = (SemNodeV) node.getChild(1);
            SemNodeV lista_naredbi = (SemNodeV) node.getChild(2);

            // 1. provjeri (<lista_deklaracija>)
            check(lista_deklaracija, table);
            // 2. provjeri (<lista_naredbi>)
            check(lista_naredbi, table);

        } else if (pe == ProductionEnum.lista_naredbi_1) {
            // <lista_naredbi> ::= <naredba>
            SemNodeV naredba = (SemNodeV) node.getChild(0);

            // 1. provjeri (<naredba>)
            check(naredba, table);

        } else if (pe == ProductionEnum.lista_naredbi_2) {
            // <lista_naredbi> ::= <lista_naredbi> <naredba>
            SemNodeV lista_naredbi = (SemNodeV) node.getChild(0);
            SemNodeV naredba = (SemNodeV) node.getChild(1);

            // 1. provjeri (<lista_naredbi>)
            check(lista_naredbi, table);
            // 2. provjeri (<naredba>)
            check(naredba, table);

        } else if (pe == ProductionEnum.naredba_1) {
            // <naredba> ::= <slozena_naredba>
            SemNodeV slozena_naredba = (SemNodeV) node.getChild(0);

            SymbolTable nested = table.createNested();

            // 1. provjeri (<slozena_naredba>)
            check(slozena_naredba, nested);

        } else if (pe == ProductionEnum.naredba_2 || pe == ProductionEnum.naredba_3
                || pe == ProductionEnum.naredba_4 || pe == ProductionEnum.naredba_5) {
            // <naredba> ::= (<izraz_naredba> | <naredba_grananja> | 
            // <naredba_petlje> | <naredba_skoka>)
            SemNodeV naredba = (SemNodeV) node.getChild(0);

            // 1. provjeri (<naredba>)
            check(naredba, table);

        } else if (pe == ProductionEnum.izraz_naredba_1) {
            // <izraz_naredba> ::= TOCKAZAREZ

            // tip <- int
            node.setType(Type.INT);

        } else if (pe == ProductionEnum.izraz_naredba_2) {
            // <izraz_naredba> ::= <izraz> TOCKAZAREZ
            SemNodeV izraz = (SemNodeV) node.getChild(0);

            // 1. provjeri (<izraz>)
            check(izraz, table);

            // tip <- <izraz>.tip
            node.setType(izraz.getType());

        } else if (pe == ProductionEnum.naredba_grananja_1) {
            // <naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba>
            SemNodeV izraz = (SemNodeV) node.getChild(2);
            SemNodeV naredba = (SemNodeV) node.getChild(4);

            // 1. provjeri (<izraz>)
            check(izraz, table);
            // 2. <izraz>.tip ~ int
            checkImplicit2Int(izraz, node);
            // 3. provjeri (<naredba>)
            check(naredba, table);

        } else if (pe == ProductionEnum.naredba_grananja_2) {
            // <naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba> KR_ELSE <naredba>
            SemNodeV izraz = (SemNodeV) node.getChild(2);
            SemNodeV naredba1 = (SemNodeV) node.getChild(4);
            SemNodeV naredba2 = (SemNodeV) node.getChild(6);

            // 1. provjeri (<izraz>)
            check(izraz, table);
            // 2. <izraz>.tip ~ int
            checkImplicit2Int(izraz, node);
            // 3. provjeri (<naredba> 1)
            check(naredba1, table);
            // 4. provjeri (<naredba> 2)
            check(naredba2, table);

        } else if (pe == ProductionEnum.naredba_petlje_1) {
            // <naredba_petlje> ::= KR_WHILE L_ZAGRADA <izraz> D_ZAGRADA <naredba>
            SemNodeV izraz = (SemNodeV) node.getChild(2);
            SemNodeV naredba = (SemNodeV) node.getChild(4);

            node.setAttributeRecursive(Attribute.LOOP, true);

            // 1. provjeri (<izraz>)
            check(izraz, table);
            // 2. <izraz>.tip ~ int
            checkImplicit2Int(izraz, node);
            // 3. provjeri (<naredba>)
            check(naredba, table);

        } else if (pe == ProductionEnum.naredba_petlje_2) {
            // <naredba_petlje> ::= KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> D_ZAGRADA <naredba>
            SemNodeV izraz_naredba1 = (SemNodeV) node.getChild(2);
            SemNodeV izraz_naredba2 = (SemNodeV) node.getChild(3);
            SemNodeV naredba = (SemNodeV) node.getChild(5);

            node.setAttributeRecursive(Attribute.LOOP, true);

            // 1. provjeri (<izraz_naredba> 1)
            check(izraz_naredba1, table);
            // 2. provjeri (<izraz_naredba> 2)
            check(izraz_naredba2, table);
            // 3. <izraz_naredba> 2 .tip ~ int
            checkImplicit2Int(izraz_naredba2, node);
            // 4. provjeri (<naredba>)
            check(naredba, table);

        } else if (pe == ProductionEnum.naredba_petlje_3) {
            // <naredba_petlje> ::= KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> <izraz> D_ZAGRADA <naredba>
            SemNodeV izraz_naredba1 = (SemNodeV) node.getChild(2);
            SemNodeV izraz_naredba2 = (SemNodeV) node.getChild(3);
            SemNodeV izraz = (SemNodeV) node.getChild(4);
            SemNodeV naredba = (SemNodeV) node.getChild(6);

            node.setAttributeRecursive(Attribute.LOOP, true);

            // 1. provjeri (<izraz_naredba>1)
            check(izraz_naredba1, table);
            // 2. provjeri (<izraz_naredba>2)
            check(izraz_naredba2, table);
            // 3. <izraz_naredba>2.tip ~ int
            checkImplicit2Int(izraz_naredba2, node);
            // 4. provjeri (<izraz>)
            check(izraz, table);
            // 5. provjeri (<naredba>)
            check(naredba, table);

        } else if (pe == ProductionEnum.naredba_skoka_1 || pe == ProductionEnum.naredba_skoka_2) {
            // <naredba_skoka> ::= (KR_CONTINUE | KR_BREAK) TOCKAZAREZ

            // 1. naredba se nalazi unutar petlje ili unutar bloka koji je ugnijezden u petlji
            boolean loop = false;
            try {
                loop = (boolean) node.getAttribute(Attribute.LOOP);
            } catch (Exception ex) {
            }
            if (!loop) {
                throw new SemanticException("Jump command outside of a loop", node);
            }

        } else if (pe == ProductionEnum.naredba_skoka_3) {
            // <naredba_skoka> ::= KR_RETURN TOCKAZAREZ

            // 1. naredba se nalazi unutar funkcije tipa funkcija(params -> void)
            if (table.getReturnType() != Type.VOID) {
                throw new SemanticException("Return without parameters", node);
            }

        } else if (pe == ProductionEnum.naredba_skoka_4) {
            // <naredba_skoka> ::= KR_RETURN <izraz> TOCKAZAREZ
            SemNodeV izraz = (SemNodeV) node.getChild(1);

            // 1. provjeri (<izraz>)
            check(izraz, table);

            // 2. naredba se nalazi unutar funkcije tipa funkcija(params -> pov) i vrijedi
            // <izraz>.tip ~ pov
            Type t = table.getReturnType();
            if (t == null || !izraz.getType().implicit(t)) {
                throw new SemanticException("Return types don't match", node);
            }

        } else if (pe == ProductionEnum.prijevodna_jedinica_1) {
            // <prijevodna_jedinica> ::= <vanjska_deklaracija>
            SemNodeV vanjska_deklaracija = (SemNodeV) node.getChild(0);

            // 1. provjeri (<vanjska_deklaracija>)
            check(vanjska_deklaracija, table);

        } else if (pe == ProductionEnum.prijevodna_jedinica_2) {
            // <prijevodna_jedinica> ::= <prijevodna_jedinica> <vanjska_deklaracija>
            SemNodeV prijevodna_jedinica = (SemNodeV) node.getChild(0);
            SemNodeV vanjska_deklaracija = (SemNodeV) node.getChild(1);

            // 1. provjeri (<prijevodna_jedinica>)
            check(prijevodna_jedinica, table);
            // 2. provjeri (<vanjska_deklaracija>)
            check(vanjska_deklaracija, table);

        } else if (pe == ProductionEnum.vanjska_deklaracija_1) {
            // <vanjska_deklaracija> ::= <definicija_funkcije>
            SemNodeV definicija_funkcije = (SemNodeV) node.getChild(0);

            // 1. provjeri(<definicija_funkcije>)
            check(definicija_funkcije, table);

        } else if (pe == ProductionEnum.vanjska_deklaracija_2) {
            // <vanjska_deklaracija> ::= <deklaracija>
            SemNodeV deklaracija = (SemNodeV) node.getChild(0);

            // 1. provjeri(<deklaracija>)
            check(deklaracija, table);

        } else if (pe == ProductionEnum.definicija_funkcije_1) {
            // <definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA KR_VOID D_ZAGRADA <slozena_naredba>
            SemNodeV ime_tipa = (SemNodeV) node.getChild(0);
            SemNodeT idn = (SemNodeT) node.getChild(1);
            SemNodeV slozena_naredba = (SemNodeV) node.getChild(5);

            // 1. provjeri (<ime_tipa>)
            check(ime_tipa, table);
            // 2. <ime_tipa>.tip != const(T)
            Type t = ime_tipa.getType();
            if (t instanceof NumericType) {
                NumericType nt = (NumericType) t;
                if (nt.isConst()) {
                    throw new SemanticException("Can't be const qualified", node);
                }
            }

            String fName = idn.getValue();

            SymbolTableEntry ste = global.getEntry(fName);
            FunctionType ft = new FunctionType(new ListType(Type.VOID), t);
            if (ste == null) {
                ste = new SymbolTableEntry(ft);
                table.addEntry(fName, ste);
            } else {
                // 3. ne postoji prije definirana funkcija imena IDN.ime
                if (ste.getDefined()) {
                    throw new SemanticException("Function already defined", node);
                }
                // 4. ako postoji deklaracija imena IDN.ime u globalnom djelokrugu onda je pripadni
                // tip te deklaracije funkcija(void -> <ime_tipa>.tip)
                if (!ste.getType().same(ft)) {
                    throw new SemanticException("Function definitions differ", node);
                }
            }
            // 5. zabiljezi definiciju i deklaraciju funkcije
            ste.setDefined();
            // 6. provjeri (<slozena_naredba>)
            SymbolTable functionTable = table.createNested();
            functionTable.setReturnType(t);

            codegen.functionStart(fName);

            check(slozena_naredba, functionTable);

            int numberOfLocalVariables = functionTable.getLocalSize();
            boolean returns = t != Type.VOID;
            codegen.functionEnd(fName, numberOfLocalVariables, returns);

        } else if (pe == ProductionEnum.definicija_funkcije_2) {
            // <definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA <lista_parametara> D_ZAGRADA <slozena_naredba>
            SemNodeV ime_tipa = (SemNodeV) node.getChild(0);
            SemNodeT idn = (SemNodeT) node.getChild(1);
            SemNodeV lista_parametara = (SemNodeV) node.getChild(3);
            SemNodeV slozena_naredba = (SemNodeV) node.getChild(5);

            // 1. provjeri (<ime_tipa>)
            check(ime_tipa, table);
            // 2. <ime_tipa>.tip != const(T)
            Type t = ime_tipa.getType();
            if (t instanceof NumericType) {
                NumericType nt = (NumericType) t;
                if (nt.isConst()) {
                    throw new SemanticException("Can't be const qualified", node);
                }
            }
            String fName = idn.getValue();
            // 3. ne postoji prije definirana funkcija imena IDN.ime
            SymbolTableEntry ste = global.getEntry(fName);
            if (ste != null && ste.getDefined()) {
                throw new SemanticException("Function already defined", node);
            }
            // 4. provjeri (<lista_parametara>)
            check(lista_parametara, table);
            // 5. ako postoji deklaracija imena IDN.ime u globalnom djelokrugu onda je pripadni
            // tip te deklaracije funkcija(<lista_parametara>.tipovi -> <ime_tipa>.tip)
            FunctionType ft = new FunctionType((ListType) lista_parametara.getType(), t);
            if (ste != null && !ft.same(ste.getType())) {
                throw new SemanticException("Parameters don't match", node);
            }
            if (ste == null) {
                ste = new SymbolTableEntry(ft);
                table.addEntry(fName, ste);
            }
            // 6. zabiljezi definiciju i deklaraciju funkcije
            ste.setDefined();
            // 7. provjeri (<slozena_naredba>) uz parametre funkcije koristeci <lista_parametara>.tipovi
            // i <lista_parametara>.ime_imena
            SymbolTable functionTable = table.createNested();
            functionTable.setReturnType(t);

            @SuppressWarnings("unchecked")
            List<String> names = (List<String>) lista_parametara.getAttribute(Attribute.NAMES);
            ListType types = (ListType) lista_parametara.getType();
            functionTable.addParameters(names, types);

            codegen.functionStart(fName);

            check(slozena_naredba, functionTable);

            int numberOfLocalVariables = functionTable.getLocalSize();
            boolean returns = t != Type.VOID;
            codegen.functionEnd(fName, numberOfLocalVariables, returns);

        } else if (pe == ProductionEnum.lista_parametara_1) {
            // <lista_parametara> ::= <deklaracija_parametra>
            SemNodeV deklaracija_parametara = (SemNodeV) node.getChild(0);

            // 1. provjeri (<deklaracija_parametra>)
            check(deklaracija_parametara, table);

            // tipovi <- [ <deklaracija_parametra>.tip ]
            node.setType(new ListType(deklaracija_parametara.getType()));
            // imena <- [ <deklaracija_parametra>.ime ]
            String name = (String) deklaracija_parametara.getAttribute(Attribute.NAME);
            List<String> names = new LinkedList<>();
            names.add(name);
            node.setAttribute(Attribute.NAMES, names);

        } else if (pe == ProductionEnum.lista_parametara_2) {
            // <lista_parametara> ::= <lista_parametara> ZAREZ <deklaracija_parametra>
            SemNodeV lista_parametara = (SemNodeV) node.getChild(0);
            SemNodeV deklaracija_parametara = (SemNodeV) node.getChild(2);

            // 1. provjeri (<lista_parametara>)
            check(lista_parametara, table);
            // 2. provjeri (<deklaracija_parametra>)
            check(deklaracija_parametara, table);
            // 3. <deklaracija_parametra>.ime ne postoji u <lista_parametara>.imena
            @SuppressWarnings("unchecked")
            List<String> names = (List<String>) lista_parametara.getAttribute(Attribute.NAMES);
            String name = (String) deklaracija_parametara.getAttribute(Attribute.NAME);
            if (names.contains(name)) {
                throw new SemanticException("Parameter name already defined", node);
            }

            names.add(name);
            ListType lt = (ListType) lista_parametara.getType();
            lt.addType(deklaracija_parametara.getType());

            // tipovi <- <lista_parametara>.tipovi + [ <deklaracija_parametra>.tip ]
            node.setType(lt);
            // imena <- <lista_parametara>.imena + [ <deklaracija_parametra>.ime ]
            node.setAttribute(Attribute.NAMES, names);

        } else if (pe == ProductionEnum.deklaracija_parametra_1) {
            // <deklaracija_parametra> ::= <ime_tipa> IDN
            SemNodeV ime_tipa = (SemNodeV) node.getChild(0);
            SemNodeT idn = (SemNodeT) node.getChild(1);

            // 1. provjeri (<ime_tipa>)
            check(ime_tipa, table);
            // 2. <ime_tipa>.tip != void
            if (ime_tipa.getType() == Type.VOID) {
                throw new SemanticException("Can't declare void type", node);
            }

            // tip <- <ime_tipa>.tip
            node.setType(ime_tipa.getType());
            // ime <- IDN.ime
            node.setAttribute(Attribute.NAME, idn.getValue());

        } else if (pe == ProductionEnum.deklaracija_parametra_2) {
            // <deklaracija_parametra> ::= <ime_tipa> IDN L_UGL_ZAGRADA D_UGL_ZAGRADA
            SemNodeV ime_tipa = (SemNodeV) node.getChild(0);
            SemNodeT idn = (SemNodeT) node.getChild(1);

            // 1. provjeri (<ime_tipa>)
            check(ime_tipa, table);
            // 2. <ime_tipa>.tip != void
            if (ime_tipa.getType() == Type.VOID) {
                throw new SemanticException("Can't declare void type", node);
            }

            // tip <- niz(<ime_tipa>.tip)
            node.setType(Type.getArray(ime_tipa.getType()));
            // ime <- IDN.ime
            node.setAttribute(Attribute.NAME, idn.getValue());

        } else if (pe == ProductionEnum.lista_deklaracija_1) {
            // <lista_deklaracija> ::= <deklaracija>
            SemNodeV deklaracija = (SemNodeV) node.getChild(0);

            // 1. provjeri (<deklaracija>)
            check(deklaracija, table);

        } else if (pe == ProductionEnum.lista_deklaracija_2) {
            // <lista_deklaracija> ::= <lista_deklaracija> <deklaracija>
            SemNodeV lista_deklaracija = (SemNodeV) node.getChild(0);
            SemNodeV deklaracija = (SemNodeV) node.getChild(1);

            // 1. provjeri (<lista_deklaracija>)
            check(lista_deklaracija, table);
            // 2. provjeri (<deklaracija>)
            check(deklaracija, table);

        } else if (pe == ProductionEnum.deklaracija_1) {
            // <deklaracija> ::= <ime_tipa> <lista_init_deklaratora> TOCKAZAREZ
            SemNodeV ime_tipa = (SemNodeV) node.getChild(0);
            SemNodeV lista_init_deklaratora = (SemNodeV) node.getChild(1);

            // 1. provjeri (<ime_tipa>)
            check(ime_tipa, table);
            // 2. provjeri (<lista_init_deklaratora>) uz nasljedno svojstvo
            // <lista_init_deklaratora>.ntip <- <ime_tipa>.tip
            lista_init_deklaratora.setAttribute(Attribute.NTYPE, ime_tipa.getType());
            check(lista_init_deklaratora, table);

        } else if (pe == ProductionEnum.lista_init_deklaratora_1) {
            // <lista_init_deklaratora> ::= <init_deklarator>
            SemNodeV init_deklarator = (SemNodeV) node.getChild(0);

            // 1. provjeri (<init_deklarator>) uz nasljedno svojstvo
            // <init_deklarator>.ntip <- <lista_init_deklaratora>.ntip
            init_deklarator.setAttribute(Attribute.NTYPE, node.getAttribute(Attribute.NTYPE));
            check(init_deklarator, table);

        } else if (pe == ProductionEnum.lista_init_deklaratora_2) {
            // <lista_init_deklaratora> ::= <lista_init_deklaratora> ZAREZ <init_deklarator>
            SemNodeV lista_init_deklaratora = (SemNodeV) node.getChild(0);
            SemNodeV init_deklarator = (SemNodeV) node.getChild(2);

            // 1. provjeri (<lista_init_deklaratora> 2 ) uz nasljedno svojstvo
            // <lista_init_deklaratora> 2 .ntip <- <lista_init_deklaratora> 1 .ntip
            lista_init_deklaratora.setAttribute(Attribute.NTYPE,
                    node.getAttribute(Attribute.NTYPE));
            check(lista_init_deklaratora, table);
            // 2. provjeri (<init_deklarator>) uz nasljedno svojstvo
            // <init_deklarator>.ntip <- <lista_init_deklaratora> 1 .ntip
            init_deklarator.setAttribute(Attribute.NTYPE, node.getAttribute(Attribute.NTYPE));
            check(init_deklarator, table);

        } else if (pe == ProductionEnum.init_deklarator_1) {
            // <init_deklarator> ::= <izravni_deklarator>
            SemNodeV izravni_deklarator = (SemNodeV) node.getChild(0);

            // 1. provjeri (<izravni_deklarator>) uz nasljedno svojstvo
            // <izravni_deklarator>.ntip <- <init_deklarator>.ntip
            izravni_deklarator.setAttribute(Attribute.NTYPE, node.getAttribute(Attribute.NTYPE));
            check(izravni_deklarator, table);
            // 2. <izravni_deklarator>.tip != const(T)
            // <izravni_deklarator>.tip != niz (const(T))
            Type t = izravni_deklarator.getType();
            if (t instanceof ConstType) {
                throw new SemanticException("Can't be const", node);
            } else if (t instanceof ArrayType) {
                ArrayType at = (ArrayType) t;
                if (at.getType().isConst()) {
                    throw new SemanticException("Can't be const", node);
                }
            }

        } else if (pe == ProductionEnum.init_deklarator_2) {
            // <init_deklarator> ::= <izravni_deklarator> OP_PRIDRUZI <inicijalizator>
            SemNodeV izravni_deklarator = (SemNodeV) node.getChild(0);
            SemNodeV inicijalizator = (SemNodeV) node.getChild(2);

            // 1. provjeri (<izravni_deklarator>) uz nasljedno svojstvo
            // <izravni_deklarator>.ntip <- <init_deklarator>.ntip
            izravni_deklarator.setAttribute(Attribute.NTYPE, node.getAttribute(Attribute.NTYPE));
            check(izravni_deklarator, table);

            // 2. provjeri (<incijalizator>)
            check(inicijalizator, table);

            boolean oneByte = false;
            // 3. ako je <izravni_deklarator>.tip T ili const(T) 
            //      <inicijalizator>.tip ~ T
            // inace ako je <izravni_deklarator>.tip niz (T) ili niz (const(T))
            //      <inicijalizator>.br-elem <= <izravni_deklarator>.br-elem 
            //      za svaki U iz <inicijalizator>.tipovi vrijedi U ~ T 
            // inace greska
            Type t = izravni_deklarator.getType();
            if (t instanceof NumericType) {
                Type to = t;
                if (to instanceof ConstType) {
                    to = ((ConstType) to).getType();
                }
                if (!inicijalizator.getType().implicit(to)) {
                    throw new SemanticException("Can't convert to type", node);
                }

                oneByte = ((NumericType) t).bytes() == 1;

            } else if (t instanceof ArrayType) {
                if (!inicijalizator.hasAttribute(Attribute.NUM_EL)) {
                    throw new SemanticException("When char a[2] = \"a\"; char b[2] = a;", node);
                }

                int inic_br_elem = (int) inicijalizator.getAttribute(Attribute.NUM_EL);
                int izde_br_elem = (int) izravni_deklarator.getAttribute(Attribute.NUM_EL);

                if (inic_br_elem > izde_br_elem) {
                    throw new SemanticException("Index too big", node);
                }

                ListType lt = (ListType) inicijalizator.getType();
                NumericType nt = ((ArrayType) t).getType();
                if (nt instanceof ConstType) {
                    nt = ((ConstType) nt).getType();
                }
                if (!lt.eachImplicit(nt)) {
                    throw new SemanticException("Elements can't be converted", node);
                }
            } else {
                throw new SemanticException("Invalid type", node);
            }

            codegen.assign(oneByte);

        } else if (pe == ProductionEnum.izravni_deklarator_1) {
            // <izravni_deklarator> ::= IDN
            SemNodeT idn = (SemNodeT) node.getChild(0);
            String varName = idn.getValue();

            // 1. ntip != void
            Type ntype = (Type) node.getAttribute(Attribute.NTYPE);
            if (ntype == Type.VOID) {
                throw new SemanticException("Type can't be void", node);
            }
            // 2. IDN.ime nije deklarirano u lokalnom djelokrugu
            if (table.getLocalEntry(varName) != null) {
                throw new SemanticException("Already declared identifier", node);
            }
            // 3. zabiljezi deklaraciju IDN.ime s odgovarajucim tipom
            SymbolTableEntry ste = new SymbolTableEntry(ntype);
            table.addEntry(varName, ste);

            // tip <- ntip
            node.setType(ntype);

            boolean oneByte = false;
            if (ntype instanceof NumericType) oneByte = ((NumericType) ntype).bytes() == 1;

            if (table.isGlobal()) {
                codegen.allocGlobal(varName, oneByte);
                codegen.globalRef(varName, oneByte, false);
            } else {
                codegen.allocLocal(varName);
                codegen.localRef(ste.getOffset(table), oneByte, false);
            }

        } else if (pe == ProductionEnum.izravni_deklarator_2) {
            // <izravni_deklarator> ::= IDN L_UGL_ZAGRADA BROJ D_UGL_ZAGRADA
            SemNodeT idn = (SemNodeT) node.getChild(0);
            SemNodeT broj = (SemNodeT) node.getChild(2);
            String varName = idn.getValue();

            // 1. ntip != void
            Type ntype = (Type) node.getAttribute(Attribute.NTYPE);
            if (ntype == Type.VOID) {
                throw new SemanticException("Type can't be void", node);
            }
            // 2. IDN.ime nije deklarirano u lokalnom djelokrugu
            if (table.getLocalEntry(varName) != null) {
                throw new SemanticException("Already declared identifier", node);
            }
            // 3. BROJ.vrijednost je pozitivan broj (> 0) ne veci od 1024
            int vrijednost = parseInt(broj.getValue(), node);
            if (vrijednost <= 0 || vrijednost > ARRAY_TOP) {
                throw new SemanticException("Index out of bounds", node);
            }
            Type arrayNtype = Type.getArray(ntype);
            // 4. zabiljezi deklaraciju IDN.ime s odgovarajucim tipom
            SymbolTableEntry ste = new SymbolTableEntry(arrayNtype);
            table.addEntry(varName, ste);

            // tip <- niz (ntip)
            node.setType(arrayNtype);
            // br-elem <- BROJ.vrijednost
            node.setAttribute(Attribute.NUM_EL, vrijednost);

            boolean oneByte = false; // false because its a pointer = int
            if (table.isGlobal()) {
                codegen.allocGlobal(varName, oneByte);
                codegen.arrayAlloc(varName, vrijednost, oneByte);
                codegen.globalRef(varName, oneByte, false);
            } else {
                codegen.allocLocal(varName);
                codegen.arrayAlloc(varName, vrijednost, oneByte);
                codegen.localRef(ste.getOffset(table), oneByte, false);
            }

        } else if (pe == ProductionEnum.izravni_deklarator_3) {
            // <izravni_deklarator> ::= IDN L_ZAGRADA KR_VOID D_ZAGRADA
            SemNodeT idn = (SemNodeT) node.getChild(0);

            Type ntype = (Type) node.getAttribute(Attribute.NTYPE);
            FunctionType ft = new FunctionType(new ListType(Type.VOID), (PrimitiveType) ntype);
            SymbolTableEntry ste = table.getLocalEntry(idn.getValue());

            if (ste != null) {
                // 1. ako je IDN.ime deklarirano u lokalnom djelokrugu, tip prethodne deklaracije
                // je jednak funkcija(void -> ntip)
                if (!ft.same(ste.getType())) {
                    throw new SemanticException("Wrong function declaration", node);
                }
            } else {
                // 2. zabiljezi deklaraciju IDN.ime s odgovarajucim tipom ako ista funkcija vec nije
                // deklarirana u lokalnom djelokrugu
                table.addEntry(idn.getValue(), new SymbolTableEntry(ft));
            }

            // tip <- funkcija(void -> ntip)
            node.setType(ft);

        } else if (pe == ProductionEnum.izravni_deklarator_4) {
            // <izravni_deklarator> ::= IDN L_ZAGRADA <lista_parametara> D_ZAGRADA
            SemNodeT idn = (SemNodeT) node.getChild(0);
            SemNodeV lista_parametara = (SemNodeV) node.getChild(2);

            Type ntype = (Type) node.getAttribute(Attribute.NTYPE);
            SymbolTableEntry ste = table.getLocalEntry(idn.getValue());

            // 1. provjeri (<lista_parametara>)
            check(lista_parametara, table);

            FunctionType ft = new FunctionType((ListType) lista_parametara.getType(), ntype);

            if (ste != null) {
                // 2. ako je IDN.ime deklarirano u lokalnom djelokrugu, tip prethodne deklaracije
                // je jednak funkcija(<lista_parametara>.tipovi -> ntip)
                if (!ft.same(ste.getType())) {
                    throw new SemanticException("Wrong function declaration", node);
                }
            } else {
                // 3. zabiljezi deklaraciju IDN.ime s odgovarajucim tipom ako ista funkcija vec nije
                // deklarirana u lokalnom djelokrugu
                table.addEntry(idn.getValue(), new SymbolTableEntry(ft));
            }

            // tip <- funkcija(<lista_parametara>.tipovi -> ntip)
            node.setType(ft);

        } else if (pe == ProductionEnum.inicijalizator_1) {
            // <inicijalizator> ::= <izraz_pridruzivanja>
            SemNodeV izraz_pridruzivanja = (SemNodeV) node.getChild(0);

            // 1. provjeri (<izraz_pridruzivanja>)
            check(izraz_pridruzivanja, table);

            // ako je <izraz_pridruzivanja> => NIZ_ZNAKOVA
            //      br-elem <- duljina niza znakova + 1
            //      tipovi <- lista duljine br-elem, svi elementi su char
            // inace
            //      tip <- <izraz_pridruzivanja>.tip
            SemNode sn = izraz_pridruzivanja;
            while (sn instanceof SemNodeV) {
                SemNodeV snv = (SemNodeV) sn;
                if (snv.numOfChildren() != 1) {
                    break;
                }
                sn = snv.getChild(0);
            }

            if (sn instanceof SemNodeT && sn.getName().equals("NIZ_ZNAKOVA")) {
                String decoded = parseString(((SemNodeT) sn).getValue(), node);
                int duljina = decoded.length();
                ListType lt = new ListType(Type.CHAR);
                for (int i = 0; i < duljina; i++) {
                    lt.addType(Type.CHAR);
                }
                node.setAttribute(Attribute.NUM_EL, duljina + 1);
                node.setType(lt);
            } else {
                node.setType(izraz_pridruzivanja.getType());
            }

        } else if (pe == ProductionEnum.inicijalizator_2) {
            // <inicijalizator> ::= L_VIT_ZAGRADA <lista_izraza_pridruzivanja> D_VIT_ZAGRADA
            SemNodeV lista_izraza_pridruzivanja = (SemNodeV) node.getChild(1);

            // 1. provjeri (<lista_izraza_pridruzivanja>)
            check(lista_izraza_pridruzivanja, table);

            int brElem = (int) lista_izraza_pridruzivanja.getAttribute(Attribute.NUM_EL);
            // br-elem <- <lista_izraza_pridruzivanja>.br-elem
            node.setAttribute(Attribute.NUM_EL, brElem);
            // tipovi <- <lista_izraza_pridruzivanja>.tipovi
            node.setType(lista_izraza_pridruzivanja.getType());

            boolean oneByte = false;
            Type t = ((ListType) lista_izraza_pridruzivanja.getType()).getType(0);
            if (t instanceof NumericType) oneByte = ((NumericType) t).bytes() == 1;
            codegen.arrayInitialization(brElem, oneByte);

        } else if (pe == ProductionEnum.lista_izraza_pridruzivanja_1) {
            // <lista_izraza_pridruzivanja> ::= <izraz_pridruzivanja>
            SemNodeV izraz_pridruzivanja = (SemNodeV) node.getChild(0);

            // 1. provjeri (<izraz_pridruzivanja>)
            check(izraz_pridruzivanja, table);

            // tipovi <- [ <izraz_pridruzivanja>.tip ]
            node.setType(new ListType(izraz_pridruzivanja.getType()));
            // br-elem <- 1
            node.setAttribute(Attribute.NUM_EL, 1);

        } else if (pe == ProductionEnum.lista_izraza_pridruzivanja_2) {
            // <lista_izraza_pridruzivanja> ::= <lista_izraza_pridruzivanja> ZAREZ <izraz_pridruzivanja
            SemNodeV lista_izraza_pridruzivanja = (SemNodeV) node.getChild(0);
            SemNodeV izraz_pridruzivanja = (SemNodeV) node.getChild(2);

            // 1. provjeri (<lista_izraza_pridruzivanja>)
            check(lista_izraza_pridruzivanja, table);
            // 2. provjeri (<izraz_pridruzivanja>)
            check(izraz_pridruzivanja, table);

            // tipovi <- <lista_izraza_pridruzivanja>.tipovi + [ <izraz_pridruzivanja>.tip ]
            ListType lt = (ListType) lista_izraza_pridruzivanja.getType();
            lt.addType(izraz_pridruzivanja.getType());
            node.setType(lt);
            // br-elem <- <lista_izraza_pridruzivanja>.br-elem+ 1
            int numel = (int) lista_izraza_pridruzivanja.getAttribute(Attribute.NUM_EL) + 1;
            node.setAttribute(Attribute.NUM_EL, numel);
        }

    }

}
