package csp_etud;

import java.io.File;

public class ExperimentalNetworksGenerator {

    public static void main(String[] args) {

        int nbReseauxParDurete = 10;

        double densite = 0.20;
        int dureteMin = 10;
        int dureteMax = 90;
        int incrementDurete = 5;

        int nbVar = 20;
        int nbDom = 10;
        int nbContraintes = Math.round((float) (densite * (nbVar * nbVar - nbVar)) / 2);
        int nbTuples;

        String dirName = "ReseauxExp/Var" + nbVar + "_Dom" + nbDom + "_Densite" + densite;

        File directory = new File(dirName);
        if (!directory.exists()){
            directory.mkdirs();
        }

        for (int durete = dureteMin; durete <= dureteMax; durete += incrementDurete) {

            nbTuples = Math.round(-((durete / 100f) * (nbDom * nbDom) - (nbDom * nbDom)));

            for (int index = 1; index <= nbReseauxParDurete; index++) {
                System.out.println("urbcsp.exe " + nbVar + " " + nbDom + " " + nbContraintes + " " + nbTuples + " 1 > " + dirName + "/durete" + durete + "_reseau" + index + ".txt");
            }
        }

        System.out.println("\n" + dirName);
    }
}
