package ch.epfl.sweng.jassatepfl;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.jassatepfl.model.Match;
import ch.epfl.sweng.jassatepfl.tools.DatabaseUtils;
import ch.epfl.sweng.jassatepfl.tools.NotAFirebaseMatchListAdapter;

/**
 * Activity displaying matches as a scrolling list.
 * <br>
 * Clicking on a list item prompts the user to join the match.
 */
public class MatchListActivity extends BaseActivityWithNavDrawer
        implements OnItemClickListener {

    //private MatchListAdapter mAdapter;
    private BaseAdapter adapter;
    private List<Match> matches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_list);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_list, drawer, false);
        drawer.addView(contentView, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().hide();
        }

        ImageButton menuButton = (ImageButton) findViewById(R.id.list_menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        TextView emptyList = new TextView(this);
        emptyList.setText(R.string.list_empty_list);
        emptyList.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        emptyList.setTextColor(Color.BLACK);

        ListView listView = (ListView) findViewById(android.R.id.list);
        ((ViewGroup) listView.getParent()).addView(emptyList);
        listView.setEmptyView(emptyList);

        matches = new ArrayList<>();
        contactFirebase();
        adapter = new NotAFirebaseMatchListAdapter(this, matches);
        listView.setAdapter(adapter);

        //mAdapter = new MatchListAdapter(this);

        //listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        // Opens dialog box to ask user if he wants to join match
        // Allows user to cancel or accept
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_join_match)
                .setMessage(R.string.dialog_join_message)
                .setPositiveButton(R.string.dialog_join_confirmation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        //final Match match = mAdapter.getItem(position);
                        final Match match = (Match) adapter.getItem(position);

                        DatabaseUtils.addPlayerToMatch(MatchListActivity.this,
                                dbRefWrapped,
                                match.getMatchID(),
                                fAuth.getCurrentUser().getDisplayName(),
                                match);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing, goes back to MatchListActivity
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mAdapter.cleanup();
    }

    private void contactFirebase() {
        Query ref = dbRefWrapped.child("matches").child("privateMatch").equalTo(false);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Match match = dataSnapshot.getValue(Match.class);
                matches.add(match);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Match match = dataSnapshot.getValue(Match.class);
                matches.remove(match);
                matches.add(match);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Match match = dataSnapshot.getValue(Match.class);
                matches.remove(match);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
