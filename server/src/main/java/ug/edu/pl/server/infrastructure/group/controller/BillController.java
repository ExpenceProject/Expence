package ug.edu.pl.server.infrastructure.group.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ug.edu.pl.server.domain.group.GroupFacade;
import ug.edu.pl.server.domain.group.dto.BillDto;
import ug.edu.pl.server.domain.group.dto.CreateBillDto;

@RestController
@RequestMapping("/api/bills")
class BillController {
    private final GroupFacade groupFacade;

    BillController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    @GetMapping("/{billId}")
    ResponseEntity<BillDto> getById(@PathVariable Long billId) {
        return ResponseEntity.ok(groupFacade.getBillById(billId));
    }

    @PostMapping
    ResponseEntity<BillDto> create(@RequestBody CreateBillDto billDto) {
        return ResponseEntity.ok(groupFacade.createBill(billDto));
    }
}
