import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ParkSmartPro extends JFrame {

    // HTML Color Palette
    private final Color CLR_BG = new Color(243, 246, 251);
    private final Color CLR_ACCENT = new Color(26, 115, 232);
    private final Color CLR_BTN_GRADIENT = new Color(74, 158, 255);
    private final Color CLR_TEXT_MAIN = new Color(26, 35, 50);
    private final Color CLR_TEXT_MUTED = new Color(107, 124, 153);
    private final Color CLR_BORDER = new Color(220, 229, 240);

    public ParkSmartPro() {
        setTitle("ParkSmart - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(CLR_BG);
        setLayout(new GridBagLayout());

        // --- Main Card Container ---
        RoundedPanel card = new RoundedPanel(30, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(420, 550));

        // 1. Logo and Tagline
        JLabel lblLogo = new JLabel("ParkSmart");
        lblLogo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblLogo.setForeground(CLR_ACCENT);
        lblLogo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblTagline = new JLabel("Hotel Parking Management System");
        lblTagline.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTagline.setForeground(CLR_TEXT_MUTED);
        lblTagline.setAlignmentX(CENTER_ALIGNMENT);

        // 2. Role Tabs (User / Watchman)
        JPanel tabContainer = new RoundedPanel(12, new Color(248, 250, 253));
        tabContainer.setMaximumSize(new Dimension(340, 50));
        tabContainer.setLayout(new GridLayout(1, 2, 5, 5));
        tabContainer.setBorder(new EmptyBorder(4, 4, 4, 4));

        JButton btnUser = createTabButton("User Login", true);
        JButton btnWatch = createTabButton("Watchman Login", false);
        tabContainer.add(btnUser);
        tabContainer.add(btnWatch);

        // 3. Welcome Text
        JLabel lblWelcome = new JLabel("Welcome Back");
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblWelcome.setForeground(CLR_TEXT_MAIN);
        lblWelcome.setBorder(new EmptyBorder(30, 0, 5, 0));

        JLabel lblSub = new JLabel("Enter your mobile number to receive an OTP");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSub.setForeground(CLR_TEXT_MUTED);

        // 4. Input Field (Mobile)
        JLabel lblInput = new JLabel("Mobile Number");
        lblInput.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblInput.setForeground(CLR_TEXT_MUTED);
        lblInput.setBorder(new EmptyBorder(25, 0, 8, 0));

        JTextField txtMobile = new JTextField("+91 98xxxxxxxx");
        txtMobile.setPreferredSize(new Dimension(340, 45));
        txtMobile.setMaximumSize(new Dimension(340, 45));
        txtMobile.setFont(new Font("SansSerif", Font.PLAIN, 15));
        txtMobile.setBackground(new Color(248, 250, 253));
        txtMobile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(0, 15, 0, 15)
        ));

        // 5. Submit Button
        JButton btnGetOtp = new JButton("Get OTP →") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, CLR_ACCENT, getWidth(), getHeight(), CLR_BTN_GRADIENT);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnGetOtp.setPreferredSize(new Dimension(340, 50));
        btnGetOtp.setMaximumSize(new Dimension(340, 50));
        btnGetOtp.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnGetOtp.setContentAreaFilled(false);
        btnGetOtp.setBorderPainted(false);
        btnGetOtp.setFocusPainted(false);
        btnGetOtp.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- Assembly ---
        card.add(lblLogo);
        card.add(lblTagline);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(tabContainer);
        card.add(lblWelcome);
        card.add(lblSub);
        card.add(lblInput);
        card.add(txtMobile);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(btnGetOtp);

        add(card);
        setVisible(true);
    }

    private JButton createTabButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        if (active) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(CLR_ACCENT);
        } else {
            btn.setBackground(new Color(248, 250, 253));
            btn.setForeground(CLR_TEXT_MUTED);
        }
        return btn;
    }

    // Custom Component for Rounded Backgrounds and Borders
    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            // Subtle Shadow/Border
            g2.setColor(CLR_BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParkSmartPro::new);
    }
}