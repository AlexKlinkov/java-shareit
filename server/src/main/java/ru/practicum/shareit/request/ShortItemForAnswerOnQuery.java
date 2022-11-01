
package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortItemForAnswerOnQuery {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer ownerId;
    private Integer requestId;
}