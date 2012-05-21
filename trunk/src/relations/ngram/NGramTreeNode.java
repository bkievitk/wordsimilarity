package relations.ngram;
import java.io.Serializable;
import java.util.Vector;


public class NGramTreeNode implements Serializable {
	
	private static final long serialVersionUID = -4001797736520515853L;
	
	private long count = 0;
	private int id;
	private Vector<NGramTreeNode> children = new Vector<NGramTreeNode>();
	private NGramTreeNode parent;
	
	public void show(int depth) {
		for(int i=0;i<depth;i++) {
			System.out.print(" ");
		}
		System.out.println(id);
		for(NGramTreeNode child : children) {
			child.show(depth+1);
		}
	}
	
	public long getCount() {
		return count;
	}

	public void incrementCount() {
		count++;
	}
	
	public void setCount(long count) {
		this.count = count;
	}
		
	public String getDescription(NGramTree tree) {
		if(id <= 0) {
			return "";
		}
		return tree.wordSet.get(id);
	}
	
	public int getChildCount() {
		return children.size();
	}
	
	public NGramTreeNode getChild(int i) {
		return children.get(i);
	}
	
	public NGramTreeNode getParent() {
		return parent;
	}

	public int getChildID(int id) {
		for(int i=0;i<children.size();i++) {
			if(children.get(i).id == id) {
				return i;
			}
		}
		NGramTreeNode newNode = new NGramTreeNode(id,this);
		children.add(newNode);
		return children.size()-1;
	}

	public void addChild(NGramTreeNode node, int limit) {
		
		int i=0;
		for(;i<children.size();i++) {
			if(node.count > children.get(i).count) {
				break;
			}
		}
		children.insertElementAt(node, i);
		while(children.size() > limit) {
			children.remove(children.size()-1);
		}
	}
	
	public void trimChildren(int limit) {
		while(children.size() > limit) {
			children.remove(children.size()-1);
		}
	}
	
	public int updateChild(int index) {
		children.get(index).count++;
		for(;index > 0;index--) {
			if(children.get(index-1).count < children.get(index).count) {
				NGramTreeNode tmp = children.get(index);
				children.set(index, children.get(index-1));
				children.set(index-1, tmp);
			} else {
				return index;
			}
		}
		return index;
	}
	
	public NGramTreeNode(int id, NGramTreeNode parent) {
		this.id = id;
		this.parent = parent;
	}
	
	public String getSentence(NGramTree tree) {
		if(parent == null) {
			return "";
		} else {
			if(parent.parent == null) {
				return tree.wordSet.get(id);
			} else {
				return parent.getSentence(tree) + " " + tree.wordSet.get(id);
			}
		}
	}
	
}
