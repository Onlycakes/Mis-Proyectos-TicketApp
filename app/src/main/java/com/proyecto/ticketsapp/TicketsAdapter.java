package com.proyecto.ticketsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.TicketViewHolder> {
    private String actualUser;
    private List<TicketsClass> ticketList;
    private ArrayList<TicketsClass> ticketsArrayList;
    private View select;/////////
    private Context context;
    private OnTicketClickListener listener;

    public TicketsAdapter(List<TicketsClass> ticketList, Context context, OnTicketClickListener listener) {
        this.ticketList = ticketList;
        this.context = context;
        this.listener = listener;
    }

    public interface OnTicketClickListener {
        void onTicketClick(TicketsClass ticketsClass);
    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_item, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TicketViewHolder holder, int position) {
        TicketsClass ticket = ticketList.get(position);
        holder.ticketIdTextView.setText(String.valueOf(ticket.getIdTicket()));
        holder.ticketTitleTextView.setText(ticket.getTitle());
        holder.ticketDescriptionTextView.setText(ticket.getDescription());
        holder.ticketStatusTextView.setText(String.valueOf(ticket.getStatus()));
        holder.ticketEmployeeTextView.setText(String.valueOf(ticket.getIdEmployee()));
        holder.ticketTechnicianTextView.setText(String.valueOf(ticket.getIdTechnic()));
        holder.ticketResolution.setText(ticket.getResolution());
        holder.ticketEstadoTextView.setText(String.valueOf(ticket.getEstado()));
        holder.newTechnicianIdTextView.setText(String.valueOf(ticket.getNewAssignmentTechnic()));

        holder.itemView.setOnClickListener(v -> listener.onTicketClick(ticket));

        holder.seeButton.setOnClickListener(v -> listener.onTicketClick(ticket));
        /*holder.seeButton.setOnClickListener(v -> {
            if (listener instanceof TicketsActivity) {
                ((TicketsActivity) listener).showTicketDetails(ticket);
            }
        });
        holder.seeButton.setOnClickListener(v -> {
            if (listener instanceof ReadTicketsEmployeeActivity) {
                ((ReadTicketsEmployeeActivity) listener).showTicketDetails(ticket);
            }
        });*/
        /*select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    int position = arrayList.getAdapterPosition();//
                if (position != RecyclerView.NO_POSITION) {
                    TicketsClass selectedTicket = ticketsArrayList.get(position);
                }
            }
        } );*/
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView ticketIdTextView;
        TextView ticketTitleTextView;
        TextView ticketDescriptionTextView;
        TextView ticketEmployeeTextView;
        TextView ticketTechnicianTextView;
        TextView newTechnicianIdTextView;
        TextView ticketStatusTextView;
        TextView ticketResolution;
        TextView ticketEstadoTextView;
        Button seeButton;

        public TicketViewHolder(View itemView) {
            super(itemView);
            ticketIdTextView = itemView.findViewById(R.id.ticketIdView);
            ticketTitleTextView = itemView.findViewById(R.id.ticketTitleView);
            ticketDescriptionTextView = itemView.findViewById(R.id.ticketDescriptionView);
            ticketEmployeeTextView = itemView.findViewById(R.id.ticketEmployeeView);
            ticketTechnicianTextView = itemView.findViewById(R.id.ticketTechnicianView);
            newTechnicianIdTextView = itemView.findViewById(R.id.newTechnicianIdView);
            ticketStatusTextView = itemView.findViewById(R.id.ticketStatusView);
            ticketResolution = itemView.findViewById(R.id.ticketResolutionView);
            ticketEstadoTextView = itemView.findViewById(R.id.ticketEstadoView);
            seeButton = itemView.findViewById(R.id.seeButton);
        }
    }

    /*public void updateData(List<TicketsClass> newTicketsList) {
        this.ticketList.clear();
        this.ticketList.addAll(newTicketsList);
        notifyDataSetChanged();
    }*/

    protected void setActual (String actualUser) {
        this.actualUser=actualUser;
    }
}

