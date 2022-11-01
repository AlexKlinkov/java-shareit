package ru.practicum.shareit.item.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Component("CommentMapperInitialization")
public class CommentMapperInitialization implements CommentMapper {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public CommentMapperInitialization(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Comment commentFromCommentDTOInput(int authorId, int itemId, CommentDTOInput commentDTOInput) {
        Comment comment = new Comment();
        comment.setText(commentDTOInput.getText());
        comment.setAuthor(userRepository.getById(authorId));
        comment.setItem(itemRepository.getById(itemId));
        return comment;
    }

    @Override
    public CommentDTOOutput commentDTOOutputFromComment(Comment comment) {
        CommentDTOOutput commentDTOOutput = new CommentDTOOutput();
        commentDTOOutput.setId(comment.getId());
        commentDTOOutput.setText(comment.getText());
        commentDTOOutput.setAuthorName(userRepository.getById(comment.getAuthor().getId()).getName());
        commentDTOOutput.setCreated(LocalDateTime.now());
        return commentDTOOutput;
    }
}
