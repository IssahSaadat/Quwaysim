package com.smis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class QuizActivity extends AppCompatActivity {
    FloatingActionButton newQuiz;
    private ListView mListView;
    private String TAG ="QuizActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        newQuiz = findViewById(R.id.new_quiz_fab);
        mListView =  findViewById(R.id.listView);

        newQuiz.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(QuizActivity.this, AddQuestionsActivity.class);
                //startActivity(intent);
            }
        }));

    }
}
