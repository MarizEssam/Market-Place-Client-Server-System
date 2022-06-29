import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Scanner;
 

public class Server {

    static final int PORT = 1978;
    static int client_counts = 0;
    public static int order_id = 0;
    public static EchoThread thrd;

    public static Connection con = null;
    public static Statement stmt= null;
    public static PreparedStatement preparedStmt = null;


    public static void main(String args[]) throws IOException{
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.print("Server is Running");
            InetAddress iAddress = InetAddress.getLocalHost();
            System.out.println(" with IP Address: " + iAddress.getHostAddress());
            Class.forName("com.mysql.cj.jdbc.Driver");
            Scanner sc = new Scanner(System.in);
            System.out.print("Please Enter Database Password : ");         
            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/market place","root",sc.nextLine());
            System.out.println("Database is Connected successfully");
            stmt= con.createStatement();
            sc.close();
        } catch(SQLException s){
            System.out.println("Wrong Database Password, Please Try again");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        } 
        while (true) {
            try {
                socket = serverSocket.accept();
                client_counts++;
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
                serverSocket.close();
            }
            // new thread for a client
            thrd = new EchoThread(socket, client_counts);
            thrd.start();
        }  
    }
}

class EchoThread extends Thread {

    protected Socket socket;
    private int client_id;
    private ResultSet rs = null;
    private String Fname = null;
    private String Lname = null;
    private String email = null;
    private String password = null;
    private String mobile_num = null;
    private String birthday = null;
    private String address = null;
    private String gender = null;
    private int productID = 0;
    private String product_name = null;
    private String category = null;
    private float price = 0;
    private int quantity = 0;
    private int actual_quantity = 0;
    private int cart_id = 0;
    private String query = null;

    public EchoThread(Socket clientSocket, int client_counts) {
        this.socket = clientSocket;
        this.client_id = client_counts;
    }

    public void run() {
        InputStream inp = null;
        BufferedReader brinp = null;
        PrintWriter pr = null;
        String client_IP;

        /*BufferedImage brimg = null;
        BufferedOutputStream bufferedOutputStream = null;*/

        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
            pr = new PrintWriter(socket.getOutputStream());
            client_IP = brinp.readLine();
            /*for image sending
            OutputStream outputStream = socket.getOutputStream();
            bufferedOutputStream = new BufferedOutputStream(outputStream);*/
        } catch (IOException e) {
            return;
        }
        System.out.println("Client ( " + client_IP + " ) connected with ID: " + client_id);

        while (true) {
            try {
                String client_sent_message = brinp.readLine();
                System.out.println("Client " + client_id + " : " + client_sent_message);
                                 switch (client_sent_message) {

                    // User Interface Backend
                    case ("login"):
                        pr.println("Enter your email: ");   pr.flush();     email = brinp.readLine();
                        pr.println("Enter your Pass: ");    pr.flush();     password = brinp.readLine();

                        System.out.println("Email :" + email);
                        System.out.println("Password :" + password);

                        rs = Server.stmt.executeQuery("SELECT email, password FROM client WHERE email='" + email + "' AND password = '" + password + "';");
                        if (rs.next()) {
                            System.out.println("Successful Login"); pr.println("Successful Login"); pr.flush();
                        } else {
                            System.out.println("UnSuccessful login, Wrong info"); pr.println("UnSuccessful login, Wrong info"); pr.flush();
                        }
                        break;
                                        case ("register"):
                        pr.println("Enter your First Name: ");      pr.flush();     Fname = brinp.readLine();
                        pr.println("Enter your Last Name: ");       pr.flush();     Lname = brinp.readLine();
                        pr.println("Enter your Email: ");           pr.flush();     email = brinp.readLine();
                        pr.println("Enter your Password: ");        pr.flush();     password = brinp.readLine();
                        pr.println("Enter your Mobile Number: ");   pr.flush();     mobile_num = brinp.readLine();
                        pr.println("Enter your Birthday: ");        pr.flush();     birthday = brinp.readLine();
                        pr.println("Enter your gender: ");          pr.flush();     gender = brinp.readLine();
                        pr.println("Enter your address: ");         pr.flush();     address = brinp.readLine();

                        System.out.println("First Name :" + Fname);
                        System.out.println("Last Name :" + Lname);
                        System.out.println("Email :" + email);
                        System.out.println("Password :" + password);
                        System.out.println("Mobile Number :" + mobile_num);
                        System.out.println("Birthday :" + birthday);
                        System.out.println("gender :" + gender);
                        System.out.println("address :" + address);

                        query = "INSERT INTO `client` (`FName`,`LName`,`Email`,`password`,`Mobile`,`amount_of_money`,`BDay`,`gender`,`address`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        Server.preparedStmt = Server.con.prepareStatement(query);
                        Server.preparedStmt.setString(1, Fname);
                        Server.preparedStmt.setString(2, Lname);
                        Server.preparedStmt.setString(3, email);
                        Server.preparedStmt.setString(4, password);
                        Server.preparedStmt.setString(5, mobile_num);
                        Server.preparedStmt.setFloat(6, 0);
                        Server.preparedStmt.setString(7, birthday);
                        Server.preparedStmt.setString(8, gender);
                        Server.preparedStmt.setString(9, address);
                        Server.preparedStmt.execute();
                        System.out.println("User added");
                        pr.println("Done"); pr.flush();
                        break;
                    case ("change Password"):
                        query = "select password from client where email='" + email + "';";
                        rs = Server.stmt.executeQuery(query);
                        System.out.println("sql : " + query);
                        String new_psw, Entered_psw, real_psw = "";
                        if(rs.next()) real_psw = rs.getString(1);
                        pr.println("Enter Your Password");pr.flush();
                        Entered_psw = brinp.readLine();
                        if(real_psw.equals(Entered_psw)){
                            pr.println("Enter Your New Password"); pr.flush(); new_psw = brinp.readLine();
                            query = "update client set password = '" + new_psw + "' where email = '" + email + "';";
                            Server.preparedStmt = Server.con.prepareStatement(query);
                            System.out.println("sql : " + query);
                            Server.preparedStmt.execute();
                            pr.println("password changed"); pr.flush();
                            System.out.println("psw changed");
                        }
                        else {pr.println("Sorry, You Entered wrong psw"); pr.flush(); System.out.println("psw not changed");}
                        pr.println("Done"); pr.flush();
                        break;
