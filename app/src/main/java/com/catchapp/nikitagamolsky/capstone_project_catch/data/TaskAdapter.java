package com.catchapp.nikitagamolsky.capstone_project_catch.data;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.catchapp.nikitagamolsky.capstone_project_catch.R;
import com.catchapp.nikitagamolsky.capstone_project_catch.Task;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {



    private Context mContext;
    private ArrayList<String> mAllCategories;
    private ArrayList<Task> mAllTasks;
    private ArrayList<Task> mCompetingTasks;

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
        mCompetingTasks = new ArrayList<>();

        String currentCategory = mAllCategories.get(position);
               for(Task task: mAllTasks){
                   if(task.getCategories().contains(currentCategory)){
                       mCompetingTasks.add(task);
                       if (task.getPriority()>topPriority){
                           topPriority = task.getPriority();
                           indexBestTask = index;
                       }
                   }
                   index +=1 ;
               }

        holder.mCategoryLabel.setText(mAllCategories.get(position));
        holder.mTaskText.setText(mAllTasks.get(indexBestTask).getTitle());

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
        public TextView mTaskText;
        public TextView mCategoryLabel;



        public ViewHolder(View itemView) {
            super(itemView);
            mTaskText = (TextView) itemView.findViewById(R.id.taskText);
            mCategoryLabel = (TextView) itemView.findViewById(R.id.categoryLabel);

        }


        @Override
        public void onClick(View v) {


        }
    }








}
