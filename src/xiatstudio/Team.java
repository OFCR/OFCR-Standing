package xiatstudio;

public class Team {
	String teamName;
	int teamPoints;

	public Team(String name) {
		this.teamName = abbrToFull(name);
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

	public String abbrToFull(String name) {
		if (name.equals("XTG"))
			return "xTacing OFCR Team";
		else if (name.equals("KST"))
			return "Kimi Sleeping Team";
		else if (name.equals("RPM"))
			return "Team Russian Poisonous Milk";
		else if (name.equals("FBT"))
			return "Audi Sport Team FlyingBird";
		else if (name.equals("NCR"))
			return "NorthernChina Racing";
		else if (name.equals("MMP"))
			return "Mamaipi GP";
		else if (name.equals("SPR"))
			return "Shanghai Power Racing";
		else
			return name;
	}
}
