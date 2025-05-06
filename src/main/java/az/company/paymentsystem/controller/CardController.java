package az.company.paymentsystem.controller;

import az.company.paymentsystem.model.request.CreateCardRequest;
import az.company.paymentsystem.model.response.CardResponse;
import az.company.paymentsystem.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCard(@RequestHeader(name = "Accept-Language", defaultValue = "en") String language,
                           @RequestBody @Valid CreateCardRequest createCardRequest) {
        cardService.createCard(createCardRequest);
    }

    @GetMapping("/{cardNumber}")
    public CardResponse getCardByNumber(@PathVariable String cardNumber) {
        return cardService.getCardByNumber(cardNumber);
    }

}
