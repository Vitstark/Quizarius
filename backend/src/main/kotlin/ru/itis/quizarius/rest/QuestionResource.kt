package ru.itis.quizarius.rest

import jakarta.ws.rs.ApplicationPath
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import org.jooq.quizarius.enums.Category
import ru.itis.quizarius.dto.AnswerOnQuestion
import ru.itis.quizarius.service.QuestionService

/**
 * @author Vitaly Chekushkin
 */

@ApplicationPath("/questions")
class QuestionResource(val questionService: QuestionService) {

    @GET
    @Path("/categories")
    @Produces(APPLICATION_JSON)
    fun categories() = Category.values()

    @GET
    @Path("/{category}")
    @Produces(APPLICATION_JSON)
    fun generateQuestions(category: Category) = questionService.getQuestionByCategory(category)

    @POST
    @Path("/answer")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    fun answerQuestion(answer: AnswerOnQuestion) = questionService.answerOnQuestion(answer)
}