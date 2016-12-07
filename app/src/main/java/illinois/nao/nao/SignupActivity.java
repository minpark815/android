package illinois.nao.nao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import illinois.nao.nao.Pages.ProfileFragment;
import illinois.nao.nao.Storage.StorageHelper;
import illinois.nao.nao.User.User;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "Signup";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsersRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    @BindView(R.id.editText_email) EditText email;
    @BindView(R.id.editText_password) EditText password;
    @BindView(R.id.editText_password_confirm) EditText confirmPassword;
    @BindView(R.id.button_signup) Button signUpButton;
    @BindView(R.id.textViewRegUserErrorMessage) TextView errorMessage;
    @BindView(R.id.editText_username) EditText userName;
    @BindView(R.id.editText_name) EditText displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
        mUsersRef = mDatabase.getReference("users");
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://nao-app-bc1b6.appspot.com");
    }

    public void signUp(View v) {
        if (!isEmpty(email) && !isEmpty(password) && !isEmpty(password) && !isEmpty(userName) && !isEmpty(displayName)) {
            if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                Log.d(TAG, "suh dude");
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                String nameString = displayName.getText().toString();
                                String userNameString = userName.getText().toString();

                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                User newUser = new User(firebaseUser, nameString, userNameString);

                                mUsersRef.child(userNameString).setValue(newUser);
                                StorageReference userReference = mStorageRef.child("users").child(userNameString);

                                StorageReference profilePicRef = userReference.child("profile");
                                Uri profilePicUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.profile);
                                StorageHelper.uploadFile(profilePicUri, profilePicRef, null, null);

                                StorageReference imageRef = userReference.child("image");
                                Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image);
                                StorageHelper.uploadFile(imageUri, imageRef, null, null);

                                StorageReference audioRef = userReference.child("audio");
                                Uri audioUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ifelephantscouldfly);
                                StorageHelper.uploadFile(audioUri, audioRef, null, null);

                                StorageReference videoRef = userReference.child("video");
                                Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.naovideo);
                                StorageHelper.uploadFile(videoUri, videoRef, null, null);

                                finish();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                    //TODO: getResult throws errors. Will throw error if password is < 6 characters
                                    Log.d(TAG, task.getResult() + "");
                                }
                                mAuth.getCurrentUser().sendEmailVerification();
                            }

                        });
                Toast.makeText(SignupActivity.this, "Verification email sent!", Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setCancelable(true);
                adb.setMessage("Are you sure the password and password confirmation matches?");
                adb.setPositiveButton(
                        "Try Again",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );
                AlertDialog alertPasswordMismatch = adb.create();
                alertPasswordMismatch.show();
            }
        }else{
            errorMessage.setText("One or more of the fields are empty!");
        }
    }

    public void goToLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        else{
            return true;
        }
    }
}
