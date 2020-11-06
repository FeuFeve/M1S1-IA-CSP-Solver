package csp_etud;

import java.util.ArrayList;
import java.util.List;

/**
 * Solveur : permet de résoudre un problème de contrainte par Backtrack :
 * Calcul d'une solution,
 * Calcul de toutes les solutions
 */
public class CSP {

    private Network network;        	// le réseau à résoudre
    private List<Assignment> solutions;	// les solutions du réseau (résultat de searchAllSolutions)
    private Assignment assignment; 		// l'assignation courante (résultat de searchSolution)
    int cptr;                    		// le compteur de noeuds explorés
    private long startTime;
    private double timeOut = 0;
    private boolean silenceWarnings = false;


    /**
     * Crée un problème de résolution de contraintes pour un réseau donné
     *
     * @param r le réseau de contraintes à résoudre
     */
    public CSP(Network r) {
        network = r;
        solutions = new ArrayList<>();
        assignment = new Assignment();

    }


    /********************** BACKTRACK UNE SOLUTION *******************************************/

    /**
     * Cherche une solution au réseau de contraintes
     *
     * @return une assignation solution du réseau, ou null si pas de solution
     */

    public Assignment searchSolution() {
        cptr = 0; // Je ne suis pas sûr de si cptr est censé être à 0 ou à 1 au début

        assignment = new Assignment();
        assignment = backtrack();

//        System.out.println(cptr + " noeuds ont été explorés");
        return assignment;
    }

    /*
     * Attention, peut retourner une assignation non nulle mais n'étant pas une solution en cas de time out.
     * Dans ce cas il faut vérifier si assignment.timedOut == true, si c'est le cas c'est que la fonction
     * a pris plus de temps que la limite timeOut imposée.
     */
    public Assignment searchSolution(long startTime, double timeOut, boolean silenceWarnings) {
        this.startTime = startTime;
        this.timeOut = timeOut;
        this.silenceWarnings = silenceWarnings;

        return searchSolution();
    }

    /* La methode bactrack ci-dessous travaille directement sur l'attribut assignment.
     * On peut aussi choisir de ne pas utiliser cet attribut et de créer plutot un objet Assignment local à searchSolution :
     * dans ce cas il faut le passer en parametre de backtrack
     */

    /**
     * Exécute l'algorithme de backtrack à la recherche d'une solution en étendant l'assignation courante
     * Utilise l'attribut assignment
     *
     * @return la prochaine solution ou null si pas de nouvelle solution
     */

    private Assignment backtrack() {

        if (timeOut != 0) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - startTime) / 1_000_000_000d;

            if (deltaTime >= timeOut) {
                assignment.timedOut = true;
                if (!silenceWarnings) {
                    System.err.println("ERROR: backtrack() timed out");
                }
                return assignment;
            }
        }

		if (assignment.size() == network.getVarNumber()) {
			return assignment;
		}

		String x = chooseVar();
		for (Object v : network.getDom(x)) {
		    cptr++;
			assignment.put(x, v);
			if (consistant(x)) {
				if (backtrack() != null) {
					return assignment;
				}
            }
            assignment.remove(x);
		}

        return null;
    }


    /********************** BACKTRACK TOUTES SOLUTIONS *******************************************/

    /**
     * Calcule toutes les solutions au réseau de contraintes
     *
     * @return la liste des assignations solution
     */
    public List<Assignment> searchAllSolutions() {
        cptr = 0; // Je ne suis pas sûr de si cptr est censé être à 0 ou à 1 au début

        assignment.clear();
        solutions.clear();
        backtrackAll();

//        System.out.println(cptr + " noeuds ont été explorés");
        return solutions;
    }

    /**
     * Exécute l'algorithme de backtrack à la recherche de toutes les solutions
     * étendant l'assignation courante
     */
    private void backtrackAll() {

        if (assignment.size() == network.getVarNumber()) {
            solutions.add(assignment.clone());
        }
        else {
            String x = chooseVar();
            for (Object v : tri(network.getDom(x))) {
                cptr++;
                assignment.put(x, v);
                if (consistant(x)) {
                    backtrackAll();
                }
                assignment.remove(x);
            }
        }
    }


    /**
     * Retourne la prochaine variable à assigner étant donné assignment (qui doit contenir la solution partielle courante)
     *
     * @return une variable non encore assignée
     */
    private String chooseVar() {

        for (String var : network.getVars()) {
            if (!assignment.containsKey(var)) {
                return var;
            }
        }

        return null;
    }


    /**
     * Fixe un ordre de prise en compte des valeurs d'un domaine
     *
     * @param values une liste de valeurs
     * @return une liste de valeurs
     */
    private List<Object> tri(List<Object> values) {
        return values; // donc en l'état n'est pas d'une grande utilité !
    }


    /**
     * Teste si l'assignation courante stokée dans l'attribut assignment est consistante, c'est à dire qu'elle
     * ne viole aucune contrainte.
     *
     * @param lastAssignedVar la variable que l'on vient d'assigner à cette étape
     * @return vrai ssi l'assignment courante ne viole aucune contrainte
     */
    private boolean consistant(String lastAssignedVar) {

        for (Constraint constraint : network.getConstraints(lastAssignedVar)) {
            if (constraint.violationOpt(assignment)) {
                return false;
            }
        }
        return true;
    }

}
