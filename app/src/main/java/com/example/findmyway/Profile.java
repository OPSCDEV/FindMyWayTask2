package com.example.findmyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Profile extends AppCompatActivity {

    EditText address,Fname, Lname;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

         reference = FirebaseDatabase.getInstance().getReference("User");
         Fname = findViewById(R.id.editTextFname);
         Lname = findViewById(R.id.editTextLname);
         address = findViewById(R.id.editTextAddress);

         reference.orderByChild("f_Name").equalTo("james").addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                 for(DataSnapshot datas: snapshot.getChildren()){
                     String Fnamedb=datas.child("f_Name").getValue().toString();
                     String Lnamedb = datas.child("l_Name").getValue().toString();
                     String addressdb = datas.child("address").getValue().toString();

                     Fname.setText(Fnamedb);
                     Lname.setText(Lnamedb);
                     address.setText(addressdb);

                     Fname.setEnabled(false);
                     Lname.setEnabled(false);
                     address.setEnabled(false);
                 }
             }

             @Override
             public void onCancelled(@NonNull @NotNull DatabaseError error) {

             }
         });

    }
}