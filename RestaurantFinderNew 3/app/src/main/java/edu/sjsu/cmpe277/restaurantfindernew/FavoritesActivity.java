package edu.sjsu.cmpe277.restaurantfindernew;

import android.app.Notification;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.app.ActionBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sjsu.cmpe277.favouriterestaurantdb.DBHandler;

public class FavoritesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SearchView.OnQueryTextListener, GoogleApiClient.OnConnectionFailedListener {

    ListView list;
    SearchView searchView;
    String searchTerm = "";
    DBHandler dbHandler;
    CustomListAdapter adapter;

    //To store list of businesses (restaurants)
    ArrayList<HashMap<String, String>> oslist;
    GoogleApiClient mGoogleApiClient;
    public final static int PLACE_PICKER_REQUEST = 1;

    //JSON node names
    public static final String TAG_RES_ID = "id";
    public static final String TAG_RES_NAME = "name";
    public static final String TAG_RES_LOCATION = "location";
    public static final String TAG_RES_CITY = "city";
    public static final String TAG_RES_ADDRESS = "display_address";
    public static final String TAG_RES_BUSINESSIMAGE = "image_url";
    public static final String TAG_RES_RATINGIMAGE = "rating_img_url";

    private String location = "san jose";
    private LatLng latLng;
    private LatLngBounds latLngBounds;
    private String locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //actionBar.setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("Favorites");

        dbHandler=new DBHandler(this,null,null,0);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        //navigation drawer auto-generated code
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get widget instances
        list = (ListView) findViewById(R.id.restaurants_list);
        oslist = new ArrayList<HashMap<String, String>>();

        View emptyView = findViewById(R.id.no_results_image);
        list.setEmptyView(emptyView);
        //list.setTextFilterEnabled(true);

        navigationView.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("searchView",searchView.getQuery().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        showFavourites();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        searchTerm = savedInstanceState.getString("searchView").toString();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String newText)
    {
        adapter.getFilter().filter(newText);
        return false;
    }


    //auto generated code for nav drawer
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);

        MenuItem sortItem = menu.findItem(R.id.sort);
        sortItem.setVisible(false);

        MenuItem placePickerItem = menu.findItem(R.id.place_picker);
        placePickerItem.setVisible(false);

        /** Get the action view of the menu item whose id is search */
        MenuItem searchViewItem = menu.findItem(R.id.search_view);
        View v = (View) searchViewItem.getActionView();

        /** Get the edit text from the action view */
        //SearchView searchView1 = ( SearchView ) v.findViewById(R.id.search_view);
        if(v instanceof SearchView) {
            searchView = (SearchView) v;

            searchView.setIconifiedByDefault(false);
            //searchView.setSubmitButtonEnabled(true);
            searchView.setQueryHint("Search within favorites");
            searchView.setOnQueryTextListener(this);

            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.setQuery(searchTerm, true);
            //searchView.setQuery(searchView.getQuery().toString(), true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.place_picker) {
            displayPlacePicker();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            //searchView.setQuery("Restaurants",true);
            Intent intent = new Intent(this, NavActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_favorites) {
            showFavourites();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showFavourites(){

        ArrayList<HashMap<String, String>> restaurantList= dbHandler.getAllRestaurants();
        final ArrayList<String> busineesIdList = dbHandler.getAllRestaurantIds();
        final ArrayList<String> busineesNameList = dbHandler.getAllRestaurantNames();
        /*ListAdapter adapter = new SimpleAdapter(NavActivity.this, restaurantList,
                R.layout.restaurant_item,
                new String[]{TAG_RES_NAME, TAG_RES_ADDRESS}, new int[]{
                R.id.name_list, R.id.address_list});*/

        adapter = new CustomListAdapter(FavoritesActivity.this, restaurantList);

        final ArrayList<HashMap<String, String>> restaurants=restaurantList;
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ArrayList<HashMap<String, String>> filteredData = adapter.getFilteredData();
                //Toast.makeText(NavActivity.this, "You Clicked at " + oslist.get(+position).get("id"), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FavoritesActivity.this, FavoritesDetailsSwipeActivity.class);
                intent.putExtra(TAG_RES_ID, filteredData.get(+position).get(TAG_RES_ID));
                intent.putExtra("list", busineesIdList);
                intent.putExtra("namesList", busineesNameList);
                startActivity(intent);
            }
        });
        if(searchView != null) {
            adapter.getFilter().filter(searchView.getQuery().toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_LONG).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            displayPlace(PlacePicker.getPlace(data, this));
        }
    }

    private void displayPlace(Place place) {
        if (place == null) {
            Toast.makeText(getApplicationContext(), "Place is Null", Toast.LENGTH_LONG).show();
            return;
        }

        //TODO: Remove this
        String content = "";
        if (!TextUtils.isEmpty(place.getName())) {
            content += "Name: " + place.getName() + "\n";
        }

        if (!TextUtils.isEmpty(place.getAddress())) {
            content += "Address: " + place.getAddress() + "\n";
        }
        if (!TextUtils.isEmpty(place.getPhoneNumber())) {
            content += "Phone: " + place.getPhoneNumber();
        }
        Toast.makeText(getApplicationContext(), content, Toast.LENGTH_LONG).show();
        //till this point


        if (!TextUtils.isEmpty(place.getName())) {
            locationName=  place.getName().toString() ;
        }
        if (!TextUtils.isEmpty(place.getAddress())) {
            location = place.getAddress().toString();
        }
        if (null != place.getLatLng()) {
            latLng = place.getLatLng();
        }
        if (null != place.getViewport()) {
            latLngBounds = place.getViewport();
        }

    }

    private void displayPlacePicker() {
        /*if( mGoogleApiClient == null || !mGoogleApiClient.isConnected() )
            return;*/

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();


        try {
            startActivityForResult(builder.build(this), NavActivity.PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d("PlacesAPI Demo", "GooglePlayServicesRepairableException thrown");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d("PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown");
        }
    }
}
