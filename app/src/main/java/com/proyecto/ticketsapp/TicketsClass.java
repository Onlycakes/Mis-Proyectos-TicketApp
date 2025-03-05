package com.proyecto.ticketsapp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import DAO.TicketsClassImplementsDAO;

public class TicketsClass {
    //public String TicketStatus {
        //NOT_ATTENDED,
        //ATTENDED,
        //RESOLVED,
        //FINALIZED,
        //REOPENED,
        //REASIGNED
   // }
 private int idTicket;
 private String title;
 private String description;
 private String status;
 private String idEmployee;
 private String idTechnic;
 private String resolution;
 private String newAssignmentTechnic;
 private int estado;
 private TicketsClassImplementsDAO ticketsClassImplementsDAO;
 private int failures;


    public TicketsClass (String idEmployee, String title, String description) {
        this.idEmployee=idEmployee;
        this.title= title;
        this.description=description;
        //this.status= "not_attended";
         }

    public void setIdTicket (int idTicket) {
        this.idTicket=idTicket;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdEmployee(String idEmployee) {
        this.idEmployee = idEmployee;
    }

    public void setIdTechnic(String idTechnic) {
        this.idTechnic = idTechnic;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setNewAssignmentTechnic(String newAssignmentTechnic) {
        this.newAssignmentTechnic = newAssignmentTechnic;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getIdTicket() {
        return this.idTicket;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getStatus () {
        return this.status;
    }

    public String getIdEmployee() {
        return this.idEmployee;
    }

    public String getIdTechnic() {
        return this.idTechnic;
    }

    public String getResolution() {
        return this.resolution;
    }

    public String getNewAssignmentTechnic() {
        return this.newAssignmentTechnic;
    }

    public int getEstado() {
        return this.estado;
    }

    public void incrementFailurCount() {
        this.failures++;
    }


}