package com.gaurav.attendance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by GAURAV on 13-04-2018.
 */

public class profile extends AppCompatActivity implements View.OnClickListener {

    Button signout;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    TextView textEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        Intent intent = getIntent();
        String Pro = intent.getExtras().getString("profession");
        String Pro1 = intent.getExtras().getString("profession1");
        TextView text = (TextView) findViewById(R.id.Profess);
        TextView text1 = (TextView) findViewById(R.id.got);
        text1.setText(Pro1);
        text.setText(Pro);
        firebaseAuth = FirebaseAuth.getInstance();

        textEmail = (TextView) findViewById(R.id.textViewEmail);
        signout = (Button) findViewById(R.id.signoutbutton);

        user = firebaseAuth.getCurrentUser();
        signout.setOnClickListener(this);
        textEmail.setOnClickListener(this);
        if(user.isEmailVerified()){
            textEmail.setText("email verified");
            textEmail.setEnabled(false);
        }

    }

    public void signout(){
        firebaseAuth.signOut();
        Toast.makeText(profile.this,"signing out",Toast.LENGTH_SHORT).show();
        Context c = profile.this;
        Class dest = signin_main.class;
        Intent startchild = new Intent(c, dest);
        startchild.putExtra("Screeen", "Profile");
        startActivity(startchild);
    }

    @Override
    public void onClick(View v) {
        if(v == signout){
            signout();
        }
        if(v == textEmail){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(profile.this,"verification email sent",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
    }
}
