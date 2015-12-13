package hr.fer.zemris.ppj.semantic;

/**
 * @author fhrenic
 */
public enum Attribute {
    LEXPR, // boolan, je li izraz l-izraz
    LOOP, // boolean, nalazi li se izraz u petlji
    NAME, // string, ime izraza
    NAMES, // lista stringova, imena koja se nalaze u izrazu
    NTYPE, // type, nasljedni tip
    NUM_EL // int, broj elemenata

}
