package helpers;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.belasri.hiresell.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import models.AdInfo;

/**
 * Created by belasri on 17/12/2017.
 */

public class FavListViewAdapter extends RecyclerView.Adapter<FavListViewAdapter.MyViewHolder>  {
    private List<AdInfo> mData;
    private LayoutInflater mInflater;
    private Activity activity;
    public FavListViewAdapter(Activity context, List<AdInfo>data){
        this.activity = context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AdInfo currentObjs = mData.get(position);
        holder.setData(currentObjs,position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        AdInfo adInfo;
        TextView title;
        TextView city;
        ImageView imageView;
        int position;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            city = (TextView) itemView.findViewById(R.id.city);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }

        public void setData(AdInfo currentObjs, int position) {
            this.title.setText(currentObjs.getTitle());
            this.city.setText(currentObjs.getCity());
            Picasso.with(activity).load(currentObjs.getImageUrl()).into(this.imageView);
            this.position = position;
            this.adInfo = currentObjs;
        }
    }
    public AdInfo getAd(int position){
        return (mData != null) ? mData.get(position) : null;
    }
}
