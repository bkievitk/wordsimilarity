package tools;


/**
* Stemmer, implementing the Porter Stemming Algorithm
*
* The Stemmer class transforms a word into its root form.  The input
* word can be provided a character at time (by calling add()), or at once
* by calling one of the various stem(something) methods.
*/

public class StemmerPorter {
		
	/* cons(i) is true <=> b[i] is a consonant. */
	
	private static final boolean cons(int i, char[] b) {
		switch (b[i]) {
			case 'a': case 'e': case 'i': case 'o': case 'u': return false;
			case 'y': return (i==0) ? true : !cons(i-1, b);
			default: return true;
	   }
	}
	
	/* m() measures the number of consonant sequences between 0 and j. if c is
	   a consonant sequence and v a vowel sequence, and <..> indicates arbitrary
	   presence,
	
	      <c><v>       gives 0
	      <c>vc<v>     gives 1
	      <c>vcvc<v>   gives 2
	      <c>vcvcvc<v> gives 3
	      ....
	*/
	
	private static final int m(char[] b, int j) {
		int n = 0;
		int i = 0;
		while(true) {  
			if (i > j) {
				return n;
			}
			if (! cons(i, b)) {
				break;
			}
			i++;
	   }
	   i++;
	   
	   while(true) {
		   while(true) {
			   if (i > j) {
				   return n;
			   }
			   if (cons(i, b)) {
				   break;
			   }
			   i++;
	      }
		   
	      i++;
	      n++;
	      while(true) {
	    	  if (i > j) {
	    		  return n;
	    	  }
	    	  if (! cons(i, b)) {
	    		  break;
	    	  }
	    	  i++;
	      }
	      i++;
	    }
	}
	
	/* vowelinstem() is true <=> 0,...j contains a vowel */
	
	private static final boolean vowelinstem(char[] b, int j)	{
		int i; 
		for (i = 0; i <= j; i++) {
			if (! cons(i,b)) {
				return true;
			}
		}
		return false;
	}
	
	/* doublec(j) is true <=> j,(j-1) contain a double consonant. */
	
	private static final boolean doublec(int j, char[] b) { 
		if (j < 1) {
			return false;
		}
		
		if (b[j] != b[j-1]) {
			return false;
		}
		
		return cons(j, b);
	}
	
	/* cvc(i) is true <=> i-2,i-1,i has the form consonant - vowel - consonant
	   and also if the second c is not w,x or y. this is used when trying to
	   restore an e at the end of a short word. e.g.
	
	      cav(e), lov(e), hop(e), crim(e), but
	      snow, box, tray.
	
	*/
	
	private static final boolean cvc(int i, char[] b) { 
		if (i < 2 || !cons(i, b) || cons(i-1, b) || !cons(i-2, b)) {
			return false;
		}
		
		int ch = b[i];
		if (ch == 'w' || ch == 'x' || ch == 'y') {
			return false;
		}
		return true;
	}
	
	private static final boolean ends(String s, int[] j, int[] k, char[] b) {
		int l = s.length();
		int o = k[0]-l+1;
		if (o < 0) {
			return false;
		}
		for (int i = 0; i < l; i++) {
			if (b[o+i] != s.charAt(i)) { 
				return false;
			}
		}
		j[0] = k[0]-l;
		return true;
	}
	
	/* setto(s) sets (j+1),...k to the characters in the string s, readjusting
	   k. */
	
	private static final void setto(String s, int[] j, int k[], char[] b) {  
		int l = s.length();
		int o = j[0]+1;
		for (int i = 0; i < l; i++) {
			b[o+i] = s.charAt(i);
		}
		k[0] = j[0]+l;
	}
	
	/* r(s) is used further down. */
	
	private static final void r(String s, int[] j, int k[], char[] b) { 
		if (m(b,j[0]) > 0) {
			setto(s, j, k, b); 
		}
	}
	
	/* step1() gets rid of plurals and -ed or -ing. e.g.
	
	       caresses  ->  caress
	       ponies    ->  poni
	       ties      ->  ti
	       caress    ->  caress
	       cats      ->  cat
	
	       feed      ->  feed
	       agreed    ->  agree
	       disabled  ->  disable
	
	       matting   ->  mat
	       mating    ->  mate
	       meeting   ->  meet
	       milling   ->  mill
	       messing   ->  mess
	
	       meetings  ->  meet
	
	*/
	
	private static final void step1(int[] j, int k[], char[] b) {
		if (b[k[0]] == 's') {
			if (ends("sses",j,k,b)) {
				k[0] -= 2;
			} else if (ends("ies",j,k,b)) {
				setto("i",j,k,b);
			} else if (b[k[0]-1] != 's') {
				k[0]--;
			}
	   }
	   if (ends("eed",j,k,b)) { 
		   if (m(b,j[0]) > 0) { 
			   k[0]--; 
		   }
	   } else if ((ends("ed",j,k,b) || ends("ing",j,k,b)) && vowelinstem(b,j[0])) {
		   k = j;
		   if (ends("at",j,k,b)) {
			   setto("ate",j,k,b);
		   } else if (ends("bl",j,k,b)) {
			   setto("ble",j,k,b);
		   } else if (ends("iz",j,k,b)) {
			   setto("ize",j,k,b);
		   } else if (doublec(k[0],b)) {
			   k[0]--;
			   int ch = b[k[0]];
			   if (ch == 'l' || ch == 's' || ch == 'z') {
				   k[0]++;
			   }
		   } else if (m(b,j[0]) == 1 && cvc(k[0],b)) {
			   setto("e",j,k,b);
		   }
	  }
	}
	
	/* step2() turns terminal y to i when there is another vowel in the stem. */
	
	private static final void step2(int[] j, int[] k, char[] b) { 
		if (ends("y",j,k,b) && vowelinstem(b,j[0])) {
			b[k[0]] = 'i'; 
		}
	}
	
	/* step3() maps double suffices to single ones. so -ization ( = -ize plus
	   -ation) maps to -ize etc. note that the string before the suffix must give
	   m() > 0. */
	
	private static final void step3(int[] j, int[] k, char[] b) { 
		if (k[0] == 0) {
			return;
		}
		/* For Bug 1 */ 
		switch (b[k[0]-1]) {
		    case 'a': if (ends("ational",j,k,b)) { r("ate",j,k,b); break; }
		              if (ends("tional",j,k,b)) { r("tion",j,k,b); break; }
		              break;
		    case 'c': if (ends("enci",j,k,b)) { r("ence",j,k,b); break; }
		              if (ends("anci",j,k,b)) { r("ance",j,k,b); break; }
		              break;
		    case 'e': if (ends("izer",j,k,b)) { r("ize",j,k,b); break; }
		              break;
		    case 'l': if (ends("bli",j,k,b)) { r("ble",j,k,b); break; }
		              if (ends("alli",j,k,b)) { r("al",j,k,b); break; }
		              if (ends("entli",j,k,b)) { r("ent",j,k,b); break; }
		              if (ends("eli",j,k,b)) { r("e",j,k,b); break; }
		              if (ends("ousli",j,k,b)) { r("ous",j,k,b); break; }
		              break;
		    case 'o': if (ends("ization",j,k,b)) { r("ize",j,k,b); break; }
		              if (ends("ation",j,k,b)) { r("ate",j,k,b); break; }
		              if (ends("ator",j,k,b)) { r("ate",j,k,b); break; }
		              break;
		    case 's': if (ends("alism",j,k,b)) { r("al",j,k,b); break; }
		              if (ends("iveness",j,k,b)) { r("ive",j,k,b); break; }
		              if (ends("fulness",j,k,b)) { r("ful",j,k,b); break; }
		              if (ends("ousness",j,k,b)) { r("ous",j,k,b); break; }
		              break;
		    case 't': if (ends("aliti",j,k,b)) { r("al",j,k,b); break; }
		              if (ends("iviti",j,k,b)) { r("ive",j,k,b); break; }
		              if (ends("biliti",j,k,b)) { r("ble",j,k,b); break; }
		              break;
		    case 'g': if (ends("logi",j,k,b)) { r("log",j,k,b); break; }
		} 
	}
	
	/* step4() deals with -ic-, -full, -ness etc. similar strategy to step3. */
	
	private static final void step4(int[] j, int[] k, char[] b) { 
		
		switch (b[k[0]]) {
		    case 'e': if (ends("icate",j,k,b)) { r("ic",j,k,b); break; }
		              if (ends("ative",j,k,b)) { r("",j,k,b); break; }
		              if (ends("alize",j,k,b)) { r("al",j,k,b); break; }
		              break;
		    case 'i': if (ends("iciti",j,k,b)) { r("ic",j,k,b); break; }
		              break;
		    case 'l': if (ends("ical",j,k,b)) { r("ic",j,k,b); break; }
		              if (ends("ful",j,k,b)) { r("",j,k,b); break; }
		              break;
		    case 's': if (ends("ness",j,k,b)) { r("",j,k,b); break; }
		              break;
		} 
	}
	
	/* step5() takes off -ant, -ence etc., in context <c>vcvc<v>. */
	
	private static final void step5(int[] j, int[] k, char[] b) {
		if (k[0] == 0) {
			return;
		}
		
		/* for Bug 1 */ 
		switch (b[k[0]-1]) {
			case 'a': 	if (ends("al",j,k,b)) break; return;
			case 'c': 	if (ends("ance",j,k,b)) break;
						if (ends("ence",j,k,b)) break; return;
			case 'e': 	if (ends("er",j,k,b)) break; return;
			case 'i': 	if (ends("ic",j,k,b)) break; return;
			case 'l': 	if (ends("able",j,k,b)) break;
	                 	if (ends("ible",j,k,b)) break; return;
			case 'n': 	if (ends("ant",j,k,b)) break;
	                 	if (ends("ement",j,k,b)) break;
	                 	if (ends("ment",j,k,b)) break;
	                 	/* element etc. not stripped before the m */
	                 	if (ends("ent",j,k,b)) break; return;
			case 'o': 	if (ends("ion",j,k,b) && j[0] >= 0 && (b[j[0]] == 's' || b[j[0]] == 't')) break;
	                                 /* j >= 0 fixes Bug 2 */
						if (ends("ou",j,k,b)) break; return;
						/* takes care of -ous */
			case 's': 	if (ends("ism",j,k,b)) break; return;
			case 't': 	if (ends("ate",j,k,b)) break;
	                 	if (ends("iti",j,k,b)) break; return;
			case 'u': 	if (ends("ous",j,k,b)) break; return;
			case 'v': 	if (ends("ive",j,k,b)) break; return;
			case 'z': 	if (ends("ize",j,k,b)) break; return;
			default: return;
	    }
	    if (m(b,j[0]) > 1) { 
	    	k[0] = j[0];
	    }
	}
	
	/* step6() removes a final -e if m() > 1. */
	
	private static final void step6(int[] j, int[] k, char[] b) {
		j[0] = k[0];
		if (b[k[0]] == 'e') {
			int a = m(b,j[0]);
			if (a > 1 || a == 1 && !cvc(k[0]-1,b)) {
				k[0]--;
			}
		}
		if (b[k[0]] == 'l' && doublec(k[0],b) && m(b,j[0]) > 1) {
			k[0]--;
		}
	}
	
	/** Stem the word placed into the Stemmer buffer through calls to add().
	 * Returns true if the stemming process resulted in a word different
	 * from the input.  You can retrieve the result with
	 * getResultLength()/getResultBuffer() or toString().
	 */
	private static String stem(int[] j, int[] k, char[] b) {
		k[0] = b.length - 1;
		if (k[0] > 1) { 
			step1(j,k,b); 
			step2(j,k,b); 
			step3(j,k,b); 
			step4(j,k,b); 
			step5(j,k,b); 
			step6(j,k,b); 
		}
		int i_end = k[0] + 1;
		return new String(b).substring(0, i_end);
	}
	
	public static String stem(String str) {
		return stem(new int[1], new int[1], str.toCharArray());
	}

	public String getName() {
		return "Porter Stemmer";
	}
}

