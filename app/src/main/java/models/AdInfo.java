package models;

import java.io.Serializable;

/**
 * Created by belasri on 25/11/2017.
 */

public class AdInfo implements Serializable {
    private static  final long id = 1L;
    private String title;
    private String desc;
    private String city;
    private String imageUrl;
    private int userId;
    private String AdId;
    public AdInfo(String AdId,String title,String desc, String city,String imageUrl) {
        title = title;
        desc = desc;
        city = city;
        imageUrl = imageUrl;
        AdId = AdId;
    }
    public AdInfo(){

    }

    public static long getId() {
        return id;
    }

    public String getAdId() {
        return AdId;
    }

    public void setAdId(String adId) {
        AdId = adId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {

        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCity() {
        return city;
    }


    public int getUserId() {
        return userId;
    }

}
