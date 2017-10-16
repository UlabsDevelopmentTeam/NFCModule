package com.ulabs.nfcmodule;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by OH-Biz on 2017-10-16.
 */

public class NFCManager implements INFCManager{
    private NfcAdapter mAdapter;
    private static NFCManager manager;
    private PendingIntent pendingIntent;
    private IntentFilter filter;
    private String[][] techListsArray = new String[][]{new String[]{NfcF.class.getName()}};

    public static final int WRITE_TYPE_WELL_KNOWN_TEXT = 0;
    public static final int WRITE_TYPE_MIME_MEDIA = 1;
    public static final int WRITE_TYPE_ABSOLUTE_URI = 2;
    public static final int WRITE_TYPE_WELL_KNOWN_URI = 3;
    public static final int WRITE_TYPE_EXTERNAL_TYPE = 4;

    private NFCManager() {
    }

    public static NFCManager getInstance(){
        if(manager == null){
            manager = new NFCManager();
        }
        return manager;
    }

    @Override
    public boolean checkNFCFeature(Context context) {
        mAdapter = NfcAdapter.getDefaultAdapter(context);
        if (mAdapter == null){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public String[] read(Tag tag) {
        Ndef ndef = Ndef.get(tag);

        try {
            ndef.connect();
            NdefMessage msg = ndef.getNdefMessage();
            NdefRecord[] records = msg.getRecords();
            String[] readData = new String[records.length];

            for(int i = 0 ; i < records.length ; i++){
                NdefRecord record = records[i];
                byte[] payload = record.getPayload();
                readData[i] = new String(payload, "UTF-8");
            }
            return readData;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }finally {
            try {
                ndef.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void write(Tag tag, int recordType, String... data) {
        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
            NdefRecord[] records = createWellKnownTextRecord(data);
            ndef.writeNdefMessage(new NdefMessage(records));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }finally {
            if(ndef != null){
                try {
                    ndef.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * createAbsoluteUriRecord()
     * <intent-filter>

     <action android:name=“android.nfc.action.NDEF_DISCOVERED”/>

     <category android:name=“android.intent.category.DEFAULT”/>

     <data android:scheme=“http”

     android:host=“developer.android.com“ <- 예시

     android:pathPrefix=“/index.html”/>

     </intent-filter>

     * */
    public NdefRecord[] createAbsoluteUriRecord(String... uriList){
        NdefRecord[] records = new NdefRecord[uriList.length];
        for(int i = 0; i < uriList.length ; i++){
            NdefRecord uriRecord = new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI, uriList[i].getBytes(Charset.forName("UTF-8")), new byte[0], new byte[0]);
            records[i] = uriRecord;
        }
        return records;
    }

    /**
     * createMimeMediaRecord()
     *<intent-filter>

     <action android:name=“android.nfc.action.NDEF_DISCOVERED”/>

     <category android:name=“android.intent.category.DEFAULT”/>

     <data android:mimeType=“application/com.example.android.beam”/> <- 예시

     </intent-filter>

     * */
    public NdefRecord[] createMimeMediaRecord(String... mimeType){
        NdefRecord[] records = new NdefRecord[mimeType.length];
        for(int i = 0 ; i < mimeType.length ; i++){
            NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeType[i].getBytes(Charset.forName("UTF-8")), new byte[0], new byte[0]);
            records[i] = mimeRecord;
        }
        return records;
    }

    /** createWellKnownTextRecord()
     * <intent-filter>

     <action android:name=“android.nfc.action.NDEF_DISCOVERED”/>

     <category android:name=“android.intent.category.DEFAULT”/>

     <data android:mimeType=“text/plain”/>

     </intent-filter>

     * */
    public NdefRecord[] createWellKnownTextRecord(String... text){
        NdefRecord[] records = new NdefRecord[text.length];
        for(int i = 0 ; i < text.length ; i++){
            try {
                NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], text[i].getBytes("UTF-8"));
                records[i] = textRecord;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return records;
    }

    /**createWellKnownURIRecord()
     * <intent-filter>

     <action android:name=“android.nfc.action.NDEF_DISCOVERED”/>

     <category android:name=“android.intent.category.DEFAULT”/>

     <data android:scheme=“http”

     android:host=“example.com“ <- 예시, uriField

     android:pathPrefix=“”/>

     </intent-filter>

     * */
    public NdefRecord[] createWellKnownURIRecord(String... uriFields){
        NdefRecord[] records = new NdefRecord[uriFields.length];
        for(int i = 0; i < uriFields.length ; i++){
            byte[] uriField = uriFields[i].getBytes(Charset.forName("UTF-8"));
            byte[] payload = new byte[uriField.length + 1];
            payload[0] = 0x01;
            System.arraycopy(uriField, 0 , payload , 1, uriField.length);
            NdefRecord uriRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
            records[i] = uriRecord;
        }
        return records;
    }



    /**
     *<intent-filter>

     <action android:name=“android.nfc.action.NDEF_DISCOVERED”/>

     <category android:name=“android.intent.category.DEFAULT”/>

     <data android:scheme=“vnd.android.nfc”

     android:host=“ext”

     android:pathPrefix=“/example.com:externalType”/>

     </intent-filter>

     * */

    public void createExternalTypeRecord(byte[] payload, String... pathPrefix){
        NdefRecord[] records = new NdefRecord[pathPrefix.length];
        for(int i = 0; i < pathPrefix.length ; i++){
            try {
                NdefRecord extRecord = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, pathPrefix[i].getBytes("UTF-8"), new byte[0], payload);
                records[i] = extRecord;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    public NfcAdapter getAdapter() {
        return mAdapter;
    }

    public void useForegroundDispatch(Context context, String dataType){
        pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            filter.addDataType(dataType);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        IntentFilter[] filters = new IntentFilter[]{filter, };
        mAdapter.enableForegroundDispatch((Activity) context, pendingIntent, filters, techListsArray);
    }

    public void unuseForgroundDispatch(Context context){
        mAdapter.disableForegroundDispatch((Activity) context);
    }

}
