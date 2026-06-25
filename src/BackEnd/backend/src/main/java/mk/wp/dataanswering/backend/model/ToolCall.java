package mk.wp.dataanswering.backend.model;

import jakarta.persistence.*;

public class ToolCall {

    @Id
    @Column(name = "tool_call_id")
    private String Id;

    @ManyToOne
    @JoinColumn(name = "response_id", nullable = false, updatable = false)
    private Response response;

    @Column
    private String content;
}
