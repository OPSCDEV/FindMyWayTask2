package com.example.findmyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    EditText Uaddress,Fname,  landPref, unitPref;
    Button edit, save;
    DatabaseReference referenceUser,referencePref;
    Intent intent;
    Spinner Flocation;
    Query refQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

         Fname = findViewById(R.id.txtFirstname);
         Uaddress = findViewById(R.id.txtAddressp);

         Flocation = findViewById(R.id.spnFavlocations);

         landPref = findViewById(R.id.txtLandPref);
         unitPref = findViewById(R.id.txtprefUnit);

         edit = findViewById(R.id.btEdit);
         save = findViewById(R.id.btSave);
         intent = getIntent();
         String email = intent.getStringExtra("Email_Key");
         String stringifyEmail = email;

        referenceUser = FirebaseDatabase.getInstance().getReference("User");
        refQuery = referenceUser.orderByChild("email").equalTo(email);

        referencePref = FirebaseDatabase.getInstance().getReference("User_Pref");

        //FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        //String uid = currentFirebaseUser.getUid();

        getUserDetails(stringifyEmail);
        getUserPref(stringifyEmail);

        edit.setOnClickListener(v -> {
            EnableText();
        });

        save.setOnClickListener(v -> {
            SaveChanges(email);
        });}

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
                    String prefLandmarkdb = datas.child("prefLandmark").getValue().toString();
                    String prefdistancedb = datas.child("prefDistance").getValue().toString();

                    landPref.setText(prefLandmarkdb);
                    unitPref.setText(prefdistancedb);

                    landPref.setEnabled(false);
                    unitPref.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void SaveChanges(String email){

                refQuery.getRef().child("address").setValue(Uaddress.getText().toString());
                referencePref.child("prefDistance").setValue(landPref.getText().toString());
                referencePref.child("prefLandmark").setValue(unitPref.getText().toString());

                Fname.setEnabled(false);
                Uaddress.setEnabled(false);
                Flocation.setEnabled(false);
                landPref.setEnabled(false);
                unitPref.setEnabled(false);

    }
    private void EnableText(){
        Uaddress.setEnabled(true);
        Flocation.setEnabled(true);
        landPref.setEnabled(true);
        unitPref.setEnabled(true);
    }
}