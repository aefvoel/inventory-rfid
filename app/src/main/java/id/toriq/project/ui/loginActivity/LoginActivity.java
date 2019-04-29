package id.toriq.project.ui.loginActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import id.toriq.project.R;
import id.toriq.project.helper.Constant;
import id.toriq.project.ui.mainActivity.DashboardActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = "LoginActivity";

    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;
    @BindView(R.id.username_input)
    EditText usernameField;
    @BindView(R.id.password_input)
    EditText passwordField;
    @BindView(R.id.login_button)
    Button loginBtn;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public FirebaseAuth firebaseAuth;
    public boolean isLoggedIn(){
        return sharedpref.getBoolean(Constant.IS_LOGIN, false);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sharedpref = getSharedPreferences(Constant.SHAREDPREF_NAME, MODE_PRIVATE);
        if (isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.login_button)
    public void onBtnClick() {
        loginAction();
    }

    public void loginAction() {
        if (usernameField.getText().toString().equals("")) {
            showMessage("Username Tidak Boleh Kosong!");
        } else if (passwordField.getText().toString().equals("")) {
            showMessage("Password Tidak Boleh Kosong!");
        } else {
            initializeLogin();
        }
    }

    public void showMessage(String message) {
        Toast.makeText(LoginActivity.this,
                message,
                Toast.LENGTH_LONG).show();
    }

    private void initializeLogin() {
        loginBtn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(usernameField.getText().toString(),
                passwordField.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("LoginActivity", "signInWithEmail:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        editor = sharedpref.edit();
                        editor.putString(Constant.NAMA, user.getEmail());
                        editor.putBoolean(Constant.IS_LOGIN, true);
                        editor.apply();
                        loginBtn.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        loginBtn.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
}
