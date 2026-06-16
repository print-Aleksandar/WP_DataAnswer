package mk.wp.dataanswering.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

@Service
public class ExternalToolService {

    private final List<ToolServiceInteractor> interactors;

    public ExternalToolService(List<ToolServiceInteractor> interactors) {
        this.interactors = interactors;
    }

    public void tryUploadToAll(Long userId, Long chatId, MultipartFile file) {
        if (interactors.isEmpty()) {
            throw new IllegalStateException("No ToolServiceInteractor implementations available");
        }

        List<String> errors = new ArrayList<>();

        for (ToolServiceInteractor interactor : interactors) {
            if(!interactor.healty()) continue;
            
            try {
                interactor.tryUpload(userId, chatId, file);
            } catch (Exception e) {
                errors.add(interactor.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        if (errors.size() == interactors.size()) {
            throw new RuntimeException("Upload Failed:\n" + String.join("\n", errors));
        }
    }

    public List<String> getSupportedFileTypes() {
        return this.interactors.stream()
        .filter(ToolServiceInteractor::healty)
        .flatMap(i -> i.getSupportedFileTypes().stream())
        .map(x -> x.startsWith(".") ? x : "." + x)
        .toList();
    }
}
