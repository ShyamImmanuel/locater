package com.example.locater;


import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText r_name,r_email,r_password,r_cpassword,r_district;
    Button signup;
    FirebaseAuth auth;
    String name,email,password,district,cpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        r_name=(EditText)findViewById(R.id.r_name);
        r_email=(EditText)findViewById(R.id.r_email);
        r_password=(EditText)findViewById(R.id.r_password);
        r_cpassword=(EditText)findViewById(R.id.r_cpassword);
        r_district=(EditText)findViewById(R.id.r_district);
        signup=(Button)findViewById(R.id.signupbtn);
        auth=FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                name=r_name.getText().toString();
                email=r_email.getText().toString();
                password=r_password.getText().toString();
                district=r_district.getText().toString();
                cpassword=r_cpassword.getText().toString();
                // Toast.makeText(Register.this,password+"\n"+cpassword,Toast.LENGTH_LONG).show();
                if(name.isEmpty() ||  email.isEmpty() || password.toString().isEmpty() || cpassword.toString().isEmpty() || district.toString().isEmpty())
                {
                    Toast.makeText(Register.this,"Please enter values in all field",Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(Register.this,password+"\n"+cpassword,Toast.LENGTH_SHORT).show();
                if(!password.equalsIgnoreCase(cpassword))
                {
                    Toast.makeText(Register.this,"Password does not match",Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {

                            CheckEmail();


                            Toast.makeText(Register.this, "Registered Successful and Verification sent to your email", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(Register.this, Login.class));


                        }
                        else
                        {
                            Toast.makeText(Register.this,"Registered Failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });



    }
    void CheckEmail() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Register.this, "Email verification link has been sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Register.this, "Email verification link not sent", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}
