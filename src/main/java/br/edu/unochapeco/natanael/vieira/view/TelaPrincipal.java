package br.edu.unochapeco.natanael.vieira.view;

import br.edu.unochapeco.natanael.vieira.controller.Conversor;
import br.edu.unochapeco.natanael.vieira.excecoes.ConversaoException;
import br.edu.unochapeco.natanael.vieira.pdf.Ajuda;
import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import br.edu.unochapeco.natanael.vieira.servicos.Resources;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.List;

public class TelaPrincipal extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel _painelPrincipal = new JPanel();
	private PanelJava _painelAbasJava = new PanelJava();
	private PanelJavaScript _painelJavaScript = new PanelJavaScript();
	private JButton _buttonConverter = new JButton();

	public TelaPrincipal() throws HeadlessException {
		super();
		inicializarFormulario();
		inicializarComponentes();
	}

	private void inicializarFormulario() {
		Dimension dimension = new Dimension(1024, 720);
		setLayout(new BorderLayout());
		setPreferredSize(dimension);
		setMinimumSize(dimension);
		setLocationRelativeTo(null);
		setIconImage(Resources.getIconeAplicacao());
		setTitle("JavaScript OO");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void inicializarComponentes() {
		Dimension tamanhoPadraoAbas = new Dimension(300, 300);
		Dimension tamanhoPadraoBotaoConverter = new Dimension(45, 30);
		GridBagConstraints gridBagConstraintsPanels = new GridBagConstraints();

		gridBagConstraintsPanels.weightx = 1;
		gridBagConstraintsPanels.weighty = 1;
		gridBagConstraintsPanels.fill = GridBagConstraints.BOTH;

		_painelAbasJava.setPreferredSize(tamanhoPadraoAbas);
		_painelJavaScript.setPreferredSize(tamanhoPadraoAbas);
		_buttonConverter.setPreferredSize(tamanhoPadraoBotaoConverter);
		_buttonConverter.setIcon(Resources.getIconeBotaoCoverter());
		_buttonConverter.setFocusable(false);
		_buttonConverter.addActionListener(action -> converterCodigo());

		_painelPrincipal.setLayout(new GridBagLayout());
		_painelPrincipal.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		_painelPrincipal.add(_painelAbasJava, gridBagConstraintsPanels);
		_painelPrincipal.add(_buttonConverter);
		_painelPrincipal.add(_painelJavaScript, gridBagConstraintsPanels);

		JMenuBar rootMenu = new JMenuBar();
		JMenu menu = new JMenu("Arquivo");
		JMenuItem menuItem;

		menuItem = new JMenuItem("Novo");
		menuItem.addActionListener(action -> novo());
		menu.add(menuItem);

		menuItem = new JMenuItem("Salvar");
		menuItem.addActionListener(action -> salvar());
		menu.add(menuItem);

		menuItem = new JMenuItem("Sair");
		menuItem.addActionListener(action -> sair());
		menu.add(menuItem);

		rootMenu.add(menu);

		menu = new JMenu("Configuração");

		menuItem = new JMenuItem("Configurações");
		menuItem.addActionListener(action -> configuracao());
		menu.add(menuItem);

		rootMenu.add(menu);

		menu = new JMenu("Ajuda");

		menuItem = new JMenuItem("Ajuda");
		menuItem.addActionListener(action -> ajuda());
		menu.add(menuItem);

		rootMenu.add(menu);
		setJMenuBar(rootMenu);

		add(_painelPrincipal, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

    private void ajuda() {
        Ajuda ajuda = new Ajuda();
        ajuda.exibir();
    }

    private void configuracao() {
		FrameConfiguracao configuracao = new FrameConfiguracao(this);
		AuxiliarConversao.getInstance().setExibirMensagens(configuracao.getExibirMensagem().equals("Sim"));
		AuxiliarConversao.getInstance().setNumeroEspacosIdentar(Integer.parseInt(configuracao.getIdentacao()));
    }

	private void converterCodigo() {
	    if (_painelAbasJava.getTextoClassePai().equals("")) {
            JOptionPane.showMessageDialog(this, "Para realizar a conversão é necessário informar a classe pai!");
        }
        else {
            List<String> codigos = new ArrayList<>();
            Conversor conversor = new Conversor();

            codigos.add(_painelAbasJava.getTextoClassePai());

            if (!_painelAbasJava.getTextoClasseFilha().equals("")) {
                codigos.add(_painelAbasJava.getTextoClasseFilha());
            }

            try {
                _painelJavaScript.setTextoJavaScript(conversor.converter(codigos));

                JOptionPane.showMessageDialog(this, "Conversão realizada com sucesso!");
            } catch (ConversaoException excecaoConversao) {
                JOptionPane.showMessageDialog(this, excecaoConversao.getMessage(), "", JOptionPane.ERROR_MESSAGE);
            }
        }
	}

	private void novo() {
		_painelAbasJava.setTextoClassePai("");
		_painelAbasJava.setTextoClasseFilha("");
        _painelAbasJava.setFocoPrimeiraAba();
		_painelJavaScript.setTextoJavaScript("");
	}

    private void sair() {
        System.exit(0);
    }

	private void salvar() {
		// implementar o método
	}
}