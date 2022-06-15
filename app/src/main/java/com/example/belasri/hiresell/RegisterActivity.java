package com.example.belasri.hiresell;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    AppCompatEditText fullName,Email,Password;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private TextInputLayout fullnameLayout,emailLayout,passwordLayout;
    private DatabaseReference db;
    private ProgressBar progressBar;
    private RelativeLayout layout;
    private TextView registerText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        layout = (RelativeLayout) findViewById(R.id.registerLayout);
        progressBar = new ProgressBar(RegisterActivity.this,null,android.R.attr.progressBarStyleLarge);
        fullName = (AppCompatEditText) findViewById(R.id.fullname);
        Email = (AppCompatEditText) findViewById(R.id.email);
        Password = (AppCompatEditText) findViewById(R.id.password);
        fullnameLayout = (TextInputLayout) findViewById(R.id.text_fullname_layout);
        passwordLayout = (TextInputLayout) findViewById(R.id.text_password_layout);
        emailLayout = (TextInputLayout) findViewById(R.id.text_email_layout);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("profiles");
        setSupportActionBar(toolbar);
        registerText = (TextView) findViewById(R.id.link_signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        goToRegister();
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
    public void checkField(){
        Boolean cancel = false;
        if(fullName.getText().toString().isEmpty()){
            fullnameLayout.setErrorEnabled(true);
            fullnameLayout.setError("This field is required");
            cancel = true;
        }else{
            fullnameLayout.setErrorEnabled(false);
            cancel = false;
        }
        if(Password.getText().toString().isEmpty()){
            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError("This field is required");
            cancel = true;
        }else{
            passwordLayout.setErrorEnabled(false);
            cancel = false;
        }
        if(Email.getText().toString().isEmpty()){
            emailLayout.setErrorEnabled(true);
            emailLayout.setError("This field is required");
            cancel = true;
        }else{
            emailLayout.setErrorEnabled(false);
            cancel = false;
        }
        if(!cancel){
            addUser();
        }
    }
    public void registerNewUser(View v){
        checkField();
    }
    public void addUser(){
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbar);
        final String fullname = fullName.getText().toString();
        final String email = Email.getText().toString();
        String password = Password.getText().toString();
        showProgressBar();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    UserInfo user = mAuth.getCurrentUser();
                    DatabaseReference current_user_profile = db.child(user.getUid());
                    current_user_profile.child("user_id").setValue(user.getUid());
                    current_user_profile.child("fullname").setValue(fullname);
                    progressBar.setVisibility(View.GONE);
                    layout.removeView(progressBar);
                    Email.setText("");
                    Password.setText("");
                    fullName.setText("");
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Acoount created", Snackbar.LENGTH_LONG).setAction("Login", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                            finish();
                            startActivity(loginIntent);
                        }
                    });
                    snackbar.show();
                }else{
                    progressBar.setVisibility(View.GONE);
                    layout.removeView(progressBar);
                    Toast.makeText(RegisterActivity.this,"Erreur : " + task.getException(), Toast.LENGTH_SHORT).show();
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
    private void goToRegister(){
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                finish();
                startActivity(registerIntent);
            }
        });
    }
}
