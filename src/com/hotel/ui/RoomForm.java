/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.hotel.ui;



public class RoomForm extends javax.swing.JFrame {
    

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RoomForm.class.getName());

    private String role;
    private int selectedRoomId = -1;  // -1 = no row picked yet
    
    // Talks to the database through RoomController, not directly.
   // The Controller checks the data is valid first, then calls RoomDAO
    private com.hotel.controller.RoomController roomController = new com.hotel.controller.RoomController();
    
    public RoomForm( String Role) {
        this.role = role;
        initComponents();
         loadAllRooms();  // fills the table when screen opens
         
                 // Receptionist can only view, not edit
    if (role.equals("receptionist")) {
        txtRoomNumber.setEnabled(false);
        cmbStatus.setEnabled(false);
        btnUpdateNumber.setEnabled(false);
        btnUpdateStatus.setEnabled(false);
    }

    // When a row is clicked, fill the form fields with that row's data
    table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelectedRow());
    }
    
    // Fills the table with all 10 rooms.
// Goes through RoomController (not directly to the database) -
// the Controller asks RoomDAO for the data, we just display it here.
private void loadAllRooms() {
    java.util.List<com.hotel.model.Room> rooms = roomController.getAllRooms();

    javax.swing.table.DefaultTableModel model =
        (javax.swing.table.DefaultTableModel) table.getModel();
    model.setRowCount(0); // clear the table first

    for (com.hotel.model.Room r : rooms) {
        model.addRow(new Object[]{
            r.getId(), r.getRoomNumber(), r.getCategory(), r.getPricePerNight(), r.getStatus()
        });
    }
}

// When a table row is clicked, copy that row's data into the form.
private void fillFormFromSelectedRow() {
    int row = table.getSelectedRow();
    if (row == -1) return; // nothing selected

    javax.swing.table.DefaultTableModel model =
        (javax.swing.table.DefaultTableModel) table.getModel();

    selectedRoomId = (int) model.getValueAt(row, 0);          // column 0 = Room ID
    txtRoomNumber.setText((String) model.getValueAt(row, 1)); // column 1 = Room No.
    cmbStatus.setSelectedItem(model.getValueAt(row, 4));      // column 4 = Status
}

// Updates the room number. Validation + the actual update both
// happen inside RoomController - this method just calls it and
// shows whatever message comes back.
private void updateRoomNumber() {
    if (selectedRoomId == -1) {
        javax.swing.JOptionPane.showMessageDialog(this, "Select a room from the table first.");
        return;
    }
    String newNumber = txtRoomNumber.getText().trim();

    // Controller returns null if it worked, or an error message if not
    String error = roomController.updateRoomNumber(selectedRoomId, newNumber);

    if (error == null) {
        javax.swing.JOptionPane.showMessageDialog(this, "Room number updated.");
        loadAllRooms();
    } else {
        javax.swing.JOptionPane.showMessageDialog(this, error);
    }
}

// Updates the room status (Available/Booked). Same pattern as above.
private void updateRoomStatus() {
    if (selectedRoomId == -1) {
        javax.swing.JOptionPane.showMessageDialog(this, "Select a room from the table first.");
        return;
    }
    String newStatus = (String) cmbStatus.getSelectedItem();

    String error = roomController.updateStatus(selectedRoomId, newStatus);

    if (error == null) {
        javax.swing.JOptionPane.showMessageDialog(this, "Room status updated.");
        loadAllRooms();
    } else {
        javax.swing.JOptionPane.showMessageDialog(this, error);
    }
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBanner = new javax.swing.JPanel();
        lblBannerTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtRoomNumber = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        btnUpdateNumber = new javax.swing.JButton();
        btnUpdateStatus = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Room Management - Grand Palm Hotel");
        setBackground(new java.awt.Color(247, 245, 242));
        setSize(new java.awt.Dimension(800, 550));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlBanner.setBackground(new java.awt.Color(44, 62, 80));
        pnlBanner.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(201, 165, 92)));
        pnlBanner.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBannerTitle.setBackground(new java.awt.Color(201, 165, 92));
        lblBannerTitle.setFont(new java.awt.Font("Segoe UI Emoji", 1, 20)); // NOI18N
        lblBannerTitle.setForeground(new java.awt.Color(201, 165, 92));
        lblBannerTitle.setText("Room Management");
        pnlBanner.add(lblBannerTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Fixed Hotel Rooms: 4 Standard + 3 Deluxe + 3 Suite = 10 Rooms");
        pnlBanner.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 30, -1, -1));

        getContentPane().add(pnlBanner, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 838, 87));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Room ID", "Room No. ", "Category", "Price/Night", "Status"
            }
        ));
        jScrollPane1.setViewportView(table);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 750, 180));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel2.setText("Update Selected Room");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 160, -1));

        jLabel3.setText("Room Number:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 90, -1));
        jPanel1.add(txtRoomNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, 200, -1));

        jLabel4.setText("Status: ");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 100, -1, -1));

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Available", "Booked" }));
        jPanel1.add(cmbStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 100, 150, 20));

        btnUpdateNumber.setBackground(new java.awt.Color(44, 62, 80));
        btnUpdateNumber.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdateNumber.setText("Update Room Number");
        btnUpdateNumber.addActionListener(this::btnUpdateNumberActionPerformed);
        jPanel1.add(btnUpdateNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 53, 160, 30));

        btnUpdateStatus.setBackground(new java.awt.Color(201, 165, 92));
        btnUpdateStatus.setText("Update Status");
        btnUpdateStatus.addActionListener(this::btnUpdateStatusActionPerformed);
        jPanel1.add(btnUpdateStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 103, 120, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 300, 750, 150));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnUpdateNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateNumberActionPerformed
        updateRoomNumber();
    }//GEN-LAST:event_btnUpdateNumberActionPerformed

    private void btnUpdateStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateStatusActionPerformed
        updateRoomStatus();
    }//GEN-LAST:event_btnUpdateStatusActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        //java.awt.EventQueue.invokeLater(() -> new RoomForm("admin").setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnUpdateNumber;
    private javax.swing.JButton btnUpdateStatus;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBannerTitle;
    private javax.swing.JPanel pnlBanner;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtRoomNumber;
    // End of variables declaration//GEN-END:variables
}
