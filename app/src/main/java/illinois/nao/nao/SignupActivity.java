package illinois.nao.nao;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private String emailInput;
    private String usernameInput;
    private String phoneInput;
    private String passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        AmazonHelper.init(this);
        init();
    }

    private void init() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read user data and register
                CognitoUserAttributes userAttributes = new CognitoUserAttributes();

                usernameInput = username.getText().toString();
                if (usernameInput == null || usernameInput.isEmpty()) {
                    return;
                }

                passwordInput = password.getText().toString();
                if (passwordInput == null || passwordInput.isEmpty()) {
                    return;
                }

                emailInput = email.getText().toString();
                if (emailInput == null) {
                    return;
                }
                userAttributes.addAttribute("email", emailInput);

                phoneInput = phone.getText().toString();
                if (phoneInput == null) {
                    return;
                }
                userAttributes.addAttribute("phone_number", phoneInput);

                AmazonHelper.getPool().signUpInBackground(usernameInput, passwordInput, userAttributes, null, signUpHandler);
            }
        });
    }

    SignUpHandler signUpHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                              CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {

            Boolean regState = signUpConfirmationState;
            if (regState) {
                // User is already confirmed
                Log.i("signup", "Success");
            }
            else {
                Log.i("signup", "Fail");
            }
        }

        @Override
        public void onFailure(Exception exception) {
            Log.i("signup", "FAILUREE");
            Log.i("signup", exception.toString());
        }
    };
}
