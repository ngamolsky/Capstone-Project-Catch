package com.catchapp.nikitagamolsky.capstone_project_catch;


import android.content.ContentValues;

import com.catchapp.nikitagamolsky.capstone_project_catch.data.TaskContract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Task  implements Serializable{
    private String title;
    private ArrayList<String> categories;
    private int priority;
    public Task() {
    }

    public Task(String title, ArrayList<String> categories,int priority) {
        this.title = title;
        this.categories = categories;
        this.priority = priority;
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

    public String serializeCategories(ArrayList<String> categories) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(categories);
        byte[] data = out.toByteArray();
        out.close();
        return new String(data);
    }

    public ArrayList<String> deserializeCategories(String encodedCategories) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(encodedCategories.getBytes()));
        ArrayList<String> categories = (ArrayList<String>)in.readObject();
        in.close();
        return categories;
    }

    public void saveTask() throws IOException {
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_TASK_NAME,title);
        values.put(TaskContract.TaskEntry.COLUMN_TASK_CATEGORY,serializeCategories(categories));
        values.put(TaskContract.TaskEntry.COLUMN_PRIORITY,"" + priority;
    }
}
