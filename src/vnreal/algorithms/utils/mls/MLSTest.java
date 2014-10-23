package vnreal.algorithms.utils.mls;

import java.util.ArrayList;
import java.util.Random;

import vnreal.demands.MLSDemand;
import vnreal.resources.MLSResource;

public class MLSTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ArrayList<String> cats = new ArrayList<String>();
		cats.add("Verwaltung");
		cats.add("Produktion");
		
		ArrayList<String> demCats = new ArrayList<String>();
		demCats.add(cats.get(0));
		
		MLSDemand dem1 = new MLSDemand(null, 2, 2, demCats);
		
		MLSResource res1 = new MLSResource(null, 2, 1, cats);
		
		System.out.println(dem1.getFulfillsVisitor().visit(res1));
		
		//List of all possible Catlists / no categorie make no sense for a node
		ArrayList<ArrayList<String>> allPosCats = new ArrayList<ArrayList<String>>();
		allPosCats.add(cats);	//Beide Cats
		ArrayList<String> verwaltung = new ArrayList<String>();
		verwaltung.add(cats.get(0));
		allPosCats.add(verwaltung);
		ArrayList<String> produktion = new ArrayList<String>();
		produktion.add(cats.get(1));
		allPosCats.add(produktion);
		
		
		//Create a test that checks all
		int maxlevel = 3;
		//Create every possible resource
		ArrayList<MLSResource> resList = new ArrayList<MLSResource>();
		//demand level loop
		for (int d = 0; d <=maxlevel; d++) {
			//Provide loop
			for(int p = 0; p <=maxlevel; p++) {
				//Categorieloop
				for(ArrayList<String> l : allPosCats) {
					MLSResource mls = new MLSResource(null, d, p, l);
					resList.add(mls);
				}
			}
		}
		
		//Now create DEmands the same way an Check if Results are OK
		for (int d = 0; d <=maxlevel; d++) {
			//Provide loop
			for(int p = 0; p <=maxlevel; p++) {
				//Categorieloop
				for(ArrayList<String> l : allPosCats) {
					MLSDemand mlsdem = new MLSDemand(null, d, d, l);
					System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println(mlsdem);
					for (MLSResource lres : resList) {
						System.out.println(lres.toString()+"---->"+mlsdem.getFulfillsVisitor().visit(lres));
					}
					System.out.println("-----------------------------------------------------------------");
				}
			}
		}
		Random random = new Random();
		
		for (int i = 0; i<100;i++) {
			System.out.print(((int)((2 + 1) * random.nextDouble()))+",");
		}
		System.out.println();
	}

}
