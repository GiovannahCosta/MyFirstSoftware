package view;

import app.CartSession;
import app.Session;
import model.entities.Person;
import model.entities.Product;
import model.repositories.RepositoryOrder;
import model.repositories.RepositoryOrderItems;
import model.repositories.RepositoryPerson;
import model.repositories.RepositoryProduct;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

public class ViewCheckout extends JFrame {

    private final RepositoryProduct repoProduct = new RepositoryProduct();
    private final RepositoryPerson repoPerson = new RepositoryPerson();
    private final RepositoryOrder repoOrder = new RepositoryOrder();
    private final RepositoryOrderItems repoOrderItems = new RepositoryOrderItems();

    private JRadioButton radioEntrega;
    private JRadioButton radioRetirada;
    private JTextArea fieldObs;

    private JLabel labelSubtotal;
    private JLabel labelTaxa;
    private JLabel labelTotal;

    private double subtotal = 0.0;
    private double taxaEntrega = 0.0;

    public ViewCheckout() {
        if (!Session.isLoggedIn()) {
            JOptionPane.showMessageDialog(this,
                    "Você precisa estar logado para finalizar a compra.",
                    "Login necessário",
                    JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        if (CartSession.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seu carrinho está vazio.",
                    "Carrinho",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }

        configureFrame();
        setContentPane(buildMainPanel());
        recalcTotals();
    }

    private void configureFrame() {
        setTitle("Finalizar compra");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    private JPanel buildMainPanel() {
        JPanel panel = ViewTheme.createPanel(24, 24, 24, 24);
        panel.setLayout(new BorderLayout(16, 16));

        panel.add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 16, 0));
        center.setBackground(ViewTheme.BACKGROUND);
        center.add(wrapCard(buildDeliveryPanel()));
        center.add(wrapCard(buildSummaryPanel()));

        panel.add(center, BorderLayout.CENTER);
        panel.add(buildFooter(), BorderLayout.SOUTH);

        return panel;
    }

    private Component buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ViewTheme.BACKGROUND);

        header.add(ViewTheme.createTitleLabel("Checkout"), BorderLayout.WEST);
        return header;
    }

    private Component buildDeliveryPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(ViewTheme.CARD_BG);

        p.add(ViewTheme.createFieldLabel("Forma de recebimento"));

        radioEntrega = new JRadioButton("Entrega (no meu endereço)");
        radioRetirada = new JRadioButton("Retirada");

        ButtonGroup group = new ButtonGroup();
        group.add(radioEntrega);
        group.add(radioRetirada);

        radioEntrega.setBackground(ViewTheme.CARD_BG);
        radioRetirada.setBackground(ViewTheme.CARD_BG);

        radioEntrega.setSelected(true);

        radioEntrega.addActionListener(e -> recalcTotals());
        radioRetirada.addActionListener(e -> recalcTotals());

        p.add(Box.createVerticalStrut(6));
        p.add(radioEntrega);
        p.add(radioRetirada);

        p.add(Box.createVerticalStrut(14));
        p.add(ViewTheme.createFieldLabel("Observações"));
        fieldObs = new JTextArea(6, 26);
        fieldObs.setFont(ViewTheme.FONT_LABEL);
        fieldObs.setLineWrap(true);
        fieldObs.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(fieldObs);
        scroll.setBorder(BorderFactory.createLineBorder(ViewTheme.BORDER));
        p.add(scroll);

        p.add(Box.createVerticalStrut(14));
        p.add(ViewTheme.createSubtitleLabel("Endereço de entrega:"));
        p.add(Box.createVerticalStrut(6));

        JLabel lblAddress = ViewTheme.createSubtitleLabel(loadUserAddressText());
        lblAddress.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblAddress);

        return p;
    }

    private Component buildSummaryPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(ViewTheme.CARD_BG);

        p.add(ViewTheme.createFieldLabel("Resumo"));

        p.add(Box.createVerticalStrut(10));
        labelSubtotal = ViewTheme.createSubtitleLabel("Subtotal: R$ 0,00");
        labelTaxa = ViewTheme.createSubtitleLabel("Taxa de entrega: R$ 0,00");
        labelTotal = ViewTheme.createSubtitleLabel("Total: R$ 0,00");

        p.add(labelSubtotal);
        p.add(Box.createVerticalStrut(6));
        p.add(labelTaxa);
        p.add(Box.createVerticalStrut(12));
        p.add(labelTotal);

        p.add(Box.createVerticalGlue());
        return p;
    }

    private Component buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setBackground(ViewTheme.BACKGROUND);

        JButton btnConfirm = ViewTheme.createPrimaryButton("Confirmar pedido");
        btnConfirm.addActionListener(e -> onConfirm());

        JButton btnClose = ViewTheme.createSecondaryButton("Fechar");
        btnClose.addActionListener(e -> dispose());

        footer.add(btnClose);
        footer.add(btnConfirm);
        return footer;
    }

    private void recalcTotals() {
        subtotal = computeSubtotalFromCart();
        taxaEntrega = radioEntrega != null && radioEntrega.isSelected() ? computeDeliveryFeeFromUserArea() : 0.0;

        labelSubtotal.setText(String.format("Subtotal: R$ %.2f", subtotal));
        labelTaxa.setText(String.format("Taxa de entrega: R$ %.2f", taxaEntrega));
        labelTotal.setText(String.format("Total: R$ %.2f", subtotal + taxaEntrega));
    }

    private double computeSubtotalFromCart() {
        double sum = 0.0;

        for (Map.Entry<Integer, Integer> entry : CartSession.getItems().entrySet()) {
            Integer productId = entry.getKey();
            Integer qty = entry.getValue();

            try {
                Product p = repoProduct.findByIdProduct(productId);
                if (p == null) continue;

                double unit = computeUnitPrice(p);
                sum += unit * qty;

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao calcular subtotal: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return 0.0;
            }
        }

        return sum;
    }

    private double computeDeliveryFeeFromUserArea() {
        try {
            // Busca pessoa/endereço/área pelo email do usuário logado
            String email = Session.getLoggedUser().getEmail();
            Person p = repoPerson.findByEmailPerson(email);
            if (p == null || p.getAddress() == null || p.getAddress().getArea() == null) return 0.0;
            Double fee = p.getAddress().getArea().getFee();
            return fee != null ? fee : 0.0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao obter taxa de entrega: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return 0.0;
        }
    }

    private String loadUserAddressText() {
        try {
            String email = Session.getLoggedUser().getEmail();
            Person p = repoPerson.findByEmailPerson(email);
            if (p == null || p.getAddress() == null) return "(endereço não encontrado)";

            String area = (p.getAddress().getArea() != null) ? p.getAddress().getArea().getName() : "";
            String street = p.getAddress().getStreet() != null ? p.getAddress().getStreet() : "";
            String number = p.getAddress().getNumber() != null ? String.valueOf(p.getAddress().getNumber()) : "";
            String cep = p.getAddress().getCep() != null ? p.getAddress().getCep() : "";
            return street + ", " + number + " - " + area + " | CEP: " + cep;

        } catch (SQLException e) {
            return "(erro ao carregar endereço: " + e.getMessage() + ")";
        }
    }

    private void onConfirm() {
        if (CartSession.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Carrinho vazio.", "Checkout", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String delivery = radioEntrega.isSelected() ? "ENTREGA" : "RETIRADA";
        String obs = fieldObs.getText() != null ? fieldObs.getText().trim() : null;

        // total_price é subtotal + taxa (se entrega)
        double total = subtotal + taxaEntrega;

        try {
            Integer idUser = Session.getLoggedUser().getIdUser();
            if (idUser == null) {
                JOptionPane.showMessageDialog(this,
                        "Sessão inválida: usuário sem id_user.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer idOrder = repoOrder.createOrderAndReturnId(
                    idUser,
                    Timestamp.from(Instant.now()),
                    total,
                    delivery,
                    obs
            );

            if (idOrder == null) {
                JOptionPane.showMessageDialog(this,
                        "Não foi possível criar o pedido.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insere itens
            for (Map.Entry<Integer, Integer> entry : CartSession.getItems().entrySet()) {
                Integer productId = entry.getKey();
                Integer qty = entry.getValue();

                Product p = repoProduct.findByIdProduct(productId);
                if (p == null) continue;

                double unit = computeUnitPrice(p);

                boolean okItem = repoOrderItems.createOrderItem(idOrder, productId, qty, unit);
                if (!okItem) {
                    JOptionPane.showMessageDialog(this,
                            "Não foi possível salvar um item do pedido.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            CartSession.clear();
            JOptionPane.showMessageDialog(this, "Pedido confirmado com sucesso!");
            dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao confirmar pedido: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static double computeUnitPrice(Product p) {
        double base = p.getBasePrice() != null ? p.getBasePrice() : 0.0;
        double sizePrice = (p.getSize() != null && p.getSize().getPrice() != null) ? p.getSize().getPrice() : 0.0;
        double levelPrice = (p.getFlavor() != null && p.getFlavor().getLevel() != null && p.getFlavor().getLevel().getPrice() != null)
                ? p.getFlavor().getLevel().getPrice() : 0.0;

        return base + sizePrice + levelPrice;
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