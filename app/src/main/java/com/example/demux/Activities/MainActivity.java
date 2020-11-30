package com.example.demux.Activities;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.demux.utilities.FilterInterface;
import com.example.demux.utilities.ListAdapter;
import com.example.demux.Objects.Question;
import com.example.demux.R;
import com.example.demux.utilities.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import co.lujun.androidtagview.ColorFactory;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

import static com.example.demux.utilities.Cache.lastDataReadIndexFromCache;
import static com.example.demux.utilities.Cache.lastFetchedQuestionId;
import static com.example.demux.utilities.Cache.cachedQuestionsList;
import static com.example.demux.utilities.Cache.loadedQuestionsIdList;

public class MainActivity extends AppCompatActivity implements FilterInterface
{
    private ArrayList<Question> questionsList;   // list questions to display
    private int QUESTION_CHUNK_SIZE = 10;       // number of questions to be loaded at a time
    final private int DELAY_BEFORE_NEXT_CHUNK = 300;  // delay in milli sec before loading next chunk of question
    private int lastVisibleItem, currentVisibleItem;  // index of visible items in list to keep track of scrolling
    private ListAdapter listAdapter;    // Adapter for recycler view
    private String searchQuery;
    private ProgressBar progressBar;
    private ArrayList<String> filteredTagsList;   // list of tags selected in the filter
    private TextView noResults;    // TextView to show when no results found
    private boolean isEndOfDatabase;   // Flag to stop when end of database is reached
    private LinearLayoutManager layoutManager;  // for recycler view
    private  SearchView searchView;
    private ArrayList<String> questionIdList;
    private MenuItem menuItem;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("questions");

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //lastFetchedQuestionId = null;

        // initialize all the fields
        progressBar = findViewById(R.id.progress_bar);
        noResults = findViewById(R.id.id_no_results);
        questionsList = new ArrayList<>();
        questionIdList = new ArrayList<>();
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

        // initialize all the constants
        new Constants();

        // listener for click on floating filter button.
        filter.setOnClickListener(v -> launchFilterSheet());

        // listener for scrolling - [ Load data on scroll ]
        recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(currentVisibleItem!=-1 && layoutManager.findLastCompletelyVisibleItemPosition()<=currentVisibleItem)
                return;
            currentVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
            // if current item is last visible item then load more data
            if(!isEndOfDatabase && currentVisibleItem+1==lastVisibleItem)
            {
                loadOnScroll();
            }
        });

        // start loading questions from database.
        getQuestions();
    }

    /**
     * Reset all the parameters before new query search or filtering
     */
    private void resetParameters() {
        lastDataReadIndexFromCache = 0;
        lastVisibleItem = 0;
        currentVisibleItem=-1;
        isEndOfDatabase = false;
        questionIdList.clear();
        questionsList.clear();
        listAdapter.notifyDataSetChanged();
        noResults.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Load questions from database or from local cache if cache is not empty.
     */
    private void getQuestions()
    {
        // load from cache first. If cache is empty or exhausted then load from database.
        if(lastDataReadIndexFromCache < cachedQuestionsList.size())
        {
            int count = 0, currentHitQueryCount = 0;
            for( ; lastDataReadIndexFromCache < cachedQuestionsList.size() && count<QUESTION_CHUNK_SIZE; count++, lastDataReadIndexFromCache++)
            {
                Question question = cachedQuestionsList.get(lastDataReadIndexFromCache);
                if(!questionIdList.contains(question.getQuestionId()) && doesQuestionContainsTagAndQuery(question))
                {
                    currentHitQueryCount++;
                    lastVisibleItem++;
                    questionIdList.add(question.getQuestionId());
                    questionsList.add(question);
                    listAdapter.notifyDataSetChanged();
                }
            }
            progressBar.setVisibility(View.GONE);
            // recycler view state will not change. So call LoadOnScroll manually.
            if(currentHitQueryCount==0 && !isEndOfDatabase)
            {
                loadOnScroll();
            }
            return;
        }

        // If loading from cache is over then load from cloud database.
        Query query;
        // If loading first page
        if (lastFetchedQuestionId == null) {
            resetParameters();
            query = databaseReference
                    .orderByKey()
                    .limitToFirst(QUESTION_CHUNK_SIZE);
        }
        else
            query = databaseReference
                    .orderByKey()
                    .startAt(lastFetchedQuestionId)
                    .limitToFirst(QUESTION_CHUNK_SIZE);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    long count = dataSnapshot.getChildrenCount();
                    if(count<QUESTION_CHUNK_SIZE)
                    {
                        isEndOfDatabase = true;
                    }

                    if(count==0 && questionsList.isEmpty())
                        noResults.setVisibility(View.VISIBLE);

                    int currentHitQueryCount = 0;
                    for(DataSnapshot question : dataSnapshot.getChildren())
                    {
                        lastFetchedQuestionId = question.getKey();
                        Question loadedQuestion = new Question(
                                question.child("title").getValue(String.class),
                                "",
                                question.child("topics").getValue(String.class),
                                question.child("tags").getValue(String.class),
                                question.child("difficulty").getValue(Integer.class),
                                question.child("frequency").getValue(Integer.class),
                                lastFetchedQuestionId
                        );

                        if(!questionIdList.contains(lastFetchedQuestionId))
                        {
                            lastDataReadIndexFromCache++;
                            cachedQuestionsList.add(loadedQuestion);
                            loadedQuestionsIdList.add(lastFetchedQuestionId);
                            if(doesQuestionContainsTagAndQuery(loadedQuestion)) {
                                lastVisibleItem++;
                                questionIdList.add(lastFetchedQuestionId);
                                questionsList.add(loadedQuestion);
                                listAdapter.notifyDataSetChanged();
                                currentHitQueryCount++;
                            }
                        }
                        count--;
                        if(count==0)
                        {
                            progressBar.setVisibility(View.GONE);
                            // recycler view state will not change. So call LoadOnScroll manually.
                            if(currentHitQueryCount==0 && !isEndOfDatabase)
                            {
                                loadOnScroll();
                            }
                            if(questionsList.isEmpty() && isEndOfDatabase)
                                noResults.setVisibility(View.VISIBLE);
                        }
                    }
                }
                else
                {
                    isEndOfDatabase = true;
                    noResults.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    /**
     * Load more data when user reaches end of the list.
     */
    private void loadOnScroll() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isEndOfDatabase)
                    getQuestions();
            }
        }, DELAY_BEFORE_NEXT_CHUNK);
    }

    /**
     * It checks whether search query or filtered tag is present in the question.
     *
     * @param question question from database
     * @return true if search query or filtered tag is present in the question
     */
    private boolean doesQuestionContainsTagAndQuery(Question question)
    {
        String tags = question.getTags().toLowerCase();
        String topics = question.getTopics().toLowerCase();
        String title = question.getTitle().toLowerCase();
        questionIdList.add(lastFetchedQuestionId);
        if(!filteredTagsList.isEmpty()) {
            for (String tag : filteredTagsList) {
                if (tags.contains(tag) || topics.contains(tag))
                    return true;
            }
        }
        else if (tags!=null && topics!=null && (title.contains(searchQuery.toLowerCase()) || tags.contains(searchQuery.toLowerCase()) || topics.contains(searchQuery.toLowerCase()) ))
            return true;
        return false;
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
                menuItem.expandActionView();
                searchView.setQuery(String.valueOf(filteredTagsList), false);
                searchView.clearFocus();
                resetParameters();
                QUESTION_CHUNK_SIZE = 20;
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
        menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();

        // lister for when user searches something
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchQuery = query;
                // remove old filters if there are any.
                filteredTagsList.clear();
                resetParameters();
                QUESTION_CHUNK_SIZE = 20;
                // load questions containing this search query.
                getQuestions();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
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
        menuItem.expandActionView();
        searchView.setQuery(tag, false);
        searchView.clearFocus();
        searchQuery = "";
        filteredTagsList.clear();
        filteredTagsList.add(tag.toLowerCase());
        resetParameters();
        QUESTION_CHUNK_SIZE = 20;
        getQuestions();
    }
}