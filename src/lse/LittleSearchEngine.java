package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/	
		
		//hash map
		HashMap<String, Occurrence> occMap = new HashMap<String, Occurrence>();
		
		//scan document
		Scanner sc = new Scanner(new File(docFile));
			
		while(sc.hasNext()) {
				String nextWord = sc.next();
				//keyword separation
				nextWord = this.getKeyword(nextWord);
				System.out.println(nextWord);
				if(nextWord != null) {
					//exists so add
					if(occMap.containsKey(nextWord)) {
						Occurrence occurred = occMap.get(nextWord);
						occurred.frequency++;
					}
					//doesn't exist
					else {
						Occurrence occurred = new Occurrence(docFile,1);
						occMap.put(nextWord, occurred);
					}
					
				}
		}
		
		return occMap;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		
		for(String keyword: kws.keySet()) {
			//exists
			if(keywordsIndex.containsKey(keyword)) {
				keywordsIndex.get(keyword).add(kws.get(keyword));
				//calling iLO
				insertLastOccurrence(keywordsIndex.get(keyword));
			}
			//DNE
			else {
				ArrayList<Occurrence> occ = new ArrayList<Occurrence>();
				occ.add(kws.get(keyword));
				//master entry added
				keywordsIndex.put(keyword,occ);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		//System.out.println(word);
		if(word == null || word.length() < 0) {
			return null;
		}
		
		//case insensitive
		word = word.toLowerCase();
		
		for(int index = word.length()-1; index>=0; index--) {
			
			//'.', ',', '?', ':', ';' and '!' ... NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
			if((word.charAt(index)=='.')||(word.charAt(index)==',')||(word.charAt(index)=='?')||(word.charAt(index)==':')||(word.charAt(index)==';')||(word.charAt(index)=='!')) {
				if(index == 0) {
					return null;
				}
				//punctuation removal
				word = word.substring(0,index);
			}
			else {
				break;
			}
		}
		
		for(int index = word.length()-1; index>=0; index--) {
			//not all alphabets in the word, e.g. "word!!" will become "word", and "word?!?!" will also become "word"
			if(!(Character.isLetter(word.charAt(index)))){
				return null;
			}
		}
		
		//System.out.println("1");
		//is noise word?
		if(noiseWords.contains(word)) {
			//System.out.println("2");
			return null;
		}
				
		return word;
		
	}

	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		
		//null if size input list is 1
		if(occs.size() == 1) {
			return null;
		}
			
		//vars for binary search
		int first = 0;
		int middle = 0;
		//the elements 0..n-2 in the list are already in the correct order
		int last = occs.size()-2;			
		
		//return
		ArrayList<Integer> midPts = new ArrayList<Integer>();
			
		while(last >= first) {
			
			middle = (first+last)/2;
			midPts.add(middle);
			
			if((occs.get(middle).frequency) == (occs.get(occs.size()-1).frequency)){
				break;
			}
			
			else if((occs.get(middle).frequency) < (occs.get(occs.size()-1).frequency)){
				last = middle - 1;
			}
			
			else if((occs.get(middle).frequency) > (occs.get(occs.size()-1).frequency)){
				first = middle + 1;
				if(last <= middle) {
					middle++;
				}
			}
		}
		//finding the correct spot using binary search
		midPts.add(middle);
		//inserting at that spot
		Occurrence entry = occs.remove(occs.size()-1);
		occs.add(midPts.get(midPts.size()-1),entry);
		
		return midPts;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		/*
		 * example
		 * search is for "deep or world"
		 * 
		 * deep:  (A,2), (W,1)
		 * world: (W,7), (A,1)
		 * 
		 * result
		 * [WowCh1.txt, AliceCh1.txt]
		 */
		
		//kw1, kw2 to lower case
		kw1 = kw1.toLowerCase();
		kw2 = kw2.toLowerCase();
		
		//kw1
		ArrayList<Occurrence> keyW1 = keywordsIndex.get(kw1);
		//kw2
		ArrayList<Occurrence> keyW2 = keywordsIndex.get(kw2);
		//result
		ArrayList<String> top5 = new ArrayList<String>();
		//both or one kw in file
		ArrayList<Occurrence> bothOrOne = new ArrayList<Occurrence>();
		
		//no matches at all, result is null
		if((keyW1 == null) && (keyW2 == null)) {
			System.out.println("kw1, kw2 DNE");
			return null;
		}
		
		//has kw1 not kw2
		else if((keywordsIndex.containsKey(kw1)) && !(keywordsIndex.containsKey(kw2))) {
			bothOrOne.addAll(keyW1);
		}
		
		//has kw2 not kw1
		else if(!(keywordsIndex.containsKey(kw1)) && (keywordsIndex.containsKey(kw2))) {
			bothOrOne.addAll(keyW2);
		}
		
		//has both
		else if((keywordsIndex.containsKey(kw1)) && (keywordsIndex.containsKey(kw2))) {
			bothOrOne.addAll(keyW1);
			bothOrOne.addAll(keyW2);
		}
			
		int top = 0;
		int temp = 0;
			
		String currDoc = "";
			
		while(top5.size()<5) {
				
			for(int i = 0; i < bothOrOne.size(); i++) {
				
				if(bothOrOne.get(i).frequency > top) {
					top = bothOrOne.get(i).frequency;
					currDoc = bothOrOne.get(i).document;
					temp = i;
				}
					
			}
			
			//doc DNE?
			if(!(top5.contains(currDoc))) {
				top5.add(currDoc);
			}
			
			top = 0;
			bothOrOne.remove(temp);	
			//empty?
			System.out.println(bothOrOne);
			if(bothOrOne.size() == 0) {
				break;
			}
		}
		
		System.out.println(top5);
		return top5;
	}
}
