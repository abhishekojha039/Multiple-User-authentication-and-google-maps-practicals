package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class user_profile extends AppCompatActivity {

    Button btn1;
    TextView welcome;
    ImageView img1;
    FirebaseAuth auth;
    DatabaseReference root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        btn1=findViewById(R.id.btn1);
        welcome=findViewById(R.id.welcome);
        img1=findViewById(R.id.img1);
        auth=FirebaseAuth.getInstance() ;
        root= FirebaseDatabase.getInstance().getReference();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad=new AlertDialog.Builder(user_profile.this);
                View layoutView= getLayoutInflater().inflate(R.layout.my_layout,null);
                final EditText et=layoutView.findViewById(R.id.edt_layout);
                ad.setTitle("Add the value");
                ad.setView(layoutView);
                ad.setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newInfo= et.getText().toString();
                        String uid=auth.getCurrentUser().getUid();
                        root.child(uid).setValue(welcome.getText().toString()+"\n"+newInfo);


                    }
                });
                ad.show();


            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        auth.signOut();
        startActivity(new Intent(user_profile.this,login.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    displayDetails();
                }
            }
        });
    }
    private void displayDetails()
    {
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              DataSnapshot currentChild =  snapshot.child(auth.getCurrentUser().getUid());
                String getInfo=currentChild.getValue(String.class);
                welcome.setText("\n"+getInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}