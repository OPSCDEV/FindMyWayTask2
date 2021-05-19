package com.example.findmyway;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private DatabaseReference Firebasedb;
    TextView Fname,Lname,Address,Email,Password;
    Button Register, SignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Fname = findViewById(R.id.txtFname);
        Lname = findViewById(R.id.txtLname);
        Address = findViewById(R.id.txtAddress);
        Email = findViewById(R.id.txtSUEmail);
        Password = findViewById(R.id.txtSUPassword);
        Register = findViewById(R.id.btContinue);
        Firebasedb = FirebaseDatabase.getInstance().getReference();
        Firebasedb = Firebasedb.getParent().child("User");

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserReg();
            }
        });
    }
    private void UserReg()
    {
        String name=Fname.getText().toString().trim();
        String lname=Lname.getText().toString().trim();
        String address=Address.getText().toString().trim();
        String email=Email.getText().toString().trim();
        String password=Password.getText().toString().trim();
        if(!TextUtils.isEmpty(name))
        {
            String id=Firebasedb.push().getKey();
            User doctorReg= new User(name,lname,address,email,password);
            Firebasedb.child(id).setValue(doctorReg);
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Error!!",Toast.LENGTH_LONG).show();
        }

    }
}