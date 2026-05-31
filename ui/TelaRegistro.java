package ui;
import dao.UsuarioDAO;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelaRegistro extends JFrame {


    private static final Color C_BG     = new Color(12, 15, 30);
    private static final Color C_CARD   = new Color(22, 28, 50);
    private static final Color C_ACCENT = new Color(99, 120, 255);
    private static final Color C_RED    = new Color(255, 90, 90);
    private static final Color C_TEXT   = new Color(235, 238, 255);
    private static final Color C_MUTED  = new Color(130, 138, 175);

    private final boolean modoAtualizacao;
    private Usuario usuarioAtual;


    private JTextField campoId;
    private JTextField campoNome;
    private JTextField campoEmail;
    private JTextField campoCelular;
    private JTextField campoNascimento;
    private JPasswordField campoSenha;
    private JPasswordField campoConfirmaSenha;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();



    /** Modo cadastro (sem usuário pré-carregado). */
    public TelaRegistro() {
        this(null, false);
    }

    /**
     * @param emailOuUsuario  e-mail do usuário logado (modo atualização) ou null (cadastro).
     * @param modoAtualizacao true → atualização; false → cadastro.
     */
    public TelaRegistro(String emailOuUsuario, boolean modoAtualizacao) {
        this.modoAtualizacao = modoAtualizacao;

        if (modoAtualizacao && emailOuUsuario != null) {
            this.usuarioAtual = usuarioDAO.buscarPorEmail(emailOuUsuario);
        }

        construirTela();
        if (modoAtualizacao && usuarioAtual != null) {
            preencherCampos(usuarioAtual);
        }

        setVisible(true);
    }


    private void construirTela() {
        setTitle(modoAtualizacao ? "Atualizar Dados — FlowBank" : "Cadastro — FlowBank");
        setSize(480, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);


        JPanel fundo = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(C_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        fundo.setLayout(new BorderLayout());
        setContentPane(fundo);


        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(60, 80, 200),
                        getWidth(), getHeight(), new Color(100, 50, 200));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(480, 80));
        header.setLayout(new FlowLayout(FlowLayout.LEFT, 24, 24));

        JLabel titulo = new JLabel(modoAtualizacao ? "✎  Atualizar Dados" : "✚  Novo Cadastro");
        titulo.setFont(new Font("Georgia", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        header.add(titulo);
        fundo.add(header, BorderLayout.NORTH);


        JPanel form = new JPanel();
        form.setBackground(C_BG);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(24, 40, 24, 40));


        campoId = criarCampo();
        campoId.setEditable(false);
        campoId.setForeground(C_MUTED);
        campoId.setText(modoAtualizacao && usuarioAtual != null
                ? String.valueOf(usuarioAtual.getId()) : "(gerado automaticamente)");
        form.add(criarLinhaFormulario("ID (protegido)", campoId));
        form.add(Box.createVerticalStrut(14));

        campoNome         = criarCampo();
        campoEmail        = criarCampo();
        campoCelular      = criarCampo();
        campoNascimento   = criarCampo();
        campoSenha        = new JPasswordField();
        estilizarCampo(campoSenha);
        campoConfirmaSenha = new JPasswordField();
        estilizarCampo(campoConfirmaSenha);

        form.add(criarLinhaFormulario("Nome *", campoNome));              form.add(Box.createVerticalStrut(14));
        form.add(criarLinhaFormulario("E-mail *", campoEmail));           form.add(Box.createVerticalStrut(14));
        form.add(criarLinhaFormulario("Celular *", campoCelular));        form.add(Box.createVerticalStrut(14));
        form.add(criarLinhaFormulario("Data de Nascimento (DD/MM/AAAA)", campoNascimento)); form.add(Box.createVerticalStrut(14));
        form.add(criarLinhaFormulario("Senha *", campoSenha));            form.add(Box.createVerticalStrut(14));
        form.add(criarLinhaFormulario("Confirmação de Senha *", campoConfirmaSenha)); form.add(Box.createVerticalStrut(28));


        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        painelBotoes.setBackground(C_BG);
        painelBotoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        String labelSalvar = modoAtualizacao ? "Atualizar" : "Cadastrar";
        JButton btnSalvar  = criarBotao(labelSalvar, C_ACCENT);
        JButton btnExcluir = criarBotao("Excluir", C_RED);
        JButton btnCancelar = criarBotao("Cancelar", new Color(60, 65, 100));


        btnExcluir.setVisible(modoAtualizacao);

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnCancelar);
        form.add(painelBotoes);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.setBackground(C_BG);
        scroll.getViewport().setBackground(C_BG);
        fundo.add(scroll, BorderLayout.CENTER);


        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnCancelar.addActionListener(e -> cancelar());
    }



    private void salvar() {
        String nome    = campoNome.getText().trim();
        String email   = campoEmail.getText().trim();
        String celular = campoCelular.getText().trim();
        String nasc    = campoNascimento.getText().trim();
        String senha   = new String(campoSenha.getPassword());
        String confirma = new String(campoConfirmaSenha.getPassword());


        if (nome.isEmpty() || email.isEmpty() || celular.isEmpty()
                || senha.isEmpty() || confirma.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha todos os campos obrigatórios (*) !",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "E-mail inválido!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!senha.equals(confirma)) {
            JOptionPane.showMessageDialog(this,
                    "Senha e confirmação de senha não conferem!",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (modoAtualizacao && usuarioAtual != null) {
                usuarioAtual.setNome(nome);
                usuarioAtual.setEmail(email);
                usuarioAtual.setCelular(celular);
                usuarioAtual.setDataDeNascimento(nasc);
                usuarioAtual.setSenha(senha);
                usuarioDAO.atualizarUsuario(usuarioAtual);
                JOptionPane.showMessageDialog(this, "Dados atualizados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                SwingUtilities.invokeLater(() -> {
                    new TelaExtrato(nome, email).setVisible(true);
                    dispose();
                });
            } else {

                if (usuarioDAO.buscarPorEmail(email) != null) {
                    JOptionPane.showMessageDialog(this, "E-mail já cadastrado!", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Usuario novo = new Usuario();
                novo.setNome(nome);
                novo.setEmail(email);
                novo.setCelular(celular);
                novo.setDataDeNascimento(nasc);
                novo.setSenha(senha);
                novo.setSaldo(0);
                usuarioDAO.inserirUsuario(novo);
                JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso!\nFaça login para entrar.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> {
                    new TelaLogin();
                    dispose();
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (usuarioAtual == null) return;

        int resp = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir sua conta?\nEssa ação não pode ser desfeita.",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (resp == JOptionPane.YES_OPTION) {
            try {
                usuarioDAO.excluirUsuario(usuarioAtual.getId());
                JOptionPane.showMessageDialog(this, "Conta excluída com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> {
                    new TelaLogin();
                    dispose();
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelar() {
        if (modoAtualizacao && usuarioAtual != null) {

            SwingUtilities.invokeLater(() -> {
                new TelaExtrato(usuarioAtual.getNome(), usuarioAtual.getEmail()).setVisible(true);
                dispose();
            });
        } else {

            SwingUtilities.invokeLater(() -> {
                new TelaLogin();
                dispose();
            });
        }
    }


    private void preencherCampos(Usuario u) {
        campoId.setText(String.valueOf(u.getId()));
        campoNome.setText(u.getNome());
        campoEmail.setText(u.getEmail());
        campoCelular.setText(u.getCelular() != null ? u.getCelular() : "");
        campoNascimento.setText(u.getDataDeNascimento() != null ? u.getDataDeNascimento() : "");

    }


    private JPanel criarLinhaFormulario(String rotulo, JComponent campo) {
        JPanel linha = new JPanel();
        linha.setLayout(new BoxLayout(linha, BoxLayout.Y_AXIS));
        linha.setBackground(C_BG);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(400, 70));

        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(C_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setMaximumSize(new Dimension(400, 38));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        linha.add(lbl);
        linha.add(Box.createVerticalStrut(4));
        linha.add(campo);
        return linha;
    }

    private JTextField criarCampo() {
        JTextField c = new JTextField();
        estilizarCampo(c);
        return c;
    }

    private void estilizarCampo(JComponent c) {
        if (c instanceof JTextField f) {
            f.setBackground(C_CARD);
            f.setForeground(C_TEXT);
            f.setCaretColor(C_ACCENT);
            f.setFont(new Font("SansSerif", Font.PLAIN, 14));
            f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 75, 130), 1, true),
                    new EmptyBorder(6, 10, 6, 10)));
        }
        if (c instanceof JPasswordField f) {
            f.setBackground(C_CARD);
            f.setForeground(C_TEXT);
            f.setCaretColor(C_ACCENT);
            f.setFont(new Font("SansSerif", Font.PLAIN, 14));
            f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 75, 130), 1, true),
                    new EmptyBorder(6, 10, 6, 10)));
        }
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? cor.darker() : (getModel().isRollover() ? cor.brighter() : cor));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 40));
        return btn;
    }
}
