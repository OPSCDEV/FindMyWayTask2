package com.example.findmyway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Settings extends AppCompatActivity {

    DatabaseReference Firebasedb;
    RadioButton historical,modern,popular, metric, imperial;
    TextView prefLandmark, prefDistance;
    Button _continue;
    Intent intent;
    Preferences preferences;
    RadioGroup prefLandmarks, prefUnits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefLandmarks =  findViewById(R.id.rgPreflandmark);
        prefUnits =findViewById(R.id.rgPrefDistance);
        historical = findViewById(R.id.rbHistorical);
        modern = findViewById(R.id.rbModern);
        popular = findViewById(R.id.rbPopular);
        metric = findViewById(R.id.rbMetric);
        imperial = findViewById(R.id.rbImperial);
        prefLandmark = findViewById(R.id.txtLandmarkPref);
        prefDistance = findViewById(R.id.txtDistancePref);
        _continue = findViewById(R.id.btprefernce);

        Firebasedb = FirebaseDatabase.getInstance().getReference();
        Firebasedb = Firebasedb.child("User_Pref");

        intent = getIntent();
        String email = intent.getStringExtra("Email_Key");

        _continue.setOnClickListener(v -> {

            String selectedPrefLandmark;
            String selectedPrefDistance;
            if(historical.isChecked()){
                 selectedPrefLandmark = "Historical";
            }else if(modern.isChecked()){
                selectedPrefLandmark = "Modern";
            }else{
                selectedPrefLandmark = "Popular";
            }
            if(metric.isChecked()){
                selectedPrefDistance = "Kilometers";
            }else{
                selectedPrefDistance = "Miles";
            }

            String id = Firebasedb.push().getKey();
            preferences = new Preferences(selectedPrefLandmark, selectedPrefDistance, email);
            Firebasedb.child(id).setValue(preferences);
            startActivity(new Intent(Settings.this, Maps.class));
        });
            prefLandmarks.setOnCheckedChangeListener((group, checkedId) -> {
                   //find which radioButton is checked by id
                if(checkedId == R.id.rbHistorical) {
                    prefLandmark.setText("Historical");
                } else if(checkedId == R.id.rbModern) {
                    prefLandmark.setText("Modern");
                } else {
                    prefLandmark.setText("Popular");
                }
            });
            prefUnits.setOnCheckedChangeListener((group, checkedId) -> {
                // find which radioButton is checked by id
                if(checkedId == R.id.rbMetric) {
                    prefDistance.setText("Metric units");
                } else {
                    prefDistance.setText("Imperial units");
                }
            });





    }
}