package main;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import wikipedia.*;
import java.net.URI;
import java.net.URISyntaxException;

public class Ionic {

	public static void main(String[] args) {
		Ionic i = new Ionic ();
		i.em = ExplorationMode.onerandom;
		i.iterationDepth = 100;
		i.examineURL ("https://en.wikipedia.org/wiki/Logic_bomb", 0);
		//i.examineURL("https://en.wikipedia.org/wiki/Gordon_Cowans");
		//i.findHitler("https://en.m.wikipedia.org/wiki/Ion");
	}
	
	public Ionic () {
		try {
			pw = new PrintWriter (new File("Links.txt"));
			for (File file: (new File("ViewedPages/")).listFiles()) file.delete();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	PrintWriter pw;
	WikipediaGetter wg = new WikipediaGetter ();
	ArrayList<WikipediaArticle> articles = new ArrayList<WikipediaArticle> ();
	Random randomer = new Random ();
	ExplorationMode em = ExplorationMode.all;
	int iterationDepth = 2;
	boolean isFindingHitler = false;
	ArrayList<String> allLinks = new ArrayList<String> ();
	
	ArrayList<String> hitlerLinks = new ArrayList<String> (Arrays.asList("Adolf_Hitler", "World_War_II", "Germany", "German"));
	
	public void addLinkToSee (String link, ArrayList<String> arr, int depth) {
		String realLink = "https://en.wikipedia.org" + link;
		if (isFindingHitler) {
			if (hitlerLinks.contains(link)) {
				allLinks.add(realLink);
				if (depth < iterationDepth) examineURL (realLink, depth+1);
				//linksToSee.add(realLink);
			} else if (depth < 2) { // Allows a one article depth expansion, to reduce dead-ends
				allLinks.add(realLink);
				if (depth < iterationDepth) examineURL (realLink, depth+1);
				//linksToSee.add(realLink);
			}
		} else if (!allLinks.contains(realLink)) {
			allLinks.add(realLink);
			arr.add(realLink);
		}
	}
	
	public void findHitler (String startingPoint) {
		int tmp = iterationDepth;
		iterationDepth = 10;
		isFindingHitler = true;
		examineURL (startingPoint, 0);
		isFindingHitler = false;
		iterationDepth = tmp;
	}
	
	
	public void examineURL (String startURL, int depth) {
		if (isFindingHitler && startURL.contains("Adolf_Hitler")) { System.out.println("We found Hitler in " + depth + " jumps!"); return; }
		
		// Log the link to terminal
		for (int i = 0; i < depth; i++) System.out.print (" ");
		System.out.println (startURL);
		// Log the link to file
		for (int i = 0; i < depth; i++) pw.print (" ");
		pw.println(startURL);
		//linksToSee.remove(startURL);
		String content = wg.getContent(startURL);
		ArrayList<String> linksToSee = new ArrayList<String> ();
		
		if (content == null) return;
		// Remove header
		content = content.substring(content.indexOf("</head>") + 8);
		
		// Remove references and footer
		int refs = content.indexOf("<h2><span class=\"mw-headline\" id=\"References\">References</span>");
		if (refs > -1) content = content.substring(0, refs);
		
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
				if (!newLink.equals("mw-selflink selflink") && !newLink.contains("mediawiki")) addLinkToSee (newLink, linksToSee, depth);
				nextIndex += 5;
			}
			content = content.substring(endIndex + 8);
		}
		
		// Cut out contents table
		int indexOfContents = content.indexOf ("<div id=\"toc\" class=\"toc\">");
		int indexOfContentsEnd = content.indexOf("</div>", indexOfContents);
		indexOfContentsEnd = content.indexOf("</div>", indexOfContentsEnd + 6);
		if (indexOfContents != -1) {
			String tmp = content.substring(0, indexOfContents);
			content = tmp + content.substring(indexOfContentsEnd + 6);
		}
		
		// Remove other unwanted tags
		content = content.replaceAll("<b>", "");
		content = content.replaceAll("</b>", "");
		
		// Search for and replace links in the content
		int endOfContent = content.length()-1;
		int searchIndex = content.indexOf("<a href=");
		while (searchIndex <= endOfContent && searchIndex != -1) {
			String suffix = content.substring(searchIndex + 9, content.indexOf ("\"", searchIndex + 9));
			String linkFullText = content.substring(searchIndex, content.indexOf ("</a>", searchIndex)+4);
			int ltStart = linkFullText.length()-5;
			while (linkFullText.charAt(ltStart) != '>') {
				ltStart--;
			}
			String linkText = linkFullText.substring (ltStart+1, linkFullText.length()-4);
			
			if (suffix.contains("/w/index.php?")) {
				content = content.replace(linkFullText, "");
			} else if (suffix.contains("#cite_note-") || suffix.contains("#cite_ref") || suffix.contains("https://") || suffix.contains(":") || suffix.contains("wikimedia") || suffix.contains("external text")) {
				content = content.replace(linkFullText, "");
			} else {
				addLinkToSee (suffix, linksToSee, depth);
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
		
//		
//		usingBufferedWriter (content, "page.html");
		if (isFindingHitler) return;
		String[] s = startURL.split("/");
		//usingBufferedWriter (content, "ViewedPages/" + s[s.length-1] + ".html");
		if (depth >= iterationDepth) return;
		if (em == ExplorationMode.all) {
			for (String link : linksToSee) {
				examineURL (link, depth + 1);
			}
		} else if (em == ExplorationMode.onerandom) {
			if (linksToSee.isEmpty ()) {
				System.out.println("Ah! Dead end!");
				return;
			}
			int r = randomer.nextInt(linksToSee.size());
			examineURL (linksToSee.get(r), depth + 1);
		}
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
