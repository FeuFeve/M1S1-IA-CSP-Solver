package csp_etud;

import java.io.BufferedReader;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByValue;

/* (non-Javadoc)
 *  Choix de codage :
 *		Variable = String
 *      Valeur = Object
 *		un domaine = ArrayList<Object>
 *
 *  un réseau de contrainte (X,D,C) est représenté par :
 * 		- X les variables et D les domaines sont représentés par un ensemble de
 *		couples (variable,domaine) utilisant une HashMap<String,ArrayList<Object>>
 *		- C les contraintes est une simple liste de contraintes : ArrayList<Constraint>
 *
 *  On pourra, pour optimiser la structure d'un réseau de contraintes, ajouter
 * une HashMap (variable, liste de contraintes) qui permet d'associer à
 * chaque variable la liste des contraintes qui portent sur elle.
 *
 */

/**
 * Structure de manipulation d'un réseau de contraintes (X,D,C)
 */
public class Network {

    private Map<String, List<Object>> varDom; // associe à chaque variable de X un domaine de D
    private List<Constraint> constraints; // l'ensemble de contraintes C

    /**
     * Construit un réseau de contraintes sans variable (ni contrainte)
     */
    public Network() {
        varDom = new HashMap<>();
        constraints = new ArrayList<>();
    }

    /**
     * Construit un réseau de contraintes à partir d'une représentation textuelle au
     * format :
     * <p> nombre de variables
     * <p> nom de la variable 1 ; val1 ; val2 ; ...
     * <p> nom de la variable 2 ; val1 ; val2 ; ...
     * <p> ...
     * <p> nombre de contraintes
     * <p> type de la contrainte 1   (pour l'instant uniquement 'ext')
     * <p> nom de la 1ière variable de la contrainte 1 ; nom de la 2nde var de la contrainte 1 ; ...
     * <p> nombre de tuples de valeurs pour cette contrainte
     * <p> val1 ; val2 ; ...
     * <p> val1 ; val2 ; ...
     * <p> type de la contrainte 2   (pour l'instant uniquement 'ext')
     * <p> nom de la 1ière variable de la contrainte 2 ; nom de la 2nde var de la contrainte 2 ; ...
     * <p> nombre de tuples de valeurs pour cette contrainte
     * <p> val1 ; val2 ; ...
     * <p> val1 ; val2 ; ...
     * <p> ...
     *
     * @param in le buffer de lecture de la représentation textuelle de la contrainte
     * @throws Exception en cas de problème dans l'analyse textuelle
     */
    public Network(BufferedReader in) throws Exception {
        varDom = new HashMap<>();
        constraints = new ArrayList<>();
        // Les variables et domaines
        int nbVariables = Integer.parseInt(in.readLine());        // le nombre de variables
        for (int i = 1; i <= nbVariables; i++) {
            String[] varDeclaration = in.readLine().split(";"); // Var;Val1;Val2;Val3;...
            List<Object> dom = new ArrayList<>();
            varDom.put(varDeclaration[0], dom);
            for (int j = 1; j < varDeclaration.length; j++) dom.add(varDeclaration[j]);
        }
        // Les contraintes
        int nbConstraints = Integer.parseInt(in.readLine());    // le nombre de contraintes
        for (int k = 0; k < nbConstraints; k++) {
            Constraint c = null;
            String type = in.readLine().trim();                    // le type de la contrainte
            if (type.equals("ext")) c = new ConstraintExt(in);
            else if (type.equals("dif")) c = new ConstraintDif(in);
            else if (type.equals("eq")) c = new ConstraintEq(in);
            else if (type.equals("exp")) c = new ConstraintExp(in);
            else {
                System.out.println(type);
                System.err.println("Type contrainte inconnu");
            }

            addConstraint(c);
        }
        maxDegree();

        // Arc consistance

//        System.out.println("Domaine avant l'arc-consistance :");
//        System.out.println(varDom);

        AC3();

//        System.out.println("Domaine après l'arc-consistance :");
//        System.out.println(varDom);
    }
    /**
     * Applique l'algorithme d'heuristique de Max-deg
     * Remplace varDom par un nouveau domaine trié
     * @return void
     */
    public void maxDegree() {
        LinkedHashMap<String,List<Object>> newVarDom = new LinkedHashMap<>(); // HashMap triée
        HashMap<String, Integer> compteur = new HashMap<String, Integer>(); // HashMap qui permet de compter le nombre de contraintes par variable
        for (String key : varDom.keySet()) {
            compteur.put(key, 0); // initialsaiton des clés avec compteur à 0
        }

        for (Constraint contraint : constraints) {
            for (String key : varDom.keySet()) {
                if (contraint.varList.contains(key)) {
                    compteur.put(key, compteur.get(key) + 1); // pour chaque contrainte qui contient le domaine, incrémenter le compteur
                }
            }

        }
//        System.out.println(compteur);
        compteur = compteur.entrySet().stream().sorted(comparingByValue(reverseOrder())). // trie dans l'ordre décroissant les domaines en fonction de leur nombre de contrainte
                collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        for (String keyC : compteur.keySet()) { // réaffecte les domaines dans la linkedHashMap newVarDom
            newVarDom.put(keyC,null);
            for (String keyD : varDom.keySet()) {
                if (keyC.equals(keyD)) {
                    newVarDom.put(keyC, varDom.get(keyC));
                }
            }
        }
        varDom = newVarDom; //affecte newVarDom à la variable de classe varDom
    }

    /**
     * Ajoute une nouvelle variable dans le réseau avec un domaine vide
     * (ne fait rien si la variable existe déjà : message d'avertissement)
     *
     * @param var la variable à ajouter
     */
    public void addVariable(String var) {
        if (varDom.get(var) == null) varDom.put(var, new ArrayList<>());
        else System.err.println("Variable " + var + " deja existante");
    }

    /**
     * Ajoute une nouvelle valeur au domaine d'une variable du réseau
     *
     * @param var la variable à laquelle il faut ajouter la valeur
     * @param val la valeur à ajouter
     */
    public void addValue(String var, Object val) {
        if (varDom.get(var) == null) System.err.println("Variable " + var + " non existante");
        else {
            List<Object> dom = varDom.get(var);
            if (!dom.add(val)) // add retourne vrai si la valeur était effectivement nouvelle
                System.err.println("La valeur " + val + " est déjà dans le domaine de la variable " + var);
        }
    }

    /**
     * Ajoute une contrainte dans le réseau. Les variables de la
     * contrainte doivent déjà exister dans le réseau.
     *
     * @param c la contrainte à ajouter
     */
    public void addConstraint(Constraint c) {
        // Attention !! On ne vérifie pas que les valeurs des contraintes sont "compatibles" avec les domaines
        if (!varDom.keySet().containsAll(c.getVars())) // si l'une des variables de la contrainte n'existe pas das le réseau
            System.err.println("La contrainte " + c.getName() + " contient des variables (" + c.getVars()
                    + ") non déclarées dans le CSP dont les variables sont " + varDom.keySet());
        else constraints.add(c);
    }

    /**
     * Retourne le nombre de variables du réseau
     *
     * @return le nombre de variables
     */
    public int getVarNumber() {
        return varDom.size();
    }

    /**
     * Retourne la taille du domaine d'une variable du réseau.
     *
     * @param var la variable dont on veut connaître la taille de son domaine
     * @return le nombre de valeurs de Dom(var)
     */
    public int getDomSize(String var) {
        return varDom.get(var).size();
    }

    /**
     * Retourne le nombre de contraintes du réseau
     *
     * @return le nombre de contraintes
     */
    public int getConstraintNumber() {
        return constraints.size();
    }

    /**
     * Retourne la liste des variables du réseau
     *
     * @return la liste des variables
     */
    public List<String> getVars() {
        return new ArrayList<>(varDom.keySet());
    }

    /**
     * Retourne le domaine d'une variable
     *
     * @param var la variable dont on veut le domaine
     * @return la liste des valeurs du domaine d'une variable
     */
    public List<Object> getDom(String var) {
        return varDom.get(var);
    }


    /**
     * Retourne la liste des contraintes du réseau
     *
     * @return la liste des contraintes
     */
    public List<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * Retourne la liste des contraintes du réseau contenant une
     * certaine variable
     *
     * @param var la variable dont on veut connaitre les contraintes
     * @return la liste des contraintes contenant var
     */
    public List<Constraint> getConstraints(String var) {
        List<Constraint> selected = new ArrayList<>();
        for (Constraint c : constraints)
            if (c.getVars().contains(var)) selected.add(c);
        return selected;
    }


    /**
     * Choisit une variable non encore assignée
     * @param assignment    l'assignation actuelle
     * @param constraint    la contrainte actuelle
     * @return              une variable appartenant à la contrainte et n'étant pas encore dans l'assignation
     */
    String getVar(Assignment assignment, Constraint constraint) {
        for (String var : constraint.varList) {
            if (!assignment.containsKey(var)) {
                return var;
            }
        }
        return null;
    }

    /**
     * Remplit l'assignation et vérifie sa consistance par rapport à la contrainte actuelle
     * @param assignment    Assignation actuelle
     * @param constraint    Contrainte à vérifier pour l'assignation
     * @param depth         Profondeur/nombre de variables déjà assignées
     * @return              true si l'assignation est consistante (qu'elle ne viole pas la contrainte), false sinon
     */
    boolean recursiveIsConsistent(Assignment assignment, Constraint constraint, int depth) {
        List<String> vars = constraint.varList;

        String var = getVar(assignment, constraint);
        if (var == null) { // Pour les contraintes à une seule variable
            return !constraint.violationOpt(assignment);
        }
        else {
            // Pour chaque valeur possible dans le domaine de la variable
            for (Object value : getDom(var)) {
                assignment.put(var, value);

                // Si on est arrivé au bout de la récursivité (que chaque variable de la contrainte a une valeur assignée)
                if (vars.size() == depth + 1) {
                    if (!constraint.violationOpt(assignment)) { // Si l'assignation ne viole pas la contrainte il n'y a pas à retirer la valeur de cette variable
                        return true;
                    }
                }

                // Sinon, on relance la fonction récursivement pour assigner une valeur à une variable de la contrainte qui n'a pas encore de valeur
                else {
                    boolean isConsistent = recursiveIsConsistent(assignment, constraint, depth + 1);
                    if (isConsistent) {
                        return true;
                    }
                }
                assignment.remove(var, value);
            }
        }

        // Arrivé ici : toutes les assignations possibles ont été testées et violent la contrainte,
        // la valeur n'a donc pas de tuple support et il faut la retirer du domaine de la variable
        return false;
    }

    /**
     * Vérifie/modifie le domaine d'une variable pour le rendre arc-consistant
     * @param x     Variable dont il faut vérifier l'arc-consistance
     * @return      true si le domaine de x a été modifié, false sinon
     */
    boolean revise(String x) {

        boolean modif = false;
        Assignment assignment = new Assignment();
        List<Object> toRemove = new ArrayList<>();

        for (Object value : getDom(x)) { // On teste chaque valeur du domaine de la variable
            for (Constraint constraint : getConstraints(x)) { // Et sur chaque contrainte concernant la variable

                assignment.clear();
                assignment.put(x, value);

                // Si aucun tuple validant l'arc consistance n'est trouvé pour la contrainte actuelle,
                // il faut retirer la valeur ('value') du domaine de 'x', puisque la contrainte ne pourra
                // en aucun cas être satisfaite pour cette valeur
                if (!recursiveIsConsistent(assignment, constraint, 1)) {
                    toRemove.add(value);
                    modif = true;
                    break;
                }
            }
        }

        // On retire ici les valeurs n'ayant pas trouvé de tuple support pour la variable
        for (Object value : toRemove) {
            getDom(x).remove(value);
        }

        return modif;
    }

    /**
     * Rétablit l'arc-consistance d'un réseau
     * @return  false si le réseau n'est plus réalisable (si un des domaines est vide), true sinon
     */
    boolean AC3() {
        // Révision sur toutes les variables du réseau
        List<String> remainingVars = new ArrayList<>(getVars());

        while (!remainingVars.isEmpty()) { // Tant qu'il reste des variables à réviser
            String var = remainingVars.get(0);
            boolean modif = revise(var);

            // Vérifier que le domaine de la variable comporte au moins une valeur, sinon c'est que le réseau n'a pas de solution
            if (getDomSize(var) == 0) {
                return false;
            }

            // Si la révision a apporté des modifications au domaine de la variable actuelle, il se peut que les domaines
            // des variables ayant des contraintes communes avec la variable actuelle changent aussi, il faut donc ajouter
            // ces variables à la liste des variables à réviser ('remainingVars') et ce même si elles ont déjà été révisées
            if (modif) {
                for (Constraint constraint : getConstraints(var)) {
                    for (String constraintVar : constraint.varList) {
                        if (!remainingVars.contains(constraintVar)) {
                            remainingVars.add(constraintVar);
                        }
                    }
                }
            }

            remainingVars.remove(var);
        }

        return true;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Var et Dom : " + varDom + "\nConstraints :" + constraints;
    }

}

