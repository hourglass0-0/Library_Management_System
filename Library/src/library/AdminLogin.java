package library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminLogin extends JFrame implements ActionListener {
    private JTextField adminIdField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;
    private Connection connection;

    public AdminLogin() {
        setTitle("管理员登录");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        JLabel adminIdLabel = new JLabel("管理员编号:");
        adminIdField = new JTextField();
        JLabel passwordLabel = new JLabel("密码:");
        passwordField = new JPasswordField();
        loginButton = new JButton("登录");
        loginButton.addActionListener(this);
        messageLabel = new JLabel("");

        add(adminIdLabel);
        add(adminIdField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(messageLabel);
        setLocationRelativeTo(null);
        setVisible(true);

        // 连接数据库
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library?serverTimezone=Asia/Shanghai", "root", "204416");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库连接失败！");
            System.exit(0);
        }
    }

    public void actionPerformed(ActionEvent e) {
        String adminId = adminIdField.getText();
        String password = new String(passwordField.getPassword());

        // 检查用户名和密码是否正确
        if (isAdminValid(adminId, password)) {
            JOptionPane.showMessageDialog(this, "登录成功！");
            // 检查是否首次登录，如果是则提示修改密码
            if (isFirstLogin(adminId)) {
                JOptionPane.showMessageDialog(this, "首次登录，请修改密码！");
                // 跳转到修改密码界面
                dispose();
                implementChangePassword(adminId);
            } else {
                // 跳转到主界面
                new LibraryManagementSystem().setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误，请重试！");
        }
    }

    // 检查用户名和密码是否正确
    private boolean isAdminValid(String adminId, String password) {
        try {
            // 查询数据库中管理员的密码
            String query = "SELECT password FROM manager WHERE manager_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, adminId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String dbPassword = resultSet.getString("password");
                return password.equals(dbPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库查询失败！");
        }
        return false;
    }

    // 检查是否首次登录
    private boolean isFirstLogin(String adminId) {
        try {
            // 查询数据库中管理员的密码
            String query = "SELECT password FROM manager WHERE manager_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, adminId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String password = resultSet.getString("password");
                return password.equals("123456");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库查询失败！");
        }
        return false;
    }


    public static void main(String[] args) {
        new AdminLogin();
    }

    // 实现修改密码界面
    private void implementChangePassword(String adminId) {
        JFrame changePasswordFrame = new JFrame("修改密码");
        changePasswordFrame.setSize(300, 200);
        changePasswordFrame.setLayout(new GridLayout(5, 2)); // 修改为5行2列的布局

        JLabel newPasswordLabel = new JLabel("新密码:");
        JPasswordField newPasswordField = new JPasswordField();
        JLabel confirmPasswordLabel = new JLabel("确认密码:");
        JPasswordField confirmPasswordField = new JPasswordField();
        JButton changePasswordButton = new JButton("确认修改");
        JLabel changePasswordMessageLabel = new JLabel("");

        changePasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newPassword = new String(newPasswordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (newPassword.equals("123456")) {
                    JOptionPane.showMessageDialog(changePasswordFrame, "新密码不能为123456，请重新输入！");
                    return;
                }

                if (newPassword.equals(confirmPassword)) {
                    // 更新密码到数据库
                    try {
                        String updateQuery = "UPDATE manager SET password = ? WHERE manager_id = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setString(1, newPassword);
                        updateStatement.setString(2, adminId);
                        int rowsAffected = updateStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(changePasswordFrame, "密码修改成功！");
                            changePasswordFrame.dispose(); // 关闭修改密码界面
                            new LibraryManagementSystem().setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(changePasswordFrame, "密码修改失败，请重试！");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(changePasswordFrame, "密码修改失败，请重试！");
                    }
                } else {
                    JOptionPane.showMessageDialog(changePasswordFrame, "新密码和确认密码不匹配，请重新输入！");
                }
            }
        });
        // 将组件添加到修改密码界面的布局中
        changePasswordFrame.add(newPasswordLabel);
        changePasswordFrame.add(newPasswordField);
        changePasswordFrame.add(confirmPasswordLabel);
        changePasswordFrame.add(confirmPasswordField);
        changePasswordFrame.add(changePasswordButton);
        changePasswordFrame.add(changePasswordMessageLabel);

        // 设置窗体为居中显示
        changePasswordFrame.setLocationRelativeTo(null);
        // 设置窗体为可见
        changePasswordFrame.setVisible(true);
    }

}

