package view;

import controller.ControllerCart;
import exceptions.ValidationException;
import model.entities.Product;

import javax.swing.*;
import java.awt.*;

public class ViewProductDetails extends JFrame {

    private final Product product;
    private final ControllerCart controllerCart = new ControllerCart();

    private JSpinner spinnerQty;
    private JLabel labelUnitPrice;
    private JLabel labelTotal;

    public ViewProductDetails(Product product) {
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Produto inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            dispose();
            this.product = null;
            return;
        }

        this.product = product;
        configureFrame();
        setContentPane(buildMainPanel());
        updateTotals();
    }

    private void configureFrame() {
        setTitle("Produto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 420);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
        setResizable(false);
    }

    private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(24, 24, 24, 24);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(ViewTheme.createTitleLabel(product.getName()));
        panel.add(Box.createVerticalStrut(10));

        String flavor = product.getFlavor() != null ? product.getFlavor().getName() : "";
        String level = (product.getFlavor() != null && product.getFlavor().getLevel() != null)
                ? product.getFlavor().getLevel().getName()
                : "";
        String size = product.getSize() != null ? product.getSize().getName() : "";

        panel.add(ViewTheme.createSubtitleLabel("Sabor: " + flavor));
        panel.add(Box.createVerticalStrut(6));
        panel.add(ViewTheme.createSubtitleLabel("Nível: " + level));
        panel.add(Box.createVerticalStrut(6));
        panel.add(ViewTheme.createSubtitleLabel("Tamanho: " + size));
        panel.add(Box.createVerticalStrut(12));

        labelUnitPrice = ViewTheme.createSubtitleLabel("Preço unitário: R$ 0,00");
        panel.add(labelUnitPrice);

        panel.add(Box.createVerticalStrut(12));
        panel.add(ViewTheme.createFieldLabel("Quantidade"));

        spinnerQty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spinnerQty.setMaximumSize(new Dimension(120, 32));
        spinnerQty.addChangeListener(e -> updateTotals());
        panel.add(spinnerQty);

        panel.add(Box.createVerticalStrut(12));
        labelTotal = ViewTheme.createSubtitleLabel("Total: R$ 0,00");
        panel.add(labelTotal);

        panel.add(Box.createVerticalStrut(18));

        JButton btnAdd = ViewTheme.createPrimaryButton("Adicionar ao carrinho");
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAdd.addActionListener(e -> onAdd());

        JButton btnGoCart = ViewTheme.createSecondaryButton("Ir para o carrinho");
        btnGoCart.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGoCart.addActionListener(e -> new ViewCart().setVisible(true));

        panel.add(btnAdd);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnGoCart);

        return panel;
    }

    private void onAdd() {
        try {
            Integer qty = (Integer) spinnerQty.getValue();
            controllerCart.addProduct(product, qty);

            JOptionPane.showMessageDialog(this,
                    "Adicionado ao carrinho!",
                    "Carrinho",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateTotals() {
        double unit = computeUnitPrice(product);
        int qty = spinnerQty != null ? (Integer) spinnerQty.getValue() : 1;
        double total = unit * qty;

        labelUnitPrice.setText(String.format("Preço unitário: R$ %.2f", unit));
        labelTotal.setText(String.format("Total: R$ %.2f", total));
    }

    private static double computeUnitPrice(Product p) {
        double base = p.getBasePrice() != null ? p.getBasePrice() : 0.0;

        double sizePrice = 0.0;
        if (p.getSize() != null && p.getSize().getPrice() != null) {
            sizePrice = p.getSize().getPrice();
        }

        double levelPrice = 0.0;
        if (p.getFlavor() != null && p.getFlavor().getLevel() != null && p.getFlavor().getLevel().getPrice() != null) {
            levelPrice = p.getFlavor().getLevel().getPrice();
        }

        return base + sizePrice + levelPrice;
    }
}