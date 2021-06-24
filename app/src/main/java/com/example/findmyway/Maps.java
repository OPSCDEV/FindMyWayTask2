package com.example.findmyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


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
    private ImageView profile,favorite,Camera;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    Intent intent;
    GetLatLng getLatLng;
    ImageView CamerPhoto;

    DatabaseReference Firebasedb;
    FirebaseAuth firebaseAuth;
    String uid = FirebaseAuth.getInstance().getUid();

    List<String> data;

    public FirebaseStorage storage;
    public StorageReference storageReference;
    public String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getLocationPermission();

        intent = getIntent();
        email = intent.getStringExtra("Email_Key");
        String preflandmark = intent.getStringExtra("PrefLandmark_Key");

        data = new ArrayList<>();

        profile = findViewById(R.id.ic_profile);
        favorite = findViewById(R.id.ic_fav_location);
        Camera = findViewById(R.id.ic_camera);
        getLatLng = new GetLatLng();
        referencePref = FirebaseDatabase.getInstance().getReference("User_Pref");

        Firebasedb = FirebaseDatabase.getInstance().getReference();
        Firebasedb = Firebasedb.child("Fav_Locations");
        firebaseAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        getLocationPermission();
        spType = findViewById(R.id.sp_type);
        btFind = findViewById(R.id.bt_find);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        if(preflandmark.equals("Historical")){
            String[] placeTypeList = new String[]{"museum", "library"};
            String[] placeNameList = new String[]{"Museum", "Library"};
            spType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, placeNameList));
            GetPlaces(placeTypeList);
        }else{
            if(preflandmark.equals("Modern")){
                String[] placeTypeList = new String[]{"landmark", "tourist_attraction"};
                String[] placeNameList = new String[]{"Landmarks", "Tourist Attractions"};
                spType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, placeNameList));
                GetPlaces(placeTypeList);
            }else{
                if(preflandmark.equals("Popular")){
                    String[] placeTypeList = new String[]{"restaurant", "cafe","bar"};
                    String[] placeNameList = new String[]{"Restaurants","Cafes","Bars"};
                    spType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, placeNameList));
                    GetPlaces(placeTypeList);
                }
            }
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SaveFavoriteLocation(uid);
        Profile(email);
        TakePhoto();
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
        Log.d(TAG, "moveCamera: lat lng: "+latLng);
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
                String rating = hashMap.get("rating");

                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(name);
                options.snippet("Rating: "+ rating);
                map.addMarker(options);

                map.setOnMarkerClickListener(marker -> {
                    if(marker.isInfoWindowShown()) {
                        marker.hideInfoWindow();
                    } else {
                        marker.showInfoWindow();
                    }
                    Log.d(TAG, "onPostExecute: "+marker.getSnippet()+"Name: "+marker.getTitle()+"Latlng: "+marker.getPosition());
                    getLatLng.setLatlng(marker.getPosition());
                    getLatLng.setName(marker.getTitle());
                    return true;
                });
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
                            "&radius=10000" +
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

    private void SaveFavoriteLocation(String userId){
        favorite.setOnClickListener(v -> {
            if(getLatLng.equals(null)){
                Toast.makeText(this, "Please select a marker to save the location", Toast.LENGTH_SHORT).show();
            }else{
                String Name = getLatLng.getName();
                data.add(Name);
                Firebasedb.child(userId).setValue(data).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(Maps.this, "Great success", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Error saving save landmark", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void TakePhoto(){
        Camera.setOnClickListener(v -> dispatchTakePictureIntent());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Error loading camera...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            File file = createImageFile();
            if (file != null) {
                FileOutputStream fout;
                try {
                    fout = new FileOutputStream(file);
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 70, fout);
                    fout.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Uri uri=Uri.fromFile(file);
                final  String emailKey =  email;
                final  String randomKey =  UUID.randomUUID().toString();
                StorageReference riverRef = storageReference.child(emailKey+"/"+randomKey);
                riverRef.putFile(uri);
            }
            Toast.makeText(this, "Photo saved...", Toast.LENGTH_SHORT).show();
        }
    }

    public File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mFileTemp = null;
        String root= getDir("my_sub_dir", Context.MODE_PRIVATE).getAbsolutePath();
        File myDir = new File(root + "/Img");
        if(!myDir.exists()){
            myDir.mkdirs();
        }
        try {
            mFileTemp=File.createTempFile(imageFileName,".jpg",myDir.getAbsoluteFile());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return mFileTemp;
    }
}

