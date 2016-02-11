package com.catchapp.nikitagamolsky.capstone_project_catch;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.catchapp.nikitagamolsky.capstone_project_catch.data.TaskContract;

public class InputTaskActivity extends AppCompatActivity {
    private EditText inputTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_task);
        inputTask = (EditText) findViewById(R.id.inputTask);

    }

    public void saveTask(View view) {
        ContentValues values = new ContentValues();
        values.put("task", inputTask.getText().toString());
        getApplicationContext().getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI,values);
    }
}
