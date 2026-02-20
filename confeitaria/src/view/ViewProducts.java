package view;

import controller.ControllerProductAdmin;
import exceptions.DataAccessException;
import exceptions.ValidationException;
import model.entities.FlavorLevel;
import model.entities.Product;
import model.entities.Size;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de administração de produtos.
 * Permite cadastrar um novo produto (criando também um novo sabor) e listar os produtos existentes.
 * Também permite excluir um produto selecionado na tabela.
 * Utiliza ControllerProductAdmin para carregar dados do banco e executar as operações.
 */
public class ViewProducts extends JFrame {

    /**
     * Controller da área administrativa de produtos.
     * Responsável por:
     * - listar níveis de sabor e tamanhos para os combos
     * - criar produto com novo sabor
     * - listar produtos para a tabela
     * - excluir produto
     */
    private final ControllerProductAdmin controller = new ControllerProductAdmin();

    /**
     * Campo de texto do nome do produto a ser cadastrado.
     */
    private JTextField fieldProductName;

    /**
     * Campo de texto do preço base do produto.
     * O valor é convertido para Double em onSave().
     */
    private JTextField fieldBasePrice;

    /**
     * Campo de texto do nome do sabor (novo).
     * A tela assume que o sabor será criado junto do produto.
     */
    private JTextField fieldFlavorName;

    /**
     * Campo de texto multilinha da descrição do produto.
     * Pode ser vazio, e é enviado como null ou string (dependendo do conteúdo) para o controller.
     */
    private JTextArea fieldDescription;

    /**
     * ComboBox de nível do sabor (FlavorLevel).
     * É carregado a partir do banco em loadCombos().
     */
    private JComboBox<FlavorLevel> comboFlavorLevel;

    /**
     * ComboBox de tamanho (Size).
     * É carregado a partir do banco em loadCombos().
     */
    private JComboBox<Size> comboSize;

    /**
     * Tabela que lista os produtos cadastrados.
     * Cada linha representa um Product da lista products.
     */
    private JTable tableProducts;

    /**
     * Modelo da tabela de produtos.
     * Colunas:
     * Produto, Sabor, Nível, Tamanho, Preço final.
     */
    private DefaultTableModel tableModel;

    /**
     * Lista de produtos carregada do banco.
     * O índice nesta lista corresponde ao índice da linha na tableProducts.
     * É usada em onDelete() para identificar o produto selecionado.
     */
    private List<Product> products = new ArrayList<>();

    /**
     * Construtor da tela.
     * Configura a janela, monta o painel principal, carrega os combos e carrega a tabela.
     */
    public ViewProducts() {
        configureFrame();
        setContentPane(buildMainPanel());
        loadCombos();
        refreshTable();
    }

    /**
     * Configura propriedades do JFrame:
     * - título
     * - operação de fechamento
     * - tamanho
     * - centralização
     * - cor de fundo do tema
     */
    private void configureFrame() {
        setTitle("Produtos (Admin) - Sistema de Confeitaria");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    /**
     * Monta o painel principal da tela.
     *
     * Funcionamento:
     * 1. Cria painel raiz com BorderLayout.
     * 2. Adiciona cabeçalho com o título.
     * 3. Adiciona conteúdo em duas colunas:
     *    - formulário de cadastro
     *    - tabela de produtos
     * 4. Envolve os painéis de conteúdo em cards (wrapCard) para padronizar visual.
     *
     * @return painel principal
     */
    private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(24, 24, 24, 24);
        panel.setLayout(new BorderLayout(16, 16));

        panel.add(buildHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 16, 0));
        content.setBackground(ViewTheme.BACKGROUND);
        content.add(wrapCard(buildFormPanel()));
        content.add(wrapCard(buildTablePanel()));

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Monta o cabeçalho da tela com o título.
     *
     * @return componente do cabeçalho
     */
    private Component buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ViewTheme.BACKGROUND);
        header.add(ViewTheme.createTitleLabel("Cadastro de Produtos (Admin)"), BorderLayout.WEST);
        return header;
    }

    /**
     * Monta o painel de formulário e inicializa campos e combos.
     *
     * Funcionamento:
     * 1. Cria painel vertical.
     * 2. Adiciona campo de nome do produto.
     * 3. Adiciona campo de preço base.
     * 4. Adiciona campo de nome do sabor.
     * 5. Adiciona combo de nível do sabor com renderer de nome.
     * 6. Adiciona combo de tamanho com renderer de nome.
     * 7. Adiciona campo de descrição com scroll.
     * 8. Adiciona painel de botões.
     *
     * @return componente do formulário
     */
    private Component buildFormPanel() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(ViewTheme.CARD_BG);

        form.add(ViewTheme.createFieldLabel("Nome do produto"));
        fieldProductName = ViewTheme.createTextField(24);
        form.add(fieldProductName);
        form.add(Box.createVerticalStrut(10));

        form.add(ViewTheme.createFieldLabel("Preço base"));
        fieldBasePrice = ViewTheme.createTextField(10);
        form.add(fieldBasePrice);
        form.add(Box.createVerticalStrut(10));

        form.add(ViewTheme.createFieldLabel("Nome do sabor (novo)"));
        fieldFlavorName = ViewTheme.createTextField(24);
        form.add(fieldFlavorName);
        form.add(Box.createVerticalStrut(10));

        form.add(ViewTheme.createFieldLabel("Nível do sabor"));
        comboFlavorLevel = new JComboBox<>();
        comboFlavorLevel.setRenderer(new NamedCellRenderer());
        styleComboBox(comboFlavorLevel);
        form.add(comboFlavorLevel);
        form.add(Box.createVerticalStrut(10));

        form.add(ViewTheme.createFieldLabel("Tamanho"));
        comboSize = new JComboBox<>();
        comboSize.setRenderer(new NamedCellRenderer());
        styleComboBox(comboSize);
        form.add(comboSize);
        form.add(Box.createVerticalStrut(10));

        form.add(ViewTheme.createFieldLabel("Descrição"));
        fieldDescription = new JTextArea(4, 24);
        fieldDescription.setFont(ViewTheme.FONT_LABEL);
        fieldDescription.setLineWrap(true);
        fieldDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(fieldDescription);
        descScroll.setBorder(BorderFactory.createLineBorder(ViewTheme.BORDER));
        form.add(descScroll);
        form.add(Box.createVerticalStrut(14));

        form.add(buildButtonsPanel());

        return form;
    }

    /**
     * Monta o painel de botões do formulário.
     *
     * Botões:
     * - Novo: limpa o formulário
     * - Salvar: chama onSave()
     * - Recarregar: chama refreshTable()
     * - Excluir: chama onDelete()
     *
     * @return componente com os botões
     */
    private Component buildButtonsPanel() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setBackground(ViewTheme.CARD_BG);

        JButton btnNew = ViewTheme.createSecondaryButton("Novo");
        btnNew.addActionListener(e -> clearForm());

        JButton btnSave = ViewTheme.createPrimaryButton("Salvar");
        btnSave.addActionListener(e -> onSave());

        JButton btnReload = ViewTheme.createSecondaryButton("Recarregar");
        btnReload.addActionListener(e -> refreshTable());

        JButton btnDelete = ViewTheme.createSecondaryButton("Excluir");
        btnDelete.addActionListener(e -> onDelete());

        buttons.add(btnNew);
        buttons.add(btnSave);
        buttons.add(btnReload);
        buttons.add(btnDelete);

        return buttons;
    }

    /**
     * Monta o painel da tabela de produtos.
     *
     * Funcionamento:
     * 1. Cria tableModel com colunas e células não editáveis.
     * 2. Cria tableProducts e define seleção única.
     * 3. Coloca tabela em JScrollPane e aplica borda do tema.
     *
     * @return componente do painel de tabela
     */
    private Component buildTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setBackground(ViewTheme.CARD_BG);

        tableModel = new DefaultTableModel(
                new String[]{"Produto", "Sabor", "Nível", "Tamanho", "Preço final"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tableProducts = new JTable(tableModel);
        tableProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tableProducts);
        scroll.setBorder(BorderFactory.createLineBorder(ViewTheme.BORDER));
        tablePanel.add(scroll, BorderLayout.CENTER);

        return tablePanel;
    }

    /**
     * Carrega os combos de nível de sabor e tamanho.
     *
     * Funcionamento:
     * 1. Limpa comboFlavorLevel e adiciona itens retornados por controller.listFlavorLevels().
     * 2. Limpa comboSize e adiciona itens retornados por controller.listSizes().
     * 3. Se algum combo ficar vazio, avisa o usuário que pode faltar seed no banco.
     *
     * Tratamento de erro:
     * - DataAccessException: exibe mensagem de erro.
     */
    private void loadCombos() {
        try {
            comboFlavorLevel.removeAllItems();
            for (FlavorLevel lvl : controller.listFlavorLevels()) {
                comboFlavorLevel.addItem(lvl);
            }

            comboSize.removeAllItems();
            for (Size size : controller.listSizes()) {
                comboSize.addItem(size);
            }

            if (comboFlavorLevel.getItemCount() == 0 || comboSize.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "Não existem níveis de sabor ou tamanhos no banco.\n" +
                                "Verifique o SeedService.",
                        "Atenção",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Recarrega a lista de produtos do banco e atualiza a tabela.
     *
     * Funcionamento:
     * 1. Chama controller.listProducts() e armazena em products.
     * 2. Limpa as linhas do tableModel.
     * 3. Para cada produto:
     *    - extrai strings de sabor, nível e tamanho tratando null
     *    - calcula preço final com computeFinalUnitPrice(Product)
     *    - adiciona uma linha na tabela
     *
     * Tratamento de erro:
     * - DataAccessException: exibe mensagem de erro.
     */
    private void refreshTable() {
        try {
            products = controller.listProducts();
            tableModel.setRowCount(0);

            for (Product p : products) {
                String flavorName = (p.getFlavor() != null) ? p.getFlavor().getName() : "";
                String levelName = (p.getFlavor() != null && p.getFlavor().getLevel() != null) ? p.getFlavor().getLevel().getName() : "";
                String sizeName = (p.getSize() != null) ? p.getSize().getName() : "";

                double finalPrice = computeFinalUnitPrice(p);

                tableModel.addRow(new Object[]{
                        p.getName(),
                        flavorName,
                        levelName,
                        sizeName,
                        String.format("R$ %.2f", finalPrice)
                });
            }
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calcula o preço final unitário exibido na tabela.
     *
     * Regra usada:
     * finalUnitPrice = basePrice + size.price + flavor.level.price
     *
     * @param p produto para cálculo
     * @return preço final unitário
     */
    private static double computeFinalUnitPrice(Product p) {
        double base = p.getBasePrice() != null ? p.getBasePrice() : 0.0;
        double sizePrice = (p.getSize() != null && p.getSize().getPrice() != null) ? p.getSize().getPrice() : 0.0;
        double levelPrice = (p.getFlavor() != null && p.getFlavor().getLevel() != null && p.getFlavor().getLevel().getPrice() != null)
                ? p.getFlavor().getLevel().getPrice()
                : 0.0;
        return base + sizePrice + levelPrice;
    }

    /**
     * Salva um novo produto com um novo sabor.
     *
     * Funcionamento:
     * 1. Lê e normaliza textos do formulário.
     * 2. Obtém o nível de sabor e o tamanho selecionados.
     * 3. Converte o preço base para Double aceitando vírgula ou ponto.
     * 4. Chama controller.createProductWithNewFlavor(...) para criar sabor e produto.
     * 5. Se der certo:
     *    - limpa o formulário
     *    - recarrega a tabela
     *    - mostra mensagem de sucesso
     *
     * Tratamento de erro:
     * - Preço inválido: exibe mensagem e retorna.
     * - ValidationException: exibe aviso.
     * - DataAccessException: exibe erro.
     */
    private void onSave() {
        String productName = text(fieldProductName);
        String basePriceText = text(fieldBasePrice);
        String flavorName = text(fieldFlavorName);
        String description = fieldDescription.getText() != null ? fieldDescription.getText().trim() : null;

        FlavorLevel level = (FlavorLevel) comboFlavorLevel.getSelectedItem();
        Size size = (Size) comboSize.getSelectedItem();

        Double basePrice;
        try {
            basePrice = Double.parseDouble(basePriceText.replace(",", "."));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Preço base inválido.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            controller.createProductWithNewFlavor(productName, basePrice, flavorName, level, size, description);
            clearForm();
            refreshTable();
            JOptionPane.showMessageDialog(this, "Produto salvo com sucesso!", "Produtos", JOptionPane.INFORMATION_MESSAGE);

        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exclui o produto selecionado na tabela.
     *
     * Funcionamento:
     * 1. Obtém a linha selecionada.
     * 2. Se não houver seleção válida, mostra mensagem e retorna.
     * 3. Obtém o Product correspondente na lista products.
     * 4. Pede confirmação ao usuário.
     * 5. Se confirmado, chama controller.deleteProduct(selected).
     * 6. Se der certo:
     *    - limpa formulário
     *    - recarrega tabela
     *    - mostra mensagem de sucesso
     *
     * Tratamento de erro:
     * - ValidationException: exibe aviso.
     * - DataAccessException: exibe erro.
     */
    private void onDelete() {
        int row = tableProducts.getSelectedRow();
        if (row < 0 || row >= products.size()) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um produto na tabela para excluir.",
                    "Produtos",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Product selected = products.get(row);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja excluir o produto \"" + selected.getName() + "\"?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            controller.deleteProduct(selected);
            clearForm();
            refreshTable();
            JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!", "Produtos", JOptionPane.INFORMATION_MESSAGE);

        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Limpa os campos do formulário e reseta os combos para o primeiro item quando possível.
     */
    private void clearForm() {
        fieldProductName.setText("");
        fieldBasePrice.setText("");
        fieldFlavorName.setText("");
        fieldDescription.setText("");
        if (comboFlavorLevel.getItemCount() > 0) comboFlavorLevel.setSelectedIndex(0);
        if (comboSize.getItemCount() > 0) comboSize.setSelectedIndex(0);
    }

    /**
     * Lê o texto de um JTextField de forma segura.
     * Retorna string vazia quando o campo estiver null ou com texto null.
     *
     * @param f campo a ler
     * @return texto normalizado (trim) ou vazio
     */
    private static String text(JTextField f) {
        return f.getText() == null ? "" : f.getText().trim();
    }

    /**
     * Envolve um componente com estilo de card.
     * Aplica background, borda e padding.
     *
     * @param content componente interno
     * @return painel estilizado
     */
    private JPanel wrapCard(Component content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ViewTheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    /**
     * Aplica estilo padrão do tema a um JComboBox.
     * Ajusta background, foreground e fonte.
     *
     * @param combo combo a estilizar
     */
    private void styleComboBox(JComboBox<?> combo) {
        combo.setBackground(ViewTheme.INPUT_BG);
        combo.setForeground(ViewTheme.TEXT);
        combo.setFont(ViewTheme.FONT_LABEL);
    }

    /**
     * Renderer de itens para combos que exibem objetos (FlavorLevel e Size).
     * Converte o objeto em texto visível no combo usando getName().
     */
    private static class NamedCellRenderer extends DefaultListCellRenderer {

        /**
         * Retorna o componente renderizado para um item do combo.
         *
         * Funcionamento:
         * 1. Chama o renderer padrão (super).
         * 2. Se value for FlavorLevel, define o texto como o nome do nível.
         * 3. Se value for Size, define o texto como o nome do tamanho.
         * 4. Retorna o próprio renderer.
         *
         * @param list lista do combo
         * @param value valor do item
         * @param index índice do item
         * @param isSelected indica se está selecionado
         * @param cellHasFocus indica se tem foco
         * @return componente que representa o item
         */
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof FlavorLevel) {
                setText(((FlavorLevel) value).getName());
            } else if (value instanceof Size) {
                setText(((Size) value).getName());
            }
            return this;
        }
    }
}