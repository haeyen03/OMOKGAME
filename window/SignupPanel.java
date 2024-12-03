package window;

import java.awt.*;
import javax.swing.*;

public class SignupPanel extends JFrame {

    private JPanel mainPanel;
    private JPanel subPanel;
    private JButton SignupButton;
    private JButton CancelButton;
    private JButton AgainButton;

    private JLabel nameLabel;
    private JLabel idLabel;
    private JLabel aliasLabel;
    private JLabel passLabel;
    private JLabel passReLabel;

    private JTextField nameTf;
    private JTextField idTf;
    private JTextField aliasTf;
    private JPasswordField passTf;
    private JPasswordField passReTf;

    public SignupPanel() {
        setTitle("회원가입");
        setSize(615, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Main Panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Sub Panel
        subPanel = new JPanel(new GridBagLayout());
        subPanel.setBorder(BorderFactory.createTitledBorder("회원 정보 입력"));

        GridBagConstraints gbc;

        // Labels and TextFields
        nameLabel = new JLabel("이름:");
        idLabel = new JLabel("아이디:");
        aliasLabel = new JLabel("닉네임:");
        passLabel = new JLabel("패스워드:");
        passReLabel = new JLabel("패스워드 확인:");

        nameTf = new JTextField(15);
        idTf = new JTextField(15);
        aliasTf = new JTextField(15);
        passTf = new JPasswordField(15);
        passReTf = new JPasswordField(15);

        // Name
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        subPanel.add(nameLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        subPanel.add(nameTf, gbc);

        // ID
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        subPanel.add(idLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        subPanel.add(idTf, gbc);

        // Alias
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        subPanel.add(aliasLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        subPanel.add(aliasTf, gbc);

        // Password
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        subPanel.add(passLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        subPanel.add(passTf, gbc);

        // Password Confirmation
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        subPanel.add(passReLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        subPanel.add(passReTf, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        SignupButton = new JButton("회원가입");
        CancelButton = new JButton("취소");
        AgainButton = new JButton("다시 입력");
        buttonPanel.add(SignupButton);
        buttonPanel.add(CancelButton);
        buttonPanel.add(AgainButton);

        // Add panels to main panel
        mainPanel.add(subPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        getContentPane().add(mainPanel);

        // Action Listeners
        SignupButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "회원가입 완료"));
        CancelButton.addActionListener(e -> dispose());
        AgainButton.addActionListener(e -> resetFields());
    }

    private void resetFields() {
        nameTf.setText("");
        idTf.setText("");
        aliasTf.setText("");
        passTf.setText("");
        passReTf.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SignupPanel gui = new SignupPanel();
            gui.setVisible(true);
        });
    }
}
