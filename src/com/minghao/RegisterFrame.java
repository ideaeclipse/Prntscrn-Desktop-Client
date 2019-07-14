package com.minghao;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// TODO: Add LOGO


class RegisterFrame extends JFrame {
    /**
     * Instance of an errorFrame
     */
    private final ErrorFrame errorFrame;

    /**
     *
     * @param loginFrame passed instance of the errorFrame
     * @param errorFrame passed instance of the authenticationFrame
     */
    RegisterFrame(final JFrame loginFrame ,final ErrorFrame errorFrame) {
        super("Register");
        this.errorFrame = errorFrame;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(600, 450);
        setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);
        setResizable(false);

        add(new RegisterPanel(loginFrame, this));
        this.setVisible(true);
    }


    /**
     * This class display the textfield where user has to input their information
     *
     */
    private final class RegisterPanel extends JPanel {

        /**
         *
         * @param loginFrame LoginFrame instance from AuthenticationFrame
         * @param parent     parent instance
         */
        RegisterPanel(final JFrame loginFrame, final JFrame parent) {
            this.setLayout(null);
            this.setBackground(Color.LIGHT_GRAY);

            Map<String, JTextField> fields = new HashMap<>();
            fields.put("Email", new JTextField("Email"));
            fields.put("First name", new JTextField("First name"));
            fields.put("Last name", new JTextField("Last name"));
            fields.put("Username", new JTextField("Username"));
            fields.put("Password", new JTextField("Password"));


            int temp = 0;
            int temp2 = 1;
            for(Map.Entry<String, JTextField> field : fields.entrySet()){
                if(field.getKey().equals("Last name") || field.getKey().equals("First name")){
                    field.getValue().setBounds(75 + 250 * temp++,150, 200,35);
                    field.getValue().setBorder(BorderFactory.createLineBorder(Color.darkGray));
                    field.getValue().setBackground(Color.WHITE);
                }else{
                    field.getValue().setBounds(75, 150 + 50 * temp2 ++, 450, 35);
                    field.getValue().setBorder(BorderFactory.createLineBorder(Color.darkGray));
                    field.getValue().setBackground(Color.WHITE);
                }
                this.add(field.getValue());
                field.getValue().addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {

                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if(e.getKeyCode() == KeyEvent.VK_ENTER)
                            submit(fields, loginFrame, parent);
                        else if(field.getValue().getText().equals(field.getKey()) && e.getKeyCode() != KeyEvent.VK_ENTER){
                            field.getValue().setText("");
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {

                    }
                });
            }

            // Back button to go back to login
            JButton back = new JButton("Back");
            back.setBounds(75, 350, 100,35);
            back.addActionListener(e ->{
                parent.dispose();
                loginFrame.setVisible(true);
            });
            this.add(back);

            // Submit button for registering account
            JButton submit = new JButton("submit");
            submit.setBounds(425, 350, 100,35);
            submit.addActionListener(e-> submit(fields, loginFrame, parent));
            this.add(submit);

        }

        /**
         * User submitting their information]
         *
         * @param fields      HashMap of JTextfield
         * @param loginFrame  LoginFrame instance
         * @param parent      Parent Instance
         */
        void submit(Map<String, JTextField> fields, final JFrame loginFrame, final JFrame parent){
            JSONObject register = new JSONObject();
            register.put("username", fields.get("Username"));
            register.put("password", fields.get("Password"));
            register.put("FirstName", fields.get("FirstName"));
            register.put("LastName", fields.get("LastName"));
            register.put("Email", fields.get("Email"));
            try {
                int statusCode = Integer.parseInt(new HttpRequests().sendJson("user", register));
                JLabel error = new JLabel();
                error.setHorizontalAlignment(JLabel.CENTER);
                error.setVisible(false);
                error.setLayout(new BorderLayout());
                error.setBounds(50,90, 500,75);
                error.setFont(new Font(("Aerial"),Font.PLAIN, 14));
                error.setForeground(Color.RED);
                if(statusCode == 200){
                    parent.dispose();
                    loginFrame.setVisible(true);
                }else if(statusCode == 401){
                    error.setText("The username has already been taken");
                    this.repaint();
                    this.add(error);
                    error.setVisible(true);
                    errorFrame.writeError("User has enter a existing username", null, this.getClass());
                }else if(statusCode == 500){
                    error.setText("Internal server error, API is currently facing issues pleases try again later");
                    errorFrame.writeError("Internal server error, API is currently facing issues pleases try again later", null, this.getClass());
                }else if(statusCode == 400){
                    error.setText("This shouldn't happen!  Please contact support");
                    errorFrame.writeError("Missing parameters passed to the API, contact support please.", null, this.getClass());
                }
                this.repaint();
                this.add(error);
                error.setVisible(true);
            } catch (IOException e1) {
                errorFrame.writeError("API is down, please try again later", e1, this.getClass());

            }
        }

    }
}