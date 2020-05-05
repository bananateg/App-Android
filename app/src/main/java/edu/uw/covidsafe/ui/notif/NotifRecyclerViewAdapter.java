package edu.uw.covidsafe.ui.notif;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import edu.uw.covidsafe.utils.Constants;


public class NotifRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context cxt;
    Activity av;
    List<NotifRecord> records = new ArrayList<>();

    public NotifRecyclerViewAdapter(Context cxt, Activity av) {
        this.cxt = cxt;
        this.av = av;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false);
        return new NotifCard(view);
    }

    int dismissedIndex = -1;
    public void setRecords(List<NotifRecord> records, View view) {
        if (records.size() > this.records.size()) {
            Log.e("notif","notif item inserted");
            notifyItemInserted(0);
//            Utils.notif2(cxt);
        }
        else {
            Log.e("notif","notif remove "+dismissedIndex);
//            if (dismissedIndex != -1) {
//                notifyItemRemoved(dismissedIndex);
//            }
            notifyDataSetChanged();
        }
        this.records = records;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        NotifRecord rec = this.records.get(position);
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm");
        SimpleDateFormat timeFormat2 = new SimpleDateFormat("h:mma");
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d");

        if (rec.msgType == Constants.MessageType.Exposure.ordinal()) {
            ((NotifCard)holder).header.setText(cxt.getResources().getString(R.string.you_might_have_exposed));

            String tt = timeFormat.format(rec.getTs_start()) +"-"+ timeFormat2.format(rec.getTs_end());

//        Spannable ss = (Spannable)Html.fromHtml(rec.msg+" This was during <b>"+dateFormat.format(rec.getTs_start())+ "</b> during <b>"+tt+"</b>");
            Spannable ss = (Spannable)Html.fromHtml(rec.msg+" "+cxt.getString(R.string.this_was_during)+" <b>"+dateFormat.format(rec.getTs_start())+".");
            ((NotifCard)holder).message.setText(ss);
            ((NotifCard)holder).icon.setImageDrawable(av.getDrawable(R.drawable.warning3));
        }
        else {
            ((NotifCard)holder).message.setText(rec.msg);
            ((NotifCard)holder).header.setText(R.string.announcement_txt);
            ((NotifCard)holder).contact.setText("");
            ((NotifCard)holder).contact.setVisibility(View.GONE);
            ((NotifCard)holder).icon.setImageDrawable(av.getDrawable(R.drawable.ic_info_outline_black_24dp));
        }
        ((NotifCard)holder).dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissedIndex = position;
                Log.e("state","remove "+position);
                new NotifOpsAsyncTask(cxt, new NotifRecord(rec.ts_start, rec.ts_end, rec.msg, rec.msgType,false)).execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class NotifCard extends RecyclerView.ViewHolder {
        Button dismissButton;
        TextView header;
        TextView message;
        TextView contact;
        ImageView icon;
        NotifCard(@NonNull View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.imageView10);
            this.dismissButton = itemView.findViewById(R.id.dismiss);
            this.header = itemView.findViewById(R.id.textView11);
            this.message = itemView.findViewById(R.id.textView12);
            this.contact = itemView.findViewById(R.id.textView13);
        }
    }
}
