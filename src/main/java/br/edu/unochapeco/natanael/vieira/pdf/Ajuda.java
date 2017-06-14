package br.edu.unochapeco.natanael.vieira.pdf;

import br.edu.unochapeco.natanael.vieira.servicos.Resources;
import java.awt.Dimension;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.util.PropertiesManager;

public final class Ajuda {

	public void exibir() {
        InputStream inputStream = Resources.getArquivoPdf();
        SwingController swingController = new SwingController();
        SwingViewBuilder factoryViewBuilder = new SwingViewBuilder(swingController, getPropriedades());
        JPanel viewerComponentPanel = factoryViewBuilder.buildViewerPanel();
        Dimension defaultDimension = new Dimension(800, 600);
        JDialog dialogView = new JDialog(new JFrame(), "Ajuda", true);
        swingController.openDocument(inputStream, "Ajuda", null);
        dialogView.setPreferredSize(defaultDimension);
        dialogView.getContentPane().add(viewerComponentPanel);
        dialogView.pack();
        dialogView.setVisible(true);
	}
	
	private PropertiesManager getPropriedades() {
		Properties properties = new Properties();

		properties.setProperty("application.showLocalStorageDialogs", Boolean.FALSE.toString());

        PropertiesManager propertiesManager = new PropertiesManager(System.getProperties(), properties, ResourceBundle.getBundle(PropertiesManager.DEFAULT_MESSAGE_BUNDLE));

        propertiesManager.setBoolean(PropertiesManager.PROPERTY_SHOW_STATUSBAR_VIEWMODE, Boolean.FALSE);
        propertiesManager.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ANNOTATION, Boolean.FALSE);
        propertiesManager.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FIT, Boolean.FALSE);
        propertiesManager.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ROTATE, Boolean.FALSE);
        propertiesManager.setBoolean(PropertiesManager.PROPERTY_SHOW_TOOLBAR_TOOL, Boolean.FALSE);
        propertiesManager.setBoolean(PropertiesManager.PROPERTY_SHOW_UTILITY_OPEN, Boolean.FALSE);
        propertiesManager.setBoolean(PropertiesManager.PROPERTY_SHOW_UTILITY_PRINT, Boolean.FALSE);
        propertiesManager.setBoolean(PropertiesManager.PROPERTY_SHOW_UTILITY_SAVE, Boolean.FALSE);
        propertiesManager.setBoolean(PropertiesManager.PROPERTY_SHOW_UTILITY_SEARCH, Boolean.TRUE);
        propertiesManager.setBoolean(PropertiesManager.PROPERTY_SHOW_UTILITYPANE_ANNOTATION, Boolean.FALSE);
        propertiesManager.setBoolean(PropertiesManager.PROPERTY_SHOW_UTILITYPANE_THUMBNAILS, Boolean.FALSE);

        return  propertiesManager;
	}
}