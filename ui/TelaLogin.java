package ui;

import dao.UsuarioDAO;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;

public class TelaLogin extends JFrame {

    private JTextField emailbox;
    private JPasswordField senhabox;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();


    class RoundedBorder extends AbstractBorder {
        private final int radius;
        RoundedBorder(int radius) { this.radius = radius; }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
        }

        @Override public Insets getBorderInsets(Component c) { return new Insets(5, 10, 5, 10); }
        @Override public Insets getBorderInsets(Component c, Insets i) {
            i.left = 10; i.right = 10; i.top = 5; i.bottom = 5; return i;
        }
    }


    public TelaLogin() {
        setTitle("FlowBank — Login");
        setSize(450, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);


        JPanel fundo = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(10, 20, 45));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        fundo.setLayout(null);
        setContentPane(fundo);

        JPanel painel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 16, 16);
                g2.setColor(new Color(30, 60, 120, 220));
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 16, 16);
            }
        };
        painel.setSize(320, 400);
        painel.setLayout(null);
        painel.setOpaque(false);

        fundo.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                painel.setLocation(
                        fundo.getWidth()  / 2 - painel.getWidth()  / 2,
                        fundo.getHeight() / 2 - painel.getHeight() / 2);
            }
        });
        fundo.add(painel);

        int larg = 220, alt = 38;
        int xC = (320 - 8 - larg) / 2;
        int y = 30;


        JLabel logo = new JLabel("FlowBank", SwingConstants.CENTER);
        logo.setFont(new Font("Georgia", Font.BOLD, 26));
        logo.setBounds(0, y, 312, 36);
        logo.setForeground(Color.WHITE);
        painel.add(logo);

        y += 44;

        JLabel sub = new JLabel("Acesse sua conta", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setBounds(0, y, 312, 20);
        sub.setForeground(new Color(180, 200, 255));
        painel.add(sub);

        y += 44;


        emailbox = new JTextField();
        emailbox.setBounds(xC, y, larg, alt);
        emailbox.setBackground(Color.WHITE);
        emailbox.setForeground(Color.BLACK);
        emailbox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        emailbox.setToolTipText("Digite seu e-mail");
        emailbox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        painel.add(emailbox);

        y += 54;


        senhabox = new JPasswordField();
        senhabox.setBounds(xC, y, larg, alt);
        senhabox.setBackground(Color.WHITE);
        senhabox.setForeground(Color.BLACK);
        senhabox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        senhabox.setToolTipText("Digite sua senha");
        senhabox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        painel.add(senhabox);

        y += 60;


        JButton btnAutenticar = criarBotao("Autenticar", new Color(59, 130, 246));
        btnAutenticar.setBounds(xC, y, larg, 42);
        painel.add(btnAutenticar);

        y += 56;


        JButton btnCadastrar = criarBotao("Cadastrar", new Color(40, 160, 100));
        btnCadastrar.setBounds(xC, y, larg, 42);
        painel.add(btnCadastrar);

        y += 60;


        JLabel esqueceuSenha = new JLabel("<HTML><U>Esqueci a senha</U></HTML>", SwingConstants.CENTER);
        esqueceuSenha.setBounds(0, y, 312, 20);
        esqueceuSenha.setForeground(new Color(180, 180, 255));
        esqueceuSenha.setCursor(new Cursor(Cursor.HAND_CURSOR));
        painel.add(esqueceuSenha);


        Runnable autenticar = () -> {
            String email = emailbox.getText().trim();
            String senha = new String(senhabox.getPassword());

            if (email.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(this, "E-mail inválido!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }


            try {
                Usuario u = usuarioDAO.autenticarUsuario(email, senha);
                if (u != null) {
                    abrirExtrato(u.getNome(), u.getEmail());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "E-mail ou senha incorretos!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {

                if (email.equals("admin@gmail.com") && senha.equals("123456")) {
                    abrirExtrato("Administrador", email);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Banco de dados não disponível.\nUse admin@gmail.com / 123456 para demo.",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        };

        btnAutenticar.addActionListener(e -> autenticar.run());
        senhabox.addActionListener(e -> autenticar.run());

        btnCadastrar.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new TelaRegistro();
                dispose();
            });
        });

        esqueceuSenha.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                String emailRec = JOptionPane.showInputDialog(null, "Digite seu e-mail para recuperação:");
                if (emailRec == null || emailRec.trim().isEmpty()) return;
                try {
                    Usuario u = usuarioDAO.buscarPorEmail(emailRec.trim());
                    if (u != null) {
                        JOptionPane.showMessageDialog(null,
                                "E-mail encontrado!\nInstruções de recuperação seriam enviadas para: " + emailRec,
                                "Recuperação", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "E-mail não encontrado!", "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Banco de dados não disponível.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        setVisible(true);
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
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void abrirExtrato(String nome, String email) {
        SwingUtilities.invokeLater(() -> {
            new TelaExtrato(nome, email).setVisible(true);
            dispose();
        });
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(TelaLogin::new);
    }
}
