package com.msnet.util;

//import com.newSystem.Dialogs.SignUpForm;

import java.awt.*;
import java.io.*;

public class Settings {

	public static String id;
	public static String password;
	private static String sysUsrName;
    public Settings() {
        ////////////////////////////////////////////////////////////////
    	sysUsrName = System.getProperty("user.name");
        ////////////////////////////////////////////////////////////////
        // AppData\\Roaming\\Bitcoin 폴더에 bincoind.exe가 있다고 가정.
        if (!isThereBitcoind()) {
            // bincoind.exe가 없을 경우 jar 파일로부터 복사해서옴.
            copyBitcoind();
        }
        ////////////////////////////////////////////////////////////////
        if (!isThereConfFile()) {
            // AppData\\Roaming\\Bitcoin 폴더에 bincoin.conf 파일이 없을 경우}
            makeConfFile();
        }
        ////////////////////////////////////////////////////////////////
        // License.txt 파일 복사.
        if (!isThereLicense())
            copyLicense();
        ////////////////////////////////////////////////////////////////
    }
    
    private boolean isThereConfFile() {
        File confFile = new File(
                "C:\\Users\\"
                        + sysUsrName
                        + "\\AppData\\Roaming\\Bitcoin\\bitcoin.conf");
        if (confFile.exists())
            return true;
        return false;
    }
    
    private boolean isThereBitcoind() {
        ///////////////////////////////////////////////////////////
        // AppData/Roaming/ 에 BItcoin 폴더가 없을 때 폴더를 새로 생성.
        File bitcoin_directory = new File(
                "C:\\Users\\"
                        + sysUsrName
                        + "\\AppData\\Roaming\\Bitcoin");
        if (!bitcoin_directory.exists()) {
            bitcoin_directory.mkdir();
        }
        
        File bincoind_exe = new File(
                "C:\\Users\\"
                        + sysUsrName
                        + "\\AppData\\Roaming\\Bitcoin\\bincoind.exe");
        if (bincoind_exe.exists())
            return true;
        else
            return false;
    }
    
    private boolean isThereLicense() {
        File license_txt = new File(
                "C:\\Users\\"
                        + sysUsrName
                        + "\\AppData\\Roaming\\Bitcoin\\License.txt");
        if (license_txt.exists())
            return true;
        else
            return false;
    }
    
    private void copyLicense() {
        try {
        	File bincoind = new File("res/license.txt");
        	FileInputStream is = new FileInputStream(bincoind);

            //sets the output stream to a system folder
            OutputStream os = new FileOutputStream(
                    "C:\\Users\\"
                            + sysUsrName
                            + "\\AppData\\Roaming\\Bitcoin\\License.txt");
            //2048 here is just my preference
            byte[] b = new byte[4096];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();
        } catch (Exception e) {
            System.err.println("Error : Copy license.txt into System");
            System.exit(1);
        }
    }
    
    private void copyBitcoind() {
        try {
        	File bincoind = new File("res/bincoind.exe");
        	FileInputStream is = new FileInputStream(bincoind);

            //sets the output stream to a system folder
            OutputStream os = new FileOutputStream("C:\\Users\\"
                    + sysUsrName
                    + "\\AppData\\Roaming\\Bitcoin\\bincoind.exe");
            
            byte[] b = new byte[4096];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();
        } catch (Exception e) {
        	e.printStackTrace();
            System.err.println("Error : Copy Bitcoind.exe into System");
            System.exit(1);
        }
    }
    
    private void makeConfFile() {
        String fileName = "C:\\Users\\" + sysUsrName + "\\AppData\\Roaming\\Bitcoin\\bitcoin.conf";
        try{
            BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, false));
            // 파일안에 문자열 쓰기
            fw.write("rpcuser=" + id);
            fw.newLine();
            fw.write("rpcpassword=" + password);
            fw.newLine();
            fw.write("server=1");
            fw.newLine();
            fw.write("msnet=1");
            fw.newLine();
            fw.write("printtoconsole=1");
            fw.flush();
            // 객체 닫기
            fw.close();
        } catch(Exception e){
            System.err.println("Error : Making bitcoin.conf file error.");
        }
    }
}