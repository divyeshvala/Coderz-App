package com.example.demux;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants
{
    public static Map<String, Integer> colorMap = new HashMap<>();
    public static List<String> tagsList = new ArrayList<>();
    public static List<int[]> tagColorsList = new ArrayList<int[]>();

    public Constants()
    {
        // Map
        colorMap.put("Google", Color.parseColor("#ff0000"));
        colorMap.put("Facebook", Color.parseColor("#F93004"));
        colorMap.put("SAP Labs", Color.parseColor("#0823F6"));
        colorMap.put("Visa", Color.parseColor("#070F4E"));
        colorMap.put("Amazon", Color.parseColor("#4A4B4F"));
        colorMap.put("Demux Academy", Color.parseColor("#F9D5CD"));

        colorMap.put("Goldman", Color.parseColor("#F9D5CD"));
        colorMap.put("DE Shaw", Color.parseColor("#F9D5CD"));

        for(Map.Entry<String, Integer> entry : colorMap.entrySet())
        {
            tagsList.add(entry.getKey());
            tagColorsList.add(new int[]{entry.getValue(), Color.parseColor("#000000"), Color.parseColor("#ffffff"), Color.parseColor("#999999")});
        }
    }

    public static List<String> getTagsList() {
        return tagsList;
    }

    public static List<int[]> getTagColorsList() {
        return tagColorsList;
    }

    public static List<int[]> getTagColorsList(List<String> tagsList)
    {
        List<int[]> tagColorsList = new ArrayList<int[]>();
        for(String tag : tagsList)
        {
            if(colorMap.containsKey(tag))
                tagColorsList.add(new int[]{ colorMap.get(tag), Color.parseColor("#000000"), Color.parseColor("#ffffff"), Color.parseColor("#999999") });
            else
                tagColorsList.add(new int[]{ Color.parseColor("#000000"), Color.parseColor("#000000"), Color.parseColor("#ffffff"), Color.parseColor("#999999") });
        }
        return tagColorsList;
    }
}
