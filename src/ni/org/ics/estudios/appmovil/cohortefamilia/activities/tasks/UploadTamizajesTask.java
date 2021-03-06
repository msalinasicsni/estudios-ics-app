package ni.org.ics.estudios.appmovil.cohortefamilia.activities.tasks;

import java.util.ArrayList;
import java.util.List;


import ni.org.ics.estudios.appmovil.database.EstudiosAdapter;
import ni.org.ics.estudios.appmovil.domain.Tamizaje;
import ni.org.ics.estudios.appmovil.listeners.UploadListener;
import ni.org.ics.estudios.appmovil.utils.Constants;
import ni.org.ics.estudios.appmovil.utils.MainDBConstants;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.util.Log;

public class UploadTamizajesTask extends UploadTask {
	
	private final Context mContext;

	public UploadTamizajesTask(Context context) {
		mContext = context;
	}

	protected static final String TAG = UploadTamizajesTask.class.getSimpleName();
    
	private EstudiosAdapter estudioAdapter = null;
    
    private List<Tamizaje> mTamizajes = new ArrayList<Tamizaje>();
    //private List<Participante> mParticipantes = new ArrayList<Participante>();
    

	private String url = null;
	private String username = null;
	private String password = null;
	private String error = null;
	protected UploadListener mStateListener;

    
    public static final String TAMIZAJE = "1";
    
	

	@Override
	protected String doInBackground(String... values) {
		url = values[0];
		username = values[1];
		password = values[2];

		try {
			publishProgress("Obteniendo registros de la base de datos", "1", "2");
			estudioAdapter = new EstudiosAdapter(mContext, password, false,false);
			estudioAdapter.open();
			String filtro = MainDBConstants.estado + "='" + Constants.STATUS_NOT_SUBMITTED + "'";
			mTamizajes = estudioAdapter.getTamizajes(filtro, null);
            /*Tamizaje tam = new Tamizaje();
            tam.setAceptaAtenderCentro("S");
            tam.setAceptaParticipar("S");
            tam.setAceptaTamizajePersona("S");
            tam.setAsentimientoVerbal("S");
            tam.setCodigo("12341422");
            tam.setCriteriosInclusion("1234");
            tam.setDeviceid("sdfsfs");
            tam.setDondeAsisteProblemasSalud("222");
            tam.setEnfermedad("wwrewr");
            tam.setEsElegible("S");
            tam.setEstado('0');
            tam.setEstudio(estudioAdapter.getEstudio(MainDBConstants.codigo +"=1", null));
            tam.setFechaNacimiento(new Date());
            tam.setOtroCentroSalud("Otro");
            tam.setPasive('0');
            tam.setPuestoSalud("Puesto");
            tam.setRazonNoAceptaParticipar("R");
            tam.setRazonNoAceptaTamizajePersona("R");
            tam.setRecordDate(new Date());
            tam.setRecordUser("admin");
            tam.setSexo("M");
            mTamizajes.add(tam);*/
            
            
            
            
			publishProgress("Datos completos!", "2", "2");
			actualizarBaseDatos(Constants.STATUS_SUBMITTED, TAMIZAJE);
			error = cargarTamizajes();
            if (!error.matches("Datos recibidos!")){
                actualizarBaseDatos(Constants.STATUS_NOT_SUBMITTED, TAMIZAJE);
                return error;
            }
            estudioAdapter.close();
		} catch (Exception e1) {
			estudioAdapter.close();
			e1.printStackTrace();
			return e1.getLocalizedMessage();
		}
		return error;
	}
	
	private void actualizarBaseDatos(String estado, String opcion) {
		int c;
        if(opcion.equalsIgnoreCase(TAMIZAJE)){
            c = mTamizajes.size();
            if(c>0){
                for (Tamizaje tamizaje : mTamizajes) {
                	tamizaje.setEstado(estado.charAt(0));
                    estudioAdapter.editarTamizaje(tamizaje);
                    publishProgress("Actualizando tamizajes en base de datos local", Integer.valueOf(mTamizajes.indexOf(tamizaje)).toString(), Integer
                            .valueOf(c).toString());
                }
            }
        }
	}

    
    /***************************************************/
    /********************* Tamizajes participantes ************************/
    /***************************************************/
    // url, username, password
    protected String cargarTamizajes() throws Exception {
        try {
            if(mTamizajes.size()>0){
                // La URL de la solicitud POST
                publishProgress("Enviando tamizaje de personas cohorte familia!", TAMIZAJE, TAMIZAJE);
                final String urlRequest = url + "/movil/tamizajes";
                Tamizaje[] envio = mTamizajes.toArray(new Tamizaje[mTamizajes.size()]);
                HttpHeaders requestHeaders = new HttpHeaders();
                HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
                requestHeaders.setAuthorization(authHeader);
                HttpEntity<Tamizaje[]> requestEntity =
                        new HttpEntity<Tamizaje[]>(envio, requestHeaders);
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
                // Hace la solicitud a la red, pone los datos y espera un mensaje de respuesta del servidor
                ResponseEntity<String> response = restTemplate.exchange(urlRequest, HttpMethod.POST, requestEntity,
                        String.class);
                return response.getBody();
            }
            else{
                return "Datos recibidos!";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return e.getMessage();
        }
    }

}