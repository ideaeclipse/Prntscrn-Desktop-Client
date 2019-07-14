package com.minghao;


import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class is used to make the user login
 *
 * @author {CompanyName}
 */
class AuthenticationFrame extends JFrame {
    /**
     * Instance of authentication panel, to allow for synchronized code execution see {@link AuthenticationFrame#getToken()}
     */
    private final AuthenticationPanel frame;

    /**
     * Instance of an errorFrame
     */
    private final ErrorFrame errorFrame;

    /**
     * Web token, allows for web requests
     */
    private String token;

    /**
     * @param errorFrame passed instance of the errorFrame
     */
    AuthenticationFrame(final ErrorFrame errorFrame) {
        // Frame information
        super("Login");
        this.errorFrame = errorFrame;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(500, 350);
        setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);
        setResizable(false);

        // JPanel
        frame = new AuthenticationPanel(this);
        add(frame);
        setVisible(true);
    }

    /**
     * This function waits for the notification from the submission if the token is null
     * else it just returns the token
     *
     * @return token gather via a web request
     */
    String getToken() {
        if (token == null)
            try {
                synchronized (frame) {
                    frame.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        return token;
    }


    /**
     * This class displays the input boxes for the user to enter their username and password
     *
     * @author {CompanyName}
     */
    private final class AuthenticationPanel extends JPanel {

        /**
         * Button to handle submit
         */
        private final JButton submit;

        /**
         * Text fields to enter the userName and password
         */
        private final JTextField userName, password;

        /**
         * @param parent parent frame instance
         */
        AuthenticationPanel(final JFrame parent) {
            // Panel information
            this.setLayout(null);
            this.setBackground(Color.LIGHT_GRAY);

            // Icon
            JLabel icon = new JLabel(new ImageIcon("PrintScreen-Clone.png"));
            icon.setBounds(185, 0, 100, 50);
            this.add(icon);
            this.repaint();
            icon.setVisible(true);

            // Username JTextField
            userName = new JTextField("Username");
            userName.setBounds(25, 125, 450, 35);
            userName.setBorder(BorderFactory.createLineBorder(Color.darkGray));
            userName.setBackground(Color.WHITE);
            userName.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER)
                        submit(parent);
                    if (userName.getText().equals("Username") && e.getKeyCode() != KeyEvent.VK_ENTER)
                        userName.setText("");
                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });
            this.add(userName);

            // Password JTextField
            password = new JTextField("Password");
            password.setBounds(25, 165, 450, 35);
            password.setBorder(BorderFactory.createLineBorder(Color.darkGray));
            password.setBackground(Color.white);
            password.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER)
                        submit(parent);
                    if (userName.getText().equals("Username") && e.getKeyCode() != KeyEvent.VK_ENTER)
                        password.setText("");


                }

                @Override
                public void keyReleased(KeyEvent e) {
                }

            });
            this.add(password);

            // JButton
            submit = new JButton("Login");
            submit.setBounds(375, 205, 100, 35);

            //Notifies the synchronized lock when the token is gathered
            submit.addActionListener(e -> submit(parent));
            this.add(submit);

            // Register button
            JButton register = new JButton("Register");
            register.setBounds(25, 205, 100, 35);
            register.addActionListener(e -> {
                parent.setVisible(false);
                new RegisterFrame(parent, errorFrame);
            });
            this.add(register);
        }

        /**
         * When user submit the information
         *
         * @param parent Parent instance
         */
        void submit(final JFrame parent) {
            try {
                String userName = getUserNameText();
                String password = getPasswordText();
                JSONObject login = new JSONObject();
                login.put("username", userName);
                login.put("password", password);
                HttpRequests con = new HttpRequests();
                token = String.valueOf(new JSONObject(con.sendJson("login", login)).get("token"));
                writeToken();
                parent.dispose();
                synchronized (this) {
                    this.notifyAll();
                }
                new Menu(errorFrame);
            } catch (IOException e1) {
                JLabel invalid = new JLabel("Invalid username and password", JLabel.CENTER);
                invalid.setVisible(false);
                if (!invalid.isVisible()) {
                    invalid.setBounds(130, 75, 200, 50);
                    invalid.setForeground(Color.RED);
                    add(invalid);
                    this.repaint();
                    invalid.setVisible(true);
                    errorFrame.writeError("The user has enter an invalid username and password, if you do not have an username or password", e1, this.getClass());
                }
            }
        }


        /**
         * Write the token to file
         */
        private void writeToken() {
            try {
                PrintWriter printWriter = new PrintWriter(new FileWriter("tokenText.txt"));
                printWriter.print(token);
                printWriter.close();
            } catch (IOException e) {
                errorFrame.writeError("Unable to write token to file, user does not have permission to write to that directory", e, this.getClass());
            }
        }

        /**
         * Get string from username textfield
         *
         * @return Input from username textfield
         */
        private String getUserNameText() {
            return userName.getText();
        }

        /**
         * Get string from password textfield
         *
         * @return Input from password textfield
         */
        private String getPasswordText() {
            return password.getText();
        }
    }
}