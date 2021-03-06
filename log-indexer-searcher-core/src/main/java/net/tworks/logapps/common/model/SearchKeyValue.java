/**
 * 
 */
package net.tworks.logapps.common.model;

/**
 * @author asgs
 * 
 *         A simple common domain model representing a key-value pair with which
 *         search could be made stand-alone or augment an existing search with
 *         further refinement.
 *
 */
public class SearchKeyValue {

	/**
	 * A String denoting the key of the key-value pair.
	 */
	private final String key;

	/**
	 * A String denoting the value of the key-value pair.
	 */
	private final String value;

	public SearchKeyValue(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

}
