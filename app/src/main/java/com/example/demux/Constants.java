package com.example.demux;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants
{
    public static Map<String, Integer> colorMap = new HashMap<>();
    public static List<String> companyTagsList = new ArrayList<>();
    public static List<String> generalTagsList = new ArrayList<>();
    public static List<int[]> companyTagColorsList = new ArrayList<int[]>();
    public static List<int[]> generalTagColorsList = new ArrayList<int[]>();

    public Constants()
    {
        // General Tags
        generalTagsList.add("College");
        generalTagsList.add("Software Developer");
        generalTagsList.add("Full-time");
        generalTagsList.add("Internship");
        generalTagsList.add("Interview");
        generalTagsList.add("Front End Developer");

        colorMap.put("College", Color.parseColor("#355C7D"));
        colorMap.put("Software Developer", Color.parseColor("#7bc043"));
        colorMap.put("Full-time", Color.parseColor("#283655"));
        colorMap.put("Internship", Color.parseColor("#F8B195"));
        colorMap.put("Interview", Color.parseColor("#283655"));
        colorMap.put("Front End Developer", Color.parseColor("#A7226E"));


        // Companies Tags
        colorMap.put("Google", Color.parseColor("#fe4a49"));
        colorMap.put("Facebook", Color.parseColor("#2ab7ca"));
        colorMap.put("SAP Labs", Color.parseColor("#A7226E"));
        colorMap.put("Visa", Color.parseColor("#3d2352"));
        colorMap.put("Amazon", Color.parseColor("#8d5524"));
        colorMap.put("Demux Academy", Color.parseColor("#011f4b"));
        colorMap.put("Goldman", Color.parseColor("#03396c"));
        colorMap.put("DE Shaw", Color.parseColor("#64a1f4"));
        colorMap.put("Uber", Color.parseColor("#6497b1"));
        colorMap.put("Cisco", Color.parseColor("#3da4ab"));
        colorMap.put("Salesforce", Color.parseColor("#f6cd61"));
        colorMap.put("TCS", Color.parseColor("#fe8a71"));
        colorMap.put("Infosys", Color.parseColor("#009688"));
        colorMap.put("Oracle", Color.parseColor("#dfa290"));
        colorMap.put("Barco", Color.parseColor("#A8E6CE"));
        colorMap.put("Paytm", Color.parseColor("#355C7D"));
        colorMap.put("Microsoft", Color.parseColor("#F8B195"));

        for(Map.Entry<String, Integer> entry : colorMap.entrySet())
        {
            if(generalTagsList.contains(entry.getKey()))
            {
                generalTagColorsList.add(new int[]{entry.getValue(), Color.parseColor("#000000"), Color.parseColor("#ffffff"), Color.parseColor("#999999")});
            }
            else
            {
                companyTagsList.add(entry.getKey());
                companyTagColorsList.add(new int[]{entry.getValue(), Color.parseColor("#000000"), Color.parseColor("#ffffff"), Color.parseColor("#999999")});
            }
        }
    }

    public static List<int[]> getGeneralTagColorsList() {
        return generalTagColorsList;
    }

    public static List<String> getGeneralTagsList() {
        return generalTagsList;
    }

    public static List<String> getCompanyTagsList() {
        return companyTagsList;
    }

    public static List<int[]> getCompanyTagColorsList() {
        return companyTagColorsList;
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
