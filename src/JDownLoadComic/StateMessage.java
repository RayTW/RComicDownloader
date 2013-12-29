package JDownLoadComic;

import java.util.ArrayList;
import javax.swing.JTextArea;

public class StateMessage extends JTextArea {
	protected ArrayList<String> textList;
	public final int SHOW_LINE_COUNT = 5;
	protected int lineCount = 5;

	public StateMessage() {
		init();
		refresh();
	}

	public void init() {
		this.textList = new ArrayList();
		for (int i = 0; i < this.lineCount; i++)
			addText(" ");
	}

	public void setLineCount(int count) {
		this.lineCount = count;
	}

	public synchronized void addText(String text) {
		if (this.textList.size() >= this.lineCount) {
			this.textList.remove(0);
		}
		this.textList.add(text);
	}

	public synchronized void refresh() {
		StringBuilder buf = new StringBuilder();

		for (String msg : this.textList) {
			if (buf.length() > 0) {
				buf.append('\n');
			}
			buf.append(msg);
		}
		setText(buf.toString());
	}
}
