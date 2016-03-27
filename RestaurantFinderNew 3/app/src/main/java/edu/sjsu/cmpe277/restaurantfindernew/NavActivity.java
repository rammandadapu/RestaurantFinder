package edu.sjsu.cmpe277.restaurantfindernew;

import android.app.Notification;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.view.inputmethod.InputMethodManager;

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

public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SearchView.OnQueryTextListener, GoogleApiClient.OnConnectionFailedListener {

    ListView list;
    SearchView searchView;
    String searchTerm = "";
    DBHandler dbHandler;
    TextView locationView;
    NavigationView navigationView;

    GoogleApiClient mGoogleApiClient;
    public final static int PLACE_PICKER_REQUEST = 1;

    //To store list of businesses (restaurants)
    ArrayList<HashMap<String, String>> oslist;

    //JSON node names
    public static final String TAG_RES_ID = "id";
    public static final String TAG_RES_NAME = "name";
    public static final String TAG_RES_LOCATION = "location";
    public static final String TAG_RES_CITY = "city";
    public static final String TAG_RES_ADDRESS = "display_address";
    public static final String TAG_RES_BUSINESSIMAGE = "image_url";
    public static final String TAG_RES_RATINGIMAGE = "rating_img_url";

    private String location = "san jose";
    private String sortOption = "0";
    private LatLng latLng;
    private String latLngString = "";
    private LatLngBounds latLngBounds;
    private String locationName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //actionBar.setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("Restaurants");

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get widget instances
        list = (ListView) findViewById(R.id.restaurants_list);
        oslist = new ArrayList<HashMap<String, String>>();

        View emptyView = findViewById(R.id.no_results_image);
        list.setEmptyView(emptyView);

        if(savedInstanceState == null) {
            searchTerm = "Restaurants";
            new RestaurantsSearchTask(NavActivity.this, list).execute(searchTerm, location, latLngString, sortOption);
        }
        locationView=(TextView)findViewById(R.id.selected_location);
        locationView.setText("Selected Location:: "+location.toUpperCase());

        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("searchQuery",searchTerm);
        savedInstanceState.putString("location",location);
        savedInstanceState.putString("latLngString",latLngString);
        savedInstanceState.putString("locationView",locationView.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        searchTerm = savedInstanceState.getString("searchQuery");
        location = savedInstanceState.getString("location");
        latLngString = savedInstanceState.getString("latLngString");
        locationView.setText(savedInstanceState.getString("locationView"));
        new RestaurantsSearchTask(NavActivity.this, list).execute(searchTerm, location, latLngString, sortOption);
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        searchTerm = query;
        sortOption = "0";
        new RestaurantsSearchTask(NavActivity.this, list).execute(query, location, latLngString, sortOption);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        return true;
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

        /** Get the action view of the menu item whose id is search */
        MenuItem searchViewItem = menu.findItem(R.id.search_view);
        View v = (View) searchViewItem.getActionView();

        /** Get the edit text from the action view */
        //SearchView searchView1 = ( SearchView ) v.findViewById(R.id.search_view);
        if(v instanceof SearchView) {
            searchView = (SearchView) v;

            searchView.setIconifiedByDefault(false);
            //searchView.setSubmitButtonEnabled(true);
            searchView.setQueryHint("Search here");
            searchView.setOnQueryTextListener(this);

            searchView.setMaxWidth(Integer.MAX_VALUE);

            searchView.setQuery(searchTerm, false);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort) {
            final String [] items=new String []{"Relevance","Distance"};
            AlertDialog.Builder builder=new AlertDialog.Builder(NavActivity.this);
            builder.setTitle("Sort by");

            builder.setItems(items, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast toast = Toast.makeText(NavActivity.this, Integer.toString(which), Toast.LENGTH_LONG);
                    //toast.show();
                    sortOption = Integer.toString(which);
                    new RestaurantsSearchTask(NavActivity.this, list).execute(searchTerm, location, latLngString, sortOption);
                }
            });

            builder.show();
            return true;
        }
        else if(id == R.id.place_picker) {
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
            searchView.setQuery("Restaurants",true);
        } else if (id == R.id.nav_favorites) {
            startFavActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startFavActivity(){
        Intent intent = new Intent(this, FavoritesActivity.class);
        startActivity(intent);
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

        if (!TextUtils.isEmpty(place.getName())) {
            locationName=  place.getName().toString() ;
        }
        if (!TextUtils.isEmpty(place.getAddress())) {
            location = place.getAddress().toString();
            latLng=null;
            setLatLongString();
            locationView.setText("Selected Location:: "+location.toUpperCase());
        }

        else if (null != place.getLatLng()) {
            latLng = place.getLatLng();
            setLatLongString();
            location=null;
            locationView.setText("Selected Location:: "+latLng);
        }
        if (null != place.getViewport()) {
            latLngBounds = place.getViewport();
        }
        //locationView.setText("Current Location:"+location);
        new RestaurantsSearchTask(NavActivity.this, list).execute(searchTerm, location, latLngString, sortOption);
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

    private void setLatLongString() {
        if(latLng != null) {
            latLngString = latLng.latitude + "," + latLng.longitude;
        }
        else {
            latLngString = "";
        }
    }

}
