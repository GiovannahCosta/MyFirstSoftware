package view;

import app.CartSession;
import controller.ControllerCartView;
import exceptions.DataAccessException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ViewCart extends JFrame {

    private final ControllerCartView controller = new ControllerCartView();

    private JTable table;
    private DefaultTableModel model;
    private JLabel labelSubtotal;

    // guardamos os ids na ordem das linhas para remover corretamente
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
            if (CartSession.isEmpty()) {
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

        try {
            ControllerCartView.CartViewData data = controller.loadCartData();

            for (ControllerCartView.CartRow row : data.getRows()) {
                productIds.add(row.getProductId());

                model.addRow(new Object[]{
                        row.getProductName(),
                        row.getQty(),
                        String.format("R$ %.2f", row.getUnit()),
                        String.format("R$ %.2f", row.getTotal())
                });
            }

            double subtotal = data.getSubtotal() != null ? data.getSubtotal() : 0.0;
            labelSubtotal.setText(String.format("Subtotal: R$ %.2f", subtotal));

        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
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
        controller.removeByProductId(productId);
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
}