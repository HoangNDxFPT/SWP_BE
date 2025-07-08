package com.example.druguseprevention.controller;

import com.example.druguseprevention.dto.RegisterSlotRequest;
import com.example.druguseprevention.dto.RegisteredSlotDTO;
import com.example.druguseprevention.entity.Slot;
import com.example.druguseprevention.entity.UserSlot;
import com.example.druguseprevention.service.SlotService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/slot")
@SecurityRequirement(name = "api")
public class SlotController
{
    @Autowired
    SlotService slotService;

    // admin chạy 1 lần duy nhất
    @PostMapping
    public void generateSlot()
    {
        slotService.generateSlots();
    }

    @GetMapping
    public ResponseEntity getSlots()
    {
        List<Slot> slots = slotService.get();
        return ResponseEntity.ok(slots);
    }

    // đăng kí lịch làm theo ngày
    @PostMapping("register")
    public ResponseEntity registerSlot(@RequestBody RegisterSlotRequest registerSlotRequest)
    {
        List<UserSlot>  userSlots = slotService.registerSlot(registerSlotRequest);
        return ResponseEntity.ok(userSlots);
    }

    // xem những slot đã đăng kí
    @GetMapping("/registered")
    public ResponseEntity getRegisteredSlots(
            @RequestParam Long consultantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
    {
        List<RegisteredSlotDTO> registeredSlotDTOS = slotService.getRegisteredSlots(consultantId,date);

        return ResponseEntity.ok(registeredSlotDTOS);
    }

}
