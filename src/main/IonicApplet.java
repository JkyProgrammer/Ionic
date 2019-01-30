import java.applet.Applet;
import javax.swing.JOptionPane;


public class IonicApplet extends Applet {
	public static void main(String[] args) {
		Ionic i = new Ionic ();
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
}