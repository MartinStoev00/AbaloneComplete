package ss.project.abalone;

public class Player {

	private String name;
	private Mark mark;

	public Player(String name, Mark mark) {
		this.name = name;
		this.mark = mark;
	}

	public Player(String name) {
		this(name, Mark.EE);
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setMark(Mark mark) {
		this.mark = mark;
	}

	public String getName() {
		return name;
	}

	public Mark getMark() {
		return mark;
	}

	public String toString() {
		return name + ":" + mark;
	}
	
	public boolean equals(Player playercompared) {
		if(playercompared instanceof Player) {
			return this.toString().equals(playercompared.toString()) ;
		}
		return false;
	}
}
