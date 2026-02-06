package view;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ViewTheme {
	
	public static final Color BACKGROUND = new Color(0xFFF9F5);
    public static final Color CARD_BG = new Color(0xFFFDFB);
    public static final Color ACCENT = new Color(0xB8860B);   // dark goldenrod
    public static final Color ACCENT_HOVER = new Color(0xCD9B1D);
    public static final Color TEXT = new Color(0x4A3728);
    public static final Color TEXT_MUTED = new Color(0x6B5344);
    public static final Color BORDER = new Color(0xE8D5C4);
    public static final Color INPUT_BG = Color.WHITE;

    // Fontes
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_LABEL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, Font.BOLD, 13);
    
    private ViewTheme() {}
    
    public static JPanel createPanel(int top, int left, int bottom, int right) {
        JPanel p = new JPanel();
        p.setBackground(BACKGROUND);
        p.setBorder(new EmptyBorder(top, left, bottom, right));
        return p;
    }
    
    public static JLabel createTitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_TITLE);
        l.setForeground(TEXT);
        return l;
    }
    
    public static JLabel createSubtitleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SUBTITLE);
        l.setForeground(TEXT_MUTED);
        return l;
    }
    
    public static JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT);
        return l;
    }
    
    public static JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BUTTON);
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(true);
        /*b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_HOVER, 1),
                new EmptyBorder(10, 24, 10, 24)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));*/
        return b;
    }
    
    public static JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BUTTON);
        b.setBackground(CARD_BG);
        b.setForeground(TEXT);
        b.setFocusPainted(false);
        /*b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 20, 10, 20)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));*/
        return b;
    }
    
    public static JTextField createTextField(int columns) {
        JTextField t = new JTextField(columns);
        t.setFont(FONT_LABEL);
        t.setBackground(INPUT_BG);
        /*t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)));*/
        return t;
    }
    
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField p = new JPasswordField(columns);
        p.setFont(FONT_LABEL);
        p.setBackground(INPUT_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)));
        return p;
    }
    
    public static JPanel createSection(String title) {
        JPanel section = new JPanel(new BorderLayout(0, 6));
        section.setBackground(BACKGROUND);
        /*section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(12, 0, 12, 0)));*/
        JLabel label = createFieldLabel(title);
        label.setFont(label.getFont());
        label.setForeground(ACCENT);
        section.add(label, BorderLayout.NORTH);
        return section;
    }


}
