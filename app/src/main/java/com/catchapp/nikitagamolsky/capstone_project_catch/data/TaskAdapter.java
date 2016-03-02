package com.catchapp.nikitagamolsky.capstone_project_catch.data;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.catchapp.nikitagamolsky.capstone_project_catch.R;
import com.catchapp.nikitagamolsky.capstone_project_catch.Task;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        int topPriority = 0;
        int indexBestTask = 0;
        int index = 0;

        String currentCategory = mAllCategories.get(position);
               for(Task task: mAllTasks){
                   if(task.getCategories().contains(currentCategory)){
                       if (task.getPriority()>topPriority){
                           topPriority = task.getPriority();
                           indexBestTask = index;
                       }
                   }
                   index +=1 ;
               }
        Task currentTask = mAllTasks.get(indexBestTask);
        currentTask.updatePosition(mContext);
        holder.mCategoryLabel.setText(mAllCategories.get(position));
        holder.mTaskText.setText(currentTask.getTitle());
        Animation updatePosition = new TranslateAnimation(0,0,0,currentTask.getPosition());
        updatePosition.setDuration(1000);
        updatePosition.setFillAfter(true);
        holder.mTaskText.startAnimation(updatePosition);

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
        public TextView mTaskText;
        public TextView mCategoryLabel;
        public LinearLayout mTaskLayout;



        public ViewHolder(View itemView) {
            super(itemView);
            mTaskText = (TextView) itemView.findViewById(R.id.taskText);
            mCategoryLabel = (TextView) itemView.findViewById(R.id.categoryLabel);
            mTaskLayout = (LinearLayout) itemView.findViewById(R.id.taskLayout);
            mTaskLayout.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(R.layout.task_edit);
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            
        }
    }








}
