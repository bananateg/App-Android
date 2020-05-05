package edu.uw.covidsafe.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import edu.uw.covidsafe.seed_uuid.SeedUUIDDbRecordRepository;
import edu.uw.covidsafe.seed_uuid.SeedUUIDRecord;

public class RegenerateSeedUponReport extends AsyncTask<Void, Void, Void> {

    public Context context;

    public RegenerateSeedUponReport(Context context) {
        this.context = context;
    }

    int numRecordsToGenerate = -1;
    long ts_start = -1;

    public RegenerateSeedUponReport(int numRecordsToGenerate, long ts_start) {
        this.numRecordsToGenerate = numRecordsToGenerate;
        this.ts_start = ts_start;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.e("regen","regenerateSeedUponReport");
        // disable the current uuid generation task
        // delete all stored seeds and uuids

        SeedUUIDDbRecordRepository repo = new SeedUUIDDbRecordRepository(context);

        Constants.EnableUUIDGeneration = false;

        try {
            List<SeedUUIDRecord> records = repo.getAllSortedRecords();
            if (records.size() == 0) {
                return null;
            }
            if (this.ts_start == -1) {
                this.ts_start = records.get(records.size() - 1).getRawTs();
            }
            if (this.numRecordsToGenerate == -1) {
                this.numRecordsToGenerate = records.size();
            }

            repo.deleteAll();

            List<SeedUUIDRecord> beforeList = new LinkedList<>();
            for (int i = 0; i < 10; i++) {
                beforeList = repo.getAllRecords();
                Log.e("regen", "done with deletes " + beforeList.size());
                Thread.sleep(1000);
                if (beforeList.size() == 0) {
                    break;
                }
            }

            if (beforeList.size() != 0) {
                return null;
            }

            List<SeedUUIDRecord> afterList = repo.getAllRecords();
            Log.e("regen","size after deleting "+afterList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("regen","done deleting");

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm aa");
        Log.e("regen","generate "+this.numRecordsToGenerate);
        Log.e("regen","ts_start "+format.format(this.ts_start));
        CryptoUtils.batchGenerateRecords(context, ts_start, numRecordsToGenerate);

        try {
            for (int i = 0; i < 10; i++) {
                List<SeedUUIDRecord> afterList = repo.getAllRecords();
                Log.e("regen", "done with inserts " + afterList.size());
                Thread.sleep(1000);
                if (afterList.size() >= this.numRecordsToGenerate) {
                    Log.e("regen","after list size is good "+afterList.size());
                    break;
                }
            }
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }
        List<SeedUUIDRecord> afterList = repo.getAllRecords();
        Log.e("regen","after list size?"+afterList.size());

        Constants.EnableUUIDGeneration = true;

        return null;
    }
}
