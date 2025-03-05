package com.proyecto.ticketsapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import DAO.TicketsClassImplementsDAO;
import Exceptions.NotFoundException;

public class AdminTickets extends AppCompatActivity implements TicketsAdapter.OnTicketClickListener {
    private Button resignTicket;
    private RadioButton notAttended, attended, resolved, finalized, reopened, reasigned;
    private RadioGroup tickets;
    private String ticketTitleStr, ticketDescriptionStr;
    private TicketsClass ticketsClass;
    private ArrayList<TicketsClass> reopenedTicketsArray;
    private ArrayList<TicketsClass> notAttendedTicketsArray;
    private ArrayList<TicketsClass> attendedTicketsArray;
    private ArrayList<TicketsClass> resolvedTicketsArray;
    private ArrayList<TicketsClass> finalizedTicketsArray;
    private ArrayList<TicketsClass> reasignedTicketsArray;
    private ArrayList<TicketsClass> reOpenedTicketsArray;
    private String actualUser, select;
    private TicketsAdapter ticketsAdapter;
    private RecyclerView ticketRecyclerView;
    private ConnectionDB connectionDB;
    private int selectedId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionDB = new ConnectionDB(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_tickets);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tickets = findViewById(R.id.radioGroupTickets);
        notAttended = findViewById(R.id.notAttendedRadioButton);
        attended = findViewById(R.id.attendedRadioButton);
        resolved = findViewById(R.id.resolvedRadioButton);
        finalized = findViewById(R.id.finalizedRadioButton);
        reopened = findViewById(R.id.reopenedRadioButton);
        reasigned= findViewById(R.id.reasignedRadioButton);

        ticketRecyclerView = findViewById(R.id.reopenedTicketsView);
        resignTicket = findViewById(R.id.reasignTicketButton);
        try {
            reopenedTicketsArray = (ArrayList<TicketsClass>) connectionDB.listAllTickets(); ///listAllReopened
            ticketsAdapter = new TicketsAdapter(reopenedTicketsArray, this, this);
            ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ticketRecyclerView.setAdapter(ticketsAdapter);


        } catch (SQLiteException e) {
            Logger.getLogger(TicketsClass.class.getName()).log(Level.SEVERE, null, e);

        }
        resignTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reasignTicket(view);
            }
        });

        /*tickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTicket(view);
            }
        });*/
        tickets.setOnCheckedChangeListener((group, checkedId) -> selectTicket());
    }

    @Override
    public void onTicketClick(TicketsClass ticketsClass) {
        this.ticketsClass = ticketsClass;
    }

    public void selectTicket () {
        selectedId = tickets.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(this, "Filtre tickets por estado", Toast.LENGTH_LONG).show();
            return;
        }

        if (notAttended.isChecked()) {

            notAttendedTicketsArray = (ArrayList<TicketsClass>) connectionDB.listAllNotAttended();
            ticketsAdapter = new TicketsAdapter(notAttendedTicketsArray, this, this);
            ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ticketRecyclerView.setAdapter(ticketsAdapter);

        } else if (attended.isChecked()) {

            attendedTicketsArray = (ArrayList<TicketsClass>) connectionDB.listAllAttended();
            ticketsAdapter = new TicketsAdapter(attendedTicketsArray, this, this);
            ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ticketRecyclerView.setAdapter(ticketsAdapter);

        } else if (resolved.isChecked()) {

            resolvedTicketsArray = (ArrayList<TicketsClass>) connectionDB.listAllResolved();
            ticketsAdapter = new TicketsAdapter(resolvedTicketsArray, this, this);
            ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ticketRecyclerView.setAdapter(ticketsAdapter);

        } else if (finalized.isChecked()) {

            finalizedTicketsArray = (ArrayList<TicketsClass>) connectionDB.listAllFinalized();
            ticketsAdapter = new TicketsAdapter(finalizedTicketsArray, this, this);
            ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ticketRecyclerView.setAdapter(ticketsAdapter);

        } else if (reopened.isChecked()) {

            reOpenedTicketsArray = (ArrayList<TicketsClass>) connectionDB.listAllReopened(actualUser);
            ticketsAdapter = new TicketsAdapter(reOpenedTicketsArray, this, this);
            ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ticketRecyclerView.setAdapter(ticketsAdapter);

        } else if (reasigned.isChecked()) {

            reasignedTicketsArray = (ArrayList<TicketsClass>) connectionDB.listAllReasigned();
            ticketsAdapter = new TicketsAdapter(reasignedTicketsArray, this, this);
            ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ticketRecyclerView.setAdapter(ticketsAdapter);
        }
    }

    public void reasignTicket(View view) {
        int reasigned = 0;
        if ("reopened".equals(this.ticketsClass.getStatus())) {
            reasigned = connectionDB.updateNewAsignment(ticketsClass);
            if (reasigned > 0) {
                Toast.makeText(this, "Ticket enviado a reasignar", Toast.LENGTH_LONG).show();
                updateTable();
            }
        }
        else Toast.makeText(this, "Error. No se puede reasignar", Toast.LENGTH_LONG).show();
    }

    public void updateTable() {
        try {
            reopenedTicketsArray = (ArrayList<TicketsClass>) connectionDB.listAllReopened(actualUser);
            ticketsAdapter = new TicketsAdapter(reopenedTicketsArray, this, this);
            ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ticketRecyclerView.setAdapter(ticketsAdapter);


        } catch (SQLiteException e) {
            Logger.getLogger(TicketsClass.class.getName()).log(Level.SEVERE, null, e);

        }
    }
}
