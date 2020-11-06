package csp_etud;

import java.io.BufferedReader;

public class ConstraintEq extends Constraint {

    public ConstraintEq(BufferedReader in) throws Exception {
        super(in);
    }

    @Override
    public boolean violation(Assignment a) {
        boolean result = false;
        for (int i = 0; i < varList.size(); i++) {
            String key1 = varList.get(i);
            if (a.containsKey(key1)) {
                for (int j = i + 1; j < varList.size(); j++) {
                    String key2 = varList.get(j);
                    if (a.containsKey(key2) && !a.get(key1).equals(a.get(key2))) {
                        result =  true;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean violationOpt(Assignment a) {

        // Vérifie que les variables sont bien égales deux à deux
        for (int i = 0; i < getVars().size(); i++) {
            String var1 = getVars().get(i);
            if (a.containsKey(var1)) {
                for (int j = i + 1; j < getVars().size(); j++) {
                    String var2 = getVars().get(j);
                    if (a.containsKey(var2) && !a.get(var1).equals(a.get(var2))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "\n\t Eq "+ super.toString();
    }
}
