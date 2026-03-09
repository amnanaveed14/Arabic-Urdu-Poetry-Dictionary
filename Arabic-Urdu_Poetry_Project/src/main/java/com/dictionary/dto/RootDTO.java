package com.dictionary.dto;

/**
 * This is the root class.
 * It contains data regarding the root word.
 * It contains variables like id, rootLetters
 */

public class RootDTO {
	private int id;
	private String rootLetters;

/**
 * Constructor to set values for both root letters and id
 */
	public RootDTO() {
	    // empty constructor
	}

	public RootDTO( int id, String root) {
		this.rootLetters = root;
		this.id = id;
	}
	
/**
 * Getters and setters for variables
 */

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getRootLetters() {
		return rootLetters;
	}

	public void setRootLetters(String rootLetters) {
		this.rootLetters = rootLetters;
	}
	// CRITICAL for UI ComboBoxes
    @Override
    public String toString() {
        return rootLetters; 
    }
}
