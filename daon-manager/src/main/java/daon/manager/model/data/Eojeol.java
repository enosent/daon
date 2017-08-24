package daon.manager.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Eojeol {
    private int seq;
    private String surface;
    private List<Morpheme> morphemes;
}