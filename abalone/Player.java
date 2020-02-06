package ss.project.abalone;

public class Player {

	private String name;
	private Mark mark;

	/**
	 * Sets the input to the local values
	 * 
	 * @param name
	 * @param mark
	 */
	public Player(String name, Mark mark) {
		this.name = name;
		this.mark = mark;
	}

	/**
	 * Sets the name to this player and has Mark.EE as a values
	 * 
	 * @param name
	 */
	public Player(String name) {
		this(name, Mark.EE);
	}

	/**
	 * Sets the name variable to the given input
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the mark variable as the given input
	 * 
	 * @param mark
	 */
	public void setMark(Mark mark) {
		this.mark = mark;
	}

	/**
	 * Returns the local variable name
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the local variable Mark
	 * 
	 * @return mark
	 */
	public Mark getMark() {
		return mark;
	}

	/**
	 * Makes a toString based on name and mark
	 * 
	 */
	public String toString() {
		return name + ":" + mark;
	}

	/**
	 * Checks if two players are the same
	 * 
	 * @param playercompared
	 * @return true if two players are equal
	 */
	public boolean equals(Player playercompared) {
		if (playercompared instanceof Player) {
			return this.toString().equals(playercompared.toString());
		}
		return false;
	}
}
