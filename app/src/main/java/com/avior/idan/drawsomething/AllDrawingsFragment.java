package com.avior.idan.drawsomething;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AllDrawingsFragment - a Fragment containing a list of all drawings uploaded by users, with the exception of drawings uploaded by the logged in user.
 * The Fragment extracts a list of all the users from the database, and then converts that list into a list of Invitations. The Invitation list is then
 * passed into an InvitationAdapter to populate the ListView with the required components.
 *
 * Clicking on a list object will open a dialog box from which a GuessDrawingFragment can be started.
 *
 * Though it is inaccurate to refer to the list's objects as 'invitations', the Invitation class contains the all the information needed to populate the list
 * and is therefore used in this case.
 */
public class AllDrawingsFragment extends Fragment {

    private ListView listView;
    private DatabaseReference dbRef;
    private boolean done;

    public AllDrawingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_drawings, container, false);
        initReferences(view);
        return view;
    }

    private void initReferences(View view){
        done = false;
        listView = (ListView) view.findViewById(R.id.allDrawingsListView);
        dbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> userList = new ArrayList();
                GenericTypeIndicator<Map<String, User>> genericTypeIndicator = new GenericTypeIndicator<Map<String, User>>() {};
                Map<String, User> map = dataSnapshot.getValue(genericTypeIndicator);
                String currentUser = MainActivity.getModifiedEmailAddress(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                User user = new User();
                for (String key : map.keySet()) {
                    if (!key.equals(currentUser))
                        // All users except the logged in user will be added to the list
                        userList.add(map.get(key));
                    else
                        // The logged in user will be retrieved as well, because the user's list of viewed drawings is needed
                        user = map.get(key);
                }
                List<Invitation> invitationList = generateInvitationsList(userList);
                List<Invitation> previouslyViewedDrawings = user.getPreviouslyViewedDrawings();
                // The lists are compared to determine which drawings were already viewed by the user
                for (Invitation invitation : invitationList)
                    for (Invitation viewedDrawing : previouslyViewedDrawings)
                        if (invitation.equals(viewedDrawing))
                            invitation.setAsViewedByUser();
                final InvitationAdapter adapter = new InvitationAdapter(getActivity(), R.layout.invitations_list_item, invitationList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        AlertDialog.Builder guessDrawingDialog = new AlertDialog.Builder(getActivity());
                        guessDrawingDialog.setTitle("Guess drawing");
                        guessDrawingDialog.setMessage("Guess this drawing?");
                        guessDrawingDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Invitation invitation = adapter.getItem(position);
                                addDrawingToPreviouslyViewedList(invitation);
                                FragmentManager manager = getFragmentManager();
                                Fragment fragment = GuessDrawingFragment.newInstance(invitation.getDrawingReferenceName(), invitation.getDrawingCreator(), invitation.getDrawingDescription());
                                manager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
                            }
                        });
                        guessDrawingDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

    private List<Invitation> generateInvitationsList(List<User> userList){
        List<Invitation> allInvitations = new ArrayList<>();
        for (User user : userList){
            List<Drawing> drawingList = user.getDrawings();
            for (Drawing drawing : drawingList)
                allInvitations.add(new Invitation(drawing.getImageReference(), MainActivity.getModifiedEmailAddress(user.getEmail()), drawing.getDescription()));
        }
        return allInvitations;
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

}
