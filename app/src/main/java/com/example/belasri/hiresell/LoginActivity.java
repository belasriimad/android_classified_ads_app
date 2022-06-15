package com.example.belasri.hiresell;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;


public class LoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AppCompatEditText  Email,Password;
    private TextInputLayout emailLayout,passwordLayout;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private RelativeLayout layout;
    private TextView loginText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layout = (RelativeLayout) findViewById(R.id.loginLayout);
        progressBar = new ProgressBar(LoginActivity.this,null,android.R.attr.progressBarStyleLarge);
        Email = (AppCompatEditText) findViewById(R.id.pseudo);
        Password = (AppCompatEditText) findViewById(R.id.password);
        loginText = (TextView) findViewById(R.id.link_signup);
        emailLayout = (TextInputLayout) findViewById(R.id.text_email_layout);
        passwordLayout = (TextInputLayout) findViewById(R.id.text_password_layout);
        mAuth = FirebaseAuth.getInstance();
        goToLogin();
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
    public Boolean checkField(){
        Boolean valid = true;
        if(Email.getText().toString().isEmpty()){
            emailLayout.setErrorEnabled(true);
            emailLayout.setError("This field is required");
            valid = false;
        }else{
            emailLayout.setErrorEnabled(false);
        }
        if(Password.getText().toString().isEmpty()){
            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError("This field is required");
            valid = false;
        }else{
            passwordLayout.setErrorEnabled(false);
        }
        return valid;
    }
    public void logUser(View view) {
        if(checkField()){
            authUser();
        }
    }
    private void authUser() {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbar);
        String email = Email.getText().toString();
        String passord = Password.getText().toString();
        showProgressBar();
        mAuth.signInWithEmailAndPassword(email,passord).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    layout.removeView(progressBar);
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Logged in", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    },2000);
                    Intent loginIntent = new Intent(LoginActivity.this,MainActivity.class);
                    finish();
                    startActivity(loginIntent);
                }else{
                    progressBar.setVisibility(View.GONE);
                    layout.removeView(progressBar);
                    Toast.makeText(LoginActivity.this,"Invalid credentials try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showProgressBar(){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar,params);
        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar// To Hide ProgressBar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    private void goToLogin(){
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                finish();
                startActivity(registerIntent);
            }
        });
    }
}
