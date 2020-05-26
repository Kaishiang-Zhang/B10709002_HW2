package com.example.b10709002_hw2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    public static   GuestListAdapter mAdapter;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView waitlistRecyclerView;
        waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.guest_rev);
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        Cursor cusor = getAllGuests();
        mAdapter = new GuestListAdapter(this,cusor);
        waitlistRecyclerView.setAdapter(mAdapter);
        setupSharedPreferences();
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            // COMPLETED (4) Override onMove and simply return false inside
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return false;
            }


            // COMPLETED (5) Override onSwiped
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                new AlertDialog.Builder(MainActivity.this).setTitle(R.string.warning).setMessage(R.string.warningcheck)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // COMPLETED (8) Inside, get the viewHolder's itemView's tag and store in a long variable id
                                //get the id of the item being swiped
                                long id = (long) viewHolder.itemView.getTag();
                                Log.d(LOG_TAG, String.valueOf(id));
                                // COMPLETED (9) call removeGuest and pass through that id
                                //remove from DB
                                removeGuest(id);
                                // COMPLETED (10) call swapCursor on mAdapter passing in getAllGuests() as the argument
                                //update the list
                                mAdapter.swapCursor(getAllGuests());
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        }).create().show();
            }
            //COMPLETED (11) attach the ItemTouchHelper to the waitlistRecyclerView
        }).attachToRecyclerView(waitlistRecyclerView);

    }


    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.guest_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.guest_add:
                Intent intent = new Intent(MainActivity.this,GuestaddActivity.class);
                startActivity(intent);

                Toast.makeText(this,"add被觸及",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.guest_setting:
                Intent intent2 = new Intent(MainActivity.this,GuestsettingActivity.class);
                startActivity(intent2);

                Toast.makeText(this,"setting被觸及",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private boolean removeGuest(long id) {
        // COMPLETED (2) Inside, call mDb.delete to pass in the TABLE_NAME and the condition that WaitlistEntry._ID equals id
        return mDb.delete(WaitlistContract.WaitlistEntry.TABLE_NAME, WaitlistContract.WaitlistEntry._ID + "=" + id, null) > 0;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
