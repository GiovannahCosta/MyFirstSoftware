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

public class ViewCart extends JFrame {

    private final RepositoryProduct repoProduct = new RepositoryProduct();

    private JTable table;
    private DefaultTableModel model;
    private JLabel labelSubtotal;

    private final List<Integer> productIds = new ArrayList<>();

    public ViewCart() {
        configureFrame();
        setContentPane(buildMainPanel());
        refresh();
    }

    private void configureFrame() {
        setTitle("Carrinho");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(820, 520);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(24, 24, 24, 24);
        panel.setLayout(new BorderLayout(16, 16));

        panel.add(buildHeader(), BorderLayout.NORTH);
        panel.add(buildTable(), BorderLayout.CENTER);

        return panel;
    }

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

    private JPanel wrapCard(Component content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ViewTheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ViewTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private static double computeUnitPrice(Product p) {
        double base = p.getBasePrice() != null ? p.getBasePrice() : 0.0;

        double sizePrice = (p.getSize() != null && p.getSize().getPrice() != null) ? p.getSize().getPrice() : 0.0;
        double levelPrice = (p.getFlavor() != null && p.getFlavor().getLevel() != null && p.getFlavor().getLevel().getPrice() != null)
                ? p.getFlavor().getLevel().getPrice() : 0.0;

        return base + sizePrice + levelPrice;
    }
}