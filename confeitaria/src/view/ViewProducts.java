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

public class ViewProducts extends JFrame {

    private final ControllerProductAdmin controller = new ControllerProductAdmin();

    private JTextField fieldProductName;
    private JTextField fieldBasePrice;
    private JTextField fieldFlavorName;
    private JTextArea fieldDescription;
    private JComboBox<FlavorLevel> comboFlavorLevel;
    private JComboBox<Size> comboSize;

    private JTable tableProducts;
    private DefaultTableModel tableModel;

    private List<Product> products = new ArrayList<>();

    public ViewProducts() {
        configureFrame();
        setContentPane(buildMainPanel());
        loadCombos();
        refreshTable();
    }

    private void configureFrame() {
        setTitle("Produtos (Admin) - Sistema de Confeitaria");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

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

    private Component buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ViewTheme.BACKGROUND);
        header.add(ViewTheme.createTitleLabel("Cadastro de Produtos (Admin)"), BorderLayout.WEST);
        return header;
    }

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

    private static double computeFinalUnitPrice(Product p) {
        double base = p.getBasePrice() != null ? p.getBasePrice() : 0.0;
        double sizePrice = (p.getSize() != null && p.getSize().getPrice() != null) ? p.getSize().getPrice() : 0.0;
        double levelPrice = (p.getFlavor() != null && p.getFlavor().getLevel() != null && p.getFlavor().getLevel().getPrice() != null)
                ? p.getFlavor().getLevel().getPrice()
                : 0.0;
        return base + sizePrice + levelPrice;
    }

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

    private void clearForm() {
        fieldProductName.setText("");
        fieldBasePrice.setText("");
        fieldFlavorName.setText("");
        fieldDescription.setText("");
        if (comboFlavorLevel.getItemCount() > 0) comboFlavorLevel.setSelectedIndex(0);
        if (comboSize.getItemCount() > 0) comboSize.setSelectedIndex(0);
    }

    private static String text(JTextField f) {
        return f.getText() == null ? "" : f.getText().trim();
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

    private void styleComboBox(JComboBox<?> combo) {
        combo.setBackground(ViewTheme.INPUT_BG);
        combo.setForeground(ViewTheme.TEXT);
        combo.setFont(ViewTheme.FONT_LABEL);
    }

    private static class NamedCellRenderer extends DefaultListCellRenderer {
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