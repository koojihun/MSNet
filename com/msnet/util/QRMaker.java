package com.msnet.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRMaker {
	
	private int width;
	private int height;
	private String filePath;

	public QRMaker(int width, int height) {
		this.width = width;
		this.height = height;
		this.filePath = "C:\\Users\\" + Settings.getSysUsrName() + "\\AppData\\Roaming\\bitcoin\\QRcodes";
	}

	public void makeQR(String fileName, String content, String filePath) {
		try {
			File file = null;
			// 큐알이미지를 저장할 디렉토리 지정
			filePath = this.filePath + "\\" + filePath;
			file = new File(filePath);
			if (!file.exists())
				file.mkdirs();
			// 코드인식시 링크걸 URL주소
			String codeurl = new String(content.getBytes("UTF-8"), "ISO-8859-1");
			
			// 큐알코드 바코드 생상값
			int qrcodeColor = 0xFF2e4e96;
			// 큐알코드 배경색상값
			int backgroundColor = 0xFFFFFFFF;
			
			Hashtable<EncodeHintType, Object> hintMap = new Hashtable<>();
			hintMap.put(EncodeHintType.QR_VERSION, 13);
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			
			// 3,4번째 parameter값 : width/height값 지정
			BitMatrix bitMatrix = qrCodeWriter.encode(codeurl, BarcodeFormat.QR_CODE, width, height, hintMap);
			MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(qrcodeColor, backgroundColor);
			BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig);
			// ImageIO를 사용한 바코드 파일쓰기
			ImageIO.write(bufferedImage, "png", new File(filePath + "\\" + fileName + ".png"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
