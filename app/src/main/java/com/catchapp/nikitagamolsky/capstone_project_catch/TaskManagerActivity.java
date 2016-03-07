package com.catchapp.nikitagamolsky.capstone_project_catch;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.catchapp.nikitagamolsky.capstone_project_catch.data.CategoryAdapter;
import com.catchapp.nikitagamolsky.capstone_project_catch.data.DividerItemDecoration;
import com.catchapp.nikitagamolsky.capstone_project_catch.data.TaskAdapter;
import com.catchapp.nikitagamolsky.capstone_project_catch.data.TaskContract;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class TaskManagerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>{
    private RecyclerView mRecyclerView;
    private TaskAdapter mTaskAdapter;
    private CategoryAdapter mCategoryAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;
    private ArrayList<String> allCategories;
    private ArrayList<Task> allTasks;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        getLoaderManager().initLoader(0,null,this);
        getLoaderManager().initLoader(1, null, this);
        allCategories = new ArrayList<>();
        allTasks = new ArrayList<>();
        //AdView mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), InputTaskActivity.class));
            }
        });



        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.taskList);
        mCategoryAdapter = new CategoryAdapter(null,mContext);
        mTaskAdapter = new TaskAdapter(mContext,allCategories,allTasks);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.HORIZONTAL_LIST));
        mRecyclerView.setAdapter(mTaskAdapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void launchDBManager(View view) {
        startActivity(new Intent(this,AndroidDatabaseManager.class));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id==0) {
            return new CursorLoader(mContext, TaskContract.TaskEntry.CONTENT_URI, null, null, null, null);
        } else {
            return new CursorLoader(mContext, TaskContract.CategoryEntry.CONTENT_URI, null, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == 0) {
            if (data.getCount()!=0) {
                while (data.moveToNext()){
                    Date date = null;
                    String taskName = data.getString(data.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_NAME));
                    String encodedCategory = data.getString(data.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_CATEGORY));
                    String replace = encodedCategory.replace("[", "");
                    String replace1 = replace.replace("]", "");
                    ArrayList<String> taskCategory = new ArrayList<>(Arrays.asList(replace1.split(", ")));
                    int priority = Integer.parseInt(data.getString(data.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY)));
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", new Locale("English"));
                    String dateString = data.getString(data.getColumnIndex(TaskContract.TaskEntry.COLUMN_DATE_ENTERED));
                    try {
                        date = format.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Task task = new Task(taskName,taskCategory,priority);
                    task.setDateEntered(date);
                    allTasks.add(task);
                }
            }

        } else {
            while (data.moveToNext()) {
                allCategories.add(data.getString(data.getColumnIndex(TaskContract.CategoryEntry.COLUMN_CATEGORY)));
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
