package com.catchapp.nikitagamolsky.capstone_project_catch.data;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.catchapp.nikitagamolsky.capstone_project_catch.R;

public class CategoryAdapter extends CursorAdapter {
    private LayoutInflater inflater;

    public CategoryAdapter(Context context, Cursor categoryCursor, int flags)  {
        super(context, categoryCursor, 0);
        inflater = LayoutInflater.from(context);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.category_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView categoryText = (TextView)view.findViewById(R.id.categoryText);
        String currentCategory = cursor.getString(cursor.getColumnIndex(TaskContract.CategoryEntry.COLUMN_CATEGORY));
        categoryText.setText(currentCategory);


    }

}
