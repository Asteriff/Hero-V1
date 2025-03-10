package com.example.heromodelapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private List<Reminder> reminderList;

    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle, textViewDescription, textViewTime;

        //importing reminder values from reminder item
        public ReminderViewHolder(View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.text_view_title);
            textViewDescription = view.findViewById(R.id.text_view_description);
            textViewTime = view.findViewById(R.id.text_view_time);
        }
    }

    public ReminderAdapter(List<Reminder> reminderList) {
        this.reminderList = reminderList;
    }


    //inflate the reminder item onto a new view holder in the recycler view
    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_item, parent, false);

        return new ReminderViewHolder(itemView);
    }

    //implement the reminder values into the holder
    @Override
    public void onBindViewHolder(ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.textViewTitle.setText(reminder.getTitle());
        holder.textViewDescription.setText(reminder.getDescription());

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
        String dateString = formatter.format(new Date(reminder.getTimeInMillis()));
        holder.textViewTime.setText(dateString);
    }

    //receive item count for scrolling and layout updates
    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    // add new reminder to the recycler view
    public void addReminder(Reminder reminder) {
        reminderList.add(reminder);
        notifyItemInserted(reminderList.size() - 1);
    }
}
