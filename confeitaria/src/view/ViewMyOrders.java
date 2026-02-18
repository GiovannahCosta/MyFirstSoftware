package view;

import app.Session;
import controller.ControllerMyOrders;
import exceptions.AppException;
import exceptions.DataAccessException;
import exceptions.ValidationException;
import model.entities.OrderItemSummary;
import model.entities.OrderSummary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewMyOrders extends JFrame {

    private final ControllerMyOrders controller = new ControllerMyOrders();

    private JTable tableOrders;
    private DefaultTableModel modelOrders;

    private JTable tableItems;
    private DefaultTableModel modelItems;

    private JLabel labelOrderTitle;

    private List<OrderSummary> orders = new ArrayList<>();

    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ViewMyOrders() {
        if (!Session.isLoggedIn()) {
            JOptionPane.showMessageDialog(this,
                    "Você precisa fazer login para ver seus pedidos.",
                    "Login necessário",
                    JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        configureFrame();
        setContentPane(buildMainPanel());
        loadOrders();
    }

    private void configureFrame() {
        setTitle("Meus pedidos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 560);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(24, 24, 24, 24);
        panel.setLayout(new BorderLayout(16, 16));

        panel.add(buildHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 16, 0));
        content.setBackground(ViewTheme.BACKGROUND);

        content.add(wrapCard(buildOrdersPanel()));
        content.add(wrapCard(buildItemsPanel()));

        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private Component buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ViewTheme.BACKGROUND);

        header.add(ViewTheme.createTitleLabel("Meus pedidos"), BorderLayout.WEST);

        JButton btnRefresh = ViewTheme.createSecondaryButton("Atualizar");
        btnRefresh.addActionListener(e -> loadOrders());
        header.add(btnRefresh, BorderLayout.EAST);

        return header;
    }

    private Component buildOrdersPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(ViewTheme.CARD_BG);

        JLabel title = ViewTheme.createSubtitleLabel("Pedidos");
        p.add(title, BorderLayout.NORTH);

        modelOrders = new DefaultTableModel(new String[]{"ID", "Data/Hora", "Tipo", "Total"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableOrders = new JTable(modelOrders);
        tableOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableOrders.getSelectionModel().addListSelectionListener(e -> onOrderSelected());

        JScrollPane scroll = new JScrollPane(tableOrders);
        scroll.setBorder(BorderFactory.createLineBorder(ViewTheme.BORDER));
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private Component buildItemsPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(ViewTheme.CARD_BG);

        labelOrderTitle = ViewTheme.createSubtitleLabel("Itens do pedido: (selecione um pedido)");
        p.add(labelOrderTitle, BorderLayout.NORTH);

        modelItems = new DefaultTableModel(new String[]{"Produto", "Qtd", "Unitário", "Total"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableItems = new JTable(modelItems);
        tableItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tableItems);
        scroll.setBorder(BorderFactory.createLineBorder(ViewTheme.BORDER));
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private void loadOrders() {
        modelOrders.setRowCount(0);
        modelItems.setRowCount(0);
        labelOrderTitle.setText("Itens do pedido: (selecione um pedido)");
        orders = new ArrayList<>();

        try {
            Integer idUser = Session.getLoggedUser().getIdUser();
            orders = controller.listOrdersByUser(idUser);

            for (OrderSummary o : orders) {
                String dt = o.getDatetime() != null ? fmt.format(o.getDatetime()) : "";
                modelOrders.addRow(new Object[]{
                        o.getId(),
                        dt,
                        o.getDelivery(),
                        String.format("R$ %.2f", o.getTotalPrice() != null ? o.getTotalPrice() : 0.0)
                });
            }

        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onOrderSelected() {
        int row = tableOrders.getSelectedRow();
        if (row < 0 || row >= orders.size()) return;

        OrderSummary selected = orders.get(row);
        loadItems(selected.getId());
    }

    private void loadItems(Integer idOrder) {
        modelItems.setRowCount(0);

        try {
            List<OrderItemSummary> items = controller.listItems(idOrder);
            labelOrderTitle.setText("Itens do pedido: #" + idOrder);

            for (OrderItemSummary it : items) {
                double unit = it.getPriceAtMoment() != null ? it.getPriceAtMoment() : 0.0;
                int qty = it.getQuantity() != null ? it.getQuantity() : 0;

                modelItems.addRow(new Object[]{
                        it.getProductName(),
                        qty,
                        String.format("R$ %.2f", unit),
                        String.format("R$ %.2f", unit * qty)
                });
            }
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
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