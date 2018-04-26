package elegion.com.secondappforcontentprovidertest;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Button mGoButton;
    private Spinner mTableSpinner;
    private Spinner mRequestTypeSpinner;
    private EditText mIdEditText;
    private EditText mNewData01EditText;
    private EditText mNewData02EditText;
    private EditText mNewData03EditText;
    private static final String CONTENT_PROVIDER_URI = "content://com.elegion.roomdatabase.musicprovider/";
    private static final int ID_PARSE_ERROR = 1;
    private static final int ID_PARSE_EMPTY = 2;
    private static final int ID_PARSE_OK = 3;

    private static final int TYPE_CONTENT_VALUES_ALBUM = 1;
    private static final int TYPE_CONTENT_VALUES_SONG = 2;
    private static final int TYPE_CONTENT_VALUES_ALBUMSONG = 3;

    private static final int METHOD_CONTENT_VALUES_INSERT = 1;
    private static final int METHOD_CONTENT_VALUES_UPDATE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoButton = findViewById(R.id.bt_go);
        mTableSpinner = findViewById(R.id.sp_table);
        mRequestTypeSpinner = findViewById(R.id.sp_types);
        mIdEditText = findViewById(R.id.et_id);
        mNewData01EditText = findViewById(R.id.et_new_data01);
        mNewData02EditText = findViewById(R.id.et_new_data02);
        mNewData03EditText = findViewById(R.id.et_new_data03);



    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Uri.parse("content://com.elegion.roomdatabase.musicprovider/album"),
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            StringBuilder builder = new StringBuilder();
            do {
                builder.append(data.getString(data.getColumnIndex("name"))).append("\n");
            } while (data.moveToNext());
            Toast.makeText(this, builder.toString(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    protected void onDestroy() {
        mGoButton = null;
        mTableSpinner = null;
        mRequestTypeSpinner = null;
        mIdEditText = null;
        mNewData01EditText = null;
        mNewData02EditText = null;
        mNewData03EditText = null;
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName;
                Uri uri;
                Cursor cursor;
                int id = -1;
                int idParseResult = ID_PARSE_OK;
                int contentCode;


                // parse ID
                if (!mRequestTypeSpinner.getSelectedItem().toString().equals("insert")) {
                    if (mIdEditText.getText().toString().isEmpty()) idParseResult = ID_PARSE_EMPTY;
                    else {

                        id = editTextValueToInt(mIdEditText);

                        if (id != -1) idParseResult = ID_PARSE_OK;
                        else idParseResult = ID_PARSE_ERROR;
                    }
                }


                //
                if (mTableSpinner.getSelectedItem().toString().equals("Albums")) {
                    tableName = "album";
                    contentCode = TYPE_CONTENT_VALUES_ALBUM;
                } else {
                    if(mTableSpinner.getSelectedItem().toString().equals("Songs")) {
                        tableName = "song";
                        contentCode = TYPE_CONTENT_VALUES_SONG;
                    } else {
                        tableName = "albumsong";
                        contentCode = TYPE_CONTENT_VALUES_ALBUMSONG;
                    }
                }

                if (idParseResult != ID_PARSE_ERROR) {

                    // **********************************************************************
                    // query

                    if (mRequestTypeSpinner.getSelectedItem().toString().equals("query")) {
                        if (idParseResult == ID_PARSE_EMPTY) {
                            uri = Uri.parse(CONTENT_PROVIDER_URI + tableName);
                        } else {
                            uri = Uri.parse(CONTENT_PROVIDER_URI + tableName + "/" + id);
                        }
                        cursor = null;
                        try {
                            cursor = getContentResolver().query(uri,
                                    null,
                                    null,
                                    null,
                                    null);
                        } catch (Throwable t) {
                            Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        }
                        if (cursor != null) {
                            if (cursor.moveToFirst())
                                Toast.makeText(MainActivity.this, cursorToString(cursor), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(MainActivity.this, R.string.id_error, Toast.LENGTH_SHORT).show();
                            cursor.close();
                        }
                    }


                    // **********************************************************************
                    // update
                    if (mRequestTypeSpinner.getSelectedItem().toString().equals("update")) {
                        if (idParseResult == ID_PARSE_EMPTY) {
                            Toast.makeText(MainActivity.this, R.string.id_error, Toast.LENGTH_SHORT).show();
                        } else {
                            uri = Uri.parse(CONTENT_PROVIDER_URI + tableName + "/" + id);
                            ContentValues contentValues = getContentValues(contentCode, METHOD_CONTENT_VALUES_UPDATE);

                            if(contentValues != null) {
                               try {
                                   if(getContentResolver().update(uri, contentValues, null, null) == 1) {
                                       Toast.makeText(MainActivity.this, R.string.complete, Toast.LENGTH_SHORT).show();
                                   } else {
                                       Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                                   }
                               } catch (Throwable t) {
                                   Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                               }
                            }
                        }
                    }

                    // **********************************************************************
                    // delete
                    if (mRequestTypeSpinner.getSelectedItem().toString().equals("delete")) {
                        if (idParseResult == ID_PARSE_EMPTY) {
                            Toast.makeText(MainActivity.this, R.string.id_error, Toast.LENGTH_SHORT).show();
                        } else {
                            uri = Uri.parse(CONTENT_PROVIDER_URI + tableName + "/" + id);
                            try {
                            if (getContentResolver().delete(uri, null, null) == 1) {
                                Toast.makeText(MainActivity.this, R.string.complete, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                            }
                            } catch (Throwable t) {
                                Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                }

                // **********************************************************************
                // insert
                if (mRequestTypeSpinner.getSelectedItem().toString().equals("insert")) {
                        uri = Uri.parse(CONTENT_PROVIDER_URI + tableName);
                        ContentValues contentValues = getContentValues(contentCode, METHOD_CONTENT_VALUES_INSERT);
                        if(contentValues != null) {
                            try {
                                getContentResolver().insert(uri, contentValues);
                                Toast.makeText(MainActivity.this, R.string.complete, Toast.LENGTH_SHORT).show();
                            } catch (Throwable t) {
                                Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                            }

                        }
                }

            }
        });
    }

    private String cursorToString(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            StringBuilder builder = new StringBuilder();
            int col_count = cursor.getColumnCount();

            do {
                for (int i = 0; i < col_count; i++) {
                    builder.append(cursor.getColumnName(i))
                            .append("=")
                            .append(cursor.getString(i))
                            .append(i < col_count-1 ? ", " : "\n-----------\n");
                }

            } while (cursor.moveToNext());
            return builder.toString();
        } else return "";
    }

    private ContentValues getContentValues(int contentType, int method) {
        int id;

        ContentValues contentValues = new ContentValues();

        if (method == METHOD_CONTENT_VALUES_INSERT && contentType != TYPE_CONTENT_VALUES_ALBUMSONG) {
            id = editTextValueToInt(mNewData01EditText);
            if (id != -1) contentValues.put("id", id);
            else return null;
        }


        switch (contentType)
        {
            case TYPE_CONTENT_VALUES_ALBUM: {
                contentValues.put("name", mNewData02EditText.getText().toString());
                contentValues.put("release", mNewData03EditText.getText().toString());
                break;
            }
            case TYPE_CONTENT_VALUES_SONG: {
                contentValues.put("name", mNewData02EditText.getText().toString());
                contentValues.put("duration", mNewData03EditText.getText().toString());
                break;
            }
            case TYPE_CONTENT_VALUES_ALBUMSONG: {
                int id1 = editTextValueToInt(mNewData02EditText);
                int id2 = editTextValueToInt(mNewData03EditText);
                if (id1 != -1 && id2 != -1) {
                    contentValues.put("album_id", id1);
                    contentValues.put("song_id", id2);
                }
                break;
            }
        }
        return contentValues;
    }

    private int editTextValueToInt(EditText editText) {
        int ret;

        try {
            ret = Integer.valueOf(editText.getText().toString());
            return ret;
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.id_error, Toast.LENGTH_SHORT).show();
            return -1;
        }

    }
}
