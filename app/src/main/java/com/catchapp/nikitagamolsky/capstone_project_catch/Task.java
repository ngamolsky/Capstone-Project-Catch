package com.catchapp.nikitagamolsky.capstone_project_catch;


import android.content.ContentValues;
import android.content.Context;

import com.catchapp.nikitagamolsky.capstone_project_catch.data.TaskContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Task  {
    private String title;
    private ArrayList<String> categories;
    private int priority;
    private Date dateEntered;


    public Task() {
    }

    public Task(String title, ArrayList<String> categories,int priority, Date dateEntered) {
        this.title = title;
        this.categories = categories;
        this.priority = priority;
        this.dateEntered = dateEntered;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }




    public void save(Context context){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_TASK_NAME,this.title);
        values.put(TaskContract.TaskEntry.COLUMN_TASK_CATEGORY, this.categories.toString());
        values.put(TaskContract.TaskEntry.COLUMN_PRIORITY, "" + this.priority);
        values.put(TaskContract.TaskEntry.COLUMN_DATE_ENTERED, new Date().toString());
        context.getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);

    }

    @Override
    public String toString() {

        return title;
    }

    public void updatePosition(Context context){

    }
}
