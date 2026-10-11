import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileWriter;

public class ParkSmartAdmin extends JFrame {

    // ═══════════ COLORS ═══════════
    static final Color BG         = new Color(240, 244, 250);
    static final Color SURFACE    = new Color(232, 238, 248);
    static final Color CARD       = Color.WHITE;
    static final Color BORDER     = new Color(220, 229, 240);
    static final Color FREE       = new Color(26, 115, 232);
    static final Color FREE_BG    = new Color(26, 115, 232, 20);
    static final Color OCCUPIED   = new Color(229, 57, 53);
    static final Color OCC_BG     = new Color(229, 57, 53, 18);
    static final Color WARN       = new Color(245, 158, 11);
    static final Color WARN_BG    = new Color(245, 158, 11, 25);
    static final Color GREEN      = new Color(22, 163, 74);
    static final Color GREEN_BG   = new Color(22, 163, 74, 22);
    static final Color PURPLE     = new Color(124, 58, 237);
    static final Color PURPLE_BG  = new Color(124, 58, 237, 20);
    static final Color TEXT       = new Color(26, 35, 50);
    static final Color MUTED      = new Color(107, 124, 153);
    static final Color MUTED2     = new Color(160, 174, 192);
    static final Color SIDEBAR_BG = Color.WHITE;

    // ═══════════ FONTS ═══════════
    static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD, 18);
    static final Font FONT_HEAD    = new Font("SansSerif", Font.BOLD, 14);
    static final Font FONT_BODY    = new Font("SansSerif", Font.PLAIN, 12);
    static final Font FONT_SMALL   = new Font("SansSerif", Font.PLAIN, 11);
    static final Font FONT_MONO    = new Font("Monospaced", Font.PLAIN, 12);
    static final Font FONT_STAT    = new Font("SansSerif", Font.BOLD, 28);
    static final Font FONT_LABEL   = new Font("SansSerif", Font.BOLD, 10);

    // ═══════════ DATA ═══════════
    static class Booking {
        String id, slot, name, mobile, vehicle, entry, exit, status;
        int paid, refund;
        long bookDate;
        Booking(String id, String slot, String name, String mobile, String vehicle,
                String entry, String exit, String status, int paid, int refund, long bookDate) {
            this.id=id; this.slot=slot; this.name=name; this.mobile=mobile;
            this.vehicle=vehicle; this.entry=entry; this.exit=exit; this.status=status;
            this.paid=paid; this.refund=refund; this.bookDate=bookDate;
        }
    }

    static class SlotData {
        String id, state;
        Booking booking;
        SlotData(String id, String state, Booking booking) {
            this.id=id; this.state=state; this.booking=booking;
        }
    }

    static class AdminUser {
        String name, phone, empid, registeredOn;
        AdminUser(String name, String phone, String empid, String registeredOn) {
            this.name=name; this.phone=phone; this.empid=empid; this.registeredOn=registeredOn;
        }
    }

    List<Booking> bookings = new ArrayList<>();
    List<SlotData> slots = new ArrayList<>();
    List<AdminUser> adminList = new ArrayList<>();

    // ═══════════ UI REFS ═══════════
    JPanel contentArea;
    CardLayout cardLayout;
    JLabel clockLabel, topbarTitle;
    JLabel statFree, statOcc, statOverdue;
    JPanel dashSlotGrid, slotsTabGrid;
    JTable bookingTable, allBookingsTable;
    DefaultTableModel bookingModel, allBookingsModel;
    JComboBox<String> bkFilter;
    JLabel adminBadge;
    JPanel adminListBody;
    JTextField rNameField, rPhoneField;
    JLabel empIdPreview;
    JTextField vVehicle, pVehicle;
    JTextField[] otpBoxes = new JTextField[4];
    JPanel verifyResult, penaltyResult;
    JLabel pSlotLabel, pOverdueLabel, pAmountLabel;
    JButton penaltyPayBtn;
    String selectedPayMethod = null;

    public ParkSmartAdmin() {
        initData();
        setTitle("ParkSmart – Admin Panel");
        setSize(1200, 750);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);

        setContentPane(root);

        startClock();
        buildDashSlotGrid(dashSlotGrid);
        buildBookingTable();

        setVisible(true);
    }

    void initData() {
        long now = System.currentTimeMillis();
        long day = 86400000L;
        bookings.add(new Booking("B001","A1","Arjun Kumar","9876543210","TN 33 AB 1234","09:30 AM","11:30 AM","active",120,0,now));
        bookings.add(new Booking("B002","A3","Priya Rajan","9988776655","TN 22 CD 5678","08:00 AM","10:00 AM","overdue",120,0,now));
        bookings.add(new Booking("B003","A5","Karthik S","9445566778","KA 05 MN 9988","10:00 AM","12:00 PM","active",120,0,now-day));
        bookings.add(new Booking("B004","A6","Meena Devi","9123456780","TN 01 ZZ 4321","07:00 AM","09:00 AM","done",120,20,now-day));
        bookings.add(new Booking("B005","A9","Ramesh V","9000112233","MH 12 XY 7654","09:00 AM","11:00 AM","overdue",120,0,now-2*day));
        bookings.add(new Booking("B006","A12","Lakshmi P","9776655443","TN 72 HH 1122","10:30 AM","12:30 PM","active",120,0,now-2*day));
        bookings.add(new Booking("B007","A15","Vijay R","9654321098","TN 11 KK 8877","11:00 AM","01:00 PM","active",120,0,now-3*day));
        bookings.add(new Booking("B008","A2","Anitha M","9333444555","TN 55 PP 3344","07:30 AM","09:30 AM","done",120,20,now-3*day));

        String[] slotIds = {"A1","A2","A3","A4","A5","A6","A7","A8","A9","A10","A11","A12","A13","A14","A15","A16"};
        Map<String,Booking> slotMap = new HashMap<>();
        for (Booking b : bookings) if (!b.status.equals("done")) slotMap.put(b.slot, b);
        for (String id : slotIds) {
            Booking b = slotMap.get(id);
            String state = b == null ? "free" : (b.status.equals("overdue") ? "overdue" : "occupied");
            slots.add(new SlotData(id, state, b));
        }
        adminList.add(new AdminUser("Raju K","9876500001","EMP-001","20 Mar 2026"));
    }

    // ═══════════ SIDEBAR ═══════════
    String activeTab = "dashboard";

    JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(new MatteBorder(0,0,0,1,BORDER));

        // Logo
        JPanel logo = new JPanel();
        logo.setLayout(new BoxLayout(logo, BoxLayout.Y_AXIS));
        logo.setBackground(SIDEBAR_BG);
        logo.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,BORDER), new EmptyBorder(14,16,14,16)));
        JLabel logoLabel = new JLabel("<html><span style='color:#1a73e8;font-size:16px;font-weight:bold'>Park</span><span style='font-size:16px;font-weight:bold'>Smart</span></html>");
        JLabel roleLabel = new JLabel("ADMIN PANEL");
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 9));
        roleLabel.setForeground(MUTED);
        logo.add(logoLabel);
        logo.add(Box.createVerticalStrut(2));
        logo.add(roleLabel);

        // Nav
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(SIDEBAR_BG);
        nav.setBorder(new EmptyBorder(8,0,8,0));

        nav.add(navSection("MAIN"));
        JButton[] navBtns = {
            navItem("🏠 Dashboard","dashboard"),
            navItem("🔐 Exit Verify","verify"),
            navItem("📋 Bookings","bookings"),
            navItem("🅿 Slot Map","slots")
        };
        for (JButton b : navBtns) nav.add(b);
        nav.add(navSection("REPORTS"));
        nav.add(navItem("⚠️ Alerts","alerts"));
        nav.add(navItem("💰 Revenue","revenue"));
        nav.add(navSection("ADMINISTRATION"));
        nav.add(navItem("👤 Manage Admins","admins"));
        nav.add(Box.createVerticalGlue());

        JScrollPane navScroll = new JScrollPane(nav);
        navScroll.setBorder(null);
        navScroll.setBackground(SIDEBAR_BG);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        footer.setBackground(SIDEBAR_BG);
        footer.setBorder(new MatteBorder(1,0,0,0,BORDER));
        JLabel avatar = new JLabel("W");
        avatar.setFont(new Font("SansSerif", Font.BOLD, 12));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(FREE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(30,30));
        avatar.setBorder(BorderFactory.createLineBorder(FREE_BG, 2));
        JPanel footInfo = new JPanel();
        footInfo.setLayout(new BoxLayout(footInfo, BoxLayout.Y_AXIS));
        footInfo.setBackground(SIDEBAR_BG);
        JLabel footName = new JLabel("Watchman");
        footName.setFont(FONT_HEAD);
        footName.setForeground(TEXT);
        JLabel footRole = new JLabel("Gate Officer");
        footRole.setFont(FONT_SMALL);
        footRole.setForeground(MUTED);
        footInfo.add(footName);
        footInfo.add(footRole);
        footer.add(avatar);
        footer.add(footInfo);

        sidebar.add(logo, BorderLayout.NORTH);
        sidebar.add(navScroll, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    Map<String, JButton> navButtons = new LinkedHashMap<>();

    JButton navItem(String text, String tab) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (tab.equals(activeTab)) {
                    g2.setColor(tab.equals("admins") ? PURPLE_BG : FREE_BG);
                    g2.fillRect(0,0,getWidth(),getHeight());
                    g2.setColor(tab.equals("admins") ? PURPLE : FREE);
                    g2.fillRect(0,0,3,getHeight());
                } else if (getModel().isRollover()) {
                    g2.setColor(FREE_BG);
                    g2.fillRect(0,0,getWidth(),getHeight());
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", tab.equals(activeTab) ? Font.BOLD : Font.PLAIN, 12));
        btn.setForeground(tab.equals(activeTab) ? (tab.equals("admins") ? PURPLE : FREE) : MUTED);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(8,16,8,16));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> switchTab(tab));
        navButtons.put(tab, btn);
        return btn;
    }

    JLabel navSection(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 9));
        lbl.setForeground(MUTED2);
        lbl.setBorder(new EmptyBorder(10,16,2,16));
        return lbl;
    }

    // ═══════════ MAIN AREA ═══════════
    JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);

        // Topbar
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(new Color(255,255,255,245));
        topbar.setPreferredSize(new Dimension(0,60));
        topbar.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,BORDER), new EmptyBorder(0,22,0,22)));

        topbarTitle = new JLabel("Dashboard");
        topbarTitle.setFont(FONT_HEAD);
        topbarTitle.setForeground(TEXT);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        topRight.setOpaque(false);
        JLabel liveChip = new JLabel("● System Live");
        liveChip.setFont(new Font("SansSerif", Font.BOLD, 10));
        liveChip.setForeground(FREE);
        liveChip.setBorder(new CompoundBorder(new LineBorder(new Color(26,115,232,76),1,true), new EmptyBorder(4,10,4,10)));
        liveChip.setOpaque(true);
        liveChip.setBackground(FREE_BG);
        clockLabel = new JLabel("--:--:-- --");
        clockLabel.setFont(FONT_MONO);
        clockLabel.setForeground(MUTED);
        topRight.add(liveChip);
        topRight.add(clockLabel);
        topbar.add(topbarTitle, BorderLayout.WEST);
        topbar.add(topRight, BorderLayout.EAST);

        // Content card layout
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(BG);
        contentArea.add(buildDashboard(), "dashboard");
        contentArea.add(buildVerifyTab(), "verify");
        contentArea.add(buildBookingsTab(), "bookings");
        contentArea.add(buildSlotsTab(), "slots");
        contentArea.add(buildAlertsTab(), "alerts");
        contentArea.add(buildRevenueTab(), "revenue");
        contentArea.add(buildAdminsTab(), "admins");

        main.add(topbar, BorderLayout.NORTH);
        main.add(contentArea, BorderLayout.CENTER);
        return main;
    }

    // ═══════════ DASHBOARD ═══════════
    JPanel buildDashboard() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(18,18,18,18));

        // Stat cards
        JPanel statsRow = new JPanel(new GridLayout(1,4,12,0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        statFree = new JLabel("10");
        statOcc  = new JLabel("6");
        statOverdue = new JLabel("2");
        statsRow.add(statCard("FREE SLOTS", statFree, FREE, "Available right now", FREE));
        statsRow.add(statCard("OCCUPIED", statOcc, OCCUPIED, "Currently parked", OCCUPIED));
        statsRow.add(statCard("OVERDUE", statOverdue, WARN, "Exceeded exit time", WARN));
        statsRow.add(statCard("TODAY REVENUE", new JLabel("₹1,440"), FREE, "12 bookings completed", FREE));

        p.add(statsRow);
        p.add(Box.createVerticalStrut(14));

        JPanel twoCol = new JPanel(new GridLayout(1,2,12,0));
        twoCol.setOpaque(false);
        twoCol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        // Slot Map
        JPanel slotCard = sectionCard("🅿 Live Slot Map – Zone A");
        dashSlotGrid = new JPanel(new GridLayout(4,4,8,8));
        dashSlotGrid.setOpaque(false);
        dashSlotGrid.setBorder(new EmptyBorder(12,12,12,12));
        slotCard.add(dashSlotGrid, BorderLayout.CENTER);
        twoCol.add(slotCard);

        // Alerts
        JPanel alertCard = sectionCard("⚠️ Live Alerts");
        JPanel alertBody = new JPanel();
        alertBody.setLayout(new BoxLayout(alertBody, BoxLayout.Y_AXIS));
        alertBody.setOpaque(false);
        alertBody.setBorder(new EmptyBorder(10,10,10,10));
        alertBody.add(alertItem("🚨","Slot A3 – Overdue 22 mins","TN 33 AB 1234 · Exit was 02:00 PM","2:22 PM",OCCUPIED,OCC_BG));
        alertBody.add(Box.createVerticalStrut(7));
        alertBody.add(alertItem("⏰","Slot A7 – Exit in 8 mins","KA 05 MN 9988 · Exit at 02:30 PM","2:22 PM",WARN,WARN_BG));
        alertBody.add(Box.createVerticalStrut(7));
        alertBody.add(alertItem("🔄","Slot A12 – Conflict Detected","Next booking starts at 03:00 PM","2:20 PM",FREE,FREE_BG));
        alertCard.add(alertBody, BorderLayout.CENTER);
        twoCol.add(alertCard);

        p.add(twoCol);
        p.add(Box.createVerticalStrut(14));

        // Today bookings
        JPanel todayCard = sectionCard("📋 Today's Bookings");
        String[] cols = {"Slot","Name","Vehicle","Entry","Exit","Status","Amount","Action"};
        bookingModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        bookingTable = styledTable(bookingModel);
        JScrollPane sp = new JScrollPane(bookingTable);
        sp.setBorder(null);
        sp.getViewport().setBackground(Color.WHITE);
        todayCard.add(sp, BorderLayout.CENTER);
        p.add(todayCard);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(scroll);
        return wrapper;
    }

    // ═══════════ EXIT VERIFY ═══════════
    JPanel buildVerifyTab() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 18));
        p.setBackground(BG);

        // OTP Panel
        JPanel otpPanel = verifyPanel("🔐 Exit OTP Verification");
        otpPanel.setPreferredSize(new Dimension(290,430));
        JPanel otpInner = (JPanel)otpPanel.getComponent(1);
        vVehicle = new JTextField();
        styleInput(vVehicle);
        vVehicle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        addFormLabel(otpInner, "Vehicle Number");
        otpInner.add(vVehicle);
        otpInner.add(Box.createVerticalStrut(8));
        addFormLabel(otpInner, "Enter Exit OTP");

        JPanel otpRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        otpRow.setOpaque(false);
        for (int i = 0; i < 4; i++) {
            otpBoxes[i] = new JTextField(1);
            otpBoxes[i].setFont(new Font("Monospaced", Font.BOLD, 20));
            otpBoxes[i].setHorizontalAlignment(SwingConstants.CENTER);
            otpBoxes[i].setPreferredSize(new Dimension(46,46));
            otpBoxes[i].setBorder(new CompoundBorder(new LineBorder(BORDER,1,true), new EmptyBorder(4,4,4,4)));
            final int idx = i;
            otpBoxes[i].addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    if (idx < 3 && Character.isDigit(e.getKeyChar()))
                        SwingUtilities.invokeLater(() -> otpBoxes[idx+1].requestFocus());
                }
            });
            otpRow.add(otpBoxes[i]);
        }
        otpInner.add(otpRow);
        otpInner.add(Box.createVerticalStrut(4));
        JLabel demoOtp = new JLabel("Demo OTP: 4287");
        demoOtp.setFont(new Font("Monospaced", Font.BOLD, 11));
        demoOtp.setForeground(WARN);
        otpInner.add(demoOtp);
        otpInner.add(Box.createVerticalStrut(10));
        JButton verifyBtn = actionButton("✓ Verify & Allow Exit", FREE, Color.WHITE);
        verifyBtn.addActionListener(e -> verifyExitOTP());
        otpInner.add(verifyBtn);
        otpInner.add(Box.createVerticalStrut(5));
        JButton clearBtn = outlineButton("Clear");
        clearBtn.addActionListener(e -> clearVerify());
        otpInner.add(clearBtn);
        verifyResult = new JPanel();
        verifyResult.setLayout(new BoxLayout(verifyResult, BoxLayout.Y_AXIS));
        verifyResult.setOpaque(false);
        otpInner.add(Box.createVerticalStrut(8));
        otpInner.add(verifyResult);

        // Penalty Panel
        JPanel penPanel = verifyPanel("💰 Penalty Collection");
        penPanel.setPreferredSize(new Dimension(290,430));
        JPanel penInner = (JPanel)penPanel.getComponent(1);
        JLabel penDesc = new JLabel("<html><div style='width:220px;color:#6b7c99'>If vehicle is overdue, collect penalty before generating exit OTP.</div></html>");
        penDesc.setFont(FONT_SMALL);
        penInner.add(penDesc);
        penInner.add(Box.createVerticalStrut(8));
        pVehicle = new JTextField();
        styleInput(pVehicle);
        pVehicle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        addFormLabel(penInner, "Vehicle Number");
        penInner.add(pVehicle);
        penInner.add(Box.createVerticalStrut(8));
        JButton checkBtn = actionButton("Check Penalty Amount", WARN, Color.WHITE);
        checkBtn.addActionListener(e -> checkPenalty());
        penInner.add(checkBtn);
        penInner.add(Box.createVerticalStrut(10));

        penaltyResult = new JPanel();
        penaltyResult.setLayout(new BoxLayout(penaltyResult, BoxLayout.Y_AXIS));
        penaltyResult.setOpaque(false);
        penaltyResult.setVisible(false);

        JPanel penBox = new JPanel();
        penBox.setLayout(new BoxLayout(penBox, BoxLayout.Y_AXIS));
        penBox.setBackground(WARN_BG);
        penBox.setBorder(new CompoundBorder(new LineBorder(new Color(245,158,11,76),1,true), new EmptyBorder(10,12,10,12)));
        JLabel penTitle = new JLabel("⚠ Penalty Due");
        penTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        penTitle.setForeground(WARN);
        pSlotLabel = new JLabel("Slot: —");
        pSlotLabel.setFont(FONT_BODY);
        pOverdueLabel = new JLabel("Overdue By: —");
        pOverdueLabel.setFont(FONT_BODY);
        pAmountLabel = new JLabel("Penalty: —");
        pAmountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        pAmountLabel.setForeground(OCCUPIED);
        penBox.add(penTitle);
        penBox.add(Box.createVerticalStrut(5));
        penBox.add(pSlotLabel);
        penBox.add(pOverdueLabel);
        penBox.add(pAmountLabel);

        JPanel payRow = new JPanel(new GridLayout(1,2,8,0));
        payRow.setOpaque(false);
        payRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JButton gpayBtn = payButton("📱 GPay / UPI","gpay");
        JButton cashBtn = payButton("💵 Hand Cash","cash");
        payRow.add(gpayBtn);
        payRow.add(cashBtn);

        penaltyPayBtn = actionButton("✓ Confirm Payment & Generate OTP", FREE, Color.WHITE);
        penaltyPayBtn.setVisible(false);
        penaltyPayBtn.addActionListener(e -> confirmPenaltyPaid());

        penaltyResult.add(penBox);
        penaltyResult.add(Box.createVerticalStrut(8));
        JLabel payLbl = new JLabel("Payment Method");
        payLbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        payLbl.setForeground(MUTED);
        penaltyResult.add(payLbl);
        penaltyResult.add(Box.createVerticalStrut(5));
        penaltyResult.add(payRow);
        penaltyResult.add(Box.createVerticalStrut(8));
        penaltyResult.add(penaltyPayBtn);
        penInner.add(penaltyResult);

        p.add(otpPanel);
        p.add(penPanel);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;   
    }

    JButton payButton(String text, String method) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 11));
        b.setForeground(TEXT);
        b.setBackground(new Color(248,250,253));
        b.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true), new EmptyBorder(6,8,6,8)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> selectPay(method));
        return b;
    }

    // ═══════════ BOOKINGS ═══════════
    JPanel buildBookingsTab() {
        JPanel p = new JPanel(new BorderLayout());
        // Date selection components
        JSpinner fromDate = new JSpinner(new SpinnerDateModel());
        JSpinner toDate = new JSpinner(new SpinnerDateModel());

        fromDate.setEditor(new JSpinner.DateEditor(fromDate, "dd-MM-yyyy"));
        toDate.setEditor(new JSpinner.DateEditor(toDate, "dd-MM-yyyy"));

        JPanel topPanel = new JPanel();

        topPanel.add(new JLabel("From Date:"));
        topPanel.add(fromDate);

        topPanel.add(new JLabel("To Date:"));
        topPanel.add(toDate);
        
        JButton downloadBtn = new JButton("Download Report");
        topPanel.add(downloadBtn);

        downloadBtn.addActionListener(e -> {
    try {
        Date from = (Date) fromDate.getValue();
        Date to = (Date) toDate.getValue();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            FileWriter writer = new FileWriter(fileToSave + ".csv");

            writer.write("From Date: " + from + "\n");
            writer.write("To Date: " + to + "\n");
            writer.write("Booking Report\n");

            writer.close();

            JOptionPane.showMessageDialog(null, "Report Downloaded Successfully!");
        }

    } catch (Exception ex) {
        ex.printStackTrace();
    }
});
// Add this panel to main panel
        p.add(topPanel, BorderLayout.NORTH);
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(18,18,18,18));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true), new EmptyBorder(0,0,0,0)));

        // Head
        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(CARD);
        head.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,BORDER), new EmptyBorder(10,14,10,14)));
        JLabel title = new JLabel("📋 All Bookings");
        title.setFont(FONT_HEAD);
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        bkFilter = new JComboBox<>(new String[]{"All Status","Active","Overdue","Completed"});
        bkFilter.setFont(FONT_SMALL);
        bkFilter.setBackground(Color.WHITE);
        bkFilter.addActionListener(e -> refreshAllBookings());
        JButton xlBtn = new JButton("📊 Export Excel");
        styleExportBtn(xlBtn, GREEN, Color.WHITE);
        xlBtn.addActionListener(e -> exportExcelDialog());
        JButton csvBtn = new JButton("📄 Export CSV");
        styleExportBtn(csvBtn, FREE_BG, FREE);
        csvBtn.setBorder(new CompoundBorder(new LineBorder(new Color(26,115,232,76),1,true), new EmptyBorder(4,10,4,10)));
        csvBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "CSV Export: " + bookings.size() + " records exported!", "Export CSV", JOptionPane.INFORMATION_MESSAGE));
        toolbar.add(bkFilter);
        toolbar.add(xlBtn);
        toolbar.add(csvBtn);
        head.add(title, BorderLayout.WEST);
        head.add(toolbar, BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        String[] cols2 = {"","Slot","Name","Mobile","Vehicle","Date","Entry","Exit","Status","Paid (₹)","Refund (₹)"};
        allBookingsModel = new DefaultTableModel(cols2, 0) {
            public boolean isCellEditable(int r, int c) { return c == 0; }
            public Class<?> getColumnClass(int c) { return c == 0 ? Boolean.class : String.class; }
        };
        allBookingsTable = styledTable(allBookingsModel);
        allBookingsTable.getColumnModel().getColumn(0).setMaxWidth(30);
        JScrollPane sp2 = new JScrollPane(allBookingsTable);
        sp2.setBorder(null);
        sp2.getViewport().setBackground(Color.WHITE);
        card.add(sp2, BorderLayout.CENTER);

        JPanel footer2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        footer2.setBackground(new Color(250,251,254));
        footer2.setBorder(new MatteBorder(1,0,0,0,BORDER));
        JLabel totLbl = new JLabel(bookings.size() + " total records");
        totLbl.setFont(FONT_SMALL);
        totLbl.setForeground(MUTED);
        JButton exportSelBtn = new JButton("📊 Export Selected");
        styleExportBtn(exportSelBtn, GREEN, Color.WHITE);
        exportSelBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,"Export selected rows","Export",JOptionPane.INFORMATION_MESSAGE));
        footer2.add(totLbl);
        footer2.add(exportSelBtn);
        card.add(footer2, BorderLayout.SOUTH);
        p.add(card);

        refreshAllBookings();
        return p;
    }

    // ═══════════ SLOT MAP ═══════════
    JPanel buildSlotsTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(18,18,18,18));

        JPanel card = sectionCard("🅿 Full Slot Map – Zone A");
        slotsTabGrid = new JPanel(new GridLayout(4,4,10,10));
        slotsTabGrid.setOpaque(false);
        slotsTabGrid.setBorder(new EmptyBorder(14,14,14,14));
        buildDashSlotGrid(slotsTabGrid);
        card.add(slotsTabGrid, BorderLayout.CENTER);
        p.add(card);
        return p;
    }

    // ═══════════ ALERTS ═══════════
    JPanel buildAlertsTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(18,18,18,18));

        JPanel card = sectionCard("⚠️ All Alerts");
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(10,10,10,10));
        body.add(alertItem("🚨","Slot A3 – Overdue 22 mins","TN 33 AB 1234 · Penalty: ₹110","2:22 PM",OCCUPIED,OCC_BG));
        body.add(Box.createVerticalStrut(7));
        body.add(alertItem("🚨","Slot A9 – Overdue 5 mins","MH 12 XY 7654 · Penalty: ₹25","2:22 PM",OCCUPIED,OCC_BG));
        body.add(Box.createVerticalStrut(7));
        body.add(alertItem("⏰","Slot A7 – Exit in 8 mins","KA 05 MN 9988 · Exit at 02:30 PM","2:22 PM",WARN,WARN_BG));
        body.add(Box.createVerticalStrut(7));
        body.add(alertItem("🔄","Slot A12 – Conflict: Next booking 03:00 PM","Current car must exit before 03:00 PM","2:20 PM",FREE,FREE_BG));
        card.add(body, BorderLayout.CENTER);
        p.add(card);
        return p;
    }

    // ═══════════ REVENUE ═══════════
    JPanel buildRevenueTab() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(18,18,18,18));

        JPanel statsRow = new JPanel(new GridLayout(1,4,12,0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        statsRow.add(statCard("TODAY", new JLabel("₹1,440"), FREE, "12 bookings", FREE));
        statsRow.add(statCard("THIS WEEK", new JLabel("₹8,760"), GREEN, "73 bookings", GREEN));
        statsRow.add(statCard("PENALTIES", new JLabel("₹480"), WARN, "4 overdue today", WARN));
        statsRow.add(statCard("REFUNDS", new JLabel("₹160"), FREE, "8 early exits", FREE));
        p.add(statsRow);
        p.add(Box.createVerticalStrut(14));

        JPanel revCard = sectionCard("💰 Revenue Breakdown");
        String[] revCols = {"Time","Slot","Vehicle","Base","Penalty","Refund","Net"};
        DefaultTableModel revModel = new DefaultTableModel(revCols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        JTable revTable = styledTable(revModel);
        for (Booking b : bookings) {
            int pen = b.status.equals("overdue") ? 30 : 0;
            int net = b.paid + pen - b.refund;
            revModel.addRow(new Object[]{b.entry, b.slot, b.vehicle, "₹"+(b.paid-20),
                    pen>0?"₹"+pen:"—", b.refund>0?"₹"+b.refund:"—", "₹"+net});
        }
        JScrollPane sp = new JScrollPane(revTable);
        sp.setBorder(null);
        sp.getViewport().setBackground(Color.WHITE);
        revCard.add(sp, BorderLayout.CENTER);
        p.add(revCard);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(scroll);
        return wrapper;
    }

    // ═══════════ ADMINS ═══════════
    JPanel buildAdminsTab() {
        JPanel p = new JPanel(new GridLayout(1,2,14,0));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(18,18,18,18));

        // Register card
        JPanel regCard = new JPanel(new BorderLayout());
        regCard.setBackground(CARD);
        regCard.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true), new EmptyBorder(0,0,0,0)));

        JPanel regHead = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 14));
        regHead.setBackground(new Color(124,58,237,15));
        regHead.setBorder(new MatteBorder(0,0,1,0,BORDER));
        JLabel regIcon = new JLabel("👤");
        regIcon.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JPanel regHeadText = new JPanel();
        regHeadText.setLayout(new BoxLayout(regHeadText, BoxLayout.Y_AXIS));
        regHeadText.setOpaque(false);
        JLabel regTitle = new JLabel("Register New Admin");
        regTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel regSub = new JLabel("Add a new staff member to the system");
        regSub.setFont(FONT_SMALL);
        regSub.setForeground(MUTED);
        regHeadText.add(regTitle);
        regHeadText.add(regSub);
        regHead.add(regIcon);
        regHead.add(regHeadText);

        JPanel regBody = new JPanel();
        regBody.setLayout(new BoxLayout(regBody, BoxLayout.Y_AXIS));
        regBody.setBackground(CARD);
        regBody.setBorder(new EmptyBorder(14,14,14,14));

        regBody.add(formLabel("Full Name *"));
        rNameField = new JTextField();
        styleInput(rNameField);
        rNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        regBody.add(rNameField);
        regBody.add(Box.createVerticalStrut(10));

        regBody.add(formLabel("Mobile Number *"));
        rPhoneField = new JTextField();
        styleInput(rPhoneField);
        rPhoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        regBody.add(rPhoneField);
        regBody.add(Box.createVerticalStrut(10));

        regBody.add(formLabel("Employee ID (Auto-generated)"));
        empIdPreview = new JLabel("EMP-002");
        empIdPreview.setFont(new Font("Monospaced", Font.BOLD, 13));
        empIdPreview.setForeground(PURPLE);
        empIdPreview.setOpaque(true);
        empIdPreview.setBackground(SURFACE);
        empIdPreview.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true), new EmptyBorder(8,12,8,12)));
        empIdPreview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        regBody.add(empIdPreview);
        regBody.add(Box.createVerticalStrut(12));

        JPanel divider = new JPanel();
        divider.setBackground(BORDER);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        divider.setPreferredSize(new Dimension(1,1));
        regBody.add(divider);
        regBody.add(Box.createVerticalStrut(12));

        JButton regBtn = actionButton("➕ Register Admin", PURPLE, Color.WHITE);
        regBtn.addActionListener(e -> registerAdmin());
        regBody.add(regBtn);

        regCard.add(regHead, BorderLayout.NORTH);
        regCard.add(regBody, BorderLayout.CENTER);

        // Admin list card
        JPanel listCard = new JPanel(new BorderLayout());
        listCard.setBackground(CARD);
        listCard.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true), new EmptyBorder(0,0,0,0)));

        JPanel listHead = new JPanel(new BorderLayout());
        listHead.setBackground(CARD);
        listHead.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,BORDER), new EmptyBorder(10,14,10,14)));
        JLabel listTitle = new JLabel("👥 Registered Admins");
        listTitle.setFont(FONT_HEAD);
        adminBadge = new JLabel("1");
        adminBadge.setFont(new Font("SansSerif", Font.BOLD, 10));
        adminBadge.setForeground(PURPLE);
        adminBadge.setOpaque(true);
        adminBadge.setBackground(PURPLE_BG);
        adminBadge.setBorder(new CompoundBorder(new LineBorder(new Color(124,58,237,76),1,true), new EmptyBorder(2,7,2,7)));
        listHead.add(listTitle, BorderLayout.WEST);
        listHead.add(adminBadge, BorderLayout.EAST);

        adminListBody = new JPanel();
        adminListBody.setLayout(new BoxLayout(adminListBody, BoxLayout.Y_AXIS));
        adminListBody.setBackground(CARD);
        adminListBody.setBorder(new EmptyBorder(10,10,10,10));
        JScrollPane adminScroll = new JScrollPane(adminListBody);
        adminScroll.setBorder(null);
        adminScroll.getViewport().setBackground(CARD);

        listCard.add(listHead, BorderLayout.NORTH);
        listCard.add(adminScroll, BorderLayout.CENTER);

        p.add(regCard);
        p.add(listCard);

        JScrollPane scroll = new JScrollPane(p);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;   
    }

    // ═══════════ SLOT GRID ═══════════
    void buildDashSlotGrid(JPanel grid) {
        if (grid == null) return;
        grid.removeAll();
        int free=0, occ=0, ov=0;
        for (SlotData s : slots) {
            if (s.state.equals("free")) free++;
            else if (s.state.equals("occupied")) occ++;
            else ov++;
            grid.add(slotButton(s));
        }
        if (statFree != null) statFree.setText(String.valueOf(free));
        if (statOcc  != null) statOcc.setText(String.valueOf(occ));
        if (statOverdue != null) statOverdue.setText(String.valueOf(ov));
        grid.revalidate();
        grid.repaint();
    }

    JButton slotButton(SlotData s) {
        Color bg, fg, border;
        String tag;
        switch (s.state) {
            case "occupied": bg=OCC_BG; fg=OCCUPIED; border=new Color(229,57,53,90); tag="OCCUPIED"; break;
            case "overdue":  bg=WARN_BG; fg=WARN;    border=new Color(245,158,11,100); tag="⚠ OVERDUE"; break;
            default:         bg=FREE_BG; fg=FREE;     border=new Color(26,115,232,90); tag="FREE"; break;
        }
        JButton btn = new JButton("<html><center><b>"+s.id+"</b><br><span style='font-size:8px'>"+tag+"</span></center></html>");
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBorder(new CompoundBorder(new LineBorder(border,1,true), new EmptyBorder(2,2,2,2)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(70,60));
        btn.addActionListener(e -> showSlotModal(s));
        return btn;
    }

    // ═══════════ SLOT MODAL ═══════════
    void showSlotModal(SlotData s) {
        JDialog dlg = new JDialog(this, "Slot " + s.id, true);
        dlg.setSize(380, 300);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CARD);
        content.setBorder(new EmptyBorder(18,18,18,18));

        JLabel title = new JLabel("Slot " + s.id + " — " + s.state.toUpperCase());
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(s.state.equals("free") ? FREE : s.state.equals("overdue") ? WARN : OCCUPIED);
        content.add(title);
        content.add(Box.createVerticalStrut(12));

        if (s.booking != null) {
            JPanel info = new JPanel(new GridLayout(3,2,8,8));
            info.setOpaque(false);
            info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            info.add(infoCell("Name", s.booking.name));
            info.add(infoCell("Vehicle", s.booking.vehicle));
            info.add(infoCell("Entry", s.booking.entry));
            info.add(infoCell("Exit", s.booking.exit));
            info.add(infoCell("Paid", "₹" + s.booking.paid));
            info.add(infoCell("Status", s.state.toUpperCase()));
            content.add(info);
        } else {
            JLabel avail = new JLabel("✓ Available for Booking");
            avail.setForeground(FREE);
            avail.setFont(FONT_HEAD);
            content.add(avail);
        }
        content.add(Box.createVerticalStrut(12));
        JButton closeBtn = outlineButton("Close");
        closeBtn.addActionListener(e -> dlg.dispose());
        closeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(closeBtn);

        dlg.setContentPane(content);
        dlg.setVisible(true);
    }

    // ═══════════ BOOKING TABLES ═══════════
    void buildBookingTable() {
        if (bookingModel == null) return;
        bookingModel.setRowCount(0);
        for (Booking b : bookings) {
            String status = b.status.equals("active") ? "● Active" : b.status.equals("overdue") ? "⚠ Overdue" : "✓ Done";
            bookingModel.addRow(new Object[]{b.slot, b.name, b.vehicle, b.entry, b.exit, status, "₹"+b.paid, b.status.equals("done")?"—":"Verify"});
        }
    }

    void refreshAllBookings() {
        if (allBookingsModel == null) return;
        allBookingsModel.setRowCount(0);
        String filter = bkFilter != null ? bkFilter.getSelectedItem().toString() : "All Status";
        for (Booking b : bookings) {
            if (!filter.equals("All Status")) {
                if (filter.equals("Active") && !b.status.equals("active")) continue;
                if (filter.equals("Overdue") && !b.status.equals("overdue")) continue;
                if (filter.equals("Completed") && !b.status.equals("done")) continue;
            }
            String status = b.status.equals("active") ? "● Active" : b.status.equals("overdue") ? "⚠ Overdue" : "✓ Done";
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            allBookingsModel.addRow(new Object[]{false, b.slot, b.name, b.mobile, b.vehicle,
                    sdf.format(new Date(b.bookDate)), b.entry, b.exit, status, "₹"+b.paid,
                    b.refund > 0 ? "₹"+b.refund : "—"});
        }
    }

    // ═══════════ EXIT VERIFY LOGIC ═══════════
    void verifyExitOTP() {
        String vehicle = vVehicle.getText().trim().toUpperCase();
        StringBuilder otp = new StringBuilder();
        for (JTextField tf : otpBoxes) otp.append(tf.getText());
        verifyResult.removeAll();
        if (vehicle.isEmpty()) {
            verifyResult.add(resultBox("Enter vehicle number", OCCUPIED, OCC_BG));
        } else if (!otp.toString().equals("4287")) {
            verifyResult.add(resultBox("❌ Invalid OTP · Demo OTP is: 4287", OCCUPIED, OCC_BG));
        } else {
            Booking found = bookings.stream().filter(b -> b.vehicle.replace(" ","").equalsIgnoreCase(vehicle.replace(" ","")) && !b.status.equals("done")).findFirst().orElse(null);
            if (found == null) {
                verifyResult.add(resultBox("❌ Vehicle not found in active bookings", OCCUPIED, OCC_BG));
            } else {
                found.status = "done";
                slots.stream().filter(s -> s.id.equals(found.slot)).findFirst().ifPresent(s -> s.state = "free");
                buildDashSlotGrid(dashSlotGrid);
                buildDashSlotGrid(slotsTabGrid);
                buildBookingTable();
                verifyResult.add(resultBox("✓ OTP Verified – Exit Allowed!\n" + found.name + " | " + found.slot + " | " + found.vehicle, FREE, FREE_BG));
            }
        }
        verifyResult.revalidate();
        verifyResult.repaint();
    }

    void clearVerify() {
        vVehicle.setText("");
        for (JTextField tf : otpBoxes) tf.setText("");
        verifyResult.removeAll();
        verifyResult.revalidate();
        verifyResult.repaint();
    }

    // ═══════════ PENALTY LOGIC ═══════════
    void checkPenalty() {
        String v = pVehicle.getText().trim().toUpperCase();
        Booking found = bookings.stream().filter(b -> b.vehicle.replace(" ","").equalsIgnoreCase(v.replace(" ","")) && b.status.equals("overdue")).findFirst().orElse(null);
        if (found == null) {
            JOptionPane.showMessageDialog(this, "Vehicle not found or not overdue.", "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }
        pSlotLabel.setText("Slot: " + found.slot);
        pOverdueLabel.setText("Overdue By: ~22 mins");
        pAmountLabel.setText("Penalty: ₹110");
        penaltyResult.setVisible(true);
        penaltyPayBtn.setVisible(false);
        selectedPayMethod = null;
    }

    void selectPay(String method) {
        selectedPayMethod = method;
        penaltyPayBtn.setVisible(true);
    }

    void confirmPenaltyPaid() {
        JOptionPane.showMessageDialog(this, "✓ Penalty collected via " + (selectedPayMethod.equals("gpay") ? "GPay/UPI" : "Hand Cash") + ".\nGo to Exit Verify tab to complete exit.", "Payment Confirmed", JOptionPane.INFORMATION_MESSAGE);
        penaltyResult.setVisible(false);
        pVehicle.setText("");
    }

    // ═══════════ ADMIN REGISTRATION ═══════════
    void registerAdmin() {
        String name = rNameField.getText().trim();
        String phone = rPhoneField.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(this,"Please enter the admin's full name.","Validation Error",JOptionPane.WARNING_MESSAGE); return; }
        if (!phone.matches("\\d{10}")) { JOptionPane.showMessageDialog(this,"Enter a valid 10-digit mobile number.","Validation Error",JOptionPane.WARNING_MESSAGE); return; }
        String empId = String.format("EMP-%03d", adminList.size() + 1);
        adminList.add(new AdminUser(name, phone, empId, new SimpleDateFormat("dd MMM yyyy").format(new Date())));
        rNameField.setText("");
        rPhoneField.setText("");
        empIdPreview.setText(String.format("EMP-%03d", adminList.size() + 1));
        renderAdminList();
        JOptionPane.showMessageDialog(this, "✅ Admin registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    Color[] avatarColors = {FREE, PURPLE, GREEN, OCCUPIED, WARN, new Color(8,145,178), new Color(190,24,93)};

    void renderAdminList() {
        adminListBody.removeAll();
        adminBadge.setText(String.valueOf(adminList.size()));
        for (int i = 0; i < adminList.size(); i++) {
            AdminUser a = adminList.get(i);
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setBackground(new Color(248,250,253));
            row.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true), new EmptyBorder(8,10,8,10)));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

            JLabel avatar = new JLabel(String.valueOf(a.name.charAt(0)).toUpperCase());
            avatar.setFont(new Font("SansSerif", Font.BOLD, 13));
            avatar.setForeground(Color.WHITE);
            avatar.setOpaque(true);
            avatar.setBackground(avatarColors[i % avatarColors.length]);
            avatar.setPreferredSize(new Dimension(34,34));
            avatar.setHorizontalAlignment(SwingConstants.CENTER);
            avatar.setBorder(BorderFactory.createLineBorder(avatarColors[i % avatarColors.length].darker(), 1));

            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setOpaque(false);
            JLabel nameLabel = new JLabel(a.name + "  [" + a.empid + "]");
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            JLabel meta = new JLabel("📱 +91 " + a.phone + "   · Registered: " + a.registeredOn);
            meta.setFont(new Font("SansSerif", Font.PLAIN, 10));
            meta.setForeground(MUTED);
            info.add(nameLabel);
            info.add(meta);

            final int idx = i;
            JButton del = new JButton("✕");
            del.setFont(new Font("SansSerif", Font.BOLD, 10));
            del.setForeground(MUTED);
            del.setBackground(Color.WHITE);
            del.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true), new EmptyBorder(3,6,3,6)));
            del.setFocusPainted(false);
            del.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            del.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(this,"Remove "+adminList.get(idx).name+"?","Confirm",JOptionPane.YES_NO_OPTION)==0) {
                    adminList.remove(idx);
                    renderAdminList();
                    empIdPreview.setText(String.format("EMP-%03d", adminList.size() + 1));
                }
            });

            row.add(avatar, BorderLayout.WEST);
            row.add(info, BorderLayout.CENTER);
            row.add(del, BorderLayout.EAST);
            adminListBody.add(row);
            adminListBody.add(Box.createVerticalStrut(6));
        }
        adminListBody.revalidate();
        adminListBody.repaint();
    }

    void exportExcelDialog() {
        JOptionPane.showMessageDialog(this,
            "Export to Excel: " + bookings.size() + " records ready.\n\n" +
            "In production, integrate Apache POI library\nto generate actual .xlsx files.",
            "Export Excel", JOptionPane.INFORMATION_MESSAGE);
    }

    // ═══════════ TAB SWITCH ═══════════
    void switchTab(String tab) {
        activeTab = tab;
        cardLayout.show(contentArea, tab);
        Map<String, String> titles = new HashMap<>();
        titles.put("dashboard","Dashboard"); titles.put("verify","Exit Verification");
        titles.put("bookings","All Bookings"); titles.put("slots","Slot Map");
        titles.put("alerts","Alerts"); titles.put("revenue","Revenue"); titles.put("admins","Manage Admins");
        topbarTitle.setText(titles.getOrDefault(tab, tab));
        navButtons.forEach((t, btn) -> {
            boolean active = t.equals(tab);
            btn.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 12));
            btn.setForeground(active ? (t.equals("admins") ? PURPLE : FREE) : MUTED);
        });
        if (tab.equals("slots")) buildDashSlotGrid(slotsTabGrid);
        if (tab.equals("bookings")) refreshAllBookings();
        if (tab.equals("admins")) renderAdminList();
        contentArea.revalidate();
        contentArea.repaint();
    }

    // ═══════════ CLOCK ═══════════
    void startClock() {
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (clockLabel != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
                        clockLabel.setText(sdf.format(new Date()));
                    }
                });
            }
        }, 0, 1000);
    }

    // ═══════════ COMPONENT HELPERS ═══════════
    JPanel statCard(String label, JLabel valLabel, Color accent, String sub, Color valColor) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(accent);
                g.fillRect(0, 0, getWidth(), 3);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true), new EmptyBorder(14,14,14,14)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 9));
        lbl.setForeground(MUTED);

        valLabel.setFont(FONT_STAT);
        valLabel.setForeground(valColor);

        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(FONT_SMALL);
        subLbl.setForeground(MUTED);

        card.add(lbl);
        card.add(Box.createVerticalStrut(6));
        card.add(valLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subLbl);
        return card;
    }

    JPanel sectionCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true), new EmptyBorder(0,0,0,0)));

        JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        head.setBackground(CARD);
        head.setBorder(new MatteBorder(0,0,1,0,BORDER));
        JLabel t = new JLabel(title);
        t.setFont(FONT_HEAD);
        head.add(t);
        card.add(head, BorderLayout.NORTH);
        return card;
    }

    JPanel alertItem(String icon, String title, String sub, String time, Color accent, Color bg) {
        JPanel item = new JPanel(new BorderLayout(8, 0));
        item.setBackground(bg);
        item.setBorder(new CompoundBorder(new LineBorder(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 76),1,true), new EmptyBorder(8,10,8,10)));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel textPnl = new JPanel();
        textPnl.setLayout(new BoxLayout(textPnl, BoxLayout.Y_AXIS));
        textPnl.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subLbl.setForeground(MUTED);
        textPnl.add(titleLbl);
        textPnl.add(subLbl);

        JLabel timeLbl = new JLabel(time);
        timeLbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
        timeLbl.setForeground(MUTED2);

        item.add(iconLbl, BorderLayout.WEST);
        item.add(textPnl, BorderLayout.CENTER);
        item.add(timeLbl, BorderLayout.EAST);
        return item;
    }

    JPanel verifyPanel(String title) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(CARD);
        outer.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true), new EmptyBorder(0,0,0,0)));

        JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT,12,12));
        head.setBackground(CARD);
        head.setBorder(new MatteBorder(0,0,1,0,BORDER));
        JLabel t = new JLabel(title);
        t.setFont(FONT_HEAD);
        head.add(t);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(CARD);
        inner.setBorder(new EmptyBorder(14,14,14,14));

        outer.add(head, BorderLayout.NORTH);
        outer.add(inner, BorderLayout.CENTER);
        return outer;
    }

    JPanel infoCell(String label, String value) {
        JPanel cell = new JPanel();
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        cell.setBackground(new Color(248,250,253));
        cell.setBorder(new CompoundBorder(new LineBorder(BORDER,1,true), new EmptyBorder(6,9,6,9)));
        JLabel l = new JLabel(label.toUpperCase());
        l.setFont(new Font("SansSerif", Font.BOLD, 9));
        l.setForeground(MUTED);
        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.BOLD, 12));
        cell.add(l);
        cell.add(Box.createVerticalStrut(2));
        cell.add(v);
        return cell;
    }

    JPanel resultBox(String msg, Color fg, Color bg) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(bg);
        box.setBorder(new CompoundBorder(new LineBorder(new Color(fg.getRed(),fg.getGreen(),fg.getBlue(),64),1,true), new EmptyBorder(10,12,10,12)));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        for (String line : msg.split("\n")) {
            JLabel lbl = new JLabel(line);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setForeground(fg);
            box.add(lbl);
        }
        return box;
    }

    JTable styledTable(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        tbl.setFont(FONT_BODY);
        tbl.setRowHeight(34);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0,0));
        tbl.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 10));
        tbl.getTableHeader().setBackground(new Color(250,251,254));
        tbl.getTableHeader().setForeground(MUTED);
        tbl.getTableHeader().setBorder(new MatteBorder(0,0,1,0,BORDER));
        tbl.setSelectionBackground(FREE_BG);
        tbl.setSelectionForeground(TEXT);
        tbl.setBackground(Color.WHITE);
        tbl.setForeground(TEXT);
        tbl.setGridColor(BORDER);
        tbl.setShowHorizontalLines(true);
        tbl.setBorder(null);
        return tbl;
    }

    JButton actionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorder(new CompoundBorder(new LineBorder(bg.darker(), 1, true), new EmptyBorder(9, 14, 9, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    JButton outlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setForeground(MUTED);
        btn.setBackground(Color.WHITE);
        btn.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8,14,8,14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    void styleInput(JTextField tf) {
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT);
        tf.setBackground(new Color(248,250,253));
        tf.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8,10,8,10)));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    void styleExportBtn(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorder(new CompoundBorder(new LineBorder(bg.darker(), 1, true), new EmptyBorder(5,11,5,11)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    void addFormLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(3));
    }

    JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // ═══════════ MAIN ═══════════
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        UIManager.put("Button.arc", 10);
        SwingUtilities.invokeLater(ParkSmartAdmin::new);
    }
}
