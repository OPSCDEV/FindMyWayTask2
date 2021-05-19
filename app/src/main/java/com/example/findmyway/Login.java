package com.example.findmyway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class Login extends AppCompatActivity {

    public EditText Email, Password;
    Button SignUp,SignIn;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.txtEmail);
        Password = findViewById(R.id.txtPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        SignUp = findViewById(R.id.btRegister);
        SignIn = findViewById(R.id.btSignIn);

        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user != null){
                Toast.makeText(Login.this, "User logged in", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, Profile.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(Login.this, "Login to continue", Toast.LENGTH_SHORT).show();
            }
        };

        SignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            startActivity(intent);
        });

        SignIn.setOnClickListener(v -> {
            String userEmail = Email.getText().toString();
            String userPassword = Password.getText().toString();
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
                            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(Login.this, (OnCompleteListener) task -> {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Not sucessfull", Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(Login.this, Profile.class));
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
    @Override
    protected void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
