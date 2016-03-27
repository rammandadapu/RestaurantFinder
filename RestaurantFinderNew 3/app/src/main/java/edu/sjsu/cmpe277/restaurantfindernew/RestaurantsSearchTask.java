package edu.sjsu.cmpe277.restaurantfindernew;

/**
 * Created by divya.chittimalla on 3/20/16.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.content.Context;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestaurantsSearchTask extends  AsyncTask<String, String, ListAdapter> {
    private Context context;
    ListView list;
    ArrayList<HashMap<String, String>> oslist;
    ArrayList<String>  busineesIdList;
    ArrayList<String> businessNamesList;
    private static final String TAG_RES_ID = "id";
    private static final String TAG_RES_NAME = "name";
    private static final String TAG_RES_LOCATION = "location";
    private static final String TAG_RES_CITY = "city";
    private static final String TAG_RES_ADDRESS = "display_address";
    private static final String TAG_RES_BUSINESSIMAGE = "image_url";
    private static final String TAG_RES_RATINGIMAGE = "rating_img_url";

    public RestaurantsSearchTask(Context context, ListView list) {
        this.context = context.getApplicationContext();
        this.list = list;
        oslist = new ArrayList<HashMap<String, String>>();
        busineesIdList=new ArrayList<>();
        businessNamesList=new ArrayList<>();
    }

    protected ListAdapter doInBackground(String... params) {
        Yelp yelp = Yelp.getYelp(context);
        String businesses;
        if(TextUtils.isEmpty(params[2])) {
            businesses = yelp.searchLoc(params[0], params[1], params[3]);
        }
        else {
            businesses = yelp.searchLatLng(params[0], params[2], params[3]);
        }
        System.out.println(businesses);
        try {
            return processJson(businesses);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ListAdapter adapter) {
        //mSearchResultsText.setText(result);
        //setProgressBarIndeterminateVisibility(false);
        //System.out.println(adapter.getItem(0));
        //System.out.println(adapter.getItem(1));

        //update the list using the adapter created in processJson method
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(context, "You Clicked at " + oslist.get(+position).get("id"), Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent(context, RestaurantDetailsActivity.class);
                intent.putExtra(TAG_RES_ID,oslist.get(+position).get("id"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
                Intent intent = new Intent(context, RestaurantDetailsSwipeActivity.class);
                intent.putExtra(TAG_RES_ID,oslist.get(+position).get("id"));
                intent.putExtra("list",busineesIdList);
                intent.putExtra("namesList",businessNamesList);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);
            }
        });
    }

    ListAdapter processJson(String jsonStuff) throws JSONException {
        JSONObject json = new JSONObject(jsonStuff);
        JSONArray result = json.getJSONArray("businesses");

        try {
                for (int i = 0; i < result.length(); i++) {
                    JSONObject c = result.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String id = c.getString(TAG_RES_ID);
                    String name = c.getString(TAG_RES_NAME);
                    String address = c.getJSONObject(TAG_RES_LOCATION).getJSONArray(TAG_RES_ADDRESS).getString(0);
                    address += ", "+ c.getJSONObject(TAG_RES_LOCATION).getString(TAG_RES_CITY);
                    String businessImage = c.getString(TAG_RES_BUSINESSIMAGE);
                    String ratingImage = c.getString(TAG_RES_RATINGIMAGE);

                    // Adding value HashMap key => value

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_RES_ID, id);
                    map.put(TAG_RES_NAME, name);
                    map.put(TAG_RES_ADDRESS, address);
                    map.put(TAG_RES_BUSINESSIMAGE, businessImage);
                    map.put(TAG_RES_RATINGIMAGE, ratingImage);

                    System.out.println("MAP: " + map.toString());
                    busineesIdList.add(id);
                    businessNamesList.add(name);
                    oslist.add(map);
                }
            ListAdapter adapter = new CustomListAdapter(context, oslist);
            return adapter;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
