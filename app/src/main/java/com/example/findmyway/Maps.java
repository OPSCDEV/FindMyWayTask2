package com.example.findmyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;


public class Maps extends AppCompatActivity implements OnMapReadyCallback {

    Spinner spType;
    Button btFind;
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference referencePref;

    private boolean mLocationPermissionGranted = false;
    private static final String TAG = "MainActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private ImageView profile;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getLocationPermission();

        profile = findViewById(R.id.ic_profile);
        referencePref = FirebaseDatabase.getInstance().getReference("User_Pref");

        intent = getIntent();
        String email = intent.getStringExtra("Email_Key");
        String preflandmark = intent.getStringExtra("PrefLandmark_Key");
        String prefmark = intent.getStringExtra("selectedLand");

        getLocationPermission();
        spType = findViewById(R.id.sp_type);
        btFind = findViewById(R.id.bt_find);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        if( preflandmark.equals("Historical") /*|| (prefmark.equals("Historical"))*/){
            String[] placeTypeList = new String[]{"museum", "library", "monument"};
            String[] placeNameList = new String[]{"Museum", "Library", "Monument"};
            spType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, placeNameList));
            GetPlaces(placeTypeList);
        }else{
            if(preflandmark.equals("Modern")/*|| (prefmark.equals("Modern"))*/){
                String[] placeTypeList = new String[]{"natural_feature","landmark", "tourist_attraction"};
                String[] placeNameList = new String[]{"Natural Features", " ", "Tourist Attractions"};
                spType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, placeNameList));
                GetPlaces(placeTypeList);
            }else{
                if(preflandmark.equals("Popular")/*|| (prefmark.equals("Popular"))*/){
                    String[] placeTypeList = new String[]{"restaurant", "cafe", "park","night_club"};
                    //Display
                    String[] placeNameList = new String[]{"Restaurant", "Cafe", "Park","Night Club"};
                    spType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, placeNameList));
                    GetPlaces(placeTypeList);
                }
            }
        }

        Profile(email);
        //AddFavLocation(email);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        map = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
            }
        }
    }

    private void getDeviceLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {

                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();

                        moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                15f);

                    } else {
                        Toast.makeText(Maps.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }

    public void init() {
        Log.d(TAG, "init: initializing");
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(Maps.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private class PlaceTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: DoingBackground");
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new ParseTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        Log.d(TAG, "downloadUrl: Doing DownloadUrl");
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        String data = builder.toString();

        reader.close();
        return data;
    }

    private class ParseTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser();
            List<HashMap<String, String>> mapList = null;
            try {
                JSONObject object = new JSONObject(strings[0]);
                mapList = jsonParser.parseResult(object);
                Log.d(TAG, "doInBackground: doing background check" + mapList.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            map.clear();
            for (int i = 0; i < hashMaps.size(); i++) {

                HashMap<String, String> hashMap = hashMaps.get(i);
                double lat = Double.parseDouble(hashMap.get("lat"));
                double lng = Double.parseDouble(hashMap.get("lng"));
                String name = hashMap.get("name");
                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(name);
                map.addMarker(options);
            }
        }
    }

    private void GetPlaces(String[] placeTypeList){
        if (ActivityCompat.checkSelfPermission(Maps.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentFocus();
        } else {
            ActivityCompat.requestPermissions(Maps.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        btFind.setOnClickListener(v -> {
            int i = spType.getSelectedItemPosition();

            final Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Location currentLocation = (Location) task.getResult();

                    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                            "?location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() +
                            "&radius=5000" +
                            "&types=" + placeTypeList[i] +
                            "&sensor=true" +
                            "&key=" + getResources().getString(R.string.google_map_key);
                    Log.d(TAG, "onCreate: Url: " + url);
                    new PlaceTask().execute(url);


                } else {
                    Toast.makeText(Maps.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    private void Profile(String email) {
        profile.setOnClickListener(v -> {
            Intent passSetting = new Intent(Maps.this, Profile.class);
            passSetting.putExtra("Email_Key", email);
            startActivity(passSetting);
        });
    }

//    private void AddFavLocation(String email) {
//        //favLocation.setOnClickListener(v -> Toast.makeText(Maps.this, "UserEmail" + email + " User favorite location saved;", Toast.LENGTH_SHORT).show());
//
//    }

}

