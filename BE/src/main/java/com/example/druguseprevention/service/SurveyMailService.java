package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.EmailDetail;
import com.example.druguseprevention.entity.*;
import com.example.druguseprevention.enums.SurveySendStatus;
import com.example.druguseprevention.enums.SurveyType;
import com.example.druguseprevention.exception.exceptions.BadRequestException;
import com.example.druguseprevention.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyMailService {

    private final ProgramParticipationRepository participationRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final SurveyTemplateRepository surveyTemplateRepository;
    private final SurveySendHistoryRepository surveySendHistoryRepository;
    private final ProgramRepository programRepository;
    private final TemplateEngine templateEngine;

    public void sendSurveyToParticipants(Long programId, SurveyType type) {
        Program program = programRepository.findByIdAndIsDeletedFalse(programId)
                .orElseThrow(() -> new BadRequestException("Program not found"));

        if (type == SurveyType.POST) {
            long preSurveyCount = surveySendHistoryRepository.countSentSurveysByProgramAndType(
                    programId, SurveyType.PRE);

            if (preSurveyCount == 0) {
                throw new BadRequestException(
                        "Cannot send post-survey because no pre-survey has been sent for this program"
                );
            }
        }

        SurveyTemplate template = surveyTemplateRepository.findByProgramIdAndTypeAndIsDeletedFalse(programId, type)
                .orElseThrow(() -> new BadRequestException("Survey template not found for this program and type"));

        List<ProgramParticipation> participations = participationRepository.findByProgramId(programId);

        for (ProgramParticipation p : participations) {
            User user = p.getMember();

            // Check if survey has already been sent to this user for this program and type
            boolean alreadySent = surveySendHistoryRepository.existsByUserIdAndProgramIdAndTemplateTypeAndStatus(
                    user.getId(), programId, type, SurveySendStatus.SENT);

            if (alreadySent) {
                continue;
            }

            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setRecipient(user.getEmail());
            emailDetail.setSubject("Survey: " + template.getName());

            Context context = new Context();
            context.setVariable("name", user.getFullName());
            context.setVariable("button", "Take the survey now");
            context.setVariable("link", template.getGoogleFormUrl());
            String html = templateEngine.process("surveytemplate", context);

            SurveySendHistory history = new SurveySendHistory();
            history.setUser(user);
            history.setProgram(program);
            history.setTemplate(template);
            history.setTemplateType(type);
            history.setFormUrl(template.getGoogleFormUrl());
            history.setSentAt(LocalDateTime.now());

            try {
                emailService.sendHtmlEmail(emailDetail, html);
                history.setStatus(SurveySendStatus.SENT);
            } catch (Exception e) {
                history.setStatus(SurveySendStatus.FAILED);
                history.setErrorMessage(e.getMessage());
            }

            surveySendHistoryRepository.save(history);
        }
    }
}