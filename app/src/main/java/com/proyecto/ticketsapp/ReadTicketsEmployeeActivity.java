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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import DAO.TicketsClassImplementsDAO;
import Exceptions.NotFoundException;

public class ReadTicketsEmployeeActivity extends AppCompatActivity implements TicketsAdapter.OnTicketClickListener {
    private EditText ticketResolution;
    private Button attended, resolved, reasign, edit, returnTicket;
    private String ticketResolutionStr;
    private TicketsClass ticketsClass;
    private TicketsClass selectedTicket;
    private ArrayList<TicketsClass> ticketsArrayList;
    private ArrayList<TicketsClass> attendedTicketsArrayList;
    private String actualUser;
    private TicketsAdapter ticketsAdapter;
    private RecyclerView ticketsView, attendedTicketsView;
    private ScrollView title, description;
    private TextView titleTextView, descriptionTextView;
    ConnectionDB connectionDB;
    private SQLiteDatabase accessDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read_tickets_emplyee);
        connectionDB = new ConnectionDB(this);
        accessDB = connectionDB.getReadableDatabase();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        actualUser = intent.getStringExtra("actualUser");
        ticketResolution = findViewById(R.id.editTextMLResolution);
        ticketsView = findViewById(R.id.ticketsView);
        attended = findViewById(R.id.attendedTicketButton);
        attendedTicketsView = findViewById(R.id.attendedTicketsView);
        title = findViewById(R.id.titleScrollView);
        description = findViewById(R.id.descriptionScrollView);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        resolved = findViewById(R.id.resolvedTicketButton);
        reasign = findViewById(R.id.reopenTicketButton);
        edit = findViewById(R.id.editResolutionTicket);
        returnTicket = findViewById(R.id.returnResolved);

        try {
            ticketsArrayList = (ArrayList<TicketsClass>) connectionDB.listAllTechnician(actualUser);
            ticketsAdapter = new TicketsAdapter(ticketsArrayList, this, this);
            ticketsView.setLayoutManager(new LinearLayoutManager(this));
            ticketsView.setAdapter(ticketsAdapter);

            attendedTicketsArrayList = (ArrayList<TicketsClass>) connectionDB.listAllAttendedTechnician(actualUser);
            ticketsAdapter = new TicketsAdapter(attendedTicketsArrayList, this, this);
            attendedTicketsView.setLayoutManager(new LinearLayoutManager(this));
            attendedTicketsView.setAdapter(ticketsAdapter);

        } catch (SQLiteException e) {
            Logger.getLogger(TicketsClass.class.getName()).log(Level.SEVERE, null, e);

        }

        attended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attendTicket(view);
            }
        });

        resolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolveTicket(view);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setUpdate(view);
        }
    });
        returnTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnResolvedTicket(view);
            }
        });

        reasign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    reasignTicket(view);
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

}

    public void attendTicket (View view) {
        try{
            boolean b= countTicket(view);
        if (selectedTicket!=null && b==true ) {

           // selectedTicket.setStatus("attended");
            int attended = connectionDB.updateAttendedTicket(actualUser, selectedTicket.getIdTicket(), selectedTicket, "attended");
            if (attended > 0) {
                Toast.makeText(this, "Ticket atendido", Toast.LENGTH_LONG).show();
                updateTable();

            }
            if (attended == 0) {
                Toast.makeText(this, "Error: el ticket no se pudo actualizar.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Ticket atendido con éxito.", Toast.LENGTH_LONG).show();
            }
            if ("reopened".equals(selectedTicket.getStatus()) || "reasigned".equals(selectedTicket.getStatus())) {
                //selectedTicket.setNewAssignmentTechnic(actualUser);
                selectedTicket.setStatus("attended");
                int updateNewT=connectionDB.updateNewTechnician(selectedTicket, actualUser);
                if (updateNewT > 0) {
                    Toast.makeText(this, "Nuevo técnico cargado", Toast.LENGTH_LONG).show();
                    updateTable();

                }
            }
            //}
        }
    } catch (Exception e) {
        Toast.makeText(this, "Error al atender el ticket.", Toast.LENGTH_LONG).show();
        throw new RuntimeException(e);
    }}

    public void resolveTicket (View view) {
        if (selectedTicket!=null) {

        try {
        ticketResolutionStr = ticketResolution.getText().toString();

        if (ticketResolutionStr == null || ticketResolutionStr.isEmpty()) {
            Toast.makeText(this, "Error. Resolucion vacía", Toast.LENGTH_LONG).show();
            return;
        }
            //if (ticketsClass!=null) {

      //          if(selectedTicket.getResolution()!=null) {
                    //selectedTicket.setResolution(ticketResolutionStr);
            if (!"attended".equals(selectedTicket.getStatus())) {
                Toast.makeText(this, "Error. El ticket no está en estado 'attended'.", Toast.LENGTH_LONG).show();
                return;
            }
            if (selectedTicket.getResolution()!=null && !selectedTicket.getResolution().isEmpty()) {
                Toast.makeText(this, "Error. Ticket ya resuelto. Editar resolucion", Toast.LENGTH_LONG).show();
                return;
            }

            int resolved = connectionDB.updateResolution(selectedTicket, selectedTicket.getIdTicket(), actualUser, ticketResolutionStr);
                    if (resolved >0) {
                        Toast.makeText(this, "Resolucion guardada", Toast.LENGTH_LONG).show();
                        updateTable();
                        //updateTableattended();
                        clear();
                    }
                    if (resolved == -1) {
                        Toast.makeText(this, "Error al guardar resolución", Toast.LENGTH_LONG).show();
                    }

        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            throw new RuntimeException(e);
        }}}

    public void setUpdate (View view) {

        if (selectedTicket != null) {

            try {
                 if (selectedTicket.getResolution()==null || selectedTicket.getResolution().isEmpty()) {
                 Toast.makeText(this, "Error. No se encontró ticket resuelto.", Toast.LENGTH_LONG).show();
                 return;
            }
                ticketResolutionStr = ticketResolution.getText().toString();

                if (ticketResolutionStr == null || ticketResolutionStr.isEmpty()) {
                    Toast.makeText(this, "Error. Resolucion vacía", Toast.LENGTH_LONG).show();
                    return;
                }
                    this.selectedTicket.setResolution(ticketResolutionStr);
                    int updatedTicket = connectionDB.updateResolved(selectedTicket, selectedTicket.getIdTicket(), actualUser);
                    if (updatedTicket !=-1) {
                        Toast.makeText(this, "Resolución actualizada", Toast.LENGTH_LONG).show();
                        updateTable();
                    }
                    if (updatedTicket == -1) {
                        Toast.makeText(this, "Error al actualizar resolución", Toast.LENGTH_LONG).show();
                    }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

            private void setCampos (TicketsClass t) {
        ticketResolution.setText(t.getResolution());
    }

    public void returnResolvedTicket (View view)
    {
        UserClass newTechnician = null;
        int returned = 0;
        if (selectedTicket!= null) {
            try {
                if ("attended".equals(selectedTicket.getStatus()) && (!selectedTicket.getResolution().isEmpty()||
                        !(selectedTicket.getResolution() ==null))) {
                    selectedTicket.setStatus("resolved"); ////
                    returned = connectionDB.returnResolved(selectedTicket, actualUser);
                }
                    if (returned > 0) {
                        Toast.makeText(this, "Ticket resuelto ha sido devuelto", Toast.LENGTH_LONG).show();
                        updateTable();
                        //updateTableattended();

                    if (selectedTicket.getNewAssignmentTechnic() != null) {
                        int newTn = (Integer.parseInt(selectedTicket.getNewAssignmentTechnic()));
                        UserClass idUserclass = connectionDB.getUserById(newTn);
                        int id = idUserclass.getIdUser();
                        if (newTn == id) {
                            newTechnician = connectionDB.getUserById(newTn);
                        }

                        if (newTechnician.getFailures() > 0) {
                            //boolean blocked= newTechnician.decrementFailuresCount();
                            newTechnician.setFailures(newTechnician.getFailures()-1);
                            connectionDB.updateFailures(newTechnician);
                            Toast.makeText(this, "El técnico ahora tiene " + newTechnician.getFailures() + "fallas", Toast.LENGTH_LONG).show();
                        }


                    }
                    }
             else {
                Toast.makeText(this, "Error al devolver el ticket", Toast.LENGTH_LONG).show();
            }
                }catch(Exception e){
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }

            }

        }

    public void reasignTicket (View view) throws NotFoundException {
        int reasign = 0;
        if (selectedTicket!=null) {
            if ("attended".equals(selectedTicket.getStatus()) && (selectedTicket.getResolution() == null ||
                    selectedTicket.getResolution().isEmpty())) {

                try {
                    selectedTicket.setStatus("reopened");
                    reasign=connectionDB.updateReasign(selectedTicket, actualUser);
                    Toast.makeText(this, "Ticket ha sido devuelto para reabrir", Toast.LENGTH_LONG).show();
                    updateTable();
                } catch (Exception e) {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                    throw new RuntimeException(e);
                }
            }
            else {
                Toast.makeText(this, "Error al reabrir el ticket", Toast.LENGTH_LONG).show();
            }

            if (reasign>0) {
            UserClass technician = connectionDB.getUserById(Integer.parseInt(selectedTicket.getIdTechnic()));
            if (technician != null) {
                if (technician.getMarks() == 1) {
                    technician.setMarks(technician.getMarks()-1);
                    technician.setFailures(technician.getFailures()+1);
                    connectionDB.updateMarks(technician);
                    connectionDB.updateFailures(technician);
                }
                if (technician.getMarks() == 0) {
                    technician.setMarks(technician.getMarks()+1);
                    connectionDB.updateMarks(technician);
                }

                if (technician.getFailures() > 3) {
                    Toast.makeText(this, "El técnico ha sido bloqueado debido a 3 fallas.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "El técnico tiene " + technician.getMarks() + " marcas", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    }
    private void updateTable () {
        /* try {
            attendedTicketsArrayList= (ArrayList<TicketsClass>) connectionDB.listAllAttendedTechnician(actualUser);
            ticketsAdapter = new TicketsAdapter(attendedTicketsArrayList, this, this);
            attendedTicketsView.setLayoutManager(new LinearLayoutManager(this));
            attendedTicketsView.setAdapter(ticketsAdapter);
        } catch (SQLiteException e) {
            Logger.getLogger(TicketsClass.class.getName()).log(Level.SEVERE, null, e);

        } */
    /*    try {
            // Solo actualiza la lista y notifica al adaptador
            attendedTicketsArrayList.clear();  // Limpia la lista existente
            attendedTicketsArrayList.addAll((ArrayList<TicketsClass>) connectionDB.listAllAttendedTechnician(actualUser)); // Añade los nuevos tickets
            ticketsAdapter.notifyDataSetChanged();  // Notifica al adaptador que los datos han cambiado
        } catch (SQLiteException e) {
            Logger.getLogger(TicketsClass.class.getName()).log(Level.SEVERE, null, e);
        } */
       /* try {
            ArrayList<TicketsClass> updatedList = (ArrayList<TicketsClass>) connectionDB.listAllAttendedTechnician(actualUser);
            //attendedTicketsArrayList.clear();  // Limpia la lista existente
            //attendedTicketsArrayList.addAll(updatedList); // Añade los nuevos tickets
            ticketsAdapter = new TicketsAdapter(updatedList, this, this);
            ticketsView.setLayoutManager(new LinearLayoutManager(this));
            ticketsView.setAdapter(ticketsAdapter);
           // ticketsAdapter.notifyDataSetChanged();  // Notifica al adaptador que los datos han cambiado
        } catch (SQLiteException e) {
            Logger.getLogger(TicketsClass.class.getName()).log(Level.SEVERE, null, e);
        }*/
        ticketsArrayList = (ArrayList<TicketsClass>) connectionDB.listAllTechnician(actualUser);
        ticketsAdapter = new TicketsAdapter(ticketsArrayList, this, this);
        ticketsView.setLayoutManager(new LinearLayoutManager(this));
        ticketsView.setAdapter(ticketsAdapter);

        attendedTicketsArrayList = (ArrayList<TicketsClass>) connectionDB.listAllAttendedTechnician(actualUser);
        ticketsAdapter = new TicketsAdapter(attendedTicketsArrayList, this, this);
        attendedTicketsView.setLayoutManager(new LinearLayoutManager(this));
        attendedTicketsView.setAdapter(ticketsAdapter);
    }

    /*private void updateTableattended () {

        try {
            ArrayList<TicketsClass> updatedList = (ArrayList<TicketsClass>) connectionDB.listAllTechnician(actualUser);

            ticketsAdapter = new TicketsAdapter(updatedList, this, this);
            attendedTicketsView.setLayoutManager(new LinearLayoutManager(this));
            attendedTicketsView.setAdapter(ticketsAdapter);

        } catch (SQLiteException e) {
            Logger.getLogger(TicketsClass.class.getName()).log(Level.SEVERE, null, e);
        }
    }*/



    public boolean countTicket(View view) {
        int attendedTicketsCount = connectionDB.countTicketsByTechnician(actualUser);
        if (attendedTicketsCount >= 3) {
            Toast.makeText(this, "El técnico ya está atendiendo el máximo de 3 tickets.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void showTicketDetails(TicketsClass selectedTicket) {
        titleTextView.setText(selectedTicket.getTitle());
        title.setVisibility(View.VISIBLE);
        descriptionTextView.setText(selectedTicket.getDescription());
        description.setVisibility(View.VISIBLE);
        ticketResolution.setText(selectedTicket.getResolution());
    }


    private void clear () {
        ticketResolution.getText().clear();
              // title, description
    }

    /*@Override
    public void onTicketClick(TicketsClass ticketsClass) {
        this.ticketsClass=ticketsClass;
    }*/

    @Override
    public void onTicketClick(TicketsClass selectedTicket) {
        this.selectedTicket = selectedTicket;
        showTicketDetails(selectedTicket);
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