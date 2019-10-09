package com.audiorecorder.voicerecorderhd.editor;


import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class SimplePermissonListener implements MultiplePermissionsListener {
    private final MainActivity mainActivity;

    public SimplePermissonListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {

    }

    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

        mainActivity.showPermissionRationalbe(token);

    }
}
