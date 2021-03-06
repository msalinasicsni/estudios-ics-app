package ni.org.ics.estudios.appmovil.cohortefamilia.forms;

import android.content.Context;
import ni.org.ics.estudios.appmovil.catalogs.MessageResource;
import ni.org.ics.estudios.appmovil.database.EstudiosAdapter;
import ni.org.ics.estudios.appmovil.utils.CatalogosDBConstants;
import ni.org.ics.estudios.appmovil.utils.Constants;
import ni.org.ics.estudios.appmovil.wizard.model.*;

import java.util.List;

/**
 * Created by Miguel Salinas on 5/16/2017.
 * V1.0
 */
public class EncuestaPesoTallaForm extends AbstractWizardModel {

    int index = 0;
    private String[] catSiNo;

    private EstudiosAdapter estudiosAdapter;
    private EncuestaPesoTallaFormLabels labels;

    public EncuestaPesoTallaForm(Context context, String pass) {
        super(context,pass);
    }

    private String[] fillCatalog(String codigoCatalogo){
        String[] catalogo;
        List<MessageResource> mCatalogo = estudiosAdapter.getMessageResources(CatalogosDBConstants.catRoot + "='"+codigoCatalogo+"'", CatalogosDBConstants.order);
        catalogo = new String[mCatalogo.size()];
        index = 0;
        for (MessageResource message: mCatalogo){
            catalogo[index] = message.getSpanish();
            index++;
        }
        return catalogo;
    }

    @Override
    protected PageList onNewRootPageList() {
        labels = new EncuestaPesoTallaFormLabels();
        this.estudiosAdapter = new EstudiosAdapter(mContext,mPass,false,false);

        estudiosAdapter.open();
        catSiNo = fillCatalog("CHF_CAT_SINO");
        estudiosAdapter.close();

        Page scTomoMedidaSna = new SingleFixedChoicePage(this, labels.getTomoMedidaSn(), "", Constants.WIZARD, true).setChoices(catSiNo).setRequired(true);
        Page tpRazonNoTomoMedidas = new TextPage(this, labels.getRazonNoTomoMedidas(), "", Constants.WIZARD, false).setRequired(true);
        Page npPeso1 = new NumberPage(this, labels.getPeso1(), labels.getPesoHint(), Constants.WIZARD, false).setRequired(true);
        Page npTalla1 = new NumberPage(this, labels.getTalla1(), labels.getTallaHint(), Constants.WIZARD, false).setRequired(true);
        Page imc1 = new LabelPage(this, labels.getImc1(), "", Constants.WIZARD, false).setRequired(true);
        Page npPeso2 = new NumberPage(this, labels.getPeso2(), labels.getPesoHint(), Constants.WIZARD, false).setRequired(true);
        Page npTalla2 = new NumberPage(this, labels.getTalla2(), labels.getTallaHint(), Constants.WIZARD, false).setRequired(true);
        Page imc2 = new LabelPage(this, labels.getImc2(), "", Constants.WIZARD, false).setRequired(true);
        Page npPeso3 = new NumberPage(this, labels.getPeso3(), labels.getPesoHint(), Constants.WIZARD, false).setRequired(true);
        Page npTalla3 = new NumberPage(this, labels.getTalla3(), labels.getTallaHint(), Constants.WIZARD, false).setRequired(true);
        Page imc3 = new LabelPage(this, labels.getImc3(), "", Constants.WIZARD, false).setRequired(false);
        //Page difPeso = new NumberPage(this, labels.getDifPeso(), "", Constants.WIZARD, false).setRequired(true);
        //Page difTalla = new NumberPage(this, labels.getDifTalla(), "", Constants.WIZARD, false).setRequired(true);
        Page difMediciones = new LabelPage(this, labels.getDifMediciones(), "", Constants.ROJO, false).setRequired(true);

        return new PageList(scTomoMedidaSna, tpRazonNoTomoMedidas, npPeso1,npTalla1,imc1, npPeso2, npTalla2, imc2, difMediciones, npPeso3, npTalla3, imc3);
    }
}
