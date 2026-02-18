package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.*;

import controller.ControllerCadastro;
import model.entities.Area;

import java.util.ArrayList;
import java.util.List;

public class ViewCadastro extends JFrame {
	
	private JTextField fieldFirstName;
    private JTextField fieldLastName;
    private JTextField fieldEmail;
    private JPasswordField fieldPassword;
    private JComboBox<Area> comboArea;
    private JTextField fieldStreet;
    private JTextField fieldNumber;
    private JTextField fieldCep;
    private JTextField fieldComplement;
    private JTextField fieldReference;
	private ControllerCadastro controller;
	
	public ViewCadastro() {
		this.controller = new ControllerCadastro();
        configureFrame();
        setContentPane(buildMainPanel());
    }
	
	public void configureFrame() {
		setTitle("Cadastrar - Sistema de Confeitaria");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(440, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
	}
    
	
	private JScrollPane buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(24, 28, 24, 28);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(buildTitleSection());
        panel.add(Box.createVerticalStrut(16));
        panel.add(buildPersonalSection());
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildAddressSection());
        panel.add(Box.createVerticalStrut(20));
        panel.add(buildButtonsSection());

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(ViewTheme.BACKGROUND);
        return scroll;
    }
	
	private Component buildTitleSection() {
		JPanel section = new JPanel(new BorderLayout(0, 6));
		section.setBackground(ViewTheme.BACKGROUND);
		JLabel label = ViewTheme.createFieldLabel("Novo Cadastro");
		label.setFont(ViewTheme.FONT_TITLE);
        label.setForeground(ViewTheme.ACCENT);
        label.setAlignmentX(CENTER_ALIGNMENT);
        section.add(label, BorderLayout.NORTH);
		
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ViewTheme.BACKGROUND);
        
        content.add(Box.createVerticalStrut(4));
        content.add(ViewTheme.createSubtitleLabel("Preencha os dados para se cadastrar"));
        section.add(content, BorderLayout.CENTER);
        return section;
    }
	
	private Component buildPersonalSection() {
        JPanel section = ViewTheme.createSection("Dados pessoais");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ViewTheme.BACKGROUND);

        content.add(addFieldRow("Nome *", fieldFirstName = ViewTheme.createTextField(20)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Sobrenome", fieldLastName = ViewTheme.createTextField(20)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("E-mail *", fieldEmail = ViewTheme.createTextField(20)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Senha *", fieldPassword = ViewTheme.createPasswordField(20)));

        section.add(content, BorderLayout.CENTER);
        return section;
    }
	
	private Component buildAddressSection() {
        JPanel section = ViewTheme.createSection("Endereço");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ViewTheme.BACKGROUND);

        content.add(ViewTheme.createFieldLabel("Bairro (região) *"));
        content.add(Box.createVerticalStrut(4));
        content.add(buildAreaCombo());
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Rua *", fieldStreet = ViewTheme.createTextField(20)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Número", fieldNumber = ViewTheme.createTextField(8)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("CEP", fieldCep = ViewTheme.createTextField(12)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Complemento", fieldComplement = ViewTheme.createTextField(20)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Referência", fieldReference = ViewTheme.createTextField(20)));

        section.add(content, BorderLayout.CENTER);
        return section;
    }
	
	private JPanel addFieldRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setBackground(ViewTheme.BACKGROUND);
        row.add(ViewTheme.createFieldLabel(labelText), BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }
	
	private Component buildButtonsSection() {
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setBackground(ViewTheme.BACKGROUND);
        
        JButton btnBack = ViewTheme.createSecondaryButton("Voltar");
        btnBack.addActionListener(e -> {
            this.dispose();
            new ViewHome().setVisible(true);
        });
        
        JButton btnRegister = ViewTheme.createPrimaryButton("Cadastrar");
        btnRegister.addActionListener(e -> onRegisterClick());
        
        buttons.add(btnBack);
        buttons.add(btnRegister);
        return buttons;
	}
	
	private JComboBox<Area> buildAreaCombo() {
		
		comboArea = new JComboBox<>();
		
	    try {
	  
	    	
	        List<Area> areas = this.controller.listAreas();
	        
	     
	        
	        for (Area a : areas) {
	        	comboArea.addItem(a);
	        }
	        
	        
	    } catch (Exception e) {
	        JOptionPane.showMessageDialog(this, "Erro ao carregar áreas: " + e.getMessage());
	    }
	    
	    return comboArea;
	}
	
	private void onRegisterClick() {
		try {
			Area areaSelecionada = (Area) comboArea.getSelectedItem();
			Integer idArea = null;
	        
			if (areaSelecionada != null) {
	            idArea = areaSelecionada.getId();
	        }
			
			Integer number = null;
	        if (!fieldNumber.getText().isEmpty()) {
	            number = Integer.parseInt(fieldNumber.getText());
	        }
	        
			String firstName = fieldFirstName.getText();
			String lastName = fieldLastName.getText();
			String email = fieldEmail.getText();
			char[] password = fieldPassword.getPassword();
			String street = fieldStreet.getText();
			String cep = fieldCep.getText();
			String complement = fieldComplement.getText();
			String reference = fieldReference.getText();
			
			this.controller.register(firstName, lastName, email, password, idArea, street, number, cep, complement, reference);
			
			
			JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso!", "Cadastro", JOptionPane.INFORMATION_MESSAGE);
	        dispose();
			
		} catch (NumberFormatException e) {
	        JOptionPane.showMessageDialog(this, "O campo 'Número' deve conter apenas dígitos válidos.");
	    } catch (Exception e) {
	        JOptionPane.showMessageDialog(this, "Erro ao registrar: " + e.getMessage());
	    }
		
	}
		
	
	
	
    
}