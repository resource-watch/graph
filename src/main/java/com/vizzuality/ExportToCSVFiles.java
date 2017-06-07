package com.vizzuality;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pabloparejatobes on 6/7/17.
 */
public class ExportToCSVFiles {

    public static void main(String[] args) throws Exception{
        if(args.length != 1){
            System.out.println("This program expect one parameter: TSV file name");
        }else{

            BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
            String line;
            String[] columns = reader.readLine().split("\t");

            ArrayList<String> relTypes = new ArrayList<>();
            HashMap<String, BufferedWriter> writersMap = new HashMap<>();

            for(int i=2;i<columns.length;i++){
                String relName = columns[i];
                relTypes.add(relName);
                writersMap.put(relName, new BufferedWriter(new FileWriter(new File("csv_files/" + relName + ".csv"))));
            }

            BufferedWriter nodesFileWriter = new BufferedWriter(new FileWriter(new File("csv_files/nodes.csv")));

            while((line = reader.readLine()) != null){
                String[] values = line.split("\t");
                String valueID = values[0];
                nodesFileWriter.write( valueID.trim() + "," + values[1].trim() + "\n");
                for(int i=2;i<values.length;i++){
                    String relValue = values[i].trim();
                    if(relValue.length() > 0){
                        String[] relColumns = relValue.split(",");
                        for(String relColumn : relColumns){
                            writersMap.get(relTypes.get(i-2)).write(valueID + "," + relColumn.trim() + "\n");
                        }
                    }
                }
            }
            reader.close();

            System.out.println("Closing files...");
            nodesFileWriter.close();
            for(String key: writersMap.keySet()){
                writersMap.get(key).close();
            }
            System.out.println("Done!");

        }
    }
}
