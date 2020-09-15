package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    TextView signup;
    EditText email,password;
    Button btn1;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signup = findViewById(R.id.signup);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn1 = findViewById(R.id.login);
        auth = FirebaseAuth.getInstance();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emal = email.getText().toString();

                String passwrd = password.getText().toString();
                if (TextUtils.isEmpty(emal)) {
                    email.setError("Required");
                    email.requestFocus();
                    return;
                }

                auth.signInWithEmailAndPassword(emal, passwrd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            startActivity(new Intent(login.this,user_profile.class));

                            Toast.makeText(login.this, "User Account Created", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(login.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                if (TextUtils.isEmpty(passwrd)) {
                    password.setError("Required");
                    password.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emal).matches()) {
                    email.setError("Not Valid Email");
                    email.requestFocus();
                    return;
                }
                if (passwrd.length() < 6) {
                    password.setError("Atleast six character");
                    password.requestFocus();
                    return;
                }
            }
            });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this,registration.class));
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    Toast.makeText(login.this, "user is not null", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(login.this,user_profile.class));
                }
            }
        });
    }
}