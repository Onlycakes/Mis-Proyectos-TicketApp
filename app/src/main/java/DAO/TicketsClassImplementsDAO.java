package DAO;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.proyecto.ticketsapp.TicketsClass;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import Exceptions.NotFoundException;

public class TicketsClassImplementsDAO implements DAO<TicketsClass> {
    private SQLiteDatabase connection;
    private static List<TicketsClass> list = new ArrayList();
    private TicketsClass ticketsClass;

    public TicketsClassImplementsDAO(SQLiteDatabase connection) throws SQLException {

        this.connection = connection;
    }

    @Override
    public void save(TicketsClass ticketsClass) {
        String sql = "INSERT INTO Tickets (employee, title, description, idTechnician, idNewTechnician, status) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            connection.execSQL(sql, new Object[]{
                    ticketsClass.getIdEmployee(),
                    ticketsClass.getTitle(),
                    ticketsClass.getDescription(),
                    ticketsClass.getIdTechnic(),
                    ticketsClass.getNewAssignmentTechnic(),

            });
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public TicketsClass getById(int id) throws NotFoundException {
        TicketsClass ticketsClass = null;
        String sql = "SELECT * FROM Tickets where Id = ?";
        Cursor cursor = null;

        try {
            cursor = connection.rawQuery(sql, new String[]{String.valueOf(id)});
            if (cursor != null && cursor.moveToFirst()) {
                ticketsClass = new TicketsClass(
                        cursor.getString(cursor.getColumnIndexOrThrow("employee")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description"))
                );
                ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow("idTicket")));
                ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow("idTechnician")));
                ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow("idNewTechnician")));
                ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow("resolution")));
                ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow("estado")));
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
        }
        return ticketsClass;
    }

    @Override
    public List<TicketsClass> listAll() {
        List<TicketsClass> list = new ArrayList<>();
        String sql = "SELECT * FROM Tickets";
        Cursor cursor = null;
        try {
            cursor = connection.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                do {
                    TicketsClass ticketsClass = new TicketsClass(
                            cursor.getString(cursor.getColumnIndexOrThrow("employee")),
                            cursor.getString(cursor.getColumnIndexOrThrow("title")),
                            cursor.getString(cursor.getColumnIndexOrThrow("description"))
                    );
                    ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow("idTicket")));
                    ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow("idTechnician")));
                    ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow("idNewTechnician")));
                    ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                    ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow("resolution")));
                    ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow("estado")));
                    list.add(ticketsClass);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al listar tickets: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /*public List<TicketsClass> listAllEmployee(String user) {
        List<TicketsClass> list = new ArrayList<>();
        String sql = "SELECT * FROM Tickets WHERE idEmployee = ? AND (status = ? OR status = ? OR status = ?)";
        Cursor cursor = null;

        try {
            cursor = connection.rawQuery(sql, new String[]{
                    user,
                    TicketsClass.TicketStatus.NOT_ATTENDED.name(),
                    TicketsClass.TicketStatus.ATTENDED.name(),
                    TicketsClass.TicketStatus.REOPENED.name()
            });

            while (cursor.moveToNext()) {
                TicketsClass ticketsClass = new TicketsClass(
                        cursor.getString(cursor.getColumnIndexOrThrow("employee")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description"))
                );
                ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow("idTicket")));
                ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow("idTechnician")));
                ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow("idNewTechnician")));
                ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow("resolution")));
                ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow("estado")));
                list.add(ticketsClass);
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al listar tickets: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }*/

    /*public List<TicketsClass> listResolvedEmployee(String user) throws SQLException {
        List<TicketsClass> list = new ArrayList<>();
        String sql = "SELECT * FROM Tickets WHERE idEmployee = ? AND estado = '1' AND status = ?";
        Cursor cursor = null;

        try {
            cursor = connection.rawQuery(sql, new String[]{
                    user,
                    TicketsClass.TicketStatus.RESOLVED.name()
            });

            while (cursor.moveToNext()) {
                TicketsClass ticketsClass = new TicketsClass(
                        cursor.getString(cursor.getColumnIndexOrThrow("employee")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description"))
                );
                ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow("idTicket")));
                ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow("idTechnician")));
                ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow("idNewTechnician")));
                ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow("resolution")));
                ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow("estado")));
                list.add(ticketsClass);
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al listar tickets: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }*/

    /*public List<TicketsClass> listAllTechnician(String user) {// agregar reabiertos, no?
        List<TicketsClass> list = new ArrayList<>();
        String sql = "SELECT * FROM Tickets WHERE estado = '1' AND status = ? OR (status = ? AND idTechnician <> user = ?)";
        Cursor cursor = null;

        try {
            cursor = connection.rawQuery(sql, new String[]{
                    TicketsClass.TicketStatus.NOT_ATTENDED.name(),
                    TicketsClass.TicketStatus.REOPENED.name(),
                    user
            });

            while (cursor.moveToNext()) {
                TicketsClass ticketsClass = new TicketsClass(
                        cursor.getString(cursor.getColumnIndexOrThrow("employee")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description"))
                );
                ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow("idTicket")));
                ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow("idTechnician")));
                ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow("idNewTechnician")));
                ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow("resolution")));
                ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow("estado")));
                list.add(ticketsClass);
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al listar tickets: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }*/

    /*public List<TicketsClass> listAllAttendedTechnician(String user) {
        List<TicketsClass> list = new ArrayList<>();
        String sql = "SELECT * FROM Tickets WHERE estado = '1' AND idTechnician = ? AND status = ?";
        Cursor cursor = null;

        try {
            cursor = connection.rawQuery(sql, new String[]{
                    user,
                    TicketsClass.TicketStatus.ATTENDED.name()
            });

            while (cursor.moveToNext()) {
                TicketsClass ticketsClass = new TicketsClass(
                        cursor.getString(cursor.getColumnIndexOrThrow("employee")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description"))
                );
                ticketsClass.setIdTicket(cursor.getInt(cursor.getColumnIndexOrThrow("idTicket")));
                ticketsClass.setIdTechnic(cursor.getString(cursor.getColumnIndexOrThrow("idTechnician")));
                ticketsClass.setNewAssignmentTechnic(cursor.getString(cursor.getColumnIndexOrThrow("idNewTechnician")));
                ticketsClass.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                ticketsClass.setResolution(cursor.getString(cursor.getColumnIndexOrThrow("resolution")));
                ticketsClass.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow("estado")));
                list.add(ticketsClass);
            }
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al listar tickets: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }*/

    @Override
    public void update(TicketsClass ticketsClass, int id) throws NotFoundException {
        String sql = "UPDATE Tickets SET title = ?, description = ? WHERE estado = '1' AND IdTicket = ?";
        try {
            connection.execSQL(sql, new Object[]{
                    ticketsClass.getTitle(),
                    ticketsClass.getDescription(),
                    id
            });
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al actualizar ticket: " + e.getMessage(), e);
        }
    }

    /*public void updateAttended(TicketsClass ticketsClass, String user, int id) throws NotFoundException {
        String sql = "UPDATE Tickets SET status = ?, idTechnician = ?  WHERE estado = '1' AND IdTicket = ?";
        try {
            connection.execSQL(sql, new Object[]{
                    TicketsClass.TicketStatus.ATTENDED.name(),
                    Integer.parseInt(user),
                    id
            });
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al actualizar ticket: " + e.getMessage(), e);
        }
    }*/

   /* public void updateResolution(TicketsClass ticketsClass, int id, String user) throws NotFoundException {
        String sql = "UPDATE Tickets SET resolution = ? WHERE estado = '1' AND status = ? AND IdTicket = ? AND idTechnician = ?";
        try {
            connection.execSQL(sql, new Object[]{
                    ticketsClass.getResolution(),
                    TicketsClass.TicketStatus.ATTENDED.name(),
                    id,
                    Integer.parseInt(user)
            });
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al actualizar resolución del ticket: " + e.getMessage(), e);
        }
    }*/

    /*public void updateResolved(TicketsClass ticketsClass, int id, String user) throws NotFoundException {
        String sql = "UPDATE Tickets SET status = ? WHERE status = ? AND estado = '1' AND IdTicket = ? AND IdTechnician = ?";
        try {
            connection.execSQL(sql, new Object[]{
                    TicketsClass.TicketStatus.RESOLVED.name(),
                    TicketsClass.TicketStatus.ATTENDED.name(),
                    id,
                    Integer.parseInt(user)
            });
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al actualizar ticket: " + e.getMessage(), e);
        }
    }*/

    /*public void updateReopened(TicketsClass ticketsClass, int id, String user) throws NotFoundException {
        String sql = "UPDATE Tickets SET status = ? WHERE estado = '1' AND IdTicket = ? AND IdTechnician = ?";
        try {
            connection.execSQL(sql, new Object[]{
                    TicketsClass.TicketStatus.REOPENED.name(),
                    id,
                    Integer.parseInt(user)
            });
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al actualizar ticket: " + e.getMessage(), e);
        }
    }*/

    /*public void updateNewTechnician(TicketsClass ticketsClass, int id, String user) throws NotFoundException {
        String sql = "UPDATE Tickets SET status = ?, idNewTechnician = ? WHERE estado = '1' AND IdTicket = ?";
        try {
            connection.execSQL(sql, new Object[]{
                    TicketsClass.TicketStatus.ATTENDED.name(),
                    Integer.parseInt(user),
                    id
            });
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al actualizar técnico del ticket: " + e.getMessage(), e);
        }
    }*/

    public void updateConfirm(TicketsClass ticketsClass, String user) throws NotFoundException {
        String sql = "UPDATE Tickets SET status = ? WHERE estado = '1' AND IdTicket = ? AND idEmployee = ?";
        try {
            connection.execSQL(sql, new Object[]{
                    ticketsClass.getStatus(),
                    ticketsClass.getIdTicket(),
                    Integer.parseInt(user)
            });
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al actualizar ticket: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(TicketsClass ticketsClass) {
        String sql = "DELETE FROM Tickets WHERE IdTicket = ?";
        try {
            connection.execSQL(sql, new Object[]{ticketsClass.getIdTicket()});
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al eliminar ticket: " + e.getMessage(), e);
        }
    }

    public void deshabilitarTicket(TicketsClass ticketsClass) throws NotFoundException {
        String sql = "UPDATE Tickets SET estado = ? WHERE estado = '1' AND IdTicket = ?";
        try {
            connection.execSQL(sql, new Object[]{ticketsClass.getIdTicket()});
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al deshabilitar ticket: " + e.getMessage(), e);
        }
    }

    public void habilitarTicket(TicketsClass ticketsClass) throws NotFoundException {
        String sql = "UPDATE Tickets SET estado = ? WHERE estado = '0' AND IdTicket = ?";
        try {
            connection.execSQL(sql, new Object[]{ticketsClass.getIdTicket()});
        } catch (SQLiteException e) {
            throw new RuntimeException("Error al habilitar ticket: " + e.getMessage(), e);
        }
    }

    /*public int countTicketsByTechnician(String user) {
        String sql = "SELECT COUNT(*) FROM Tickets WHERE idTechnician = ? AND status = ?";
        Cursor cursor = null;
        try {
            cursor = connection.rawQuery(sql, new String[]{
                    user,
                    TicketsClass.TicketStatus.ATTENDED.name()
            });

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
    }*/
}
