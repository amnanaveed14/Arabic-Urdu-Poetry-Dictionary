package com.dictionary.dalfacade;

import com.dictionary.dao.IPatternDAO;
import com.dictionary.dao.IRootDAO;
import com.dictionary.dao.IWordDAO;

/**
 * This interface combines all DAO operations for centralized access
 */

public interface IDALFacade extends IRootDAO,IWordDAO,IPatternDAO{

}
