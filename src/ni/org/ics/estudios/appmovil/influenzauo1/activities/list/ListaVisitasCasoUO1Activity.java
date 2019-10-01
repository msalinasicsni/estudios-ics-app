package ni.org.ics.estudios.appmovil.influenzauo1.activities.list;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import ni.org.ics.estudios.appmovil.AbstractAsyncListActivity;
import ni.org.ics.estudios.appmovil.MainActivity;
import ni.org.ics.estudios.appmovil.MyIcsApplication;
import ni.org.ics.estudios.appmovil.R;
import ni.org.ics.estudios.appmovil.catalogs.MessageResource;
import ni.org.ics.estudios.appmovil.cohortefamilia.activities.MenuVisitaSeguimientoCasoActivity;
import ni.org.ics.estudios.appmovil.database.EstudiosAdapter;
import ni.org.ics.estudios.appmovil.domain.influenzauo1.ParticipanteCasoUO1;
import ni.org.ics.estudios.appmovil.domain.influenzauo1.VisitaCasoUO1;
import ni.org.ics.estudios.appmovil.influenzauo1.activities.MenuVisitaCasoUO1Activity;
import ni.org.ics.estudios.appmovil.influenzauo1.activities.enterdata.NuevaVisitaCasoUO1Activity;
import ni.org.ics.estudios.appmovil.influenzauo1.adapters.VisitasCasoUO1Adapter;
import ni.org.ics.estudios.appmovil.utils.*;

import java.util.ArrayList;
import java.util.List;

public class ListaVisitasCasoUO1Activity extends AbstractAsyncListActivity {
	
	private TextView textView;
	private Drawable img = null;
	private Button mButton;
	private Button mAddVisitButton;
	private Button mFailVisitsButton;
	private static ParticipanteCasoUO1 partCaso = new ParticipanteCasoUO1();
    private VisitaCasoUO1 visitaCasoUO1 = new VisitaCasoUO1();
	private ArrayAdapter<VisitaCasoUO1> mVisitaFallidaCasoAdapter;
	private List<VisitaCasoUO1> mVisitasCasos = new ArrayList<VisitaCasoUO1>();
	private List<MessageResource> mRazonNoVisita = new ArrayList<MessageResource>();
	private EstudiosAdapter estudiosAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_add_uo1);
		
		partCaso = (ParticipanteCasoUO1) getIntent().getExtras().getSerializable(Constants.PARTICIPANTE);
		textView = (TextView) findViewById(R.id.label);
		img=getResources().getDrawable(R.drawable.ic_menu_today);
		textView.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
		String mPass = ((MyIcsApplication) this.getApplication()).getPassApp();
		estudiosAdapter = new EstudiosAdapter(this.getApplicationContext(),mPass,false,false);
		new FetchDataVisitasCasosTask().execute(partCaso.getCodigoCasoParticipante());

		mButton = (Button) findViewById(R.id.new_rojo_uo1_button);
		mButton.setVisibility(View.GONE);
		mButton = (Button) findViewById(R.id.new_pbmc_uo1_button);
		mButton.setVisibility(View.GONE);
		mButton = (Button) findViewById(R.id.add_symptom_uo1_button);
		mButton.setVisibility(View.GONE);

        mAddVisitButton = (Button) findViewById(R.id.add_visit_uo1_button);
		mAddVisitButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_menu_btn_add), null, null);
		mAddVisitButton.setOnClickListener(new View.OnClickListener()  {
			@Override
			public void onClick(View v) {
				Bundle arguments = new Bundle();
				arguments.putSerializable(Constants.PARTICIPANTE , partCaso);
				Intent i = new Intent(getApplicationContext(),
						NuevaVisitaCasoUO1Activity.class);
				i.putExtras(arguments);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.general, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.MENU_BACK:
			finish();
			return true;
		case R.id.MENU_HOME:
			i = new Intent(getApplicationContext(),
					MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position,
			long id) {
        visitaCasoUO1 = (VisitaCasoUO1)this.getListAdapter().getItem(position);
        if (visitaCasoUO1.getVisitaExitosa().equalsIgnoreCase(Constants.YESKEYSND)) {
			// Opcion de menu seleccionada
			Bundle arguments = new Bundle();
			Intent i;
			arguments.putSerializable(Constants.VISITA, visitaCasoUO1);
			i = new Intent(getApplicationContext(),
					MenuVisitaCasoUO1Activity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtras(arguments);
			startActivity(i);
			finish();
		}else{
			Toast toast = Toast.makeText(getApplicationContext(),getString(R.string.visit_not_suscesfull),Toast.LENGTH_LONG);
			toast.show();
		}
	}
	
	@Override
	public void onBackPressed (){
		Bundle arguments = new Bundle();
		Intent i = new Intent(getApplicationContext(),
				ListaParticipantesCasosUO1Activity.class);
		i.putExtras(arguments);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	private class FetchDataVisitasCasosTask extends AsyncTask<String, Void, String> {
		private String codigoCaso = null;
		@Override
		protected void onPreExecute() {
			// before the request begins, show a progress indicator
			showLoadingProgressDialog();
		}

		@Override
		protected String doInBackground(String... values) {
			codigoCaso = values[0];
			try {
				estudiosAdapter.open();
				mVisitasCasos = estudiosAdapter.getVisitasCasosUO1(InfluenzaUO1DBConstants.participanteCasoUO1 +" = '" + codigoCaso +"'", MainDBConstants.fechaVisita);
				mRazonNoVisita = estudiosAdapter.getMessageResources(CatalogosDBConstants.catRoot + "='CHF_CAT_VISITA_NO_P' or " +CatalogosDBConstants.catRoot + "='CHF_CAT_VISITA_NO_C'" , null);
				estudiosAdapter.close();
			} catch (Exception e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				return "error";
			}
			return "exito";
		}

		protected void onPostExecute(String resultado) {
			// after the request completes, hide the progress indicator
			textView.setText("");
			textView.setTextColor(Color.BLACK);
			textView.setText(getString(R.string.main_3) +"\n"+ getString(R.string.list_uo1_visit)+"\n"+ getString(R.string.code)+ " "+ getString(R.string.participant)+ ": "+partCaso.getParticipante().getCodigo());
			mVisitaFallidaCasoAdapter = new VisitasCasoUO1Adapter(getApplication().getApplicationContext(), R.layout.complex_list_item, mVisitasCasos,mRazonNoVisita);
			setListAdapter(mVisitaFallidaCasoAdapter);
			dismissProgressDialog();
		}

	}	

}
