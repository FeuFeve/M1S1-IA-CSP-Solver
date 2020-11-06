package csp_etud;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;

public class Application {

    public static void main(String[] args) throws Exception {
        String filename = "ReseauxSimples/Zebre.txt";

        System.out.println("Chargement du fichier : " + new java.io.File(".").getCanonicalPath() + "\\" + filename);
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        Network network = new Network(reader);

        reader.close();

        CSP csp = new CSP(network);

        ThreadMXBean thread = ManagementFactory.getThreadMXBean();

        long startTime = System.nanoTime();
        long startCpuTime = thread.getCurrentThreadCpuTime();
        long startUserTime = thread.getCurrentThreadUserTime();

        Assignment solution = csp.searchSolution();

        System.out.println("Solution trouvée avec backtrack() : " + solution);
        System.out.println("Nombre de noeuds explorés : " + csp.cptr);

//        List<Assignment> allSolutions = csp.searchAllSolutions();
//
//        System.out.println("Solution(s) trouvée(s) avec backtrackAll() : " + allSolutions.size());
//        for (int i = 0; i < allSolutions.size(); i++) {
//            System.out.println("S" + (i + 1) + " : " + allSolutions.get(i));
//        }
//        System.out.println("Nombre de noeuds explorés : " + csp.cptr);

        long userTime = thread.getCurrentThreadUserTime() - startUserTime;
        long cpuTime = thread.getCurrentThreadCpuTime() - startCpuTime;
        long sysTime = cpuTime - userTime;
        long realTime = System.nanoTime() - startTime;

        System.out.println("####################");
        System.out.println("Temps d'exécution :");
        System.out.println("Real time = " + (realTime / 1000000f) + "ms");
        System.out.println("System time = " + (sysTime / 1000000f) + "ms");
        System.out.println("CPU time = " + (cpuTime / 1000000f) + "ms");
        System.out.println("User time = " + (userTime / 1000000f) + "ms");
    }
}
