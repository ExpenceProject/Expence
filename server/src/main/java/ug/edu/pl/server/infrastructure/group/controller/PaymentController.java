package ug.edu.pl.server.infrastructure.group.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ug.edu.pl.server.domain.group.GroupFacade;
import ug.edu.pl.server.domain.group.dto.CreatePaymentDto;
import ug.edu.pl.server.domain.group.dto.PaymentDto;

import java.util.Collection;

@RestController
@RequestMapping("/api/payments")
class PaymentController {
    private final GroupFacade groupFacade;

    PaymentController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    @PostMapping
    ResponseEntity<PaymentDto> createPayment(@RequestBody @Valid CreatePaymentDto dto) {
        return ResponseEntity.ok(groupFacade.createPayment(dto));
    }

    @GetMapping("/{id}")
    ResponseEntity<PaymentDto> getPaymentById(@PathVariable String id) {
        return ResponseEntity.ok(groupFacade.getPaymentById(id));
    }

    @GetMapping("/sender/{senderId}/group/{groupId}")
    ResponseEntity<Collection<PaymentDto>> getPaymentsBySenderIdAndGroupId(@PathVariable String senderId, @PathVariable String groupId) {
        return ResponseEntity.ok(groupFacade.getPaymentsBySenderIdAndGroupId(senderId, groupId));
    }

    @GetMapping("/receiver/{receiverId}/group/{groupId}")
    ResponseEntity<Collection<PaymentDto>> getPaymentsByReceiverIdAndGroupId(@PathVariable String receiverId, @PathVariable String groupId) {
        return ResponseEntity.ok(groupFacade.getPaymentsByReceiverIdAndGroupId(receiverId, groupId));
    }

    @GetMapping("/group/{groupId}")
    ResponseEntity<Collection<PaymentDto>> getPaymentsByGroupId(@PathVariable String groupId) {
        return ResponseEntity.ok(groupFacade.getPaymentsByGroupId(groupId));
    }

    @GetMapping("/group/{groupId}/sender/{senderId}/receiver/{receiverId}")
    ResponseEntity<Collection<PaymentDto>> getPaymentsByGroupIdAndSenderIdAndReceiverId(@PathVariable String groupId, @PathVariable String senderId, @PathVariable String receiverId) {
        return ResponseEntity.ok(groupFacade.getPaymentsByGroupIdAndSenderIdAndReceiverId(groupId, senderId, receiverId));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePayment(@PathVariable String id) {
        groupFacade.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}