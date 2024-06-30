package library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StatisticsPanel extends JPanel implements ActionListener {
    private Connection connection;
    private JTextField readerNumberField;
    private JTextField categoryField;
    private JTextField bookNumberField;
    private JTextArea displayArea;

    public StatisticsPanel(Connection connection) {
        this.connection = connection;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("读者编号:"), gbc);
        readerNumberField = new JTextField(15);
        gbc.gridx = 1;
        inputPanel.add(readerNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("图书类别:"), gbc);
        categoryField = new JTextField(15);
        gbc.gridx = 1;
        inputPanel.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("书号:"), gbc);
        bookNumberField = new JTextField(15);
        gbc.gridx = 1;
        inputPanel.add(bookNumberField, gbc);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton readerBorrowCountButton = new JButton("统计读者借书数量");
        readerBorrowCountButton.addActionListener(this);
        buttonPanel.add(readerBorrowCountButton);

        JButton categoryTotalCountButton = new JButton("统计类别总藏书量");
        categoryTotalCountButton.addActionListener(this);
        buttonPanel.add(categoryTotalCountButton);

        JButton bookBorrowCountButton = new JButton("统计书籍借阅量");
        bookBorrowCountButton.addActionListener(this);
        buttonPanel.add(bookBorrowCountButton);

        // 将输入面板和按钮面板添加到主面板的顶部
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.add(inputPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        // 创建显示区域
        displayArea = new JTextArea(15, 50);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        displayArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("统计结果"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("统计读者借书数量")) {
            String readerNumber = readerNumberField.getText().trim();
            countReaderBorrows(readerNumber);
        } else if (command.equals("统计类别总藏书量")) {
            String category = categoryField.getText().trim();
            countCategoryTotalBooks(category);
        } else if (command.equals("统计书籍借阅量")) {
            String bookNumber = bookNumberField.getText().trim();
            countBookBorrows(bookNumber);
        }
    }

    private void countReaderBorrows(String readerNumber) {
        try {
            String query = "SELECT COUNT(*) FROM borrow_book WHERE reader_number = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, readerNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                displayArea.setText("读者编号 " + readerNumber + " 的借书数量: " + count);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "统计读者借书数量失败！");
        }
    }

    private void countCategoryTotalBooks(String category) {
        try {
            String query = "SELECT SUM(book_total) FROM book WHERE category = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, category);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int total = resultSet.getInt(1);
                displayArea.setText("图书类别 " + category + " 的总藏书量: " + total);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "统计类别总藏书量失败！");
        }
    }

    private void countBookBorrows(String bookNumber) {
        try {
            String query = "SELECT COUNT(*) FROM borrow_book WHERE book_number = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, bookNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                displayArea.setText("书号 " + bookNumber + " 的借阅量: " + count);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "统计书籍借阅量失败！");
        }
    }
}
