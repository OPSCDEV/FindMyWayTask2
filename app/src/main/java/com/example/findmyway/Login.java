package com.example.findmyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

public class Login extends AppCompatActivity {

    private EditText Email, Password;
    private Button SignUp,SignIn;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.txtEmail);
        Password = findViewById(R.id.txtPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        SignUp = findViewById(R.id.btRegister);
        SignIn = findViewById(R.id.btSignIn);

        reference = FirebaseDatabase.getInstance().getReference("User_Pref");

        if(isServiceOkay()){
            getAuth();
            SignUpUser();
            init();
        }
    }

    private void init(){
        checkUser();
    }
    public boolean isServiceOkay(){
        Log.d(TAG, "isServiceOkay: checking google services version");
        int availible = GoogleApiAvailability
                .getInstance()
                .isGooglePlayServicesAvailable(this);
        if(availible== ConnectionResult.SUCCESS){
            Log.d(TAG, "isServiceOkay: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(availible)){
            Log.d(TAG, "isServiceOkay: an error occurred we can fix it ");
            Dialog dialog = GoogleApiAvailability
                    .getInstance()
                    .getErrorDialog(this,availible,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this,"You can't Make map request",Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void checkUser(){
        SignIn.setOnClickListener(v -> {
            String userEmail = Email.getText().toString().trim();
            String userPassword = Password.getText().toString().trim();
            if(userEmail.isEmpty()){
                Email.setError("Provide your email first!");
                Password.requestFocus();
            }
            else{
                if(userPassword.isEmpty()){
                    Password.setError("Enter Password!");
                    Password.requestFocus();
                }else{
                    if(userEmail.isEmpty() && userPassword.isEmpty()){
                        Toast.makeText(Login.this, "Fields Empty", Toast.LENGTH_SHORT).show();
                    }else{
                        if(!(userEmail.isEmpty() && userPassword.isEmpty())){
                            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(Login.this, task -> {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Not successful", Toast.LENGTH_SHORT).show();
                                } else {

                                    //Checks if user records exist
                                    reference.orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                Toast.makeText(Login.this, "loading profile...", Toast.LENGTH_SHORT).show();
                                                CheckUserPref(userEmail);
                                            }
                                            else{
                                                Toast.makeText(Login.this, "you don't have a pref", Toast.LENGTH_SHORT).show();
                                                Intent passSetting =new Intent(Login.this, Settings.class);
                                                passSetting.putExtra("Email_Key", userEmail);
                                                startActivity(passSetting);
                                            }

                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });
                        }
                        else{
                            Toast.makeText(this, "Login Error!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
    private void getAuth(){
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user != null){
                Toast.makeText(Login.this, "User logged in", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(Login.this, "Login to continue", Toast.LENGTH_SHORT).show();
            }
        };
    }
    private void SignUpUser(){
        SignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }
    private void CheckUserPref(String useremail){
        reference.orderByChild("email").equalTo(useremail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot datas: snapshot.getChildren()) {

                    String email = datas.child("email").getValue().toString().trim();

                    String StringifyEmail = email;
                    String stringifyUserEmail = useremail;

                    Intent passSetting;
                    if (stringifyUserEmail.matches(StringifyEmail)) {
                        passSetting = new Intent(Login.this, Maps.class);
                        passSetting.putExtra("Email_Key", useremail);
                        startActivity(passSetting);
                    } else{
                        Toast.makeText(Login.this, "emails dont match", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.d(TAG, "onCancelled: Error null references");
            }
        });
    }
}
