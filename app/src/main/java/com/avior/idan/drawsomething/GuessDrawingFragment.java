package com.avior.idan.drawsomething;

import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * GuessDrawingFragment - where the user can try and guess a drawing.
 */
public class GuessDrawingFragment extends Fragment {

    private static final String ARG_DRAWING_REFERENCE = "drawingName";
    private static final String ARG_DRAWING_CREATOR = "drawingCreator";
    private static final String ARG_DRAWING_DESCRIPTION = "drawingDescription";

    private static final int IMG_DIMENSION = 60;

    private String drawingName;
    private String drawingCreator;
    private String drawingDescription;

    private TextView clueTextView;
    private ImageView drawingImageView;
    private EditText answerEditText;
    private Button submitButton, returnButton;
    private ImageView firstWrongAnswer, secondWrongAnswer, thirdWrongAnswer, helpImageView;
    private int numOfWrongAnswers, correctAnswerSoundID, wrongAnswerSoundID;
    private SoundPool soundPool;

    public GuessDrawingFragment() {
        // Required empty public constructor
    }

    /**
     * @param drawingName Parameter 1.
     * @param drawingCreator Parameter 2.
     * @param drawingDescription Parameter 3.
     * @return A new instance of fragment GuessDrawingFragment.
     */
    public static GuessDrawingFragment newInstance(String drawingName, String drawingCreator, String drawingDescription) {
        GuessDrawingFragment fragment = new GuessDrawingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DRAWING_REFERENCE, drawingName);
        args.putString(ARG_DRAWING_CREATOR, drawingCreator);
        args.putString(ARG_DRAWING_DESCRIPTION, drawingDescription);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guess_drawing, container, false);
        initReferences(view);
        setScreen();
        return view;
    }

    private void initReferences(View view) {
        if (getArguments() != null) {
            drawingName = getArguments().getString(ARG_DRAWING_REFERENCE);
            drawingCreator = getArguments().getString(ARG_DRAWING_CREATOR);
            drawingDescription = getArguments().getString(ARG_DRAWING_DESCRIPTION);
        }

        clueTextView = (TextView) view.findViewById(R.id.guessDrawingClue);
        drawingImageView = (ImageView) view.findViewById(R.id.guessDrawingImageView);
        helpImageView = (ImageView) view.findViewById(R.id.guessingHelpImageView);
        answerEditText = (EditText) view.findViewById(R.id.guessDrawingEditText);
        submitButton = (Button) view.findViewById(R.id.guessDrawingSubmitButton);
        returnButton = (Button) view.findViewById(R.id.guessDrawingReturnButton);
        initWrongAnswerMarkers(view);
        numOfWrongAnswers = 0;
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.i("sound loaded", "status: " + status);
            }
        });
        correctAnswerSoundID = soundPool.load(getActivity(), R.raw.correct_answer, 1);
        wrongAnswerSoundID = soundPool.load(getActivity(), R.raw.wrong_answer, 1);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.hideKeyboard(getActivity());
                String input = answerEditText.getText().toString();
                if (!input.isEmpty()){
                    if (input.toUpperCase().equals(drawingDescription.toUpperCase())) {
                        soundPool.play(correctAnswerSoundID, 1,1,1,0,1f);
                        Toast.makeText(getActivity(), "Correct!", Toast.LENGTH_SHORT).show();
                        endGuessing();
                    }
                    else {
                        Toast.makeText(getActivity(), "Wrong!", Toast.LENGTH_SHORT).show();
                        reactToWrongAnswer();
                    }
                }
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        helpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, new GuessingHelpFragment()).addToBackStack(null).commit();
            }
        });
    }

    private void initWrongAnswerMarkers(View view) {
        firstWrongAnswer = (ImageView) view.findViewById(R.id.firstWrongAnswerImage);
        secondWrongAnswer = (ImageView) view.findViewById(R.id.secondtWrongAnswerImage);
        thirdWrongAnswer = (ImageView) view.findViewById(R.id.thirdWrongAnswerImage);
        firstWrongAnswer.getLayoutParams().height = IMG_DIMENSION;
        firstWrongAnswer.getLayoutParams().width = IMG_DIMENSION;
        secondWrongAnswer.getLayoutParams().height = IMG_DIMENSION;
        secondWrongAnswer.getLayoutParams().width = IMG_DIMENSION;
        thirdWrongAnswer.getLayoutParams().height = IMG_DIMENSION;
        thirdWrongAnswer.getLayoutParams().width = IMG_DIMENSION;
    }

    private void setScreen(){
        setClueTextView();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(drawingCreator).child(drawingName);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("TAG", "Image loading successful");
                Glide.with(getActivity()).load(uri).into(drawingImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG", "Image loading failed");
            }
        });
    }

    private void setClueTextView(){
        int length = drawingDescription.length();
        StringBuilder clueString = new StringBuilder(length*2);
        for (int i = 0; i < length; i++) {
            if (drawingDescription.charAt(i) != ' ')
                clueString.append("_ ");
            else
                clueString.append("  ");
        }
        clueTextView.setText(clueString.toString());
    }

    private void reactToWrongAnswer(){
        soundPool.play(wrongAnswerSoundID, 1,1,1,0,1f);
        ++numOfWrongAnswers;
        if (numOfWrongAnswers == 1){
            firstWrongAnswer.setVisibility(View.VISIBLE);
        }
        else if (numOfWrongAnswers == 2){
            secondWrongAnswer.setVisibility(View.VISIBLE);
        }
        else if (numOfWrongAnswers == 3){
            thirdWrongAnswer.setVisibility(View.VISIBLE);
            endGuessing();
        }
    }

    private void endGuessing(){
        clueTextView.setText(drawingDescription.toUpperCase());
        answerEditText.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        returnButton.setVisibility(View.VISIBLE);
    }
}
