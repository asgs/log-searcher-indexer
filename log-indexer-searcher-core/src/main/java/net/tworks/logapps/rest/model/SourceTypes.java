/**
 * 
 */
package net.tworks.logapps.rest.model;

import java.util.Collection;

/**
 * @author asgs
 * 
 *         A collection of {@link SourceType} representing all the source types
 *         configured in the system..
 *
 */
public class SourceTypes {

	private Collection<SourceType> sourceTypes;

	/**
	 * @return the sourceTypes
	 */
	public Collection<SourceType> getSourceTypes() {
		return sourceTypes;
	}

	/**
	 * @param sourceTypes
	 *            the sourceTypes to set
	 */
	public void setSourceTypes(Collection<SourceType> sourceTypes) {
		this.sourceTypes = sourceTypes;
	}

}
