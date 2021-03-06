package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

import wikipedia.WikipediaArticle;
import wikipedia.WikipediaGetter;

public class Ionic {

	public static void main(String[] args) {
		Ionic i = new Ionic (false);
//		i.em = ExplorationMode.onerandom;
//		i.iterationDepth = 100;
//		i.examineURL ("https://en.wikipedia.org/wiki/Logic_bomb", 0);
		//i.examineURL("https://en.wikipedia.org/wiki/Gordon_Cowans");
		//i.findHitler("https://en.m.wikipedia.org/wiki/Ion");
		//i.lateralSearch("https://en.wikipedia.org/wiki/Logic_bomb");
		//i.em = ExplorationMode.singleexamine;
		//for (int ii = 0; ii < 8; ii++) {
		//	i.examineURL("https://en.m.wikipedia.org/wiki/Japan", 0);
		//}
		//i.examineURL("https://en.m.wikipedia.org/wiki/Incumbent", 0);
		//i.examineURL("https://en.m.wikipedia.org/wiki/Ion", 0);
		//i.examineURL("https://en.m.wikipedia.org/wiki/Logic_bomb", 0);
		//i.examineURL("https://en.m.wikipedia.org/wiki/Gordon_Cowans", 0);
		//i.examineURL("https://en.m.wikipedia.org/wiki/Battle_of_Liberty", 0);
		//i.examineURL("https://en.m.wikipedia.org/wiki/Boer_War_Memorial_(Montreal)", 0);
		//i.examineURL("https://en.m.wikipedia.org/wiki/Buddhism", 0);
		//i.lateralSearch("https://en.wikipedia.org/wiki/Incumbent");
		String res;
		do {
			res = JOptionPane.showInputDialog("Enter a wiki suffix as the origin: ");
			System.out.println (res);
			if (res != null && !res.equals("")) {
				String target = JOptionPane.showInputDialog("Enter a wiki suffix as the target: ");
				if (target != null && target != "") i.lateralSearch("https://en.wikipedia.org/wiki/" + res, target);
			}
		} while (res != null);
	}
	
	public Ionic (boolean b) {
		try {
			pw = new PrintWriter (new File("Links.txt"));
			//for (File file: (new File("ViewedPages/")).listFiles()) file.delete();
			isApplet = b;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	boolean isApplet;
	PrintWriter pw;
	WikipediaGetter wg = new WikipediaGetter ();
	ArrayList<WikipediaArticle> articles = new ArrayList<WikipediaArticle> ();
	Random randomer = new Random ();
	ExplorationMode em = ExplorationMode.all;
	int iterationDepth = 2;
	boolean isFindingHitler = false;
	boolean botherRemovingLinks = false;
	ArrayList<String> allLinks = new ArrayList<String> ();
		
	private void dialog (String text) {
		if (!isApplet) JOptionPane.showMessageDialog(null, text);
		else JOptionPane.showMessageDialog(null, text);
	}
	
	private void foundHitler (int jumps, WikipediaArticle end) {
		String jumpPath = end.title;
		WikipediaArticle e = end.parent;
		while (e != null) {
			jumpPath = e.title + " > " + jumpPath;
			e = e.parent;
		}
		
		
		System.out.println("Found Target after " + jumps + " jumps. Path was: " + jumpPath);
		dialog ("Found Target after " + jumps + " jumps. Path was: " + jumpPath);
	}
	
	String targetURL = "https://en.wikipedia.org/wiki/Adolf_Hitler";
	
	public void lateralSearch (String startingPoint, String endingPoint) {
		targetURL = "https://en.wikipedia.org/wiki/" + endingPoint;
		em = ExplorationMode.lateralsearch;
		String content = wg.getContent(startingPoint);
		if (content != null) {
			dialog ("Click the button below to begin search.");
		} else {
			dialog ("Failed to retreive article.");
			System.exit(1);
		}
		WikipediaArticle wa = examineURL (startingPoint, 0);
		ArrayList<WikipediaArticle> thisLayerLinks = new ArrayList<WikipediaArticle> ();
		thisLayerLinks.add(wa);
		
		int i = 0;
		while (i < 6) {
			ArrayList<WikipediaArticle> nextLayerLinks = new ArrayList<WikipediaArticle> ();
			for (WikipediaArticle w : thisLayerLinks) {
				if (w.containedLinks.contains(targetURL)) {
					WikipediaArticle tmp = new WikipediaArticle ();
					tmp.title = targetURL.substring(targetURL.lastIndexOf("/")+1).replaceAll("_", " ");
					tmp.parent = w;
					foundHitler (i+1, tmp);
					return;
				} else {
					for (String l : w.containedLinks) {
						WikipediaArticle n = examineURL (l, i+1);
						if (n != null) {
							n.parent = w;
							if (n.containedLinks.contains(targetURL)) {
								WikipediaArticle tmp = new WikipediaArticle ();
								tmp.title = targetURL.substring(targetURL.lastIndexOf("/")+1).replaceAll("_", " ");;
								tmp.parent = n;
								foundHitler (i+2, tmp);
								return;
							}
							nextLayerLinks.add(n);
						} else {
							System.out.println("Got null article.");
						}
					}
				}
			}
			thisLayerLinks = nextLayerLinks;
			i++;
		}
		dialog ("Oh no! Target not found within six jumps!");
	}
	
	
	public void addLinkToSee (String link, ArrayList<String> arr, int depth) {
		String realLink = "https://en.wikipedia.org" + link;
		if (!allLinks.contains(realLink)) {
			allLinks.add(realLink);
			arr.add(realLink);
			//System.out.println ("[Discovered] " + realLink);
		} else if (em == ExplorationMode.lateralsearch && !arr.contains(realLink)) {
			arr.add(realLink);
			//System.out.println ("[Discovered] " + realLink);
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
	
	public void outputLength () {
		Instant end = Instant.now();
		System.out.println(Duration.between(start, end));
		start = end;
	}
	
	Instant start;
	
	public WikipediaArticle examineURL (String startURL, int depth) {
		start = Instant.now();
		// Log the link to terminal
		for (int i = 0; i < depth; i++) System.out.print (" ");
		System.out.println ("[Examining] " + startURL);
		
		
		// Log the link to file
		//for (int i = 0; i < depth; i++) pw.print (" ");
		//pw.println("[Examining] " + startURL);
		String content = wg.getContent(startURL);
		if (content == null) return null;
		ArrayList<String> linksToSee = new ArrayList<String> ();
		
		
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
			while ((nextIndex = content.indexOf("<li>", nextIndex + 4)) < endIndex && nextIndex > indexOfTable) {
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
		
		outputLength();
		
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
				if (botherRemovingLinks) content = content.replace(linkFullText, "");
			} else if (suffix.contains("#") || suffix.contains("https://") || suffix.contains(":") || suffix.contains("wikimedia") || suffix.contains("external text")) {
				if (botherRemovingLinks) content = content.replace(linkFullText, "");
			} else {
				addLinkToSee (suffix, linksToSee, depth);
				if (botherRemovingLinks) content = content.replace(linkFullText, linkText);
			}
			
			searchIndex += 5;
			searchIndex = content.indexOf("<a href=", searchIndex);
			endOfContent = content.length()-1;
		}
		
		
		// Analyse the text content
		endOfContent = content.length()-1;
		searchIndex = 0;
		
		WikipediaArticle currentArticle = new WikipediaArticle ();
		currentArticle.containedLinks = linksToSee;
		currentArticle.title = startURL.substring(startURL.lastIndexOf("/")+1).replaceAll("_", " ");
		outputLength();
		System.out.println (linksToSee.size());
		//String[] s = startURL.split("/");
		//usingBufferedWriter (content, "ViewedPages/" + s[s.length-1] + ".html");
		if (depth >= iterationDepth) return currentArticle;
		if (em == ExplorationMode.all) {
			for (String link : linksToSee) {
				examineURL (link, depth + 1);
			}
		} else if (em == ExplorationMode.singleexamine) {
			return currentArticle;
		} else if (em == ExplorationMode.onerandom) {
			if (linksToSee.isEmpty ()) {
				System.out.println("Ah! Dead end!");
				return null;
			}
			int r = randomer.nextInt(linksToSee.size());
			examineURL (linksToSee.get(r), depth + 1);
		} else if (em == ExplorationMode.lateralsearch) {
			return currentArticle;
		}
		return null;
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
