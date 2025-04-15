import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SimpleATM extends JFrame implements ActionListener {
    private CardLayout cardLayout;
    private JPanel mainPanel, loginPanel, atmPanel;
    private JTextField userField;
    private JPasswordField passField;
    private JLabel statusLabel, balanceLabel;
    private double balance = 1000.0;
    private String pinSK = "2522";
    private String pinShelly = "2525";
    private String currentUser = "";
    private ArrayList<String> transactionHistory = new ArrayList<>();

    public SimpleATM() {
        setTitle("BCA BANK IND");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        Font labelFont = new Font("Arial", Font.BOLD, 20);
        Font inputFont = new Font("Arial", Font.PLAIN, 18);
        Font titleFont = new Font("Arial", Font.BOLD, 32);
        Color darkRed = new Color(139, 0, 0);

        // Login Panel
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("BCA BANK IND");
        title.setFont(titleFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(title, gbc);

        JLabel userLabel = new JLabel("USERNAME:");
        userLabel.setFont(labelFont);
        userLabel.setForeground(darkRed);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(userLabel, gbc);

        userField = new JTextField(15);
        userField.setFont(inputFont);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(userField, gbc);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setFont(labelFont);
        pinLabel.setForeground(darkRed);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(pinLabel, gbc);

        passField = new JPasswordField(15);
        passField.setFont(inputFont);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(passField, gbc);

        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(labelFont);
        loginButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);

        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(Color.RED);
        gbc.gridy = 4;
        loginPanel.add(statusLabel, gbc);

        atmPanel = new JPanel(new GridLayout(8, 1, 10, 10));
        atmPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        balanceLabel = new JLabel("Balance: ₹" + balance);
        balanceLabel.setFont(labelFont);
        atmPanel.add(balanceLabel);

        addButton("Deposit", e -> verifyPinThen(this::deposit));
        addButton("Withdraw", e -> verifyPinThen(this::withdraw));
        addButton("Send Money", e -> verifyPinThen(this::sendMoney));
        addButton("Check Balance", e -> verifyPinThen(this::updateBalance));
        addButton("Transaction History", e -> showTransactionHistory());
        addButton("Change PIN", e -> changePin());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(labelFont);
        logoutBtn.addActionListener(e -> {
            userField.setText("");
            passField.setText("");
            cardLayout.show(mainPanel, "login");
        });
        atmPanel.add(logoutBtn);

        mainPanel.add(loginPanel, "login");
        mainPanel.add(atmPanel, "atm");

        add(mainPanel);
        setVisible(true);
    }

    private void addButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.addActionListener(action);
        atmPanel.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String enteredUser = userField.getText().trim().toLowerCase();
        String enteredPin = new String(passField.getPassword());

        if ((enteredUser.equals("sk") && enteredPin.equals(pinSK)) ||
                (enteredUser.equals("shelly") && enteredPin.equals(pinShelly))) {
            currentUser = enteredUser;
            updateBalance();
            cardLayout.show(mainPanel, "atm");
            statusLabel.setText("");
        } else {
            statusLabel.setText("Invalid login!");
        }
    }

    private void verifyPinThen(Runnable action) {
        String enteredPin = JOptionPane.showInputDialog(this, "Enter PIN to continue:");
        if ((currentUser.equals("sk") && enteredPin.equals(pinSK)) ||
                (currentUser.equals("shelly") && enteredPin.equals(pinShelly))) {
            action.run();
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect PIN!");
        }
    }

    private void updateBalance() {
        balanceLabel.setText("Balance: ₹" + String.format("%.2f", balance));
    }

    private void deposit() {
        String input = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
        try {
            double amount = Double.parseDouble(input);
            if (amount > 0) {
                balance += amount;
                transactionHistory.add("Deposited: ₹" + amount);
                updateBalance();
                JOptionPane.showMessageDialog(this, "Deposited ₹" + amount);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input!");
        }
    }

    private void withdraw() {
        String input = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
        try {
            double amount = Double.parseDouble(input);
            if (amount > 0 && amount <= balance) {
                balance -= amount;
                transactionHistory.add("Withdrew: ₹" + amount);
                updateBalance();
                JOptionPane.showMessageDialog(this, "Withdrew ₹" + amount);
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient funds or invalid input!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input!");
        }
    }

    private void sendMoney() {
        String recipient = JOptionPane.showInputDialog(this, "Enter recipient name:");
        if (recipient == null || recipient.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Recipient name cannot be empty.");
            return;
        }

        String amountStr = JOptionPane.showInputDialog(this, "Enter amount to send:");
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount > 0 && amount <= balance) {
                balance -= amount;
                transactionHistory.add("Sent ₹" + amount + " to " + recipient);
                updateBalance();
                JOptionPane.showMessageDialog(this, "Sent ₹" + amount + " to " + recipient);
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient funds or invalid amount.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input!");
        }
    }

    private void showTransactionHistory() {
        if (transactionHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No transactions yet.");
        } else {
            StringBuilder history = new StringBuilder("Transaction History:\n");
            for (String t : transactionHistory) {
                history.append(t).append("\n");
            }
            JOptionPane.showMessageDialog(this, history.toString());
        }
    }

    private void changePin() {
        String oldPin = JOptionPane.showInputDialog(this, "Enter current PIN:");
        if ((currentUser.equals("sk") && oldPin.equals(pinSK)) ||
                (currentUser.equals("shelly") && oldPin.equals(pinShelly))) {

            String newPin = JOptionPane.showInputDialog(this, "Enter new PIN:");
            if (newPin != null && newPin.length() >= 4) {
                if (currentUser.equals("sk")) {
                    pinSK = newPin;
                } else {
                    pinShelly = newPin;
                }
                JOptionPane.showMessageDialog(this, "PIN changed successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "PIN must be at least 4 digits.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect current PIN.");
        }
    }

    public static void main(String[] args) {
        new SimpleATM();
    }
}