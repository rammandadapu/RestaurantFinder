package edu.sjsu.cmpe277.favouriterestaurantdb;

/**
 * Created by ram.mandadapu on 3/20/16.
 */
public class Restaurant {

    String businessId;
    String location;
    String phone;
    String address;
    String name;
    String url;
    String ratingUrl;

    public Restaurant() {
    }

    public Restaurant(String businessId, String location, String phone, String address, String name, String url, String ratingUrl) {
        this.businessId = businessId;
        this.location = location;
        this.phone = phone;
        this.address = address;
        this.name = name;
        this.url = url;
        this.ratingUrl = ratingUrl;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRatingUrl() {
        return ratingUrl;
    }

    public void setRatingUrl(String ratingUrl) {
        this.ratingUrl = ratingUrl;
    }
}
