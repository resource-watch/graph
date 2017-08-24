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
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("KnowledgeGraphTree.json")));
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

            Iterator<Object> iterator = nodes.iterator();
            while(iterator.hasNext()) {
                JSONObject object = (JSONObject) iterator.next();
                nodesMap.put(object.getString("id"), object);
            }

            HashMap<String, ArrayList<JSONObject>> edgesMap = new HashMap<>(); //target, edge

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

            HashSet<String> conceptsAdded = new HashSet();

            JSONObject generalConcept = nodesMap.get("general");

            JSONObject resultJSON = generateJSONForChildren(generalConcept, edgesMap, nodesMap, conceptsAdded);

            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            String pretJson = prettyGson.toJson(resultJSON);

            writer.write(resultJSON.toString());

            writer.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject generateJSONForChildren(JSONObject currentNode, HashMap<String, ArrayList<JSONObject>> edgesMap, HashMap<String, JSONObject> nodesMap, HashSet<String> conceptsAdded){
        JSONObject newJSON = new JSONObject();
        newJSON.put("label", currentNode.getString("label"));
        newJSON.put("value", currentNode.getString("id"));
        newJSON.put("checked", false);

        ArrayList<JSONObject> children = edgesMap.get(currentNode.get("id"));

        if (children != null && children.size() > 0) {
            JSONArray childrenArray = new JSONArray();
            HashSet<String> alreadyVisited = new HashSet<>();
            for (JSONObject child : children) {
//                if (!conceptsAdded.contains(child.getString("source"))) {
                    if (!alreadyVisited.contains(currentNode.getString("id"))) {
                        childrenArray.put(generateJSONForChildren(nodesMap.get(child.getString("source")), edgesMap, nodesMap, conceptsAdded));
                    }
                    alreadyVisited.add(child.getString("source"));
//                }
            }
            newJSON.put("children", childrenArray);
        }

        conceptsAdded.add(currentNode.getString("id"));

        return newJSON;
    }
}
