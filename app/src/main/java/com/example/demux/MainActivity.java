package com.example.demux;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.lujun.androidtagview.ColorFactory;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

// todo: store already loaded questions in static list. Don't change lastFetchedQueId. use it when searching.

public class MainActivity extends AppCompatActivity implements FilterInterface
{
    private static ArrayList<String> questionIdList;

    private ArrayList<Question> questionsList;   // list questions to display
    private static int lastVisibleItem, currentVisibleItem;  // index of visible items in list to keep track of scrolling
    private ListAdapter listAdapter;    // Adapter for recycler view
    private static String lastFetchedQuestionId;  // Id of last fetched question from firebase
    private static String searchQuery;
    private ProgressBar progressBar;
    private ArrayList<String> filteredTagsList;   // list of tags selected in the filter
    private TextView noResults;    // TextView to show when no results found
    private static boolean isEndOfDatabase;   // Flag to stop when end of database is reached
    private LinearLayoutManager layoutManager;  // for recycler view
    private  SearchView searchView;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("coding"); //todo

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionIdList = new ArrayList<>();
        lastFetchedQuestionId = null;

        // initialize all the fields
        progressBar = findViewById(R.id.progress_bar);
        noResults = findViewById(R.id.id_no_results);
        questionsList = new ArrayList<>();
        filteredTagsList = new ArrayList<>();
        searchQuery = "";
        lastVisibleItem = 0;
        isEndOfDatabase = false;
        FloatingActionButton filter = findViewById(R.id.id_filter);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        listAdapter = new ListAdapter(this, questionsList, this);
        recyclerView.setAdapter(listAdapter);

        new Constants();  // todo

        // listener for click on floating filter button.
        filter.setOnClickListener(v -> launchFilterSheet());

        // listener for scrolling - [ Load data on scroll ]
        recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(currentVisibleItem!=-1 && layoutManager.findLastCompletelyVisibleItemPosition()<=currentVisibleItem)
                return;
            currentVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
            Log.i("__Main__", ""+currentVisibleItem+", "+lastVisibleItem+", "+isEndOfDatabase);
            // if current item is last visible item then loa more data
            if(!isEndOfDatabase && currentVisibleItem+1==lastVisibleItem)
            {
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isEndOfDatabase)
                            getQuestions();
                            //loadNextSetOfQuestions();

                    }
                }, 500);
            }
        });

        // start loading questions from database.
        getQuestions();
    }

    private void resetParameters() {
        lastVisibleItem = 0;
        currentVisibleItem=-1;
        isEndOfDatabase = false;
        questionIdList.clear();
        questionsList.clear();
        listAdapter.notifyDataSetChanged();
        noResults.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void getQuestions()
    {
        Log.i("***Main###", ""+currentVisibleItem+", "+lastVisibleItem+", "+isEndOfDatabase);
        Query query;
        if (lastFetchedQuestionId == null) {
            resetParameters();
            query = databaseReference
                    .orderByKey()
                    .limitToFirst(10); //todo
        }
        else
            query = databaseReference
                    .orderByKey()
                    .startAt(lastFetchedQuestionId)
                    .limitToFirst(10);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    long count = dataSnapshot.getChildrenCount();
                    Log.i("Main","count :"+count);
                    if(count<10)
                        isEndOfDatabase = true;
                    if(count==0 && questionsList.isEmpty())
                        noResults.setVisibility(View.VISIBLE);

                    int currentHitQueryCount = 0;
                    for(DataSnapshot question : dataSnapshot.getChildren())
                    {
                        lastFetchedQuestionId = question.getKey();
                        String title = question.child("title").getValue(String.class);
                        String tags = question.child("tags").getValue(String.class);
                        String topics = question.child("topics").getValue(String.class);
                        int difficultyLevel = question.child("difficulty").getValue(Integer.class);
                        int frequency = question.child("frequency").getValue(Integer.class);
                        int questionId = question.child("questionID").getValue(Integer.class);

                        if(!questionIdList.contains(lastFetchedQuestionId))
                        {
                            questionIdList.add(lastFetchedQuestionId);
                            if(!filteredTagsList.isEmpty())
                            {
                                String tagsLowerCase = tags.toLowerCase();
                                String topicsLowerCase = topics.toLowerCase();
                                for(String tag : filteredTagsList)
                                {
                                    if(tagsLowerCase.contains(tag) || topicsLowerCase.contains(tag))
                                    {
                                        lastVisibleItem++;
                                        currentHitQueryCount++;
                                        questionsList.add(new Question(title, "", topics, tags, difficultyLevel, frequency, questionId));
                                        listAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                            else if (tags != null && title != null && (title.toLowerCase().contains(searchQuery.toLowerCase()) || tags.toLowerCase().contains(searchQuery.toLowerCase()) || topics.toLowerCase().contains(searchQuery.toLowerCase()) ))
                            {
                                lastVisibleItem++;
                                currentHitQueryCount++;
                                questionsList.add(new Question(title, "", topics, tags, difficultyLevel, frequency, questionId));
                                listAdapter.notifyDataSetChanged();
                            }
                        }
                        count--;
                        if(count==0)
                        {
                            progressBar.setVisibility(View.GONE);
                            // recycler view state will not change.
                            if(currentHitQueryCount==0 && !isEndOfDatabase)
                            {
                                progressBar.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!isEndOfDatabase)
                                            getQuestions();
                                    }
                                }, 500);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     *  Initialize and launch bottom sheet fragment.
     */
    private void launchFilterSheet() {

        // remove old filters
        filteredTagsList.clear();

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                MainActivity.this, R.style.BottomSheetDialogTheme
        );

        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.layout_bottom_sheet,
                        findViewById(R.id.bottom_sheet_container)
                );

        // List of general tags
        TagContainerLayout tagContainerGeneral = bottomSheetView.findViewById(R.id.filter_tag_container_layout_general);
        tagContainerGeneral.setTheme(ColorFactory.RANDOM);
        tagContainerGeneral.setBackgroundColor(Color.TRANSPARENT);
        tagContainerGeneral.setTagTextSize(48);
        tagContainerGeneral.setIsTagViewClickable(true);
        tagContainerGeneral.setIsTagViewSelectable(true);
        tagContainerGeneral.setTags(Constants.getGeneralTagsList(), Constants.getGeneralTagColorsList());

        // List of company tags
        TagContainerLayout tagContainerCompanies = bottomSheetView.findViewById(R.id.filter_tag_container_layout_companies);
        tagContainerCompanies.setTheme(ColorFactory.RANDOM);
        tagContainerCompanies.setBackgroundColor(Color.TRANSPARENT);
        tagContainerCompanies.setTagTextSize(48);
        tagContainerCompanies.setIsTagViewClickable(true);
        tagContainerCompanies.setIsTagViewSelectable(true);
        tagContainerCompanies.setTags(Constants.getCompanyTagsList(), Constants.getCompanyTagColorsList());

        // listener for click on tags
        tagContainerGeneral.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                List<Integer> selectedPositions = tagContainerGeneral.getSelectedTagViewPositions();
                // if tag was already selected then deselect it.
                if (selectedPositions.contains(position)) {
                    tagContainerGeneral.deselectTagView(position);
                    filteredTagsList.remove(text.toLowerCase());
                }
                else {
                    tagContainerGeneral.selectTagView(position);
                    filteredTagsList.add(text.toLowerCase());
                }
            }
            @Override
            public void onTagLongClick(int position, String text) { }
            @Override
            public void onSelectedTagDrag(int position, String text) { }
            @Override
            public void onTagCrossClick(int position) { }
        });

        // listener for click on tags
        tagContainerCompanies.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                List<Integer> selectedPositions = tagContainerCompanies.getSelectedTagViewPositions();
                // if tag was already selected then deselect it.
                if (selectedPositions.contains(position)) {
                    tagContainerCompanies.deselectTagView(position);
                    filteredTagsList.remove(text.toLowerCase());
                }
                else {
                    tagContainerCompanies.selectTagView(position);
                    filteredTagsList.add(text.toLowerCase());
                }
            }
            @Override
            public void onTagLongClick(int position, String text) { }
            @Override
            public void onSelectedTagDrag(int position, String text) { }
            @Override
            public void onTagCrossClick(int position) {
            }
        });

        bottomSheetView.findViewById(R.id.id_apply_filter_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                bottomSheetDialog.dismiss();
                searchQuery = "";
                searchView.clearFocus();
                searchView.setQuery(String.valueOf(filteredTagsList), false);
                lastFetchedQuestionId = null;
                getQuestions();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    /**
     * Setting up material search view in the Menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();

        // lister for when user searches something
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchQuery = query;
                // remove old filters if there are any.
                filteredTagsList.clear();
                // load questions containing this search query.
                lastFetchedQuestionId = null;
                getQuestions();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText)
            { return false; }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * When user click on some tag in the question this method will be called.
     * It searches for the questions which contains this tag.
     *
     * @param tag text of the tag which was clicked by user
     */
    public void applyFilter(String tag)
    {
        searchView.clearFocus();
        searchView.setQuery(tag, false);
        searchQuery = "";
        filteredTagsList.clear();
        filteredTagsList.add(tag.toLowerCase());
        lastFetchedQuestionId = null;
        getQuestions();
    }

    /**
     * If user click back after searching or applying filter then activity should
     * get closed. Activity will get reloaded in this scenario.
     */
    @Override
    public void onBackPressed()
    {
        if(!searchQuery.isEmpty() || !filteredTagsList.isEmpty())
        {
            finish();
            startActivity(getIntent());
        }
        super.onBackPressed();
    }
}