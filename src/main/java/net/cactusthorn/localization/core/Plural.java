package net.cactusthorn.localization.core;

import java.util.Locale;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;
import jakarta.el.ValueExpression;

public enum Plural {

    // FYI : https://github.com/translate/l10n-guide/blob/master/docs/l10n/pluralforms.rst

    // @formatter:off
    ACH(2, "${n > 1}", Boolean.class),
    AFR(2, "${n != 1}", Boolean.class),
    AKA(2, "${n > 1}", Boolean.class),
    AMH(2, "${n > 1}", Boolean.class),
    ARG(2, "${n != 1}", Boolean.class),
    ANP(2, "${n != 1}", Boolean.class),
    ARA(6, "${n==0 ? 0 : n==1 ? 1 : n==2 ? 2 : n%100>=3 && n%100<=10 ? 3 : n%100>=11 ? 4 : 5}", Integer.class),
    ASM(2, "${n != 1}", Boolean.class),
    AST(2, "${n != 1}", Boolean.class),
    AYM(1, "${0}", Integer.class),
    AZE(2, "${n != 1}", Boolean.class),

    BEL(3, "${n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2}", Integer.class),
    BUL(2, "${n != 1}", Boolean.class),
    BEN(2, "${n != 1}", Boolean.class),
    BOD(1, "${0}", Integer.class),
    BRE(2, "${n > 1}", Boolean.class),
    BRX(2, "${n != 1}", Boolean.class),
    BOS(3, "${n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2}", Integer.class),

    CAT(2, "${n != 1}", Boolean.class),
    CGG(1, "${0}", Integer.class),
    CES(3, "${(n==1) ? 0 : (n>=2 && n<=4) ? 1 : 2}", Integer.class),
    CSB(3, "${(n==1) ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2}", Integer.class),
    CYM(4, "${(n==1) ? 0 : (n==2) ? 1 : (n != 8 && n != 11) ? 2 : 3}", Integer.class),

    DAN(2, "${n != 1}", Boolean.class),
    DEU(2, "${n != 1}", Boolean.class),
    DOI(2, "${n != 1}", Boolean.class),
    DZO(1, "${0}", Integer.class),

    ELL(2, "${n != 1}", Boolean.class),
    ENG(2, "${n != 1}", Boolean.class),
    EPO(2, "${n != 1}", Boolean.class),
    SPA(2, "${n != 1}", Boolean.class),
    EST(2, "${n != 1}", Boolean.class),
    EUS(2, "${n != 1}", Boolean.class),

    FAS(2, "${n > 1}", Boolean.class),
    FUL(2, "${n != 1}", Boolean.class),
    FIN(2, "${n != 1}", Boolean.class),
    FIL(2, "${n > 1}", Boolean.class),
    FAO(2, "${n != 1}", Boolean.class),
    FRA(2, "${n > 1}", Boolean.class),
    FUR(2, "${n != 1}", Boolean.class),
    FRY(2, "${n != 1}", Boolean.class),

    GLE(5, "${n==1 ? 0 : n==2 ? 1 : (n>2 && n<7) ? 2 :(n>6 && n<11) ? 3 : 4}", Integer.class),
    GLA(4, "${(n==1 || n==11) ? 0 : (n==2 || n==12) ? 1 : (n > 2 && n < 20) ? 2 : 3}", Integer.class),
    GLG(2, "${n != 1}", Boolean.class),
    GUJ(2, "${n != 1}", Boolean.class),
    GUN(2, "${n > 1}", Boolean.class),

    HAU(2, "${n != 1}", Boolean.class),
    HEB(2, "${n != 1}", Boolean.class),
    HIN(2, "${n != 1}", Boolean.class),
    HNE(2, "${n != 1}", Boolean.class),
    HRV(3, "${n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2}", Integer.class),
    HUN(2, "${n != 1}", Boolean.class),
    HYE(2, "${n != 1}", Boolean.class),

    INA(2, "${n != 1}", Boolean.class),
    IND(1, "${0}", Integer.class),
    ISL(2, "${n%10!=1 || n%100==11}", Boolean.class),
    ITA(2, "${n != 1}", Boolean.class),

    JPN(1, "${0}", Integer.class),
    JBO(1, "${0}", Integer.class),
    JAV(2, "${n != 0}", Boolean.class),

    KAT(1, "${0}", Integer.class),
    KAZ(2, "${n != 1}", Boolean.class),
    KAL(2, "${n != 1}", Boolean.class),
    KHM(1, "${0}", Integer.class),
    KAN(2, "${n != 1}", Boolean.class),
    KOR(1, "${0}", Integer.class),
    KUR(2, "${n != 1}", Boolean.class),
    COR(4, "${(n==1) ? 0 : (n==2) ? 1 : (n == 3) ? 2 : 3}", Integer.class),
    KIR(2, "${n != 1}", Boolean.class),

    LTZ(2, "${n != 1}", Boolean.class),
    LIN(2, "${n > 1}", Boolean.class),
    LAO(1, "${0}", Integer.class),
    LIT(3, "${n%10==1 && n%100!=11 ? 0 : n%10>=2 && (n%100<10 || n%100>=20) ? 1 : 2}", Integer.class),
    LAV(3, "${n%10==1 && n%100!=11 ? 0 : n != 0 ? 1 : 2}", Integer.class),

    MAI(2, "${n != 1}", Boolean.class),
    CNR(3, "${n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2}", Integer.class),
    MFE(2, "${n > 1}", Boolean.class),
    MLG(2, "${n > 1}", Boolean.class),
    MRI(2, "${n > 1}", Boolean.class),
    MKD(2, "${n==1 || n%10==1 ? 0 : 1}", Integer.class),
    MAL(2, "${n != 1}", Boolean.class),
    MON(2, "${n != 1}", Boolean.class),
    MNI(2, "${n != 1}", Boolean.class),
    MNK(3, "${n==0 ? 0 : n==1 ? 1 : 2}", Integer.class),
    MAR(2, "${n != 1}", Boolean.class),
    MSA(1, "${0}", Integer.class),
    MLT(4, "${n==1 ? 0 : n==0 || ( n%100>1 && n%100<11) ? 1 : (n%100>10 && n%100<20 ) ? 2 : 3", Integer.class),
    MYA(1, "${0}", Integer.class),

    NAH(2, "${n != 1}", Boolean.class),
    NAP(2, "${n != 1}", Boolean.class),
    NOB(2, "${n != 1}", Boolean.class),
    NEP(2, "${n != 1}", Boolean.class),
    NLD(2, "${n != 1}", Boolean.class),
    NNO(2, "${n != 1}", Boolean.class),
    NOR(2, "${n != 1}", Boolean.class),
    NSO(2, "${n != 1}", Boolean.class),

    OCI(2, "${n > 1}", Boolean.class),
    ORI(2, "${n != 1}", Boolean.class),

    PAN(2, "${n != 1}", Boolean.class),
    PAP(2, "${n != 1}", Boolean.class),
    POL(3, "${n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2}", Integer.class),
    PMS(2, "${n != 1}", Boolean.class),
    PUS(2, "${n != 1}", Boolean.class),
    POR(2, "${n != 1}", Boolean.class),

    ROH(2, "${n != 1}", Boolean.class),
    RON(3, "${n==1 ? 0 : (n==0 || (n%100 > 0 && n%100 < 20)) ? 1 : 2}", Integer.class),
    RUS(3, "${n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2}", Integer.class),
    KIN(2, "${n != 1}", Boolean.class),

    SAH(1, "${0}", Integer.class),
    SAT(2, "${n != 1}", Boolean.class),
    SCO(2, "${n != 1}", Boolean.class),
    SND(2, "${n != 1}", Boolean.class),
    SME(2, "${n != 1}", Boolean.class),
    SIN(2, "${n != 1}", Boolean.class),
    SLK(3, "${(n==1) ? 0 : (n>=2 && n<=4) ? 1 : 2}", Integer.class),
    SLV(4, "${n%100==1 ? 0 : n%100==2 ? 1 : n%100==3 || n%100==4 ? 2 : 3}", Integer.class),
    SOM(2, "${n != 1}", Boolean.class),
    SON(2, "${n != 1}", Boolean.class),
    SQI(2, "${n != 1}", Boolean.class),
    SRP(3, "${n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2}", Integer.class),
    SUN(1, "${0}", Integer.class),
    SWE(2, "${n != 1}", Boolean.class),
    SWA(2, "${n != 1}", Boolean.class),

    TAM(2, "${n != 1}", Boolean.class),
    TEL(2, "${n != 1}", Boolean.class),
    TGK(2, "${n > 1}", Boolean.class),
    THA(1, "${0}", Integer.class),
    TIR(2, "${n > 1}", Boolean.class),
    TUK(2, "${n != 1}", Boolean.class),
    TUR(2, "${n > 1}", Boolean.class),
    TAT(1, "${0}", Integer.class),

    UIG(1, "${0}", Integer.class),
    UKR(3, "${n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2}", Integer.class),
    URD(2, "${n != 1}", Boolean.class),
    UZB(2, "${n > 1}", Boolean.class),

    VIE(1, "${0}", Integer.class),

    WLN(2, "${n > 1}", Boolean.class),
    WOL(1, "${0}", Integer.class),

    YOR(2, "${n != 1}", Boolean.class),

    ZHO(1, "${0}", Integer.class);
    // @formatter:on

    private final int nplurals;
    private final String expression;
    private final Class<?> expressionType;

    Plural(int nplurals, String expression, Class<?> expressionType) {
        this.nplurals = nplurals;
        this.expression = expression;
        this.expressionType = expressionType;
    }

    public int nplurals() {
        return nplurals;
    }

    public String expression() {
        return expression;
    }

    public Class<?> expressionType() {
        return expressionType;
    }

    public static Plural of(Locale locale) {

        if (locale == null) {
            throw new IllegalArgumentException("locale may not be null");
        }

        String lang = locale.getISO3Language().toUpperCase();

        for (Plural p : Plural.values()) {
            if (p.name().equals(lang)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Couldn't find " + Plural.class.getSimpleName() + " enum const for locale: " + locale);
    }

    private static final ExpressionFactory EXPRESSION_FACTORY = ExpressionFactory.newInstance();

    int evalPlural(int count) {

        ELContext context = new StandardELContext(EXPRESSION_FACTORY);

        ValueExpression countVar = EXPRESSION_FACTORY.createValueExpression(count, Integer.class);
        context.getVariableMapper().setVariable("n", countVar);

        ValueExpression valueExpression = EXPRESSION_FACTORY.createValueExpression(context, expression, expressionType);

        Object result = valueExpression.getValue(context);
        if (result instanceof Boolean) {
            return (Boolean) result ? 1 : 0;
        }

        return (int) result;
    }
}
