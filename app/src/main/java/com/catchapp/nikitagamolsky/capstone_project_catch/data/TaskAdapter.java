package com.catchapp.nikitagamolsky.capstone_project_catch.data;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.catchapp.nikitagamolsky.capstone_project_catch.R;
import com.catchapp.nikitagamolsky.capstone_project_catch.Task;
import com.catchapp.nikitagamolsky.capstone_project_catch.TaskManagerActivity;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {



    private Context mContext;
    private ArrayList<String> mAllCategories;
    private ArrayList<Task> mAllTasks;

    public TaskAdapter (Context context, ArrayList<String> allCategories, ArrayList<Task> allTasks) {
        mContext = context;
        mAllCategories = allCategories;
        mAllTasks = allTasks;
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
        int index = 0;
        holder.mCategoryLabel.setText(mAllCategories.get(position));
        String currentCategory = mAllCategories.get(position);
        for (Task task : mAllTasks) {
                if (task.getCategories().contains(currentCategory)) {
                    if (task.getPriority() > topPriority) {
                        topPriority = task.getPriority();
                        indexBestTask = index;
                    }
                }
                index += 1;
            }

        if (indexBestTask != -1) {
            Task currentTask = mAllTasks.get(indexBestTask);
            currentTask.updatePosition(mContext);
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
                            Animation floating = AnimationUtils.loadAnimation(mContext, R.anim.floating);
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

        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation floating = AnimationUtils.loadAnimation(mContext, R.anim.floating);
                holder.mTaskItem.startAnimation(floating);
            }
        }, 400*position);

    }

    @Override
    public int getItemCount() {
        if (mAllCategories.size() != 0) {
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
            TaskDialog dialog = new TaskDialog(mContext);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
        }


        public class TaskDialog extends AlertDialog implements View.OnClickListener
        {
            public FloatingActionButton deleteFab;
            public FloatingActionButton confirmFab;
            public EditText setPosition;
            public Context mContext;
            public TaskDialog(Context context) {
                super(context);
                mContext = context;
            }

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                setContentView(R.layout.task_edit);
                deleteFab = (FloatingActionButton)findViewById(R.id.deleteTask);
                confirmFab = (FloatingActionButton)findViewById(R.id.confirmTask);
                setPosition = (EditText) findViewById(R.id.positionEdit);
                setPosition.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        return false;
                    }
                });
                deleteFab.setOnClickListener(this);
                confirmFab.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                String whereClause = TaskContract.TaskEntry.COLUMN_TASK_NAME+"=?";
                String [] whereArgs = {mTaskText.getText().toString()};
                mContext.getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, whereClause, whereArgs);
                mContext.startActivity(new Intent(mContext, TaskManagerActivity.class));

            }
        }

    }

}
