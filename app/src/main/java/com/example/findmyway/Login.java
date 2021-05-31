package com.example.findmyway;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Login extends AppCompatActivity {

    private EditText Email, Password;
    private Button SignUp,SignIn;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
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

        SignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

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

                                    reference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            for(DataSnapshot datas: snapshot.getChildren()) {
                                                String email = datas.child("email").getValue().toString().trim();

                                                String StringifyEmail = email;
                                                String stringifyUserEmail = userEmail;

                                                Intent passSetting;
                                                if (stringifyUserEmail.matches(StringifyEmail)) {
                                                    passSetting = new Intent(Login.this, Profile.class);
                                                } else {
                                                    Toast.makeText(Login.this, "Email Don't match", Toast.LENGTH_SHORT).show();
                                                    passSetting = new Intent(Login.this, Settings.class);
                                                }
                                                passSetting.putExtra("Email_Key", userEmail);
                                                startActivity(passSetting);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

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
}
