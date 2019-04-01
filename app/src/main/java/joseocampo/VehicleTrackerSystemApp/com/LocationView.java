package joseocampo.VehicleTrackerSystemApp.com;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationView extends FragmentActivity implements OnMapReadyCallback,
        Response.ErrorListener, Response.Listener<JSONObject> {

    private GoogleMap mMap;
    private Button btnGPS, btnNormal, btnHybrid, btnLand, btnSatelital;
    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;
    private String street = "";
    private TextView txtDireccion, txtCoordenadas;
    private int loanNumber;
    private String userLoginName;
    private MarkerOptions myMarker;
    private double latitude, longitude;
    private ImageView image_terminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnNormal = (Button) findViewById(R.id.btn_normal);
        btnHybrid = (Button) findViewById(R.id.btnHybrid);
        btnLand = (Button) findViewById(R.id.btn_land);
        btnSatelital = (Button) findViewById(R.id.btnSatelital);
        image_terminar = (ImageView) findViewById(R.id.image_terminar);

        //agregamos eventos a los botones de cambiar el tipo de vista para el ampa.
        addEvents();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userLoginName = bundle.getString("usuario");
            loanNumber = bundle.getInt("loanNumber");


            Toast.makeText(getApplicationContext(), " usuario: " + userLoginName + " LoanNumber: " + loanNumber, Toast.LENGTH_LONG).show();
        }

        //iniciamos el recorrido
        initTravel();


    }

    private void initTravel() {
        int permissionCheck =
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                //si el permiso no está denegado, hacemos la solicitud del permiso.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        }

        //Toast.makeText(getApplicationContext(), "Hola dentro de onclick", Toast.LENGTH_SHORT).show();

        LocationManager locationManager =
                (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        // Toast.makeText(getBaseContext(),"Hola",Toast.LENGTH_LONG).show();
        final boolean gpsActivado = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsActivado) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Para que la aplicacion funcione es recomandable activar GPS !");
            builder.setMessage("Desea activar GPS ?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Activando GPS", Toast.LENGTH_SHORT).show();
                    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(settingsIntent);
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "La aplicacion no funciona sin GPS", Toast.LENGTH_SHORT).show();
                }
            });

            builder.show();


        }

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setDirection(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        //se registra el listener con el loctaion manager para recivir actualizacion de localizacion.

        permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    private void failureReport() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reportar Averías !");
        builder.setMessage("Desea reportar alguna avería del vehículo ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent settingsIntent = new Intent(LocationView.this, FailureReport.class);
                startActivity(settingsIntent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishTravel();
            }
        });

        builder.show();
    }

    public void setDirection(Location location) {
        if (location != null) {
            if (location.getLongitude() != 0 && location.getLatitude() != 0) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if (!addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        // txtDireccion.setText("Direccion:  " + address.getAddressLine(0));

                        if (street.equals(address.getAddressLine(0))) {


                        } else {

                            saveStreet(address.getAddressLine(0));
                            setCurrentLocation(location.getLatitude(), location.getLongitude());
                            street = address.getAddressLine(0);

                        }

                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    private void saveStreet(String calle) {

        String url = new StringBuilder().append("http://vtsmsph.com/guardarCalle.php?user=tony").append("&route=").append(String.valueOf(loanNumber)).append("&calle=").append(calle).toString();
        //Toast.makeText(getApplicationContext(), "URL:   " +calle, Toast.LENGTH_LONG).show();
        url.replace(" ", "%20");
        //Toast.makeText(getApplicationContext(), "URL despues de replace:   " +url, Toast.LENGTH_LONG).show();


        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);

        request = Volley.newRequestQueue(getApplicationContext());
        request.add(jsonObjectRequest);//aqui le enviamos la respuesta de la bd, a el metodo response.
    }

    private void changeTypeHybrid() {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    private void changeTypeLand() {
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    private void changeTypeSatelital() {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    private void changeTypeNormal() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }


    public void addEvents() {

        btnHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTypeHybrid();
            }
        });
        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTypeNormal();
            }
        });
        btnLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTypeLand();
            }
        });
        btnSatelital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTypeSatelital();
            }
        });
        image_terminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failureReport();
            }
        });
    }

    private void finishTravel() {
        Intent intent = new Intent(getApplicationContext(), RoutesRequests.class);
        intent.putExtra("usuario", userLoginName);
        startActivity(intent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng myLocation = new LatLng(9.9948033, -84.0982678);

        myMarker = new MarkerOptions().position(myLocation).title("Mi ubicacion: " + myLocation.latitude + " " + myLocation.longitude).icon(BitmapDescriptorFactory.fromResource(R.drawable.car3));

        mMap.addMarker(myMarker);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        //activamos los botones de zomm en el mapa.
        mMap.getUiSettings().setZoomControlsEnabled(true);


        //preguntamos si tenemos los permisos de usar mi ubicacion del celular.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public void setCurrentLocation(double x, double y) {
        LatLng myLocation = new LatLng(x, y);
        myMarker.position(myLocation);

        mMap.addMarker(new MarkerOptions().position(myLocation).title("Mi ubicacion" + x + " " + y).icon(BitmapDescriptorFactory.fromResource(R.drawable.car3)));
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
