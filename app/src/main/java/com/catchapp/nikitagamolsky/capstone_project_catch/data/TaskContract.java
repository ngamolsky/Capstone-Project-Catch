package com.catchapp.nikitagamolsky.capstone_project_catch.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class TaskContract {

    public static final String CONTENT_AUTHORITY = "com.catchapp.nikitagamolsky.capstone_project_catch";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TASKS = "tasks";

    public static final class TaskEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;

        public static Uri buildTaskUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "tasks";

        public static final String COLUMN_TASK_NAME = "title"; //Name of the Task
        public static final String COLUMN_TASK_CATEGORY = "category"; // Category of the Task
        public static final String COLUMN_URGENCY = "urgency"; // Urgency of the Task, Set in the Priority Matrix
        public static final String COLUMN_IMPORTANCE = "importance"; // Importance of the Task, Set in the Priority Matrix

    }
}
