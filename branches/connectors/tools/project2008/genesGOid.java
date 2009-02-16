// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2007 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class genesGOid {

	public static void main(String[] args){
		String s; 
		List<String[]> arrayGOgenes = new ArrayList<String[]>();
		
		try {
			 FileReader fr = new FileReader(args[2]);
		      BufferedReader br = new BufferedReader(fr);

		     while((s = br.readLine()) != null){
		    	 arrayGOgenes.add(s.split("\t"));
		      }
		        //System.out.println(s);
		
		
		      fr.close();
		    }
		    catch(Exception e) {
		      System.out.println("Exception: " + e);
				}
	
    String[] tweedeUitDeLijst = arrayGOgenes.get(1);
    String eersteKolom = tweedeUitDeLijst[0];
    System.out.println(eersteKolom);
	System.out.println(arrayGOgenes.size());
	
	
	List<String> ensemblGeneIds = new ArrayList<String>();
	
	//i begint bij 1 en niet bij 0 omdat de string telkens wordt vergeleken met de string ervoor.
	for (int i = 1;i<(arrayGOgenes.size());i++){
		String currentGene = arrayGOgenes.get(i)[0];
		String previousGene = arrayGOgenes.get(i-1)[0];
		if (!currentGene.equals(previousGene)){
			ensemblGeneIds.add(currentGene);}
		}
	
	Map<String,List<String>> goByGene = new HashMap<String,List<String>>();
	List<String> gOIds = new ArrayList<String>();
	
	for (int i = 1;i<(arrayGOgenes.size());i++){
		if (arrayGOgenes.get(i).length > 2){
			String currentGene = arrayGOgenes.get(i)[0];
			String currentGOId = arrayGOgenes.get(i)[2];
			String previousGene = arrayGOgenes.get(i-1)[0];
			
			if (!currentGene.equals(previousGene)){
				goByGene.put(previousGene,gOIds);
				gOIds = new ArrayList<String>();
				gOIds.add(currentGOId);}
			else {gOIds.add(currentGOId);}
		}
	}
	
	for (String key : goByGene.keySet())
	{
		System.out.println (key);
		
		List<String> values = goByGene.get(key);
		
		for (String value : values)
		{
			System.out.println ("  " + value);
		}
		}
	}
	
	}


	