package org.example.icatch.WebSocket;

import org.example.icatch.Picture.PictureResponseDTO;

import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/newImage")
    @SendTo("/topic/images")
    public PictureResponseDTO notifyNewImage(PictureResponseDTO picture) {
        return picture;
    }
}