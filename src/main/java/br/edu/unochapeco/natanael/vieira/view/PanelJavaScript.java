package br.edu.unochapeco.natanael.vieira.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

final class PanelJavaScript extends JPanel {
	private static final long serialVersionUID = 1L;
	private RSyntaxTextArea _textAreaJavaScript = new RSyntaxTextArea(20,50);

	public PanelJavaScript() {
		inicializarComponentes();
	}

	private void inicializarComponentes() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(31, 10, 10, 10));
		setPreferredSize(new Dimension(250, 50));
		_textAreaJavaScript.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		_textAreaJavaScript.setCodeFoldingEnabled(true);
		RTextScrollPane textScrollPane = new RTextScrollPane(_textAreaJavaScript);
		add(textScrollPane, BorderLayout.CENTER);
	}
	
	public String getTextoJavaScript() {
		return _textAreaJavaScript.getText();
	}
	
	public void setTextoJavaScript(String text) {
		_textAreaJavaScript.setText(text);
	}
}