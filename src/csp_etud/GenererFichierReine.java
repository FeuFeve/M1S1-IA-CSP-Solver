package csp_etud;

import java.io.FileWriter;
import java.io.Writer;

public class GenererFichierReine {


    String GenereFichierReine(int n){
        try {

            Writer writer = new FileWriter("Reine"+n+"Exp.txt");

            writer.write(n+"\n");
            for (int i = 1; i <= n; i++) {
                writer.write("R"+i+";");
                for (int j = 1; j <= n; j++) {
                    writer.write(j+"");
                    if(j!=n){
                        writer.write(";");
                    }
                }
                writer.write("\n");
            }
            writer.write((n*(n-1))/2+"\n");

            for (int i = 1; i <= n-1 ; i++) {
                for (int j = i+1; j <=n ; j++) {
                    writer.write("exp\n");
                    writer.write("R"+i+";R"+j+"\n");
                    writer.write("R"+i+"!=R"+j+" && Math.abs(R"+i+"-R"+j+")!=\""+(j-i)+"\"\n");
                }
            }
            writer.close() ;
        }
        catch (Exception e) {
            System.err.println(e);
        }

        return "Reine"+n+"Exp.txt";
    }

}
