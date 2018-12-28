package xiatstudio;

public class Team {
	String teamName;
	int teamPoints;
	
	public Team(String name) {
		this.teamName = name;
		this.teamPoints = 0;
	}
	
	public void setName(String newName) {
		this.teamName = newName;
	}
	
	public String getName() {
		return this.teamName;
	}
	
	public void addPoints(int points) {
		this.teamPoints += points;
	}
	
	public int getPoints() {
		return this.teamPoints;
	}
}

