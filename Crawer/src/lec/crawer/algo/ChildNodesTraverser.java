package lec.crawer.algo;

import jodd.lagarto.dom.Node;

public class ChildNodesTraverser {

	private IAction<Node> action;
	public ChildNodesTraverser(IAction<Node> action){
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
    		action.doAction(child);
    	}  	
    }
	
}
