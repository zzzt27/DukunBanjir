package paskal.zzzt.dukunbanjir.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.flaviofaria.kenburnsview.Transition;

import java.util.Timer;
import java.util.TimerTask;

import paskal.zzzt.dukunbanjir.Dialog.DialogNoInternet;
import paskal.zzzt.dukunbanjir.Network.NetworkManager;
import paskal.zzzt.dukunbanjir.R;

/**
 * Created by ManUnited on 3/17/2016.
 */
public class SplashActivity extends AppCompatActivity {

    private KenBurnsView kenBurnsView;

    // Set Duration of the Splash Screen
    //long Delay = 6000;
    //private Handler splashHandler = new Handler();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        DialogNoInternet alert = new DialogNoInternet();

        kenBurnsView = (KenBurnsView) findViewById(R.id.image);
        AccelerateDecelerateInterpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
        RandomTransitionGenerator generator = new RandomTransitionGenerator(5000, ACCELERATE_DECELERATE);
        kenBurnsView.setTransitionGenerator(generator); //set new transition on kenburns view

        kenBurnsView.setTransitionListener(onTransittionListener());


        Timer t = new Timer();
        boolean checkConnection=new NetworkManager().checkConnection(this);
        if (checkConnection) {
            t.schedule(new splash(), 6000);
        } else {
            //alert.showDialog(getActivity());
            alert.showDialog(this);
        }
    }


    class splash extends TimerTask {

        @Override
        public void run() {
            Intent i = new Intent(SplashActivity.this,DetailActivity.class);
            finish();
            startActivity(i);
        }

    }

    class error extends TimerTask {

        @Override
        public void run() {
            DialogNoInternet alert = new DialogNoInternet();
            //.showDialog();

        }

    }



        //NetworkManager.isConnected(this);


        /*
        // Create a Timer
        //Timer RunSplash = new Timer();

        // Task to do when the timer ends
        TimerTask ShowSplash = new TimerTask() {
            @Override
            public void run() {
                // Close SplashScreenActivity.class
                finish();

                // Start MainActivity.class
                Intent myIntent = new Intent(SplashActivity.this,
                        DetailActivity.class);
                startActivity(myIntent);
            }
        };

        // Start the timer
        RunSplash.schedule(ShowSplash, Delay);
    }*/

    private KenBurnsView.TransitionListener onTransittionListener() {
        return new KenBurnsView.TransitionListener() {

            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
            }
        };
    }

}
