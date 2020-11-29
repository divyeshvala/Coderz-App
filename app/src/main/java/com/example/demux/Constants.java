package com.example.demux;

import android.graphics.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It contains all the constants frequently used by the application.
 */
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
        colorMap.put("Full-time", Color.parseColor("#bd33a4"));
        colorMap.put("Internship", Color.parseColor("#F8B195"));
        colorMap.put("Interview", Color.parseColor("#283655"));
        colorMap.put("Front End Developer", Color.parseColor("#A7226E"));
        colorMap.put("Offcampus", Color.parseColor("#12232E"));

        // Companies Tags
        colorMap.put("Google", Color.parseColor("#fe4a49"));
        colorMap.put("Facebook", Color.parseColor("#2ab7ca"));
        colorMap.put("SAP Labs", Color.parseColor("#A7226E"));
        colorMap.put("Visa", Color.parseColor("#3d2352"));
        colorMap.put("Amazon", Color.parseColor("#8d5524"));
        colorMap.put("Goldman Sachs", Color.parseColor("#03396c"));
        colorMap.put("DE Shaw", Color.parseColor("#64a1f4"));
        colorMap.put("Uber", Color.parseColor("#6497b1"));
        colorMap.put("Cisco", Color.parseColor("#3da4ab"));
        colorMap.put("Salesforce", Color.parseColor("#f6cd61"));
        colorMap.put("TCS", Color.parseColor("#fe8a71"));
        colorMap.put("Infosys", Color.parseColor("#009688"));
        colorMap.put("Oracle", Color.parseColor("#dfa290"));
        colorMap.put("Barco", Color.parseColor("#A8E6CE"));
        colorMap.put("Paytm", Color.parseColor("#355C7D"));
        colorMap.put("Microsoft", Color.parseColor("#12232E"));
        colorMap.put("Citrix", Color.parseColor("#3C1874"));
        colorMap.put("Barclays", Color.parseColor("#4D774E"));
        colorMap.put("Myntra", Color.parseColor("#004E7C"));
        colorMap.put("Citi", Color.parseColor("#007CC7"));
        colorMap.put("Flipkart", Color.parseColor("#5C5F58"));
        colorMap.put("Morgan Stanley", Color.parseColor("#266150"));
        colorMap.put("Sprinklr", Color.parseColor("#4DA8DA"));
        colorMap.put("Demux Academy", Color.parseColor("#011f4b"));

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
                tagColorsList.add(new int[]{ Color.parseColor("#ffffff"), Color.parseColor("#555555"), Color.parseColor("#000000"), Color.parseColor("#999999") });
        }
        return tagColorsList;
    }
}
