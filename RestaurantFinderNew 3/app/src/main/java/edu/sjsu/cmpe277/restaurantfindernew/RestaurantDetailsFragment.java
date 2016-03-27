package edu.sjsu.cmpe277.restaurantfindernew;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import static edu.sjsu.cmpe277.restaurantfindernew.RestaurantDetailsActivity.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import java.text.Normalizer;
import java.util.regex.Pattern;

import edu.sjsu.cmpe277.favouriterestaurantdb.DBHandler;
import edu.sjsu.cmpe277.favouriterestaurantdb.Restaurant;

/**
 * Created by ram.mandadapu on 3/26/16.
 */
public class RestaurantDetailsFragment extends Fragment {

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

    public RestaurantDetailsFragment(){

    }

    public  static  RestaurantDetailsFragment newInstance(int pageNumber,String restaurantId){
        RestaurantDetailsFragment fragment=new RestaurantDetailsFragment();
        Bundle bundle=new Bundle();
        bundle.putString(TAG_RES_ID, restaurantId);
        fragment.setArguments(bundle);
        return  fragment;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_restaurant_details,container,false);

        nameView = (TextView) view.findViewById(R.id.business_name_detail);
        addressView = (TextView) view.findViewById(R.id.address_detail);
        phoneView = (TextView) view.findViewById(R.id.phone_detail);
        reviewCountView = (TextView) view.findViewById(R.id.review_count);
        staticMap = (ImageView) view.findViewById(R.id.static_map);
        ratingImage = (ImageView) view.findViewById(R.id.rating_detail);
        favouriteImage=(ImageView)view.findViewById(R.id.favoriteImage);
        dbHandler=new DBHandler(getActivity(), null, null, 0);
        favouriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String businessId = getArguments().getString(TAG_RES_ID);
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
                    Toast.makeText(getContext(), "Added to Favourites", Toast.LENGTH_SHORT).show();

                } else {
                    dbHandler.deleteRestaurant(businessId);
                    toggleFavouriteImage(false);
                    Toast.makeText(getContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show();
                }

            }
        });

        final String businessId = getArguments().getString(TAG_RES_ID);
        //Toast.makeText(getContext(),"id:"+businessId,Toast.LENGTH_LONG).show();
        if(businessId != null) {

            new AsyncTask<String, String, HashMap>() {

                @Override
                protected void onPreExecute(){
                    super.onPreExecute();
                    //progressDialog = new ProgressDialog(getActivity());
                    //progressDialog.setMessage("Loading...");
                    //progressDialog.show();
                }

                @Override
                protected HashMap doInBackground(String... params) {
                    Yelp yelp = Yelp.getYelp(getActivity());
                    String restaurantDetails = yelp.searchBusiness(deAccent(businessId));
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
                    //progressDialog.dismiss();
                    try {
                        System.out.println("Hiiiiiiiiii");
                        nameView.setText(map.get(TAG_RES_NAME).toString());
                        addressView.setText(map.get(TAG_RES_ADDRESS).toString());
                        phoneView.setText(map.get(TAG_RES_PHONE).toString());
                        reviewCountView.setText(map.get(TAG_RES_REVIEWCOUNT).toString());

                        imageUrl = map.get(TAG_RES_BUSINESSIMAGE).toString();
                        ratingImageListUrl = map.get(TAG_RES_RATINGIMAGE_LIST).toString();


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
        return view;
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

    public String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }
}
