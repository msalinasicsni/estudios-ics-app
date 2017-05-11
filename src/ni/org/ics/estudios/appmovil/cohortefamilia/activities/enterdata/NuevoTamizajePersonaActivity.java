package ni.org.ics.estudios.appmovil.cohortefamilia.activities.enterdata;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import ni.org.ics.estudios.appmovil.AbstractAsyncActivity;
import ni.org.ics.estudios.appmovil.MainActivity;
import ni.org.ics.estudios.appmovil.MyIcsApplication;
import ni.org.ics.estudios.appmovil.R;
import ni.org.ics.estudios.appmovil.activities.DataEnterActivity;
import ni.org.ics.estudios.appmovil.catalogs.Estudio;
import ni.org.ics.estudios.appmovil.database.EstudiosAdapter;
import ni.org.ics.estudios.appmovil.domain.Casa;
import ni.org.ics.estudios.appmovil.domain.Tamizaje;
import ni.org.ics.estudios.appmovil.utils.Constants;
import ni.org.ics.estudios.appmovil.utils.DeviceInfo;
import ni.org.ics.estudios.appmovil.utils.FileUtils;
import ni.org.ics.estudios.appmovil.utils.GPSTracker;


public class NuevoTamizajePersonaActivity extends AbstractAsyncActivity {

	protected static final String TAG = NuevoTamizajePersonaActivity.class.getSimpleName();
	public static final int ADD_TAMIZAJE = 1;
	Dialog dialogInit;
	GPSTracker gps;
	DeviceInfo infoMovil;
	private static Casa casa = new Casa();
	private EstudiosAdapter estudiosAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!FileUtils.storageReady()) {
			Toast toast = Toast.makeText(getApplicationContext(),getString(R.string.error, R.string.storage_error),Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
		gps = new GPSTracker(NuevoTamizajePersonaActivity.this);
		infoMovil = new DeviceInfo(NuevoTamizajePersonaActivity.this);
		casa = (Casa) getIntent().getExtras().getSerializable(Constants.CASA);
		createInitDialog();
	}

	/**
	 * Presenta dialogo inicial
	 */

	private void createInitDialog() {
		dialogInit = new Dialog(this, R.style.FullHeightDialog); 
		dialogInit.setContentView(R.layout.yesno); 
		dialogInit.setCancelable(false);

		//to set the message
		TextView message =(TextView) dialogInit.findViewById(R.id.yesnotext);
		message.setText(getString(R.string.add)+ " " + getString(R.string.new_screen));

		//add some action to the buttons

		Button yes = (Button) dialogInit.findViewById(R.id.yesnoYes);
		yes.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialogInit.dismiss();
				addTamizaje();
			}
		});

		Button no = (Button) dialogInit.findViewById(R.id.yesnoNo);
		no.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Cierra
				dialogInit.dismiss();
				finish();
			}
		});
		dialogInit.show();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.general, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.MENU_BACK){
			finish();
			return true;
		}
		else if(item.getItemId()==R.id.MENU_HOME){
			Intent i = new Intent(getApplicationContext(),
					MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			return true;
		}
		else{
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		if(requestCode == ADD_TAMIZAJE) {
	        if(resultCode == RESULT_OK) {
	        	String mPass = ((MyIcsApplication) this.getApplication()).getPassApp();
	    		estudiosAdapter = new EstudiosAdapter(this.getApplicationContext(),mPass,false,false);
	    		estudiosAdapter.open();
	    		Estudio estudio = new Estudio();
	    		estudio.setCodigo(1);
	    		estudio.setNombre("Cohorte Influenza");
	        	Tamizaje tamizaje =  new Tamizaje();
	        	String acepta = intent.getExtras().getString(this.getString(R.string.aceptaTamizaje));
	        	tamizaje.setAceptaTamizaje(acepta.charAt(0));
	        	tamizaje.setEstudio(estudio);
	        	estudiosAdapter.close();
	        }
	        else{
	        	
	        }
	    }
		Intent i = new Intent(getApplicationContext(),
				MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
		super.onActivityResult(requestCode, resultCode, intent);
	}

	/**
	 * 
	 */
	private void addTamizaje() {
		try{
			Intent i = new Intent(getApplicationContext(),
					DataEnterActivity.class);
			i.putExtra(Constants.FORM_NAME, Constants.FORM_NUEVO_TAMIZAJE_PERS);
			startActivityForResult(i , ADD_TAMIZAJE);
		}
		catch(Exception e){
			Log.e(TAG, e.getMessage(), e);
		}
	}

}
