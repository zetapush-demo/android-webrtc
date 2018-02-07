package com.zetapush.core.callBrick;


/** Result of the call to macro 'timeoutCall' */
public class timeoutCallOutput {
	private String id;

	/** Default constructor */
	public timeoutCallOutput() {
	}
	/**
	 * All args constructor
	 *
	 * @param id
	 *        
	 */
	public timeoutCallOutput(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	/** Field id
	 * 
	*/
	public void setId(String id) {
		this.id = id;
	}

	public String toString() { 
		return "timeoutCallOutput[id=" + id + "]";
	}
}
