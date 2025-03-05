package com.proyecto.ticketsapp;

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
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Exceptions.NotFoundException;

public class UserActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener{
    private EditText fullName, email, cellPhone;
    private Button load, delete, update;
    private RadioButton admin, employee, technician;
    private RadioGroup radioGroup;
    private String fullNameStr, emailStr, cellPhoneStr, select;
    private UserClass userClass;
    private String actualUser;
    private ArrayList<UserClass> userArrayList;
    private UserAdapter userAdapter;
    private RecyclerView userRecyclerView;
    private UserClass selectedUser;
    private int selectedId;
    private SQLiteDatabase accessDB;
    private ConnectionDB connectionDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);
        connectionDB = new ConnectionDB(this);
        accessDB = connectionDB.getReadableDatabase();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        actualUser = intent.getStringExtra("actualUser");
        fullName = this.findViewById(R.id.fullNameUserEditText);
        email = this.findViewById(R.id.emailUserEditText);
        cellPhone = this.findViewById(R.id.cellPhoneUserEditText);
        radioGroup = this.findViewById(R.id.radioGroupUser);
        admin = this.findViewById(R.id.adminRadioButton);
        employee = this.findViewById(R.id.employeeRadioButton);
        technician = this.findViewById(R.id.technicRadioButton);
        load = this.findViewById(R.id.loadUserButton);
        delete = this.findViewById(R.id.deleteUserButton);
        update = this.findViewById(R.id.updateUserButton);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        userRecyclerView.setLayoutManager(layoutManager);

        try {
            userArrayList = new ArrayList<>();
            userAdapter = new UserAdapter(userArrayList, this, this);
            userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            userRecyclerView.setAdapter(userAdapter);

            updateTable();/////////////////

        } catch (SQLiteException e) {
            Log.e("UserActivity", "Error estableciendo la conexión a la base de datos: " + e.getMessage());
            Toast.makeText(this, "Error al abrir la base de datos", Toast.LENGTH_LONG).show();
        }

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUser(view);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setUpdate(view);///////
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rehabUser(view);/////
            }
        });
    }
    public void setUser(View view) {
        try {
            if (!isValidName(fullName)) {
                Toast.makeText(this, "Nombre completo contiene caracteres inválidos", Toast.LENGTH_LONG).show();
                return;
            }
            fullNameStr = this.fullName.getText().toString();
            emailStr = this.email.getText().toString().trim();
            cellPhoneStr = this.cellPhone.getText().toString().trim();
            selectedId = radioGroup.getCheckedRadioButtonId();

            if (this.fullNameStr.isEmpty() || this.emailStr.trim().isEmpty() || this.cellPhoneStr.trim().isEmpty() || selectedId == -1) {
                Toast.makeText(this, "Los campos del usuario no pueden estar vacios", Toast.LENGTH_LONG).show();
                return;
            }
            select = getSelectedRole(radioGroup.findViewById(selectedId));
            userClass = new UserClass(select, fullNameStr, emailStr, cellPhoneStr);

            if (!userClass.validEmail(emailStr)) {
                Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_LONG).show();
                return;
            }

            if (!userClass.validCellPhone(cellPhoneStr)) { ///////////////falta a Long
                Toast.makeText(this, "Número de teléfono no válido", Toast.LENGTH_LONG).show();
                return;
            }

            if (this.userClass.getIdUser() == 0) {
                int size = connectionDB.listAllUsers().size();
                this.userClass.setUserUser(String.valueOf(size + 1));
                String user = this.userClass.getUserUser();
                this.userClass.setPasswordUser(user);
                Long newUser = connectionDB.saveUser(this.userClass);
                if (newUser == 0 || newUser == -1) {
                    Toast.makeText(this, "error al guardar Usuario", Toast.LENGTH_LONG).show();
                }
                if (newUser > 0) {
                    Toast.makeText(this, "Usuario guardado exitosamente", Toast.LENGTH_LONG).show();
                    updateTable();
                }
            }

        } catch (SQLiteException ex) {
            Toast.makeText(UserActivity.this, "Error", Toast.LENGTH_LONG).show();
            Logger.getLogger(UserActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        //idUser.setText(userClass.getIdUser());///////////creo que no van
        //user.setText(userClass.getUserUser());
        //password.setText(userClass.getPasswordUser());

    }

    @Override
    public void onUserClick(UserClass userClass) {
        selectedUser = userClass;
        setCampos(selectedUser);
}

    public void setUpdate (View view) throws NotFoundException {
        if (this.selectedUser != null) {
            try {
                if (!isValidName(fullName)) {
                    Toast.makeText(this, "Nombre completo contiene caracteres inválidos", Toast.LENGTH_LONG).show();
                    return;
                }
                fullNameStr = this.fullName.getText().toString();
                emailStr = this.email.getText().toString().trim();
                cellPhoneStr = this.cellPhone.getText().toString().trim();
                selectedId = radioGroup.getCheckedRadioButtonId();

                if (this.fullNameStr.isEmpty() || this.emailStr.trim().isEmpty() || this.cellPhoneStr.trim().isEmpty() || selectedId == -1) {
                    Toast.makeText(this, "Los campos del usuario no pueden estar vacios", Toast.LENGTH_LONG).show();
                    return;
                }
                select = getSelectedRole(radioGroup.findViewById(selectedId));
                selectedUser.setRol(select);
                selectedUser.setCellPhone(cellPhoneStr);
                selectedUser.setFullname(fullNameStr);
                selectedUser.setEmail(emailStr);

                if (!selectedUser.validEmail(emailStr)) {
                    Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!selectedUser.validCellPhone(cellPhoneStr)) {
                    Toast.makeText(this, "Número de teléfono no válido", Toast.LENGTH_LONG).show();
                    return;
                }

                int id = selectedUser.getIdUser();
                int updatedUser = connectionDB.update(selectedUser, id);
                if (updatedUser == 0 || updatedUser == -1) {
                    Toast.makeText(this, "error al actualizar Usuario", Toast.LENGTH_LONG).show();
                }
                if (updatedUser > 0) {
                    Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_LONG).show();
                    updateTable();
                }

            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void rehabUser(View view) {

        try {
            if (this.selectedUser.getEstado() == 0) {
                connectionDB.habilitarUserClass(selectedUser.getIdUser());
                Toast.makeText(this, "Usuario rehabilitado", Toast.LENGTH_LONG).show();
                if (this.selectedUser.getFailures()>3) {
                 this.selectedUser.resetFailureCount();
                 connectionDB.updateFailures(selectedUser);
                }
            }

                if (this.selectedUser.getEstado() == 1) {
                    connectionDB.deshabilitarUserClass(selectedUser.getIdUser());
                    Toast.makeText(this, "Usuario deshabilitado", Toast.LENGTH_LONG).show();
                }

            updateTable();
            clear();
        } catch (SQLiteException e) {
            throw new RuntimeException(e);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidName(EditText text) {
        /*for (int i = 0; i < text.getText().length(); i++) {
            if (!Character.isLetter(text.getText().charAt(i)))
                return false;
        }
        return true;*/

            for (int i = 0; i < text.getText().length(); i++) {
                char currentChar = text.getText().charAt(i);
                if (!Character.isLetter(currentChar) && currentChar != ' ') {
                    return false;
                }
            }
            // El nombre es válido si pasa todas las comprobaciones.
            return true;
        }

    private String getSelectedRole(RadioButton rb) {
        if (admin.isChecked()) {
            return "administrador";
        } else if (employee.isChecked()) {
            return "empleado";
        } else {
            return "tecnico";
        }
    }

    private void clear() {
        fullName.setText("");
        email.setText("");
        cellPhone.setText("");
        radioGroup.clearCheck();
    }

    private void setCampos (UserClass u) {
        switch (u.getRol()) {
            case "administrador":
                admin.setChecked(true);
                break;
            case "empleado":
                employee.setChecked(true);
                break;
            case "tecnico":
                technician.setChecked(true);
                break;
        }
        fullName.setText(u.getFullname());
        cellPhone.setText(u.getCellPhone());
        email.setText(u.getEmail());
    }

   /* private void updateTable () {
        try {
           // userArrayList= (ArrayList<UserClass>) connectionDB.listAllUsers();
           // userAdapter = new UserAdapter(userArrayList, this, this);
           // userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
           // userRecyclerView.setAdapter(userAdapter);
           // userAdapter.notifyDataSetChanged();
            try {
                userArrayList.clear(); // Limpia la lista actual
                userArrayList.addAll(connectionDB.listAllUsers()); // Añade los nuevos usuarios
                userAdapter.notifyDataSetChanged(); // Notifica al adaptador
            } catch (SQLiteException e) {
                Log.e("UserActivity", "Error al actualizar la tabla: " + e.getMessage());
            }
        } catch (SQLiteException e) {
            Log.e("UserActivity", "Error al actualizar la tabla: " + e.getMessage());
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accessDB != null && accessDB.isOpen()) {
            accessDB.close();
        }
    }

    private void updateTable() {
        try {
            List<UserClass> updatedList = connectionDB.listAllUsers();
            userAdapter.updateData(updatedList);
        } catch (SQLiteException e) {
            Log.e("UserActivity", "Error al actualizar la tabla: " + e.getMessage());
        }
    }
}

