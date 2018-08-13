package com.junkersolutions.poefun.Dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.junkersolutions.poefun.Class.Useful;
import com.junkersolutions.poefun.R;

import java.io.File;

/**
 * Created by jever on 23/11/2017.
 */

public class Dialog {


    public interface OnClickOkDialogMessage {
        void onClickOkDialogMessage();
    }

    public static void showDialogMessage(Activity activity,String message, final OnClickOkDialogMessage dialogMessage ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity ,R.style.AlertDialogStyle);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.ok_message_dialog), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogMessage.onClickOkDialogMessage();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

}

