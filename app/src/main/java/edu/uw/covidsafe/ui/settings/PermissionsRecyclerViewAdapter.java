package edu.uw.covidsafe.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.gps.GpsUtils;
import edu.uw.covidsafe.preferences.AppPreferencesHelper;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class PermissionsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> desc = new ArrayList<>();
    private ArrayList<Drawable> icons = new ArrayList<>();
    Context cxt;
    Activity av;
    View view;

    public PermissionsRecyclerViewAdapter(Context cxt, Activity av, View view) {
        this.cxt = cxt;
        this.av = av;
        this.view = view;
        titles.add(cxt.getString(R.string.perm1));
        titles.add(cxt.getString(R.string.perm2));
        titles.add(cxt.getString(R.string.perm3));
        desc.add(cxt.getString(R.string.perm1desc));
        desc.add(cxt.getString(R.string.perm2desc));
        desc.add(cxt.getString(R.string.perm3desc));
        icons.add(cxt.getDrawable(R.drawable.perm1));
        icons.add(cxt.getDrawable(R.drawable.perm2));
        icons.add(cxt.getDrawable(R.drawable.perm3));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_permission, parent, false);
        return new PermissionCard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((PermissionCard)holder).title.setText(titles.get(position));
        ((PermissionCard)holder).desc.setText(desc.get(position));
        ((PermissionCard)holder).icon.setImageDrawable(icons.get(position));

        SharedPreferences prefs = cxt.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = cxt.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();

        /////////////////////////////////////////////
        // update switch states
        /////////////////////////////////////////////
        if (titles.get(position).toLowerCase().contains(cxt.getString(R.string.perm1).toLowerCase())) {
            Constants.notifSwitch = ((PermissionCard)holder).sw;
            boolean hasNotifPerms = NotificationManagerCompat.from(cxt).areNotificationsEnabled();
            boolean notifEnabled =  AppPreferencesHelper.areNotificationsEnabled(av, Constants.NOTIFS_ENABLED);
            Log.e("perm","notif get "+hasNotifPerms+","+notifEnabled);
            if (hasNotifPerms && notifEnabled) {
                ((PermissionCard) holder).sw.setChecked(true);
            }
            else {
                ((PermissionCard) holder).sw.setChecked(false);
            }
        } else if (titles.get(position).toLowerCase().contains(cxt.getString(R.string.perm2).toLowerCase())) {
            Constants.gpsSwitch = ((PermissionCard)holder).sw;
            boolean hasGpsPerms = Utils.hasGpsPermissions(cxt);
            boolean gpsEnabled = AppPreferencesHelper.isGPSEnabled(cxt, Constants.GPS_ENABLED);
            Log.e("perm","gps get "+hasGpsPerms+","+gpsEnabled);
            if (hasGpsPerms && gpsEnabled) {
//                ((PermissionCard)holder).sw.setOnCheckedChangeListener (null);
                ((PermissionCard)holder).sw.setChecked (true);
//                ((PermissionCard)holder).sw.setOnCheckedChangeListener (PermUtil.listener);
            }
            else {
//                ((PermissionCard)holder).sw.setOnCheckedChangeListener (null);
                ((PermissionCard)holder).sw.setChecked (false);
//                ((PermissionCard)holder).sw.setOnCheckedChangeListener (PermUtil.listener);
            }
        } else if (titles.get(position).toLowerCase().contains(cxt.getString(R.string.perm3).toLowerCase())) {
            Constants.bleSwitch = ((PermissionCard)holder).sw;
            Constants.bleDesc = ((PermissionCard)holder).desc;

            if (!BluetoothUtils.checkBluetoothSupport(av)) {
                ((PermissionCard)holder).sw.setEnabled(false);
                ((PermissionCard)holder).desc.setText(R.string.bluetooth_is_disabled_on_this_device);
                AppPreferencesHelper.setBluetoothEnabled(av, false);
            }
            else if (!BluetoothUtils.isBluetoothOn()) {
                ((PermissionCard)holder).desc.setText(cxt.getString(R.string.bluetooth_is_off));
                AppPreferencesHelper.setBluetoothEnabled(av, false);
            }
            else {
                ((PermissionCard)holder).sw.setEnabled(true);
                boolean hasBlePerms = Utils.hasBlePermissions(cxt);
                boolean bleEnabled = AppPreferencesHelper.isBluetoothEnabled(cxt);
                Log.e("perm", "ble get " + hasBlePerms + "," + bleEnabled);
                if (hasBlePerms && bleEnabled) {
//                ((PermissionCard)holder).sw.setOnCheckedChangeListener (null);
                    ((PermissionCard) holder).sw.setChecked(true);
//                ((PermissionCard)holder).sw.setOnCheckedChangeListener (PermUtil.listener);
                } else {
//                ((PermissionCard)holder).sw.setOnCheckedChangeListener (null);
                    ((PermissionCard) holder).sw.setChecked(false);
//                ((PermissionCard)holder).sw.setOnCheckedChangeListener (PermUtil.listener);
                }
            }
        }

        /////////////////////////////////////////////
        // switch listener
        /////////////////////////////////////////////
        ((PermissionCard)holder).sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = cxt.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
                if (titles.get(position).toLowerCase().contains(cxt.getString(R.string.perm1).toLowerCase())) {
                    boolean hasNotifPerms = NotificationManagerCompat.from(cxt).areNotificationsEnabled();
                    if (hasNotifPerms) {
                        AppPreferencesHelper.setNotificationEnabled(cxt, isChecked);
                        Log.e("perm", "notif set " + isChecked);
                    }
                    else {
                        if (isChecked) {
                            // need to open settings for this
                            // preemptively add the permission
                            AppPreferencesHelper.setNotificationEnabled(cxt, isChecked);
                            Log.e("perm", "notif set " + isChecked);
                            makeOpenSettingsDialog(av,cxt);
                        }
                    }
                }
                else if (titles.get(position).toLowerCase().contains(cxt.getString(R.string.perm2).toLowerCase())) {
                    Log.e("perm","gps set "+isChecked);
                    if (isChecked) {
                        PermUtils.gpsSwitchLogic(av);
                    }
                    else {
                        AppPreferencesHelper.setGPSEnabled(av, false);
                        GpsUtils.haltGps();
                        // ble and gps are turned off, halt
                        if (!Constants.bleSwitch.isChecked()) {
                            Utils.haltLoggingService(av, view);
                        }
                    }
                }
                else if (titles.get(position).toLowerCase().contains(cxt.getString(R.string.perm3).toLowerCase())) {
                    Log.e("perm","ble set "+isChecked);
                    if (isChecked) {
                        PermUtils.bleSwitchLogic(av);
                    }
                    else {
                        AppPreferencesHelper.setBluetoothEnabled(av, Constants.BLUETOOTH_ENABLED);
                        BluetoothUtils.haltBle(av);
                        // ble and gps are turned off, halt
                        if (!Constants.gpsSwitch.isChecked()) {
                            Utils.haltLoggingService(av, view);
                        }
                    }
                }
            }});
    }

    public static void makeOpenSettingsDialog(Activity av, Context cxt) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(av)
                .setTitle(av.getString(R.string.perm_denied))
                .setMessage(av.getString(R.string.perm_notifs_ask))
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Constants.notifSwitch != null) {
//                                Constants.notifSwitch.setOnCheckedChangeListener (null);
                            Constants.notifSwitch.setChecked (false);
//                                Constants.notifSwitch.setOnCheckedChangeListener (PermUtil.listener);
                            AppPreferencesHelper.setNotificationEnabled(cxt, false);
                        }
                    }})
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", av.getPackageName(), null);
                        intent.setData(uri);
                        av.startActivity(intent);
                    }
                })
                .setCancelable(false).create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class PermissionCard extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView desc;
        Switch sw;

        PermissionCard(@NonNull View itemView) {
            super(itemView);
            this.icon = itemView.findViewById(R.id.icon);
            this.title = itemView.findViewById(R.id.perm1);
            this.desc = itemView.findViewById(R.id.perm1desc);
            this.sw = itemView.findViewById(R.id.permSwitch);
        }
    }
}

