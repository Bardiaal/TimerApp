package com.example.timerapp;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView countdown, textView2;
    FloatingActionButton fab;
    EditText editText;
    String[] strings;
    Spinner spinner;
    AsyncTaskCD taskCD;
    ThreadCD threadCD;
    TimerTaskThread taskThread;
    boolean hasStarted;
    boolean runningThread;
    int seconds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        strings = getResources().getStringArray(R.array.spinner);
        countdown = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        spinner = findViewById(R.id.spinner);
        editText = findViewById(R.id.editText);
        taskCD = new AsyncTaskCD();
        threadCD = new ThreadCD();
        taskThread = new TimerTaskThread();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().isEmpty()) {
                    seconds = 10;
                } else {
                    seconds = Integer.parseInt(editText.getText().toString());
                }
                if (spinner.getSelectedItem().equals(strings[0])) {
                    if (taskCD.getStatus() == AsyncTaskCD.Status.RUNNING) {
                        taskCD.cancel(true);
                        textView2.setText(getResources().getString(R.string.button_start));
                        fab.setImageDrawable(getDrawable(R.drawable.ic_action_start_timer));
                        countdown.setText(getResources().getString(R.string.timer_start));
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.finished_by_user), Toast.LENGTH_LONG).show();
                    } else {
                        taskCD = new AsyncTaskCD();
                        taskCD.execute();
                    }
                } else if (spinner.getSelectedItem().equals(strings[1])) {
                    if (threadCD.isAlive()) {
                        runningThread = false;
                        textView2.setText(getResources().getString(R.string.button_start));
                        fab.setImageDrawable(getDrawable(R.drawable.ic_action_start_timer));
                        countdown.setText(getResources().getString(R.string.timer_start));
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.finished_by_user), Toast.LENGTH_LONG).show();
                    } else {
                        textView2.setText(getResources().getString(R.string.button_stop));
                        fab.setImageDrawable(getDrawable(R.drawable.ic_action_stop_timer));
                        runningThread = true;
                        threadCD = new ThreadCD();
                        threadCD.start();
                    }
                } else if (spinner.getSelectedItem().equals(strings[2])) {
                    if (hasStarted) {
                        hasStarted = false;
                        textView2.setText(getResources().getString(R.string.button_start));
                        fab.setImageDrawable(getDrawable(R.drawable.ic_action_start_timer));
                        countdown.setText(getResources().getString(R.string.timer_start));
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.finished_by_user), Toast.LENGTH_LONG).show();
                    } else {
                        textView2.setText(getResources().getString(R.string.button_stop));
                        fab.setImageDrawable(getDrawable(R.drawable.ic_action_stop_timer));
                        hasStarted = true;
                        taskThread = new TimerTaskThread();
                        Thread thread = new Thread(taskThread);
                        thread.start();
                    }
                }
            }
        });


    }

    private class AsyncTaskCD extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            textView2.setText(getResources().getString(R.string.button_stop));
            fab.setImageDrawable(getDrawable(R.drawable.ic_action_stop_timer));
        }

        @Override
        protected Void doInBackground(Void... params) {
            for(int i=seconds; i >= 0; i--){
                try {
                    Thread.sleep(1000);
                    publishProgress(i);
                } catch (InterruptedException e) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.err), Toast.LENGTH_LONG).show();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            countdown.setText(String.valueOf(values[0]));
        }

        @Override
        protected void onPostExecute(Void result) {
            textView2.setText(getResources().getString(R.string.button_start));
            fab.setImageDrawable(getDrawable(R.drawable.ic_action_start_timer));
            countdown.setText(getResources().getString(R.string.timer_start));
            Toast.makeText(MainActivity.this, getResources().getString(R.string.finished), Toast.LENGTH_LONG).show();
        }
    }

    private class ThreadCD extends Thread {
        @Override
        public void run() {
            for(int i=seconds; i >= 0; i--){
                if (!runningThread) {
                    interrupt();
                    break;
                }
                try {
                    Thread.sleep(1000);
                    setValue(i);
                } catch (InterruptedException e) {
                    e.getMessage();
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView2.setText(getResources().getString(R.string.button_start));
                    fab.setImageDrawable(getDrawable(R.drawable.ic_action_start_timer));
                    countdown.setText(getResources().getString(R.string.timer_start));
                    if (runningThread) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.finished), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        void setValue(final int i) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countdown.setText(String.valueOf(i));
                }
            });
        }
    }

    private class TimerTaskThread extends TimerTask {

        @Override
        public void run() {
            for(int i=seconds; i >= 0; i--){
                if (!hasStarted) {
                    cancel();
                    break;
                }
                try {
                    Thread.sleep(1000);
                    setValue(i);
                } catch (InterruptedException e) {
                    e.getMessage();
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView2.setText(getResources().getString(R.string.button_start));
                    fab.setImageDrawable(getDrawable(R.drawable.ic_action_start_timer));
                    countdown.setText(getResources().getString(R.string.timer_start));
                    if (hasStarted) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.finished), Toast.LENGTH_LONG).show();
                    }
                }
            });
            hasStarted = false;
        }

        void setValue(final int i) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countdown.setText(String.valueOf(i));
                }
            });
        }
    }

}
