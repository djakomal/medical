package medico.PPE.Controllers;



import org.springframework.web.bind.annotation.RestController;

import medico.PPE.Services.NotiService;
import medico.PPE.Services.WebsocketService;
import medico.PPE.dtos.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class PostToMessageBoardController {

    @Autowired
    NotiService notiService;

    @Autowired
    private WebsocketService service;

    @PostMapping("/posttomsgboard")
    public void  postMethodName(@RequestBody final Message message) {
        
        System.out.println("Service message sent : " + message.getMessageContent());
        service.postToMessageBoard(message.getMessageContent());
        notiService.sendPublicNoti();
    }
    

    @PostMapping("/postprivatetomsgboard/{id}")
    public void  postPrivateMessage(
            @PathVariable final String id,
            @RequestBody final Message message) {
        
        System.out.println("Service message sent : " + message.getMessageContent());
        service.postPrivateToMessageBoard(id, message.getMessageContent());
        notiService.sendPrivateNoti(id);
    }
}
