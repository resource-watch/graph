package com.vizzuality;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Iterator;

public class ImportDatabase {

    public static void main (String[] args) {

        try {

            BufferedWriter datasetWriter = new BufferedWriter(new FileWriter(new File("import_db_csv_files/datasets.csv")));
            datasetWriter.write("ID\n");
            BufferedWriter datasetTagsWriter = new BufferedWriter(new FileWriter(new File("import_db_csv_files/datasetTags.csv")));
            datasetWriter.write("ID,TAG_ID\n");
            BufferedWriter widgetWriter = new BufferedWriter(new FileWriter(new File("import_db_csv_files/widgets.csv")));
            widgetWriter.write("ID\n");
            BufferedWriter layerWriter = new BufferedWriter(new FileWriter(new File("import_db_csv_files/layers.csv")));
            layerWriter.write("ID\n");
            BufferedWriter userWriter = new BufferedWriter(new FileWriter(new File("import_db_csv_files/users.csv")));
            userWriter.write("ID\n");
            BufferedWriter conceptWriter = new BufferedWriter(new FileWriter(new File("import_db_csv_files/concepts.csv")));
            conceptWriter.write("ID,LABEL,SYNONYMS,DEFAULT_PARENT,TYPE\n");
            BufferedWriter widgetEdgesWriter = new BufferedWriter(new FileWriter(new File("import_db_csv_files/widgetEdges.csv")));
            widgetEdgesWriter.write("WIDGET_ID,DATASET_ID\n");
            BufferedWriter layerEdgesWriter = new BufferedWriter(new FileWriter(new File("import_db_csv_files/layerEdges.csv")));
            layerEdgesWriter.write("LAYER_ID,DATASET_ID\n");
            BufferedWriter favoritesWriter = new BufferedWriter(new FileWriter(new File("import_db_csv_files/favoritesEdges.csv")));
            favoritesWriter.write("USER_ID,RESOURCE_TYPE,RESOURCE_ID\n");
            BufferedWriter conceptEdgesWriter = new BufferedWriter(new FileWriter(new File("import_db_csv_files/conceptEdges.csv")));
            conceptEdgesWriter.write("SOURCE_CONCEPT_ID,EDGE_TYPE,TARGET_CONCEPT_ID\n");

            BufferedReader reader = new BufferedReader(new FileReader(new File("/Users/pabloparejatobes/Downloads/out.txt")));
            String line;
            // Skip first lines
            for (int i=0;i<3;i++) {
                reader.readLine();
            }
            line = reader.readLine();
            reader.close();

            line = line.replaceAll(" ->", ":");
            System.out.println(line.substring(0, 100));

            JSONObject newJSON = new JSONObject(line.substring(2));
            JSONArray nodes = newJSON.getJSONArray("nodes");
            JSONArray edges = newJSON.getJSONArray("edges");

            System.out.println("Exporting nodes...");

            for (int i=0; i<nodes.length(); i++) {
                JSONObject node = (JSONObject) nodes.get(i);
                JSONArray labels = node.getJSONArray("labels");
                if (labels.length() == 0) {
                    System.out.println("Empty node! " + node);
                } else {
                    if (labels.toString().contains("DATASET")) {
                        datasetWriter.write(node.getString("id") + "\n");
                    }
                    if (labels.toString().contains("WIDGET")) {
                        widgetWriter.write(node.getString("id") + "\n");
                    }
                    if (labels.toString().contains("LAYER")) {
                        layerWriter.write(node.getString("id") + "\n");
                    }
                    if (labels.toString().contains("USER")) {
                        userWriter.write(node.getString("id") + "\n");
                    }
                    if (labels.toString().contains("CONCEPT")) {
                        String nodeType = "";
                        Iterator<Object> iterator = node.getJSONArray("labels").iterator();
                        while(iterator.hasNext()) {
                            String elem = iterator.next().toString();
                            if(!elem.equals("CONCEPT")) {
                                nodeType = elem;
                            }
                        }
                        String labelSt = node.getString("label");
                        String synonymsSt = node.get("synonyms").toString().replaceAll("\"", "'");
                        conceptWriter.write(node.getString("id") + "," + labelSt + "," +
                                synonymsSt + "," + node.getString("default_parent") + "," +
                                nodeType + "\n") ;
                    }
                }
            }

            System.out.println("Exporting nodes...");

            for (int i=0; i<edges.length(); i++) {
                JSONObject edge = (JSONObject) edges.get(i);
                System.out.println(edge);
                String sourceType = (String) edge.getJSONArray("sourceType").iterator().next();
                String targetType = (String) edge.getJSONArray("targetType").iterator().next();
                String source = edge.getString("source");
                String target = edge.getString("target");
                String relType = edge.getString("relType");
                if (sourceType.equals("WIDGET")) {
                    widgetEdgesWriter.write(source + "," + target + "\n" );
                }
                if (sourceType.equals("LAYER")) {
                    layerEdgesWriter.write(source + "," + target + "\n" );
                }
                if (sourceType.equals("USER")) {
                    favoritesWriter.write(source + "," + targetType + "," + target + "\n" );
                }
                if (sourceType.equals("CONCEPT")) {
                    conceptEdgesWriter.write(source + "," + relType + "," + target + "\n");
                }
                if (sourceType.equals("DATASET")) {
                    datasetTagsWriter.write(source + "," + target + "\n");
                }
            }

            System.out.println("Closing writers...");
            datasetWriter.close();
            widgetWriter.close();
            layerWriter.close();
            userWriter.close();
            conceptWriter.close();
            widgetEdgesWriter.close();
            layerEdgesWriter.close();
            favoritesWriter.close();
            conceptEdgesWriter.close();
            datasetTagsWriter.close();
            System.out.println("Done!");

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
