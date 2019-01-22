package wikipedia;

import java.util.ArrayList;

public class WikipediaHeading {
	private String title;
	private ArrayList<WikipediaDataItem> data;

	public WikipediaHeading (String t) {
		title = t;
	}

	public void addDataItem (WikipediaDataItem d) {
		data.add (d);
	}
}
