package com.vizzuality;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by pabloparejatobes on 4/12/17.
 */
public class RWImporter {

    private static GraphDatabaseService GRAPH_DB ;

    private static enum RelTypes implements RelationshipType
    {
        RELATED_TO
    }

    private static enum Labels implements Label
    {
        CONCEPT,
        LOCATION,
        FOREST,
        WATER,
        FOOD,
        CLIMATE,
        ENERGY,
        COMMERCE,
        URBAN,
        SOCIETY,
        DISASTER,
        BIODIVERSITY
    }

    public static void main(String[] args){

        String dbFolder = "/Users/pabloparejatobes/vizzuality/projects/RW/RW_DB";
        String termsFile = "/Users/pabloparejatobes/vizzuality/projects/RW/terms.tsv";

        try{

            GRAPH_DB = new GraphDatabaseFactory().newEmbeddedDatabase( new File(dbFolder) );

            System.out.println("Importing terms");

            String line;
            BufferedReader reader = new BufferedReader(new FileReader(new File(termsFile)));
            reader.readLine();

            int lineCounter = 0;

            System.out.println("Importing nodes...");

            Transaction tx = GRAPH_DB.beginTx();
            while((line = reader.readLine()) != null){

                String[] columns = line.split("\t");
                String id = columns[0].trim();
                System.out.println("id = " + id);
                String label = columns[1].trim();

                Node conceptNode = GRAPH_DB.createNode(Labels.CONCEPT);
                conceptNode.setProperty("id", id);
                conceptNode.setProperty("label", label);

                lineCounter++;
            }
            tx.success();
            tx.close();
            reader.close();

            System.out.println("Done!");

            lineCounter = 0;

            System.out.println("Importing relationships...");

            reader = new BufferedReader(new FileReader(new File(termsFile)));
            tx = GRAPH_DB.beginTx();
            while((line = reader.readLine()) != null){
                String[] columns = line.split("\t");
                String id = columns[0];
                if(columns.length > 2){
                    String parents = columns[2];

                    System.out.println("Looking for node: " + id);
                    Node node = GRAPH_DB.findNode(Labels.CONCEPT, "id", id);

                    if(node != null){
                        String[] parentsArray = parents.split(",");
                        for(String parent: parentsArray){
                            Node parentNode = GRAPH_DB.findNode(Labels.CONCEPT, "id", parent.trim());
                            if (parentNode != null){
                                node.createRelationshipTo(parentNode, RelTypes.RELATED_TO);
                            }
                        }
                    }
                }
            }
            tx.success();
            tx.close();
            reader.close();

            System.out.println("Applying labels...");

            tagDescendantsWithLabel("location", Labels.LOCATION);
            tagDescendantsWithLabel("forest", Labels.FOREST);
            tagDescendantsWithLabel("water", Labels.WATER);
            tagDescendantsWithLabel("food", Labels.FOOD);
            tagDescendantsWithLabel("climate", Labels.CLIMATE);
            tagDescendantsWithLabel("energy", Labels.ENERGY);
            tagDescendantsWithLabel("commerce", Labels.COMMERCE);
            tagDescendantsWithLabel("urban", Labels.URBAN);
            tagDescendantsWithLabel("society", Labels.SOCIETY);
            tagDescendantsWithLabel("disaster", Labels.DISASTER);
            tagDescendantsWithLabel("biodiversity", Labels.BIODIVERSITY);

            System.out.println("done!");

            GRAPH_DB.shutdown();

            System.out.println("Program finished!!");

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private static void tagDescendantsWithLabel(String nodeId, Label label){
        Transaction tx = GRAPH_DB.beginTx();
        Node node = GRAPH_DB.findNode(Labels.CONCEPT, "id", nodeId);
        if (node != null) {
            node.addLabel(label);
            Iterator<Relationship> iterator = node.getRelationships(Direction.INCOMING, RelTypes.RELATED_TO).iterator();
            while (iterator.hasNext()) {
                tagDescendantsWithLabel(iterator.next().getStartNode().getProperty("id").toString(), label);
            }
        }
        tx.success();
        tx.close();
    }


}
