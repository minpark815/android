package illinois.nao.nao.Pages;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import illinois.nao.nao.R;
import illinois.nao.nao.Storage.StorageHelper;
import illinois.nao.nao.UX.AudioDialog;
import illinois.nao.nao.UX.PostDialog;
import illinois.nao.nao.User.PostEvent;
import nz.co.delacour.exposurevideoplayer.ExposureVideoPlayer;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ProfileFragment";
    private static final int REQUEST_TAKE_PHOTO = 1001;
    private static final int REQUEST_RECORD_VIDEO = 1002;
    private static final int PICK_IMAGE_FILE = 1003;
    private static final int PICK_VIDEO_FILE = 1004;
    private static final int CHANGE_PROFILE = 1005;

    @BindView(R.id.profile_videoplayer) ExposureVideoPlayer videoPlayer;
    @BindView(R.id.profile_button_audio) ImageButton buttonAudio;
    @BindView(R.id.scrollView_profile) NestedScrollView scrollView;
    @BindView(R.id.textView_textContent) TextView textContent;
    @BindView(R.id.textView_name) TextView name;
    @BindView(R.id.imageView2) ImageView imageContent;
    @BindView(R.id.imageView) ImageView profilePicture;
    @BindView(R.id.floatingActionMenu) FloatingActionMenu floatingActionMenu;

    private MediaPlayer mp;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsersRef;
    private DatabaseReference mUserRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private StorageReference mUserStorageRef;
    private StorageReference mAllUserStorageRef;
    private String userName;
    private File audioFile;
    private File videoFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (getArguments() != null && getArguments().getString("userName") != null) {
            userName = getArguments().getString("userName");
        } else {
            userName = mUser.getDisplayName();
        }
        mDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mDatabase.getReference("users");
        mUserRef = mUsersRef.child(userName);
        mStorage = FirebaseStorage.getInstance();
        mStorageRef  = mStorage.getReferenceFromUrl("gs://nao-app-bc1b6.appspot.com");
        mAllUserStorageRef = mStorageRef.child("users");
        mUserStorageRef = mStorageRef.child("users").child(userName);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        // videoPlayer.setVideoSource(Uri.parse("android.resource://illinois.nao.nao/" + R.raw.naovideo));
        name.setText(userName);
        populateProfilePicture();
        populateText();
        populateImage();
        populateVideo();
        populateAudio();

        ImageView profile = (ImageView) view.findViewById(R.id.imageView);
        profile.setOnClickListener(this);

        FloatingActionButton photo = (FloatingActionButton) view.findViewById(R.id.add_photo);
        FloatingActionButton video = (FloatingActionButton) view.findViewById(R.id.record_video);
        FloatingActionButton text  = (FloatingActionButton) view.findViewById(R.id.write_post);
        FloatingActionButton audio = (FloatingActionButton) view.findViewById(R.id.record_audio);
        photo.setOnClickListener(this);
        video.setOnClickListener(this);
        text.setOnClickListener(this);
        audio.setOnClickListener(this);

        if (!userName.equals(mUser.getDisplayName())) {
            floatingActionMenu.setVisibility(View.GONE);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) floatingActionMenu.getLayoutParams();
            params.setBehavior(null);
            profile.setOnClickListener(null);
            floatingActionMenu.setLayoutParams(params);
        }

        buttonAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Media Player", "Is Playing: " + mp.isPlaying());

                if(mp.isPlaying()) {
                    mp.pause();
                    //mp.stop();
                    Log.i("Media Player", "Pause");
                    buttonAudio.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                } else {
                    mp.start();
                    Log.i("Media Player", "Play");
                    buttonAudio.setImageResource(R.drawable.ic_pause_black_24dp);
                }
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                buttonAudio.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (userName.equals(mUser.getDisplayName())) {
            inflater.inflate(R.menu.profile_menu, menu);
        } else {
            inflater.inflate(R.menu.people_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void populateText() {
        ValueEventListener userTextListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    try {
                        String text = dataSnapshot.getValue(String.class);
                        textContent.setText(text);
                        Log.d(TAG, "text set to" + text);
                    } catch (DatabaseException e) {
                        mUserRef.child("profileDescription").setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "populate text failed");
            }
        };

        mUsersRef.child(userName).child("profileDescription").addListenerForSingleValueEvent(userTextListener);
    }

    public void populateImage() {
        StorageReference imageReference = mUserStorageRef.child("image");
        StorageHelper.populateImage(imageReference, imageContent);
    }

    public void populateVideo() {
        try {
            videoFile = File.createTempFile("video", "mp4");
            StorageReference userVideoRef = mAllUserStorageRef.child(userName).child("video");
            userVideoRef.getFile(videoFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created

                    videoPlayer.setVideoSource(Uri.fromFile(videoFile));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            Log.d(TAG, "exception downloading files");
        } catch (IllegalStateException e) {
            Log.d(TAG, "error setting files");
        }
    }

    public void populateAudio() {
        try {
            audioFile = File.createTempFile("audio", "mp3");
            StorageReference userVideoRef = mAllUserStorageRef.child(userName).child("audio");
            userVideoRef.getFile(audioFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created

                    mp = MediaPlayer.create(getContext(), Uri.fromFile(audioFile));
                    Log.d(TAG, "media player created");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            Log.d(TAG, "exception downloading files");
        }
    }

    public void populateProfilePicture() {
        StorageReference profilePicReference = mUserStorageRef.child("profile");
        StorageHelper.populateImage(profilePicReference, profilePicture);
    }

    public void uploadText(String text) {
        DatabaseReference textRef = mUsersRef.child(userName);
        textRef.setValue(text);
        StorageHelper.pushToFeed(mUser.getDisplayName(), PostEvent.Type.TEXT);
    }

    /**
     * This method takes a file as a Uri and uploads it to the storage reference /username/video/file
     * @param file Uri file
     */
    public void uploadVideo(Uri file) {
        StorageReference userVideoRef = mUserStorageRef.child("video");
        StorageHelper.uploadFile(file, userVideoRef,null, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                populateVideo();
            }
        });
        StorageHelper.pushToFeed(mUser.getDisplayName(), PostEvent.Type.VIDEO);
    }

    public void uploadImage(Uri file) {
        StorageReference userPhotoRef = mUserStorageRef.child("image");
        StorageHelper.uploadFile(file, userPhotoRef, null, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                populateImage();
            }
        });
        StorageHelper.pushToFeed(mUser.getDisplayName(), PostEvent.Type.IMAGE);
    }

    public void uploadSound(Uri file) {
        StorageReference userSoundRef = mUserStorageRef.child("sound");
        StorageHelper.uploadFile(file, userSoundRef,null, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                populateAudio();
            }
        });
        StorageHelper.pushToFeed(mUser.getDisplayName(), PostEvent.Type.AUDIO);
    }

    public void changeProfile(Uri file){
        StorageReference userProfileRef = mUserStorageRef.child("profile");
        StorageHelper.uploadFile(file, userProfileRef, null, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                populateProfilePicture();
            }
        });
    }

    @Override
    public void onClick(final View view) {
        final int button = view.getId();
        System.out.println(button);
        CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        if(button == R.id.record_video){
            options[0] = "Record Video";
        }
        if(button == R.id.add_photo || button == R.id.record_video){
            final CharSequence[] choices = options;
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(view.getContext());
            builder.setTitle("Add Photo");
            builder.setItems(choices, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i == 0 && button == R.id.add_photo){
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        }
                    }else if(i == 0){
                        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        if (takeVideoIntent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivityForResult(takeVideoIntent, REQUEST_RECORD_VIDEO);
                        }
                    }else if(i == 1){
                        Intent intent = new Intent();
                        if (button == R.id.add_photo) {
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_FILE);
                        } else {
                            intent.setType("video/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_VIDEO_FILE);
                        }

                        System.out.println("Pick from Gallery");
                    }else if(i == 2){
                        dialogInterface.dismiss();
                    }
                }
            });
            builder.show();
        }else if(button == R.id.record_audio){
            System.out.println("record");
            AudioDialog dialog = new AudioDialog(view.getContext(), mUserStorageRef, mUser.getDisplayName());
            dialog.setContentView(R.layout.audio_dialog);
            dialog.setTitle("Record Audio");
            dialog.show();
        }else if(button == R.id.write_post){
            PostDialog dialog = new PostDialog(view.getContext(), mUser.getDisplayName());
            dialog.show();
        }else if(button == R.id.imageView){
            final CharSequence[] choices = options;
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(view.getContext());
            builder.setTitle("Add Photo");
            builder.setItems(choices, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        }
                    } else if(i==1){
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), CHANGE_PROFILE);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            });
            builder.show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    uploadImage(data.getData());
                    populateImage();
                }
                break;
            case REQUEST_RECORD_VIDEO:
                if (resultCode == RESULT_OK) {
                    uploadVideo(data.getData());
                }
                break;
            case PICK_IMAGE_FILE:
                if (resultCode == RESULT_OK) {
                    uploadImage(data.getData());
                }
                break;
            case PICK_VIDEO_FILE:
                if (resultCode == RESULT_OK) {
                    uploadVideo(data.getData());
                }
            case CHANGE_PROFILE:
                if (resultCode == RESULT_OK){
                    changeProfile(data.getData());
                }
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mp != null) {
            mp.release();
        }
        if(videoFile != null) {
            Log.d(TAG, "Video file deleted: " + videoFile.delete());
        }
        if(audioFile != null) {
            Log.d(TAG, "Audio file deleted: " + audioFile.delete());
        }
    }
}
