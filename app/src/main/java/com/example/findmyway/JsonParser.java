package com.example.findmyway;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String,String> parseJsonObject(JSONObject object){
        HashMap<String,String> datalist = new HashMap<>();
        try {
            String name = object.getString("name");
            String lat = object.getJSONObject("geometry").getJSONObject("location").getString("lat");
            String lng = object.getJSONObject("geometry").getJSONObject("location").getString("lng");


            datalist.put("name",name);
            datalist.put("lat",lat);
            datalist.put("lng",lng);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return datalist;
    }
    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray){
        List<HashMap<String,String>> dataList = new ArrayList<>();
        for(int i = 0; i<jsonArray.length();i++){
            try {
                HashMap<String,String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }
    public List<HashMap<String,String>> parseResult(JSONObject object){
        JSONArray jsonArray = null;
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJsonArray(jsonArray);
    }
}
