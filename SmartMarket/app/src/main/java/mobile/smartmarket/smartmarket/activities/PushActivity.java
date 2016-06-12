package mobile.smartmarket.smartmarket.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import mobile.smartmarket.smartmarket.R;
import mobile.smartmarket.smartmarket.beans.BasicDataBean;
import mobile.smartmarket.smartmarket.helpers.AsyncTaskMW;
import mobile.smartmarket.smartmarket.helpers.Constants;
import mobile.smartmarket.smartmarket.helpers.UpdatePayment;

public class PushActivity extends AppCompatActivity {
    private static final String LOG_TAG = Constants.STR_LOG_TAG.concat("PushActivity");
    private static final boolean IS_DEBBUG = Constants.isDebbud;

    private TextView txtVendedor = null;
    private TextView txtMonto = null;
    private Button btoAceptar = null;
    private Button btoRechazar = null;
    private ProgressBar progressBar = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);

        if (IS_DEBBUG) Log.d(LOG_TAG, "message:    " + getIntent().getStringExtra("message"));
        if (IS_DEBBUG) Log.d(LOG_TAG, "title:      " + getIntent().getStringExtra("title"));
        if (IS_DEBBUG) Log.d(LOG_TAG, "idcard:     " + getIntent().getStringExtra("idcard"));
        if (IS_DEBBUG) Log.d(LOG_TAG, "vendedor: " + getIntent().getStringExtra("vendedor"));
        if (IS_DEBBUG) Log.d(LOG_TAG, "monto:  " + getIntent().getStringExtra("monto"));

        txtVendedor = (TextView) findViewById(R.id.vendedor);
        txtMonto = (TextView) findViewById(R.id.monto);
        btoAceptar = (Button) findViewById(R.id.button);
        btoRechazar = (Button) findViewById(R.id.button2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        txtVendedor.setText(getIntent().getStringExtra("vendedor"));
        txtMonto.setText(getIntent().getStringExtra("monto"));

        btoAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePayment(new BasicDataBean(
                        getIntent().getStringExtra("message"),
                        getIntent().getStringExtra("title"),
                        getIntent().getStringExtra("idcard"),
                        getIntent().getStringExtra("vendedor"),
                        getIntent().getStringExtra("monto")
                ), true);

            }
        });

        btoRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePayment(new BasicDataBean(
                        getIntent().getStringExtra("message"),
                        getIntent().getStringExtra("title"),
                        getIntent().getStringExtra("idcard"),
                        getIntent().getStringExtra("vendedor"),
                        getIntent().getStringExtra("monto")
                ), false);

            }
        });

    }

    private void updatePayment(BasicDataBean basicDataBean, final boolean isAccepted){
        progressBar.setVisibility(View.VISIBLE);

        new AsyncTaskMW<BasicDataBean, Boolean, String>(basicDataBean, ""){

            @Override
            protected Boolean process(BasicDataBean input) {
                if (IS_DEBBUG) {
                    StringBuffer msgLog = new StringBuffer("process: input: ");
                    msgLog.append(input.toString());
                    Log.d(LOG_TAG, msgLog.toString());
                }

                return UpdatePayment.getInstance().process(input, isAccepted);
            }

            @Override
            protected void applyOutputToTarget(Boolean output, String target) {
                if (IS_DEBBUG) {
                    StringBuffer msgLog = new StringBuffer("process: output: ");
                    msgLog.append(output.toString());
                    Log.d(LOG_TAG, msgLog.toString());
                }
                drawAnswer(output);

            }
        }.execute();

    }

    private void drawAnswer(Boolean output){
        progressBar.setVisibility(View.GONE);


        if(output){
            Toast.makeText(this, "Procesado con exito", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Error al procesar el pago", Toast.LENGTH_LONG).show();
        }

    }
}
