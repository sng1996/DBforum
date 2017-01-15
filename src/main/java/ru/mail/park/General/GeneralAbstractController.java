package ru.mail.park.General;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = {"http://127.0.0.1"})
@RestController
public class GeneralAbstractController {

    @RequestMapping(path = "/db/api/clear", method = RequestMethod.POST)
    public ResponseEntity clear() throws JsonProcessingException {
        GeneralService generalService = new GeneralService();
        return generalService.clear();
    }

    @RequestMapping(path = "/db/api/status", method = RequestMethod.GET)
    public ResponseEntity status() throws JsonProcessingException {
        GeneralService generalService = new GeneralService();
        return generalService.status();
    }

}
