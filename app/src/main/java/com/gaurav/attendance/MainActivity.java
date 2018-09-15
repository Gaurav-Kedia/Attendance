package com.gaurav.attendance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.view.View.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextMessage;
    private Button buttonregister;
    private EditText edittextemail;
    private EditText edittextpassword;
    private TextView textviewsignin;
    private EditText roll;
    EditText firstname, lastname;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseauth;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Firebase mfire;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    roll.setVisibility(INVISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    roll.setVisibility(VISIBLE);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    roll.setVisibility(INVISIBLE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        mTextMessage = (TextView) findViewById(R.id.message);
        firstname = (EditText) findViewById(R.id.First);
        lastname = (EditText) findViewById(R.id.Last);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        firebaseauth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        buttonregister = (Button) findViewById(R.id.buttonRegister);
        edittextemail = (EditText) findViewById(R.id.email);
        edittextpassword = (EditText) findViewById(R.id.password);
        roll = (EditText) findViewById(R.id.rollnum);
        roll.setVisibility(INVISIBLE);
        textviewsignin = (TextView) findViewById(R.id.textviewsignin);

        buttonregister.setOnClickListener(this);
        textviewsignin.setOnClickListener(this);
    }



    private void registeruser(){
        final String email = edittextemail.getText().toString().trim();
        final String password = edittextpassword.getText().toString().trim();
        //int Rollnumber;
        final String firstnm = firstname.getText().toString().trim();
        final String lastnm = lastname.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        if(mTextMessage.getText().equals("Teacher")) {
            mfire = new Firebase("https://attendance-26b6a.firebaseio.com/Teacher/" + firstnm);
        }
        else if (mTextMessage.getText().equals("Student")){
            mfire = new Firebase("https://attendance-26b6a.firebaseio.com/Student/" + firstnm);
        }
        else {
            mfire = new Firebase("https://attendance-26b6a.firebaseio.com/Parent/" + firstnm);
        }

       // DatabaseReference myRef = database.getReference("email");
        firebaseauth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //user successfully registered
                            Firebase role = mfire.child("role");
                            role.setValue(mTextMessage.getText());

                            Firebase emailID = mfire.child("email");
                            emailID.setValue(email);

                            Firebase namef = mfire.child("name");
                            namef.setValue(firstnm);

                            Firebase namel = mfire.child("last");
                            namel.setValue(lastnm);

                            if(mTextMessage.getText().equals("Student")){
                                int Rollnumber = Integer.parseInt(roll.getText().toString());
                                Firebase rolll = mfire.child("Roll no");
                                rolll.setValue(Rollnumber);
                            }
                            Toast.makeText(MainActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        }

                        else{
                            progressDialog.hide();
                            checkemail();
                        }
                    }
                });
    }
    private void checkemail() {
        firebaseauth.fetchProvidersForEmail((edittextemail.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        boolean check = !task.getResult().getProviders().isEmpty();
                        if(check){
                            Toast.makeText(MainActivity.this, "email already registered found", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Fail to register, Please try again", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    @Override
    public void onClick(View v) {
        if(v == buttonregister){
            registeruser();
        }
        if(v == textviewsignin){
            //login activity opens
            Context c = MainActivity.this;
            Class dest = signin_main.class;
            Intent startchild = new Intent(c, dest);
            startchild.putExtra("Screeen","Main");
            startActivity(startchild);

            Toast.makeText(this, "Please signin to continue", Toast.LENGTH_SHORT).show();
        }

    }

}
