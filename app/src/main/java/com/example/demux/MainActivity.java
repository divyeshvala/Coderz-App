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
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import co.lujun.androidtagview.ColorFactory;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

// TODO: order by frequency.
// TODO: Data upload
// TODO: proper design pattern.
// TODO: Search problem - optional.

public class MainActivity extends AppCompatActivity implements FilterInterface
{
    private ListAdapter listAdapter;
    private ArrayList<Question> questionsList;
    private static int lastVisibleItem, currentVisibleItem;
    private static String lastFetchedQuestionId;
    private static String searchQuery;
    private ProgressBar progressBar;
    private ArrayList<String> filteredTagsList;
    private TextView noResults;
    private static boolean isEndOfDatabase;
    private LinearLayoutManager layoutManager;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("questions");

    @RequiresApi(api = Build.VERSION_CODES.M)  // todo
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constants constants = new Constants();

        progressBar = findViewById(R.id.progress_bar);
        noResults = findViewById(R.id.id_no_results);
        questionsList = new ArrayList<>();
        filteredTagsList = new ArrayList<>();
        searchQuery = "";
        lastVisibleItem = 0;
        isEndOfDatabase = false;

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        listAdapter = new ListAdapter(this, questionsList, this);
        recyclerView.setAdapter(listAdapter);

        FloatingActionButton filter = findViewById(R.id.id_filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFilterSheet();
            }
        });

        loadFirstSetOfQuestions();

        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                currentVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
                if(!isEndOfDatabase && currentVisibleItem+1>=lastVisibleItem)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadNextSetOfQuestions();
                        }
                    }, 1000);
                }
            }
        });
    }

    private void displayQuestions(DataSnapshot dataSnapshot, boolean isSkipFirst)
    {
        Log.i("Main", "................."+dataSnapshot.getChildrenCount());
        long count = dataSnapshot.getChildrenCount();
        if(count==0 || (isSkipFirst && count==1))
        {
            Log.i("Main", ".................STOP.........");
            isEndOfDatabase = true;
            if(questionsList.isEmpty())
                noResults.setVisibility(View.VISIBLE);
        }
        int successCount = 0;
        for (DataSnapshot question : dataSnapshot.getChildren())
        {
            if(isSkipFirst && count==dataSnapshot.getChildrenCount()){
                count--;
                continue;
            }
            lastFetchedQuestionId = question.getKey();
            String title = question.child("title").getValue(String.class);
            String tags = question.child("tags").getValue(String.class);
            int frequency = question.child("frequency").getValue(Integer.class);

            if(!filteredTagsList.isEmpty())
            {
                String tagsLowerCase = tags.toLowerCase();
                for(String tag : filteredTagsList)
                {
                    if(tagsLowerCase.contains(tag))
                    {
                        successCount++;
                        questionsList.add(new Question(title, "", tags, frequency));
                        lastVisibleItem++;
                        listAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
            else if (tags != null && title != null && (title.toLowerCase().contains(searchQuery.toLowerCase()) || tags.toLowerCase().contains(searchQuery.toLowerCase())))
            {
                successCount++;
                questionsList.add(new Question(title, "", tags, frequency));
                lastVisibleItem++;
                listAdapter.notifyDataSetChanged();
            }

            count--;
            if( count == 0 )
            {
                progressBar.setVisibility(View.GONE);
                if(dataSnapshot.getChildrenCount()<10)
                {
                    Log.i("Main", ".................STOP.........");
                    isEndOfDatabase = true;
                    if(questionsList.isEmpty())
                    {
                        noResults.setVisibility(View.VISIBLE);
                    }
                }
                else if(successCount==0)
                {
                    currentVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
                    Log.i("___Main", ""+currentVisibleItem+", "+lastVisibleItem);
                    if(currentVisibleItem+1>=lastVisibleItem)
                    {
                        progressBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadNextSetOfQuestions();
                            }
                        }, 1000);
                    }
                }
            }
        }
    }

    private void loadNextSetOfQuestions()
    {
        databaseReference.orderByKey().startAt(lastFetchedQuestionId).limitToFirst(11).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Log.i("Main", "Inside loadN questions. ");
                displayQuestions(dataSnapshot, true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void loadFirstSetOfQuestions()
    {
        lastVisibleItem = 0;
        isEndOfDatabase = false;
        questionsList.clear();
        listAdapter.notifyDataSetChanged();
        noResults.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.orderByKey().limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Log.i("Main", "Inside loadF questions. ");
                displayQuestions(dataSnapshot, false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void launchFilterSheet() {
        filteredTagsList.clear();

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                MainActivity.this, R.style.BottomSheetDialogTheme
        );

        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.layout_bottom_sheet,
                        findViewById(R.id.bottom_sheet_container)
                );

        TagContainerLayout tagContainerGeneral = bottomSheetView.findViewById(R.id.filter_tag_container_layout_general);
        tagContainerGeneral.setTheme(ColorFactory.RANDOM);
        tagContainerGeneral.setBackgroundColor(Color.TRANSPARENT);
        tagContainerGeneral.setTagTextSize(48);
        tagContainerGeneral.setIsTagViewClickable(true);
        tagContainerGeneral.setIsTagViewSelectable(true);

        TagContainerLayout tagContainerCompanies = bottomSheetView.findViewById(R.id.filter_tag_container_layout_companies);
        tagContainerCompanies.setTheme(ColorFactory.RANDOM);
        tagContainerCompanies.setBackgroundColor(Color.TRANSPARENT);
        tagContainerCompanies.setTagTextSize(48);
        tagContainerCompanies.setIsTagViewClickable(true);
        tagContainerCompanies.setIsTagViewSelectable(true);

        tagContainerGeneral.setTags(Constants.getGeneralTagsList(), Constants.getGeneralTagColorsList());
        tagContainerCompanies.setTags(Constants.getCompanyTagsList(), Constants.getCompanyTagColorsList());

        tagContainerGeneral.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                List<Integer> selectedPositions = tagContainerGeneral.getSelectedTagViewPositions();
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

        tagContainerCompanies.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                Log.i("Filter", "" + position);
                List<Integer> selectedPositions = tagContainerCompanies.getSelectedTagViewPositions();
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
                loadFirstSetOfQuestions();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchQuery = query;
                filteredTagsList.clear();
                loadFirstSetOfQuestions();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText)
            {
//                searchQuery = newText;
//                filteredTagsList.clear();
//                loadFirstSetOfQuestions();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void applyFilter(String tag)
    {
        searchQuery = "";
        filteredTagsList.clear();
        filteredTagsList.add(tag.toLowerCase());
        loadFirstSetOfQuestions();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("Main", "question size : "+questionsList.size());
            }
        }, 3000);
    }

    @Override
    public void onBackPressed()
    {
        if(!searchQuery.isEmpty() || !filteredTagsList.isEmpty())
        {
            finish();
            startActivity(getIntent());
        }
    }

    // TODO: Remove it later
    private void uploadDummyQuestionsList()
    {
        questionsList.add(new Question("Trapping rain water", "",
                "Google, Microsoft", 4));
        questionsList.add(new Question("Rainy night in tallin", "",
                "Facebook, DEShaw, Goldman, SAP Labs", 4));
        questionsList.add(new Question("Shortest path to leaf", "",
                "Visa, DE Shaw", 4));
        questionsList.add(new Question("Groking the rain water", "",
                "Microsoft, SAP Labs", 4));

        questionsList.add(new Question("Rain water in middle", "",
                "SAP Labs", 4));

        questionsList.add(new Question("Longest path to leaf", "",
                "DE Shaw, Goldman, SAP Labs", 4));

        questionsList.add(new Question("Mountain Shortest path to leaf", "",
                "Goldman, Microsoft", 4));

        questionsList.add(new Question("Rainy night in tallin", "",
                "Facebook, SAP Labs", 4));
        questionsList.add(new Question("Shortest path to leaf", "",
                "Visa, Microsoft", 4));
        questionsList.add(new Question("Groking the rain water", "",
                "Amazon", 4));

        questionsList.add(new Question("Rain water in middle", "",
                "SAP Labs", 4));

        questionsList.add(new Question("Longest path to leaf", "",
                "DE Shaw, Goldman, Microsoft, SAP Labs", 4));

        questionsList.add(new Question("Mountain Shortest path to leaf", "",
                "Microsoft", 4));

        questionsList.add(new Question("Trapping rain water", "",
                "Paytm", 4));

        questionsList.add(new Question("Trucks in place", "",
                "Barco", 4));

        DatabaseReference databaseReference;
        Map<String, Object> messageData = new HashMap<>();
        for(Question question : questionsList)
        {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("questions").push();
            messageData.put("title", question.getTitle());
            messageData.put("frequency", question.getFrequency());
            messageData.put("tags", question.getTags());
            databaseReference.updateChildren(messageData);
        }
    }
}