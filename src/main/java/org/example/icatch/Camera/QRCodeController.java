package org.example.icatch.Camera;

import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/qrcode")
public class QRCodeController {

    @Autowired
    private QRCodeService qrCodeService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateQRCode(@RequestBody WifiCredentials wifiCredentials) {
        try {
            String wifiJson = String.format("{\"wifiId\":\"%s\",\"wifiPassword\":\"%s\"}",
                    wifiCredentials.getWifiId(),
                    wifiCredentials.getWifiPassword());

            String qrCodeImage = qrCodeService.generateQRCodeImage(wifiJson, 250, 250);

            Map<String, Object> response = new HashMap<>();
            response.put("qrCode", "data:image/png;base64," + qrCodeImage);

            return ResponseEntity.ok(response);
        } catch (WriterException | IOException e) {
            return ResponseEntity.internalServerError().body("QR 코드 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}