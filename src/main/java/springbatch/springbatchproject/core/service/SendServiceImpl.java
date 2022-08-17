package springbatch.springbatchproject.core.service;

import org.springframework.stereotype.Service;

@Service
public class SendServiceImpl implements SendService {
    @Override
    public void send(String email, String message) {
        System.out.println("email: " + email + "\nmessage\n" + message);
    }
}
