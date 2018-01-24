package com.moviestoremb.musabbozkurt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBConnector
{
    private static final String DATABASE_NAME = "UserMovies";
    private SQLiteDatabase database;
    private DatabaseOpenHelper databaseOpenHelper;

    public DBConnector(Context context)
    {
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    public void open() throws SQLException
    {
        database = databaseOpenHelper.getWritableDatabase();
    }

    public void close()
    {
        if (database != null)
            database.close();
    }

    public void insertMovie(String title, String director, String releaseYear)
    {
        ContentValues newMovie = new ContentValues();
        newMovie.put("title", title);
        newMovie.put("director", director);
        newMovie.put("releaseYear", releaseYear);

        open();
        database.insert("movies", null, newMovie);
        close();
    }

    public void updateMovie(long id, String title, String director, String releaseYear)
    {
        ContentValues editMovie = new ContentValues();
        editMovie.put("title", title);
        editMovie.put("director", director);
        editMovie.put("releaseYear", releaseYear);

        open();
        database.update("movies", editMovie, "_id=" + id, null);
        close();
    }

    public Cursor getAllMovies()
    {
        return database.query("movies", new String[] {"_id", "title"},
                null, null, null, null, "title");
    }

    public Cursor getOneMovie(long id)
    {
        return database.query("movies", null, "_id=" + id, null, null, null, null);
    }

    public void deleteMovie(long id)
    {
        open();
        database.delete("movies", "_id=" + id, null);
        close();
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper
    {
        public DatabaseOpenHelper(Context context, String title,
                                  CursorFactory factory, int version)
        {
            super(context, title, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            String createQuery = "CREATE TABLE movies" +
                    "(_id integer primary key autoincrement," +
                    "title TEXT, " +
                    "director TEXT," +
                    "releaseYear TEXT);";

            db.execSQL(createQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        }
    }
}