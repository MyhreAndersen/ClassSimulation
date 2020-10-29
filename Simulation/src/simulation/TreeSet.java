package simulation;

public class TreeSet {
	private Node head;
	
	public TreeSet() {
		head=new Node();
		head.bl = head;
		head.ll = head;
		head.rl = head;
	}
	
	public void clear() {
		head=new Node();
		head.bl = head;
		head.ll = head;
		head.rl = head;
	}
	
    public  boolean empty() {
        if(head.rl != head) IERR("RANK_EMPTY");
        return(head.bl == head);
    }

    public Node first() {
        if(head.rl != head) IERR("RANK_FIRST");
        Node first = head.bl;
        if(first == head) first=null;
        return(first);
    }

    public Node last() {
        if(head.rl != head) IERR("RANK_LAST");
        Node last = head.ll;
        if((last == (head))) last=null;
        return(last);
    }

	
	class Node {
		Node bl,ll,rl;
		double rnk;
		
		
	    public Node pred(Node ins) {
	        Node prd=null; // Return value
	        if(ins.rl == ins) {
	            prd = null;
	        } else if(ins.rl != null) {
	            prd = ins.rl;
	        } else if(ins.ll != null) {
	            prd = ins.ll;
	            if(prd.rl == prd) {
	                prd = null;
	            }
	        } else if(ins.bl == null) {
	            prd = null;
	        } else {
	            prd = ins;
	            while(prd.bl.ll == prd) {
	                prd = prd.bl;
	            }
	            prd = prd.bl.ll;
	        }
	        return(prd);
	    }

	    public Node suc(Node ins) {
	        if(ins.bl == null) return(null);
	        if(ins.bl.rl == ins.bl) return(null);
	        Node suc = ins.bl;
	        if(suc.rl != ins && suc.rl != null) {
	        	suc = suc.rl;
	        	while(suc.ll != null) suc = suc.ll;
	        }
	        return(suc);
	    }

	    public void follow(Node prd) {
	    	Node ins=this;
	    	if(ins.rl == ins) IERR("RANK_FOLLOW");
	    	if(ins.bl != null) this.out();
	    	if(prd != null && prd.bl != null) {
	    		ins.rnk = prd.rnk;
	    		if(prd == prd.bl.ll)
	    			 prd.bl.ll = ins;
	    		else prd.bl.rl = ins;
	    		ins.ll = prd;
	    		ins.bl = prd.bl;
	    		prd.bl = ins;
	    	}
	    }
	    
	    public void precede(Node suc) {
	    	Node ins=this;
	        if(ins.rl == ins) IERR("RANK_PRECEDE-1");
	        if(ins.bl != null) out();
	        if(suc != null) {
	            if(suc.rl == suc) IERR("RANK_PRECEDE-2");
	            if(suc.bl != null) {
	                ins.rnk = suc.rnk;
	                ins.bl = suc;
	                ins.ll = suc.ll;
	                ins.rl = suc.rl;
	                suc.ll = ins;
	                suc.rl = null;
	                if(ins.ll != null) {
	                    ins.ll.bl = ins;
	                    if(ins.rl != null) ins.rl.bl = ins;
	                }
	            }
	        }
	    }
	    
	    public void into(double rnk) {
	    	Node ins=this;
	        if(ins.rl == ins) IERR("RANK_INTO-1");
	        if(ins.bl != null) out();
	        if((head != (null))) {
	            if(head.rl != head) IERR("RANK_INTO-2");
	            ins.rnk = rnk;
	            if(rnk >= head.ll.rnk) {
	                ins.bl = head;
	                ins.ll = head.ll;
	                head.ll.bl = ins;
	                head.ll = ins;
	            } else if(rnk < head.bl.rnk) {
	                ins.ll = head;
	                ins.bl = head.bl;
	                head.bl.ll = ins;
	                head.bl = ins;
	            } else {
	            	Node e = head.ll;

	            	LOOP: while(true) {
	            		//LABEL$(0,"L1");
	            		//LABEL$(0,"L2");
	            		if((e.ll == (null))) {
	            			e.ll = ins;
	            		} else if(rnk < e.ll.rnk) {
	            			e = e.ll;
	            			continue LOOP; //GOTO$(L1);
	            		} else if((e.rl == (null))) {
	            			e.rl = ins;
	            		} else if(rnk < e.rl.rnk) {
	            			e = e.rl;
	            			continue LOOP; //GOTO$(L2);
	            		} else {
	            			ins.ll = e.rl;
	            			e.rl.bl = ins;
	            			e.rl = ins;
	            		}
	            		break LOOP;
	            	}
	            	ins.bl = e;
	            }
	        }
	    }
	    
	    public void intoPrior(double rnk) {
	    	Node ins=this;
	        if(ins.rl == ins) IERR("RANK_PRIOR-1");
	        if(ins.bl != null) out();
	        if((head != (null))) {
	            if(head.rl != head) IERR("RANK_PRIOR-2");
	            ins.rnk = rnk;
	            if(rnk > head.ll.rnk) {
	                ins.bl = head;
	                ins.ll = head.ll;
	                head.ll.bl = ins;
	                head.ll = ins;
	            } else if(rnk <= head.bl.rnk) {
	                ins.ll = head;
	                ins.bl = head.bl;
	                head.bl.ll = ins;
	                head.bl = ins;
	            } else {
	            	Node e = head.ll;
	                
	                LOOP: while(true) {
	                	//LABEL$(0,"L1");
	                	//LABEL$(0,"L2");
	                	if(e.ll == null) {
	                		e.ll = ins;
	                	} else if(rnk <= e.ll.rnk) {
	                		e = e.ll;
	                		continue LOOP; //GOTO$(L1);
	                	} else if(e.rl == null) {
	                		e.rl = ins;
	                	} else if(rnk <= e.rl.rnk) {
	                		e = e.rl;
	                		continue LOOP; //GOTO$(L2);
	                	} else {
	                		ins.ll = e.rl;
	                		e.rl.bl = ins;
	                		e.rl = ins;
	                	}
	                	break LOOP;
	                }
	                
	                ins.bl = e;
	            }
	        }
	    }

	    public void out() {
	    	Node ins=this;
	    	Node suc=null;
	    	if(ins.rl == ins) IERR("RANK_OUT");
	    	if(ins.bl == null) return;
	    	Node bl = ins.bl;
	    	Node ll = ins.ll;
	    	Node rl = ins.rl;
	    	if((ll == (null))) {
	    		if(bl.ll == ins) bl.ll = bl.rl;
	    		bl.rl = null;
	    	} else if((ll.rl == (ll))) {
	    		if(bl == ll) {
	    			ll.ll = ll;
	    			ll.bl = ll;
	    		} else {
	    			bl.ll = bl.rl;
	    			bl.rl = null;
	    			suc = bl;
	    			while(suc.ll != null) suc = suc.ll;
	    			ll.bl = suc;
	    			suc.ll = ll;
	    		}
	    	} else if((rl == (null))) {
	    		if(bl.ll == ins)
	    			 bl.ll = ll;
	    		else bl.rl = ll;
	    		ll.bl = bl;
	    	} else {
	    		if(bl.ll == ins)
	    			 bl.ll = rl;
	    		else bl.rl = rl;
	    		rl.bl = bl;
	    		suc = rl;
	    		while(suc.ll != null) suc = suc.ll;
	    		ll.bl = suc;
	    		suc.ll = ll;
	    	}
	    	ins.bl = null;
	    	ins.ll = null;
	    	ins.rl = null;

	    }

	    
	}


    public static void IERR(String msg) {}

	
}
