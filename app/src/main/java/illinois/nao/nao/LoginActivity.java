package illinois.nao.nao;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobile.user.IdentityProvider;
import com.amazonaws.mobile.user.signin.SignInManager;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/passwordEditText.
 */
public class LoginActivity extends AppCompatActivity {
    private SignInManager signInManager;

    @BindView(R.id.login_textview_createnewaccount) TextView createNewAccount;
    @BindView(R.id.login_edittext_username) EditText usernameEditText;
    @BindView(R.id.login_edittext_password) EditText passwordEditText;

    private String username;
    private String password;

    /**
     * SignInResultsHandler handles the final result from sign in. Making it static is a best
     * practice since it may outlive the SplashActivity's life span.
     */
    private class SignInResultsHandler implements IdentityManager.SignInResultsHandler {
        /**
         * Receives the successful sign-in result and starts the main activity.
         * @param provider the identity provider used for sign-in.
         */
        @Override
        public void onSuccess(final IdentityProvider provider) {
            // The sign-in manager is no longer needed once signed in.
            SignInManager.dispose();

            // ... implement code to go to your main activity ...
        }

        /**
         * Recieves the sign-in result indicating the user canceled.
         * @param provider the identity provider with which the user attempted sign-in.
         */
        @Override
        public void onCancel(final IdentityProvider provider) {
            // Sign in was cancelled for the specified provider.

            // ... Nothing may need to be done here, but if you added a spinner that
            //     has been shown, you could remove it and allow the user to press
            //     one of the sign in buttons again ...
        }

        /**
         * Receives the sign-in result that an error occurred signing in.
         * @param provider the identity provider with which the user attempted sign-in.
         * @param ex the exception that occurred.
         */
        @Override
        public void onError(final IdentityProvider provider, final Exception ex) {
            // ... Handle informing the user of an error signing in ...
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        AmazonHelper.init(getApplicationContext());
        signInManager = SignInManager.getInstance(this);
        signInManager.setResultsHandler(this, new SignInResultsHandler());

        // ... Initialize sign-in buttons. An example per provider will be shown in
        //     the following integration instruction sections ...
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                // From SignUpActivity
                if(resultCode == RESULT_OK) {

                }
                break;
            case 2:
                // From ConfirmActivity
                if(resultCode == RESULT_OK) {
                    String username = data.getStringExtra("usernameEditText");

                    if(username != null && !username.isEmpty()) {
                        this.usernameEditText.setText(username);
                    }
                }
                break;
            case 3:
                // From ForgotPasswordActivity
                if(resultCode == RESULT_OK) {

                }
                break;
//            case 4:
//                // User
//                if(resultCode == RESULT_OK) {
//
//                }
//                break;
//            case 5:
//                // MFA
//                if(resultCode == RESULT_OK) {
//
//                }
//                break;
//            case 6:
//                // New passwordEditText
//                if(resultCode == RESULT_OK) {
//
//                }
//                break;
        }
        signInManager.handleActivityResult(requestCode, resultCode, data);
    }

    // ... handle other activity life cycle events here if needed
    //     such as instrumenting onResume and onPause for Mobile Analytics ...
    public void goToSignup(View v) {
        Intent intent = new Intent(v.getContext(), SignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void logIn(View v) {
        Log.i("Sign-in", "Attempting to log in...");

        username = usernameEditText.getText().toString().trim();

        if(username == null || username.isEmpty()) {
            usernameEditText.setError("Forget to enter your username?");
            return;
        }

        AmazonHelper.setUser(username);
        password = passwordEditText.getText().toString();

        if(password == null || password.isEmpty()) {
            passwordEditText.setError("Forget to enter your password?");
            return;
        }

        AmazonHelper.getPool().getUser(username).getSessionInBackground(auth);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    AuthenticationHandler auth = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            //AmazonHelper.setCurrSession(cognitoUserSession);
            AmazonHelper.newDevice(device);
            Log.i("Sign-in", "Success!");
            goToMainActivity();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            Locale.setDefault(Locale.US);
            Log.i("Sign-in", "getAuthenticationDetails");
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            Log.i("Sign-in", "MFA");
            //mfaAuth(multiFactorAuthenticationContinuation);
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            Log.i("Sign-in", "Challenge");

        }

        @Override
        public void onFailure(Exception e) {
            // Sign in failed
            Log.i("Sign-in", "FAILED: " + e.getMessage());
        }
    };
}

