package view;

import controller.ControllerCart;
import exceptions.ValidationException;
import model.entities.Product;

import javax.swing.*;
import java.awt.*;

/**
 * Tela de detalhes do produto.
 * Exibe informações do produto selecionado (nome, sabor, nível, tamanho) e permite:
 * - escolher uma quantidade
 * - ver o preço unitário calculado
 * - ver o total (unitário * quantidade)
 * - adicionar o produto ao carrinho
 * - navegar para a tela do carrinho
 *
 * Esta tela recebe um Product já carregado e não consulta o banco diretamente.
 */
public class ViewProductDetails extends JFrame {

    /**
     * Produto que será exibido na tela.
     * É fornecido no construtor.
     * Se for null, a tela mostra erro e é fechada.
     */
    private final Product product;

    /**
     * Controller responsável por adicionar produtos ao carrinho (CartSession).
     * Ele executa validações (por exemplo, quantidade válida) e delega a atualização do carrinho.
     */
    private final ControllerCart controllerCart = new ControllerCart();

    /**
     * Spinner de quantidade.
     * Controla quantas unidades do produto serão adicionadas ao carrinho.
     * Também dispara recálculo do total ao alterar o valor.
     */
    private JSpinner spinnerQty;

    /**
     * Label que exibe o preço unitário calculado do produto.
     * É atualizado em updateTotals().
     */
    private JLabel labelUnitPrice;

    /**
     * Label que exibe o total (unitário * quantidade).
     * É atualizado em updateTotals().
     */
    private JLabel labelTotal;

    /**
     * Construtor da tela de detalhes do produto.
     * Recebe o produto e monta a interface.
     *
     * Funcionamento:
     * 1. Verifica se product é null.
     * 2. Se for null:
     *    - mostra mensagem de erro
     *    - fecha a janela
     *    - define this.product como null e retorna
     * 3. Se não for null:
     *    - salva o produto
     *    - configura a janela
     *    - monta painel principal
     *    - calcula preços iniciais chamando updateTotals()
     *
     * @param product produto que será exibido
     */
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

    /**
     * Configura propriedades do JFrame:
     * - título
     * - operação de fechamento
     * - tamanho
     * - centralização
     * - cor de fundo do tema
     * - desabilita redimensionamento
     */
    private void configureFrame() {
        setTitle("Produto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 420);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
        setResizable(false);
    }

    /**
     * Monta o painel principal com as informações do produto e ações.
     *
     * Funcionamento:
     * 1. Cria painel com padding e layout vertical.
     * 2. Exibe nome do produto.
     * 3. Extrai strings de sabor, nível e tamanho tratando null.
     * 4. Cria labels para exibição dessas informações.
     * 5. Cria labelUnitPrice.
     * 6. Cria spinnerQty com limites (1 a 999) e listener para updateTotals().
     * 7. Cria labelTotal.
     * 8. Cria botão "Adicionar ao carrinho" que chama onAdd().
     * 9. Cria botão "Ir para o carrinho" que abre ViewCart.
     *
     * @return painel principal
     */
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

    /**
     * Adiciona o produto ao carrinho com a quantidade selecionada.
     *
     * Funcionamento:
     * 1. Obtém o valor atual do spinnerQty.
     * 2. Chama controllerCart.addProduct(product, qty).
     * 3. Se funcionar, exibe mensagem de confirmação.
     * 4. Se ocorrer ValidationException, exibe mensagem de atenção ao usuário.
     */
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

    /**
     * Recalcula e atualiza os labels de preço unitário e total.
     *
     * Funcionamento:
     * 1. Calcula preço unitário chamando computeUnitPrice(product).
     * 2. Obtém quantidade atual do spinner.
     * 3. Calcula total como unit * qty.
     * 4. Atualiza labelUnitPrice e labelTotal.
     */
    private void updateTotals() {
        double unit = computeUnitPrice(product);
        int qty = spinnerQty != null ? (Integer) spinnerQty.getValue() : 1;
        double total = unit * qty;

        labelUnitPrice.setText(String.format("Preço unitário: R$ %.2f", unit));
        labelTotal.setText(String.format("Total: R$ %.2f", total));
    }

    /**
     * Calcula o preço unitário final do produto.
     *
     * Regra usada:
     * unitário = basePrice + size.price + flavor.level.price
     *
     * Funcionamento:
     * 1. Lê basePrice (se null assume 0).
     * 2. Lê price do Size (se não existir assume 0).
     * 3. Lê price do FlavorLevel (se não existir assume 0).
     * 4. Retorna a soma.
     *
     * @param p produto para cálculo
     * @return valor unitário final
     */
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