package relations.ngram;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JFileChooser;
import tools.TASA;
import tools.TASABook;

public class NGramTree implements Serializable {

	private static final long serialVersionUID = -3363655386814543638L;
	
	public Hashtable<String,Integer> idSet = new Hashtable<String,Integer>();
	public Vector<String> wordSet = new Vector<String>();
	public NGramTreeNode root = new NGramTreeNode(-1,null);
		
	public void show() {
		root.show(0);
	}
		
	public static void compress(NGramTreeNode node, int childLimit) {
		node.trimChildren(childLimit);
		for(int i=0;i<node.getChildCount();i++) {
			compress(node.getChild(i),childLimit);
		}
	}
	
	public static NGramTree load(String file) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(file)));
			return (NGramTree)in.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void buildTASA(int nGramLimit) {
		NGramTree tree = null;

		JFileChooser choose = new JFileChooser(new File("."));
		
		choose.showOpenDialog(null);
		
		File f = choose.getSelectedFile();
		if(f == null) {
			return;
		}
		else if(f.getName().endsWith(".txt")) {
			tree = new NGramTree();
	
			TASA tasa = new TASA(f);
			TASABook book;
			
			int bookCount = 0;
			
			// Read until no more books.
			while((book = tasa.readBook()) != null) {
				if(bookCount > 200) {
					break;
				}
				System.out.println("Reading book " + bookCount + " of 37649");
				for(String sentence : book.sentences) {
					tree.learn(sentence,nGramLimit);
				}
				bookCount++;
			}
			
			choose.showSaveDialog(null);
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(choose.getSelectedFile()));
				out.writeObject(tree);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else if(f.getName().endsWith(".dat")){
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
				tree = (NGramTree)in.readObject();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			return;
		}		                
	}
	
	public void learn(String sentence, int nGramLimit) {
		
		sentence = sentence.toLowerCase();
		sentence = sentence.replaceAll("[^a-z ]", "");
		sentence = sentence.replaceAll(" +", " ");
		
		String[] strings = sentence.split(" ");
		
		if(strings.length > nGramLimit) {
			String[] tmp = new String[nGramLimit];
			for(int i=0;i<tmp.length;i++) {
				tmp[i] = strings[i];
			}
			strings = tmp;
		}
		
		int[] ids = new int[strings.length];
		for(int i=0;i<ids.length;i++) {
			Integer id = idSet.get(strings[i]);
			if(id == null) {
				id = idSet.size();
				idSet.put(strings[i], id);
				wordSet.add(strings[i]);
			}
			ids[i] = id;
		}
				
		//for(int i=0;i<ids.length-1;i++) {
		//	learn(ids,i);
		//}
		learn(ids,0);
	}
	
	public Integer getID(String string) {
		Integer id = idSet.get(string);
		if(id == null) {
			id = idSet.size();
			idSet.put(string, id);
			wordSet.add(string);
		}
		return id;
	}
	
	public void learn(int[] sentence, int start) {
		NGramTreeNode node = root;
		root.incrementCount();
		for(int i=start;i<sentence.length;i++) {
			int id = node.getChildID(sentence[i]);
			id = node.updateChild(id);
			node = node.getChild(id);
		}
	}
	
	public void printSimple() {
		printSimple(root,0);
	}
	
	public void printSimple(NGramTreeNode node, int deapth) {
		for(int i=0;i<deapth;i++) {
			System.out.print(" ");
		}
		
		if(node.getDescription(this) == null) {
			System.out.println("ROOT (" + node.getCount() + ")");
		} else {
			System.out.println(node.getDescription(this) + " (" + node.getCount() + ")");
		}
		
		for(int i=0;i<node.getChildCount();i++) {
			NGramTreeNode child = node.getChild(i);
			printSimple(child, deapth+1);
		}
	}
}
