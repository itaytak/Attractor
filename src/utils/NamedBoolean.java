/**
 * 
 */
package utils;

/**
 * @author Itay
 *
 */
public class NamedBoolean {
	private String name;
	private Boolean value;
	
	/**
	 * 
	 */
	public NamedBoolean() {
		super();
		this.name = null;
		this.value = null;
	}
	/**
	 * @param other
	 */
	public NamedBoolean(NamedBoolean other) {
		super();
		this.name = new String(other.name);
		this.value = new Boolean(other.value);
	}
	/**
	 * @param name
	 * @param value
	 */
	public NamedBoolean(String name, Boolean value) {
		super();
		this.name = new String(name);
		this.value = new Boolean(value);
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Boolean value) {
		this.value = value;
	}
	/**
	 * @return the value
	 */
	public Boolean getValue() {
		return value;
	}
	
	public void mutate(Double mutationRatio) {
		Double mutationFactor = new Double(Math.random());
		if(mutationFactor.compareTo(mutationRatio) >= 0) {
			this.value = new Boolean(!this.value);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof NamedBoolean)) {
			return false;
		}
		
		NamedBoolean other = (NamedBoolean) obj;	
		if (name == null) {
			if(other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		
		if (value == null) {
			if(other.value != null)
			{
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		
		return true;
	}
}
