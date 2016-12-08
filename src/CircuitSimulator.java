import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import Jama.Matrix;

public class CircuitSimulator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		FileReader fr = null;
		String filename = "1.txt";
		Component[]components = new Component[20];
		int[] nodes = new int[20];
		int m = 0;
		int n = 2;
		int i=0;
		int j=0;
		Vector<Integer> myVector = new Vector<Integer> ();

		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filename));
			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				String[] words = sCurrentLine.split(" ");
				String type=words[0];
				if (type=="Vsrc")
					m++;
				int node1 = Character.getNumericValue(words[1].charAt(1));
				int node2 = Character.getNumericValue(words[2].charAt(1));
				double value = Double.parseDouble(words[3]);
				double initvalue = Double.parseDouble(words[4]);
				components[i]= new Component(type,node1,node2,value,initvalue);
				
				System.out.println(components[i].type+" "+ components[i].node1+ " "+components[i].node2+" "+ components[i].value+ " "+components[i].initial_value);
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*for (int j=1; j<i; j++){
			for (int k=j; k<=0; k--){
				if (components[j].node1 == components[k].node1 || components)
			}
		}*/
		double[][] array = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}};
		Matrix A = new Matrix(array);
		Matrix b = Matrix.random(3,1);
		Matrix x = A.solve(b);
		Matrix Residual = A.times(x).minus(b);
		double rnorm = Residual.normInf();
	}

}
