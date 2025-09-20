package com.cgmoffice._sample.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sample/qr")
@RequiredArgsConstructor
@Slf4j
public class SampleQrController {

	@GetMapping
	public String data(Model model) throws WriterException, IOException {

		// QR 정보
		int width = 200;
		int height = 200;
		String url = "http://192.168.1.18:8080/sample/QrCodeTest/call?p=111";

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);

		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
		String qrImg = Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());

		return qrImg;
	}

}
