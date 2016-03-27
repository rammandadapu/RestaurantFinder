package edu.sjsu.cmpe277.restaurantfindernew;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.ImageView;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import java.util.HashMap;

import edu.sjsu.cmpe277.favouriterestaurantdb.DBHandler;
import edu.sjsu.cmpe277.favouriterestaurantdb.Restaurant;

public class RestaurantDetailsActivity extends AppCompatActivity {

    //Node names
     static final String TAG_RES_NAME = "name";
     static final String TAG_RES_LOCATION = "location";
     static final String TAG_RES_CITY = "city";
     static final String TAG_RES_ADDRESS = "address";
     static final String TAG_RES_PHONE = "display_phone";
     static final String TAG_RES_REVIEWCOUNT = "review_count";
     static final String TAG_RES_COORDINATE = "coordinate";
     static final String TAG_RES_LATITUDE = "latitude";
     static final String TAG_RES_LONGITUDE = "longitude";
     static final String TAG_RES_RATINGIMAGE = "rating_img_url_large";
     static final String TAG_RES_RATINGIMAGE_LIST = "rating_img_url";
     static final String TAG_RES_BUSINESSIMAGE = "image_url";
     static final String TAG_RES_ID = "id";

    TextView nameView;
    TextView addressView;
    TextView phoneView;
    TextView reviewCountView;
    ImageView staticMap;
    ImageView ratingImage;
    ImageView favouriteImage;

    String imageUrl;
    String ratingImageListUrl;

    DBHandler dbHandler;


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        dbHandler=new DBHandler(this,null,null,0);

        nameView = (TextView) findViewById(R.id.business_name_detail);
        addressView = (TextView) findViewById(R.id.address_detail);
        phoneView = (TextView) findViewById(R.id.phone_detail);
        reviewCountView = (TextView) findViewById(R.id.review_count);
        staticMap = (ImageView) findViewById(R.id.static_map);
        ratingImage = (ImageView) findViewById(R.id.rating_detail);
        favouriteImage=(ImageView)findViewById(R.id.favoriteImage);


        favouriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                final String businessId = extras.getString(TAG_RES_ID);
                if (!dbHandler.isFavourite(businessId)) {
                    Restaurant restaurant = new Restaurant();
                    restaurant.setBusinessId(businessId);
                    restaurant.setAddress(addressView.getText().toString());
                    restaurant.setName(nameView.getText().toString());
                    restaurant.setPhone(nameView.getText().toString());
                    restaurant.setUrl(imageUrl);
                    restaurant.setRatingUrl(ratingImageListUrl);
                    dbHandler.addRestaurant(restaurant);
                    toggleFavouriteImage(true);
                    Toast.makeText(getApplicationContext(), "Added to Favourites", Toast.LENGTH_SHORT).show();

                } else {
                    dbHandler.deleteRestaurant(businessId);
                    toggleFavouriteImage(false);
                    Toast.makeText(getApplicationContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            final String businessId = extras.getString(TAG_RES_ID);
            new AsyncTask<String, String, HashMap>() {

                @Override
                protected void onPreExecute(){
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(RestaurantDetailsActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }

                @Override
                protected HashMap doInBackground(String... params) {
                    Yelp yelp = Yelp.getYelp(RestaurantDetailsActivity.this);
                    String restaurantDetails = yelp.searchBusiness(businessId);
                    System.out.println(restaurantDetails);
                    try {
                        return processJson(restaurantDetails);
                    } catch (JSONException e) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(HashMap map) {
                    super.onPostExecute(map);
                    progressDialog.dismiss();
                    try {
                        System.out.println("Hiiiiiiiiii");
                        nameView.setText(map.get(TAG_RES_NAME).toString());
                        addressView.setText(map.get(TAG_RES_ADDRESS).toString());
                        phoneView.setText(map.get(TAG_RES_PHONE).toString());
                        reviewCountView.setText(map.get(TAG_RES_REVIEWCOUNT).toString());

                        imageUrl = map.get(TAG_RES_BUSINESSIMAGE).toString();
                        ratingImageListUrl = map.get(TAG_RES_RATINGIMAGE_LIST).toString();

                        getSupportActionBar().setTitle(map.get(TAG_RES_NAME).toString());

                        if(dbHandler.isFavourite(businessId)) {
                            toggleFavouriteImage(true);
                        }

                        String latitude = map.get(TAG_RES_LATITUDE).toString();
                        String longitude = map.get(TAG_RES_LONGITUDE).toString();

                        String staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?center="+latitude+","+longitude+"&zoom=17&size=450x225&maptype=roadmap&markers=color:red%7Clabel:D%7C"+latitude+","+longitude;

                        new ImageDownloaderTask(staticMap).execute(staticMapURL);

                        String ratingImageURL = map.get(TAG_RES_RATINGIMAGE).toString();

                        new ImageDownloaderTask(ratingImage).execute(ratingImageURL);

                    } catch(NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    HashMap processJson(String jsonStuff) throws JSONException {
        JSONObject json = new JSONObject(jsonStuff);

        try {
            // Storing  JSON item in a Variable
            String name = json.getString(TAG_RES_NAME);
            String address = json.getJSONObject(TAG_RES_LOCATION).getJSONArray(TAG_RES_ADDRESS).getString(0);
            address += ", "+ json.getJSONObject(TAG_RES_LOCATION).getString(TAG_RES_CITY);
            String phone = json.getString(TAG_RES_PHONE);
            String reviewCount = json.getString(TAG_RES_REVIEWCOUNT);
            String ratingImageURL = json.getString(TAG_RES_RATINGIMAGE);
            String businessImageURL = json.getString(TAG_RES_BUSINESSIMAGE);
            String ratingImageListURL = json.getString(TAG_RES_RATINGIMAGE_LIST);
            String latitude = json.getJSONObject(TAG_RES_LOCATION).getJSONObject(TAG_RES_COORDINATE).getString(TAG_RES_LATITUDE);
            String longitude = json.getJSONObject(TAG_RES_LOCATION).getJSONObject(TAG_RES_COORDINATE).getString(TAG_RES_LONGITUDE);

            //String image_url = c.getString("image_url");

            // Adding value HashMap key => value

            HashMap<String, String> map = new HashMap<String, String>();

            map.put(TAG_RES_NAME, name);
            map.put(TAG_RES_ADDRESS, address);
            map.put(TAG_RES_PHONE, phone);
            map.put(TAG_RES_REVIEWCOUNT, reviewCount);
            map.put(TAG_RES_RATINGIMAGE, ratingImageURL);
            map.put(TAG_RES_BUSINESSIMAGE, businessImageURL);
            map.put(TAG_RES_RATINGIMAGE_LIST, ratingImageListURL);
            map.put(TAG_RES_LATITUDE, latitude);
            map.put(TAG_RES_LONGITUDE, longitude);
            //map.put("image_url", image_url);

            System.out.println("MAP: " + map.toString());
            return map;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void toggleFavouriteImage(boolean flag){
        if(flag)
            favouriteImage.setImageResource(R.mipmap.full_heart);
        else
            favouriteImage.setImageResource(R.mipmap.empty_heart);
    }
}
