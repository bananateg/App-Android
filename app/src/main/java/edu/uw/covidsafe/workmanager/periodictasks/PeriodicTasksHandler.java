package edu.uw.covidsafe.workmanager.periodictasks;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.workmanager.workers.LogPurgerWorker;
import edu.uw.covidsafe.workmanager.workers.PullFromServerWorker;
import edu.uw.covidsafe.workmanager.workers.UUIDGeneratorWorker;

public class PeriodicTasksHandler {

    private static final String PULL_SERVICE_TAG = "pullservice";
    private static final String LOG_PURGER_TAG = "logpurger";
    private static final String UUID_GENERATOR_TAG = "uuidgenerator";
    private Context context;

    public PeriodicTasksHandler(Context context) {
        this.context = context;
    }

    private Map<String, PeriodicWorkRequest> periodicWorkRequests = new HashMap<>();

    public void initAllPeriodicRequests() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicPullServiceWorkRequest =
                new PeriodicWorkRequest.Builder(PullFromServerWorker.class, Constants.PullFromServerIntervalInMilliseconds, TimeUnit.MILLISECONDS)
                        .addTag(PULL_SERVICE_TAG)
                        .setConstraints(constraints)
                        .build();
        PeriodicWorkRequest periodicLogPurgerWorkRequest =
                new PeriodicWorkRequest.Builder(LogPurgerWorker.class, Constants.LogPurgerIntervalInDays, TimeUnit.DAYS)
                        .addTag(LOG_PURGER_TAG)
                        .build();

        PeriodicWorkRequest periodicUUIDGeneratorWorkRequest =
                new PeriodicWorkRequest.Builder(UUIDGeneratorWorker.class, getUUIDInterval(), TimeUnit.SECONDS)
                        .addTag(UUID_GENERATOR_TAG)
                        .build();

        periodicWorkRequests.put(PULL_SERVICE_TAG, periodicPullServiceWorkRequest);
        periodicWorkRequests.put(LOG_PURGER_TAG, periodicLogPurgerWorkRequest);
//        periodicWorkRequests.put(UUID_GENERATOR_TAG, periodicUUIDGeneratorWorkRequest);
        startWorkIfNotScheduled();
    }

    private void startWorkIfNotScheduled() {
        for (Map.Entry<String, PeriodicWorkRequest> entry : periodicWorkRequests.entrySet()) {
            startUniqueWork(entry.getValue(), entry.getKey());
        }
    }

    private void startUniqueWork(PeriodicWorkRequest periodicWorkRequest, String workTag) {
        WorkManager instance = WorkManager.getInstance(context);
        if (workTag.equalsIgnoreCase(UUID_GENERATOR_TAG)) {
            instance.enqueueUniquePeriodicWork(workTag, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
        } else
            instance.enqueueUniquePeriodicWork(workTag, ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }

    private int getUUIDInterval() {
        if (Constants.DEBUG) {
            return Constants.UUIDGenerationIntervalInSecondsDebug;
        } else {
            return Constants.UUIDGenerationIntervalInSeconds;
        }
    }

}
