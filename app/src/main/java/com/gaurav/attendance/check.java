package com.gaurav.attendance;

/**
 * Created by GAURAV on 16-09-2018.
 */

public class check {

    public String role, Password, email, name, last;
    public int Roll;
    public check(){}
    public check(String role, String email, String Password, String name, String Last, int Roll){
        this.role = role;
        this.email = email;
        this.Password = Password;
        this.name = name;
        this.last = Last;
        this.Roll = Roll;
    }
}
