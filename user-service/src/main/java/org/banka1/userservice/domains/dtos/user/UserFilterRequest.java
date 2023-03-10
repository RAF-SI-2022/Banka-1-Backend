package org.banka1.userservice.domains.dtos.user;

import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.userservice.domains.entities.Position;
import org.banka1.userservice.domains.entities.QUser;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private Position position;

    QUser qUser = QUser.user;

    public BooleanBuilder getPredicate() {
        BooleanBuilder predicate = new BooleanBuilder();
        if(firstName != null)
            predicate.and(qUser.firstName.containsIgnoreCase(firstName));

        if(lastName != null)
            predicate.and(qUser.lastName.containsIgnoreCase(lastName));

        if(email != null)
            predicate.and(qUser.email.containsIgnoreCase(email));

        if(position != null)
            predicate.and(qUser.position.eq(position));

        return predicate;
    }

}
