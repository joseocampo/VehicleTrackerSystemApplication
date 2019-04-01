package joseocampo.VehicleTrackerSystemApp.com;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class FailureReport extends AppCompatActivity {
    private ImageView save_failure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failure_report);
        getSupportActionBar().setBackgroundDrawable(
                new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.fondos)));
        getSupportActionBar().setTitle("Reporte de averías");
        save_failure = (ImageView) findViewById(R.id.save_failure);

        save_failure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFailure();
            }
        });
    }

    private void saveFailure() {
        Toast.makeText(getApplicationContext(), "Conectarse a la bd ", Toast.LENGTH_LONG).show();
    }
}
