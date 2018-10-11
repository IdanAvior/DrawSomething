package com.avior.idan.drawsomething;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SelectUserFragment extends Fragment {

    private static final String ARG_DRAWING_REFERENCE = "drawingReference";
    private static final String ARG_DRAWING_CREATOR = "drawingCreator";
    private static final String ARG_DRAWING_DESCRIPTION = "drawingDescription";

    private FirebaseDatabase database;
    private ListView listView;
    private User user;
    private boolean done;
    private Button cancelButton;

    private String drawingReference;
    private String drawingCreator;
    private String drawingDescription;
    //private List<User> userList;

//    private OnFragmentInteractionListener mListener;

    public SelectUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param drawingReference Parameter 1.
     * @param drawingCreator Parameter 2.
     * @return A new instance of fragment SelectUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectUserFragment newInstance(String drawingReference, String drawingCreator, String drawingDescription) {
        SelectUserFragment fragment = new SelectUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DRAWING_REFERENCE, drawingReference);
        args.putString(ARG_DRAWING_CREATOR, drawingCreator);
        args.putString(ARG_DRAWING_DESCRIPTION, drawingDescription);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_DRAWING_REFERENCE);
//            mParam2 = getArguments().getString(ARG_DRAWING_CREATOR);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_select_user, container, false);
        initReferences(view);
        return view;
    }

    private void initReferences(View view){
        if (getArguments() != null) {
            drawingReference = getArguments().getString(ARG_DRAWING_REFERENCE);
            drawingCreator = getArguments().getString(ARG_DRAWING_CREATOR);
            drawingDescription = getArguments().getString(ARG_DRAWING_DESCRIPTION);
        }
        database = FirebaseDatabase.getInstance();
        listView = (ListView) view.findViewById(R.id.selectUserListView);
        List<User> userList = new ArrayList<>();
        populateUserList(userList);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void populateUserList(final List<User> userList){
        final DatabaseReference databaseReference = database.getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, User>> genericTypeIndicator = new GenericTypeIndicator<Map<String, User>>() {};
                Map<String, User> map = dataSnapshot.getValue(genericTypeIndicator);
                userList.clear();
                for (String key : map.keySet())
                    if (!key.equals(getUserEmail()))
                        userList.add(map.get(key));
                final UserAdapter adapter = new UserAdapter(getActivity(), R.layout.select_user_list_item, userList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String username = adapter.getItem(position).getEmail();
                        final AlertDialog.Builder challengeDialog = new AlertDialog.Builder(getActivity());
                        challengeDialog.setTitle("Challenge user");
                        challengeDialog.setMessage("Challenge " + username + " to guess this drawing?");
                        challengeDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                done = false;
                                final DatabaseReference userReference = database.getReference("Users").child(MainActivity.getModifiedEmailAddress(username));
                                userReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        user = dataSnapshot.getValue(User.class);
                                        if (!done) {
                                            user.addInvitation(new Invitation(drawingReference, drawingCreator, drawingDescription));
                                            userReference.setValue(user);
                                            done = true;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                        challengeDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        challengeDialog.show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private String getUserEmail(){
        return MainActivity.getModifiedEmailAddress(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }
}
