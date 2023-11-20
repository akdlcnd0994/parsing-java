import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import java.awt.Image;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.awt.event.WindowEvent;

import java.awt.event.WindowListener;

import java.io.BufferedReader;

import java.io.InputStreamReader;

import java.net.MalformedURLException;

import java.net.URL;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.SQLIntegrityConstraintViolationException;


import javax.swing.ImageIcon;

import javax.swing.JButton;

import javax.swing.JFrame;

import javax.swing.JLabel;

import javax.swing.JOptionPane;

import javax.swing.JPanel;


class mealFrame extends JFrame implements ActionListener, WindowListener {

    ImageIcon img;

    JPanel p = new JPanel();

    JPanel p2 = new JPanel();

    JButton b[] = new JButton[35];

    parsing pa = new parsing();

    DB db = new DB();

    

    mealFrame() {

        addWindowListener(this);

        this.setTitle("2022년 6월 식단표");

        this.setSize(500, 500);

        this.setLayout(new BorderLayout());

        this.add(p2, "North");

        this.add(p, "Center");

        p.setLayout(new GridLayout(5, 7, 2, 2));

        p2.setLayout(new GridLayout(1, 7, 2, 2));

        labelSet();

        buttonSet();

        this.setVisible(true);

        

        db.connectDB();

    }

    

    void labelSet() {

        

        String[] arr = {
            "일",
            "월",
            "화",
            "수",
            "목",
            "금",
            "토"
        };

        

        p2.setSize(200, 200);

        

        for (int i = 0; i < 7; i++) {

            JButton b = new JButton(arr[i]);

            b.setBackground(new Color(255, 128, 0));

            b.setSize(50, 50);

            p2.add(b);

        }

        

    }

    

    void buttonSet() {

        

        int[] num = {
            29,
            30,
            31,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15,
            16,
            17,
            18,
            19,
            20,
            21,
            22,
            23,
            24,

            25,
            26,
            27,
            28,
            29,
            30,
            1,
            2
        };

        int[] weight = {
            -11,
            -11,
            -11,
            -11,
            0,
            1,
            -11,
            -11,
            -11,
            2,
            3,
            4,
            5,
            -11,
            -11,
            6,
            7,
            8,
            9,
            10,
            -11,
            -11,
            11,

            12,
            13,
            14,
            15,
            -11,
            -11,
            16,
            17,
            18,
            19,
            -11,
            -11
        };

        for (int i = 0; i < 35; i++) {

            JButton temp = new JButton(String.valueOf(num[i]));

            temp.setSize(10, 10);

            temp.setActionCommand(String.valueOf(weight[i]));

            b[i] = temp;

            b[i].addActionListener(this);

            p.add(b[i]);

            

        }

        

    }

    

    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("-11")) {

            JOptionPane.showMessageDialog(this, "공휴일이거나 6월달에 해당하지 않습니다.");

        } else {

            pa.parse(Integer.parseInt(e.getActionCommand()));

            

        }

    }

    

    class foodFrame extends JFrame {

        

        int day = 2;

        JPanel p = new JPanel();
        JLabel date = new JLabel();
        

        foodFrame(String[] temp, int num) {

            day += num;

            this.setSize(500, 420);

            this.setTitle("급식");

            this.setVisible(true);

            this.setLayout(new BorderLayout());
            
            date.setText(String.valueOf(day)+"일 급식");
            
            date.setFont(new Font("고딕체", Font.BOLD,25));
            
            
            this.add(this.date,"North");
            this.add(p, "Center");

            p.setLayout(new GridLayout(temp.length-1, 1, 5, 5));

            

            for (int i = 0; i < temp.length; i++) {

                JLabel l = new JLabel(temp[i]);

                p.add(l);

            }

            this.add(new JLabel(img), "South");

        }

    }

    

    class parsing {

        void token(int date, String temp[]) throws SQLException {

            String[] result = new String[100];

            int start, end;

            ResultSet rs = null;

            String a;

            

            for (int i = 0; i < temp.length; i++) {

                temp[i] = temp[i].replaceAll("[0-9]", "");

                temp[i] = temp[i].replaceAll("[/,\\.]", "");

                temp[i] = temp[i].replaceAll("뷁", "\n");

                temp[i] = temp[i].replace("&nbsp;", "\n");

                while (temp[i].indexOf("(") != -1) {

                    start = temp[i].indexOf("(");

                    end = temp[i].indexOf(")");

                    a = temp[i].substring(start, end + 1);

                    temp[i] = temp[i].replace(a, "");

                }

                

                if (!temp[i].equals("")) {

                    try {

                        db.pstmt.setInt(1, date);

                        db.pstmt.setString(2, temp[i]);

                        db.pstmt.execute();

                        System.out.println(temp[i]);

                    } catch (SQLIntegrityConstraintViolationException e) {

                        System.out.println("사전데이터 준비되어있음");

                    }

                }

            }

            db.pstmt2.setInt(1, date);

            rs = db.pstmt2.executeQuery();

            

            String t = null;
            while (rs.next()) {
                t = rs.getString("food");
            }
            result = t.split("\n");
            foodFrame ff = new foodFrame(result, date);

        }

        

        public void parse(int date) {

            
            URL url = null;

            BufferedReader input = null;

            int num = 10483818;

            String address = "https://school.busanedu.net/yongsu-m/dv/dietView/selectDietDetailView.do?mi=695735&dietSeq="

                +
                (num + date) + "&sysId=yongsu-m";

            

            String line = "";

            String a = "";

            

            String[] temp;

            try {

                url = new URL(address);

                input = new BufferedReader(new InputStreamReader(url.openStream()));

                

                while ((line = input.readLine()) != null) {

                    a += line;

                }

            

                imgParsing ip = new imgParsing(a);

                a = a.replaceAll("\\s", "");

                a = a.replaceAll("<br/>", "");

                a = a.replaceAll(" ", "뷁");
                

                a = a.replaceAll("용", "");

                a = a.replaceAll("&nbsp;&nbsp;", "\n");
                
                temp = a.split("\"font_b\">");

                temp = temp[2].split("</td>");

                temp = temp[0].split("뷁뷁");

                token(date, temp);

                input.close();

                

            } catch (Exception e) {

                

                e.printStackTrace();

                

            }

            

        }

        

    }


    class imgParsing {

        String[] temp;

        String a;

        URL url;

        Image ch;
        


        imgParsing(String a) throws MalformedURLException {

            this.a = a;

            source();

        }

        void source() throws MalformedURLException {

            temp = a.split("yongsu-m/di/img");

            temp = temp[1].split(".jpg");

            String link = "https://school.busanedu.net/upload/yongsu-m/di/img" + temp[0] + ".jpg";

            img = new ImageIcon(new URL(link));

            ch = img.getImage();

            ch = ch.getScaledInstance(400, 200, ch.SCALE_SMOOTH);

            img = new ImageIcon(ch);

        }

    }

    static class DB {

        private static final String DB_DRIVER_CLASS = "org.mariadb.jdbc.Driver";

        private static final String DB_URL = "jdbc:mariadb://127.0.0.1:3306/academy";

        private static final String DB_USERNAME = "root";

        private static final String DB_PASSWORD = "root";

        private static Connection conn;

        static String SQL = "INSERT meal(num,food) VALUES(?,?)";

        static String SQL2 = "SELECT food from meal where num=?";

        public static PreparedStatement pstmt = null;

        public static PreparedStatement pstmt2 = null;
        

        private static void connectDB() {

            try {

                Class.forName(DB_DRIVER_CLASS);

                conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

                pstmt = conn.prepareStatement(SQL);

                pstmt2 = conn.prepareStatement(SQL2);

                System.out.println("연결성공");

            } catch (ClassNotFoundException e) {

                System.out.println("드라이브 로딩 실패");

            } catch (SQLException e) {

                System.out.println("연결 실패");

            }

        }
    }


    @Override

    public void windowOpened(WindowEvent e) {

        // TODO Auto-generated method stub

    }

    @Override

    public void windowClosed(WindowEvent e) {

    }

    @Override

    public void windowClosing(WindowEvent e) {

        System.exit(0);

    }


    @Override

    public void windowIconified(WindowEvent e) {

        // TODO Auto-generated method stub

    }

    @Override

    public void windowDeiconified(WindowEvent e) {

        // TODO Auto-generated method stub

    }

    @Override

    public void windowActivated(WindowEvent e) {

        // TODO Auto-generated method stub

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

        // TODO Auto-generated method stub​

    }

}

public class Main {

    public static void main(String[] args) {

        mealFrame f = new mealFrame();

    }

    }
