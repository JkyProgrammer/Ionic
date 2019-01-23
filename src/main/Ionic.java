package main;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import wikipedia.*;
import java.net.URI;
import java.net.URISyntaxException;

public class Ionic {

	public static void main(String[] args) {
		Ionic i = new Ionic ();
		i.examineURL ("https://en.wikipedia.org/wiki/Logic_bomb");
		i.examineURL("https://en.wikipedia.org/wiki/Gordon_Cowans");
	}
	
	WikipediaGetter wg = new WikipediaGetter ();
	ArrayList<WikipediaArticle> articles = new ArrayList<WikipediaArticle> ();
	
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
				
				String newLink = content.substring(nextIndex, content.indexOf("\"", nextIndex));
				if (!newLink.equals("mw-selflink selflink")) linksToSee.add("https://en.wikipedia.org" + newLink);
			}
		}
		content = content.substring(endIndex + 8);
		
		// Cut out contents table
		int indexOfContents = content.indexOf ("<div id=\"toc\" class=\"toc\">");
		int indexOfContentsEnd = content.indexOf("</div>", indexOfContents);
		indexOfContentsEnd = content.indexOf("</div>", indexOfContentsEnd + 6);
		String tmp = content.substring(0, indexOfContents);
		content = tmp + content.substring(indexOfContentsEnd + 6);
		
		// Remove other unwanted tags
		content = content.replaceAll("<b>", "");
		content = content.replaceAll("</b>", "");
		
		// Search for and replace links in the content
		int endOfContent = content.length()-1;
		int searchIndex = content.indexOf("<a href=");
		while (searchIndex <= endOfContent && searchIndex != -1) {
			String suffix = content.substring(searchIndex + 9, content.indexOf ("\"", searchIndex + 9));
			String linkFullText = content.substring(searchIndex, content.indexOf ("</a>", searchIndex)+4);
			int ltStart = linkFullText.length()-6;
			while (linkFullText.charAt(ltStart) != '>') {
				ltStart--;
			}
			String linkText = linkFullText.substring (ltStart+1, linkFullText.length()-4);
			
			if (suffix.contains("action=edit")) {
				content = content.replace(linkFullText, "");
			} else if (suffix.contains("#cite_note-")) {
				content = content.replace(linkFullText, "");
			} else {
				linksToSee.add("https://en.wikipedia.org" + suffix);
				content = content.replace(linkFullText, linkText);
			}
			
			searchIndex = content.indexOf("<a href=", searchIndex);
			endOfContent = content.length()-1;
		}
		
		// Analyse the text content
		endOfContent = content.length()-1;
		searchIndex = 0;
		
		WikipediaArticle currentArticle = new WikipediaArticle ();
		currentArticle.title = startURL.substring(startURL.lastIndexOf("/")+1);
		currentArticle.link = startURL;
		WikipediaHeading currentHeading = new WikipediaHeading ("Summary");
		
//		while (searchIndex <= endOfContent && searchIndex != -1) { // TODO: Jump to next TAG, single nested loop
//			while ((searchIndex = content.indexOf("<p>", searchIndex)) != -1 && searchIndex <= endOfContent) {
//				String tmpStr = "";
//				// TODO: Extract rendered text and links
//			}
//			currentArticle.headings.add(currentHeading);
//			currentHeading = new WikipediaHeading ("<Next heading>"); // TODO: Find heading name
//		}
		
		
		System.out.println(content);
		for (String s : linksToSee) {
//			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
//			    try {
//					Desktop.getDesktop().browse(new URI(s));
//				} catch (IOException e) {
//					e.printStackTrace();
//				} catch (URISyntaxException e) {
//					e.printStackTrace();
//				}
//			}
			System.out.println(s);
		}
		
		usingBufferedWriter (content, "page.html");
	}
	
	public static void usingBufferedWriter(String s, String path) {
		try {
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
	    writer.write(s);
	    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
