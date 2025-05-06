package az.company.paymentsystem.service;

import az.company.paymentsystem.dao.repository.CardRepository;
import az.company.paymentsystem.exception.NotFoundException;
import az.company.paymentsystem.mapper.CardMapper;
import az.company.paymentsystem.model.request.CreateCardRequest;
import az.company.paymentsystem.model.request.CreateDepositRequest;
import az.company.paymentsystem.model.response.CardResponse;
import az.company.paymentsystem.util.CardUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static az.company.paymentsystem.model.enums.ErrorMessage.CARD_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    private final CardRepository cardRepository;
    private final CardUtil cardUtil;
    private final CardMapper cardMapper;

    public void createCard(CreateCardRequest createCardRequest) {
        log.info("ActionLog.createCard.start holder name: {}", createCardRequest.getHolderName());
        var generatedCardNumber = cardUtil.generate("409858", 16);
        var entity = cardMapper.toEntity(createCardRequest);
        entity.setCardNumber(generatedCardNumber);
        cardRepository.save(entity);
        log.info("ActionLog.createCard.end holder name: {}", createCardRequest.getHolderName());
    }

    public CardResponse getCardByNumber(String cardNumber) {
        log.info("ActionLog.getCardByNumber.start");
        var cardEntity = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new NotFoundException(CARD_NOT_FOUND.getFormattedMessage(cardNumber)));
        var response = cardMapper.toResponse(cardEntity);
        log.info("ActionLog.getCardByNumber.end holder name: {}", cardEntity.getHolderName());
        return response;
    }

    public void deposit(CreateDepositRequest createDepositRequest) {
        log.info("ActionLog.deposit.start card number: {}", createDepositRequest.getCardNumber());
        var cardEntity = cardRepository.findByCardNumber(createDepositRequest.getCardNumber())
                .orElseThrow(() -> new NotFoundException(CARD_NOT_FOUND.getFormattedMessage(createDepositRequest.getCardNumber())));
        cardEntity.setBalance(cardEntity.getBalance().add(createDepositRequest.getAmount()));
        cardRepository.save(cardEntity);
        log.info("ActionLog.deposit.end card number: {}", createDepositRequest.getCardNumber());
    }
}
