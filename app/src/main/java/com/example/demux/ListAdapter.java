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

public class ListAdapter extends RecyclerView.Adapter< ListAdapter.QuestionListViewHolder >
{
    private ArrayList<Question> questionList;
    private Context context;

    public ListAdapter(Context context, ArrayList<Question> questionList)
    {
        this.questionList = questionList;
        this.context = context;
    }

    public class QuestionListViewHolder extends RecyclerView.ViewHolder
    {
        private TextView title;
        private TagContainerLayout tagLayout;
        private CardView card;

        public QuestionListViewHolder(View view)
        {
            super(view);
            title = view.findViewById(R.id.title);
            tagLayout = view.findViewById(R.id.tag_container_layout);
            card = view.findViewById(R.id.card_view);
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
        //questionListViewHolder.setIsRecyclable(false);
        questionListViewHolder.title.setText(questionList.get(i).getTitle());

        String tags = questionList.get(i).getTags();
        List<String> tagsList = Arrays.asList(tags.split("\\s*,\\s*"));

        questionListViewHolder.tagLayout.setTheme(ColorFactory.RANDOM);
        questionListViewHolder.tagLayout.setBackgroundColor(Color.TRANSPARENT);
        questionListViewHolder.tagLayout.setTagTextSize(48);
//        questionListViewHolder.tagLayout.setTagBackgroundColor(R.color.colorBlue);
//        questionListViewHolder.tagLayout.setTagTextColor(R.color.colorWhite);
//        questionListViewHolder.tagLayout.setTagBorderColor(Color.TRANSPARENT);

        questionListViewHolder.tagLayout.setTags(tagsList, Constants.getTagColorsList(tagsList));

        questionListViewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(context, "Tapped on question", Toast.LENGTH_SHORT).show();
            }
        });
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