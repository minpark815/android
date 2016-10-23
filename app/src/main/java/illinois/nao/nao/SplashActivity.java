package illinois.nao.nao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobile.user.IdentityProvider;
import com.amazonaws.mobile.user.signin.SignInManager;
import com.amazonaws.mobile.user.signin.SignInProvider;

public class SplashActivity extends AppCompatActivity {
    private SignInManager signInManager;
    /**
     * SignInResultsHandler handles the results from sign-in for a previously signed in user.
     */
    private class SignInResultsHandler implements IdentityManager.SignInResultsHandler {
        /**
         * Receives the successful sign-in result for an alraedy signed in user and starts the main
         * activity.
         * @param provider the identity provider used for sign-in.
         */
        @Override
        public void onSuccess(final IdentityProvider provider) {
            // The user is now signed in with the preivously signed-in provider.

            // The sign-in manager is no longer needed once signed in.
            SignInManager.dispose();

            // ... implement code to go to your main activity ....
        }

        /**
         * For the case where the user previously was signed in and an attempt is made to sign the
         * user back in again, there is not an option for the user to cancel, so this is overriden
         * as a stub.
         * @param provider the identity provider with which the user attempted sign-in.
         */
        @Override
        public void onCancel(final IdentityProvider provider) {
            Log.wtf("CANCEL", "Cancel can't happen when handling a previously sign-in user.");
        }

        /**
         * Receives the sign-in result that an error occurred signing in with the previously signed
         * in provider and re-directs the user to the sign-in activity to sign in again.
         * @param provider the identity provider with which the user attempted sign-in.
         * @param ex the exception that occurred.
         */
        @Override
        public void onError(final IdentityProvider provider, Exception ex) {
            // The user is not sign-ed in.

            // ... Either direct the user to the sign-in activity or direct the user
            //     back to the main activity to continue as an unauthenticated user ...
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ... set up your views ...

        final Thread thread = new Thread(new Runnable() {
            public void run() {
                signInManager = SignInManager.getInstance(SplashActivity.this);

                final SignInProvider provider = signInManager.getPreviouslySignedInProvider();

                // If the user was already previously signed-in by a provider.
                if (provider != null) {
                    // asynchronously handle refreshing credentials and call our handler.
                    signInManager.refreshCredentialsWithProvider(SplashActivity.this,
                            provider, new SignInResultsHandler());
                } {
                    // User was not previously signed in.

                    // ... Either direct the user to the sign-in activity or direct the user
                    //     back to the main activity to continue as an unauthenticated user ...
                }
            }
        });
        thread.start();
    }
}
