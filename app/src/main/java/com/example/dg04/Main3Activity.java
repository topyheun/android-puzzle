package com.example.dg04;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;

public class Main3Activity extends AppCompatActivity {
    static final int aryImageButtons[] = {R.id.image_but_1, R.id.image_but_2, R.id.image_but_3, R.id.image_but_4,
            R.id.image_but_5, R.id.image_but_6, R.id.image_but_7, R.id.image_but_8,
            R.id.image_but_9,R.id.image_but_10,R.id.image_but_11,
            R.id.image_but_12,R.id.image_but_13,R.id.image_but_14,R.id.image_but_15,R.id.image_but_16};

    static final int aryInitialImages[] = {R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4,
            R.drawable.a5, R.drawable.a6, R.drawable.a7, R.drawable.a8,
            R.drawable.a9, R.drawable.a10, R.drawable.a11, R.drawable.a12,
            R.drawable.a13, R.drawable.a14, R.drawable.a15, R.drawable.fig_blank};


    static final int aryNextPanelIndex[][] = {{1,4},{0,2,5},{1,3,6},{2,7},
    {0,5,8},{1,4,6,9},{2,5,7,10},{3,6,11},
    {4,9,12},{5,8,10,13},{6,9,11,14},{7,10,15},
    {8,13},{9,12,14},{10,13,15},{11,14}};


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
        setContentView(R.layout.page4);
        createPanelController();
        setShuffleButtonListener();
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );


        Button btn38 = (Button) findViewById(R.id.btn38);
        btn38.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(Main3Activity.this,Main2Activity.class);
                startActivity(intent3);
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

    public void onUserLeaveHint(){  //핸드폰 홈 버튼
        mp.pause();
        super.onUserLeaveHint();
    }
    public void onBackPressed(){    //핸드폰 뒤로가기
        mp.stop();
        super.onBackPressed();
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

    }
}
