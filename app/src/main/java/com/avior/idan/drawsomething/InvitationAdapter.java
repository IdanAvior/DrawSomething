package com.avior.idan.drawsomething;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by Idan Avior on 10/2/2017.
 */

/**
 * InvitationAdapter
 */
public class InvitationAdapter extends ArrayAdapter<Invitation> {

    private List<Invitation> invitationList;

    public InvitationAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Invitation> invitations) {
        super(context, resource);
        invitationList = invitations;
    }

    @Override
    public int getCount(){
        return invitationList.size();
    }

    @Override
    public @Nullable
    Invitation getItem(int position) {
        return invitationList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.invitations_list_item, null);
        }
        final Invitation invitation = invitationList.get(position);
        final TextView creatorNameTextView = (TextView) convertView.findViewById(R.id.invitationTextView);
        creatorNameTextView.setText("By " + invitation.getDrawingCreator());
        final TextView previouslyViewedTextView = (TextView) convertView.findViewById(R.id.previouslyViewedTextView);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.invitationImageView);
        if (invitationList.get(position).hasBeenViewedByUser()) {
            previouslyViewedTextView.setText("Viewed");
            previouslyViewedTextView.setBackgroundColor(getContext().getResources().getColor(R.color.colorViewedDrawingListItem));
            creatorNameTextView.setBackgroundColor(getContext().getResources().getColor(R.color.colorViewedDrawingListItem));
            imageView.setBackgroundColor(getContext().getResources().getColor(R.color.colorViewedDrawingListItem));
        }
        else {
            previouslyViewedTextView.setText("Not viewed");
            previouslyViewedTextView.setBackgroundColor(getContext().getResources().getColor(R.color.colorUnviewedDrawingListItem));
            creatorNameTextView.setBackgroundColor(getContext().getResources().getColor(R.color.colorUnviewedDrawingListItem));
            imageView.setBackgroundColor(getContext().getResources().getColor(R.color.colorUnviewedDrawingListItem));
        }
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference(invitation.getDrawingCreator()).child(invitation.getDrawingReferenceName());
        Log.d("TAG", "StorgaeReference: " + storageReference);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("TAG", "Successfully downloaded URL");
                Glide.with(getContext()).load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "Failed to download URL");
            }
        });
        return convertView;
    }


    private String getUserEmail(){
        return FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_").replace("@", "_at_");
    }


}
