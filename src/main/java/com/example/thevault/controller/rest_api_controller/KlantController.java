// Created by S.C. van Gils
// Creation date 1-12-2021

package com.example.thevault.controller.rest_api_controller;

import com.example.thevault.domain.model.Klant;
import com.example.thevault.domain.transfer.KlantDto;
import com.example.thevault.domain.transfer.WelkomDTO;
import com.example.thevault.service.RegistrationService;
import com.example.thevault.domain.transfer.LoginDto;
import com.example.thevault.service.KlantService;
import com.example.thevault.support.authorization.AuthorizationService;
import com.example.thevault.support.authorization.TokenKlantCombinatie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;

@RestController
public class KlantController extends BasisApiController{

    private final Logger logger = LoggerFactory.getLogger(KlantController.class);

    public KlantController(RegistrationService registrationService, KlantService klantService, AuthorizationService authorizationService) {
        super(registrationService, klantService, authorizationService);
        logger.info("New KlantController");
    }

    /**
     *  Deze methode zorgt ervoor dat ingevoerde registratiegegevens in een Klant-object
     *  worden omgezet, waarna het registratieproces in gang wordt gezet.
     *  Als alles goed gaat, krijgt de klant de voor hem relevante informatie,
     *  met daarin zijn nieuwe rekeningnummer, terug alsmede een HTTP-respons 201 = created.
     *
     * @param klant een Klant-object dat wordt aangemaakt op basis van ingevoerde gegevens
     * @return een DTO waar de relevante klantgegevens in staan als de klant succesvol is opgeslagen
     */
    @PostMapping("/register")
    public ResponseEntity<KlantDto> registreerKlantHandler(@RequestBody Klant klant){
        KlantDto klantDto = registrationService.registreerKlant(klant);
    return ResponseEntity.status(HttpStatus.CREATED).body(klantDto);
    }

    /**
    @author Wim 20211207
    methode die inloggegevens ontvangt en laat checken of deze correct zijn en inlog succesvol wordt
    of een algemene foutmelding verstuurt
     */
    @PostMapping("/login")
    public ResponseEntity<WelkomDTO> loginHandler(@RequestBody LoginDto loginDto) throws LoginException {
        //roep loginValidatie aan in de service
        Klant klant = loginService.valideerLogin(loginDto);
        if(klant != null){
            TokenKlantCombinatie tokenKlantCombinatie = authorizationService.authoriseerKlantMetOpaakToken(klant);
            String jwtToken = authorizationService.generateJwtToken(klant);
            // hier moeten de tokens worden toegevoegd aan de header
            return ResponseEntity.ok()
                    .header("Authorization", tokenKlantCombinatie.getKey().toString(), "AuthoriatJwt", jwtToken)
                    .body(new WelkomDTO(klant));
        }
        throw new LoginException();
    }

}
