package com.catchapp.nikitagamolsky.capstone_project_catch;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.catchapp.nikitagamolsky.capstone_project_catch.data.TaskContract;
import com.catchapp.nikitagamolsky.capstone_project_catch.data.WrappableGridLayoutManager;

import java.util.ArrayList;

public class InputTaskActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>, NavigationView.OnNavigationItemSelectedListener {

    private Context mContext;
    private CategoryAdapter mCategoryAdapter;
    private String inputTitle;
    private ArrayList<String> inputCategories;
    public EditText editCategory;
    private int priority;
    private String inputCategory;
    private  EditText taskTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        editCategory = (EditText)findViewById(R.id.editCategory);
        editCategory.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                    inputCategory = editCategory.getText().toString();
                    if (!inputCategory.isEmpty()){
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    ContentValues values = new ContentValues();
                    values.put(TaskContract.CategoryEntry.COLUMN_CATEGORY, inputCategory);
                    getContentResolver().insert(TaskContract.CategoryEntry.CONTENT_URI, values);
                    editCategory.setVisibility(View.GONE);
                    editCategory.setText("");
                    mCategoryAdapter.notifyDataSetChanged();}
                    else{
                        final View coordinatorLayoutView = findViewById(R.id.snackbarPosition);

                        Snackbar
                                .make(coordinatorLayoutView, "Please Enter a Cateogory", Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
                return handled;
            }
        });




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mContext = this;
        mCategoryAdapter = new CategoryAdapter(null,mContext);
        inputCategories = new ArrayList<>();
        getLoaderManager().initLoader(0, null, this);
        taskTitle = (EditText)findViewById(R.id.inputTask);
        taskTitle.setText(inputTitle);
        taskTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                    inputTitle = taskTitle.getText().toString();
                    ImageView check = (ImageView) findViewById(R.id.titleCheck);
                    check.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                }
                return handled;
            }
        });

        taskTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ImageView check = (ImageView) findViewById(R.id.titleCheck);
                    check.setVisibility(View.VISIBLE);
                    inputTitle = taskTitle.getText().toString();
                }
            }
        });

        RecyclerView categoryList = (RecyclerView) findViewById(R.id.categoryList);
        WrappableGridLayoutManager mLayoutManager = new WrappableGridLayoutManager(mContext,3);
        categoryList.setLayoutManager(mLayoutManager);
        categoryList.setAdapter(mCategoryAdapter);
        final TextView priorityView = (TextView) findViewById(R.id.priorityView);
        priorityView.setText("0");
        final SeekBar priorityBar = (SeekBar)findViewById(R.id.priorityBar);
        priorityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                priorityView.setText(""+progress);
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




    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext, TaskContract.CategoryEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCategoryAdapter.changeCursor(data);

    }

    @Override
    public void onLoaderReset(Loader loader) {
        mCategoryAdapter.changeCursor(null);
    }



    public void saveTask(View v){
        inputTitle = taskTitle.getText().toString();
        Task inputTask = new Task(inputTitle, inputCategories, priority);
        if(inputTitle.isEmpty() || inputCategories.isEmpty()){
            final View coordinatorLayoutView = findViewById(R.id.snackbarPosition);

            Snackbar
                    .make(coordinatorLayoutView, "Either Title or Categories are empty!", Snackbar.LENGTH_LONG)
                    .show();
        } else{
        inputTask.save(mContext);
        startActivity(new Intent(this,TaskManagerActivity.class));}
    }

    public void addCategory(String category){
        inputCategories.add(category);
    }

    public void removeCategory(String category){
        inputCategories.remove(category);
    }

    public void showEditCategory(){
        editCategory.setVisibility(View.VISIBLE);
        editCategory.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add_task) {
            startActivity(new Intent(getApplicationContext(), InputTaskActivity.class));
        }

        if (id == R.id.task_manager) {
            startActivity(new Intent(getApplicationContext(),TaskManagerActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
