package view;

import app.CartSession;
import app.Session;
import controller.ControllerCheckout;
import exceptions.AppException;
import exceptions.DataAccessException;
import exceptions.ValidationException;
import model.entities.Person;
import model.entities.Product;
import model.repositories.RepositoryPerson;
import model.repositories.RepositoryProduct;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

/**
 * Tela de checkout.
 * Permite o usuário finalizar a compra escolhendo entrega ou retirada e informando observações.
 * Calcula subtotal, taxa de entrega (se aplicável) e total.
 * Confirma o pedido chamando o ControllerCheckout.
 */
public class ViewCheckout extends JFrame {

    /**
     * Repositório de produtos usado para calcular subtotal.
     * O carrinho armazena apenas ids e quantidades, então é necessário buscar o Product no banco.
     */
    private final RepositoryProduct repoProduct = new RepositoryProduct();

    /**
     * Repositório de pessoas usado para obter o endereço e a taxa de entrega associada à área do usuário.
     * A taxa de entrega é calculada com base na área do Address da Person.
     */
    private final RepositoryPerson repoPerson = new RepositoryPerson();

    /**
     * Controller responsável por persistir o pedido e os itens no banco.
     * Recebe o total, tipo de recebimento, observações e itens do carrinho.
     */
    private final ControllerCheckout controllerCheckout = new ControllerCheckout();

    /**
     * RadioButton para seleção de entrega.
     * Quando selecionado, a taxa de entrega é aplicada ao total.
     */
    private JRadioButton radioEntrega;

    /**
     * RadioButton para seleção de retirada.
     * Quando selecionado, a taxa de entrega é zero.
     */
    private JRadioButton radioRetirada;

    /**
     * Campo de texto para observações do pedido.
     * É opcional e enviado para o pedido no banco.
     */
    private JTextArea fieldObs;

    /**
     * Label que exibe o subtotal calculado a partir do carrinho.
     */
    private JLabel labelSubtotal;

    /**
     * Label que exibe a taxa de entrega calculada a partir da área do usuário.
     * Só é aplicada quando a opção de entrega estiver selecionada.
     */
    private JLabel labelTaxa;

    /**
     * Label que exibe o total final (subtotal + taxaEntrega).
     */
    private JLabel labelTotal;

    /**
     * Subtotal calculado com base nos itens do carrinho.
     * Atualizado em recalcTotals().
     */
    private double subtotal = 0.0;

    /**
     * Taxa de entrega calculada com base na área do usuário.
     * Atualizada em recalcTotals().
     */
    private double taxaEntrega = 0.0;

    /**
     * Construtor da tela.
     * Valida pré-condições (usuário logado e carrinho não vazio).
     * Em seguida configura a janela, monta a UI e calcula os totais.
     */
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

    /**
     * Configura propriedades do JFrame:
     * - título
     * - operação de fechamento
     * - tamanho
     * - centralização
     * - cor de fundo
     */
    private void configureFrame() {
        setTitle("Finalizar compra");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    /**
     * Monta o painel principal do checkout.
     *
     * Funcionamento:
     * 1. Cria painel raiz com BorderLayout.
     * 2. Adiciona header (título).
     * 3. Adiciona centro com duas colunas:
     *    - painel de entrega/observações/endereço
     *    - painel de resumo (subtotal, taxa e total)
     * 4. Adiciona footer com botões de fechar e confirmar.
     *
     * @return painel principal
     */
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

    /**
     * Monta o cabeçalho da tela.
     *
     * @return componente do cabeçalho
     */
    private Component buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ViewTheme.BACKGROUND);
        header.add(ViewTheme.createTitleLabel("Checkout"), BorderLayout.WEST);
        return header;
    }

    /**
     * Monta o painel de forma de recebimento, observações e endereço.
     *
     * Funcionamento:
     * 1. Cria radio buttons de entrega e retirada.
     * 2. Agrupa os radios em ButtonGroup para seleção exclusiva.
     * 3. Define entrega como padrão.
     * 4. Adiciona listeners que chamam recalcTotals() ao mudar a opção.
     * 5. Cria campo de observações com scroll.
     * 6. Carrega texto do endereço do usuário via loadUserAddressText().
     *
     * @return componente do painel de entrega
     */
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

    /**
     * Monta o painel de resumo (subtotal, taxa de entrega e total).
     *
     * Funcionamento:
     * 1. Cria os labels (inicialmente com zero).
     * 2. Os valores reais são atualizados em recalcTotals().
     *
     * @return componente do painel de resumo
     */
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

    /**
     * Monta o rodapé com botões:
     * - Fechar: fecha a tela
     * - Confirmar pedido: chama onConfirm()
     *
     * @return componente do rodapé
     */
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

    /**
     * Recalcula subtotal, taxa de entrega e total e atualiza os labels.
     *
     * Funcionamento:
     * 1. Calcula subtotal chamando computeSubtotalFromCart().
     * 2. Se entrega estiver selecionada, calcula taxa chamando computeDeliveryFeeFromUserArea(),
     *    caso contrário taxa é 0.
     * 3. Atualiza labelSubtotal, labelTaxa e labelTotal.
     */
    private void recalcTotals() {
        subtotal = computeSubtotalFromCart();
        taxaEntrega = radioEntrega != null && radioEntrega.isSelected() ? computeDeliveryFeeFromUserArea() : 0.0;

        labelSubtotal.setText(String.format("Subtotal: R$ %.2f", subtotal));
        labelTaxa.setText(String.format("Taxa de entrega: R$ %.2f", taxaEntrega));
        labelTotal.setText(String.format("Total: R$ %.2f", subtotal + taxaEntrega));
    }

    /**
     * Calcula o subtotal do carrinho.
     *
     * Funcionamento:
     * 1. Itera sobre CartSession.getItems() (productId -> qty).
     * 2. Para cada item, busca o Product no banco.
     * 3. Calcula unitário com computeUnitPrice(Product).
     * 4. Soma unit * qty no acumulador.
     * 5. Em caso de erro SQL, exibe mensagem e retorna 0.0.
     *
     * @return subtotal calculado
     */
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

    /**
     * Calcula a taxa de entrega do usuário logado a partir da sua área/bairro.
     *
     * Funcionamento:
     * 1. Obtém o email do usuário logado.
     * 2. Busca a Person no banco via repoPerson.findByEmailPerson(email).
     * 3. Se não encontrar Person/Address/Area, retorna 0.0.
     * 4. Retorna Area.fee (ou 0.0 se estiver null).
     * 5. Em caso de erro SQL, exibe mensagem e retorna 0.0.
     *
     * @return taxa de entrega do usuário
     */
    private double computeDeliveryFeeFromUserArea() {
        try {
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

    /**
     * Carrega uma string resumida do endereço do usuário logado para exibição.
     *
     * Funcionamento:
     * 1. Obtém email do usuário logado.
     * 2. Busca Person por email no banco.
     * 3. Se não encontrar Person/Address, retorna texto padrão.
     * 4. Extrai área, rua, número e CEP, substituindo null por string vazia.
     * 5. Monta e retorna a string no formato:
     *    "rua, numero - area | CEP: cep"
     *
     * @return texto do endereço para exibição na tela
     */
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

    /**
     * Confirma o pedido e persiste no banco.
     *
     * Funcionamento:
     * 1. Se o carrinho estiver vazio, mostra mensagem e retorna.
     * 2. Determina o tipo de recebimento:
     *    - ENTREGA se radioEntrega estiver selecionado
     *    - RETIRADA caso contrário
     * 3. Lê observações (trim), podendo resultar em null.
     * 4. Calcula total como subtotal + taxaEntrega.
     * 5. Obtém idUser da sessão.
     * 6. Chama controllerCheckout.confirmOrder(...) para criar pedido e itens.
     * 7. Se der certo:
     *    - limpa o carrinho
     *    - mostra mensagem de sucesso com número do pedido
     *    - fecha a tela
     * 8. Em caso de erro:
     *    - exibe a mensagem conforme exceção (ValidationException, DataAccessException, AppException)
     */
    private void onConfirm() {
        if (CartSession.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Carrinho vazio.", "Checkout", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String delivery = radioEntrega.isSelected() ? "ENTREGA" : "RETIRADA";
        String obs = fieldObs.getText() != null ? fieldObs.getText().trim() : null;

        double total = subtotal + taxaEntrega;

        try {
            Integer idUser = Session.getLoggedUser().getIdUser();

            Integer idOrder = controllerCheckout.confirmOrder(
                    idUser,
                    total,
                    delivery,
                    obs,
                    CartSession.getItems()
            );

            CartSession.clear();
            JOptionPane.showMessageDialog(this, "Pedido confirmado com sucesso! (Pedido #" + idOrder + ")");
            dispose();

        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calcula o preço unitário final de um produto para fins de subtotal/total.
     *
     * Regra usada:
     * unitário = basePrice + size.price + flavor.level.price
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

    /**
     * Envolve um componente com estilo de card.
     * Aplica background, borda e padding.
     *
     * @param content componente interno
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
}