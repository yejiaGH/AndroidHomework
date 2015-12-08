package com.yejia.listviewfreshload;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yejiapc on 15/12/8.
 */
public class PostsDb extends SQLiteOpenHelper {


    public PostsDb(Context context) {
        super(context, "postdb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE post("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "title TEXT DEFAULT \"\"," +
                "date TEXT DEFAULT \"\"," +
                "content TEXT DEFAULT \"\")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
