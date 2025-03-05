package com.proyecto.ticketsapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.IllegalFormatException;

import DAO.UserClassImplementsDAO;
import Exceptions.NotFoundException;

public class MainActivity extends AppCompatActivity {
    private EditText user, password, email;
    private String userSt, passwordSt, emailSt, id;
    private Button loginBT, forgotBT;
    private MainLoginClass login;
    private UserClass loggedUser;//////////
    private String actualUser;
    ConnectionDB connectionDB;
    private SQLiteDatabase accessDB;

    @Override
  protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        connectionDB = new ConnectionDB(this);
        accessDB = connectionDB.getReadableDatabase();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        user = this.findViewById(R.id.userET);
        password = this.findViewById(R.id.passwordET);
        loginBT = this.findViewById(R.id.enterUserBT);
        forgotBT = this.findViewById(R.id.forgotPasswordButton);
        email = this.findViewById(R.id.emailText);

        loginBT.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {

                                    /*         userSt = user.getText().toString().trim();
                                               passwordSt = password.getText().toString().trim();
                                               if (userSt.trim().isEmpty() ||passwordSt.trim().isEmpty()) {
                                                   Toast.makeText(MainActivity.this, "Los campos del login no pueden estar vacios", Toast.LENGTH_LONG).show();
                                                   return;
                                               }
                                           try {
                                               Integer.parseInt(userSt);
                                               Integer.parseInt(passwordSt);

                                           } catch (NumberFormatException e) {
                                               Toast.makeText(MainActivity.this, "El usuario y la contraseña deben ser numéricos.", Toast.LENGTH_LONG).show();
                                               return;
                                           }

                                           Log.d("Login", "Intentando iniciar sesión con: " + userSt + " y " + passwordSt);
                                           try {
                                               login = new MainLoginClass(userSt, passwordSt, MainActivity.this);
                                           } catch (SQLException e) {
                                               throw new RuntimeException(e);
                                           } */
                                           try {
                                               userSt = user.getText().toString().trim();
                                               passwordSt = password.getText().toString().trim();
                                               if (userSt.trim().isEmpty() ||passwordSt.trim().isEmpty()) {
                                                   Toast.makeText(MainActivity.this, "Los campos del login no pueden estar vacios", Toast.LENGTH_LONG).show();
                                                   return;
                                               }
                                               Integer.parseInt(userSt);
                                               Integer.parseInt(passwordSt);
                                               login = new MainLoginClass(userSt, passwordSt, MainActivity.this);

                                              ////////////// loggedUser = login.validLog();

                                               loggedUser = connectionDB.getLoggedUser(userSt, passwordSt);
                                               if (loggedUser!=null && loggedUser.getEstado()==1) {
                                                   Log.d("Login", "Usuario encontrado: " + loggedUser.getUserUser());
                                                   boolean passwordChange;
                                                   passwordChange = login.newPass();

                                                  if (passwordChange == true) {

                                                 ///////////      actualUser = login.getActualUser(); //acá guardo el String
                                                       actualUser = loggedUser.getUserUser();
                                                       int id = loggedUser.getIdUser();
                                                      Toast.makeText(MainActivity.this, "Ingreso exitoso. Ingrese nueva clave numerica", Toast.LENGTH_LONG).show();
                                                      {
                                                          Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
                                                          intent.putExtra("actualUser", actualUser);
                                                          startActivity(intent);
                                                          return;
                                                      }

                                                   }

                                                   else {
                                                       Toast.makeText(MainActivity.this, "Ingreso exitoso.", Toast.LENGTH_LONG).show();
                                                       if (loggedUser.getRol().equalsIgnoreCase("administrador") || loggedUser.getRol().equalsIgnoreCase("Administrador")) {
                                                           actualUser = loggedUser.getUserUser();
                                                           Intent intent = new Intent(MainActivity.this, MenuAdminActivity.class);
                                                           intent.putExtra("actualUser", actualUser); ///////
                                                           startActivity(intent);
                                                       }

                                                       else if (loggedUser.getRol().equalsIgnoreCase("empleado")) {
                                                           actualUser = loggedUser.getUserUser();
                                                           Intent intent = new Intent(MainActivity.this, MenuEmployeeActivity.class);
                                                           intent.putExtra("actualUser", actualUser);
                                                           startActivity(intent);
                                                       }
                                                       else if (loggedUser.getRol().equalsIgnoreCase("tecnico")) {
                                                           actualUser = loggedUser.getUserUser();
                                                           Intent intent = new Intent(MainActivity.this, MenuTechnicActivity.class);
                                                           intent.putExtra("actualUser", actualUser);
                                                           startActivity(intent);
                                                       }
                                                   }
                                               }
          if (loggedUser!=null && loggedUser.getEstado()==0) {
              Toast.makeText(MainActivity.this, "Usuario bloqueado", Toast.LENGTH_LONG).show();
              clear();
          }
         } catch (IllegalFormatException e) {
         Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
         }
         catch (Exception e) {
         Log.e("Login", "Usuario no encontrado: " + e.getMessage());
         Toast.makeText(MainActivity.this, "Usuario y/o contraseña incorrectos. Por favor, intenta nuevamente.", Toast.LENGTH_LONG).show();
                                           }

                  }
                      }
        );

        forgotBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotBT.setText("ingresar email");

                if (forgotBT.getText().toString().equals("ingresar email")) {
                    email.setVisibility(View.VISIBLE);
                    forgotBT.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                try {
                    emailSt = email.getText().toString();
                    id = connectionDB.getUserByEmail(emailSt);
                    connectionDB.updatePassword(Integer.parseInt(id), id);
                    email.setText("Su clave es:" + id);
                } catch (NotFoundException e) {
                    email.setText("Usuario no encontrado");
                    throw new RuntimeException(e);
                }

            }});}}}
            );
        }


    private void clear () {
        user.getText().clear();
        password.getText().clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accessDB != null && accessDB.isOpen()) {
            accessDB.close();
        }
    }
}
