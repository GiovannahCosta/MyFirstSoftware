package view;

import app.CartSession;
import model.entities.Product;
import model.repositories.RepositoryProduct;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tela de carrinho.
 * Exibe os itens adicionados ao carrinho (CartSession), calculando subtotal e permitindo:
 * - atualizar a tabela
 * - remover um item selecionado
 * - navegar para o checkout
 *
 * Esta tela consulta o banco para carregar o Product completo a partir do id armazenado no carrinho.
 */
public class ViewCart extends JFrame {

    /**
     * Repositório de produtos usado para buscar o produto completo no banco a partir do id do carrinho.
     * É necessário porque o carrinho armazena apenas o id do produto e a quantidade.
     */
    private final RepositoryProduct repoProduct = new RepositoryProduct();

    /**
     * Tabela Swing que exibe os itens do carrinho.
     */
    private JTable table;

    /**
     * Modelo da tabela com as colunas:
     * Produto, Qtd, Unitário, Total.
     */
    private DefaultTableModel model;

    /**
     * Label de subtotal exibido no cabeçalho.
     * É recalculado no método refresh().
     */
    private JLabel labelSubtotal;

    /**
     * Lista paralela ao model da JTable para manter o id do produto por linha.
     * O índice desta lista corresponde ao índice da linha da tabela.
     * É usada em onRemoveSelected() para descobrir qual produto remover do carrinho.
     */
    private final List<Integer> productIds = new ArrayList<>();

    /**
     * Construtor da tela.
     * Configura a janela, monta o conteúdo e carrega os dados do carrinho.
     */
    public ViewCart() {
        configureFrame();
        setContentPane(buildMainPanel());
        refresh();
    }

    /**
     * Configura propriedades do JFrame:
     * - título
     * - operação de fechamento
     * - tamanho
     * - centralização na tela
     * - cor de fundo
     */
    private void configureFrame() {
        setTitle("Carrinho");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(820, 520);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    /**
     * Monta o painel principal da tela.
     * Cria painel raiz com padding e layout BorderLayout.
     * Adiciona cabeçalho no NORTH (título, subtotal e botões).
     * Adiciona tabela no CENTER.
     *
     * @return painel principal
     */
    private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(24, 24, 24, 24);
        panel.setLayout(new BorderLayout(16, 16));

        panel.add(buildHeader(), BorderLayout.NORTH);
        panel.add(buildTable(), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Monta o cabeçalho da tela com:
     * - título "Carrinho"
     * - subtotal
     * - botões: Atualizar, Remover item e Finalizar compra
     *
     * Finalizar compra só abre o checkout se o carrinho não estiver vazio.
     *
     * @return componente do cabeçalho
     */
    private Component buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ViewTheme.BACKGROUND);

        header.add(ViewTheme.createTitleLabel("Carrinho"), BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(ViewTheme.BACKGROUND);

        labelSubtotal = ViewTheme.createSubtitleLabel("Subtotal: R$ 0,00");
        right.add(labelSubtotal);

        JButton btnRefresh = ViewTheme.createSecondaryButton("Atualizar");
        btnRefresh.addActionListener(e -> refresh());

        JButton btnRemove = ViewTheme.createSecondaryButton("Remover item");
        btnRemove.addActionListener(e -> onRemoveSelected());

        JButton btnCheckout = ViewTheme.createPrimaryButton("Finalizar compra");
        btnCheckout.addActionListener(e -> {
            if (app.CartSession.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Carrinho vazio.", "Checkout", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new ViewCheckout().setVisible(true);
        });

        right.add(btnRefresh);
        right.add(btnRemove);
        right.add(btnCheckout);

        header.add(right, BorderLayout.EAST);
        return header;
    }

    /**
     * Monta a tabela do carrinho.
     * Cria DefaultTableModel com colunas e células não editáveis.
     * Cria JTable e define modo de seleção para apenas uma linha.
     * Coloca a tabela em JScrollPane.
     * Aplica borda e encapsula em um card (wrapCard).
     *
     * @return componente com a tabela
     */
    private Component buildTable() {
        model = new DefaultTableModel(new String[]{"Produto", "Qtd", "Unitário", "Total"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(ViewTheme.BORDER));
        return wrapCard(scroll);
    }

    /**
     * Recarrega os itens do carrinho e atualiza a tabela e o subtotal.
     *
     * Funcionamento:
     * 1. Limpa a tabela e a lista productIds.
     * 2. Se o carrinho estiver vazio:
     *    - atualiza subtotal para 0
     *    - retorna
     * 3. Para cada entrada (productId -> qty) do carrinho:
     *    - busca Product no banco via repoProduct.findByIdProduct(productId)
     *    - calcula unitário via computeUnitPrice(product)
     *    - calcula total do item (unit * qty)
     *    - adiciona a linha na tabela
     *    - adiciona productId na lista productIds para manter vínculo com a linha
     * 4. Atualiza labelSubtotal com a soma dos totais.
     *
     * Se ocorrer SQLException ao buscar um produto, exibe mensagem e interrompe o refresh.
     */
    private void refresh() {
        model.setRowCount(0);
        productIds.clear();

        if (CartSession.isEmpty()) {
            labelSubtotal.setText("Subtotal: R$ 0,00");
            return;
        }

        double subtotal = 0.0;

        for (Map.Entry<Integer, Integer> entry : CartSession.getItems().entrySet()) {
            Integer productId = entry.getKey();
            Integer qty = entry.getValue();

            try {
                Product p = repoProduct.findByIdProduct(productId);
                if (p == null) continue;

                double unit = computeUnitPrice(p);
                double total = unit * qty;
                subtotal += total;

                productIds.add(productId);
                model.addRow(new Object[]{
                        p.getName(),
                        qty,
                        String.format("R$ %.2f", unit),
                        String.format("R$ %.2f", total)
                });

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao carregar produto do carrinho: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        labelSubtotal.setText(String.format("Subtotal: R$ %.2f", subtotal));
    }

    /**
     * Remove o item selecionado da tabela do carrinho.
     *
     * Obtém a linha selecionada na JTable.
     * Se não houver linha selecionada, exibe mensagem e retorna.
     * Usa a lista productIds para descobrir o productId associado à linha.
     * Remove do carrinho via CartSession.remove(productId).
     * Atualiza a tabela chamando refresh().
     */
    private void onRemoveSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= productIds.size()) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um item para remover.",
                    "Carrinho",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Integer productId = productIds.get(row);
        CartSession.remove(productId);
        refresh();
    }

    /**
     * Envolve um componente em um painel com estilo de "card".
     * Aplica:
     * - background de card
     * - borda com cor do tema
     * - padding interno
     *
     * @param content componente que será exibido dentro do card
     * @return painel estilizado contendo o componente
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
     * Calcula o preço unitário final de um produto no carrinho.
     *
     * preço unitário = basePrice + size.price + flavor.level.price
     *
     * Lê basePrice (se null assume 0).
     * Lê price do Size (se não existir assume 0).
     * Lê price do FlavorLevel (se não existir assume 0).
     * Retorna a soma.
     *
     * @param p produto carregado do banco
     * @return preço unitário final
     */
    private static double computeUnitPrice(Product p) {
        double base = p.getBasePrice() != null ? p.getBasePrice() : 0.0;

        double sizePrice = (p.getSize() != null && p.getSize().getPrice() != null) ? p.getSize().getPrice() : 0.0;
        double levelPrice = (p.getFlavor() != null && p.getFlavor().getLevel() != null && p.getFlavor().getLevel().getPrice() != null)
                ? p.getFlavor().getLevel().getPrice() : 0.0;

        return base + sizePrice + levelPrice;
    }
}