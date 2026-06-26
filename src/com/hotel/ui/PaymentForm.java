/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.hotel.ui;

/**
 *
 * @author sheha
 */
public class PaymentForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PaymentForm.class.getName());

   private String role; // "admin" or "receptionist" - both can record payments

// Talks to the database through PaymentController, never directly.
// The Controller validates the amount first, then calls PaymentDAO.
private com.hotel.controller.PaymentController paymentController =
    new com.hotel.controller.PaymentController();


    public PaymentForm(String role) {
        initComponents();
        this.role = role;
                
    // No role restrictions on this screen - both Admin and
    // Receptionist can record payments, since it's a normal
    // front-desk task, not a sensitive admin-only action.
    loadReservationsIntoDropdown();
    loadAllPayments();
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBanner = new javax.swing.JPanel();
        lblBannerTitle = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cmbReservation = new javax.swing.JComboBox<>();
        txtAmount = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnRecordPayment = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Payment Management - Grand Palm Hotel");
        setBackground(new java.awt.Color(247, 245, 242));
        setSize(new java.awt.Dimension(800, 550));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlBanner.setBackground(new java.awt.Color(44, 62, 80));
        pnlBanner.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(201, 165, 92)));
        pnlBanner.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBannerTitle.setFont(new java.awt.Font("Segoe UI Emoji", 1, 20)); // NOI18N
        lblBannerTitle.setForeground(new java.awt.Color(201, 165, 92));
        lblBannerTitle.setText("Payment Management");
        pnlBanner.add(lblBannerTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 43, 220, -1));

        getContentPane().add(pnlBanner, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, -3, 910, 90));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel1.setText("Record Payment");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel2.setText("Reservation:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        cmbReservation.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));
        jPanel1.add(cmbReservation, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 430, 30));
        jPanel1.add(txtAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 140, 430, -1));

        jLabel3.setText("Amount (Rs.):");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, -1));

        btnRecordPayment.setBackground(new java.awt.Color(46, 125, 82));
        btnRecordPayment.setForeground(new java.awt.Color(255, 255, 255));
        btnRecordPayment.setText("Record Payment");
        btnRecordPayment.addActionListener(this::btnRecordPaymentActionPerformed);
        jPanel1.add(btnRecordPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 70, 270, 30));

        btnRefresh.setBackground(new java.awt.Color(227, 224, 218));
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);
        jPanel1.add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 140, 270, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 870, 190));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Payment ID", "ReservationID", "Customer", "Amount (Rs.)", "Date"
            }
        ));
        jScrollPane1.setViewportView(table);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 320, 870, 190));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRecordPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecordPaymentActionPerformed
           recordPayment();     
    }//GEN-LAST:event_btnRecordPaymentActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
    // Re-fetch fresh data, in case a new reservation was made
    // or a payment was added from another screen since this opened
           loadReservationsIntoDropdown();
           loadAllPayments();
    }//GEN-LAST:event_btnRefreshActionPerformed

// Fills the dropdown with all ACTIVE reservations as "id - customer - room",
// e.g. "5 - Anusha Silva - Room D-202". Cancelled reservations are
// excluded - you shouldn't be able to pay for a booking that's been cancelled.
private void loadReservationsIntoDropdown() {
    cmbReservation.removeAllItems();
    try {
        java.sql.Connection con = com.hotel.util.DBConnection.getConnection();
        java.sql.Statement st = con.createStatement();

        // 3 JOINs: reservation -> customer (for the name) and
        // reservation -> room (for the room number)
        String sql = "SELECT res.reservation_id, c.full_name, r.room_number "
                   + "FROM reservation res "
                   + "JOIN customer c ON res.customer_id = c.customer_id "
                   + "JOIN room r ON res.room_id = r.room_id "
                   + "WHERE res.status = 'Active' "
                   + "ORDER BY res.reservation_id DESC";
        java.sql.ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            String item = rs.getInt("reservation_id") + " - " + rs.getString("full_name")
                    + " - Room " + rs.getString("room_number");
            cmbReservation.addItem(item);
        }
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Could not load reservations: " + e.getMessage());
    }
}

// Loads every recorded payment into the table, newest first.
// Goes through paymentController, not directly to the database -
// same layered pattern as the other screens.
private void loadAllPayments() {
    java.util.List<com.hotel.model.Payment> payments = paymentController.getAllPayments();

    javax.swing.table.DefaultTableModel model =
        (javax.swing.table.DefaultTableModel) table.getModel();
    model.setRowCount(0); // clear old rows

    for (com.hotel.model.Payment p : payments) {
        model.addRow(new Object[]{
            p.getPaymentId(), p.getReservationId(), p.getCustomerId(), p.getAmount()
            // Note: no "Date" column data here, since the Payment model
            // doesn't currently store the date as a field. The table
            // column header says "Date" but this row will leave it
            // blank for now - mention this if asked in the VIVA.
        });
    }
}

// Validates the amount, looks up which reservation was picked,
// then asks PaymentController to save it.
private void recordPayment() {

    Object selectedReservation = cmbReservation.getSelectedItem();

    // Guard against NullPointerException - nothing selected yet
    if (selectedReservation == null) {
        javax.swing.JOptionPane.showMessageDialog(this, "No active reservations available to pay for.");
        return;
    }

    String amountText = txtAmount.getText().trim();
    if (amountText.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, "Payment amount cannot be empty.");
        return;
    }

    double amount;
    try {
        // Convert the typed text into a real number.
        amount = Double.parseDouble(amountText);
    } catch (NumberFormatException e) {
        // This fires if someone types letters instead of numbers,
        // e.g. "abc" instead of "5000"
        javax.swing.JOptionPane.showMessageDialog(this, "Payment amount must be a valid number.");
        return;
    }

    try {
        // Pull just the reservation ID out of "5 - Anusha Silva - Room D-202"
        String dropdownText = (String) selectedReservation;
        int reservationId = Integer.parseInt(dropdownText.split(" - ")[0].trim());

        // The Controller checks amount > 0 itself, then saves the
        // payment if everything's fine. Returns null on success,
        // or an error message string if something went wrong.
        String error = paymentController.recordPayment(reservationId, amount);

        if (error == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Payment of Rs. " + amount + " recorded.");
            txtAmount.setText(""); // clear the field for the next entry
            loadAllPayments();      // refresh the table to show the new row
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, error);
        }

    } catch (NumberFormatException e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Could not read the selected reservation's ID.");
    }
}
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
        java.awt.EventQueue.invokeLater(() -> new PaymentForm("admin").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRecordPayment;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox<String> cmbReservation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBannerTitle;
    private javax.swing.JPanel pnlBanner;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtAmount;
    // End of variables declaration//GEN-END:variables
}
