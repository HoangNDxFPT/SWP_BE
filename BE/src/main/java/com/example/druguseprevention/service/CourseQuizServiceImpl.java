package com.example.druguseprevention.service;

import com.example.druguseprevention.dto.CourseQuizDto;
import com.example.druguseprevention.entity.Course;
import com.example.druguseprevention.entity.CourseQuiz;
import com.example.druguseprevention.entity.CourseQuizResult;
import com.example.druguseprevention.repository.CourseQuizRepository;
import com.example.druguseprevention.repository.CourseRepository;
import com.example.druguseprevention.repository.CourseQuizResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseQuizServiceImpl implements CourseQuizService {

    private final CourseQuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final CourseQuizResultRepository resultRepository; // ✅ injected

    @Override
    public List<CourseQuizDto> getQuizByCourseId(Long courseId) {
        return quizRepository.findByCourseId(courseId).stream().map(q -> {
            CourseQuizDto dto = new CourseQuizDto();
            dto.setId(q.getId());
            dto.setCourseId(q.getCourse().getId());
            dto.setQuestion(q.getQuestion());
            dto.setAnswer(q.getAnswer());
            dto.setCorrect(q.getCorrect());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public CourseQuizDto createQuiz(CourseQuizDto dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        CourseQuiz quiz = new CourseQuiz();
        quiz.setCourse(course);
        quiz.setQuestion(dto.getQuestion());
        quiz.setAnswer(dto.getAnswer());
        quiz.setCorrect(dto.getCorrect());

        quiz = quizRepository.save(quiz);

        dto.setId(quiz.getId());
        return dto;
    }

    @Override
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    // ✅ Trả về danh sách ID khóa học mà user đã hoàn thành (điểm >= 60%)
    @Override
    public List<Long> getCompletedCourseIdsByUserId(Long userId) {
        List<CourseQuizResult> results = resultRepository.findByUserId(userId);
        return results.stream()
                .filter(result -> result.getScore() >= 0.6 * result.getTotalQuestions())
                .map(result -> result.getCourse().getId())
                .distinct()
                .toList();
    }
}
