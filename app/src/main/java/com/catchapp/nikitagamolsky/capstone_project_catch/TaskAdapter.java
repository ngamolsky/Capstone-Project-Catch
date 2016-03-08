package com.catchapp.nikitagamolsky.capstone_project_catch;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.catchapp.nikitagamolsky.capstone_project_catch.data.TaskContract;

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
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int topPriority = 0;
        int indexBestTask = -1;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        final float density = displaymetrics.density;
        final float ydpi = height/density;
        String currentCategory = mAllCategories.get(position);
        holder.mCategoryLabel.setText(currentCategory);
        if (mDataValid) {
            mTaskCursor.moveToPosition(-1);
            while (mTaskCursor.moveToNext()) {
                ArrayList<String> taskCategories = getTaskCategories(mTaskCursor, mTaskCursor.getPosition());
                int currentPriority = Integer.parseInt(mTaskCursor.getString(mTaskCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY)));
                if (taskCategories.contains(currentCategory)) {
                    if (currentPriority > topPriority) {
                        topPriority = currentPriority;
                        indexBestTask = mTaskCursor.getPosition();
                    }
                }
            }




            if (indexBestTask != -1) {
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
                final Task currentTask = new Task(taskTitle, taskCategories, taskPriority);
                holder.currentTask = currentTask;
                currentTask.setDateEntered(dateEntered);
                currentTask.updatePosition();
                holder.mTaskText.setText(currentTask.getTitle());
                ObjectAnimator updatePosition = ObjectAnimator.ofFloat(holder.mTaskItem,"translationY",currentTask.getPosition()*density);
                updatePosition.setDuration(1000);
                updatePosition.start();
                updatePosition.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ObjectAnimator floating = ObjectAnimator.ofFloat(holder.mTaskItem, "translationY", currentTask.getPosition()*density, currentTask.getPosition()*density + 15);
                        floating.setRepeatCount(Animation.INFINITE);
                        floating.setRepeatMode(Animation.REVERSE);
                        floating.setDuration(500);
                        floating.setStartDelay(200 * position);
                        floating.start();

                        if(holder.currentTask.getPosition()>(ydpi-200)){
                            holder.poppedBalloon.setVisibility(View.VISIBLE);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    holder.poppedBalloon.setVisibility(View.GONE);
                                    String whereClause = TaskContract.TaskEntry.COLUMN_TASK_NAME+"=?";
                                    String [] whereArgs = {holder.currentTask.getTitle()};
                                    mContext.getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, whereClause, whereArgs);
                                }
                            }, 1000);
                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });


                Log.v("HEIGHT", holder.currentTask.getPosition()+"");
                if(holder.currentTask.getPosition()>(ydpi-280)){
                    android.support.v4.app.NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(mContext)
                                    .setSmallIcon(R.drawable.ic_nikita_balloon_no_string_accent)
                                    .setContentTitle(holder.currentTask.getTitle())
                                    .setContentText("This Task is About to Expire!!");
                    Intent resultIntent = new Intent(mContext, TaskManagerActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
                    stackBuilder.addParentStack(TaskManagerActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, mBuilder.build());
                }




            } else {
                holder.currentTask = new Task("No Task", null,0);
                holder.currentTask.setPosition(0);
                holder.mTaskText.setText(R.string.no_task_in_category);
                ObjectAnimator floating = ObjectAnimator.ofFloat(holder.mTaskItem,"translationY",0,15);
                floating.setRepeatCount(Animation.INFINITE);
                floating.setRepeatMode(Animation.REVERSE);
                floating.setDuration(500);
                floating.setStartDelay(200 * position);
                floating.start();

            }


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
        public ImageView poppedBalloon;
        public ViewHolder(View itemView) {
            super(itemView);
            mTaskText = (TextView) itemView.findViewById(R.id.taskText);
            mCategoryLabel = (TextView) itemView.findViewById(R.id.categoryLabel);
            mTaskItem = (FrameLayout) itemView.findViewById(R.id.taskItem);
            deleteFab = (FloatingActionButton) itemView.findViewById(R.id.deleteFab);
            mTaskItem.setOnClickListener(this);
            poppedBalloon = (ImageView)itemView.findViewById(R.id.poppedBalloon);
        }


        @Override
        public void onClick(View v) {
            if(!mTaskText.getText().toString().equals(mContext.getString(R.string.no_task_in_category))) {
                deleteFab.setVisibility(View.VISIBLE);
                deleteFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String whereClause = TaskContract.TaskEntry.COLUMN_TASK_NAME+"=?";
                        String [] whereArgs = {mTaskText.getText().toString()};
                        mContext.getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI, whereClause, whereArgs);
                        deleteFab.setVisibility(View.GONE);
                        notifyDataSetChanged();
                    }
                });
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
