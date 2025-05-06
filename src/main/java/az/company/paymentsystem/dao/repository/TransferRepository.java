package az.company.paymentsystem.dao.repository;

import az.company.paymentsystem.dao.entity.TransferEntity;
import org.springframework.data.repository.CrudRepository;

public interface TransferRepository extends CrudRepository<TransferEntity, Long> {
}
