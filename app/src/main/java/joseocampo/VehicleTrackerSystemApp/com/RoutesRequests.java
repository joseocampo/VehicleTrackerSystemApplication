package joseocampo.VehicleTrackerSystemApp.com;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Constraints;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RoutesRequests extends AppCompatActivity
        implements Response.ErrorListener, Response.Listener<JSONArray> {
    private LinearLayout layoutCards;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest;
    private ArrayList<Loan> myLoans;
    private String userNameLogin;
    private List<ExpandObjects> loansItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_requests);
        //colocamos titulo y color al actionbar.

        getSupportActionBar().setBackgroundDrawable(
                new BitmapDrawable(BitmapFactory.decodeResource(getResources(),R.drawable.fondos)));
        getSupportActionBar().setTitle("Prestamos Activos");


        //obtenemos el nombre del usuario logeado.
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userNameLogin = bundle.getString("usuario");
            Toast.makeText(getApplicationContext(), "Usuario: " + userNameLogin, Toast.LENGTH_SHORT).show();
        }


        layoutCards = (LinearLayout) findViewById(R.id.layoutCards);
        myLoans = new ArrayList<>();
        loansItemView = new ArrayList<ExpandObjects>();

        //cargamos los prestamos desde la base de datos..
        readLoans();
    }

    public void readLoans() {

        //en este metodo nos conectamos a la base de datos y cargamos todos los prestamos
        //del usuario logeado y el vehiculo seleccionado.

        String url = "http://vtsmsph.com/loadCarLoans.php?user=david" + "&ident=" + userNameLogin;
        url.replace(" ", "%20");

        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onResponse(JSONArray response) {

        //llenamos la lista de prestamos, para su posterior uso.
        createLoans(response);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //sino podemos leer los prestamos de la base de datos, mostramos un mensaje de error.
        Toast.makeText(getApplicationContext(), "NOT LOANS: " + error.toString(), Toast.LENGTH_SHORT).show();

    }

    public void createLoans(JSONArray pmyLoans) {
        //llenamos una lista con todos los prestmos leidos de la bd.
        for (int i = 0; i < pmyLoans.length(); i++) {
            try {

                JSONObject jsonObject = pmyLoans.getJSONObject(i);
                Loan loan = new Loan();
                loan.setConsecutive(jsonObject.getInt("consecutivo"));
                loan.setDate(jsonObject.getString("PK_Date"));
                loan.setJustification(jsonObject.getString("Justification"));
                loan.setDetails(jsonObject.getString("Details"));
                loan.setUser(jsonObject.getString("name") + " " +
                        jsonObject.getString("surname")
                        + " " + jsonObject.getString("second_surname"));
                loan.setVehicle(jsonObject.getString("FK_Vehicle"));
                loan.setBeginHour(jsonObject.getString("beginHour"));
                loan.setEndHour(jsonObject.getString("endHour"));

                loansItemView.add(new ExpandObjects(loan.toString(), "", R.drawable.route, R.drawable.inicio));
                // myLoans.add(loan);

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(getApplicationContext(), "Prestamos: " + myLoans.size(), Toast.LENGTH_SHORT).show();

        //llamamos el metodo que forma la vista de cards con los prestamos.
        //LlenarListaObjetos();
        fillListView();
        addEventListeners();
    }


    public void showLoans() {
        //creamos los parametros de configuracion para cada cardView
        LinearLayout.LayoutParams layoutParamsCards =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        //colocamos un margin a cada cardView
        layoutParamsCards.setMargins(10, 7, 10, 7);

        //creamos los parametros de configuracion para cada TextView dentro de la cardView.
        LinearLayout.LayoutParams layoutParamstextView =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);


        //este bucle agrega un cardView que representa un prestamos
        //si leemos 10 prestamos activos desd ela base de datos, entonces
        //este ciclo crea 10 cardView uno para cada prestamos.
        for (int i = 0; i < myLoans.size(); i++) {
            // Toast.makeText(getContext(),myLoans.get(i).toString()+"\n",Toast.LENGTH_SHORT).show();
            CardView cardView = new CardView(getApplicationContext());
            cardView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            cardView.setLayoutParams(layoutParamsCards);

            LinearLayout linearLayoutComponents = new LinearLayout(getApplicationContext());
            linearLayoutComponents.setLayoutParams(layoutParamsCards);
            linearLayoutComponents.setOrientation(LinearLayout.VERTICAL);


            TextView textView = new TextView(getApplicationContext());
            textView.setLayoutParams(layoutParamstextView);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(5, 5, 5, 5);

            textView.setText(myLoans.get(i).toString());


            Button button = new Button(getApplicationContext());
            button.setText("Iniciar Recorrido para el prestamo # " + myLoans.get(i).getConsecutive());
            button.setBackgroundColor(Color.parseColor("#F5F5F5"));
            button.setTextColor(Color.BLACK);
            button.setOnClickListener(new ButtonsOnClickListener(getApplicationContext()));


            linearLayoutComponents.addView(textView);
            linearLayoutComponents.addView(button);

            cardView.addView(linearLayoutComponents);
            layoutCards.addView(cardView);

        }
    }


    private void fillObjectsList() {
        for (int i = 0; i < myLoans.size(); i++) {
            loansItemView.add(new ExpandObjects(myLoans.get(i).toString(), "A1-02", R.drawable.ruta, R.drawable.iniciar));
        }
        Mensaje("Objetos: " + loansItemView.size());
    }

    private void fillListView() {
        ArrayAdapter<ExpandObjects> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.loans_list_view);
        list.setAdapter(adapter);
    }

    private void addEventListeners() {
        ListView list = (ListView) findViewById(R.id.loans_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {
                ExpandObjects ObjEscogido = loansItemView.get(position);

                Mensaje("Seleccione el ícono de la derecha para iniciar el recorrido!");
            }
        });
    }

    public void Mensaje(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private class MyListAdapter extends ArrayAdapter<ExpandObjects> {
        public MyListAdapter() {
            super(RoutesRequests.this, R.layout.expandingobjects, loansItemView);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.expandingobjects, parent, false);
            }
            ExpandObjects ObjetoActual = loansItemView.get(position);
            // Fill the view
            ImageView imageView = (ImageView) itemView.findViewById(R.id.ivdibujo);
            imageView.setImageResource(ObjetoActual.getNumDibujo1());
            TextView elatributo01 = (TextView) itemView.findViewById(R.id.paraelatributo01);
            elatributo01.setText(ObjetoActual.getAtributo01());

            ImageView imageView2 = (ImageView) itemView.findViewById(R.id.ivdibujo2);
            imageView2.setImageResource(ObjetoActual.getNumDibujo2());
            imageView2.setOnClickListener(new ButtonsOnClickListener(getContext()));

            return itemView;
        }
    }

    class ButtonsOnClickListener implements View.OnClickListener {
        Context context;

        public ButtonsOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            ImageView imageView = (ImageView) v;
            ConstraintLayout constraintLayout = (ConstraintLayout) imageView.getParent();
            TextView textView = (TextView) constraintLayout.getChildAt(1);

            int loanNumber = Integer.parseInt(loanNumber(textView.getText().toString()));

            //este intent nos lleva a la vista donde se muestra el mapa para iniciar el recorrido.
            Intent intent = new Intent(getApplicationContext(), LocationView.class);
            intent.putExtra("usuario", userNameLogin);
            intent.putExtra("loanNumber", loanNumber);
            startActivity(intent);
        }

        public String loanNumber(String cadena) {
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

    }

}
