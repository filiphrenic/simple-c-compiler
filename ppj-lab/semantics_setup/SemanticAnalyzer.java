package hr.fer.zemris.ppj.semantic;

/**
 * @author fhrenic
 */
public class SemanticAnalyzer{
    
private static final String ESCAPED = "tn0'\"\\";

    private Trie trie;

    private static String checkString(String s) {
        boolean escaping = false;
        for (int idx = 1; idx < s.length() - 1; idx++) {
            char c = s.charAt(idx);
            if (escaping) {
                escaping = false;
                if (!ESCAPED.contains(c + "")) {
                    return "Unknown escape sequence at " + s;
                }
            } else {
                if (c == '\\') {
                    escaping = true;
                }
                if (c == '"' || c == '\'') {
                    return "Invalid string " + s;
                }
            }
        }
        if (escaping) {
            return "Invalid string " + s;
        }
        return null;
    }


    public void check(SemNodeV node, SymbolTable table){

        ProductionEnum pe = trie.findProduction(node);
        if (pe == null) {
            throw new SemanticException("Production not found", node);
        }

 if (pe==ProductionEnum.primarni_izraz_1){
// <primarni_izraz> ::= IDN
 	SemNodeT idn = (SemNodeT) node.getChild(0);
                SymbolTableEntry idnSte = table.getEntry(idn.getName());

                // 1. IDN.ime je deklarirano
                if (idnSte == null) {
                    throw new SemanticException("IDN.ime not declared", node);
                }
                // tip ← IDN.tip
                node.setAttribute(Attribute.TYPE, idnSte.getType());
                // l-izraz ← IDN.l-izraz
                node.setAttribute(Attribute.LEXPR, idnSte.isLExpression());

}
else if (pe==ProductionEnum.primarni_izraz_2){
// <primarni_izraz> ::= BROJ
	 SemNodeT broj = (SemNodeT) node.getChild(0);

                // 1. vrijednost je u rasponu tipa int
                int intValue = Integer.parseInt(broj.getValue());
                if (!Type.INT.isInRange(intValue)) {
                    throw new SemanticException("Value not in INT range", node);
                }
                // tip ← int
                node.setAttribute(Attribute.TYPE, Type.INT);
                // l-izraz ← 0
                node.setAttribute(Attribute.LEXPR, false);
}
else if (pe==ProductionEnum.primarni_izraz_3){
// <primarni_izraz> ::= ZNAK
	SemNodeT znak = (SemNodeT) node.getChild(0);

                // 1. vrijednost je u redu po 4.3.2
                String znakValue = znak.getValue();
                if (znakValue.length() > 4) {
                    throw new SemanticException("Invalid sign " + znakValue, node);
                }
                String error = checkString(znakValue);
                if (error != null)
                    throw new SemanticException(error, node);

                // tip ← char
                node.setAttribute(Attribute.TYPE, Type.CHAR);
                // l-izraz ← 0
                node.setAttribute(Attribute.LEXPR, false);
}
else if (pe==ProductionEnum.primarni_izraz_4){
// <primarni_izraz> ::= NIZ_ZNAKOVA
	 SemNodeT niz_znakova = (SemNodeT) node.getChild(0);

                // 1. konstantni niz znakova je ispravan po 4.3.2
                error = checkString(niz_znakova.getValue());
                if (error != null)
                    throw new SemanticException(error, node);
                // tip ← niz (const(char))
                node.setAttribute(Attribute.TYPE, Type.ARRAY_CONST_CHAR);
                // l-izraz ← 0
                node.setAttribute(Attribute.LEXPR, false);
}
else if (pe==ProductionEnum.primarni_izraz_5){
// <primarni_izraz> ::= L_ZAGRADA <izraz> D_ZAGRADA
	SemNodeV izraz = (SemNodeV) node.getChild(1);

                // 1. provjeri (<izraz>)
                check(izraz, table);
                // tip ← <izraz>.tip
                node.setAttribute(Attribute.TYPE, izraz.getAttribute(Attribute.TYPE));
                // l-izraz ← <izraz>.l-izraz
                node.setAttribute(Attribute.LEXPR, izraz.getAttribute(Attribute.LEXPR));
}
else if (pe==ProductionEnum.postfiks_izraz_1){
// <postfiks_izraz> ::= <primarni_izraz>
	SemNodeV primarni_izraz = (SemNodeV) node.getChild(0);

                // 1. provjeri (<primarni_izraz>)
                check(primarni_izraz, table);
                // tip ← <primarni_izraz>.tip
                node.setAttribute(Attribute.TYPE, primarni_izraz.getAttribute(Attribute.TYPE));
                //l-izraz ← <primarni_izraz>.l-izraz
                node.setAttribute(Attribute.LEXPR, primarni_izraz.getAttribute(Attribute.LEXPR));
}
else if (pe==ProductionEnum.postfiks_izraz_2){
// <postfiks_izraz> ::= <postfiks_izraz> L_UGL_ZAGRADA <izraz> D_UGL_ZAGRADA
	 postfiks_izraz
                // 1. provjeri (<postfiks_izraz>)
                // 2. <postfiks_izraz>.tip = niz (X )
                // 3. provjeri (<izraz>)
                // 4. <izraz>.tip ∼ int
}
else if (pe==ProductionEnum.postfiks_izraz_3){
// <postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA D_ZAGRADA
}
else if (pe==ProductionEnum.postfiks_izraz_4){
// <postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA <lista_argumenata> D_ZAGRADA
}
else if (pe==ProductionEnum.postfiks_izraz_5){
// <postfiks_izraz> ::= <postfiks_izraz> OP_INC
}
else if (pe==ProductionEnum.postfiks_izraz_6){
// <postfiks_izraz> ::= <postfiks_izraz> OP_DEC
}
else if (pe==ProductionEnum.lista_argumenata_1){
// <lista_argumenata> ::= <izraz_pridruzivanja>
}
else if (pe==ProductionEnum.lista_argumenata_2){
// <lista_argumenata> ::= <lista_argumenata> ZAREZ <izraz_pridruzivanja>
}
else if (pe==ProductionEnum.unarni_izraz_1){
// <unarni_izraz> ::= <postfiks_izraz>
}
else if (pe==ProductionEnum.unarni_izraz_2){
// <unarni_izraz> ::= OP_INC <unarni_izraz>
}
else if (pe==ProductionEnum.unarni_izraz_3){
// <unarni_izraz> ::= OP_DEC <unarni_izraz>
}
else if (pe==ProductionEnum.unarni_izraz_4){
// <unarni_izraz> ::= <unarni_operator> <cast_izraz>
}
else if (pe==ProductionEnum.unarni_operator_1){
// <unarni_operator> ::= PLUS
}
else if (pe==ProductionEnum.unarni_operator_2){
// <unarni_operator> ::= MINUS
}
else if (pe==ProductionEnum.unarni_operator_3){
// <unarni_operator> ::= OP_TILDA
}
else if (pe==ProductionEnum.unarni_operator_4){
// <unarni_operator> ::= OP_NEG
}
else if (pe==ProductionEnum.cast_izraz_1){
// <cast_izraz> ::= <unarni_izraz>
}
else if (pe==ProductionEnum.cast_izraz_2){
// <cast_izraz> ::= L_ZAGRADA <ime_tipa> D_ZAGRADA <cast_izraz>
}
else if (pe==ProductionEnum.ime_tipa_1){
// <ime_tipa> ::= <specifikator_tipa>
}
else if (pe==ProductionEnum.ime_tipa_2){
// <ime_tipa> ::= KR_CONST <specifikator_tipa>
}
else if (pe==ProductionEnum.specifikator_tipa_1){
// <specifikator_tipa> ::= KR_VOID
}
else if (pe==ProductionEnum.specifikator_tipa_2){
// <specifikator_tipa> ::= KR_CHAR
}
else if (pe==ProductionEnum.specifikator_tipa_3){
// <specifikator_tipa> ::= KR_INT
}
else if (pe==ProductionEnum.multiplikativni_izraz_1){
// <multiplikativni_izraz> ::= <cast_izraz>
}
else if (pe==ProductionEnum.multiplikativni_izraz_2){
// <multiplikativni_izraz> ::= <multiplikativni_izraz> OP_PUTA <cast_izraz>
}
else if (pe==ProductionEnum.multiplikativni_izraz_3){
// <multiplikativni_izraz> ::= <multiplikativni_izraz> OP_DIJELI <cast_izraz>
}
else if (pe==ProductionEnum.multiplikativni_izraz_4){
// <multiplikativni_izraz> ::= <multiplikativni_izraz> OP_MOD <cast_izraz>
}
else if (pe==ProductionEnum.aditivni_izraz_1){
// <aditivni_izraz> ::= <multiplikativni_izraz>
}
else if (pe==ProductionEnum.aditivni_izraz_2){
// <aditivni_izraz> ::= <aditivni_izraz> PLUS <multiplikativni_izraz>
}
else if (pe==ProductionEnum.aditivni_izraz_3){
// <aditivni_izraz> ::= <aditivni_izraz> MINUS <multiplikativni_izraz>
}
else if (pe==ProductionEnum.odnosni_izraz_1){
// <odnosni_izraz> ::= <aditivni_izraz>
}
else if (pe==ProductionEnum.odnosni_izraz_2){
// <odnosni_izraz> ::= <odnosni_izraz> OP_LT <aditivni_izraz>
}
else if (pe==ProductionEnum.odnosni_izraz_3){
// <odnosni_izraz> ::= <odnosni_izraz> OP_GT <aditivni_izraz>
}
else if (pe==ProductionEnum.odnosni_izraz_4){
// <odnosni_izraz> ::= <odnosni_izraz> OP_LTE <aditivni_izraz>
}
else if (pe==ProductionEnum.odnosni_izraz_5){
// <odnosni_izraz> ::= <odnosni_izraz> OP_GTE <aditivni_izraz>
}
else if (pe==ProductionEnum.jednakosni_izraz_1){
// <jednakosni_izraz> ::= <odnosni_izraz>
}
else if (pe==ProductionEnum.jednakosni_izraz_2){
// <jednakosni_izraz> ::= <jednakosni_izraz> OP_EQ <odnosni_izraz>
}
else if (pe==ProductionEnum.jednakosni_izraz_3){
// <jednakosni_izraz> ::= <jednakosni_izraz> OP_NEQ <odnosni_izraz>
}
else if (pe==ProductionEnum.bin_i_izraz_1){
// <bin_i_izraz> ::= <jednakosni_izraz>
}
else if (pe==ProductionEnum.bin_i_izraz_2){
// <bin_i_izraz> ::= <bin_i_izraz> OP_BIN_I <jednakosni_izraz>
}
else if (pe==ProductionEnum.bin_xili_izraz_1){
// <bin_xili_izraz> ::= <bin_i_izraz>
}
else if (pe==ProductionEnum.bin_xili_izraz_2){
// <bin_xili_izraz> ::= <bin_xili_izraz> OP_BIN_XILI <bin_i_izraz>
}
else if (pe==ProductionEnum.bin_ili_izraz_1){
// <bin_ili_izraz> ::= <bin_xili_izraz>
}
else if (pe==ProductionEnum.bin_ili_izraz_2){
// <bin_ili_izraz> ::= <bin_ili_izraz> OP_BIN_ILI <bin_xili_izraz>
}
else if (pe==ProductionEnum.log_i_izraz_1){
// <log_i_izraz> ::= <bin_ili_izraz>
}
else if (pe==ProductionEnum.log_i_izraz_2){
// <log_i_izraz> ::= <log_i_izraz> OP_I <bin_ili_izraz>
}
else if (pe==ProductionEnum.log_ili_izraz_1){
// <log_ili_izraz> ::= <log_i_izraz>
}
else if (pe==ProductionEnum.log_ili_izraz_2){
// <log_ili_izraz> ::= <log_ili_izraz> OP_ILI <log_i_izraz>
}
else if (pe==ProductionEnum.izraz_pridruzivanja_1){
// <izraz_pridruzivanja> ::= <log_ili_izraz>
}
else if (pe==ProductionEnum.izraz_pridruzivanja_2){
// <izraz_pridruzivanja> ::= <postfiks_izraz> OP_PRIDRUZI <izraz_pridruzivanja>
}
else if (pe==ProductionEnum.izraz_1){
// <izraz> ::= <izraz_pridruzivanja>
}
else if (pe==ProductionEnum.izraz_2){
// <izraz> ::= <izraz> ZAREZ <izraz_pridruzivanja>
}
else if (pe==ProductionEnum.slozena_naredba_1){
// <slozena_naredba> ::= L_VIT_ZAGRADA <lista_naredbi> D_VIT_ZAGRADA
}
else if (pe==ProductionEnum.slozena_naredba_2){
// <slozena_naredba> ::= L_VIT_ZAGRADA <lista_deklaracija> <lista_naredbi> D_VIT_ZAGRADA
}
else if (pe==ProductionEnum.lista_naredbi_1){
// <lista_naredbi> ::= <naredba>
}
else if (pe==ProductionEnum.lista_naredbi_2){
// <lista_naredbi> ::= <lista_naredbi> <naredba>
}
else if (pe==ProductionEnum.naredba_1){
// <naredba> ::= <slozena_naredba>
}
else if (pe==ProductionEnum.naredba_2){
// <naredba> ::= <izraz_naredba>
}
else if (pe==ProductionEnum.naredba_3){
// <naredba> ::= <naredba_grananja>
}
else if (pe==ProductionEnum.naredba_4){
// <naredba> ::= <naredba_petlje>
}
else if (pe==ProductionEnum.naredba_5){
// <naredba> ::= <naredba_skoka>
}
else if (pe==ProductionEnum.izraz_naredba_1){
// <izraz_naredba> ::= TOCKAZAREZ
}
else if (pe==ProductionEnum.izraz_naredba_2){
// <izraz_naredba> ::= <izraz> TOCKAZAREZ
}
else if (pe==ProductionEnum.naredba_grananja_1){
// <naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba>
}
else if (pe==ProductionEnum.naredba_grananja_2){
// <naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba> KR_ELSE <naredba>
}
else if (pe==ProductionEnum.naredba_petlje_1){
// <naredba_petlje> ::= KR_WHILE L_ZAGRADA <izraz> D_ZAGRADA <naredba>
}
else if (pe==ProductionEnum.naredba_petlje_2){
// <naredba_petlje> ::= KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> D_ZAGRADA <naredba>
}
else if (pe==ProductionEnum.naredba_petlje_3){
// <naredba_petlje> ::= KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> <izraz> D_ZAGRADA <naredba>
}
else if (pe==ProductionEnum.naredba_skoka_1){
// <naredba_skoka> ::= KR_CONTINUE TOCKAZAREZ
}
else if (pe==ProductionEnum.naredba_skoka_2){
// <naredba_skoka> ::= KR_BREAK TOCKAZAREZ
}
else if (pe==ProductionEnum.naredba_skoka_3){
// <naredba_skoka> ::= KR_RETURN TOCKAZAREZ
}
else if (pe==ProductionEnum.naredba_skoka_4){
// <naredba_skoka> ::= KR_RETURN <izraz> TOCKAZAREZ
}
else if (pe==ProductionEnum.prijevodna_jedinica_1){
// <prijevodna_jedinica> ::= <vanjska_deklaracija>
}
else if (pe==ProductionEnum.prijevodna_jedinica_2){
// <prijevodna_jedinica> ::= <prijevodna_jedinica> <vanjska_deklaracija>
}
else if (pe==ProductionEnum.vanjska_deklaracija_1){
// <vanjska_deklaracija> ::= <definicija_funkcije>
}
else if (pe==ProductionEnum.vanjska_deklaracija_2){
// <vanjska_deklaracija> ::= <deklaracija>
}
else if (pe==ProductionEnum.definicija_funkcije_1){
// <definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA KR_VOID D_ZAGRADA <slozena_naredba>
}
else if (pe==ProductionEnum.definicija_funkcije_2){
// <definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA <lista_parametara> D_ZAGRADA <slozena_naredba>
}
else if (pe==ProductionEnum.lista_parametara_1){
// <lista_parametara> ::= <deklaracija_parametra>
}
else if (pe==ProductionEnum.lista_parametara_2){
// <lista_parametara> ::= <lista_parametara> ZAREZ <deklaracija_parametra>
}
else if (pe==ProductionEnum.deklaracija_parametra_1){
// <deklaracija_parametra> ::= <ime_tipa> IDN
}
else if (pe==ProductionEnum.deklaracija_parametra_2){
// <deklaracija_parametra> ::= <ime_tipa> IDN L_UGL_ZAGRADA D_UGL_ZAGRADA
}
else if (pe==ProductionEnum.lista_deklaracija_1){
// <lista_deklaracija> ::= <deklaracija>
}
else if (pe==ProductionEnum.lista_deklaracija_2){
// <lista_deklaracija> ::= <lista_deklaracija> <deklaracija>
}
else if (pe==ProductionEnum.deklaracija_1){
// <deklaracija> ::= <ime_tipa> <lista_init_deklaratora> TOCKAZAREZ
}
else if (pe==ProductionEnum.lista_init_deklaratora_1){
// <lista_init_deklaratora> ::= <init_deklarator>
}
else if (pe==ProductionEnum.lista_init_deklaratora_2){
// <lista_init_deklaratora> ::= <lista_init_deklaratora> ZAREZ <init_deklarator>
}
else if (pe==ProductionEnum.init_deklarator_1){
// <init_deklarator> ::= <izravni_deklarator>
}
else if (pe==ProductionEnum.init_deklarator_2){
// <init_deklarator> ::= <izravni_deklarator> OP_PRIDRUZI <inicijalizator>
}
else if (pe==ProductionEnum.izravni_deklarator_1){
// <izravni_deklarator> ::= IDN
}
else if (pe==ProductionEnum.izravni_deklarator_2){
// <izravni_deklarator> ::= IDN L_UGL_ZAGRADA BROJ D_UGL_ZAGRADA
}
else if (pe==ProductionEnum.izravni_deklarator_3){
// <izravni_deklarator> ::= IDN L_ZAGRADA KR_VOID D_ZAGRADA
}
else if (pe==ProductionEnum.izravni_deklarator_4){
// <izravni_deklarator> ::= IDN L_ZAGRADA <lista_parametara> D_ZAGRADA
}
else if (pe==ProductionEnum.inicijalizator_1){
// <inicijalizator> ::= <izraz_pridruzivanja>
}
else if (pe==ProductionEnum.inicijalizator_2){
// <inicijalizator> ::= L_VIT_ZAGRADA <lista_izraza_pridruzivanja> D_VIT_ZAGRADA
}
else if (pe==ProductionEnum.lista_izraza_pridruzivanja_1){
// <lista_izraza_pridruzivanja> ::= <izraz_pridruzivanja>
}
else if (pe==ProductionEnum.lista_izraza_pridruzivanja_2){
// <lista_izraza_pridruzivanja> ::= <lista_izraza_pridruzivanja> ZAREZ <izraz_pridruzivanja
}

    }

}
