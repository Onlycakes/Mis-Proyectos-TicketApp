package com.proyecto.ticketsapp;


import java.util.ArrayList;

public class TicketRepository {
    private static final TicketRepository instance = new TicketRepository();
    private TicketsClass ticketsClass;
    private static int counter=1;
    private final ArrayList<TicketsClass> ticketsList = new ArrayList<>();

    public static TicketRepository getInstance() {
        return instance;
    }

    public void setTicketClass(TicketsClass ticketsClass) {
        this.ticketsClass=ticketsClass;
        setIdTicket();
    }

    public void setIdTicket () {
        this.ticketsClass.setIdTicket(counter);
        updateCounter();
        ticketsList.add(this.ticketsClass);
        //addTicket(this.ticketsClass);
    }

    public void updateCounter () {
       this.counter++;
   }

    public ArrayList<TicketsClass> getTicketsList() {
        return ticketsList;
    }

}
