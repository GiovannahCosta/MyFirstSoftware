package view;

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
                SwingUtilities.getWindowAncestor(btnRegister).setVisible(false);
        	}
        });
        
        JButton btnLogin = ViewTheme.createSecondaryButton("Entrar");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(220, 44));
        
        section.add(btnRegister);
        section.add(Box.createVerticalStrut(14));
        section.add(btnLogin);
        
        return section;
	}
	
	
}