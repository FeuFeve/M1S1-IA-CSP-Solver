package csp_etud;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ExperimentationLauncher {

    private static final String dirName = "ReseauxExp/Var20_Dom10_Densite0.2";

    private static final boolean printAll = true;               /** Afficher ou non les résultats pour chaque réseau */
    private static final double timeOut = 10;                   /** Limite de temps d'exécution par réseau (en s) */

    private static final int minHardness = 10;                  /** Duretée minimum testée (en %) */
    private static final int maxHardness = 90;                  /** Duretée maximum testée (en %) */
    private static final int hardnessIncrement = 5;             /** Pas entre chaque niveau de dureté (en %) */
    private static final int networksPerHardnessIncrement = 10; /** Nombre de réseaux par niveau de dureté */
    private static final int testsPerNetwork = 5;               /** Nombre de tests par réseau */

    private static final DecimalFormat df = new DecimalFormat("0.000");


    private static void delMinMax(List<Double> list) {
        double min = list.get(0);
        double max = list.get(0);

        for (Double d : list) {
            if (d < min) min = d;
            if (d > max) max = d;
        }

    }

    private static double getAverage(List<Double> list) {
        double total = 0;

        for (Double d : list) {
            total += d;
        }

        return total / list.size();
    }


    public static void main(String[] args) throws Exception {

        int hardnessRange = (maxHardness - minHardness) / hardnessIncrement + 1;
        int networkCount = hardnessRange * networksPerHardnessIncrement;

        int totalSolutionCount = 0;
        int totalTimeOutCount = 0;

        double totalRealTime = 0;
        double totalSystemTime = 0;
        double totalCpuTime = 0;
        double totalUserTime = 0;

        for (int i = minHardness; i <= maxHardness; i += hardnessIncrement) {

            int solutionCount = 0;
            int timeOutCount = 0;

            double hardnessTotalRealTime = 0;
            double hardnessTotalSystemTime = 0;
            double hardnessTotalCpuTime = 0;
            double hardnessTotalUserTime = 0;

            for (int j = 0; j < networksPerHardnessIncrement; j++) {

                String filename = "durete" + i + "_reseau" + (j + 1) + ".txt";
                BufferedReader reader = new BufferedReader(new FileReader(dirName + "/" + filename));

                Network network = new Network(reader);

                reader.close();

                CSP csp = new CSP(network);

                List<Double> realTimes = new ArrayList<>();
                List<Double> systemTimes = new ArrayList<>();
                List<Double> cpuTimes = new ArrayList<>();
                List<Double> userTimes = new ArrayList<>();

                boolean timedOut = false;
                boolean hasSolution = false;

                for (int testIndex = 0; testIndex < testsPerNetwork; testIndex++) {
                    ThreadMXBean thread = ManagementFactory.getThreadMXBean();

                    long startTime = System.nanoTime();
                    long startCpuTime = thread.getCurrentThreadCpuTime();
                    long startUserTime = thread.getCurrentThreadUserTime();

                    Assignment solution = csp.searchSolution(startTime, timeOut, true);
                    if (solution != null) {
                        if (solution.timedOut) {
                            timedOut = true;
                            break; // Sort des tests sur le réseau actuel si on rencontre un timeout
                        }
                        else {
                            hasSolution = true;
                            if (testIndex == 0) {
                                solutionCount++;
                                totalSolutionCount++;
                            }
                        }
                    }

                    long userTime = thread.getCurrentThreadUserTime() - startUserTime;
                    long cpuTime = thread.getCurrentThreadCpuTime() - startCpuTime;
                    long sysTime = cpuTime - userTime;
                    long realTime = System.nanoTime() - startTime;

                    realTimes.add(realTime / 1000000d);
                    systemTimes.add(sysTime / 1000000d);
                    cpuTimes.add(cpuTime / 1000000d);
                    userTimes.add(userTime / 1000000d);
                }

                if (!timedOut) {
                    if (testsPerNetwork >= 5) {
                        delMinMax(realTimes);
                        delMinMax(systemTimes);
                        delMinMax(cpuTimes);
                        delMinMax(userTimes);
                    }

                    double realTimeAverage = getAverage(realTimes);
                    double systemTimeAverage = getAverage(systemTimes);
                    double cpuTimeAverage = getAverage(cpuTimes);
                    double userTimeAverage = getAverage(userTimes);

                    hardnessTotalRealTime += realTimeAverage;
                    hardnessTotalSystemTime += systemTimeAverage;
                    hardnessTotalCpuTime += cpuTimeAverage;
                    hardnessTotalUserTime += userTimeAverage;

                    totalRealTime += realTimeAverage;
                    totalSystemTime += systemTimeAverage;
                    totalCpuTime += cpuTimeAverage;
                    totalUserTime += userTimeAverage;

                    if (printAll) {
                        TableLine tableLine = new TableLine();
                        tableLine.addColumn(filename);
                        tableLine.addColumn("sol: " + hasSolution);
                        tableLine.addColumn("real: " + df.format(realTimeAverage) + "ms");
                        tableLine.addColumn("system: " + df.format(systemTimeAverage) + "ms");
                        tableLine.addColumn("cpu: " + df.format(cpuTimeAverage) + "ms");
                        tableLine.addColumn("user: " + df.format(userTimeAverage) + "ms");
                        System.out.println(tableLine);
                    }
                }
                else {
                    timeOutCount++;
                    totalTimeOutCount++;

                    if (printAll) {
                        TableLine tableLine = new TableLine();
                        tableLine.addColumn(filename);
                        tableLine.addColumn("sol: TIMED OUT");
                        tableLine.addColumn("real: > " + timeOut + "s");
                        tableLine.addColumn("system: ---");
                        tableLine.addColumn("cpu: ---");
                        tableLine.addColumn("user: ---");
                        System.out.println(tableLine);
                    }
                }
            }

            double hardnessAverageRealTime = hardnessTotalRealTime / (networksPerHardnessIncrement - timeOutCount);
            double hardnessAverageSystemTime = hardnessTotalSystemTime / (networksPerHardnessIncrement - timeOutCount);
            double hardnessAverageCpuTime = hardnessTotalCpuTime / (networksPerHardnessIncrement - timeOutCount);
            double hardnessAverageUserTime = hardnessTotalUserTime / (networksPerHardnessIncrement - timeOutCount);

            TableLine tableLine = new TableLine();
            tableLine.addColumn("Moyenne pour durete " + i + "%");
            tableLine.addColumn("sol: " + solutionCount + "/" + networksPerHardnessIncrement + " | TO: " + timeOutCount + "/" + networksPerHardnessIncrement);
            if (timeOutCount == networksPerHardnessIncrement) {
                tableLine.addColumn("real: ---");
                tableLine.addColumn("system: ---");
                tableLine.addColumn("cpu: ---");
                tableLine.addColumn("user: ---");
            }
            else {
                tableLine.addColumn("real: " + df.format(hardnessAverageRealTime) + "ms");
                tableLine.addColumn("system: " + df.format(hardnessAverageSystemTime) + "ms");
                tableLine.addColumn("cpu: " + df.format(hardnessAverageCpuTime) + "ms");
                tableLine.addColumn("user: " + df.format(hardnessAverageUserTime) + "ms");
            }
            System.out.println(tableLine);
            if (printAll) {
                TableLine.printSeparator();
            }
        }

        double averageRealTime = totalRealTime / (networkCount - totalTimeOutCount);
        double averageSystemTime = totalSystemTime / (networkCount - totalTimeOutCount);
        double averageCpuTime = totalCpuTime / (networkCount - totalTimeOutCount);
        double averageUserTime = totalUserTime / (networkCount - totalTimeOutCount);

        TableLine tableLine = new TableLine();
        tableLine.addColumn("Moyenne finale");
        tableLine.addColumn("sol: " + totalSolutionCount + "/" + networkCount + " | TO: " + totalTimeOutCount + "/" + networkCount);
        if (totalTimeOutCount == networkCount) {
            tableLine.addColumn("real: ---");
            tableLine.addColumn("system: ---");
            tableLine.addColumn("cpu: ---");
            tableLine.addColumn("user: ---");
        }
        else {
            tableLine.addColumn("real: " + df.format(averageRealTime) + "ms");
            tableLine.addColumn("system: " + df.format(averageSystemTime) + "ms");
            tableLine.addColumn("cpu: " + df.format(averageCpuTime) + "ms");
            tableLine.addColumn("user: " + df.format(averageUserTime) + "ms");
        }
        System.out.println(tableLine);
    }

}


class TableLine {

    static int[] colLength = {30, 30, 20, 22, 19, 20};

    int currentCol = 0;
    String content = "";


    void addColumn(String text) {
        String columnFormatter = "%1$-" + colLength[currentCol] + "s";
        content += String.format(columnFormatter, text);
        currentCol++;
    }

    static void printSeparator() {
        int lineLength = 0;
        for (int colLength : colLength) {
            lineLength += colLength;
        }
        System.out.println(new String(new char[lineLength]).replace("\0", "-"));
    }

    @Override
    public String toString() {
        return content;
    }
}
