package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "requests")
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    Page<ItemRequest> findAllByRequestorIdNot(Integer userId, Pageable pageable);
    ItemRequest findAllByRequestorIdAndId (Integer userId, Integer itemRequestId);
    List<ItemRequest> findAllByRequestor_Id (Integer userId);
}
