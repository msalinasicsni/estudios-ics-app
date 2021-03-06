package ni.org.ics.estudios.appmovil.cohortefamilia.activities.enterdata;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.List;
import java.util.Map;

import ni.org.ics.estudios.appmovil.MyIcsApplication;
import ni.org.ics.estudios.appmovil.R;
import ni.org.ics.estudios.appmovil.catalogs.Estudio;
import ni.org.ics.estudios.appmovil.catalogs.MessageResource;
import ni.org.ics.estudios.appmovil.cohortefamilia.activities.MenuCasaActivity;
import ni.org.ics.estudios.appmovil.cohortefamilia.forms.PreTamizajeForm;
import ni.org.ics.estudios.appmovil.cohortefamilia.forms.PreTamizajeFormLabels;
import ni.org.ics.estudios.appmovil.database.EstudiosAdapter;
import ni.org.ics.estudios.appmovil.domain.Casa;
import ni.org.ics.estudios.appmovil.domain.VisitaTerreno;
import ni.org.ics.estudios.appmovil.domain.cohortefamilia.CasaCohorteFamilia;
import ni.org.ics.estudios.appmovil.domain.cohortefamilia.PreTamizaje;
import ni.org.ics.estudios.appmovil.preferences.PreferencesActivity;
import ni.org.ics.estudios.appmovil.utils.CatalogosDBConstants;
import ni.org.ics.estudios.appmovil.utils.Constants;
import ni.org.ics.estudios.appmovil.utils.DeviceInfo;
import ni.org.ics.estudios.appmovil.utils.FileUtils;
import ni.org.ics.estudios.appmovil.utils.GPSTracker;
import ni.org.ics.estudios.appmovil.utils.MainDBConstants;
import ni.org.ics.estudios.appmovil.wizard.model.AbstractWizardModel;
import ni.org.ics.estudios.appmovil.wizard.model.BarcodePage;
import ni.org.ics.estudios.appmovil.wizard.model.DatePage;
import ni.org.ics.estudios.appmovil.wizard.model.LabelPage;
import ni.org.ics.estudios.appmovil.wizard.model.ModelCallbacks;
import ni.org.ics.estudios.appmovil.wizard.model.MultipleFixedChoicePage;
import ni.org.ics.estudios.appmovil.wizard.model.NewDatePage;
import ni.org.ics.estudios.appmovil.wizard.model.NumberPage;
import ni.org.ics.estudios.appmovil.wizard.model.Page;
import ni.org.ics.estudios.appmovil.wizard.model.SelectParticipantPage;
import ni.org.ics.estudios.appmovil.wizard.model.SingleFixedChoicePage;
import ni.org.ics.estudios.appmovil.wizard.model.TextPage;
import ni.org.ics.estudios.appmovil.wizard.ui.PageFragmentCallbacks;
import ni.org.ics.estudios.appmovil.wizard.ui.ReviewFragment;
import ni.org.ics.estudios.appmovil.wizard.ui.StepPagerStrip;


public class NuevoTamizajeCasaActivity extends FragmentActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        ModelCallbacks {
	private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private boolean mEditingAfterReview;
    private AbstractWizardModel mWizardModel;
    private boolean mConsumePageSelectedEvent;
    private Button mNextButton;
    private Button mPrevButton;
    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;
    private PreTamizajeFormLabels labels = new PreTamizajeFormLabels();
    private EstudiosAdapter estudiosAdapter;
    private DeviceInfo infoMovil;
    private GPSTracker gps;
    private static Casa casa = new Casa();
	private String username;
	private SharedPreferences settings;
	private static final int EXIT = 1;
	private AlertDialog alertDialog;
	private boolean notificarCambios = true;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!FileUtils.storageReady()) {
			Toast toast = Toast.makeText(getApplicationContext(),getString(R.string.error, R.string.storage_error),Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
        setContentView(R.layout.activity_data_enter);
        settings =
				PreferenceManager.getDefaultSharedPreferences(this);
		username =
				settings.getString(PreferencesActivity.KEY_USERNAME,
						null);
		infoMovil = new DeviceInfo(NuevoTamizajeCasaActivity.this);
		gps = new GPSTracker(NuevoTamizajeCasaActivity.this);
		casa = (Casa) getIntent().getExtras().getSerializable(Constants.CASA);
        String mPass = ((MyIcsApplication) this.getApplication()).getPassApp();
        mWizardModel = new PreTamizajeForm(this,mPass);
        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }
        mWizardModel.registerListener(this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(mPagerAdapter.getCount() - 1, position);
                if (mPager.getCurrentItem() != position) {
                    mPager.setCurrentItem(position);
                }
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return; 
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
                    DialogFragment dg = new DialogFragment() {
                        @Override
                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                            return new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.submit_confirm_message)
                                    .setPositiveButton(R.string.submit_confirm_button, new DialogInterface.OnClickListener() {
                                    	@Override
										public void onClick(DialogInterface arg0, int arg1) {
                                    		saveData();
										}
                                    })
                                    .setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                                    	@Override
										public void onClick(DialogInterface arg0, int arg1) {
                                    		createDialog(EXIT);
										}
                                    })
                                    .create();
                        }
                    };
                    dg.show(getSupportFragmentManager(), "guardar_dialog");
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });
        onPageTreeChanged(); 
    }
    
	@Override
	public void onBackPressed (){
		createDialog(EXIT);
	}
	
	private void createDialog(int dialog) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch(dialog){
		case EXIT:
			builder.setTitle(this.getString(R.string.confirm));
			builder.setMessage(this.getString(R.string.exiting));
			builder.setPositiveButton(this.getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Finish app
					dialog.dismiss();
					finish();
				}
			});
			builder.setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Do nothing
					dialog.dismiss();
				}
			});
			break;		
		default:
			break;
		}
		alertDialog = builder.create();
		alertDialog.show();
	}

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 = review step
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size()) {
            mNextButton.setText(R.string.finish);
            mNextButton.setBackgroundResource(R.drawable.finish_background);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        } else {
            mNextButton.setText(mEditingAfterReview
                    ? R.string.review
                    : R.string.next);
            mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
            mNextButton.setTextAppearance(this, v.resourceId);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }
        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
    	updateModel(page);
    	updateConstrains();
        if (recalculateCutOffPage()) {
        	if (notificarCambios) mPagerAdapter.notifyDataSetChanged();
            updateBottomBar();
        }
        notificarCambios = true;
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            String clase = page.getClass().toString();
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }     
            if (!page.getData().isEmpty() && clase.equals("class ni.org.ics.estudios.appmovil.wizard.model.NumberPage")) {
            	NumberPage np = (NumberPage) page;
            	String valor = np.getData().getString(NumberPage.SIMPLE_DATA_KEY);
        		if((np.ismValRange() && (np.getmGreaterOrEqualsThan() > Integer.valueOf(valor) || np.getmLowerOrEqualsThan() < Integer.valueOf(valor)))
        				|| (np.ismValPattern() && !valor.matches(np.getmPattern()))){
        			cutOffPage = i;
        			break;
        		}
            }
            if (!page.getData().isEmpty() && clase.equals("class ni.org.ics.estudios.appmovil.wizard.model.TextPage")) {
            	TextPage tp = (TextPage) page;
            	if (tp.ismValPattern()) {
            		String valor = tp.getData().getString(TextPage.SIMPLE_DATA_KEY);
            		if(!valor.matches(tp.getmPattern())){
            			cutOffPage = i;
            			break;
            		}
            	}
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }
    
    
    public void updateConstrains(){
        
    }
    
    public void updateModel(Page page){
    	try{
    		boolean visible = false;
    		if (page.getTitle().equals(labels.getVisitaExitosa())) {
                visible = page.getData().getString(TextPage.SIMPLE_DATA_KEY) != null && page.getData().getString(TextPage.SIMPLE_DATA_KEY).matches("Si");
                changeStatus(mWizardModel.findByKey(labels.getAceptaTamizajeCasa()), visible);
                //notificarCambios = false;
                changeStatus(mWizardModel.findByKey(labels.getRazonVisitaNoExitosa()), !visible);
                //notificarCambios = false;
                onPageTreeChanged();
            }
            if (page.getTitle().equals(labels.getRazonVisitaNoExitosa())) {
                visible = page.getData().getString(TextPage.SIMPLE_DATA_KEY) != null && page.getData().getString(TextPage.SIMPLE_DATA_KEY).matches(Constants.OTRO);
                changeStatus(mWizardModel.findByKey(labels.getOtraRazonVisitaNoExitosa()), visible);
                //notificarCambios = false;
                onPageTreeChanged();
            }
    		if (page.getTitle().equals(labels.getAceptaTamizajeCasa())) {
                visible = page.getData().getString(TextPage.SIMPLE_DATA_KEY) != null && page.getData().getString(TextPage.SIMPLE_DATA_KEY).matches("Si");
                changeStatus(mWizardModel.findByKey(labels.getCodigoCHF()), visible);
                //notificarCambios = false;
                changeStatus(mWizardModel.findByKey(labels.getMismoJefe()), visible);
                //notificarCambios = false;
                changeStatus(mWizardModel.findByKey(labels.getFinTamizajeLabel()), visible);
                //notificarCambios = false;
                visible = page.getData().getString(TextPage.SIMPLE_DATA_KEY) != null && page.getData().getString(TextPage.SIMPLE_DATA_KEY).matches("No");
                changeStatus(mWizardModel.findByKey(labels.getRazonNoAceptaTamizajeCasa()), visible);
                //notificarCambios = false;
                changeStatus(mWizardModel.findByKey(labels.getRazonNoAceptaTamizajeCasaLabel()), visible);
                //notificarCambios = false;
                onPageTreeChanged();
            }
            if (page.getTitle().equals(labels.getRazonNoAceptaTamizajeCasa())) {
                visible = page.getData().getString(TextPage.SIMPLE_DATA_KEY) != null && page.getData().getString(TextPage.SIMPLE_DATA_KEY).matches(Constants.OTRO);
                changeStatus(mWizardModel.findByKey(labels.getOtraRazonNoAceptaTamizajeCasa()), visible);
                //notificarCambios = false;
                onPageTreeChanged();
            }
    		if (page.getTitle().equals(labels.getMismoJefe())) {
                visible = page.getData().getString(TextPage.SIMPLE_DATA_KEY) != null && page.getData().getString(TextPage.SIMPLE_DATA_KEY).matches("No");
                changeStatus(mWizardModel.findByKey(labels.getNombre1JefeFamilia()), visible);
                //notificarCambios = false;
                changeStatus(mWizardModel.findByKey(labels.getNombre2JefeFamilia()), visible);
                //notificarCambios = false;
                changeStatus(mWizardModel.findByKey(labels.getApellido1JefeFamilia()), visible);
                //notificarCambios = false;
                changeStatus(mWizardModel.findByKey(labels.getApellido2JefeFamilia()), visible);
                //notificarCambios = false;
                onPageTreeChanged();
            }
    		
    	}catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void changeStatus(Page page, boolean visible){
    	String clase = page.getClass().toString();
    	if (clase.equals("class ni.org.ics.estudios.appmovil.wizard.model.SingleFixedChoicePage")){
    		SingleFixedChoicePage modifPage = (SingleFixedChoicePage) page; modifPage.resetData(new Bundle()); modifPage.setmVisible(visible);
    	}
    	else if (clase.equals("class ni.org.ics.estudios.appmovil.wizard.model.BarcodePage")){
    		BarcodePage modifPage = (BarcodePage) page; modifPage.resetData(new Bundle()); modifPage.setmVisible(visible);
    	}
    	else if (clase.equals("class ni.org.ics.estudios.appmovil.wizard.model.LabelPage")){
    		LabelPage modifPage = (LabelPage) page; modifPage.setmVisible(visible);
    	}
    	else if (clase.equals("class ni.org.ics.estudios.appmovil.wizard.model.TextPage")){
    		TextPage modifPage = (TextPage) page; modifPage.resetData(new Bundle()); modifPage.setmVisible(visible);
    	}
    	else if (clase.equals("class ni.org.ics.estudios.appmovil.wizard.model.NumberPage")){
    		NumberPage modifPage = (NumberPage) page; modifPage.resetData(new Bundle()); modifPage.setmVisible(visible);
    	}
    	else if (clase.equals("class ni.org.ics.estudios.appmovil.wizard.model.MultipleFixedChoicePage")){
    		MultipleFixedChoicePage modifPage = (MultipleFixedChoicePage) page; modifPage.resetData(new Bundle()); modifPage.setmVisible(visible);
    	}
    	else if (clase.equals("class ni.org.ics.estudios.appmovil.wizard.model.DatePage")){
    		DatePage modifPage = (DatePage) page; modifPage.resetData(new Bundle()); modifPage.setmVisible(visible);
    	}
    	else if (clase.equals("class ni.org.ics.estudios.appmovil.wizard.model.SelectParticipantPage")){
    		SelectParticipantPage modifPage = (SelectParticipantPage) page; modifPage.resetData(new Bundle()); modifPage.setmVisible(visible);
    	}
    	else if (clase.equals("class ni.org.ics.estudios.appmovil.wizard.model.NewDatePage")){
    		NewDatePage modifPage = (NewDatePage) page; modifPage.resetData(new Bundle()); modifPage.setmVisible(visible);
    	}
    }
    
    private boolean tieneValor(String entrada){
        return (entrada != null && !entrada.isEmpty());
    }
    
    public void saveData(){
		Map<String, String> mapa = mWizardModel.getAnswers();
		//Guarda las respuestas en un bundle
		Bundle datos = new Bundle();
		for (Map.Entry<String, String> entry : mapa.entrySet()){
			datos.putString(entry.getKey(), entry.getValue());
		}
		
		//Abre la base de datos
		String mPass = ((MyIcsApplication) this.getApplication()).getPassApp();
		estudiosAdapter = new EstudiosAdapter(this.getApplicationContext(),mPass,false,false);
		estudiosAdapter.open();
		
		//Obtener datos del bundle para la visita de terreno
		String id = infoMovil.getId();
		String visitaExitosa = datos.getString(this.getString(R.string.visitaExitosa));
		String razonVisitaNoExitosa = datos.getString(this.getString(R.string.razonVisitaNoExitosa));
        String otraRazonVisitaNoExitosa = datos.getString(this.getString(R.string.otraRazonVisitaNoExitosa));
		
		//Crea una nueva visita de terreno
		VisitaTerreno vt = new VisitaTerreno();
		vt.setCodigoVisita(id);
		vt.setCasa(casa);
		vt.setFechaVisita(new Date());
		if (tieneValor(visitaExitosa)) {
			MessageResource catVisitaExitosa = estudiosAdapter.getMessageResource(CatalogosDBConstants.spanish + "='" + visitaExitosa + "' and " + CatalogosDBConstants.catRoot + "='CHF_CAT_SINO'", null);
			if (catVisitaExitosa!=null) vt.setVisitaExitosa(catVisitaExitosa.getCatKey());
		}
		if (tieneValor(razonVisitaNoExitosa)) {
			MessageResource catRazonVisitaNoExitosa = estudiosAdapter.getMessageResource(CatalogosDBConstants.spanish + "='" + razonVisitaNoExitosa + "' and " + CatalogosDBConstants.catRoot + "='CHF_CAT_NV'", null);
			if (catRazonVisitaNoExitosa!=null) vt.setRazonVisitaNoExitosa(catRazonVisitaNoExitosa.getCatKey());
		}
        vt.setOtraRazonVisitaNoExitosa(otraRazonVisitaNoExitosa);
		vt.setRecordDate(new Date());
		vt.setRecordUser(username);
		vt.setDeviceid(infoMovil.getDeviceId());
		vt.setEstado('0');
		vt.setPasive('0');
		
		//Guarda la visita de terreno
		estudiosAdapter.crearVisitaTereno(vt);
		
		if (vt.getVisitaExitosa().equals(Constants.YESKEYSND)){
			//Obtener datos del bundle para el pretamizaje
			String aceptaTamizajeCasa = datos.getString(this.getString(R.string.aceptaTamizajeCasa));
			String razonNoAceptaTamizajeCasa = datos.getString(this.getString(R.string.razonNoAceptaTamizajeCasa));
            String otraRazonNoAceptaTamizajeCasa = datos.getString(this.getString(R.string.otraRazonNoAceptaTamizajeCasa));
			//Recupera el estudio de la base de datos para el pretamizaje
			Estudio estudio = estudiosAdapter.getEstudio(MainDBConstants.codigo + "=1", null);
			//Crea un Nuevo Registro de pretamizaje
	    	PreTamizaje pt =  new PreTamizaje();
	    	pt.setCodigo(id);
	    	if (tieneValor(aceptaTamizajeCasa)) {
				MessageResource catAceptaTamizajeCasa = estudiosAdapter.getMessageResource(CatalogosDBConstants.spanish + "='" + aceptaTamizajeCasa + "' and " + CatalogosDBConstants.catRoot + "='CHF_CAT_SINO'", null);
				if (catAceptaTamizajeCasa!=null) pt.setAceptaTamizajeCasa(catAceptaTamizajeCasa.getCatKey().charAt(0));
			}
	    	if (tieneValor(razonNoAceptaTamizajeCasa)) {
				MessageResource catRazonNoAceptaTamizajeCasa = estudiosAdapter.getMessageResource(CatalogosDBConstants.spanish + "='" + razonNoAceptaTamizajeCasa + "' and " + CatalogosDBConstants.catRoot + "='CHF_CAT_NPT'", null);
				if (catRazonNoAceptaTamizajeCasa!=null) pt.setRazonNoAceptaTamizajeCasa(catRazonNoAceptaTamizajeCasa.getCatKey());
			}
            pt.setOtraRazonNoAceptaTamizajeCasa(otraRazonNoAceptaTamizajeCasa);
	    	pt.setCasa(casa);
	    	pt.setEstudio(estudio);
	    	pt.setRecordDate(new Date());
	    	pt.setRecordUser(username);
	    	pt.setDeviceid(infoMovil.getDeviceId());
	    	pt.setEstado('0');
	    	pt.setPasive('0');
	    	//Inserta un Nuevo Registro de Pretamizaje
	    	estudiosAdapter.crearPreTamizaje(pt);
	    	//Pregunta si acepta el tamizaje
	    	if (pt.getAceptaTamizajeCasa()==Constants.YESKEYSND.charAt(0)) {
	    		//Si la respuesta es si crea un nuevo registro de casa CHF
	    		//Obtener datos del bundle para la nueva casa
	    		String codigoCHF = datos.getString(this.getString(R.string.codigoCHF));
	    		String mismoJefe = datos.getString(this.getString(R.string.mismoJefe));
	    		//Crea nueva casa
	        	CasaCohorteFamilia cchf = new CasaCohorteFamilia();
	        	cchf.setCodigoCHF(codigoCHF);
	        	cchf.setCasa(casa);
	        	//Pregunta si es el mismo jefe de familia que la casa de cohorte
	        	if (tieneValor(mismoJefe)) {
					MessageResource catMismoJefe = estudiosAdapter.getMessageResource(CatalogosDBConstants.spanish + "='" + mismoJefe + "' and " + CatalogosDBConstants.catRoot + "='CHF_CAT_SINO'", null);
					if(catMismoJefe.getCatKey().matches(Constants.YESKEYSND)){
		        		cchf.setNombre1JefeFamilia(casa.getNombre1JefeFamilia());
		        		cchf.setNombre2JefeFamilia(casa.getNombre2JefeFamilia());
		        		cchf.setApellido1JefeFamilia(casa.getApellido1JefeFamilia());
		        		cchf.setApellido2JefeFamilia(casa.getApellido2JefeFamilia());
		        	}
		        	else{
		        		String nombre1JefeFamilia = datos.getString(this.getString(R.string.nombre1JefeFamilia));
		        		String nombre2JefeFamilia = datos.getString(this.getString(R.string.nombre2JefeFamilia));
		        		String apellido1JefeFamilia = datos.getString(this.getString(R.string.apellido1JefeFamilia));
		        		String apellido2JefeFamilia = datos.getString(this.getString(R.string.apellido2JefeFamilia));
		        		if(tieneValor(nombre1JefeFamilia)) cchf.setNombre1JefeFamilia(nombre1JefeFamilia);
		        		if(tieneValor(nombre2JefeFamilia)) cchf.setNombre2JefeFamilia(nombre2JefeFamilia);
		        		if(tieneValor(apellido1JefeFamilia)) cchf.setApellido1JefeFamilia(apellido1JefeFamilia);
		        		if(tieneValor(apellido2JefeFamilia)) cchf.setApellido2JefeFamilia(apellido2JefeFamilia);
		        	}
				}
	        	if(gps.canGetLocation()){
	        		cchf.setLatitud(gps.getLatitude());
	        		cchf.setLongitud(gps.getLongitude());
	        	}
	        	cchf.setRecordDate(new Date());
	        	cchf.setRecordUser(username);
	        	cchf.setDeviceid(infoMovil.getDeviceId());
	        	cchf.setEstado('0');
	        	cchf.setPasive('0');
	        	//Inserta una nueva casa CHF
	        	estudiosAdapter.crearCasaCohorteFamilia(cchf);
                estudiosAdapter.close();
	        	Bundle arguments = new Bundle();
	            if (cchf!=null) arguments.putSerializable(Constants.CASA , cchf);
	            Intent i = new Intent(getApplicationContext(),
	            		MenuCasaActivity.class);
	            i.putExtras(arguments);
	            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(i);
	        	Toast toast = Toast.makeText(getApplicationContext(),getString(R.string.success),Toast.LENGTH_LONG);
	    		toast.show();
	    		finish();
	    	}
	    	else{
	    		Toast toast = Toast.makeText(getApplicationContext(),getString(R.string.noAceptaTamizajeCasa)+ " " + pt.getRazonNoAceptaTamizajeCasa(),Toast.LENGTH_LONG);
				toast.show();
				finish();
	    	}
		}
		else{
	    	Toast toast = Toast.makeText(getApplicationContext(),getString(R.string.visitaNoExitosa)+ " " + vt.getRazonVisitaNoExitosa(),Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
    }


    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }

            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            return Math.min(mCutOffPage + 1, (mCurrentPageSequence != null ? mCurrentPageSequence.size() : 0) + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }
}
