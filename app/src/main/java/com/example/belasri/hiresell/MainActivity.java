package com.example.belasri.hiresell;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import helpers.BottomNavigationViewHelper;
import models.AdInfo;
import models.SessionManager;


public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton createAd;
    private BottomNavigationViewHelper bottomNavigationViewHelper;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;
    private TextView userHeaderName,userEmailName;
    private ImageView HeaderProfileImage;
    private DatabaseReference db;
    private DatabaseReference adsDb;
    private RecyclerView recyclerview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer);
        mAuth = FirebaseAuth.getInstance();
        showMenu();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        navigationView = (NavigationView) findViewById(R.id.navView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        createAd = (FloatingActionButton) findViewById(R.id.createBtn);
        navigationView.setNavigationItemSelectedListener(this);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        linearManager.setOrientation(linearManager.VERTICAL);
        recyclerview.setLayoutManager(new GridLayoutManager(this,2));
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        db = FirebaseDatabase.getInstance().getReference().child("profiles");
        adsDb = FirebaseDatabase.getInstance().getReference().child("Ads");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        showAds();
        createNewAd();
    }
    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(authStateListener);
        super.onStart();
    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.home :
                Intent homeIntent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(homeIntent);
                break;
            case R.id.fav :
                Intent favsIntent = new Intent(MainActivity.this,FavActivity.class);
                startActivity(favsIntent);
                break;
            case R.id.register :
                Intent registerIntent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.login :
                Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.profile :
                Intent profileIntent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(profileIntent);
                break;
            case R.id.search :
                Intent searchIntent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(searchIntent);
                break;
            case R.id.logout :
                mAuth.signOut();
                Intent home = new Intent(MainActivity.this,MainActivity.class);
                startActivity(home);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    public void createNewAd(){
        createAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createAd = new Intent(MainActivity.this,CreateAds.class);
                startActivity(createAd);
            }
        });
    }
    private void showAds(){
        FirebaseRecyclerAdapter<AdInfo, AdsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AdInfo, AdsViewHolder>(
                AdInfo.class,
                R.layout.list_item,
                AdsViewHolder.class,
                adsDb
        ) {
            @Override
            protected void populateViewHolder(AdsViewHolder viewHolder, AdInfo model, int position) {
                //get selected post id
                final String ad_id = getRef(position).getKey().toString();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setCity(model.getCity());
                viewHolder.setImage(model.getImageUrl(),getApplicationContext());
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singlePost = new Intent(MainActivity.this,AdDetails.class);
                        singlePost.putExtra("ad_id",ad_id);
                        startActivity(singlePost);
                    }
                });
            }
        };
        recyclerview.setAdapter(firebaseRecyclerAdapter);
    }
    public static class AdsViewHolder extends RecyclerView.ViewHolder{
        View view;
        public AdsViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
        public void setTitle(String title){
            TextView adTitle = (TextView) view.findViewById(R.id.title);
            adTitle.setText(title);
        }
        public void setCity(String city){
            TextView adCity = (TextView) view.findViewById(R.id.city);
            adCity.setText(city);
        }
        public void setImage(String image,Context ctx){
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            Picasso.with(ctx).load(image).into(imageView);
        }
    }
    public void showMenu(){
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    navigationView.getMenu().findItem(R.id.register).setVisible(true);
                    navigationView.getMenu().findItem(R.id.login).setVisible(true);
                    navigationView.getMenu().findItem(R.id.logout).setVisible(false);
                    navigationView.getMenu().findItem(R.id.profile).setVisible(false);
                    //get header textviews
                    View header =  navigationView.getHeaderView(0);
                    userHeaderName = header.findViewById(R.id.user_header_fullname);
                    userEmailName =  header.findViewById(R.id.user_header_email);
                    HeaderProfileImage = header.findViewById(R.id.user_header_image);
                    HeaderProfileImage.setVisibility(View.INVISIBLE);
                    userHeaderName.setText("");
                    userEmailName.setText("");
                }else{
                    navigationView.getMenu().findItem(R.id.register).setVisible(false);
                    navigationView.getMenu().findItem(R.id.login).setVisible(false);
                    navigationView.getMenu().findItem(R.id.logout).setVisible(true);
                    navigationView.getMenu().findItem(R.id.profile).setVisible(true);
                    //get header textviews
                    View header =  navigationView.getHeaderView(0);
                    userHeaderName = header.findViewById(R.id.user_header_fullname);
                    userEmailName =  header.findViewById(R.id.user_header_email);
                    HeaderProfileImage = header.findViewById(R.id.user_header_image);
                    db.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String fullname = (String) dataSnapshot.child("fullname").getValue();
                            String email = mAuth.getCurrentUser().getEmail();
                            String image = (String) dataSnapshot.child("image_url").getValue();
                            if(image != null) {
                                Picasso.with(MainActivity.this).load(image).into(HeaderProfileImage);
                            }else {
                                HeaderProfileImage.setImageResource(R.drawable.abc56789);
                            }
                            userHeaderName.setText(fullname);
                            userEmailName.setText(email);


                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
    }

}
