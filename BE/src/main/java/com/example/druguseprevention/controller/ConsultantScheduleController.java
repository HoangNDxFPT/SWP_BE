//package com.example.druguseprevention.controller;
//
//import com.example.druguseprevention.dto.ConsultantScheduleRequest;
//import com.example.druguseprevention.dto.ConsultantScheduleResponse;
//import com.example.druguseprevention.service.ConsultantScheduleService;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//@SecurityRequirement(name = "api")
//@SecurityRequirement(name = "bearer-key")
//@RestController
//@RequestMapping("/api/consultant/schedules")
//@RequiredArgsConstructor
//public class ConsultantScheduleController {
//
//    private final ConsultantScheduleService scheduleService;
//
//    @PostMapping
//    public ConsultantScheduleResponse create(@RequestBody ConsultantScheduleRequest request) {
//        return scheduleService.create(request);
//    }
//
//    @PutMapping("/{id}")
//    public ConsultantScheduleResponse update(@PathVariable Long id, @RequestBody ConsultantScheduleRequest request) {
//        return scheduleService.update(id, request);
//    }
//
//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable Long id) {
//        scheduleService.delete(id);
//    }
//
//    @GetMapping
//    public List<ConsultantScheduleResponse> getAll() {
//        return scheduleService.getAll();
//    }
//
//    @GetMapping("/{consultantId}")
//    public List<ConsultantScheduleResponse> getByConsultant(@PathVariable Long consultantId) {
//        return scheduleService.getByConsultantId(consultantId);
//    }
//}
