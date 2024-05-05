package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Comment;
import com.handballleague.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService implements HandBallService<Comment> {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment create(Comment entity) throws InvalidArgumentException, EntityAlreadyExistsException {
        if (entity == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if (checkIfEntityExistsInDb(entity)) throw new EntityAlreadyExistsException("Comment with given data already exists in the database");
        if (entity.getContent().isEmpty() ||
                entity.getAuthor() == null ||
                entity.getReferee()== null ||
                entity.getMatch() == null) throw new InvalidArgumentException("Comment content cannot be empty and author must be specified.");

        commentRepository.save(entity);
        return entity;
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
