package com.example.belasri.hiresell;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

public class CreateAds extends AppCompatActivity {
    private Spinner citieSpinner,catSpinner;
    private AppCompatEditText titleEdit,descTitle,phoneEdit;
    private Toolbar toolbar;
    ImageButton imageButton;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;
    private  static final int RESULT_IMAGE = 1;
    Uri resultUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_ads);
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = database.getInstance().getReference().child("Ads");
        checkIfLogged();
        citieSpinner = (Spinner) findViewById(R.id.citySpinner);
        catSpinner = (Spinner) findViewById(R.id.categorieSpinner);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageButton = (ImageButton) findViewById(R.id.image_uploaded);
        titleEdit = (AppCompatEditText) findViewById(R.id.adtitle);
        descTitle = (AppCompatEditText) findViewById(R.id.adbody);
        phoneEdit = (AppCompatEditText) findViewById(R.id.userTel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getCitites();
        getCategories();
        citieSpinner.setSelection(0);
        catSpinner.setSelection(0);
    }
    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(authStateListener);
        super.onStart();
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
    public void getCitites(){
        ArrayList<String> cities = new ArrayList<>();
        cities.add("Veuillez choisir une ville");
        cities.add("Fés");
        cities.add("Rabat");
        cities.add("Casablanca");
        cities.add("Taza");
        cities.add("Tanger");
        cities.add("Agadir");
        cities.add("Oujda");
        cities.add("Marrakech");
        final ArrayAdapter<String> spinnnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,cities){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                return view;
            }
        };
        spinnnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        citieSpinner.setAdapter(spinnnerArrayAdapter);
    }
    public void getCategories(){
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Veuillez chosir une catégorie");
        categories.add("Véhicules");
        categories.add("Animaux");
        categories.add("Immobilier");
        categories.add("Informatique");
        categories.add("Emplois");
        categories.add("Vétements");
        categories.add("Ventes divers");
        final ArrayAdapter<String> spinnnerCatArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,categories){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                }
                return view;
            }
        };
        spinnnerCatArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        catSpinner.setAdapter(spinnnerCatArrayAdapter);
    }
    public void uploadAdImage(View view) {
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
                imageButton.setImageURI(resultUri);
                imageButton.setTag(resultUri);
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
    }
    public void checkIfLogged(){
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(CreateAds.this,LoginActivity.class);
                    //prevent user from returning back
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };
    }
    public void createNewAd(View view) {
        final String title = titleEdit.getText().toString().trim();
        final String desc = descTitle.getText().toString().trim();
        final String phone = phoneEdit.getText().toString().trim();
        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(phone)) {
            StorageReference filePath = storageReference.child("images").child(resultUri.getLastPathSegment());
            filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    UserInfo userInfo = mAuth.getCurrentUser();
                    Log.d("userInfo", userInfo.toString());
                    DatabaseReference newPost = databaseReference.push();
                    newPost.child("title").setValue(title);
                    newPost.child("description").setValue(desc);
                    newPost.child("imageUrl").setValue(downloadUrl.toString());
                    newPost.child("user_id").setValue(userInfo.getUid());
                    newPost.child("category").setValue(catSpinner.getSelectedItem().toString());
                    newPost.child("city").setValue(citieSpinner.getSelectedItem().toString());
                    Toast.makeText(CreateAds.this, "Annonce ajoutée avec succés", Toast.LENGTH_SHORT).show();
                    Intent home = new Intent(CreateAds.this, MainActivity.class);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(home);
                }
            });
        }else{
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
        }
    }
}
