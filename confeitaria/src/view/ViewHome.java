package view;

import app.Session;
import auth.EmailWhitelist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tela inicial do sistema.
 * Apresenta as opções principais de navegação:
 * - abrir tela de cadastro
 * - abrir tela de login
 * - abrir cadastro de produtos (admin)
 * - abrir loja de produtos
 *
 * Também aplica regras simples de acesso:
 * - loja exige usuário logado
 * - admin exige usuário logado e e-mail autorizado na whitelist
 */
public class ViewHome extends JFrame {

	/**
	 * Construtor da tela.
	 * Configura o JFrame e define o painel principal.
	 */
	public ViewHome() {
        configureFrame();
        setContentPane(buildMainPanel());
    }

    /**
     * Configura propriedades do JFrame:
     * - título
     * - operação ao fechar (encerra a aplicação)
     * - tamanho e posição
     * - desabilita redimensionamento
     * - aplica cor de fundo do tema
     * - garante que a janela inicie no estado normal
     */
	private void configureFrame() {
        setTitle("Sistema de Confeitaria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 420);
        setLocation(80, 80);
        setResizable(false);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
        setState(Frame.NORMAL);
    }

    /**
     * Monta o painel principal da tela.
     *
     * Funcionamento:
     * 1. Cria um JPanel com padding padrão do tema.
     * 2. Define layout vertical.
     * 3. Adiciona seção de título.
     * 4. Adiciona espaçamento.
     * 5. Adiciona seção de botões.
     *
     * @return painel principal
     */
	private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(48, 56, 48, 56);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(buildTitleSection());
        panel.add(Box.createVerticalStrut(36));
        panel.add(buildButtonsSection());

        return panel;
    }

    /**
     * Monta a seção de título e subtítulo.
     *
     * Funcionamento:
     * 1. Cria painel com layout vertical e centralizado.
     * 2. Adiciona label de título.
     * 3. Adiciona subtítulo com instrução.
     *
     * @return componente da seção de título
     */
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

    /**
     * Monta a seção de botões e configura os eventos de clique.
     *
     * Botões:
     * - Cadastrar: abre ViewCadastro
     * - Entrar: abre ViewLogin
     * - Cadastro de Produtos (Admin): chama openProductAdmin()
     * - Comprar produtos: chama openShop()
     *
     * @return componente da seção de botões
     */
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

    /**
     * Abre a tela de compra (ViewShopProducts).
     *
     * Regra:
     * - Exige que o usuário esteja logado (Session.isLoggedIn()).
     *
     * Funcionamento:
     * 1. Se não estiver logado, mostra mensagem e retorna.
     * 2. Se estiver logado, instancia e exibe ViewShopProducts.
     */
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

    /**
     * Abre a tela de cadastro de produtos (admin) (ViewProducts).
     *
     * Regras:
     * - Exige que o usuário esteja logado.
     * - Exige que o e-mail do usuário esteja na whitelist (EmailWhitelist).
     *
     * Funcionamento:
     * 1. Se não estiver logado, mostra mensagem e retorna.
     * 2. Obtém o email do usuário logado.
     * 3. Se o e-mail não estiver autorizado, mostra mensagem e retorna.
     * 4. Se autorizado, abre ViewProducts.
     */
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