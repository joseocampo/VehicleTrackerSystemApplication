package joseocampo.VehicleTrackerSystemApp.com;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class FragmentSolicitarVhiculo extends Fragment
        implements Response.Listener<JSONArray>, Response.ErrorListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String idSolicitud = "consulta"; //Variable para determinar el tipo de transaccion a la base de datos

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    //este es un comentario de prueba para hacer commit.
    private EditText campoDestino, campoJustificacion;
    private Button btnRealizarSolicitd, btnSelectbeginHour, btnSelectEndHour, btnSelectEndDate, btnSelectInitDate;
    private TextView userRequest, vehicleRequest, targetRequest, dateTimerequest;

    private Spinner lista;
    private String[] vehicles = {"Selecciona un vehículo"};
    private String[] vehiculos = {"Vehiculos disponibles", "Nissan", "Toyota", "Nissan Versa", "Susuki", "Chevrolet", "Huydani"};

    private OnFragmentInteractionListener mListener;
    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue request;

    //variables para guardar la hora
    private int beginHour, beginMinutes, endHour, endMinutes = -1;
    private String vehiclePlate;

    //en este atributo se guarda el nombre del usuario logeado
    private String userNameLogin;


    //parametro fecha
    private static final String CERO = "0";
    private static final String BARRA = "/";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    final Calendar myCalendar = Calendar.getInstance();


    public FragmentSolicitarVhiculo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSolicitarVhiculo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSolicitarVhiculo newInstance(String param1, String param2) {
        FragmentSolicitarVhiculo fragment = new FragmentSolicitarVhiculo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_fragment_solicitar_vhiculo, container, false);

        //obtenemos el botoon para seleccionar la hora
        btnSelectbeginHour = (Button) vista.findViewById(R.id.btnBeginHour);
        btnSelectEndHour = (Button) vista.findViewById(R.id.btnEndHour);
        btnSelectEndDate = (Button) vista.findViewById(R.id.endDate);
        btnSelectInitDate = (Button) vista.findViewById(R.id.initDate);


        lista = (Spinner) vista.findViewById(R.id.vehicleList);
        campoDestino = (EditText) vista.findViewById(R.id.campoDestino);
        campoJustificacion = (EditText) vista.findViewById(R.id.campoJustificacion);
        btnRealizarSolicitd = (Button) vista.findViewById(R.id.btnRealizarSolicitud);

        //obtenemos los widgets para mostrar el estado de la solicitud de un vehiculo.
        userRequest = (TextView) vista.findViewById(R.id.userRequest);
        vehicleRequest = (TextView) vista.findViewById(R.id.vehicleRequest);
        targetRequest = (TextView) vista.findViewById(R.id.targetRequest);
        dateTimerequest = (TextView) vista.findViewById(R.id.dateTimeRequest);


        //obtenemos el nombre del usaurio logeado
        userNameLogin = getArguments().getString("usuario");
        Toast.makeText(getContext(), "Usuario: " + userNameLogin, Toast.LENGTH_LONG).show();


        // colocamos este texto para indicar que la solicitud esta vacia porque no se ha solicitado ningun vehiculo.
        userRequest.setText("       -- ningún vehículo solicitado --");

        //se llena el select de vehiculos disponibles cuando se entra a la pagina para solicitar un vehiculo..
        //ver la importancia de llenar de primero el select con los vehiculos para que el usuario puedea escoger uno de la lista.
        loadVehicles();


        //colocamos todos los click listeners...

        btnRealizarSolicitd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se realiza la solicitud del vehiculo seleccionado por el usuario..
                cargarWebService();
            }
        });
        btnSelectbeginHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                beginHour = calendar.get(Calendar.HOUR_OF_DAY);
                beginMinutes = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int beginHourOfDay, int minute) {
                        btnSelectbeginHour.setText("Hora inicial:  " + beginHourOfDay + ": " + minute);
                        beginHour = beginHourOfDay;
                        beginMinutes = minute;
                    }
                }, beginHour, beginMinutes, false);

                //mostramos el selector de la hora
                timePickerDialog.show();

            }
        });
        btnSelectEndHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                endHour = calendar.get(Calendar.HOUR_OF_DAY);
                endMinutes = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int beginHourOfDay, int minute) {
                        btnSelectEndHour.setText("Hora final:  " + beginHourOfDay + ": " + minute);
                        endHour = beginHourOfDay;
                        endMinutes = minute;
                    }
                }, endHour, endMinutes, false);

                //mostramos el selector de la hora
                timePickerDialog.show();

            }
        });

        btnSelectEndDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                chooseEndDate();
            }

        });
        btnSelectInitDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                chooseInitDate();
            }

        });

        return vista;
    }

    private void chooseInitDate() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = year + "-" + (month + 1) + "-" + day;
                btnSelectInitDate.setText(selectedDate);
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void chooseEndDate() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = year + "-" + (month + 1) + "-" + day;
                btnSelectEndDate.setText(selectedDate);
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    public String getPlate(String cadena) {
        char[] auxilar = cadena.toCharArray();
        String plate = "";
        for (int i = 0; i < auxilar.length; i++) {
            if (auxilar[i] == ' ') {
                return plate;
            } else {
                plate += auxilar[i];
            }
        }
        return plate;

    }

    private void cargarWebService() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String fecha = simpleDateFormat.format(new Date());


        //String url = "http://192.168.1.9/solicitarVehiculo.php?user=tony"
        if (campoDestino.getText().toString().equals("") | campoJustificacion.getText().toString().equals("") |
                beginHour == -1 | beginMinutes == -1 | endHour == -1 | endMinutes == -1) {

            Toast.makeText(getContext(), "Por favor ingrese todos los datos", Toast.LENGTH_LONG).show();
        } else {

            String url = "http://vtsmsph.com/solicitarVehiculo.php?user=tony"

                    + "&cedula=" + userNameLogin
                    + "&vehicle=" + getPlate(lista.getSelectedItem().toString())
                    + "&destino=" + campoDestino.getText().toString()
                    + "&justificacion=" + campoJustificacion.getText().toString()
                    + "&dateBegin=" + btnSelectInitDate.getText().toString()
                    + "&beginHour=" + beginHour + ":" + beginMinutes + ":00"
                    + "&dateEnd=" + btnSelectEndDate.getText().toString()
                    + "&endHour=" + endHour + ":" + endMinutes + ":00";

            url.replace(" ", "%20");
            idSolicitud = "solicitud"; //Para identificar el tipo de solicitud que se esta haciendo


            //esto nos permite establecer comunicacion con los metodos onErrorResponse() y onResponse().
            ///el metodo JsonArrayRequest nos devuelve un jsonArray
            jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);

            request = Volley.newRequestQueue(getContext());
            //en el metodo add le enviamos la respuesta de la bd, a el metodo response.(se le envia un jsonArray..
            request.add(jsonArrayRequest);
        }


    }

    public void loadVehicles() {

        //  String url = "http://192.168.1.9/loadVehicles.php?" + "user=drocampo";
        String url = "http://vtsmsph.com/loadVehicles.php?" + "user=drocampo";


        //esto hace que permita ingresar los datos con espacios, ejemplo: Didier Jose
        url.replace(" ", "%20");

        //esto nos permite establecer comunicacion con los metodos onErrorResponse() y onResponse().
        ///el metodo JsonArrayRequest nos devuelve un jsonArray
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);

        request = Volley.newRequestQueue(getContext());
        //en el metodo add le enviamos la respuesta de la bd, a el metodo response.(se le envia un jsonArray..
        request.add(jsonArrayRequest);


    }


    @Override
    public void onResponse(JSONArray response) {
        //recibimos un jsonArray por parametros y en un ciclo for
        //Toast.makeText(getContext(),"Detalle del error:  "+response.toString(),Toast.LENGTH_LONG).show();
        try {

            if (idSolicitud.equals("solicitud")) {
                JSONObject jsonObject = new JSONObject();
                jsonObject = response.getJSONObject(0);

                if (jsonObject.getString("Resultado").equals("1")) {
                    Toast.makeText(getContext(), "La solicitud se realizó con éxito!", Toast.LENGTH_LONG).show();

                    userRequest.setText("Usuario: ");
                    vehicleRequest.setText("Vehículo: " + lista.getSelectedItem().toString());
                    targetRequest.setText("Destino: " + campoDestino.getText().toString());

                    if (beginHour < 12) {
                        dateTimerequest.setText("Fecha: " + "26/01/2019  -  " + btnSelectbeginHour.getText().toString() + " am");
                    } else {
                        dateTimerequest.setText("Fecha: " + "26/01/2019  -  " + btnSelectbeginHour.getText().toString() + " pm");
                    }

                } else {
                    if (jsonObject.getString("Resultado").equals("0")) {


                        MensajeOK(
                                "Ya existe un prestamo activo del " +
                                        response.getJSONObject(1).getString("fechaInicio")
                                        + " a las " + response.getJSONObject(2).getString("horaInicio")
                                        + " hasta el " + response.getJSONObject(4).getString("fechaFinal")
                                        + " a las " + response.getJSONObject(3).getString("horaFinal")
                        );

                    } else {
                        Toast.makeText(getContext(), "Error desconocido", Toast.LENGTH_LONG).show();
                    }
                }

                idSolicitud = "consulta";


            } else {
                JSONObject jsonObject = new JSONObject();

                //creamos un vector del tamaño de la cantidad de vehiculos disponibles..
                vehicles = new String[response.length()];
                //recorremos el jsonArray para obtener uno por uno cada jsonObject
                for (int i = 0; i < response.length(); i++) {
                    //una vez que tenemos cada jsonObject
                    jsonObject = response.getJSONObject(i);
                    //procedemos a llenar el vector de strings con el nombre de cada vehiculo
                    vehicles[i] = jsonObject.getString("PK_License_plate") + " " + jsonObject.getString("Brand") + " " + jsonObject.getString("Model");

                }

                //le mandamos el vector con los nombres de los vehiculos, placa y modelo al spinner que tenemos en la vista.
                ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, vehicles);

                lista.setAdapter(adaptador);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void MensajeOK(String msg) {
        View v1 = getActivity().getWindow().getDecorView().getRootView();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(v1.getContext());
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Detalle del error:  " + error.toString(), Toast.LENGTH_LONG).show();
        System.out.println("Detalle del error:  " + error.toString());
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
