package com.ulabs.nfcmodule;

import android.content.Context;
import android.nfc.Tag;

/**
 * Created by OH-Biz on 2017-10-16.
 */

public interface INFCManager {
    boolean checkNFCFeature(Context context);
    String[] read(Tag tag);
    void write(Tag tag, int recordType, String... data);
}
