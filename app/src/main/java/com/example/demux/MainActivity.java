package com.example.demux;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class MainActivity extends AppCompatActivity
{
    private ListAdapter listAdapter;
    private ArrayList<Question> questionsList;
    private static int lastVisibleItem, currentVisibleItem;
    private static String lastFetchedQuestionId;
    private static String searchQuery;
    private ProgressBar progressBar, initialProgressBar;

    @RequiresApi(api = Build.VERSION_CODES.M)  // todo
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        initialProgressBar = findViewById(R.id.initial_progress_bar);
        questionsList = new ArrayList<>();
        searchQuery = "";
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        listAdapter = new ListAdapter(this, questionsList);
        recyclerView.setAdapter(listAdapter);

        lastVisibleItem = 9;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadFirstTenQuestions();
            }
        }, 1000);

        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
            {
                currentVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
                if(currentVisibleItem==lastVisibleItem)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    lastVisibleItem = lastVisibleItem + 10;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadNextTenQuestions();
                        }
                    }, 1000);
                }
            }
        });
    }

    private void loadNextTenQuestions()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("questions");

        databaseReference.orderByKey().startAt(lastFetchedQuestionId).limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                long count = dataSnapshot.getChildrenCount();
                for (DataSnapshot question : dataSnapshot.getChildren())
                {
                    lastFetchedQuestionId = question.getKey(); ////HERE WE ARE SAVING THE LAST POST_ID FROM URS POST

                    String title = question.child("title").getValue(String.class);
                    String tags = question.child("tags").getValue(String.class);
                    int frequency = question.child("frequency").getValue(Integer.class);

                    if (tags != null && title != null && (title.contains(searchQuery) || tags.contains(searchQuery)))
                    {
                        questionsList.add(new Question(title, "", tags, frequency));
                    }
                    count--;
                    if( count == 0 )
                    {
                        progressBar.setVisibility(View.GONE);
                    }
                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void loadFirstTenQuestions()
    {
        initialProgressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("questions");

        databaseReference.orderByKey().limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
            long count = dataSnapshot.getChildrenCount();
            for (DataSnapshot question : dataSnapshot.getChildren())
            {
                lastFetchedQuestionId = question.getKey(); ////HERE WE ARE SAVING THE LAST POST_ID FROM URS POST

                String title = question.child("title").getValue(String.class);
                String tags = question.child("tags").getValue(String.class);
                int frequency = question.child("frequency").getValue(Integer.class);

                if (tags != null && title != null && (title.contains(searchQuery) || tags.contains(searchQuery)))
                {
                    questionsList.add(new Question(title, "", tags, frequency));
                }
                count--;
                if(count==0)
                {
                    initialProgressBar.setVisibility(View.GONE);
                }
                listAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
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
//                Log.i("MainActivity", "Search query : "+query);
//                searchQuery = query;
//                questionsList.clear();
//                listAdapter.notifyDataSetChanged();
//                loadFirstTenQuestions();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                searchQuery = newText;
                questionsList.clear();
                listAdapter.notifyDataSetChanged();
                loadFirstTenQuestions();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // TODO: Remove it later
    private void uploadDummyQuestionsList()
    {
        questionsList.add(new Question("Trapping rain water", "",
                "Google, Microsoft, SAP, Labs", 4));
        questionsList.add(new Question("Rainy night in tallin", "",
                "Facebook, DEShaw, Goldman, Microsoft, SAP Labs", 4));
        questionsList.add(new Question("Shortest path to leaf", "",
                "Visa, DEShaw ,Goldman, Microsoft, SAP Labs", 4));
        questionsList.add(new Question("Groking the rain water", "",
                "Microsoft, SAP Labs", 4));

        questionsList.add(new Question("Rain water in middle", "",
                "SAP Labs", 4));

        questionsList.add(new Question("Longest path to leaf", "",
                "DE Shaw, Goldman, Microsoft, SAP Labs", 4));

        questionsList.add(new Question("Mountain Shortest path to leaf", "",
                "Goldman, Microsoft, SAP Labs", 4));

        questionsList.add(new Question("Rainy night in tallin", "",
                "Facebook, DEShaw, Goldman, Microsoft, SAP Labs", 4));
        questionsList.add(new Question("Shortest path to leaf", "",
                "Visa, DEShaw ,Goldman, Microsoft, SAP Labs", 4));
        questionsList.add(new Question("Groking the rain water", "",
                "Microsoft, SAP Labs", 4));

        questionsList.add(new Question("Rain water in middle", "",
                "SAP Labs", 4));

        questionsList.add(new Question("Longest path to leaf", "",
                "DE Shaw, Goldman, Microsoft, SAP Labs", 4));

        questionsList.add(new Question("Mountain Shortest path to leaf", "",
                "Goldman, Microsoft, SAP Labs", 4));

        questionsList.add(new Question("Trapping rain water", "",
                "Google, Microsoft, SAP Labs", 4));

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