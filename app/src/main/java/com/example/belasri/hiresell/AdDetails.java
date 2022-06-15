package com.example.belasri.hiresell;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import helpers.BottomNavigationViewHelper;
import models.AdInfo;
import models.DataBaseHandler;
import models.SessionManager;

import static android.R.attr.category;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.belasri.hiresell.R.id.adtitle;

public class AdDetails extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private TextView adTitleText,adBodyText,adCityText,adUserFullname,adCatText;
    private ImageView userProfileImage,adImageView;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton createAd;
    private BottomNavigationViewHelper bottomNavigationViewHelper;
    private String email;
    private String phone;
    private FirebaseDatabase database;
    private DatabaseReference reference,userRef;
    private FirebaseAuth mAuth;
    private String ad_id = null;
    private SessionManager session;
    private String user_stored;
    private DataBaseHandler dba;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.AdDetaisBottomNav);
        bottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        adTitleText = (TextView) findViewById(R.id.adTitle);
        adBodyText = (TextView) findViewById(R.id.adDesc);
        adCityText = (TextView) findViewById(R.id.adCity);
        adUserFullname = (TextView) findViewById(R.id.adUserFullname);
        adCatText = (TextView) findViewById(R.id.adCat);
        adImageView = (ImageView) findViewById(R.id.adImage);
        userProfileImage = (ImageView) findViewById(R.id.profileImage);
        ad_id = getIntent().getStringExtra("ad_id");
        reference = FirebaseDatabase.getInstance().getReference().child("Ads");
        userRef = FirebaseDatabase.getInstance().getReference().child("profiles");
        mAuth = FirebaseAuth.getInstance();
        session = new SessionManager(this);
        dba =  new DataBaseHandler(this);
        createAd = (FloatingActionButton) findViewById(R.id.addAdToFav);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIfFavourite();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
    private void getData(){
        reference.child(ad_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String adtitle = (String) dataSnapshot.child("title").getValue();
                final String adcity = (String) dataSnapshot.child("city").getValue();
                final String addescription = (String) dataSnapshot.child("description").getValue();
                final String  adimage = (String) dataSnapshot.child("imageUrl").getValue();
                String user_ad_id = (String) dataSnapshot.child("user_id").getValue();
                String category = (String) dataSnapshot.child("category").getValue();
                adCityText.setText(adcity);
                adBodyText.setText(addescription);
                adTitleText.setText(adtitle);
                adCatText.setText(category);
                createAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        storeFav(adtitle,addescription,adcity,adimage);
                    }
                });
                setToolbarTitle(adtitle);
                Picasso.with(AdDetails.this).load(adimage).into(adImageView);
                setUserName(user_ad_id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void setUserName(String userId){
        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("fullname").getValue();
                phone = (String) dataSnapshot.child("phone").getValue();
                email = (String) dataSnapshot.child("email").getValue();
                String image = (String) dataSnapshot.child("image_url").getValue();
                adUserFullname.setText(name);
                if(image != null) {
                    Picasso.with(AdDetails.this).load(image).into(userProfileImage);
                }else{
                    userProfileImage.setImageResource(R.drawable.abc56789);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private  void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.callAdUser :
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                if(!phone.equals("")){
                    callIntent.setData(Uri.parse("tel:"+phone));
                    Log.d("useradphone",phone);
                }else{
                    Toast.makeText(this, "Aucun téléphone fournis", Toast.LENGTH_SHORT).show();
                }
                if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    requestPermission();
                }else{
                    startActivity(callIntent);
                }
                break;
            case R.id.SendSms :
                Uri uri = Uri.parse("smsto:"+phone);
                Intent sendSmsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                sendSmsIntent.putExtra("sms_body", "Sms à envoyer");
                startActivity(sendSmsIntent);
                break;
            case R.id.SendEmail :
                Intent sendEmail = new Intent(Intent.ACTION_SEND);
                sendEmail.setData(Uri.parse("email"));
                String[] userEmail = {email};
                sendEmail.putExtra(Intent.EXTRA_EMAIL,userEmail);
                sendEmail.putExtra(Intent.EXTRA_SUBJECT,"le sujet");
                sendEmail.putExtra(Intent.EXTRA_TEXT,"le contenu");
                sendEmail.setType("message/rfc822");
                Intent launchEmail = Intent.createChooser(sendEmail,"Envoyer");
                startActivity(launchEmail);
                break;
        }
        return true;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CALL_PHONE},1);
    }
    private void storeFav(String title,String desc,String city,String image){
        if(!dba.isFavourite(ad_id)){
            dba.addFav(title,desc,city,image,ad_id,getUserId());
            createAd.setImageResource(R.drawable.fav);
            Toast.makeText(this,title +  " ajoutée à vos favoris", Toast.LENGTH_SHORT).show();
            Log.d("favouritestored", dba.getFavs().get(0).getTitle());
        }else{
            dba.deleteFavourite(ad_id);
            createAd.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            Toast.makeText(this,title +  " retirée de vos favoris", Toast.LENGTH_SHORT).show();
        }
    }
    private void checkIfFavourite(){
        if(dba.isFavourite(ad_id)){
            createAd.setImageResource(R.drawable.fav);
        }else{
            createAd.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }
    private String getUserId(){
        if(mAuth.getCurrentUser() == null){
            String user_id;
            if(session.getData().equals("")){
                user_id = generateRandomUserId();
            }else{
                user_id = session.getData();
            }
            return user_id;
        }else{
            return  mAuth.getCurrentUser().getUid();
        }
    }
    private String generateRandomUserId(){
        String user_id = String.valueOf(UUID.randomUUID());
        return user_id;
    }
}
