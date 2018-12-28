package xiatstudio;

public class Driver {
	String driverName;
	int driverNumber;
	int driverPoints;
	Team driverTeam;
	
	public Driver() {
		this.driverName = "DRIVERNAME";
		this.driverNumber = 0;
		this.driverPoints = 0;
		this.driverTeam = new Team("TEAMNAME");
	}
}
