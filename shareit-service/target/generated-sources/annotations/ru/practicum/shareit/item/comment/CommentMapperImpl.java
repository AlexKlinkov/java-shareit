package ru.practicum.shareit.item.comment;

import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-01-30T00:02:02+0300",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 11.0.15 (Amazon.com Inc.)"
)
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment commentFromCommentDTOInput(int authorId, int itemId, CommentDTOInput commentDTOInput) {
        if ( commentDTOInput == null ) {
            return null;
        }

        Comment comment = new Comment();

        if ( commentDTOInput != null ) {
            comment.setText( commentDTOInput.getText() );
        }

        return comment;
    }

    @Override
    public CommentDTOOutput commentDTOOutputFromComment(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentDTOOutput commentDTOOutput = new CommentDTOOutput();

        commentDTOOutput.setId( comment.getId() );
        commentDTOOutput.setText( comment.getText() );
        commentDTOOutput.setCreated( comment.getCreated() );

        return commentDTOOutput;
    }
}
