package view;

import app.Session;
import auth.EmailWhitelist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewHome extends JFrame {

	public ViewHome() {
        configureFrame();
        setContentPane(buildMainPanel());
    }

	private void configureFrame() {
        setTitle("Sistema de Confeitaria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 420);
        setLocation(80, 80);
        setResizable(false);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
        setState(Frame.NORMAL);
    }

	private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(48, 56, 48, 56);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(buildTitleSection());
        panel.add(Box.createVerticalStrut(36));
        panel.add(buildButtonsSection());

        return panel;
    }

	private Component buildTitleSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ViewTheme.BACKGROUND);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = ViewTheme.createTitleLabel("Sistema de Confeitaria");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.add(title);

        section.add(Box.createVerticalStrut(8));

        JLabel subtitle = ViewTheme.createSubtitleLabel("Cadastre-se ou entre para continuar");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.add(subtitle);

        return section;
    }

	private Component buildButtonsSection() {
		JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ViewTheme.BACKGROUND);
        section.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnRegister = ViewTheme.createPrimaryButton("Cadastrar");
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(220, 44));
        btnRegister.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		ViewCadastro telaCadastro = new ViewCadastro();
                telaCadastro.setVisible(true);
        	}
        });

        JButton btnLogin = ViewTheme.createSecondaryButton("Entrar");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(220, 44));
        btnLogin.addActionListener(e -> {
            ViewLogin tela = new ViewLogin();
            tela.setVisible(true);
        });

        JButton btnProductAdmin = ViewTheme.createSecondaryButton("Cadastro de Produtos (Admin)");
        btnProductAdmin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnProductAdmin.setMaximumSize(new Dimension(220, 44));
        btnProductAdmin.addActionListener(e -> openProductAdmin());
        
        JButton btnShop = ViewTheme.createSecondaryButton("Comprar produtos");
        btnShop.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnShop.setMaximumSize(new Dimension(220, 44));
        btnShop.addActionListener(e -> openShop());

        section.add(btnRegister);
        section.add(Box.createVerticalStrut(14));
        section.add(btnLogin);
        section.add(Box.createVerticalStrut(14));
        section.add(btnProductAdmin);
        section.add(Box.createVerticalStrut(14));
        section.add(btnShop);

        return section;
	}
	
	
	private void openShop() {
		if (!app.Session.isLoggedIn()) {
            JOptionPane.showMessageDialog(this,
                    "Faça login para acessar a loja.",
                    "Login necessário",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        new ViewShopProducts().setVisible(true);
	}
    
	
	private void openProductAdmin() {
        if (!Session.isLoggedIn()) {
            JOptionPane.showMessageDialog(this,
                    "Você precisa fazer login antes de acessar.",
                    "Acesso negado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String email = Session.getLoggedUser().getEmail();
        if (!EmailWhitelist.isAllowed(email)) {
            JOptionPane.showMessageDialog(this,
                    "Seu e-mail não tem permissão para acessar o cadastro de produtos.\n\nE-mail: " + email,
                    "Acesso negado",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        new ViewProducts().setVisible(true);
    }
}