package com.ulabs.nfcmodule;

import android.nfc.NdefRecord;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by OH-Biz on 2017-10-17.
 */

public class NdefRecordFactory {

    private ArrayList<NdefRecord[]> recordList = new ArrayList<NdefRecord[]>();

    public void append(NdefRecord[] records) {
        recordList.add(records);
    }

    public NdefRecord[] getAll() {
        ArrayList<NdefRecord> collection = new ArrayList<NdefRecord>();

        for(int i = 0; i < recordList.size() ; i++){
            for (int j = 0; j < recordList.get(i).length ; j++){
                NdefRecord record = recordList.get(i)[j];
                collection.add(record);
            }
        }

        NdefRecord[] result = new NdefRecord[collection.size()];
        for(int i = 0 ; i < collection.size() ; i++){
            result[i] = collection.get(i);
        }
        Log.d("ljm2006", "result array length => " + result.length);
        return result;
    }

    public ArrayList<NdefRecord[]> getRecordList() {
        return recordList;
    }
}
