import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Vector;

import Jama.Matrix;

public class CircuitSimulator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		FileReader fr = null;
		Scanner reader = new Scanner(System.in);
		System.out.println("file: ");
		String input = reader.next();
		String filename = input+".txt";
		Vector<Component> components = new Vector<Component> ();
		int m = 0;
		int v = 0;
		Vector<Integer> nodes = new Vector<Integer> ();

		
		System.out.println("h: ");
		float h = reader.nextFloat(); 
		System.out.println("end time: ");
		float endtime = reader.nextFloat();
		
		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filename));
			while ((sCurrentLine = br.readLine()) != null) {
				String[] words = sCurrentLine.split(" ");
				String type=words[0];
				if (type.equals("Vsrc")){
					m++;
					v++;
				}
				else if (type.equals("I"))	
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
		int s=n+v;
		for (int i = 0; i < components.size(); i++) {
			if (components.elementAt(i).type.equals("R")){
				a[components.elementAt(i).node1-1][components.elementAt(i).node1-1]+=1/components.elementAt(i).value;
				if (components.elementAt(i).node2 != 0){
					a[components.elementAt(i).node1-1][components.elementAt(i).node2-1]-=1/components.elementAt(i).value;
					a[components.elementAt(i).node2-1][components.elementAt(i).node1-1]-=1/components.elementAt(i).value;
					a[components.elementAt(i).node2-1][components.elementAt(i).node2-1]+=1/components.elementAt(i).value;
				}
			}
			else if (components.elementAt(i).type.equals("Isrc") ){
				z[components.elementAt(i).node1-1][0]+=components.elementAt(i).value;
				if (components.elementAt(i).node2 != 0){
					z[components.elementAt(i).node2-1][0]-=components.elementAt(i).value;
				}
			}
			else if (components.elementAt(i).type.equals("Vsrc")){
				a[components.elementAt(i).node1-1][k]+=1;
				a[k][components.elementAt(i).node1-1]+=1;
				if (components.elementAt(i).node2 != 0){
					a[components.elementAt(i).node2-1][k]-=1;
					a[k][components.elementAt(i).node2-1]-=1;
				}
				z[k][0]+=components.elementAt(i).value;
				k++;
			}
			else if (components.elementAt(i).type.equals("C")){
				a[components.elementAt(i).node1-1][components.elementAt(i).node1-1]+=components.elementAt(i).value/h;
				if (components.elementAt(i).node2 != 0){
					a[components.elementAt(i).node1-1][components.elementAt(i).node2-1]-=components.elementAt(i).value/h;
					a[components.elementAt(i).node2-1][components.elementAt(i).node1-1]-=components.elementAt(i).value/h;
					a[components.elementAt(i).node2-1][components.elementAt(i).node2-1]+=components.elementAt(i).value/h;
				}
			}
			else if (components.elementAt(i).type.equals("I")){
				if (components.elementAt(i).node2 != 0){
					a[components.elementAt(i).node2-1][s]-=1;
					a[s][components.elementAt(i).node2-1]-=1;
				}
				a[components.elementAt(i).node1-1][s]+=1;
				a[s][components.elementAt(i).node1-1]+=1;
				a[s][s]-=components.elementAt(i).value/h;
				s++;
			}
			
		}
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(input+"_solution.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.print("timestep ");
		for (int i = 1; i < n+1; i++) {
			out.print("V"+nodes.elementAt(i)+" ");
		}
		for (int i = 0; i < v; i++) {
			out.print("I_Vsrc" + i + " ");
		}
		for (int i = 0; i < m-v; i++) {
			out.print("I_ind" +i + " ");
		}
		out.println(" ");
		Matrix A = new Matrix(a);
		double [][] new_z = new double[m+n][1];
		for (int i = 0; i < new_z.length; i++) {
			new_z[i][0] = z[i][0];
		}
		s=n+v;
		for (int i = 0; i < components.size(); i++) {
			if (components.elementAt(i).type.equals("C")){
				new_z[components.elementAt(i).node1-1][0] += (components.elementAt(i).value/h) * components.elementAt(i).initial_value;
				if (components.elementAt(i).node2 != 0)
					new_z[components.elementAt(i).node2-1][0] -= (components.elementAt(i).value/h) * components.elementAt(i).initial_value;
			}
			else if (components.elementAt(i).type.equals("I")){
				z[s][0] -= (components.elementAt(i).value/h) * components.elementAt(i).initial_value;
				s++;
			}
		}
		
		Matrix Z = new Matrix(new_z);
		Matrix x = A.solve(Z);
		out.print( h + " ");
		for (int i = 0; i < m+n; i++) {
			out.print(x.get(i, 0) + " ");
		}
		out.println(" ");
		
		for (float time = 2*h; (float)Math.round(time * 10000d) / 10000d  <= endtime; time+=h) {
			for (int i = 0; i < new_z.length; i++) {
				new_z[i][0] = z[i][0];
			}
			s=n+v;
			for (int i = 0; i < components.size(); i++) {
				if (components.elementAt(i).type.equals("C")){
					if (components.elementAt(i).node2 != 0){
						new_z[components.elementAt(i).node2-1][0] -= (components.elementAt(i).value/h) * (x.get(components.elementAt(i).node1-1, 0) - x.get(components.elementAt(i).node2-1, 0));
						new_z[components.elementAt(i).node1-1][0] += (components.elementAt(i).value/h) * (x.get(components.elementAt(i).node1-1, 0) - x.get(components.elementAt(i).node2-1, 0));
					}
					else {
						new_z[components.elementAt(i).node1-1][0] += (components.elementAt(i).value/h) * (x.get(components.elementAt(i).node1-1, 0));
					}
				}
				else if (components.elementAt(i).type.equals("I")){
					z[s][0] -= (components.elementAt(i).value/h) * x.get(s, 0);
					s++;
				}
			}
			
			Z = new Matrix(new_z);
			x = A.solve(Z);
			out.print(time + " ");
			for (int i = 0; i < m+n; i++) {
				out.print(x.get(i, 0) + " ");
			}
			out.println(" ");
			
		}
	}
	
}

