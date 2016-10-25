package illinois.nao.nao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.editText_email) EditText email;
    @BindView(R.id.editText_username) EditText username;
    @BindView(R.id.editText_password) EditText password;
    @BindView(R.id.editText_phone) EditText phone;
    @BindView(R.id.button_signup) Button signUpButton;
    @BindView(R.id.textViewRegUserErrorMessage) TextView errorMessage;

    private String emailInput;
    private String usernameInput;
    private String phoneInput;
    private String passwordInput;

    private Context context;

    private SignUpHandler signUpHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        init();

        context = this;
    }

    private void init() {
        signUpHandler = new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                                  CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {

                Boolean regState = signUpConfirmationState;
                if (regState) {
                    // User is already confirmed
                    Log.i("signup", "Success");
                    // Go to MainActivity
                }
                else {
                    Log.i("signup", "Has To Confirm");
                    // Go to ConfirmActivity
                    goToConfirm(cognitoUserCodeDeliveryDetails);
                }
            }

            @Override
            public void onFailure(Exception exception) {
                Log.i("signup", "FAILURE");
                Log.i("signup", exception.toString());
                final AlertDialog.Builder edb = new AlertDialog.Builder(SignupActivity.this);
                edb.setTitle("Signup Error");
                edb.setMessage(String.format(exception.getMessage()).substring(0,String.format(exception.getMessage()).indexOf("(")-1));
                edb.setNeutralButton("Ok", null);
                edb.show();
            }
        };
    }

    public void signUp(View v) {
        // Read user data and register
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        // TODO: Set error messages to strings.xml
        usernameInput = username.getText().toString().trim();
        if (usernameInput == null || usernameInput.isEmpty()) {
            username.setError("You actually need something here.");
            errorMessage.setText("Did you forget setting a usernameEditText?");
            return;
        }

        phoneInput = phone.getText().toString();
        if (phoneInput == null || phoneInput.isEmpty()) {
            phone.setError("Can we have your number?");
            errorMessage.setText("What's your phone number?");
            return;
        }
        userAttributes.addAttribute("phone_number", phoneInput);

        emailInput = email.getText().toString().trim();
        if (emailInput == null || emailInput.isEmpty()) {
            email.setError("You need an e-mail for Nao.");
            errorMessage.setText("You forgot to tell us your e-mail.");
            return;
        }
        userAttributes.addAttribute("email", emailInput);

        passwordInput = password.getText().toString();
        if (passwordInput == null || passwordInput.isEmpty()) {
            password.setError("You need at least 8 characters with: " +
                    "\n 1. One lower-case letter." +
                    "\n 2. One upper-case letter." +
                    "\n 3. One number." +
                    "\nThanks!");
            errorMessage.setText("Your passwordEditText isn't that secure.");
            return;
        }

        AmazonHelper.getPool().signUpInBackground(usernameInput, passwordInput, userAttributes, null, signUpHandler);
    }

    private void goToConfirm(CognitoUserCodeDeliveryDetails c) {
        Intent intent = new Intent(this, ConfirmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("usernameEditText", usernameInput);
        intent.putExtra("destination", c.getDestination());
        intent.putExtra("medium", c.getDeliveryMedium());
        intent.putExtra("attribute", c.getAttributeName());
        startActivity(intent);
    }

    public void goToLogin(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
