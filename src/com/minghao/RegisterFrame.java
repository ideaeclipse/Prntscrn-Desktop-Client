package com.minghao;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class RegisterFrame extends JFrame {
    private final ErrorFrame errorFrame;

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

    private final class RegisterPanel extends JPanel {

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
                        if(field.getValue().getText().equals(field.getKey())){
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
            submit.addActionListener(e->{
                JSONObject register = new JSONObject();
                register.put("username", fields.get("Username"));
                register.put("password", fields.get("Password"));
                //register.put("FirstName", fields.get("FirstName"));
                //register.put("LastName", fields.get("LastName"));
                //register.put("Email", fields.get("Email"));
                try {
                    int statusCode = Integer.parseInt(new HttpRequests().sendJson("user", register));
                    if(statusCode == 200){
                        parent.dispose();
                        loginFrame.setVisible(true);
                    }else if(statusCode == 401){
                        JLabel error = new JLabel("The username has already been taken", JLabel.CENTER);
                        error.setLayout(new BorderLayout());
                        error.setBounds(118,90, 350,75);
                        error.setFont(new Font(("Aerial"),Font.PLAIN, 14));
                        error.setForeground(Color.RED);
                        this.repaint();
                        this.add(error);
                        error.setVisible(true);
                        errorFrame.writeError("User has enter a existing username", null, this.getClass());
                    }else{

                    }
                    System.out.println(statusCode);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
            this.add(submit);

        }
    }
}