package illinois.nao.nao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import illinois.nao.nao.User.User;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "Signup";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mUsersRef = mDatabase.getReference("users");

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
    }

    public void signUp(View v) {

        if (password.getText().toString().equals(confirmPassword.getText().toString())) {
            Log.d(TAG, "suh dude");
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            String nameString = displayName.getText().toString();
                            String userNameString = userName.getText().toString();

                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            User newUser = new User(firebaseUser, nameString, userNameString);

                            mUsersRef.child(firebaseUser.getUid()).setValue(newUser);
                            finish();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                Log.d(TAG, task.getResult() + "");
                            }
                        }
                    });
        } else {
            // TODO: passwords do not match
        }
    }

    public void goToLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
