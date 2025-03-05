package com.proyecto.ticketsapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.SQLException;

import Exceptions.NotFoundException;

public class ChangePasswordActivity extends AppCompatActivity {
private String actualUser;
private EditText user, password;
private String userSt, passwordSt;
private Button saveNewPass;
private ConnectionDB connectionDB;
private SQLiteDatabase accessDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        connectionDB = new ConnectionDB(this);
        accessDB = connectionDB.getReadableDatabase();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        actualUser = intent.getStringExtra("actualUser");
        user = findViewById(R.id.userText);
        password = findViewById(R.id.newPasswordText);
        saveNewPass = findViewById(R.id.saveNewPasswordButton);

        saveNewPass.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                changePass(view);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    });
    }

    public void changePass(View view) throws SQLException, NotFoundException {
        UserClass userChangePass;
        try {
            //password.setText("");
            user.setText(actualUser);
            userChangePass = connectionDB.getUserById(Integer.parseInt(actualUser));
            userSt = user.getText().toString().trim();
            passwordSt = password.getText().toString().trim();
            if (userSt == null || userSt.trim().isEmpty() || passwordSt == null || passwordSt.trim().isEmpty()) {
                Toast.makeText(ChangePasswordActivity.this, "Los campos del login no pueden estar vacios", Toast.LENGTH_LONG).show();
                return;
            }
            Integer.parseInt(userSt);
            Integer.parseInt(passwordSt);
            //MainLoginClass login = new MainLoginClass(userSt, passwordSt, ChangePasswordActivity.this);
            int success= connectionDB.updatePassword(userChangePass.getIdUser(), passwordSt);
            if (success>-1) {
                Toast.makeText(ChangePasswordActivity.this, "Actualización exitosa.", Toast.LENGTH_LONG).show();
            }
            //if (success==-1) {
            //    Toast.makeText(ChangePasswordActivity.this, "Error al actualizar clave", Toast.LENGTH_LONG).show();
            //}

            } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        if (userChangePass.getRol().equalsIgnoreCase("administrador") || userChangePass.getRol().equalsIgnoreCase("Administrador")) {
            actualUser = userChangePass.getUserUser();
            Intent intent = new Intent(ChangePasswordActivity.this, MenuAdminActivity.class);
            intent.putExtra("actualUser", actualUser);
            startActivity(intent);
        }

        else if (userChangePass.getRol().equalsIgnoreCase("empleado")) {
            actualUser = userChangePass.getUserUser();
            Intent intent = new Intent(ChangePasswordActivity.this, MenuEmployeeActivity.class);
            intent.putExtra("actualUser", actualUser);
            startActivity(intent);
        }
        else if (userChangePass.getRol().equalsIgnoreCase("tecnico")) {
            actualUser = userChangePass.getUserUser();
            Intent intent = new Intent(ChangePasswordActivity.this, MenuTechnicActivity.class);
            intent.putExtra("actualUser", actualUser);
            startActivity(intent);
        }
    }
    }