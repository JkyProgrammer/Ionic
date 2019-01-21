package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import wikipedia.*;

public class Ionic {

	public static void main(String[] args) {
		Ionic i = new Ionic ();
		i.examineURL ("https://en.wikipedia.org/wiki/Logic_bomb");
	}
	
	WikipediaGetter wg = new WikipediaGetter ();
	
	public void examineURL (String startURL) {
		String content = wg.getContent(startURL);
		ArrayList<String> linksToSee = new ArrayList<String> ();
	
		// Remove header
		content = content.substring(content.indexOf("</head>") + 8);
		
		// Remove references and footer
		content = content.substring(0, content.indexOf("<h2><span class=\"mw-headline\" id=\"References\">References</span>"));
		
		// Remove series table if present
		int indexOfTable = content.indexOf("<table class=\"vertical-navbox nowraplinks\"");
		int endIndex = content.indexOf("</table>", indexOfTable);
		if (indexOfTable != -1) {
			// Read links into linksToSee
			int nextIndex = 0;
			while ((nextIndex = content.indexOf("<li>", nextIndex + 4)) < endIndex) {
				nextIndex = content.indexOf("=", nextIndex);
				nextIndex += 2;
				linksToSee.add("https://en.wikipedia.org" + content.substring(nextIndex, content.indexOf("\"", nextIndex)));
			}
		}
		content = content.substring(endIndex + 8);
		
		// Cut out contents table
		int indexOfContents = content.indexOf ("<div id=\"toc\" class=\"toc\">");
		int indexOfContentsEnd = content.indexOf("</div>", indexOfContents);
		indexOfContentsEnd = content.indexOf("</div>", indexOfContentsEnd + 6);
		String tmp = content.substring(0, indexOfContents);
		content = tmp + content.substring(indexOfContentsEnd + 6);
		
		System.out.println(content);
		for (String s : linksToSee) {
			System.out.println(s);
		}
	}

}
