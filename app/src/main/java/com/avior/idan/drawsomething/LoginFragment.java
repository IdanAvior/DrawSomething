package com.avior.idan.drawsomething;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    TextView loginTextView, registerTextView, titleTextView, signOutTextView, newInvitationsTextView;
    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button sendButton;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initReferences(view);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        if (firebaseAuth.getCurrentUser() != null)
            setSignedInScreen();
        return view;
    }

    private void initReferences(View view){
        loginTextView = (TextView) view.findViewById(R.id.loginTv);
        registerTextView = (TextView) view.findViewById(R.id.registerTv);
        titleTextView = (TextView) view.findViewById(R.id.title);
        signOutTextView = (TextView) view.findViewById(R.id.signOutTv);
        newInvitationsTextView = (TextView) view.findViewById(R.id.newInvitationsTextView);

        emailEditText = (EditText) view.findViewById(R.id.email);
        passwordEditText = (EditText) view.findViewById(R.id.password);
        confirmPasswordEditText = (EditText) view.findViewById(R.id.confirmPassword);

        sendButton = (Button) view.findViewById(R.id.sendButton);

        loginTextView.setOnClickListener(changeListener);
        registerTextView.setOnClickListener(changeListener);
        signOutTextView.setOnClickListener(changeListener);
        sendButton.setOnClickListener(loginListener);
    }

    View.OnClickListener changeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag().toString().equals("login")){
                loginTextView.setVisibility(View.VISIBLE);
                registerTextView.setVisibility(View.GONE);
                confirmPasswordEditText.setVisibility(View.GONE);
                titleTextView.setText("login");
                sendButton.setText("login");
                //MainActivity.hideKeyboard(getActivity());
            }
            else if (v.getTag().toString().equals("register")){
                loginTextView.setVisibility(View.GONE);
                registerTextView.setVisibility(View.VISIBLE);
                confirmPasswordEditText.setVisibility(View.VISIBLE);
                titleTextView.setText("register");
                sendButton.setText("register");
                //MainActivity.hideKeyboard(getActivity());
            }
            else{
                loginTextView.setVisibility(View.VISIBLE);
                emailEditText.setVisibility(View.VISIBLE);
                emailEditText.setText("");
                passwordEditText.setVisibility(View.VISIBLE);
                passwordEditText.setText("");
                titleTextView.setText("login");
                sendButton.setText("login");
                sendButton.setVisibility(View.VISIBLE);
                signOutTextView.setVisibility(View.GONE);
                newInvitationsTextView.setVisibility(View.GONE);
                firebaseAuth.signOut();
            }
        }
    };

    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainActivity.hideKeyboard(getActivity());
            final String email = emailEditText.getText().toString();
            final String password = passwordEditText.getText().toString();
            final String confirmPassword = confirmPasswordEditText.getText().toString();
            if (email.isEmpty()){
                emailEditText.setError("Please enter an email address");
                return;
            }
            if (password.isEmpty()){
                passwordEditText.setError("Please enter a password");
                return;
            }
            if (loginTextView.getVisibility() == View.VISIBLE){
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(getActivity(), "logged in as " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            setSignedInScreen();
                        }
                        else{
                            Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                if (!password.equals(confirmPassword)){
                    confirmPasswordEditText.setError("Passwords don't match");
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(getActivity(), "Created user " + user.getEmail(), Toast.LENGTH_LONG).show();
                            try{
                                database.getReference("Users").child(MainActivity.getModifiedEmailAddress(email)).setValue(new User(email));
                                setSignedInScreen();
                            }
                            catch (Exception e){
                                Log.e("Error: ", e.getMessage());
                            }
                        }
                        else{
                            Toast.makeText(getActivity(), "User creation failed - try a different email/password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    };

    private void setSignedInScreen(){
        sendButton.setVisibility(View.GONE);
        titleTextView.setText("Signed in as " + firebaseAuth.getCurrentUser().getEmail());
        emailEditText.setVisibility(View.GONE);
        loginTextView.setVisibility(View.GONE);
        registerTextView.setVisibility(View.GONE);
        passwordEditText.setVisibility(View.GONE);
        confirmPasswordEditText.setVisibility(View.GONE);
        signOutTextView.setVisibility(View.VISIBLE);
        setNewInvitationsTextView();
        MainActivity.hideKeyboard(getActivity());
    }

    private void setNewInvitationsTextView(){
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String username = MainActivity.getModifiedEmailAddress(userEmail);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(username);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                List<Invitation> invitationList = user.getInvitations();
                List<Invitation> viewedDrawings = user.getPreviouslyViewedDrawings();
                int numOfUnviewedInvitations = invitationList.size();
                for (Invitation invitation : invitationList)
                    for (Invitation viewedDrawing : viewedDrawings)
                        if (invitation.equals(viewedDrawing))
                            --numOfUnviewedInvitations;
                if (numOfUnviewedInvitations == 0)
                    newInvitationsTextView.setText("You have no new invitations");
                else if (numOfUnviewedInvitations == 1)
                    newInvitationsTextView.setText("You have 1 new invitation");
                else
                    newInvitationsTextView.setText("You have " + numOfUnviewedInvitations + " new invitations");
                newInvitationsTextView.setVisibility(View.VISIBLE);
                newInvitationsTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, new InvitationsFragment()).addToBackStack(null).commit();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




}
