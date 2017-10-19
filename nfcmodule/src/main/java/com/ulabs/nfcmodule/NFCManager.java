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
import android.os.Parcelable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by OH-Biz on 2017-10-16.
 */

public class NFCManager{

    public static final int[] URI_PREFIX = new int[] {
     0x00 , /**-empty-*/
     0x01 , /**http://www.*/
     0x02 , /**https://www.*/
     0x03  ,/**http://*/
     0x04 , /**https://*/
     0x05 , /**tel:*/
     0x06 , /**mailto:*/
     0x07 , /**ftp://anonymous:anonymous@*/
     0x08 , /**ftp://ftp.*/
     0x09 , /**ftps://*/
     0x0A , /**sftp://*/
     0x0B , /**smb://*/
     0x0C , /**nfs://*/
     0x0D , /**ftp://*/
     0x0E , /**dav://*/
     0x0F , /**news:*/
     0x10 , /**telnet://*/
     0x11 , /**imap:*/
     0x12 , /**rtsp://*/
     0x13 , /**urn:*/
     0x14 , /**pop:*/
     0x15 , /**sip:*/
     0x16 , /**sips:*/
     0x17 , /**tftp:*/
     0x18  ,/**btspp://*/
     0x19  ,/**btl2cap://*/
     0x1A  ,/**btgoep://*/
     0x1B  ,/**tcpobex://*/
     0x1C  ,/**irdaobex://*/
     0x1D  ,/**file://*/
     0x1E  ,/**urn:epc:id:*/
     0x1F  ,/**urn:epc:tag:*/
     0x20  ,/**urn:epc:pat:*/
     0x21  ,/**urn:epc:raw:*/
     0x22  ,/**urn:epc:*/
     0x23  ,/**urn:nfc:*/
    };

    private NfcAdapter mAdapter;
    private static NFCManager manager;
    private PendingIntent pendingIntent;
    private String[][] techListsArray = new String[][]{new String[]{NfcF.class.getName()}};

    private NFCManager() {
    }

    public static NFCManager getInstance(){
        if(manager == null){
            manager = new NFCManager();
        }
        return manager;
    }

    public boolean checkNFCFeature(Context context) {
        mAdapter = NfcAdapter.getDefaultAdapter(context);
        if (mAdapter == null){
            return false;
        }else{
            return true;
        }
    }

    public NdefRecord[] read(Tag tag) {
        Ndef ndef = Ndef.get(tag);
        NdefRecord[] records;
        try {
            ndef.connect();
            NdefMessage msg = ndef.getNdefMessage();
            records = msg.getRecords();
            return records;
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

    public NdefRecord[] readBeam(Parcelable[] rawMessage){
        NdefMessage msg = (NdefMessage) rawMessage[0];
        return msg.getRecords();
    }

    public void write(Tag tag, NdefRecord[] records) {
        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
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


    public void writeMimeTypeFormat(Tag tag, String[] payload, String... mimeType){
        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
            NdefRecord[] records = createMimeMediaRecord(payload, mimeType);
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

    public void writeAbsoluteUriFormat(Tag tag, String... uriList){
        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
            NdefRecord[] records = createAbsoluteUriRecord(uriList);
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

    public void writeWellKnownTextFormat(Tag tag, String... text){
        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
            NdefRecord[] records = createWellKnownTextRecord(text);
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

    public void writeWellKnownUriFormat(Tag tag, int prefix, String... uriList){
        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
            NdefRecord[] records = createWellKnownURIRecord(prefix, uriList);
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

    public void writeExternalTypeFormat(Tag tag, String payload, String... pathPrefix){
        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
            NdefRecord[] records = createExternalTypeRecord(payload, pathPrefix);
            ndef.writeNdefMessage(new NdefMessage(records));
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
    }

    public void beamMessage(NdefRecord[] records, Activity activity, Activity... activities){
        NdefMessage msg = new NdefMessage(records);
        mAdapter.setNdefPushMessage(msg, activity, activities);
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
    public NdefRecord[] createMimeMediaRecord(String[] payload, String... mimeType){
        NdefRecord[] records = new NdefRecord[mimeType.length];
        for(int i = 0 ; i < mimeType.length ; i++){
            NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeType[i].getBytes(Charset.forName("UTF-8")),
                    new byte[0], payload[i].getBytes(Charset.forName("UTF-8")));

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
            NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], text[i].getBytes(Charset.forName("UTF-8")));
            records[i] = textRecord;
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
    public NdefRecord[] createWellKnownURIRecord(int prefix, String... uriFields){
        NdefRecord[] records = new NdefRecord[uriFields.length];
        for(int i = 0; i < uriFields.length ; i++){
            byte[] uriField = uriFields[i].getBytes(Charset.forName("UTF-8"));
            byte[] payload = new byte[uriField.length + 1];
            payload[0] = (byte) prefix;
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

    public NdefRecord[] createExternalTypeRecord(String payload, String... pathPrefix){
        NdefRecord[] records = new NdefRecord[pathPrefix.length];
        byte[] payload_byte = payload.getBytes(Charset.forName("UTF-8"));
        for(int i = 0; i < pathPrefix.length ; i++){
            try {
                NdefRecord extRecord = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, pathPrefix[i].getBytes("UTF-8"), new byte[0], payload_byte);
                records[i] = extRecord;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return records;
    }

    public NdefRecord[] createAndroidApplicationRecord(String packageName){
        return new NdefRecord[]{NdefRecord.createApplicationRecord(packageName)};
    }


    public NfcAdapter getAdapter() {
        return mAdapter;
    }

    public IntentFilter makeIntentFilter(String dataType, String NFC_Action){
        IntentFilter filter = new IntentFilter(NFC_Action);
        try {
            filter.addDataType(dataType);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        return filter;
    }

    public IntentFilter makeIntentFilter(String uriScheme, String host, String NFC_Action){
        IntentFilter filter = new IntentFilter(NFC_Action);
        filter.addDataScheme(uriScheme);
        filter.addDataAuthority(host, null);
        return filter;
    }

    public void useForegroundDispatch(Context context, IntentFilter... filters){
        pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        //앱에서 nfc 감지할 필터를 필요한 만큼 설정해야한다.

        IntentFilter[] array = new IntentFilter[filters.length];
        System.arraycopy(filters, 0, array, 0, filters.length);
        mAdapter.enableForegroundDispatch((Activity) context, pendingIntent, array, techListsArray);
    }



    public void disuseForegroundDispatch(Context context){
        mAdapter.disableForegroundDispatch((Activity) context);
    }

}
