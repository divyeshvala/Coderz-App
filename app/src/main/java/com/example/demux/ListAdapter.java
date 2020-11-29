package com.example.demux;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.lujun.androidtagview.ColorFactory;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class ListAdapter extends RecyclerView.Adapter< ListAdapter.QuestionListViewHolder >
{
    private final ArrayList<Question> questionList;
    private final Context context;
    private final FilterInterface listener;

    public ListAdapter(Context context, ArrayList<Question> questionList, FilterInterface listener)
    {
        this.questionList = questionList;
        this.context = context;
        this.listener = listener;
    }

    public static class QuestionListViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView title;
        private final TextView frequency;
        private final TagContainerLayout tagLayout;
        private final TagContainerLayout topicsLayout;
        private final CardView card;
        private final TextView difficulty;

        public QuestionListViewHolder(View view)
        {
            super(view);
            title = view.findViewById(R.id.title);
            tagLayout = view.findViewById(R.id.tag_container_layout);
            topicsLayout = view.findViewById(R.id.topics_container_layout);
            frequency = view.findViewById(R.id.frequency);
            card = view.findViewById(R.id.card_view);
            difficulty = view.findViewById(R.id.id_difficulty);
        }
    }

    @NonNull
    @Override
    public QuestionListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        return new QuestionListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_question, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionListViewHolder questionListViewHolder, final int i)
    {
        //Set text and background color of difficulty level
        if(questionList.get(i).getDifficultyLevel()==1) {
            questionListViewHolder.difficulty.setBackgroundColor(context.getResources().getColor(R.color.easyColor));
            questionListViewHolder.difficulty.setText(R.string.difficulty_easy);
        }
        else if(questionList.get(i).getDifficultyLevel()==2) {
            questionListViewHolder.difficulty.setBackgroundColor(context.getResources().getColor(R.color.mediumColor));
            questionListViewHolder.difficulty.setText(R.string.difficulty_medium);
        }
        else {
            questionListViewHolder.difficulty.setBackgroundColor(context.getResources().getColor(R.color.hardColor));
            questionListViewHolder.difficulty.setText(R.string.difficulty_hard);
        }

        // set title of the question
        questionListViewHolder.title.setText(questionList.get(i).getTitle());
        // set frequency of the question
        questionListViewHolder.frequency.setText(String.valueOf(questionList.get(i).getFrequency()));

        // initialize tags container
        List<String> tagsList = Arrays.asList(questionList.get(i).getTags().split("\\s*,\\s*"));
        questionListViewHolder.tagLayout.setTheme(ColorFactory.RANDOM);
        questionListViewHolder.tagLayout.setBackgroundColor(Color.TRANSPARENT);
        questionListViewHolder.tagLayout.setTagTextSize(40);
        questionListViewHolder.tagLayout.setIsTagViewClickable(true);
        questionListViewHolder.tagLayout.setTags(tagsList, Constants.getTagColorsList(tagsList));
        questionListViewHolder.tagLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                // call apply filter method of MainActivity
                listener.applyFilter(text);
            }
            @Override
            public void onTagLongClick(int position, String text) { }
            @Override
            public void onSelectedTagDrag(int position, String text) { }
            @Override
            public void onTagCrossClick(int position) { }
        });

        // initialize topics container
        List<String> topicsList = Arrays.asList(questionList.get(i).getTopics().split("\\s*,\\s*"));
        questionListViewHolder.topicsLayout.setTheme(ColorFactory.NONE);
        questionListViewHolder.topicsLayout.setBackgroundColor(Color.TRANSPARENT);
        questionListViewHolder.topicsLayout.setTagTextSize(40);
        questionListViewHolder.topicsLayout.setIsTagViewClickable(true);
        questionListViewHolder.topicsLayout.setTags(topicsList, Constants.getTagColorsList(topicsList));
        questionListViewHolder.topicsLayout.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                // call apply filter method of MainActivity
                listener.applyFilter(text);
            }
            @Override
            public void onTagLongClick(int position, String text) { }
            @Override
            public void onSelectedTagDrag(int position, String text) { }
            @Override
            public void onTagCrossClick(int position) { }
        });

        // handle click on the question
        questionListViewHolder.card.setOnClickListener(v -> Toast.makeText(context, "Coming Soon: [ tapped on question.]", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount()
    {
        return questionList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}