package net.cactusthorn.localization.core;

import java.util.Locale;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.StandardELContext;
import jakarta.el.ValueExpression;

public enum Plural {

    //FYI : https://github.com/translate/l10n-guide/blob/master/docs/l10n/pluralforms.rst

    // @formatter:off
    EN(2, "${n != 1}", Boolean.class),
    FR(2, "${n > 1}", Boolean.class),
    RU(3, "${n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2}", Integer.class);
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

        String lang = locale.getLanguage().toUpperCase();

        for (Plural p : Plural.values()) {
            if (p.name().equals(lang)) {
                return p;
            }
        }
        throw new IllegalArgumentException("No enum const " + Plural.class + " for locale : " + locale);
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
