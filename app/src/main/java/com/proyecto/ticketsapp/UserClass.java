
package com.proyecto.ticketsapp;

import android.content.Context;

import java.sql.SQLData;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import DAO.UserClassImplementsDAO;
import Exceptions.DuplicateEntryException;

public class UserClass {
 private int idUser, marks, failures;
 private String userUser;
 private String passwordUser;
 private String rol;
 private String fullname;
 private String email;
 private String cellPhone;
 private int estado;
 protected ArrayList <UserClass> arrayListUser= new ArrayList<>();
 private String actualUser;
 private int size;
 private int requestPassword;
 private UserClass userClass;

    public UserClass(String rol, String fullname, String email, String cellPhone) {
        this.rol = rol;
        this.fullname = fullname;
        this.email = email;
        this.cellPhone = cellPhone;
        this.estado=1;
    }


    public boolean validEmail (String email) {
        return (email.contains("@") && email.endsWith(".com"));

    }

    public boolean validCellPhone(String cellPhone) {
        if (cellPhone.length() == 10 || cellPhone.length() == 11)
            try {
                Long.parseLong(cellPhone);
                return true;
            } catch (Exception e) {
                return false;
            }
        return false;
    }

    public void setIdUser (int idUser) {
        this.idUser=idUser;
    }

    public void setUserUser (String userUser) {
        this.userUser=userUser;
    }

    public void setPasswordUser (String passwordUser) {
        this.passwordUser=passwordUser;
    }

    public void setRol (String rol) {
        this.rol=rol;
    }

    public void setFullname (String fullname) {
        this.fullname=fullname;
    }

    public void setEmail (String email) {
        this.email=email;
    }

    public void setCellPhone (String cellPhone) {
        this.cellPhone=cellPhone;
    }

    public void setEstado (int estado) {
        this.estado=estado;
    }

    public void setFailures (int failures) {
        this.failures=failures;
    }

    public void setRequestPassword (int requestPassword) {
        this.requestPassword=requestPassword;
    }

    public void setMarks (int marks) {
        this.marks=marks;
    }

    public int getIdUser () {
        return this.idUser;
    }

    public String getUserUser() {
        return userUser;
    }

    public String getPasswordUser() {
        return passwordUser;
    }

    public String getRol () {
        return this.rol;
    }

    public int getEstado () {
        return this.estado;
    }

    public int getFailures () {return this.failures;}

    public int getMarks () {return this.marks;}

    public int getRequestPassword () {return this.requestPassword;}

    public String getFullname () {
        return this.fullname;
    }
    public String getEmail () {
        return this.email;
    }
    public String getCellPhone () {
        return this.cellPhone;
    }

    public boolean incrementFailureCount() {
        int failures = this.getFailures();
        failures++;
        if (failures >= 3) {
            this.setEstado(0);
            return true;
        }
            this.setFailures(failures);
            return false;
        }


    public void incrementMarksCount() {
        int marks = this.getMarks();
        marks++;
        this.setMarks(marks);

        }

    public boolean decrementFailuresCount() {
        int failures = this.getFailures();
        failures--;
        if (failures < 3) {
            this.setEstado(1);
            return true;
        }
        this.setFailures(failures);
        return false;
    }


    public void decrementMarksCount() {
        int marks = this.getMarks();
        marks--;
        this.setMarks(marks);

    }
    public void resetFailureCount() {
        this.failures = 0;
    }

    public boolean checkUserStatus() {
        if (this.getFailures() >= 3) {
            this.setEstado(0);
            return true;
        }
        return false;
    }


    public ArrayList<UserClass> getArrayListUser() {//////////////////////77///// sacar??
        return arrayListUser;
    }
    }