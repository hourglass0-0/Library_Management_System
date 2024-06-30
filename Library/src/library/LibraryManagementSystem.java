package library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LibraryManagementSystem extends JFrame implements ActionListener {
    private JTabbedPane tabbedPane;
    private Connection connection;

    public LibraryManagementSystem() {
        setTitle("图书管理系统");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 连接数据库
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library?serverTimezone=Asia/Shanghai", "root", "204416");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库连接失败！");
            System.exit(0);
        }

        tabbedPane = new JTabbedPane();

        // 创建图书管理功能界面
        BookManagementPanel bookManagementPanel = new BookManagementPanel(connection);
        tabbedPane.addTab("图书管理", bookManagementPanel);

        // 创建读者管理功能界面
        ReaderManagementPanel readerManagementPanel = new ReaderManagementPanel(connection);
        tabbedPane.addTab("读者管理",readerManagementPanel);

        // 创建借还书功能界面
        BorrowReturnPanel borrowReturnPanel = new BorrowReturnPanel(connection);
        tabbedPane.addTab("借还书", borrowReturnPanel);

        // 创建统计功能界面
        StatisticsPanel statisticsPanel = new StatisticsPanel(connection);
        tabbedPane.addTab("统计", statisticsPanel);

        add(tabbedPane);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("进入图书管理")) {
            // 切换到图书管理选项卡
            tabbedPane.setSelectedIndex(0);
        } else if (e.getActionCommand().equals("进入读者管理")) {
            // 跳转到读者管理界面
            tabbedPane.setSelectedIndex(1);
        } else if (e.getActionCommand().equals("进入借还书")) {
            // 跳转到借还书界面
            tabbedPane.setSelectedIndex(2);
        } else if (e.getActionCommand().equals("进入统计")) {
            // 跳转到统计界面
            JOptionPane.showMessageDialog(this, "进入统计界面");
        }
    }

    public static void main(String[] args) {
        new LibraryManagementSystem();
    }
}
