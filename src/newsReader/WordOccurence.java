package newsReader;

import java.io.Serializable;

public class WordOccurence implements  Comparable<WordOccurence>,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String key;
    public int count;
	
	public WordOccurence(String key,int count)
	{
	this.key = key;
	this.count = count;
	}
	
	@Override
    public int compareTo(WordOccurence o) {
        return count < o.count ? -1 : count > o.count ? 1 : 0;
    }
}