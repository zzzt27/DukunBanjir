package paskal.zzzt.dukunbanjir.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import paskal.zzzt.dukunbanjir.R;

/**
 * Created by ManUnited on 4/1/2016.
 */
public class DialogNoInternet {

    public void showDialog(final Activity activity){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_internet);

        FrameLayout exitButton = (FrameLayout) dialog.findViewById(R.id.btnExitDialogInternet);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });


        dialog.show();

    }

}