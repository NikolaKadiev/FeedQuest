package newsReader;

public class FeedEntry
{
    private String title;
    private String link;
    private String description;

    public FeedEntry()
    {
	this.title = null;
	this.link = null;
	this.description = null;
    }

    public void setTitle(String title)
    {
	this.title = title;
    }

    public String getTitle()
    {
	return title;
    }

    public void setLink(String link)
    {
	this.link = link;
    }

    public String getLink()
    {
	return link;
    }

    public void setDescription(String description)
    {
	this.description = description;
    }

    public String getDescription()
    {
	return description;
    }
}
