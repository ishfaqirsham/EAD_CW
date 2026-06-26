/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.hotel.ui;

import javax.swing.JOptionPane;


/**
 *
 * @author sheha
 */
public class CustomerForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CustomerForm.class.getName());

    private String role;           // "admin" or "receptionist"
    private int selectedId = -1;     // -1 means "no row selected" (Add mode)
    
    public CustomerForm(String role) {  //constructor
        initComponents();
        this.role = role;
        loadAllCustomers();        // fill the table when screen opens
        
     // Receptionist can't delete customers
    if (role.equals("receptionist")) {
        btnDelete.setEnabled(false);
    }

    // When a table row is clicked, copy that row's data into the form
    table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelectedRow());
    }
 
    private void addCustomer() {

    // Get text from the form fields
    String name = txtName.getText().trim();
    String phone = txtPhone.getText().trim();
    String email = txtEmail.getText().trim();
    String nic = txtNic.getText().trim();
    String nationality = txtNationality.getText().trim();
    String address = txtAddress.getText().trim();

    // Validation: name can't be empty
    if (name.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Customer name cannot be empty.");
        return;
    }
    // Validation: phone must be exactly 10 digits
    if (phone.length() != 10) {
        JOptionPane.showMessageDialog(this, "Phone must be exactly 10 digits.");
        return;
    }
    // Validation: basic email check
    if (!email.contains("@") || !email.contains(".")) {
        JOptionPane.showMessageDialog(this, "Email format is invalid.");
        return;
    }

    try {
        // Get the shared DB connection (Singleton)
        java.sql.Connection con = com.hotel.util.DBConnection.getConnection();

        // ? marks are placeholders, filled in safely below (prevents SQL injection)
        String sql = "INSERT INTO customer (full_name, phone, email, nic_passport, nationality, address) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        java.sql.PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, phone);
        ps.setString(3, email);
        ps.setString(4, nic);
        ps.setString(5, nationality);
        ps.setString(6, address);
        ps.executeUpdate(); // runs the INSERT

        JOptionPane.showMessageDialog(this, "Customer added.");
        clearForm();        // reset the form
        loadAllCustomers(); // refresh the table

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error adding customer: " + e.getMessage());
    }
}
    
    private void updateCustomer() {

    // Must have clicked a row first
    if (selectedId == -1) {
        JOptionPane.showMessageDialog(this, "Select a customer from the table first.");
        return;
    }

    String name = txtName.getText().trim();
    String phone = txtPhone.getText().trim();
    String email = txtEmail.getText().trim();
    String nic = txtNic.getText().trim();
    String nationality = txtNationality.getText().trim();
    String address = txtAddress.getText().trim();

    // Same validation rules as Add
    if (name.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Customer name cannot be empty.");
        return;
    }
    if (phone.length() != 10) {
        JOptionPane.showMessageDialog(this, "Phone must be exactly 10 digits.");
        return;
    }
    if (!email.contains("@") || !email.contains(".")) {
        JOptionPane.showMessageDialog(this, "Email format is invalid.");
        return;
    }

    try {
        java.sql.Connection con = com.hotel.util.DBConnection.getConnection();

        // Updates the row whose ID matches selectedId
        String sql = "UPDATE customer SET full_name=?, phone=?, email=?, nic_passport=?, "
                   + "nationality=?, address=? WHERE customer_id=?";
        java.sql.PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, phone);
        ps.setString(3, email);
        ps.setString(4, nic);
        ps.setString(5, nationality);
        ps.setString(6, address);
        ps.setInt(7, selectedId); // which row to update
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Customer updated.");
        clearForm();
        loadAllCustomers();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error updating customer: " + e.getMessage());
    }
}

    private void deleteCustomer() {

    // Receptionist is blocked from deleting (double safety check,
    // even though the button is already disabled for them)
    if (role.equals("receptionist")) {
        JOptionPane.showMessageDialog(this, "Only Admin can delete customers.");
        return;
    }

    // Must have clicked a row first
    if (selectedId == -1) {
        JOptionPane.showMessageDialog(this, "Select a customer from the table first.");
        return;
    }

    // Ask for confirmation before deleting
    int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this customer?", "Confirm", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) return;

    try {
        java.sql.Connection con = com.hotel.util.DBConnection.getConnection();
        String sql = "DELETE FROM customer WHERE customer_id=?";
        java.sql.PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, selectedId);
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Customer deleted.");
        clearForm();
        loadAllCustomers();

    } catch (Exception e) {
        // This usually fails if the customer still has reservations or
        // payments linked to them (foreign key protection in the database)
        JOptionPane.showMessageDialog(this,
            "Could not delete. This customer may have existing reservations or payments.");
    }
}
    
    private void searchCustomer() {

    // Get whatever text was typed in the search box
    String keyword = txtSearch.getText().trim();

    try {
        java.sql.Connection con = com.hotel.util.DBConnection.getConnection();

        // LIKE %keyword% checks if the keyword appears ANYWHERE in
        // the name, phone, or email - matches partial text too
        String sql = "SELECT * FROM customer WHERE full_name LIKE ? OR phone LIKE ? OR email LIKE ?";
        java.sql.PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, "%" + keyword + "%");
        ps.setString(2, "%" + keyword + "%");
        ps.setString(3, "%" + keyword + "%");
        java.sql.ResultSet rs = ps.executeQuery();

        fillTable(rs); // shows the matching rows in the table

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Search failed: " + e.getMessage());
    }
}
    
    
    private void loadAllCustomers() {

    try {
        java.sql.Connection con = com.hotel.util.DBConnection.getConnection();
        java.sql.Statement st = con.createStatement();

        // No WHERE clause = get every customer, sorted by name
        java.sql.ResultSet rs = st.executeQuery("SELECT * FROM customer ORDER BY full_name");

        fillTable(rs); // shows all rows in the table

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Could not load customers: " + e.getMessage());
    }
}
    
    
    private void fillTable(java.sql.ResultSet rs) throws Exception {

    javax.swing.table.DefaultTableModel model =
        (javax.swing.table.DefaultTableModel) table.getModel();

    model.setRowCount(0); // clear the table first

    while (rs.next()) {
        model.addRow(new Object[]{
            rs.getInt("customer_id"), rs.getString("full_name"), rs.getString("phone"),
            rs.getString("email"), rs.getString("nic_passport"),
            rs.getString("nationality"), rs.getString("address")
        });
    }
}
    
    
    private void fillFormFromSelectedRow() {

    int row = table.getSelectedRow(); // which row was clicked
    if (row == -1) return; // nothing selected, do nothing

    // Copy that row's data into the form fields, using the
    // table's model to read each column by position (0, 1, 2...)
    javax.swing.table.DefaultTableModel model =
        (javax.swing.table.DefaultTableModel) table.getModel();

    selectedId = (int) model.getValueAt(row, 0);          // column 0 = ID
    txtName.setText((String) model.getValueAt(row, 1));    // column 1 = Name
    txtPhone.setText((String) model.getValueAt(row, 2));   // column 2 = Phone
    txtEmail.setText((String) model.getValueAt(row, 3));   // column 3 = Email
    txtNic.setText((String) model.getValueAt(row, 4));     // column 4 = NIC
    txtNationality.setText((String) model.getValueAt(row, 5)); // column 5 = Nationality
    txtAddress.setText((String) model.getValueAt(row, 6)); // column 6 = Address
}
    
    
    private void clearForm() {
    selectedId = -1; // back to "no row selected" / Add mode
    txtName.setText("");
    txtPhone.setText("");
    txtEmail.setText("");
    txtNic.setText("");
    txtNationality.setText("");
    txtAddress.setText("");
    table.clearSelection(); // unhighlight any selected row
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBanner = new javax.swing.JPanel();
        lblBannerTitle = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtNic = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtNationality = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnShowAll = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Customer Management - Grand Palm Hotel");
        setBackground(new java.awt.Color(247, 245, 242));
        setSize(new java.awt.Dimension(900, 600));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlBanner.setBackground(new java.awt.Color(44, 62, 80));
        pnlBanner.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(201, 165, 92)));
        pnlBanner.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBannerTitle.setFont(new java.awt.Font("Segoe UI Emoji", 1, 20)); // NOI18N
        lblBannerTitle.setForeground(new java.awt.Color(201, 165, 92));
        lblBannerTitle.setText("Customer Management");
        pnlBanner.add(lblBannerTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 270, -1));

        getContentPane().add(pnlBanner, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 930, 70));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(44, 62, 80));
        jLabel1.setText("Customer Details");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel2.setText("Full Name:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, -1, -1));
        jPanel1.add(txtName, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, 220, -1));

        jLabel3.setText("Phone Number :");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 40, -1, -1));
        jPanel1.add(txtPhone, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 60, 220, -1));

        jLabel4.setText("Email :");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 40, -1, -1));

        txtEmail.addActionListener(this::txtEmailActionPerformed);
        jPanel1.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 60, 230, -1));
        jPanel1.add(txtNic, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 120, 220, -1));

        jLabel5.setText("NIC/Passport :");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 100, -1, -1));
        jPanel1.add(txtNationality, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 120, 220, -1));

        jLabel6.setText("Nationality :");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 100, -1, -1));
        jPanel1.add(txtAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 120, 230, -1));

        jLabel7.setText("Address:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 100, -1, -1));

        btnAdd.setBackground(new java.awt.Color(44, 62, 80));
        btnAdd.setForeground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("Add Customer");
        btnAdd.addActionListener(this::btnAddActionPerformed);
        jPanel1.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, 140, -1));

        btnUpdate.setBackground(new java.awt.Color(44, 62, 80));
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Update Customer");
        btnUpdate.addActionListener(this::btnUpdateActionPerformed);
        jPanel1.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 160, 150, -1));

        btnDelete.setBackground(new java.awt.Color(192, 57, 43));
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Delete Customer");
        btnDelete.addActionListener(this::btnDeleteActionPerformed);
        jPanel1.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 160, 140, -1));

        btnClear.setBackground(new java.awt.Color(227, 224, 218));
        btnClear.setText("Clear");
        btnClear.addActionListener(this::btnClearActionPerformed);
        jPanel1.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 160, 110, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 880, 200));

        jLabel8.setText("Search:");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 320, 50, -1));

        txtSearch.addActionListener(this::txtSearchActionPerformed);
        getContentPane().add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 310, 270, 30));

        btnSearch.setBackground(new java.awt.Color(201, 165, 92));
        btnSearch.setText("Search Customer");
        btnSearch.addActionListener(this::btnSearchActionPerformed);
        getContentPane().add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 310, -1, 30));

        btnShowAll.setBackground(new java.awt.Color(227, 224, 218));
        btnShowAll.setText("Show All");
        btnShowAll.addActionListener(this::btnShowAllActionPerformed);
        getContentPane().add(btnShowAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 310, -1, 30));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Phone", "Email", "NIC/Passport", "Nationality", "Address"
            }
        ));
        jScrollPane1.setViewportView(table);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 350, 880, 200));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        addCustomer(); // runs the Add logic below
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        updateCustomer(); // runs the Update logic below
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteCustomer(); // runs the Delete logic below
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
         clearForm(); // empties all the text fields
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchCustomer(); // filters the table by the search box text
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowAllActionPerformed
        loadAllCustomers(); // removes the filter, shows everyone again
    }//GEN-LAST:event_btnShowAllActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        // Lets you test-run this screen by itself (Shift+F6), without
// going through Login first. "admin" here is just a fake role
// for testing - the real role comes from LoginForm when the app
// actually runs normally.
        java.awt.EventQueue.invokeLater(() -> new CustomerForm("admin").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnShowAll;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBannerTitle;
    private javax.swing.JPanel pnlBanner;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNationality;
    private javax.swing.JTextField txtNic;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
