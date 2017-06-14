package com.vizzuality;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by pabloparejatobes on 6/14/17.
 */
public class CreateSocialLayerOfGraph {

    public static void main(String[] args) throws Exception {


        ArrayList<String> widgetsArray = new ArrayList<>();
        ArrayList<String> layersArray = new ArrayList<>();
        ArrayList<String> datasetsArray = new ArrayList<>();
        BufferedWriter widgetsWriter = new BufferedWriter(new FileWriter(new File("user_widgets.csv")));
        BufferedWriter layersWriter = new BufferedWriter(new FileWriter(new File("user_layers.csv")));
        BufferedWriter datasetsWriter = new BufferedWriter(new FileWriter(new File("user_datasets.csv")));

        BufferedReader reader = new BufferedReader(new FileReader(new File("widgets.csv")));
        String line;
        System.out.println("Reading widgets...");
        while ((line = reader.readLine()) != null) {
            widgetsArray.add(line.split(",")[0]);
        }
        reader.close();

        reader = new BufferedReader(new FileReader(new File("layers.csv")));
        System.out.println("Reading layers...");
        while ((line = reader.readLine()) != null) {
            layersArray.add(line.split(",")[0]);
        }
        reader.close();

        reader = new BufferedReader(new FileReader(new File("dataset_tags.csv")));
        System.out.println("Reading datasets...");
        while ((line = reader.readLine()) != null) {
            datasetsArray.add(line.split(",")[0]);
        }
        reader.close();

        System.out.println("Reading names...");
        reader = new BufferedReader(new FileReader(new File("users.csv")));
        while ((line = reader.readLine()) != null) {
            String userName = line.trim();
            double numberOfWidgets = Math.floor(Math.random() * 5);
            double numberOfLayers = Math.floor(Math.random() * 5);
            double numberOfDatasets = Math.floor(Math.random() * 5);

            for (int i=0; i<numberOfWidgets; i++){
                Double index = Math.floor(Math.random()*widgetsArray.size());
                widgetsWriter.write(userName + "," + widgetsArray.get(index.intValue()) + "\n");
            }

            for (int i=0; i<numberOfLayers; i++){
                Double index = Math.floor(Math.random()*layersArray.size());
                layersWriter.write(userName + "," + layersArray.get(index.intValue()) + "\n");
            }

            for (int i=0; i<numberOfDatasets; i++){
                Double index = Math.floor(Math.random()*datasetsArray.size());
                datasetsWriter.write(userName + "," + datasetsArray.get(index.intValue()) + "\n");
            }
        }
        reader.close();

        widgetsWriter.close();
        layersWriter.close();
        datasetsWriter.close();
        System.out.println("done!");
    }
}
