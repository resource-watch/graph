package com.vizzuality;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

/**
 * Created by pabloparejatobes on 6/14/17.
 */
public class CreateLayersCSV {

    public static void main(String[] args) throws Exception{
        if(args.length != 1){
            System.out.println("This program expect one parameter: JSON file name");
        }else{

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("layers.csv")));

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
                JSONObject layer = (JSONObject) obj;
                String layerID = layer.getString("id");
                System.out.println("layerID = " + layerID);
                JSONObject attributes = layer.getJSONObject("attributes");
                String layerName = attributes.getString("name");
                String layerDataset = "";
                try {
                    layerDataset = attributes.getString("dataset");
                }catch(Exception e){

                }

                writer.write(layerID + "," + layerName + "," + layerDataset + "\n");
            }

            writer.close();
            System.out.println("done!");

        }
    }
}
