package ug.edu.pl.server.infrastructure.group.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ug.edu.pl.server.domain.group.GroupFacade;
import ug.edu.pl.server.domain.group.dto.BillDto;
import ug.edu.pl.server.domain.group.dto.CreateBillDto;

import java.util.Collection;

@RestController
@RequestMapping("/api/bills")
class BillController {
    private final GroupFacade groupFacade;

    BillController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    @GetMapping("/{billId}")
    ResponseEntity<BillDto> getById(@PathVariable String billId) {
        return ResponseEntity.ok(groupFacade.getBillById(billId));
    }

    @PostMapping
    ResponseEntity<BillDto> create(@RequestBody CreateBillDto billDto) {
        return ResponseEntity.ok(groupFacade.createBill(billDto));
    }

    @PutMapping("/{billId}")
    ResponseEntity<BillDto> update(@PathVariable String billId, @RequestBody CreateBillDto billDto) {
        return ResponseEntity.ok(groupFacade.updateBill(billDto, billId));
    }

    @GetMapping("/group/{groupId}")
    ResponseEntity<Collection<BillDto>> getBillsByGroupId(@PathVariable String groupId) {
        return ResponseEntity.ok(groupFacade.getBillsByGroupId(groupId));
    }

    @GetMapping("/user/{userId}/group/{groupId}")
    ResponseEntity<Collection<BillDto>> getBillsByUserIdAndGroupId(@PathVariable String userId, @PathVariable String groupId) {
        return ResponseEntity.ok(groupFacade.getBillsByUserIdAndGroupId(userId, groupId));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteBill(@PathVariable String id) {
        groupFacade.deleteBill(id);
        return ResponseEntity.noContent().build();
    }
}
