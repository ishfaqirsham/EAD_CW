package com.hotel.ui;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author sheha
 */
public class ReservationForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ReservationForm.class.getName());

   private String role;  // "admin" or "receptionist" - decides what this screen allows

// This screen talks to the database through ReservationController,
// never directly. The Controller does validation + business rules,
// then calls ReservationDAO underneath to actually run SQL.
private com.hotel.controller.ReservationController reservationController =
    new com.hotel.controller.ReservationController();

// Dates are typed as plain text like "2026-06-25" in the text fields.
// This FORMAT object knows how to convert that text into a real
// java.util.Date, and back again, using the yyyy-MM-dd pattern.
private static final java.text.SimpleDateFormat FORMAT =
    new java.text.SimpleDateFormat("yyyy-MM-dd");


    public ReservationForm(String role) {
        this.role = role;          // remember who's using this screen
        initComponents();        // builds the visual layout (NetBeans-managed)
        
        // Recalculate the preview total every time the check-out date changes
        txtCheckOut.addActionListener(e -> updateTotalPreview());
        // Also recalculate if the room selection changes
        cmbRoom.addActionListener(e -> updateTotalPreview());
        
    // Fill the dropdowns and table with live data as soon as the screen opens
    loadCustomersIntoDropdown();
    loadAvailableRoomsIntoDropdown();
    loadAllReservations();
    }
    
// Calculates and displays the total WITHOUT saving anything -
// just a live preview so the user sees the price before committing.
// Runs every time the check-in or check-out text changes.
private void updateTotalPreview() {
    try {
        Object selectedRoom = cmbRoom.getSelectedItem();
        if (selectedRoom == null) return; // no room picked yet, nothing to calculate

        int roomId = extractIdFromDropdown((String) selectedRoom);
        java.sql.Date checkIn = new java.sql.Date(FORMAT.parse(txtCheckIn.getText().trim()).getTime());
        java.sql.Date checkOut = new java.sql.Date(FORMAT.parse(txtCheckOut.getText().trim()).getTime());

        if (!checkOut.after(checkIn)) {
            lblTotal.setText("Rs. 0.00"); // invalid range, show zero instead of guessing
            return;
        }

        long nights = (checkOut.getTime() - checkIn.getTime()) / (1000L * 60 * 60 * 24); //1000millisec x 60 sec x 60 min * 24 hours

        // Look up the price directly here just for the preview -
        // this does NOT save anything to the database, purely visual
        java.sql.Connection con = com.hotel.util.DBConnection.getConnection();
        String sql = "SELECT c.price_per_night FROM room r "
                   + "JOIN room_category c ON r.category_id = c.category_id WHERE r.room_id = ?";
        java.sql.PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, roomId);
        java.sql.ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            double price = rs.getDouble("price_per_night");
            double total = nights * price;
            lblTotal.setText("Rs. " + total + " (" + nights + " night(s))");
        }

    } catch (Exception e) {
        lblTotal.setText("Rs. 0.00"); // dates not typed yet or invalid - just show zero, no error popup
    }
}

    // Fills the Customer dropdown with "id - name" for every customer,
// e.g. "3 - Kamala Jayasuriya". We store both the ID and the name
// together as one String, then split it back apart later when we
// need just the ID (see extractIdFromDropdown below).
private void loadCustomersIntoDropdown() {
    cmbCustomer.removeAllItems(); // clear out any old items first
    try {
        java.sql.Connection con = com.hotel.util.DBConnection.getConnection();
        java.sql.Statement st = con.createStatement();
        java.sql.ResultSet rs = st.executeQuery("SELECT customer_id, full_name FROM customer ORDER BY full_name");
        while (rs.next()) { // loop through every customer row returned
            cmbCustomer.addItem(rs.getInt("customer_id") + " - " + rs.getString("full_name"));
        }
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Could not load customers: " + e.getMessage());
    }
}

// Fills the Room dropdown with ONLY rooms that are currently
// "Available" - a room that's already Booked should never show up
// here, so the WHERE clause filters those out before we even see them.
private void loadAvailableRoomsIntoDropdown() {
    cmbRoom.removeAllItems();
    try {
        java.sql.Connection con = com.hotel.util.DBConnection.getConnection();
        java.sql.Statement st = con.createStatement();

        // JOIN brings in the category name and price from room_category,
        // since the room table itself only stores a category_id number.
        String sql = "SELECT r.room_id, r.room_number, c.category_name, c.price_per_night "
                   + "FROM room r JOIN room_category c ON r.category_id = c.category_id "
                   + "WHERE r.status = 'Available' ORDER BY r.room_number";
        java.sql.ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            // Build one readable line per room, e.g. "5 - D-201 - Deluxe - Rs.8500.0"
            String item = rs.getInt("room_id") + " - " + rs.getString("room_number")
                    + " - " + rs.getString("category_name") + " - Rs." + rs.getDouble("price_per_night");
            cmbRoom.addItem(item);
        }
    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Could not load rooms: " + e.getMessage());
    }
}

// Loads every reservation into the table at the bottom of the screen.
// Notice this method does NOT write any SQL itself - it just asks
// reservationController for the list, and the Controller asks the
// DAO underneath. This screen never touches the database directly
// for reservation data, only the Controller does.
private void loadAllReservations() {
    java.util.List<com.hotel.model.Reservation> reservations = reservationController.getAllReservations();

    // Every JTable has a "model" behind it that actually holds the
    // row data. We grab that model so we can clear it and add new rows.
    javax.swing.table.DefaultTableModel model =
        (javax.swing.table.DefaultTableModel) table.getModel();
    model.setRowCount(0); // wipe out whatever rows were there before

    for (com.hotel.model.Reservation r : reservations) {
        model.addRow(new Object[]{
            r.getId(), r.getCustomerId(), r.getRoomId(),
            r.getCheckInDate(), r.getCheckOutDate(), r.getTotalAmount(), r.getStatus()
        });
    }
}

// Dropdown items look like "5 - John Doe". This method splits that
// text on " - " and grabs just the first piece (the ID number),
// then converts it from String to int so we can use it in SQL.
private int extractIdFromDropdown(String dropdownText) {
    try {
        return Integer.parseInt(dropdownText.split(" - ")[0].trim());
    } catch (NumberFormatException e) {
        // This would only happen if the dropdown text was somehow
        // malformed - shown as a safety net, not expected to trigger normally.
        javax.swing.JOptionPane.showMessageDialog(this, "Could not read the selected item's ID.");
        return -1;
    }
}

//   ==================================
// THE MAIN TRANSACTION of the whole system.
// Flow: read the form -> ask the Controller to create the booking
// -> the Controller checks dates, checks for conflicts, calculates
// the price, and saves it -> if anything's wrong, it throws our
// CUSTOM EXCEPTION (InvalidBookingException) instead of returning
// a normal value, which we catch below and show to the user.
// ==============================================

private void reserveRoom() {

    Object selectedCustomer = cmbCustomer.getSelectedItem();
    Object selectedRoom = cmbRoom.getSelectedItem();

    // Guard against NullPointerException: if nothing is picked yet,
    // getSelectedItem() returns null - check for that BEFORE using it.
    if (selectedCustomer == null) {
        javax.swing.JOptionPane.showMessageDialog(this, "Please select a customer.");
        return;
    }
    if (selectedRoom == null) {
        javax.swing.JOptionPane.showMessageDialog(this, "No available rooms to select.");
        return;
    }

    int customerId = extractIdFromDropdown((String) selectedCustomer);
    int roomId = extractIdFromDropdown((String) selectedRoom);
    if (customerId == -1 || roomId == -1) return; // error already shown above

    java.sql.Date checkIn, checkOut;
    try {
        // Convert the typed text (like "2026-06-25") into real Date objects.
        checkIn = new java.sql.Date(FORMAT.parse(txtCheckIn.getText().trim()).getTime());
        checkOut = new java.sql.Date(FORMAT.parse(txtCheckOut.getText().trim()).getTime());
    } catch (Exception e) {
        // This catches both empty text fields and badly-typed dates
        // (e.g. "25/06/2026" instead of "2026-06-25")
        javax.swing.JOptionPane.showMessageDialog(this, "Enter valid dates in yyyy-MM-dd format.");
        return;
    }

    try {
        // Hand everything off to the Controller. It will:
        //   1) check check-out is after check-in
        //   2) check the room isn't already booked for these dates
        //   3) calculate nights x price-per-night
        //   4) save the reservation and mark the room as Booked
        // If steps 1 or 2 fail, it throws InvalidBookingException
        // instead of returning a number - that's why this whole
        // call sits inside a try block.
        double total = reservationController.createReservation(customerId, roomId, checkIn, checkOut);

        javax.swing.JOptionPane.showMessageDialog(this, "Reservation created. Total: Rs. " + total);
        
        // Show the final total on screen (don't reset it to zero anymore)
        lblTotal.setText("Rs. " + total);

        // Reset the form and refresh the screen's data
        txtCheckIn.setText("");
        txtCheckOut.setText("");
        loadAvailableRoomsIntoDropdown(); // the just-booked room disappears from this list
        loadAllReservations();            // the new reservation appears in the table

    } catch (com.hotel.exception.InvalidBookingException ex) {
        // ex.getMessage() contains whichever specific error the
        // Controller decided to throw (bad dates, or room conflict).
        javax.swing.JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Booking", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}

// Cancels a reservation and frees its room back to "Available".
private void cancelReservation() {

    // Double safety check - the button is already disabled for
    // receptionist, but we check again here too in case this method
    // is ever called another way.
    if (role.equals("receptionist")) {
        javax.swing.JOptionPane.showMessageDialog(this, "Only Admin can cancel reservations.");
        return;
    }

    int row = table.getSelectedRow();
    if (row == -1) {
        javax.swing.JOptionPane.showMessageDialog(this, "Select a reservation from the table first.");
        return;
    }

    // Read the reservation ID and current status straight from the
    // clicked table row (column 0 = ID, column 6 = Status)
    int reservationId = (int) table.getValueAt(row, 0);
    String currentStatus = (String) table.getValueAt(row, 6);

    if ("Cancelled".equals(currentStatus)) {
        javax.swing.JOptionPane.showMessageDialog(this, "This reservation is already cancelled.");
        return;
    }

    // Ask for confirmation before doing anything irreversible
    int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
            "Cancel this reservation? The room will become available again.",
            "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
    if (confirm != javax.swing.JOptionPane.YES_OPTION) return; // user clicked No

    // The Controller handles both: marking the reservation Cancelled,
    // AND setting the room back to Available - we don't do that here.
    String error = reservationController.cancelReservation(reservationId);

    if (error == null) { // null means it worked
        javax.swing.JOptionPane.showMessageDialog(this, "Reservation cancelled.");
        loadAvailableRoomsIntoDropdown(); // the freed room reappears in this list
        loadAllReservations();
    } else {
        javax.swing.JOptionPane.showMessageDialog(this, error);
    }
}


// Marks the selected reservation as Completed (guest stayed and
// checked out normally) and frees the room back to Available.
private void completeReservation() {

    int row = table.getSelectedRow();
    if (row == -1) {
        javax.swing.JOptionPane.showMessageDialog(this, "Select a reservation from the table first.");
        return;
    }

    int reservationId = (int) table.getValueAt(row, 0);
    String currentStatus = (String) table.getValueAt(row, 6);

    // Only an Active reservation makes sense to "complete" -
    // can't complete something already Cancelled or already Completed
    if (!"Active".equals(currentStatus)) {
        javax.swing.JOptionPane.showMessageDialog(this, "Only an Active reservation can be marked as completed.");
        return;
    }

    int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
            "Mark this reservation as completed? The room will become available again.",
            "Confirm", javax.swing.JOptionPane.YES_NO_OPTION);
    if (confirm != javax.swing.JOptionPane.YES_OPTION) return;

    String error = reservationController.completeReservation(reservationId);

    if (error == null) {
        javax.swing.JOptionPane.showMessageDialog(this, "Reservation marked as completed.");
        loadAvailableRoomsIntoDropdown(); // the freed room reappears in this list
        loadAllReservations();
    } else {
        javax.swing.JOptionPane.showMessageDialog(this, error);
    }
}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBanner = new javax.swing.JPanel();
        lblBannerTitle = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCheckIn = new javax.swing.JTextField();
        cmbCustomer = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cmbRoom = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtCheckOut = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        btnReserve = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        btnCancel = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnComplete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reservation Management - Grand Palm Hotel");
        setBackground(new java.awt.Color(247, 245, 242));
        setSize(new java.awt.Dimension(950, 600));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlBanner.setBackground(new java.awt.Color(44, 62, 80));
        pnlBanner.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(201, 165, 92)));
        pnlBanner.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBannerTitle.setFont(new java.awt.Font("Segoe UI Semibold", 1, 20)); // NOI18N
        lblBannerTitle.setForeground(new java.awt.Color(201, 165, 92));
        lblBannerTitle.setText("Reservation Management");
        pnlBanner.add(lblBannerTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 260, 50));

        getContentPane().add(pnlBanner, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 890, 80));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel1.setText("New Reservation ");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 110, -1));

        jLabel2.setText("Customer");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));
        jPanel1.add(txtCheckIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 220, -1));

        jPanel1.add(cmbCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 260, -1));

        jLabel3.setText("Room (Available only):");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 40, -1, -1));

        jPanel1.add(cmbRoom, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 60, 270, -1));

        jLabel4.setText("Check-in (yyyy-MM-dd)");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, -1, -1));

        jLabel5.setText("Check-out (yyyy-MM-dd):");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 100, -1, -1));
        jPanel1.add(txtCheckOut, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 120, 210, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI Emoji", 1, 17)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(46, 125, 82));
        jLabel6.setText("Total Amount:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 130, 70));

        lblTotal.setFont(new java.awt.Font("Segoe UI Historic", 1, 17)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(46, 125, 82));
        lblTotal.setText("Rs. 0.00");
        jPanel1.add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 170, 190, 50));

        btnReserve.setBackground(new java.awt.Color(46, 125, 82));
        btnReserve.setForeground(new java.awt.Color(255, 255, 255));
        btnReserve.setText("Reserve Room");
        btnReserve.addActionListener(this::btnReserveActionPerformed);
        jPanel1.add(btnReserve, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 170, 150, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 830, 220));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Customer", "Room", "Check-in", "Check-out", "Total", "Status"
            }
        ));
        jScrollPane1.setViewportView(table);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, 780, 180));

        btnCancel.setBackground(new java.awt.Color(192, 57, 43));
        btnCancel.setForeground(new java.awt.Color(255, 255, 255));
        btnCancel.setText("Cancel Reservation");
        btnCancel.addActionListener(this::btnCancelActionPerformed);
        getContentPane().add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 530, -1, -1));

        btnRefresh.setBackground(new java.awt.Color(227, 224, 218));
        btnRefresh.setText("Refresh ");
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);
        getContentPane().add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 530, -1, -1));

        btnComplete.setBackground(new java.awt.Color(46, 125, 82));
        btnComplete.setForeground(new java.awt.Color(255, 255, 255));
        btnComplete.setText("Complete Reservation");
        btnComplete.addActionListener(this::btnCompleteActionPerformed);
        getContentPane().add(btnComplete, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 530, 160, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnReserveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReserveActionPerformed
        reserveRoom(); // runs the full booking logic below
    }//GEN-LAST:event_btnReserveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        cancelReservation(); // runs the cancel logic below
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
    // Re-fetch everything fresh from the database -
    // useful if data changed in another screen since this one opened
    loadCustomersIntoDropdown();
    loadAvailableRoomsIntoDropdown();
    loadAllReservations();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnCompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompleteActionPerformed
            completeReservation();
    }//GEN-LAST:event_btnCompleteActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new ReservationForm("admin").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnComplete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnReserve;
    private javax.swing.JComboBox<String> cmbCustomer;
    private javax.swing.JComboBox<String> cmbRoom;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBannerTitle;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlBanner;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtCheckIn;
    private javax.swing.JTextField txtCheckOut;
    // End of variables declaration//GEN-END:variables
}
