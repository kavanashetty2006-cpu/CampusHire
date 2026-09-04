import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

/**
 * CampusHire - College Placement & Recruitment Management System
 * 
 * Complete Java Swing Desktop Application in a Single File.
 * 
 * Core Functionality:
 *  - Multiple Student Registration with unique username validation
 *  - Student Login with individual credentials (stored in-memory)
 *  - Student Profile view and update
 *  - CGPA and academic marks validation
 *  - Resume selection using JFileChooser (with selected filename display)
 *  - Company recruitment drive browsing with package, location & CGPA eligibility
 *  - Job application submission and cancellation
 *  - Admin Login (admin / admin123) with masked password
 *  - Admin Placement Dashboard with applicant table
 *  - Admin selecting students for interviews and rejecting applicants
 *  - High-visibility interview notification and application status tracking
 *  - High-contrast, beautifully aligned UI with zero white-on-white contrast issues
 */
public class CampusHire {

    // ==========================================
    // COLOR PALETTE & DESIGN SYSTEM
    // ==========================================
    public static final Color PRIMARY_BLUE       = new Color(29, 78, 216);      // #1D4ED8 (Royal Blue)
    public static final Color PRIMARY_HOVER      = new Color(30, 58, 138);     // #1E3A8A
    public static final Color ACCENT_GREEN       = new Color(16, 149, 94);      // #10955E (Emerald Green)
    public static final Color ACCENT_GREEN_HOVER = new Color(11, 114, 71);
    public static final Color DANGER_RED         = new Color(220, 38, 38);      // #DC2626 (Crimson Red)
    public static final Color DANGER_RED_HOVER   = new Color(185, 28, 28);
    public static final Color WARNING_AMBER      = new Color(217, 119, 6);      // #D97706 (Amber)
    public static final Color WARNING_HOVER      = new Color(180, 83, 9);
    public static final Color NAVY_DARK          = new Color(15, 23, 42);       // #0F172A (Deep Slate Navy)
    public static final Color NAVY_SIDEBAR       = new Color(30, 41, 59);       // #1E293B (Slate 800)
    public static final Color NAVY_HOVER         = new Color(51, 65, 85);       // #334155 (Slate 700)

    public static final Color BG_MAIN            = new Color(241, 245, 249);    // #F1F5F9 (Clean Soft Slate)
    public static final Color CARD_BG            = Color.WHITE;
    public static final Color TEXT_DARK          = new Color(15, 23, 42);       // #0F172A (Deep Charcoal)
    public static final Color TEXT_MUTED         = new Color(71, 85, 105);      // #475569 (Slate 600)
    public static final Color BORDER_INPUT       = new Color(148, 163, 184);    // #94A3B8 (Visible Border)
    public static final Color BORDER_STRONG      = new Color(148, 163, 184);    // #94A3B8
    public static final Color BORDER_FOCUS       = new Color(29, 78, 216);      // #1D4ED8 (Active Blue Focus)
    public static final Color BORDER_CARD        = new Color(203, 213, 225);    // #CBD5E1 (Card Border)

    // Standard Typography
    public static final Font FONT_HERO      = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER    = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_LABEL     = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_INPUT     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BTN       = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL     = new Font("Segoe UI", Font.PLAIN, 12);

    // ==========================================
    // DATA MODELS
    // ==========================================
    public static class Student {
        public String name;
        public String srn;
        public String branch;
        public String email;
        public String phone;
        public String cgpa;
        public String marks;
        public String username;
        public String password;
        public String resume; // Absolute path to file on disk

        public Student(String name, String srn, String branch, String email, String phone,
                       String cgpa, String marks, String username, String password, String resume) {
            this.name = name;
            this.srn = (srn == null || srn.trim().isEmpty()) ? "SRN-" + System.currentTimeMillis() % 10000 : srn;
            this.branch = (branch == null || branch.trim().isEmpty()) ? "Computer Science & Engg" : branch;
            this.email = (email == null || email.trim().isEmpty()) ? username + "@campus.edu" : email;
            this.phone = (phone == null || phone.trim().isEmpty()) ? "+91 9876543210" : phone;
            this.cgpa = cgpa;
            this.marks = (marks == null || marks.trim().isEmpty()) ? "80%" : marks;
            this.username = username;
            this.password = password;
            this.resume = (resume == null) ? "" : resume;
        }

        public int getProfileCompletion() {
            int score = 0;
            if (name != null && !name.trim().isEmpty()) score += 10;
            if (srn != null && !srn.trim().isEmpty()) score += 10;
            if (branch != null && !branch.trim().isEmpty()) score += 10;
            if (email != null && !email.trim().isEmpty()) score += 10;
            if (phone != null && !phone.trim().isEmpty()) score += 10;
            if (cgpa != null && !cgpa.trim().isEmpty()) score += 15;
            if (marks != null && !marks.trim().isEmpty()) score += 15;
            if (username != null && !username.trim().isEmpty()) score += 10;
            if (resume != null && !resume.trim().isEmpty()) score += 10;
            return score;
        }
    }

    public static class Company {
        public String name;
        public String role;
        public double packageLpa;
        public double minCgpa;
        public String location;
        public String description;

        public Company(String name, String role, double packageLpa, double minCgpa, String location, String description) {
            this.name = name;
            this.role = role;
            this.packageLpa = packageLpa;
            this.minCgpa = minCgpa;
            this.location = location;
            this.description = description;
        }
    }

    public static class Application {
        public Student student;
        public Company company;
        public String applicationStatus; // "Applied", "Shortlisted", "Rejected"
        public String interviewStatus;   // "Pending Review", "SELECTED FOR INTERVIEW", "Interview Confirmed", "Interview Declined", "Not Selected"
        public String appliedDate;

        public Application(Student student, Company company) {
            this.student = student;
            this.company = company;
            this.applicationStatus = "Applied";
            this.interviewStatus = "Pending Review";
            this.appliedDate = new SimpleDateFormat("dd MMM yyyy").format(new Date());
        }
    }

    // ==========================================
    // GLOBAL DATABASE & STATE (In-Memory)
    // ==========================================
    public static final ArrayList<Student> students = new ArrayList<>();
    public static final ArrayList<Company> companies = new ArrayList<>();
    public static final ArrayList<Application> applications = new ArrayList<>();
    public static JFrame mainFrame;

    // ==========================================
    // MAIN APPLICATION ENTRY POINT
    // ==========================================
    public static void main(String[] args) {
        // Enable high-quality anti-aliasing text rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            // Set CrossPlatform look and feel for uniform, crisp component styling
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        seedInitialData();
        SwingUtilities.invokeLater(CampusHire::showWelcomeWindow);
    }

    private static void seedInitialData() {
        // Seed Companies
        companies.add(new Company("Google", "Software Engineer", 32.0, 8.5, "Bangalore, IN", "Cloud Platform, Distributed Systems & Applied AI."));
        companies.add(new Company("Microsoft", "Cloud Solution Architect", 28.5, 8.0, "Hyderabad, IN", "Azure Core Engineering & Enterprise Cloud."));
        companies.add(new Company("Amazon", "SDE-1", 25.0, 7.5, "Bangalore, IN", "AWS Platform & High-Throughput E-Commerce Systems."));
        companies.add(new Company("TCS", "Digital Specialist Engineer", 7.5, 6.5, "Pan India", "Enterprise Digital Transformation & Automation."));
        companies.add(new Company("Infosys", "Specialist Programmer", 9.5, 7.0, "Mysore / Pune", "NextGen Digital Platforms & Full Stack Cloud."));
        companies.add(new Company("Wipro", "Turbo Developer", 6.5, 6.0, "Bangalore, IN", "Digital Innovation, IoT & Engineering Services."));
        companies.add(new Company("Accenture", "Advanced App Analyst", 8.5, 6.5, "Bangalore, IN", "Applied Intelligence & Enterprise Software."));
        companies.add(new Company("IBM", "Associate System Engineer", 11.0, 7.0, "Bangalore, IN", "Quantum Computing & Hybrid Cloud Infrastructure."));

        // Seed Pre-Registered Demo Students
        Student s1 = new Student("Rahul Sharma", "PES1UG20CS101", "Computer Science & Engg", "rahul.sharma@campus.edu", "+91 9845123456", "8.8", "89%", "rahul", "123", "C:/Resumes/Rahul_Resume.pdf");
        Student s2 = new Student("Priya Patel", "PES1UG20EC045", "Electronics & Comm", "priya.patel@campus.edu", "+91 9886098765", "9.2", "94%", "priya", "123", "C:/Resumes/Priya_Resume.pdf");
        Student s3 = new Student("Amit Kumar", "PES1UG20IS088", "Information Science", "amit.kumar@campus.edu", "+91 9741001122", "7.4", "76%", "amit", "123", "C:/Resumes/Amit_Resume.pdf");

        students.add(s1);
        students.add(s2);
        students.add(s3);

        // Seed Initial Applications with distinct Application & Interview Statuses
        Application a1 = new Application(s1, companies.get(0)); // Google
        a1.applicationStatus = "Shortlisted";
        a1.interviewStatus = "SELECTED FOR INTERVIEW";

        Application a2 = new Application(s1, companies.get(1)); // Microsoft
        a2.applicationStatus = "Applied";
        a2.interviewStatus = "Pending Review";

        Application a3 = new Application(s2, companies.get(0)); // Google
        a3.applicationStatus = "Shortlisted";
        a3.interviewStatus = "Interview Confirmed";

        Application a4 = new Application(s3, companies.get(3)); // TCS
        a4.applicationStatus = "Applied";
        a4.interviewStatus = "Pending Review";

        applications.add(a1);
        applications.add(a2);
        applications.add(a3);
        applications.add(a4);
    }

    // ==========================================
    // CUSTOM HIGH-VISIBILITY BUTTON COMPONENT
    // ==========================================
    /**
     * Custom JButton that guarantees solid backgrounds, visible borders, and readable
     * text on ALL platforms without being overridden by native Windows button UI.
     */
    public static class StyledButton extends JButton {
        private final Color normalBg;
        private final Color hoverBg;
        private final Color pressedBg;
        private final Color borderColor;
        private final int cornerRadius;

        public StyledButton(String text, Color bg, Color fg, Color hoverBg, Color border, int radius) {
            super(text);
            this.normalBg = bg;
            this.hoverBg = hoverBg;
            this.pressedBg = hoverBg.darker();
            this.borderColor = border;
            this.cornerRadius = radius;
            setForeground(fg);
            setFont(FONT_BTN);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 18, 10, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (!isEnabled()) {
                g2.setColor(new Color(226, 232, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
                g2.setColor(new Color(148, 163, 184));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
                g2.dispose();
                super.paintComponent(g);
                return;
            }

            ButtonModel model = getModel();
            if (model.isPressed()) {
                g2.setColor(pressedBg);
            } else if (model.isRollover()) {
                g2.setColor(hoverBg);
            } else {
                g2.setColor(normalBg);
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

            // High-contrast outer border
            g2.setColor(borderColor != null ? borderColor : normalBg.darker());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static JButton createPrimaryButton(String text) {
        return new StyledButton(text, PRIMARY_BLUE, Color.WHITE, PRIMARY_HOVER, PRIMARY_HOVER, 8);
    }

    public static JButton createSuccessButton(String text) {
        return new StyledButton(text, ACCENT_GREEN, Color.WHITE, ACCENT_GREEN_HOVER, ACCENT_GREEN_HOVER, 8);
    }

    public static JButton createDangerButton(String text) {
        return new StyledButton(text, DANGER_RED, Color.WHITE, DANGER_RED_HOVER, DANGER_RED_HOVER, 8);
    }

    public static JButton createWarningButton(String text) {
        return new StyledButton(text, WARNING_AMBER, Color.WHITE, WARNING_HOVER, WARNING_HOVER, 8);
    }

    public static JButton createSecondaryButton(String text) {
        return new StyledButton(text, new Color(226, 232, 240), TEXT_DARK, new Color(203, 213, 225), BORDER_STRONG, 8);
    }

    public static JButton createNavyButton(String text) {
        return new StyledButton(text, NAVY_DARK, Color.WHITE, NAVY_HOVER, NAVY_HOVER, 8);
    }

    // ==========================================
    // UI HELPER WIDGETS
    // ==========================================
    public static JPanel createCardPanel() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_CARD, 2, false),
                new EmptyBorder(16, 20, 16, 20)
        ));
        return p;
    }

    public static JTextField createStyledTextField() {
        JTextField f = new JTextField();
        f.setFont(FONT_INPUT);
        f.setForeground(TEXT_DARK);
        f.setBackground(Color.WHITE);
        f.setCaretColor(TEXT_DARK);
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, 38));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_INPUT, 2, false),
                new EmptyBorder(6, 12, 6, 12)
        ));

        f.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_FOCUS, 2, false),
                        new EmptyBorder(6, 12, 6, 12)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_INPUT, 2, false),
                        new EmptyBorder(6, 12, 6, 12)
                ));
            }
        });
        return f;
    }

    public static JPasswordField createStyledPasswordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(FONT_INPUT);
        f.setForeground(TEXT_DARK);
        f.setBackground(Color.WHITE);
        f.setCaretColor(TEXT_DARK);
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, 38));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_INPUT, 2, false),
                new EmptyBorder(6, 12, 6, 12)
        ));

        f.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_FOCUS, 2, false),
                        new EmptyBorder(6, 12, 6, 12)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_INPUT, 2, false),
                        new EmptyBorder(6, 12, 6, 12)
                ));
            }
        });
        return f;
    }

    public static JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_DARK);
        return l;
    }

    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel("  " + text + "  ");
        badge.setFont(FONT_LABEL);
        badge.setOpaque(true);
        badge.setBackground(bg);
        badge.setForeground(fg);
        badge.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(fg, 1, false),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return badge;
    }

    public static void showToast(Component parent, String message, int messageType) {
        JOptionPane.showMessageDialog(parent, message, "CampusHire System Notification", messageType);
    }

    // ==========================================
    // WELCOME & AUTHENTICATION PORTAL (Main Window)
    // ==========================================
    public static void showWelcomeWindow() {
        if (mainFrame != null) {
            mainFrame.dispose();
        }

        mainFrame = new JFrame("CampusHire - College Placement & Recruitment Management System");
        mainFrame.setSize(1020, 680);
        mainFrame.setMinimumSize(new Dimension(900, 600));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.getContentPane().setBackground(BG_MAIN);

        // Split Layout: Left Info Banner, Right Form Panel
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        splitPanel.setBackground(BG_MAIN);

        // --- LEFT HERO PANEL ---
        JPanel heroPanel = new JPanel();
        heroPanel.setLayout(new BoxLayout(heroPanel, BoxLayout.Y_AXIS));
        heroPanel.setBackground(NAVY_DARK);
        heroPanel.setBorder(new EmptyBorder(45, 45, 45, 45));

        JLabel badgeTag = new JLabel("🎓 COLLEGE PLACEMENT CELL");
        badgeTag.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badgeTag.setForeground(new Color(96, 165, 250)); // Sky Blue

        JLabel brandTitle = new JLabel("CampusHire");
        brandTitle.setFont(FONT_HERO);
        brandTitle.setForeground(Color.WHITE);

        JLabel brandMotto = new JLabel("Campus Recruitment & Placement Automation Portal");
        brandMotto.setFont(FONT_REGULAR);
        brandMotto.setForeground(new Color(203, 213, 225));

        heroPanel.add(badgeTag);
        heroPanel.add(Box.createVerticalStrut(10));
        heroPanel.add(brandTitle);
        heroPanel.add(Box.createVerticalStrut(4));
        heroPanel.add(brandMotto);
        heroPanel.add(Box.createVerticalStrut(25));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        sep.setForeground(new Color(51, 65, 85));
        heroPanel.add(sep);
        heroPanel.add(Box.createVerticalStrut(25));

        heroPanel.add(createHeroFeature("🏢 Active Campus Drives", "Explore top companies like Google, Microsoft, Amazon & TCS."));
        heroPanel.add(Box.createVerticalStrut(16));
        heroPanel.add(createHeroFeature("📄 Resume Verification", "Upload local resumes via JFileChooser for recruiter review."));
        heroPanel.add(Box.createVerticalStrut(16));
        heroPanel.add(createHeroFeature("🔔 Real-Time Interview Alerts", "High-visibility notifications when shortlisted for interviews."));
        heroPanel.add(Box.createVerticalStrut(16));
        heroPanel.add(createHeroFeature("🛡️ Officer Admin Console", "Review applicants, verify CGPA & select candidates for interviews."));

        heroPanel.add(Box.createVerticalGlue());

        JLabel heroFooter = new JLabel("CampusHire Desktop Suite v2.0 • Pure Java Swing");
        heroFooter.setFont(FONT_SMALL);
        heroFooter.setForeground(new Color(148, 163, 184));
        heroPanel.add(heroFooter);

        // --- RIGHT FORM PANEL (Cards: Home Menu, Student Login, Registration, Admin Login) ---
        CardLayout rightLayout = new CardLayout();
        JPanel rightPanel = new JPanel(rightLayout);
        rightPanel.setBackground(BG_MAIN);
        rightPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel homeCard = createHomeMenuCard(rightLayout, rightPanel);
        JPanel studentLoginCard = createStudentLoginCard(rightLayout, rightPanel);
        JPanel registerCard = createStudentRegisterCard(rightLayout, rightPanel);
        JPanel adminLoginCard = createAdminLoginCard(rightLayout, rightPanel);

        rightPanel.add(homeCard, "HOME");
        rightPanel.add(studentLoginCard, "STUDENT_LOGIN");
        rightPanel.add(registerCard, "REGISTER");
        rightPanel.add(adminLoginCard, "ADMIN_LOGIN");

        splitPanel.add(heroPanel);
        splitPanel.add(rightPanel);

        mainFrame.add(splitPanel, BorderLayout.CENTER);
        mainFrame.setVisible(true);
    }

    private static JPanel createHeroFeature(String title, String desc) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(new Color(241, 245, 249));

        JLabel d = new JLabel("<html><body style='width: 280px;'>" + desc + "</body></html>");
        d.setFont(FONT_SMALL);
        d.setForeground(new Color(148, 163, 184));

        p.add(t);
        p.add(Box.createVerticalStrut(3));
        p.add(d);
        return p;
    }

    // --- 1. HOME PORTAL SELECTOR CARD ---
    private static JPanel createHomeMenuCard(CardLayout layout, JPanel parent) {
        JPanel card = createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Select Portal");
        heading.setFont(FONT_TITLE);
        heading.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("Choose your account type to proceed:");
        sub.setFont(FONT_REGULAR);
        sub.setForeground(TEXT_MUTED);

        card.add(heading);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(30));

        JButton btnStudent = createMenuOptionButton("👨‍🎓  Student Login", "Access placement drives, status & interviews", PRIMARY_BLUE);
        btnStudent.addActionListener(e -> layout.show(parent, "STUDENT_LOGIN"));

        JButton btnRegister = createMenuOptionButton("📝  Student Registration", "Create your placement profile & upload resume", ACCENT_GREEN);
        btnRegister.addActionListener(e -> layout.show(parent, "REGISTER"));

        JButton btnAdmin = createMenuOptionButton("🛡️  Admin / Officer Login", "Placement cell management & interview selection", NAVY_DARK);
        btnAdmin.addActionListener(e -> layout.show(parent, "ADMIN_LOGIN"));

        JButton btnExit = createDangerButton("❌  Exit Application");
        btnExit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnExit.addActionListener(e -> System.exit(0));

        card.add(btnStudent);
        card.add(Box.createVerticalStrut(14));
        card.add(btnRegister);
        card.add(Box.createVerticalStrut(14));
        card.add(btnAdmin);
        card.add(Box.createVerticalStrut(24));
        card.add(btnExit);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private static JButton createMenuOptionButton(String title, String subtitle, Color brandColor) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(12, 0));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_CARD, 2, false),
                new EmptyBorder(12, 16, 12, 16)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel t = new JLabel(title);
        t.setFont(FONT_HEADER);
        t.setForeground(brandColor);

        JLabel s = new JLabel(subtitle);
        s.setFont(FONT_SMALL);
        s.setForeground(TEXT_MUTED);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        textPanel.add(t);
        textPanel.add(s);

        JLabel arrow = new JLabel("→");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 20));
        arrow.setForeground(brandColor);

        btn.add(textPanel, BorderLayout.CENTER);
        btn.add(arrow, BorderLayout.EAST);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(241, 245, 249));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(brandColor, 2, false),
                        new EmptyBorder(12, 16, 12, 16)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_CARD, 2, false),
                        new EmptyBorder(12, 16, 12, 16)
                ));
            }
        });

        return btn;
    }

    // --- 2. STUDENT LOGIN CARD ---
    private static JPanel createStudentLoginCard(CardLayout layout, JPanel parent) {
        JPanel card = createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Student Login");
        heading.setFont(FONT_TITLE);
        heading.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("Enter your registered credentials to sign in:");
        sub.setFont(FONT_REGULAR);
        sub.setForeground(TEXT_MUTED);

        card.add(heading);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(25));

        JTextField userField = createStyledTextField();
        JPasswordField passField = createStyledPasswordField();

        card.add(createFieldLabel("Username:"));
        card.add(Box.createVerticalStrut(6));
        card.add(userField);
        card.add(Box.createVerticalStrut(18));

        card.add(createFieldLabel("Password:"));
        card.add(Box.createVerticalStrut(6));
        card.add(passField);
        card.add(Box.createVerticalStrut(10));

        JLabel hint = new JLabel("💡 Demo accounts: rahul / 123  |  priya / 123  |  amit / 123");
        hint.setFont(FONT_SMALL);
        hint.setForeground(PRIMARY_BLUE);
        card.add(hint);
        card.add(Box.createVerticalStrut(25));

        JButton btnLogin = createPrimaryButton("🔑  Login to Student Portal");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JButton btnBack = createSecondaryButton("←  Back to Main Menu");
        btnBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnLogin.addActionListener(e -> {
            String u = userField.getText().trim();
            String pwd = new String(passField.getPassword()).trim();

            if (u.isEmpty() || pwd.isEmpty()) {
                showToast(mainFrame, "Please enter both Username and Password.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Student matched = null;
            for (Student s : students) {
                if (s.username.equalsIgnoreCase(u) && s.password.equals(pwd)) {
                    matched = s;
                    break;
                }
            }

            if (matched != null) {
                userField.setText("");
                passField.setText("");
                openStudentDashboard(matched);
            } else {
                showToast(mainFrame, "Invalid credentials! Please verify your username and password.", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> layout.show(parent, "HOME"));

        card.add(btnLogin);
        card.add(Box.createVerticalStrut(12));
        card.add(btnBack);
        card.add(Box.createVerticalGlue());

        return card;
    }

    // --- 3. STUDENT REGISTRATION CARD ---
    private static JPanel createStudentRegisterCard(CardLayout layout, JPanel parent) {
        JPanel outer = createCardPanel();
        outer.setLayout(new BorderLayout(0, 14));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel heading = new JLabel("Student Registration");
        heading.setFont(FONT_TITLE);
        heading.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("Complete all fields to create your placement account:");
        sub.setFont(FONT_REGULAR);
        sub.setForeground(TEXT_MUTED);

        header.add(heading);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        outer.add(header, BorderLayout.NORTH);

        // Form Fields in clean GridBagLayout
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JTextField nameField = createStyledTextField();
        JTextField userField = createStyledTextField();
        JPasswordField passField = createStyledPasswordField();
        JTextField emailField = createStyledTextField();
        JTextField phoneField = createStyledTextField();
        JTextField cgpaField = createStyledTextField();
        JTextField srnField = createStyledTextField();

        String[] branchList = {
                "Computer Science & Engg",
                "Information Science & Engg",
                "Electronics & Comm Engg",
                "Electrical & Electronics",
                "Mechanical Engineering",
                "Civil Engineering",
                "Biotechnology"
        };
        JComboBox<String> branchCombo = new JComboBox<>(branchList);
        branchCombo.setFont(FONT_INPUT);
        branchCombo.setBackground(Color.WHITE);
        branchCombo.setBorder(new LineBorder(BORDER_INPUT, 2, false));
        branchCombo.setPreferredSize(new Dimension(branchCombo.getPreferredSize().width, 38));

        // Resume Picker Component
        JTextField resumePathField = createStyledTextField();
        resumePathField.setEditable(false);
        resumePathField.setBackground(new Color(241, 245, 249));
        resumePathField.setText("No resume selected");

        JButton btnChooseResume = createNavyButton("Choose Resume...");
        btnChooseResume.setFont(FONT_SMALL);

        btnChooseResume.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Your Resume File (PDF / DOC / DOCX)");
            int res = chooser.showOpenDialog(mainFrame);
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                resumePathField.setText(file.getAbsolutePath());
            }
        });

        JPanel resumeBox = new JPanel(new BorderLayout(8, 0));
        resumeBox.setOpaque(false);
        resumeBox.add(resumePathField, BorderLayout.CENTER);
        resumeBox.add(btnChooseResume, BorderLayout.EAST);

        // Alignment in order matching specification
        int row = 0;
        addFormGridRow(form, gbc, row++, "Full Name *", nameField);
        addFormGridRow(form, gbc, row++, "SRN / Roll No", srnField);
        addFormGridRow(form, gbc, row++, "Branch", branchCombo);
        addFormGridRow(form, gbc, row++, "Username *", userField);
        addFormGridRow(form, gbc, row++, "Password *", passField);
        addFormGridRow(form, gbc, row++, "Email *", emailField);
        addFormGridRow(form, gbc, row++, "Phone *", phoneField);
        addFormGridRow(form, gbc, row++, "CGPA (e.g. 8.5) *", cgpaField);
        addFormGridRow(form, gbc, row++, "Resume *", resumeBox);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        outer.add(scroll, BorderLayout.CENTER);

        // Buttons at Bottom
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnBack = createSecondaryButton("←  Back / Cancel");
        JButton btnSubmit = createSuccessButton("📝  Register Profile");

        btnSubmit.addActionListener(e -> {
            String name = nameField.getText().trim();
            String srn = srnField.getText().trim();
            String branch = (String) branchCombo.getSelectedItem();
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String cgpa = cgpaField.getText().trim();
            String resume = resumePathField.getText().trim();

            if (name.isEmpty() || user.isEmpty() || pass.isEmpty() || email.isEmpty() || phone.isEmpty() || cgpa.isEmpty()) {
                showToast(mainFrame, "Please fill in all required fields marked with *.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (resume.isEmpty() || resume.equals("No resume selected")) {
                showToast(mainFrame, "Please select a resume file from your computer using 'Choose Resume...'.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate CGPA format
            try {
                double val = Double.parseDouble(cgpa);
                if (val < 0.0 || val > 10.0) {
                    showToast(mainFrame, "Please enter a valid CGPA between 0.0 and 10.0.", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                showToast(mainFrame, "CGPA must be a valid number (e.g. 8.5).", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check duplicate username
            for (Student s : students) {
                if (s.username.equalsIgnoreCase(user)) {
                    showToast(mainFrame, "Username '" + user + "' is already registered! Please choose a different username.", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Student newStudent = new Student(name, srn.isEmpty() ? "SRN-" + user.toUpperCase() : srn, branch, email, phone, cgpa, "85%", user, pass, resume);
            students.add(newStudent);

            showToast(mainFrame, "Registration Successful! You can now log in with username: " + user, JOptionPane.INFORMATION_MESSAGE);

            // Clear inputs
            nameField.setText("");
            srnField.setText("");
            userField.setText("");
            passField.setText("");
            emailField.setText("");
            phoneField.setText("");
            cgpaField.setText("");
            resumePathField.setText("No resume selected");

            layout.show(parent, "STUDENT_LOGIN");
        });

        btnBack.addActionListener(e -> layout.show(parent, "HOME"));

        btnPanel.add(btnBack);
        btnPanel.add(btnSubmit);
        outer.add(btnPanel, BorderLayout.SOUTH);

        return outer;
    }

    private static void addFormGridRow(JPanel form, GridBagConstraints gbc, int row, String label, Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_DARK);
        form.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        form.add(field, gbc);
    }

    // --- 4. ADMIN LOGIN CARD ---
    private static JPanel createAdminLoginCard(CardLayout layout, JPanel parent) {
        JPanel card = createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Admin & Officer Login");
        heading.setFont(FONT_TITLE);
        heading.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("Authorized Placement Cell Administrator access:");
        sub.setFont(FONT_REGULAR);
        sub.setForeground(TEXT_MUTED);

        card.add(heading);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(25));

        JTextField userField = createStyledTextField();
        JPasswordField passField = createStyledPasswordField();

        card.add(createFieldLabel("Admin Username:"));
        card.add(Box.createVerticalStrut(6));
        card.add(userField);
        card.add(Box.createVerticalStrut(18));

        card.add(createFieldLabel("Admin Password:"));
        card.add(Box.createVerticalStrut(6));
        card.add(passField);
        card.add(Box.createVerticalStrut(10));

        JLabel hint = new JLabel("💡 Default credentials: username: 'admin' | password: 'admin123'");
        hint.setFont(FONT_SMALL);
        hint.setForeground(PRIMARY_BLUE);
        card.add(hint);
        card.add(Box.createVerticalStrut(25));

        JButton btnLogin = createNavyButton("🛡️  Login to Placement Console");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JButton btnBack = createSecondaryButton("←  Back to Main Menu");
        btnBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnLogin.addActionListener(e -> {
            String u = userField.getText().trim();
            String pwd = new String(passField.getPassword()).trim();

            if (u.equals("admin") && pwd.equals("admin123")) {
                userField.setText("");
                passField.setText("");
                openAdminDashboard();
            } else {
                showToast(mainFrame, "Invalid Admin Credentials! (Use admin / admin123)", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> layout.show(parent, "HOME"));

        card.add(btnLogin);
        card.add(Box.createVerticalStrut(12));
        card.add(btnBack);
        card.add(Box.createVerticalGlue());

        return card;
    }

    // ==========================================
    // STUDENT DASHBOARD WINDOW
    // ==========================================
    public static void openStudentDashboard(Student student) {
        JFrame f = new JFrame("CampusHire - Student Placement Dashboard (" + student.name + ")");
        f.setSize(1140, 720);
        f.setMinimumSize(new Dimension(950, 620));
        f.setLocationRelativeTo(mainFrame);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new BorderLayout());

        // Header
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(NAVY_DARK);
        topHeader.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel logo = new JLabel("🎓 CampusHire  |  Student Placement Portal");
        logo.setFont(FONT_HEADER);
        logo.setForeground(Color.WHITE);

        JPanel userBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userBadge.setOpaque(false);

        JLabel studentInfo = new JLabel("👤 " + student.name + " (" + student.srn + ") | CGPA: " + student.cgpa);
        studentInfo.setFont(FONT_LABEL);
        studentInfo.setForeground(new Color(226, 232, 240));

        JButton btnLogout = createDangerButton("Logout");
        btnLogout.setFont(FONT_SMALL);
        btnLogout.addActionListener(e -> {
            f.dispose();
            showWelcomeWindow();
        });

        userBadge.add(studentInfo);
        userBadge.add(btnLogout);

        topHeader.add(logo, BorderLayout.WEST);
        topHeader.add(userBadge, BorderLayout.EAST);

        // Sidebar Navigation + Dynamic Workspace
        JPanel workspace = new JPanel(new BorderLayout());

        CardLayout contentCards = new CardLayout();
        JPanel contentPanel = new JPanel(contentCards);
        contentPanel.setBackground(BG_MAIN);

        // Sidebar Panel
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(NAVY_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(24, 14, 24, 14));

        JLabel menuHeading = new JLabel("NAVIGATION MENU");
        menuHeading.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuHeading.setForeground(new Color(148, 163, 184));
        menuHeading.setBorder(new EmptyBorder(0, 10, 12, 0));
        sidebar.add(menuHeading);

        ArrayList<JButton> navButtons = new ArrayList<>();

        JButton btnTabOverview      = createSidebarNavButton("📊  Dashboard Overview");
        JButton btnTabCompanies     = createSidebarNavButton("🏢  Browse Companies");
        JButton btnTabApplications  = createSidebarNavButton("📝  My Applications");
        JButton btnTabNotifications = createSidebarNavButton("🔔  Interview Status");
        JButton btnTabProfile       = createSidebarNavButton("👤  My Profile & Resume");

        navButtons.add(btnTabOverview);
        navButtons.add(btnTabCompanies);
        navButtons.add(btnTabApplications);
        navButtons.add(btnTabNotifications);
        navButtons.add(btnTabProfile);

        // View Panels
        JPanel overviewView      = createStudentOverviewPanel(student, contentCards, contentPanel, navButtons, btnTabCompanies, btnTabNotifications);
        JPanel companiesView     = createStudentCompaniesPanel(student, f);
        JPanel applicationsView  = createStudentApplicationsPanel(student, f);
        JPanel notificationsView = createStudentNotificationsPanel(student, f);
        JPanel profileView       = createStudentProfilePanel(student, f);

        contentPanel.add(overviewView, "OVERVIEW");
        contentPanel.add(companiesView, "COMPANIES");
        contentPanel.add(applicationsView, "APPLICATIONS");
        contentPanel.add(notificationsView, "NOTIFICATIONS");
        contentPanel.add(profileView, "PROFILE");

        bindNavAction(btnTabOverview, "OVERVIEW", contentCards, contentPanel, navButtons, () -> refreshStudentOverview(overviewView, student, contentCards, contentPanel, navButtons, btnTabCompanies, btnTabNotifications));
        bindNavAction(btnTabCompanies, "COMPANIES", contentCards, contentPanel, navButtons, () -> refreshStudentCompanies(companiesView, student, f));
        bindNavAction(btnTabApplications, "APPLICATIONS", contentCards, contentPanel, navButtons, () -> refreshStudentApplications(applicationsView, student, f));
        bindNavAction(btnTabNotifications, "NOTIFICATIONS", contentCards, contentPanel, navButtons, () -> refreshStudentNotifications(notificationsView, student, f));
        bindNavAction(btnTabProfile, "PROFILE", contentCards, contentPanel, navButtons, () -> refreshStudentProfile(profileView, student, f));

        setActiveNav(btnTabOverview, navButtons);

        sidebar.add(btnTabOverview);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnTabCompanies);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnTabApplications);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnTabNotifications);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnTabProfile);
        sidebar.add(Box.createVerticalGlue());

        workspace.add(sidebar, BorderLayout.WEST);
        workspace.add(contentPanel, BorderLayout.CENTER);

        f.add(topHeader, BorderLayout.NORTH);
        f.add(workspace, BorderLayout.CENTER);
        f.setVisible(true);
    }

    private static void bindNavAction(JButton btn, String cardName, CardLayout layout, JPanel panel, ArrayList<JButton> allBtns, Runnable task) {
        btn.addActionListener(e -> {
            setActiveNav(btn, allBtns);
            if (task != null) task.run();
            layout.show(panel, cardName);
        });
    }

    private static void setActiveNav(JButton activeBtn, ArrayList<JButton> allBtns) {
        for (JButton b : allBtns) {
            if (b == activeBtn) {
                b.setBackground(PRIMARY_BLUE);
                b.setForeground(Color.WHITE);
                b.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY_HOVER, 2, false),
                        new EmptyBorder(10, 14, 10, 14)
                ));
            } else {
                b.setBackground(NAVY_SIDEBAR);
                b.setForeground(new Color(226, 232, 240));
                b.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(51, 65, 85), 1, false),
                        new EmptyBorder(10, 14, 10, 14)
                ));
            }
        }
    }

    private static JButton createSidebarNavButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_LABEL);
        b.setForeground(new Color(226, 232, 240));
        b.setBackground(NAVY_SIDEBAR);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(51, 65, 85), 1, false),
                new EmptyBorder(10, 14, 10, 14)
        ));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);

        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!b.getBackground().equals(PRIMARY_BLUE)) {
                    b.setBackground(NAVY_HOVER);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!b.getBackground().equals(PRIMARY_BLUE)) {
                    b.setBackground(NAVY_SIDEBAR);
                }
            }
        });
        return b;
    }

    // --- STUDENT TAB 1: OVERVIEW DASHBOARD ---
    private static JPanel createStudentOverviewPanel(Student s, CardLayout layout, JPanel parentPanel, ArrayList<JButton> navButtons, JButton btnCompanies, JButton btnNotifications) {
        JPanel p = new JPanel(new BorderLayout(0, 18));
        p.setBackground(BG_MAIN);
        p.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel welcome = new JLabel("Welcome back, " + s.name + " 👋");
        welcome.setFont(FONT_TITLE);
        welcome.setForeground(TEXT_DARK);
        JLabel sub = new JLabel("Your campus placement overview, interview invitations, and application progress.");
        sub.setFont(FONT_REGULAR);
        sub.setForeground(TEXT_MUTED);
        header.add(welcome, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);
        p.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        // Interview Highlight Box (Shown if shortlisted for an interview)
        Application interviewApp = null;
        for (Application a : applications) {
            if (a.student == s && (a.interviewStatus.equals("SELECTED FOR INTERVIEW") || a.interviewStatus.equals("Interview Confirmed"))) {
                interviewApp = a;
                break;
            }
        }

        if (interviewApp != null) {
            JPanel interviewAlertBox = new JPanel(new BorderLayout(14, 0));
            interviewAlertBox.setBackground(new Color(236, 253, 245)); // Soft Emerald
            interviewAlertBox.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ACCENT_GREEN, 2, true),
                    new EmptyBorder(14, 18, 14, 18)
            ));
            interviewAlertBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

            JPanel leftInfo = new JPanel(new GridLayout(2, 1, 0, 4));
            leftInfo.setOpaque(false);

            JLabel alertTitle = new JLabel("🎉 INTERVIEW INVITATION ACTIVE!");
            alertTitle.setFont(FONT_HEADER);
            alertTitle.setForeground(ACCENT_GREEN);

            JLabel alertDetail = new JLabel("<html><b>Company:</b> " + interviewApp.company.name + " &nbsp;|&nbsp; "
                    + "<b>Application Status:</b> " + interviewApp.applicationStatus + " &nbsp;|&nbsp; "
                    + "<b style='color:#059669;'>Interview Status: " + interviewApp.interviewStatus.toUpperCase() + "</b></html>");
            alertDetail.setFont(FONT_REGULAR);
            alertDetail.setForeground(TEXT_DARK);

            leftInfo.add(alertTitle);
            leftInfo.add(alertDetail);

            JButton btnViewNotif = createSuccessButton("Review & Confirm →");
            btnViewNotif.addActionListener(e -> {
                setActiveNav(btnNotifications, navButtons);
                layout.show(parentPanel, "NOTIFICATIONS");
            });

            interviewAlertBox.add(leftInfo, BorderLayout.CENTER);
            interviewAlertBox.add(btnViewNotif, BorderLayout.EAST);

            body.add(interviewAlertBox);
            body.add(Box.createVerticalStrut(16));
        }

        // Stats Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        int totalApplied = 0, pendingCount = 0, shortlistedCount = 0, confirmedCount = 0;
        for (Application a : applications) {
            if (a.student == s) {
                totalApplied++;
                if (a.interviewStatus.equals("SELECTED FOR INTERVIEW")) shortlistedCount++;
                else if (a.interviewStatus.equals("Interview Confirmed")) confirmedCount++;
                else if (a.applicationStatus.equals("Applied")) pendingCount++;
            }
        }

        statsRow.add(createMetricCard("Total Applied", String.valueOf(totalApplied), "Active Drives", PRIMARY_BLUE));
        statsRow.add(createMetricCard("Under Review", String.valueOf(pendingCount), "Awaiting Decision", WARNING_AMBER));
        statsRow.add(createMetricCard("Interview Selected", String.valueOf(shortlistedCount), "Action Required", ACCENT_GREEN));
        statsRow.add(createMetricCard("Confirmed Drives", String.valueOf(confirmedCount), "Scheduled Calls", NAVY_DARK));

        body.add(statsRow);
        body.add(Box.createVerticalStrut(20));

        // Two Content Boxes
        JPanel grid = new JPanel(new GridLayout(1, 2, 18, 0));
        grid.setOpaque(false);

        // Profile Snapshot Box
        JPanel profileBox = createCardPanel();
        profileBox.setLayout(new BoxLayout(profileBox, BoxLayout.Y_AXIS));

        JLabel pbTitle = new JLabel("Profile Strength & Details");
        pbTitle.setFont(FONT_HEADER);
        pbTitle.setForeground(TEXT_DARK);

        int compScore = s.getProfileCompletion();
        JLabel scoreLbl = new JLabel(compScore + "% Profile Completed");
        scoreLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        scoreLbl.setForeground(compScore == 100 ? ACCENT_GREEN : PRIMARY_BLUE);

        JProgressBar pbar = new JProgressBar(0, 100);
        pbar.setValue(compScore);
        pbar.setForeground(compScore == 100 ? ACCENT_GREEN : PRIMARY_BLUE);
        pbar.setBackground(new Color(226, 232, 240));

        JLabel detailsText = new JLabel("<html><body style='line-height: 1.6;'>"
                + "<b>SRN:</b> " + s.srn + "<br>"
                + "<b>Branch:</b> " + s.branch + "<br>"
                + "<b>Email:</b> " + s.email + "<br>"
                + "<b>Phone:</b> " + s.phone + "<br>"
                + "<b>CGPA:</b> " + s.cgpa + " &nbsp;|&nbsp; <b>Academic Score:</b> " + s.marks + "<br>"
                + "<b>Attached Resume:</b> " + (s.resume.isEmpty() ? "None" : new File(s.resume).getName())
                + "</body></html>");
        detailsText.setFont(FONT_REGULAR);
        detailsText.setForeground(TEXT_DARK);

        profileBox.add(pbTitle);
        profileBox.add(Box.createVerticalStrut(8));
        profileBox.add(scoreLbl);
        profileBox.add(Box.createVerticalStrut(6));
        profileBox.add(pbar);
        profileBox.add(Box.createVerticalStrut(12));
        profileBox.add(detailsText);

        // Placement Actions Box
        JPanel actionBox = createCardPanel();
        actionBox.setLayout(new BoxLayout(actionBox, BoxLayout.Y_AXIS));

        JLabel abTitle = new JLabel("Placement Actions");
        abTitle.setFont(FONT_HEADER);
        abTitle.setForeground(TEXT_DARK);

        JLabel abDesc = new JLabel("<html>Apply to active recruiters matching your CGPA or view your scheduled interview rounds.</html>");
        abDesc.setFont(FONT_REGULAR);
        abDesc.setForeground(TEXT_MUTED);

        JButton btnExplore = createPrimaryButton("🏢  Browse Recruitment Drives");
        btnExplore.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnExplore.addActionListener(e -> {
            setActiveNav(btnCompanies, navButtons);
            layout.show(parentPanel, "COMPANIES");
        });

        JButton btnCheckInterview = createSuccessButton("🔔  View Interview Status (" + shortlistedCount + ")");
        btnCheckInterview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnCheckInterview.addActionListener(e -> {
            setActiveNav(btnNotifications, navButtons);
            layout.show(parentPanel, "NOTIFICATIONS");
        });

        actionBox.add(abTitle);
        actionBox.add(Box.createVerticalStrut(6));
        actionBox.add(abDesc);
        actionBox.add(Box.createVerticalStrut(20));
        actionBox.add(btnExplore);
        actionBox.add(Box.createVerticalStrut(12));
        actionBox.add(btnCheckInterview);

        grid.add(profileBox);
        grid.add(actionBox);

        body.add(grid);
        body.add(Box.createVerticalGlue());

        p.add(body, BorderLayout.CENTER);
        return p;
    }

    private static void refreshStudentOverview(JPanel panel, Student s, CardLayout layout, JPanel parentPanel, ArrayList<JButton> navButtons, JButton btnCompanies, JButton btnNotifications) {
        panel.removeAll();
        JPanel updated = createStudentOverviewPanel(s, layout, parentPanel, navButtons, btnCompanies, btnNotifications);
        panel.setLayout(new BorderLayout());
        panel.add(updated, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private static JPanel createMetricCard(String title, String count, String sub, Color accent) {
        JPanel p = createCardPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title);
        t.setFont(FONT_LABEL);
        t.setForeground(TEXT_MUTED);

        JLabel c = new JLabel(count);
        c.setFont(new Font("Segoe UI", Font.BOLD, 24));
        c.setForeground(accent);

        JLabel s = new JLabel(sub);
        s.setFont(FONT_SMALL);
        s.setForeground(TEXT_MUTED);

        p.add(t);
        p.add(Box.createVerticalStrut(4));
        p.add(c);
        p.add(Box.createVerticalStrut(2));
        p.add(s);
        return p;
    }

    // --- STUDENT TAB 2: BROWSE RECRUITING COMPANIES ---
    private static JPanel createStudentCompaniesPanel(Student s, JFrame parentFrame) {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG_MAIN);
        p.setBorder(new EmptyBorder(22, 26, 22, 26));

        // Header and Search Bar
        JPanel top = new JPanel(new BorderLayout(14, 10));
        top.setOpaque(false);

        JLabel title = new JLabel("Browse Campus Recruitment Drives");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_DARK);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterRow.setOpaque(false);

        JLabel lblSearch = new JLabel("🔍 Search:");
        lblSearch.setFont(FONT_LABEL);
        lblSearch.setForeground(TEXT_DARK);

        JTextField searchField = createStyledTextField();
        searchField.setPreferredSize(new Dimension(240, 38));

        JCheckBox eligibleOnlyCheck = new JCheckBox("Show only eligible (CGPA >= " + s.cgpa + ")");
        eligibleOnlyCheck.setFont(FONT_LABEL);
        eligibleOnlyCheck.setForeground(TEXT_DARK);
        eligibleOnlyCheck.setOpaque(false);

        filterRow.add(lblSearch);
        filterRow.add(searchField);
        filterRow.add(eligibleOnlyCheck);

        top.add(title, BorderLayout.NORTH);
        top.add(filterRow, BorderLayout.SOUTH);
        p.add(top, BorderLayout.NORTH);

        // Container of Company Cards
        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(BG_MAIN);

        buildCompanyCardList(listContainer, s, parentFrame, "", false);

        searchField.addCaretListener(e -> buildCompanyCardList(listContainer, s, parentFrame, searchField.getText().trim().toLowerCase(), eligibleOnlyCheck.isSelected()));
        eligibleOnlyCheck.addActionListener(e -> buildCompanyCardList(listContainer, s, parentFrame, searchField.getText().trim().toLowerCase(), eligibleOnlyCheck.isSelected()));

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private static void refreshStudentCompanies(JPanel panel, Student s, JFrame parentFrame) {
        JScrollPane scroll = (JScrollPane) panel.getComponent(1);
        JPanel list = (JPanel) scroll.getViewport().getView();
        buildCompanyCardList(list, s, parentFrame, "", false);
    }

    private static void buildCompanyCardList(JPanel container, Student s, JFrame parentFrame, String query, boolean eligibleOnly) {
        container.removeAll();

        double studentCgpa = 0.0;
        try {
            studentCgpa = Double.parseDouble(s.cgpa);
        } catch (Exception ignored) {}

        int count = 0;
        for (Company c : companies) {
            boolean matchesSearch = query.isEmpty()
                    || c.name.toLowerCase().contains(query)
                    || c.role.toLowerCase().contains(query)
                    || c.location.toLowerCase().contains(query);

            boolean isEligible = studentCgpa >= c.minCgpa;

            if (eligibleOnly && !isEligible) {
                continue;
            }

            if (matchesSearch) {
                count++;
                container.add(createStructuredCompanyCard(c, s, parentFrame, isEligible));
                container.add(Box.createVerticalStrut(14));
            }
        }

        if (count == 0) {
            JPanel empty = createCardPanel();
            JLabel lbl = new JLabel("No recruitment drives found matching your filter criteria.");
            lbl.setFont(FONT_LABEL);
            lbl.setForeground(TEXT_MUTED);
            empty.add(lbl);
            container.add(empty);
        }

        container.revalidate();
        container.repaint();
    }

    private static JPanel createStructuredCompanyCard(Company c, Student s, JFrame parentFrame, boolean isEligible) {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(16, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));

        // Left Information Area
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JPanel titleLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleLine.setOpaque(false);

        JLabel compName = new JLabel(c.name);
        compName.setFont(FONT_HEADER);
        compName.setForeground(TEXT_DARK);

        JLabel roleBadge = createBadge(c.role, new Color(219, 234, 254), PRIMARY_BLUE);
        JLabel pkgBadge = createBadge(c.packageLpa + " LPA", new Color(209, 250, 229), ACCENT_GREEN);
        JLabel locLabel = new JLabel("📍 " + c.location);
        locLabel.setFont(FONT_SMALL);
        locLabel.setForeground(TEXT_MUTED);

        titleLine.add(compName);
        titleLine.add(roleBadge);
        titleLine.add(pkgBadge);
        titleLine.add(locLabel);

        JLabel desc = new JLabel(c.description);
        desc.setFont(FONT_REGULAR);
        desc.setForeground(TEXT_DARK);
        desc.setBorder(new EmptyBorder(4, 4, 4, 4));

        JLabel crit = new JLabel("Required CGPA: " + c.minCgpa + "  |  Your CGPA: " + s.cgpa + (isEligible ? "  (Eligible)" : "  (Not Eligible)"));
        crit.setFont(FONT_LABEL);
        crit.setForeground(isEligible ? ACCENT_GREEN : DANGER_RED);
        crit.setBorder(new EmptyBorder(2, 4, 0, 4));

        left.add(titleLine);
        left.add(desc);
        left.add(crit);

        // Right Action Area
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        right.setOpaque(false);

        Application existing = null;
        for (Application a : applications) {
            if (a.student == s && a.company.name.equalsIgnoreCase(c.name)) {
                existing = a;
                break;
            }
        }

        if (existing != null) {
            JLabel statusBadge = createBadge("✓ " + existing.interviewStatus, new Color(219, 234, 254), PRIMARY_BLUE);
            right.add(statusBadge);

            if (existing.applicationStatus.equals("Applied") && existing.interviewStatus.equals("Pending Review")) {
                Application toCancel = existing;
                JButton btnCancel = createWarningButton("Cancel App");
                btnCancel.setFont(FONT_SMALL);
                btnCancel.addActionListener(e -> {
                    int conf = JOptionPane.showConfirmDialog(parentFrame,
                            "Withdraw and cancel your application to " + c.name + "?",
                            "Confirm Cancel", JOptionPane.YES_NO_OPTION);
                    if (conf == JOptionPane.YES_OPTION) {
                        applications.remove(toCancel);
                        showToast(parentFrame, "Application withdrawn for " + c.name + ".", JOptionPane.INFORMATION_MESSAGE);
                        buildCompanyCardList((JPanel) card.getParent(), s, parentFrame, "", false);
                    }
                });
                right.add(btnCancel);
            }
        } else if (!isEligible) {
            JButton btnIneligible = createSecondaryButton("Not Eligible");
            btnIneligible.setEnabled(false);
            btnIneligible.setFont(FONT_SMALL);
            right.add(btnIneligible);
        } else {
            JButton btnApply = createPrimaryButton("🚀  APPLY");
            btnApply.setFont(FONT_LABEL);
            btnApply.addActionListener(e -> {
                int conf = JOptionPane.showConfirmDialog(parentFrame,
                        "Submit your application and resume to " + c.name + " for " + c.role + "?",
                        "Confirm Application", JOptionPane.YES_NO_OPTION);
                if (conf == JOptionPane.YES_OPTION) {
                    Application newApp = new Application(s, c);
                    applications.add(newApp);
                    showToast(parentFrame, "Application successfully submitted to " + c.name + "!", JOptionPane.INFORMATION_MESSAGE);
                    buildCompanyCardList((JPanel) card.getParent(), s, parentFrame, "", false);
                }
            });
            right.add(btnApply);
        }

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    // --- STUDENT TAB 3: MY APPLICATIONS & STATUS TABLE ---
    private static JPanel createStudentApplicationsPanel(Student s, JFrame parentFrame) {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG_MAIN);
        p.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = new JLabel("My Applications & Placement Status");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_DARK);
        top.add(title, BorderLayout.WEST);
        p.add(top, BorderLayout.NORTH);

        String[] cols = {"#", "Company Name", "Job Role", "Package (LPA)", "Applied Date", "Application Status", "Interview Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(model);
        styleStandardTable(table);

        populateStudentAppsTable(model, s);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(BORDER_CARD, 2, false));
        p.add(scroll, BorderLayout.CENTER);

        // Bottom Action Bar: Cancel Selected Application
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bottom.setOpaque(false);

        JButton btnCancelApp = createDangerButton("🚫  Cancel Selected Application");
        btnCancelApp.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                showToast(parentFrame, "Please select an application row from the table first.", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String compName = (String) table.getValueAt(row, 1);
            Application appToCancel = null;
            for (Application a : applications) {
                if (a.student == s && a.company.name.equalsIgnoreCase(compName)) {
                    appToCancel = a;
                    break;
                }
            }

            if (appToCancel != null) {
                if (appToCancel.interviewStatus.equals("Interview Confirmed") || appToCancel.interviewStatus.equals("SELECTED FOR INTERVIEW")) {
                    showToast(parentFrame, "Cannot cancel application after interview shortlisting. Please decline in Interview Status tab if necessary.", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int conf = JOptionPane.showConfirmDialog(parentFrame,
                        "Are you sure you want to withdraw/cancel your application for " + compName + "?",
                        "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

                if (conf == JOptionPane.YES_OPTION) {
                    applications.remove(appToCancel);
                    showToast(parentFrame, "Application for " + compName + " successfully cancelled.", JOptionPane.INFORMATION_MESSAGE);
                    populateStudentAppsTable(model, s);
                }
            }
        });

        bottom.add(btnCancelApp);
        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }

    private static void refreshStudentApplications(JPanel panel, Student s, JFrame parentFrame) {
        JScrollPane scroll = (JScrollPane) panel.getComponent(1);
        JTable table = (JTable) scroll.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        populateStudentAppsTable(model, s);
    }

    private static void populateStudentAppsTable(DefaultTableModel model, Student s) {
        model.setRowCount(0);
        int idx = 1;
        for (Application a : applications) {
            if (a.student == s) {
                model.addRow(new Object[]{
                        idx++,
                        a.company.name,
                        a.company.role,
                        a.company.packageLpa + " LPA",
                        a.appliedDate,
                        a.applicationStatus,
                        a.interviewStatus
                });
            }
        }
    }

    // --- STUDENT TAB 4: INTERVIEW STATUS & CONFIRMATION ---
    private static JPanel createStudentNotificationsPanel(Student s, JFrame parentFrame) {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG_MAIN);
        p.setBorder(new EmptyBorder(22, 26, 22, 26));

        JLabel title = new JLabel("Interview Status & Scheduled Rounds");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_DARK);
        p.add(title, BorderLayout.NORTH);

        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(BG_MAIN);

        buildInterviewStatusCards(listContainer, s, parentFrame);

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private static void refreshStudentNotifications(JPanel panel, Student s, JFrame parentFrame) {
        JScrollPane scroll = (JScrollPane) panel.getComponent(1);
        JPanel list = (JPanel) scroll.getViewport().getView();
        buildInterviewStatusCards(list, s, parentFrame);
    }

    private static void buildInterviewStatusCards(JPanel container, Student s, JFrame parentFrame) {
        container.removeAll();

        int count = 0;
        for (Application a : applications) {
            if (a.student == s) {
                count++;
                JPanel card = createCardPanel();
                card.setLayout(new BorderLayout(16, 10));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));

                JPanel info = new JPanel();
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                info.setOpaque(false);

                JLabel compTitle = new JLabel("🏢 Company: " + a.company.name + " (" + a.company.role + ")");
                compTitle.setFont(FONT_HEADER);
                compTitle.setForeground(TEXT_DARK);

                JLabel appStatusLbl = new JLabel("Application Status: " + a.applicationStatus + "  |  Applied Date: " + a.appliedDate);
                appStatusLbl.setFont(FONT_REGULAR);
                appStatusLbl.setForeground(TEXT_MUTED);

                JLabel interviewLbl = new JLabel("Interview Status: " + a.interviewStatus.toUpperCase());
                interviewLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
                actionPanel.setOpaque(false);

                if (a.interviewStatus.equals("SELECTED FOR INTERVIEW")) {
                    interviewLbl.setForeground(ACCENT_GREEN);

                    JButton btnAccept = createSuccessButton("✓  Confirm Interview");
                    JButton btnDecline = createDangerButton("✗  Decline");

                    btnAccept.addActionListener(e -> {
                        a.interviewStatus = "Interview Confirmed";
                        showToast(parentFrame, "Interview confirmed for " + a.company.name + "! Placement cell notified.", JOptionPane.INFORMATION_MESSAGE);
                        buildInterviewStatusCards(container, s, parentFrame);
                    });

                    btnDecline.addActionListener(e -> {
                        int conf = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to decline this interview?", "Decline Interview", JOptionPane.YES_NO_OPTION);
                        if (conf == JOptionPane.YES_OPTION) {
                            a.interviewStatus = "Interview Declined";
                            showToast(parentFrame, "Interview invitation declined.", JOptionPane.INFORMATION_MESSAGE);
                            buildInterviewStatusCards(container, s, parentFrame);
                        }
                    });

                    actionPanel.add(btnAccept);
                    actionPanel.add(btnDecline);

                } else if (a.interviewStatus.equals("Interview Confirmed")) {
                    interviewLbl.setForeground(PRIMARY_BLUE);
                    actionPanel.add(createBadge("CONFIRMED FOR INTERVIEW", new Color(219, 234, 254), PRIMARY_BLUE));

                } else if (a.interviewStatus.equals("Interview Declined")) {
                    interviewLbl.setForeground(TEXT_MUTED);
                    actionPanel.add(createBadge("DECLINED", new Color(241, 245, 249), TEXT_MUTED));

                } else if (a.applicationStatus.equals("Rejected") || a.interviewStatus.equals("Not Selected")) {
                    interviewLbl.setForeground(DANGER_RED);
                    actionPanel.add(createBadge("NOT SHORTLISTED", new Color(254, 226, 226), DANGER_RED));

                } else {
                    interviewLbl.setForeground(WARNING_AMBER);
                    actionPanel.add(createBadge("PENDING REVIEW", new Color(254, 243, 199), WARNING_AMBER));
                }

                info.add(compTitle);
                info.add(Box.createVerticalStrut(4));
                info.add(appStatusLbl);
                info.add(Box.createVerticalStrut(4));
                info.add(interviewLbl);

                card.add(info, BorderLayout.CENTER);
                card.add(actionPanel, BorderLayout.EAST);

                container.add(card);
                container.add(Box.createVerticalStrut(12));
            }
        }

        if (count == 0) {
            JPanel empty = createCardPanel();
            JLabel emptyLbl = new JLabel("No active applications yet. Browse companies and apply to view your interview status!");
            emptyLbl.setFont(FONT_LABEL);
            emptyLbl.setForeground(TEXT_MUTED);
            empty.add(emptyLbl);
            container.add(empty);
        }

        container.revalidate();
        container.repaint();
    }

    // --- STUDENT TAB 5: MY CANDIDATE PROFILE ---
    private static JPanel createStudentProfilePanel(Student s, JFrame parentFrame) {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG_MAIN);
        p.setBorder(new EmptyBorder(22, 26, 22, 26));

        JLabel title = new JLabel("My Candidate Profile");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_DARK);
        p.add(title, BorderLayout.NORTH);

        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(20, 20));
        card.setBorder(new EmptyBorder(25, 30, 25, 30));

        JPanel detailsGrid = new JPanel(new GridLayout(8, 2, 14, 14));
        detailsGrid.setOpaque(false);

        addProfileField(detailsGrid, "Full Name:", s.name);
        addProfileField(detailsGrid, "SRN / Roll No:", s.srn);
        addProfileField(detailsGrid, "Engineering Branch:", s.branch);
        addProfileField(detailsGrid, "College Email:", s.email);
        addProfileField(detailsGrid, "Phone Number:", s.phone);
        addProfileField(detailsGrid, "Current CGPA:", s.cgpa);
        addProfileField(detailsGrid, "Academic Score %:", s.marks);
        addProfileField(detailsGrid, "Attached Resume:", s.resume.isEmpty() ? "No resume attached" : new File(s.resume).getName());

        JPanel bottomActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        bottomActions.setOpaque(false);

        JButton btnOpenResume = createPrimaryButton("📄  View / Open Resume");
        JButton btnUpdateResume = createNavyButton("Choose Resume...");

        btnOpenResume.addActionListener(e -> {
            if (s.resume == null || s.resume.trim().isEmpty()) {
                showToast(parentFrame, "No resume attached to this profile.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            File rf = new File(s.resume);
            if (rf.exists() && Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(rf);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Resume Path:\n" + s.resume, "Resume Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Resume File Path:\n" + s.resume + "\n\n(Local disk path)", "Resume Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnUpdateResume.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int res = chooser.showOpenDialog(parentFrame);
            if (res == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                s.resume = f.getAbsolutePath();
                showToast(parentFrame, "Resume updated successfully!", JOptionPane.INFORMATION_MESSAGE);
                refreshStudentProfile(p, s, parentFrame);
            }
        });

        bottomActions.add(btnOpenResume);
        bottomActions.add(btnUpdateResume);

        card.add(detailsGrid, BorderLayout.CENTER);
        card.add(bottomActions, BorderLayout.SOUTH);

        p.add(card, BorderLayout.CENTER);
        return p;
    }

    private static void refreshStudentProfile(JPanel panel, Student s, JFrame parentFrame) {
        panel.removeAll();
        JPanel updated = createStudentProfilePanel(s, parentFrame);
        panel.setLayout(new BorderLayout());
        panel.add(updated, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private static void addProfileField(JPanel grid, String label, String value) {
        JLabel l = new JLabel(label);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_MUTED);

        JLabel v = new JLabel(value);
        v.setFont(FONT_HEADER);
        v.setForeground(TEXT_DARK);

        grid.add(l);
        grid.add(v);
    }

    // ==========================================
    // ADMIN DASHBOARD & PLACEMENT CONSOLE
    // ==========================================
    public static void openAdminDashboard() {
        JFrame f = new JFrame("CampusHire - Placement Cell Administration Console");
        f.setSize(1200, 720);
        f.setMinimumSize(new Dimension(1000, 620));
        f.setLocationRelativeTo(mainFrame);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new BorderLayout());

        // Header
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(NAVY_DARK);
        topHeader.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel logo = new JLabel("🛡️ CampusHire  |  Placement Cell Administration Console");
        logo.setFont(FONT_HEADER);
        logo.setForeground(Color.WHITE);

        JButton btnLogout = createDangerButton("Logout");
        btnLogout.setFont(FONT_SMALL);
        btnLogout.addActionListener(e -> {
            f.dispose();
            showWelcomeWindow();
        });

        topHeader.add(logo, BorderLayout.WEST);
        topHeader.add(btnLogout, BorderLayout.EAST);

        // Body Container
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(BG_MAIN);
        body.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Metrics Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setPreferredSize(new Dimension(0, 95));

        JLabel lblTotalApps = new JLabel("0");
        JLabel lblPendingApps = new JLabel("0");
        JLabel lblSelectedApps = new JLabel("0");
        JLabel lblConfirmedApps = new JLabel("0");

        statsRow.add(createAdminMetricCard("Total Applicants", lblTotalApps, "All Active Drives", PRIMARY_BLUE));
        statsRow.add(createAdminMetricCard("Pending Reviews", lblPendingApps, "Action Needed", WARNING_AMBER));
        statsRow.add(createAdminMetricCard("Selected for Interview", lblSelectedApps, "Shortlisted", ACCENT_GREEN));
        statsRow.add(createAdminMetricCard("Confirmed Interviews", lblConfirmedApps, "Ready for Calls", NAVY_DARK));

        // Control & Search Bar
        JPanel controlBar = new JPanel(new BorderLayout(15, 0));
        controlBar.setOpaque(false);

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBox.setOpaque(false);

        JLabel lblSearch = new JLabel("🔍 Search Applicant:");
        lblSearch.setFont(FONT_LABEL);
        lblSearch.setForeground(TEXT_DARK);

        JTextField filterText = createStyledTextField();
        filterText.setPreferredSize(new Dimension(180, 38));

        JLabel lblStatus = new JLabel("Status Filter:");
        lblStatus.setFont(FONT_LABEL);
        lblStatus.setForeground(TEXT_DARK);

        String[] statusOptions = {"All Statuses", "Applied", "Shortlisted", "Rejected", "SELECTED FOR INTERVIEW", "Interview Confirmed", "Interview Declined"};
        JComboBox<String> statusFilter = new JComboBox<>(statusOptions);
        statusFilter.setFont(FONT_INPUT);
        statusFilter.setBackground(Color.WHITE);
        statusFilter.setBorder(new LineBorder(BORDER_INPUT, 2, false));
        statusFilter.setPreferredSize(new Dimension(190, 38));

        searchBox.add(lblSearch);
        searchBox.add(filterText);
        searchBox.add(lblStatus);
        searchBox.add(statusFilter);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBtns.setOpaque(false);

        JButton btnSelectInterview = createSuccessButton("🎯 Select for Interview");
        JButton btnReject = createDangerButton("✗ Reject Applicant");
        JButton btnViewDossier = createPrimaryButton("👤 View Profile & Resume");
        JButton btnAddCompany = createNavyButton("➕ Add Company Drive");
        JButton btnRefresh = createSecondaryButton("🔄 Refresh");

        actionBtns.add(btnSelectInterview);
        actionBtns.add(btnReject);
        actionBtns.add(btnViewDossier);
        actionBtns.add(btnAddCompany);
        actionBtns.add(btnRefresh);

        controlBar.add(searchBox, BorderLayout.WEST);
        controlBar.add(actionBtns, BorderLayout.EAST);

        // Main Table (Fulfills exact Admin Dashboard columns)
        String[] colNames = {"#", "Student Name", "Username", "Email", "CGPA", "Applied Company", "Job Role", "Application Status", "Interview Status"};
        DefaultTableModel tableModel = new DefaultTableModel(colNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(tableModel);
        styleStandardTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Adjust column preferred widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(140);
        table.getColumnModel().getColumn(6).setPreferredWidth(140);
        table.getColumnModel().getColumn(7).setPreferredWidth(120);
        table.getColumnModel().getColumn(8).setPreferredWidth(180);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        Runnable refreshStatsAndTable = () -> {
            tableModel.setRowCount(0);
            int total = applications.size();
            int pending = 0;
            int selected = 0;
            int confirmed = 0;

            int idx = 1;
            for (Application a : applications) {
                if (a.interviewStatus.equals("SELECTED FOR INTERVIEW")) selected++;
                else if (a.interviewStatus.equals("Interview Confirmed")) confirmed++;
                else if (a.applicationStatus.equals("Applied")) pending++;

                tableModel.addRow(new Object[]{
                        idx++,
                        a.student.name,
                        a.student.username,
                        a.student.email,
                        a.student.cgpa,
                        a.company.name,
                        a.company.role,
                        a.applicationStatus,
                        a.interviewStatus
                });
            }

            lblTotalApps.setText(String.valueOf(total));
            lblPendingApps.setText(String.valueOf(pending));
            lblSelectedApps.setText(String.valueOf(selected));
            lblConfirmedApps.setText(String.valueOf(confirmed));
        };

        refreshStatsAndTable.run();

        // Filtering
        Runnable applyFilters = () -> {
            String query = filterText.getText().trim();
            String status = (String) statusFilter.getSelectedItem();

            ArrayList<RowFilter<Object, Object>> filters = new ArrayList<>();
            if (!query.isEmpty()) {
                filters.add(RowFilter.regexFilter("(?i)" + query));
            }
            if (status != null && !status.equals("All Statuses")) {
                filters.add(RowFilter.regexFilter("(?i)" + status));
            }

            if (filters.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filters));
            }
        };

        filterText.addCaretListener(e -> applyFilters.run());
        statusFilter.addActionListener(e -> applyFilters.run());
        btnRefresh.addActionListener(e -> refreshStatsAndTable.run());

        // Admin Action: Select Student for Interview
        btnSelectInterview.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                showToast(f, "Please select an applicant from the table first.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            Application app = applications.get(modelRow);
            app.applicationStatus = "Shortlisted";
            app.interviewStatus = "SELECTED FOR INTERVIEW";
            refreshStatsAndTable.run();
            showToast(f, "Student " + app.student.name + " SELECTED FOR INTERVIEW with " + app.company.name + "! Notification updated.", JOptionPane.INFORMATION_MESSAGE);
        });

        // Admin Action: Reject Application
        btnReject.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                showToast(f, "Please select an applicant from the table first.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            Application app = applications.get(modelRow);
            app.applicationStatus = "Rejected";
            app.interviewStatus = "Not Selected";
            refreshStatsAndTable.run();
            showToast(f, "Student " + app.student.name + " application status set to REJECTED for " + app.company.name + ".", JOptionPane.INFORMATION_MESSAGE);
        });

        // Admin Action: View Full Dossier & Resume
        btnViewDossier.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                showToast(f, "Please select an applicant from the table to view their dossier.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            Application app = applications.get(modelRow);
            showApplicantDossierDialog(f, app, refreshStatsAndTable);
        });

        // Admin Action: Post New Company Recruitment Drive
        btnAddCompany.addActionListener(e -> showAddCompanyDialog(f));

        // Assemble Layout
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.add(statsRow);
        topContainer.add(Box.createVerticalStrut(16));
        topContainer.add(controlBar);

        body.add(topContainer, BorderLayout.NORTH);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new LineBorder(BORDER_CARD, 2, false));
        body.add(tableScroll, BorderLayout.CENTER);

        f.add(topHeader, BorderLayout.NORTH);
        f.add(body, BorderLayout.CENTER);
        f.setVisible(true);
    }

    private static JPanel createAdminMetricCard(String title, JLabel valLabel, String sub, Color accent) {
        JPanel card = createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title);
        t.setFont(FONT_LABEL);
        t.setForeground(TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valLabel.setForeground(accent);

        JLabel s = new JLabel(sub);
        s.setFont(FONT_SMALL);
        s.setForeground(TEXT_MUTED);

        card.add(t);
        card.add(Box.createVerticalStrut(4));
        card.add(valLabel);
        card.add(Box.createVerticalStrut(2));
        card.add(s);
        return card;
    }

    // Modal: Full Candidate Profile & Resume Inspection
    private static void showApplicantDossierDialog(JFrame parent, Application app, Runnable onUpdate) {
        JDialog dlg = new JDialog(parent, "Applicant Dossier - " + app.student.name, true);
        dlg.setSize(620, 560);
        dlg.setLocationRelativeTo(parent);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(CARD_BG);
        p.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel nameLbl = new JLabel(app.student.name + " (" + app.student.srn + ")");
        nameLbl.setFont(FONT_TITLE);
        nameLbl.setForeground(TEXT_DARK);

        JLabel compLbl = new JLabel("Applying for: " + app.company.name + " • " + app.company.role + " (" + app.company.packageLpa + " LPA)");
        compLbl.setFont(FONT_HEADER);
        compLbl.setForeground(PRIMARY_BLUE);

        header.add(nameLbl, BorderLayout.NORTH);
        header.add(compLbl, BorderLayout.SOUTH);
        p.add(header, BorderLayout.NORTH);

        // Details Grid
        JPanel grid = new JPanel(new GridLayout(7, 2, 10, 10));
        grid.setOpaque(false);

        addProfileField(grid, "Branch:", app.student.branch);
        addProfileField(grid, "Username:", app.student.username);
        addProfileField(grid, "Email:", app.student.email);
        addProfileField(grid, "Phone:", app.student.phone);
        addProfileField(grid, "Current CGPA:", app.student.cgpa);
        addProfileField(grid, "Application Status:", app.applicationStatus);
        addProfileField(grid, "Interview Status:", app.interviewStatus);

        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        centerBox.setOpaque(false);
        centerBox.add(grid);
        centerBox.add(Box.createVerticalStrut(14));

        // Resume Inspector Box
        JPanel resumeBox = createCardPanel();
        resumeBox.setBackground(new Color(241, 245, 249));
        resumeBox.setLayout(new BorderLayout(10, 0));

        JLabel rPathLbl = new JLabel("<html><b>Resume:</b> " + (app.student.resume.isEmpty() ? "No resume attached" : new File(app.student.resume).getName()) + "</html>");
        rPathLbl.setFont(FONT_SMALL);

        JButton btnOpen = createPrimaryButton("Open Resume File");
        btnOpen.setFont(FONT_SMALL);
        btnOpen.addActionListener(e -> {
            if (app.student.resume == null || app.student.resume.trim().isEmpty()) {
                showToast(dlg, "No resume file attached.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            File f = new File(app.student.resume);
            if (f.exists() && Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(f);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dlg, "Resume Path: " + app.student.resume, "Resume Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dlg, "Resume File Path:\n" + app.student.resume + "\n\n(Local disk path)", "Resume Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        resumeBox.add(rPathLbl, BorderLayout.CENTER);
        resumeBox.add(btnOpen, BorderLayout.EAST);
        centerBox.add(resumeBox);

        p.add(centerBox, BorderLayout.CENTER);

        // Bottom Action Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton btnSelect = createSuccessButton("🎯 Select for Interview");
        JButton btnReject = createDangerButton("✗ Reject");
        JButton btnClose = createSecondaryButton("Close");

        btnSelect.addActionListener(e -> {
            app.applicationStatus = "Shortlisted";
            app.interviewStatus = "SELECTED FOR INTERVIEW";
            onUpdate.run();
            dlg.dispose();
            showToast(parent, "Student selected for interview! Notification sent.", JOptionPane.INFORMATION_MESSAGE);
        });

        btnReject.addActionListener(e -> {
            app.applicationStatus = "Rejected";
            app.interviewStatus = "Not Selected";
            onUpdate.run();
            dlg.dispose();
            showToast(parent, "Candidate application rejected.", JOptionPane.INFORMATION_MESSAGE);
        });

        btnClose.addActionListener(e -> dlg.dispose());

        btnRow.add(btnSelect);
        btnRow.add(btnReject);
        btnRow.add(btnClose);
        p.add(btnRow, BorderLayout.SOUTH);

        dlg.add(p);
        dlg.setVisible(true);
    }

    // Modal: Add New Company Recruitment Drive
    private static void showAddCompanyDialog(JFrame parent) {
        JDialog dlg = new JDialog(parent, "Post New Recruitment Drive", true);
        dlg.setSize(520, 520);
        dlg.setLocationRelativeTo(parent);
        dlg.setLayout(new BorderLayout());

        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(CARD_BG);
        p.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel title = new JLabel("New Company Recruitment Drive");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_DARK);
        p.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 12));
        form.setOpaque(false);

        JTextField nameFld = createStyledTextField();
        JTextField roleFld = createStyledTextField();
        JTextField pkgFld  = createStyledTextField();
        JTextField cgpaFld = createStyledTextField();
        JTextField locFld  = createStyledTextField();
        JTextField descFld = createStyledTextField();

        form.add(createFieldLabel("Company Name *"));
        form.add(nameFld);
        form.add(createFieldLabel("Job Role *"));
        form.add(roleFld);
        form.add(createFieldLabel("Package (LPA) *"));
        form.add(pkgFld);
        form.add(createFieldLabel("Min CGPA Req *"));
        form.add(cgpaFld);
        form.add(createFieldLabel("Location"));
        form.add(locFld);
        form.add(createFieldLabel("Description"));
        form.add(descFld);

        p.add(form, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton btnSave = createSuccessButton("Save & Post Drive");
        JButton btnCancel = createSecondaryButton("Cancel");

        btnSave.addActionListener(e -> {
            String name = nameFld.getText().trim();
            String role = roleFld.getText().trim();
            String pkgStr = pkgFld.getText().trim();
            String cgpaStr = cgpaFld.getText().trim();
            String loc = locFld.getText().trim();
            String desc = descFld.getText().trim();

            if (name.isEmpty() || role.isEmpty() || pkgStr.isEmpty() || cgpaStr.isEmpty()) {
                showToast(dlg, "Please fill in all mandatory fields (*).", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double pkg = Double.parseDouble(pkgStr);
                double minCgpa = Double.parseDouble(cgpaStr);
                companies.add(new Company(name, role, pkg, minCgpa, loc.isEmpty() ? "Bangalore, IN" : loc, desc.isEmpty() ? "Campus Recruitment Drive" : desc));
                showToast(parent, "Recruitment drive for " + name + " posted successfully!", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } catch (Exception ex) {
                showToast(dlg, "Please enter valid numeric values for Package and Min CGPA.", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dlg.dispose());

        btnRow.add(btnCancel);
        btnRow.add(btnSave);
        p.add(btnRow, BorderLayout.SOUTH);

        dlg.add(p);
        dlg.setVisible(true);
    }

    // ==========================================
    // JTABLE STYLING & STATUS CELL RENDERERS
    // ==========================================
    private static void styleStandardTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(38);
        table.setShowGrid(true);
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_DARK);
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_DARK);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_LABEL);
        header.setBackground(NAVY_SIDEBAR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setFont(FONT_LABEL);

                if (value != null) {
                    String str = value.toString();
                    if (str.equalsIgnoreCase("SELECTED FOR INTERVIEW") || str.equalsIgnoreCase("Shortlisted")) {
                        lbl.setForeground(ACCENT_GREEN);
                    } else if (str.equalsIgnoreCase("Interview Confirmed")) {
                        lbl.setForeground(PRIMARY_BLUE);
                    } else if (str.equalsIgnoreCase("Rejected") || str.equalsIgnoreCase("Not Selected")) {
                        lbl.setForeground(DANGER_RED);
                    } else if (str.equalsIgnoreCase("Interview Declined")) {
                        lbl.setForeground(TEXT_MUTED);
                    } else {
                        lbl.setForeground(WARNING_AMBER);
                    }
                }
                return lbl;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnName(i).toLowerCase().contains("status")) {
                table.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }
}