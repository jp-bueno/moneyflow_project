package view;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import controller.MoneyFlowController;
import model.vo.MetaVO;

import javax.swing.*;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class AdicionarMetaVisao extends JInternalFrame {
	
	private MoneyFlowController controller;

    private JTextField textCodigo;
    private JTextField textDescricao;
    private JTextField textValorEsperado;
    private JTextField textValorArrecadado;
    
    private JButton buttonCancelar;
    private JButton buttonSalvar;
    
    private GerenciamentoMetasVisao gerenciamentoMetasVisao;
    
    private MetaVO metaVO;
    
    private String emailUsuario;

    public AdicionarMetaVisao(GerenciamentoMetasVisao gerenciamentoMetasVisao, MoneyFlowController controller, String emailUsuario) {
        super("Adicionar Meta");
        
        this.gerenciamentoMetasVisao = gerenciamentoMetasVisao;
        this.controller = controller;
        this.emailUsuario = emailUsuario;

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");	// Java Swing Nimbus
        } catch (Exception e) {
        }

        // Chama o método responsável por inicializar os componentes
        inicializaComponentes();

        /* Monta todo o layout e em seguida o adiciona */
        this.getContentPane().add(this.montaPainel());
        
        buttonSalvar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				salvar();
			}
		});
        
        buttonCancelar.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelar();
			}
		});
        
        textValorArrecadado.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if(!controller.ehNumero(textValorArrecadado.getText())) {
					JOptionPane.showMessageDialog(AdicionarMetaVisao.this, "Entre com um valor numérico", "", JOptionPane.ERROR_MESSAGE);
					textValorArrecadado.requestFocus();
					textValorArrecadado.selectAll();
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
        
        textValorEsperado.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if(!controller.ehNumero(textValorEsperado.getText())) {
					JOptionPane.showMessageDialog(AdicionarMetaVisao.this, "Entre com um valor numérico", "", JOptionPane.ERROR_MESSAGE);
					textValorEsperado.requestFocus();
					textValorEsperado.selectAll();
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
    }

    private void inicializaComponentes() {
        textCodigo = new JTextField();
        textDescricao = new JTextField();
        textValorEsperado = new JTextField();
        textValorArrecadado = new JTextField();
        
        textValorArrecadado.setText("0");
        
        textValorArrecadado.setEnabled(false);
        textCodigo.setEnabled(false);
        
        buttonCancelar = new JButton("Cancelar");
        buttonSalvar = new JButton("Salvar");   
    }

    private JComponent montaPainel() {
        FormLayout layout = new FormLayout(
                "10dlu, p:grow, 10dlu",
                "10dlu:grow, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu,p, 5dlu, p, 10dlu:grow");

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);

        CellConstraints cc = new CellConstraints();

        builder.addLabel("Código:", cc.xy(2, 2));
        builder.add(textCodigo, cc.xy(2, 4));

        builder.addLabel("Descrição:", cc.xy(2, 6));
        builder.add(textDescricao, cc.xy(2, 8));

        builder.addLabel("Valor Esperado:", cc.xy(2, 10));
        builder.add(textValorEsperado, cc.xy(2, 12));
        
        builder.addLabel("Valor Arrecadado:", cc.xy(2, 14));
        builder.add(textValorArrecadado, cc.xy(2, 16));

        builder.add(buttonCancelar, cc.xy(2, 18));
        builder.add(buttonSalvar, cc.xy(2, 20));

        return builder.getPanel();
    }
    
    private void salvar() {
    	if(!(controller.ehNumero(textValorArrecadado.getText()) || controller.ehNumero(textValorEsperado.getText())))
    		return;
    	
    	if (textDescricao.getText().isEmpty() || textValorArrecadado.getText().isEmpty() || textValorEsperado.getText().isEmpty()) {
			JOptionPane.showMessageDialog(AdicionarMetaVisao.this, "Por favor, preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}
    	
    	metaVO = new MetaVO(-1, textDescricao.getText());
    	metaVO.setValorEsperado(Float.parseFloat(textValorEsperado.getText()));
    	
    	int codigo = controller.addMeta(emailUsuario, metaVO);
    	
    	if(codigo != -1)
    		JOptionPane.showMessageDialog(AdicionarMetaVisao.this, "Meta adicionada com sucesso", "", JOptionPane.INFORMATION_MESSAGE);
    	else
    		JOptionPane.showMessageDialog(AdicionarMetaVisao.this, "Erro ao adicionar Meta", "", JOptionPane.ERROR_MESSAGE);
    	
    	voltar();
    }
    
    private void cancelar() {
    	int opc = JOptionPane.showConfirmDialog(AdicionarMetaVisao.this, "Tem certeza que deseja cancelar?", "", JOptionPane.WARNING_MESSAGE);
    	
    	if(opc == 0) 
    		voltar();
    }
    
    private void voltar() {
    	this.dispose();
    	gerenciamentoMetasVisao.atualizaTabela();
    	gerenciamentoMetasVisao.setVisible(true);
    }
    
    public void setPosicao() {
		Dimension d = this.getDesktopPane().getSize();
		this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
	}
}