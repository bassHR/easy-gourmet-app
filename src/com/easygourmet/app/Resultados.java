package com.easygourmet.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.easygourmet.beans.Receta;
import com.easygourmet.db.RecetaDBA;
import com.easygourmet.main.R;
import com.easygourmet.ui.ResultadosAdapter;

public class Resultados extends ActionBarActivity {

	private ResultadosAdapter adapterRecetas;
	private ArrayAdapter<String> adapterResultadosSpinner;
	private List<Receta> recetas;
	private Spinner resultados_spinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resultados);

		overridePendingTransition(R.drawable.slide_in, R.drawable.slide_out);
		
		Intent intenet = getIntent();
		
		Bundle b = intenet.getExtras();
		Integer idCategoria = b.getInt("idCategoria");
		ArrayList<Integer> ingredintesElegidos = intenet.getIntegerArrayListExtra("ingredientesElegidos");
		
		if(ingredintesElegidos != null){
			this.recetas = RecetaDBA.getRecetasByIngredientes(Resultados.this, ingredintesElegidos);
			if(this.recetas.size() < 1){
				this.recetas = RecetaDBA.getRecetasByIngredientesAnyMatch(Resultados.this, ingredintesElegidos);
			}
			
			this.adapterRecetas = new ResultadosAdapter(this, R.layout.list_recetas, recetas);
			
		} else if(idCategoria != null){
			this.recetas = RecetaDBA.getRecetasByCategoria(Resultados.this, idCategoria);
			this.adapterRecetas = new ResultadosAdapter(this, R.layout.list_recetas, recetas);
		}
		
		ListView listaRecetas = (ListView) findViewById(R.id.recetas);
		
		listaRecetas.setAdapter(this.adapterRecetas);
		listaRecetas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Receta r = Resultados.this.adapterRecetas.getItem(position);
				
				Intent i = new Intent(Resultados.this, RecetaView.class);
				i.putExtra("idReceta", r.getIdReceta());
				Resultados.this.startActivity(i);
			}
			
		});
		
		this.resultados_spinner = (Spinner) findViewById(R.id.resultados_spinner);
		this.adapterResultadosSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.resultados_spinner_array));
		this.resultados_spinner.setAdapter(adapterResultadosSpinner);
		this.resultados_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
//				TextView tv = (TextView) parent.getChildAt(0);
//				tv.setTextColor(Color.WHITE);
//		        tv.setTypeface(null, Typeface.BOLD);
		        
		        String value = Resultados.this.adapterResultadosSpinner.getItem(position);
				switch(value){
					case "Mas Sanos":
						
						Collections.sort(Resultados.this.recetas, new Comparator<Receta>() {
								@Override
								public int compare(Receta r1, Receta r2) {
									int ri1 = r1.getSalud();
									int ri2 = r2.getSalud();
									
									if(ri1 > ri2){
										return -1;
									}
									else if(ri1 < ri2){
										return 1;
									}
									else{
										return 0;
									}
								}
						});
						Resultados.this.adapterRecetas.notifyDataSetChanged();
						break;
						
					case "Menor Tiempo": 
						
						Collections.sort(Resultados.this.recetas, new Comparator<Receta>() {
							@Override
							public int compare(Receta r1, Receta r2) {
								return r1.getTiempo().compareTo(r2.getTiempo());
							}
						});
						Resultados.this.adapterRecetas.notifyDataSetChanged();
						break;
						
					case "Dificultad": 
						
						Collections.sort(Resultados.this.recetas, new Comparator<Receta>() {
							
							private final String facil = "Facil";
							private final String medio = "Medio";
							private final String dificil = "Dificil";
							
							@Override
							public int compare(Receta r1, Receta r2) {
								
								String r1Dif = r1.getDificultad();
								String r2Dif = r2.getDificultad();
								int res = 0;
								
								switch(r1Dif){
									case facil:
										
										if(!r2Dif.equals(facil)) res = -1;
										break;
									
									case medio:
										
										if(r2Dif.equals(facil)) res = 1;
										else if(r2Dif.equals(dificil)) res = -1;
										break;
									
									case dificil:
										if(!r2Dif.equals(dificil)) res = 1;
										break;
								}
								System.out.println(r1Dif + " - " + r2Dif + " res=" + res);
								return res;
								
							}
						});
						
						Resultados.this.adapterRecetas.notifyDataSetChanged();
						break;
						
					case "Alfabéticamente": 
						
						Collections.sort(Resultados.this.recetas, new Comparator<Receta>() {
							@Override
							public int compare(Receta r1, Receta r2) {
								return r1.getNombre().compareTo(r2.getNombre());
							}
						});
						Resultados.this.adapterRecetas.notifyDataSetChanged();
						break;
						
					default: 
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.resultados, menu);
		return true;
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
