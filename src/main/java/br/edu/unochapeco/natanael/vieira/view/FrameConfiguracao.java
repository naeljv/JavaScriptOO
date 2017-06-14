package br.edu.unochapeco.natanael.vieira.view;

import br.edu.unochapeco.natanael.vieira.servicos.AuxiliarConversao;
import java.awt.*;
import javax.swing.*;

final class FrameConfiguracao extends JDialog{
	private static final long serialVersionUID = 1L;
	private JComboBox<String> _comboBoxExibirMensagem  = new JComboBox<String>();
	private JComboBox<String> _comboBoxIdentacao  = new JComboBox<String>();
	private JLabel _labelExibirMensagem = new JLabel("Exibir Mensagem: ");
	private JLabel _labelIdentacao = new JLabel("Identação: ");
	private JPanel _panelExibirMensagem = new JPanel();
	private JPanel _panelIdentacao = new JPanel();
	private JPanel _panelConfiguracao = new JPanel();

	public FrameConfiguracao(Frame frame) {
		super(frame);
		inicializarFrame();
		inicializarComponentes();
		pack();
		setVisible(true);
	}
	
	private void inicializarFrame() {
		Dimension dimension = new Dimension(400, 200);
		setLayout(new GridLayout(1,1));
		setPreferredSize(dimension);
		setMinimumSize(dimension);
		setLocationRelativeTo(null);
		setResizable(false);
		setTitle("Configurações");
		setModal(true);
	}

	private void inicializarComponentes() {
		_comboBoxExibirMensagem.addItem("Sim");
		_comboBoxExibirMensagem.addItem("Não");
        _comboBoxExibirMensagem.setSelectedIndex(AuxiliarConversao.getInstance().isExibirMensagens() ? 0 : 1);
		_panelExibirMensagem.setLayout(new GridLayout(1,2));
		_panelExibirMensagem.add(_labelExibirMensagem);
		_panelExibirMensagem.add(_comboBoxExibirMensagem);

		_comboBoxIdentacao.addItem("2");
		_comboBoxIdentacao.addItem("4");
        _comboBoxIdentacao.setSelectedIndex(AuxiliarConversao.getInstance().getNumeroEspacosIdentar() == 2 ? 0 : 1);
		_panelIdentacao.setLayout(new GridLayout(1,2));
		_panelIdentacao.add(_labelIdentacao);
		_panelIdentacao.add(_comboBoxIdentacao);

		_panelConfiguracao.setLayout(new GridLayout(2,1,5,5));
		_panelConfiguracao.setBorder(BorderFactory.createEmptyBorder(55,55,65,55));
		_panelConfiguracao.add(_panelExibirMensagem);
		_panelConfiguracao.add(_panelIdentacao);

		add(_panelConfiguracao);
	}

	public String getExibirMensagem(){
		return _comboBoxExibirMensagem.getSelectedItem().toString();
	}

	public String getIdentacao(){
		return _comboBoxIdentacao.getSelectedItem().toString();
	}
}