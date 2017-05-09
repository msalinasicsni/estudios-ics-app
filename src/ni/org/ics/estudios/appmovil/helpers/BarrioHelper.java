package ni.org.ics.estudios.appmovil.helpers;

import ni.org.ics.estudios.appmovil.catalogs.Barrio;
import ni.org.ics.estudios.appmovil.utils.MainDBConstants;

import android.content.ContentValues;
import android.database.Cursor;

public class BarrioHelper {
	
	public static ContentValues crearBarrioContentValues(Barrio barrio){
		ContentValues cv = new ContentValues();
		cv.put(MainDBConstants.codigo, barrio.getCodigo());
		return cv; 
	}	
	
	public static Barrio crearBarrio(Cursor cursorBarrio){
		
		Barrio mBarrio = new Barrio();
		mBarrio.setCodigo(cursorBarrio.getInt(cursorBarrio.getColumnIndex(MainDBConstants.barrio)));
		return mBarrio;
	}
	
}