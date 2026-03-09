package com.dictionary.dao;

import java.util.List;
import com.dictionary.dto.RootDTO;

/**
 * This interface is for Data Access operation for root
 * It has functions for creating, adding, deleting etc....
 */
public interface IRootDAO {
	
	/**
	 * This creates root in database
	 * @param root
	 */
	RootDTO createRoot(RootDTO root);
	
	/**
	 * This gets the root from database according to its ID
	 * @param id unique root id
	 */
	RootDTO getRootById(int id);
	
	/**
	 * This gets the root according to letters
	 * @param rootLetters
	 */
	RootDTO getRootByLetters(String rootLetters);
	
	/**
	 * Gets all the roots from database into a list
	 * @return those roots
	 */
	List<RootDTO> getAllRoots();
	
	/**
	 * Updates the specific root in the database
	 * @param root
	 */
	void updateRoot(RootDTO root);
	
	/**
	 * Deletes the specific root form the database
	 * @param id
	 */
	void deleteRoot(int id);
}