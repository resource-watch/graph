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
        RELATED_TO,
        IS_A,
        HAS,
        TYPE_OF,
        LOCATED_AT,
        PART_OF,
        GENERATED_BY,
        CAN_AFFECT,
        TRIGGERED_BY,
        IS_INVOLVED_IN
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
                        String[] relsArray = parents.split(",");
                        for(String rel: relsArray){
                            String[] array = rel.split(":");
                            RelationshipType relType = RelTypes.RELATED_TO;
                            String parent;
                            if(array.length > 1){
                                parent = array[1];
                                switch (array[0]){
                                    case "part_of":
                                        relType = RelTypes.PART_OF;
                                        break;
                                    case "is_a":
                                        relType = RelTypes.IS_A;
                                        break;
                                    case "type_of":
                                        relType = RelTypes.TYPE_OF;
                                        break;
                                    case "has":
                                        relType = RelTypes.HAS;
                                        break;
                                    case "located_at":
                                        relType = RelTypes.LOCATED_AT;
                                        break;
                                    case "generated_by":
                                        relType = RelTypes.GENERATED_BY;
                                        break;
                                    case "can_affect":
                                        relType = RelTypes.CAN_AFFECT;
                                        break;
                                    case "triggered_by":
                                        relType = RelTypes.TRIGGERED_BY;
                                        break;
                                    case "is_involved_in":
                                        relType = RelTypes.IS_INVOLVED_IN;
                                        break;
                                }
                            }else{
                                parent = array[0];
                            }

                            Node parentNode = GRAPH_DB.findNode(Labels.CONCEPT, "id", parent.trim());
                            if (parentNode != null){
                                node.createRelationshipTo(parentNode, relType);
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
