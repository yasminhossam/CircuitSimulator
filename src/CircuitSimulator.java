import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Vector;

import Jama.Matrix;

public class CircuitSimulator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		FileReader fr = null;
		String filename = "1.txt";
		Vector<Component> components = new Vector<Component> ();
		int m = 0;
		Vector<Integer> nodes = new Vector<Integer> ();

		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filename));
			while ((sCurrentLine = br.readLine()) != null) {
				String[] words = sCurrentLine.split(" ");
				String type=words[0];
				if (type.equals("Vsrc"))
					m++;
				int node1 = Character.getNumericValue(words[1].charAt(1));
				int node2 = Character.getNumericValue(words[2].charAt(1));
				nodes.add(node1);
				nodes.add(node2);
				double value = Double.parseDouble(words[3]);
				double initvalue = Double.parseDouble(words[4]);
				components.add(new Component(type,node1,node2,value,initvalue));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < components.size(); i++) {
			System.out.println(components.elementAt(i).type +" "+ components.elementAt(i).node1 +" "+ components.elementAt(i).node2 +" "+ components.elementAt(i).value +" "+ components.elementAt(i).initial_value);
		}
		
		nodes=new Vector(new HashSet(nodes));
		int n=nodes.size()-1;
		
		double [][] a = new double[m+n][m+n];
		double[][] z = new double[m+n][1];
		
		for (int i = 0; i < m+n; i++) {
			for (int j = 0; j < m+n; j++) {
				a[i][j]=0;
			}
		}
		for (int i = 0; i < z.length; i++) {
			z[i][0]=0;
		}
		int k=n;
		for (int i = 0; i < components.size(); i++) {
			if (components.elementAt(i).type.equals("R")){
				if (components.elementAt(i).node2 != 0){
					a[components.elementAt(i).node1-1][components.elementAt(i).node1-1]+=1/components.elementAt(i).value;
					a[components.elementAt(i).node1-1][components.elementAt(i).node2-1]-=1/components.elementAt(i).value;
					a[components.elementAt(i).node2-1][components.elementAt(i).node1-1]-=1/components.elementAt(i).value;
					a[components.elementAt(i).node2-1][components.elementAt(i).node2-1]+=1/components.elementAt(i).value;
				}
				else {
					a[components.elementAt(i).node1-1][components.elementAt(i).node1-1]+=1/components.elementAt(i).value;
				}
			}
			else if (components.elementAt(i).type.equals("Isrc") ){
				if (components.elementAt(i).node2 != 0){
					z[components.elementAt(i).node1-1][0]+=components.elementAt(i).value;
					z[components.elementAt(i).node2-1][0]-=components.elementAt(i).value;
				}
				else {
					z[components.elementAt(i).node1-1][0]+=components.elementAt(i).value;
				}
			}
			else if (components.elementAt(i).type.equals("Vsrc")){
				if (components.elementAt(i).node2 != 0){
					a[components.elementAt(i).node1-1][k]+=1;
					a[components.elementAt(i).node2-1][k]-=1;
					a[k][components.elementAt(i).node1-1]+=1;
					a[k][components.elementAt(i).node2-1]-=1;
				}
				else {
					a[components.elementAt(i).node1-1][k]+=1;
					a[k][components.elementAt(i).node1-1]+=1;
				}
				z[k][0]+=components.elementAt(i).value;
				k++;
			}
		}
		System.out.println("m:"+m +" n:" +n);
		for (int i = 0; i < m+n; i++) {
			for (int j = 0; j < m+n; j++) {
				System.out.print(a[i][j] + " ");
			}
			System.out.println("");
		}
		for (int i = 0; i < z.length; i++) {
			System.out.println(z[i][0]);
		}
		Matrix A = new Matrix(a);
		Matrix Z = new Matrix(z);
		Matrix x = A.solve(Z);
		for (int i = 0; i < m+n; i++) {
			System.out.println(x.get(i, 0));
		}
		
		/*		
		double[][] array = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}};
		Matrix A = new Matrix(array);
		Matrix b = Matrix.random(3,1);
		Matrix x = A.solve(b);
		Matrix Residual = A.times(x).minus(b);
		double rnorm = Residual.normInf();*/
	}

}

