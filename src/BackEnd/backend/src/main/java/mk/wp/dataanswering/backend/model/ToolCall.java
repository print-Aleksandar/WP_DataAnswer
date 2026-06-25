package mk.wp.dataanswering.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="tool_call")
public class ToolCall {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tool_call_id")
    private String id;

    @Column(name = "tool_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "response_id", nullable = false, updatable = false)
    private Response response;

    @Column(columnDefinition = "TEXT")
    private String content;
}
