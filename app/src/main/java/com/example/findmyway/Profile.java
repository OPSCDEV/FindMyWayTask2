package com.example.findmyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

public class Profile extends AppCompatActivity {

    EditText address,Fname, Lname, Flocation, landPref, unitPref;
    DatabaseReference reference,referencePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

         reference = FirebaseDatabase.getInstance().getReference().child("User");
         referencePref = FirebaseDatabase.getInstance().getReference("User_Pref");
         Fname = findViewById(R.id.txtFirstname);
         Lname = findViewById(R.id.txtLastname);
         address = findViewById(R.id.txtAddress);
         Flocation = findViewById(R.id.txtFavlocations);
         landPref = findViewById(R.id.txtLandPref);
         unitPref = findViewById(R.id.txtLandPref);
         //FirebaseUser user;
         //String uid;
         //user = FirebaseAuth.getInstance().getCurrentUser();
        // uid = user.getUid();
            //ArrayList<String> arrayList = new ArrayList<>();
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference uDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        uDatabaseReference.child(currentUid).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 String user_name = snapshot.child("f_Name").getValue().toString();
                 String user_surname = snapshot.child("l_Name").getValue().toString();
                 String user_address = snapshot.child("address").getValue().toString();
                 //String user_preflandmark = datas.child(uid).child("prefLandmark").getValue().toString();
                // String user_prefdistance = datas.child(uid).child("prefdistance").getValue().toString();
                 Fname.setText(user_name);
                 Lname.setText(user_surname);
                 address.setText(user_address);
                // landPref.setText(user_preflandmark);
                 //unitPref.setText(user_prefdistance);

             }

             @Override
             public void onCancelled(@NonNull @NotNull DatabaseError error) {

             }
         });

    }
}