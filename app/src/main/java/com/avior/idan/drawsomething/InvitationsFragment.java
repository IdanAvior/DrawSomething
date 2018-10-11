package com.avior.idan.drawsomething;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class InvitationsFragment extends Fragment {

    private ListView listView;
    private DatabaseReference dbRef;
    private boolean done;
    private TextView noNewInvitations;

    public InvitationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_invitations, container, false);
        initReferences(view);
        return view;
    }

    private void initReferences(View view){
        done = false;
        listView = (ListView) view.findViewById(R.id.invitationsListView);
        noNewInvitations = (TextView) view.findViewById(R.id.noNewInvitationsTV);
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(MainActivity.getModifiedEmailAddress(currentUser));
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                List<Invitation> invitations = user.getInvitations();
                if (invitations.size() == 0)
                    noNewInvitations.setVisibility(View.VISIBLE);
                List<Invitation> viewedByUser = user.getPreviouslyViewedDrawings();
                Log.d("TAG", "Viewed list size: " + viewedByUser.size());
                for (Invitation invitation : invitations)
                    for (Invitation userInvitation : viewedByUser) {
                        Log.d("TAG", "Comparing " + invitation.getDrawingDescription() + " with " + userInvitation.getDrawingDescription());
                        if (invitation.equals(userInvitation)) {
                            Log.d("TAG", "Setting drawing as viewed");
                            invitation.setAsViewedByUser();
                        }
                    }
                final InvitationAdapter adapter = new InvitationAdapter(getActivity(), R.layout.invitations_list_item, invitations);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                        AlertDialog.Builder guessDrawingDialog = new AlertDialog.Builder(getActivity());
                        guessDrawingDialog.setTitle("Invitation");
                        guessDrawingDialog.setMessage("What would you like to do?");
                        guessDrawingDialog.setPositiveButton("Guess drawing", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Invitation invitation = adapter.getItem(position);
                                addDrawingToPreviouslyViewedList(invitation);
                                FragmentManager manager = getFragmentManager();
                                Fragment fragment = GuessDrawingFragment.newInstance(invitation.getDrawingReferenceName(), invitation.getDrawingCreator(), invitation.getDrawingDescription());
                                manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
                            }
                        });
                        guessDrawingDialog.setNegativeButton("Remove invitation", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Invitation invitation = adapter.getItem(position);
                                removeInvitation(invitation);
                            }
                        });
                        guessDrawingDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        guessDrawingDialog.show();

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addDrawingToPreviouslyViewedList(final Invitation invitation){
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(MainActivity.getModifiedEmailAddress(userEmail));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!done) {
                    User user = dataSnapshot.getValue(User.class);
                    user.addPreviouslyViewedDrawing(invitation);
                    databaseReference.setValue(user);
                    done = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeInvitation(final Invitation invitation){
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(MainActivity.getModifiedEmailAddress(userEmail));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.removeInvitation(invitation);
                databaseReference.setValue(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
