package csp_etud;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;

public class ConstraintExp extends Constraint {

    private final String expression;
    private final ScriptEngineManager mgr = new ScriptEngineManager();
    private final ScriptEngine engine = mgr.getEngineByName("JavaScript");


    ConstraintExp(BufferedReader in) throws Exception {
        super(in);
        expression = in.readLine().trim();
    }

    @Override
    public boolean violation(Assignment a) {

        for (String var : getVars()) {
            if (!a.getVars().contains(var)) {
                return false;
            }
        }

        String assignedExpression = expression;
        for (String var : getVars()) {
//            assignedExpression = assignedExpression.replace(var, a.get(var).toString());
            // Revient à faire (je crois) :
             engine.put(var, a.get(var));
        }

        boolean result = false;
        try {
            result = (boolean) engine.eval(assignedExpression);
        } catch (ScriptException e) {
            System.err.println("Problème dans l'expression : " + assignedExpression);
        }

        return !result;
    }

    @Override
    public boolean violationOpt(Assignment a) {
        return violation(a);
    }

    @Override
    public String toString() {
        return "\n\t Exp "+ super.toString();
    }
}
