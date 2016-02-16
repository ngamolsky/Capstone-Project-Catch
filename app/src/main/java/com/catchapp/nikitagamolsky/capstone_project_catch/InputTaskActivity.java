package com.catchapp.nikitagamolsky.capstone_project_catch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class InputTaskActivity extends AppCompatActivity {

    private Context mContext;
    private Task inputTask;
    private String inputTitle;
    private ArrayList<String> inputCategories;
    private int priority;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_task);
        mContext = this;
        inputCategories = getIntent().getStringArrayListExtra("addedCategories");
        TextView taskTitle = (TextView)findViewById(R.id.task_title);
        inputTitle = taskTitle.getText().toString();
        SeekBar priorityBar = (SeekBar)findViewById(R.id.seekBar);
        priorityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                priority = seekBar.getProgress();
            }
        });


    }

    public void selectCategory(View view){
        startActivity(new Intent(mContext, PickCategoryDialog.class));
    }

    public void saveTask(View v){
        inputTask = new Task(inputTitle,inputCategories,priority);

    }

}
