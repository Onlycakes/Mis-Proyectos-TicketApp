package com.proyecto.ticketsapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Exceptions.NotFoundException;



public class ConnectionDB extends SQLiteOpenHelper {

    private  int DATABASE_VERSION = 1;
    private String DATABASE_NAME = "T.db";
   // private Cursor cursor;

    public ConnectionDB(@Nullable Context context) {
        super(context,"T.db", null, 1);

    }
    public static final String TABLE_USERS = "Users";
    public static final String COLUMN_ID_USER = "idUser";
    public static final String COLUMN_ROL_USER = "rolUser";
    public static final String COLUMN_FULLNAME_USER = "fullNameUser";
    public static final String COLUMN_CELLPHONE_USER = "cellPhoneUser";
    public static final String COLUMN_EMAIL_USER = "emailUser";
    public static final String COLUMN_USER_USER = "User";
    public static final String COLUMN_PASSWORD = "Password";
    public static final String COLUMN_ESTADO_U = "estado";
    public static final String COLUMN_FAILURES = "failures";
    public static final String COLUMN_MARKS = "marks";


    public static final String TABLE_TICKETS = "Tickets";
    public static final String COLUMN_ID_TICKET = "idTicket";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_ID_EMPLOYEE = "idEmployee";
    public static final String COLUMN_ID_TECHNICIAN = "idTechnician";
    public static final String COLUMN_RESOLUTION = "resolution";
    public static final String COLUMN_ID_NEW_TECHNICIAN = "idNewTechnician";
    public static final String COLUMN_ESTADO_T = "estado";

    private static final String SQL_CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ROL_USER + " VARCHAR NOT NULL, " +
            COLUMN_FULLNAME_USER + " VARCHAR NOT NULL, " +
            COLUMN_CELLPHONE_USER + " VARCHAR NOT NULL, " +
            COLUMN_EMAIL_USER + " VARCHAR NOT NULL, " +
            COLUMN_USER_USER + " VARCHAR NOT NULL, " +
            COLUMN_PASSWORD + " VARCHAR NOT NULL, " +
            COLUMN_ESTADO_U + " INTEGER DEFAULT 1, " +
            COLUMN_FAILURES + " INTEGER DEFAULT 0, " +
            COLUMN_MARKS + " INTEGER DEFAULT 0)";

    private static final String SQL_CREATE_TABLE_TICKETS = "CREATE TABLE " + TABLE_TICKETS + " (" +
            COLUMN_ID_TICKET + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
            COLUMN_STATUS + " TEXT NOT NULL, " +
            COLUMN_ID_EMPLOYEE + " INTEGER NOT NULL, " +
            COLUMN_ID_TECHNICIAN + " INTEGER, " +
            COLUMN_RESOLUTION + " TEXT, " +
            COLUMN_ID_NEW_TECHNICIAN + " INTEGER, " +
            COLUMN_ESTADO_T + " INTEGER DEFAULT 1)";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_USERS);
        db.execSQL(SQL_CREATE_TABLE_TICKETS);
        insertAdministrator(db);
    }

    private void insertAdministrator(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROL_USER, "administrador");
        values.put(COLUMN_FULLNAME_USER, "Administrador");
        values.put(COLUMN_EMAIL_USER, "admin@admin.com");
        values.put(COLUMN_CELLPHONE_USER, "1111111111");
        values.put(COLUMN_USER_USER, "1");
        values.put(COLUMN_PASSWORD, "1");
        db.insert(TABLE_USERS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
        onCreate(db);

    }

        public Long saveUser(UserClass userClass) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = createUserContentValues(userClass);

            Log.d("saveUser", "Guardando usuario: " + values.toString()); // Log para depuración

            long result = -1;
            try {
                result = db.insert(TABLE_USERS, null, values);
                if (result == -1) {
                    Log.e("saveUser", "Error al insertar el usuario");
                }
            } catch (Exception e) {
                Log.e("saveUser", "Excepción al guardar usuario", e);
            } finally {
                db.close();
            }
            return result;
        }


    private ContentValues createUserContentValues(UserClass userClass) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROL_USER, userClass.getRol());
        values.put(COLUMN_FULLNAME_USER, userClass.getFullname());
        values.put(COLUMN_CELLPHONE_USER, userClass.getCellPhone());
        values.put(COLUMN_EMAIL_USER, userClass.getEmail());
        values.put(COLUMN_USER_USER, userClass.getUserUser());
        values.put(COLUMN_PASSWORD, userClass.getPasswordUser());
        values.put(COLUMN_ESTADO_U, userClass.getEstado());
        values.put(COLUMN_FAILURES, userClass.getFailures());
        values.put(COLUMN_MARKS, userClass.getMarks());
        return values;
    }

    public UserClass getUserById(int id) throws NotFoundException {
        UserClass userClass = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_ID_USER + " = ? ", new String[]{String.valueOf(id)});
            if (cursor.moveToFirst()) {
                userClass = new UserClass(
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
            } else {
                throw new NotFoundException("No se encontró el usuario con id: " + id);
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al acceder a la base de datos", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return userClass;
    }

    public UserClass getLoggedUser(String user, String password) throws NotFoundException {
        UserClass userClass = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_USER + " = ? AND " + COLUMN_PASSWORD + " = ? ", new String[]{user, password});
            if (cursor.moveToFirst()) {
                userClass = new UserClass(
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
            } else {
                throw new NotFoundException("No se encontró el usuario ");
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al acceder a la base de datos", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return userClass;
    }


    public String getUserByEmail(String email) throws NotFoundException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String id;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL_USER + " = ? ", new String[]{(email)});
            if (cursor.moveToFirst()) {
                id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_USER));
            } else {
                throw new NotFoundException("No se encontró el usuario con email: " + email);
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al acceder a la base de datos", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return id;
    }

    public String getUserByUser(String user) throws NotFoundException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String id;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_USER + " = ? ", new String[]{(user)});
            if (cursor.moveToFirst()) {
                id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_USER));
            } else {
                throw new NotFoundException("No se encontró el usuario con username: " + user);
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al acceder a la base de datos", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return id;
    }

    public List<UserClass> listAllUsers() {
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
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return list;
    }

  /*  public List<UserClass> listAllUsers() {
        List<UserClass> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
            if (cursor.moveToFirst()) {
                do {
                    Log.d("listAllUsers", "Usuario recuperado: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULLNAME_USER)));

                    list.add(userClass);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e("listAllUsers", "Error al listar usuarios: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return list;
    }*/


    public int update(UserClass userClass, int id) throws NotFoundException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ROL_USER, userClass.getRol());
        contentValues.put(COLUMN_FULLNAME_USER, userClass.getFullname());
        contentValues.put(COLUMN_EMAIL_USER, userClass.getEmail());
        contentValues.put(COLUMN_CELLPHONE_USER, userClass.getCellPhone());

        int rowsUpdated = -1;

        try {
            rowsUpdated = db.update(TABLE_USERS, contentValues, COLUMN_ID_USER + " = ? ", new String[]{String.valueOf(id)});
            if (rowsUpdated == 0) {
                throw new NotFoundException("No se encontró el usuario con id: " + id);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowsUpdated;
    }

    public void updateFailures(UserClass userClass) throws NotFoundException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FAILURES, userClass.getFailures());

        try {
            db.update(TABLE_USERS, contentValues, COLUMN_ID_USER + " = ? ", new String[]{String.valueOf(userClass.getIdUser())});

        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.close();
        }
    }

    public void updateMarks(UserClass userClass) throws NotFoundException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MARKS, userClass.getMarks());

        try {
            db.update(TABLE_USERS, contentValues, COLUMN_ID_USER + " = ? ", new String[]{String.valueOf(userClass.getIdUser())});

        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.close();
        }
    }

    public int updatePassword(int id, String password) throws NotFoundException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PASSWORD, password);

        int result = -1;
        try {
            db.update(TABLE_USERS, contentValues, COLUMN_ID_USER + " = ? ", new String[]{String.valueOf(id)});

        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.close();
        }
        return result;
    }

    public void delete(UserClass userClass) {/*
        String sql = "DELETE FROM Users where idUser = ?";
        try {PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userClass.getIdUser());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new NotFoundException("Usuario no encontrado para eliminar");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage(), e);
        }    catch (NotFoundException ex) {
            Logger.getLogger(UserClassImplementsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    public void deshabilitarUserClass(int id) throws NotFoundException { //cómo hacer where estado = 1
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ESTADO_U, 0);
        try {
            db.update(TABLE_USERS, contentValues, COLUMN_ID_USER + " = ? AND " + COLUMN_ESTADO_U + " = ?", new String[]{String.valueOf(id), "1"});

        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.close();
        }
    }

    public void habilitarUserClass(int id) throws NotFoundException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ESTADO_U, 1);
        try {
            db.update(TABLE_USERS, contentValues, COLUMN_ID_USER + " = ? AND " + COLUMN_ESTADO_U + " = ?", new String[]{String.valueOf(id), "0"});

        } catch (SQLiteException e) {
            e.printStackTrace();

        } finally {
            db.close();
        }
    }

    public long saveTicket(TicketsClass ticketsClass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, ticketsClass.getTitle());
        values.put(COLUMN_DESCRIPTION, ticketsClass.getDescription());
        values.put(COLUMN_ID_EMPLOYEE, ticketsClass.getIdEmployee());
        values.put(COLUMN_ID_TECHNICIAN, ticketsClass.getIdTechnic());
        values.put(COLUMN_ID_NEW_TECHNICIAN, ticketsClass.getNewAssignmentTechnic());
        values.put(COLUMN_STATUS, "not_attended");

        long result = -1;
        try {
            result = db.insert(TABLE_TICKETS, null, values);
        } finally {
            db.close();
        }
        return result;
    }


    public TicketsClass getTicketById(int id) throws NotFoundException {
        TicketsClass ticketsClass = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_ID_TICKET + " = ? ", new String[]{String.valueOf(id)});
            if (cursor.moveToFirst()) { //ver si agregar estado 1
                ticketsClass = new TicketsClass(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_EMPLOYEE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))

                );

                ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_TICKET)));
                ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_TECHNICIAN)));
                ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_NEW_TECHNICIAN)));
                ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))); // ver que sea así
                ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESOLUTION)));
                ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_T)));
            } else {
                throw new NotFoundException("No se encontró el ticket con id: " + id);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al acceder a la base de datos", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return ticketsClass;
    }

    public List<TicketsClass> listAllTickets() {
        List<TicketsClass> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_TICKETS;
        try {cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    TicketsClass ticketsClass = new TicketsClass(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_EMPLOYEE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))

                    );
                    ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_TICKET)));
                    ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_TECHNICIAN)));
                    ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_NEW_TECHNICIAN)));
                    ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))); // ver que sea así
                    ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESOLUTION)));
                    ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_T)));
                    list.add(ticketsClass);
                } while (cursor.moveToNext());
            }
    } catch (SQLiteException e) {
        e.printStackTrace();
        throw new RuntimeException("Error al acceder a la base de datos", e);
    } finally {
        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }

        return list;
}
    public List<TicketsClass> listAllNotAttended() {
        List<TicketsClass> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_STATUS + " = ? ";

        String[] selectionArgs = {"not_attended"};

        try {cursor = db.rawQuery(query, selectionArgs);
            if (cursor.moveToFirst()) {
                do {
                    TicketsClass ticketsClass = new TicketsClass(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_EMPLOYEE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_TICKET)));
                    ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_TECHNICIAN)));
                    ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_NEW_TECHNICIAN)));
                    ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))); // ver que sea así
                    ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESOLUTION)));
                    ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_T)));
                    list.add(ticketsClass);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public List<TicketsClass> listAllAttended() {
        List<TicketsClass> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_STATUS + " = ? ";

        String[] selectionArgs = {"attended"};

        try {cursor = db.rawQuery(query, selectionArgs);
            if (cursor.moveToFirst()) {
                do {
                    TicketsClass ticketsClass = new TicketsClass(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_EMPLOYEE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_TICKET)));
                    ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_TECHNICIAN)));
                    ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_NEW_TECHNICIAN)));
                    ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))); // ver que sea así
                    ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESOLUTION)));
                    ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_T)));
                    list.add(ticketsClass);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public List<TicketsClass> listAllResolved() {
        List<TicketsClass> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_STATUS + " = ? ";

        String[] selectionArgs = {"resolved"};

        try {cursor = db.rawQuery(query, selectionArgs);
            if (cursor.moveToFirst()) {
                do {
                    TicketsClass ticketsClass = new TicketsClass(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_EMPLOYEE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_TICKET)));
                    ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_TECHNICIAN)));
                    ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_NEW_TECHNICIAN)));
                    ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))); // ver que sea así
                    ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESOLUTION)));
                    ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_T)));
                    list.add(ticketsClass);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public List<TicketsClass> listAllFinalized() {
        List<TicketsClass> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_STATUS + " = ? ";

        String[] selectionArgs = {"finalized"};

        try {cursor = db.rawQuery(query, selectionArgs);
            if (cursor.moveToFirst()) {
                do {
                    TicketsClass ticketsClass = new TicketsClass(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_EMPLOYEE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_TICKET)));
                    ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_TECHNICIAN)));
                    ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_NEW_TECHNICIAN)));
                    ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))); // ver que sea así
                    ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESOLUTION)));
                    ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_T)));
                    list.add(ticketsClass);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public List<TicketsClass> listAllReasigned() {
        List<TicketsClass> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_STATUS + " = ? ";

        String[] selectionArgs = {"reasigned"};

        try {cursor = db.rawQuery(query, selectionArgs);
            if (cursor.moveToFirst()) {
                do {
                    TicketsClass ticketsClass = new TicketsClass(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_EMPLOYEE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_TICKET)));
                    ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_TECHNICIAN)));
                    ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_NEW_TECHNICIAN)));
                    ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))); // ver que sea así
                    ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESOLUTION)));
                    ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_T)));
                    list.add(ticketsClass);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public List<TicketsClass> listAllEmployee(int user) {
        List<TicketsClass> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        //String query = "SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_ID_EMPLOYEE + " = ? AND " + COLUMN_STATUS + " = ? OR " + COLUMN_STATUS + " = ? OR " + COLUMN_STATUS + " = ? ";
        String query = " SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_ID_EMPLOYEE + " = ? AND " + COLUMN_STATUS + " IN (?, ?, ?) ";
        String[] selectionArgs = {String.valueOf(user), "not_attended", "attended", "reasigned"};

        try {cursor = db.rawQuery(query, selectionArgs);
            if (cursor.moveToFirst()) {
                do {
                    TicketsClass ticketsClass = new TicketsClass(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_EMPLOYEE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_TICKET)));
                    ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_TECHNICIAN)));
                    ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_NEW_TECHNICIAN)));
                    ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))); // ver que sea así
                    ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESOLUTION)));
                    ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_T)));
                    list.add(ticketsClass);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }


    public List<TicketsClass> listAllReopened(String user) {
        List<TicketsClass> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_STATUS + " = ? ";

        String[] selectionArgs = {"reopened"};

        try {cursor = db.rawQuery(query, selectionArgs);
            if (cursor.moveToFirst()) {
                do {
                    TicketsClass ticketsClass = new TicketsClass(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_EMPLOYEE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_TICKET)));
                    ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_TECHNICIAN)));
                    ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_NEW_TECHNICIAN)));
                    ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))); // ver que sea así
                    ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESOLUTION)));
                    ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_T)));
                    list.add(ticketsClass);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public List<TicketsClass> listResolvedEmployee(String user) {

        List<TicketsClass> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_ID_EMPLOYEE + " = ? AND " + COLUMN_ESTADO_T + " = ? AND " + COLUMN_STATUS + " = ?";
        String[] selectionArgs = {user, "1", "resolved"};

        try{ cursor = db.rawQuery(query, selectionArgs);
            if (cursor.moveToFirst()) {
                do {
                    TicketsClass ticketsClass = new TicketsClass(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_EMPLOYEE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_TICKET)));
                    ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_TECHNICIAN)));
                    ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_NEW_TECHNICIAN)));
                    ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))); // ver que sea así
                    ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESOLUTION)));
                    ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_T)));
                    list.add(ticketsClass);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public List<TicketsClass> listAllTechnician(String user) {
        List<TicketsClass> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_ESTADO_T + " = ? AND " + COLUMN_STATUS + " = ? OR " + (COLUMN_STATUS + " = ? AND " + COLUMN_ID_TECHNICIAN + "<> ?");
        String[] selectionArgs = {"1", "not_attended", "reasigned", user};

        try { cursor = db.rawQuery(query, selectionArgs);
            if (cursor.moveToFirst()) {
                do {
                    TicketsClass ticketsClass = new TicketsClass(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_EMPLOYEE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_TICKET)));
                    ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_TECHNICIAN)));
                    ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_NEW_TECHNICIAN)));
                    ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))); // ver que sea así
                    ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESOLUTION)));
                    ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_T)));
                    list.add(ticketsClass);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public List<TicketsClass> listAllAttendedTechnician(String user) {
        List<TicketsClass> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_TICKETS + " WHERE " + COLUMN_ESTADO_T + " = ? AND " + COLUMN_STATUS + " = ? AND " + COLUMN_ID_TECHNICIAN + "= ?";
        String[] selectionArgs = {"1", "attended", user};

        try { cursor = db.rawQuery(query, selectionArgs);
            if (cursor.moveToFirst()) {
                do {
                    TicketsClass ticketsClass = new TicketsClass(
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_EMPLOYEE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_TICKET)));
                    ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_TECHNICIAN)));
                    ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_NEW_TECHNICIAN)));
                    ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))); // ver que sea así
                    ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESOLUTION)));
                    ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_T)));
                    list.add(ticketsClass);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public int updateTicket(TicketsClass ticket, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, ticket.getTitle());
        values.put(COLUMN_DESCRIPTION, ticket.getDescription());

        int rowsUpdated = 0;
        try {
            rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ID_TICKET + " = ? AND " + COLUMN_STATUS + " = ? ", new String[]{String.valueOf(id), "not_attended"});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowsUpdated;
    }
    //ticketsAdapter.notifyDataSetChanged();

   /* public int updateAttendedTicket(String user, int id, TicketsClass ticket) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, "attended");///////////////////
        values.put(COLUMN_ID_TECHNICIAN, user);

        int rowsUpdated = 0;
        try {
            rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ESTADO_T + " = ? AND " + COLUMN_ID_TICKET + " = ? ", new String[]{"1", String.valueOf(id)});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowsUpdated;
    }*/
   /*public int updateAttendedTicket(String user, int id, TicketsClass ticket) {
       SQLiteDatabase db = this.getWritableDatabase();
       ContentValues values = new ContentValues();
       values.put(COLUMN_TITLE,ticket.getTitle());
       values.put(COLUMN_DESCRIPTION, ticket.getDescription());
       values.put(COLUMN_ID_EMPLOYEE, ticket.getIdEmployee());
       values.put(COLUMN_STATUS, "attended"); // Cambia el estado a "attended"
       values.put(COLUMN_ID_TECHNICIAN, user); // Establece el ID del técnico

       int rowsUpdated = 0;
       try {
           // Actualiza el ticket donde el estado es "not_attended" y el ticket está activo
           rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ID_TICKET + " = ?",
                   new String[]{String.valueOf(id)});
       } catch (SQLiteException e) {
           e.printStackTrace();
       } finally {
           db.close();
       }
       return rowsUpdated;
   }*/
   public int updateAttendedTicket(String user, int id, TicketsClass ticket, String status) {
       SQLiteDatabase db = this.getWritableDatabase();
       ContentValues values = new ContentValues();
       values.put(COLUMN_TITLE, ticket.getTitle());
       values.put(COLUMN_DESCRIPTION, ticket.getDescription());
       values.put(COLUMN_ID_EMPLOYEE, ticket.getIdEmployee());
       values.put(COLUMN_STATUS, status); // Cambia el estado a "attended"
       values.put(COLUMN_ID_TECHNICIAN, user); // Establece el ID del técnico

       /*int rowsUpdated = 0;
       try {
           // Actualiza el ticket donde el ID coincide y el estado es "not_attended"
           rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ID_TICKET + " = ? AND " + COLUMN_STATUS + " = ?",
                   new String[]{String.valueOf(id), "not_attended"});
       } catch (SQLiteException e) {
           e.printStackTrace();
       } finally {
           db.close();
       }
       return rowsUpdated;*/
       Log.d("updateAttendedTicket", "Actualizando ticket ID: " + id + " con estado: " + values);

       int rowsUpdated = 0;
       try {
           rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ID_TICKET + " = ?",
                   new String[]{String.valueOf(id)});
           if (rowsUpdated == 0) {
               Log.e("updateAttendedTicket", "No se encontró el ticket con ID: " + id);
           }
       } catch (SQLiteException e) {
           Log.e("updateAttendedTicket", "Error al actualizar el ticket", e);
       } finally {
           db.close();
       }
       return rowsUpdated;
   }


    public int updateResolution(TicketsClass ticket, int id, String user, String resolution) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RESOLUTION, resolution);

        int rowsUpdated = 0;
        try {
            rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ESTADO_T + " = ? AND " + COLUMN_ID_TICKET + " = ? AND " + COLUMN_ID_TECHNICIAN + " = ? AND " + COLUMN_STATUS + " = ? ", new String[]{"1", String.valueOf(id), user, "attended"});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowsUpdated;
    }

    public int updateResolved(TicketsClass ticket, int id, String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RESOLUTION, ticket.getResolution());

        int rowsUpdated = 0;
        try {
            rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ESTADO_T + " = ? AND " + COLUMN_ID_TICKET + " = ? AND " + COLUMN_ID_TECHNICIAN + " = ? AND " + COLUMN_STATUS + " = ? ", new String[]{"1", String.valueOf(id), user, "attended"});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowsUpdated;
    }

    public int returnResolved(TicketsClass ticket, String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, ticket.getStatus());////////////////////

        int rowsUpdated = 0;
        try {
            rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ESTADO_T + " = ? AND " + COLUMN_ID_TICKET + " = ? AND " + COLUMN_ID_TECHNICIAN + " = ? AND " + COLUMN_STATUS + " = ? ", new String[]{"1", String.valueOf(ticket.getIdTicket()), user, "attended"});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowsUpdated;
    }

    public int updateReasign(TicketsClass ticket, String user) {///////desarrollar
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, ticket.getStatus());/////////////////////

        int rowsUpdated = 0;
        try {
            rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ESTADO_T + " = ? AND " + COLUMN_ID_TICKET + " = ? AND " + COLUMN_ID_TECHNICIAN + " = ? AND " + COLUMN_STATUS + " = ? ", new String[]{"1", String.valueOf(ticket.getIdTicket()), user, "attended"});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowsUpdated;
    }

    public int updateNewAsignment(TicketsClass ticket) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, "reasigned");/////////////////7

        int rowsUpdated = 0;
        try {
            rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ESTADO_T + " = ? AND " + COLUMN_ID_TICKET + " = ? " , new String[]{"1", String.valueOf(ticket.getIdTicket())});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowsUpdated;
    }

    public int updateNewTechnician(TicketsClass ticket, String user) { ///desarrollar
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, ticket.getStatus());
        values.put(COLUMN_ID_NEW_TECHNICIAN, user);

        int rowsUpdated = 0;
        try {
            rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ESTADO_T + " = ? AND " + COLUMN_ID_TICKET + " = ? " , new String[]{"1", String.valueOf(ticket.getIdTicket())});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowsUpdated;
    }

    public int updateConfirm(TicketsClass ticket, String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, ticket.getStatus());///////////////7

        int rowsUpdated = 0;
        try {
            rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ESTADO_T + " = ? AND " + COLUMN_ID_TICKET + " = ? AND " + COLUMN_STATUS + " = ? AND " + COLUMN_ID_EMPLOYEE + " = ?", new String[]{"1", String.valueOf(ticket.getIdTicket()), "resolved", user});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowsUpdated;
    }
    public int updateReject(TicketsClass ticket, String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, ticket.getStatus());/////////////////////

        int rowsUpdated = 0;
        try {
            rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ESTADO_T + " = ? AND " + COLUMN_ID_TICKET + " = ? AND " + COLUMN_STATUS + " = ? AND " + COLUMN_ID_EMPLOYEE + " = ?", new String[]{"1", String.valueOf(ticket.getIdTicket()), "resolved", user});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return rowsUpdated;
    }

    public void deshabilitarTicket(TicketsClass ticket) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ESTADO_T, 0);

        int rowsUpdated = 0;
        try {
            rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ESTADO_T + " = ? AND " + COLUMN_STATUS + " = ? AND "+ COLUMN_ID_TICKET + " = ? ", new String[]{"1", "not_attended", String.valueOf(ticket.getIdTicket())});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void rehabilitarTicket(TicketsClass ticket) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ESTADO_T, 1);

        int rowsUpdated = 0;
        try {
            rowsUpdated = db.update(TABLE_TICKETS, values, COLUMN_ESTADO_T + " = ? AND " + COLUMN_STATUS + " = ? AND "+ COLUMN_ID_TICKET + " = ? ", new String[]{"0", "not_attended", String.valueOf(ticket.getIdTicket())});
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        //"UPDATE Tickets SET estado = ? WHERE estado = '1' AND IdTicket = ?";
    }

    public int countTicketsByTechnician(String user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT COUNT(*) FROM " + TABLE_TICKETS + " WHERE (" + COLUMN_ID_TECHNICIAN + " = ? OR " + COLUMN_ID_NEW_TECHNICIAN + " = ?) AND " + COLUMN_STATUS + " = ?";
        String[] selectionArgs = {user, user, "attended"};
        try {cursor = db.rawQuery(query, selectionArgs);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al contar tickets del técnico: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }
}