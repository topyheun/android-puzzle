package com.example.dg04;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {


    static final int aryImageButtons[] = {R.id.image_but_1, R.id.image_but_2, R.id.image_but_3, R.id.image_but_4,
            R.id.image_but_5, R.id.image_but_6, R.id.image_but_7, R.id.image_but_8, R.id.image_but_9};

    static final int aryInitialImages[] = {R.drawable.su_1, R.drawable.su_2, R.drawable.su_3, R.drawable.su_4,
            R.drawable.su_5, R.drawable.su_6, R.drawable.su_7, R.drawable.su_8, R.drawable.fig_blank};


    static final int aryNextPanelIndex[][] = {{1, 3}, {0, 4, 2}, {1, 5}, {0, 4, 6}, {1, 3, 5, 7}, {2, 4, 8}, {3, 7}, {4, 6, 8}, {7, 5}};

    PanelController Panels[] = new PanelController[aryImageButtons.length];



    class PanelController implements View.OnClickListener {
        ImageButton move_button;
        int current_image = 0;
        final int move_next[];

        public PanelController(ImageButton button, int resid, final int next[]) {
            move_button = button;
            move_next = next;
            setImageResource(resid);
            move_button.setOnClickListener(this);
        }

        public int setImageResource(int resid) {
            int old = current_image;
            current_image = resid;
            move_button.setImageResource(resid);
            return old;
        }

        public int getImageResource() {
            return current_image;
        }

        public boolean isBlank() {
            return current_image == R.drawable.fig_blank;
        }

        public void swapImage(PanelController other) {
            int previous = other.setImageResource(current_image);

            setImageResource(previous);
        }
        public void swapImage2()
        {
            for (int i = 0; i < move_next.length; i++) {
                if (Panels[move_next[i]].isBlank()) {
                    swapImage(Panels[move_next[i]]);
                    break;
                }
            }
        }
        public void onClick(View v) {
            for (int i = 0; i < move_next.length; i++) {
                if (Panels[move_next[i]].isBlank()) {
                    swapImage(Panels[move_next[i]]);
                    break;
                }
            }
            if (isCompleted()) complete();
        }
    }
    MediaPlayer mp = new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createPanelController();
        setShuffleButtonListener();
        vibrator();
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ); //화면 세로 고정

        Button btn39 = (Button) findViewById(R.id.btn39);
        btn39.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent4 = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent4);
                stopplay();
            }
        });
    }
    public void play(){
        mp= MediaPlayer.create(this,R.raw.bgm);
        mp.setLooping(true);
        mp.start();

    }
    public  void stopplay()
    {
        mp.stop();
    }

    public void onUserLeaveHint(){
        mp.pause();
        super.onUserLeaveHint();
    }
    public void onBackPressed(){
        mp.stop();
        super.onBackPressed();
    }
    Vibrator vibrate;

    private void vibrator()
    {
        vibrate=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    }



    private void createPanelController() {
        for (int i = 0; i < aryImageButtons.length; i++) {
            ImageButton b = (ImageButton) findViewById(aryImageButtons[i]);
            Panels[i] = new PanelController(b, aryInitialImages[i], aryNextPanelIndex[i]);
        }
    }

    private boolean isCompleted() {
        for (int i = 0; i < aryInitialImages.length; i++) {
            if (aryInitialImages[i] != Panels[i].getImageResource()) return false;
        }
        return true;
    }

    private void shuffle() {
        int size = aryInitialImages.length;
        for(int j=0;j<350;j++) {
            for (int i = 0; i < size - 1; i++) {
                int swap = (int) (Math.random() * (size - i));
                Panels[swap].swapImage2();
            }
        }
    }

    private void startChronometer() {
        Chronometer c = (Chronometer) findViewById(R.id.chronometer);
        c.setBase(SystemClock.elapsedRealtime());
        c.start();
    }

    private long stopChronometer() {
        Chronometer c = (Chronometer) findViewById(R.id.chronometer);
        c.stop();
        return SystemClock.elapsedRealtime() - c.getBase();
    }

    private void setShuffleButtonListener() {
        Button b = (Button) findViewById(R.id.shuffle_button);

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shuffle();
                startChronometer();
                stopplay();
                play();
            }
        });
    }


    private void complete() {
        long msec = stopChronometer();
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.complete_title);
        b.setMessage(msec / 1000 + "sec");
        b.setIcon(R.mipmap.ic_launcher);
        b.setPositiveButton(R.string.complete_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        b.show();
        stopplay();
        vibrate.vibrate(3000);
        vibrator();
    }
    //아멘
}
