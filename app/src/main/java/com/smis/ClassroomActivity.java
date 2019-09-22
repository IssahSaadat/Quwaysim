package com.smis;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smis.utilities.UniversalImageLoader;

public class ClassroomActivity extends AppCompatActivity implements UploadFileDialog.OnFileReceivedListener {
    int FILE_SELECT_CODE = 0;
    ProgressBar mProgressBar;
    Boolean okay = false;
    StorageReference sReference = FirebaseStorage.getInstance().getReference();

    private String TAG = "ClassroomActivity";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Uri mSelectedFileUri;
    private double progress;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void getFilePath(Uri filePath) {
        if (!filePath.toString().equals("")) {
            mSelectedFileUri = filePath;
            Log.d("TAG", "getFilePath: got the file uri: " + mSelectedFileUri);
            //ImageLoader.getInstance().displayImage(filePath.toString(), mProfile_image);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        Log.d("TAG", "onCreate Started");
        setupFirebaseAuth();
        FloatingActionButton upload_file = findViewById(R.id.upload_file_fab);
        FloatingActionButton select_file = findViewById(R.id.select_file_fab);
        mProgressBar = findViewById(R.id.upload_progressBar);
        mRecyclerView = findViewById(R.id.rv_row);
        /*
         * RecyclerView Implementation
         */
        mRecyclerView = findViewById(R.id.rv_row);


        select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadFileDialog dialog = new UploadFileDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.dialog_upload_file));
            }
        });
        upload_file.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedFileUri != null) {
                    executeUploadTask();
                } else {
                    Toast.makeText(ClassroomActivity.this, "Please, first select a file to upload", Toast.LENGTH_SHORT).show();
                }
            }
        }));
        initImageLoader();
    }

    /*
     * Upload process of the classroom files
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Results when selecting new image from phone memory
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: image: " + selectedImageUri);

        }
    }

    //This method is used to start the upload task to the firebase storage
    private void executeUploadTask() {
        showProgressBar();

        //specify where the photo will be stored
        /*TODO this StorageReference replaces pdf files uploaded to the storage, separate paths needs to be given to each files
         *TODO --> So that the files wont replace each other on the Firebase storage
         *TODO --> You may change the storage directory to suit your taste
         */
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("classroom/" + Math.random() * 100000 + mSelectedFileUri.getPathSegments().get(0) + ".pdf");

        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("classroom/files")
                .setContentLanguage("en") //see nodes below
                .build();

        //if the file size is valid then we can submit to database
        UploadTask uploadTask = null;
        uploadTask = storageReference.putFile(mSelectedFileUri, metadata);
        //uploadTask = storageReference.putBytes(mBytes); //without metadata

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Now insert the download url into the firebase database
                //Task<Uri> firebaseURL = taskSnapshot.getStorage().getDownloadUrl();
                Toast.makeText(ClassroomActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();
                okay = true;
                //mSelectedFileUri = null;
                //Log.d("newTag", "onSuccess: firebase download url = " + firebaseURL.toString());
//                FirebaseDatabase.getInstance().getReference()
//                        .child(getString(R.string.dbnode_users))
//                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                        .child(getString(R.string.field_profile_image))
//                        .setValue(firebaseURL.toString());
                hideProgressBar();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ClassroomActivity.this, "could not upload file", Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                if (currentProgress > (progress + 15)) {
                    progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.d(TAG, "onProgress: Upload is " + progress + "% done");
                    Toast.makeText(ClassroomActivity.this, progress + "%", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * init universal image loader
     */
    private void initImageLoader() {
        UniversalImageLoader imageLoader = new UniversalImageLoader(ClassroomActivity.this);
        ImageLoader.getInstance().init(imageLoader.getConfig());
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState() {
        Log.d("TAG", "Check Auth State: checking auth state.");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d("TAG", "checkAuthState: user is null, redirecting to Login Activity");
            Intent intent = new Intent(ClassroomActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Log.d("TAG", "User is Authenticated.");
        }
    }

    private void setupFirebaseAuth() {
        Log.d("TAG", "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(ClassroomActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        //isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
        //isActivityRunning = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                Log.d("TAG", "Signed Out Btn Clicked");
                //Intent intent = new Intent(ClassroomActivity.this, LoginActivity.class);
                //startActivity(intent);
                //checkAuthenticationState();
                return true;
            case R.id.chat:
                Log.d("TAG", "Chat Button Clicked");
                Intent intent = new Intent(ClassroomActivity.this, ChatActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings:
                Log.d("TAG", "Settings Button Clicked!");
                Intent settingsIntent = new Intent(ClassroomActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.quiz:
                Log.d("TAG", "Settings Button Clicked!");
                Intent quizIntent = new Intent(ClassroomActivity.this, QuizActivity.class);
                startActivity(quizIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }





    /*
     * This method here is for testing purposes only
     */
//    public String downloadFile() {
//
//        sReference.child("classroom/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                downloadURL = uri.toString();
//                //DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//                //ref.child(getString(R.string.dbnode_users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                //.child(getString(R.string.field_profile_image)).setValue(uri.toString());
//                Toast.makeText(ClassroomActivity.this, "download url at: " + uri.toString(), Toast.LENGTH_SHORT).show();
//                // Got the download URL for 'users/me/profile.png'
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Toast.makeText(ClassroomActivity.this, "Download Failed. Please Try Again Later", Toast.LENGTH_SHORT).show();
//                // Handle any errors
//            }
//        });
//        return downloadURL;
//    }
}