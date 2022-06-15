package com.example.belasri.hiresell;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.os.Build.VERSION_CODES.M;
import static com.example.belasri.hiresell.R.drawable.user;
import static com.example.belasri.hiresell.R.id.fullname;

public class ProfileActivity extends AppCompatActivity {
    private AppCompatEditText EditFullName,EditPhone;
    private TextInputLayout fullnameLayout,phoneeLayout;
    private DatabaseReference db;
    private ProgressBar progressBar;
    private RelativeLayout layout;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;
    private TextView TextUserFullName,TextuUserEmail,TextUserPhone;
    private StorageReference storageReference;
    private  static final int RESULT_IMAGE = 1;
    Uri resultUri;
    private ImageView profileButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("profiles");
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images");
        layout = (RelativeLayout) findViewById(R.id.profileLayout);
        progressBar = new ProgressBar(ProfileActivity.this,null,android.R.attr.progressBarStyleLarge);
        TextUserFullName = (TextView) findViewById(R.id.user_profile_name);
        TextuUserEmail = (TextView) findViewById(R.id.user_profile_email);
        TextUserPhone = (TextView) findViewById(R.id.user_profile_txtphone);
        EditFullName = (AppCompatEditText) findViewById(R.id.user_profile_fullname);
        EditPhone = (AppCompatEditText) findViewById(R.id.user_profile_phone);
        profileButton = (ImageView) findViewById(R.id.profile_image_btn);
        showData();
        setUserProfileImage(mAuth.getCurrentUser().getUid());
    }
    public void uploadProfileImage(View view) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,RESULT_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_IMAGE && resultCode == RESULT_OK){
            Uri imagePath = data.getData();
            //make the user crop the image
            CropImage.activity(imagePath).setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        //if image has successufully croped
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            //get result
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                resultUri = result.getUri();
                profileButton.setImageURI(resultUri);
                profileButton.setTag(resultUri);
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
    }
    public void addUserInfo(View view) {
        final String username = EditFullName.getText().toString();
        final String phone = EditPhone.getText().toString();
        String backgroundImageName = String.valueOf(profileButton.getTag());
        //Log.d("profilebuttontag",profileButton.getTag().toString());
        //Log.d("tagstring",tag);
        if(!backgroundImageName.equals("profileDefaultImage")){
            StorageReference filePath = storageReference.child(resultUri.getLastPathSegment());
            if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(phone) && profileButton.getDrawable() != null){
                Log.d("pathImage",resultUri.toString());
                showProgressBar();
                //store image
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") String imageUrl = taskSnapshot.getDownloadUrl().toString();
                        UserInfo user = mAuth.getCurrentUser();
                        DatabaseReference current_user_profile = db.child(user.getUid());
                        current_user_profile.child("fullname").setValue(username);
                        current_user_profile.child("phone").setValue(phone);
                        current_user_profile.child("image_url").setValue(imageUrl);
                        progressBar.setVisibility(View.GONE);
                        layout.removeView(progressBar);
                        Intent homeIntent = new Intent(ProfileActivity.this,MainActivity.class);
                        finish();
                        startActivity(homeIntent);
                    }
                });
            }else{
                Toast.makeText(this,"Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            }
        }else{
            if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(phone) && profileButton.getDrawable() != null){
                showProgressBar();
                UserInfo user = mAuth.getCurrentUser();
                DatabaseReference current_user_profile = db.child(user.getUid());
                current_user_profile.child("fullname").setValue(username);
                current_user_profile.child("phone").setValue(phone);
                progressBar.setVisibility(View.GONE);
                layout.removeView(progressBar);
                Intent homeIntent = new Intent(ProfileActivity.this,MainActivity.class);
                finish();
                startActivity(homeIntent);
            }else{
                Toast.makeText(this,"Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void showData(){
        db.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fullname = (String) dataSnapshot.child("fullname").getValue();
                String phone = (String) dataSnapshot.child("phone").getValue();
                String email = mAuth.getCurrentUser().getEmail();
                TextUserFullName.setText(fullname);
                TextuUserEmail.setText(email);
                TextUserPhone.setText(phone);
                EditFullName.setText(fullname);
                EditPhone.setText(phone);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

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
    public void setUserProfileImage(String userId){
        db.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String image = (String) dataSnapshot.child("image_url").getValue();
                if(image != null) {
                    Picasso.with(ProfileActivity.this).load(image).into(profileButton);
                }else {
                    profileButton.setImageResource(R.drawable.abc56789);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
