package edu.uw.covidsafe.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.covidsafe.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.Date;

import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.contact_trace.ContactStepFragment;
import edu.uw.covidsafe.contact_trace.HumanOpsAsyncTask;
import edu.uw.covidsafe.gps.ImportLocationHistoryFragment;
import edu.uw.covidsafe.preferences.AppPreferencesHelper;
import edu.uw.covidsafe.preferences.LocaleHelper;
import edu.uw.covidsafe.symptoms.AddEditSymptomsFragment;
import edu.uw.covidsafe.symptoms.SymptomConfirmFragment;
import edu.uw.covidsafe.symptoms.SymptomTrackerFragment;
import edu.uw.covidsafe.ui.contact_log.ContactLogFragment;
import edu.uw.covidsafe.ui.contact_log.ContactLogPageAdapter;
import edu.uw.covidsafe.ui.contact_log.LocationFragment;
import edu.uw.covidsafe.ui.contact_log.PeopleFragment;
import edu.uw.covidsafe.ui.health.DiagnosisFragment;
import edu.uw.covidsafe.ui.health.TipRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.HistoryRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.NotifRecyclerViewAdapter;
import edu.uw.covidsafe.ui.onboarding.PermissionFragment;
import edu.uw.covidsafe.ui.settings.PermUtils;
import edu.uw.covidsafe.ui.settings.SettingsFragment;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Activity activity;
    Context cxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("state","main activity oncreate");
        this.activity = this;
        this.cxt = this;
        setContentView(R.layout.activity_main);

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        Log.e("metadata","BUILD "+android.os.Build.VERSION.SDK_INT+"");
        Log.e("metadata","RELEASE "+android.os.Build.VERSION.RELEASE+"");
        Log.e("metadata","MANUFACTURER "+manufacturer);
        Log.e("metadata","MODEL "+model);

        AppCenter.start(getApplication(), Constants.AnalyticsSecret, Analytics.class, Crashes.class);
        Crashes.setEnabled(true);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_release);

        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(getString(R.string.onboard_enabled_pkey), false);
        editor.commit();

        Constants.MainTipAdapter = new TipRecyclerViewAdapter(this, this);
        Constants.DiagnosisTipAdapter = new TipRecyclerViewAdapter(this, this);
        Constants.NotificationAdapter = new NotifRecyclerViewAdapter(this,this);
        Constants.HistoryAdapter = new HistoryRecyclerViewAdapter(this,this);

        Utils.minApiCheck(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("demo","on new intent");
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionLogic.permissionLogic(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onBackPressed() {
        if (!menuLogic()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                menuLogic();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean menuLogic() {
        Log.e("menu","menu "+Constants.CurrentFragment.toString());
        if (Constants.CurrentFragment.getClass().toString().contains(SettingsFragment.class.toString())) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.setCustomAnimations(
                    R.anim.enter_left_to_right,R.anim.exit_left_to_right,
                    R.anim.enter_left_to_right,R.anim.exit_left_to_right);
            tx.replace(R.id.fragment_container, Constants.MainFragment).commit();
            return true;
        }
        else if (Constants.CurrentFragment.getClass().toString().contains(DiagnosisFragment.class.toString())) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.setCustomAnimations(
                    R.anim.enter_left_to_right,R.anim.exit_left_to_right,
                    R.anim.enter_left_to_right,R.anim.exit_left_to_right);
            tx.replace(R.id.fragment_container, Constants.HealthFragment).commit();
            return true;
        }
        else if (Constants.CurrentFragment.getClass().toString().contains(AddEditSymptomsFragment.class.toString())) {
            Log.e("menu","back on add symptoms");
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.setCustomAnimations(
                    R.anim.enter_left_to_right,R.anim.exit_left_to_right,
                    R.anim.enter_left_to_right,R.anim.exit_left_to_right);
            if (Constants.entryPoint.equals("main")) {
                tx.replace(R.id.fragment_container, Constants.MainFragment).commit();
            }
            else {
                tx.replace(R.id.fragment_container, Constants.HealthFragment).commit();
            }
            return true;
        }
        else if (Constants.CurrentFragment.getClass().toString().contains(SymptomConfirmFragment.class.toString())) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.setCustomAnimations(
                    R.anim.enter_left_to_right,R.anim.exit_left_to_right,
                    R.anim.enter_left_to_right,R.anim.exit_left_to_right);
            if (Constants.entryPoint.equals("main")) {
                tx.replace(R.id.fragment_container, Constants.MainFragment).commit();
            }
            else {
                tx.replace(R.id.fragment_container, Constants.HealthFragment).commit();
            }
            return true;
        }
        else if (Constants.CurrentFragment.getClass().toString().contains(ContactStepFragment.class.toString())) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.setCustomAnimations(
                    R.anim.enter_left_to_right,R.anim.exit_left_to_right,
                    R.anim.enter_left_to_right,R.anim.exit_left_to_right);
            tx.replace(R.id.fragment_container, Constants.HealthFragment).commit();
            return true;
        }
        else if (Constants.CurrentFragment.getClass().toString().contains(ImportLocationHistoryFragment.class.toString())) {
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.setCustomAnimations(
                    R.anim.enter_left_to_right,R.anim.exit_left_to_right,
                    R.anim.enter_left_to_right,R.anim.exit_left_to_right);
            tx.replace(R.id.fragment_container, Constants.SettingsFragment).commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.e("state","onactivityresult "+requestCode+","+resultCode);
        boolean hasBlePerms = Utils.hasBlePermissions(getApplicationContext());
        if (requestCode == 0) {
            if (resultCode == -1) {
                if (hasBlePerms) {
                    AppPreferencesHelper.setBluetoothEnabled(this);
                    if (!Constants.LoggingServiceRunning) {
                        Utils.startLoggingService(this);
                        Log.e("ble","ble switch logic");
                        PermUtils.transition(false,this);
                    }
                    else {
                        Log.e("ble","ble switch logic2");
                        BluetoothUtils.startBle(this);
                    }
                }
            }
            else if (resultCode == 0) {
                Utils.updateSwitchStates(this);
            }
        }
        else if(requestCode == 2) {
            if (data != null) {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                if (c.moveToFirst()) {
                    String phone = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String photo = c.getString(c.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                    String id = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID));

                    String email = null;
                    Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
                    while (emails.moveToNext()) {
                        email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        break;
                    }
                    emails.close();
                    new HumanOpsAsyncTask(this, phone, name, photo, email).execute();
                }
                c.close();
            }
        }
        else if (requestCode == 3) {
            List<ContactResult> results = MultiContactPicker.obtainResult(data);
            String email = null;
            String uri;
            for (ContactResult contactResult : results) {
                if (!contactResult.getEmails().isEmpty()) {
                    email = contactResult.getEmails().get(0);
                } else {
                    email = null;
                }

                if (contactResult.getPhoto() == null) {
                    uri = null;
                } else {
                    uri = contactResult.getPhoto().toString();
                }

                new HumanOpsAsyncTask(this, contactResult.getPhoneNumbers().get(0).getNumber(), contactResult.getDisplayName(), uri, email).execute();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.e("menu","oncreate menu");
        getMenuInflater().inflate(R.menu.mymenu, menu);
        Constants.menu = menu;
        menu.findItem(R.id.mybutton).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                menu.findItem(R.id.mybutton).setIcon(R.drawable.calendar_highlighted);
                calSetup();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void calSetup() {
        Log.e("date","cal setup");
        Calendar myCalendar = Calendar.getInstance();

        if (Constants.CurrentFragment.getClass().toString().contains(Constants.LocationFragment.getClass().toString())) {
            Log.e("date","contact");
            myCalendar = Constants.contactLogMonthCalendar;
        }
        else if (Constants.CurrentFragment.getClass().toString().contains(Constants.HealthFragment.getClass().toString())) {
            Log.e("date","symptom");
            myCalendar = Constants.symptomTrackerMonthCalendar;
        }

        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                Log.e("date","ondateset");
            }
        };

        int yyyy = myCalendar.get(Calendar.YEAR);
        int mm = myCalendar.get(Calendar.MONTH);
        int dd = myCalendar.get(Calendar.DAY_OF_MONTH);
        Log.e("time","setting month "+mm);
        Log.e("time","setting day "+dd);
        DatePickerDialog dialog = new DatePickerDialog(activity, dateListener,
                yyyy,mm,dd);

        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        int days = prefs.getInt(getString(R.string.infection_window_in_days_pkeys), Constants.DefaultInfectionWindowInDays);

        Calendar finalMyCalendar = myCalendar;

        dialog.getDatePicker().init(yyyy,mm,dd, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i2, int i3) {
                finalMyCalendar.set(i, i2, i3);
            }
        });
        dialog.getDatePicker().setMinDate(TimeUtils.getNDaysForward(-days));
        dialog.getDatePicker().setMaxDate(new Date(TimeUtils.getTime()).getTime());

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                Constants.menu.findItem(R.id.mybutton).setIcon(R.drawable.calendar);
            }
        });

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Constants.menu.findItem(R.id.mybutton).setIcon(R.drawable.calendar);
                int year = finalMyCalendar.get(Calendar.YEAR);
                int month = finalMyCalendar.get(Calendar.MONTH)+1;
                int day = finalMyCalendar.get(Calendar.DAY_OF_MONTH);
                Log.e("date","ok "+year+","+month+","+day);
              
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (fragment instanceof ContactLogFragment && fragment.isVisible()) {
                    TabLayout layout = fragment.getView().findViewById(R.id.tabLayout);
                    if(layout != null){
                        if (layout.getSelectedTabPosition() == 0) {
                            ContactLogPageAdapter adapter = ((ContactLogPageAdapter)Constants.contactLogViewPager.getAdapter());
                            Fragment locationFragment = adapter.getItem(0);
                            if(locationFragment instanceof LocationFragment){
                                ((LocationFragment) locationFragment).updateLocationView(CalendarDay.from(year, month, day), getApplicationContext());
                            }
                        }
                    }
                }
                else if (Constants.CurrentFragment.getClass().toString().contains(Constants.HealthFragment.getClass().toString())) {
                    SymptomTrackerFragment.updateFeaturedDate(CalendarDay.from(year,month,day),
                            getApplicationContext(), activity);
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("state","main activity onresume");

//        Constants.init(this);
        initView();
        initBottomNav();

        if (!Constants.LoggingServiceRunning) {
            boolean gpsEnabled = AppPreferencesHelper.isGPSEnabled(this);
            boolean bleEnabled = AppPreferencesHelper.isBluetoothEnabled(this);
            if (gpsEnabled) {
                PermUtils.gpsSwitchLogic(this);
            }
            if (bleEnabled) {
                PermUtils.bleSwitchLogic(this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("logme","activity destroyed");
        try {
            this.unregisterReceiver(BluetoothUtils.bluetoothReceiver);
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }
    }

    public void initView() {
        if (Constants.CurrentFragment != null &&
            !Constants.CurrentFragment.getClass().toString().contains(PermissionFragment.class.toString()) &&
                !Constants.CurrentFragment.getClass().toString().contains(ContactStepFragment.class.toString())&&
                !Constants.CurrentFragment.getClass().toString().contains(PeopleFragment.class.toString())&&
                !Constants.CurrentFragment.getClass().toString().contains(LocationFragment.class.toString())) {
             getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.CurrentFragment).commit();
        } else if (Constants.CurrentFragment != null && Constants.CurrentFragment.getClass().toString().contains(ContactStepFragment.class.toString())) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.ContactTraceFragment).commit();
        }else if (Constants.CurrentFragment != null && Constants.CurrentFragment.getClass().toString().contains(PeopleFragment.class.toString())) {
            Bundle data = new Bundle();
            data.putInt("pg", 1);
            if (Constants.ContactLogFragment == null) {
                Constants.ContactLogFragment = new ContactLogFragment();
            }
            Constants.ContactLogFragment.setArguments(data);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.ContactLogFragment).commit();
        }
        else if (Constants.CurrentFragment != null && Constants.CurrentFragment.getClass().toString().contains(LocationFragment.class.toString())) {
            Bundle data = new Bundle();
            data.putInt("pg", 0);
            if (Constants.ContactLogFragment == null) {
                Constants.ContactLogFragment = new ContactLogFragment();
            }
            Constants.ContactLogFragment.setArguments(data);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.ContactLogFragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.MainFragment).commit();
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.HealthFragment).commit();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    public void initBottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_track:
//                        if (Constants.MainFragmentState.toString().toLowerCase().contains("main")) {
                            selectedFragment = Constants.MainFragment;
//                        }
//                        else if (Constants.MainFragmentState.toString().toLowerCase().contains("settings")) {
//                            selectedFragment = Constants.SettingsFragment;
//                        }
                        break;
                    case R.id.action_contact_log:
                        selectedFragment = Constants.ContactLogFragment;
                        break;
                    case R.id.action_report:
                        selectedFragment = Constants.HealthFragment;
                        if (Constants.CurrentFragment.getClass().toString().contains(ContactStepFragment.class.toString())) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.HealthFragment).commit();
                            return true;
                        }
                        break;
                    case R.id.action_settings:
                        selectedFragment = Constants.FaqFragment;
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            }
        });
    }
}
