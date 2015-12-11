package hr.fer.zemris.ppj.semantic.parse;

/**
 * @author fhrenic
 */
public enum ProductionEnum{
// <primarni_izraz>
primarni_izraz_1, // <primarni_izraz> ::= IDN
primarni_izraz_2, // <primarni_izraz> ::= BROJ
primarni_izraz_3, // <primarni_izraz> ::= ZNAK
primarni_izraz_4, // <primarni_izraz> ::= NIZ_ZNAKOVA
primarni_izraz_5, // <primarni_izraz> ::= L_ZAGRADA <izraz> D_ZAGRADA

// <postfiks_izraz>
postfiks_izraz_1, // <postfiks_izraz> ::= <primarni_izraz>
postfiks_izraz_2, // <postfiks_izraz> ::= <postfiks_izraz> L_UGL_ZAGRADA <izraz> D_UGL_ZAGRADA
postfiks_izraz_3, // <postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA D_ZAGRADA
postfiks_izraz_4, // <postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA <lista_argumenata> D_ZAGRADA
postfiks_izraz_5, // <postfiks_izraz> ::= <postfiks_izraz> OP_INC
postfiks_izraz_6, // <postfiks_izraz> ::= <postfiks_izraz> OP_DEC

// <lista_argumenata>
lista_argumenata_1, // <lista_argumenata> ::= <izraz_pridruzivanja>
lista_argumenata_2, // <lista_argumenata> ::= <lista_argumenata> ZAREZ <izraz_pridruzivanja>

// <unarni_izraz>
unarni_izraz_1, // <unarni_izraz> ::= <postfiks_izraz>
unarni_izraz_2, // <unarni_izraz> ::= OP_INC <unarni_izraz>
unarni_izraz_3, // <unarni_izraz> ::= OP_DEC <unarni_izraz>
unarni_izraz_4, // <unarni_izraz> ::= <unarni_operator> <cast_izraz>

// <unarni_operator>
unarni_operator_1, // <unarni_operator> ::= PLUS
unarni_operator_2, // <unarni_operator> ::= MINUS
unarni_operator_3, // <unarni_operator> ::= OP_TILDA
unarni_operator_4, // <unarni_operator> ::= OP_NEG

// <cast_izraz>
cast_izraz_1, // <cast_izraz> ::= <unarni_izraz>
cast_izraz_2, // <cast_izraz> ::= L_ZAGRADA <ime_tipa> D_ZAGRADA <cast_izraz>

// <ime_tipa>
ime_tipa_1, // <ime_tipa> ::= <specifikator_tipa>
ime_tipa_2, // <ime_tipa> ::= KR_CONST <specifikator_tipa>

// <specifikator_tipa>
specifikator_tipa_1, // <specifikator_tipa> ::= KR_VOID
specifikator_tipa_2, // <specifikator_tipa> ::= KR_CHAR
specifikator_tipa_3, // <specifikator_tipa> ::= KR_INT

// <multiplikativni_izraz>
multiplikativni_izraz_1, // <multiplikativni_izraz> ::= <cast_izraz>
multiplikativni_izraz_2, // <multiplikativni_izraz> ::= <multiplikativni_izraz> OP_PUTA <cast_izraz>
multiplikativni_izraz_3, // <multiplikativni_izraz> ::= <multiplikativni_izraz> OP_DIJELI <cast_izraz>
multiplikativni_izraz_4, // <multiplikativni_izraz> ::= <multiplikativni_izraz> OP_MOD <cast_izraz>

// <aditivni_izraz>
aditivni_izraz_1, // <aditivni_izraz> ::= <multiplikativni_izraz>
aditivni_izraz_2, // <aditivni_izraz> ::= <aditivni_izraz> PLUS <multiplikativni_izraz>
aditivni_izraz_3, // <aditivni_izraz> ::= <aditivni_izraz> MINUS <multiplikativni_izraz>

// <odnosni_izraz>
odnosni_izraz_1, // <odnosni_izraz> ::= <aditivni_izraz>
odnosni_izraz_2, // <odnosni_izraz> ::= <odnosni_izraz> OP_LT <aditivni_izraz>
odnosni_izraz_3, // <odnosni_izraz> ::= <odnosni_izraz> OP_GT <aditivni_izraz>
odnosni_izraz_4, // <odnosni_izraz> ::= <odnosni_izraz> OP_LTE <aditivni_izraz>
odnosni_izraz_5, // <odnosni_izraz> ::= <odnosni_izraz> OP_GTE <aditivni_izraz>

// <jednakosni_izraz>
jednakosni_izraz_1, // <jednakosni_izraz> ::= <odnosni_izraz>
jednakosni_izraz_2, // <jednakosni_izraz> ::= <jednakosni_izraz> OP_EQ <odnosni_izraz>
jednakosni_izraz_3, // <jednakosni_izraz> ::= <jednakosni_izraz> OP_NEQ <odnosni_izraz>

// <bin_i_izraz>
bin_i_izraz_1, // <bin_i_izraz> ::= <jednakosni_izraz>
bin_i_izraz_2, // <bin_i_izraz> ::= <bin_i_izraz> OP_BIN_I <jednakosni_izraz>

// <bin_xili_izraz>
bin_xili_izraz_1, // <bin_xili_izraz> ::= <bin_i_izraz>
bin_xili_izraz_2, // <bin_xili_izraz> ::= <bin_xili_izraz> OP_BIN_XILI <bin_i_izraz>

// <bin_ili_izraz>
bin_ili_izraz_1, // <bin_ili_izraz> ::= <bin_xili_izraz>
bin_ili_izraz_2, // <bin_ili_izraz> ::= <bin_ili_izraz> OP_BIN_ILI <bin_xili_izraz>

// <log_i_izraz>
log_i_izraz_1, // <log_i_izraz> ::= <bin_ili_izraz>
log_i_izraz_2, // <log_i_izraz> ::= <log_i_izraz> OP_I <bin_ili_izraz>

// <log_ili_izraz>
log_ili_izraz_1, // <log_ili_izraz> ::= <log_i_izraz>
log_ili_izraz_2, // <log_ili_izraz> ::= <log_ili_izraz> OP_ILI <log_i_izraz>

// <izraz_pridruzivanja>
izraz_pridruzivanja_1, // <izraz_pridruzivanja> ::= <log_ili_izraz>
izraz_pridruzivanja_2, // <izraz_pridruzivanja> ::= <postfiks_izraz> OP_PRIDRUZI <izraz_pridruzivanja>

// <izraz>
izraz_1, // <izraz> ::= <izraz_pridruzivanja>
izraz_2, // <izraz> ::= <izraz> ZAREZ <izraz_pridruzivanja>

// <slozena_naredba>
slozena_naredba_1, // <slozena_naredba> ::= L_VIT_ZAGRADA <lista_naredbi> D_VIT_ZAGRADA
slozena_naredba_2, // <slozena_naredba> ::= L_VIT_ZAGRADA <lista_deklaracija> <lista_naredbi> D_VIT_ZAGRADA

// <lista_naredbi>
lista_naredbi_1, // <lista_naredbi> ::= <naredba>
lista_naredbi_2, // <lista_naredbi> ::= <lista_naredbi> <naredba>

// <naredba>
naredba_1, // <naredba> ::= <slozena_naredba>
naredba_2, // <naredba> ::= <izraz_naredba>
naredba_3, // <naredba> ::= <naredba_grananja>
naredba_4, // <naredba> ::= <naredba_petlje>
naredba_5, // <naredba> ::= <naredba_skoka>

// <izraz_naredba>
izraz_naredba_1, // <izraz_naredba> ::= TOCKAZAREZ
izraz_naredba_2, // <izraz_naredba> ::= <izraz> TOCKAZAREZ

// <naredba_grananja>
naredba_grananja_1, // <naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba>
naredba_grananja_2, // <naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba> KR_ELSE <naredba>

// <naredba_petlje>
naredba_petlje_1, // <naredba_petlje> ::= KR_WHILE L_ZAGRADA <izraz> D_ZAGRADA <naredba>
naredba_petlje_2, // <naredba_petlje> ::= KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> D_ZAGRADA <naredba>
naredba_petlje_3, // <naredba_petlje> ::= KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> <izraz> D_ZAGRADA <naredba>

// <naredba_skoka>
naredba_skoka_1, // <naredba_skoka> ::= KR_CONTINUE TOCKAZAREZ
naredba_skoka_2, // <naredba_skoka> ::= KR_BREAK TOCKAZAREZ
naredba_skoka_3, // <naredba_skoka> ::= KR_RETURN TOCKAZAREZ
naredba_skoka_4, // <naredba_skoka> ::= KR_RETURN <izraz> TOCKAZAREZ

// <prijevodna_jedinica>
prijevodna_jedinica_1, // <prijevodna_jedinica> ::= <vanjska_deklaracija>
prijevodna_jedinica_2, // <prijevodna_jedinica> ::= <prijevodna_jedinica> <vanjska_deklaracija>

// <vanjska_deklaracija>
vanjska_deklaracija_1, // <vanjska_deklaracija> ::= <definicija_funkcije>
vanjska_deklaracija_2, // <vanjska_deklaracija> ::= <deklaracija>

// <definicija_funkcije>
definicija_funkcije_1, // <definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA KR_VOID D_ZAGRADA <slozena_naredba>
definicija_funkcije_2, // <definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA <lista_parametara> D_ZAGRADA <slozena_naredba>

// <lista_parametara>
lista_parametara_1, // <lista_parametara> ::= <deklaracija_parametra>
lista_parametara_2, // <lista_parametara> ::= <lista_parametara> ZAREZ <deklaracija_parametra>

// <deklaracija_parametra>
deklaracija_parametra_1, // <deklaracija_parametra> ::= <ime_tipa> IDN
deklaracija_parametra_2, // <deklaracija_parametra> ::= <ime_tipa> IDN L_UGL_ZAGRADA D_UGL_ZAGRADA

// <lista_deklaracija>
lista_deklaracija_1, // <lista_deklaracija> ::= <deklaracija>
lista_deklaracija_2, // <lista_deklaracija> ::= <lista_deklaracija> <deklaracija>

// <deklaracija>
deklaracija_1, // <deklaracija> ::= <ime_tipa> <lista_init_deklaratora> TOCKAZAREZ

// <lista_init_deklaratora>
lista_init_deklaratora_1, // <lista_init_deklaratora> ::= <init_deklarator>
lista_init_deklaratora_2, // <lista_init_deklaratora> ::= <lista_init_deklaratora> ZAREZ <init_deklarator>

// <init_deklarator>
init_deklarator_1, // <init_deklarator> ::= <izravni_deklarator>
init_deklarator_2, // <init_deklarator> ::= <izravni_deklarator> OP_PRIDRUZI <inicijalizator>

// <izravni_deklarator>
izravni_deklarator_1, // <izravni_deklarator> ::= IDN
izravni_deklarator_2, // <izravni_deklarator> ::= IDN L_UGL_ZAGRADA BROJ D_UGL_ZAGRADA
izravni_deklarator_3, // <izravni_deklarator> ::= IDN L_ZAGRADA KR_VOID D_ZAGRADA
izravni_deklarator_4, // <izravni_deklarator> ::= IDN L_ZAGRADA <lista_parametara> D_ZAGRADA

// <inicijalizator>
inicijalizator_1, // <inicijalizator> ::= <izraz_pridruzivanja>
inicijalizator_2, // <inicijalizator> ::= L_VIT_ZAGRADA <lista_izraza_pridruzivanja> D_VIT_ZAGRADA

// <lista_izraza_pridruzivanja>
lista_izraza_pridruzivanja_1, // <lista_izraza_pridruzivanja> ::= <izraz_pridruzivanja>
lista_izraza_pridruzivanja_2, // <lista_izraza_pridruzivanja> ::= <lista_izraza_pridruzivanja> ZAREZ <izraz_pridruzivanja

}