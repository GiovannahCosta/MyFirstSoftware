package view;

import app.Session;
import controller.ControllerLogin;
import exceptions.AppException;
import exceptions.AuthenticationException;
import exceptions.DataAccessException;
import exceptions.ValidationException;
import model.entities.User;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ViewLogin extends JFrame {

    private JTextField fieldEmail;
    private JPasswordField fieldPassword;

    private final ControllerLogin controller = new ControllerLogin();

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
            User user = controller.login(email, password);

            Session.setLoggedUser(user);
            JOptionPane.showMessageDialog(this, "Bem-vindo(a), " + user.getFirstName() + "!");

            setVisible(false);
            dispose();

        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (AuthenticationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (password != null) Arrays.fill(password, '\0');
        }
    }
}