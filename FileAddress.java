package destello2;



import java.awt.Color;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FileAddress.java
 *
 * Created on 8 Jun, 2016, 6:48:06 PM
 */

/**
 *
 * @author SOURODEEP
 */
public class FileAddress extends javax.swing.JFrame {

    /**
	 * 
	 */
	DestelloDebugger d = new DestelloDebugger();
	private static final long serialVersionUID = 1L;
	/** Creates new form FileAddress */
    public FileAddress() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AddressTF = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        OkayBtn = new javax.swing.JButton();

        jLabel1.setText("Enter The Complete Path of The Hex File ");

        OkayBtn.setText("Okay");
        OkayBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkayBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(106, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(37, 37, 37)
                .addComponent(AddressTF, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76))
            .addGroup(layout.createSequentialGroup()
                .addGap(315, 315, 315)
                .addComponent(OkayBtn)
                .addContainerGap(355, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddressTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(66, 66, 66)
                .addComponent(OkayBtn)
                .addContainerGap(79, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OkayBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkayBtnActionPerformed
        String s = AddressTF.getText();
        
        d.loadInstMemory(s);
     long q =  d.startingPC;
        String  dissassembly[];
      dissassembly = d.disassemblyView(q);
   
     long r = d.time;
     String disView="";
     String currDissLine="";
     for (int i=0;i<4;i++){
     String s1 = Long.toHexString(q);
    String s2 = dissassembly[i];
         currDissLine = "0x"+s1+"\t \t \t"+s2+"\n";
         if(i==2){
             home.disTP.setForeground(Color.BLUE);
         }
         else
             home.disTP.setForeground(Color.BLACK);
         disView = disView+ currDissLine;
     q=q+4;
     }
     home.disTP.setText(disView );
        dispose();

    }//GEN-LAST:event_OkayBtnActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FileAddress().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AddressTF;
    private javax.swing.JButton OkayBtn;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

}

