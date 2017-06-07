package com.vizzuality;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pabloparejatobes on 6/7/17.
 */
public class CreateDatasetTagCSV {

    public static void main(String[] args) throws Exception{
        if(args.length != 1){
            System.out.println("This program expect one parameter: JSON file name");
        }else{

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("dataset_tags.csv")));

            BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
            String line;
            String jsonString = "";
            while((line = reader.readLine()) != null){
                jsonString += line;
            }
            reader.close();

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (Object obj : jsonArray){
                JSONObject dataset = (JSONObject) obj;
                String datasetID = dataset.getString("id");
                System.out.println("datasetID = " + datasetID);
                JSONObject attributes = dataset.getJSONObject("attributes");
                JSONArray vocabulary = attributes.getJSONArray("vocabulary");

                for(Object o : vocabulary){
                    JSONObject voc = (JSONObject) o;
                    JSONArray tagsArray = voc.getJSONObject("attributes").getJSONArray("tags");
                    for (Object o1 : tagsArray) {
                        String tag = String.valueOf(o1);
                        writer.write(datasetID + "," + tag + "\n");
                    }
                }
            }

            writer.close();
            System.out.println("done!");

        }
    }
}
