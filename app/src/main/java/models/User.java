package models;

/**
 * Created by belasri on 03/12/2017.
 */

public class User {
    private String fullname;
    private String email;
    private String password;
    private int city_id;
    private String adress;
    private int tel;
    private String user_id;

    public User(String user_id,String fullname, String email,String password,int city_id,int tel,String adress){
        user_id = user_id;
        fullname = fullname;
        email = email;
        password = password;
        city_id = city_id;
        tel = tel;
        adress = adress;
    }
    public User(){

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public int getTel() {
        return tel;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }
}
