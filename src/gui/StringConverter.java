package gui;

import java.awt.FontMetrics;
import java.awt.Insets;

import javax.swing.JPanel;

/**
 * A utility class containing methods for manipulating strings.
 * 
 * @author Adrian Stritzinger
 *
 */
public class StringConverter {

	/**
	 * Converts the <code>message</code> into a <code>String</code> in html-code so
	 * that its graphical representation's width is lesser than or equal to the
	 * width of the <code>panel</code> by adding line breaks.
	 * 
	 * @param panel that the message needs to fit in
	 * @param message that is to be converted
	 * @return message in html-code so that graphical representation fits panel
	 */
	public static String convertToMultilineToFitPanel(JPanel panel, String message) {
		FontMetrics fontMetrics = panel.getFontMetrics(panel.getFont());
		int stringWidth = fontMetrics.stringWidth(message);
		Insets insets = panel.getBorder().getBorderInsets(panel);
		int maxContentWidth = panel.getWidth() - insets.left - insets.right;
		if (stringWidth < maxContentWidth)
			return message;
		String[] substrings = message.split(" ");
		StringBuilder stringBuilder = new StringBuilder("<html>");
		int lineWidth = 0;
		for (int i = 0; i < substrings.length; i++) {
			if (lineWidth == 0 || lineWidth + fontMetrics.stringWidth(substrings[i] + " ") < maxContentWidth) {
				stringBuilder.append(substrings[i] + " ");
			} else {
				stringBuilder.append("<br>" + substrings[i] + " ");
				lineWidth = 0;
			}
			lineWidth = lineWidth + fontMetrics.stringWidth(substrings[i] + " ");
		}
		stringBuilder.append("</html>");
		return stringBuilder.toString();
	}
}
