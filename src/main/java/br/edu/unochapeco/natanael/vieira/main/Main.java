package br.edu.unochapeco.natanael.vieira.main;

import br.edu.unochapeco.natanael.vieira.view.TelaPrincipal;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import com.jtattoo.plaf.fast.FastLookAndFeel;

public class Main 
{
	public static void main(String[] args) {
		configurarLookAndFell();
		new TelaPrincipal();
	}
	
	private static void configurarLookAndFell() {
		try {
			FastLookAndFeel.setTheme("Large-Font", "", "");
			UIManager.setLookAndFeel(new FastLookAndFeel());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}