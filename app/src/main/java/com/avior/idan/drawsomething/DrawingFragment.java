package com.avior.idan.drawsomething;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DrawingFragment - where the user can create a new drawing.
 */
public class DrawingFragment extends Fragment implements View.OnClickListener {

    private DrawingView drawingView;
    private ImageButton currentPaint, drawButton, eraseButton, newButton, saveButton, fillButton;
    private ImageButton color1, color2, color3, color4, color5, color6, color7, color8,
            color9, color10, color11, color12, color13, color14, color15, color16;
    private float smallBrush, mediumBrush, largeBrush;
    private DatabaseReference dbRef;
    private StorageReference mStorageRef;
    private List<Drawing> drawingList;
    private SoundPool soundPool;
    int clickSoundID;
    private ImageView drawingHelpImageView;

    public DrawingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_drawing, container, false);
        initReferences(v);
        return v;
    }

    private void initReferences(View v) {
        drawingView = (DrawingView) v.findViewById(R.id.drawingView);
        LinearLayout paintLayout = (LinearLayout) v.findViewById(R.id.paint_colors);
        currentPaint = (ImageButton) paintLayout.getChildAt(0);
        currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        drawButton = (ImageButton) v.findViewById(R.id.brush_button);
        drawButton.setOnClickListener(this);

        drawingView.setBrushSize(mediumBrush);

        eraseButton = (ImageButton) v.findViewById(R.id.erase_button);
        eraseButton.setOnClickListener(this);

        newButton = (ImageButton) v.findViewById(R.id.new_button);
        newButton.setOnClickListener(this);

        saveButton = (ImageButton) v.findViewById(R.id.save_button);
        saveButton.setOnClickListener(this);

        fillButton = (ImageButton) v.findViewById(R.id.fill_button);
        fillButton.setOnClickListener(this);

        initColorButtons(v);

        drawingList = new ArrayList<>();
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(getUserEmail());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null)
                    drawingList = user.getDrawings();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mStorageRef = FirebaseStorage.getInstance().getReference();
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.i("sound loaded", "status: " + status);
            }
        });
        clickSoundID = soundPool.load(getActivity(), R.raw.button_click, 1);
        drawingHelpImageView = (ImageView) v.findViewById(R.id.drawingHelpImageView);
        drawingHelpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, new DrawingHelpFragment()).addToBackStack(null).commit();
            }
        });
    }

    private void initColorButtons(View v) {
        color1 = (ImageButton) v.findViewById(R.id.color1);
        color1.setOnClickListener(this);

        color2 = (ImageButton) v.findViewById(R.id.color2);
        color2.setOnClickListener(this);

        color3 = (ImageButton) v.findViewById(R.id.color3);
        color3.setOnClickListener(this);

        color4 = (ImageButton) v.findViewById(R.id.color4);
        color4.setOnClickListener(this);

        color5 = (ImageButton) v.findViewById(R.id.color5);
        color5.setOnClickListener(this);

        color6 = (ImageButton) v.findViewById(R.id.color6);
        color6.setOnClickListener(this);

        color7 = (ImageButton) v.findViewById(R.id.color7);
        color7.setOnClickListener(this);

        color8 = (ImageButton) v.findViewById(R.id.color8);
        color8.setOnClickListener(this);

        color9 = (ImageButton) v.findViewById(R.id.color9);
        color9.setOnClickListener(this);

        color10 = (ImageButton) v.findViewById(R.id.color10);
        color10.setOnClickListener(this);

        color11 = (ImageButton) v.findViewById(R.id.color11);
        color11.setOnClickListener(this);

        color12 = (ImageButton) v.findViewById(R.id.color12);
        color12.setOnClickListener(this);

        color13 = (ImageButton) v.findViewById(R.id.color13);
        color13.setOnClickListener(this);

        color14 = (ImageButton) v.findViewById(R.id.color14);
        color14.setOnClickListener(this);

        color15 = (ImageButton) v.findViewById(R.id.color15);
        color15.setOnClickListener(this);

        color16 = (ImageButton) v.findViewById(R.id.color16);
        color16.setOnClickListener(this);
    }

    private boolean isColorClicked(View view) {
        int id = view.getId();
        return (id == R.id.color1 || id == R.id.color2 || id == R.id.color3 || id == R.id.color4
                || id == R.id.color5 || id == R.id.color6 || id == R.id.color7 || id == R.id.color8
                || id == R.id.color9 || id == R.id.color10 || id == R.id.color11 || id == R.id.color12
                || id == R.id.color13 || id == R.id.color14 || id == R.id.color15 || id == R.id.color16);
    }

    public void onPaintClicked(View view) {
        drawingView.setErase(false);
        drawingView.setBrushSize(drawingView.getLastBrushSize());
        if (view != currentPaint) {
            ImageButton imageView = (ImageButton) view;
            String color = view.getTag().toString();
            drawingView.setColor(color);

            imageView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currentPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currentPaint = (ImageButton) view;
        }
    }

    @Override
    public void onClick(View view) {
        soundPool.play(clickSoundID, 1, 1, 1, 0, 1f);
        if (view.getId() == R.id.brush_button) {
            onBrushButtonClicked();
        } else if (view.getId() == R.id.erase_button) {
            onEraseButtonClicked();
        } else if (view.getId() == R.id.new_button) {
            onNewButtonClicked();
        } else if (view.getId() == R.id.save_button) {
            onSaveButtonClicked();
        } else if (isColorClicked(view)) {
            onPaintClicked(view);
        }
        else if (view.getId() == R.id.fill_button){
            drawingView.fillCanvas();
        }
    }

    /*
    Saving the drawing -
    The drawing itself is saved into FirebaseStorage as a JPEG with a unique name within the user's designated drawings folder.
    In addition, the user's FirebaseDatabase entry is updated - the new Drawing object is added to the user's Drawings list.
    The Drawing's imageReference is identical to the saved image's name and is the means through which the image will be retrieved.
     */
    private void onSaveButtonClicked() {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(getActivity());
        saveDialog.setTitle("Save drawing");
        saveDialog.setMessage("Enter drawing description: ");
        final EditText input = new EditText(getActivity());
        saveDialog.setView(input);
        saveDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!input.getText().toString().isEmpty()) {
                    drawingView.setDrawingCacheEnabled(true);
                    drawingView.buildDrawingCache();
                    Bitmap imageSaved = Bitmap.createBitmap(drawingView.getDrawingCache());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageSaved.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    String referenceName = "img" + (drawingList.size() + 1);
                    StorageReference childRef = mStorageRef.child(getUserEmail()).child(referenceName);
                    UploadTask uploadTask = childRef.putBytes(data);
                    drawingList.add(new Drawing(referenceName, input.getText().toString()));
                    dbRef.child("drawings").setValue(drawingList);
                    drawingView.destroyDrawingCache();
                }
                else{
                    Toast.makeText(getActivity(), "Drawing not uploaded - please enter a description!", Toast.LENGTH_LONG).show();
                }
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        saveDialog.show();
    }

    private void onNewButtonClicked() {
        AlertDialog.Builder newDialog = new AlertDialog.Builder(getContext());
        newDialog.setTitle("New drawing");
        newDialog.setMessage("Start a new drawing (you will lose the current drawing)?");
        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                drawingView.startNewDrawing();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    private void onEraseButtonClicked() {
        final Dialog eraserDialog = new Dialog(getContext());
        eraserDialog.setTitle("Eraser size:");
        eraserDialog.setContentView(R.layout.brush_chooser);

        ImageButton smallButton = (ImageButton) eraserDialog.findViewById(R.id.small_brush);
        smallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setErase(true);
                drawingView.setBrushSize(smallBrush);
                eraserDialog.dismiss();
            }
        });

        ImageButton mediumButton = (ImageButton) eraserDialog.findViewById(R.id.medium_brush);
        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setErase(true);
                drawingView.setBrushSize(mediumBrush);
                eraserDialog.dismiss();
            }
        });

        ImageButton largeButton = (ImageButton) eraserDialog.findViewById(R.id.large_brush);
        largeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setErase(true);
                drawingView.setBrushSize(largeBrush);
                eraserDialog.dismiss();
            }
        });
        eraserDialog.show();
    }

    private void onBrushButtonClicked() {
        final Dialog brushDialog = new Dialog(getContext());
        brushDialog.setTitle("Brush size:");
        brushDialog.setContentView(R.layout.brush_chooser);

        final ImageButton smallButton = (ImageButton) brushDialog.findViewById(R.id.small_brush);
        smallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setBrushSize(smallBrush);
                drawingView.setLastBrushSize(smallBrush);
                drawingView.setErase(false);
                brushDialog.dismiss();
            }
        });

        final ImageButton mediumButton = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setBrushSize(mediumBrush);
                drawingView.setLastBrushSize(mediumBrush);
                drawingView.setErase(false);
                brushDialog.dismiss();
            }
        });

        final ImageButton largeButton = (ImageButton) brushDialog.findViewById(R.id.large_brush);
        largeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setBrushSize(largeBrush);
                drawingView.setLastBrushSize(largeBrush);
                drawingView.setErase(false);
                brushDialog.dismiss();
            }
        });

        brushDialog.show();
    }

    private String getUserEmail() {
        return MainActivity.getModifiedEmailAddress(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }
}
