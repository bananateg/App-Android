package edu.uw.covidsafe.workmanager.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.covidsafe.R;

import java.util.UUID;

import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.preferences.AppPreferencesHelper;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.CryptoUtils;
import edu.uw.covidsafe.utils.Utils;

public class UUIDGeneratorWorker extends Worker {

    private Context context;
    private WorkerParameters workerParameters;
    private Data.Builder resultData;

    public UUIDGeneratorWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.workerParameters = workerParams;
        resultData = new Data.Builder();
    }


    @NonNull
    @Override
    public Result doWork() {
        if (Constants.EnableUUIDGeneration && AppPreferencesHelper.isBluetoothEnabled(context)) {
            Log.e("crypto","generate uuid");
            // get the most recently generated seed
            SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
            long mostRecentSeedTimestamp = prefs.getLong(context.getString(R.string.most_recent_seed_timestamp_pkey), 0);

            Log.e("crypto","most recent seed timestamp "+mostRecentSeedTimestamp);
            UUID uuid = CryptoUtils.generateSeedHelperWithMostRecent(context, mostRecentSeedTimestamp);
            Log.e("ble", "changing contact uuid now");
            if (uuid != null) {
                Log.e("ble", "changing contact uuid now to " + uuid.toString());
                Constants.contactUUID = UUID.fromString(uuid.toString());
            }

            Log.e("ble", "can we broadcast?" + (Constants.blueAdapter != null));
            if (Constants.blueAdapter != null) {
                Log.e("ble", "is advertiser null?" + (Constants.blueAdapter.getBluetoothLeAdvertiser() == null));
            }
            if (Constants.blueAdapter != null && Constants.blueAdapter.getBluetoothLeAdvertiser() != null) {
                Log.e("ble", "about to stop advertising");
                Constants.blueAdapter.getBluetoothLeAdvertiser().stopAdvertising(BluetoothUtils.advertiseCallback);
                //restart beacon after UUID generation
                Log.e("ble", "about to mkbeacon");
                BluetoothUtils.mkBeacon(context);
                return Result.failure();
            }
        }
        return Result.success();
    }
}
