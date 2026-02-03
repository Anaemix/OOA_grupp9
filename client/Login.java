package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;


public class Login {
    private JFrame loginFrame;
    private JPanel panel;
    private JTextField loginField;
    private JButton loginButton;

    public Login() {
        loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginField = new JTextField();
        loginField.setPreferredSize(new Dimension(200, 28));
        loginButton = new JButton("Login");

        panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Username:"));
        panel.add(loginField);
        panel.add(loginButton);

        loginFrame.add(panel);
        loginFrame.pack();
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }
    
    
    public String getLoginText() {
        return (loginField != null) ? loginField.getText() : "";
    }

    public void addLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void addLoginFieldListener(ActionListener listener) {
        loginField.addActionListener(listener);
    }
}
