package componentes;

public class RoadPlace {

	protected String road = null;
	protected int m = 0;
	protected int start = 0;
	protected int end   = 0;
	
	public RoadPlace(String road, int m) {
		this.road = road;
		this.m = m;
	}
	
	public void setM(int m) {
		this.m = m;
	}
	
	public int getM() {
		return m;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public int getStart() {
		return start;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public int getEnd() {
		return end;
	}
	
	public String getRoad() {
		return road;
	}
	
	public void setRoad(String road) {
		this.road = road;
	}
	
}
