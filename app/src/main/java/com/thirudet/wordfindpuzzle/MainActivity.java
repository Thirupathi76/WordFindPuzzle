package com.thirudet.wordfindpuzzle;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thirudet.wordfindpuzzle.wordsearchcore.Model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_COME_BACK = 454;
    private static ArrayList<Integer> Colors = null;
    String curResult;
    int currentPoint = 0;
    GameView gView;
    private Model model;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        model = new Model();
        InitModels();
        InitColors();
        model.generate();
//        showDialog(456, "10.23");
        new StartGameTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public class StartGameTask extends AsyncTask<Void, Integer, Model> {
        private StartGameTask() {
        }

        protected Model doInBackground(Void... val) {
            if (model == null) {
                InitModels();
            }
            model.generate();
            return model;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Model model) {

            gView = new GameView(MainActivity.this, model.getMatrix(), 10, 10, model.GetFinishedList());
            setContentView(gView);
        }
    }

    public static int GetRandomColor() {
        return Colors.get(new Random().nextInt(Colors.size()));
    }

    public void SubmitScore(int point, String time, ArrayList<String> wordList) {
        this.currentPoint = point;
        this.curResult = "";
        this.curResult = getString(R.string.score) + ": " + point + "\n" + getString(R.string.time) + ": " + time;

        showDialog(point, time);
        /*AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Successful")
                .setMessage("View Analysis?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Intent newIn = new Intent(MainActivity.this, ResultListActivity.class);
                        ArrayList<String> wordList =  model.GetFinishedList().toWordList();
                        newIn.putStringArrayListExtra("WORD_LIST", wordList);
                        startActivityForResult(newIn, REQ_COME_BACK);

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();*/

        /*if (haveNetworkConnection() && isSignedIn()) {
            ShowResultDialog(this.curResult + "\n" + getString(R.string.submitting_score));
            Games.Leaderboards.submitScoreImmediate(getApiClient(),
                    getResources().getString(R.string.leaderboard_id),
                    (long) point).setResultCallback(new C08333());
        } else {
            ShowResultDialog(this.curResult);
        }*/
    }

    private void InitModels() {
        if (model != null) {
            InputStream inputStream = null;
            try {
                inputStream = getAssets().open("WORDS.txt");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                model.loadWordList(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            model.choosePuzzle(Model.PuzzleType.WORDSEARCH);
        }
    }

    private void InitColors() {
        if (Colors == null) {
            Colors = new ArrayList();
            Colors.add(Color.argb(100, 221, 30, 47));
            Colors.add(Color.argb(100, 6, 162, 203));
            Colors.add(Color.argb(100, 33, 133, 89));
            Colors.add(Color.argb(100, 62, 73, 87));
            Colors.add(Color.argb(100, 141, 58, 97));
            Colors.add(Color.argb(100, 215, 75, 75));
            Colors.add(Color.argb(100, 101, 153, MotionEventCompat.ACTION_MASK));
            Colors.add(Color.argb(100, 153, 0, 51));
//            Colors.add(Integer.valueOf(Color.argb(100, 68, 50, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)));
            Colors.add(Color.argb(100, 140, 72, 159));
            Colors.add(Color.argb(100, 51, 0, 51));
//            Colors.add(Integer.valueOf(Color.argb(100, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, 0, 51)));
//            Colors.add(Integer.valueOf(Color.argb(100, 153, 51, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)));
//            Colors.add(Integer.valueOf(Color.argb(100, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, 153, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)));
            Colors.add(Color.argb(100, 0, 0, 128));
            Colors.add(Color.argb(100, MotionEventCompat.ACTION_MASK, 0, 128));
//            Colors.add(Integer.valueOf(Color.argb(100, MotionEventCompat.ACTION_MASK, MARGIN, 147)));
            Colors.add(Color.argb(100, 51, 51, MotionEventCompat.ACTION_MASK));
            Colors.add(Color.argb(100, MotionEventCompat.ACTION_MASK, 69, 0));
            Colors.add(Color.argb(100, MotionEventCompat.ACTION_MASK, 140, 0));
            Colors.add(Color.argb(100, MotionEventCompat.ACTION_MASK, 165, 0));
            Colors.add(Color.argb(100, MotionEventCompat.ACTION_MASK, 215, 0));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_COME_BACK) {
                new StartGameTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);

            }

        }
    }

    void showDialog(int point, String time){
        alertDialog = null;
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        // ...Irrelevant code for customizing the buttons and title

        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView= inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        Button rate_btn = dialogView.findViewById(R.id.btn_rate);
        Button share_btn = dialogView.findViewById(R.id.btn_share);

        TextView txt_close = dialogView.findViewById(R.id.txt_close);
        TextView txt_score = dialogView.findViewById(R.id.txt_score);
        TextView txt_time = dialogView.findViewById(R.id.txt_time);

        txt_score.setText(String.valueOf(point));
        txt_time.setText("Time : "+time);
        LinearLayout analysis_layout = dialogView.findViewById(R.id.layout_analysis);
        LinearLayout new_layout = dialogView.findViewById(R.id.layout_new_game);


        dialogBuilder.setCancelable(false);
        alertDialog = dialogBuilder.create();

        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                new StartGameTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        });

        new_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                new StartGameTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        });

        analysis_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIn = new Intent(MainActivity.this, ResultListActivity.class);
                ArrayList<String> wordList =  model.GetFinishedList().toWordList();
                newIn.putStringArrayListExtra("WORD_LIST", wordList);
                startActivityForResult(newIn, REQ_COME_BACK);
                alertDialog.cancel();
            }
        });
        rate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // rate button goes here
                alertDialog.cancel();
            }
        });

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // share button
                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }
}
