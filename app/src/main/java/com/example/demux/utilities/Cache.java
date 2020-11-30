package com.example.demux.utilities;

import com.example.demux.Objects.Question;
import java.util.ArrayList;

/**
 * This class contains cached data.
 */
public class Cache
{
    /**
     *  cachedQuestionsList - stores previously loaded questions from cloud.
     *  loadedQuestionsIdList - stores id of previously loaded questions.
     *  lastDataReadIndexFromCache - index of the last question read from cache.
     *  lastFetchedQuestionId - id of last fetched question from database
     */
    // Id of last fetched question from firebase
    public static String lastFetchedQuestionId = null;
    public static int lastDataReadIndexFromCache = 0;
    public static ArrayList<Question> cachedQuestionsList = new ArrayList<>();
    public static ArrayList<String> loadedQuestionsIdList = new ArrayList<>();
}
