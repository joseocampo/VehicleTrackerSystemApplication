package joseocampo.VehicleTrackerSystemApp.com;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
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
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentRoutesRequest.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentRoutesRequest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRoutesRequest extends Fragment
        implements Response.ErrorListener, Response.Listener<JSONArray> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout layoutCards;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest;
    private ArrayList<Loan> loans;
    private String userNameLogin;

    private OnFragmentInteractionListener mListener;

    public FragmentRoutesRequest() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRoutesRequest.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRoutesRequest newInstance(String param1, String param2) {
        FragmentRoutesRequest fragment = new FragmentRoutesRequest();
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


        //obtenemos el nombre del usuario logeado.
        userNameLogin = getArguments().getString("usuario");
        Toast.makeText(getContext(), "Usuario: " + userNameLogin, Toast.LENGTH_LONG).show();


        // Inflate the layout for this fragment
        View view =
                inflater.inflate(R.layout.fragment_fragment_routes_request, container, false);
        layoutCards = (LinearLayout) view.findViewById(R.id.layoutCards);
        loans = new ArrayList<>();


        //este metodo carga los prestamos desde la bd y los guarda en una estructura de dtos tipo ArrayList.
        loadLoans();


        return view;
    }

    public void loadLoans() {

        String url = "http://vtsmsph.com/loadCarLoans.php?user=david" + "&ident=" + userNameLogin;

        url.replace(" ", "%20");

        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);

        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonArrayRequest);
    }


    @Override
    public void onResponse(JSONArray response) {
        createLoans(response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();

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
                Toast.makeText(getContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        Toast.makeText(getContext(), "Prestamos: " + loans.size(), Toast.LENGTH_SHORT).show();
        //llamamos el metodo que forma la vista de cards con los prestamos.
        showLoans();
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
            CardView cardView = new CardView(getContext());
            cardView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            cardView.setLayoutParams(layoutParamsCards);

            LinearLayout linearLayoutComponents = new LinearLayout(getContext());
            linearLayoutComponents.setLayoutParams(layoutParamsCards);
            linearLayoutComponents.setOrientation(LinearLayout.VERTICAL);


            TextView textView = new TextView(getContext());
            textView.setLayoutParams(layoutParamstextView);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(5, 5, 5, 5);

            textView.setText(loans.get(i).toString());


            Button button = new Button(getContext());
            button.setText("Iniciar Recorrido para el prestamo # " + loans.get(i).getConsecutive());
            button.setBackgroundColor(Color.parseColor("#F5F5F5"));
            button.setTextColor(Color.BLACK);
            button.setOnClickListener(new ButtonsOnClickListener(getContext()));


            linearLayoutComponents.addView(textView);
            linearLayoutComponents.addView(button);

            cardView.addView(linearLayoutComponents);
            layoutCards.addView(cardView);

        }
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





    class ButtonsOnClickListener implements View.OnClickListener {
        Context context;

        public ButtonsOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Button b = (Button) v;
//            Toast.makeText(this.context,b.getText(),Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), LocationView.class);
            StringBuilder r = new StringBuilder(getLoanNumber(b.getText().toString()));
            intent.putExtra("loanNumber", r.reverse().toString());
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
