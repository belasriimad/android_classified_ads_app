package com.example.belasri.hiresell;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import models.AdInfo;

import static com.example.belasri.hiresell.R.id.liste;

public class SearchResult extends AppCompatActivity {
    public final static String data = "data";
    private Toolbar toolbar;
    private TextView userHeaderName,userEmailName;
    private ImageView HeaderProfileImage;
    private DatabaseReference adsDb;
    private RecyclerView recyclerview;
    private RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        linearManager.setOrientation(linearManager.VERTICAL);
        recyclerview.setLayoutManager(new GridLayoutManager(this,2));
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        adsDb = FirebaseDatabase.getInstance().getReference().child("Ads");
        relativeLayout = (RelativeLayout) findViewById(R.id.emptySearch);
        String query = getSavedData(data);
        Log.d("querstored",query);
        showAds(query);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showAds(String query){
        FirebaseRecyclerAdapter<AdInfo, AdsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AdInfo, AdsViewHolder>(
                AdInfo.class,
                R.layout.list_item,
                AdsViewHolder.class,
                adsDb.orderByChild("title").startAt(query).endAt(query+"\uf8ff")
        ) {
            @Override
            protected void onDataChanged() {
                super.onDataChanged();
                if(getItemCount() == 0){
                    recyclerview.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                }else {
                    recyclerview.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.GONE);
                }

            }

            @Override
            protected void populateViewHolder(AdsViewHolder viewHolder, AdInfo model, int position) {
                //get selected post id
                final String ad_id = getRef(position).getKey().toString();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setCity(model.getCity());
                viewHolder.setImage(model.getImageUrl(),getApplicationContext());
                Log.d("SearchedData",model.toString());
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singlePost = new Intent(SearchResult.this,AdDetails.class);
                        singlePost.putExtra("ad_id",ad_id);
                        startActivity(singlePost);
                        finish();
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
    private String getSavedData(String data) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return  prefs.getString(data,"");
    }
}
