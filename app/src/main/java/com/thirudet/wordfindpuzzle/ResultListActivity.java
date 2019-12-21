package com.thirudet.wordfindpuzzle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class ResultListActivity extends AppCompatActivity {

    RecyclerView result_list_rc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        result_list_rc = findViewById(R.id.result_list_rc);
        ArrayList<String> wordList = getIntent().getStringArrayListExtra("WORD_LIST");
        result_list_rc.setLayoutManager(new LinearLayoutManager(this));
        ResultListAdapter adapter = new ResultListAdapter(ResultListActivity.this, wordList);
        result_list_rc.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }
}
