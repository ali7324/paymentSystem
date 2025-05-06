package az.company.paymentsystem.service;

import az.company.paymentsystem.dao.repository.CardRepository;
import az.company.paymentsystem.dao.repository.TransferRepository;
import az.company.paymentsystem.exception.ForbiddenException;
import az.company.paymentsystem.exception.InsufficientBalanceException;
import az.company.paymentsystem.exception.NotFoundException;
import az.company.paymentsystem.mapper.TransferMapper;
import az.company.paymentsystem.model.enums.CardStatus;
import az.company.paymentsystem.model.enums.TransferStatus;
import az.company.paymentsystem.model.request.CreateTransferRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static az.company.paymentsystem.model.enums.ErrorMessage.CARD_NOT_ACTIVE;
import static az.company.paymentsystem.model.enums.ErrorMessage.CARD_NOT_ENOUGH_BALANCE;
import static az.company.paymentsystem.model.enums.ErrorMessage.CARD_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    private final TransferRepository transferRepository;
    private final CardRepository cardRepository;
    private final TransferMapper transferMapper;

    @Transactional
    public void doTransfer(CreateTransferRequest createTransferRequest) {
        log.info("ActionLog.doTransfer.start senderCardNumber {}", createTransferRequest.getSenderCardNumber());
        var senderCardEntity = cardRepository.findByCardNumber(createTransferRequest.getSenderCardNumber())
                .orElseThrow(() -> {
                    log.error("ActionLog.doTransfer.error sender card not found {}", createTransferRequest.getSenderCardNumber());
                    return new NotFoundException(
                            CARD_NOT_ENOUGH_BALANCE.getFormattedMessage(createTransferRequest.getSenderCardNumber())
                    );
                });

        if (senderCardEntity.getStatus() != CardStatus.ACTIVE) {
            log.error("ActionLog.doTransfer.error sender card is not active {}", createTransferRequest.getSenderCardNumber());
            throw new ForbiddenException(
                    CARD_NOT_ACTIVE.getFormattedMessage(createTransferRequest.getSenderCardNumber())
            );
        }

        if (senderCardEntity.getBalance().compareTo(createTransferRequest.getAmount()) < 0) {
            log.error("ActionLog.doTransfer.error insufficient balance for card {}", createTransferRequest.getSenderCardNumber());
            throw new InsufficientBalanceException(
                    CARD_NOT_ENOUGH_BALANCE.getFormattedMessage(createTransferRequest.getSenderCardNumber())
            );
        }

        var receiverCardEntity = cardRepository.findByCardNumber(createTransferRequest.getReceiverCardNumber())
                .orElseThrow(() -> {
                    log.error("ActionLog.doTransfer.error receiver card not found {}", createTransferRequest.getReceiverCardNumber());
                    return new NotFoundException(
                            CARD_NOT_FOUND.getFormattedMessage(createTransferRequest.getReceiverCardNumber())
                    );
                });

        if (receiverCardEntity.getStatus() != CardStatus.ACTIVE) {
            log.error("ActionLog.doTransfer.error receiver card is not active {}", createTransferRequest.getReceiverCardNumber());
            throw new ForbiddenException(
                    CARD_NOT_ACTIVE.getFormattedMessage(createTransferRequest.getReceiverCardNumber())
            );
        }

        senderCardEntity.setBalance(senderCardEntity.getBalance().subtract(createTransferRequest.getAmount()));
        receiverCardEntity.setBalance(receiverCardEntity.getBalance().add(createTransferRequest.getAmount()));
        cardRepository.save(senderCardEntity);
        cardRepository.save(receiverCardEntity);

        var entity = transferMapper.toEntity(
                createTransferRequest.getSenderCardNumber(),
                createTransferRequest.getReceiverCardNumber(),
                createTransferRequest.getAmount(),
                TransferStatus.SUCCESS
        );
        transferRepository.save(entity);
        log.info("ActionLog.doTransfer.end senderCardNumber {}", createTransferRequest.getSenderCardNumber());
    }
}
