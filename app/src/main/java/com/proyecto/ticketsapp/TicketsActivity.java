package com.proyecto.ticketsapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class TicketsActivity extends AppCompatActivity implements TicketsAdapter.OnTicketClickListener {
    private EditText ticketTitleMain, ticketDescriptionMain;
    private Button loadTicket, updateTicket, deleteTicket, confirmTicket, rejectTicket;
    private String ticketTitleStr, ticketDescriptionStr;
    private TicketsClass ticketsClass;
    private TicketsClass selectedTicket;
    private ArrayList<TicketsClass> ticketsArrayList;
    private ArrayList<TicketsClass> ticketsArrayListResolved;
    private String actualUser;
    private TicketsAdapter ticketsAdapter;
    private RecyclerView ticketRecyclerView;
    private RecyclerView ticketRecyclerViewResolved;
    private ScrollView scrollView;
    private TextView resolutionText;
    private ConnectionDB connectionDB;
    private SQLiteDatabase accessDB;
    private int idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tickets_employee);
        connectionDB = new ConnectionDB(this);
        accessDB = connectionDB.getReadableDatabase();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        actualUser = intent.getStringExtra("actualUser");
        try {
            idUser = Integer.parseInt(connectionDB.getUserByUser(actualUser));
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        ticketTitleMain = findViewById(R.id.ticketTitleET);
        ticketDescriptionMain = findViewById(R.id.ticketDescriptionET);
        loadTicket = findViewById(R.id.acceptLoadTicketButton);
        updateTicket = findViewById(R.id.updateTicketButton);
        deleteTicket = findViewById(R.id.deleteTicketButton);
        confirmTicket = findViewById(R.id.confirmTicketButton);
        rejectTicket = findViewById(R.id.rejectTicketButton);
        ticketRecyclerView = findViewById(R.id.ticketRecyclerViewPending);
        ticketRecyclerViewResolved = findViewById(R.id.ticketRecyclerViewSolved);
        resolutionText = findViewById(R.id.resolutionTextView);
        scrollView = findViewById(R.id.resolutionScrollView);

        try {
            ticketsArrayList = (ArrayList<TicketsClass>) connectionDB.listAllEmployee(idUser);
            ticketsAdapter = new TicketsAdapter(ticketsArrayList, this, this);
            ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ticketRecyclerView.setAdapter(ticketsAdapter);

            ticketsArrayListResolved = (ArrayList<TicketsClass>) connectionDB.listResolvedEmployee(actualUser);
            ticketsAdapter = new TicketsAdapter(ticketsArrayListResolved, this, this);
            ticketRecyclerViewResolved.setLayoutManager(new LinearLayoutManager(this));
            ticketRecyclerViewResolved.setAdapter(ticketsAdapter);

           // updateTable();

        } catch (SQLiteException e) {
            Log.e("UserActivity", "Error estableciendo la conexión a la base de datos: " + e.getMessage());
            Toast.makeText(this, "Error al abrir la base de datos", Toast.LENGTH_LONG).show();
        }

        loadTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTicket(view);
            }
        });

        updateTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpdate(view);
            }
        });

        deleteTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rehabTicket(view);
            }
        });

        confirmTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTicket != null) {
                    try {
                        confirmTicketResolution(selectedTicket);
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        rejectTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTicket != null) {
                    try {
                        rejectTicketResolution(selectedTicket);
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void setTicket (View view) {
        try {
        ticketTitleStr = ticketTitleMain.getText().toString();
        ticketDescriptionStr = ticketDescriptionMain.getText().toString();
            if (this.ticketTitleStr == null || this.ticketTitleStr.isEmpty() || this.ticketDescriptionStr == null || this.ticketDescriptionStr.isEmpty()) {
                Toast.makeText(this, "Los campos del ticket no pueden estar vacios", Toast.LENGTH_LONG).show();
                return;
            }

        this.ticketsClass = new TicketsClass(this.actualUser, this.ticketTitleStr, this.ticketDescriptionStr);

            if (this.ticketsClass.getIdTicket() == 0) {
                this.ticketsClass.setStatus("not_attended");
                //ticketsClass.setIdEmployee(actualUser);
                Long newTicket=connectionDB.saveTicket(this.ticketsClass);
                if (newTicket == -1) {
                    Toast.makeText(this, "Error al crear el ticket", Toast.LENGTH_LONG).show();
                }
                if (newTicket !=-1) {
                    Toast.makeText(this, "Alta de ticket exitosa", Toast.LENGTH_LONG).show();
                    updateTable();
                    clear();
                }
            }

    } catch (Exception e) {
            throw new RuntimeException(e);
        }}


    public void setUpdate (View view) {
        //  setCampos(ticketsClass);
        if (this.selectedTicket != null) {
            try {
                ticketTitleStr= ticketTitleMain.getText().toString();
                ticketDescriptionStr= ticketDescriptionMain.getText().toString();
                if (this.ticketTitleStr == null || this.ticketTitleStr.isEmpty() || this.ticketDescriptionStr == null || this.ticketDescriptionStr.isEmpty()) {
                    Toast.makeText(this, "Los campos del ticket no pueden estar vacios", Toast.LENGTH_LONG).show();
                    return;
                }
                selectedTicket.setTitle(ticketTitleStr);
                selectedTicket.setDescription(ticketDescriptionStr);

                int id = selectedTicket.getIdTicket();
                int updatedTicket = connectionDB.updateTicket(selectedTicket, id);
                if (updatedTicket == -1) {
                    Toast.makeText(this, "error al actualizar Ticket", Toast.LENGTH_LONG).show();
                }
                if (updatedTicket !=-1) {
                    Toast.makeText(this, "Ticket actualizado", Toast.LENGTH_LONG).show();
                    updateTable();
                }

                updateTable(); /////////////
                clear();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void setCampos (TicketsClass t) {
        ticketTitleMain.setText(t.getTitle());
        ticketDescriptionMain.setText(t.getDescription());
    }

    private void rehabTicket(View view) {

        try {

            if (this.selectedTicket.getEstado() == 0) {
                connectionDB.rehabilitarTicket(selectedTicket);
                Toast.makeText(this, "Ticket rehabilitado", Toast.LENGTH_LONG).show();
            } else {
                if (this.selectedTicket.getEstado() == 1) {
                    connectionDB.deshabilitarTicket(selectedTicket);
                    Toast.makeText(this, "Ticket deshabilitado", Toast.LENGTH_LONG).show();
                }
            }
            updateTable();
            clear();
        } catch (SQLiteException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateTable () {
        try {
            ticketsArrayList = (ArrayList<TicketsClass>) connectionDB.listAllEmployee(idUser);
            ticketsAdapter = new TicketsAdapter(ticketsArrayList, this, this);
            ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ticketRecyclerView.setAdapter(ticketsAdapter);

            ticketsArrayListResolved = (ArrayList<TicketsClass>) connectionDB.listResolvedEmployee(actualUser);
            ticketsAdapter = new TicketsAdapter(ticketsArrayListResolved, this, this);
            ticketRecyclerViewResolved.setLayoutManager(new LinearLayoutManager(this));
            ticketRecyclerViewResolved.setAdapter(ticketsAdapter);
        } catch (SQLiteException e) {
            Logger.getLogger(TicketsClass.class.getName()).log(Level.SEVERE, null, e);

        }

    }

    private void confirmTicketResolution(TicketsClass selectedTicket) throws NotFoundException, SQLException {
        int confirm = 0;
        clear();
        if ("resolved".equals(selectedTicket.getStatus())) {
            selectedTicket.setStatus("finalized");
            confirm = connectionDB.updateConfirm(selectedTicket, actualUser);
            if (confirm > 0) {
                Toast.makeText(this, "Ticket finalizado.", Toast.LENGTH_LONG).show();
                updateTable();
            } else Toast.makeText(this, "Error al finalizar ticket.", Toast.LENGTH_LONG).show();
        }
    }

    private void rejectTicketResolution(TicketsClass selectedTicket) throws NotFoundException, SQLException {
        int rejected = 0;
        clear();
        if ("resolved".equals(selectedTicket.getStatus())) {
            selectedTicket.setStatus("reasigned");////reopened
            rejected = connectionDB.updateReject(selectedTicket, actualUser);
            if (rejected > 0) {
                Toast.makeText(this, "Ticket reabierto para reasignar.", Toast.LENGTH_LONG).show();
                updateTable();
                UserClass technician = connectionDB.getUserById(Integer.parseInt(selectedTicket.getIdTechnic()));
                if (technician != null) {
                    //technician.incrementFailureCount();
                    //boolean blocked = technician.incrementFailureCount();
                    technician.setFailures(technician.getFailures()+1);
                    connectionDB.updateFailures(technician);
                    updateTable();
                    //failures++;
                    if (technician.getFailures() >= 3) {
                        technician.setEstado(0);
                        connectionDB.deshabilitarUserClass(technician.getIdUser());
                        Toast.makeText(this, "El técnico ha sido bloqueado debido a 3 fallas.", Toast.LENGTH_LONG).show();
                    }
                   // if (failures < 3) {
                   //     technician.setFailures(failures);
                   //     connectionDB.updateFailures(technician);
                   //     Toast.makeText(this, "Error al reabrir ticket para reasignar.", Toast.LENGTH_LONG).show();
                    }
                }

        }
                 else {
                    Toast.makeText(this, "Error al reabrir ticket para reasignar.", Toast.LENGTH_LONG).show();
                }
            }

   private void clear () {
        ticketTitleMain.getText().clear();
        ticketDescriptionMain.getText().clear();
    }

   /* @Override
    public void onTicketClick(TicketsClass ticketsClass) {
      this.ticketsClass=ticketsClass;
    }*/

    @Override
    public void onTicketClick(TicketsClass selectedTicket) {
        this.selectedTicket=selectedTicket;
        showTicketDetails(selectedTicket);
    }

    public void showTicketDetails(TicketsClass selectedTicket) {//necesita este parámetro? no es ticketclass?
        ticketTitleMain.setText(selectedTicket.getTitle());
        ticketDescriptionMain.setText(selectedTicket.getDescription());
        scrollView.setVisibility(View.VISIBLE);
        resolutionText.setText(selectedTicket.getResolution());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accessDB != null && accessDB.isOpen()) {
            accessDB.close();
        }
    }

    protected String getActualUser () {
        return actualUser;
    }
}

