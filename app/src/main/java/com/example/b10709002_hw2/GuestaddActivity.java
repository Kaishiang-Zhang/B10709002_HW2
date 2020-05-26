package com.example.b10709002_hw2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class GuestaddActivity extends AppCompatActivity {
    private EditText Guestname_edt;
    private EditText Waitnum_edt;
    private SQLiteDatabase mDb;
    private final static String LOG_TAG = GuestaddActivity.class.getSimpleName();
//    private GuestListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guestadd);
        Guestname_edt=(EditText)this.findViewById(R.id.name_edt);
        Waitnum_edt=(EditText)this.findViewById(R.id.count_edt);
        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        Button btn_ok = findViewById(R.id.but_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Guestname_edt.getText().length() == 0 ||
                        Waitnum_edt.getText().length() == 0) {
                    return;
                }
                int partySize = 1;
                try {
                    partySize = Integer.parseInt(Waitnum_edt.getText().toString());
                } catch (NumberFormatException ex) {
                    Log.e(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
                }
                addNewGuest(Guestname_edt.getText().toString(), partySize);


                MainActivity.mAdapter.swapCursor(getAllGuests());

                Waitnum_edt.clearFocus();
                Guestname_edt.getText().clear();
                Waitnum_edt.getText().clear();
                finish();
            }
        });
        Button btn_cancel = findViewById(R.id.but_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private long addNewGuest(String name, int partySize) {
        ContentValues cv = new ContentValues();
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);
        return mDb.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);
    }
    private Cursor getAllGuests() {
        return mDb.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
    }
}
