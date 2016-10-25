package illinois.nao.nao;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobile.user.IdentityProvider;
import com.amazonaws.mobile.user.signin.SignInManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private SignInManager signInManager;

    @BindView(R.id.signIn_textView_CreateNewAccount) TextView createNewAccount;
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
        signInManager = SignInManager.getInstance(this);
        signInManager.setResultsHandler(this, new SignInResultsHandler());

        // ... Initialize sign-in buttons. An example per provider will be shown in
        //     the following integration instruction sections ...
        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        signInManager.handleActivityResult(requestCode, resultCode, data);
    }

    // ... handle other activity life cycle events here if needed
    //     such as instrumenting onResume and onPause for Mobile Analytics ...

}

