package com.avior.idan.drawsomething;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * MyDrawingsFragment - where users are able to view all of their previous drawings.
 */
public class MyDrawingsFragment extends Fragment {

    private GridView gridView;
    private FirebaseDatabase database;

    public MyDrawingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_my_drawings, container, false);
        initReferences(view);
        initListView();
        return view;
    }

    private void initReferences(View view){
        database = FirebaseDatabase.getInstance();
        gridView = (GridView) view.findViewById(R.id.drawingsGridView);
    }

    private void initListView(){
        DatabaseReference dbRef = database.getReference("Users").child(getUserEmail());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                List<Drawing> drawingList = user.getDrawings();
                final DrawingAdapter adapter = new DrawingAdapter(getActivity(), R.layout.drawings_list_item, drawingList);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getActivity(), adapter.getItem(position).getImageReference(), Toast.LENGTH_SHORT).show();
                        Drawing drawing = adapter.getItem(position);
                        FragmentManager fragmentManager = getFragmentManager();
                        Fragment fragment = SelectUserFragment.newInstance(drawing.getImageReference(), getUserEmail(), drawing.getDescription());
                        fragmentManager.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit();
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
