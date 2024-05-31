package com.handballleague.services;

import com.handballleague.exceptions.EntityAlreadyExistsException;
import com.handballleague.exceptions.InvalidArgumentException;
import com.handballleague.exceptions.ObjectNotFoundInDataBaseException;
import com.handballleague.model.Post;
import com.handballleague.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService implements HandBallService<Post>{
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Post create(Post entity) throws InvalidArgumentException, EntityAlreadyExistsException {
        if (entity == null) throw new InvalidArgumentException("Passed parameter is invalid");
        if (checkIfEntityExistsInDb(entity)) throw new EntityAlreadyExistsException("Post with given data already exists in the database");
        if (entity.getContent().isEmpty() ||
                entity.getTitle().isEmpty()) throw new InvalidArgumentException("Post content cannot be empty and title must be specified.");
        entity.setPostedDate(LocalDateTime.now());
        postRepository.save(entity);
        return entity;
    }

    @Override
    public boolean delete(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if (id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        } else {
            throw new ObjectNotFoundInDataBaseException("Post with id: " + id + " not found in the database.");
        }
    }

    @Override
    public Post update(Long id, Post entity) throws InvalidArgumentException {
        if (id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        if (entity == null) throw new InvalidArgumentException("New post is null.");
        if (entity.getContent().isEmpty()) throw new InvalidArgumentException("Post content cannot be empty.");

        Post postToUpdate = postRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInDataBaseException("Post with given id was not found in the database."));

        postToUpdate.setContent(entity.getContent());
        postToUpdate.setTitle(entity.getTitle());
        return postRepository.save(postToUpdate);
    }

    @Override
    public Post getById(Long id) throws InvalidArgumentException, ObjectNotFoundInDataBaseException {
        if (id <= 0) throw new InvalidArgumentException("Passed id is invalid.");
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new ObjectNotFoundInDataBaseException("Post with given id was not found in the database.");
        }
        return optionalPost.get();
    }

    @Override
    public List<Post> getAll() {
        Sort sortByCreatedDate = Sort.by(Sort.Direction.DESC, "postedDate");
        return postRepository.findAll(sortByCreatedDate);
    }

    @Override
    public boolean checkIfEntityExistsInDb(Post entity) {
        return postRepository.findAll().stream().anyMatch(existingEntity -> existingEntity.equals(entity));
    }

    @Override
    public boolean checkIfEntityExistsInDb(Long entityID) {
        return postRepository.existsById(entityID);
    }
}
