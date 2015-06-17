/**
 * 
 */
package net.tworks.logapps.common.database;

import java.util.List;
import java.util.Map;

/**
 * @author asgs
 * 
 *         A unified interface to implement all operations on a given table. It
 *         can do all basic CRUD stuff. This interface is transparent to the
 *         type of the database, so the implementors will do what is necessary
 *         to cater to different RDBMS' implementations. This interface is meant
 *         to be Thread-Safe which means it won't hold any transaction or user
 *         data. All operations can be accessed by multiple threads
 *         concurrently.
 * 
 *         This class remains unused at the moment. But it serves as a layer to
 *         integrate with Database access layer, regardless of the type of the
 *         SQL/No-SQL databases used or the ORM frameworks used to simplify the
 *         database operations.
 */
public interface DBTableManager {

	void insertRow(String statement);

	List<Map<String, Object>> selectRows(String statement);

	void updateRows(String statement);

	void deleteRows(String statement);

	void alterTable(String statement);

}
