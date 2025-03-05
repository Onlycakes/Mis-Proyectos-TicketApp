package com.proyecto.ticketsapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<UserClass> userList;
    private Context context;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(UserClass userClass);

    }

    public UserAdapter(List<UserClass> userList, Context context, OnUserClickListener listener) {
        this.userList = userList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserClass user = userList.get(position);
        holder.idUserTextView.setText(String.valueOf(user.getIdUser()));
        holder.userTextView.setText(user.getUserUser());
        holder.passwordTextView.setText(user.getPasswordUser());
        holder.fullNameTextView.setText(user.getFullname());
        holder.emailTextView.setText(user.getEmail());
        holder.cellPhoneTextView.setText(String.valueOf(user.getCellPhone()));///////
        holder.roleTextView.setText(user.getRol());
        holder.marksTextView.setText(String.valueOf(user.getMarks()));
        holder.failuresTextView.setText(String.valueOf(user.getFailures()));
        holder.estadoTextView.setText(String.valueOf(user.getEstado()));

        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView idUserTextView, userTextView, passwordTextView, fullNameTextView,
        emailTextView, cellPhoneTextView, roleTextView, marksTextView, failuresTextView, estadoTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            idUserTextView = itemView.findViewById(R.id.id_user_text_view);
            userTextView = itemView.findViewById(R.id.user_text_view);
            passwordTextView = itemView.findViewById(R.id.password_text_view);
            fullNameTextView = itemView.findViewById(R.id.full_name_text_view);
            emailTextView = itemView.findViewById(R.id.email_text_view);
            cellPhoneTextView = itemView.findViewById(R.id.phone_text_view);
            roleTextView = itemView.findViewById(R.id.role_text_view);
            marksTextView = itemView.findViewById(R.id.marks_text_view);
            failuresTextView = itemView.findViewById(R.id.failures_text_view);
            estadoTextView = itemView.findViewById(R.id.estado_text_view);
        }
    }

    public void updateData(List<UserClass> newUserList) {
        this.userList.clear();
        this.userList.addAll(newUserList);
        notifyDataSetChanged();
    }
}