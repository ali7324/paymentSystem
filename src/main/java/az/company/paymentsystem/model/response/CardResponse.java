package az.company.paymentsystem.model.response;

import az.company.paymentsystem.model.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    private String holderName;
    private String cardNumber;
    private LocalDate expirationDate;
    private String cvv;
    private CardType type;
    private BigDecimal balance;
}
