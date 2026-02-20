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

/**
 * Tela "Meus pedidos".
 * Exibe os pedidos do usuário logado e, ao selecionar um pedido, exibe os itens daquele pedido.
 * Os dados são carregados através do ControllerMyOrders, que consulta o repositório de pedidos do usuário.
 * Esta tela não permite alterar pedidos, apenas visualizar.
 */
public class ViewMyOrders extends JFrame {

    /**
     * Controller do caso de uso "Meus pedidos".
     * Responsável por listar pedidos do usuário e listar itens de um pedido.
     */
    private final ControllerMyOrders controller = new ControllerMyOrders();

    /**
     * Tabela que exibe a lista de pedidos.
     * Cada linha corresponde a um OrderSummary.
     */
    private JTable tableOrders;

    /**
     * Modelo da tabela de pedidos.
     * Colunas:
     * ID, Data/Hora, Tipo, Total.
     */
    private DefaultTableModel modelOrders;

    /**
     * Tabela que exibe os itens do pedido selecionado.
     * Cada linha corresponde a um OrderItemSummary.
     */
    private JTable tableItems;

    /**
     * Modelo da tabela de itens.
     * Colunas:
     * Produto, Qtd, Unitário, Total.
     */
    private DefaultTableModel modelItems;

    /**
     * Label que indica qual pedido está selecionado no painel de itens.
     * É atualizado em loadOrders() e loadItems(idOrder).
     */
    private JLabel labelOrderTitle;

    /**
     * Lista de pedidos carregados do banco.
     * O índice desta lista corresponde ao índice da linha na tableOrders.
     * É usada em onOrderSelected() para obter o OrderSummary selecionado.
     */
    private List<OrderSummary> orders = new ArrayList<>();

    /**
     * Formatador de data/hora usado para exibir o Timestamp do pedido na tabela.
     */
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * Construtor da tela.
     * Exige que o usuário esteja logado.
     * Monta a UI e carrega a lista de pedidos do usuário.
     */
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

    /**
     * Configura propriedades do JFrame:
     * - título
     * - operação de fechamento (fecha a janela)
     * - tamanho
     * - centralização
     * - cor de fundo do tema
     */
    private void configureFrame() {
        setTitle("Meus pedidos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 560);
        setLocationRelativeTo(null);
        getContentPane().setBackground(ViewTheme.BACKGROUND);
    }

    /**
     * Monta o painel principal da tela.
     *
     * Funcionamento:
     * 1. Cria painel raiz com BorderLayout.
     * 2. Adiciona cabeçalho (título e botão atualizar).
     * 3. Adiciona painel central em duas colunas:
     *    - esquerda: lista de pedidos
     *    - direita: itens do pedido selecionado
     * 4. Cada coluna é colocada dentro de um card (wrapCard) para padronizar estilo.
     *
     * @return painel principal
     */
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

    /**
     * Monta o cabeçalho da tela.
     *
     * Funcionamento:
     * 1. Exibe o título "Meus pedidos".
     * 2. Adiciona botão "Atualizar" que chama loadOrders().
     *
     * @return componente do cabeçalho
     */
    private Component buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ViewTheme.BACKGROUND);

        header.add(ViewTheme.createTitleLabel("Meus pedidos"), BorderLayout.WEST);

        JButton btnRefresh = ViewTheme.createSecondaryButton("Atualizar");
        btnRefresh.addActionListener(e -> loadOrders());
        header.add(btnRefresh, BorderLayout.EAST);

        return header;
    }

    /**
     * Monta o painel da lista de pedidos.
     *
     * Funcionamento:
     * 1. Cria o modelOrders com colunas e células não editáveis.
     * 2. Cria tableOrders e define seleção de linha única.
     * 3. Adiciona listener de seleção para chamar onOrderSelected().
     * 4. Coloca tableOrders dentro de JScrollPane e aplica borda do tema.
     *
     * @return componente do painel de pedidos
     */
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

    /**
     * Monta o painel de itens do pedido selecionado.
     *
     * Funcionamento:
     * 1. Cria labelOrderTitle com texto inicial pedindo seleção de pedido.
     * 2. Cria modelItems com colunas e células não editáveis.
     * 3. Cria tableItems e define seleção de linha única.
     * 4. Coloca tableItems dentro de JScrollPane e aplica borda do tema.
     *
     * @return componente do painel de itens
     */
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

    /**
     * Carrega os pedidos do usuário logado e preenche a tabela de pedidos.
     * Também limpa a tabela de itens e reseta o texto do título dos itens.
     *
     * Funcionamento:
     * 1. Limpa modelOrders e modelItems.
     * 2. Reseta labelOrderTitle.
     * 3. Reseta a lista orders.
     * 4. Obtém idUser da Session.
     * 5. Chama controller.listOrdersByUser(idUser).
     * 6. Para cada OrderSummary:
     *    - formata data/hora
     *    - adiciona linha na tabela com id, data, tipo e total
     *
     * Tratamento de erro:
     * - ValidationException: exibe aviso
     * - DataAccessException: exibe erro
     * - AppException: exibe erro
     */
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

    /**
     * Manipulador de seleção de pedido na tabela.
     *
     * Funcionamento:
     * 1. Obtém a linha selecionada.
     * 2. Se não houver seleção válida, retorna.
     * 3. Obtém o OrderSummary correspondente na lista orders.
     * 4. Chama loadItems(idPedido) para carregar os itens.
     */
    private void onOrderSelected() {
        int row = tableOrders.getSelectedRow();
        if (row < 0 || row >= orders.size()) return;

        OrderSummary selected = orders.get(row);
        loadItems(selected.getId());
    }

    /**
     * Carrega os itens de um pedido e preenche a tabela de itens.
     *
     * Funcionamento:
     * 1. Limpa modelItems.
     * 2. Chama controller.listItems(idOrder).
     * 3. Atualiza labelOrderTitle com o número do pedido.
     * 4. Para cada item:
     *    - obtém preço unitário e quantidade tratando null como 0
     *    - adiciona linha com produto, qtd, unitário e total do item
     *
     * Tratamento de erro:
     * - ValidationException: exibe aviso
     * - DataAccessException: exibe erro
     * - AppException: exibe erro
     *
     * @param idOrder id do pedido
     */
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

    /**
     * Envolve um componente em um painel com estilo de card.
     * Aplica background, borda e padding.
     *
     * @param content componente interno
     * @return painel estilizado
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