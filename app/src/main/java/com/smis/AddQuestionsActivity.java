package com.smis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AddQuestionsActivity extends AppCompatActivity {
    Button mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);

        mSave = findViewById(R.id.save_course_btn);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddQuestionsActivity.this, SetQuestionsActivity.class);
                startActivity(intent);
            }
        });
    }
}
