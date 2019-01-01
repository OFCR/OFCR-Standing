package xiatstudio;

public class Driver {
	String driverName;
	int driverNumber;
	int driverPoints;
	Team driverTeam;
	int laps;
	double time;
	int pos;
	double PB;
	int positionGain;
	int seasonStarts;
	int winCounts;
	int podiumCounts;
	int pointCounts;
	int dnfCounts;

	public Driver(String name) {
		this.driverName = name;
		this.driverNumber = 0;
		this.driverPoints = 0;
		this.driverTeam = new Team("TEAMNAME");
		this.laps = 0;
		this.time = 0;
		this.pos = 0;
		this.PB = 0;
		this.positionGain = 0;
		this.seasonStarts = 0;
		this.winCounts = 0;
		this.podiumCounts = 0;
		this.pointCounts = 0;
		this.dnfCounts = 0;
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

	public void setPos(int newPos) {
		this.pos = newPos;
	}

	public int getPos() {
		return this.pos;
	}

	public void setLap(int newLap) {
		this.laps = newLap;
	}

	public int getLap() {
		return this.laps;
	}

	public void setTime(double newTime) {
		this.time = newTime;
	}

	public double getTime() {
		return this.time;
	}

	public void setPB(double newPB) {
		this.PB = newPB;
	}

	public double getPB() {
		return this.PB;
	}

	public void setPosGain(int gain) {
		this.positionGain = gain;
	}

	public int getPosGain() {
		return this.positionGain;
	}

	public void regStart() {
		this.seasonStarts++;
	}

	public int getStart() {
		return this.seasonStarts;
	}
	
	public void resetStart() {
		this.seasonStarts = 0;
	}

	public void regWin() {
		this.winCounts++;
	}

	public int getWin() {
		return this.winCounts;
	}
	
	public void resetWin() {
		this.winCounts = 0;
	}

	public void regPodium() {
		this.podiumCounts++;
	}

	public int getPodium() {
		return this.podiumCounts;
	}

	public void resetPodium() {
		this.podiumCounts = 0;
	}
	
	public void regPoint() {
		this.pointCounts++;
	}

	public int getPointStart() {
		return this.pointCounts;
	}
	
	public void resetPointStart() {
		this.pointCounts = 0;
	}

	public void regDNF() {
		this.dnfCounts++;
	}

	public int getDNF() {
		return this.dnfCounts;
	}
	
	public void resetDNF() {
		this.dnfCounts = 0;
	}
}
