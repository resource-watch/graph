package com.vizzuality;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ConvertKnowledgeGraphIntoTreeFormat {

    public static void main(String[] args) {

        String fileSt = "KnowledgeGraph.json";
        File file = new File(fileSt);

        try {

            // Read JSON file exported with Cypher
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuffer stringBuffer =  new StringBuffer();
            String line;

            while((line = reader.readLine()) != null ) {
                stringBuffer.append(line);
            }
            reader.close();

            JSONObject root = new JSONObject(stringBuffer.toString());
            JSONArray edges = root.getJSONArray("edges");
            JSONArray nodes = root.getJSONArray("nodes");

            HashMap<String, JSONObject> nodesMap = new HashMap<>();

            // Create a map holding the attributes of the nodes stored by ID
            Iterator<Object> iterator = nodes.iterator();
            while(iterator.hasNext()) {
                JSONObject object = (JSONObject) iterator.next();
                nodesMap.put(object.getString("id"), object);
            }

            HashMap<String, ArrayList<JSONObject>> edgesMap = new HashMap<>(); //target, edge

            // Create a map holding the edges of the graph stored by the ID of the edge target
            iterator = edges.iterator();
            while(iterator.hasNext()) {
                JSONObject object = (JSONObject) iterator.next();
                ArrayList<JSONObject> array = edgesMap.get(object.getString("target"));
                if (array == null) {
                    array = new ArrayList<>();
                    edgesMap.put(object.getString("target"), array);
                }
                array.add(object);
                edgesMap.put(object.getString("target"), array);
            }

            // Set including the concepts that have already been added to the tree
            HashSet<String> conceptsAdded = new HashSet();

            JSONObject generalConcept = nodesMap.get("general");
            JSONObject datasetConcept = nodesMap.get("dataset");
            JSONObject geographiesConcept = nodesMap.get("continent");

            JSONObject resultJSON = generateJSONForChildren(generalConcept, edgesMap, nodesMap, conceptsAdded);
            conceptsAdded.clear();
            JSONObject dataTypesJSON = generateJSONForChildren(datasetConcept, edgesMap, nodesMap, conceptsAdded);
            conceptsAdded.clear();
            JSONObject geographiesJSON = generateJSONForChildren(geographiesConcept, edgesMap, nodesMap, conceptsAdded);

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("KnowledgeGraphTree.json")));
            writer.write(resultJSON.toString());
            writer.close();
            writer = new BufferedWriter(new FileWriter(new File("GeographiesTree.json")));
            writer.write(geographiesJSON.getJSONArray("children").toString());
            writer.close();
            writer = new BufferedWriter(new FileWriter(new File("DataTypesTree.json")));
            writer.write(dataTypesJSON.getJSONArray("children").toString());
            writer.close();


            // Remove location + dataset + their descendants in order to create the topics tree
            iterator = resultJSON.getJSONArray("children").iterator();
            JSONArray tempArray = new JSONArray();

            while(iterator.hasNext()) {
                JSONObject obj = (JSONObject) iterator.next();
                String objID = obj.getString("value");
                if (!objID.equals("location") && !objID.equals("dataset")) {
                    tempArray.put(obj);
                }
            }

            resultJSON.remove("children");
            resultJSON.put("children", tempArray);

            writer = new BufferedWriter(new FileWriter(new File("TopicsTree.json")));
            writer.write(resultJSON.getJSONArray("children").toString());
            writer.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject generateJSONForChildren(JSONObject currentNode, HashMap<String, ArrayList<JSONObject>> edgesMap, HashMap<String, JSONObject> nodesMap, HashSet<String> conceptsAdded){
        String currentId = currentNode.getString("id");

        JSONObject newJSON = new JSONObject();
        newJSON.put("label", currentNode.getString("label"));
        newJSON.put("value", currentNode.getString("id"));
        newJSON.put("checked", false);
        newJSON.put("tagClassName", "selected-item");

        ArrayList<JSONObject> children = edgesMap.get(currentId);

        if (children != null && children.size() > 0) {
            newJSON.put("className", "parent-category");
            JSONArray childrenArray = new JSONArray();
            HashSet<String> alreadyVisited = new HashSet<>();

            System.out.println("currentId: " + currentId);
            for (JSONObject child : children) {
                System.out.println("child: " + child.getString("source"));
                if (!conceptsAdded.contains(child.getString("source"))) {
                    if (!alreadyVisited.contains(child.getString("source"))) {
                        System.out.println("not visited!");
                        if (nodesMap.get(child.getString("source")).get("default_parent").equals(currentId)) {
                            // We are in the default parent of the child
                            childrenArray.put(generateJSONForChildren(nodesMap.get(child.getString("source")), edgesMap, nodesMap, conceptsAdded));
                        }
                    }
                    alreadyVisited.add(child.getString("source"));
                }
            }
            newJSON.put("children", childrenArray);
        }

        conceptsAdded.add(currentNode.getString("id"));

        return newJSON;
    }
}
