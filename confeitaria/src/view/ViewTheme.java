package view;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Classe utilitária de tema da aplicação.
 * Centraliza cores, fontes e métodos de criação de componentes Swing com aparência padronizada.
 * O objetivo é evitar repetição de estilo nas telas e manter consistência visual.
 */
public class ViewTheme {

	/**
	 * Cor de fundo padrão das telas.
	 */
	public static final Color BACKGROUND = new Color(0xF7B6BD);

    /**
     * Cor de fundo usada em cards e áreas internas.
     */
    public static final Color CARD_BG = new Color(0xFAD1D6);

    /**
     * Cor principal de destaque (ex.: botões primários, títulos com destaque).
     */
    public static final Color ACCENT = new Color(0x6B3A2A);

    /**
     * Cor alternativa de destaque (pensada para hover/destaque adicional).
     * Pode ser usada em bordas ou variações de botões.
     */
    public static final Color ACCENT_HOVER = new Color(0x7A4230);

    /**
     * Cor padrão do texto.
     */
    public static final Color TEXT = new Color(0x6B3A2A);

    /**
     * Cor do texto secundário (menos destaque).
     */
    public static final Color TEXT_MUTED = new Color(0x8B5A4A);

    /**
     * Cor padrão para bordas de componentes (tabelas, cards, inputs).
     */
    public static final Color BORDER = new Color(0xE2A5AD);

    /**
     * Cor de fundo padrão de inputs (campos de texto e senha).
     */
    public static final Color INPUT_BG = new Color(0xFCECEE);

    /**
     * Nome da família de fonte usada no sistema.
     */
    public static final String FONT_FAMILY = "Segoe UI";

    /**
     * Fonte para títulos.
     */
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 22);

    /**
     * Fonte para subtítulos.
     */
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.PLAIN, 14);

    /**
     * Fonte para labels e campos (texto menor).
     */
    public static final Font FONT_LABEL = new Font(FONT_FAMILY, Font.PLAIN, 12);

    /**
     * Fonte para botões.
     */
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 13);

    /**
     * Construtor privado para impedir instanciação.
     * Esta classe deve ser usada apenas com membros estáticos.
     */
    private ViewTheme() {}

    /**
     * Cria um JPanel com:
     * - cor de fundo padrão do tema
     * - padding com EmptyBorder
     *
     * Funcionamento:
     * 1. Cria um JPanel vazio.
     * 2. Define background como BACKGROUND.
     * 3. Define borda interna com os valores informados.
     * 4. Retorna o painel.
     *
     * @param top padding superior
     * @param left padding esquerdo
     * @param bottom padding inferior
     * @param right padding direito
     * @return painel configurado com padding e background
     */
    public static JPanel createPanel(int top, int left, int bottom, int right) {
        JPanel p = new JPanel();
        p.setBackground(BACKGROUND);
        p.setBorder(new EmptyBorder(top, left, bottom, right));
        return p;
    }

    /**
     * Cria um JLabel de título.
     *
     * Funcionamento:
     * 1. Cria JLabel com o texto informado.
     * 2. Aplica FONT_TITLE.
     * 3. Aplica cor TEXT.
     * 4. Retorna o label.
     *
     * @param text texto do título
     * @return label configurado como título
     */
    public static JLabel createTitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(TEXT);
        return l;
    }

    /**
     * Cria um JLabel de subtítulo.
     *
     * Funcionamento:
     * 1. Cria JLabel com o texto informado.
     * 2. Aplica FONT_SUBTITLE.
     * 3. Aplica cor TEXT_MUTED.
     * 4. Retorna o label.
     *
     * @param text texto do subtítulo
     * @return label configurado como subtítulo
     */
    public static JLabel createSubtitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SUBTITLE);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    /**
     * Cria um JLabel para rótulo de campo.
     *
     * Funcionamento:
     * 1. Cria JLabel com o texto informado.
     * 2. Aplica FONT_LABEL.
     * 3. Aplica cor TEXT.
     * 4. Retorna o label.
     *
     * @param text texto do rótulo
     * @return label configurado como rótulo
     */
    public static JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT);
        return l;
    }

    /**
     * Cria um botão primário.
     * É usado para ações principais (ex.: salvar, confirmar, entrar).
     *
     * Funcionamento:
     * 1. Cria JButton com o texto informado.
     * 2. Aplica FONT_BUTTON.
     * 3. Define background como ACCENT.
     * 4. Define foreground como branco.
     * 5. Desabilita pintura de foco.
     * 6. Mantém borda pintada.
     * 7. Retorna o botão.
     *
     * @param text texto do botão
     * @return botão configurado como primário
     */
    public static JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BUTTON);
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(true);
        return b;
    }

    /**
     * Cria um botão secundário.
     * É usado para ações auxiliares (ex.: atualizar, fechar, cancelar).
     *
     * Funcionamento:
     * 1. Cria JButton com o texto informado.
     * 2. Aplica FONT_BUTTON.
     * 3. Define background como CARD_BG.
     * 4. Define foreground como TEXT.
     * 5. Desabilita pintura de foco.
     * 6. Retorna o botão.
     *
     * @param text texto do botão
     * @return botão configurado como secundário
     */
    public static JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BUTTON);
        b.setBackground(CARD_BG);
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        return b;
    }

    /**
     * Cria um JTextField com estilo do tema.
     *
     * Funcionamento:
     * 1. Cria JTextField com número de colunas informado.
     * 2. Aplica FONT_LABEL.
     * 3. Define background como INPUT_BG.
     * 4. Retorna o campo.
     *
     * @param columns número de colunas do campo
     * @return campo de texto configurado
     */
    public static JTextField createTextField(int columns) {
        JTextField t = new JTextField(columns);
        t.setFont(FONT_LABEL);
        t.setBackground(INPUT_BG);
        return t;
    }

    /**
     * Cria um JPasswordField com estilo do tema.
     *
     * Funcionamento:
     * 1. Cria JPasswordField com número de colunas informado.
     * 2. Aplica FONT_LABEL.
     * 3. Define background como INPUT_BG.
     * 4. Aplica borda composta com linha e padding interno.
     * 5. Retorna o campo.
     *
     * @param columns número de colunas do campo
     * @return campo de senha configurado
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField p = new JPasswordField(columns);
        p.setFont(FONT_LABEL);
        p.setBackground(INPUT_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)));
        return p;
    }

    /**
     * Cria um painel de seção com um título.
     * Este método padroniza um "cabeçalho" simples de seção (ex.: "Dados pessoais", "Endereço").
     *
     * Funcionamento:
     * 1. Cria um JPanel com BorderLayout e espaçamento vertical.
     * 2. Aplica background BACKGROUND.
     * 3. Cria um JLabel com createFieldLabel(title).
     * 4. Aplica cor ACCENT no label para destacar o título da seção.
     * 5. Adiciona o label no topo do painel e retorna.
     *
     * @param title título da seção
     * @return painel de seção com label de título
     */
    public static JPanel createSection(String title) {
        JPanel section = new JPanel(new BorderLayout(0, 6));
        section.setBackground(BACKGROUND);

        JLabel label = createFieldLabel(title);
        label.setFont(label.getFont());
        label.setForeground(ACCENT);

        section.add(label, BorderLayout.NORTH);
        return section;
    }
}