package cursoandroid.localizandousuario.com;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION};

    private LocationManager locationManager; // essa classe que vai permitir a localizacao do usuario
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Validar as permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Objeto responsável por gerenciar a localizacao do usuário
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {// Quando a localizacao do usuario muda
                Log.d("Localizacao", "onLocationChanged :" + locationListener);

                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                /*
                mMap.clear();
                LatLng localUsuario = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(localUsuario).title("Meu local"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localUsuario, 15));
                */
                /*
                Geocoding -> processo de transformar um endereço
                ou descrição de um local em latitude/longitude
                Reverse Geocoding -> processo de transformar latitude/longitude
                em um endereço
                 */
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {

                    // List<Address> listaEndereco = geocoder.getFromLocation(latitude, longitude,1);
                    String stringEndereco = "Rua Flôr da Verdade, 48 - Vila Verde, São Paulo - SP";
                    List<Address> listaEndereco = geocoder.getFromLocationName(stringEndereco, 1);
                    if (listaEndereco != null && listaEndereco.size() > 0) {
                        Address endereco = listaEndereco.get(0);

                            /*Address[addressLines=[0:"Rua do Cardo Limão, 33 - Vila Verde, São Paulo - SP, 08230-680, Brasil"],
                            feature=33,
                            admin=São Paulo,
                            sub-admin=São Paulo,
                            locality=null,
                            thoroughfare=Rua do Cardo Limão,
                            postalCode=08230-680,
                            countryCode=BR,
                            countryName=Brasil,
                            hasLatitude=true,
                            latitude=-23.5172979,
                            hasLongitude=true,
                            longitude=-46.4505516,
                            phone=null,
                            url=null,extras=null]
                            endereco.getCountryCode();//Recuperar o país por exemplo
                             */


                        Double lat = endereco.getLatitude();
                        Double lon = endereco.getLongitude();

                        mMap.clear();
                        LatLng localUsuario = new LatLng(lat, lon);
                        mMap.addMarker(new MarkerOptions().position(localUsuario).title("Meu local"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localUsuario, 18));

                        Log.d("local ", "onLocationChanged: " + endereco.toString());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { // Quando o status do serviço de localizacao muda

            }

            @Override
            public void onProviderEnabled(String provider) {// Quando o usuario habilita o servico de localizacao

            }

            @Override
            public void onProviderDisabled(String provider) {// Quando o usuario Desabilita o servico de localizacao

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(// Esse metodo que vai recuperar a loalizacao do usuario
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListener

            );
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissoesResultado : grantResults) {
            if (permissoesResultado == PackageManager.PERMISSION_DENIED) {
                //Alerta
                alertaValidacaoPermissao();
            } else if (permissoesResultado == PackageManager.PERMISSION_GRANTED) {
                //Recuperar localizacao usuario
                /*
                 * 1) Provedor da localização
                 * 2) Tempo mínimo entre atualizações de localização(milesegundos)
                 * 3) Distancia mínima entre atualizações de localização(metros)
                 * 4) Location listener (para recebermos as atualizações
                 */

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(// Esse metodo que vai recuperar a loalizacao do usuario
                            LocationManager.GPS_PROVIDER,
                            0,
                            0,
                            locationListener

                    );
                }
            }
        }
    }

    private void alertaValidacaoPermissao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões! ");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.dismiss();
    }
}
