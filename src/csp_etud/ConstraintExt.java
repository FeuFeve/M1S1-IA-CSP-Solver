package csp_etud;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Pour manipuler des contraintes en extension
 */
public class ConstraintExt extends Constraint {

    private List<List<Object>> tuples;    // ensemble des tuples de la contrainte

    /**
     * Construit une contrainte d'extension vide à partir
     * d'une liste de variables
     *
     * @param var la liste de variables
     */
    public ConstraintExt(List<String> var) {
        super(var);
        tuples = new ArrayList<>();
    }

    /**
     * Construit une contrainte d'extension vide à partir
     * d'une liste de variables et d'un nom
     *
     * @param var  la liste de variables
     * @param name son nom
     */
    public ConstraintExt(List<String> var, String name) {
        super(var, name);
        tuples = new ArrayList<>();
    }

    /**
     * Construit une contrainte en extension à partir d'une représentation
     * textuelle de la contrainte. La liste de variables est donnée sous la forme : Var1;Var2;...
     * Puis le nombre de tuples est indiqué et enfin chaque tupe est donné sous la forme d'une
     * suite de valeurs "String" : Val1;Val2;...
     * Aucune vérification n'est prévue si la syntaxe n'est pas respectée !!
     *
     * @param in le buffer de lecture de la représentation textuelle de la contrainte
     * @throws Exception en cas d'erreur de format
     */
    public ConstraintExt(BufferedReader in) throws Exception {
        super(in);
        tuples = new ArrayList<>();
        int nbTuples = Integer.parseInt(in.readLine());        // nombre de tuples de valeurs
        for (int j = 1; j <= nbTuples; j++) {
            List<Object> tuple = new ArrayList<>();
            for (String v : in.readLine().split(";")) tuple.add(v);    // Val1;Val2;...;Val(arity)
            if (tuple.size() != varList.size())
                System.err.println("Le tuple " + tuple + " n'a pas l'arité " + varList.size() + " de la contrainte " + name);
            else if (!tuples.add(tuple))
                System.err.println("Le tuple " + tuple + " est déjà présent dans la contrainte " + name);
        }
    }

    /**
     * Ajoute un tuple de valeur à la contrainte
     *
     * @param valTuple le tuple à ajouter
     */
    public void addTuple(List<Object> valTuple) {
        if (valTuple.size() != varList.size())
            System.err.println("Le tuple " + valTuple + " n'a pas l'arité " + varList.size() + " de la contrainte " + name);
        else if (!tuples.add(valTuple))
            System.err.println("Le tuple " + valTuple + " est déjà présent dans la contrainte " + name);
    }


    /**
     * Teste si une assignation viole la contrainte.
     * La violation "classique" n'est avérée que si :
     * - toutes les variables de la contrainte ont une valeur associée dans l'assignation testée
     * - le tuple de valeur pour les variables de la contrainte dans l'assignation testée n'appartient
     * pas aux tuples autorisés par la contrainte
     *
     * @param a l'assignation à tester
     * @return vrai ssi l'assignation viole la contrainte
     */
    @Override
    public boolean violation(Assignment a) {
        for(List<Object> tuple : this.tuples){
            boolean validTuple = true;
            for(String variable : a.getVars()){
                if(this.getVars().contains(variable)){
                    if(!((a.get(variable)).equals(tuple.get(this.getVars().indexOf(variable))))){
                        validTuple = false;
                    }
                }
                if(!validTuple) break;
            }
            if(validTuple) return false;
        }
        return true;
    }

    @Override
    public boolean violationOpt(Assignment a) {
        if (a.getVars().containsAll(varList)) {

            List<Object> tuple = new ArrayList<>();
            for (int i = 0; i < getArity(); i++) {
                tuple.add(i, a.get(getVars().get(i)));
            }
            return !tuples.contains(tuple);
        }
        return false;
    }

//    // FROM CC2 - ConstraintProd
//    public boolean violation(Assignment a) {
//
//        List<String> assignationVars = a.getVars();
//        if (assignationVars.containsAll(varList)) {
//
//            int expectedResult = (int) a.get(assignationVars.get(0));
//            int total = 1;
//
//            for (int i = 1; i < assignationVars.size(); i++) {
//                int value = (int) a.get(assignationVars.get(i));
//                total *= value;
//            }
//
//            return total == expectedResult;
//        }
//        return false;
//    }

    /* (non-Javadoc)
     * @see Constraint#toString()
     */
    public String toString() {
        return "\n\t Ext " + super.toString() + " : " + tuples;
    }


}
