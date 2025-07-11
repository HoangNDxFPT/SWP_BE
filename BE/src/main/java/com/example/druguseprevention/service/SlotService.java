package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.RegisterSlotRequest;
import com.example.druguseprevention.dto.RegisteredSlotDTO;
import com.example.druguseprevention.dto.RegisteredSlotForConsultantDTO;
import com.example.druguseprevention.entity.Appointment;
import com.example.druguseprevention.entity.Slot;
import com.example.druguseprevention.entity.User;
import com.example.druguseprevention.entity.UserSlot;
import com.example.druguseprevention.enums.AppointmentStatus;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.AppointmentRepository;
import com.example.druguseprevention.repository.AuthenticationRepository;
import com.example.druguseprevention.repository.SlotRepository;
import com.example.druguseprevention.repository.UserSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SlotService {
    @Autowired
    AuthenticationRepository authenticationRepository;
    @Autowired
    SlotRepository slotRepository;
    @Autowired
    UserSlotRepository userSlotRepository;
    @Autowired
    UserService userService;

    @Autowired
    AppointmentRepository appointmentRepository;

    public List<Slot> get()
    {
        return slotRepository.findAll();
    }


    public List<UserSlot> registerSlot(RegisterSlotRequest registerSlotRequest)
    {
        User consultant = userService.getCurrentUser();
        List<UserSlot>  consultantSlots = new ArrayList<>();
        List<UserSlot> oldAccountSlot= userSlotRepository.findUserSlotsByConsultantAndDate (consultant, registerSlotRequest.getDate());

        if(!oldAccountSlot.isEmpty())
        {
            //=> da co lich roi
            throw new BadRequestException("........");
        }

        for(Slot slot : slotRepository.findAll())
        {
            UserSlot consultantSlot = new UserSlot();
            consultantSlot.setSlot(slot);
            consultantSlot.setConsultant(consultant);
            consultantSlot.setDate(registerSlotRequest.getDate());
            consultantSlots.add(consultantSlot);
        }

        return  userSlotRepository.saveAll(consultantSlots);
    }


    public List<RegisteredSlotDTO> getRegisteredSlots(Long consultantId, LocalDate date) {
        User consultant = authenticationRepository.findById(consultantId)
                .orElseThrow(() -> new BadRequestException("Consultant not found"));


        return  userSlotRepository.findUserSlotsByConsultantAndDate (consultant,date)
                // duyệt qua danh sách user slot
                .stream()
                // lọc những slot còn trống
                .filter(UserSlot::isAvailable)
                // mapping từ UserSlot entity sang RegisteredSlotDTO bằng hàm toDTO
                .map(this::toDTO)
                // trả về list DTO đã map
                .collect(Collectors.toList());
    }

    private RegisteredSlotDTO toDTO(UserSlot userSlot) {
        Slot slot = userSlot.getSlot();
        return new RegisteredSlotDTO(slot.getId(),
                                    slot.getLabel(),
                                    slot.getStart().toString(),
                                    slot.getEnd().toString(),
                                    userSlot.getDate(),
                                    userSlot.isAvailable());
    }


    // lấy ra lịch làm việc của consultant bao gồm slot trống và ko trống
    public List<RegisteredSlotForConsultantDTO> getRegisteredSlotsForConsultant( LocalDate date) {
        User consultant = userService.getCurrentUser();

        List<UserSlot> slots = userSlotRepository.findUserSlotsByConsultantAndDate(consultant, date);

        List<RegisteredSlotForConsultantDTO> result = new ArrayList<>();

        for (UserSlot slot : slots) {
            Optional<Appointment> appointmentOpt = appointmentRepository.findByUserSlotAndStatusIn(
                    slot,
                    List.of(AppointmentStatus.PENDING, AppointmentStatus.COMPLETED)
            );

            String status;
            String memberName;

            if (appointmentOpt.isPresent()) {
                Appointment appointment = appointmentOpt.get();
                status = "ĐÃ ĐẶT";
                memberName = appointment.getMember().getFullName();
            } else {
                status = "CÒN TRỐNG";
                memberName = null;
            }

            RegisteredSlotForConsultantDTO dto = new RegisteredSlotForConsultantDTO(
                    slot.getId(),
                    date,
                    slot.getSlot().getStart().toString(),
                    slot.getSlot().getEnd().toString(),
                    status,
                    memberName
            );

            result.add(dto);
        }

        return result;
    }

    public void generateSlots() {
        //generate tu 7h sang toi 17h
        LocalTime start = LocalTime.of(7, 0);
        LocalTime end = LocalTime.of(17, 0);
        List<Slot> slots = new ArrayList<>();

        while(start.isBefore(end)) {
            Slot slot = new Slot();
            slot.setStart(start);
            slot.setLabel(start.toString());
            slot.setEnd(start.plusMinutes(30));

            slots.add(slot);
            start = start.plusMinutes(30);
        }
        slotRepository.saveAll(slots);
    }
}
