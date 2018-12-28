package xiatstudio;

public class Driver {
	String driverName;
	int driverNumber;
	int driverPoints;
	Team driverTeam;
	int laps;
	double time;
	int pos;
	
	public Driver(String name) {
		this.driverName = name;
		this.driverNumber = 0;
		this.driverPoints = 0;
		this.driverTeam = new Team("TEAMNAME");
		this.laps = 0;
		this.time = 0;
		this.pos = 0;
	}
	
	public void setName(String newName) {
		this.driverName = newName;
	}
	
	public String getName() {
		return this.driverName;
	}
	
	public void setNumber(int newNumber) {
		this.driverNumber = newNumber;
	}
	
	public int getNumber() {
		return this.driverNumber;
	}
	
	public void addPoints(int newPoints) {
		this.driverPoints += newPoints;
	}
	
	public int getPoints() {
		return this.driverPoints;
	}
	
	public void setTeam(Team newTeam) {
		this.driverTeam = newTeam;
	}
	
	public Team getTeam() {
		return this.driverTeam;
	}

	public void setPos(int newPos){
		this.pos = newPos;
	}

	public int getPos(){
		return this.pos;
	}

	public void setLap(int newLap){
		this.laps = newLap;
	}

	public int getLap(){
		return this.laps;
	}

	public void setTime(double newTime){
		this.time = newTime;
	}

	public double getTime(){
		return this.time;
	}
	
}
