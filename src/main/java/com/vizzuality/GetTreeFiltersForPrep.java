package com.vizzuality;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class GetTreeFiltersForPrep {

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
                String relType = object.getString("relType");
                if (relType.equals("PART_OF") || relType.equals("IS_A") || relType.equals("TYPE_OF") ||
                        relType.equals("QUALITY_OF")) {
                    if (array == null) {
                        array = new ArrayList<>();
                        edgesMap.put(object.getString("target"), array);
                    }
                    array.add(object);
                    edgesMap.put(object.getString("target"), array);
                }
            }

            // Set including the concepts that have already been added to the tree
            HashSet<String> conceptsAdded = new HashSet();

            String[] mainElements = {"administrative_boundaries", "climate", "climate_hazard", "coast", "economic",
                    "ecosystem", "energy", "extreme_event", "food_and_agriculture", "infrastructure", "land", "ocean", "society",
                    "precipitation", "settlements", "temperature", "vulnerability", "water"};

            JSONArray resultJSON = new JSONArray();

            for (int i=0; i<mainElements.length; i++) {
                String currentElem = mainElements[i];
                System.out.println("hey! " + currentElem);
                JSONObject concept = nodesMap.get(currentElem);
                JSONObject tempJSON = generateJSONForChildren(concept, edgesMap, nodesMap, conceptsAdded);
                conceptsAdded.clear();
                resultJSON.put(tempJSON);
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("PrepTreeFilters.json")));
            writer.write(resultJSON.toString());
            writer.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject generateJSONForChildren(JSONObject currentNode, HashMap<String, ArrayList<JSONObject>> edgesMap, HashMap<String, JSONObject> nodesMap, HashSet<String> conceptsAdded){
        String currentId = currentNode.getString("id");
        System.out.println("currentId " + currentId);
        JSONArray labels = currentNode.getJSONArray("labels");
        List<Object> labelsList = labels.toList();
        labelsList.remove("CONCEPT");

        JSONObject newJSON = new JSONObject();
        newJSON.put("label", currentNode.getString("label"));
        newJSON.put("value", currentNode.getString("id"));
        newJSON.put("checked", false);
        newJSON.put("tagClassName", "selected-item");
        newJSON.put("labels", labelsList);

        ArrayList<JSONObject> children = edgesMap.get(currentId);

        if (children != null && children.size() > 0) {
            newJSON.put("className", "parent-category");
            JSONArray childrenArray = new JSONArray();
            HashSet<String> alreadyVisited = new HashSet<>();

            for (JSONObject child : children) {
                System.out.println("child: " + child.getString("source"));
                if (!conceptsAdded.contains(child.getString("source"))) {
                    if (!alreadyVisited.contains(child.getString("source"))) {
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
