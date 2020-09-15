package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    DatabaseReference myref;
    EditText txt1,txt2;
    TextView txt3;
    Button btn1,btn2;
    int count=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt1=findViewById(R.id.txt1);
        btn1=findViewById(R.id.btn1);
        txt2=findViewById(R.id.txt2);
        btn2=findViewById(R.id.btn2);
        txt3=findViewById(R.id.txt3);
        myref= FirebaseDatabase.getInstance().getReference();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**  myref.child("Name").setValue(txt1.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                Toast.makeText(MainActivity.this, "values are added", Toast.LENGTH_SHORT).show();
                }
                }
                });**/
                User currentUser=new User(txt1.getText().toString(),Integer.parseInt(txt2.getText().toString()));

                myref.child("User"+count).setValue(currentUser);
                count++;
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                myref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> iterable=dataSnapshot.getChildren();
                        String s="";
                        for(DataSnapshot ds: iterable)
                        {
                            User fetchUser=ds.getValue(User.class);
                            String name=fetchUser.getName();
                            int mob=fetchUser.getMob();
                            s=s+name+"  "+mob+"\n";
                            Toast.makeText(MainActivity.this, "kimn"+name+mob, Toast.LENGTH_SHORT).show();

                        }

                        txt3.setText(s);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }
}