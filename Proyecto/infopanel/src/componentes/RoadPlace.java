package componentes;

public class RoadPlace {

	protected String road = null;
	protected int m = 0;
	
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
	
	public String getRoad() {
		return road;
	}
	
	public void setRoad(String road) {
		this.road = road;
	}
	
}
