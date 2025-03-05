package com.proyecto.ticketsapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuTechnicActivity extends AppCompatActivity {
private Button ticketReadMain, ticketTechnicMain;
private String actualUser;
private ConnectionDB connectionDB;
private SQLiteDatabase accessDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_technic);
        connectionDB = new ConnectionDB(this);
        accessDB = connectionDB.getReadableDatabase();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        actualUser = intent.getStringExtra("actualUser");
        ticketReadMain = this.findViewById(R.id.readTicketButtonTechnic);
        //ticketTechnicMain=this.findViewById(R.id.ticketTechnicButton);

        ticketReadMain.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //setMenuOption(view);
            Intent intent = new Intent(MenuTechnicActivity.this, ReadTicketsEmployeeActivity.class);/////////////ver actividad. no sería esa
            intent.putExtra("actualUser", actualUser);
            startActivity(intent);
        }
    });

}
public void setMenuOption(View view) {

    if  (view == ticketReadMain) {
        Intent intent = new Intent(this, ReadTicketsEmployeeActivity.class);/////////////ver actividad. no sería esa
        intent.putExtra("actualUser", this.actualUser);
        startActivity(intent);
    }
}

protected String getActualUser () {
    return this.actualUser;
}
}
