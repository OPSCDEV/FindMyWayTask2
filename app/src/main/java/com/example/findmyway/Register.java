package com.example.findmyway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
    DatabaseReference Firebasedb;
    TextView Fname,Lname,Address,Email,Password;
    Button SignUp, SignIn;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    String uid = FirebaseAuth.getInstance().getUid();
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
        SignIn = findViewById(R.id.btSUSignIn);
        Firebasedb = FirebaseDatabase.getInstance().getReference();
        Firebasedb = Firebasedb.child("User");
        firebaseAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference("User");
        SignUp.setOnClickListener(v -> CheckUserExists());
        SignIn.setOnClickListener(v -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        });
    }
    private  void CheckUserExists(){
        String userEmail = Email.getText().toString().trim();
        reference.orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Intent intent;
                if (dataSnapshot.exists()){
                    Toast.makeText( Register.this, "Account is already registered", Toast.LENGTH_SHORT).show();
                    intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
                }
                else{
                    UserReg(uid);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Register.this, "There was an error",Toast.LENGTH_SHORT);
            }
        });
    }

    private void UserReg(String userId)
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
                        startActivity(new Intent(Register.this, Login.class));
                    }
                });
                User doctorReg = new User(name, lname, address, email);
                Firebasedb.child(userId).setValue(doctorReg);
                Intent passSetting =new Intent(Register.this, Login.class);
                startActivity(passSetting);
            }
            else{
                Toast.makeText(this, "Please fill in email and password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}