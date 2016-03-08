package com.catchapp.nikitagamolsky.capstone_project_catch.data;


import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.catchapp.nikitagamolsky.capstone_project_catch.R;
import com.catchapp.nikitagamolsky.capstone_project_catch.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {



    private Context mContext;
    private ArrayList<String> mAllCategories;
    private Cursor mTaskCursor;
    public String inputPosition = "0";
    private DataSetObserver mDataSetObserver;
    private boolean mDataValid;


    public TaskAdapter (Context context, Cursor taskCursor,ArrayList<String> allCategories) {
        mContext = context;
        mAllCategories = allCategories;
        mTaskCursor = taskCursor;
        mDataValid = (taskCursor != null) && (!allCategories.isEmpty());
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mTaskCursor != null) {
            mTaskCursor.registerDataSetObserver(mDataSetObserver);
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int topPriority = 0;
        int indexBestTask = -1;
        String currentCategory = mAllCategories.get(position);
        holder.mCategoryLabel.setText(currentCategory);

        while (mTaskCursor.moveToNext()) {
            ArrayList<String> taskCategories = getTaskCategories(mTaskCursor,mTaskCursor.getPosition());
            int currentPriority = Integer.parseInt(mTaskCursor.getString(mTaskCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY)));
            if(taskCategories.contains(currentCategory)){
                if(currentPriority > topPriority){
                    topPriority = currentPriority;
                    indexBestTask = mTaskCursor.getPosition();
                }
            }
        }


        if (indexBestTask!=-1) {
            mTaskCursor.moveToPosition(indexBestTask);
            String taskTitle = mTaskCursor.getString(mTaskCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_NAME));
            ArrayList<String> taskCategories = getTaskCategories(mTaskCursor, indexBestTask);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", new Locale("English"));
            Date dateEntered = null;
            int taskPriority = Integer.valueOf(mTaskCursor.getString(mTaskCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY)));
            try {
                dateEntered = format.parse(mTaskCursor.getString(mTaskCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DATE_ENTERED)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            final Task currentTask = new Task(taskTitle,taskCategories,taskPriority);
            holder.currentTask = currentTask;
            currentTask.setDateEntered(dateEntered);
            currentTask.updatePosition();
            holder.mTaskText.setText(currentTask.getTitle());
            Animation updatePosition = new TranslateAnimation(0, 0, 0, currentTask.getPosition());
            updatePosition.setDuration(1000);
            updatePosition.setFillAfter(true);
            holder.mTaskItem.startAnimation(updatePosition);
            updatePosition.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Animation floating = new TranslateAnimation(0,0,currentTask.getPosition(),(currentTask.getPosition()+35));
                            floating.setRepeatCount(Animation.INFINITE);
                            floating.setRepeatMode(Animation.REVERSE);
                            floating.setDuration(500);
                            holder.mTaskItem.startAnimation(floating);
                        }
                    }, 400 * position);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


        } else {

            holder.mTaskText.setText(R.string.no_task_in_category);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation floating = new TranslateAnimation(0, 0, 0, 35);
                    floating.setRepeatCount(Animation.INFINITE);
                    floating.setRepeatMode(Animation.REVERSE);
                    floating.setDuration(500);
                    holder.mTaskItem.startAnimation(floating);
                }
            }, 400 * position);

        }



    }

    @Override
    public int getItemCount() {
        if (!mAllCategories.isEmpty()) {
            return mAllCategories.size();
        } else {
            return 0;
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public FrameLayout mTaskItem;
        public TextView mTaskText;
        public TextView mCategoryLabel;
        public FloatingActionButton deleteFab;
        public Task currentTask;





        public ViewHolder(View itemView) {
            super(itemView);
            mTaskText = (TextView) itemView.findViewById(R.id.taskText);
            mCategoryLabel = (TextView) itemView.findViewById(R.id.categoryLabel);
            mTaskItem = (FrameLayout) itemView.findViewById(R.id.taskItem);
            deleteFab = (FloatingActionButton) itemView.findViewById(R.id.deleteTask);
            mTaskItem.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            TaskDialog dialog = new TaskDialog(mContext,currentTask);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
        }


        public class TaskDialog extends AlertDialog implements View.OnClickListener
        {
            public FloatingActionButton deleteFab;
            public FloatingActionButton confirmFab;

            public Context mContext;
            public Task selectedTask;

            public TaskDialog(Context context, Task currentTask) {
                super(context);
                mContext = context;
                selectedTask = currentTask;
            }

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                setContentView(R.layout.task_edit);
                deleteFab = (FloatingActionButton)findViewById(R.id.deleteTask);
                confirmFab = (FloatingActionButton)findViewById(R.id.confirmTask);


                deleteFab.setOnClickListener(this);
                confirmFab.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                String whereClause = TaskContract.TaskEntry.COLUMN_TASK_NAME+"=?";
                String [] whereArgs = {mTaskText.getText().toString()};
                mContext.getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, whereClause, whereArgs);
                Log.v("THING", "DELETED");
                notifyDataSetChanged();
               dismiss();
            }
        }

    }


    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mTaskCursor) {
            return null;
        }
        final Cursor oldCursor = mTaskCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mTaskCursor = newCursor;
        if (mTaskCursor != null) {
            if (mDataSetObserver != null) {
                mTaskCursor.registerDataSetObserver(mDataSetObserver);
            }
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }


    public ArrayList<String> getTaskCategories(Cursor taskCursor, int position){
        taskCursor.moveToPosition(position);
        String encodedCategory = taskCursor.getString(taskCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_CATEGORY));
        String replace = encodedCategory.replace("[", "");
        String replace1 = replace.replace("]", "");
        return new ArrayList<>(Arrays.asList(replace1.split(", ")));
    }

}
