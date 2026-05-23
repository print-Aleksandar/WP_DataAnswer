package mk.wp.dataanswering.backend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ToolServiceInteractor {
    void tryUpload(Long user_id, Long chat_id, MultipartFile file);
    List<String> getSupportedFileTypes();

    boolean healty();
}
