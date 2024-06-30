package library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BorrowReturnPanel extends JPanel implements ActionListener {
    private Connection connection;
    private JTextField readerNumberField;
    private JTextField bookNumberField;
    private JTextArea displayArea;

    public BorrowReturnPanel(Connection connection) {
        this.connection = connection;
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("读者编号:"));
        readerNumberField = new JTextField(10);
        inputPanel.add(readerNumberField);

        inputPanel.add(new JLabel("书号:"));
        bookNumberField = new JTextField(10);
        inputPanel.add(bookNumberField);

        JButton borrowButton = new JButton("借书");
        borrowButton.addActionListener(this);
        inputPanel.add(borrowButton);

        JButton returnButton = new JButton("还书");
        returnButton.addActionListener(this);
        inputPanel.add(returnButton);

        add(inputPanel, BorderLayout.NORTH);

        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton queryButton = new JButton("查询借书还书信息");
        queryButton.addActionListener(this);
        add(queryButton, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String readerNumber = readerNumberField.getText().trim();
        String bookNumber = bookNumberField.getText().trim();

        if (command.equals("借书")) {
            borrowBook(readerNumber, bookNumber);
        } else if (command.equals("还书")) {
            returnBook(readerNumber, bookNumber);
        } else if (command.equals("查询借书还书信息")) {
            queryBorrowReturnInfo(readerNumber);
        }
    }

    private void borrowBook(String readerNumber, String bookNumber) {
        try {
            // 检查读者是否已借超过15本书
            String countQuery = "SELECT COUNT(*) FROM borrow_book WHERE reader_number = ?";
            PreparedStatement countStatement = connection.prepareStatement(countQuery);
            countStatement.setString(1, readerNumber);
            ResultSet countResultSet = countStatement.executeQuery();
            if (countResultSet.next() && countResultSet.getInt(1) >= 15) {
                JOptionPane.showMessageDialog(this, "每个读者最多借15本书！");
                return;
            }

            // 插入借书记录
            String borrowQuery = "INSERT INTO borrow_book (reader_number, book_number, borrow_time) VALUES (?, ?, NOW())";
            PreparedStatement borrowStatement = connection.prepareStatement(borrowQuery);
            borrowStatement.setString(1, readerNumber);
            borrowStatement.setString(2, bookNumber);
            int rowsInserted = borrowStatement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "借书成功！");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "借书失败！");
        }
    }

    private void returnBook(String readerNumber, String bookNumber) {
        try {
            // 插入还书记录
            String returnQuery = "INSERT INTO return_book (reader_number, book_number, return_time) VALUES (?, ?, NOW())";
            PreparedStatement returnStatement = connection.prepareStatement(returnQuery);
            returnStatement.setString(1, readerNumber);
            returnStatement.setString(2, bookNumber);
            int rowsInserted = returnStatement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "还书成功！");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "还书失败！");
        }
    }

    private void queryBorrowReturnInfo(String readerNumber) {
        try {
            String query = "SELECT b.book_number, b.borrow_time, r.return_time " +
                    "FROM borrow_book b LEFT JOIN return_book r " +
                    "ON b.reader_number = r.reader_number AND b.book_number = r.book_number " +
                    "WHERE b.reader_number = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, readerNumber);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append("书号\t借书时间\t\t还书时间\n");
            while (resultSet.next()) {
                String bookNumber = resultSet.getString("book_number");
                String borrowTime = resultSet.getString("borrow_time");
                String returnTime = resultSet.getString("return_time");
                resultBuilder.append(bookNumber).append("\t").append(borrowTime).append("\t").append(returnTime != null ? returnTime : "未还").append("\n");
            }
            displayArea.setText(resultBuilder.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "查询借书还书信息失败！");
        }
    }
}
