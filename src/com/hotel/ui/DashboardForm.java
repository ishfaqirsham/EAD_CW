/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.hotel.ui;

/**
 *
 * @author sheha
 */
public class DashboardForm extends javax.swing.JFrame {
    private String role;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DashboardForm.class.getName());


    public DashboardForm(String role) {
        this.role = role;
        initComponents();
        loadStats();

    if (role.equals("receptionist")) {
        btnReport.setEnabled(false);
    }
    }
    
    // Runs 5 small queries, one per stat, and updates the matching label.
// Each query is simple on purpose: easy to read top to bottom
// this counts rows in the customer table
// this sums total_amount but only for Active reservations
private void loadStats() {
    try {
        // Step 1: ask DBConnection (our Singleton) for the one shared
        // database connection - we don't create a new connection here.
        java.sql.Connection con = com.hotel.util.DBConnection.getConnection();
        java.sql.Statement st = con.createStatement();
        java.sql.ResultSet rs;

        // Total Customers - simple row count on the customer table.
        rs = st.executeQuery("SELECT COUNT(*) AS total FROM customer");
        if (rs.next()) lblCustomers.setText(rs.getString("total"));

        // Total Rooms - will always be 10, but we still read it from
        // the database instead of hardcoding "10", in case that ever changes.
        rs = st.executeQuery("SELECT COUNT(*) AS total FROM room");
        if (rs.next()) lblRooms.setText(rs.getString("total"));

        // Available Rooms - only counts rooms whose status is 'Available'.
        rs = st.executeQuery("SELECT COUNT(*) AS total FROM room WHERE status = 'Available'");
        if (rs.next()) lblAvailable.setText(rs.getString("total"));

        // Total Reservations - counts ALL reservations, including
        // cancelled ones, so this number reflects total bookings ever made.
        rs = st.executeQuery("SELECT COUNT(*) AS total FROM reservation");
        if (rs.next()) lblReservations.setText(rs.getString("total"));

        // Revenue - adds up total_amount, but ONLY for reservations
        // that are still 'Active'. A Cancelled reservation should not
        // count as money earned, so it's excluded with the WHERE clause.
        rs = st.executeQuery("SELECT SUM(total_amount) AS revenue FROM reservation WHERE status = 'Completed'");
        if (rs.next()) {
            String revenue = rs.getString("revenue");
            // SUM() returns NULL (no rows) if there are zero Active
            // reservations - we check for that so the label shows "0"
            // instead of the literal text "null".
            lblRevenue.setText(revenue == null ? "0" : revenue);
        }

    } catch (Exception e) {
        // If the database is down, or the query has a typo, this catches
        // it instead of crashing the whole screen - shows a popup instead.
        javax.swing.JOptionPane.showMessageDialog(this, "Could not load dashboard: " + e.getMessage());
    }
}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBanner = new javax.swing.JPanel();
        lblBannerTitle = new javax.swing.JLabel();
        lblRole = new javax.swing.JLabel();
        pnlCustomers = new javax.swing.JPanel();
        JLabel = new javax.swing.JLabel();
        lblCustomers = new javax.swing.JLabel();
        pnlRooms = new javax.swing.JPanel();
        JLabel1 = new javax.swing.JLabel();
        lblRooms = new javax.swing.JLabel();
        pnlAvailable = new javax.swing.JPanel();
        JLabel2 = new javax.swing.JLabel();
        lblAvailable = new javax.swing.JLabel();
        pnlReservations = new javax.swing.JPanel();
        JLabel3 = new javax.swing.JLabel();
        lblReservations = new javax.swing.JLabel();
        pnlRevenue = new javax.swing.JPanel();
        JLabel4 = new javax.swing.JLabel();
        lblRevenue = new javax.swing.JLabel();
        btnCustomers = new javax.swing.JButton();
        btnRooms = new javax.swing.JButton();
        btnReservations = new javax.swing.JButton();
        btnPayments = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dashboard - Grand Palm Hotel");
        setBackground(new java.awt.Color(247, 245, 242));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlBanner.setBackground(new java.awt.Color(44, 62, 80));
        pnlBanner.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(201, 165, 92)));
        pnlBanner.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBannerTitle.setFont(new java.awt.Font("Segoe UI Historic", 1, 20)); // NOI18N
        lblBannerTitle.setForeground(new java.awt.Color(201, 165, 92));
        lblBannerTitle.setText("GRAND PALM HOTEL");
        pnlBanner.add(lblBannerTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 220, -1));

        lblRole.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblRole.setForeground(new java.awt.Color(255, 255, 255));
        lblRole.setText("Welcome Back,");
        pnlBanner.add(lblRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 30, 140, -1));

        getContentPane().add(pnlBanner, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 910, 70));

        pnlCustomers.setBackground(new java.awt.Color(255, 255, 255));
        pnlCustomers.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(201, 165, 92)));
        pnlCustomers.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        JLabel.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        JLabel.setForeground(new java.awt.Color(44, 62, 80));
        JLabel.setText("Total Customers");
        pnlCustomers.add(JLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 100, -1));

        lblCustomers.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblCustomers.setForeground(new java.awt.Color(44, 62, 80));
        lblCustomers.setText("0");
        pnlCustomers.add(lblCustomers, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, 50, -1));

        getContentPane().add(pnlCustomers, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 160, 90));

        pnlRooms.setBackground(new java.awt.Color(255, 255, 255));
        pnlRooms.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(201, 165, 92)));
        pnlRooms.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        JLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        JLabel1.setForeground(new java.awt.Color(44, 62, 80));
        JLabel1.setText("Total Rooms");
        pnlRooms.add(JLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 100, -1));

        lblRooms.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblRooms.setText("0");
        pnlRooms.add(lblRooms, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, 50, -1));

        getContentPane().add(pnlRooms, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 130, 160, 90));

        pnlAvailable.setBackground(new java.awt.Color(255, 255, 255));
        pnlAvailable.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(201, 165, 92)));
        pnlAvailable.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        JLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        JLabel2.setForeground(new java.awt.Color(44, 62, 80));
        JLabel2.setText("Available Rooms");
        pnlAvailable.add(JLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 100, -1));

        lblAvailable.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblAvailable.setText("0");
        pnlAvailable.add(lblAvailable, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, 40, -1));

        getContentPane().add(pnlAvailable, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 130, 160, 90));

        pnlReservations.setBackground(new java.awt.Color(255, 255, 255));
        pnlReservations.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(201, 165, 92)));
        pnlReservations.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        JLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        JLabel3.setForeground(new java.awt.Color(44, 62, 80));
        JLabel3.setText("Total Reservations");
        pnlReservations.add(JLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 100, -1));

        lblReservations.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblReservations.setText("0");
        pnlReservations.add(lblReservations, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, 40, -1));

        getContentPane().add(pnlReservations, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 130, 160, 90));

        pnlRevenue.setBackground(new java.awt.Color(255, 255, 255));
        pnlRevenue.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(201, 165, 92)));
        pnlRevenue.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        JLabel4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        JLabel4.setForeground(new java.awt.Color(44, 62, 80));
        JLabel4.setText("Revenue (Rs.)");
        pnlRevenue.add(JLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 100, -1));

        lblRevenue.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblRevenue.setText("0");
        pnlRevenue.add(lblRevenue, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 150, -1));

        getContentPane().add(pnlRevenue, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 130, 160, 90));

        btnCustomers.setBackground(new java.awt.Color(44, 62, 80));
        btnCustomers.setForeground(new java.awt.Color(255, 255, 255));
        btnCustomers.setText("Manage Customers");
        btnCustomers.addActionListener(this::btnCustomersActionPerformed);
        getContentPane().add(btnCustomers, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 290, 240, 40));

        btnRooms.setBackground(new java.awt.Color(44, 62, 80));
        btnRooms.setForeground(new java.awt.Color(255, 255, 255));
        btnRooms.setText("Manage Rooms");
        btnRooms.addActionListener(this::btnRoomsActionPerformed);
        getContentPane().add(btnRooms, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 290, 240, 40));

        btnReservations.setBackground(new java.awt.Color(44, 62, 80));
        btnReservations.setForeground(new java.awt.Color(255, 255, 255));
        btnReservations.setText("Reservations");
        btnReservations.addActionListener(this::btnReservationsActionPerformed);
        getContentPane().add(btnReservations, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 290, 240, 40));

        btnPayments.setBackground(new java.awt.Color(44, 62, 80));
        btnPayments.setForeground(new java.awt.Color(255, 255, 255));
        btnPayments.setText("Payments");
        btnPayments.addActionListener(this::btnPaymentsActionPerformed);
        getContentPane().add(btnPayments, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 370, 240, 40));

        btnReport.setBackground(new java.awt.Color(44, 62, 80));
        btnReport.setForeground(new java.awt.Color(255, 255, 255));
        btnReport.setText("Generate Report");
        btnReport.addActionListener(this::btnReportActionPerformed);
        getContentPane().add(btnReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 370, 240, 40));

        btnLogout.setBackground(new java.awt.Color(192, 57, 43));
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.addActionListener(this::btnLogoutActionPerformed);
        getContentPane().add(btnLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 370, 240, 40));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCustomersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomersActionPerformed
    // Opens the Customer screen, passing along the current role
    // so that screen knows whether to allow Delete or not.
      new CustomerForm(role).setVisible(true);
    }//GEN-LAST:event_btnCustomersActionPerformed

    private void btnRoomsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRoomsActionPerformed
      new RoomForm(role).setVisible(true);
    }//GEN-LAST:event_btnRoomsActionPerformed

    private void btnReservationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReservationsActionPerformed
        new ReservationForm(role).setVisible(true);
    }//GEN-LAST:event_btnReservationsActionPerformed

    private void btnPaymentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPaymentsActionPerformed
        new PaymentForm(role).setVisible(true);
    }//GEN-LAST:event_btnPaymentsActionPerformed

    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        new ReportForm(role).setVisible(true);
    }//GEN-LAST:event_btnReportActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
            new LoginForm().setVisible(true);
            this.dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new DashboardForm("admin").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel JLabel;
    private javax.swing.JLabel JLabel1;
    private javax.swing.JLabel JLabel2;
    private javax.swing.JLabel JLabel3;
    private javax.swing.JLabel JLabel4;
    private javax.swing.JButton btnCustomers;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPayments;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnReservations;
    private javax.swing.JButton btnRooms;
    private javax.swing.JLabel lblAvailable;
    private javax.swing.JLabel lblBannerTitle;
    private javax.swing.JLabel lblCustomers;
    private javax.swing.JLabel lblReservations;
    private javax.swing.JLabel lblRevenue;
    private javax.swing.JLabel lblRole;
    private javax.swing.JLabel lblRooms;
    private javax.swing.JPanel pnlAvailable;
    private javax.swing.JPanel pnlBanner;
    private javax.swing.JPanel pnlCustomers;
    private javax.swing.JPanel pnlReservations;
    private javax.swing.JPanel pnlRevenue;
    private javax.swing.JPanel pnlRooms;
    // End of variables declaration//GEN-END:variables
}
