package com.example.findmyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

public class Profile extends AppCompatActivity {

    EditText Uaddress,Fname;
    TextView  lanPref, disPref;
    Spinner landPref, unitPref;
    Button edit, save;
    DatabaseReference referenceUser,referencePref;
    Intent intent;
    Spinner Flocation;
    Query refQuery;
    String prefdistancedb, prefLandmarkdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

         Fname = findViewById(R.id.txtFirstname);
         Uaddress = findViewById(R.id.txtAddressp);
         lanPref = findViewById(R.id.txtlPref);
         disPref = findViewById(R.id.txtDis);

         Flocation = findViewById(R.id.spnFavlocations);

         landPref = findViewById(R.id.spnlandPref);
         unitPref = findViewById(R.id.spnPrefUnit);

         edit = findViewById(R.id.btEdit);
         save = findViewById(R.id.btSave);

         intent = getIntent();
         String email = intent.getStringExtra("Email_Key");
         String stringifyEmail = email;

        referenceUser = FirebaseDatabase.getInstance().getReference("User");
        refQuery = referenceUser.orderByChild("email").equalTo(email);

        referencePref = FirebaseDatabase.getInstance().getReference("User_Pref");

        getUserDetails(stringifyEmail);
        getUserPref(stringifyEmail);


        edit.setOnClickListener(v -> {
            EnableText();
        });

        save.setOnClickListener(v -> {
            SavePrefUser();
        });
        /*disUserpref();*/
    }



    private void getUserDetails(String email){
        //Works
        referenceUser.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot datas : snapshot.getChildren()) {
                    String Fnamedb = datas.child("f_Name").getValue().toString();
                    String Lnamedb = datas.child("l_Name").getValue().toString();
                    String addressdb = datas.child("address").getValue().toString();

                   Fname.setText(Fnamedb + " " + Lnamedb);
                    Uaddress.setText(addressdb);

                    Fname.setEnabled(false);
                    Uaddress.setEnabled(false);
                    Flocation.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void getUserPref(String email){
        referencePref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot datas : snapshot.getChildren()) {
                     prefLandmarkdb = datas.child("prefLandmark").getValue().toString();
                     prefdistancedb = datas.child("prefDistance").getValue().toString();
                    String[] lPref = new String[]{"Historical", "Popular" , "Modern"};
                    landPref.setAdapter(new ArrayAdapter<>(Profile.this, android.R.layout.simple_spinner_dropdown_item, lPref));
                    String[] uPref = new String[]{"Kilometers", "Miles"};
                    unitPref.setAdapter(new ArrayAdapter<>(Profile.this, android.R.layout.simple_spinner_dropdown_item, uPref));
                    lanPref.setText(prefLandmarkdb);
                    disPref.setText(prefdistancedb);

                    landPref.setEnabled(false);
                    unitPref.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void SavePrefUser(){
        Register a1 = new Register();
        String a = a1.uid;

        referencePref.child(a).child("prefDistance").setValue(unitPref.getItemAtPosition(unitPref.getSelectedItemPosition()).toString());
        referencePref.child(a).child("prefLandmark").setValue(landPref.getItemAtPosition(landPref.getSelectedItemPosition()).toString());

        landPref.setEnabled(false);
        unitPref.setEnabled(false);
        Fname.setEnabled(false);
        Uaddress.setEnabled(false);
        Flocation.setEnabled(false);

        TextView textView = (TextView)landPref.getSelectedView();
        String result = textView.getText().toString();
        Intent passSetting = new Intent(this, Maps.class);

    }
    private void EnableText(){
        Flocation.setEnabled(true);
        landPref.setEnabled(true);
        unitPref.setEnabled(true);
    }

}