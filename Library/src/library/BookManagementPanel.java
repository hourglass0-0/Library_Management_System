package library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class BookManagementPanel extends JPanel implements ActionListener {
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton searchButton;
    private JTextField searchField;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private Connection connection;

    public BookManagementPanel(Connection connection) {
        this.connection = connection;
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("添加图书");
        addButton.addActionListener(this);
        buttonPanel.add(addButton);

        deleteButton = new JButton("删除图书");
        deleteButton.addActionListener(this);
        buttonPanel.add(deleteButton);

        updateButton = new JButton("修改图书信息");
        updateButton.addActionListener(this);
        buttonPanel.add(updateButton);

        searchButton = new JButton("查找");
        searchButton.addActionListener(this);
        buttonPanel.add(searchButton);

        searchField = new JTextField(20);
        buttonPanel.add(searchField);

        add(buttonPanel, BorderLayout.NORTH);

        // 设置表格模型
        String[] columnNames = {"书号", "类别", "书名", "出版社", "作者", "价格", "总藏书量", "库存"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        loadAllBooks();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            // 添加图书功能
            addBook();
        } else if (e.getSource() == deleteButton) {
            // 删除图书功能
            deleteBook();
        } else if (e.getSource() == updateButton) {
            // 修改图书信息功能
            updateBook();
        } else if (e.getSource() == searchButton) {
            // 查找图书功能
            String keyword = searchField.getText();
            searchBooks(keyword);
        }
    }

    private void addBook() {
        JTextField categoryField = new JTextField(12);
        JTextField bookNameField = new JTextField(30);
        JTextField publisherField = new JTextField(30);
        JTextField authorField = new JTextField(10);
        JTextField priceField = new JTextField(5);
        JTextField bookTotalField = new JTextField(5);
        JTextField inventoryField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("类别:"));
        panel.add(categoryField);
        panel.add(new JLabel("书名:"));
        panel.add(bookNameField);
        panel.add(new JLabel("出版社:"));
        panel.add(publisherField);
        panel.add(new JLabel("作者:"));
        panel.add(authorField);
        panel.add(new JLabel("价格:"));
        panel.add(priceField);
        panel.add(new JLabel("总藏书量:"));
        panel.add(bookTotalField);
        panel.add(new JLabel("库存:"));
        panel.add(inventoryField);

        int result = JOptionPane.showConfirmDialog(null, panel, "添加图书", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String category = categoryField.getText();
            String bookName = bookNameField.getText();
            String publisher = publisherField.getText();
            String author = authorField.getText();
            String priceStr = priceField.getText();
            String bookTotalStr = bookTotalField.getText();
            String inventoryStr = inventoryField.getText();

            try {
                double price = Double.parseDouble(priceStr);
                int bookTotal = Integer.parseInt(bookTotalStr);
                int inventory = Integer.parseInt(inventoryStr);

                if (price > 0 && bookTotal > 0 && inventory > 0) {
                    String bookNumber = generateBookNumber();
                    String query = "INSERT INTO book (book_number, category, book_name, publisher, author, price, book_total, inventory) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, bookNumber);
                    statement.setString(2, category);
                    statement.setString(3, bookName);
                    statement.setString(4, publisher);
                    statement.setString(5, author);
                    statement.setDouble(6, price);
                    statement.setInt(7, bookTotal);
                    statement.setInt(8, inventory);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(this, "图书添加成功！");
                        loadAllBooks(); // 更新表格内容
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "价格、总藏书量和库存必须大于0！");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的数字！");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "添加图书失败！");
            }
        }
    }

    private String generateBookNumber() {
        String bookNumber ="1";
        try {
            String query = "SELECT MAX(book_number) FROM book";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String maxBookNumber = resultSet.getString(1);
                if (maxBookNumber != null) {
                    int maxNumber = Integer.parseInt(maxBookNumber);
                    bookNumber = String.format("%d", maxNumber + 1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "生成书号失败！");
        }
        return bookNumber;
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            String bookNumber = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "确定要删除书号为 " + bookNumber + " 的图书吗？", "确认删除", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM book WHERE book_number = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, bookNumber);
                    int rowsDeleted = statement.executeUpdate();
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(this, "图书删除成功！");
                        loadAllBooks(); // 更新表格内容
                    } else {
                        JOptionPane.showMessageDialog(this, "图书删除失败！");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "删除图书失败！");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "请先选择要删除的图书！");
        }
    }

    private void updateBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            String bookNumber = (String) tableModel.getValueAt(selectedRow, 0);
            String category = (String) tableModel.getValueAt(selectedRow, 1);
            String bookName = (String) tableModel.getValueAt(selectedRow, 2);
            String publisher = (String) tableModel.getValueAt(selectedRow, 3);
            String author = (String) tableModel.getValueAt(selectedRow, 4);
            double price = (Double) tableModel.getValueAt(selectedRow, 5);
            int bookTotal = (Integer) tableModel.getValueAt(selectedRow, 6);
            int inventory = (Integer) tableModel.getValueAt(selectedRow, 7);

            JTextField categoryField = new JTextField(category, 12);
            JTextField bookNameField = new JTextField(bookName, 30);
            JTextField publisherField = new JTextField(publisher, 30);
            JTextField authorField = new JTextField(author, 10);
            JTextField priceField = new JTextField(String.valueOf(price), 5);
            JTextField bookTotalField = new JTextField(String.valueOf(bookTotal), 5);
            JTextField inventoryField = new JTextField(String.valueOf(inventory), 5);

            JPanel panel = new JPanel(new GridLayout(7, 2));
            panel.add(new JLabel("类别:"));
            panel.add(categoryField);
            panel.add(new JLabel("书名:"));
            panel.add(bookNameField);
            panel.add(new JLabel("出版社:"));
            panel.add(publisherField);
            panel.add(new JLabel("作者:"));
            panel.add(authorField);
            panel.add(new JLabel("价格:"));
            panel.add(priceField);
            panel.add(new JLabel("总藏书量:"));
            panel.add(bookTotalField);
            panel.add(new JLabel("库存:"));
            panel.add(inventoryField);

            int result = JOptionPane.showConfirmDialog(null, panel, "修改图书信息", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                category = categoryField.getText();
                bookName = bookNameField.getText();
                publisher = publisherField.getText();
                author = authorField.getText();
                String priceStr = priceField.getText();
                String bookTotalStr = bookTotalField.getText();
                String inventoryStr = inventoryField.getText();

                try {
                    price = Double.parseDouble(priceStr);
                    bookTotal = Integer.parseInt(bookTotalStr);
                    inventory = Integer.parseInt(inventoryStr);

                    if (price > 0 && bookTotal > 0 && inventory > 0) {
                        String query = "UPDATE book SET category = ?, book_name = ?, publisher = ?, author = ?, price = ?, book_total = ?, inventory = ? WHERE book_number = ?";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, category);
                        statement.setString(2, bookName);
                        statement.setString(3, publisher);
                        statement.setString(4, author);
                        statement.setDouble(5, price);
                        statement.setInt(6, bookTotal);
                        statement.setInt(7, inventory);
                        statement.setString(8, bookNumber);

                        int rowsUpdated = statement.executeUpdate();
                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(this, "图书信息修改成功！");
                            loadAllBooks(); // 更新表格内容
                        } else {
                            JOptionPane.showMessageDialog(this, "图书信息修改失败！");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "价格、总藏书量和库存必须大于0！");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "请输入有效的数字！");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "修改图书信息失败！");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "请先选择要修改的图书！");
        }
    }

    private void loadAllBooks() {
        try {
            String query = "SELECT * FROM book";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0); // 清空表格内容
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getString("book_number"),
                        resultSet.getString("category"),
                        resultSet.getString("book_name"),
                        resultSet.getString("publisher"),
                        resultSet.getString("author"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("book_total"),
                        resultSet.getInt("inventory")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载图书信息失败！");
        }
    }

    private void searchBooks(String keyword) {
        try {
            String query = "SELECT * FROM book WHERE book_number LIKE ? OR category LIKE ? OR book_name LIKE ? " +
                    "OR publisher LIKE ? OR author LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            String likeKeyword = "%" + keyword + "%";
            statement.setString(1, likeKeyword);
            statement.setString(2, likeKeyword);
            statement.setString(3, likeKeyword);
            statement.setString(4, likeKeyword);
            statement.setString(5, likeKeyword);
            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0); // 清空表格内容
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getString("book_number"),
                        resultSet.getString("category"),
                        resultSet.getString("book_name"),
                        resultSet.getString("publisher"),
                        resultSet.getString("author"),
                        resultSet.getDouble("price"),
                        resultSet.getInt("book_total"),
                        resultSet.getInt("inventory")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "查询失败！");
        }
    }
}
