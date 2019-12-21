package com.thirudet.wordfindpuzzle;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.MyHolder> {

    private ArrayList<String> wordList;
    private Context context;
    public ResultListAdapter(Context context, ArrayList<String> wordList) {
        this.wordList = wordList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.result_list_item, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int i) {

        myHolder.word.setText(wordList.get(i));
        myHolder.word_mean.setText(wordList.get(i) +" "+wordList.get(i)+" "+wordList.get(i) +" "+wordList.get(i) );

        myHolder.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, WebViewActivity.class);
                in.putExtra("WORD", wordList.get(i));
                context.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView word, word_mean, search;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            search = itemView.findViewById(R.id.search);
           word = itemView.findViewById(R.id.word);
           word_mean = itemView.findViewById(R.id.word_mean);

        }
    }
}
