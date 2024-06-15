package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.InvalidCommentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Comment;
import com.handballleague.model.Referee;
import com.handballleague.repositories.CommentRepository;
import com.handballleague.repositories.RefereeRepository;
import com.handballleague.util.Translator;
import com.handballleague.util.SentimentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService implements HandBallService<Comment> {
    private final CommentRepository commentRepository;
    private final ConfigurationPropertiesAutoConfiguration configurationPropertiesAutoConfiguration;
    private final RefereeRepository refereeRepository;
    private final Translator translator;
    private final SentimentProvider sentimentProvider;

    @Autowired
    public CommentService(CommentRepository commentRepository, ConfigurationPropertiesAutoConfiguration configurationPropertiesAutoConfiguration, RefereeRepository refereeRepository, Translator translator, SentimentProvider sentimentProvider) {
        this.commentRepository = commentRepository;
        this.configurationPropertiesAutoConfiguration = configurationPropertiesAutoConfiguration;
        this.refereeRepository = refereeRepository;
        this.translator = translator;
        this.sentimentProvider = sentimentProvider;
    }

    @Override
    public Comment create(Comment entity) throws InvalidArgumentException, EntityAlreadyExistsException {
        if (entity == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if (checkIfEntityExistsInDb(entity)) throw new EntityAlreadyExistsException("Comment with given data already exists in the database");
        if (entity.getContent().isEmpty() ||
                entity.getAuthor() == null ||
                entity.getReferee()== null ||
                entity.getMatch() == null) throw new InvalidArgumentException("Comment content cannot be empty and author must be specified.");

        if(!isCommentValid(entity)) throw new InvalidCommentException("Comment can not be created.");

        try {

            String translatedCommentContent = translator.translate(entity.getContent());
            if (translatedCommentContent == null || translatedCommentContent.isEmpty()) {
                throw new InvalidArgumentException("Translation failed or resulted in empty content.");
            }
            System.out.println("After translate2");
            List<Float> sentiment = sentimentProvider.getSentiment(translatedCommentContent);
            entity.setSentimentScore(sentiment.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidCommentException("Sentiment analysis failed");
        }

        commentRepository.save(entity);

        updateRefereeScore(entity);

        return entity;
    }

    private boolean isCommentValid(Comment comment) throws InvalidCommentException {
        if(!comment.getMatch().isFinished())
            throw new InvalidCommentException("This match was not yet finished. Finish this match to unlock adding comments.");
        if(comment.getMatch().getReferee() != comment.getReferee())
            throw new InvalidCommentException("Referee wan not conducting this match.");
        if(!comment.getMatch().getHomeTeam().getPlayers().contains(comment.getAuthor()) &&
                !comment.getMatch().getAwayTeam().getPlayers().contains(comment.getAuthor()))
            throw new InvalidCommentException("Author is not a player for any of teams playing in this match.");
        if(!comment.getAuthor().isCaptain())
            throw new InvalidCommentException("Author is not a captain of any team playing in this match.");

        return true;
    }

    private void updateRefereeScore(Comment entity) {
        Referee referee = entity.getReferee();
        List<Comment> allComments = commentRepository.findByRefereeUuid(referee.getUuid());

        float sentiment = 0f;

        for (Comment comment : allComments) {
            sentiment += comment.getSentimentScore();
        }
        float score = sentiment/allComments.size();

        referee.setRating(score);
        refereeRepository.save(referee);
    }

    @Override
    public boolean delete(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if (id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            return true;
        } else {
            throw new ObjectNotFoundInDataBaseException("Comment with id: " + id + " not found in the database.");
        }
    }

    @Override
    public Comment update(Long id, Comment entity) throws InvalidArgumentException {
        if (id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if (entity == null) throw new InvalidArgumentException("New comment is null.");
        if (entity.getContent().isEmpty()) throw new InvalidArgumentException("Comment content cannot be empty.");

        Comment commentToUpdate = commentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Comment with given id was not found in the database."));

        commentToUpdate.setContent(entity.getContent());
        commentToUpdate.setEdited(true);
        return commentRepository.save(commentToUpdate);
    }

    @Override
    public Comment getById(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if (id <= 0) throw new InvalidArgumentException("Passed id is invalid.");

        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isEmpty()) {
            throw new ObjectNotFoundInDataBaseException("Comment with given id was not found in the database.");
        }

        return optionalComment.get();
    }

    @Override
    public List<Comment> getAll() {
        Sort sortByCreatedDate = Sort.by(Sort.Direction.DESC, "createdDate");
        return commentRepository.findAll(sortByCreatedDate);
    }

    @Override
    public boolean checkIfEntityExistsInDb(Comment entity) {
        return commentRepository.findAll().stream().anyMatch(existingEntity -> existingEntity.equals(entity));
    }

    @Override
    public boolean checkIfEntityExistsInDb(Long entityID) {
        return commentRepository.existsById(entityID);
    }
}
