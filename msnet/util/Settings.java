package com.msnet.util;

//import com.newSystem.Dialogs.SignUpForm;

import java.awt.*;
import java.io.*;

public class Settings {

    static private String userNmae;
    static private String rpcUser;
    static private String rpcPassword;
    static public String companyName;
    static public String directorName;
    static public String directorEmail;
    static public String directorPhone;
    static public Font Font14;
    static public Font Font15;
    static public Font Font16;
    static public Font Font17;
    static public Font Font18;
    static public Font Font19;
    static public boolean signUpFinished;
    public Settings() {
        ////////////////////////////////////////////////////////////////
        userNmae = System.getProperty("user.name");
        ////////////////////////////////////////////////////////////////
        // AppData\\Roaming\\Bitcoin ������ bincoind.exe�� �ִٰ� ����.
        if (!isThereBitcoind()) {
            // bincoind.exe�� ���� ��� jar ���Ϸκ��� �����ؼ���.
            copyBitcoind();
        }
        ////////////////////////////////////////////////////////////////
        if (!isThereConfFile()) {
            // AppData\\Roaming\\Bitcoin ������ bincoin.conf ������ ���� ���}
            makeConfFile();
        } else {
            // bitcoin.conf ���Ͽ��� rpcuser�� rpcpassword�� �о� ���� �Լ�.
            getRPCUserAndCompanyInfo();
        }
        ////////////////////////////////////////////////////////////////
        // License.txt ���� ����.
        if (!isThereLicense())
            copyLicense();
        ////////////////////////////////////////////////////////////////
    }
    public static String getUserNmae() { return userNmae; }
    public void getRPCUserAndCompanyInfo() {
        try {
            ////////////////////////////////////////////////////////////////
            // bitcoin.conf file //
            String bitcoin_conf = "C:\\Users\\"
                    + userNmae
                    + "\\AppData\\Roaming\\Bitcoin\\bitcoin.conf";
            BufferedReader in = new BufferedReader(new FileReader(bitcoin_conf));
            String s;
            while ((s = in.readLine()) != null) {
                if (s.contains("rpcuser")) {
                    int equalIndex = s.indexOf('=');
                    rpcUser = s.substring(equalIndex + 1);
                }
                if (s.contains("rpcpassword")) {
                    int equalIndex = s.indexOf('=');
                    rpcPassword = s.substring(equalIndex + 1);
                }
                if (s.contains("companyName")) {
                    int equalIndex = s.indexOf('=');
                    companyName = s.substring(equalIndex + 1);
                }
                if (s.contains("directorName")) {
                    int equalIndex = s.indexOf('=');
                    directorName = s.substring(equalIndex + 1);
                }
                if (s.contains("directorEmail")) {
                    int equalIndex = s.indexOf('=');
                    directorEmail = s.substring(equalIndex + 1);
                }
                if (s.contains("directorPhone")) {
                    int equalIndex = s.indexOf('=');
                    directorPhone = s.substring(equalIndex + 1);
                }
            }
            in.close();
            ////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            System.err.println("Error : From reading rpcuser & rpcpassword from bitcoin.conf file.");
            System.exit(1);
        }
    }
    public static String getRpcUser() {
        return rpcUser;
    }
    public static String getRpcPassword() {
        return rpcPassword;
    }
    private boolean isThereConfFile() {
        File confFile = new File(
                "C:\\Users\\"
                        + userNmae
                        + "\\AppData\\Roaming\\Bitcoin\\bitcoin.conf");
        if (confFile.exists())
            return true;
        return false;
    }
    private boolean isThereBitcoind() {
        ///////////////////////////////////////////////////////////
        // AppData/Roaming/ �� BItcoin ������ ���� �� ������ ���� ����.
        File bitcoin_directory = new File(
                "C:\\Users\\"
                        + userNmae
                        + "\\AppData\\Roaming\\Bitcoin");
        if (!bitcoin_directory.exists()) {
            bitcoin_directory.mkdir();
        }
        //////////////////////////////////////////////////////////
        File bincoind_exe = new File(
                "C:\\Users\\"
                        + userNmae
                        + "\\AppData\\Roaming\\Bitcoin\\bincoind.exe");
        if (bincoind_exe.exists())
            return true;
        else
            return false;
    }
    private boolean isThereLicense() {
        //////////////////////////////////////////////////////////
        File licenses_txt = new File(
                "C:\\Users\\"
                        + userNmae
                        + "\\AppData\\Roaming\\Bitcoin\\Licenses.txt");
        if (licenses_txt.exists())
            return true;
        else
            return false;
    }
    private void copyLicense() {
        try {
            InputStream is = getClass().getClassLoader().getResource("Licenses.txt").openStream();

            //sets the output stream to a system folder
            OutputStream os = new FileOutputStream(
                    "C:\\Users\\"
                            + userNmae
                            + "\\AppData\\Roaming\\Bitcoin\\Licenses.txt");
            //2048 here is just my preference
            byte[] b = new byte[4096];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();
        } catch (Exception e) {
            System.err.println("Error : Copy Licenses.txt into System");
            System.exit(1);
        }
    }
    private void copyBitcoind() {
        try {
            InputStream is = getClass().getClassLoader().getResource("bincoind.exe").openStream();

            //sets the output stream to a system folder
            OutputStream os = new FileOutputStream("C:\\Users\\"
                    + userNmae
                    + "\\AppData\\Roaming\\Bitcoin\\bincoind.exe");
            //2048 here is just my preference
            byte[] b = new byte[4096];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();
        } catch (Exception e) {
            System.err.println("Error : Copy Bitcoind.exe into System");
            System.exit(1);
        }
    }
    private void makeConfFile() {
        rpcUser = userNmae;
        rpcPassword = "temppassword";
        String fileName = "C:\\Users\\" + userNmae + "\\AppData\\Roaming\\Bitcoin\\bitcoin.conf";
        try{
            BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, false));
            // ���Ͼȿ� ���ڿ� ����
            fw.write("rpcuser=" + rpcUser);
            fw.newLine();
            fw.write("rpcpassword=" + rpcPassword);
            fw.newLine();
            fw.write("server=1");
            fw.newLine();
            fw.write("msnet=1");
            fw.newLine();
            fw.write("printtoconsole=1");
            fw.newLine();
            fw.write("companyName=" + companyName);
            fw.newLine();
            fw.write("directorName=" + directorName);
            fw.newLine();
            fw.write("directorEmail=" + directorEmail);
            fw.newLine();
            fw.write("directorPhone=" + directorPhone);
            fw.flush();
            // ��ü �ݱ�
            fw.close();
        } catch(Exception e){
            System.err.println("Error : Making bitcoin.conf file error.");
        }
    }
}