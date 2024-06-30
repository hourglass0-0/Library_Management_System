package library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ReaderManagementPanel extends JPanel implements ActionListener {
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton searchButton;
    private JTextField searchField;
    private JTable readerTable;
    private DefaultTableModel tableModel;
    private Connection connection;

    public ReaderManagementPanel(Connection connection) {
        this.connection = connection;
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("添加读者");
        addButton.addActionListener(this);
        buttonPanel.add(addButton);

        deleteButton = new JButton("删除读者");
        deleteButton.addActionListener(this);
        buttonPanel.add(deleteButton);

        updateButton = new JButton("修改读者信息");
        updateButton.addActionListener(this);
        buttonPanel.add(updateButton);

        searchButton = new JButton("查找");
        searchButton.addActionListener(this);
        buttonPanel.add(searchButton);

        searchField = new JTextField(20);
        buttonPanel.add(searchField);

        add(buttonPanel, BorderLayout.NORTH);

        // 设置表格模型
        String[] columnNames = {"读者编号", "姓名", "单位", "性别", "手机号"};
        tableModel = new DefaultTableModel(columnNames, 0);
        readerTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(readerTable);
        add(scrollPane, BorderLayout.CENTER);

        loadAllReaders();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            // 添加读者功能
            addReader();
        } else if (e.getSource() == deleteButton) {
            // 删除读者功能
            deleteReader();
        } else if (e.getSource() == updateButton) {
            // 修改读者信息功能
            updateReader();
        } else if (e.getSource() == searchButton) {
            // 查找读者功能
            String keyword = searchField.getText();
            searchReaders(keyword);
        }
    }

    private void addReader() {
        JTextField nameField = new JTextField(8);
        JTextField departmentField = new JTextField(20);
        JTextField genderField = new JTextField(2);
        JTextField telephoneField = new JTextField(11);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("姓名:"));
        panel.add(nameField);
        panel.add(new JLabel("单位:"));
        panel.add(departmentField);
        panel.add(new JLabel("性别:"));
        panel.add(genderField);
        panel.add(new JLabel("手机号:"));
        panel.add(telephoneField);

        int result = JOptionPane.showConfirmDialog(null, panel, "添加读者", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String department = departmentField.getText().trim();
            String gender = genderField.getText().trim();
            String telephone = telephoneField.getText().trim();

            // 校验性别是否为“男”或“女”
            if (!gender.equals("男") && !gender.equals("女")) {
                JOptionPane.showMessageDialog(this, "性别必须是“男”或“女”！");
                return;
            }

            // 校验手机号格式
            if (!telephone.matches("\\d{8}")) {
                JOptionPane.showMessageDialog(this, "手机号格式不正确！");
                return;
            }

            try {
                String readerNumber = generateReaderNumber();
                String query = "INSERT INTO reader (reader_number, name, department, gender, telephone) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, readerNumber);
                statement.setString(2, name);
                statement.setString(3, department);
                statement.setString(4, gender);
                statement.setString(5, telephone);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "读者添加成功！");
                    loadAllReaders(); // 更新表格内容
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "添加读者失败！");
            }
        }
    }


    private String generateReaderNumber() {
        String readerNumber = "1";
        try {
            String query = "SELECT MAX(reader_number) FROM reader";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String maxReaderNumber = resultSet.getString(1);
                if (maxReaderNumber != null) {
                    int maxNumber = Integer.parseInt(maxReaderNumber);
                    readerNumber = String.format("%d", maxNumber + 1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "生成读者编号失败！");
        }
        return readerNumber;
    }

    private void deleteReader() {
        int selectedRow = readerTable.getSelectedRow();
        if (selectedRow != -1) {
            String readerNumber = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "确定要删除读者编号为 " + readerNumber + " 的读者吗？", "确认删除", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM reader WHERE reader_number = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, readerNumber);
                    int rowsDeleted = statement.executeUpdate();
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(this, "读者删除成功！");
                        loadAllReaders(); // 更新表格内容
                    } else {
                        JOptionPane.showMessageDialog(this, "读者删除失败！");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "删除读者失败！");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "请先选择要删除的读者！");
        }
    }

    private void updateReader() {
        int selectedRow = readerTable.getSelectedRow();
        if (selectedRow != -1) {
            String readerNumber = (String) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            String department = (String) tableModel.getValueAt(selectedRow, 2);
            String gender = (String) tableModel.getValueAt(selectedRow, 3);
            String telephone = (String) tableModel.getValueAt(selectedRow, 4);

            JTextField nameField = new JTextField(name, 8);
            JTextField departmentField = new JTextField(department, 20);
            JTextField genderField = new JTextField(gender, 2);
            JTextField telephoneField = new JTextField(telephone, 11);

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.add(new JLabel("姓名:"));
            panel.add(nameField);
            panel.add(new JLabel("单位:"));
            panel.add(departmentField);
            panel.add(new JLabel("性别:"));
            panel.add(genderField);
            panel.add(new JLabel("手机号:"));
            panel.add(telephoneField);

            int result = JOptionPane.showConfirmDialog(null, panel, "修改读者信息", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                name = nameField.getText().trim();
                department = departmentField.getText().trim();
                gender = genderField.getText().trim();
                telephone = telephoneField.getText().trim();

                // 校验性别是否为“男”或“女”
                if (!gender.equals("男") && !gender.equals("女")) {
                    JOptionPane.showMessageDialog(this, "性别必须是“男”或“女”！");
                    return;
                }

                // 校验手机号格式
                if (!telephone.matches("\\d{8}")) {
                    JOptionPane.showMessageDialog(this, "手机号格式不正确！");
                    return;
                }

                try {
                    String query = "UPDATE reader SET name = ?, department = ?, gender = ?, telephone = ? WHERE reader_number = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, name);
                    statement.setString(2, department);
                    statement.setString(3, gender);
                    statement.setString(4, telephone);
                    statement.setString(5, readerNumber);

                    int rowsUpdated = statement.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(this, "读者信息修改成功！");
                        loadAllReaders(); // 更新表格内容
                    } else {
                        JOptionPane.showMessageDialog(this, "读者信息修改失败！");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "修改读者信息失败！");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "请先选择要修改的读者！");
        }
    }


    private void loadAllReaders() {
        try {
            String query = "SELECT * FROM reader";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0); // 清空表格内容
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getString("reader_number"),
                        resultSet.getString("name"),
                        resultSet.getString("department"),
                        resultSet.getString("gender"),
                        resultSet.getString("telephone")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载读者信息失败！");
        }
    }

    private void searchReaders(String keyword) {
        try {
            String query = "SELECT * FROM reader WHERE reader_number LIKE ? OR name LIKE ? OR department LIKE ? OR gender LIKE ? OR telephone LIKE ?";
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
                        resultSet.getString("reader_number"),
                        resultSet.getString("name"),
                        resultSet.getString("department"),
                        resultSet.getString("gender"),
                        resultSet.getString("telephone")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "查找读者信息失败！");
        }
    }
}
