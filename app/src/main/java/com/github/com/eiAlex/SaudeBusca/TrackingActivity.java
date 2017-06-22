package com.github.com.eiAlex.SaudeBusca;

import android.app.MediaRouteButton;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.com.eiAlex.SaudeBusca.domain.MessageEB;
import com.github.com.eiAlex.SaudeBusca.domain.UBS;
import com.github.com.eiAlex.SaudeBusca.network.HttpConnection;
import com.github.com.eiAlex.SaudeBusca.service.JobSchedulerService;
import de.greenrobot.event.EventBus;
import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;

import static com.github.com.eiAlex.SaudeBusca.R.layout.activity_tracking;

//import static com.google.android.gms.internal.a.R;

public class TrackingActivity extends ActionBarActivity  {
    public static final String PREF_KEY = "pref_key";
    public static final String LATITUDE_KEY = "latitude_key";
    public static final String LONGITUDE_KEY = "longitude_key";
    public static final String ALTITUDE_KEY = "altitude_key";

    private SupportMapFragment mapFrag;
    private GoogleMap map;
    private Marker marker;
    private View popup = null;
    private LatLng BRASIL = new LatLng(-13.08, -51.95);
    protected TextView tvFeedbackPlaces;
    protected EditText etDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_tracking);

        Log.i("LOG", "TrackingActivity.onCreate()");

        EventBus.getDefault().register(this);

        tvFeedbackPlaces = (TextView) findViewById(R.id.tv_feedback_places);
        etDistance = (EditText) findViewById(R.id.et_distance);
        MyAsyncTaskPlaces.setWeakReference(this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_maps);
        configMap();

      /* // imageButton.setImageResource(R.drawable.ic_youtube_searched_for_black_24dp);
      //  imageButton.F
        ImageButton imageButton =  new (ImageButton); imageButton.setImageResource();
*/
        //Custom POPUP
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {




                if (popup == null) {
                    popup = getWindow().getLayoutInflater().inflate(R.layout.popupmaps, null);
                }
                TextView tv = (TextView) popup.findViewById(R.id.txtTitulo);
                TextView tv1 = (TextView) popup.findViewById(R.id.txtDescricao);
                ImageView iv = (ImageView) popup.findViewById(R.id.icon);
                iv.setImageResource(R.drawable.logo_95);
                tv.setText(marker.getTitle());
                tv = (TextView) popup.findViewById(R.id.txtTitleNumero);
                tv1 = (TextView) popup.findViewById(R.id.txtDescricao);

                RatingBar rb1 = (RatingBar) popup.findViewById(R.id.rtbEstrutura);
                RatingBar rb2 = (RatingBar) popup.findViewById(R.id.rtbAcessodef);
                RatingBar rb3 = (RatingBar) popup.findViewById(R.id.rtbEquipamentos);
                RatingBar rb4 = (RatingBar) popup.findViewById(R.id.rtbMedicamentos);

                String[] textoSeparado = marker.getSnippet().split(";|;\\s");
                Log.i("LOG", " SPLIT STRING: " + Arrays.deepToString(textoSeparado));


                tv.setText(textoSeparado[0]);
                rb1.setRating(Float.parseFloat(textoSeparado[1]));
                rb2.setRating(Float.parseFloat(textoSeparado[2]));
                rb3.setRating(Float.parseFloat(textoSeparado[3]));
                rb4.setRating(Float.parseFloat(textoSeparado[4]));
                if (5 < textoSeparado.length) {
                    tv1.setText(textoSeparado[5]);
                } else {
                    Log.i("LOG", " Index Array: " + textoSeparado.length);
                    tv1.setText(" ");
                }

                //click marquer
                //test add pop
                showCallButton(true,textoSeparado[0]);

                return popup;

            }
        });







    }


    public void configMap() {
        map = mapFrag.getMap();

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton2);
        toggle.getBackground().setAlpha(200);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }
        });




       /* double latitude = Double.parseDouble(TrackingActivity.getInSharedPreferences(this, LATITUDE_KEY, "-13.08"));
        double longitude = Double.parseDouble(TrackingActivity.getInSharedPreferences(this, LONGITUDE_KEY, "-51.95"));
        LatLng latLng = new LatLng(latitude, longitude);*/

        CameraPosition cameraPosition = new CameraPosition.Builder().target(BRASIL).zoom(3).tilt(90).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.moveCamera(update);
        //zoom
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setMyLocationEnabled(true);
        //customAddMarker(BRASIL, "Minha Localização", ";5;5;5;5; Busque Pela Unidade Básica de Saúde mais proxima de Você!");
    }

    public void customAddMarker(LatLng latLng, String title, String snippet) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng).title(title).snippet(snippet).draggable(false);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_black_24dp));

        marker = map.addMarker(options);
    }


    private void updatePosition(LatLng latLng) {
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        marker.setPosition(latLng);
    }


    // METHODS AUX PLACES
    public void clearMap() {

        double latitude = Double.parseDouble(TrackingActivity.getInSharedPreferences(this, LATITUDE_KEY, "-13.08"));
        double longitude = Double.parseDouble(TrackingActivity.getInSharedPreferences(this, LONGITUDE_KEY, "-51.95"));
        LatLng latLng = new LatLng(latitude, longitude);

        map.clear();
        customAddMarker(latLng, "Minha Localização", ";5;5;5;5; Busque Pela Unidade Básica de Saúde mais proxima de Você!");

        showCallButton(false,"00");


    }

    private void addMarkerPlace(LatLng latLng, String title, String snippet, int category) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng)
                .title(title)
                .snippet(snippet)
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromResource(UBS.getCategorySource(category)));

        map.addMarker(options);
        showCallButton(false,"00");
    }


    public void addClosestPlaces(List<UBS> list) {
        for (UBS p : list) {
            addMarkerPlace(new LatLng(p.getLatitude(), p.getLongitude()),
                    p.getName(),
                    p.getPhone() +
                            ";" + p.getAmbience() +
                            ";" + p.getEquipments() +
                            ";" + p.getMedicines() +
                            ";" + p.getElderly() +
                            ";" + p.getDescription(),
                    p.getCategory());
        }
    }

    public void showCallButton(boolean on , final String number){
        ImageButton bt = (ImageButton) getWindow().findViewById(R.id.imageButtonCall);
        final String uri = "tel:" + number;

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent call =new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse(uri));
                startActivity(call);
            }
        });

        if(on == true){


            bt.setVisibility(View.VISIBLE);

        }else {
            bt.setVisibility(View.INVISIBLE);
        }

    }

    // LISTENERS
    public void startTracking() {

        Log.i("LOG", " Start Traking ON ! ") ;
        ComponentName cp = new ComponentName(this, JobSchedulerService.class);

        JobInfo jb = new JobInfo.Builder(1, cp)
                .setBackoffCriteria(4000, JobInfo.BACKOFF_POLICY_LINEAR)
                .setPersisted(true)
                .setPeriodic(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();

        JobScheduler js = JobScheduler.getInstance(this);
        js.schedule(jb);
    }

    public void stopTracking(View view) {
        JobScheduler js = JobScheduler.getInstance(this);
        js.cancelAll();

        Log.i("LOG", " Start Traking OFF ! ") ;
    }

    public void onEvent(MessageEB m) {
        if (m.getClassName().equalsIgnoreCase(TrackingActivity.class.getName())) {
            LatLng latLng = new LatLng(m.getLocation().getLatitude(), m.getLocation().getLongitude());
            updatePosition(latLng);
        }
    }

    public void getClosestPlaces(View view) {
        startTracking();

        try {
            Double.parseDouble(etDistance.getText().toString());

            new MyAsyncTaskPlaces(this).execute();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Entre com a distância máxima", Toast.LENGTH_SHORT).show();

            showCallButton(false,"00");

        }
    }


    // SHARED PREFERENCES
    public static void saveInSharedPreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getInSharedPreferences(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String auxValue = sharedPreferences.getString(key, defaultValue);
        return (auxValue);
    }




    // INNER CLASS
    private static class MyAsyncTaskPlaces extends AsyncTask<Void, Void, List<UBS>> {
        private static WeakReference<TrackingActivity> weakReference;

        public MyAsyncTaskPlaces(TrackingActivity ta) {
            weakReference = new WeakReference<>(ta);
        }

        public static void setWeakReference(TrackingActivity ta) {
            weakReference = new WeakReference<>(ta);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            if (weakReference.get() != null) {
                weakReference.get().tvFeedbackPlaces.setVisibility(View.VISIBLE);
                weakReference.get().tvFeedbackPlaces.setText("Buscando UBS...");
            }
        }

        @Override
        protected List<UBS> doInBackground(Void... params) {
            List<UBS> list = new ArrayList<>();
            SystemClock.sleep(3000);

            if (weakReference.get() != null) {
                double distance;
                distance = Double.parseDouble(weakReference.get().etDistance.getText().toString());

                double latitude = Double.parseDouble(TrackingActivity.getInSharedPreferences(weakReference.get(),weakReference.get().LATITUDE_KEY, "-13.08"));
                double longitude = Double.parseDouble(TrackingActivity.getInSharedPreferences(weakReference.get(),weakReference.get().LONGITUDE_KEY, "-51.95"));


                LatLng latLng = new LatLng(latitude, longitude);

                String answer = HttpConnection.getSetDataWeb("https://saudebusca.000webhostapp.com/location-api/package/ctrl/CtrlMap.php",
                        "get-places-closest",
                        latLng,
                        distance);

                Log.i("LOG", " ANSWER: " + answer);
                try {
                    JSONObject json = new JSONObject(answer);

                    if (!json.isNull("places")) {
                        JSONArray ja = json.getJSONArray("places");

                        for (int i = 0, tamI = ja.length(); i < tamI; i++) {
                            JSONObject jo = null;

                            jo = ja.getJSONObject(i);

                            UBS p = new UBS();
                            p.setName(jo.getString("name"));
                            p.setDescription(jo.getString("description"));
                            p.setCategory(jo.getJSONObject("category").getInt("item"));
                            p.setLatitude(jo.getDouble("latitude"));
                            p.setLongitude(jo.getDouble("longitude"));
                            p.setAmbience(jo.getString("ambience"));
                            p.setElderly(jo.getString("elderly"));
                            p.setEquipments(jo.getString("equipments"));
                            p.setMedicines(jo.getString("medicines"));
                            p.setPhone(jo.getString("phone"));

                            list.add(p);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return (list);
        }

        @Override
        protected void onPostExecute(List<UBS> list) {
            super.onPostExecute(list);
            if (weakReference.get() != null) {
                weakReference.get().tvFeedbackPlaces.setVisibility(View.GONE);
                weakReference.get().clearMap();
                weakReference.get().addClosestPlaces(list);
            }
        }
    }





}
