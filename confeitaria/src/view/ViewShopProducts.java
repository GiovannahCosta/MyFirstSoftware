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

public class ViewShopProducts extends JFrame {

    private final ControllerShop controller = new ControllerShop();

    private JTable tableProducts;
    private DefaultTableModel tableModel;

    private List<Product> products = new ArrayList<>();

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

    private void configureFrame() {
        setTitle("Comprar - Produtos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(920, 560);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(24, 24, 24, 24);
        panel.setLayout(new BorderLayout(16, 16));

        panel.add(buildHeader(), BorderLayout.NORTH);
        panel.add(buildTablePanel(), BorderLayout.CENTER);

        return panel;
    }

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