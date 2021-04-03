package warehouse;

public class Dock {
	int nr;
	Van occupant;
	
	public Dock(int nr) {
		this.nr=nr;
	}
	
	public String toString() {
		return("Dock "+nr+" occupant="+((occupant==null)?"null":occupant.name));
	}

}
