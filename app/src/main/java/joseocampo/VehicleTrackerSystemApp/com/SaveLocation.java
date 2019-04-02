package joseocampo.VehicleTrackerSystemApp.com;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SaveLocation extends AppCompatActivity
        implements Response.ErrorListener, Response.Listener<JSONObject> {

    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;
    private String street = "";
    private Button btnActivarUbicacion;
    private TextView txtDireccion, txtCoordenadas;
    private String loan;
    private int precision = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_location);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
        getSupportActionBar().setTitle("Guardar mi Ubicación");


        btnActivarUbicacion = findViewById(R.id.btnActivarUbicacion);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtCoordenadas = findViewById(R.id.txtCoordenadas);

        Bundle bundle = getIntent().getExtras();
        loan = "";
        if (bundle != null) {
            loan = bundle.getString("loanNumber");
            Toast.makeText(getApplicationContext(), "Numero de Prestamo: " + loan, Toast.LENGTH_LONG).show();
        }


        btnActivarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager locationManager =
                        (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                // Toast.makeText(getBaseContext(),"Hola",Toast.LENGTH_LONG).show();
                final boolean gpsActivado = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!gpsActivado) {


                }

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        txtCoordenadas.setText("Latitud:  " + location.getLatitude() + "    Longitud:  "
                                + location.getLongitude()
                        );
                        obtenerDireccion(location);

                    }


                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        ///Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        // startActivity(settingsIntent);


                    }
                };

                int permissionCheck = ContextCompat.checkSelfPermission(SaveLocation.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }

        });

        int permissionCheck =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                //si el permiso no está denegado, hacemos la solicitud del permiso.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    public void obtenerDireccion(Location location) {
        if (location != null) {
            if (location.getLongitude() != 0 && location.getLatitude() != 0) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if (!addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        txtDireccion.setText("Direccion:  " + address.getAddressLine(0));

                        if (street.equals(address.getAddressLine(0))) {
                            precision = 0;

                        } else {
                            if (precision < 10) {
                                precision++;

                            } else {
                                guardarCalle(address.getAddressLine(0));
                                street = address.getAddressLine(0);
                            }
                        }

                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    private void guardarCalle(String calle) {


        String url = new StringBuilder().append("http://vtsmsph.com/guardarCalle.php?user=tony").append("&route=").append(loan).append("&calle=").append(calle).toString();
        //Toast.makeText(getApplicationContext(), "URL:   " +calle, Toast.LENGTH_LONG).show();
        url.replace(" ", "%20");
        //Toast.makeText(getApplicationContext(), "URL despues de replace:   " +url, Toast.LENGTH_LONG).show();


        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);

        request = Volley.newRequestQueue(getApplicationContext());
        request.add(jsonObjectRequest);//aqui le enviamos la respuesta de la bd, a el metodo response.
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "Error " + error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(getApplicationContext(), "Response " + response.toString(), Toast.LENGTH_LONG).show();
    }
}
