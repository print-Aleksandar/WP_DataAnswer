package mk.wp.dataanswering.backend.service;

import mk.wp.dataanswering.backend.model.Chat;

public interface RequestService {

    boolean isNotRequestLimitForChatExceeded(Chat chat);
}
