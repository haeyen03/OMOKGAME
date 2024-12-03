package window;

import gameClient.ClientLobby;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


public class LoginForm extends JFrame {
    private static final long serialVersionUID = 0L;

    private Container cp = this.getContentPane();

    private JPanel panel;
    private JTextField inputId;
    private JPasswordField inputPass;
    private JButton loginButton;
    private JLabel idLabel;
    private JLabel passwordLabel;

    public LoginForm() {
        // Create panel without any image
        panel = new JPanel();
        panel.setLayout(null);

        // Create labels, text fields and buttons
        idLabel = new JLabel("ID:");
        passwordLabel = new JLabel("Password:");
        inputId = new JTextField(7);
        inputPass = new JPasswordField(7);
        loginButton = new JButton("Login");

        // Set bounds for the components
        idLabel.setBounds(130, 120, 100, 25);
        passwordLabel.setBounds(130, 160, 100, 25);
        inputId.setBounds(200, 120, 150, 25);
        inputPass.setBounds(200, 160, 150, 25);
        loginButton.setBounds(200, 200, 150, 30);

        // Add components to the panel
        panel.add(idLabel);
        panel.add(passwordLabel);
        panel.add(inputId);
        panel.add(inputPass);
        panel.add(loginButton);

        // Add action listener to the login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!inputId.getText().equals("") && inputPass.getPassword().length > 0) {
                    new ClientLobby(inputId.getText());
                    setVisible(false);
                } else {
                    // Show error if input is incorrect
                    JOptionPane.showMessageDialog(null, "Please enter valid ID and Password.");
                }
            }
        });

        // Set preferred size for the panel
        panel.setPreferredSize(new java.awt.Dimension(500, 300));

        // Add the panel to the frame
        cp.add(panel);

        // Set frame size and make it visible
        setSize(500, 300);
        setVisible(true);

        // Close the application when the window is closed
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}
