import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.*;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.text.SimpleDateFormat;
import javax.mail.*;
import javax.mail.internet.*;


public class atm {

    private Frame fr, fr2, withdrawFrame, depositFrame, transferFrame, transactionHistoryFrame , registerFrame , registerNamePass;
    private Label accno, pass, withdraw, deposit, transfer, history, withdrawFrameLabel, withdrawSuccess,
            depositFrameLabel, depositSuccess, transferFrameAccnoLabel, transferFrameAmtLabel, transferFrameSuccess , registerFrameEmailLabel , registerFrameOtpLabel, RNPNameLabel,RNPPassLabel,RNPConfirmPassLabel;
    private TextField accTextField, passTextField, withdrawFrametf, depositFrametf, transferFrameacnoTf,
            transferFrameamtTf , registerFrameEmailTf , registerFrameOtpTf, RNPNameTf,RNPPassTf,RNPConfirmPassTf;
    private Button loginButton, withdrawbtn, depositbtn, transferbtn, historybtn, fr2exitbtn, withdrawFrameBtn,
            backToLogin, depositFrameBtn, transferFrameBtn, tfExitBtn , registerBtn , sendMailBtn , authenticateOtpBtn , createAccBtn;
    private JScrollPane jsp;
    private JTable t;
    private String[][] data;
    private String columns[] = { "Account Number", "Balance", "Transaction Type", "Transaction Amount",
            "Transaction Time", "Transaction ID" };

    double balance, updatedBal, withDrawAmt, depositAmt, transferAmt, senderBal, receiverBal;
    int accnoint = 0, newaccnoint , randomNum , otp , max;
    String userpassword = "", query = "", date , registerEmail ;

    int registerAccno;
    String registerName,registerPassword, registerConfirmPassword;
    
    
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    Random r= new Random();
    Withdraw wobj = new Withdraw();
    Deposit dobj = new Deposit();
    Transfer tobj = new Transfer();
    TransactionHistory tHist = new TransactionHistory();
    Email email = new Email();
    
    
    
    public atm() {
    	
    	try {
    		Class.forName("org.postgresql.Driver");
    		con = DriverManager.getConnection("jdbc:postgresql://localhost/atm", "postgres","anurag");
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}

        // Frame 1 : user authentication page
        fr = new Frame("ATM Interface");
        fr.setSize(500, 500);
        fr.setLayout(null);
        backToLogin = new Button("<< Back to login page ");

        accno = new Label("Account No :- ");
        accno.setBounds(20, 50, 100, 30);
        accTextField = new TextField(30);
        accTextField.setBounds(120, 50, 300, 30);
        
        registerBtn = new Button("Register");
        registerBtn.setBounds(150, 175, 100, 30);
        registerBtn.addActionListener(new ActionListener () {
        	public void actionPerformed(ActionEvent ae) {
        		fr.setVisible(false);
        		registerFrame.setVisible(true);
        	}
        });

        pass = new Label("Password :- ");
        pass.setBounds(20, 100, 100, 30);
        passTextField = new TextField(30);
        passTextField.setBounds(120, 100, 300, 30);
        passTextField.setEchoChar('*');

        loginButton = new Button("Login");
        loginButton.setBounds(20, 175, 100, 30);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String accountNumber = accTextField.getText();
                String userenpassword = passTextField.getText();
                if (accountNumber.isEmpty()) {
                    JOptionPane.showMessageDialog(fr, "Please enter Account Number", "Warning",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (userenpassword.isEmpty()) {
                    JOptionPane.showMessageDialog(fr, "Please enter Password", "Warning",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {

                    // Updated the class variables
                    try {
                        accnoint = Integer.parseInt(accountNumber);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(fr, "Please enter account number", "Warning",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    userpassword = userenpassword;

                    if (authenticate(accnoint, userpassword , fr)) {
                        JOptionPane.showMessageDialog(fr, "Authentication Successful", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        fr.setVisible(false);
                        fr2.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(fr, "Authentication Failed", "Warning",
                                JOptionPane.INFORMATION_MESSAGE);

                    }
                }
            }
        });
        
        fr.add(accno);
        fr.add(accTextField);
        fr.add(pass);
        fr.add(passTextField);
        fr.add(loginButton);
        fr.add(registerBtn);
        fr.setVisible(true);
        fr.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        // Frame 2 :- User activity page

        fr2 = new Frame("Services");
        fr2.setSize(500, 500);
        fr2.setLayout(null);

        history = new Label("Transaction History :");
        withdraw = new Label("Withdraw :");
        deposit = new Label("Desposit :");
        transfer = new Label("Transfer :");

        historybtn = new Button("Proceed>>");
        withdrawbtn = new Button("Proceed>>");
        depositbtn = new Button("Proceed>>");
        transferbtn = new Button("Proceed>>");
        fr2exitbtn = new Button("Exit >>");

        withdrawbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                fr2.setVisible(false);
                fr.setVisible(false);
                withdrawFrame.setVisible(true);
                withdrawFrame.add(withdrawFrameBtn);
                withdrawFrame.add(withdrawFrameLabel);
                withdrawFrame.add(withdrawFrametf);
                withdrawFrame.remove(backToLogin);
                withdrawFrame.remove(withdrawSuccess);
            }
        });

        JPanel btnPanel = new JPanel();
        Button tHistbuttonExit = new Button("Exit>>");
        tHistbuttonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });
        btnPanel.add(tHistbuttonExit);
        btnPanel.add(backToLogin);



         // Transaction history frame
        transactionHistoryFrame = new Frame("Transaction History");
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        transactionHistoryFrame.setSize(d.width, d.height);
        transactionHistoryFrame.setLayout(new BorderLayout());
        transactionHistoryFrame.setVisible(false);
        t = new JTable(new DefaultTableModel(data, columns));
        jsp = new JScrollPane(t);
        t.setEnabled(false);


        historybtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                fr.setVisible(false);
                fr2.setVisible((false));
                withdrawFrame.setVisible(false);
                transferFrame.setVisible(false);
                depositFrame.setVisible(false);
                transactionHistoryFrame.setVisible(true);
                data = tHist.getTransactionHistory(query, accnoint,con,ps,rs);
                DefaultTableModel dtm = (DefaultTableModel) t.getModel();
                dtm.setDataVector(data, columns);

                // Set the text alignment to center and adjust columns width
                DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
                dtcr.setHorizontalAlignment(JLabel.CENTER);
                for (int i = 0; i < t.getColumnCount(); i++) {
                    t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
                    int maxWidth = 0;

                    for (int row = 0; row < t.getRowCount(); row++) {
                        TableCellRenderer cellRenderer = t.getCellRenderer(row, i);
                        Component comp = t.prepareRenderer(cellRenderer, row, i);
                        maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
                    }
                    TableColumn column = t.getColumnModel().getColumn(i);
                    column.setPreferredWidth(maxWidth);
                }

                transactionHistoryFrame.add(jsp, BorderLayout.CENTER);
                transactionHistoryFrame.add(btnPanel, BorderLayout.SOUTH);

            }
        });

        depositbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                fr.setVisible(false);
                fr2.setVisible(false);
                withdrawFrame.setVisible(false);
                depositFrame.setVisible(true);
                depositFrame.add(depositFrameBtn);
                depositFrame.add(depositFrameLabel);
                depositFrame.add(depositFrametf);
                depositFrame.remove(backToLogin);
                depositFrame.remove(withdrawSuccess);
            }
        });

        transferbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                fr.setVisible(false);
                fr2.setVisible(false);
                withdrawFrame.setVisible(false);
                depositFrame.setVisible(false);
                transferFrame.setVisible(true);
                transferFrame.add(transferFrameAmtLabel);
                transferFrame.add(transferFrameAccnoLabel);
                transferFrame.add(transferFrameacnoTf);
                transferFrame.add(transferFrameamtTf);
                transferFrame.add(transferFrameBtn);
                transferFrame.remove(transferFrameSuccess);
                transferFrame.remove(backToLogin);
                transferFrame.remove(tfExitBtn);
            }
        });

        fr2.add(history);
        fr2.add(withdraw);
        fr2.add(deposit);
        fr2.add(transfer);
        fr2.add(historybtn);
        fr2.add(withdrawbtn);
        fr2.add(depositbtn);
        fr2.add(transferbtn);
        fr2.add(fr2exitbtn);

        history.setBounds(20, 50, 130, 30);
        withdraw.setBounds(20, 100, 120, 30);
        deposit.setBounds(20, 150, 120, 30);
        transfer.setBounds(20, 200, 120, 30);

        historybtn.setBounds(200, 50, 100, 30);
        withdrawbtn.setBounds(200, 100, 100, 30);
        depositbtn.setBounds(200, 150, 100, 30);
        transferbtn.setBounds(200, 200, 100, 30);
        fr2exitbtn.setBounds(20, 275, 100, 25);

        fr2exitbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });

        fr2.setVisible(false);

        backToLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                withdrawFrame.setVisible(false);
                fr2.setVisible(false);
                depositFrame.setVisible(false);
                transferFrame.setVisible(false);
                registerFrame.setVisible(false);
                fr.setVisible(true);
                accnoint = 0;
                userpassword = "";
                query = "";
                accTextField.setText("");
                passTextField.setText("");
                withdrawFrametf.setText("");
                depositFrametf.setText("");
                transferFrameacnoTf.setText("");
                transferFrameamtTf.setText("");
                registerFrameEmailTf.setText("");
                registerFrameOtpTf.setText("");
                RNPNameTf.setText("");
                RNPPassTf.setText("");
                RNPConfirmPassTf.setText("");
                
                DefaultTableModel dtm = (DefaultTableModel) t.getModel();
                dtm.setRowCount(0);
                data = null;
                btnPanel.add(backToLogin);
                transactionHistoryFrame.setVisible(false);
            }
        });

        // Withdraw amt frame

        withdrawFrame = new Frame("Withdraw");
        withdrawFrame.setTitle("WithDraw Amount");
        withdrawFrame.setSize(500, 500);
        withdrawFrame.setLayout(null);

        Button withdrawexitbtn = new Button("Exit>>");
        withdrawexitbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });

        withdrawFrameLabel = new Label("Withdraw Amount :- ");
        withdrawFrametf = new TextField(30);
        withdrawFrameBtn = new Button("Proceed>> ");
        withdrawSuccess = new Label("Withdrawn Successfully");

        withdrawFrameLabel.setBounds(20, 50, 130, 30);
        withdrawFrametf.setBounds(200, 50, 150, 30);
        withdrawFrameBtn.setBounds(20, 100, 100, 30);

        withdrawFrame.add(withdrawFrameLabel);
        withdrawFrame.add(withdrawFrametf);
        withdrawFrame.add(withdrawFrameBtn);
        withdrawexitbtn.setBounds(200, 100, 100, 30);
        withdrawFrame.add(withdrawexitbtn);

        withdrawFrameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    // Get withdraw amt from gui application
                	try {
                		withDrawAmt = Double.parseDouble(withdrawFrametf.getText());
                		// Check if withdraw amount is in multiples of 100
                        if (withDrawAmt < 0 || withDrawAmt == 0 || withDrawAmt < 100 || withDrawAmt % 100 != 0) {
                            JOptionPane.showMessageDialog(withdrawFrame, "Please enter amount in multiples of 100 ",
                                    "Warning", JOptionPane.INFORMATION_MESSAGE);
                        }
                        // If withdraw amount is in multiple of 100 call the withdraw function from the
                        // withdraw class
                        else if (withDrawAmt % 100 == 0) {
                            // Perform the actual withdraw calculation
                        	date=getDate();
                            wobj.withdraw(con, query, accnoint, balance, withDrawAmt, updatedBal, date , ps , rs);
                            JOptionPane.showMessageDialog(withdrawFrame, "Withdrawn Successfully ", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            withdrawFrame.remove(withdrawFrameBtn);
                            withdrawFrame.remove(withdrawFrameLabel);
                            withdrawFrame.remove(withdrawFrametf);
                            withdrawSuccess.setBounds(20, 50, 150, 30);
                            backToLogin.setBounds(20, 100, 130, 30);
                            withdrawFrame.add(backToLogin);
                            withdrawFrame.add(withdrawSuccess);
                        } else {
                            System.out.println("Failed");
                        }
                	}
                	catch(NumberFormatException e) {
                		JOptionPane.showMessageDialog(withdrawFrame , "Please enter the amount in number ","Warning",JOptionPane.INFORMATION_MESSAGE);
                	}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        withdrawFrame.setVisible(false);

        // Deposit frame ;-
        depositFrame = new Frame("Deposit");
        depositFrame.setTitle("Deposit Amount");
        depositFrame.setSize(500, 500);
        depositFrame.setLayout(null);
        depositFrameLabel = new Label("Deposit Amount :- ");
        depositFrametf = new TextField(30);
        depositFrameBtn = new Button("Proceed>> ");
        depositSuccess = new Label("Deposited Successfully");
        depositFrameLabel.setBounds(20, 50, 130, 30);
        depositFrametf.setBounds(200, 50, 150, 30);
        depositFrameBtn.setBounds(20, 100, 100, 30);
        depositFrame.add(depositFrameLabel);
        depositFrame.add(depositFrametf);
        depositFrame.add(depositFrameBtn);
        fr2exitbtn.setBounds(200, 100, 100, 30);
        depositFrame.add(fr2exitbtn);
        depositFrame.setVisible(false);

        depositFrameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    depositAmt = Double.parseDouble(depositFrametf.getText());
                    if (depositAmt < 0 || depositAmt == 0 || depositAmt < 100) {
                        JOptionPane.showMessageDialog(depositFrame, "Please enter amount greater than 100 ", "Warning",JOptionPane.INFORMATION_MESSAGE);
                    } 
                    else if(depositAmt%100!=0){
                        JOptionPane.showMessageDialog(depositFrame, "Please enter amount in multiples of 100 ", "Warning",JOptionPane.INFORMATION_MESSAGE);
                    }
                    else  if(depositAmt%100==0){
                    	date=getDate();
                        dobj.deposit(con, query, accnoint, balance, depositAmt, updatedBal, date,ps,rs);
                        JOptionPane.showMessageDialog(depositFrame, "Deposited Successfully ", "Success",JOptionPane.INFORMATION_MESSAGE);
                        depositFrame.remove(depositFrameLabel);
                        depositFrame.remove(depositFrametf);
                        depositFrame.remove(depositFrameBtn);
                        depositSuccess.setBounds(20, 50, 150, 30);
                        backToLogin.setBounds(20, 100, 130, 30);
                        depositFrame.add(backToLogin);
                        depositFrame.add(depositSuccess);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Transfer frame
        transferFrame = new Frame("Transfer Amount");
        transferFrame.setTitle("Transfer Amount");
        transferFrame.setSize(500, 500);
        transferFrame.setLayout(null);

        transferFrameAmtLabel = new Label("Transfer Amount :- ");
        transferFrameAccnoLabel = new Label("Account number :- ");
        transferFrameSuccess = new Label("Transferred Successfully");
        transferFrameacnoTf = new TextField(30);
        transferFrameamtTf = new TextField(30);

        transferFrameBtn = new Button("Proceed>> ");
        tfExitBtn = new Button("Exit");
        tfExitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });
        transferFrameSuccess = new Label("Transfered Successfully");

        transferFrame.add(transferFrameAmtLabel);
        transferFrame.add(transferFrameAccnoLabel);
        transferFrame.add(transferFrameacnoTf);
        transferFrame.add(transferFrameamtTf);
        transferFrame.add(transferFrameBtn);

        transferFrameAccnoLabel.setBounds(20, 50, 150, 30);
        transferFrameacnoTf.setBounds(180, 50, 300, 30);
        transferFrameAmtLabel.setBounds(20, 100, 150, 30);
        transferFrameamtTf.setBounds(180, 100, 300, 30);
        transferFrameBtn.setBounds(20, 175, 130, 25);

        transferFrame.setVisible(false);

        transferFrameBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                    try {
                        newaccnoint = Integer.parseInt(transferFrameacnoTf.getText());
                        transferAmt = Double.parseDouble(transferFrameamtTf.getText());

                        // Get the balance for amount sender
                        query = "select balance from userinfo where accno=?";
                        ps = con.prepareStatement(query);
                        ps.setInt(1, accnoint);
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            senderBal = rs.getDouble(1);
                        }
                        if (transferAmt > senderBal) {
                            JOptionPane.showMessageDialog(transferFrame, "Account Balance Insufficient", "Warning",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                        else if(transferAmt%100!=0){
                            JOptionPane.showMessageDialog(transferFrame, "Please enter amount in multiples of 100", "Warning",JOptionPane.INFORMATION_MESSAGE);
                        }
                        else if (transferAmt <= senderBal && transferAmt % 100 == 0) {
                        	date=getDate();
                            tobj.transfer(senderBal, transferAmt, con, accnoint, query, date, newaccnoint, receiverBal,
                                    transferFrame,ps , rs);
                            JOptionPane.showMessageDialog(transferFrame, "Transferred Successfully ", "Success",
                                    JOptionPane.INFORMATION_MESSAGE);
                            transferFrame.remove(transferFrameAmtLabel);
                            transferFrame.remove(transferFrameAccnoLabel);
                            transferFrame.remove(transferFrameacnoTf);
                            transferFrame.remove(transferFrameamtTf);
                            transferFrame.remove(transferFrameBtn);
                            transferFrame.add(transferFrameSuccess);
                            transferFrameSuccess.setBounds(20, 50, 140, 30);
                            transferFrame.add(backToLogin);
                            backToLogin.setBounds(20, 100, 130, 30);
                            transferFrame.add(tfExitBtn);
                            tfExitBtn.setBounds(200, 100, 100, 30);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }	
            }
        });

       
        //Register Frame
        registerFrame = new Frame("Register Page");
        registerFrame.setSize(500,500);
        registerFrame.setLayout(null);
        registerFrame.setVisible(false);
        registerFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        registerFrameEmailLabel = new Label("Enter Email ID : ");
        registerFrameOtpLabel = new Label("Enter OTP : ");
        registerFrameEmailTf = new TextField(30);
        registerFrameOtpTf = new TextField(30);
        sendMailBtn = new Button("Send OTP>>");
        authenticateOtpBtn = new Button("Confirm OTP>>");
        
        registerFrameEmailLabel.setBounds(20,50,100,30);
        registerFrameEmailTf.setBounds(150,50,200,30);
        sendMailBtn.setBounds(20,130,100,30);
        
        registerFrameOtpTf.setBounds(150,50,200,30);
        authenticateOtpBtn.setBounds(20,130,130,30);
        registerFrameOtpLabel.setBounds(20,50,100,30);
        
        registerFrame.add(registerFrameEmailLabel);
        registerFrame.add(registerFrameEmailTf);
        registerFrame.add(sendMailBtn);
        
        sendMailBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		try {
        			registerEmail=registerFrameEmailTf.getText();
        			ps=con.prepareStatement("Select * from userinfo where email=?");
        			ps.setString(1, registerEmail);
        			rs = ps.executeQuery();
        			if(rs.next()) {
        				JOptionPane.showMessageDialog(registerFrame, "Email already exists....Please login ","Warning",JOptionPane.INFORMATION_MESSAGE);
        			}
        			else if(registerEmail.contains("@gmail.com") || registerEmail.contains("@yahoo.com") || registerEmail.contains("@live.com") ) {	
            			randomNum=r.nextInt(900000)+100000;
            			JOptionPane.showMessageDialog(registerFrame, "Please wait.... ","Warning",JOptionPane.INFORMATION_MESSAGE);
            			
            			sendMailBtn.setLabel("Please wait ...");
            			sendMailBtn.setEnabled(false);
            			email.sendEmailTo(registerEmail,randomNum);
            			sendMailBtn.setLabel("Send OTP>>");
            			sendMailBtn.setEnabled(true);
       
            			registerFrame.remove(sendMailBtn);
            			registerFrame.remove(registerFrameEmailTf);
            			registerFrame.remove(registerFrameEmailLabel);
            			
            			registerFrame.add(registerFrameOtpLabel);
            			registerFrame.add(authenticateOtpBtn);
            			registerFrame.add(registerFrameOtpTf);
            			
            			JOptionPane.showMessageDialog(registerFrame, "Email sent successfully ","Success",JOptionPane.INFORMATION_MESSAGE);
        			}
        			else {
            			JOptionPane.showMessageDialog(registerFrame, "Please enter valid email ","Warning",JOptionPane.INFORMATION_MESSAGE);        			
            		}
        		}
        		catch(Exception e) {
        			e.printStackTrace();
        		}
        	}
        });
        
        authenticateOtpBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae ) {
        		try {
        			otp = Integer.parseInt(registerFrameOtpTf.getText());
        			if(otp==randomNum) {
        				JOptionPane.showMessageDialog(registerFrame, "Email Verified","Success",JOptionPane.INFORMATION_MESSAGE);
        				
        				registerFrame.remove(registerFrameOtpLabel);
        				registerFrame.remove(authenticateOtpBtn);
        				registerFrame.remove(registerFrameOtpTf);
        				registerFrame.setVisible(false);
        				registerNamePass.setVisible(true);
        			}
        		}
        		catch(Exception e) {
        			JOptionPane.showMessageDialog(registerFrame, "Error..Please Try Again","Warning",JOptionPane.INFORMATION_MESSAGE); 
        		}
        	}
        });
        
        
        //Register Name and Password frame
        registerNamePass=new Frame("Register Details");
        registerNamePass.setSize(500,500);
        registerNamePass.setLayout(null);
        RNPNameLabel = new Label("Enter your name : ");
        RNPPassLabel = new Label("Create a password : ");
        RNPPassTf = new TextField(30);
        RNPNameTf = new TextField(30);
        RNPConfirmPassTf = new TextField(30);
        RNPConfirmPassLabel = new Label("Re-enter the password : ");
        createAccBtn = new Button("Create Account>>");
        
        RNPNameLabel.setBounds(20,50,130,30);
        RNPPassLabel.setBounds(20,100,130,30);
        RNPConfirmPassLabel.setBounds(20,150,155,30);
        
        RNPNameTf.setBounds(190, 50, 250, 30);
        RNPPassTf.setBounds(190, 100, 250, 30);
        RNPPassTf.setEchoChar('*');
        RNPConfirmPassTf.setEchoChar('*');
        RNPConfirmPassTf.setBounds(190, 150, 250, 30);
        
        createAccBtn.setBounds(20,220,130,30);
        
        registerNamePass.add(RNPNameLabel);
        registerNamePass.add(RNPPassLabel);
        registerNamePass.add(RNPConfirmPassLabel);
        registerNamePass.add(RNPNameTf);
        registerNamePass.add(RNPPassTf);
        registerNamePass.add(RNPConfirmPassTf);
        registerNamePass.add(createAccBtn);
        registerNamePass.setVisible(false);
        
        createAccBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae ) {
        		try {
        			registerName = RNPNameTf.getText();
        			registerPassword = RNPPassTf.getText();
        			registerConfirmPassword = RNPConfirmPassTf.getText();
        			if(registerPassword!=null) {
        				if(registerPassword.length()>6) {
        					if(registerPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
        						if(registerPassword.equals(registerConfirmPassword)) {
        							try {
        								//Get max accno from database to give maxaccno+1 accno to newly registered user
        								query="select max(accno) from userinfo";
        								ps = con.prepareStatement(query);
        								rs = ps.executeQuery();
        								while(rs.next()) {
        									max = rs.getInt(1);
        								}
        								
        								//Insert the new acount holder details in the database
        								ps=con.prepareStatement("insert into userinfo values(?,?,?,?,?)");
        								ps.setInt(1, max+1);
        								ps.setString(2,registerPassword);
        								ps.setDouble(3, 0.00);
        								ps.setString(4,registerName);
        								ps.setString(5,registerEmail);
        								ps.executeUpdate();
        								
        								JOptionPane.showMessageDialog(registerNamePass, "Account Successfully Created \n Account number generated : " + (max+1),"Success",JOptionPane.INFORMATION_MESSAGE);
        								JOptionPane.showMessageDialog(registerNamePass, "Redirecting to Login","Redirect",JOptionPane.INFORMATION_MESSAGE);
        								registerNamePass.setVisible(false);
        								fr.setVisible(true);
        							}
        							catch(Exception sql) {
        								JOptionPane.showMessageDialog(registerNamePass, "Database Connection Problem","Warning",JOptionPane.INFORMATION_MESSAGE);
        							}
        						}
        						else {
        							JOptionPane.showMessageDialog(registerNamePass, "Passwords do not match","Warning",JOptionPane.INFORMATION_MESSAGE); 
        						}
        					}
        					else {
        						JOptionPane.showMessageDialog(registerNamePass, "Password should contain one special character","Warning",JOptionPane.INFORMATION_MESSAGE); 
        					}
        				}
        				else {
        					JOptionPane.showMessageDialog(registerNamePass, "Password length should be greater than 6 characters","Warning",JOptionPane.INFORMATION_MESSAGE); 
        				}
        			}
        			else {
        				JOptionPane.showMessageDialog(registerNamePass, "Password should not be empty","Warning",JOptionPane.INFORMATION_MESSAGE); 
        			}
        		}
        		catch(Exception e) {
        			e.printStackTrace();
        		}
        	}
        });

    }

    public boolean authenticate(int username, String password , Frame fr) {
        try {
            String sql = "select COUNT(*) from userinfo where accno=? and password=?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            // if resultset has only one row with entered accno this whole function returns
            // true
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    return true;
                }
                else{
                    JOptionPane.showMessageDialog(fr,"Invalid Account Number or Password");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to get date
    public String getDate() {
        Date d = new Date();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        String dateStr = sd.format(d);
        return dateStr;
    }

    public static void main(String[] args) {
        new atm();
    }
}


class Withdraw {
    public void withdraw(Connection con, String query, int accnoint, Double balance, Double withDrawAmt,
            Double updatedBal, String date , PreparedStatement ps , ResultSet rs ) {
        // Get balance from userinfo table and update it
        try {
            query = "select balance from userinfo where accno=?";
            ps = con.prepareStatement(query);
            ps.setInt(1, accnoint);
            rs = ps.executeQuery();
            while (rs.next()) {
                balance = rs.getDouble(1);
                updatedBal = balance - withDrawAmt;
            }

            // Set the updated balance in the userinfo table
            query = "update userinfo set balance=? where accno=?";
            ps = con.prepareStatement(query);
            ps.setDouble(1, updatedBal);
            ps.setInt(2, accnoint);
            ps.executeUpdate();

            // Set the updated balance in the transaction table for recording transaction
            // history
            query = "insert into usertransaction values (?,?,?,?,?)";
            ps = con.prepareStatement(query);
            ps.setInt(1, accnoint);
            ps.setDouble(2, updatedBal);
            ps.setString(3, "Withdraw");
            ps.setDouble(4, withDrawAmt);
            ps.setString(5, date);
            ps.executeUpdate();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}


class Deposit {
    public void deposit(Connection con, String query, int accnoint, Double balance, Double depositAmt,
            Double updatedBal, String date , PreparedStatement ps , ResultSet rs) {
        try {
            // Get user balance
            query = "select balance from userinfo where accno=?";
            ps = con.prepareStatement(query);
            ps.setInt(1, accnoint);
            rs = ps.executeQuery();
            while (rs.next()) {
                balance = rs.getDouble(1);
                updatedBal = balance + depositAmt;
            }

            // Set the updated balance in the userinfo table
            query = "update userinfo set balance=? where accno=?";
            ps = con.prepareStatement(query);
            ps.setDouble(1, updatedBal);
            ps.setInt(2, accnoint);
            ps.executeUpdate();

            // Update transaction table
            query = "insert into usertransaction values (?,?,?,?,?)";
            ps = con.prepareStatement(query);
            ps.setInt(1, accnoint);
            ps.setDouble(2, updatedBal);
            ps.setString(3, "Deposit");
            ps.setDouble(4, depositAmt);
            ps.setString(5, date);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


class Transfer {
    public void transfer(Double senderBal, Double transferAmt, Connection con, int accnoint, String query, String date,
            int newaccnoint, Double receiverBal, Frame transferFrame , PreparedStatement ps , ResultSet rs) {
        // Reduce the transferamt from sender's balance
        try {
            senderBal = senderBal - transferAmt;
            query = "update userinfo set balance=? where accno=?";
            ps = con.prepareStatement(query);
            ps.setDouble(1, senderBal);
            ps.setInt(2, accnoint);
            ps.executeUpdate();

            // Insert sender transaction detail in userTransaction table
            query= "insert into usertransaction values (?,?,?,?,?)";
            ps = con.prepareStatement(query);
            ps.setInt(1, accnoint);
            ps.setDouble(2, senderBal);
            ps.setString(3, "Transfer");
            ps.setDouble(4, transferAmt);
            ps.setString(5, date);
            ps.executeUpdate();
            

            // Get the balance for amount reciever
            query = "select balance from userinfo where accno=?";
            ps = con.prepareStatement(query);
            ps.setInt(1, newaccnoint);
            rs = ps.executeQuery();
            while (rs.next()) {
                receiverBal = rs.getDouble(1);
                JOptionPane.showMessageDialog(transferFrame, "Transfering to the Accno number : " + newaccnoint,
                        "Processing", JOptionPane.INFORMATION_MESSAGE);
            }

            // Add the transferamt to reciever's balance
            receiverBal = receiverBal + transferAmt;
            query = "update userinfo set balance=? where accno=?";
            ps = con.prepareStatement(query);
            ps.setDouble(1, receiverBal);
            ps.setInt(2, newaccnoint);
            ps.executeUpdate();
            
            //Insert transaction record of reciever to recieved
            ps=con.prepareStatement("insert into usertransaction values (?,?,?,?,?)");
            ps.setInt(1, newaccnoint);
            ps.setDouble(2, receiverBal);
            ps.setString(3, "Received");
            ps.setDouble(4, transferAmt);
            ps.setString(5, date);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


class TransactionHistory {
    String transactionData[][];
    public String[][] getTransactionHistory(String query, int accnoint,Connection con , PreparedStatement ps , ResultSet rs) {
        try {
            query = "select * from usertransaction where accno=?";
            ps = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, accnoint);
            rs = ps.executeQuery();

            // Get column count
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            int rowCount = 0;
            int row = 0;

            while (rs.next()) {
                rowCount++;
            }
            // Bring the cursor in the starting of the resultset
            rs.beforeFirst();
            transactionData = new String[rowCount][columnCount];

            while (rs.next()) {
                int acno = rs.getInt(1);
                Double bal = rs.getDouble(2);
                String tType = rs.getString(3);
                Double tAmt = rs.getDouble(4);
                String time = rs.getString(5);
                int tID = rs.getInt(6);

                transactionData[row][0] = String.valueOf(acno);
                transactionData[row][1] = String.valueOf(bal);
                transactionData[row][2] = tType;
                transactionData[row][3] = String.valueOf(tAmt);
                transactionData[row][4] = time;
                transactionData[row][5] = String.valueOf(tID);

                row++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactionData;
    }
}


class Email{
	void sendEmailTo(String reciever , int randomNum) {
		String senderMail = "anuragsutar33@gmail.com";
		String password = "vwdkoxrcyeygdnpe";
		String host = "smtp.gmail.com";
		
		Properties props = System.getProperties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port","587");
		props.put("mail.smtp.auth", "true");
		
		Session session = Session.getInstance(props , new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication (senderMail, password);
			}
		});
		
		try {
			MimeMessage ms = new MimeMessage(session);
			ms.setFrom(new InternetAddress(senderMail));
			ms.setRecipients(Message.RecipientType.TO , InternetAddress.parse(reciever));
			ms.setSubject("OTP Verification");
			ms.setText("Thank you for registering with us \n Your OTP for authentication is : "+randomNum);
			Transport.send(ms);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

