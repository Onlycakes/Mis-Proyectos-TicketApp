package com.proyecto.ticketsapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.sql.SQLData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DAO.UserClassImplementsDAO;

public class MainLoginClass {
 private String user;
 private String password;
 private String actualUser;
 private ConnectionDB connectionDB;
 private SQLiteDatabase db;


    public MainLoginClass(String user, String password, Context context) throws SQLException {
        this.user = user;
        this.password = password;
        this.connectionDB = new ConnectionDB(context);
    }

    protected UserClass validLog() /*(MainLoginClass log)*/
{
       try {

            ArrayList <UserClass> arrayListUser= (ArrayList<UserClass>) connectionDB.listAllUsers();
            for (UserClass userClass : arrayListUser) {
                if (userClass.getUserUser().equalsIgnoreCase(user) && userClass.getPasswordUser().equalsIgnoreCase(password) && userClass.getEstado()==1) {
                    this.actualUser=userClass.getUserUser();
                    return userClass;
                }
            }
        }
        catch (Exception e) {
            Log.e("Login", "Error durante la validación del login: " + e.getMessage());
            return null;
        }
        return null;
   /* public List<UserClass> listAllUsers() {
    List<UserClass> list = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = null;

    try {
        cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        if (cursor.moveToFirst()) {
            do {
                UserClass userClass = new UserClass(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROL_USER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULLNAME_USER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_USER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CELLPHONE_USER))
                );

                userClass.setIdUser(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_USER)));
                userClass.setUserUser(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_USER)));
                userClass.setPasswordUser(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
                userClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_U)));
                userClass.setFailures(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FAILURES)));
                userClass.setMarks(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MARKS)));
                list.add(userClass);
            } while (cursor.moveToNext());
        }
    } catch (SQLiteException e) {
        throw new RuntimeException("Error al listar usuarios: " + e.getMessage(), e);
    } /*finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }*/

    }

    protected boolean newPass () {
        if (this.user.matches(this.password)) {
            return true;
        }
        return false;
    }
    protected String getActualUser () {
        return this.actualUser;
    }

}
