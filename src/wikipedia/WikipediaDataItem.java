package wikipedia;

public class WikipediaDataItem {
	private String string;
	private ArrayList<String> bullets;

	private WikipediaDataItemType type;

	public WikipediaDataItem (String[] b) {
		type = WikipediaDataItemType.bullets;
		string = null;
		bullets = new ArrayList<String> (Arrays.asList (b));
	}

	public WikipediaDataItem (String s) {
		type = WikipediaDataItemType.string;
		string = s;
		bullets = null;
	}
}
