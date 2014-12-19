import arrayList.*;
import java.util.*;

public class ProbarArrayList {
	private static Collection<Integer> col;
	private static Collection<Integer> col2;
	private static int[] array;
	private static int[] array2;
	private static ArrayList<Integer> al;
	private static ArrayList<Integer> al2;
	private static ArrayList<Integer> al3;
	private static TIntArrayList tial1;
	private static TIntArrayList tial2;
	private static TIntArrayList tial3;
	private static int numElem = 1000000;
	private static int numElem2 = 10000;
	private static int numProc;
	private static int numIt = 5;
	private static long startTime;
	private static long endTime;
	private static float tiempo;
	private static Random r = new Random();
	
	public static void inicializar ()	{
		col = new ArrayList<Integer> ();
		col2 = new ArrayList<Integer> ();
		array = new int[numElem];
		array2 = new int[numElem2];
		for (int i=0; i<numElem; i++)	{
			col.add(i);
			array[i] = i;
		}
		for (int i=0; i<numElem2; i++)	{
			col2.add(i);
			array2[i] = i;
		}
		al = new ArrayList<Integer> ();
		al2 = new ArrayList<Integer> ();
		al2.addAll (col);
		al3 = new ArrayList<Integer> ();
		al3.addAll (col2);
		tial1 = new TIntArrayList ();
		tial2 = new TIntArrayList ();
		tial3 = new TIntArrayList ();
		tial3.addAll (col2);
	}
	
	public static void inicioMedida ()	{
		startTime = System.nanoTime();
	}
	
	public static void finMedida (String nombreMetodo)	{
		endTime = System.nanoTime();
		tiempo = (endTime - startTime)/1000000.0f;
		System.out.println ("Tiempo de " + nombreMetodo + " : " + tiempo + "ms");
	}
	
	public static void main (String[] args)	{
		/**if (args[0] != null)
		numProc = Integer.parseInt (args[0]);
		if (args[1] != null)
		numIt = Integer.parseInt(args[1]);
		if (args[2] != null)
		numElem = Integer.parseInt(args[2]);
		if (args[3] != null)
		numElem2 = Integer.parseInt(args[3]);*/
		for (numProc = 2; numProc < 32; numProc *= 2)	{
		inicializar();
		System.out.println ("Tamaño: " + numElem);
		System.out.println ("Número de iteraciones: " + numIt);
		System.out.println ("Número de procesadores: " + numProc);
		for (int i=0; i<numIt; i++)	{
			al.clear();
			inicioMedida();
			al.addAll(col);
			finMedida ("addAll (Collection) - jdk");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			tial1.clear();
			inicioMedida();
			tial1.addAllPar(col, numProc);
			finMedida ("addAll (Collection) - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			tial2.clear();	
			inicioMedida();
			tial2.addAll (col);
			finMedida ("addAll (Collection) - trove");
		}	
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			tial1.clear();
			inicioMedida();
			tial1.addAllPar (array, numProc);
			finMedida ("addAll (int[]) - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{	
			tial2.clear();
			inicioMedida();
			tial2.addAll (array);
			finMedida ("addAll (int[]) - trove");
		}	
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			tial1.clear();
			inicioMedida();
			tial1.addAllPar (tial2, numProc);
			finMedida ("addAll (TIntArrayList - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{	
			tial2.clear();
			inicioMedida();
			tial2.addAll (tial1);
			finMedida ("addAll (TIntArrayList - trove");
		}	
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			if (!al.equals (al2)) System.out.println ("Distintos");
			finMedida ("equals - jdk");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			if (!tial1.equalsPar (tial2, numProc)) System.out.println ("Distintos");
			finMedida ("equals - nuevo");
			System.out.println ("Tamaño: " + tial1.size());
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			if (!tial2.equals (tial1)) System.out.println ("Distintos");
			finMedida ("equals - trove");
			System.out.println("Tamaño: " + tial2.size());
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			al.hashCode();
			finMedida ("hashCode - jdk");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial1.hashCodePar (numProc);
			finMedida ("hashCode - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial2.hashCode ();
			finMedida ("hashCode - trove");
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			al.indexOf (numElem-1);
			finMedida("indexOf - jdk");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial1.indexOfPar (numElem-1, numProc);
			finMedida ("indexOf - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial2.indexOf (numElem-1);
			finMedida ("indexOf - trove");
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			al.lastIndexOf (0);
			finMedida("lastIndexOf - jdk");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial1.lastIndexOfPar (0, numProc);
			finMedida ("lastIndexOf - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial2.lastIndexOf (0);
			finMedida ("lastIndexOf - trove");
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial1.maxPar (numProc);
			finMedida ("max - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial2.max();
			finMedida ("max - trove");
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial1.minPar (numProc);
			finMedida ("min - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial2.min();
			finMedida ("min - trove");
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			System.out.println ("Tamaño: " + al.size());
			inicioMedida();
			al.remove ((Integer)numElem-1);
			finMedida("remove - jdk");
			al.add (numElem-1);
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			System.out.println ("Tamaño: " + tial1.size());
			inicioMedida();
			tial1.removePar (numElem-1, numProc);
			finMedida ("remove - nuevo");
			tial1.add (numElem-1);
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			System.out.println ("Tamaño: " + tial2.size());
			inicioMedida();
			tial2.remove (numElem-1);
			finMedida ("remove - trove");
			tial2.add (numElem-1);
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial1.reversePar (numProc);
			finMedida ("reverse - nuevo");
			tial1.clear();
			tial1.addAllPar (col, numProc);
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial2.reverse ();
			finMedida ("reverse - trove");
			tial2.clear();
			tial2.addAll (col);
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			al.subList (0, numElem);
			finMedida ("subList - jdk");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial1.subListPar (0, numElem, numProc);
			finMedida ("subList - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial1.subListPar2 (0, numElem, numProc);
			finMedida ("subList - nuevo2");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial2.subList (0, numElem);
			finMedida ("subList - trove");
		}
		System.out.println();
		
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial1.sumPar (numProc);
			finMedida ("sum - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial2.sum();
			finMedida ("sum - trove");
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			al.toArray();
			finMedida ("toArray - jdk");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial1.toArrayPar (numProc);
			finMedida ("toArray - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial2.toArray();
			finMedida ("toArray - trove");
		}
		System.out.println();
		
		System.out.println ("Tamaño : " + numElem2);
		System.out.println ("Número de procesadores : " + numProc);
		System.out.println ("Número de iteraciones: " + numIt);
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial3.containsAllPar (array2, numProc);
			finMedida ("containsAll (int[]) - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial3.containsAll (array2);
			finMedida ("containsAll (int[]) - trove");
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial3.containsAllPar (col2, numProc);
			finMedida ("containsAll (Collection) - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial3.containsAll (col2);
			finMedida ("containsAll (Collection) - trove");
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			al3.removeAll (col2);
			finMedida ("removeAll - jdk");
			al3.addAll (col2);
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial3.removeAllPar (col2, numProc);
			finMedida ("removeAll - nuevo");
			tial3.addAllPar (col2, numProc);
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial3.removeAll (col2);
			finMedida ("removeAll - trove");
			tial3.addAll (col2);
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			al3.retainAll (col2);
			finMedida ("retainAll - jdk");
			al3.clear();
			al3.addAll (col2);
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial3.retainAllPar (col2, numProc);
			finMedida ("retainAll - nuevo");
			tial3.clear();
			tial3.addAll (col2);
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial3.retainAll (col2);
			finMedida ("retainAll - trove");
			tial3.clear();
			tial3.addAll (col2);
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			System.out.println ("Tamaño: " + tial3.size());
			inicioMedida();
			tial3.shufflePar (r, numProc);
			finMedida ("shuffle - nuevo");
			tial3.clear();
			tial3.addAllPar (col2, numProc);
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			System.out.println ("Tamaño: " + tial3.size());
			inicioMedida();
			tial3.shuffle (r);
			finMedida ("shuffle - trove");
			tial3.clear();
			tial3.addAll (col2);
		}
		System.out.println();

		for (int i=0; i<numIt; i++)	{
			System.out.println ("Tamaño: " + tial3.size());
			inicioMedida();
			tial3.toStringPar (numProc);
			finMedida ("toString - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			System.out.println ("Tamaño: " + tial3.size());
			inicioMedida();
			tial3.toString();
			finMedida ("toString - trove");
		}
		System.out.println();
		
		/**for (int i=0; i<numIt; i++)	{
			inicioMedida();
			finMedida (" - jdk");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			finMedida (" - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			finMedida (" - trove");
		}
		System.out.println();*/
		}
	}
}
