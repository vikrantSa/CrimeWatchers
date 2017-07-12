package police2.com.crimewatchers.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import police2.com.crimewatchers.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.spinnerCity)
    Spinner spinnerCity;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.field_email)
    EditText mEmailField;
    @BindView(R.id.field_password)
    EditText mPasswordField;
    @BindView(R.id.button_sign_in)
    Button mSignInButton;
    @BindView(R.id.input_layout_name)
    TextInputLayout input_layout_name;
    @BindView(R.id.input_layout_password)
    TextInputLayout input_layout_password;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        Boolean bool = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("firebase", true);
        if (!bool) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("firebase", true).apply();
        }

        mAuth = FirebaseAuth.getInstance();

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.city_array, R.layout.spinner_item);
        spinnerCity.setAdapter(adapter);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.state_array, R.layout.spinner_item);
        spinner.setAdapter(adapter2);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    @OnClick(R.id.button_sign_in)
    public void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            input_layout_name.setError("Required");
            result = false;
        } else {
            input_layout_name.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            input_layout_password.setError("Required");
            result = false;
        } else {
            input_layout_password.setError(null);
        }

        return result;
    }
}
