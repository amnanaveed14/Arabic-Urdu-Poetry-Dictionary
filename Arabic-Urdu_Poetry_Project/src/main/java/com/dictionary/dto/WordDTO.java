package com.dictionary.dto;

/*
 * This is the word class which includes info about word.
 * It has Arabic word, its urdu meaning and root id.
 */

public class WordDTO 
{
private int wordID;
private String arabicWord;
private String urduMeaning;
private int rootID;
private int patternID; 
private String lemma;
/**
 * Constructor that sets all fields of the word.
 * @param wordID the unique id for the word
 * @param arabicWord the Arabic form of the word
 * @param urduMeaning the Urdu translation or meaning
 * @param rootID the id of the root linked to this word
 */

public WordDTO() {}

public WordDTO(int wordID, String arabicWord, String urduMeaning, int rootID, int patternID) {
    this.wordID = wordID;
    this.arabicWord = arabicWord;
    this.urduMeaning = urduMeaning;
    this.rootID = rootID;
    this.patternID = patternID;
}
/**
 * Getter and setters of word class variables
 */

public int getWordID() {
	return wordID;
}

public String getArabicWord() {
	return arabicWord;
}

public String getUrduMeaning() {
	return urduMeaning;
}


public int getRootID() {
    return rootID;
}


public int getPatternID() { 
    return patternID;
}
public String getLemma() {
	return lemma; 
	}

public void setWordID(int wordID) {
    this.wordID = wordID;
}

public void setArabicWord(String arabicWord) {
    this.arabicWord = arabicWord;
}

public void setUrduMeaning(String urduMeaning) {
    this.urduMeaning = urduMeaning;
}
public void setRootID(int rootID) {
    this.rootID = rootID;
}
public void setPatternID(int patternID) { 
    this.patternID = patternID;
}

public void setLemma(String lemma) { this.lemma = lemma; 
}
@Override
public String toString() {
    return arabicWord + " (" + urduMeaning + ")";
}
}


