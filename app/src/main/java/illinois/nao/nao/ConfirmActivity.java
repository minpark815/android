package illinois.nao.nao;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmActivity extends AppCompatActivity {
    @BindView(R.id.editText_confirm) EditText confirmText;
    @BindView(R.id.button_confirm) TextView confirmResend;
    @BindView(R.id.confirm_username) TextView usernameText;
    @BindView(R.id.confirm_button_resend) TextView bottomText;
    @BindView(R.id.confirm_description) TextView description;

    private String username;
    private String destination;
    private String medium;
    private String attribute;
    private String confirmationCode;
    private GenericHandler verHandler;
    private VerificationHandler verReqHandler;

    private Context context;

    private boolean sentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        ButterKnife.bind(this);
        context = this;
        Intent extras = getIntent();
        destination = extras.getStringExtra("destination");
        medium = extras.getStringExtra("medium");
        attribute = extras.getStringExtra("attribute");
        username = extras.getStringExtra("usernameEditText");
        usernameText.setText(username);

        description.setText("A confirmation code has been sent to a phone number ending in "
                + destination.substring(destination.length() - 4, destination.length())
                + " via " + medium);
        sentText = false;

        verHandler = new GenericHandler() {
            @Override
            public void onSuccess() {
                // Refresh the screen
                goToLogin();
            }

            @Override
            public void onFailure(Exception exception) {
                // Show error
                Log.i("Verification", "FAIL: " + exception.getMessage());
            }
        };

        verReqHandler = new VerificationHandler() {
            @Override
            public void onSuccess(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                // Show message
                // It was sent!
            }

            @Override
            public void onFailure(Exception exception) {
                // Show error
                // Was not sent!
            }
        };
    }

    public void verifyUser(View v) {
        confirmationCode = confirmText.getText().toString();

        if (confirmationCode == null || confirmationCode.isEmpty()) {
            // Handle Error
        } else {
            AmazonHelper.getPool().getUser(username)
                    .confirmSignUpInBackground(confirmationCode, false, verHandler);
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    public void resendText(View v) {
        if(!sentText) {
            bottomText.setText("Text sent!");
            sentText = true;
            AmazonHelper.getPool().getUser(username)
                    .resendConfirmationCodeInBackground(verReqHandler);
        }
    }
}
