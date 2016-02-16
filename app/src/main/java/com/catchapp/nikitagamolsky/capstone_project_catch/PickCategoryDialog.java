package com.catchapp.nikitagamolsky.capstone_project_catch;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.catchapp.nikitagamolsky.capstone_project_catch.data.CategoryAdapter;
import com.catchapp.nikitagamolsky.capstone_project_catch.data.TaskContract;

import java.util.ArrayList;

public class PickCategoryDialog extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Context mContext;
    private ListView listView;
    private CategoryAdapter mCategoryAdapter;
    private ArrayList<String> mSelectedCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_category_dialog);
        mContext = this;
        getLoaderManager().initLoader(0, null, this);
        mCategoryAdapter = new CategoryAdapter(mContext,null,0);
        mSelectedCategories = new ArrayList<>();
        listView = (ListView) findViewById(R.id.categoryList);
        final EditText categoryEdit = (EditText) findViewById(R.id.categoryEdit);
        final TextView addCategoryText = (TextView) findViewById(R.id.addCategoryText);
        listView.setAdapter(mCategoryAdapter);
        addCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                categoryEdit.setText("");
                categoryEdit.setVisibility(View.VISIBLE);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView categoryText = (TextView) view.findViewById(R.id.categoryText);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.categoryCheckBox);
                String selectedCategory = categoryText.getText().toString();
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    mSelectedCategories.remove(selectedCategory);
                } else {
                    checkBox.setChecked(true);
                    mSelectedCategories.add(selectedCategory);
                }
            }
        });


        categoryEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                    String newCategory = categoryEdit.getText().toString();
                    ContentValues values = new ContentValues();
                    values.put(TaskContract.CategoryEntry.COLUMN_CATEGORY, newCategory);
                    mContext.getContentResolver().insert(TaskContract.CategoryEntry.CONTENT_URI, values);
                    categoryEdit.setVisibility(View.GONE);
                    addCategoryText.setVisibility(View.VISIBLE);
                }
                return handled;
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext, TaskContract.CategoryEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCategoryAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mCategoryAdapter.swapCursor(null);
    }

    public void addCategories(View v){
        startActivity(new Intent(mContext, InputTaskActivity.class).putStringArrayListExtra("addedCategories", mSelectedCategories));
    }

}