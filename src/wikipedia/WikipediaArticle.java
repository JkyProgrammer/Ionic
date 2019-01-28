package wikipedia;

import java.util.ArrayList;

public class WikipediaArticle {
	public String title;
	public WikipediaArticle parent;
	public String link;
	public ArrayList<String> containedLinks;
	public ArrayList<WikipediaHeading> headings;
}
