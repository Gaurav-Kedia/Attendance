package com.gaurav.attendance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GAURAV on 13-04-2018.
 */

public class signin_main extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText edittextemail;
    private EditText edittextpassword;
    private Button buttonsi;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Context c;
    private Class dest;
    private TextView forgottextview;
    String item;

    String screen;

    private String YouHave;
    private String YouAre;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);
        Intent intentSignin = getIntent();
        screen = intentSignin.getExtras().getString("Screeen");

        Spinner spinner = (Spinner) findViewById(R.id.SpinnerV);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Teacher");
        categories.add("Student");
        categories.add("Parent");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        edittextemail = (EditText) findViewById(R.id.email);
        edittextpassword = (EditText) findViewById(R.id.password);
        buttonsi = (Button) findViewById(R.id.buttonsignin);
        forgottextview = (TextView) findViewById(R.id.forgot);

        buttonsi.setOnClickListener(this);
        forgottextview.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (screen.equalsIgnoreCase("Profile")){
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonsi) {
            signinuser();
        }
        if(v == forgottextview){
            String email = edittextemail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                //email is empty
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                firebaseAuth.sendPasswordResetEmail(email);
                Toast.makeText(this, "Password reset sent", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        item = parent.getItemAtPosition(position).toString();
        YouHave = item.trim();
        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }





    public void signinuser() {
        final String email = edittextemail.getText().toString().trim();
        final String password = edittextpassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        // check if the email is present in the YOUHAVE directory of database,
        // if not give a tost message ,
        // if yes the give the following code in if condition.


        progressDialog.setMessage("signing in User...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            DatabaseReference refe = FirebaseDatabase.getInstance().getReference(item);
                            Query q = FirebaseDatabase.getInstance().getReference(item)
                                    .orderByChild("role")
                                    .equalTo(item);
                            q.addListenerForSingleValueEvent(valueEventListener);
                        }
                        else {
                            firebaseAuth.fetchProvidersForEmail((edittextemail.getText().toString()))
                                    .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                            boolean check = !task.getResult().getProviders().isEmpty();
                                            if(check){
                                                Toast.makeText(signin_main.this, "Please enter correct credentials", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(signin_main.this, "Email is not Registered", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            progressDialog.hide();
                        }
                    }
                });
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            check roleR = null;
            if (dataSnapshot.exists()){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    roleR = snapshot.getValue(check.class);
                }
                YouAre = roleR.role;
                if((YouAre).equalsIgnoreCase(item) && ((roleR.email).equalsIgnoreCase(edittextemail.getText().toString().trim()))){
                    progressDialog.hide();
                    c = signin_main.this;
                    dest = profile.class;
                    Intent startchild = new Intent(c, dest);
                    startchild.putExtra("profession", YouHave);
                    startchild.putExtra("profession1", YouAre);
                    startActivity(startchild);
                    Toast.makeText(signin_main.this, "signin successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.hide();
                    Toast.makeText(signin_main.this, "User is not Registerted as: " + YouHave, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                progressDialog.hide();
                Toast.makeText(signin_main.this, "User is not Registerted as: " + YouHave, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void resume(View view) {
        Intent intent = new Intent(signin_main.this, MainActivity.class);
        startActivity(intent);
    }
}
