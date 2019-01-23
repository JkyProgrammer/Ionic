package wikipedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class WikipediaGetter {
	private URLConnection conn;
	private BufferedReader br;
	
	public String getContent (String path) {
	    try {
	        // get URL content
	    	URL url;
	        url = new URL(path);
	        conn = url.openConnection();
	
	        // open the stream and put it into BufferedReader
	        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	
	        String inputLine;
	        String output = "";
	        while ((inputLine = br.readLine()) != null) {
	                output += inputLine + '\n';
	        }
	        br.close();
	        return output;
	    } catch (MalformedURLException e) {
	        //e.printStackTrace();
	    } catch (IOException e) {
	        //e.printStackTrace();
	    }
	    return null;
	}
}
