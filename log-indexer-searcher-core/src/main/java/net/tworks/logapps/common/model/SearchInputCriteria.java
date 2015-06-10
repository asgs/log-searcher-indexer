/**
 * 
 */
package net.tworks.logapps.common.model;

/**
 * @author asgs
 * 
 *         A model object encapsulating the details user entered from the front
 *         end.
 * 
 */
public class SearchInputCriteria {

	private String searchString;

	private String index;

	private String sourceType;

	private String[] tokenPairs;

	/**
	 * @return the searchString
	 */
	public String getSearchString() {
		return searchString;
	}

	/**
	 * @param searchString
	 *            the searchString to set
	 */
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	/**
	 * @return the index
	 */
	public String getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(String index) {
		this.index = index;
	}

	/**
	 * @return the sourceType
	 */
	public String getSourceType() {
		return sourceType;
	}

	/**
	 * @param sourceType
	 *            the sourceType to set
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * @return the tokenPairs
	 */
	public String[] getTokenPairs() {
		return tokenPairs;
	}

	/**
	 * @param tokenPairs
	 *            the tokenPairs to set
	 */
	public void setTokenPairs(String[] tokenPairs) {
		this.tokenPairs = tokenPairs;
	}

}
