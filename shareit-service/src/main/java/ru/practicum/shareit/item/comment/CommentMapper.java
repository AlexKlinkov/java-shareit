package ru.practicum.shareit.item.comment;

import org.mapstruct.Mapper;

@Mapper
public interface CommentMapper {
    Comment commentFromCommentDTOInput(int authorId, int itemId, CommentDTOInput commentDTOInput);
    CommentDTOOutput commentDTOOutputFromComment(Comment comment);
}
