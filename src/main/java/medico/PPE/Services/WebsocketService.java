package medico.PPE.Services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import medico.PPE.dtos.Message;


@Service
public class WebsocketService {

    private SimpMessagingTemplate template;

    public WebsocketService ( SimpMessagingTemplate template){
        this.template = template;
    }

    public void postToMessageBoard( final String msg){

        System.out.println("Message received in Backend" + msg);
       
        Message  message  = new Message(msg);
        template.convertAndSend("/topic/messages", message);
    }


    public void postPrivateToMessageBoard( final String id , final String msg){

        System.out.println("Private message received in Backend" + msg);
       
        Message  message  = new Message(msg);
        template.convertAndSendToUser(id, "/topic/private-messages", message);
    }
}
