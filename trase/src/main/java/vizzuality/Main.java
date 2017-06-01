package vizzuality;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class Main {

    public static void main(String[] args) {

        String fileName = "/Users/pabloparejatobes/vizzuality/projects/TRASE/BRAZIL_SOY.csv";
        String dbFolder = "/Users/pabloparejatobes/vizzuality/projects/TRASE/TRASEDB";

        try{

            GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( dbFolder );

            BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
            reader.readLine();
            String line;

            Transaction tx = graphDb.beginTx();

            while((line = reader.readLine()) != null){
                System.out.println(line);

                tx.success();
            }

            tx.finish();

            reader.close();
            graphDb.shutdown();

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
