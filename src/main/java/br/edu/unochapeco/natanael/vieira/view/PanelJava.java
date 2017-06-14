package br.edu.unochapeco.natanael.vieira.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

final class PanelJava extends JPanel{
	private static final long serialVersionUID = 1L;
	private Dimension _tamanhoPadraoAba = new Dimension(100, 15);
	private JTabbedPane _tabbedPanelAbas = new JTabbedPane();
	private JPanel _panelClassePai = new JPanel();
	private JPanel _panelClasseFilha = new JPanel();
	private JLabel _labelNomeAbaClassePai = new JLabel();
	private JLabel _labelNomeAbaClasseFilha = new JLabel();
	private RSyntaxTextArea _textAreaClasseJavaPai = new RSyntaxTextArea(20, 50);
	private RSyntaxTextArea _textAreaClasseJavaFilha = new RSyntaxTextArea(20, 50);
	
	public PanelJava() {
		inicializarComponentes();
	}
	
	private void inicializarComponentes() {
		setLayout(new BorderLayout());
		setBorder(getBordaPanelPadrao());
		setPreferredSize(new Dimension(250, 50));
		
		_textAreaClasseJavaPai.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		_textAreaClasseJavaPai.setCodeFoldingEnabled(true);
		RTextScrollPane textScrollPaneClassePai = new RTextScrollPane(_textAreaClasseJavaPai);
		
		_textAreaClasseJavaFilha.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		_textAreaClasseJavaFilha.setCodeFoldingEnabled(true);
		RTextScrollPane textScrollPaneClasseFilha = new RTextScrollPane(_textAreaClasseJavaFilha);
		
		_panelClassePai.setPreferredSize(getTamanhoPadrao());
		_panelClassePai.setLayout(new BorderLayout());
		_panelClassePai.setBorder(getBordaPanelPadrao());
		_panelClassePai.add(textScrollPaneClassePai, BorderLayout.CENTER);
		_tabbedPanelAbas.addTab(null, _panelClassePai);
		
		_labelNomeAbaClassePai.setText("Classe Pai");
		_labelNomeAbaClassePai.setPreferredSize(_tamanhoPadraoAba);
		_labelNomeAbaClassePai.setHorizontalAlignment(SwingConstants.CENTER);
		_tabbedPanelAbas.setTabComponentAt(0, _labelNomeAbaClassePai);
		
		_panelClasseFilha.setPreferredSize(getTamanhoPadrao());
		_panelClasseFilha.setLayout(new BorderLayout());
		_panelClasseFilha.setBorder(getBordaPanelPadrao());
		_panelClasseFilha.add(textScrollPaneClasseFilha, BorderLayout.CENTER);
		_tabbedPanelAbas.addTab(null, _panelClasseFilha);
		
		_labelNomeAbaClasseFilha.setText("Classe Filha");
		_labelNomeAbaClasseFilha.setPreferredSize(_tamanhoPadraoAba);
		_labelNomeAbaClasseFilha.setHorizontalAlignment(SwingConstants.CENTER);
		_tabbedPanelAbas.setTabComponentAt(1, _labelNomeAbaClasseFilha);
		
		add(_tabbedPanelAbas);
	}

	private Border getBordaPanelPadrao() {
		return BorderFactory.createEmptyBorder(10, 10, 10, 10);
	}

	private Dimension getTamanhoPadrao() {
		return new Dimension(250, 25);
	}

	public String getTextoClasseFilha() {
		return _textAreaClasseJavaFilha.getText().trim();
	}

	public void setTextoClasseFilha(String text) {
		_textAreaClasseJavaFilha.setText(text);
	}

	public String getTextoClassePai() {
		return _textAreaClasseJavaPai.getText().trim();
	}
	
	public void setTextoClassePai(String text) {
		_textAreaClasseJavaPai.setText(text);
	}

	public void setFocoPrimeiraAba() {
		_tabbedPanelAbas.setSelectedIndex(0);
	}
}