package br.edu.unochapeco.natanael.vieira.servicos;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import javax.swing.ImageIcon;

public final class Resources {
	
	public static ImageIcon getIconeBotaoCoverter() {
		Image image = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("icones/iconeBotao.png"));
		return new ImageIcon(image);
	}

	public static Image getIconeAplicacao() {
		return Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("icones/aplicacao.png"));
	}
	
	public static InputStream getArquivoPdf(){
		return ClassLoader.getSystemResourceAsStream("pdf/ajuda.pdf");
	}
}