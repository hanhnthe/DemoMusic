package com.example.hanh29_10.sqlite;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class FavoriteSongProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.hanh29_10.sqlite.FavoriteSongProvider";
    public static final int SONG_TABLE = 100;
    public static final int SONG_ID = 110;

    private static final String SONG_BASE_PATH = "songFavorite";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + SONG_BASE_PATH);

    private SongsFavoriteTable mSongsFavorite;
    private SQLiteDatabase mData;


    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, SONG_BASE_PATH, SONG_TABLE);
        sURIMatcher.addURI(AUTHORITY, SONG_BASE_PATH + "/#", SONG_ID);
    }

    @Override
    public boolean onCreate() {
        mSongsFavorite = new SongsFavoriteTable(getContext(), null, null, 1);
        mData = mSongsFavorite.getWritableDatabase();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(SongsFavoriteTable.TABLE_NAME);
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case SONG_ID:
                queryBuilder.appendWhere(SongsFavoriteTable.ID_PROVIDER + "="
                        + uri.getLastPathSegment());
                break;
            case SONG_TABLE:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        Cursor cursor = queryBuilder.query(mSongsFavorite.getReadableDatabase(), projection,
                selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        long id = 0;
        switch (uriType) {
            case SONG_TABLE:
                id = mData.insert(SongsFavoriteTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(SONG_BASE_PATH + "/" + id);
//        //them 1 bai hat moi
//        long rowID = mData.insert(mSongsFavorite.TABLE_NAME, null, values);
//        //neu 1 ban ghi dc them thanh cong
//
//        if (rowID > 0) {
//            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
//            getContext().getContentResolver().notifyChange(_uri, null);
//            return _uri;
//        }
//        throw new SQLException("failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted = 0;
        switch (uriType) {
            case SONG_TABLE:
                rowsDeleted = mData.delete(SongsFavoriteTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case SONG_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = mData.delete(SongsFavoriteTable.TABLE_NAME,
                            mSongsFavorite.ID_PROVIDER + "=" + id, null);
                } else {
                    rowsDeleted = mData.delete(SongsFavoriteTable.TABLE_NAME,
                            mSongsFavorite.ID_PROVIDER + "=" + id + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsUpdated = 0;
        switch (uriType) {
            case SONG_TABLE:
                rowsUpdated = mData.update(SongsFavoriteTable.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case SONG_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = mData.update(SongsFavoriteTable.TABLE_NAME,
                            values, SongsFavoriteTable.ID_PROVIDER + "=" + id, null);
                } else {
                    rowsUpdated = mData.update(SongsFavoriteTable.TABLE_NAME,
                            values, SongsFavoriteTable.ID_PROVIDER + "=" + id + " and "
                                    + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
