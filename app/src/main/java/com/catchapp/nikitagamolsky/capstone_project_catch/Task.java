package com.catchapp.nikitagamolsky.capstone_project_catch;


import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.catchapp.nikitagamolsky.capstone_project_catch.data.TaskContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Task  {
    private String title;
    private ArrayList<String> categories;
    private int priority;
    private Date dateEntered;
    private long position;


    public Task() {
    }

    public Task(String title, ArrayList<String> categories,int priority) {
        this.title = title;
        this.categories = categories;
        this.priority = priority;
        this.dateEntered = null;
    }


    public long getPosition() {
        return position;
    }


    public void setDateEntered(Date dateEntered) {
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", new Locale("English"));
        Date date = new Date();
        String datetime = format.format(date);
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_TASK_NAME,this.title);
        values.put(TaskContract.TaskEntry.COLUMN_TASK_CATEGORY, this.categories.toString());
        values.put(TaskContract.TaskEntry.COLUMN_PRIORITY, "" + this.priority);
        values.put(TaskContract.TaskEntry.COLUMN_DATE_ENTERED, datetime);
        context.getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);

    }

    @Override
    public String toString() {

        return title;
    }

    public void updatePosition(Context context){
        Date currentDate = new Date();
        position = (priority*960*(currentDate.getTime()-dateEntered.getTime()))/(1000*60*60*24*10);
        Log.v("NEWPOSITION", "" + position);
    }
}
