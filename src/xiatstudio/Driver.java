package xiatstudio;

public class Driver {
	String driverName;
	int driverNumber;
	int driverPoints;
	Team driverTeam;
	
	public Driver(String name) {
		this.driverName = name;
		this.driverNumber = 0;
		this.driverPoints = 0;
		this.driverTeam = new Team("TEAMNAME");
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
	
	@Override
	public String toString() {
		return (String.valueOf(this.driverNumber) + " " + this.driverName + " " + this.driverTeam.getName());
	}
}
