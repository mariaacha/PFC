import arrayList.*;
import java.util.*;

public class ProbarByteArrayList {
	private static Collection<Byte> col;
	private static Collection<Byte> col2;
	private static byte[] array;
	private static byte[] array2;
	private static ArrayList<Byte> al;
	private static ArrayList<Byte> al2;
	private static ArrayList<Byte> al3;
	private static TByteArrayList tial1;
	private static TByteArrayList tial2;
	private static TByteArrayList tial3;
	private static int numElem = 100000;
	private static int numElem2 = 100;
	private static int numProc;
	private static int numIt = 5;
	private static long startTime;
	private static long endTime;
	private static float tiempo;
	private static Random r = new Random();
	
	public static void inicializar ()	{
		col = new ArrayList<Byte> ();
		col2 = new ArrayList<Byte> ();
		array = new byte[numElem];
		array2 = new byte[numElem2];
		col.add((byte)0);
		array[0] = (byte)0;
		for (int i=1; i<numElem-1; i++)	{
			col.add((byte)(i%126+1));
			array[i] = (byte)(i%126);
		}
		col.add((byte)127);
		array[numElem-1] = (byte)127;
		col2.add((byte)0);
		array2[0] = (byte)0;
		for (int i=1; i<numElem2-1; i++)	{
			col2.add((byte)(i%126+1));
			array2[i] = (byte)(i%126+1);
		}
		col2.add((byte)127);
		array2[numElem2-1] = (byte)127;
		al = new ArrayList<Byte> ();
		al2 = new ArrayList<Byte> ();
		al2.addAll (col);
		al3 = new ArrayList<Byte> ();
		al3.addAll (col2);
		tial1 = new TByteArrayList ();
		tial2 = new TByteArrayList ();
		tial3 = new TByteArrayList ();
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
		System.out.println ("Byte máximo: " + Byte.MAX_VALUE);
		System.out.println ("Byte mínimo: " + Byte.MIN_VALUE);
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
			finMedida ("addAll (TByteArrayList - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{	
			tial2.clear();
			inicioMedida();
			tial2.addAll (tial1);
			finMedida ("addAll (TByteArrayList - trove");
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
			al.indexOf ((byte)127);
			finMedida("indexOf - jdk");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial1.indexOfPar ((byte)127, numProc);
			finMedida ("indexOf - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial2.indexOf ((byte)127);
			finMedida ("indexOf - trove");
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			al.lastIndexOf ((byte)0);
			finMedida("lastIndexOf - jdk");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial1.lastIndexOfPar ((byte)0, numProc);
			finMedida ("lastIndexOf - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			tial2.lastIndexOf ((byte)0);
			finMedida ("lastIndexOf - trove");
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			System.out.println ("Máximo: " + tial1.maxPar (numProc));
			finMedida ("max - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			System.out.println("Máximo: " + tial2.max());
			finMedida ("max - trove");
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			System.out.println("Mínimo: " + tial1.minPar (numProc));
			finMedida ("min - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			inicioMedida();
			System.out.println("Mínimo: " + tial2.min());
			finMedida ("min - trove");
		}
		System.out.println();
		
		for (int i=0; i<numIt; i++)	{
			System.out.println ("Tamaño: " + al.size());
			inicioMedida();
			al.remove (numElem-1);
			finMedida("remove - jdk");
			al.add ((byte)127);
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			System.out.println ("Tamaño: " + tial1.size());
			inicioMedida();
			tial1.removePar ((byte)127, numProc);
			finMedida ("remove - nuevo");
			tial1.add((byte)127);
			
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			System.out.println ("Tamaño: " + tial2.size());
			inicioMedida();
			tial2.remove ((byte)127);
			finMedida ("remove - trove");
			tial2.add ((byte)127);
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
			System.out.println (tial3.toStringPar (numProc));
			finMedida ("toString - nuevo");
		}
		System.out.println();
		for (int i=0; i<numIt; i++)	{
			System.out.println ("Tamaño: " + tial3.size());
			inicioMedida();
			System.out.println (tial3.toString());
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
