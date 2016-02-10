package com.catchapp.nikitagamolsky.capstone_project_catch.data;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.catchapp.nikitagamolsky.capstone_project_catch.R;

public class TaskAdapter extends CursorAdapter {

    public TaskAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView taskBalloon = (ImageView)view.findViewById(R.id.balloon);
    }
}
