package view;

import app.Session;
import model.entities.User;
import model.repositories.RepositoryUser;
import services.EncryptionService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

public class ViewLogin extends JFrame {

    private JTextField fieldEmail;
    private JPasswordField fieldPassword;

    private final RepositoryUser repoUser = new RepositoryUser();

    public ViewLogin() {
        configureFrame();
        setContentPane(buildMainPanel());
    }

    private void configureFrame() {
        setTitle("Entrar - Sistema de Confeitaria");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(440, 280);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(24, 24, 24, 24);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(ViewTheme.createTitleLabel("Entrar"));
        panel.add(Box.createVerticalStrut(12));

        panel.add(ViewTheme.createFieldLabel("E-mail"));
        panel.add(Box.createVerticalStrut(4));
        fieldEmail = ViewTheme.createTextField(28);
        panel.add(fieldEmail);

        panel.add(Box.createVerticalStrut(10));

        panel.add(ViewTheme.createFieldLabel("Senha"));
        panel.add(Box.createVerticalStrut(4));
        fieldPassword = ViewTheme.createPasswordField(28);
        panel.add(fieldPassword);

        panel.add(Box.createVerticalStrut(16));

        JButton btnEnter = ViewTheme.createPrimaryButton("Entrar");
        btnEnter.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEnter.addActionListener(e -> onLogin());

        panel.add(btnEnter);

        return panel;
    }

    private void onLogin() {
        String email = fieldEmail.getText() != null ? fieldEmail.getText().trim() : "";
        char[] password = fieldPassword.getPassword();

        try {
            if (email.isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe o e-mail.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (password == null || password.length == 0) {
                JOptionPane.showMessageDialog(this, "Informe a senha.", "Atenção", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User user = repoUser.findByEmailUser(email);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "E-mail ou senha inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Calcula hash da senha digitada e compara com o hash salvo no banco.
            String typedHash = EncryptionService.hashPassword(password);
            boolean ok = typedHash != null && typedHash.equals(user.getPasswordHash());

            if (!ok) {
                JOptionPane.showMessageDialog(this, "E-mail ou senha inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Session.setLoggedUser(user);
            JOptionPane.showMessageDialog(this, "Bem-vindo(a), " + user.getFirstName() + "!");

            setVisible(false);
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao acessar o banco: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao realizar login: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            if (password != null) Arrays.fill(password, '\0');
        }
    }
}