import paralel.*;
import java.util.*;
public class Prueba {
	private static HashMap<Integer,Integer> hm;
	private static TIntIntHashMap tiihm;
	private static TIntIntHashMap tiihm2;
	private static TIntIntHashMap tiihm3;
	private static int numElem = 100000;
	private static int numProcs = 2;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Creación
		hm = new HashMap<Integer,Integer>();
		tiihm = new TIntIntHashMap();
		tiihm2 = new TIntIntHashMap();
		tiihm3 = new TIntIntHashMap();
		long startTime = System.nanoTime(); 
		//long startHeapSize = Runtime.getRuntime().freeMemory();
		//Put en HashMap
		for (int i=0; i<numElem; i++)	hm.put(i*2, i*2);
		System.out.println("Tiempo de put en jdk " + (System.nanoTime() - startTime) / 1000000000.0 );
		//startTime = System.nanoTime();
		//HashMap hm2 = new HashMap (hm);
		//System.out.println("Tiempo de putAll jdk " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		tiihm.putAll(hm, numProcs);
		System.out.println("Tiempo de putAll (HashMap) nuevo " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		tiihm2.putAll(hm);
		System.out.println("Tiempo de putAll (HashMap) trove " + (System.nanoTime() - startTime) / 1000000000.0 );
		System.out.println ();
		
		//Comprobación de que el putAll funciona
		startTime = System.nanoTime();
		if (tiihm.equals((Object)tiihm2))	System.out.println ("Son iguales");
		else System.out.println ("Son distintos");
		System.out.println("Tiempo equals trove " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		if (tiihm.equals((Object) tiihm2, numProcs)) System.out.println ("Son iguales");
		else System.out.println ("Son distintos");
		System.out.println("Tiempo equals nuevo " + (System.nanoTime() - startTime) / 1000000000.0 );
		System.out.println ();
		
		/**System.out.println ("Tamaño jdk: " + hm.size());
		System.out.println ("Tamaño nuevo: " + tiihm.size());
		System.out.println ("Tamaño trove: " + tiihm2.size());*/
		startTime = System.nanoTime();
		for (int i=0; i<numElem/50; i+=50)
			if (!hm.containsValue(i)) System.out.println ("No está");
		System.out.println("Tiempo de containsValue jdk " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		for (int i=0; i<numElem/50; i+=50)
			if (!tiihm.containsValue(i, numProcs)) System.out.println ("No está");
		System.out.println("Tiempo de containsValue nuevo " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		for (int i=0; i<numElem/50; i+=50)
			if (!tiihm2.containsValue(i)) System.out.println ("No está");
		System.out.println("Tiempo de containsValue trove " + (System.nanoTime() - startTime) / 1000000000.0 );
		System.out.println();
		
		startTime = System.nanoTime();
		System.out.println (hm.hashCode());
		System.out.println("Tiempo de hashCode jdk " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		System.out.println (tiihm.hashCode(numProcs));
		System.out.println("Tiempo de hashCode nuevo " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		System.out.println (tiihm2.hashCode());
		System.out.println("Tiempo de hashCode trove " + (System.nanoTime() - startTime) / 1000000000.0 );
		System.out.println ();
		
		startTime = System.nanoTime();
		tiihm.keys(numProcs);
		System.out.println("Tiempo de keys1 " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		tiihm2.keys(numProcs);
		System.out.println("Tiempo de keys2 " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		tiihm2.keys();
		System.out.println("Tiempo de keys trove " + (System.nanoTime() - startTime) / 1000000000.0 );
		System.out.println ();
		
		//Prueba de toString
		startTime = System.nanoTime();
		tiihm.toString();
		System.out.println("Tiempo de toString jdk " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		tiihm.toString(numProcs);
		System.out.println("Tiempo de toString nuevo " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		tiihm2.toString();
		System.out.println("Tiempo de toString trove " + (System.nanoTime() - startTime) / 1000000000.0 );
		//if (string1.equals (string2))	System.out.println ("Strings iguales");
		//else	{
		//	System.out.println ("Strings distintos");
			//System.out.println (string1);
			//System.out.println (string2);
		//}
		System.out.println ();
		
		startTime = System.nanoTime();
		//tiihm.pruebaRehash(10000000);
		System.out.println("Tiempo de rehash nuevo " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		//tiihm2.pruebaRehash2(10000000);
		System.out.println("Tiempo de rehash trove " + (System.nanoTime() - startTime) / 1000000000.0 );
		System.out.println ();
		
		System.out.println ("Tamaño hashMap antes de clear: " + hm.size());
		System.out.println ("Tamaño nuevo antes de clear: " + tiihm.size());
		System.out.println ("Tamaño trove antes de clear: " + tiihm2.size());
		startTime = System.nanoTime();
		hm.clear();
		System.out.println("Tiempo de clear jdk " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		tiihm.clear3();
		System.out.println("Tiempo de clear nuevo " + (System.nanoTime() - startTime) / 1000000000.0 );
		startTime = System.nanoTime();
		tiihm2.clear2();
		System.out.println("Tiempo de clear trove " + (System.nanoTime() - startTime) / 1000000000.0 );
		System.out.println ("Tamaño hashMap después de clear: " + hm.size());
		System.out.println ("Tamaño nuevo después de clear: " + tiihm.size());
		System.out.println ("Tamaño trove después de clear: " + tiihm2.size());
	}

}
