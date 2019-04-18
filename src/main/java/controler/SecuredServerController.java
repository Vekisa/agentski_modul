package controler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
@RequestMapping("/communication")
@RestController
public class SecuredServerController {
    @RequestMapping("/secured")
    public String secured(){
        System.out.println("Inside secured()");

        ConnectionC test = new ConnectionC();

        String response = null;
        try {
            response = test.sendRequest("https://localhost:8888/test/proba");
        } catch (IOException e) {
            System.out.println("Sertifikat nije od poverenja");
        }

        return response;
    }
}
