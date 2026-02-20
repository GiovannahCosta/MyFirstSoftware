package view;

import controller.ControllerCadastro;
import exceptions.AppException;
import exceptions.ConflictException;
import exceptions.DataAccessException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import model.entities.Area;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Tela de cadastro de usuário.
 * Responsável por montar o formulário de cadastro e coletar os dados digitados.
 * Delega a validação e a persistência para o ControllerCadastro.
 * Exibe mensagens para o usuário em caso de sucesso ou erro.
 */
public class ViewCadastro extends JFrame {

    /**
     * Campo de texto do nome (obrigatório no cadastro).
     */
    private JTextField fieldFirstName;

    /**
     * Campo de texto do sobrenome (opcional no cadastro).
     */
    private JTextField fieldLastName;

    /**
     * Campo de texto do e-mail (obrigatório no cadastro).
     */
    private JTextField fieldEmail;

    /**
     * Campo de senha (obrigatório no cadastro).
     * O valor é obtido como char[] para permitir limpeza da memória após uso.
     */
    private JPasswordField fieldPassword;

    /**
     * Combo de seleção de área/bairro (obrigatório no cadastro).
     * O conteúdo é carregado a partir do banco via ControllerCadastro.
     */
    private JComboBox<Area> comboArea;

    /**
     * Campo de texto da rua (obrigatório no cadastro).
     */
    private JTextField fieldStreet;

    /**
     * Campo de texto do número (opcional).
     * O valor é convertido para Integer no momento do envio do formulário.
     */
    private JTextField fieldNumber;

    /**
     * Campo de texto do CEP (opcional).
     */
    private JTextField fieldCep;

    /**
     * Campo de texto do complemento (opcional).
     */
    private JTextField fieldComplement;

    /**
     * Campo de texto da referência (opcional).
     */
    private JTextField fieldReference;

    /**
     * Controller responsável pelas regras do caso de uso de cadastro.
     * Usado para listar áreas e registrar o usuário.
     */
    private final ControllerCadastro controller;

    /**
     * Construtor da tela de cadastro.
     * Inicializa o controller e monta a UI.
     */
    public ViewCadastro() {
        this.controller = new ControllerCadastro();
        configureFrame();
        setContentPane(buildMainPanel());
    }

    /**
     * Configura a janela (JFrame) com título, tamanho, posição e estilo.
     */
    public void configureFrame() {
        setTitle("Cadastrar - Sistema de Confeitaria");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(440, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    /**
     * Monta o painel principal da tela.
     * Retorna um JScrollPane porque o formulário pode exceder a altura da janela.
     * Cria painel raiz com padding e layout vertical.
     * Adiciona seções: título, dados pessoais, endereço e botões.
     * Coloca o painel dentro de um JScrollPane.
     *
     * @return scroll pane contendo o formulário
     */
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

    /**
     * Monta a seção de título/instrução da tela.
     *
     * @return componente com título e subtítulo
     */
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

    /**
     * Monta a seção de dados pessoais do formulário e inicializa os campos.
     *
     * Campos criados:
     * - Nome
     * - Sobrenome
     * - E-mail
     * - Senha
     *
     * @return componente com a seção de dados pessoais
     */
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

    /**
     * Monta a seção de endereço do formulário e inicializa os campos.
     * Também chama loadAreas para preencher o combo de áreas.
     *
     * Campos criados:
     * - Bairro/Área (combo)
     * - Rua
     * - Número
     * - CEP
     * - Complemento
     * - Referência
     *
     * @return componente com a seção de endereço
     */
    private Component buildAddressSection() {
        JPanel section = ViewTheme.createSection("Endereço");

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ViewTheme.BACKGROUND);

        content.add(ViewTheme.createFieldLabel("Bairro (região) *"));
        content.add(Box.createVerticalStrut(4));

        comboArea = new JComboBox<>();
        content.add(comboArea);

        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Rua *", fieldStreet = ViewTheme.createTextField(20)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Número", fieldNumber = ViewTheme.createTextField(10)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("CEP", fieldCep = ViewTheme.createTextField(10)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Complemento", fieldComplement = ViewTheme.createTextField(20)));
        content.add(Box.createVerticalStrut(8));
        content.add(addFieldRow("Referência", fieldReference = ViewTheme.createTextField(20)));

        section.add(content, BorderLayout.CENTER);

        loadAreas();

        return section;
    }

    /**
     * Monta a seção de botões da tela.
     *
     * Botões:
     * - Cadastrar: envia os dados do formulário para o controller.
     * - Cancelar: fecha a tela sem salvar.
     *
     * @return componente com botões
     */
    private Component buildButtonsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.X_AXIS));
        section.setBackground(ViewTheme.BACKGROUND);

        JButton btnRegister = ViewTheme.createPrimaryButton("Cadastrar");
        btnRegister.addActionListener(e -> onRegister());

        JButton btnCancel = ViewTheme.createSecondaryButton("Cancelar");
        btnCancel.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        section.add(btnRegister);
        section.add(Box.createHorizontalStrut(10));
        section.add(btnCancel);

        return section;
    }

    /**
     * Carrega as áreas/bairros disponíveis no banco e preenche o comboArea.
     * Chama controller.listAreas().
     * Limpa o combo e adiciona as áreas retornadas.
     * Se ocorrer erro de acesso, exibe mensagem ao usuário.
     */
    private void loadAreas() {
        try {
            List<Area> areas = controller.listAreas();
            comboArea.removeAllItems();

            if (areas != null) {
                for (Area a : areas) {
                    comboArea.addItem(a);
                }
            }
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar áreas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Manipulador do botão de cadastro.
     * Lê os campos do formulário, faz conversões simples e chama o controller para registrar.
     *
     * Lê nome, sobrenome, email e senha.
     * Obtém a área selecionada e extrai idArea.
     * Lê rua, número, CEP, complemento e referência.
     * Converte número de String para Integer quando possível.
     * Chama controller.register(...) com os dados.
     * Em caso de sucesso, exibe mensagem e fecha a janela.
     * Em caso de erro, exibe mensagem apropriada.
     * Limpa o array de senha ao final.
     */
    private void onRegister() {
        String firstName = fieldFirstName.getText();
        String lastName = fieldLastName.getText();
        String email = fieldEmail.getText();
        char[] password = fieldPassword.getPassword();

        Area area = (Area) comboArea.getSelectedItem();
        Integer idArea = area != null ? area.getId() : null;

        String street = fieldStreet.getText();
        String cep = fieldCep.getText();
        String complement = fieldComplement.getText();
        String reference = fieldReference.getText();

        Integer number = null;
        try {
            String n = fieldNumber.getText();
            if (n != null && !n.trim().isEmpty()) {
                number = Integer.parseInt(n.trim());
            }
        } catch (Exception ignored) {
            number = null;
        }

        try {
            controller.register(
                    firstName,
                    lastName,
                    email,
                    password,
                    idArea,
                    street,
                    number,
                    cep,
                    complement,
                    reference
            );

            JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso!", "Cadastro", JOptionPane.INFORMATION_MESSAGE);
            setVisible(false);
            dispose();

        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (ConflictException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Conflito", JOptionPane.WARNING_MESSAGE);
        } catch (NotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (password != null) {
                java.util.Arrays.fill(password, '\0');
            }
        }
    }

    /**
     * Cria uma linha padrão de formulário contendo um label e um campo abaixo.
     * Também limita a altura do campo para manter consistência visual.
     *
     * @param labelText texto do label
     * @param field componente de entrada
     * @return componente com o label e o campo
     */
    private Component addFieldRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setBackground(ViewTheme.BACKGROUND);

        JLabel label = ViewTheme.createFieldLabel(labelText);
        row.add(label, BorderLayout.NORTH);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.add(field, BorderLayout.CENTER);

        return row;
    }
}