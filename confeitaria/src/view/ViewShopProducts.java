package view;

import app.Session;
import controller.ControllerShop;
import exceptions.DataAccessException;
import model.entities.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de compra de produtos.
 * Exibe a lista de produtos disponíveis para o usuário logado e permite:
 * - atualizar a lista
 * - abrir detalhes de um produto para adicionar ao carrinho
 * - abrir o carrinho
 * - abrir a tela de "Meus pedidos"
 *
 * A listagem é carregada através do ControllerShop.
 */
public class ViewShopProducts extends JFrame {

    /**
     * Controller do fluxo de compra.
     * Responsável por listar os produtos disponíveis no banco.
     */
    private final ControllerShop controller = new ControllerShop();

    /**
     * Tabela Swing que exibe os produtos disponíveis.
     */
    private JTable tableProducts;

    /**
     * Modelo da tabela de produtos.
     * Colunas:
     * Produto, Sabor, Nível, Tamanho, Preço base.
     */
    private DefaultTableModel tableModel;

    /**
     * Lista de produtos carregada do banco.
     * O índice na lista corresponde ao índice da linha na tabela.
     * É usada no método openDetails() para recuperar o Product selecionado.
     */
    private List<Product> products = new ArrayList<>();

    /**
     * Construtor da tela.
     * Exige que o usuário esteja logado.
     * Se não estiver, exibe mensagem, fecha a tela e não monta a UI.
     * Se estiver, configura a janela, monta a UI e carrega a tabela.
     */
    public ViewShopProducts() {
        if (!Session.isLoggedIn()) {
            JOptionPane.showMessageDialog(this,
                    "Você precisa fazer login para comprar.",
                    "Login necessário",
                    JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        configureFrame();
        setContentPane(buildMainPanel());
        refreshTable();
    }

    /**
     * Configura propriedades do JFrame:
     * - título
     * - operação de fechamento (fecha a janela)
     * - tamanho
     * - centralização
     * - cor de fundo do tema
     */
    private void configureFrame() {
        setTitle("Comprar - Produtos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(920, 560);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    /**
     * Monta o painel principal da tela.
     *
     * Funcionamento:
     * 1. Cria painel raiz com padding e BorderLayout.
     * 2. Adiciona cabeçalho com título e botões de ação.
     * 3. Adiciona painel com tabela no centro.
     *
     * @return painel principal
     */
    private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(24, 24, 24, 24);
        panel.setLayout(new BorderLayout(16, 16));

        panel.add(buildHeader(), BorderLayout.NORTH);
        panel.add(buildTablePanel(), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Monta o cabeçalho com:
     * - título "Produtos disponíveis"
     * - botões de ação: Atualizar, Ver/Adicionar, Carrinho, Meus pedidos
     *
     * Funcionamento dos botões:
     * - Atualizar: chama refreshTable()
     * - Ver/Adicionar: chama openDetails()
     * - Carrinho: abre ViewCart
     * - Meus pedidos: abre ViewMyOrders
     *
     * @return componente do cabeçalho
     */
    private Component buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ViewTheme.BACKGROUND);

        JLabel title = ViewTheme.createTitleLabel("Produtos disponíveis");
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(ViewTheme.BACKGROUND);

        JButton btnRefresh = ViewTheme.createSecondaryButton("Atualizar");
        btnRefresh.addActionListener(e -> refreshTable());

        JButton btnDetails = ViewTheme.createPrimaryButton("Ver / Adicionar");
        btnDetails.addActionListener(e -> openDetails());

        JButton btnCart = ViewTheme.createSecondaryButton("Carrinho");
        btnCart.addActionListener(e -> new ViewCart().setVisible(true));

        JButton btnMyOrders = ViewTheme.createSecondaryButton("Meus pedidos");
        btnMyOrders.addActionListener(e -> new ViewMyOrders().setVisible(true));

        actions.add(btnRefresh);
        actions.add(btnDetails);
        actions.add(btnCart);
        actions.add(btnMyOrders);

        header.add(actions, BorderLayout.EAST);
        return header;
    }

    /**
     * Monta o painel da tabela de produtos.
     *
     * Funcionamento:
     * 1. Cria um painel com background de card.
     * 2. Cria tableModel com as colunas e células não editáveis.
     * 3. Cria tableProducts e define seleção de linha única.
     * 4. Encapsula a tabela em JScrollPane com borda do tema.
     * 5. Envolve o painel em um card via wrapCard().
     *
     * @return componente do painel da tabela
     */
    private Component buildTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(ViewTheme.CARD_BG);

        tableModel = new DefaultTableModel(
                new String[]{"Produto", "Sabor", "Nível", "Tamanho", "Preço base"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tableProducts = new JTable(tableModel);
        tableProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tableProducts);
        scroll.setBorder(BorderFactory.createLineBorder(ViewTheme.BORDER));
        tablePanel.add(scroll, BorderLayout.CENTER);

        return wrapCard(tablePanel);
    }

    /**
     * Recarrega a lista de produtos e atualiza a tabela.
     *
     * Funcionamento:
     * 1. Chama controller.listAllProducts() e armazena em products.
     * 2. Limpa as linhas do tableModel.
     * 3. Para cada produto, extrai sabor, nível e tamanho tratando null.
     * 4. Adiciona linha na tabela com informações do produto.
     *
     * Tratamento de erro:
     * - DataAccessException: exibe mensagem de erro.
     */
    private void refreshTable() {
        try {
            products = controller.listAllProducts();
            tableModel.setRowCount(0);

            for (Product p : products) {
                String flavorName = p.getFlavor() != null ? p.getFlavor().getName() : "";
                String levelName = (p.getFlavor() != null && p.getFlavor().getLevel() != null)
                        ? p.getFlavor().getLevel().getName()
                        : "";
                String sizeName = p.getSize() != null ? p.getSize().getName() : "";

                tableModel.addRow(new Object[]{
                        p.getName(),
                        flavorName,
                        levelName,
                        sizeName,
                        p.getBasePrice()
                });
            }
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre a tela de detalhes do produto selecionado.
     *
     * Funcionamento:
     * 1. Obtém a linha selecionada na tabela.
     * 2. Se não houver seleção válida, exibe mensagem e retorna.
     * 3. Obtém o Product correspondente na lista products.
     * 4. Abre ViewProductDetails passando o produto.
     */
    private void openDetails() {
        int row = tableProducts.getSelectedRow();
        if (row < 0 || row >= products.size()) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um produto.",
                    "Produtos",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Product selected = products.get(row);
        new ViewProductDetails(selected).setVisible(true);
    }

    /**
     * Envolve um componente em um painel com estilo de card.
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
}