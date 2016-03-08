package com.catchapp.nikitagamolsky.capstone_project_catch;


import android.animation.ObjectAnimator;
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
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.catchapp.nikitagamolsky.capstone_project_catch.data.TaskContract;

import java.util.Random;

// The Adapter responsible for loading Categories into the Input Task Activity
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
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mDataValid) {
            mCategoryCursor.moveToFirst();

            //Load All Categories
            if(position<mCategoryCursor.getCount()) {
                mCategoryCursor.moveToPosition(position);
                String currentCategory = mCategoryCursor.getString(mCategoryCursor.getColumnIndex(TaskContract.CategoryEntry.COLUMN_CATEGORY));
                holder.mCategoryText.setText(currentCategory);

            //Add Extra Item for Adding Categories
            } else {
                holder.mCategoryText.setText(R.string.add_category);

            }

            //Floating Category Balloons Animation, with staggered start delays
            ObjectAnimator floating = ObjectAnimator.ofFloat(holder.mCategoryLayout, "translationY", 0, 15);
            floating.setRepeatCount(Animation.INFINITE);
            floating.setRepeatMode(Animation.REVERSE);
            floating.setDuration(500);
            Random r = new Random();
            long number = ((long)(r.nextDouble()*(8)));
            floating.setStartDelay(number*200);
            floating.start();
        }
    }

    //Modified to allow for the "Add Category" slot
    @Override
    public int getItemCount() {
        if (mCategoryCursor != null) {
            return mCategoryCursor.getCount()+1;
        } else {
            return 0;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView mCategoryText;
        public FrameLayout mCategoryLayout;
        public ImageView mBalloonView;
        public boolean selected;

        public ViewHolder(View itemView) {
            super(itemView);
            mCategoryText = (TextView) itemView.findViewById(R.id.categoryText);
            mCategoryLayout = (FrameLayout) itemView.findViewById(R.id.categoryLayout);
            mBalloonView = (ImageView)itemView.findViewById(R.id.balloonImage);
            mCategoryLayout.setOnClickListener(this);
            mCategoryLayout.setOnLongClickListener(this);
            selected = false;
        }

        // Provides interface for adding categories to Task
        @Override
        public void onClick(View v) {
        if(!selected){
            if(!mCategoryText.getText().toString().equals("Add Category")) {// If item represents actual category
                selected = true;
                mBalloonView.setImageResource(R.drawable.ic_nikita_balloon_no_string_accent);
                mCategoryText.setTextColor(Color.WHITE);
                ((InputTaskActivity) mContext).addCategory(mCategoryText.getText().toString()); // Adds Category to Task

            } else{
                mBalloonView.setImageResource(R.drawable.ic_nikita_balloon_no_string_accent);
                mCategoryText.setTextColor(Color.WHITE);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBalloonView.setImageResource(R.drawable.ic_nikita_balloon_no_string_black);
                        mCategoryText.setTextColor(Color.BLACK);
                    }
                }, 300);

                ((InputTaskActivity) mContext).showEditCategory();
                ((InputTaskActivity) mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        } else{
                selected = false;
                mBalloonView.setImageResource(R.drawable.ic_nikita_balloon_no_string_black);
                mCategoryText.setTextColor(Color.BLACK);
                ((InputTaskActivity) mContext).removeCategory(mCategoryText.getText().toString()); //Removes Category
        }
        }

        @Override
        public boolean onLongClick(View v) {

            final String whereClause = TaskContract.CategoryEntry.COLUMN_CATEGORY+"=?";
            final String [] whereArgs = {mCategoryText.getText().toString()};
            mBalloonView.setImageResource(R.drawable.ic_balloon_pop);
            mCategoryText.setText("");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mContext.getContentResolver().delete(TaskContract.CategoryEntry.CONTENT_URI,whereClause,whereArgs);
                    notifyDataSetChanged();
                }
            }, 300);
            return true;
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
