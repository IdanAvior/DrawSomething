package com.avior.idan.drawsomething;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Idan Avior on 9/25/2017.
 */

/**
 * DrawingAdapter
 */
public class DrawingAdapter extends ArrayAdapter<Drawing> {

    private static final int IMG_DIMENSION = 200;

    private List<Drawing> drawingList;

    public DrawingAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Drawing> drawings) {
        super(context, resource);
        drawingList = drawings;
    }

    @Override
    public int getCount(){
        return drawingList.size();
    }

    @Override
    public @Nullable
    Drawing getItem(int position) {
        return drawingList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drawings_list_item, null);
        }
            try{
                final StorageReference storageReference = FirebaseStorage.getInstance().getReference(getUserEmail()).child(drawingList.get(position).getImageReference());
                Log.d("TAG", "path = " + getUserEmail() + "/" + drawingList.get(position).getImageReference());
                final ImageView imageView = (ImageView) convertView.findViewById(R.id.drawingImageView);

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("TAG", "downloaded uri: " + uri + " position: " + position);
                        Glide.with(getContext()).load(uri).into(imageView);
                        imageView.setLayoutParams(new RelativeLayout.LayoutParams(IMG_DIMENSION, IMG_DIMENSION));
                        Log.d("TAG", "image view ref: " + imageView);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Unable to download url");
                    }
                });
            }
            catch (Exception e){
                e.printStackTrace();
            }
            Log.d("TAG", "returning convertView" + position);
            return convertView;
        }


    private String getUserEmail(){
        return MainActivity.getModifiedEmailAddress(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

}

