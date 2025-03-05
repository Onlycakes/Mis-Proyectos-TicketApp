package DAO;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.proyecto.ticketsapp.UserClass;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import Exceptions.NotFoundException;

public class UserClassImplementsDAO implements DAO<UserClass> {

private static List<UserClass> list=new ArrayList();
private UserClass userClass;
private SQLiteDatabase connection;

 public UserClassImplementsDAO(SQLiteDatabase connection) {
        this.connection = connection;
    }

        @Override
    public void save(UserClass userClass) {
        String sql = "INSERT INTO Users (rolUser, fullNameUser, emailUser, cellPhoneUser, user, password) VALUES (?, ?, ?, ?, ?, ?)";
            try {
                connection.execSQL(sql, new Object[]{
                        userClass.getRol(),
                        userClass.getFullname(),
                        userClass.getEmail(),
                        userClass.getCellPhone(),
                        userClass.getUserUser(),
                        userClass.getPasswordUser()
                });
            }
        catch (SQLiteException e){
                e.printStackTrace();
        }
    }

    @Override
    public UserClass getById(int id) throws NotFoundException {
        UserClass userClass = null;
        String sql = "SELECT * FROM Users WHERE idUser = ?";
        Cursor cursor = null;

        try {
            if (connection != null) {
                cursor = connection.rawQuery(sql, new String[]{String.valueOf(id)});
                if (cursor != null && cursor.moveToFirst()) {
                    userClass = new UserClass(
                            cursor.getString(cursor.getColumnIndexOrThrow("rolUser")),
                            cursor.getString(cursor.getColumnIndexOrThrow("fullNameUser")),
                            cursor.getString(cursor.getColumnIndexOrThrow("emailUser")),
                            cursor.getString(cursor.getColumnIndexOrThrow("cellPhoneUser"))
                    );
                    userClass.setIdUser(cursor.getInt(cursor.getColumnIndexOrThrow("idUser")));
                    userClass.setUserUser(cursor.getString(cursor.getColumnIndexOrThrow("user")));
                    userClass.setPasswordUser(cursor.getString(cursor.getColumnIndexOrThrow("password")));
                    userClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow("estado")));
                    userClass.setFailures(cursor.getInt(cursor.getColumnIndexOrThrow("fallas")));
                } else {
                    throw new NotFoundException("No se encontró el usuario con id: " + id);
                }
            } else {
                throw new RuntimeException("Conexion nula");
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al acceder a la base de datos", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userClass;
    }

    @Override
    public List <UserClass> listAll() {
        List<UserClass> list = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        Cursor cursor = null;
        try
        {
            cursor = connection.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    UserClass userClass = new UserClass(
                            cursor.getString(cursor.getColumnIndexOrThrow("rolUser")),
                            cursor.getString(cursor.getColumnIndexOrThrow("fullNameUser")),
                            cursor.getString(cursor.getColumnIndexOrThrow("emailUser")),
                            cursor.getString(cursor.getColumnIndexOrThrow("cellPhoneUser"))
                    );

                    userClass.setIdUser(cursor.getInt(cursor.getColumnIndexOrThrow("idUser")));
                    userClass.setUserUser(cursor.getString(cursor.getColumnIndexOrThrow("user")));
                    userClass.setPasswordUser(cursor.getString(cursor.getColumnIndexOrThrow("password")));
                    userClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow("estado")));
                    userClass.setFailures(cursor.getInt(cursor.getColumnIndexOrThrow("fallas")));
                    list.add(userClass);

                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al listar usuarios: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }

    @Override
    public void update(UserClass userClass, int id) throws NotFoundException {
      String sql = "UPDATE Users SET rolUser = ?, fullNameUser = ?, cellPhoneUser = ?, emailUser = ?, password = ? WHERE idUser = ?";
        try {
            connection.execSQL(sql, new Object[]{
                    userClass.getRol(),
                    userClass.getFullname(),
                    userClass.getCellPhone(),
                    userClass.getEmail(),
                    userClass.getPasswordUser(),
                    id
            });
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    public void updatePassword (int id, String password) throws NotFoundException {
        String sql = "UPDATE Users SET password = ? WHERE idUser = ?";
        try {
            connection.execSQL(sql, new Object[]{password, id});
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al actualizar contraseña: " + e.getMessage(), e);
        }

    }

    @Override
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

    public void close() throws SQLException {

    }

    public void deshabilitarUserClass(int id) throws NotFoundException {
        String sql = "UPDATE Users SET estado = 0 WHERE estado = 1 AND idUser = ?";
        try {
            connection.execSQL(sql, new Object[]{id});
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al deshabilitar usuario: " + e.getMessage(), e);
        }
    }

    public void habilitarUserClass(int id) throws NotFoundException {
        String sql = "UPDATE Users SET estado = ? WHERE estado = 0 AND Id = ?";
        try {
            connection.execSQL(sql, new Object[]{id});
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al habilitar usuario: " + e.getMessage(), e);
        }
    }
}


