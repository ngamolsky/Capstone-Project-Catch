package com.catchapp.nikitagamolsky.capstone_project_catch.data;


import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.catchapp.nikitagamolsky.capstone_project_catch.InputTaskActivity;
import com.catchapp.nikitagamolsky.capstone_project_catch.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Cursor mCategoryCursor;
    private boolean mDataValid;
    private DataSetObserver mDataSetObserver;
    private Context mContext;



    public CategoryAdapter(Cursor data, Context context) {
        mContext = context;
        mCategoryCursor = data;
        mDataValid = data != null;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCategoryCursor != null) {
            mCategoryCursor.registerDataSetObserver(mDataSetObserver);
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDataValid) {
            if(position<mCategoryCursor.getCount()) {
                mCategoryCursor.moveToPosition(position);
                String currentCategory = mCategoryCursor.getString(mCategoryCursor.getColumnIndex(TaskContract.CategoryEntry.COLUMN_CATEGORY));
                holder.mCategoryText.setText(currentCategory);
            } else {
                holder.mCategoryText.setText(R.string.add_category);

            }
        }
    }

    @Override
    public int getItemCount() {
        if (mCategoryCursor != null) {
            return mCategoryCursor.getCount()+1;
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mCategoryText;
        public FrameLayout mCategoryLayout;
        public boolean selected;

        public ViewHolder(View itemView) {
            super(itemView);
            mCategoryText = (TextView) itemView.findViewById(R.id.categoryText);
            mCategoryLayout = (FrameLayout) itemView.findViewById(R.id.categoryLayout);
            mCategoryLayout.setOnClickListener(this);
            selected = false;
        }


        @Override
        public void onClick(View v) {
        if(!selected){
            if(!mCategoryText.getText().toString().equals("Add Category")) {
                selected = true;
                mCategoryLayout.setBackgroundResource(R.drawable.rounded_corners_selected);
                mCategoryText.setTextColor(Color.WHITE);
                ((InputTaskActivity) mContext).addCategory(mCategoryText.getText().toString());

            } else{
                mCategoryLayout.setBackgroundResource(R.drawable.rounded_corners_selected);
                mCategoryText.setTextColor(Color.WHITE);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCategoryLayout.setBackgroundResource(R.drawable.rounded_corners);
                        mCategoryText.setTextColor(Color.BLACK);
                    }
                }, 300);

                ((InputTaskActivity) mContext).showEditCategory();
                ((InputTaskActivity) mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        } else{
            if(!mCategoryText.getText().toString().equals("Add Category")) {
                selected = false;
                mCategoryLayout.setBackgroundResource(R.drawable.rounded_corners);
                mCategoryText.setTextColor(Color.BLACK);
                ((InputTaskActivity) mContext).removeCategory(mCategoryText.getText().toString());
            }
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
        if (newCursor == mCategoryCursor) {
            return null;
        }
        final Cursor oldCursor = mCategoryCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCategoryCursor = newCursor;
        if (mCategoryCursor != null) {
            if (mDataSetObserver != null) {
                mCategoryCursor.registerDataSetObserver(mDataSetObserver);
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




}
