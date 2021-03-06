package ni.org.ics.estudios.appmovil.cohortefamilia.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ni.org.ics.estudios.appmovil.R;
import ni.org.ics.estudios.appmovil.domain.cohortefamilia.PersonaCama;

import java.text.SimpleDateFormat;
import java.util.List;

public class PersonaCamaAdapter extends ArrayAdapter<PersonaCama> {

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MMM dd, yyyy");
	
	public PersonaCamaAdapter(Context context, int textViewResourceId,
                          List<PersonaCama> items) {
		super(context, textViewResourceId, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.complex_list_item, null);
		}
		PersonaCama p = getItem(position);
		if (p != null) {

			TextView textView = (TextView) v.findViewById(R.id.identifier_text);
			textView.setTextColor(Color.BLACK);
			if (textView != null) {
				if (p.getParticipante()!=null){
					textView.setText(this.getContext().getString(R.string.code) + ": " + p.getParticipante().getCodigo());
				}
			}
			
			textView = (TextView) v.findViewById(R.id.der_text);
			textView.setTextColor(Color.BLACK);
			if (textView != null) {
				if (p.getParticipante()!=null){
					textView.setText(this.getContext().getString(R.string.fechaNacimiento)+": "+mDateFormat.format(p.getParticipante().getFechaNac()));
				}
				else{
					textView.setText(mDateFormat.format(p.getRecordDate()));
				}
			}

			textView = (TextView) v.findViewById(R.id.name_text);
			textView.setTextColor(Color.BLACK);
			if (textView != null) {
				if (p.getParticipante()!=null){
					textView.setText(p.getParticipante().getNombre1()+" "+ p.getParticipante().getApellido1());
				}
				else {
					textView.setText(this.getContext().getString(R.string.edad) + ": " + p.getEdad()+" - " +this.getContext().getString(R.string.sexo) + ": " + p.getSexo());
				}
			}
			
			ImageView imageView = (ImageView) v.findViewById(R.id.image);
			if (imageView != null) {
				if (p.getParticipante()!=null){
					if(p.getParticipante().getSexo().equals("M")) {
						imageView.setImageResource(R.drawable.male);
					} else if (p.getParticipante().getSexo().equals("F")) {
						imageView.setImageResource(R.drawable.female);
					}
				}
				else{
					if(p.getSexo().equals("M")) {
						imageView.setImageResource(R.drawable.male);
					} else if (p.getSexo().equals("F")) {
						imageView.setImageResource(R.drawable.female);
					}
				}
			}
		
		}
		return v;
	}
}
