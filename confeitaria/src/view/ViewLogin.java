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

/**
 * Tela de login do sistema.
 * Responsável por coletar e-mail e senha do usuário e solicitar autenticação ao ControllerLogin.
 * Em caso de sucesso, grava o usuário na Session e fecha a tela.
 * Em caso de erro, exibe mensagem apropriada ao usuário.
 */
public class ViewLogin extends JFrame {

    /**
     * Campo de texto onde o usuário digita o e-mail.
     * O valor é lido e normalizado (trim) no método onLogin().
     */
    private JTextField fieldEmail;

    /**
     * Campo de senha onde o usuário digita a senha.
     * O valor é obtido como char[] no método onLogin() para permitir limpeza após o uso.
     */
    private JPasswordField fieldPassword;

    /**
     * Controller responsável pela autenticação.
     * Executa validações básicas, consulta ao banco e verificação de senha.
     */
    private final ControllerLogin controller = new ControllerLogin();

    /**
     * Construtor da tela de login.
     * Configura a janela e monta o painel principal.
     */
    public ViewLogin() {
        configureFrame();
        setContentPane(buildMainPanel());
    }

    /**
     * Configura propriedades do JFrame:
     * - título
     * - operação de fechamento (fecha apenas a janela)
     * - tamanho
     * - centralização
     * - desabilita redimensionamento
     * - aplica cor de fundo do tema
     */
    private void configureFrame() {
        setTitle("Entrar - Sistema de Confeitaria");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(440, 280);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    /**
     * Monta o painel principal da tela.
     *
     * Funcionamento:
     * 1. Cria um painel com padding e layout vertical.
     * 2. Adiciona título.
     * 3. Adiciona campo de e-mail.
     * 4. Adiciona campo de senha.
     * 5. Adiciona botão "Entrar" que chama onLogin().
     *
     * @return painel principal
     */
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

    /**
     * Executa o fluxo de login ao clicar no botão "Entrar".
     *
     * Funcionamento:
     * 1. Obtém o e-mail do campo, aplicando trim e tratando null como string vazia.
     * 2. Obtém a senha do campo como char[].
     * 3. Chama controller.login(email, password) para autenticar.
     * 4. Em caso de sucesso:
     *    - grava o usuário na sessão com Session.setLoggedUser(user)
     *    - mostra mensagem de boas-vindas
     *    - fecha a janela
     * 5. Em caso de erro:
     *    - ValidationException: dados inválidos (exibe aviso)
     *    - AuthenticationException: credenciais incorretas (exibe erro)
     *    - DataAccessException: falha ao acessar banco (exibe erro)
     *    - AppException: outros erros do domínio (exibe erro)
     * 6. No finally, limpa o array de senha com Arrays.fill para reduzir tempo de permanência em memória.
     */
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