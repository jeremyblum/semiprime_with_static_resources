package edu.psu.jjb24.factorprimes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity  {
    FactoringTask backgroundTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnStart).setOnClickListener(this::factor);
        findViewById(R.id.btnUpdate).setOnClickListener(this::update);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isFactoring")) {
            BigInteger lastTested = new BigInteger(savedInstanceState.getString("lastTested"));
            BigInteger semiPrime = new BigInteger(savedInstanceState.getString("semiprime"));
            backgroundTask = new FactoringTask(semiPrime, lastTested);
            backgroundTask.execute();
            findViewById(R.id.btnUpdate).setEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundTask != null) {
            backgroundTask.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

        if (backgroundTask != null) {
            savedState.putBoolean("isFactoring", true);
            savedState.putString("semiprime", backgroundTask.getSemiprime().toString());
            savedState.putString("lastTested", backgroundTask.getLastTested().toString());
        }
        else {
            savedState.putBoolean("isFactoring", false);
        }
    }

    private void factor(View v) {
        if (backgroundTask != null) {
            backgroundTask.cancel();
        }

        BigInteger semiPrime = new BigInteger(((EditText) findViewById(R.id.etNumber)).getText().toString());

        backgroundTask = new FactoringTask(semiPrime, null);
        backgroundTask.execute();
        findViewById(R.id.btnUpdate).setEnabled(true);
    }

    private void update(View v) {
        if (backgroundTask == null) {
            v.setEnabled(false);
        }
        else {
            BigInteger factor = backgroundTask.getFactor();
            if (factor != null) {
                ((TextView) findViewById(R.id.txtProgress)).setText("FACTORED!!!\n" + factor.toString());
                backgroundTask = null;
                v.setEnabled(false);
            }
            else {
                ((TextView) findViewById(R.id.txtProgress)).setText("No factor found yet.\nLast factor tested:\n" + backgroundTask.getLastTested());
            }
        }
    }
}