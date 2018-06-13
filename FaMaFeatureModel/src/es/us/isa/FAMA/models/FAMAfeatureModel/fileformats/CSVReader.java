/**
 * 	This file is part of FaMaTS.
 *
 *     FaMaTS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FaMaTS is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.us.isa.FAMA.models.FAMAfeatureModel.fileformats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.csvreader.CsvReader;

import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.featureModel.Cardinality;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.IReader;

public class CSVReader implements IReader {

	File fichero;
	FileReader freader;
	CsvReader reader;

	public CSVReader() throws FileNotFoundException {
		fichero = new File("./mh.csv");
		freader = new FileReader(fichero);

		reader = new CsvReader(freader, ',');
	}

	@Override
	public boolean canParse(String fileName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public VariabilityModel parseFile(String fileName) throws Exception {
		FAMAFeatureModel fm = new FAMAFeatureModel();
		Feature root = new Feature("MetaHFM");
		fm.setRoot(root);
		reader.readHeaders();
		
		Feature areaF=null;
		Feature caracteristicsF=null;
		
		Relation arel=new Relation();
		Relation crel=new Relation();
		Relation frel=new Relation();	
				
		int caractN=0;
		int featN=0;
		
		while(reader.readRecord()){
			String area = reader.get("AREA");
			String caracteristics = reader.get("CARACTERISTIC");
			String feature = reader.get("FEATURE");
			
			
			if(area!=""){
				//creamos la nueva area y la asociamos a la root
				areaF = new Feature(area);
				arel = new Relation();
				root.addRelation(arel);
				arel.setParent(root);				
				arel.addCardinality(new Cardinality(1, 1));
				arel.addDestination(areaF);
				
				crel.addCardinality(new Cardinality(1,caractN));
				
				caractN=0;
				crel = new Relation();
				crel.setParent(areaF);
				areaF.addRelation(crel);
				}
			
			if(caracteristics!=""){
				//Creamos la nueva caracteristica y la asociamos a la area 
				//tambien creamos la relacion que usaran los hijos
				caracteristicsF= new Feature(caracteristics);
				crel.addDestination(caracteristicsF);
				caractN++;
				frel.addCardinality(new Cardinality(1, featN));
				featN=0;
				frel=new Relation();
				frel.setParent(caracteristicsF);
				caracteristicsF.addRelation(frel);
			}
			
			if(feature!=""){
				//creamos la caracteristica y la asociamos a la caracteristica
				Feature featureF=new Feature(feature);
				frel.addDestination(featureF);
				featN++;
			}
			
		}
		
		
		return fm;
	}

	@Override
	public VariabilityModel parseString(String data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) throws Exception{
		CSVReader csvReader= new CSVReader();
		VariabilityModel vm=csvReader.parseFile("");
		System.out.println(vm);
		X3DWriter writer = new X3DWriter();
		writer.writeFile("./test.dot", vm);
	}
}
