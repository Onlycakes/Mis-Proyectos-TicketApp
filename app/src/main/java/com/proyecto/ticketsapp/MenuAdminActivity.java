package com.proyecto.ticketsapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;

public class MenuAdminActivity extends AppCompatActivity {
private Button userAccessMain, ticketsAccessMain, rehabTechnic, passwords;
private String actualUser;
private ConnectionDB connectionDB;
private SQLiteDatabase accessDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_admin);
        connectionDB = new ConnectionDB(this);
        accessDB = connectionDB.getReadableDatabase();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        actualUser = intent.getStringExtra("actualUser");
        userAccessMain = this.findViewById(R.id.userAccessButton);
        ticketsAccessMain = this.findViewById(R.id.ticketsAccessButton);

        userAccessMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdminActivity.this, UserActivity.class);
                 intent.putExtra("actualUser", actualUser);
                startActivity(intent);
            }
        });
       ticketsAccessMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuAdminActivity.this, AdminTickets.class);
                intent.putExtra("actualUser", actualUser);
                startActivity(intent);
            }
    });

    }
    /*public void setMenuOption(View view) {

            if (view == userAccessMain) {


            } else if (view == ticketsAccessMain) {
                Intent intent = new Intent(this, TicketsActivity.class);
                intent.putExtra("actualUser", this.actualUser);
                startActivity(intent);

            }
    }

    protected String getActualUser () {
        return this.actualUser;
    }*/
}
