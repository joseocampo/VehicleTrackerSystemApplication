package joseocampo.VehicleTrackerSystemApp.com;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private ArrayList<Loan> loans;
    private String userNameLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_requests);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#263238")));
        getSupportActionBar().setTitle("Prestamos Activos");


        //obtenemos el nombre del usuario logeado.
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            userNameLogin = bundle.getString("usuario");
             Toast.makeText(getApplicationContext(), "Usuario: " + userNameLogin, Toast.LENGTH_LONG).show();
        }




        layoutCards = (LinearLayout) findViewById(R.id.layoutCards);
        loans = new ArrayList<>();
        loadLoans();
       // LlenarListaObjetos();
//        LlenarListView();
//        RegistrarClicks();
    }

    public void loadLoans() {

        String url = "http://vtsmsph.com/loadCarLoans.php?user=david" + "&ident=" + userNameLogin;

        url.replace(" ", "%20");

        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onResponse(JSONArray response) {
//        Mensaje("Total: "+loans.size());
        createLoans(response);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

    }

    public void createLoans(JSONArray pLoans) {
        for (int i = 0; i < pLoans.length(); i++) {
            try {

                JSONObject jsonObject = pLoans.getJSONObject(i);
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


                loans.add(loan);

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(getApplicationContext(), "Prestamos: " + loans.size(), Toast.LENGTH_SHORT).show();
        //llamamos el metodo que forma la vista de cards con los prestamos.
       // showLoans();
        LlenarListaObjetos();
        LlenarListView();
        RegistrarClicks();
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
        for (int i = 0; i < loans.size(); i++) {
            // Toast.makeText(getContext(),loans.get(i).toString()+"\n",Toast.LENGTH_SHORT).show();
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

            textView.setText(loans.get(i).toString());


            Button button = new Button(getApplicationContext());
            button.setText("Iniciar Recorrido para el prestamo # " + loans.get(i).getConsecutive());
            button.setBackgroundColor(Color.parseColor("#F5F5F5"));
            button.setTextColor(Color.BLACK);
            button.setOnClickListener(new ButtonsOnClickListener(getApplicationContext()));


            linearLayoutComponents.addView(textView);
            linearLayoutComponents.addView(button);

            cardView.addView(linearLayoutComponents);
            layoutCards.addView(cardView);

        }
    }

    private List<ExpandObjects> misObjetos = new ArrayList<ExpandObjects>();

    private void LlenarListaObjetos() {
        for (int i = 0; i < loans.size(); i++) {
//            misObjetos.add(new ExpandObjects("Destino: La Aurora, Heredia.\n\n" + "Usuario: Mauricio González\n" + "Vehículo: AD231\n" + "Hora Inicio: 10:30am\n" + "Hora Final: 2:00pm", "A1-02", R.drawable.prestamo, R.drawable.iniciar));
            misObjetos.add(new ExpandObjects(loans.get(i).toString(), "A1-02", R.drawable.ruta, R.drawable.iniciar));
        }
        Mensaje("Objetos: "+misObjetos.size());
    }

    private void LlenarListView() {
        ArrayAdapter<ExpandObjects> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.loans_list_view);
        list.setAdapter(adapter);
    }

    private void RegistrarClicks() {
        ListView list = (ListView) findViewById(R.id.loans_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {
                ExpandObjects ObjEscogido = misObjetos.get(position);

                Mensaje("Seleccione el ícono de la derecha para iniciar el recorrido!");
            }
        });
    }

    public void Mensaje(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    ;

    private class MyListAdapter extends ArrayAdapter<ExpandObjects> {
        public MyListAdapter() {
            super(RoutesRequests.this, R.layout.expandingobjects, misObjetos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.expandingobjects, parent, false);
            }
            ExpandObjects ObjetoActual = misObjetos.get(position);
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
            Button b = (Button) v;
//            Toast.makeText(this.context,b.getText(),Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), LocationView.class);

            intent.putExtra("usuario", userNameLogin);
            startActivity(intent);
        }

        public String getLoanNumber(String cadena) {
            char[] auxilar = cadena.toCharArray();
            String plate = "";
            for (int i = (auxilar.length - 1); i > 0; i--) {
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
