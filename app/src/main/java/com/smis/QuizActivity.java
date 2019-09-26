package com.smis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smis.Fragment.CategoryFragment;
import com.smis.Fragment.RankingFragment;

public class QuizActivity extends AppCompatActivity {
    FloatingActionButton newQuiz;
    private ListView mListView;
    private String TAG ="QuizActivity";
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        newQuiz = findViewById(R.id.new_quiz_fab);
        mListView =  findViewById(R.id.listView);

        newQuiz.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizActivity.this, AddQuizActivity.class);
                startActivity(intent);
            }
        }));


        bottomNavigationView = findViewById(R.id.navigation);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, CategoryFragment.newInstance());
        fragmentTransaction.commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectFragment = null;
                switch (item.getItemId()) {
                    case R.id.category:
                        selectFragment = CategoryFragment.newInstance();
                        break;
                    case R.id.ranking:
                        selectFragment = RankingFragment.rankingFragment();
                        break;
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, selectFragment);
                fragmentTransaction.commit();
                return true;

            }
        });
    }
    }

