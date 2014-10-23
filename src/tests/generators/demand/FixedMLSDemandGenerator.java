package tests.generators.demand;

import java.util.ArrayList;
import java.util.Random;

import tests.ConversionHelper;
import tests.generators.GeneratorParameter;
import tests.generators.resource.RandomMLSResourceGenerator;
import vnreal.algorithms.utils.mls.MLSLattice;
import vnreal.demands.MLSDemand;
import vnreal.network.NetworkStack;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

@GeneratorParameter(
		parameters = { "Networks:Networks", "TR:MLS_SecLevels", "TR:MLS_NumCategories", "Seed:Seed"}
		)
public class FixedMLSDemandGenerator extends AbstractDemandGenerator<Integer> {

	@Override
	public Integer generate(ArrayList<Object> parameters) {
		NetworkStack ns = (NetworkStack)parameters.get(0);
		Integer sLevels = ConversionHelper.paramObjectToInteger(parameters.get(1));
		Integer sCats = ConversionHelper.paramObjectToInteger(parameters.get(2));
		Long seed = ConversionHelper.paramObjectToLong(parameters.get(3));
		
		Random random = new Random();
		random.setSeed(seed);
		
		MLSLattice lattice = RandomMLSResourceGenerator.createLattice(sLevels, sCats);
		
		
		int maxLevel = lattice.getNumberOfLevels() - 1;
		ArrayList<String> cats = lattice.getCategories();
		
		for(int u = 1; u < ns.size(); u++) {
		
			VirtualNetwork vNetwork = (VirtualNetwork)ns.getLayer(u);
			
			for (VirtualNode n : vNetwork.getVertices()) {
				//Create Random resource
				int resDem = (int)((maxLevel + 1) * random.nextDouble());
				int resProv = (int)((maxLevel + 1) * random.nextDouble());
			
				//we need at least one Category
				int countCat;
				do {
					countCat = (int)((cats.size()+1) * random.nextDouble());
				} while (countCat < 1);
				ArrayList<String> chosenCats = new ArrayList<String>();
				for (int c = 0; c < countCat; c++) {
					String cat;
					do {
						cat = cats.get((int)(cats.size() * random.nextDouble()));
					} while (chosenCats.contains(cat));
					chosenCats.add(cat);
				}

					MLSDemand res = new MLSDemand(n, resDem, resProv, chosenCats);
				n.add(res);
			}
		}
		return 0;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
