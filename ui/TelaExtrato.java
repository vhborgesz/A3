package ui;

import dao.TransacoesDAO;
import dao.UsuarioDAO;
import model.Transacoes;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TelaExtrato extends JFrame {


    private static final Color C_BG      = new Color(12,  15,  30);
    private static final Color C_CARD    = new Color(22,  28,  50);
    private static final Color C_CARD2   = new Color(28,  36,  62);
    private static final Color C_ACCENT  = new Color(99, 120, 255);
    private static final Color C_ACCENT2 = new Color(60, 220, 170);
    private static final Color C_PIX     = new Color(0,  200, 150);
    private static final Color C_RED     = new Color(255, 90,  90);
    private static final Color C_TEXT    = new Color(235, 238, 255);
    private static final Color C_MUTED   = new Color(130, 138, 175);
    private static final Color C_OVERLAY = new Color(0, 0, 0, 180);

    private String nomeUsuario;
    private String emailUsuario;
    private Usuario usuarioLogado;

    private JLabel valorSaldo;
    private JPanel listaTransacoes;
    private JScrollPane scrollLista;

    private final UsuarioDAO    usuarioDAO    = new UsuarioDAO();
    private final TransacoesDAO transacoesDAO = new TransacoesDAO();

    private final NumberFormat moedaFmt =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public TelaExtrato(String nome, String email) {
        this.nomeUsuario  = nome;
        this.emailUsuario = email;

        try {
            this.usuarioLogado = usuarioDAO.buscarPorEmail(email);
        } catch (Exception ignored) {
            this.usuarioLogado = null;
        }

        construirTela();
    }

    public TelaExtrato() {
        this("Administrador", "admin@gmail.com");
    }

    private void construirTela() {
        setTitle("FlowBank — Extrato");
        setSize(440, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(C_BG);

        JLayeredPane layered = new JLayeredPane();
        layered.setBackground(C_BG);
        layered.setOpaque(true);
        setContentPane(layered);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(C_BG);
        main.setOpaque(true);
        main.setBounds(0, 0, 440, 820);

        main.add(criarHeader(), BorderLayout.NORTH);

        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setBackground(C_BG);
        corpo.setBorder(new EmptyBorder(24, 20, 24, 20));

        corpo.add(criarAcoesRapidas(layered));
        corpo.add(Box.createVerticalStrut(28));
        corpo.add(criarSecaoExtrato());
        corpo.add(Box.createVerticalStrut(28));
        corpo.add(criarBotaoAtualizar());

        JScrollPane scroll = new JScrollPane(corpo);
        scroll.setBorder(null);
        scroll.setBackground(C_BG);
        scroll.getViewport().setBackground(C_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        main.add(scroll, BorderLayout.CENTER);
        layered.add(main, JLayeredPane.DEFAULT_LAYER);
    }

    private JPanel criarHeader() {
        JPanel header = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(60, 80, 200),
                        getWidth(), getHeight(), new Color(100, 50, 200)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillOval(-40, -40, 200, 200);
                g2.fillOval(getWidth() - 80, getHeight() - 60, 180, 180);
            }
        };
        header.setPreferredSize(new Dimension(440, 240));
        header.setOpaque(true);

        JLabel lblBanco = new JLabel("FlowBank");
        lblBanco.setFont(new Font("Georgia", Font.BOLD, 22));
        lblBanco.setForeground(Color.WHITE);
        lblBanco.setBounds(24, 28, 200, 30);
        header.add(lblBanco);

        JLabel avatar = new JLabel(inicialNome(), SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Georgia", Font.BOLD, 18));
        avatar.setForeground(Color.WHITE);
        avatar.setBounds(380, 20, 44, 44);
        header.add(avatar);

        JLabel saudacao = new JLabel("Olá, " + primeiroNome() + " 👋");
        saudacao.setFont(new Font("SansSerif", Font.PLAIN, 15));
        saudacao.setForeground(new Color(200, 215, 255));
        saudacao.setBounds(24, 68, 280, 22);
        header.add(saudacao);

        JLabel lblRotulo = new JLabel("Saldo disponível");
        lblRotulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblRotulo.setForeground(new Color(180, 195, 255));
        lblRotulo.setBounds(24, 110, 200, 18);
        header.add(lblRotulo);

        valorSaldo = new JLabel(getSaldoFormatado());
        valorSaldo.setFont(new Font("Georgia", Font.BOLD, 34));
        valorSaldo.setForeground(Color.WHITE);
        valorSaldo.setBounds(24, 130, 380, 48);
        header.add(valorSaldo);

        JPanel resumo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        resumo.setOpaque(false);
        resumo.setBounds(24, 196, 400, 26);
        resumo.add(criarResumoItem("▲ Entradas", calcularEntradas(), C_ACCENT2));
        resumo.add(criarResumoItem("▼ Saídas",   calcularSaidas(),   C_RED));
        header.add(resumo);

        return header;
    }

    private JPanel criarResumoItem(String rotulo, String valor, Color cor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel r = new JLabel(rotulo);
        r.setFont(new Font("SansSerif", Font.PLAIN, 11));
        r.setForeground(new Color(180, 195, 255));
        JLabel v = new JLabel(valor);
        v.setFont(new Font("SansSerif", Font.BOLD, 11));
        v.setForeground(cor);
        p.add(r); p.add(v);
        return p;
    }

    private JPanel criarAcoesRapidas(JLayeredPane layered) {
        JPanel secao = new JPanel();
        secao.setLayout(new BoxLayout(secao, BoxLayout.Y_AXIS));
        secao.setBackground(C_BG);
        secao.setAlignmentX(Component.LEFT_ALIGNMENT);
        secao.setMaximumSize(new Dimension(440, 200));

        JLabel titulo = new JLabel("Ações Rápidas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        titulo.setForeground(C_MUTED);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        secao.add(titulo);
        secao.add(Box.createVerticalStrut(14));

        JPanel botoes = new JPanel(new GridLayout(1, 3, 12, 0));
        botoes.setBackground(C_BG);
        botoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        botoes.setMaximumSize(new Dimension(400, 90));

        JButton btnPix    = criarBotaoAcao("Pix",        "➤", C_PIX);
        JButton btnTransf = criarBotaoAcao("Transferir", "⇄", C_ACCENT);
        JButton btnPagar  = criarBotaoAcao("Pagar",      "✓", new Color(255, 180, 50));

        btnPix.addActionListener(e -> abrirModalOperacao(layered,
                "Enviar Pix", "➤", C_PIX, "Chave Pix (CPF, e-mail, celular ou aleatória)", "Pix Enviado", "Para: "));

        btnTransf.addActionListener(e -> abrirModalOperacao(layered,
                "Transferência", "⇄", C_ACCENT, "Conta / Agência destino", "Transferência", "Para: "));

        btnPagar.addActionListener(e -> abrirModalOperacao(layered,
                "Pagar", "✓", new Color(255, 180, 50), "Código de barras ou descrição", "Pagamento", "Ref: "));

        botoes.add(btnPix);
        botoes.add(btnTransf);
        botoes.add(btnPagar);

        secao.add(botoes);
        return secao;
    }

    private JButton criarBotaoAcao(String texto, String icone, Color cor) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? C_CARD2 : C_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(cor);
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(icone, (getWidth() - fm.stringWidth(icone)) / 2, 42);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.setColor(C_TEXT);
                FontMetrics fm2 = g2.getFontMetrics();
                g2.drawString(texto, (getWidth() - fm2.stringWidth(texto)) / 2, 66);
            }
        };
        btn.setPreferredSize(new Dimension(120, 82));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel criarSecaoExtrato() {
        JPanel secao = new JPanel();
        secao.setLayout(new BoxLayout(secao, BoxLayout.Y_AXIS));
        secao.setBackground(C_BG);
        secao.setAlignmentX(Component.LEFT_ALIGNMENT);
        secao.setMaximumSize(new Dimension(440, 9999));

        JPanel cabec = new JPanel(new BorderLayout());
        cabec.setBackground(C_BG);
        cabec.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabec.setMaximumSize(new Dimension(400, 30));

        JLabel lbl = new JLabel("Lançamentos");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(C_MUTED);
        cabec.add(lbl, BorderLayout.WEST);

        secao.add(cabec);
        secao.add(Box.createVerticalStrut(14));


        listaTransacoes = new JPanel();
        listaTransacoes.setLayout(new BoxLayout(listaTransacoes, BoxLayout.Y_AXIS));
        listaTransacoes.setBackground(C_BG);
        listaTransacoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        carregarTransacoes();

        secao.add(listaTransacoes);
        return secao;
    }

    /** Carrega (ou recarrega) as transações do banco no painel. */
    private void carregarTransacoes() {
        listaTransacoes.removeAll();

        try {
            if (usuarioLogado != null) {
                List<Transacoes> lista = transacoesDAO.consultarExtrato(usuarioLogado.getId());

                if (lista.isEmpty()) {
                    JLabel vazio = new JLabel("Nenhuma transação encontrada.", SwingConstants.CENTER);
                    vazio.setFont(new Font("SansSerif", Font.ITALIC, 13));
                    vazio.setForeground(C_MUTED);
                    vazio.setAlignmentX(Component.LEFT_ALIGNMENT);
                    listaTransacoes.add(vazio);
                } else {
                    for (Transacoes t : lista) {
                        boolean entrada = t.getValor() >= 0;
                        String valorFmt = (entrada ? "+ " : "- ") + moedaFmt.format(Math.abs(t.getValor()));
                        Color cor = entrada ? C_ACCENT2 : C_RED;
                        String tag = t.getTransacao() != null
                                ? t.getTransacao().substring(0, Math.min(3, t.getTransacao().length())).toUpperCase()
                                : "TX";
                        listaTransacoes.add(criarItemExtrato(t.getTransacao(), t.getDescricao(), valorFmt, cor, tag));
                        listaTransacoes.add(Box.createVerticalStrut(10));
                    }
                }

            } else {

                Object[][] demo = {
                    {"Pix Recebido",  "João Silva",   "+ R$ 850,00",   C_ACCENT2, "PIX"},
                    {"Salário",       "Empresa XYZ",  "+ R$ 5.200,00", C_ACCENT2, "SAL"},
                    {"Pagamento",     "Netflix",      "- R$ 39,90",    C_RED,     "PAG"},
                    {"Transferência", "Maria Souza",  "- R$ 250,00",   C_RED,     "TRF"},
                };
                for (Object[] t : demo) {
                    listaTransacoes.add(criarItemExtrato(
                            (String) t[0], (String) t[1], (String) t[2], (Color) t[3], (String) t[4]));
                    listaTransacoes.add(Box.createVerticalStrut(10));
                }
            }
        } catch (Exception ex) {
            JLabel erro = new JLabel("Erro ao carregar transações.", SwingConstants.CENTER);
            erro.setForeground(C_RED);
            listaTransacoes.add(erro);
        }

        listaTransacoes.revalidate();
        listaTransacoes.repaint();
    }

    private JPanel criarItemExtrato(String tipo, String desc, String valor, Color corValor, String tag) {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
        };
        card.setOpaque(false);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(400, 70));
        card.setPreferredSize(new Dimension(400, 70));

        JLabel badge = new JLabel(tag, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(corValor.equals(C_ACCENT2)
                        ? new Color(0, 200, 150, 35) : new Color(255, 90, 90, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("SansSerif", Font.BOLD, 10));
        badge.setForeground(corValor);
        badge.setBounds(14, 18, 38, 22);
        card.add(badge);

        JLabel lblTipo = new JLabel(tipo != null ? tipo : "");
        lblTipo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTipo.setForeground(C_TEXT);
        lblTipo.setBounds(64, 14, 210, 20);
        card.add(lblTipo);

        JLabel lblDesc = new JLabel(desc != null ? desc : "");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDesc.setForeground(C_MUTED);
        lblDesc.setBounds(64, 36, 210, 18);
        card.add(lblDesc);

        JLabel lblValor = new JLabel(valor, SwingConstants.RIGHT);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblValor.setForeground(corValor);
        lblValor.setBounds(220, 22, 162, 24);
        card.add(lblValor);

        return card;
    }


    private JPanel criarBotaoAtualizar() {
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrap.setBackground(C_BG);
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.setMaximumSize(new Dimension(400, 60));

        JButton btn = new JButton("Atualizar Dados Cadastrados") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(C_ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(C_ACCENT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(340, 46));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));


        btn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new TelaRegistro(emailUsuario, true);
                dispose();
            });
        });

        wrap.add(btn);
        return wrap;
    }


    private void abrirModalOperacao(JLayeredPane layered, String titOp, String icone,
                                    Color corOp, String labelChave, String tipoTx, String prefixDesc) {
        JPanel overlay = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(C_OVERLAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.setBounds(0, 0, 440, 820);

        JPanel modal = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(6, 6, getWidth() - 6, getHeight() - 6, 20, 20);
                g2.setColor(new Color(20, 26, 50));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
            }
        };
        modal.setOpaque(false);
        int mw = 370, mh = 360;
        modal.setBounds((440 - mw) / 2, (820 - mh) / 2 - 40, mw, mh);


        JLabel icoLabel = new JLabel(icone, SwingConstants.CENTER);
        icoLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        icoLabel.setForeground(corOp);
        icoLabel.setBounds(0, 22, mw - 6, 36);
        modal.add(icoLabel);


        JLabel titulo = new JLabel(titOp, SwingConstants.CENTER);
        titulo.setFont(new Font("Georgia", Font.BOLD, 20));
        titulo.setForeground(C_TEXT);
        titulo.setBounds(0, 62, mw - 6, 28);
        modal.add(titulo);


        JLabel lblChave = new JLabel(labelChave);
        lblChave.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblChave.setForeground(C_MUTED);
        lblChave.setBounds(24, 104, 316, 16);
        modal.add(lblChave);

        JTextField campoChave = criarCampoModal();
        campoChave.setBounds(24, 122, 316, 40);
        modal.add(campoChave);


        JLabel lblValorLabel = new JLabel("Valor (R$)");
        lblValorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblValorLabel.setForeground(C_MUTED);
        lblValorLabel.setBounds(24, 176, 200, 16);
        modal.add(lblValorLabel);

        JTextField campoValor = criarCampoModal();
        campoValor.setBounds(24, 194, 316, 40);
        modal.add(campoValor);


        JLabel lblSaldoDisp = new JLabel("Saldo disponível: " + getSaldoFormatado(), SwingConstants.CENTER);
        lblSaldoDisp.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSaldoDisp.setForeground(C_MUTED);
        lblSaldoDisp.setBounds(0, 242, mw - 6, 16);
        modal.add(lblSaldoDisp);


        JButton btnConfirmar = criarBotaoModal("Confirmar", corOp);
        btnConfirmar.setBounds(24, 268, 148, 44);
        modal.add(btnConfirmar);


        JButton btnCancelar = criarBotaoModal("Cancelar", new Color(60, 65, 100));
        btnCancelar.setBounds(186, 268, 148, 44);
        modal.add(btnCancelar);

        overlay.add(modal);
        layered.add(overlay, JLayeredPane.POPUP_LAYER);
        layered.repaint();

        btnCancelar.addActionListener(e -> {
            layered.remove(overlay);
            layered.repaint();
        });

        btnConfirmar.addActionListener(e -> {
            String chave    = campoChave.getText().trim();
            String valorStr = campoValor.getText().trim().replace(",", ".");

            if (chave.isEmpty() || valorStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double valor;
            try {
                valor = Double.parseDouble(valorStr);
                if (valor <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }


            double saldoAtual = getSaldoAtual();
            if (saldoAtual >= 0 && valor > saldoAtual) {
                JOptionPane.showMessageDialog(this,
                        "Saldo insuficiente!\nSaldo atual: " + moedaFmt.format(saldoAtual),
                        "Saldo Insuficiente", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (usuarioLogado != null) {

                    usuarioDAO.descontarSaldo(usuarioLogado.getId(), valor);
                    usuarioLogado.setSaldo(saldoAtual - valor);


                    Transacoes tx = new Transacoes();
                    tx.setUsuarioId(usuarioLogado.getId());
                    tx.setTransacao(tipoTx);
                    tx.setDescricao(prefixDesc + chave);
                    tx.setValor(-valor);
                    tx.setData(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    tx.setHora(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    transacoesDAO.inserirTransacao(tx);
                }
            } catch (Exception ex) {

            }


            layered.remove(overlay);
            layered.repaint();


            atualizarSaldoELista();

            JOptionPane.showMessageDialog(this,
                    String.format("%s de %s realizado para:\n%s", tipoTx, moedaFmt.format(valor), chave),
                    tipoTx + " ✓", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /** Recarrega o saldo no header e as transações na lista. */
    private void atualizarSaldoELista() {
        if (usuarioLogado != null) {
            try {
                usuarioLogado = usuarioDAO.buscarPorId(usuarioLogado.getId());
            } catch (Exception ignored) {}
        }
        valorSaldo.setText(getSaldoFormatado());
        carregarTransacoes();
    }


    private double getSaldoAtual() {
        if (usuarioLogado != null) return usuarioLogado.getSaldo();
        return 12450.90;
    }

    private String getSaldoFormatado() {
        return moedaFmt.format(getSaldoAtual());
    }

    private String calcularEntradas() {
        if (usuarioLogado == null) return "R$ 6.050,00";
        try {
            double total = transacoesDAO.consultarExtrato(usuarioLogado.getId())
                    .stream().filter(t -> t.getValor() > 0).mapToDouble(Transacoes::getValor).sum();
            return moedaFmt.format(total);
        } catch (Exception e) { return "R$ 0,00"; }
    }

    private String calcularSaidas() {
        if (usuarioLogado == null) return "R$ 289,90";
        try {
            double total = transacoesDAO.consultarExtrato(usuarioLogado.getId())
                    .stream().filter(t -> t.getValor() < 0).mapToDouble(t -> Math.abs(t.getValor())).sum();
            return moedaFmt.format(total);
        } catch (Exception e) { return "R$ 0,00"; }
    }

    private JTextField criarCampoModal() {
        JTextField c = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(35, 44, 80));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        c.setOpaque(false);
        c.setBackground(new Color(35, 44, 80));
        c.setForeground(C_TEXT);
        c.setCaretColor(C_ACCENT);
        c.setFont(new Font("SansSerif", Font.PLAIN, 14));
        c.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(60, 75, 130), 1, 10),
                new EmptyBorder(8, 12, 8, 12)));
        return c;
    }

    private JButton criarBotaoModal(String texto, Color cor) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? cor.darker() : (getModel().isRollover() ? cor.brighter() : cor));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String inicialNome() {
        return nomeUsuario != null && !nomeUsuario.isEmpty()
                ? String.valueOf(nomeUsuario.charAt(0)).toUpperCase() : "U";
    }

    private String primeiroNome() {
        if (nomeUsuario == null) return "Usuário";
        return nomeUsuario.trim().split("\\s+")[0];
    }

    static class RoundedLineBorder extends AbstractBorder {
        private final Color color; private final int thickness, radius;
        RoundedLineBorder(Color c, int t, int r) { color = c; thickness = t; radius = r; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(thickness, thickness, thickness, thickness); }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new TelaExtrato().setVisible(true));
    }
}
