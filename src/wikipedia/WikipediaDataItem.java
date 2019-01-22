package wikipedia;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;

public class WikipediaDataItem {
	String text;
	Image image;
	ArrayList<String> bullets;

	WikipediaDataItemType type;

	public WikipediaDataItem (String[] b) {
		type = WikipediaDataItemType.bullets;
		text = null;
		image = null;
		bullets = new ArrayList<String> (Arrays.asList (b));
	}

	public WikipediaDataItem (String s) {
		type = WikipediaDataItemType.text;
		text = s;
		image = null;
		bullets = null;
	}
	
	public WikipediaDataItem (Image i) {
		type = WikipediaDataItemType.image;
		text = null;
		image = i;
		bullets = null;
	}
}
