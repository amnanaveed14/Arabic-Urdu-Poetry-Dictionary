package com.dictionary.dto;

/**
 * This is DT Layer of the pattern class, its used to store information about pattern.
 * It includes variable declarations like id, name, template and description.
 */

public class PatternDTO 
{
	int id;
	String name;
	String template;
	String description;
	
	public PatternDTO() {}
	
	/**
	 * This is the constructor that gives values to these variables.
	 * @param patterID its the id of pattern to index it
	 * @param name its the name of the pattern
	 * @param template this is the format of pattern
	 * @param this is the short explanation of pattern
	 */
	
	public PatternDTO(int id, String name, String template, String description)
	{
		this.id=id;
		this.name=name;
		this.template=template;
		this.description=description;
	}
	
	/*
	 * Constructor created to initialize the values in the pattern table,
	 * its helpful when id is generated
	 */
	
	public PatternDTO(String name, String template, String description )
	{
		this(-1, name, template, description);
	}
	
	/**
	 * These re the getter and setters of the variables such as
	 * getId---> @return the id of the pattern
	 * setId---> @sets the value for id for the pattern
	 */
	
	public int getId() {return id;}
	public void setId(int id) {this.id=id;}
	
	public String getName(){return name;}
	public void setName(String name) {this.name=name;}
	
	public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /**
     * returns all the details regarding the pattern in a single string.
     * @return a string containing all values 
     */
    
    @Override
    public String toString() {
        return name + " (" + template + ")";
    }


}
