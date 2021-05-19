package com.example.findmyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private DatabaseReference Firebasedb;
    TextView Fname,Lname,Address,Email,Password;
    Button SignUp, SignIn;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Fname = findViewById(R.id.txtFname);
        Lname = findViewById(R.id.txtLname);
        Address = findViewById(R.id.txtAddress);
        Email = findViewById(R.id.txtSUEmail);
        Password = findViewById(R.id.txtSUPassword);
        SignUp = findViewById(R.id.btContinue);
        Firebasedb = FirebaseDatabase.getInstance().getReference();
        Firebasedb = Firebasedb.getParent().child("User");

        SignUp.setOnClickListener(new View.OnClickListener() {
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
        if(name.isEmpty()||lname.isEmpty()||address.isEmpty()||email.isEmpty()||password.isEmpty())
        {
            Toast.makeText(this, "Please fill all the fields",Toast.LENGTH_LONG).show();
        }
        else
        {
            if(!(email.isEmpty() && password.isEmpty())) {

                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Register.this, task -> {
                    if(!task.isSuccessful()){
                        Toast.makeText(Register.this.getApplicationContext(),
                                "Sign Up Unsuccessful:"+ task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        startActivity(new Intent(Register.this, Profile.class));
                    }
                });

                String id = Firebasedb.push().getKey();
                User doctorReg = new User(name, lname, address, email, password);
                Firebasedb.child(id).setValue(doctorReg);
            }
            else{
                Toast.makeText(this, "Please fill in email and password", Toast.LENGTH_SHORT).show();
            }
        }

    }
}