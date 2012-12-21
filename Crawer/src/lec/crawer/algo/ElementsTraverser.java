package lec.crawer.algo;

import jodd.lagarto.dom.Node;

public class ElementsTraverser {
	private IAction<Node> action;
	public ElementsTraverser(IAction<Node> action){
		this.action=action;
	}
	
    public void traverse(Node node){
    	if(node==null){
    		return;
    	}
    	Node[] children=node.getChildNodes();
    	if(children==null||children.length==0){
    		return;
    	}    	
    	for(Node child : children){
    	    if(child.getChildNodesCount()>0){
    	    	traverse(child);
    	    }
    		action.doAction(child);
    	}  	
    }
}
