package edu.uw.covidsafe.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.covidsafe.R;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import javax.crypto.SecretKey;

import edu.uw.covidsafe.contact_trace.ContactTraceFragment;
import edu.uw.covidsafe.contact_trace.GpsHistoryRecyclerViewAdapter2;
import edu.uw.covidsafe.contact_trace.HumanRecord;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.gps.ImportLocationHistoryFragment;
import edu.uw.covidsafe.preferences.AppPreferencesHelper;
import edu.uw.covidsafe.symptoms.SymptomTrackerFragment;
import edu.uw.covidsafe.symptoms.SymptomsRecord;
import edu.uw.covidsafe.ui.MainFragment;
import edu.uw.covidsafe.ui.contact_log.ContactLogFragment;
import edu.uw.covidsafe.ui.contact_log.LocationFragment;
import edu.uw.covidsafe.ui.contact_log.PeopleFragment;
import edu.uw.covidsafe.ui.faq.FaqFragment;
import edu.uw.covidsafe.ui.health.DiagnosisFragment;
import edu.uw.covidsafe.ui.health.HealthFragment;
import edu.uw.covidsafe.ui.health.TipRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.HistoryRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.NotifRecyclerViewAdapter;
import edu.uw.covidsafe.ui.onboarding.PagerFragment;
import edu.uw.covidsafe.ui.onboarding.PermissionFragment;
import edu.uw.covidsafe.ui.settings.SettingsFragment;

public class Constants {

    public enum MessageType {
        Exposure,NarrowCast
    }
    public static Menu menu;

    public static boolean UI_AUTH = false;
    public static boolean WRITE_TO_DISK = false;
    public static boolean DEBUG = false;
    public static boolean PUBLIC_DEMO = true;
    public static boolean NARROWCAST_ENABLE = true;
    public static boolean USE_LAST_QUERY_TIME = true;
    public static boolean PAYLOAD_CHECK = true;

    public enum BleDatabaseOps {
        Insert,ViewAll,DeleteAll
    }

    public enum HumanDatabaseOps {
        Insert,Delete,DeleteAll
    }

    public enum NotifDatabaseOps {
        Insert,ViewAll,DeleteAll
    }

    public enum SymptomsDatabaseOps {
        Insert,ViewAll,DeleteAll,Delete
    }

    public enum GpsDatabaseOps {
        Insert,ViewAll,DeleteAll,Delete
    }

    public enum UUIDDatabaseOps {
        BatchInsert, Insert,ViewAll,DeleteAll
    }

    public static int MIN_OS = 6;
    public static int MIN_API = 23;
    public static int ContactPageNumber;
    public static String entryPoint = "";
    public static List<SymptomsRecord> symptomRecords;
    public static boolean EnableUUIDGeneration = true;
    public static TipRecyclerViewAdapter MainTipAdapter;
    public static TipRecyclerViewAdapter DiagnosisTipAdapter;
    public static NotifRecyclerViewAdapter NotificationAdapter;
    public static HistoryRecyclerViewAdapter HistoryAdapter;
    public static boolean PullServiceRunning = false;
    public static boolean LoggingServiceRunning = false;
    public static boolean SuppressSwitchStateCheck = false;
    public static int QuarantineLengthInDays = 14;
    public static int MaxPayloadSize = 10000;
    public static int rssiCutoff = -82;
    public static int MaximumGpsPrecision = 4;
    public static int MinimumGpsPrecision = 0;
    public static int SPLASH_DISPLAY_LENGTH = 1000;
    public static String AnalyticsSecret = "4cd15ae0-9294-40ba-a8b5-a8d77b76783b";
    public static int BluetoothScanIntervalInSecondsDebug = 10;
    public static int BluetoothScanIntervalInMinutes = 5;
    public static int BluetoothScanPeriodInSeconds = 10;
    public static int PullFromServerIntervalInMinutes = 60;
    public static int PullFromServerIntervalInMilliseconds = PullFromServerIntervalInMinutes*60*1000;
    public static boolean PullFromServerTaskRunning = false;
    public static int LogPurgerIntervalInDays = 1;
    public static int UUIDGenerationIntervalInMinutes = 15;
    public static int UUIDGenerationIntervalInSecondsDebug = 10;
    public static int UUIDGenerationIntervalInSeconds = UUIDGenerationIntervalInMinutes*60;
    public static int CDCExposureTimeInMinutes = 10;
    public static double CDCExposureTimeInMinutesDebug = 0.5;
    public static int TimestampDeviationInMilliseconds = 10*1000;
    public static int InfectionWindowIntervalDeviationInMilliseconds = 60*1000;
    public static UUID GATT_SERVICE_UUID = UUID.fromString("8cf0282e-d80f-4eb7-a197-e3e0f965848d");
    public static UUID CHARACTERISTIC_UUID = UUID.fromString("d945590b-5b09-4144-ace7-4063f95bd0bb");
    public static UUID BEACON_SERVICE_UUID = UUID.fromString("0000D028-0000-1000-8000-00805F9B34FB");
    public static UUID contactUUID = null;

    public static String GOOGLE_SIGNIN_PAGE = "https://accounts.google.com/signin/v2/identifier?flowName=GlifWebSignIn&flowEntry=ServiceLogin";
    public static String GOOGLE_DOWNLOAD_PAGE = "https://myaccount.google.com/?utm_source=sign_in_no_continue";
    public static String GOOGLE_KML_STRING_FORMAT = "https://www.google.com/maps/timeline/kml?authuser=0&pb=!1m8!1m3!1i%s!2i%s!3i%s!2m3!1i%s!2i%s!3i%s";

    //GPS_TIME_INTERVAL and GPS_LOCATION_INTERVAL used to control frequency of location updates
    //to optimize for power, note that GPS_TIME_INTERVAL is the primary method by which
    // power is conserverd.
    public static final int GPS_TIME_INTERVAL_IN_MINUTES = 10;
    public static final int GPS_TIME_INTERVAL_IN_MILLISECONDS = 1000*60*GPS_TIME_INTERVAL_IN_MINUTES;

    public static final int GPS_TIME_INTERVAL_IN_SECONDS_DEBUG = 1;
    public static final int GPS_TIME_INTERVAL_IN_MILLISECONDS_DEBUG = 1000*GPS_TIME_INTERVAL_IN_SECONDS_DEBUG;

    // our maximum GPS precision corresponds to ~7km
    // our spatial sampling rate should be 2x less than that
    public static final float GPS_LOCATION_INTERVAL_IN_METERS = 3500;
    public static final float GPS_LOCATION_INTERVAL_IN_METERS_DEBUG = 1;

    public static boolean NOTIFS_ENABLED = false;
    public static boolean GPS_ENABLED = false;
    public static boolean BLUETOOTH_ENABLED = false;
    public static boolean LOG_TO_DISK = false;

    public static Switch gpsSwitch;
    public static Switch bleSwitch;
    public static TextView bleDesc;
    public static Switch notifSwitch;

    public static ViewPager contactViewPager;
    public static SecretKey secretKey;
    public static KeyStore keyStore;
    public static int IV_LEN = 12;
    public static int GCM_TLEN = 128;
    public static String CharSet = "UTF-8";
    public static String AES_SETTINGS = "AES/GCM/NoPadding";
    public static String RSA_SETTINGS = "RSA/ECB/PKCS1Padding";
    public static String KEY_PROVIDER = "AndroidKeyStore";
    public static String KEY_ALIAS = "mykeys";
    public static BluetoothGattServer gattServer;
    public static BluetoothAdapter blueAdapter;
    public static int statusSubmitted = -1;
    public static ScheduledFuture uuidGeneartionTask;
    public static ScheduledFuture bluetoothScanTask;
    public static Timer pullFromServerTaskTimer;
    public static ScheduledFuture logPurgerTask;
    public static boolean startingToTrack = false;
    public static String SHARED_PREFENCE_NAME = "preferences";
    public static String NOTIFICATION_CHANNEL = "channel";
    public static Fragment MainFragment;
    public static Fragment HealthFragmentState;
    public static Fragment MainFragmentState;
    public static Fragment HealthFragment;
    public static Fragment SymptomTrackerFragment;
    public static Fragment DiagnosisFragment;
    public static Fragment SettingsFragment;
    public static Fragment FaqFragment;
    public static Fragment LocationFragment;
    public static Fragment PeopleFragment;
    public static Fragment ContactLogFragment;
    public static Fragment CurrentFragment;
    public static Fragment PermissionsFragment;
    public static Fragment PagerFragment;
    public static Fragment ContactTraceFragment;
    public static Fragment ImportLocationHistoryFragment;
    public static String notifDirName = "notif";
    public static String gpsDirName = "gps";
    public static String bleDirName = "ble";
    public static String symptomsDirName = "symptoms";
    public static String uuidDirName = "uuid";
    public static String lastSentName = "lastsent";
    public static boolean tracking = false;
    public static int NumFilesToDisplay = 14;
    public static int SubmitThresh = 1;
    public static int DefaultInfectionWindowInDays = 14;
    public static int DefaultInfectionWindowInDaysDebug = 1;
    public static int DefaultDaysOfLogsToKeep = DefaultInfectionWindowInDays;
    public static int DefaultDaysOfLogsToKeepDebug = DefaultInfectionWindowInDaysDebug;
    public static LocationManager mLocationManager = null;
    public static HashSet<String> scannedUUIDs;
    public static HashMap<String,Integer> scannedUUIDsRSSIs;
    public static HashMap<String,Long> scannedUUIDsTimes;
    public static HashSet<String> writtenUUIDs;
    public static int pageNumber = -1;
    public static ViewPager healthViewPager;
    public static ViewPager contactLogViewPager;
    public static Calendar contactLogMonthCalendar = Calendar.getInstance();
    public static Calendar symptomTrackerMonthCalendar = Calendar.getInstance();
    public static String KML_FILE_NAME = "CovidSafe_Google_Location_History.kml";
    public static int deviceID;

    public static GpsHistoryRecyclerViewAdapter2 contactGpsAdapter;
    public static List<HumanRecord> changedContactHumanRecords;
    public static List<SymptomsRecord> changedContactSympRecords;
    public static List<GpsRecord> changedContactGpsRecords;
    public static MaterialCalendarView contactLogCal;

    public static List<String> symptoms = new LinkedList<>();
    public static List<String> symptomDesc = new LinkedList<>();

    public static String[] gpsPermissions= {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    public static String[] gpsPermissionsLite= {
//            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    public static String[] blePermissions= {
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
    };
    public static String[] miscPermissions= {
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.INTERNET
    };

    public static List<String> languages = new LinkedList<>(Arrays.asList("en","es"));
    public static String defaultLocale = "en";
    public static HashMap<Integer,Integer> bleThresholds = new HashMap<>();
    public static List<String> deviceNames = new LinkedList<>();
    public static List<String> manufacturerNames = new LinkedList<>();

    public static void getDeviceID() {
        String manufacturer = Build.MANUFACTURER.toLowerCase();

        String model = Build.MODEL.toLowerCase();
        model = model.replace("-","");
        model = model.replace(manufacturer,"");

        if (!Constants.manufacturerNames.contains(manufacturer)) {
            Constants.deviceID = 0;
        }
        else {
            int counter = 1;
            for (String deviceName : Constants.deviceNames) {
                // we are a samsung model
                if (model.startsWith("sm")) {
                    // trim off the last character, which is the carrier designator
                    deviceName = deviceName.substring(0, deviceName.length()-1);
                    model = model.substring(0, model.length()-1);

                }
                if (model.equals(deviceName)) {
                    deviceID = counter;
                    break;
                }
                counter++;
            }
        }
    }

    public static void init(Activity av) {
        Log.e("logme","constants init");

        if (symptomDesc.size() == 0) {
            symptomDesc.add(av.getString(R.string.fever_desc));
            symptomDesc.add(av.getString(R.string.ab_pain_desc));
            symptomDesc.add(av.getString(R.string.chills_desc));
            symptomDesc.add(av.getString(R.string.cough_desc));
            symptomDesc.add(av.getString(R.string.diarrhea_desc));
            symptomDesc.add(av.getString(R.string.breathing_desc));
            symptomDesc.add(av.getString(R.string.headache_desc));
            symptomDesc.add(av.getString(R.string.sore_throat_desc));
            symptomDesc.add(av.getString(R.string.vomiting_desc));
        }

        if (symptoms.size() == 0) {
            symptoms.add(av.getString(R.string.fever_txt));
            symptoms.add(av.getString(R.string.abdominal_pain_txt));
            symptoms.add(av.getString(R.string.chills_txt));
            symptoms.add(av.getString(R.string.cough_text));
            symptoms.add(av.getString(R.string.diarrhea_txt));
            symptoms.add(av.getString(R.string.difficult_in_breathing_not_severe));
            symptoms.add(av.getString(R.string.headache_txt));
            symptoms.add(av.getString(R.string.sore_throat_txt));
            symptoms.add(av.getString(R.string.vomiting_txt));
        }

        if (bleThresholds.keySet().size() == 0) {
            bleThresholds = FileOperations.readDeviceThresholds(av, R.raw.device_data);
            deviceNames = FileOperations.readDeviceList(av, R.raw.device_data);
            manufacturerNames = FileOperations.readManufacturerList(av, R.raw.device_data);
        }

        getDeviceID();

        MainFragment = new MainFragment();
        MainFragmentState = MainFragment;
        SettingsFragment = new SettingsFragment();

        ContactLogFragment = new ContactLogFragment();

        DiagnosisFragment = new DiagnosisFragment();
        HealthFragment = new HealthFragment();

        FaqFragment = new FaqFragment();

        PermissionsFragment = new PermissionFragment();
        PagerFragment = new PagerFragment();

        SymptomTrackerFragment = new SymptomTrackerFragment();
        HealthFragmentState = SymptomTrackerFragment;

        ContactTraceFragment = new ContactTraceFragment();
        PeopleFragment = new PeopleFragment();
        LocationFragment = new LocationFragment();
        ImportLocationHistoryFragment = new ImportLocationHistoryFragment();

        if (!DEBUG) {
            LOG_TO_DISK = false;
        }
        else {
            LOG_TO_DISK = true;
        }
        Constants.BLUETOOTH_ENABLED = AppPreferencesHelper.isBluetoothEnabled(av, Constants.BLUETOOTH_ENABLED);
        Constants.GPS_ENABLED = AppPreferencesHelper.isGPSEnabled(av, Constants.GPS_ENABLED);
        Constants.NOTIFS_ENABLED = AppPreferencesHelper.areNotificationsEnabled(av, Constants.NOTIFS_ENABLED);
    }
}
